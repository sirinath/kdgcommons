// Copyright (c) Keith D Gregory, all rights reserved
package net.sf.kdgcommons.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import junit.framework.TestCase;

public class TestFormatUtil extends TestCase
{
    // we'll just verify that we can do the format; no easy way to verify
    // thread-safety
    public void testLocalDateFormatting() throws Exception
    {
        Calendar cal = GregorianCalendar.getInstance();
        cal.clear();
        cal.set(2011, Calendar.JUNE, 30, 13, 14, 15);

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(cal, "yyyy-MM-dd HH:mm:ss"));

        Date date = new Date(cal.getTimeInMillis());
        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss"));

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date.getTime(), "yyyy-MM-dd HH:mm:ss"));
    }


    public void testTzDateFormatting() throws Exception
    {
        Calendar cal = GregorianCalendar.getInstance();
        cal.clear();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(2011, Calendar.JUNE, 30, 13, 14, 15);

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(cal, "yyyy-MM-dd HH:mm:ss", "GMT"));
        assertEquals("2011-06-30 08:14:15", FormatUtil.formatDate(cal, "yyyy-MM-dd HH:mm:ss", "GMT-0500"));

        Date date = new Date(cal.getTimeInMillis());
        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss", "GMT"));
        assertEquals("2011-06-30 08:14:15", FormatUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss", "GMT-0500"));

        assertEquals("2011-06-30 13:14:15", FormatUtil.formatDate(date.getTime(), "yyyy-MM-dd HH:mm:ss", "GMT"));
        assertEquals("2011-06-30 08:14:15", FormatUtil.formatDate(date.getTime(), "yyyy-MM-dd HH:mm:ss", "GMT-0500"));
    }

}
