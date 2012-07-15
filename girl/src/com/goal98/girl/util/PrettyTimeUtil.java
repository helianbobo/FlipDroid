package com.goal98.girl.util;

import com.ocpsoft.pretty.time.PrettyTime;

import java.util.Date;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 5/30/11
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrettyTimeUtil {
    public static String getPrettyTime(String localeStr, Date date) {

        Locale locale = null;
        PrettyTime p = null;
        if (localeStr != null && localeStr.length() > 0) {
            locale = new Locale(localeStr);
            p = new PrettyTime(locale);
        } else {
            p = new PrettyTime();
        }
        return p.format(date);
    }
}
