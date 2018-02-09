package org.celllife.idart.utils;

import org.junit.Test;
import org.testng.Assert;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class WorkingDaysUtilTest {

    @Test
    public void testIsWorkingDay() throws ParseException {
        Date date = WorkingDaysUtil.createDate(1,1,2014); //NB: This won't work if you change the properties file!!
        Assert.assertEquals(WorkingDaysUtil.isWorkingDay(date),false);

        date = WorkingDaysUtil.createDate(2,1,2014); //NB: This won't work if you change the properties file!!
        Assert.assertEquals(WorkingDaysUtil.isWorkingDay(date),true);

        date = WorkingDaysUtil.createDate(18,4,2014); //NB: This won't work if you change the properties file!!
        Assert.assertEquals(WorkingDaysUtil.isWorkingDay(date),false);

        date = WorkingDaysUtil.createDate(10,8,2015); //NB: This won't work if you change the properties file!!
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Assert.assertEquals(Calendar.MONDAY,calendar.get(Calendar.DAY_OF_WEEK));
        Assert.assertEquals(WorkingDaysUtil.isWorkingDay(date),false);

    }

}