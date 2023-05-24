package com.sharememories.sharememories.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {

    LocalDateTime creationDateTime;
    LocalDateTime currentDateTime;

    @BeforeEach
    void setUp() {
        this.creationDateTime = LocalDateTime.of(1,1,1,0,0,0);
        this.currentDateTime = null;
    }

    @Test
    @DisplayName("Given 2 identical dates and times " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2IdenticalDatesAndTimes() {
        currentDateTime = creationDateTime;

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("0 minutes" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 1 minute difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith1MinuteDifference() {
        currentDateTime = creationDateTime.plusMinutes(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 minute" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 1 hour difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith1HourDifference() {
        currentDateTime = creationDateTime.plusHours(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 hour" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 2 hours difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith2HoursDifference() {
        currentDateTime = creationDateTime.plusHours(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 hours" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 1 day difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith1DayDifference() {
        currentDateTime = creationDateTime.plusDays(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 day" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 2 days difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith2DaysDifference() {
        currentDateTime = creationDateTime.plusDays(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 days" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 1 month difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith1MonthDifference() {
        currentDateTime = creationDateTime.plusMonths(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 month" ,message);
    }
    @Test
    @DisplayName("Given 2 dates and times with 2 months difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith2MonthsDifference() {
        currentDateTime = creationDateTime.plusMonths(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 months" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 1 year difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith1YearDifference() {
        currentDateTime = creationDateTime.plusYears(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 year" ,message);
    }

    @Test
    @DisplayName("Given 2 dates and times with 2 years difference " +
            "When generating elapsed time message " +
            "Then proper string is returned")
    void generateElapsedTimeMessageBy2DatesAndTimesWith2YearsDifference() {
        currentDateTime = creationDateTime.plusYears(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 years" ,message);
    }
}