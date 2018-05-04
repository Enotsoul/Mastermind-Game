/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.data;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author lostone
 */
public class MiscFunctions {
    public static long  unixtime() {
        return (long) (System.currentTimeMillis() / 1000L);
    }
    
    //Timestamp to show..
    public static String timestamp(long time) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(time*1000L);
        return "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH)
                + "-" + calendar.get(Calendar.YEAR)  + " " +  calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) ;
    }

    public static void main(String[] args) {

        System.out.println("oh yeah friend");
        System.out.println(timestamp(unixtime()));
    }
}
