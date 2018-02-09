package org.celllife.idart.utils;

import org.celllife.idart.commonobjects.PropertiesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkingDaysUtil {

    public static Date createDate(Integer day, Integer month, Integer year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * A function to check whether the date is a "working day". This will depend on the values set in working_days.properties
     * @param date The date in question
     * @return True if it is a working day, false if it is not a working day.
     * @throws ParseException
     */
    public static boolean isWorkingDay(Date date) throws ParseException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        boolean isWorkingDay;

        // check whether the day of the week is a "working days" according to working_days.properties
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                isWorkingDay = PropertiesManager.workingDaysProperties().workingSunday();
                break;
            case 2:
                isWorkingDay = PropertiesManager.workingDaysProperties().workingMonday();
                break;
            case 3:
                isWorkingDay = PropertiesManager.workingDaysProperties().workingTuesday();
                break;
            case 4:
                isWorkingDay = PropertiesManager.workingDaysProperties().workingWednesday();
                break;
            case 5:
                isWorkingDay = PropertiesManager.workingDaysProperties().workingThursday();
                break;
            case 6:
                isWorkingDay = PropertiesManager.workingDaysProperties().workingFriday();
                break;
            case 7:
                isWorkingDay = PropertiesManager.workingDaysProperties().workingSaturday();
                break;
            default:
                isWorkingDay = true;
        }

        if (!isWorkingDay) {
            return false;
        }

        // check whether the day is a public holiday, as entered in working_days.properties
        for (Date publicHoliday : getPublicHolidaysFromProperties()) {
            if (monthAndDateEquals(publicHoliday, date)) {
                return false;
            }
        }

        // if Easter Friday (Good Friday) and Easter Monday (Family Day) are holidays,
        // then check that this is not one of those.
        if ((PropertiesManager.workingDaysProperties().easterFridayHoliday()) && monthAndDateEquals(getEasterFriday(calendar.get(Calendar.YEAR)), date)) {
            return false;
        }
        if ((PropertiesManager.workingDaysProperties().easterMondayHoliday()) && monthAndDateEquals(getEasterMonday(calendar.get(Calendar.YEAR)), date)) {
            return false;
        }

        return true;

    }

    private static boolean monthAndDateEquals(Date date1, Date date2) {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        if ((calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)) && (calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE))) {
            return true;
        }
        return false;
    }

    private static List<Date> getPublicHolidaysFromProperties() throws ParseException {

        List<Date> publicHolidayDates = new ArrayList<Date>();

        List<String> publicHolidays = PropertiesManager.workingDaysProperties().holidays();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.ENGLISH);

        for (String publicHoliday : publicHolidays) {

            // parse the string
            Date publicHolidayDate = dateFormat.parse(publicHoliday);

            // if the holiday falls on a Sunday, then postpone it for one day
            Calendar cal = Calendar.getInstance();
            cal.setTime(publicHolidayDate);
            if ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) && (PropertiesManager.workingDaysProperties().sundayMondayRule())) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                publicHolidayDate = cal.getTime();
            }

            publicHolidayDates.add(publicHolidayDate);
        }

        return publicHolidayDates;

    }

    // see: http://www.smart.net/~mmontes/nature1876.html
    private static Date getEasterSunday(Integer year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int p = (h + l - 7 * m + 114) % 31;

        int easter_month = (h + l - 7 * m + 114) / 31;
        int easter_day = p + 1;

        return createDate(easter_day, easter_month, year);
    }

    private static Date getEasterFriday(Integer year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getEasterSunday(year));
        cal.add(Calendar.DAY_OF_YEAR, -2);
        return cal.getTime();
    }

    private static Date getEasterMonday(Integer year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getEasterSunday(year));
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }

}
