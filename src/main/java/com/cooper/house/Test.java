package com.cooper.house;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cooper on 10/13/15.
 */
public class Test {

    public static void main(String[] args){

        try {
            Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-10-20");
            Date d2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2015-10-19 1:1:1");
            System.out.print(d2.after(d1));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
