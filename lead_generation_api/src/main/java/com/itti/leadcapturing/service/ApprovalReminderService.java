package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ✅ SERVICE: Approval Reminder Scheduler
 * 
 * Sends automated email reminders to approvers who haven't responded
 * to pending RFQs within 2 WORKING DAYS.
 * 
 * Features:
 * - Runs daily at 9:00 AM
 * - Only counts working days (excludes weekends and holidays)
 * - Sends ONE reminder per approval
 * - Tracks reminder sent status to avoid duplicates
 */
@Service
public class ApprovalReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalReminderService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    @Autowired
    private DynamicRFQApprovalRepository approvalRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private HolidayService holidayService;

    // ==================== SCHEDULED TASK ====================

    /**
     * ⏰ SCHEDULED JOB
     * Runs every day at 9:00 AM
     * 
     * Cron expression: "0 0 9 * * ?" means:
     * - Second: 0
     * - Minute: 0
     * - Hour: 9
     * - Day of month: * (every day)
     * - Month: * (every month)
     * - Day of week: ? (any day)
     */
    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9:00 AM
    @Transactional
    public void sendPendingApprovalReminders() {
        logger.info("=".repeat(80));
        logger.info("⏰ [SCHEDULED JOB] Checking for pending approvals needing reminders");
        logger.info("   Time: {}", LocalDateTime.now().format(DATE_FORMAT));
        logger.info("=".repeat(80));

        try {
            // Get all PENDING approvals
            List<DynamicRFQApproval> pendingApprovals = approvalRepository
                    .findByStatus(ApprovalActionStatus.PENDING);

            logger.info("📊 Found {} total pending approvals", pendingApprovals.size());

            int remindersSent = 0;
            int alreadySent = 0;
            int notYetDue = 0;
            int errors = 0;

            for (DynamicRFQApproval approval : pendingApprovals) {
                try {
                    boolean sent = processApprovalReminder(approval);
                    
                    if (sent) {
                        remindersSent++;
                    } else {
                        // Check why it wasn't sent
                        if (approval.getReminderSentAt() != null) {
                            alreadySent++;
                        } else {
                            notYetDue++;
                        }
                    }
                    
                } catch (Exception e) {
                    logger.error("❌ Failed to process approval {}: {}", 
                               approval.getId(), e.getMessage());
                    errors++;
                }
            }

            // ==================== SUMMARY ====================
            logger.info("=".repeat(80));
            logger.info("✅ [REMINDER JOB COMPLETED]");
            logger.info("   📧 Reminders Sent: {}", remindersSent);
            logger.info("   ✓ Already Sent: {}", alreadySent);
            logger.info("   ⏳ Not Yet Due: {}", notYetDue);
            logger.info("   ❌ Errors: {}", errors);
            logger.info("=".repeat(80));

        } catch (Exception e) {
            logger.error("❌ [CRITICAL ERROR] Reminder job failed", e);
        }
    }

    // ==================== CORE LOGIC ====================

    /**
     * Process individual approval reminder
     * 
     * @return true if reminder was sent, false otherwise
     */
    private boolean processApprovalReminder(DynamicRFQApproval approval) {
        logger.debug("🔍 Processing approval ID: {}", approval.getId());

        // ✅ CHECK 1: Has reminder already been sent?
        if (approval.getReminderSentAt() != null) {
            logger.debug("   ℹ️ Reminder already sent on {}", 
                        approval.getReminderSentAt().format(DATE_FORMAT));
            return false;
        }

        // ✅ CHECK 2: Is approver assigned?
        if (approval.getApproverUser() == null) {
            logger.warn("   ⚠️ No approver assigned - skipping");
            return false;
        }

        // ✅ CHECK 3: Have 2 working days passed?
        LocalDateTime createdAt = approval.getCreatedAt();
        boolean shouldSend = holidayService.shouldSendReminder(createdAt);

        if (!shouldSend) {
            int workingDays = holidayService.calculateWorkingDaysBetween(
                createdAt, 
                LocalDateTime.now()
            );
            logger.debug("   ⏳ Only {} working days passed - reminder not due yet", workingDays);
            return false;
        }

        // ✅ SEND REMINDER
        logger.info("📧 [SENDING REMINDER]");
        logger.info("   RFQ: {} ({})", 
                   approval.getRfq().getRfqNumber(), 
                   approval.getRfq().getRfqTitle());
        logger.info("   Approver: {} ({})", 
                   approval.getApproverUser().getFullName(),
                   approval.getApproverUser().getEmail());
        logger.info("   Level: {}", approval.getHierarchyLevel().getLevelName());
        logger.info("   Pending Since: {}", createdAt.format(DATE_FORMAT));

        try {
            // Send email
            emailService.sendApprovalReminderEmail(
                approval.getRfq(),
                approval.getApproverUser(),
                approval.getHierarchyLevel(),
                createdAt
            );

            // ✅ MARK REMINDER AS SENT
            approval.setReminderSentAt(LocalDateTime.now());
            approvalRepository.save(approval);

            logger.info("   ✅ Reminder sent successfully");
            return true;

        } catch (Exception e) {
            logger.error("   ❌ Failed to send reminder: {}", e.getMessage());
            throw e;
        }
    }

    // ==================== MANUAL TRIGGER (FOR TESTING) ====================

    /**
     * ⚠️ MANUAL TRIGGER
     * Use this endpoint for testing during development
     */
    @Transactional
    public String sendRemindersManually() {
        logger.warn("⚠️ [MANUAL TRIGGER] Sending reminders manually");
        sendPendingApprovalReminders();
        return "Manual reminder check completed. See logs for details.";
    }

    /**
     * Get reminder statistics
     */
    @Transactional(readOnly = true)
    public ReminderStatistics getReminderStatistics() {
        List<DynamicRFQApproval> allPending = approvalRepository
                .findByStatus(ApprovalActionStatus.PENDING);

        int totalPending = allPending.size();
        int remindersSent = 0;
        int dueForReminder = 0;
        int notYetDue = 0;

        for (DynamicRFQApproval approval : allPending) {
            if (approval.getReminderSentAt() != null) {
                remindersSent++;
            } else if (holidayService.shouldSendReminder(approval.getCreatedAt())) {
                dueForReminder++;
            } else {
                notYetDue++;
            }
        }

        return new ReminderStatistics(
            totalPending,
            remindersSent,
            dueForReminder,
            notYetDue
        );
    }

    // ==================== STATISTICS DTO ====================

    public static class ReminderStatistics {
        public int totalPending;
        public int remindersSent;
        public int dueForReminder;
        public int notYetDue;

        public ReminderStatistics(int totalPending, int remindersSent, 
                                 int dueForReminder, int notYetDue) {
            this.totalPending = totalPending;
            this.remindersSent = remindersSent;
            this.dueForReminder = dueForReminder;
            this.notYetDue = notYetDue;
        }
    }
}