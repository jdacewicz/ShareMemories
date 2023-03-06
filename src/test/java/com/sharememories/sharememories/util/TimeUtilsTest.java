package com.sharememories.sharememories.util;

import org.junit.jupiter.api.BeforeEach;
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
    void Given_DateTimesWith0minutesDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime;

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("0 minutes" ,message);
    }

    @Test
    void Given_DateTimesWith1MinuteDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusMinutes(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 minute" ,message);
    }

    @Test
    void Given_DateTimesWith1HourDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusHours(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 hour" ,message);
    }

    @Test
    void Given_DateTimesWith2HoursDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusHours(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 hours" ,message);
    }

    @Test
    void Given_DateTimesWith1DayDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusDays(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 day" ,message);
    }

    @Test
    void Given_DateTimesWith2DaysDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusDays(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 days" ,message);
    }

    @Test
    void Given_DateTimesWith1MonthDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusMonths(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 month" ,message);
    }
    @Test
    void Given_DateTimesWith2MonthsDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusMonths(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 months" ,message);
    }

    @Test
    void Given_DateTimesWith1YearDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusYears(1);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("1 year" ,message);
    }

    @Test
    void Given_DateTimesWith2YearsDifference_When_ElapsedTimeMessageIsGenerated_Then_ProperStringIsReturned() {
        currentDateTime = creationDateTime.plusYears(2);

        String message = TimeUtils.getElapsedTimeMessage(creationDateTime, currentDateTime);

        assertEquals("2 years" ,message);
    }
}