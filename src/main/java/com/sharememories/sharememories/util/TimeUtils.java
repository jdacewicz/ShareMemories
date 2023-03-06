package com.sharememories.sharememories.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtils {

    public static String getElapsedTimeMessage(LocalDateTime fromDateTime , LocalDateTime toDateTime) {
        //TODO Maybe add some languages.
        long minutes =  ChronoUnit.MINUTES.between(fromDateTime, toDateTime);
        long hours =  ChronoUnit.HOURS.between(fromDateTime, toDateTime);
        long days = ChronoUnit.DAYS.between(fromDateTime, toDateTime);
        long months = ChronoUnit.MONTHS.between(fromDateTime, toDateTime);
        long years = ChronoUnit.YEARS.between(fromDateTime, toDateTime);

        String grammar;
        if (years > 0) {
            grammar = (years == 1) ? "year" : "years";
            return years + " " + grammar;

        } else if (months > 0) {
            grammar = (months == 1) ? "month" : "months";
            return  months + " " + grammar;

        } else if (days > 0) {
            grammar = (days == 1) ? "day" : "days";
            return  days + " " + grammar;

        } else if (hours > 0) {
            grammar = (hours == 1) ? "hour" : "hours";
            return  hours + " " + grammar;

        } else {
            grammar = (minutes == 1) ? "minute" : "minutes";
            return minutes + " " + grammar;
        }
    }
}
