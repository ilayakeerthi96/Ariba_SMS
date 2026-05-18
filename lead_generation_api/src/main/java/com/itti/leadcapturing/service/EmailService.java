// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.repo.HierarchyUserRepository;
// import jakarta.mail.MessagingException;
// import jakarta.mail.internet.MimeMessage;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;
// import org.thymeleaf.TemplateEngine;
// import org.thymeleaf.context.Context;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.Set;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class EmailService {

//     private final JavaMailSender mailSender;
//     private final TemplateEngine templateEngine;
//     private final HierarchyUserRepository hierarchyUserRepository;

//     @Value("${app.email.from:noreply@company.com}")
//     private String fromEmail;

//     @Value("${app.email.enabled:true}")
//     private boolean emailEnabled;

//     @Value("${app.base.url:http://localhost:3000}")
//     private String baseUrl;

//    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

//     // ==================== PRIVATE HELPER ====================

//     private void sendHtmlEmail(String to, String subject, String body) {
//         try {
//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(to);
//             helper.setSubject(subject);
//             helper.setText(body, true);
//             mailSender.send(message);
//             log.info("✅ Email sent to: {}", to);
//         } catch (MessagingException e) {
//             log.error("❌ Email send failed to: {} - {}", to, e.getMessage());
//         }
//     }

//  private String formatDateTime(LocalDateTime dateTime) {
//     if (dateTime == null) return "Not set";
//     return dateTime.format(DISPLAY_FMT);
// }

//     private int calculateWorkingDays(LocalDateTime start, LocalDateTime end) {
//         java.time.LocalDate startDate = start.toLocalDate();
//         java.time.LocalDate endDate = end.toLocalDate();
//         int workingDays = 0;
//         java.time.LocalDate current = startDate.plusDays(1);
//         while (!current.isAfter(endDate)) {
//             java.time.DayOfWeek dayOfWeek = current.getDayOfWeek();
//             if (dayOfWeek != java.time.DayOfWeek.SATURDAY &&
//                     dayOfWeek != java.time.DayOfWeek.SUNDAY) {
//                 workingDays++;
//             }
//             current = current.plusDays(1);
//         }
//         return workingDays;
//     }

//     // ==================== RFQ: APPROVAL PENDING ====================

//     @Async
//     public void sendApprovalPendingEmail(
//             RFQ rfq,
//             HierarchyUser approver,
//             HierarchyLevel level,
//             Integer sequenceOrder,
//             Integer totalLevels) {

//         if (!emailEnabled) {
//             log.info("📧 Email disabled - skipping approval notification");
//             return;
//         }

//         try {
//             log.info("=".repeat(80));
//             log.info("📧 [SENDING APPROVAL EMAIL]");
//             log.info("  RFQ: {} ({})", rfq.getRfqNumber(), rfq.getRfqTitle());
//             log.info("  To: {} ({})", approver.getFullName(), approver.getEmail());
//             log.info("  Level: {} (Order: {})", level.getLevelName(), level.getLevelOrder());
//             log.info("  Sequence: {}/{}", sequenceOrder, totalLevels);

//             Context context = new Context();
//             context.setVariable("approverName", approver.getFullName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("rfqDescription", rfq.getRfqDescription());
//             context.setVariable("buyerName", rfq.getBuyer().getCompanyName());
//             context.setVariable("createdBy", rfq.getCreatedByUser().getEmail());
//             context.setVariable("levelName", level.getLevelName());
//             context.setVariable("sequenceOrder", sequenceOrder);
//             context.setVariable("totalLevels", totalLevels);
//             context.setVariable("isLastLevel", sequenceOrder.equals(totalLevels));
//             context.setVariable("createdDate", formatDateTime(rfq.getCreatedAt()));
//             context.setVariable("approvalUrl", baseUrl + "/approvals/pending/" + rfq.getId());
//             context.setVariable("priority", rfq.getPriority().toString());
//             context.setVariable("itemsCount", rfq.getItems() != null ? rfq.getItems().size() : 0);
//             context.setVariable("suppliersCount", rfq.getSelectedSuppliers() != null ?
//                     rfq.getSelectedSuppliers().size() : 0);

//             String htmlContent = templateEngine.process("email/approval-pending", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(approver.getEmail());
//             helper.setSubject("🔔 RFQ Approval Required - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Email sent successfully to {}", approver.getEmail());
//             log.info("=".repeat(80));

//         } catch (MessagingException e) {
//             log.error("❌ Failed to send approval email to {}", approver.getEmail(), e);
//         }
//     }

//     // ==================== RFQ: APPROVAL FORWARDED ====================

//     @Async
//     public void sendApprovalForwardedEmail(
//             RFQ rfq,
//             HierarchyUser previousApprover,
//             HierarchyUser nextApprover,
//             HierarchyLevel nextLevel,
//             String comments) {

//         if (!emailEnabled) return;

//         try {
//             log.info("📧 [APPROVAL FORWARDED] From {} to {}",
//                     previousApprover.getFullName(), nextApprover.getFullName());

//             Context context = new Context();
//             context.setVariable("approverName", nextApprover.getFullName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("previousApprover", previousApprover.getFullName());
//             context.setVariable("previousLevel", previousApprover.getHierarchyLevel().getLevelName());
//             context.setVariable("currentLevel", nextLevel.getLevelName());
//             context.setVariable("comments", comments != null ? comments : "No comments");
//             context.setVariable("approvalUrl", baseUrl + "/approvals/pending/" + rfq.getId());
//             context.setVariable("approvedDate", formatDateTime(LocalDateTime.now()));

//             String htmlContent = templateEngine.process("email/approval-forwarded", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(nextApprover.getEmail());
//             helper.setSubject("🔔 RFQ Forwarded for Approval - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Forwarded notification sent to {}", nextApprover.getEmail());

//         } catch (MessagingException e) {
//             log.error("❌ Failed to send forwarded email", e);
//         }
//     }

//     // ==================== RFQ: PUBLISHED ====================

//     @Async
//     public void sendRFQPublishedEmail(
//             RFQ rfq,
//             HierarchyUser finalApprover,
//             Set<Supplier> suppliers) {

//         if (!emailEnabled) return;

//         try {
//             log.info("=".repeat(80));
//             log.info("📧 [RFQ PUBLISHED - NOTIFYING STAKEHOLDERS]");
//             log.info("  RFQ: {}", rfq.getRfqNumber());
//             log.info("  Final Approver: {}", finalApprover.getFullName());
//             log.info("  Suppliers: {}", suppliers != null ? suppliers.size() : 0);

//             notifyCreatorOfPublication(rfq, finalApprover);
//             notifyApproversOfPublication(rfq);
//             notifySuppliersOfNewRFQ(rfq, suppliers);

//             log.info("  ✅ All publication notifications sent");
//             log.info("=".repeat(80));

//         } catch (Exception e) {
//             log.error("❌ Error in published notifications", e);
//         }
//     }

//     private void notifyCreatorOfPublication(RFQ rfq, HierarchyUser finalApprover) {
//         try {
//             User creator = rfq.getCreatedByUser();

//             Context context = new Context();
//             context.setVariable("creatorName", creator.getFirstName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("finalApprover", finalApprover.getFullName());
//             context.setVariable("approvedDate", formatDateTime(rfq.getApprovalDate()));
//             context.setVariable("dueDate", formatDateTime(rfq.getDueDate()));
//             context.setVariable("deliveryDate", formatDateTime(rfq.getItemRequiredDate()));
//             context.setVariable("suppliersCount", rfq.getSelectedSuppliers().size());
//             context.setVariable("rfqUrl", baseUrl + "/rfq/view/" + rfq.getId());

//             String htmlContent = templateEngine.process("email/rfq-published-creator", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(creator.getEmail());
//             helper.setSubject("✅ RFQ Published - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Creator notified: {}", creator.getEmail());

//         } catch (MessagingException e) {
//             log.error("❌ Failed to notify creator", e);
//         }
//     }

//     private void notifyApproversOfPublication(RFQ rfq) {
//         try {
//             Set<User> approvers = rfq.getApprovers();
//             if (approvers == null || approvers.isEmpty()) {
//                 log.info("   ℹ️ No approvers to notify");
//                 return;
//             }
//             log.info("   📧 Notifying {} approvers of publication", approvers.size());
//             for (User approver : approvers) {
//                 try {
//                     sendApproverPublicationNotification(rfq, approver);
//                 } catch (Exception e) {
//                     log.error("   ❌ Failed to notify approver: {} - {}", approver.getEmail(), e.getMessage());
//                 }
//             }
//         } catch (Exception e) {
//             log.error("❌ Error in notifyApproversOfPublication", e);
//         }
//     }

//     private void sendApproverPublicationNotification(RFQ rfq, User approver) {
//         try {
//             Context context = new Context();
//             context.setVariable("approverName", approver.getFirstName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("publishedDate", formatDateTime(LocalDateTime.now()));
//             context.setVariable("suppliersCount", rfq.getSelectedSuppliers().size());

//             String htmlContent = templateEngine.process("email/rfq-published-approver", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(approver.getEmail());
//             helper.setSubject("✅ RFQ Published - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//         } catch (MessagingException e) {
//             log.error("❌ Failed to notify approver {}", approver.getEmail(), e);
//         }
//     }

//     private void notifySuppliersOfNewRFQ(RFQ rfq, Set<Supplier> suppliers) {
//         log.info("📧 [NOTIFYING SUPPLIERS] Starting supplier notifications");
//         log.info("   Total Suppliers: {}", suppliers != null ? suppliers.size() : 0);

//         if (suppliers == null || suppliers.isEmpty()) {
//             log.warn("   ⚠️ No suppliers to notify!");
//             return;
//         }

//         int successCount = 0;
//         int failureCount = 0;

//         for (Supplier supplier : suppliers) {
//             try {
//                 log.info("   📧 Processing supplier: {}", supplier.getCompanyName());

//                 String supplierEmail = supplier.getContactPersonEmail();
//                 if (supplierEmail == null || supplierEmail.trim().isEmpty()) {
//                     log.error("   ❌ Supplier {} has no email - skipping", supplier.getCompanyName());
//                     failureCount++;
//                     continue;
//                 }

//                 int itemsCount = 0;
//                 try {
//                     if (rfq.getItems() != null) itemsCount = rfq.getItems().size();
//                 } catch (Exception e) {
//                     log.warn("   ⚠️ Could not get items count: {}", e.getMessage());
//                 }

//                 Context context = new Context();
//                 context.setVariable("supplierName", supplier.getCompanyName());
//                 context.setVariable("rfqNumber", rfq.getRfqNumber());
//                 context.setVariable("rfqTitle", rfq.getRfqTitle());
//                 context.setVariable("rfqDescription", rfq.getRfqDescription());
//                 context.setVariable("buyerName", rfq.getBuyer().getCompanyName());
//                 context.setVariable("dueDate", formatDateTime(rfq.getDueDate()));
//                 context.setVariable("deliveryDate", formatDateTime(rfq.getItemRequiredDate()));
//                 context.setVariable("itemsCount", itemsCount);
//                 context.setVariable("supplierPortalUrl", baseUrl + "/supplier/rfq/" + rfq.getId());
//                 context.setVariable("priority", rfq.getPriority() != null ? rfq.getPriority().toString() : "MEDIUM");

//                 String htmlContent = templateEngine.process("email/supplier-new-rfq", context);

//                 MimeMessage message = mailSender.createMimeMessage();
//                 MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//                 helper.setFrom(fromEmail);
//                 helper.setTo(supplierEmail);
//                 helper.setSubject("🆕 New RFQ - " + rfq.getRfqNumber());
//                 helper.setText(htmlContent, true);
//                 mailSender.send(message);

//                 log.info("   ✅ Supplier notified: {} ({})", supplier.getCompanyName(), supplierEmail);
//                 successCount++;

//             } catch (MessagingException e) {
//                 log.error("   ❌ Failed to notify supplier: {} - {}", supplier.getCompanyName(), e.getMessage());
//                 failureCount++;
//             } catch (Exception e) {
//                 log.error("   ❌ Unexpected error notifying supplier: {}", supplier.getCompanyName(), e);
//                 failureCount++;
//             }
//         }

//         log.info("📧 [SUPPLIER NOTIFICATIONS COMPLETE] ✅ Success: {} | ❌ Failed: {}", successCount, failureCount);
//     }

//     // ==================== RFQ: REJECTED ====================

//     @Async
//     public void sendRFQRejectedEmail(RFQ rfq, HierarchyUser rejector, String rejectRemarks) {
//         if (!emailEnabled) return;

//         try {
//             log.info("📧 [RFQ REJECTED] RFQ: {}, Rejected by: {}", rfq.getRfqNumber(), rejector.getFullName());

//             User creator = rfq.getCreatedByUser();

//             Context context = new Context();
//             context.setVariable("creatorName", creator.getFirstName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("rejectorName", rejector.getFullName());
//             context.setVariable("rejectorLevel", rejector.getHierarchyLevel().getLevelName());
//             context.setVariable("rejectRemarks", rejectRemarks != null ? rejectRemarks : "No remarks provided");
//             context.setVariable("rejectedDate", formatDateTime(LocalDateTime.now()));
//             context.setVariable("rfqUrl", baseUrl + "/rfq/view/" + rfq.getId());

//             String htmlContent = templateEngine.process("email/rfq-rejected", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(creator.getEmail());
//             helper.setSubject("❌ RFQ Rejected - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Rejection notification sent to creator");

//         } catch (MessagingException e) {
//             log.error("❌ Failed to send rejection email", e);
//         }
//     }

//     // ==================== RFQ: RETURNED FOR REVISION ====================

//     @Async
//     public void sendRFQReturnedForRevisionEmail(RFQ rfq, HierarchyUser returner, String revisionComments) {
//         if (!emailEnabled) return;

//         try {
//             log.info("📧 [RFQ RETURNED FOR REVISION] RFQ: {}, By: {}", rfq.getRfqNumber(), returner.getFullName());

//             User creator = rfq.getCreatedByUser();

//             Context context = new Context();
//             context.setVariable("creatorName", creator.getFirstName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("returnerName", returner.getFullName());
//             context.setVariable("returnerLevel", returner.getHierarchyLevel().getLevelName());
//             context.setVariable("revisionComments", revisionComments != null ? revisionComments : "No comments");
//             context.setVariable("returnedDate", formatDateTime(LocalDateTime.now()));
//             context.setVariable("editUrl", baseUrl + "/rfq/edit/" + rfq.getId());

//             String htmlContent = templateEngine.process("email/rfq-returned-revision", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(creator.getEmail());
//             helper.setSubject("🔄 RFQ Returned for Revision - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Revision notification sent to creator");

//         } catch (MessagingException e) {
//             log.error("❌ Failed to send revision email", e);
//         }
//     }

//     // ==================== RFQ: ON HOLD ====================

//     @Async
//     public void sendRFQOnHoldEmail(RFQ rfq, HierarchyUser holder, String holdRemarks) {
//         if (!emailEnabled) return;

//         try {
//             log.info("📧 [RFQ ON HOLD] By: {}", holder.getFullName());

//             User creator = rfq.getCreatedByUser();

//             Context context = new Context();
//             context.setVariable("creatorName", creator.getFirstName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("holderName", holder.getFullName());
//             context.setVariable("holderLevel", holder.getHierarchyLevel().getLevelName());
//             context.setVariable("holdRemarks", holdRemarks);
//             context.setVariable("holdDate", formatDateTime(LocalDateTime.now()));

//             String htmlContent = templateEngine.process("email/rfq-on-hold", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(creator.getEmail());
//             helper.setSubject("⏸️ RFQ On Hold - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Hold notification sent");

//         } catch (MessagingException e) {
//             log.error("❌ Failed to send hold email", e);
//         }
//     }

//     // ==================== RFQ: HOLD RELEASED ====================

//     @Async
//     public void sendRFQHoldReleasedEmail(
//             RFQ rfq,
//             HierarchyUser releaser,
//             HierarchyUser nextApprover,
//             String releaseRemarks) {

//         if (!emailEnabled) return;

//         try {
//             log.info("📧 [HOLD RELEASED] By: {}, Next: {}", releaser.getFullName(), nextApprover.getFullName());

//             Context context = new Context();
//             context.setVariable("approverName", nextApprover.getFullName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("releaserName", releaser.getFullName());
//             context.setVariable("releaseRemarks", releaseRemarks != null ? releaseRemarks : "No remarks");
//             context.setVariable("releasedDate", formatDateTime(LocalDateTime.now()));
//             context.setVariable("approvalUrl", baseUrl + "/approvals/pending/" + rfq.getId());

//             String htmlContent = templateEngine.process("email/rfq-hold-released", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(nextApprover.getEmail());
//             helper.setSubject("▶️ RFQ Hold Released - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             notifyCreatorOfHoldRelease(rfq, releaser, releaseRemarks);

//             log.info("  ✅ Release notifications sent");

//         } catch (MessagingException e) {
//             log.error("❌ Failed to send release email", e);
//         }
//     }

//     private void notifyCreatorOfHoldRelease(RFQ rfq, HierarchyUser releaser, String remarks) {
//         try {
//             User creator = rfq.getCreatedByUser();

//             Context context = new Context();
//             context.setVariable("creatorName", creator.getFirstName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("releaserName", releaser.getFullName());
//             context.setVariable("releaseRemarks", remarks);

//             String htmlContent = templateEngine.process("email/rfq-hold-released-creator", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(creator.getEmail());
//             helper.setSubject("▶️ RFQ Hold Released - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//         } catch (MessagingException e) {
//             log.error("❌ Failed to notify creator of hold release", e);
//         }
//     }

//     // ==================== RFQ: APPROVAL REMINDER ====================

//     @Async
//     public void sendApprovalReminderEmail(
//             RFQ rfq,
//             HierarchyUser approver,
//             HierarchyLevel level,
//             LocalDateTime pendingSince) {

//         if (!emailEnabled) {
//             log.info("📧 Email disabled - skipping reminder notification");
//             return;
//         }

//         try {
//             log.info("⏰ [SENDING REMINDER EMAIL] RFQ: {}, To: {}", rfq.getRfqNumber(), approver.getEmail());

//             int workingDaysPassed = calculateWorkingDays(pendingSince, LocalDateTime.now());

//             Context context = new Context();
//             context.setVariable("approverName", approver.getFullName());
//             context.setVariable("rfqNumber", rfq.getRfqNumber());
//             context.setVariable("rfqTitle", rfq.getRfqTitle());
//             context.setVariable("rfqDescription", rfq.getRfqDescription());
//             context.setVariable("buyerName", rfq.getBuyer().getCompanyName());
//             context.setVariable("levelName", level.getLevelName());
//             context.setVariable("pendingSince", formatDateTime(pendingSince));
//             context.setVariable("workingDaysPassed", workingDaysPassed);
//             context.setVariable("priority", rfq.getPriority().toString());

//             String htmlContent = templateEngine.process("email/approval-reminder", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(approver.getEmail());
//             helper.setSubject("⏰ REMINDER: RFQ Approval Pending - " + rfq.getRfqNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Reminder email sent successfully to {}", approver.getEmail());

//         } catch (MessagingException e) {
//             log.error("❌ Failed to send reminder email to {}", approver.getEmail(), e);
//         }
//     }

//     // ==================== PO: APPROVAL PENDING ====================

//     public void sendPOApprovalPendingEmail(PurchaseOrder po, HierarchyUser approver,
//                                            HierarchyLevel level, int currentStep, int totalSteps) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] Approval pending — To: {}", approver.getEmail());

//             String subject = "Action Required: Purchase Order " + po.getPoNumber() + " Pending Your Approval";
//             String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                     + "<h2 style='color:#1a73e8;'>Purchase Order Approval Required</h2>"
//                     + "<p>Dear <strong>" + approver.getFullName() + "</strong>,</p>"
//                     + "<p>A Purchase Order requires your approval.</p>"
//                     + "<table style='border-collapse:collapse;width:100%;'>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>"
//                     + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Approval Level</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + level.getLevelName()
//                     + " (Step " + currentStep + " of " + totalSteps + ")</td></tr>"
//                     + "</table>"
//                     + "<p style='margin-top:20px;'>Please log in to review and take action.</p>"
//                     + "<p style='color:#888;font-size:12px;'>This is an automated notification.</p>"
//                     + "</body></html>";

//             sendHtmlEmail(approver.getEmail(), subject, body);

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO approval pending email", e);
//         }
//     }

//     // ==================== PO: APPROVAL FORWARDED ====================

//     public void sendPOApprovalForwardedEmail(PurchaseOrder po, HierarchyUser fromApprover,
//                                              HierarchyUser toApprover, HierarchyLevel nextLevel,
//                                              String comments) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] Forwarding approval to: {}", toApprover.getEmail());

//             String subject = "Action Required: Purchase Order " + po.getPoNumber() + " Forwarded for Your Approval";
//             String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                     + "<h2 style='color:#1a73e8;'>Purchase Order Forwarded for Approval</h2>"
//                     + "<p>Dear <strong>" + toApprover.getFullName() + "</strong>,</p>"
//                     + "<p>PO <strong>" + po.getPoNumber() + "</strong> has been approved by "
//                     + fromApprover.getFullName() + " and is now pending your approval at <strong>"
//                     + nextLevel.getLevelName() + "</strong> level.</p>"
//                     + "<table style='border-collapse:collapse;width:100%;'>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>"
//                     + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
//                     + (comments != null && !comments.isEmpty()
//                         ? "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Comments</strong></td>"
//                           + "<td style='padding:8px;border:1px solid #ddd;'>" + comments + "</td></tr>" : "")
//                     + "</table>"
//                     + "<p style='margin-top:20px;'>Please log in to review and take action.</p>"
//                     + "</body></html>";

//             sendHtmlEmail(toApprover.getEmail(), subject, body);

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO forwarded email", e);
//         }
//     }

//     // ==================== PO: FULLY APPROVED ====================

//     public void sendPOApprovedEmail(PurchaseOrder po, HierarchyUser finalApprover) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] PO approved — {}", po.getPoNumber());

//             HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
//             if (creator == null) {
//                 log.warn("  ⚠️ PO creator not found for ID: {}", po.getCreatedByUserId());
//                 return;
//             }

//             String subject = "✅ Purchase Order " + po.getPoNumber() + " — Fully Approved";
//             String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                     + "<h2 style='color:#34a853;'>Purchase Order Approved!</h2>"
//                     + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
//                     + "<p>Your Purchase Order has been fully approved and is ready to be sent to the supplier.</p>"
//                     + "<table style='border-collapse:collapse;width:100%;'>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>"
//                     + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Approved By</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + finalApprover.getFullName() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Approved On</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
//                     + "</table>"
//                     + "<p style='margin-top:20px;'>You can now proceed to send the PO to the supplier.</p>"
//                     + "</body></html>";

//             sendHtmlEmail(creator.getEmail(), subject, body);

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO approved email", e);
//         }
//     }

//     // ==================== PO: REJECTED ====================

//     public void sendPORejectedEmail(PurchaseOrder po, HierarchyUser rejector, String rejectRemarks) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] PO rejected — {}", po.getPoNumber());

//             HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
//             if (creator == null) return;

//             String subject = "❌ Purchase Order " + po.getPoNumber() + " — Rejected";
//             String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                     + "<h2 style='color:#ea4335;'>Purchase Order Rejected</h2>"
//                     + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
//                     + "<p>Your Purchase Order has been permanently rejected. No resubmission is allowed.</p>"
//                     + "<table style='border-collapse:collapse;width:100%;'>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Rejected By</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + rejector.getFullName() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Rejection Reason</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;color:#ea4335;'>" + rejectRemarks + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Rejected On</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
//                     + "</table>"
//                     + "<p style='margin-top:20px;color:#888;'>Please create a new PO if required.</p>"
//                     + "</body></html>";

//             sendHtmlEmail(creator.getEmail(), subject, body);

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO rejected email", e);
//         }
//     }

//     // ==================== PO: RETURNED FOR REVISION ====================

//     public void sendPOReturnedForRevisionEmail(PurchaseOrder po, HierarchyUser returner, String returnRemarks) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] PO returned for revision — {}", po.getPoNumber());

//             HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
//             if (creator == null) return;

//             String subject = "🔄 Purchase Order " + po.getPoNumber() + " — Returned for Revision";
//             String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                     + "<h2 style='color:#fbbc04;'>Purchase Order Returned for Revision</h2>"
//                     + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
//                     + "<p>Your Purchase Order has been returned for revision. Please make the requested changes and resubmit.</p>"
//                     + "<table style='border-collapse:collapse;width:100%;'>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Returned By</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + returner.getFullName() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Revision Required</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;color:#f57c00;'>" + returnRemarks + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Returned On</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
//                     + "</table>"
//                     + "<p style='margin-top:20px;'>Please log in, update the PO, and resubmit for approval.</p>"
//                     + "</body></html>";

//             sendHtmlEmail(creator.getEmail(), subject, body);

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO revision email", e);
//         }
//     }

//     // ==================== PO: ON HOLD ====================

//     public void sendPOOnHoldEmail(PurchaseOrder po, HierarchyUser holder, String holdRemarks) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] PO on hold — {}", po.getPoNumber());

//             HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
//             if (creator == null) return;

//             String subject = "⏸️ Purchase Order " + po.getPoNumber() + " — Put on Hold";
//             String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                     + "<h2 style='color:#ff7043;'>Purchase Order On Hold</h2>"
//                     + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
//                     + "<p>Your Purchase Order has been put on hold temporarily.</p>"
//                     + "<table style='border-collapse:collapse;width:100%;'>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Held By</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + holder.getFullName() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Reason</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + holdRemarks + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Hold Date</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
//                     + "</table>"
//                     + "<p style='margin-top:20px;'>You will be notified once the hold is released.</p>"
//                     + "</body></html>";

//             sendHtmlEmail(creator.getEmail(), subject, body);

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO hold email", e);
//         }
//     }

//     // ==================== PO: HOLD RELEASED ====================

//     public void sendPOHoldReleasedEmail(PurchaseOrder po, HierarchyUser releaser,
//                                         HierarchyUser nextApprover, String releaseRemarks) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] PO hold released — {}", po.getPoNumber());

//             // Notify next approver
//             if (nextApprover != null) {
//                 String subject = "▶️ Purchase Order " + po.getPoNumber() + " — Hold Released, Action Required";
//                 String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                         + "<h2 style='color:#34a853;'>Purchase Order Hold Released</h2>"
//                         + "<p>Dear <strong>" + nextApprover.getFullName() + "</strong>,</p>"
//                         + "<p>The hold on PO <strong>" + po.getPoNumber() + "</strong> has been released by "
//                         + releaser.getFullName() + ". Your approval is now required.</p>"
//                         + (releaseRemarks != null && !releaseRemarks.isEmpty()
//                             ? "<p><strong>Release Remarks:</strong> " + releaseRemarks + "</p>" : "")
//                         + "<p>Please log in to review and take action.</p>"
//                         + "</body></html>";
//                 sendHtmlEmail(nextApprover.getEmail(), subject, body);
//             }

//             // Notify creator
//             HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
//             if (creator != null) {
//                 String subject = "▶️ Purchase Order " + po.getPoNumber() + " — Hold Released";
//                 String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                         + "<h2 style='color:#34a853;'>Purchase Order Hold Released</h2>"
//                         + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
//                         + "<p>The hold on your PO <strong>" + po.getPoNumber() + "</strong> has been released by "
//                         + releaser.getFullName() + ". Approval is now continuing.</p>"
//                         + "</body></html>";
//                 sendHtmlEmail(creator.getEmail(), subject, body);
//             }

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO hold released email", e);
//         }
//     }

//     // ==================== PO: ISSUED TO SUPPLIER ====================

//     @Async
//     public void sendPOIssuedToSupplierEmail(PurchaseOrder po) {
//         if (!emailEnabled) return;
//         try {
//             log.info("📧 [PO EMAIL] PO issued to supplier — {}", po.getPoNumber());

//             // 1. Notify Supplier
//             notifySupplierOfPOIssued(po);

//             // 2. Notify RFQ Creator
//             notifyRfqCreatorOfPOIssued(po);

//             // 3. Notify PO Creator (if different from RFQ creator)
//             notifyPoCreatorOfPOIssued(po);

//             log.info("  ✅ All PO issued notifications sent for {}", po.getPoNumber());

//         } catch (Exception e) {
//             log.error("❌ Failed to send PO issued emails for {}", po.getPoNumber(), e);
//         }
//     }

//     private void notifySupplierOfPOIssued(PurchaseOrder po) {
//         try {
//             Supplier supplier = po.getSupplier();
//             if (supplier == null) {
//                 log.warn("  ⚠️ No supplier on PO {}", po.getPoNumber());
//                 return;
//             }

//             String supplierEmail = supplier.getContactPersonEmail();
//             if (supplierEmail == null || supplierEmail.trim().isEmpty()) {
//                 log.warn("  ⚠️ Supplier {} has no email — skipping", supplier.getCompanyName());
//                 return;
//             }

//             Context context = new Context();
//             context.setVariable("supplierName", supplier.getCompanyName());
//             context.setVariable("poNumber", po.getPoNumber());
//             context.setVariable("buyerName", po.getBuyer() != null ? po.getBuyer().getCompanyName() : "N/A");
//             context.setVariable("grandTotal", po.getGrandTotal());
//             context.setVariable("issueDate", formatDateTime(LocalDateTime.now()));
//             context.setVariable("paymentTerms", po.getPaymentTerms() != null ? po.getPaymentTerms() : "As per agreement");
//             context.setVariable("deliveryTerms", po.getDeliveryTerms() != null ? po.getDeliveryTerms() : "As per agreement");
//             context.setVariable("supplierPortalUrl", baseUrl + "/supplier/po/" + po.getId());

//             String htmlContent = templateEngine.process("email/po-issued-supplier", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(supplierEmail);
//             helper.setSubject("📦 New Purchase Order Issued - " + po.getPoNumber());
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ Supplier notified: {} ({})", supplier.getCompanyName(), supplierEmail);

//         } catch (MessagingException e) {
//             log.error("  ❌ Failed to notify supplier for PO {}", po.getPoNumber(), e);
//         }
//     }

//     private void notifyRfqCreatorOfPOIssued(PurchaseOrder po) {
//         try {
//             if (po.getRfq() == null || po.getRfq().getCreatedByUser() == null) {
//                 log.warn("  ⚠️ No RFQ or RFQ creator found for PO {}", po.getPoNumber());
//                 return;
//             }

//             User rfqCreator = po.getRfq().getCreatedByUser();

//             Context context = new Context();
//             context.setVariable("rfqCreatorName", rfqCreator.getFirstName());
//             context.setVariable("rfqNumber", po.getRfq().getRfqNumber());
//             context.setVariable("poNumber", po.getPoNumber());
//             context.setVariable("supplierName", po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A");
//             context.setVariable("grandTotal", po.getGrandTotal());
//             context.setVariable("issueDate", formatDateTime(LocalDateTime.now()));
//             context.setVariable("poUrl", baseUrl + "/rfq/po-view/" + po.getId());

//             String htmlContent = templateEngine.process("email/po-created-from-rfq", context);

//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//             helper.setFrom(fromEmail);
//             helper.setTo(rfqCreator.getEmail());
//             helper.setSubject("📑 PO Issued to Supplier - " + po.getPoNumber() + " (RFQ: " + po.getRfq().getRfqNumber() + ")");
//             helper.setText(htmlContent, true);
//             mailSender.send(message);

//             log.info("  ✅ RFQ creator notified: {}", rfqCreator.getEmail());

//         } catch (MessagingException e) {
//             log.error("  ❌ Failed to notify RFQ creator for PO {}", po.getPoNumber(), e);
//         }
//     }

//     private void notifyPoCreatorOfPOIssued(PurchaseOrder po) {
//         try {
//             // Skip if PO creator is same as RFQ creator (already notified above)
//             if (po.getRfq() != null && po.getRfq().getCreatedByUser() != null) {
//                 Long rfqCreatorId = po.getRfq().getCreatedByUser().getId();
//                 if (rfqCreatorId != null && rfqCreatorId.equals(po.getCreatedByUserId())) {
//                     log.info("  ℹ️ PO creator same as RFQ creator — skipping duplicate email");
//                     return;
//                 }
//             }

//             HierarchyUser poCreator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
//             if (poCreator == null) {
//                 log.warn("  ⚠️ PO creator not found for ID: {}", po.getCreatedByUserId());
//                 return;
//             }

//             String subject = "📦 Purchase Order " + po.getPoNumber() + " — Issued to Supplier";
//             String body = "<html><body style='font-family:Arial,sans-serif;'>"
//                     + "<h2 style='color:#1a73e8;'>Purchase Order Issued to Supplier</h2>"
//                     + "<p>Dear <strong>" + poCreator.getFullName() + "</strong>,</p>"
//                     + "<p>The Purchase Order has been successfully issued to the supplier.</p>"
//                     + "<table style='border-collapse:collapse;width:100%;'>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>PO Number</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + po.getPoNumber() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Supplier</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>"
//                     + (po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A") + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Grand Total</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>&#8377;" + po.getGrandTotal() + "</td></tr>"
//                     + "<tr><td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>Issued On</strong></td>"
//                     + "<td style='padding:8px;border:1px solid #ddd;'>" + formatDateTime(LocalDateTime.now()) + "</td></tr>"
//                     + "</table>"
//                     + "</body></html>";

//             sendHtmlEmail(poCreator.getEmail(), subject, body);
//             log.info("  ✅ PO creator notified: {}", poCreator.getEmail());

//         } catch (Exception e) {
//             log.error("  ❌ Failed to notify PO creator for PO {}", po.getPoNumber(), e);
//         }
//     }

//      @Async
//     public void sendInvoiceSubmittedToRfqCreator(Invoice invoice) {
//         if (invoice.getRfqCreatorEmail() == null) return;

//         Context ctx = new Context();
//         ctx.setVariable("rfqCreatorName",  invoice.getRfqCreatorName());
//         ctx.setVariable("invoiceNumber",   invoice.getInvoiceNumber());
//         ctx.setVariable("supplierName",    invoice.getSupplierName());
//         ctx.setVariable("poNumber",        invoice.getPoNumber());
//         ctx.setVariable("totalAmount",     invoice.getTotalAmount());
//         ctx.setVariable("currency",        invoice.getCurrency());
//         ctx.setVariable("submittedAt",     invoice.getUpdatedAt() != null
//                                                ? invoice.getUpdatedAt().format(DISPLAY_FMT) : "");
//         ctx.setVariable("reviewLink",      baseUrl + "/buyer/invoices/" + invoice.getId());

//         String body = templateEngine.process("invoice-submitted-to-buyer", ctx);
//         sendHtmlEmail(invoice.getRfqCreatorEmail(),
//                       "New Invoice Submitted: " + invoice.getInvoiceNumber(), body);
//     }

//     // ── 2. RFQ creator approved → notify supplier ─────────────────────────────

//     @Async
//     public void sendInvoiceApprovedToSupplier(Invoice invoice, String buyerName) {
//         if (invoice.getSupplierEmail() == null) return;

//         Context ctx = new Context();
//         ctx.setVariable("supplierName",  invoice.getSupplierName());
//         ctx.setVariable("invoiceNumber", invoice.getInvoiceNumber());
//         ctx.setVariable("poNumber",      invoice.getPoNumber());
//         ctx.setVariable("totalAmount",   invoice.getTotalAmount());
//         ctx.setVariable("currency",      invoice.getCurrency());
//         ctx.setVariable("approvedBy",    buyerName);
//         ctx.setVariable("approvedAt",    invoice.getApprovedRejectedAt() != null
//                                              ? invoice.getApprovedRejectedAt().format(DISPLAY_FMT) : "");
//         ctx.setVariable("remarks",       invoice.getApprovalRemarks());
//         ctx.setVariable("invoiceLink",   baseUrl + "/supplier/invoices/" + invoice.getId());

//         String body = templateEngine.process("invoice-approved-supplier", ctx);
//         sendHtmlEmail(invoice.getSupplierEmail(),
//                       "Invoice Approved: " + invoice.getInvoiceNumber(), body);
//     }

//     // ── 3. RFQ creator requests changes → notify supplier ────────────────────

//     @Async
//     public void sendInvoiceResubmissionRequest(Invoice invoice, String remarks) {
//         if (invoice.getSupplierEmail() == null) return;

//         Context ctx = new Context();
//         ctx.setVariable("supplierName",  invoice.getSupplierName());
//         ctx.setVariable("invoiceNumber", invoice.getInvoiceNumber());
//         ctx.setVariable("poNumber",      invoice.getPoNumber());
//         ctx.setVariable("buyerName",     invoice.getApprovedRejectedBy());
//         ctx.setVariable("remarks",       remarks);
//         ctx.setVariable("invoiceLink",   baseUrl + "/supplier/invoices/" + invoice.getId());

//         String body = templateEngine.process("invoice-resubmission-request", ctx);
//         sendHtmlEmail(invoice.getSupplierEmail(),
//                       "Action Required – Resubmit Invoice: " + invoice.getInvoiceNumber(), body);
//     }

//     // ── 4. Supplier resubmitted → notify RFQ creator ─────────────────────────

//     @Async
//     public void sendInvoiceResubmittedToRfqCreator(Invoice invoice, String supplierRemarks) {
//         if (invoice.getRfqCreatorEmail() == null) return;

//         Context ctx = new Context();
//         ctx.setVariable("rfqCreatorName",   invoice.getRfqCreatorName());
//         ctx.setVariable("invoiceNumber",    invoice.getInvoiceNumber());
//         ctx.setVariable("supplierName",     invoice.getSupplierName());
//         ctx.setVariable("poNumber",         invoice.getPoNumber());
//         ctx.setVariable("totalAmount",      invoice.getTotalAmount());
//         ctx.setVariable("currency",         invoice.getCurrency());
//         ctx.setVariable("supplierRemarks",  supplierRemarks);
//         ctx.setVariable("resubmitCount",    invoice.getResubmitCount());
//         ctx.setVariable("reviewLink",       baseUrl + "/buyer/invoices/" + invoice.getId());

//         String body = templateEngine.process("invoice-resubmitted-to-buyer", ctx);
//         sendHtmlEmail(invoice.getRfqCreatorEmail(),
//                       "Invoice Resubmitted: " + invoice.getInvoiceNumber(), body);
//     }

//     // ── 5. Permanent rejection → notify supplier ──────────────────────────────

//     @Async
//     public void sendInvoiceRejectedPermanently(Invoice invoice, String remarks) {
//         if (invoice.getSupplierEmail() == null) return;

//         Context ctx = new Context();
//         ctx.setVariable("supplierName",  invoice.getSupplierName());
//         ctx.setVariable("invoiceNumber", invoice.getInvoiceNumber());
//         ctx.setVariable("poNumber",      invoice.getPoNumber());
//         ctx.setVariable("buyerName",     invoice.getApprovedRejectedBy());
//         ctx.setVariable("remarks",       remarks);

//         String body = templateEngine.process("invoice-rejected-permanently", ctx);
//         sendHtmlEmail(invoice.getSupplierEmail(),
//                       "Invoice Rejected: " + invoice.getInvoiceNumber(), body);
//     }

//     // ── 6. Marked as paid → notify supplier ───────────────────────────────────

//     @Async
//     public void sendInvoiceMarkedPaid(Invoice invoice, String paymentReference) {
//         if (invoice.getSupplierEmail() == null) return;

//         Context ctx = new Context();
//         ctx.setVariable("supplierName",      invoice.getSupplierName());
//         ctx.setVariable("invoiceNumber",     invoice.getInvoiceNumber());
//         ctx.setVariable("poNumber",          invoice.getPoNumber());
//         ctx.setVariable("totalAmount",       invoice.getTotalAmount());
//         ctx.setVariable("currency",          invoice.getCurrency());
//         ctx.setVariable("paymentReference",  paymentReference);
//         ctx.setVariable("paidAt",            invoice.getPaidAt() != null
//                                                  ? invoice.getPaidAt().format(DISPLAY_FMT) : "");
//         ctx.setVariable("paidBy",            invoice.getPaidBy());
//         ctx.setVariable("invoiceLink",       baseUrl + "/supplier/invoices/" + invoice.getId());

//         String body = templateEngine.process("invoice-paid-supplier", ctx);
//         sendHtmlEmail(invoice.getSupplierEmail(),
//                       "Payment Processed: " + invoice.getInvoiceNumber(), body);
//     }
// }

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

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // ==================== PRIVATE HELPERS ====================

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
            log.error("❌ Email send failed to: {} — {}", to, e.getMessage());
        }
    }

    private String fmt(LocalDateTime dt) {
        return dt != null ? dt.format(DISPLAY_FMT) : "—";
    }

    private int calculateWorkingDays(LocalDateTime start, LocalDateTime end) {
        java.time.LocalDate s = start.toLocalDate();
        java.time.LocalDate e = end.toLocalDate();
        int days = 0;
        java.time.LocalDate cur = s.plusDays(1);
        while (!cur.isAfter(e)) {
            java.time.DayOfWeek d = cur.getDayOfWeek();
            if (d != java.time.DayOfWeek.SATURDAY && d != java.time.DayOfWeek.SUNDAY) days++;
            cur = cur.plusDays(1);
        }
        return days;
    }

    // ==========================================================
    // ✅ NEW — SUPPLIER REGISTRATION → NOTIFY BUYER / APPROVER
    // ==========================================================

    @Async
    public void sendSupplierRegistrationToBuyer(Supplier supplier,
                                                 HierarchyUser firstApprover,
                                                 HierarchyLevel firstLevel) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [SUPPLIER REGISTRATION EMAIL] To: {}", firstApprover.getEmail());

            String body = "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:620px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#1a237e,#1565c0);padding:30px;border-radius:10px 10px 0 0;text-align:center;'>"
                + "<h1 style='color:white;margin:0;font-size:22px;'>🏭 New Supplier Registration</h1>"
                + "<p style='color:rgba(255,255,255,0.85);margin:6px 0 0;'>Pending Your Approval</p>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;'>"
                + "<p>Dear <strong>" + firstApprover.getFullName() + "</strong>,</p>"
                + "<p>A new supplier has registered on the platform and requires your approval.</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
                + row("Company Name",  supplier.getCompanyName())
                + row("Contact Person", safe(supplier.getContactPersonName()))
                + row("Contact Email",  safe(supplier.getContactPersonEmail()))
                + row("Contact Phone",  safe(supplier.getContactPersonPhone()))
                + row("Industry",       safe(supplier.getIndustrySector()))
                + row("Approval Level", firstLevel.getLevelName() + " (You)")
                + row("Registered On",  fmt(LocalDateTime.now()))
                + "</table>"
                + "<div style='background:#e3f2fd;border-left:4px solid #1565c0;padding:15px;margin:20px 0;border-radius:4px;'>"
                + "<strong>⚡ Action Required</strong><br>"
                + "Please log in to the portal and review this supplier's details before approving, rejecting, or requesting more information."
                + "</div>"
                + "<p style='text-align:center;margin-top:25px;'>"
                + "<a href='" + baseUrl + "/pending-approvals' "
                + "style='background:#1565c0;color:white;padding:12px 30px;border-radius:6px;text-decoration:none;font-weight:bold;'>"
                + "Review Supplier →</a>"
                + "</p>"
                + "<p style='color:#888;font-size:12px;margin-top:30px;'>This is an automated notification. Do not reply to this email.</p>"
                + "</div></div></body></html>";

            sendHtmlEmail(firstApprover.getEmail(),
                    "🏭 New Supplier Registration — " + supplier.getCompanyName() + " (Approval Pending)",
                    body);
        } catch (Exception e) {
            log.error("❌ Failed to send supplier registration email", e);
        }
    }

    // ==========================================================
    // ✅ NEW — SUPPLIER FULLY APPROVED → NOTIFY SUPPLIER
    // ==========================================================

    @Async
    public void sendSupplierFullyApproved(Supplier supplier,
                                           HierarchyUser finalApprover,
                                           HierarchyLevel finalLevel) {
        if (!emailEnabled) return;
        if (supplier.getContactPersonEmail() == null) return;
        try {
            log.info("📧 [SUPPLIER APPROVED EMAIL] To: {}", supplier.getContactPersonEmail());

            String body = "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:620px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#1b5e20,#2e7d32);padding:30px;border-radius:10px 10px 0 0;text-align:center;'>"
                + "<h1 style='color:white;margin:0;font-size:24px;'>🎉 Congratulations!</h1>"
                + "<p style='color:rgba(255,255,255,0.9);margin:6px 0 0;'>Your supplier account has been approved</p>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;'>"
                + "<p>Dear <strong>" + safe(supplier.getContactPersonName()) + "</strong>,</p>"
                + "<div style='background:#e8f5e9;border-left:4px solid #2e7d32;padding:20px;border-radius:4px;margin:20px 0;text-align:center;'>"
                + "<h2 style='color:#1b5e20;margin:0;'>✅ Account Fully Approved</h2>"
                + "<p style='margin:8px 0 0;'>Your company <strong>" + supplier.getCompanyName() + "</strong> is now an active supplier.</p>"
                + "</div>"
                + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
                + row("Company Name",   supplier.getCompanyName())
                + row("Approved By",    finalApprover.getFullName() + " (" + finalLevel.getLevelName() + ")")
                + row("Approved On",    fmt(LocalDateTime.now()))
                + "</table>"
                + "<p><strong>What's Next?</strong></p>"
                + "<ol style='line-height:2;'>"
                + "<li>Log in to the supplier portal</li>"
                + "<li>You will now receive RFQ (Request for Quotation) invitations</li>"
                + "<li>Submit competitive quotes to win purchase orders</li>"
                + "<li>Track your orders, invoices and payments in one place</li>"
                + "</ol>"
                + "<p style='text-align:center;margin-top:25px;'>"
                + "<a href='" + baseUrl + "/login' "
                + "style='background:#2e7d32;color:white;padding:12px 30px;border-radius:6px;text-decoration:none;font-weight:bold;'>"
                + "Login to Portal →</a>"
                + "</p>"
                + "<p style='color:#888;font-size:12px;margin-top:30px;'>This is an automated notification from the Procurement System.</p>"
                + "</div></div></body></html>";

            sendHtmlEmail(supplier.getContactPersonEmail(),
                    "✅ Supplier Account Approved — " + supplier.getCompanyName(),
                    body);
        } catch (Exception e) {
            log.error("❌ Failed to send supplier approved email", e);
        }
    }

    // ==========================================================
    // ✅ NEW — NEED MORE INFO → NOTIFY SUPPLIER
    // ==========================================================

    @Async
    public void sendNeedMoreInfoToSupplier(Supplier supplier,
                                            HierarchyUser requester,
                                            HierarchyLevel level,
                                            String infoRequest) {
        if (!emailEnabled) return;
        if (supplier.getContactPersonEmail() == null) return;
        try {
            log.info("📧 [NEED MORE INFO EMAIL] To: {}", supplier.getContactPersonEmail());

            String body = "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:620px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#e65100,#f57c00);padding:30px;border-radius:10px 10px 0 0;text-align:center;'>"
                + "<h1 style='color:white;margin:0;font-size:22px;'>❓ Additional Information Required</h1>"
                + "<p style='color:rgba(255,255,255,0.9);margin:6px 0 0;'>Regarding Your Supplier Registration</p>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;'>"
                + "<p>Dear <strong>" + safe(supplier.getContactPersonName()) + "</strong>,</p>"
                + "<p>Our approval team has reviewed your supplier registration for <strong>"
                + supplier.getCompanyName() + "</strong> and requires some additional information before proceeding.</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
                + row("Company Name",     supplier.getCompanyName())
                + row("Requested By",     requester.getFullName())
                + row("Approver Level",   level.getLevelName())
                + row("Request Date",     fmt(LocalDateTime.now()))
                + "</table>"
                + "<div style='background:#fff3e0;border-left:4px solid #f57c00;padding:20px;border-radius:4px;margin:20px 0;'>"
                + "<p style='margin:0;font-weight:bold;color:#e65100;'>📋 Information Requested:</p>"
                + "<p style='margin:10px 0 0;font-size:15px;line-height:1.6;'>" + infoRequest + "</p>"
                + "</div>"
                + "<div style='background:#e3f2fd;border-left:4px solid #1565c0;padding:15px;border-radius:4px;margin:20px 0;'>"
                + "<strong>📌 How to Respond:</strong><br>"
                + "Please reply directly to this email or contact us at <a href='mailto:" + fromEmail + "'>"
                + fromEmail + "</a> with the requested information.<br><br>"
                + "Your approval process will continue once we receive the required details."
                + "</div>"
                + "<p>If you have any questions, please contact our procurement team.</p>"
                + "<p style='color:#888;font-size:12px;margin-top:30px;'>This is an automated notification from the Procurement System.</p>"
                + "</div></div></body></html>";

            sendHtmlEmail(supplier.getContactPersonEmail(),
                    "❓ Additional Info Required — " + supplier.getCompanyName() + " Registration",
                    body);
        } catch (Exception e) {
            log.error("❌ Failed to send need-more-info email", e);
        }
    }

    // ==========================================================
    // ✅ NEW — SUPPLIER REJECTED → NOTIFY SUPPLIER
    // ==========================================================

    @Async
    public void sendSupplierRejected(Supplier supplier,
                                      HierarchyUser rejector,
                                      HierarchyLevel level,
                                      String remarks) {
        if (!emailEnabled) return;
        if (supplier.getContactPersonEmail() == null) return;
        try {
            log.info("📧 [SUPPLIER REJECTED EMAIL] To: {}", supplier.getContactPersonEmail());

            String body = "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:620px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#b71c1c,#c62828);padding:30px;border-radius:10px 10px 0 0;text-align:center;'>"
                + "<h1 style='color:white;margin:0;font-size:22px;'>❌ Supplier Registration Update</h1>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;'>"
                + "<p>Dear <strong>" + safe(supplier.getContactPersonName()) + "</strong>,</p>"
                + "<p>We regret to inform you that the supplier registration for <strong>"
                + supplier.getCompanyName() + "</strong> has not been approved at this time.</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
                + row("Company Name",   supplier.getCompanyName())
                + row("Rejected By",    rejector.getFullName() + " (" + level.getLevelName() + ")")
                + row("Rejection Date", fmt(LocalDateTime.now()))
                + "</table>"
                + "<div style='background:#ffebee;border-left:4px solid #c62828;padding:15px;border-radius:4px;margin:20px 0;'>"
                + "<strong>Reason for Rejection:</strong><br>"
                + "<p style='margin:8px 0 0;'>" + remarks + "</p>"
                + "</div>"
                + "<p>If you believe this decision was made in error, or if you would like to provide additional "
                + "information, please contact our procurement team at "
                + "<a href='mailto:" + fromEmail + "'>" + fromEmail + "</a>.</p>"
                + "<p style='color:#888;font-size:12px;margin-top:30px;'>This is an automated notification from the Procurement System.</p>"
                + "</div></div></body></html>";

            sendHtmlEmail(supplier.getContactPersonEmail(),
                    "❌ Supplier Registration — " + supplier.getCompanyName(),
                    body);
        } catch (Exception e) {
            log.error("❌ Failed to send supplier rejected email", e);
        }
    }

    // ==========================================================
    // ✅ NEW — SUPPLIER ON HOLD → NOTIFY SUPPLIER
    // ==========================================================

    @Async
    public void sendSupplierOnHold(Supplier supplier,
                                    HierarchyUser holder,
                                    HierarchyLevel level,
                                    String holdRemarks) {
        if (!emailEnabled) return;
        if (supplier.getContactPersonEmail() == null) return;
        try {
            log.info("📧 [SUPPLIER HOLD EMAIL] To: {}", supplier.getContactPersonEmail());

            String body = "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:620px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#e65100,#ff8f00);padding:30px;border-radius:10px 10px 0 0;text-align:center;'>"
                + "<h1 style='color:white;margin:0;font-size:22px;'>⏸️ Supplier Registration — On Hold</h1>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;'>"
                + "<p>Dear <strong>" + safe(supplier.getContactPersonName()) + "</strong>,</p>"
                + "<p>The approval process for <strong>" + supplier.getCompanyName()
                + "</strong> has been temporarily put on hold.</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
                + row("Company Name", supplier.getCompanyName())
                + row("Held By",      holder.getFullName() + " (" + level.getLevelName() + ")")
                + row("Hold Date",    fmt(LocalDateTime.now()))
                + "</table>"
                + "<div style='background:#fff3e0;border-left:4px solid #ff8f00;padding:15px;border-radius:4px;margin:20px 0;'>"
                + "<strong>Reason for Hold:</strong><br>"
                + "<p style='margin:8px 0 0;'>" + holdRemarks + "</p>"
                + "</div>"
                + "<p>Your approval will resume once the hold is released. "
                + "You will receive an email notification when the process continues.</p>"
                + "<p style='color:#888;font-size:12px;margin-top:30px;'>This is an automated notification from the Procurement System.</p>"
                + "</div></div></body></html>";

            sendHtmlEmail(supplier.getContactPersonEmail(),
                    "⏸️ Registration On Hold — " + supplier.getCompanyName(),
                    body);
        } catch (Exception e) {
            log.error("❌ Failed to send supplier hold email", e);
        }
    }

    // ==========================================================
    // ✅ NEW — SUPPLIER APPROVAL FORWARDED → NOTIFY NEXT APPROVER
    // ==========================================================

    @Async
    public void sendSupplierApprovalForwarded(Supplier supplier,
                                               HierarchyUser previousApprover,
                                               HierarchyUser nextApprover,
                                               HierarchyLevel nextLevel,
                                               String comments) {
        if (!emailEnabled) return;
        try {
            log.info("📧 [SUPPLIER FORWARDED EMAIL] To: {}", nextApprover.getEmail());

            String body = "<html><body style='font-family:Arial,sans-serif;color:#333;'>"
                + "<div style='max-width:620px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#1a237e,#283593);padding:30px;border-radius:10px 10px 0 0;text-align:center;'>"
                + "<h1 style='color:white;margin:0;font-size:22px;'>➡️ Supplier Approval — Forwarded to You</h1>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:30px;border-radius:0 0 10px 10px;'>"
                + "<p>Dear <strong>" + nextApprover.getFullName() + "</strong>,</p>"
                + "<p>A supplier approval has been forwarded to you for review at the <strong>"
                + nextLevel.getLevelName() + "</strong> level.</p>"
                + "<div style='background:#e8eaf6;border-left:4px solid #283593;padding:15px;border-radius:4px;margin:20px 0;'>"
                + "<strong>✅ Previously Approved By:</strong> "
                + previousApprover.getFullName() + " (" + previousApprover.getHierarchyLevel().getLevelName() + ")"
                + (comments != null && !comments.isBlank()
                        ? "<br><strong>Comments:</strong> " + comments : "")
                + "</div>"
                + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
                + row("Company Name",   supplier.getCompanyName())
                + row("Contact Person", safe(supplier.getContactPersonName()))
                + row("Contact Email",  safe(supplier.getContactPersonEmail()))
                + row("Your Level",     nextLevel.getLevelName())
                + row("Forwarded On",   fmt(LocalDateTime.now()))
                + "</table>"
                + "<p style='text-align:center;margin-top:25px;'>"
                + "<a href='" + baseUrl + "/pending-approvals' "
                + "style='background:#283593;color:white;padding:12px 30px;border-radius:6px;text-decoration:none;font-weight:bold;'>"
                + "Review Supplier →</a>"
                + "</p>"
                + "<p style='color:#888;font-size:12px;margin-top:30px;'>This is an automated notification.</p>"
                + "</div></div></body></html>";

            sendHtmlEmail(nextApprover.getEmail(),
                    "➡️ Supplier Approval Forwarded — " + supplier.getCompanyName(),
                    body);
        } catch (Exception e) {
            log.error("❌ Failed to send supplier forwarded email", e);
        }
    }

    // ==========================================================
    // EXISTING RFQ EMAIL METHODS (unchanged)
    // ==========================================================

    @Async
    public void sendApprovalPendingEmail(RFQ rfq, HierarchyUser approver,
                                          HierarchyLevel level, Integer sequenceOrder, Integer totalLevels) {
        if (!emailEnabled) return;
        try {
            Context context = new Context();
            context.setVariable("approverName",   approver.getFullName());
            context.setVariable("rfqNumber",      rfq.getRfqNumber());
            context.setVariable("rfqTitle",        rfq.getRfqTitle());
            context.setVariable("rfqDescription",  rfq.getRfqDescription());
            context.setVariable("buyerName",       rfq.getBuyer().getCompanyName());
            context.setVariable("createdBy",       rfq.getCreatedByUser().getEmail());
            context.setVariable("levelName",       level.getLevelName());
            context.setVariable("sequenceOrder",   sequenceOrder);
            context.setVariable("totalLevels",     totalLevels);
            context.setVariable("isLastLevel",     sequenceOrder.equals(totalLevels));
            context.setVariable("createdDate",     fmt(rfq.getCreatedAt()));
            context.setVariable("approvalUrl",     baseUrl + "/approvals/pending/" + rfq.getId());
            context.setVariable("priority",        rfq.getPriority().toString());
            context.setVariable("itemsCount",      rfq.getItems() != null ? rfq.getItems().size() : 0);
            context.setVariable("suppliersCount",  rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0);

            String html = templateEngine.process("email/approval-pending", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(approver.getEmail());
            h.setSubject("🔔 RFQ Approval Required - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to send RFQ approval pending email", e);
        }
    }

    @Async
    public void sendApprovalForwardedEmail(RFQ rfq, HierarchyUser previousApprover,
                                            HierarchyUser nextApprover, HierarchyLevel nextLevel, String comments) {
        if (!emailEnabled) return;
        try {
            Context context = new Context();
            context.setVariable("approverName",    nextApprover.getFullName());
            context.setVariable("rfqNumber",       rfq.getRfqNumber());
            context.setVariable("rfqTitle",         rfq.getRfqTitle());
            context.setVariable("previousApprover", previousApprover.getFullName());
            context.setVariable("previousLevel",    previousApprover.getHierarchyLevel().getLevelName());
            context.setVariable("currentLevel",     nextLevel.getLevelName());
            context.setVariable("comments",         comments != null ? comments : "No comments");
            context.setVariable("approvalUrl",      baseUrl + "/approvals/pending/" + rfq.getId());
            context.setVariable("approvedDate",     fmt(LocalDateTime.now()));

            String html = templateEngine.process("email/approval-forwarded", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(nextApprover.getEmail());
            h.setSubject("🔔 RFQ Forwarded for Approval - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to send RFQ forwarded email", e);
        }
    }

    @Async
    public void sendRFQPublishedEmail(RFQ rfq, HierarchyUser finalApprover, Set<Supplier> suppliers) {
        if (!emailEnabled) return;
        try {
            notifyCreatorOfPublication(rfq, finalApprover);
            notifyApproversOfPublication(rfq);
            notifySuppliersOfNewRFQ(rfq, suppliers);
        } catch (Exception e) {
            log.error("❌ Error in RFQ published notifications", e);
        }
    }

    private void notifyCreatorOfPublication(RFQ rfq, HierarchyUser finalApprover) {
        try {
            User creator = rfq.getCreatedByUser();
            Context context = new Context();
            context.setVariable("creatorName",    creator.getFirstName());
            context.setVariable("rfqNumber",      rfq.getRfqNumber());
            context.setVariable("rfqTitle",        rfq.getRfqTitle());
            context.setVariable("finalApprover",   finalApprover.getFullName());
            context.setVariable("approvedDate",    fmt(rfq.getApprovalDate()));
            context.setVariable("dueDate",         fmt(rfq.getDueDate()));
            context.setVariable("deliveryDate",    fmt(rfq.getItemRequiredDate()));
            context.setVariable("suppliersCount",  rfq.getSelectedSuppliers().size());
            context.setVariable("rfqUrl",          baseUrl + "/rfq/view/" + rfq.getId());

            String html = templateEngine.process("email/rfq-published-creator", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(creator.getEmail());
            h.setSubject("✅ RFQ Published - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to notify creator of publication", e);
        }
    }

    private void notifyApproversOfPublication(RFQ rfq) {
        Set<User> approvers = rfq.getApprovers();
        if (approvers == null || approvers.isEmpty()) return;
        for (User approver : approvers) {
            try {
                Context context = new Context();
                context.setVariable("approverName",    approver.getFirstName());
                context.setVariable("rfqNumber",       rfq.getRfqNumber());
                context.setVariable("rfqTitle",         rfq.getRfqTitle());
                context.setVariable("publishedDate",    fmt(LocalDateTime.now()));
                context.setVariable("suppliersCount",   rfq.getSelectedSuppliers().size());

                String html = templateEngine.process("email/rfq-published-approver", context);
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
                h.setFrom(fromEmail);
                h.setTo(approver.getEmail());
                h.setSubject("✅ RFQ Published - " + rfq.getRfqNumber());
                h.setText(html, true);
                mailSender.send(msg);
            } catch (Exception e) {
                log.error("❌ Failed to notify approver {}", approver.getEmail(), e);
            }
        }
    }

    private void notifySuppliersOfNewRFQ(RFQ rfq, Set<Supplier> suppliers) {
        if (suppliers == null || suppliers.isEmpty()) return;
        for (Supplier supplier : suppliers) {
            try {
                String supplierEmail = supplier.getContactPersonEmail();
                if (supplierEmail == null || supplierEmail.trim().isEmpty()) continue;

                Context context = new Context();
                context.setVariable("supplierName",      supplier.getCompanyName());
                context.setVariable("rfqNumber",         rfq.getRfqNumber());
                context.setVariable("rfqTitle",           rfq.getRfqTitle());
                context.setVariable("rfqDescription",     rfq.getRfqDescription());
                context.setVariable("buyerName",          rfq.getBuyer().getCompanyName());
                context.setVariable("dueDate",            fmt(rfq.getDueDate()));
                context.setVariable("deliveryDate",       fmt(rfq.getItemRequiredDate()));
                context.setVariable("itemsCount",         rfq.getItems() != null ? rfq.getItems().size() : 0);
                context.setVariable("supplierPortalUrl",  baseUrl + "/supplier/rfq/" + rfq.getId());
                context.setVariable("priority",           rfq.getPriority() != null ? rfq.getPriority().toString() : "MEDIUM");

                String html = templateEngine.process("email/supplier-new-rfq", context);
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
                h.setFrom(fromEmail);
                h.setTo(supplierEmail);
                h.setSubject("🆕 New RFQ - " + rfq.getRfqNumber());
                h.setText(html, true);
                mailSender.send(msg);
            } catch (Exception e) {
                log.error("❌ Failed to notify supplier: {}", supplier.getCompanyName(), e);
            }
        }
    }

    @Async
    public void sendRFQRejectedEmail(RFQ rfq, HierarchyUser rejector, String rejectRemarks) {
        if (!emailEnabled) return;
        try {
            User creator = rfq.getCreatedByUser();
            Context context = new Context();
            context.setVariable("creatorName",    creator.getFirstName());
            context.setVariable("rfqNumber",      rfq.getRfqNumber());
            context.setVariable("rfqTitle",        rfq.getRfqTitle());
            context.setVariable("rejectorName",    rejector.getFullName());
            context.setVariable("rejectorLevel",   rejector.getHierarchyLevel().getLevelName());
            context.setVariable("rejectRemarks",   rejectRemarks != null ? rejectRemarks : "No remarks");
            context.setVariable("rejectedDate",    fmt(LocalDateTime.now()));
            context.setVariable("rfqUrl",          baseUrl + "/rfq/view/" + rfq.getId());

            String html = templateEngine.process("email/rfq-rejected", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(creator.getEmail());
            h.setSubject("❌ RFQ Rejected - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to send RFQ rejected email", e);
        }
    }

    @Async
    public void sendRFQReturnedForRevisionEmail(RFQ rfq, HierarchyUser returner, String revisionComments) {
        if (!emailEnabled) return;
        try {
            User creator = rfq.getCreatedByUser();
            Context context = new Context();
            context.setVariable("creatorName",        creator.getFirstName());
            context.setVariable("rfqNumber",          rfq.getRfqNumber());
            context.setVariable("rfqTitle",            rfq.getRfqTitle());
            context.setVariable("returnerName",        returner.getFullName());
            context.setVariable("returnerLevel",       returner.getHierarchyLevel().getLevelName());
            context.setVariable("revisionComments",    revisionComments != null ? revisionComments : "No comments");
            context.setVariable("returnedDate",        fmt(LocalDateTime.now()));
            context.setVariable("editUrl",             baseUrl + "/rfq/edit/" + rfq.getId());

            String html = templateEngine.process("email/rfq-returned-revision", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(creator.getEmail());
            h.setSubject("🔄 RFQ Returned for Revision - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to send RFQ revision email", e);
        }
    }

    @Async
    public void sendRFQOnHoldEmail(RFQ rfq, HierarchyUser holder, String holdRemarks) {
        if (!emailEnabled) return;
        try {
            User creator = rfq.getCreatedByUser();
            Context context = new Context();
            context.setVariable("creatorName",  creator.getFirstName());
            context.setVariable("rfqNumber",    rfq.getRfqNumber());
            context.setVariable("rfqTitle",      rfq.getRfqTitle());
            context.setVariable("holderName",    holder.getFullName());
            context.setVariable("holderLevel",   holder.getHierarchyLevel().getLevelName());
            context.setVariable("holdRemarks",   holdRemarks);
            context.setVariable("holdDate",      fmt(LocalDateTime.now()));

            String html = templateEngine.process("email/rfq-on-hold", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(creator.getEmail());
            h.setSubject("⏸️ RFQ On Hold - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to send RFQ hold email", e);
        }
    }

    @Async
    public void sendRFQHoldReleasedEmail(RFQ rfq, HierarchyUser releaser,
                                          HierarchyUser nextApprover, String releaseRemarks) {
        if (!emailEnabled) return;
        try {
            Context context = new Context();
            context.setVariable("approverName",  nextApprover.getFullName());
            context.setVariable("rfqNumber",     rfq.getRfqNumber());
            context.setVariable("rfqTitle",       rfq.getRfqTitle());
            context.setVariable("releaserName",   releaser.getFullName());
            context.setVariable("releaseRemarks", releaseRemarks != null ? releaseRemarks : "No remarks");
            context.setVariable("releasedDate",   fmt(LocalDateTime.now()));
            context.setVariable("approvalUrl",    baseUrl + "/approvals/pending/" + rfq.getId());

            String html = templateEngine.process("email/rfq-hold-released", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(nextApprover.getEmail());
            h.setSubject("▶️ RFQ Hold Released - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);

            notifyCreatorOfHoldRelease(rfq, releaser, releaseRemarks);
        } catch (MessagingException e) {
            log.error("❌ Failed to send RFQ hold released email", e);
        }
    }

    private void notifyCreatorOfHoldRelease(RFQ rfq, HierarchyUser releaser, String remarks) {
        try {
            User creator = rfq.getCreatedByUser();
            Context context = new Context();
            context.setVariable("creatorName",    creator.getFirstName());
            context.setVariable("rfqNumber",      rfq.getRfqNumber());
            context.setVariable("releaserName",   releaser.getFullName());
            context.setVariable("releaseRemarks", remarks);

            String html = templateEngine.process("email/rfq-hold-released-creator", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(creator.getEmail());
            h.setSubject("▶️ RFQ Hold Released - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to notify creator of hold release", e);
        }
    }

    @Async
    public void sendApprovalReminderEmail(RFQ rfq, HierarchyUser approver,
                                           HierarchyLevel level, LocalDateTime pendingSince) {
        if (!emailEnabled) return;
        try {
            int days = calculateWorkingDays(pendingSince, LocalDateTime.now());
            Context context = new Context();
            context.setVariable("approverName",      approver.getFullName());
            context.setVariable("rfqNumber",         rfq.getRfqNumber());
            context.setVariable("rfqTitle",           rfq.getRfqTitle());
            context.setVariable("rfqDescription",     rfq.getRfqDescription());
            context.setVariable("buyerName",          rfq.getBuyer().getCompanyName());
            context.setVariable("levelName",          level.getLevelName());
            context.setVariable("pendingSince",       fmt(pendingSince));
            context.setVariable("workingDaysPassed",  days);
            context.setVariable("priority",           rfq.getPriority().toString());

            String html = templateEngine.process("email/approval-reminder", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(approver.getEmail());
            h.setSubject("⏰ REMINDER: RFQ Approval Pending - " + rfq.getRfqNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to send reminder email", e);
        }
    }

    // ==================== PO EMAILS (unchanged) ====================

    public void sendPOApprovalPendingEmail(PurchaseOrder po, HierarchyUser approver,
                                            HierarchyLevel level, int currentStep, int totalSteps) {
        if (!emailEnabled) return;
        try {
            String subject = "Action Required: Purchase Order " + po.getPoNumber() + " Pending Your Approval";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2 style='color:#1a73e8;'>Purchase Order Approval Required</h2>"
                + "<p>Dear <strong>" + approver.getFullName() + "</strong>,</p>"
                + "<p>A Purchase Order requires your approval.</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + poRow("PO Number",       po.getPoNumber())
                + poRow("Supplier",        po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A")
                + poRow("Grand Total",     "&#8377;" + po.getGrandTotal())
                + poRow("Approval Level",  level.getLevelName() + " (Step " + currentStep + " of " + totalSteps + ")")
                + "</table>"
                + "<p style='margin-top:20px;'>Please log in to review and take action.</p>"
                + "</body></html>";
            sendHtmlEmail(approver.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("❌ Failed to send PO approval pending email", e);
        }
    }

    public void sendPOApprovalForwardedEmail(PurchaseOrder po, HierarchyUser fromApprover,
                                              HierarchyUser toApprover, HierarchyLevel nextLevel, String comments) {
        if (!emailEnabled) return;
        try {
            String subject = "Action Required: PO " + po.getPoNumber() + " Forwarded for Your Approval";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2 style='color:#1a73e8;'>Purchase Order Forwarded for Approval</h2>"
                + "<p>Dear <strong>" + toApprover.getFullName() + "</strong>,</p>"
                + "<p>PO <strong>" + po.getPoNumber() + "</strong> has been approved by "
                + fromApprover.getFullName() + " and is now pending your approval at <strong>"
                + nextLevel.getLevelName() + "</strong> level.</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + poRow("PO Number",  po.getPoNumber())
                + poRow("Supplier",   po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A")
                + poRow("Grand Total","&#8377;" + po.getGrandTotal())
                + (comments != null && !comments.isEmpty() ? poRow("Comments", comments) : "")
                + "</table>"
                + "<p style='margin-top:20px;'>Please log in to review and take action.</p>"
                + "</body></html>";
            sendHtmlEmail(toApprover.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("❌ Failed to send PO forwarded email", e);
        }
    }

    public void sendPOApprovedEmail(PurchaseOrder po, HierarchyUser finalApprover) {
        if (!emailEnabled) return;
        try {
            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) return;
            String subject = "✅ Purchase Order " + po.getPoNumber() + " — Fully Approved";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2 style='color:#34a853;'>Purchase Order Approved!</h2>"
                + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                + "<p>Your Purchase Order has been fully approved.</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + poRow("PO Number",    po.getPoNumber())
                + poRow("Supplier",     po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A")
                + poRow("Grand Total",  "&#8377;" + po.getGrandTotal())
                + poRow("Approved By",  finalApprover.getFullName())
                + poRow("Approved On",  fmt(LocalDateTime.now()))
                + "</table></body></html>";
            sendHtmlEmail(creator.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("❌ Failed to send PO approved email", e);
        }
    }

    public void sendPORejectedEmail(PurchaseOrder po, HierarchyUser rejector, String rejectRemarks) {
        if (!emailEnabled) return;
        try {
            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) return;
            String subject = "❌ Purchase Order " + po.getPoNumber() + " — Rejected";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2 style='color:#ea4335;'>Purchase Order Rejected</h2>"
                + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + poRow("PO Number",        po.getPoNumber())
                + poRow("Rejected By",      rejector.getFullName())
                + poRow("Rejection Reason", rejectRemarks)
                + poRow("Rejected On",      fmt(LocalDateTime.now()))
                + "</table></body></html>";
            sendHtmlEmail(creator.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("❌ Failed to send PO rejected email", e);
        }
    }

    public void sendPOReturnedForRevisionEmail(PurchaseOrder po, HierarchyUser returner, String returnRemarks) {
        if (!emailEnabled) return;
        try {
            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) return;
            String subject = "🔄 Purchase Order " + po.getPoNumber() + " — Returned for Revision";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2 style='color:#fbbc04;'>PO Returned for Revision</h2>"
                + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + poRow("PO Number",        po.getPoNumber())
                + poRow("Returned By",      returner.getFullName())
                + poRow("Revision Required", returnRemarks)
                + poRow("Returned On",      fmt(LocalDateTime.now()))
                + "</table></body></html>";
            sendHtmlEmail(creator.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("❌ Failed to send PO revision email", e);
        }
    }

    public void sendPOOnHoldEmail(PurchaseOrder po, HierarchyUser holder, String holdRemarks) {
        if (!emailEnabled) return;
        try {
            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator == null) return;
            String subject = "⏸️ Purchase Order " + po.getPoNumber() + " — Put on Hold";
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2 style='color:#ff7043;'>Purchase Order On Hold</h2>"
                + "<p>Dear <strong>" + creator.getFullName() + "</strong>,</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + poRow("PO Number", po.getPoNumber())
                + poRow("Held By",   holder.getFullName())
                + poRow("Reason",    holdRemarks)
                + poRow("Hold Date", fmt(LocalDateTime.now()))
                + "</table></body></html>";
            sendHtmlEmail(creator.getEmail(), subject, body);
        } catch (Exception e) {
            log.error("❌ Failed to send PO hold email", e);
        }
    }

    public void sendPOHoldReleasedEmail(PurchaseOrder po, HierarchyUser releaser,
                                         HierarchyUser nextApprover, String releaseRemarks) {
        if (!emailEnabled) return;
        try {
            if (nextApprover != null) {
                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#34a853;'>PO Hold Released</h2>"
                    + "<p>Dear <strong>" + nextApprover.getFullName() + "</strong>,</p>"
                    + "<p>Hold on PO <strong>" + po.getPoNumber() + "</strong> released by "
                    + releaser.getFullName() + ". Your approval is now required.</p>"
                    + (releaseRemarks != null ? "<p><strong>Remarks:</strong> " + releaseRemarks + "</p>" : "")
                    + "</body></html>";
                sendHtmlEmail(nextApprover.getEmail(),
                        "▶️ PO Hold Released — " + po.getPoNumber(), body);
            }
            HierarchyUser creator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (creator != null) {
                String body = "<html><body style='font-family:Arial,sans-serif;'>"
                    + "<h2 style='color:#34a853;'>PO Hold Released</h2>"
                    + "<p>Dear <strong>" + creator.getFullName() + "</strong>, the hold on PO <strong>"
                    + po.getPoNumber() + "</strong> has been released by " + releaser.getFullName()
                    + ". Approval is now continuing.</p></body></html>";
                sendHtmlEmail(creator.getEmail(),
                        "▶️ PO Hold Released — " + po.getPoNumber(), body);
            }
        } catch (Exception e) {
            log.error("❌ Failed to send PO hold released email", e);
        }
    }

    @Async
    public void sendPOIssuedToSupplierEmail(PurchaseOrder po) {
        if (!emailEnabled) return;
        try {
            notifySupplierOfPOIssued(po);
            notifyRfqCreatorOfPOIssued(po);
            notifyPoCreatorOfPOIssued(po);
        } catch (Exception e) {
            log.error("❌ Failed to send PO issued emails", e);
        }
    }

    private void notifySupplierOfPOIssued(PurchaseOrder po) {
        try {
            Supplier supplier = po.getSupplier();
            if (supplier == null) return;
            String email = supplier.getContactPersonEmail();
            if (email == null || email.trim().isEmpty()) return;

            Context context = new Context();
            context.setVariable("supplierName",     supplier.getCompanyName());
            context.setVariable("poNumber",         po.getPoNumber());
            context.setVariable("buyerName",        po.getBuyer() != null ? po.getBuyer().getCompanyName() : "N/A");
            context.setVariable("grandTotal",       po.getGrandTotal());
            context.setVariable("issueDate",        fmt(LocalDateTime.now()));
            context.setVariable("paymentTerms",     po.getPaymentTerms() != null ? po.getPaymentTerms() : "As per agreement");
            context.setVariable("deliveryTerms",    po.getDeliveryTerms() != null ? po.getDeliveryTerms() : "As per agreement");
            context.setVariable("supplierPortalUrl", baseUrl + "/supplier/po/" + po.getId());

            String html = templateEngine.process("email/po-issued-supplier", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail); h.setTo(email);
            h.setSubject("📦 New Purchase Order Issued - " + po.getPoNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to notify supplier for PO issued", e);
        }
    }

    private void notifyRfqCreatorOfPOIssued(PurchaseOrder po) {
        try {
            if (po.getRfq() == null || po.getRfq().getCreatedByUser() == null) return;
            User rfqCreator = po.getRfq().getCreatedByUser();
            Context context = new Context();
            context.setVariable("rfqCreatorName", rfqCreator.getFirstName());
            context.setVariable("rfqNumber",      po.getRfq().getRfqNumber());
            context.setVariable("poNumber",       po.getPoNumber());
            context.setVariable("supplierName",   po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A");
            context.setVariable("grandTotal",     po.getGrandTotal());
            context.setVariable("issueDate",      fmt(LocalDateTime.now()));
            context.setVariable("poUrl",          baseUrl + "/rfq/po-view/" + po.getId());

            String html = templateEngine.process("email/po-created-from-rfq", context);
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");
            h.setFrom(fromEmail); h.setTo(rfqCreator.getEmail());
            h.setSubject("📑 PO Issued - " + po.getPoNumber());
            h.setText(html, true);
            mailSender.send(msg);
        } catch (MessagingException e) {
            log.error("❌ Failed to notify RFQ creator for PO issued", e);
        }
    }

    private void notifyPoCreatorOfPOIssued(PurchaseOrder po) {
        try {
            if (po.getRfq() != null && po.getRfq().getCreatedByUser() != null) {
                Long rfqCreatorId = po.getRfq().getCreatedByUser().getId();
                if (rfqCreatorId != null && rfqCreatorId.equals(po.getCreatedByUserId())) return;
            }
            HierarchyUser poCreator = hierarchyUserRepository.findById(po.getCreatedByUserId()).orElse(null);
            if (poCreator == null) return;
            String body = "<html><body style='font-family:Arial,sans-serif;'>"
                + "<h2 style='color:#1a73e8;'>PO Issued to Supplier</h2>"
                + "<p>Dear <strong>" + poCreator.getFullName() + "</strong>,</p>"
                + "<table style='border-collapse:collapse;width:100%;'>"
                + poRow("PO Number",  po.getPoNumber())
                + poRow("Supplier",   po.getSupplier() != null ? po.getSupplier().getCompanyName() : "N/A")
                + poRow("Grand Total","&#8377;" + po.getGrandTotal())
                + poRow("Issued On",  fmt(LocalDateTime.now()))
                + "</table></body></html>";
            sendHtmlEmail(poCreator.getEmail(), "📦 PO Issued — " + po.getPoNumber(), body);
        } catch (Exception e) {
            log.error("❌ Failed to notify PO creator", e);
        }
    }

    // ==================== INVOICE EMAILS ====================

    @Async
    public void sendInvoiceSubmittedToRfqCreator(Invoice invoice) {
        if (invoice.getRfqCreatorEmail() == null) return;
        Context ctx = new Context();
        ctx.setVariable("rfqCreatorName", invoice.getRfqCreatorName());
        ctx.setVariable("invoiceNumber",  invoice.getInvoiceNumber());
        ctx.setVariable("supplierName",   invoice.getSupplierName());
        ctx.setVariable("poNumber",       invoice.getPoNumber());
        ctx.setVariable("totalAmount",    invoice.getTotalAmount());
        ctx.setVariable("currency",       invoice.getCurrency());
        ctx.setVariable("submittedAt",    invoice.getUpdatedAt() != null ? invoice.getUpdatedAt().format(DISPLAY_FMT) : "");
        ctx.setVariable("reviewLink",     baseUrl + "/buyer/invoices/" + invoice.getId());
        String body = templateEngine.process("invoice-submitted-to-buyer", ctx);
        sendHtmlEmail(invoice.getRfqCreatorEmail(), "New Invoice Submitted: " + invoice.getInvoiceNumber(), body);
    }

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
        ctx.setVariable("approvedAt",    invoice.getApprovedRejectedAt() != null ? invoice.getApprovedRejectedAt().format(DISPLAY_FMT) : "");
        ctx.setVariable("remarks",       invoice.getApprovalRemarks());
        ctx.setVariable("invoiceLink",   baseUrl + "/supplier/invoices/" + invoice.getId());
        String body = templateEngine.process("invoice-approved-supplier", ctx);
        sendHtmlEmail(invoice.getSupplierEmail(), "Invoice Approved: " + invoice.getInvoiceNumber(), body);
    }

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
        sendHtmlEmail(invoice.getSupplierEmail(), "Action Required – Resubmit Invoice: " + invoice.getInvoiceNumber(), body);
    }

    @Async
    public void sendInvoiceResubmittedToRfqCreator(Invoice invoice, String supplierRemarks) {
        if (invoice.getRfqCreatorEmail() == null) return;
        Context ctx = new Context();
        ctx.setVariable("rfqCreatorName",  invoice.getRfqCreatorName());
        ctx.setVariable("invoiceNumber",   invoice.getInvoiceNumber());
        ctx.setVariable("supplierName",    invoice.getSupplierName());
        ctx.setVariable("poNumber",        invoice.getPoNumber());
        ctx.setVariable("totalAmount",     invoice.getTotalAmount());
        ctx.setVariable("currency",        invoice.getCurrency());
        ctx.setVariable("supplierRemarks", supplierRemarks);
        ctx.setVariable("resubmitCount",   invoice.getResubmitCount());
        ctx.setVariable("reviewLink",      baseUrl + "/buyer/invoices/" + invoice.getId());
        String body = templateEngine.process("invoice-resubmitted-to-buyer", ctx);
        sendHtmlEmail(invoice.getRfqCreatorEmail(), "Invoice Resubmitted: " + invoice.getInvoiceNumber(), body);
    }

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
        sendHtmlEmail(invoice.getSupplierEmail(), "Invoice Rejected: " + invoice.getInvoiceNumber(), body);
    }

    @Async
    public void sendInvoiceMarkedPaid(Invoice invoice, String paymentReference) {
        if (invoice.getSupplierEmail() == null) return;
        Context ctx = new Context();
        ctx.setVariable("supplierName",     invoice.getSupplierName());
        ctx.setVariable("invoiceNumber",    invoice.getInvoiceNumber());
        ctx.setVariable("poNumber",         invoice.getPoNumber());
        ctx.setVariable("totalAmount",      invoice.getTotalAmount());
        ctx.setVariable("currency",         invoice.getCurrency());
        ctx.setVariable("paymentReference", paymentReference);
        ctx.setVariable("paidAt",           invoice.getPaidAt() != null ? invoice.getPaidAt().format(DISPLAY_FMT) : "");
        ctx.setVariable("paidBy",           invoice.getPaidBy());
        ctx.setVariable("invoiceLink",      baseUrl + "/supplier/invoices/" + invoice.getId());
        String body = templateEngine.process("invoice-paid-supplier", ctx);
        sendHtmlEmail(invoice.getSupplierEmail(), "Payment Processed: " + invoice.getInvoiceNumber(), body);
    }

    // ==================== TABLE ROW HELPERS ====================

    private String row(String label, String value) {
        return "<tr>"
            + "<td style='padding:8px 12px;border:1px solid #e0e0e0;background:#f5f5f5;font-weight:bold;width:40%;'>" + label + "</td>"
            + "<td style='padding:8px 12px;border:1px solid #e0e0e0;'>" + (value != null ? value : "—") + "</td>"
            + "</tr>";
    }

    private String poRow(String label, String value) {
        return "<tr>"
            + "<td style='padding:8px;border:1px solid #ddd;background:#f5f5f5;'><strong>" + label + "</strong></td>"
            + "<td style='padding:8px;border:1px solid #ddd;'>" + (value != null ? value : "—") + "</td>"
            + "</tr>";
    }

    private String safe(String val) {
        return val != null ? val : "—";
    }
}