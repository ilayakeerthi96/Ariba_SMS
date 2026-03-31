package com.itti.leadcapturing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Service to manage holidays and calculate working days
 * Excludes weekends (Saturday, Sunday) and national holidays
 */
@Service
public class HolidayService {

    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);

    // ==================== HOLIDAY CONFIGURATION ====================
    
    /**
     * National holidays for the year
     * TODO: Load this from database or configuration file
     */
    private Set<LocalDate> nationalHolidays = new HashSet<>();

    /**
     * Initialize with some sample Indian holidays for 2025
     * In production, load from database
     */
    public HolidayService() {
        // Sample holidays for 2025 (India)
        nationalHolidays.add(LocalDate.of(2025, 1, 26));  // Republic Day
        nationalHolidays.add(LocalDate.of(2025, 3, 14));  // Holi
        nationalHolidays.add(LocalDate.of(2025, 3, 31));  // Eid ul-Fitr
        nationalHolidays.add(LocalDate.of(2025, 4, 14));  // Dr. Ambedkar Jayanti
        nationalHolidays.add(LocalDate.of(2025, 4, 18));  // Good Friday
        nationalHolidays.add(LocalDate.of(2025, 8, 15));  // Independence Day
        nationalHolidays.add(LocalDate.of(2025, 10, 2));  // Gandhi Jayanti
        nationalHolidays.add(LocalDate.of(2025, 10, 20)); // Dussehra
        nationalHolidays.add(LocalDate.of(2025, 11, 8));  // Diwali
        nationalHolidays.add(LocalDate.of(2025, 12, 25)); // Christmas
        
        logger.info("📅 Loaded {} national holidays", nationalHolidays.size());
    }

    // ==================== WORKING DAYS CALCULATION ====================

    /**
     * Check if a date is a working day
     * (Not Saturday, Sunday, or National Holiday)
     */
    public boolean isWorkingDay(LocalDate date) {
        // Check if weekend
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }
        
        // Check if national holiday
        if (nationalHolidays.contains(date)) {
            return false;
        }
        
        return true;
    }

    /**
     * Calculate number of working days between two dates
     * EXCLUDING the start date, INCLUDING the end date
     * 
     * @param startDateTime Start date-time
     * @param endDateTime End date-time
     * @return Number of working days
     */
    public int calculateWorkingDaysBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();
        
        int workingDays = 0;
        LocalDate current = startDate.plusDays(1); // Start from next day
        
        while (!current.isAfter(endDate)) {
            if (isWorkingDay(current)) {
                workingDays++;
            }
            current = current.plusDays(1);
        }
        
        return workingDays;
    }

    /**
     * Check if reminder should be sent
     * (2 working days have passed since creation)
     */
    public boolean shouldSendReminder(LocalDateTime createdAt) {
        if (createdAt == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        int workingDaysPassed = calculateWorkingDaysBetween(createdAt, now);
        
        logger.debug("📊 Working days passed since {}: {}", createdAt, workingDaysPassed);
        
        return workingDaysPassed >= 2;
    }

    /**
     * Get next working day from given date
     */
    public LocalDate getNextWorkingDay(LocalDate fromDate) {
        LocalDate nextDay = fromDate.plusDays(1);
        
        while (!isWorkingDay(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        
        return nextDay;
    }

    /**
     * Add working days to a date
     */
    public LocalDate addWorkingDays(LocalDate fromDate, int workingDaysToAdd) {
        LocalDate result = fromDate;
        int addedDays = 0;
        
        while (addedDays < workingDaysToAdd) {
            result = result.plusDays(1);
            if (isWorkingDay(result)) {
                addedDays++;
            }
        }
        
        return result;
    }

    // ==================== HOLIDAY MANAGEMENT ====================

    /**
     * Add a national holiday
     */
    public void addHoliday(LocalDate date) {
        nationalHolidays.add(date);
        logger.info("➕ Added holiday: {}", date);
    }

    /**
     * Remove a holiday
     */
    public void removeHoliday(LocalDate date) {
        nationalHolidays.remove(date);
        logger.info("➖ Removed holiday: {}", date);
    }

    /**
     * Get all holidays
     */
    public Set<LocalDate> getHolidays() {
        return new HashSet<>(nationalHolidays);
    }

    /**
     * Check if a date is a holiday
     */
    public boolean isHoliday(LocalDate date) {
        return nationalHolidays.contains(date);
    }

    /**
     * Clear all holidays
     */
    public void clearHolidays() {
        nationalHolidays.clear();
        logger.info("🗑️ Cleared all holidays");
    }

    /**
     * Load holidays for a specific year from database
     * TODO: Implement database loading
     */
    public void loadHolidaysForYear(int year) {
        // TODO: Load from database
        logger.info("📅 Loading holidays for year: {}", year);
    }
}