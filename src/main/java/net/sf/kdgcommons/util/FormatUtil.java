// Copyright Keith D Gregory
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.sf.kdgcommons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;


/**
 *  Contains static utility methods that invoke the various <code>java.text</code>
 *  formatters, using a pool of thread-local instances. This avoids the cost of
 *  constructing these objects on an as-needed basis (a tenth of a millisecond
 *  or so, but it adds up), and also avoids the risk of using a single object
 *  multiple threads.
 */
public class FormatUtil
{
    /**
     *  Formats a date using a <code>SimpleDateFormat</code> instance with the
     *  current locale's timezone.
     */
    public static String formatDate(Date date, String format)
    {
        return formatDate(date, format, localZone);
    }


    /**
     *  Formats a date using a <code>SimpleDateFormat</code> instance with the
     *  specified timezone.
     */
    public static String formatDate(Date date, String format, String zoneId)
    {
        return getDateFormatter(format, zoneId).format(date);
    }


    /**
     *  Formats the date held in a <code>Calendar</code> using the current
     *  locale's timezone (<em>not</em> the calendar's own timezone).
     */
    public static String formatDate(Calendar cal, String format)
    {
        return formatDate(cal.getTime(), format, localZone);
    }


    /**
     *  Formats the date held in a <code>Calendar</code> using the specified
     *  timezone (<em>not</em> the calendar's own timezone).
     */
    public static String formatDate(Calendar cal, String format, String zoneId)
    {
        return getDateFormatter(format, zoneId).format(cal.getTime());
    }


    /**
     *  Formats a Java timestamp (millis since epoch) using the current
     *  locale's timezone.
     */
    public static String formatDate(long time, String format)
    {
        return formatDate(new Date(time), format, localZone);
    }


    /**
     *  Formats a Java timestamp (millis since epoch) using the specified
     *  timezone (<em>not</em> the calendar's own timezone).
     */
    public static String formatDate(long time, String format, String zoneId)
    {
        return getDateFormatter(format, zoneId).format(new Date(time));
    }


//----------------------------------------------------------------------------
//  Internals
//----------------------------------------------------------------------------

    private static String localZone = TimeZone.getDefault().getID();

    // note: these maps do not guarantee atomic update-if-empty; we may create
    //       multiple threadlocals, but eventually only one will remain

    private static Map<String,Map<String,ThreadLocal<SimpleDateFormat>>> tzDateFormatters
            = new ConcurrentHashMap<String,Map<String,ThreadLocal<SimpleDateFormat>>>();


    private static SimpleDateFormat getDateFormatter(final String format, final String zoneId)
    {
        Map<String,ThreadLocal<SimpleDateFormat>> zoneFormatters = tzDateFormatters.get(zoneId);
        if (zoneFormatters == null)
        {
            zoneFormatters = new ConcurrentHashMap<String,ThreadLocal<SimpleDateFormat>>();
            tzDateFormatters.put(zoneId, zoneFormatters);
        }

        ThreadLocal<SimpleDateFormat> threadLocal = zoneFormatters.get(format);
        if (threadLocal == null)
        {
            threadLocal = new ThreadLocal<SimpleDateFormat>()
            {
                @Override
                protected SimpleDateFormat initialValue()
                {
                    SimpleDateFormat formatter = new SimpleDateFormat(format);
                    formatter.setTimeZone(TimeZone.getTimeZone(zoneId));
                    return formatter;
                }
            };
            zoneFormatters.put(format, threadLocal);
        }
        return threadLocal.get();
    }
}
