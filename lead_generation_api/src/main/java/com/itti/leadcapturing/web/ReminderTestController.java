package com.itti.leadcapturing.web;

import com.itti.leadcapturing.service.ApprovalReminderService;
import com.itti.leadcapturing.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ⚠️ TEST CONTROLLER
 * For testing reminder functionality during development
 * Can be removed in production or secured with admin role
 */
@RestController
@RequestMapping("/api/test/reminders")
@CrossOrigin(origins = "*")
public class ReminderTestController {

    @Autowired
    private ApprovalReminderService reminderService;

    @Autowired
    private HolidayService holidayService;

    /**
     * Manual trigger to send reminders immediately
     * GET /api/test/reminders/send-now
     */
    @GetMapping("/send-now")
    public ResponseEntity<Map<String, Object>> sendRemindersNow() {
        String result = reminderService.sendRemindersManually();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get reminder statistics
     * GET /api/test/reminders/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getReminderStats() {
        ApprovalReminderService.ReminderStatistics stats = 
            reminderService.getReminderStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if date is working day
     * GET /api/test/reminders/is-working-day?date=2025-01-30
     */
    @GetMapping("/is-working-day")
    public ResponseEntity<Map<String, Object>> isWorkingDay(
            @RequestParam String date) {
        LocalDate checkDate = LocalDate.parse(date);
        boolean isWorking = holidayService.isWorkingDay(checkDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("date", date);
        response.put("isWorkingDay", isWorking);
        response.put("dayOfWeek", checkDate.getDayOfWeek().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Calculate working days between two dates
     * GET /api/test/reminders/working-days?from=2025-01-27&to=2025-01-30
     */
    @GetMapping("/working-days")
    public ResponseEntity<Map<String, Object>> calculateWorkingDays(
            @RequestParam String from,
            @RequestParam String to) {
        LocalDateTime fromDate = LocalDate.parse(from).atStartOfDay();
        LocalDateTime toDate = LocalDate.parse(to).atStartOfDay();
        
        int workingDays = holidayService.calculateWorkingDaysBetween(fromDate, toDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("from", from);
        response.put("to", to);
        response.put("workingDays", workingDays);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all configured holidays
     * GET /api/test/reminders/holidays
     */
    @GetMapping("/holidays")
    public ResponseEntity<Map<String, Object>> getHolidays() {
        Set<LocalDate> holidays = holidayService.getHolidays();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", holidays.size());
        response.put("holidays", holidays);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Add a holiday
     * POST /api/test/reminders/holidays
     * Body: {"date": "2025-02-14"}
     */
    @PostMapping("/holidays")
    public ResponseEntity<Map<String, Object>> addHoliday(
            @RequestBody Map<String, String> request) {
        LocalDate date = LocalDate.parse(request.get("date"));
        holidayService.addHoliday(date);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Holiday added: " + date);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Test if reminder should be sent for a date
     * GET /api/test/reminders/should-send?created=2025-01-27T10:00:00
     */
    @GetMapping("/should-send")
    public ResponseEntity<Map<String, Object>> shouldSendReminder(
            @RequestParam String created) {
        LocalDateTime createdAt = LocalDateTime.parse(created);
        boolean shouldSend = holidayService.shouldSendReminder(createdAt);
        
        int workingDays = holidayService.calculateWorkingDaysBetween(
            createdAt, 
            LocalDateTime.now()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("createdAt", created);
        response.put("currentTime", LocalDateTime.now().toString());
        response.put("workingDaysPassed", workingDays);
        response.put("shouldSendReminder", shouldSend);
        
        return ResponseEntity.ok(response);
    }
}