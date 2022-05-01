package com.vtmer.microteachingquality.personneltraining.simpletest;


import cn.hutool.core.util.ObjectUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

public class Test {

    @org.junit.jupiter.api.Test
    public void test1() {

        // 设置东八区时区
        // TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        // TimeZone.setDefault(tz);
        // System.setProperty("user.timezone", "Asia/Shanghai");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        System.out.println(df.format(new Date()));
        System.out.println(calendar.getTime());
        System.out.println(calendar.getTimeZone());
    }

    @org.junit.jupiter.api.Test
    public void test2() {
        System.setProperty("user.timezone", "Asia/Shanghai");

        final Properties p = System.getProperties();
        final Enumeration e = p.keys();
        while (e.hasMoreElements()) {
            final String prt = (String) e.nextElement();
            final String prtValue = System.getProperty(prt);
            System.out.println(prt + ":" + prtValue);
        }
    }

    @org.junit.jupiter.api.Test
    public void test3() {
        Date date = new Date();
        Boolean flag = ObjectUtil.isNull(date);
        System.out.println(flag);
    }

}
