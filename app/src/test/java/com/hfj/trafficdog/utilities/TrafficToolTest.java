package com.hfj.trafficdog.utilities;

import android.app.usage.NetworkStatsManager;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import utilities.NetworkPeriod;
import utilities.TrafficTool;

public class TrafficToolTest {
    @Test
    public void BtoMTest(){
        long rs = 418299790L;
        System.out.println(TrafficTool.BparseToRacialDigit(rs));
        System.out.println(TrafficTool.getDigitString(rs));
        System.out.println(TrafficTool.getUnit(rs));
    }

    @Test
    public void testGetStartTime(){
        long toDay = TrafficTool.getStartTimeFromPeriod(NetworkPeriod.WEEK);
        Date date = new Date(toDay);
        DateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String datestr = df.format(date);
        System.out.println(datestr);
    }

    @Test
    public void testParseSpecial(){
        Long testVal = 259895554546L;
        Double res = TrafficTool.BparseToSpecialDigit(TrafficTool.FlowUnit.GB,895554546L);
        System.out.println(res);
    }
}
