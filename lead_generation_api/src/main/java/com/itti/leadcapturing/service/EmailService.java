package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.HierarchyUserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final HierarchyUserRepository hierarchyUserRepository;

    @Value("${app.email.from:noreply@company.com}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${app.base.url:http://localhost:3000}")
    private String baseUrl;

   private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // ==================== PRIVATE HELPER ====================

    private void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("✅ Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("❌ Email send failed to: {} - {}", to, e.getMessage());
        }
    }

 private String formatDateTime(LocalDateTime dateTime) {
    if (dateTime == null) return "Not set";
    return dateTime.format(DISPLAY_FMT);
}

    private int calculateWorkingDays(LocalDateTime start, LocalDateTime end) {
        java.time.LocalDate startDate = start.toLocalDate();
        java.time.LocalDate endDate = end.toLocalDate();
        int workingDays = 0;
        java.time.LocalDate current = startDate.plusDays(1);
        while (!current.isAfter(endDate)) {
            java.time.DayOfWeek dayOfWeek = current.getDayOfWeek();
            if (dayOfWeek != java.time.DayOfWeek.SATURDAY &&
                    dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                workingDays++;
            }
            current = current.plusDays(1);
        }
        return workingDays;
    }

    // ==================== RFQ: APPROVAL PENDING ====================

    @Async
    public void sendApprovalPendingEmail(
            RFQ rfq,
            HierarchyUser approver,
            HierarchyLevel level,
            Integer sequenceOrder,
            Integer totalLevels) {

        if (!emailEnabled) {
            log.info("📧 Email disabled - skipping approval notification");
            return;
        }

        try {
            log.info("=".repeat(80));
            log.info("📧 [SENDING APPROVAL EMAIL]");
            log.info("  RFQ: {} ({})", rfq.getRfqNumber(), rfq.getRfqTitle());
            log.info("  To: {} ({})", approver.getFullName(), approver.getEmail());
            log.info("  Level: {} (Order: {})", level.getLevelName(), level.getLevelOrder());
            log.info("  Sequence: {}/{}", sequenceOrder, totalLevels);

            Context context = new Context();
            context.setVariable("approverName", approver.getFullName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("rfqDescription", rfq.getRfqDescription());
            context.setVariable("buyerName", rfq.getBuyer().getCompanyName());
            context.setVariable("createdBy", rfq.getCreatedByUser().getEmail());
            context.setVariable("levelName", level.getLevelName());
            context.setVariable("sequenceOrder", sequenceOrder);
            context.setVariable("totalLevels", totalLevels);
            context.setVariable("isLastLevel", sequenceOrder.equals(totalLevels));
            context.setVariable("createdDate", formatDateTime(rfq.getCreatedAt()));
            context.setVariable("approvalUrl", baseUrl + "/approvals/pending/" + rfq.getId());
            context.setVariable("priority", rfq.getPriority().toString());
            context.setVariable("itemsCount", rfq.getItems() != null ? rfq.getItems().size() : 0);
            context.setVariable("suppliersCount", rfq.getSelectedSuppliers() != null ?
                    rfq.getSelectedSuppliers().size() : 0);

            String htmlContent = templateEngine.process("email/approval-pending", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(approver.getEmail());
            helper.setSubject("🔔 RFQ Approval Required - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Email sent successfully to {}", approver.getEmail());
            log.info("=".repeat(80));

        } catch (MessagingException e) {
            log.error("❌ Failed to send approval email to {}", approver.getEmail(), e);
        }
    }

    // ==================== RFQ: APPROVAL FORWARDED ====================

    @Async
    public void sendApprovalForwardedEmail(
            RFQ rfq,
            HierarchyUser previousApprover,
            HierarchyUser nextApprover,
            HierarchyLevel nextLevel,
            String comments) {

        if (!emailEnabled) return;

        try {
            log.info("📧 [APPROVAL FORWARDED] From {} to {}",
                    previousApprover.getFullName(), nextApprover.getFullName());

            Context context = new Context();
            context.setVariable("approverName", nextApprover.getFullName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("previousApprover", previousApprover.getFullName());
            context.setVariable("previousLevel", previousApprover.getHierarchyLevel().getLevelName());
            context.setVariable("currentLevel", nextLevel.getLevelName());
            context.setVariable("comments", comments != null ? comments : "No comments");
            context.setVariable("approvalUrl", baseUrl + "/approvals/pending/" + rfq.getId());
            context.setVariable("approvedDate", formatDateTime(LocalDateTime.now()));

            String htmlContent = templateEngine.process("email/approval-forwarded", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(nextApprover.getEmail());
            helper.setSubject("🔔 RFQ Forwarded for Approval - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Forwarded notification sent to {}", nextApprover.getEmail());

        } catch (MessagingException e) {
            log.error("❌ Failed to send forwarded email", e);
        }
    }

    // ==================== RFQ: PUBLISHED ====================

    @Async
    public void sendRFQPublishedEmail(
            RFQ rfq,
            HierarchyUser finalApprover,
            Set<Supplier> suppliers) {

        if (!emailEnabled) return;

        try {
            log.info("=".repeat(80));
            log.info("📧 [RFQ PUBLISHED - NOTIFYING STAKEHOLDERS]");
            log.info("  RFQ: {}", rfq.getRfqNumber());
            log.info("  Final Approver: {}", finalApprover.getFullName());
            log.info("  Suppliers: {}", suppliers != null ? suppliers.size() : 0);

            notifyCreatorOfPublication(rfq, finalApprover);
            notifyApproversOfPublication(rfq);
            notifySuppliersOfNewRFQ(rfq, suppliers);

            log.info("  ✅ All publication notifications sent");
            log.info("=".repeat(80));

        } catch (Exception e) {
            log.error("❌ Error in published notifications", e);
        }
    }

    private void notifyCreatorOfPublication(RFQ rfq, HierarchyUser finalApprover) {
        try {
            User creator = rfq.getCreatedByUser();

            Context context = new Context();
            context.setVariable("creatorName", creator.getFirstName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("finalApprover", finalApprover.getFullName());
            context.setVariable("approvedDate", formatDateTime(rfq.getApprovalDate()));
            context.setVariable("dueDate", formatDateTime(rfq.getDueDate()));
            context.setVariable("deliveryDate", formatDateTime(rfq.getItemRequiredDate()));
            context.setVariable("suppliersCount", rfq.getSelectedSuppliers().size());
            context.setVariable("rfqUrl", baseUrl + "/rfq/view/" + rfq.getId());

            String htmlContent = templateEngine.process("email/rfq-published-creator", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(creator.getEmail());
            helper.setSubject("✅ RFQ Published - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Creator notified: {}", creator.getEmail());

        } catch (MessagingException e) {
            log.error("❌ Failed to notify creator", e);
        }
    }

    private void notifyApproversOfPublication(RFQ rfq) {
        try {
            Set<User> approvers = rfq.getApprovers();
            if (approvers == null || approvers.isEmpty()) {
                log.info("   ℹ️ No approvers to notify");
                return;
            }
            log.info("   📧 Notifying {} approvers of publication", approvers.size());
            for (User approver : approvers) {
                try {
                    sendApproverPublicationNotification(rfq, approver);
                } catch (Exception e) {
                    log.error("   ❌ Failed to notify approver: {} - {}", approver.getEmail(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("❌ Error in notifyApproversOfPublication", e);
        }
    }

    private void sendApproverPublicationNotification(RFQ rfq, User approver) {
        try {
            Context context = new Context();
            context.setVariable("approverName", approver.getFirstName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("publishedDate", formatDateTime(LocalDateTime.now()));
            context.setVariable("suppliersCount", rfq.getSelectedSuppliers().size());

            String htmlContent = templateEngine.process("email/rfq-published-approver", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(approver.getEmail());
            helper.setSubject("✅ RFQ Published - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("❌ Failed to notify approver {}", approver.getEmail(), e);
        }
    }

    private void notifySuppliersOfNewRFQ(RFQ rfq, Set<Supplier> suppliers) {
        log.info("📧 [NOTIFYING SUPPLIERS] Starting supplier notifications");
        log.info("   Total Suppliers: {}", suppliers != null ? suppliers.size() : 0);

        if (suppliers == null || suppliers.isEmpty()) {
            log.warn("   ⚠️ No suppliers to notify!");
            return;
        }

        int successCount = 0;
        int failureCount = 0;

        for (Supplier supplier : suppliers) {
            try {
                log.info("   📧 Processing supplier: {}", supplier.getCompanyName());

                String supplierEmail = supplier.getContactPersonEmail();
                if (supplierEmail == null || supplierEmail.trim().isEmpty()) {
                    log.error("   ❌ Supplier {} has no email - skipping", supplier.getCompanyName());
                    failureCount++;
                    continue;
                }

                int itemsCount = 0;
                try {
                    if (rfq.getItems() != null) itemsCount = rfq.getItems().size();
                } catch (Exception e) {
                    log.warn("   ⚠️ Could not get items count: {}", e.getMessage());
                }

                Context context = new Context();
                context.setVariable("supplierName", supplier.getCompanyName());
                context.setVariable("rfqNumber", rfq.getRfqNumber());
                context.setVariable("rfqTitle", rfq.getRfqTitle());
                context.setVariable("rfqDescription", rfq.getRfqDescription());
                context.setVariable("buyerName", rfq.getBuyer().getCompanyName());
                context.setVariable("dueDate", formatDateTime(rfq.getDueDate()));
                context.setVariable("deliveryDate", formatDateTime(rfq.getItemRequiredDate()));
                context.setVariable("itemsCount", itemsCount);
                context.setVariable("supplierPortalUrl", baseUrl + "/supplier/rfq/" + rfq.getId());
                context.setVariable("priority", rfq.getPriority() != null ? rfq.getPriority().toString() : "MEDIUM");

                String htmlContent = templateEngine.process("email/supplier-new-rfq", context);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(supplierEmail);
                helper.setSubject("🆕 New RFQ - " + rfq.getRfqNumber());
                helper.setText(htmlContent, true);
                mailSender.send(message);

                log.info("   ✅ Supplier notified: {} ({})", supplier.getCompanyName(), supplierEmail);
                successCount++;

            } catch (MessagingException e) {
                log.error("   ❌ Failed to notify supplier: {} - {}", supplier.getCompanyName(), e.getMessage());
                failureCount++;
            } catch (Exception e) {
                log.error("   ❌ Unexpected error notifying supplier: {}", supplier.getCompanyName(), e);
                failureCount++;
            }
        }

        log.info("📧 [SUPPLIER NOTIFICATIONS COMPLETE] ✅ Success: {} | ❌ Failed: {}", successCount, failureCount);
    }

    // ==================== RFQ: REJECTED ====================

    @Async
    public void sendRFQRejectedEmail(RFQ rfq, HierarchyUser rejector, String rejectRemarks) {
        if (!emailEnabled) return;

        try {
            log.info("📧 [RFQ REJECTED] RFQ: {}, Rejected by: {}", rfq.getRfqNumber(), rejector.getFullName());

            User creator = rfq.getCreatedByUser();

            Context context = new Context();
            context.setVariable("creatorName", creator.getFirstName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("rejectorName", rejector.getFullName());
            context.setVariable("rejectorLevel", rejector.getHierarchyLevel().getLevelName());
            context.setVariable("rejectRemarks", rejectRemarks != null ? rejectRemarks : "No remarks provided");
            context.setVariable("rejectedDate", formatDateTime(LocalDateTime.now()));
            context.setVariable("rfqUrl", baseUrl + "/rfq/view/" + rfq.getId());

            String htmlContent = templateEngine.process("email/rfq-rejected", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(creator.getEmail());
            helper.setSubject("❌ RFQ Rejected - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Rejection notification sent to creator");

        } catch (MessagingException e) {
            log.error("❌ Failed to send rejection email", e);
        }
    }

    // ==================== RFQ: RETURNED FOR REVISION ====================

    @Async
    public void sendRFQReturnedForRevisionEmail(RFQ rfq, HierarchyUser returner, String revisionComments) {
        if (!emailEnabled) return;

        try {
            log.info("📧 [RFQ RETURNED FOR REVISION] RFQ: {}, By: {}", rfq.getRfqNumber(), returner.getFullName());

            User creator = rfq.getCreatedByUser();

            Context context = new Context();
            context.setVariable("creatorName", creator.getFirstName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("returnerName", returner.getFullName());
            context.setVariable("returnerLevel", returner.getHierarchyLevel().getLevelName());
            context.setVariable("revisionComments", revisionComments != null ? revisionComments : "No comments");
            context.setVariable("returnedDate", formatDateTime(LocalDateTime.now()));
            context.setVariable("editUrl", baseUrl + "/rfq/edit/" + rfq.getId());

            String htmlContent = templateEngine.process("email/rfq-returned-revision", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(creator.getEmail());
            helper.setSubject("🔄 RFQ Returned for Revision - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Revision notification sent to creator");

        } catch (MessagingException e) {
            log.error("❌ Failed to send revision email", e);
        }
    }

    // ==================== RFQ: ON HOLD ====================

    @Async
    public void sendRFQOnHoldEmail(RFQ rfq, HierarchyUser holder, String holdRemarks) {
        if (!emailEnabled) return;

        try {
            log.info("📧 [RFQ ON HOLD] By: {}", holder.getFullName());

            User creator = rfq.getCreatedByUser();

            Context context = new Context();
            context.setVariable("creatorName", creator.getFirstName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("holderName", holder.getFullName());
            context.setVariable("holderLevel", holder.getHierarchyLevel().getLevelName());
            context.setVariable("holdRemarks", holdRemarks);
            context.setVariable("holdDate", formatDateTime(LocalDateTime.now()));

            String htmlContent = templateEngine.process("email/rfq-on-hold", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(creator.getEmail());
            helper.setSubject("⏸️ RFQ On Hold - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Hold notification sent");

        } catch (MessagingException e) {
            log.error("❌ Failed to send hold email", e);
        }
    }

    // ==================== RFQ: HOLD RELEASED ====================

    @Async
    public void sendRFQHoldReleasedEmail(
            RFQ rfq,
            HierarchyUser releaser,
            HierarchyUser nextApprover,
            String releaseRemarks) {

        if (!emailEnabled) return;

        try {
            log.info("📧 [HOLD RELEASED] By: {}, Next: {}", releaser.getFullName(), nextApprover.getFullName());

            Context context = new Context();
            context.setVariable("approverName", nextApprover.getFullName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("releaserName", releaser.getFullName());
            context.setVariable("releaseRemarks", releaseRemarks != null ? releaseRemarks : "No remarks");
            context.setVariable("releasedDate", formatDateTime(LocalDateTime.now()));
            context.setVariable("approvalUrl", baseUrl + "/approvals/pending/" + rfq.getId());

            String htmlContent = templateEngine.process("email/rfq-hold-released", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(nextApprover.getEmail());
            helper.setSubject("▶️ RFQ Hold Released - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            notifyCreatorOfHoldRelease(rfq, releaser, releaseRemarks);

            log.info("  ✅ Release notifications sent");

        } catch (MessagingException e) {
            log.error("❌ Failed to send release email", e);
        }
    }

    private void notifyCreatorOfHoldRelease(RFQ rfq, HierarchyUser releaser, String remarks) {
        try {
            User creator = rfq.getCreatedByUser();

            Context context = new Context();
            context.setVariable("creatorName", creator.getFirstName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("releaserName", releaser.getFullName());
            context.setVariable("releaseRemarks", remarks);

            String htmlContent = templateEngine.process("email/rfq-hold-released-creator", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(creator.getEmail());
            helper.setSubject("▶️ RFQ Hold Released - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            log.error("❌ Failed to notify creator of hold release", e);
        }
    }

    // ==================== RFQ: APPROVAL REMINDER ====================

    @Async
    public void sendApprovalReminderEmail(
            RFQ rfq,
            HierarchyUser approver,
            HierarchyLevel level,
            LocalDateTime pendingSince) {

        if (!emailEnabled) {
            log.info("📧 Email disabled - skipping reminder notification");
            return;
        }

        try {
            log.info("⏰ [SENDING REMINDER EMAIL] RFQ: {}, To: {}", rfq.getRfqNumber(), approver.getEmail());

            int workingDaysPassed = calculateWorkingDays(pendingSince, LocalDateTime.now());

            Context context = new Context();
            context.setVariable("approverName", approver.getFullName());
            context.setVariable("rfqNumber", rfq.getRfqNumber());
            context.setVariable("rfqTitle", rfq.getRfqTitle());
            context.setVariable("rfqDescription", rfq.getRfqDescription());
            context.setVariable("buyerName", rfq.getBuyer().getCompanyName());
            context.setVariable("levelName", level.getLevelName());
            context.setVariable("pendingSince", formatDateTime(pendingSince));
            context.setVariable("workingDaysPassed", workingDaysPassed);
            context.setVariable("priority", rfq.getPriority().toString());

            String htmlContent = templateEngine.process("email/approval-reminder", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(approver.getEmail());
            helper.setSubject("⏰ REMINDER: RFQ Approval Pending - " + rfq.getRfqNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Reminder email sent successfully to {}", approver.getEmail());

        } catch (MessagingException e) {
            log.error("❌ Failed to send reminder email to {}", approver.getEmail(), e);
        }
    }

    // ==================== PO: APPROVAL PENDING ====================

    public void sendPOApprovalPendingEmail(PurchaseOrder po, HierarchyUser approver,
                                           HierarchyLevel level, int currentStep, int totalSteps) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] Approval pending — To: {}", approver.getEmail());

            String subject = "Action Required: Purchase Order " + po.getPoNumber() + " Pending Your Approval";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#1a73e8;'>Purchase Order Approval Required</h2>"
                    + "<p>Dear <strong>" + approver.getFullName() + "</strong>,</p>"
                    + "<p>A Purchase Order requires your approval.</p>"
                    + "<table style='border-collapse:collapse;width:100%;'>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>"
                    + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Approval Level</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + level.getLevelName()
                    + " (Step " + currentStep + " of " + totalSteps + ")</td></tr>"
                    + "</table>"
                    + "<p style='margin-top:20px;'>Please log in to review and take action.</p>"
                    + "<p style='color:#888;font-size:12px;'>This is an automated notification.</p>"
                    + "</body></html>";

            sendHtmlEmail(approver.getEmail(), subject, body);

        } catch (Exception e) {
            log.error("❌ Failed to send PO approval pending email", e);
        }
    }

    // ==================== PO: APPROVAL FORWARDED ====================

    public void sendPOApprovalForwardedEmail(PurchaseOrder po, HierarchyUser fromApprover,
                                             HierarchyUser toApprover, HierarchyLevel nextLevel,
                                             String comments) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] Forwarding approval to: {}", toApprover.getEmail());

            String subject = "Action Required: Purchase Order " + po.getPoNumber() + " Forwarded for Your Approval";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#1a73e8;'>Purchase Order Forwarded for Approval</h2>"
                    + "<p>Dear <strong>" + toApprover.getFullName() + "</strong>,</p>"
                    + "<p>PO <strong>" + po.getPoNumber() + "</strong> has been approved by "
                    + fromApprover.getFullName() + " and is now pending your approval at <strong>"
                    + nextLevel.getLevelName() + "</strong> level.</p>"
                    + "<table style='border-collapse:collapse;width:100%;'>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>"
                    + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
                    + (comments != null && !comments.isEmpty()
                        ? "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Comments</strong></td>"
                          + "<td style='padding:8px;border:1px solid #ddd;'>" + comments + "</td></tr>" : "")
                    + "</table>"
                    + "<p style='margin-top:20px;'>Please log in to review and take action.</p>"
                    + "</body></html>";

            sendHtmlEmail(toApprover.getEmail(), subject, body);

        } catch (Exception e) {
            log.error("❌ Failed to send PO forwarded email", e);
        }
    }

    // ==================== PO: FULLY APPROVED ====================

    public void sendPOApprovedEmail(PurchaseOrder po, HierarchyUser finalApprover) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] PO approved — {}", po.getPoNumber());

            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) {
                log.warn("  ⚠️ PO creator not found for ID: {}", po.getCreatedByUserId());
                return;
            }

            String subject = "✅ Purchase Order " + po.getPoNumber() + " — Fully Approved";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#34a853;'>Purchase Order Approved!</h2>"
                    + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                    + "<p>Your Purchase Order has been fully approved and is ready to be sent to the supplier.</p>"
                    + "<table style='border-collapse:collapse;width:100%;'>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>"
                    + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Approved By</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + finalApprover.getFullName() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Approved On</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
                    + "</table>"
                    + "<p style='margin-top:20px;'>You can now proceed to send the PO to the supplier.</p>"
                    + "</body></html>";

            sendHtmlEmail(creator.getEmail(), subject, body);

        } catch (Exception e) {
            log.error("❌ Failed to send PO approved email", e);
        }
    }

    // ==================== PO: REJECTED ====================

    public void sendPORejectedEmail(PurchaseOrder po, HierarchyUser rejector, String rejectRemarks) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] PO rejected — {}", po.getPoNumber());

            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) return;

            String subject = "❌ Purchase Order " + po.getPoNumber() + " — Rejected";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#ea4335;'>Purchase Order Rejected</h2>"
                    + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                    + "<p>Your Purchase Order has been permanently rejected. No resubmission is allowed.</p>"
                    + "<table style='border-collapse:collapse;width:100%;'>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Rejected By</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + rejector.getFullName() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Rejection Reason</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;color:#ea4335;'>" + rejectRemarks + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Rejected On</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
                    + "</table>"
                    + "<p style='margin-top:20px;color:#888;'>Please create a new PO if required.</p>"
                    + "</body></html>";

            sendHtmlEmail(creator.getEmail(), subject, body);

        } catch (Exception e) {
            log.error("❌ Failed to send PO rejected email", e);
        }
    }

    // ==================== PO: RETURNED FOR REVISION ====================

    public void sendPOReturnedForRevisionEmail(PurchaseOrder po, HierarchyUser returner, String returnRemarks) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] PO returned for revision — {}", po.getPoNumber());

            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) return;

            String subject = "🔄 Purchase Order " + po.getPoNumber() + " — Returned for Revision";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#fbbc04;'>Purchase Order Returned for Revision</h2>"
                    + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                    + "<p>Your Purchase Order has been returned for revision. Please make the requested changes and resubmit.</p>"
                    + "<table style='border-collapse:collapse;width:100%;'>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Returned By</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + returner.getFullName() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Revision Required</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;color:#f57c00;'>" + returnRemarks + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Returned On</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
                    + "</table>"
                    + "<p style='margin-top:20px;'>Please log in, update the PO, and resubmit for approval.</p>"
                    + "</body></html>";

            sendHtmlEmail(creator.getEmail(), subject, body);

        } catch (Exception e) {
            log.error("❌ Failed to send PO revision email", e);
        }
    }

    // ==================== PO: ON HOLD ====================

    public void sendPOOnHoldEmail(PurchaseOrder po, HierarchyUser holder, String holdRemarks) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] PO on hold — {}", po.getPoNumber());

            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) return;

            String subject = "⏸️ Purchase Order " + po.getPoNumber() + " — Put on Hold";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#ff7043;'>Purchase Order On Hold</h2>"
                    + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                    + "<p>Your Purchase Order has been put on hold temporarily.</p>"
                    + "<table style='border-collapse:collapse;width:100%;'>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Held By</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + holder.getFullName() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Reason</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + holdRemarks + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Hold Date</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
                    + "</table>"
                    + "<p style='margin-top:20px;'>You will be notified once the hold is released.</p>"
                    + "</body></html>";

            sendHtmlEmail(creator.getEmail(), subject, body);

        } catch (Exception e) {
            log.error("❌ Failed to send PO hold email", e);
        }
    }

    // ==================== PO: HOLD RELEASED ====================

    public void sendPOHoldReleasedEmail(PurchaseOrder po, HierarchyUser releaser,
                                        HierarchyUser nextApprover, String releaseRemarks) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] PO hold released — {}", po.getPoNumber());

            // Notify next approver
            if (nextApprover != null) {
                String subject = "▶️ Purchase Order " + po.getPoNumber() + " — Hold Released, Action Required";
                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                        + "<h2 style='color:#34a853;'>Purchase Order Hold Released</h2>"
                        + "<p>Dear <strong>" + nextApprover.getFullName() + "</strong>,</p>"
                        + "<p>The hold on PO <strong>" + po.getPoNumber() + "</strong> has been released by "
                        + releaser.getFullName() + ". Your approval is now required.</p>"
                        + (releaseRemarks != null && !releaseRemarks.isEmpty()
                            ? "<p><strong>Release Remarks:</strong> " + releaseRemarks + "</p>" : "")
                        + "<p>Please log in to review and take action.</p>"
                        + "</body></html>";
                sendHtmlEmail(nextApprover.getEmail(), subject, body);
            }

            // Notify creator
            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator != null) {
                String subject = "▶️ Purchase Order " + po.getPoNumber() + " — Hold Released";
                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                        + "<h2 style='color:#34a853;'>Purchase Order Hold Released</h2>"
                        + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                        + "<p>The hold on your PO <strong>" + po.getPoNumber() + "</strong> has been released by "
                        + releaser.getFullName() + ". Approval is now continuing.</p>"
                        + "</body></html>";
                sendHtmlEmail(creator.getEmail(), subject, body);
            }

        } catch (Exception e) {
            log.error("❌ Failed to send PO hold released email", e);
        }
    }

    // ==================== PO: ISSUED TO SUPPLIER ====================

    @Async
    public void sendPOIssuedToSupplierEmail(PurchaseOrder po) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [PO EMAIL] PO issued to supplier — {}", po.getPoNumber());

            // 1. Notify Supplier
            notifySupplierOfPOIssued(po);

            // 2. Notify RFQ Creator
            notifyRfqCreatorOfPOIssued(po);

            // 3. Notify PO Creator (if different from RFQ creator)
            notifyPoCreatorOfPOIssued(po);

            log.info("  ✅ All PO issued notifications sent for {}", po.getPoNumber());

        } catch (Exception e) {
            log.error("❌ Failed to send PO issued emails for {}", po.getPoNumber(), e);
        }
    }

    private void notifySupplierOfPOIssued(PurchaseOrder po) {
        try {
            Supplier supplier = po.getSupplier();
            if (supplier == null) {
                log.warn("  ⚠️ No supplier on PO {}", po.getPoNumber());
                return;
            }

            String supplierEmail = supplier.getContactPersonEmail();
            if (supplierEmail == null || supplierEmail.trim().isEmpty()) {
                log.warn("  ⚠️ Supplier {} has no email — skipping", supplier.getCompanyName());
                return;
            }

            Context context = new Context();
            context.setVariable("supplierName", supplier.getCompanyName());
            context.setVariable("poNumber", po.getPoNumber());
            context.setVariable("buyerName", po.getBuyer() != null ? po.getBuyer().getCompanyName() : "N/A");
            context.setVariable("grandTotal", po.getGrandTotal());
            context.setVariable("issueDate", formatDateTime(LocalDateTime.now()));
            context.setVariable("paymentTerms", po.getPaymentTerms() != null ? po.getPaymentTerms() : "As per agreement");
            context.setVariable("deliveryTerms", po.getDeliveryTerms() != null ? po.getDeliveryTerms() : "As per agreement");
            context.setVariable("supplierPortalUrl", baseUrl + "/supplier/po/" + po.getId());

            String htmlContent = templateEngine.process("email/po-issued-supplier", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(supplierEmail);
            helper.setSubject("📦 New Purchase Order Issued - " + po.getPoNumber());
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ Supplier notified: {} ({})", supplier.getCompanyName(), supplierEmail);

        } catch (MessagingException e) {
            log.error("  ❌ Failed to notify supplier for PO {}", po.getPoNumber(), e);
        }
    }

    private void notifyRfqCreatorOfPOIssued(PurchaseOrder po) {
        try {
            if (po.getRfq() == null || po.getRfq().getCreatedByUser() == null) {
                log.warn("  ⚠️ No RFQ or RFQ creator found for PO {}", po.getPoNumber());
                return;
            }

            User rfqCreator = po.getRfq().getCreatedByUser();

            Context context = new Context();
            context.setVariable("rfqCreatorName", rfqCreator.getFirstName());
            context.setVariable("rfqNumber", po.getRfq().getRfqNumber());
            context.setVariable("poNumber", po.getPoNumber());
            context.setVariable("supplierName", po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A");
            context.setVariable("grandTotal", po.getGrandTotal());
            context.setVariable("issueDate", formatDateTime(LocalDateTime.now()));
            context.setVariable("poUrl", baseUrl + "/rfq/po-view/" + po.getId());

            String htmlContent = templateEngine.process("email/po-created-from-rfq", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(rfqCreator.getEmail());
            helper.setSubject("📑 PO Issued to Supplier - " + po.getPoNumber() + " (RFQ: " + po.getRfq().getRfqNumber() + ")");
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("  ✅ RFQ creator notified: {}", rfqCreator.getEmail());

        } catch (MessagingException e) {
            log.error("  ❌ Failed to notify RFQ creator for PO {}", po.getPoNumber(), e);
        }
    }

    private void notifyPoCreatorOfPOIssued(PurchaseOrder po) {
        try {
            // Skip if PO creator is same as RFQ creator (already notified above)
            if (po.getRfq() != null && po.getRfq().getCreatedByUser() != null) {
                Long rfqCreatorId = po.getRfq().getCreatedByUser().getId();
                if (rfqCreatorId != null && rfqCreatorId.equals(po.getCreatedByUserId())) {
                    log.info("  ℹ️ PO creator same as RFQ creator — skipping duplicate email");
                    return;
                }
            }

            HierarchyUser poCreator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (poCreator == null) {
                log.warn("  ⚠️ PO creator not found for ID: {}", po.getCreatedByUserId());
                return;
            }

            String subject = "📦 Purchase Order " + po.getPoNumber() + " — Issued to Supplier";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#1a73e8;'>Purchase Order Issued to Supplier</h2>"
                    + "<p>Dear <strong>" + poCreator.getFullName() + "</strong>,</p>"
                    + "<p>The Purchase Order has been successfully issued to the supplier.</p>"
                    + "<table style='border-collapse:collapse;width:100%;'>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>"
                    + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
                    + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Issued On</strong></td>"
                    + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
                    + "</table>"
                    + "</body></html>";

            sendHtmlEmail(poCreator.getEmail(), subject, body);
            log.info("  ✅ PO creator notified: {}", poCreator.getEmail());

        } catch (Exception e) {
            log.error("  ❌ Failed to notify PO creator for PO {}", po.getPoNumber(), e);
        }
    }

     @Async
    public void sendInvoiceSubmittedToRfqCreator(Invoice invoice) {
        if (invoice.getRfqCreatorEmail() == null) return;

        Context ctx = new Context();
        ctx.setVariable("rfqCreatorName",  invoice.getRfqCreatorName());
        ctx.setVariable("invoiceNumber",   invoice.getInvoiceNumber());
        ctx.setVariable("supplierName",    invoice.getSupplierName());
        ctx.setVariable("poNumber",        invoice.getPoNumber());
        ctx.setVariable("totalAmount",     invoice.getTotalAmount());
        ctx.setVariable("currency",        invoice.getCurrency());
        ctx.setVariable("submittedAt",     invoice.getUpdatedAt() != null
                                               ? invoice.getUpdatedAt().format(DISPLAY_FMT) : "");
        ctx.setVariable("reviewLink",      baseUrl + "/buyer/invoices/" + invoice.getId());

        String body = templateEngine.process("invoice-submitted-to-buyer", ctx);
        sendHtmlEmail(invoice.getRfqCreatorEmail(),
                      "New Invoice Submitted: " + invoice.getInvoiceNumber(), body);
    }

    // ── 2. RFQ creator approved → notify supplier ─────────────────────────────

    @Async
    public void sendInvoiceApprovedToSupplier(Invoice invoice, String buyerName) {
        if (invoice.getSupplierEmail() == null) return;

        Context ctx = new Context();
        ctx.setVariable("supplierName",  invoice.getSupplierName());
        ctx.setVariable("invoiceNumber", invoice.getInvoiceNumber());
        ctx.setVariable("poNumber",      invoice.getPoNumber());
        ctx.setVariable("totalAmount",   invoice.getTotalAmount());
        ctx.setVariable("currency",      invoice.getCurrency());
        ctx.setVariable("approvedBy",    buyerName);
        ctx.setVariable("approvedAt",    invoice.getApprovedRejectedAt() != null
                                             ? invoice.getApprovedRejectedAt().format(DISPLAY_FMT) : "");
        ctx.setVariable("remarks",       invoice.getApprovalRemarks());
        ctx.setVariable("invoiceLink",   baseUrl + "/supplier/invoices/" + invoice.getId());

        String body = templateEngine.process("invoice-approved-supplier", ctx);
        sendHtmlEmail(invoice.getSupplierEmail(),
                      "Invoice Approved: " + invoice.getInvoiceNumber(), body);
    }

    // ── 3. RFQ creator requests changes → notify supplier ────────────────────

    @Async
    public void sendInvoiceResubmissionRequest(Invoice invoice, String remarks) {
        if (invoice.getSupplierEmail() == null) return;

        Context ctx = new Context();
        ctx.setVariable("supplierName",  invoice.getSupplierName());
        ctx.setVariable("invoiceNumber", invoice.getInvoiceNumber());
        ctx.setVariable("poNumber",      invoice.getPoNumber());
        ctx.setVariable("buyerName",     invoice.getApprovedRejectedBy());
        ctx.setVariable("remarks",       remarks);
        ctx.setVariable("invoiceLink",   baseUrl + "/supplier/invoices/" + invoice.getId());

        String body = templateEngine.process("invoice-resubmission-request", ctx);
        sendHtmlEmail(invoice.getSupplierEmail(),
                      "Action Required – Resubmit Invoice: " + invoice.getInvoiceNumber(), body);
    }

    // ── 4. Supplier resubmitted → notify RFQ creator ─────────────────────────

    @Async
    public void sendInvoiceResubmittedToRfqCreator(Invoice invoice, String supplierRemarks) {
        if (invoice.getRfqCreatorEmail() == null) return;

        Context ctx = new Context();
        ctx.setVariable("rfqCreatorName",   invoice.getRfqCreatorName());
        ctx.setVariable("invoiceNumber",    invoice.getInvoiceNumber());
        ctx.setVariable("supplierName",     invoice.getSupplierName());
        ctx.setVariable("poNumber",         invoice.getPoNumber());
        ctx.setVariable("totalAmount",      invoice.getTotalAmount());
        ctx.setVariable("currency",         invoice.getCurrency());
        ctx.setVariable("supplierRemarks",  supplierRemarks);
        ctx.setVariable("resubmitCount",    invoice.getResubmitCount());
        ctx.setVariable("reviewLink",       baseUrl + "/buyer/invoices/" + invoice.getId());

        String body = templateEngine.process("invoice-resubmitted-to-buyer", ctx);
        sendHtmlEmail(invoice.getRfqCreatorEmail(),
                      "Invoice Resubmitted: " + invoice.getInvoiceNumber(), body);
    }

    // ── 5. Permanent rejection → notify supplier ──────────────────────────────

    @Async
    public void sendInvoiceRejectedPermanently(Invoice invoice, String remarks) {
        if (invoice.getSupplierEmail() == null) return;

        Context ctx = new Context();
        ctx.setVariable("supplierName",  invoice.getSupplierName());
        ctx.setVariable("invoiceNumber", invoice.getInvoiceNumber());
        ctx.setVariable("poNumber",      invoice.getPoNumber());
        ctx.setVariable("buyerName",     invoice.getApprovedRejectedBy());
        ctx.setVariable("remarks",       remarks);

        String body = templateEngine.process("invoice-rejected-permanently", ctx);
        sendHtmlEmail(invoice.getSupplierEmail(),
                      "Invoice Rejected: " + invoice.getInvoiceNumber(), body);
    }

    // ── 6. Marked as paid → notify supplier ───────────────────────────────────

    @Async
    public void sendInvoiceMarkedPaid(Invoice invoice, String paymentReference) {
        if (invoice.getSupplierEmail() == null) return;

        Context ctx = new Context();
        ctx.setVariable("supplierName",      invoice.getSupplierName());
        ctx.setVariable("invoiceNumber",     invoice.getInvoiceNumber());
        ctx.setVariable("poNumber",          invoice.getPoNumber());
        ctx.setVariable("totalAmount",       invoice.getTotalAmount());
        ctx.setVariable("currency",          invoice.getCurrency());
        ctx.setVariable("paymentReference",  paymentReference);
        ctx.setVariable("paidAt",            invoice.getPaidAt() != null
                                                 ? invoice.getPaidAt().format(DISPLAY_FMT) : "");
        ctx.setVariable("paidBy",            invoice.getPaidBy());
        ctx.setVariable("invoiceLink",       baseUrl + "/supplier/invoices/" + invoice.getId());

        String body = templateEngine.process("invoice-paid-supplier", ctx);
        sendHtmlEmail(invoice.getSupplierEmail(),
                      "Payment Processed: " + invoice.getInvoiceNumber(), body);
    }
}