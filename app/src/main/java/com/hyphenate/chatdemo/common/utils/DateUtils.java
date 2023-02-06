package com.hyphenate.chatdemo.common.utils;

import android.content.Context;

import com.hyphenate.chatdemo.R;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static boolean isGrayWhiteDate(Context context) {
        try {
            String[] dateArray = context.getResources().getStringArray(R.array.gray_white_days);
            for (String date : dateArray) {
                if(date.contains("-")) {
                    String[] days = date.split("-");
                    if(days.length != 2) {
                        continue;
                    }
                    String startDay = days[0];
                    String endDay = days[1];
                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                    Date startD = format.parse(startDay);
                    Date endD = format.parse(endDay);
                    long startDTime = startD.getTime();
                    long endDTime = endD.getTime();
                    long currentTimeMillis = System.currentTimeMillis();
                    if(startDTime <= currentTimeMillis &&  currentTimeMillis < endDTime + 24L*60*60*1000) {
                        return true;
                    }
                }else {
                    SimpleDateFormat format = new SimpleDateFormat("MM.dd");
                    Date startD = format.parse(date);
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    int curMonth = calendar.get(Calendar.MONTH);
                    int curDay = calendar.get(Calendar.DAY_OF_MONTH);
                    calendar.setTime(startD);
                    int month = calendar.get(Calendar.MONTH);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    if(curMonth == month && curDay == dayOfMonth) {
                        return true;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否是同一天
     * @param msgTimestamp
     * @return
     */
    private static boolean isSameDay(long msgTimestamp) {
        Calendar calendar = Calendar.getInstance(UTC);
        int curMonth = calendar.get(Calendar.MONTH);
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(new Date(msgTimestamp));
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return curMonth == month && curDay == day;
    }
}
