package shared;

import scriptz.RunescriptAbstractContext;
import shared.enums.Items;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static Integer[] getItemIds(List<Items> items) {
        List<Integer> idList = items.stream().map(i -> i.id).collect(Collectors.toList());
        Integer[] itemIds = new Integer[idList.size()];
        return idList.toArray(itemIds);
    }

    public static boolean isElementInList(Object elem, Object[] list) {
        for (Object o : list) {
            if (o.equals(elem)) {
                return true;
            }
        }
        return false;
    }
    /** DATE METHODS */

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
