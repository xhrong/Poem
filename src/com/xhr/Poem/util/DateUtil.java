package com.xhr.Poem.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xhrong on 2015/5/6.
 */
public class DateUtil {

    public static String getCurrentDateString(String format) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        String date = sDateFormat.format(new Date());
        return date;
    }
}
