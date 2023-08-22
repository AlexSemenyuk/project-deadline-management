package org.itstep.projectdeadlinemanagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeService {

    public static final LocalDate DATE = LocalDate.now();
    public static final int HOURS_PER_DAY = 8;
    public static final int AMOUNT_OF_MONTHS_IN_A_YEAR = 12;

    public static LocalDateTime localDateTimeAddDays(LocalDateTime start, int days){
        for (int i = 0; i < days - 1 ; i++) {
            start =  excludeWeekend(start);
            start =  excludeWeekend(start.plusDays(1));
        }
        return start;
    }

    public static LocalDate localDateAddDays(LocalDate start, int days){
        for (int i = 0; i < days - 1 ; i++) {
            start =  TimeService.excludeWeekend(start);
            start =  TimeService.excludeWeekend(start.plusDays(1));
        }
        return start;
    }


    public static LocalDateTime excludeWeekend(LocalDateTime date) {
        switch (date.getDayOfWeek().toString()) {
            case "SUNDAY" -> {
                return date.plusDays(1);
            }
            case "SATURDAY" -> {
                return date.plusDays(2);
            }
        }
        return date;
    }
    public static LocalDate excludeWeekend(LocalDate date) {
        switch (date.getDayOfWeek().toString()) {
            case "SUNDAY" -> {
                return date.plusDays(1);
            }
            case "SATURDAY" -> {
                return date.plusDays(2);
            }
        }
        return date;
    }

    public static LocalDateTime localDateTimeAddHours(LocalDateTime localDateTime, int hours) {
        int timeTMP = localDateTime.getHour() + hours;
        int days = timeTMP / HOURS_PER_DAY;
        int restOfHours = timeTMP % HOURS_PER_DAY;

        for (int i = 0; i < days ; i++) {
            localDateTime = excludeWeekend(localDateTime);
            localDateTime = excludeWeekend(localDateTime.plusDays(1));
        }
        localDateTime = localDateTime.withHour(restOfHours);
        if (localDateTime.getHour() == 8){
            localDateTime = excludeWeekend(localDateTime.plusDays(1));
            localDateTime = localDateTime.withHour(0);
        }
        return localDateTime;
    }


    public static int getDaysOfMonth(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int[] dayInMonths = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int dayOfMonth = 0;
        boolean leapYear = false;
        if (year % 100 == 0) {
            if (year % 400 == 0) {
                leapYear = true;
            }
        } else {
            if (year % 4 == 0) {
                leapYear = true;
            }
        }
        if (leapYear) {
            dayInMonths[1] = 29;
        }
        dayOfMonth = dayInMonths[month - 1];
        return dayOfMonth;
    }

    public static String formDate(LocalDate dateTmp) {
        int month = dateTmp.getMonthValue();
        int year = dateTmp.getYear();
        String date = "";
        if (month < 10) {
            date = year + "-0" + month;
        } else {
            date = year + "-" + month;
        }
        return date;
    }

    public static int planHoursPerMonth(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int daysOfMonth = TimeService.getDaysOfMonth(date);
        int sum = 0;
        for (int i = 0; i < daysOfMonth; i++) {
            LocalDate dateTmp = LocalDate.of(year, month, i + 1);
            if (!dateTmp.getDayOfWeek().toString().equals("SUNDAY") &&
                    !dateTmp.getDayOfWeek().toString().equals("SATURDAY")) {
                sum += TimeService.HOURS_PER_DAY;
            }
        }
        return sum;
    }
}
