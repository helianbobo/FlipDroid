package com.goal98.tika.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-6-6
 * Time: 下午3:16
 * To change this template use File | Settings | File Templates.
 */
public class Lab {
    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        for(int i=0;i<100000;i++){
            GregorianCalendar gCalendar = new GregorianCalendar();
            gCalendar.setTime(new Date());
            gCalendar.add(Calendar.DATE, -1);
            gCalendar.set(Calendar.HOUR, 0);
            gCalendar.set(Calendar.MINUTE, 0);
            gCalendar.set(Calendar.SECOND, 0);
            gCalendar.set(Calendar.MILLISECOND, 0);
            gCalendar.set(Calendar.AM_PM, 0);
            Date newDate = gCalendar.getTime();
        }

        System.out.println(System.currentTimeMillis()-t1);

        t1 = System.currentTimeMillis();

        for(int i=0;i<100000;i++){
            Date time = new Date();
            time.setTime(time.getTime()-1*1000*24*3600);
        }
        System.out.println(System.currentTimeMillis()-t1);

    }
}
