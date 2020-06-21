package shared;

import shared.enums.Items;

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

    public static Date dateAddSeconds(Date date, int seconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

}
