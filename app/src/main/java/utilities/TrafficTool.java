package utilities;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TrafficTool {

    public enum FlowUnit{
        GB,MB,KB,B,ERROR
    }

    public static String getDigitString(long size){
        DecimalFormat format = new DecimalFormat("###.0");
        double i = 0;
        if (size >= 1024 * 1024 * 1024) {
            i = (size / (1024.0 * 1024.0 * 1024.0));
        }
        else if (size >= 1024 * 1024) {
            i = (size / (1024.0 * 1024.0));
        }
        else if (size >= 1024) {
            i = (size / (1024.0));
        }
        else if (size < 1024) {
            if (size <= 0) {
                i = 0;
            }
            else {
                i = size;
            }
        }
        return format.format(i);
    }

    public static FlowUnit getUnit(long size) {
        if (size >= 1024 * 1024 * 1024) {
            return FlowUnit.GB;
        }
        else if (size >= 1024 * 1024) {
            return FlowUnit.MB;
        }
        else if (size >= 1024) {
            return FlowUnit.KB;
        }
        else if (size < 1024) {
            return FlowUnit.B;
        }
        return FlowUnit.ERROR;
    }

    public static double BparseToSpecialDigit(FlowUnit flowUnit,long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        double i;
        switch (flowUnit){
            case B:
                if (size <= 0) {
                    bytes.append("0");
                }
                else {
                    bytes.append((int) size);
                }
                break;
            case GB:
                i = (size / (1024.0 * 1024.0 * 1024.0));
                bytes.append(format.format(i));
                break;
            case KB:
                i = (size / (1024.0));
                bytes.append(format.format(i));
                break;
            case MB:
                i = (size / (1024.0 * 1024.0));
                bytes.append(format.format(i));
                break;

        }

        return Double.valueOf(bytes.toString());
    }
    public static String BparseToRacialDigit(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        }
        else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        }
        else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        }
        else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            }
            else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }


    public static long getStartTimeFromPeriod(NetworkPeriod networkPeriod){
        if (networkPeriod == NetworkPeriod.TOTAL)
            return 0;
        final long oneDay = 24 * 60 * 60 * 1000;
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar timeCalendar = Calendar.getInstance(curTimeZone);
        timeCalendar.setTimeInMillis(System.currentTimeMillis());
        timeCalendar.set(Calendar.HOUR_OF_DAY, 0);
        timeCalendar.set(Calendar.MINUTE, 0);
        timeCalendar.set(Calendar.SECOND, 0);
        timeCalendar.set(Calendar.MILLISECOND, 0);
        timeCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        switch (networkPeriod){
            case THREEDAY:
                timeCalendar.setTimeInMillis(timeCalendar.getTimeInMillis() - oneDay * 3);
                break;
            case WEEK:
                timeCalendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                break;
            case MONTH:
                timeCalendar.set(Calendar.DAY_OF_MONTH,1);
                break;
            case YEAR:
                timeCalendar.set(Calendar.DAY_OF_YEAR,1);
                break;
            case TODAY:
                break;
        }
        return timeCalendar.getTimeInMillis();
    }


    public static List<Long> getBytesWithTime(NetworkStatsStatistics networkStatsStatistics, int uid){
        long hour = 1000 * 60 * 60;
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(System.currentTimeMillis());
        int cHour = current.get(Calendar.HOUR_OF_DAY);
        int end = cHour;

        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar startTime = Calendar.getInstance(curTimeZone);
        startTime.setTimeInMillis(System.currentTimeMillis());
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        startTime.setFirstDayOfWeek(Calendar.MONDAY);


        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(System.currentTimeMillis());
        endTime.set(Calendar.HOUR_OF_DAY, 0);
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.SECOND, 0);
        endTime.set(Calendar.MILLISECOND, 0);
        endTime.setFirstDayOfWeek(Calendar.MONDAY);

        List<Long> res = new ArrayList<>();
        for (int i=0;i<end;i++){
            long val;
            startTime.set(Calendar.HOUR_OF_DAY,i);
            endTime.set(Calendar.HOUR_OF_DAY,i+1);
            Log.d("startTime","startTime : " + (endTime.getTimeInMillis()  - startTime.getTimeInMillis()));
            val = networkStatsStatistics.getBytesRange(uid,NetworkType.mobile,startTime.getTimeInMillis(),endTime.getTimeInMillis() );
            res.add(i,val);
        }
        startTime.set(Calendar.HOUR_OF_DAY,end);

        res.add(end,networkStatsStatistics.getBytesRange(uid,NetworkType.mobile, startTime.getTimeInMillis(),System.currentTimeMillis()));
        return res;
    }
}
