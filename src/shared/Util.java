package shared;

import scriptz.RunescriptAbstractContext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static boolean isElementInArray(Object elem, Object[] list) {
        for (Object o : list) {
            if (o.equals(elem)) {
                return true;
            }
        }
        return false;
    }
    /** DATE METHODS */

    public static Long diffDatesInMillis(Date date1, Date date2) {
        return date1.getTime() - date2.getTime();
    }

    public static Date dateAddSeconds(Date date, int seconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    public static Date dateAddMillis(Date date, int milliseconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MILLISECOND, milliseconds);
        return c.getTime();
    }

    public static Date dateAddDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /** Input dates as "HH:mm:ss" */
    public static Date getDate(String dateString) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            RunescriptAbstractContext.logScript("ERROR: couldn't parse date: " + dateString);
            return null;
        }
    }

}
