package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by cooper on 10/13/15.  WP83
 */




/*


UPDATE HOUSE SET NOITCE_OWNER = null , OLD_OWNER = null;



        UPDATE HOUSE h LEFT JOIN BUSINESS_HOUSE bh on h.ID = bh.AFTER_HOUSE LEFT JOIN OWNER_BUSINESS ob ON ob.id = bh.BUSINESS_ID
        set h.NOITCE_OWNER = h.MAIN_OWNER WHERE ob.STATUS <> 'ABORT' AND (ob.DEFINE_ID = 'WP44' or ob.DEFINE_ID = 'WP45');




        UPDATE HOUSE h LEFT JOIN BUSINESS_HOUSE bh on h.ID = bh.AFTER_HOUSE LEFT JOIN OWNER_BUSINESS ob ON ob.id = bh.BUSINESS_ID
        set h.OLD_OWNER = h.MAIN_OWNER WHERE ob.STATUS <> 'ABORT' AND ob.DEFINE_ID in ('WP40' ,'WP52','WP102', 'WP53', 'WP54', 'WP55');



        UPDATE HOUSE h LEFT JOIN BUSINESS_HOUSE bh on h.ID = bh.AFTER_HOUSE LEFT JOIN OWNER_BUSINESS ob ON ob.id = bh.BUSINESS_ID
        LEFT JOIN HOUSE sh on sh.ID = bh.START_HOUSE
        set h.NOITCE_OWNER = sh.NOITCE_OWNER WHERE ob.STATUS <> 'ABORT' AND ob.DEFINE_ID in ('WP33' , 'WP40', 'WP32' ,'WP41' ,'WP52', 'WP102' ,'WP53' ,'WP91' ,'WP54', 'WP55',
        'WP9' , 'WP10', 'WP12' ,'WP13', 'WP14' ,'WP15' ,'WP17' ,'WP44', 'WP45' ,'WP46' ,'WP1',
        'WP2', 'WP4', 'WP73' ,'WP74', 'WP36', 'WP37' ,'WP42' ,'WP43') AND h.NOITCE_OWNER is null;


        UPDATE HOUSE h LEFT JOIN BUSINESS_HOUSE bh on h.ID = bh.AFTER_HOUSE LEFT JOIN OWNER_BUSINESS ob ON ob.id = bh.BUSINESS_ID
        LEFT JOIN HOUSE sh on sh.ID = bh.START_HOUSE
        set h.OLD_OWNER = sh.OLD_OWNER WHERE ob.STATUS <> 'ABORT' AND ob.DEFINE_ID in ('WP33' , 'WP40', 'WP32' ,'WP41' ,'WP52', 'WP102' ,'WP53' ,'WP91' ,'WP54', 'WP55',
        'WP9' 'WP10' 'WP12' 'WP13' 'WP14' 'WP15' 'WP17' 'WP44' 'WP45' 'WP46' 'WP1'
        'WP2', 'WP4', 'WP73' ,'WP74', 'WP36', 'WP37' ,'WP42' ,'WP43') AND h.OLD_OWNER is null;


*/


public class RecordImport {

    public static final String[] TAKE_LAST_OWNER_BIZ ={
            "WP9"  ,"WP10","WP12","WP13","WP14","WP15","WP17","WP22","WP25","WP26","WP1","WP2","WP4","WP5","WP8"
    };

    public static final List<String> TAKE_LAST_OWNER_BIZ_LIST = Arrays.asList(TAKE_LAST_OWNER_BIZ);

    public static final String[] MUST_HAVE_SELECT = {
            "WP37", "WP43", "WP45", "WP46"
            , "WP47", "WP49", "WP2", "WP3"
            , "WP4", "WP6", "WP7", "WP8"
            , "WP10", "WP11", "WP12", "WP14"
            , "WP15", "WP16", "WP17", "WP21"
            , "WP24", "WP84", "WP100", "WP74"};
    private static final List<String> MUST_HAVE_SELECT_LIST = Arrays.asList(MUST_HAVE_SELECT);

    private static final String HOUSE_DB_URL = "jdbc:jtds:sqlserver://192.168.1.200:1433/DGHouseInfo";

    private static final String SHARK_DB_URL = "jdbc:jtds:sqlserver://192.168.1.200:1433/shark";

    private static final String RECORD_DB_URL = "jdbc:jtds:sqlserver://192.168.1.200:1433/DGHOUSERECORD";

    // 开原
//    private static final String HOUSE_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";
//
//    private static final String SHARK_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/shark";
//
//    private static final String RECORD_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHOUSERECORD";


    private static final String OUT_FILE_PATH = "/root/Documents/oldRecord.sql";

    private static final String ERROR_FILE_PATH = "/root/Documents/oldRecordError.log";

    private static final String SUCCESS_FILE_PATH = "/root/Documents/statusError.log";

    private static final String PATCH_OUT_FILE_PATH = "/root/Documents/oldPatch.sql";

//
//    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/oldRecord.sql";
//
//    private static final String PATCH_OUT_FILE_PATH = "/Users/cooper/Documents/oldPatch.sql";
//
//    private static final String ERROR_FILE_PATH = "/Users/cooper/Documents/oldRecordError.log";
//
//    private static final String SUCCESS_FILE_PATH = "/Users/cooper/Documents/statusError.log";


    //ky 2016-04-7

    private static final String BEGIN_DATE = "2016-04-21";

    private static Date CONTINUE_DATE;

    static {
        try {
            CONTINUE_DATE = new SimpleDateFormat("yyyy-MM-dd").parse(BEGIN_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private static Set<String> ONLY_HOUSE = new HashSet<>();

    static {
        ONLY_HOUSE.add("44-46-51-1231");
        ONLY_HOUSE.add("2-54-188-2163");
        ONLY_HOUSE.add("44-40-7-3121");
        ONLY_HOUSE.add("44-40-7-3111");
        ONLY_HOUSE.add("44-40-7-3112");
        ONLY_HOUSE.add("2-54-71-2021");
        ONLY_HOUSE.add("2-55-98-2041");
        ONLY_HOUSE.add("2-54-79-1061");
        ONLY_HOUSE.add("44-40-7-3101");
        ONLY_HOUSE.add("2-54-79-1051");
        ONLY_HOUSE.add("2-54-188-2121");
        ONLY_HOUSE.add("44-40-5-6081");
        ONLY_HOUSE.add("2-54-188-2112");
        ONLY_HOUSE.add("44-40-7-3161");
        ONLY_HOUSE.add("44-40-5-6073");
        ONLY_HOUSE.add("44-40-5-6072");
        ONLY_HOUSE.add("44-40-5-6071");
        ONLY_HOUSE.add("44-40-7-3152");
        ONLY_HOUSE.add("44-40-7-3151");
        ONLY_HOUSE.add("44-40-25-4");
        ONLY_HOUSE.add("44-40-7-2092");
        ONLY_HOUSE.add("44-40-7-3141");
        ONLY_HOUSE.add("44-40-7-2081");
        ONLY_HOUSE.add("44-40-7-2082");
        ONLY_HOUSE.add("44-40-7-2072");
        ONLY_HOUSE.add("44-40-7-2071");
        ONLY_HOUSE.add("2-55-86-2102");
        ONLY_HOUSE.add("44-51-15-12");
        ONLY_HOUSE.add("44-51-15-13");
        ONLY_HOUSE.add("44-40-7-2061");
        ONLY_HOUSE.add("44-51-15-10");
        ONLY_HOUSE.add("44-51-15-11");
        ONLY_HOUSE.add("44-40-5-6061");
        ONLY_HOUSE.add("44-40-7-2062");
        ONLY_HOUSE.add("44-46-116-9");
        ONLY_HOUSE.add("44-51-15-16");
        ONLY_HOUSE.add("44-46-116-8");
        ONLY_HOUSE.add("44-51-15-14");
        ONLY_HOUSE.add("44-51-15-15");
        ONLY_HOUSE.add("44-51-17-25");
        ONLY_HOUSE.add("44-46-116-3");
        ONLY_HOUSE.add("44-46-116-4");
        ONLY_HOUSE.add("44-40-5-6051");
        ONLY_HOUSE.add("44-40-7-2052");
        ONLY_HOUSE.add("44-40-7-2041");
        ONLY_HOUSE.add("44-40-7-2042");
        ONLY_HOUSE.add("44-40-7-2031");
        ONLY_HOUSE.add("44-40-7-2021");
        ONLY_HOUSE.add("44-40-7-2011");
        ONLY_HOUSE.add("44-56-10-1216");
        ONLY_HOUSE.add("44-56-10-1213");
        ONLY_HOUSE.add("44-56-10-1214");
        ONLY_HOUSE.add("44-56-10-1206");
        ONLY_HOUSE.add("44-56-10-1233");
        ONLY_HOUSE.add("44-56-10-1221");
        ONLY_HOUSE.add("44-56-10-1227");
        ONLY_HOUSE.add("44-46-115-5");
        ONLY_HOUSE.add("44-46-115-9");
        ONLY_HOUSE.add("44-46-115-6");
        ONLY_HOUSE.add("44-46-115-7");
        ONLY_HOUSE.add("2-54-170-2091");
        ONLY_HOUSE.add("44-40-14-2052");
        ONLY_HOUSE.add("44-47-57");
        ONLY_HOUSE.add("44-47-56");
        ONLY_HOUSE.add("44-47-55");
        ONLY_HOUSE.add("44-47-54");
        ONLY_HOUSE.add("44-47-60");
        ONLY_HOUSE.add("44-47-58");
        ONLY_HOUSE.add("44-47-59");
        ONLY_HOUSE.add("2-54-156-1042");
        ONLY_HOUSE.add("2-54-170-2062");
        ONLY_HOUSE.add("2-54-170-2063");
        ONLY_HOUSE.add("44-24-36");
        ONLY_HOUSE.add("44-24-35");
        ONLY_HOUSE.add("44-24-37");
        ONLY_HOUSE.add("44-47-13");
        ONLY_HOUSE.add("44-24-39");
        ONLY_HOUSE.add("2-54-67-2031");
        ONLY_HOUSE.add("44-47-15");
        ONLY_HOUSE.add("44-47-14");
        ONLY_HOUSE.add("44-47-16");
        ONLY_HOUSE.add("2-54-67-2022");
        ONLY_HOUSE.add("44-24-18");
        ONLY_HOUSE.add("2-55-73-1082");
        ONLY_HOUSE.add("2-54-156-1031");
        ONLY_HOUSE.add("44-24-29");
        ONLY_HOUSE.add("44-24-28");
        ONLY_HOUSE.add("44-24-27");
        ONLY_HOUSE.add("44-24-26");
        ONLY_HOUSE.add("44-24-25");
        ONLY_HOUSE.add("44-40-20-16");
        ONLY_HOUSE.add("44-40-20-18");
        ONLY_HOUSE.add("44-40-20-13");
        ONLY_HOUSE.add("44-40-20-12");
        ONLY_HOUSE.add("44-40-20-15");
        ONLY_HOUSE.add("44-40-20-14");
        ONLY_HOUSE.add("44-40-20-11");
        ONLY_HOUSE.add("44-40-20-10");
        ONLY_HOUSE.add("44-24-34");
        ONLY_HOUSE.add("44-47-38");
        ONLY_HOUSE.add("44-24-10");
        ONLY_HOUSE.add("44-24-11");
        ONLY_HOUSE.add("44-56-10-2275");
        ONLY_HOUSE.add("44-56-10-2274");
        ONLY_HOUSE.add("44-56-10-2273");
        ONLY_HOUSE.add("44-56-10-2272");
        ONLY_HOUSE.add("2-54-52-3042");
        ONLY_HOUSE.add("44-56-10-2286");
        ONLY_HOUSE.add("44-56-10-2285");
        ONLY_HOUSE.add("44-56-10-2282");
        ONLY_HOUSE.add("44-56-10-2281");
        ONLY_HOUSE.add("44-56-10-2284");
        ONLY_HOUSE.add("44-56-10-2283");
        ONLY_HOUSE.add("44-56-10-2251");
        ONLY_HOUSE.add("44-56-10-2255");
        ONLY_HOUSE.add("44-56-10-2254");
        ONLY_HOUSE.add("44-56-10-2253");
        ONLY_HOUSE.add("44-56-10-2252");
        ONLY_HOUSE.add("44-56-10-2256");
        ONLY_HOUSE.add("44-56-10-2262");
        ONLY_HOUSE.add("44-56-10-2261");
        ONLY_HOUSE.add("44-56-10-2264");
        ONLY_HOUSE.add("44-56-10-2263");
        ONLY_HOUSE.add("44-56-10-2265");
        ONLY_HOUSE.add("44-51-15-9");
        ONLY_HOUSE.add("44-51-15-6");
        ONLY_HOUSE.add("44-51-15-5");
        ONLY_HOUSE.add("44-51-15-8");
        ONLY_HOUSE.add("44-51-15-7");
        ONLY_HOUSE.add("44-51-15-1");
        ONLY_HOUSE.add("44-51-15-2");
        ONLY_HOUSE.add("44-51-15-3");
        ONLY_HOUSE.add("44-51-15-4");
        ONLY_HOUSE.add("44-40-8-6111");
        ONLY_HOUSE.add("44-40-22-2062");
        ONLY_HOUSE.add("44-40-7-2171");
        ONLY_HOUSE.add("44-40-5-6172");
        ONLY_HOUSE.add("44-40-5-6173");
        ONLY_HOUSE.add("44-40-7-2172");
        ONLY_HOUSE.add("44-40-5-6161");
        ONLY_HOUSE.add("2-54-154-3022");
        ONLY_HOUSE.add("44-56-10-1177");
        ONLY_HOUSE.add("44-56-10-1176");
        ONLY_HOUSE.add("2-55-99-3063");
        ONLY_HOUSE.add("2-55-99-3062");
        ONLY_HOUSE.add("44-40-5-6151");
        ONLY_HOUSE.add("2-55-68-2111");
        ONLY_HOUSE.add("44-40-7-2141");
        ONLY_HOUSE.add("44-40-7-2142");
        ONLY_HOUSE.add("44-40-5-6141");
        ONLY_HOUSE.add("2-55-86-2031");
        ONLY_HOUSE.add("2-54-149-3061");
        ONLY_HOUSE.add("44-40-5-6131");
        ONLY_HOUSE.add("44-40-7-2132");
        ONLY_HOUSE.add("2-55-86-2042");
        ONLY_HOUSE.add("44-40-7-2161");
        ONLY_HOUSE.add("44-40-7-2162");
        ONLY_HOUSE.add("44-40-5-6121");
        ONLY_HOUSE.add("44-40-5-6123");
        ONLY_HOUSE.add("2-54-64-1012");
        ONLY_HOUSE.add("2-54-106-3051");
        ONLY_HOUSE.add("44-40-7-2152");
        ONLY_HOUSE.add("44-40-5-6111");
        ONLY_HOUSE.add("44-46-101-12");
        ONLY_HOUSE.add("44-46-18-61");
        ONLY_HOUSE.add("44-46-101-13");
        ONLY_HOUSE.add("44-46-101-11");
        ONLY_HOUSE.add("44-46-101-17");
        ONLY_HOUSE.add("44-46-101-14");
        ONLY_HOUSE.add("44-46-53-8");
        ONLY_HOUSE.add("44-46-53-6");
        ONLY_HOUSE.add("44-46-53-4");
        ONLY_HOUSE.add("44-46-53-2");
        ONLY_HOUSE.add("44-40-7-2102");
        ONLY_HOUSE.add("44-40-5-6101");
        ONLY_HOUSE.add("44-40-7-2101");
        ONLY_HOUSE.add("44-46-18-73");
        ONLY_HOUSE.add("2-54-106-3022");
        ONLY_HOUSE.add("44-56-10-1104");
        ONLY_HOUSE.add("44-56-10-1105");
        ONLY_HOUSE.add("44-40-7-2111");
        ONLY_HOUSE.add("44-40-7-2112");
        ONLY_HOUSE.add("44-40-8-6051");
        ONLY_HOUSE.add("44-40-7-2122");
        ONLY_HOUSE.add("44-46-18-25");
        ONLY_HOUSE.add("44-46-75-16");
        ONLY_HOUSE.add("44-46-75-14");
        ONLY_HOUSE.add("44-46-101-22");
        ONLY_HOUSE.add("44-46-101-21");
        ONLY_HOUSE.add("44-46-18-11");
        ONLY_HOUSE.add("44-40-17-3051");
        ONLY_HOUSE.add("44-40-17-3052");
        ONLY_HOUSE.add("2-54-140-1062");
        ONLY_HOUSE.add("44-56-10-2235");
        ONLY_HOUSE.add("44-56-10-2234");
        ONLY_HOUSE.add("44-56-10-2232");
        ONLY_HOUSE.add("44-56-10-2233");
        ONLY_HOUSE.add("44-56-10-2225");
        ONLY_HOUSE.add("44-56-10-2224");
        ONLY_HOUSE.add("44-56-10-2223");
        ONLY_HOUSE.add("44-56-10-2222");
        ONLY_HOUSE.add("44-56-10-2213");
        ONLY_HOUSE.add("44-56-10-2212");
        ONLY_HOUSE.add("44-56-10-2214");
        ONLY_HOUSE.add("44-56-10-2205");
        ONLY_HOUSE.add("2-54-140-1022");
        ONLY_HOUSE.add("2-54-140-1031");
        ONLY_HOUSE.add("44-46-167-28");
        ONLY_HOUSE.add("44-40-8-6081");
        ONLY_HOUSE.add("2-54-140-1012");
        ONLY_HOUSE.add("2-55-107-3063");
        ONLY_HOUSE.add("2-55-107-3062");
        ONLY_HOUSE.add("2-55-111-2031");
        ONLY_HOUSE.add("2-54-28-1023");
        ONLY_HOUSE.add("2-54-76-1092");
        ONLY_HOUSE.add("2-54-76-1093");
        ONLY_HOUSE.add("44-40-16-1063");
        ONLY_HOUSE.add("44-56-11-1286");
        ONLY_HOUSE.add("2-54-165-2061");
        ONLY_HOUSE.add("44-40-17-4032");
        ONLY_HOUSE.add("44-40-17-4031");
        ONLY_HOUSE.add("2-54-76-1031");
        ONLY_HOUSE.add("44-40-17-4042");
        ONLY_HOUSE.add("2-54-108-2032");
        ONLY_HOUSE.add("2-62-6");
        ONLY_HOUSE.add("2-62-2");
        ONLY_HOUSE.add("2-62-3");
        ONLY_HOUSE.add("44-40-17-4051");
        ONLY_HOUSE.add("2-62-4");
        ONLY_HOUSE.add("2-62-5");
        ONLY_HOUSE.add("2-54-28-1071");
        ONLY_HOUSE.add("44-40-17-4052");
        ONLY_HOUSE.add("2-62-1");
        ONLY_HOUSE.add("2-54-76-1051");
        ONLY_HOUSE.add("44-38-22");
        ONLY_HOUSE.add("44-38-21");
        ONLY_HOUSE.add("44-46-118-5");
        ONLY_HOUSE.add("44-46-118-6");
        ONLY_HOUSE.add("2-54-131-1061");
        ONLY_HOUSE.add("2-55-94-1041");
        ONLY_HOUSE.add("44-46-118-2");
        ONLY_HOUSE.add("44-46-118-1");
        ONLY_HOUSE.add("44-46-141-3241");
        ONLY_HOUSE.add("44-46-141-3242");
        ONLY_HOUSE.add("44-51-14-1");
        ONLY_HOUSE.add("44-51-14-2");
        ONLY_HOUSE.add("44-51-14-3");
        ONLY_HOUSE.add("44-51-14-4");
        ONLY_HOUSE.add("44-51-14-5");
        ONLY_HOUSE.add("44-51-14-7");
        ONLY_HOUSE.add("44-51-14-6");
        ONLY_HOUSE.add("44-51-14-9");
        ONLY_HOUSE.add("44-51-14-8");
        ONLY_HOUSE.add("44-46-141-3232");
        ONLY_HOUSE.add("44-46-141-3231");
        ONLY_HOUSE.add("44-46-4-1125");
        ONLY_HOUSE.add("44-56-10-1067");
        ONLY_HOUSE.add("44-40-17-6111");
        ONLY_HOUSE.add("44-40-8-6121");
        ONLY_HOUSE.add("2-54-79-4032");
        ONLY_HOUSE.add("2-54-86-1051");
        ONLY_HOUSE.add("2-54-79-4062");
        ONLY_HOUSE.add("2-54-161-4031");
        ONLY_HOUSE.add("44-56-11-1202");
        ONLY_HOUSE.add("44-46-55-14");
        ONLY_HOUSE.add("2-54-161-4012");
        ONLY_HOUSE.add("2-54-86-2061");
        ONLY_HOUSE.add("44-56-11-1124");
        ONLY_HOUSE.add("2-54-159-1022");
        ONLY_HOUSE.add("44-56-11-1185");
        ONLY_HOUSE.add("44-46-4-1065");
        ONLY_HOUSE.add("44-56-11-1164");
        ONLY_HOUSE.add("2-54-79-3062");
        ONLY_HOUSE.add("2-54-159-1062");
        ONLY_HOUSE.add("44-46-119-3");
        ONLY_HOUSE.add("44-46-119-4");
        ONLY_HOUSE.add("2-54-106-1022");
        ONLY_HOUSE.add("44-46-16-68");
        ONLY_HOUSE.add("2-54-108-1021");
        ONLY_HOUSE.add("44-40-16-2021");
        ONLY_HOUSE.add("44-40-16-2031");
        ONLY_HOUSE.add("44-40-16-2041");
        ONLY_HOUSE.add("44-40-16-2011");
        ONLY_HOUSE.add("44-46-158-29");
        ONLY_HOUSE.add("44-46-158-28");
        ONLY_HOUSE.add("2-54-183-1041");
        ONLY_HOUSE.add("44-46-110-9");
        ONLY_HOUSE.add("2-55-73-1102");
        ONLY_HOUSE.add("44-40-22-4052");
        ONLY_HOUSE.add("44-40-22-4062");
        ONLY_HOUSE.add("2-54-35-2023");
        ONLY_HOUSE.add("44-46-158-31");
        ONLY_HOUSE.add("2-54-149-5032");
        ONLY_HOUSE.add("2-54-159-2062");
        ONLY_HOUSE.add("2-54-149-5061");
        ONLY_HOUSE.add("44-46-4-1095");
        ONLY_HOUSE.add("44-38-1");
        ONLY_HOUSE.add("44-38-6");
        ONLY_HOUSE.add("44-40-4-1134");
        ONLY_HOUSE.add("44-46-3-2094");
        ONLY_HOUSE.add("2-54-152-1052");
        ONLY_HOUSE.add("2-54-67-3062");
        ONLY_HOUSE.add("2-55-69-2021");
        ONLY_HOUSE.add("2-56-384-11");
        ONLY_HOUSE.add("2-56-384-10");
        ONLY_HOUSE.add("2-54-152-1041");
        ONLY_HOUSE.add("2-56-384-16");
        ONLY_HOUSE.add("2-56-384-17");
        ONLY_HOUSE.add("2-56-384-18");
        ONLY_HOUSE.add("2-56-384-12");
        ONLY_HOUSE.add("2-56-384-13");
        ONLY_HOUSE.add("2-56-384-14");
        ONLY_HOUSE.add("2-56-384-15");
        ONLY_HOUSE.add("2-54-94-2061");
        ONLY_HOUSE.add("44-46-141-4161");
        ONLY_HOUSE.add("44-46-141-4162");
        ONLY_HOUSE.add("44-46-141-4163");
        ONLY_HOUSE.add("2-55-140-3");
        ONLY_HOUSE.add("44-46-141-4164");
        ONLY_HOUSE.add("2-55-140-4");
        ONLY_HOUSE.add("44-46-3-2052");
        ONLY_HOUSE.add("2-54-94-2053");
        ONLY_HOUSE.add("44-46-141-4173");
        ONLY_HOUSE.add("44-46-141-4174");
        ONLY_HOUSE.add("44-48-6");
        ONLY_HOUSE.add("44-46-141-3041");
        ONLY_HOUSE.add("44-48-5");
        ONLY_HOUSE.add("44-46-141-4171");
        ONLY_HOUSE.add("44-46-141-4172");
        ONLY_HOUSE.add("44-48-8");
        ONLY_HOUSE.add("44-48-3");
        ONLY_HOUSE.add("44-46-141-3042");
        ONLY_HOUSE.add("44-48-2");
        ONLY_HOUSE.add("44-46-141-4181");
        ONLY_HOUSE.add("44-46-141-3052");
        ONLY_HOUSE.add("44-46-141-4182");
        ONLY_HOUSE.add("44-46-141-3051");
        ONLY_HOUSE.add("44-46-141-4183");
        ONLY_HOUSE.add("44-46-141-4184");
        ONLY_HOUSE.add("2-55-87-1022");
        ONLY_HOUSE.add("2-54-67-3022");
        ONLY_HOUSE.add("44-46-141-4191");
        ONLY_HOUSE.add("44-46-141-4192");
        ONLY_HOUSE.add("44-46-141-3061");
        ONLY_HOUSE.add("44-46-141-4193");
        ONLY_HOUSE.add("44-46-141-3062");
        ONLY_HOUSE.add("44-46-141-4194");
        ONLY_HOUSE.add("2-55-87-1013");
        ONLY_HOUSE.add("44-46-141-4124");
        ONLY_HOUSE.add("44-46-141-4123");
        ONLY_HOUSE.add("44-46-141-4122");
        ONLY_HOUSE.add("44-46-141-4121");
        ONLY_HOUSE.add("44-46-141-3071");
        ONLY_HOUSE.add("44-46-141-3072");
        ONLY_HOUSE.add("44-46-141-4132");
        ONLY_HOUSE.add("2-55-87-1033");
        ONLY_HOUSE.add("44-46-141-4131");
        ONLY_HOUSE.add("44-46-141-4134");
        ONLY_HOUSE.add("44-46-141-4133");
        ONLY_HOUSE.add("44-46-141-3082");
        ONLY_HOUSE.add("44-46-141-3081");
        ONLY_HOUSE.add("44-46-141-4144");
        ONLY_HOUSE.add("44-46-141-4143");
        ONLY_HOUSE.add("44-46-141-4142");
        ONLY_HOUSE.add("44-46-141-3091");
        ONLY_HOUSE.add("44-46-141-3092");
        ONLY_HOUSE.add("44-46-141-4141");
        ONLY_HOUSE.add("44-37-7");
        ONLY_HOUSE.add("44-46-141-4154");
        ONLY_HOUSE.add("44-46-141-4153");
        ONLY_HOUSE.add("44-46-141-4152");
        ONLY_HOUSE.add("44-46-141-4151");
        ONLY_HOUSE.add("44-46-113-22");
        ONLY_HOUSE.add("44-46-113-21");
        ONLY_HOUSE.add("44-40-4-3124");
        ONLY_HOUSE.add("44-46-141-4101");
        ONLY_HOUSE.add("44-46-141-4104");
        ONLY_HOUSE.add("44-46-141-4102");
        ONLY_HOUSE.add("44-46-141-4103");
        ONLY_HOUSE.add("44-46-113-17");
        ONLY_HOUSE.add("44-46-113-16");
        ONLY_HOUSE.add("44-46-113-10");
        ONLY_HOUSE.add("44-46-141-4111");
        ONLY_HOUSE.add("44-46-141-4112");
        ONLY_HOUSE.add("44-46-141-4113");
        ONLY_HOUSE.add("44-46-141-4114");
        ONLY_HOUSE.add("44-40-21-6062");
        ONLY_HOUSE.add("44-46-126-4");
        ONLY_HOUSE.add("44-46-126-2");
        ONLY_HOUSE.add("44-46-126-3");
        ONLY_HOUSE.add("44-46-126-1");
        ONLY_HOUSE.add("2-54-64-2051");
        ONLY_HOUSE.add("2-54-107-2051");
        ONLY_HOUSE.add("2-54-64-2061");
        ONLY_HOUSE.add("44-40-21-6061");
        ONLY_HOUSE.add("44-46-126-7");
        ONLY_HOUSE.add("44-46-126-9");
        ONLY_HOUSE.add("44-46-126-8");
        ONLY_HOUSE.add("44-46-111-8");
        ONLY_HOUSE.add("44-46-111-7");
        ONLY_HOUSE.add("44-46-111-5");
        ONLY_HOUSE.add("2-55-81-5062");
        ONLY_HOUSE.add("44-46-128-22");
        ONLY_HOUSE.add("44-46-128-21");
        ONLY_HOUSE.add("2-54-154-4012");
        ONLY_HOUSE.add("44-46-135-10");
        ONLY_HOUSE.add("44-46-135-16");
        ONLY_HOUSE.add("44-46-135-17");
        ONLY_HOUSE.add("44-46-135-11");
        ONLY_HOUSE.add("44-46-135-13");
        ONLY_HOUSE.add("44-46-135-24");
        ONLY_HOUSE.add("44-46-135-23");
        ONLY_HOUSE.add("44-46-127-25");
        ONLY_HOUSE.add("44-46-127-26");
        ONLY_HOUSE.add("44-46-135-28");
        ONLY_HOUSE.add("44-46-127-28");
        ONLY_HOUSE.add("44-46-127-21");
        ONLY_HOUSE.add("44-46-127-23");
        ONLY_HOUSE.add("44-46-127-24");
        ONLY_HOUSE.add("44-46-127-20");
        ONLY_HOUSE.add("44-46-135-19");
        ONLY_HOUSE.add("44-46-135-30");
        ONLY_HOUSE.add("44-46-135-32");
        ONLY_HOUSE.add("44-46-135-31");
        ONLY_HOUSE.add("44-46-141-3102");
        ONLY_HOUSE.add("44-46-141-3101");
        ONLY_HOUSE.add("44-46-127-17");
        ONLY_HOUSE.add("44-46-127-14");
        ONLY_HOUSE.add("44-46-127-15");
        ONLY_HOUSE.add("44-46-127-18");
        ONLY_HOUSE.add("44-46-127-19");
        ONLY_HOUSE.add("44-46-127-12");
        ONLY_HOUSE.add("44-46-127-10");
        ONLY_HOUSE.add("44-46-127-11");
        ONLY_HOUSE.add("2-54-74-1112");
        ONLY_HOUSE.add("44-46-141-3132");
        ONLY_HOUSE.add("2-55-104-1042");
        ONLY_HOUSE.add("44-46-141-3131");
        ONLY_HOUSE.add("44-47-9");
        ONLY_HOUSE.add("44-47-8");
        ONLY_HOUSE.add("44-46-141-3141");
        ONLY_HOUSE.add("44-46-141-3142");
        ONLY_HOUSE.add("44-46-141-3112");
        ONLY_HOUSE.add("44-46-141-3111");
        ONLY_HOUSE.add("44-46-141-3121");
        ONLY_HOUSE.add("44-46-141-3122");
        ONLY_HOUSE.add("44-46-141-3172");
        ONLY_HOUSE.add("44-46-141-3171");
        ONLY_HOUSE.add("44-46-141-1044");
        ONLY_HOUSE.add("44-46-141-1043");
        ONLY_HOUSE.add("44-46-141-1042");
        ONLY_HOUSE.add("44-46-141-1041");
        ONLY_HOUSE.add("44-46-141-3182");
        ONLY_HOUSE.add("44-46-141-3181");
        ONLY_HOUSE.add("2-55-82-1033");
        ONLY_HOUSE.add("44-46-141-3152");
        ONLY_HOUSE.add("44-46-141-3151");
        ONLY_HOUSE.add("44-46-65-41");
        ONLY_HOUSE.add("2-55-82-1051");
        ONLY_HOUSE.add("44-46-141-3162");
        ONLY_HOUSE.add("44-46-141-3161");
        ONLY_HOUSE.add("44-40-16-1142");
        ONLY_HOUSE.add("44-46-141-2122");
        ONLY_HOUSE.add("44-46-141-2121");
        ONLY_HOUSE.add("44-40-3-4241");
        ONLY_HOUSE.add("44-40-3-4242");
        ONLY_HOUSE.add("44-40-16-1133");
        ONLY_HOUSE.add("44-46-141-2132");
        ONLY_HOUSE.add("44-46-141-2131");
        ONLY_HOUSE.add("44-40-3-4232");
        ONLY_HOUSE.add("44-40-3-4231");
        ONLY_HOUSE.add("44-40-3-4234");
        ONLY_HOUSE.add("44-40-3-4233");
        ONLY_HOUSE.add("44-51-14-13");
        ONLY_HOUSE.add("44-46-141-3192");
        ONLY_HOUSE.add("44-51-14-14");
        ONLY_HOUSE.add("44-51-14-15");
        ONLY_HOUSE.add("44-51-14-16");
        ONLY_HOUSE.add("44-46-141-3191");
        ONLY_HOUSE.add("44-46-141-2101");
        ONLY_HOUSE.add("44-46-141-4244");
        ONLY_HOUSE.add("44-46-141-4243");
        ONLY_HOUSE.add("44-51-14-10");
        ONLY_HOUSE.add("44-46-141-4242");
        ONLY_HOUSE.add("44-51-14-11");
        ONLY_HOUSE.add("44-46-141-2102");
        ONLY_HOUSE.add("44-46-141-4241");
        ONLY_HOUSE.add("44-51-14-12");
        ONLY_HOUSE.add("44-40-3-4263");
        ONLY_HOUSE.add("44-40-3-4262");
        ONLY_HOUSE.add("44-40-3-4264");
        ONLY_HOUSE.add("44-40-16-1113");
        ONLY_HOUSE.add("44-46-141-2112");
        ONLY_HOUSE.add("44-46-141-2111");
        ONLY_HOUSE.add("44-40-3-4254");
        ONLY_HOUSE.add("44-40-3-4253");
        ONLY_HOUSE.add("44-40-3-4252");
        ONLY_HOUSE.add("44-40-3-4251");
        ONLY_HOUSE.add("44-46-141-4221");
        ONLY_HOUSE.add("44-46-141-4222");
        ONLY_HOUSE.add("2-60-9-1908");
        ONLY_HOUSE.add("2-60-9-1909");
        ONLY_HOUSE.add("2-60-9-1906");
        ONLY_HOUSE.add("2-60-9-1907");
        ONLY_HOUSE.add("44-46-141-4223");
        ONLY_HOUSE.add("2-60-9-1904");
        ONLY_HOUSE.add("2-60-9-1905");
        ONLY_HOUSE.add("44-46-141-4224");
        ONLY_HOUSE.add("2-60-9-1902");
        ONLY_HOUSE.add("2-60-9-1903");
        ONLY_HOUSE.add("2-60-9-1901");
        ONLY_HOUSE.add("44-46-128-7");
        ONLY_HOUSE.add("44-40-3-4284");
        ONLY_HOUSE.add("44-40-3-4282");
        ONLY_HOUSE.add("2-60-9-1919");
        ONLY_HOUSE.add("44-46-141-4231");
        ONLY_HOUSE.add("44-46-141-4232");
        ONLY_HOUSE.add("44-46-141-4233");
        ONLY_HOUSE.add("44-46-141-4234");
        ONLY_HOUSE.add("2-60-9-1915");
        ONLY_HOUSE.add("2-60-9-1916");
        ONLY_HOUSE.add("2-60-9-1917");
        ONLY_HOUSE.add("2-60-9-1918");
        ONLY_HOUSE.add("2-60-9-1911");
        ONLY_HOUSE.add("2-60-9-1912");
        ONLY_HOUSE.add("2-60-9-1913");
        ONLY_HOUSE.add("2-60-9-1914");
        ONLY_HOUSE.add("2-55-102-3022");
        ONLY_HOUSE.add("2-60-9-1910");
        ONLY_HOUSE.add("44-40-3-4273");
        ONLY_HOUSE.add("44-40-3-4274");
        ONLY_HOUSE.add("44-40-3-4272");
        ONLY_HOUSE.add("44-46-141-4203");
        ONLY_HOUSE.add("44-46-141-4204");
        ONLY_HOUSE.add("44-46-141-4201");
        ONLY_HOUSE.add("44-46-141-4202");
        ONLY_HOUSE.add("44-40-16-1163");
        ONLY_HOUSE.add("44-46-141-4212");
        ONLY_HOUSE.add("44-46-141-4213");
        ONLY_HOUSE.add("44-46-141-4214");
        ONLY_HOUSE.add("44-46-141-4211");
        ONLY_HOUSE.add("44-46-128-1");
        ONLY_HOUSE.add("2-60-9-1950");
        ONLY_HOUSE.add("2-60-9-1954");
        ONLY_HOUSE.add("2-60-9-1953");
        ONLY_HOUSE.add("44-46-141-2091");
        ONLY_HOUSE.add("2-60-9-1952");
        ONLY_HOUSE.add("2-60-9-1951");
        ONLY_HOUSE.add("2-60-9-1958");
        ONLY_HOUSE.add("2-60-9-1957");
        ONLY_HOUSE.add("44-46-141-2092");
        ONLY_HOUSE.add("2-60-9-1956");
        ONLY_HOUSE.add("2-60-9-1955");
        ONLY_HOUSE.add("2-60-9-1959");
        ONLY_HOUSE.add("2-54-57-11");
        ONLY_HOUSE.add("2-60-9-1941");
        ONLY_HOUSE.add("2-60-9-1940");
        ONLY_HOUSE.add("2-60-9-1943");
        ONLY_HOUSE.add("2-60-9-1942");
        ONLY_HOUSE.add("2-60-9-1945");
        ONLY_HOUSE.add("2-60-9-1944");
        ONLY_HOUSE.add("2-60-9-1947");
        ONLY_HOUSE.add("44-46-141-2082");
        ONLY_HOUSE.add("2-60-9-1946");
        ONLY_HOUSE.add("44-46-141-2081");
        ONLY_HOUSE.add("2-60-9-1949");
        ONLY_HOUSE.add("2-60-9-1948");
        ONLY_HOUSE.add("2-55-107-3023");
        ONLY_HOUSE.add("44-46-141-2071");
        ONLY_HOUSE.add("2-60-9-1936");
        ONLY_HOUSE.add("2-60-9-1935");
        ONLY_HOUSE.add("2-60-9-1934");
        ONLY_HOUSE.add("44-46-141-2072");
        ONLY_HOUSE.add("2-60-9-1933");
        ONLY_HOUSE.add("2-60-9-1932");
        ONLY_HOUSE.add("2-60-9-1931");
        ONLY_HOUSE.add("2-60-9-1930");
        ONLY_HOUSE.add("2-60-9-1939");
        ONLY_HOUSE.add("2-60-9-1938");
        ONLY_HOUSE.add("2-60-9-1937");
        ONLY_HOUSE.add("2-54-74-1093");
        ONLY_HOUSE.add("2-54-74-1094");
        ONLY_HOUSE.add("2-60-9-1923");
        ONLY_HOUSE.add("44-46-141-2062");
        ONLY_HOUSE.add("44-46-127-9");
        ONLY_HOUSE.add("2-60-9-1922");
        ONLY_HOUSE.add("44-46-141-2061");
        ONLY_HOUSE.add("2-60-9-1925");
        ONLY_HOUSE.add("2-60-9-1924");
        ONLY_HOUSE.add("44-46-127-6");
        ONLY_HOUSE.add("44-46-127-5");
        ONLY_HOUSE.add("2-60-9-1921");
        ONLY_HOUSE.add("44-46-127-8");
        ONLY_HOUSE.add("2-60-9-1920");
        ONLY_HOUSE.add("2-60-9-1927");
        ONLY_HOUSE.add("2-60-9-1926");
        ONLY_HOUSE.add("2-60-9-1929");
        ONLY_HOUSE.add("2-60-9-1928");
        ONLY_HOUSE.add("2-60-9-1990");
        ONLY_HOUSE.add("2-60-9-1994");
        ONLY_HOUSE.add("2-60-9-1993");
        ONLY_HOUSE.add("2-60-9-1992");
        ONLY_HOUSE.add("2-60-9-1991");
        ONLY_HOUSE.add("44-46-141-2052");
        ONLY_HOUSE.add("2-60-9-1997");
        ONLY_HOUSE.add("2-60-9-1998");
        ONLY_HOUSE.add("2-60-9-1995");
        ONLY_HOUSE.add("2-60-9-1996");
        ONLY_HOUSE.add("2-60-9-1999");
        ONLY_HOUSE.add("44-46-141-2051");
        ONLY_HOUSE.add("2-54-74-1072");
        ONLY_HOUSE.add("2-60-9-1981");
        ONLY_HOUSE.add("2-60-9-1980");
        ONLY_HOUSE.add("2-60-9-1983");
        ONLY_HOUSE.add("2-60-9-1982");
        ONLY_HOUSE.add("44-46-141-2041");
        ONLY_HOUSE.add("44-46-141-2042");
        ONLY_HOUSE.add("2-60-9-1984");
        ONLY_HOUSE.add("2-60-9-1985");
        ONLY_HOUSE.add("2-60-9-1986");
        ONLY_HOUSE.add("2-60-9-1987");
        ONLY_HOUSE.add("44-40-16-1102");
        ONLY_HOUSE.add("2-60-9-1988");
        ONLY_HOUSE.add("2-60-9-1989");
        ONLY_HOUSE.add("2-60-9-1972");
        ONLY_HOUSE.add("2-60-9-1971");
        ONLY_HOUSE.add("2-60-9-1970");
        ONLY_HOUSE.add("44-46-126-11");
        ONLY_HOUSE.add("2-60-9-1979");
        ONLY_HOUSE.add("44-46-126-18");
        ONLY_HOUSE.add("44-46-126-17");
        ONLY_HOUSE.add("2-60-9-1977");
        ONLY_HOUSE.add("2-60-9-1978");
        ONLY_HOUSE.add("44-46-126-14");
        ONLY_HOUSE.add("2-60-9-1975");
        ONLY_HOUSE.add("2-60-9-1976");
        ONLY_HOUSE.add("44-46-126-13");
        ONLY_HOUSE.add("44-46-126-16");
        ONLY_HOUSE.add("2-60-9-1973");
        ONLY_HOUSE.add("44-46-126-15");
        ONLY_HOUSE.add("2-60-9-1974");
        ONLY_HOUSE.add("2-60-9-1961");
        ONLY_HOUSE.add("2-54-176-1032");
        ONLY_HOUSE.add("2-60-9-1960");
        ONLY_HOUSE.add("44-46-126-23");
        ONLY_HOUSE.add("44-46-126-22");
        ONLY_HOUSE.add("44-46-126-20");
        ONLY_HOUSE.add("2-60-9-1966");
        ONLY_HOUSE.add("2-60-9-1967");
        ONLY_HOUSE.add("2-60-9-1968");
        ONLY_HOUSE.add("2-60-9-1969");
        ONLY_HOUSE.add("44-46-126-28");
        ONLY_HOUSE.add("44-46-126-27");
        ONLY_HOUSE.add("2-60-9-1962");
        ONLY_HOUSE.add("44-46-126-26");
        ONLY_HOUSE.add("2-60-9-1963");
        ONLY_HOUSE.add("44-46-126-25");
        ONLY_HOUSE.add("2-60-9-1964");
        ONLY_HOUSE.add("2-60-9-1965");
        ONLY_HOUSE.add("2-54-74-1022");
        ONLY_HOUSE.add("2-54-94-2031");
        ONLY_HOUSE.add("44-46-65-52");
        ONLY_HOUSE.add("44-46-112-4");
        ONLY_HOUSE.add("44-46-112-3");
        ONLY_HOUSE.add("44-46-112-8");
        ONLY_HOUSE.add("44-46-112-7");
        ONLY_HOUSE.add("2-55-89-1051");
        ONLY_HOUSE.add("72-56-1");
        ONLY_HOUSE.add("44-46-141-3222");
        ONLY_HOUSE.add("44-46-141-3221");
        ONLY_HOUSE.add("44-46-141-3212");
        ONLY_HOUSE.add("44-46-141-3211");
        ONLY_HOUSE.add("44-46-112-20");
        ONLY_HOUSE.add("44-46-141-3202");
        ONLY_HOUSE.add("44-46-141-3201");
        ONLY_HOUSE.add("44-46-112-21");
        ONLY_HOUSE.add("44-46-112-22");
        ONLY_HOUSE.add("44-46-112-25");
        ONLY_HOUSE.add("44-46-112-26");
        ONLY_HOUSE.add("44-46-112-10");
        ONLY_HOUSE.add("44-46-112-12");
        ONLY_HOUSE.add("2-54-65-2062");
        ONLY_HOUSE.add("44-46-98-96");
        ONLY_HOUSE.add("44-46-98-98");
        ONLY_HOUSE.add("44-46-98-84");
        ONLY_HOUSE.add("2-55-81-3063");
        ONLY_HOUSE.add("2-55-67-2062");
        ONLY_HOUSE.add("2-55-118-3042");
        ONLY_HOUSE.add("2-54-127-3062");
        ONLY_HOUSE.add("2-54-127-3061");
        ONLY_HOUSE.add("44-40-3-3093");
        ONLY_HOUSE.add("44-40-3-3091");
        ONLY_HOUSE.add("44-40-3-3081");
        ONLY_HOUSE.add("44-40-17-3041");
        ONLY_HOUSE.add("44-40-17-3042");
        ONLY_HOUSE.add("44-40-3-3071");
        ONLY_HOUSE.add("44-40-3-3073");
        ONLY_HOUSE.add("44-40-17-3031");
        ONLY_HOUSE.add("44-40-17-3032");
        ONLY_HOUSE.add("44-40-3-3063");
        ONLY_HOUSE.add("44-40-3-4191");
        ONLY_HOUSE.add("44-40-3-4192");
        ONLY_HOUSE.add("44-40-3-4181");
        ONLY_HOUSE.add("44-40-3-4182");
        ONLY_HOUSE.add("2-55-94-2112");
        ONLY_HOUSE.add("44-40-3-4172");
        ONLY_HOUSE.add("44-40-3-4171");
        ONLY_HOUSE.add("2-55-94-2101");
        ONLY_HOUSE.add("2-54-127-3012");
        ONLY_HOUSE.add("44-40-3-4161");
        ONLY_HOUSE.add("44-40-3-4162");
        ONLY_HOUSE.add("2-55-88-2041");
        ONLY_HOUSE.add("2-54-104-3061");
        ONLY_HOUSE.add("44-46-141-2201");
        ONLY_HOUSE.add("44-46-141-2202");
        ONLY_HOUSE.add("44-40-3-4151");
        ONLY_HOUSE.add("44-40-3-4152");
        ONLY_HOUSE.add("44-46-141-2212");
        ONLY_HOUSE.add("44-46-141-2211");
        ONLY_HOUSE.add("2-54-65-2011");
        ONLY_HOUSE.add("44-40-3-4142");
        ONLY_HOUSE.add("2-54-65-2021");
        ONLY_HOUSE.add("44-46-141-2222");
        ONLY_HOUSE.add("44-46-141-2221");
        ONLY_HOUSE.add("44-46-3-2252");
        ONLY_HOUSE.add("44-40-3-4132");
        ONLY_HOUSE.add("44-40-19-1021");
        ONLY_HOUSE.add("44-46-141-2231");
        ONLY_HOUSE.add("44-46-141-2232");
        ONLY_HOUSE.add("44-40-19-1032");
        ONLY_HOUSE.add("44-40-19-1022");
        ONLY_HOUSE.add("44-46-141-2242");
        ONLY_HOUSE.add("44-46-141-2241");
        ONLY_HOUSE.add("44-40-19-1042");
        ONLY_HOUSE.add("44-46-3-2234");
        ONLY_HOUSE.add("2-54-29-5073");
        ONLY_HOUSE.add("2-60-9-1858");
        ONLY_HOUSE.add("2-60-9-1859");
        ONLY_HOUSE.add("2-60-9-1856");
        ONLY_HOUSE.add("2-60-9-1857");
        ONLY_HOUSE.add("2-60-9-1854");
        ONLY_HOUSE.add("2-60-9-1855");
        ONLY_HOUSE.add("2-60-9-1852");
        ONLY_HOUSE.add("2-60-9-1853");
        ONLY_HOUSE.add("44-46-141-2151");
        ONLY_HOUSE.add("44-46-141-2152");
        ONLY_HOUSE.add("2-60-9-1851");
        ONLY_HOUSE.add("44-46-124-7");
        ONLY_HOUSE.add("2-60-9-1850");
        ONLY_HOUSE.add("44-46-124-6");
        ONLY_HOUSE.add("44-46-124-5");
        ONLY_HOUSE.add("44-46-124-4");
        ONLY_HOUSE.add("44-40-3-4211");
        ONLY_HOUSE.add("44-46-124-3");
        ONLY_HOUSE.add("44-40-3-4212");
        ONLY_HOUSE.add("44-46-124-2");
        ONLY_HOUSE.add("44-46-124-1");
        ONLY_HOUSE.add("44-40-3-4213");
        ONLY_HOUSE.add("44-40-3-4214");
        ONLY_HOUSE.add("2-60-9-1845");
        ONLY_HOUSE.add("2-60-9-1846");
        ONLY_HOUSE.add("2-60-9-1847");
        ONLY_HOUSE.add("2-60-9-1848");
        ONLY_HOUSE.add("2-60-9-1841");
        ONLY_HOUSE.add("2-60-9-1842");
        ONLY_HOUSE.add("2-60-9-1843");
        ONLY_HOUSE.add("2-60-9-1844");
        ONLY_HOUSE.add("44-46-141-2142");
        ONLY_HOUSE.add("2-60-9-1849");
        ONLY_HOUSE.add("44-46-141-2141");
        ONLY_HOUSE.add("44-40-3-4221");
        ONLY_HOUSE.add("2-60-9-1840");
        ONLY_HOUSE.add("2-54-107-3011");
        ONLY_HOUSE.add("44-40-3-4224");
        ONLY_HOUSE.add("44-40-3-4222");
        ONLY_HOUSE.add("44-40-3-4223");
        ONLY_HOUSE.add("2-60-9-1876");
        ONLY_HOUSE.add("2-60-9-1877");
        ONLY_HOUSE.add("2-60-9-1874");
        ONLY_HOUSE.add("2-60-9-1875");
        ONLY_HOUSE.add("44-46-141-2171");
        ONLY_HOUSE.add("2-60-9-1878");
        ONLY_HOUSE.add("2-60-9-1879");
        ONLY_HOUSE.add("2-55-123-2052");
        ONLY_HOUSE.add("44-46-141-2172");
        ONLY_HOUSE.add("44-40-19-1111");
        ONLY_HOUSE.add("2-54-150-3032");
        ONLY_HOUSE.add("2-60-9-1873");
        ONLY_HOUSE.add("2-60-9-1872");
        ONLY_HOUSE.add("2-60-9-1871");
        ONLY_HOUSE.add("2-60-9-1870");
        ONLY_HOUSE.add("2-60-9-1863");
        ONLY_HOUSE.add("2-60-9-1864");
        ONLY_HOUSE.add("2-60-9-1865");
        ONLY_HOUSE.add("2-60-9-1866");
        ONLY_HOUSE.add("2-60-9-1867");
        ONLY_HOUSE.add("44-46-141-2161");
        ONLY_HOUSE.add("2-60-9-1868");
        ONLY_HOUSE.add("2-60-9-1869");
        ONLY_HOUSE.add("44-46-141-2162");
        ONLY_HOUSE.add("44-40-3-4202");
        ONLY_HOUSE.add("44-40-3-4201");
        ONLY_HOUSE.add("2-60-9-1860");
        ONLY_HOUSE.add("2-54-103-2042");
        ONLY_HOUSE.add("2-60-9-1862");
        ONLY_HOUSE.add("2-60-9-1861");
        ONLY_HOUSE.add("2-60-9-1819");
        ONLY_HOUSE.add("2-60-9-1818");
        ONLY_HOUSE.add("2-60-9-1817");
        ONLY_HOUSE.add("2-60-9-1816");
        ONLY_HOUSE.add("2-60-9-1815");
        ONLY_HOUSE.add("44-46-141-2192");
        ONLY_HOUSE.add("2-60-9-1814");
        ONLY_HOUSE.add("44-46-141-2191");
        ONLY_HOUSE.add("2-60-9-1813");
        ONLY_HOUSE.add("2-60-9-1812");
        ONLY_HOUSE.add("2-60-9-1811");
        ONLY_HOUSE.add("2-60-9-1810");
        ONLY_HOUSE.add("2-60-9-1809");
        ONLY_HOUSE.add("2-60-9-1806");
        ONLY_HOUSE.add("2-60-9-1805");
        ONLY_HOUSE.add("2-60-9-1808");
        ONLY_HOUSE.add("2-60-9-1807");
        ONLY_HOUSE.add("2-60-9-1802");
        ONLY_HOUSE.add("2-60-9-1801");
        ONLY_HOUSE.add("44-46-141-2182");
        ONLY_HOUSE.add("44-46-141-2181");
        ONLY_HOUSE.add("2-60-9-1804");
        ONLY_HOUSE.add("2-60-9-1803");
        ONLY_HOUSE.add("44-46-76-10");
        ONLY_HOUSE.add("2-60-9-1839");
        ONLY_HOUSE.add("2-60-9-1838");
        ONLY_HOUSE.add("2-60-9-1833");
        ONLY_HOUSE.add("2-60-9-1832");
        ONLY_HOUSE.add("2-60-9-1831");
        ONLY_HOUSE.add("2-60-9-1830");
        ONLY_HOUSE.add("2-60-9-1837");
        ONLY_HOUSE.add("2-60-9-1836");
        ONLY_HOUSE.add("2-60-9-1835");
        ONLY_HOUSE.add("2-60-9-1834");
        ONLY_HOUSE.add("2-60-9-1828");
        ONLY_HOUSE.add("2-60-9-1827");
        ONLY_HOUSE.add("2-60-9-1829");
        ONLY_HOUSE.add("2-60-9-1820");
        ONLY_HOUSE.add("2-60-9-1822");
        ONLY_HOUSE.add("2-60-9-1821");
        ONLY_HOUSE.add("2-60-9-1824");
        ONLY_HOUSE.add("2-60-9-1823");
        ONLY_HOUSE.add("2-60-9-1826");
        ONLY_HOUSE.add("2-60-9-1825");
        ONLY_HOUSE.add("44-40-13-4011");
        ONLY_HOUSE.add("44-40-13-4012");
        ONLY_HOUSE.add("44-46-98-13");
        ONLY_HOUSE.add("44-46-98-12");
        ONLY_HOUSE.add("44-46-98-11");
        ONLY_HOUSE.add("44-46-98-18");
        ONLY_HOUSE.add("44-46-98-17");
        ONLY_HOUSE.add("44-46-98-10");
        ONLY_HOUSE.add("44-46-98-27");
        ONLY_HOUSE.add("44-46-98-26");
        ONLY_HOUSE.add("44-46-98-29");
        ONLY_HOUSE.add("44-46-98-28");
        ONLY_HOUSE.add("44-46-98-22");
        ONLY_HOUSE.add("44-46-98-24");
        ONLY_HOUSE.add("44-46-98-20");
        ONLY_HOUSE.add("44-46-98-38");
        ONLY_HOUSE.add("44-46-98-37");
        ONLY_HOUSE.add("44-46-98-34");
        ONLY_HOUSE.add("44-46-98-33");
        ONLY_HOUSE.add("44-46-98-31");
        ONLY_HOUSE.add("44-46-98-32");
        ONLY_HOUSE.add("44-46-98-30");
        ONLY_HOUSE.add("2-60-9-1892");
        ONLY_HOUSE.add("2-60-9-1893");
        ONLY_HOUSE.add("2-60-9-1894");
        ONLY_HOUSE.add("44-40-3-3121");
        ONLY_HOUSE.add("2-60-9-1895");
        ONLY_HOUSE.add("2-60-9-1890");
        ONLY_HOUSE.add("2-60-9-1891");
        ONLY_HOUSE.add("44-46-98-45");
        ONLY_HOUSE.add("44-46-98-46");
        ONLY_HOUSE.add("44-46-98-47");
        ONLY_HOUSE.add("44-46-98-48");
        ONLY_HOUSE.add("44-46-98-49");
        ONLY_HOUSE.add("44-46-98-40");
        ONLY_HOUSE.add("44-46-98-43");
        ONLY_HOUSE.add("44-46-98-42");
        ONLY_HOUSE.add("2-60-9-1897");
        ONLY_HOUSE.add("2-60-9-1896");
        ONLY_HOUSE.add("2-60-9-1899");
        ONLY_HOUSE.add("44-46-113-8");
        ONLY_HOUSE.add("2-60-9-1898");
        ONLY_HOUSE.add("2-60-9-1883");
        ONLY_HOUSE.add("44-40-3-3131");
        ONLY_HOUSE.add("2-60-9-1884");
        ONLY_HOUSE.add("2-60-9-1881");
        ONLY_HOUSE.add("2-60-9-1882");
        ONLY_HOUSE.add("2-60-9-1880");
        ONLY_HOUSE.add("44-40-3-3133");
        ONLY_HOUSE.add("44-46-98-57");
        ONLY_HOUSE.add("44-46-98-58");
        ONLY_HOUSE.add("44-46-98-56");
        ONLY_HOUSE.add("44-46-98-50");
        ONLY_HOUSE.add("44-46-98-54");
        ONLY_HOUSE.add("44-46-98-52");
        ONLY_HOUSE.add("44-46-98-51");
        ONLY_HOUSE.add("2-60-9-1889");
        ONLY_HOUSE.add("2-60-9-1888");
        ONLY_HOUSE.add("2-60-9-1887");
        ONLY_HOUSE.add("2-60-9-1886");
        ONLY_HOUSE.add("2-60-9-1885");
        ONLY_HOUSE.add("44-40-3-3101");
        ONLY_HOUSE.add("44-40-3-3103");
        ONLY_HOUSE.add("44-46-98-62");
        ONLY_HOUSE.add("44-46-98-61");
        ONLY_HOUSE.add("44-40-3-3113");
        ONLY_HOUSE.add("44-46-98-74");
        ONLY_HOUSE.add("44-46-98-73");
        ONLY_HOUSE.add("44-49-2");
        ONLY_HOUSE.add("2-54-144-2042");
        ONLY_HOUSE.add("44-49-7");
        ONLY_HOUSE.add("44-49-6");
        ONLY_HOUSE.add("44-49-5");
        ONLY_HOUSE.add("44-49-4");
        ONLY_HOUSE.add("44-49-3");
        ONLY_HOUSE.add("2-55-94-2052");
        ONLY_HOUSE.add("2-55-67-1063");
        ONLY_HOUSE.add("44-40-3-4082");
        ONLY_HOUSE.add("2-54-65-1042");
        ONLY_HOUSE.add("2-55-104-3053");
        ONLY_HOUSE.add("44-40-3-4072");
        ONLY_HOUSE.add("44-46-67-25");
        ONLY_HOUSE.add("44-40-3-4092");
        ONLY_HOUSE.add("2-55-76-3041");
        ONLY_HOUSE.add("44-40-8-3041");
        ONLY_HOUSE.add("44-40-8-3042");
        ONLY_HOUSE.add("2-54-115-1093");
        ONLY_HOUSE.add("44-40-8-3031");
        ONLY_HOUSE.add("44-40-8-3032");
        ONLY_HOUSE.add("2-54-68-4022");
        ONLY_HOUSE.add("44-40-3-4061");
        ONLY_HOUSE.add("44-40-19-1171");
        ONLY_HOUSE.add("44-40-3-4062");
        ONLY_HOUSE.add("44-40-19-1172");
        ONLY_HOUSE.add("44-40-19-1151");
        ONLY_HOUSE.add("44-40-19-1152");
        ONLY_HOUSE.add("2-54-115-1062");
        ONLY_HOUSE.add("44-40-19-1162");
        ONLY_HOUSE.add("44-46-3-2115");
        ONLY_HOUSE.add("44-40-19-1122");
        ONLY_HOUSE.add("44-40-8-3052");
        ONLY_HOUSE.add("44-40-8-3051");
        ONLY_HOUSE.add("2-55-64-1052");
        ONLY_HOUSE.add("2-60-9-1755");
        ONLY_HOUSE.add("2-60-9-1756");
        ONLY_HOUSE.add("2-60-9-1753");
        ONLY_HOUSE.add("2-60-9-1754");
        ONLY_HOUSE.add("2-60-9-1759");
        ONLY_HOUSE.add("2-60-9-1757");
        ONLY_HOUSE.add("2-60-9-1758");
        ONLY_HOUSE.add("2-60-9-1752");
        ONLY_HOUSE.add("2-60-9-1751");
        ONLY_HOUSE.add("2-60-9-1750");
        ONLY_HOUSE.add("2-54-81-4013");
        ONLY_HOUSE.add("2-60-9-1742");
        ONLY_HOUSE.add("2-60-9-1743");
        ONLY_HOUSE.add("2-60-9-1744");
        ONLY_HOUSE.add("2-60-9-1745");
        ONLY_HOUSE.add("2-60-9-1746");
        ONLY_HOUSE.add("2-60-9-1747");
        ONLY_HOUSE.add("2-60-9-1748");
        ONLY_HOUSE.add("2-60-9-1749");
        ONLY_HOUSE.add("2-54-90-4054");
        ONLY_HOUSE.add("2-60-9-1741");
        ONLY_HOUSE.add("2-60-9-1740");
        ONLY_HOUSE.add("2-55-91-3062");
        ONLY_HOUSE.add("2-55-91-3063");
        ONLY_HOUSE.add("2-60-9-1739");
        ONLY_HOUSE.add("2-60-9-1737");
        ONLY_HOUSE.add("2-60-9-1738");
        ONLY_HOUSE.add("2-60-9-1735");
        ONLY_HOUSE.add("2-60-9-1736");
        ONLY_HOUSE.add("2-60-9-1733");
        ONLY_HOUSE.add("2-60-9-1734");
        ONLY_HOUSE.add("2-60-9-1731");
        ONLY_HOUSE.add("2-60-9-1732");
        ONLY_HOUSE.add("2-60-9-1730");
        ONLY_HOUSE.add("2-55-64-1022");
        ONLY_HOUSE.add("2-60-9-1728");
        ONLY_HOUSE.add("2-60-9-1729");
        ONLY_HOUSE.add("2-55-74-1032");
        ONLY_HOUSE.add("44-40-20-2");
        ONLY_HOUSE.add("44-40-20-1");
        ONLY_HOUSE.add("44-40-20-4");
        ONLY_HOUSE.add("2-60-9-1724");
        ONLY_HOUSE.add("44-40-20-3");
        ONLY_HOUSE.add("2-60-9-1725");
        ONLY_HOUSE.add("44-40-20-6");
        ONLY_HOUSE.add("2-60-9-1726");
        ONLY_HOUSE.add("44-40-20-5");
        ONLY_HOUSE.add("2-60-9-1727");
        ONLY_HOUSE.add("2-60-9-1720");
        ONLY_HOUSE.add("44-40-20-8");
        ONLY_HOUSE.add("2-60-9-1721");
        ONLY_HOUSE.add("44-40-20-7");
        ONLY_HOUSE.add("44-46-125-8");
        ONLY_HOUSE.add("2-60-9-1722");
        ONLY_HOUSE.add("2-60-9-1723");
        ONLY_HOUSE.add("44-40-20-9");
        ONLY_HOUSE.add("44-40-13-4112");
        ONLY_HOUSE.add("2-54-81-4021");
        ONLY_HOUSE.add("2-54-81-4023");
        ONLY_HOUSE.add("2-60-9-1712");
        ONLY_HOUSE.add("2-60-9-1711");
        ONLY_HOUSE.add("44-46-124-23");
        ONLY_HOUSE.add("2-60-9-1710");
        ONLY_HOUSE.add("44-46-124-24");
        ONLY_HOUSE.add("44-46-124-25");
        ONLY_HOUSE.add("44-46-124-26");
        ONLY_HOUSE.add("2-60-9-1716");
        ONLY_HOUSE.add("2-60-9-1715");
        ONLY_HOUSE.add("2-54-127-4042");
        ONLY_HOUSE.add("2-60-9-1714");
        ONLY_HOUSE.add("2-60-9-1713");
        ONLY_HOUSE.add("2-60-9-1719");
        ONLY_HOUSE.add("2-60-9-1718");
        ONLY_HOUSE.add("2-60-9-1717");
        ONLY_HOUSE.add("44-46-124-20");
        ONLY_HOUSE.add("44-46-124-21");
        ONLY_HOUSE.add("2-55-101-2041");
        ONLY_HOUSE.add("44-46-125-3");
        ONLY_HOUSE.add("44-46-124-13");
        ONLY_HOUSE.add("44-46-124-14");
        ONLY_HOUSE.add("2-60-9-1701");
        ONLY_HOUSE.add("2-54-127-4052");
        ONLY_HOUSE.add("44-46-124-12");
        ONLY_HOUSE.add("2-60-9-1703");
        ONLY_HOUSE.add("2-60-9-1702");
        ONLY_HOUSE.add("44-46-124-18");
        ONLY_HOUSE.add("2-60-9-1705");
        ONLY_HOUSE.add("44-46-124-16");
        ONLY_HOUSE.add("2-60-9-1704");
        ONLY_HOUSE.add("2-60-9-1707");
        ONLY_HOUSE.add("2-60-9-1706");
        ONLY_HOUSE.add("2-60-9-1709");
        ONLY_HOUSE.add("2-60-9-1708");
        ONLY_HOUSE.add("2-54-181-4021");
        ONLY_HOUSE.add("44-46-124-10");
        ONLY_HOUSE.add("2-54-115-1112");
        ONLY_HOUSE.add("2-54-181-4041");
        ONLY_HOUSE.add("2-55-77-1022");
        ONLY_HOUSE.add("44-46-114-1");
        ONLY_HOUSE.add("44-46-114-5");
        ONLY_HOUSE.add("44-46-114-6");
        ONLY_HOUSE.add("2-54-107-4031");
        ONLY_HOUSE.add("2-55-104-3043");
        ONLY_HOUSE.add("2-60-9-1790");
        ONLY_HOUSE.add("2-60-9-1791");
        ONLY_HOUSE.add("2-60-9-1792");
        ONLY_HOUSE.add("2-60-9-1793");
        ONLY_HOUSE.add("2-60-9-1794");
        ONLY_HOUSE.add("2-60-9-1795");
        ONLY_HOUSE.add("2-60-9-1796");
        ONLY_HOUSE.add("2-54-72-1031");
        ONLY_HOUSE.add("2-60-9-1798");
        ONLY_HOUSE.add("2-60-9-1797");
        ONLY_HOUSE.add("2-60-9-1799");
        ONLY_HOUSE.add("2-60-9-1780");
        ONLY_HOUSE.add("44-49-14");
        ONLY_HOUSE.add("2-60-9-1781");
        ONLY_HOUSE.add("44-49-12");
        ONLY_HOUSE.add("44-49-13");
        ONLY_HOUSE.add("2-60-9-1784");
        ONLY_HOUSE.add("2-60-9-1785");
        ONLY_HOUSE.add("2-60-9-1782");
        ONLY_HOUSE.add("2-60-9-1783");
        ONLY_HOUSE.add("2-60-9-1789");
        ONLY_HOUSE.add("2-60-9-1788");
        ONLY_HOUSE.add("2-60-9-1787");
        ONLY_HOUSE.add("2-60-9-1786");
        ONLY_HOUSE.add("2-60-9-1771");
        ONLY_HOUSE.add("2-60-9-1772");
        ONLY_HOUSE.add("2-60-9-1773");
        ONLY_HOUSE.add("2-60-9-1774");
        ONLY_HOUSE.add("2-60-9-1770");
        ONLY_HOUSE.add("2-60-9-1779");
        ONLY_HOUSE.add("2-54-72-1052");
        ONLY_HOUSE.add("2-60-9-1776");
        ONLY_HOUSE.add("2-60-9-1775");
        ONLY_HOUSE.add("2-60-9-1778");
        ONLY_HOUSE.add("2-60-9-1777");
        ONLY_HOUSE.add("2-60-9-1762");
        ONLY_HOUSE.add("2-60-9-1763");
        ONLY_HOUSE.add("2-60-9-1760");
        ONLY_HOUSE.add("2-60-9-1761");
        ONLY_HOUSE.add("2-60-9-1769");
        ONLY_HOUSE.add("2-60-9-1768");
        ONLY_HOUSE.add("2-60-9-1767");
        ONLY_HOUSE.add("2-60-9-1766");
        ONLY_HOUSE.add("2-60-9-1765");
        ONLY_HOUSE.add("2-60-9-1764");
        ONLY_HOUSE.add("2-55-95-2031");
        ONLY_HOUSE.add("44-46-123-12");
        ONLY_HOUSE.add("44-46-123-13");
        ONLY_HOUSE.add("44-46-123-14");
        ONLY_HOUSE.add("44-55-1");
        ONLY_HOUSE.add("44-55-2");
        ONLY_HOUSE.add("44-55-3");
        ONLY_HOUSE.add("44-46-123-20");
        ONLY_HOUSE.add("44-55-4");
        ONLY_HOUSE.add("44-55-5");
        ONLY_HOUSE.add("44-55-6");
        ONLY_HOUSE.add("2-54-130-1021");
        ONLY_HOUSE.add("44-46-123-26");
        ONLY_HOUSE.add("44-46-123-27");
        ONLY_HOUSE.add("44-46-123-22");
        ONLY_HOUSE.add("44-46-123-19");
        ONLY_HOUSE.add("2-55-123-5062");
        ONLY_HOUSE.add("2-54-32-5043");
        ONLY_HOUSE.add("44-40-3-1093");
        ONLY_HOUSE.add("44-40-3-1094");
        ONLY_HOUSE.add("44-40-7-1011");
        ONLY_HOUSE.add("44-40-7-1012");
        ONLY_HOUSE.add("44-40-3-1082");
        ONLY_HOUSE.add("44-40-3-1083");
        ONLY_HOUSE.add("44-46-134-20");
        ONLY_HOUSE.add("44-46-134-23");
        ONLY_HOUSE.add("44-46-134-26");
        ONLY_HOUSE.add("44-46-134-19");
        ONLY_HOUSE.add("2-54-130-1061");
        ONLY_HOUSE.add("2-55-112-5022");
        ONLY_HOUSE.add("44-40-7-1031");
        ONLY_HOUSE.add("44-40-7-1032");
        ONLY_HOUSE.add("2-54-150-1041");
        ONLY_HOUSE.add("44-40-25-3012");
        ONLY_HOUSE.add("2-55-71-1051");
        ONLY_HOUSE.add("2-56-384-9");
        ONLY_HOUSE.add("2-56-384-8");
        ONLY_HOUSE.add("2-56-384-7");
        ONLY_HOUSE.add("2-55-100-3041");
        ONLY_HOUSE.add("2-56-384-6");
        ONLY_HOUSE.add("2-56-384-5");
        ONLY_HOUSE.add("2-56-384-1");
        ONLY_HOUSE.add("2-54-155-2052");
        ONLY_HOUSE.add("2-54-153-3041");
        ONLY_HOUSE.add("2-54-150-1012");
        ONLY_HOUSE.add("2-55-74-1042");
        ONLY_HOUSE.add("44-40-25-3091");
        ONLY_HOUSE.add("2-55-74-1061");
        ONLY_HOUSE.add("44-40-3-1104");
        ONLY_HOUSE.add("44-40-3-1112");
        ONLY_HOUSE.add("44-46-144-3062");
        ONLY_HOUSE.add("2-54-34-6063");
        ONLY_HOUSE.add("2-54-32-2071");
        ONLY_HOUSE.add("44-40-25-3041");
        ONLY_HOUSE.add("44-44-8");
        ONLY_HOUSE.add("44-44-9");
        ONLY_HOUSE.add("44-44-2");
        ONLY_HOUSE.add("44-44-3");
        ONLY_HOUSE.add("44-44-1");
        ONLY_HOUSE.add("44-44-6");
        ONLY_HOUSE.add("44-44-7");
        ONLY_HOUSE.add("44-44-4");
        ONLY_HOUSE.add("44-44-5");
        ONLY_HOUSE.add("44-46-144-3052");
        ONLY_HOUSE.add("44-40-3-1164");
        ONLY_HOUSE.add("44-40-3-1163");
        ONLY_HOUSE.add("2-54-162-2031");
        ONLY_HOUSE.add("44-40-5-5051");
        ONLY_HOUSE.add("44-40-5-5052");
        ONLY_HOUSE.add("44-40-3-1172");
        ONLY_HOUSE.add("44-40-3-1174");
        ONLY_HOUSE.add("44-40-3-1173");
        ONLY_HOUSE.add("44-33-5");
        ONLY_HOUSE.add("44-33-6");
        ONLY_HOUSE.add("44-33-7");
        ONLY_HOUSE.add("44-33-8");
        ONLY_HOUSE.add("44-40-7-1041");
        ONLY_HOUSE.add("44-40-3-1184");
        ONLY_HOUSE.add("2-54-105-3063");
        ONLY_HOUSE.add("44-40-3-1183");
        ONLY_HOUSE.add("44-40-3-1182");
        ONLY_HOUSE.add("2-60-9-384");
        ONLY_HOUSE.add("2-54-171-3082");
        ONLY_HOUSE.add("44-40-5-5032");
        ONLY_HOUSE.add("44-40-3-1194");
        ONLY_HOUSE.add("44-40-3-1193");
        ONLY_HOUSE.add("44-46-144-3092");
        ONLY_HOUSE.add("44-40-3-1192");
        ONLY_HOUSE.add("44-40-3-1122");
        ONLY_HOUSE.add("44-40-3-3261");
        ONLY_HOUSE.add("44-40-3-3262");
        ONLY_HOUSE.add("44-40-3-1123");
        ONLY_HOUSE.add("44-40-3-1121");
        ONLY_HOUSE.add("44-40-5-5091");
        ONLY_HOUSE.add("2-54-153-2021");
        ONLY_HOUSE.add("44-40-5-5092");
        ONLY_HOUSE.add("2-54-114-1032");
        ONLY_HOUSE.add("44-40-3-1114");
        ONLY_HOUSE.add("44-40-3-1113");
        ONLY_HOUSE.add("44-40-3-1133");
        ONLY_HOUSE.add("44-40-3-3272");
        ONLY_HOUSE.add("2-54-153-2031");
        ONLY_HOUSE.add("44-40-5-5081");
        ONLY_HOUSE.add("44-40-5-5082");
        ONLY_HOUSE.add("44-40-3-1124");
        ONLY_HOUSE.add("44-40-3-3281");
        ONLY_HOUSE.add("44-40-3-3282");
        ONLY_HOUSE.add("44-40-3-1142");
        ONLY_HOUSE.add("44-40-5-5071");
        ONLY_HOUSE.add("44-40-5-5072");
        ONLY_HOUSE.add("44-40-3-1152");
        ONLY_HOUSE.add("44-40-3-1153");
        ONLY_HOUSE.add("44-40-3-1154");
        ONLY_HOUSE.add("44-40-5-5061");
        ONLY_HOUSE.add("2-54-114-1042");
        ONLY_HOUSE.add("44-40-5-5062");
        ONLY_HOUSE.add("2-54-129-4052");
        ONLY_HOUSE.add("2-54-161-2061");
        ONLY_HOUSE.add("44-40-3-3253");
        ONLY_HOUSE.add("44-40-3-3252");
        ONLY_HOUSE.add("2-54-171-3122");
        ONLY_HOUSE.add("44-40-3-3241");
        ONLY_HOUSE.add("44-40-3-3243");
        ONLY_HOUSE.add("44-40-3-3233");
        ONLY_HOUSE.add("44-40-3-3232");
        ONLY_HOUSE.add("44-40-3-3231");
        ONLY_HOUSE.add("2-54-171-3141");
        ONLY_HOUSE.add("44-40-3-3223");
        ONLY_HOUSE.add("44-40-3-3222");
        ONLY_HOUSE.add("44-40-3-3221");
        ONLY_HOUSE.add("44-46-100-24");
        ONLY_HOUSE.add("44-40-3-3203");
        ONLY_HOUSE.add("44-46-100-22");
        ONLY_HOUSE.add("44-46-140-4121");
        ONLY_HOUSE.add("44-46-140-4123");
        ONLY_HOUSE.add("44-40-3-3211");
        ONLY_HOUSE.add("44-40-3-3212");
        ONLY_HOUSE.add("44-40-3-3213");
        ONLY_HOUSE.add("44-46-100-19");
        ONLY_HOUSE.add("2-54-171-3163");
        ONLY_HOUSE.add("44-46-100-15");
        ONLY_HOUSE.add("2-54-71-1031");
        ONLY_HOUSE.add("44-46-100-12");
        ONLY_HOUSE.add("2-54-71-1032");
        ONLY_HOUSE.add("44-46-140-4111");
        ONLY_HOUSE.add("44-40-3-3201");
        ONLY_HOUSE.add("44-40-3-3202");
        ONLY_HOUSE.add("44-46-140-4113");
        ONLY_HOUSE.add("44-54-6");
        ONLY_HOUSE.add("44-46-60-4");
        ONLY_HOUSE.add("44-54-5");
        ONLY_HOUSE.add("44-54-7");
        ONLY_HOUSE.add("44-54-4");
        ONLY_HOUSE.add("2-54-71-1062");
        ONLY_HOUSE.add("44-46-140-4101");
        ONLY_HOUSE.add("44-46-140-4103");
        ONLY_HOUSE.add("2-54-187-1044");
        ONLY_HOUSE.add("2-54-187-2111");
        ONLY_HOUSE.add("44-40-25-3151");
        ONLY_HOUSE.add("44-40-25-3152");
        ONLY_HOUSE.add("2-54-124-1021");
        ONLY_HOUSE.add("44-40-25-3161");
        ONLY_HOUSE.add("2-54-187-2113");
        ONLY_HOUSE.add("2-54-149-1051");
        ONLY_HOUSE.add("2-54-187-2123");
        ONLY_HOUSE.add("2-60-9-518");
        ONLY_HOUSE.add("2-60-9-519");
        ONLY_HOUSE.add("44-46-144-3113");
        ONLY_HOUSE.add("44-46-144-3112");
        ONLY_HOUSE.add("44-46-144-3111");
        ONLY_HOUSE.add("2-54-169-3162");
        ONLY_HOUSE.add("44-46-144-3102");
        ONLY_HOUSE.add("2-54-169-3151");
        ONLY_HOUSE.add("2-54-187-2071");
        ONLY_HOUSE.add("44-46-144-3121");
        ONLY_HOUSE.add("44-46-144-3132");
        ONLY_HOUSE.add("44-46-140-4091");
        ONLY_HOUSE.add("2-54-158-2022");
        ONLY_HOUSE.add("44-46-140-4093");
        ONLY_HOUSE.add("44-46-144-3142");
        ONLY_HOUSE.add("44-40-8-2112");
        ONLY_HOUSE.add("44-40-8-2111");
        ONLY_HOUSE.add("44-46-144-3152");
        ONLY_HOUSE.add("44-46-144-3162");
        ONLY_HOUSE.add("44-46-144-3173");
        ONLY_HOUSE.add("44-46-144-3171");
        ONLY_HOUSE.add("72-56-5-1093");
        ONLY_HOUSE.add("44-46-144-3182");
        ONLY_HOUSE.add("2-54-187-2051");
        ONLY_HOUSE.add("44-40-25-3171");
        ONLY_HOUSE.add("44-40-25-3172");
        ONLY_HOUSE.add("44-46-144-3192");
        ONLY_HOUSE.add("44-46-144-3191");
        ONLY_HOUSE.add("44-46-144-3193");
        ONLY_HOUSE.add("44-40-25-3162");
        ONLY_HOUSE.add("2-54-187-2041");
        ONLY_HOUSE.add("44-40-3-1063");
        ONLY_HOUSE.add("44-46-144-2073");
        ONLY_HOUSE.add("44-40-3-1073");
        ONLY_HOUSE.add("44-40-3-1072");
        ONLY_HOUSE.add("2-55-103-3041");
        ONLY_HOUSE.add("2-55-103-3042");
        ONLY_HOUSE.add("44-46-140-4043");
        ONLY_HOUSE.add("2-54-127-2052");
        ONLY_HOUSE.add("44-40-3-3181");
        ONLY_HOUSE.add("44-40-3-3183");
        ONLY_HOUSE.add("44-40-3-3182");
        ONLY_HOUSE.add("44-46-53-18");
        ONLY_HOUSE.add("44-46-53-14");
        ONLY_HOUSE.add("44-46-53-16");
        ONLY_HOUSE.add("2-54-127-2022");
        ONLY_HOUSE.add("44-46-53-10");
        ONLY_HOUSE.add("44-46-53-12");
        ONLY_HOUSE.add("44-46-53-20");
        ONLY_HOUSE.add("44-40-3-3193");
        ONLY_HOUSE.add("44-40-3-3192");
        ONLY_HOUSE.add("44-40-3-3191");
        ONLY_HOUSE.add("44-40-3-3163");
        ONLY_HOUSE.add("44-46-140-4071");
        ONLY_HOUSE.add("44-40-3-3161");
        ONLY_HOUSE.add("44-46-140-4073");
        ONLY_HOUSE.add("2-54-161-2032");
        ONLY_HOUSE.add("2-54-54-2053");
        ONLY_HOUSE.add("44-40-3-3173");
        ONLY_HOUSE.add("44-40-3-3171");
        ONLY_HOUSE.add("44-46-140-4083");
        ONLY_HOUSE.add("44-46-140-4081");
        ONLY_HOUSE.add("44-40-3-3141");
        ONLY_HOUSE.add("44-46-140-4053");
        ONLY_HOUSE.add("44-40-3-3143");
        ONLY_HOUSE.add("44-46-140-4051");
        ONLY_HOUSE.add("44-40-3-3153");
        ONLY_HOUSE.add("2-55-103-3062");
        ONLY_HOUSE.add("44-40-3-3151");
        ONLY_HOUSE.add("44-46-144-2061");
        ONLY_HOUSE.add("44-40-3-3152");
        ONLY_HOUSE.add("44-46-140-4061");
        ONLY_HOUSE.add("44-46-140-4223");
        ONLY_HOUSE.add("44-46-121-18");
        ONLY_HOUSE.add("44-46-140-4221");
        ONLY_HOUSE.add("44-46-121-16");
        ONLY_HOUSE.add("44-46-121-26");
        ONLY_HOUSE.add("44-46-121-25");
        ONLY_HOUSE.add("44-46-121-24");
        ONLY_HOUSE.add("44-46-121-23");
        ONLY_HOUSE.add("44-46-121-22");
        ONLY_HOUSE.add("44-46-121-20");
        ONLY_HOUSE.add("2-54-117-1052");
        ONLY_HOUSE.add("44-46-140-4213");
        ONLY_HOUSE.add("44-46-140-4211");
        ONLY_HOUSE.add("44-46-121-27");
        ONLY_HOUSE.add("2-54-54-1061");
        ONLY_HOUSE.add("44-46-140-4241");
        ONLY_HOUSE.add("44-46-140-4243");
        ONLY_HOUSE.add("2-54-152-3051");
        ONLY_HOUSE.add("44-46-140-4231");
        ONLY_HOUSE.add("44-46-140-4233");
        ONLY_HOUSE.add("44-40-8-1113");
        ONLY_HOUSE.add("2-54-54-1032");
        ONLY_HOUSE.add("2-54-117-1092");
        ONLY_HOUSE.add("44-46-140-4201");
        ONLY_HOUSE.add("44-46-140-4203");
        ONLY_HOUSE.add("2-54-164-1011");
        ONLY_HOUSE.add("44-46-121-11");
        ONLY_HOUSE.add("44-46-121-13");
        ONLY_HOUSE.add("44-46-144-3232");
        ONLY_HOUSE.add("44-46-144-3231");
        ONLY_HOUSE.add("44-68-4");
        ONLY_HOUSE.add("44-68-3");
        ONLY_HOUSE.add("44-46-144-3222");
        ONLY_HOUSE.add("2-55-126-4051");
        ONLY_HOUSE.add("2-54-42-4011");
        ONLY_HOUSE.add("2-55-87-3021");
        ONLY_HOUSE.add("44-46-144-3212");
        ONLY_HOUSE.add("44-46-144-3211");
        ONLY_HOUSE.add("2-55-100-4012");
        ONLY_HOUSE.add("2-55-87-3011");
        ONLY_HOUSE.add("44-46-144-3202");
        ONLY_HOUSE.add("44-46-144-3203");
        ONLY_HOUSE.add("44-46-111-21");
        ONLY_HOUSE.add("44-46-111-17");
        ONLY_HOUSE.add("44-46-111-18");
        ONLY_HOUSE.add("2-55-100-4032");
        ONLY_HOUSE.add("44-48-14");
        ONLY_HOUSE.add("44-48-12");
        ONLY_HOUSE.add("44-46-107-20");
        ONLY_HOUSE.add("44-40-13-3012");
        ONLY_HOUSE.add("2-55-100-4042");
        ONLY_HOUSE.add("2-55-77-4022");
        ONLY_HOUSE.add("44-46-107-11");
        ONLY_HOUSE.add("44-46-107-10");
        ONLY_HOUSE.add("44-46-107-13");
        ONLY_HOUSE.add("44-46-107-15");
        ONLY_HOUSE.add("44-46-107-14");
        ONLY_HOUSE.add("44-46-64-11");
        ONLY_HOUSE.add("44-46-64-10");
        ONLY_HOUSE.add("2-55-77-4033");
        ONLY_HOUSE.add("2-55-77-4031");
        ONLY_HOUSE.add("2-54-57-8052");
        ONLY_HOUSE.add("2-54-152-4032");
        ONLY_HOUSE.add("2-54-187-2162");
        ONLY_HOUSE.add("2-55-98-1063");
        ONLY_HOUSE.add("44-46-52-2193");
        ONLY_HOUSE.add("2-55-98-1051");
        ONLY_HOUSE.add("44-46-111-11");
        ONLY_HOUSE.add("2-54-152-4052");
        ONLY_HOUSE.add("2-54-139-2061");
        ONLY_HOUSE.add("2-54-187-2143");
        ONLY_HOUSE.add("44-46-144-3291");
        ONLY_HOUSE.add("2-55-98-1042");
        ONLY_HOUSE.add("44-46-144-3292");
        ONLY_HOUSE.add("44-46-144-3262");
        ONLY_HOUSE.add("2-54-139-2051");
        ONLY_HOUSE.add("44-46-144-3242");
        ONLY_HOUSE.add("2-55-82-2022");
        ONLY_HOUSE.add("44-46-144-3252");
        ONLY_HOUSE.add("2-54-139-2021");
        ONLY_HOUSE.add("2-54-139-2022");
        ONLY_HOUSE.add("2-55-82-2011");
        ONLY_HOUSE.add("2-55-100-1042");
        ONLY_HOUSE.add("44-46-140-4171");
        ONLY_HOUSE.add("44-46-140-4173");
        ONLY_HOUSE.add("44-46-140-4183");
        ONLY_HOUSE.add("44-46-140-4181");
        ONLY_HOUSE.add("2-54-165-1012");
        ONLY_HOUSE.add("44-46-140-4193");
        ONLY_HOUSE.add("44-46-140-4191");
        ONLY_HOUSE.add("2-55-117-2053");
        ONLY_HOUSE.add("44-46-52-2104");
        ONLY_HOUSE.add("2-55-132-3062");
        ONLY_HOUSE.add("2-55-132-3031");
        ONLY_HOUSE.add("44-46-140-4131");
        ONLY_HOUSE.add("44-46-140-4133");
        ONLY_HOUSE.add("44-40-8-1103");
        ONLY_HOUSE.add("44-52-5");
        ONLY_HOUSE.add("44-52-6");
        ONLY_HOUSE.add("44-52-3");
        ONLY_HOUSE.add("44-52-4");
        ONLY_HOUSE.add("44-52-9");
        ONLY_HOUSE.add("44-52-7");
        ONLY_HOUSE.add("44-52-8");
        ONLY_HOUSE.add("44-46-140-4143");
        ONLY_HOUSE.add("44-46-140-4141");
        ONLY_HOUSE.add("44-52-2");
        ONLY_HOUSE.add("44-52-1");
        ONLY_HOUSE.add("2-54-129-5052");
        ONLY_HOUSE.add("2-55-100-1021");
        ONLY_HOUSE.add("44-46-140-4151");
        ONLY_HOUSE.add("44-40-14-3091");
        ONLY_HOUSE.add("44-46-140-4161");
        ONLY_HOUSE.add("44-40-5-5101");
        ONLY_HOUSE.add("44-40-5-5102");
        ONLY_HOUSE.add("44-40-7-1142");
        ONLY_HOUSE.add("44-40-5-5111");
        ONLY_HOUSE.add("44-40-5-5112");
        ONLY_HOUSE.add("44-40-5-5122");
        ONLY_HOUSE.add("44-40-7-1122");
        ONLY_HOUSE.add("44-40-5-5121");
        ONLY_HOUSE.add("2-54-170-1131");
        ONLY_HOUSE.add("2-54-170-1133");
        ONLY_HOUSE.add("44-40-5-5131");
        ONLY_HOUSE.add("44-40-5-5132");
        ONLY_HOUSE.add("44-46-129-18");
        ONLY_HOUSE.add("44-46-129-16");
        ONLY_HOUSE.add("44-46-129-11");
        ONLY_HOUSE.add("44-46-129-14");
        ONLY_HOUSE.add("44-46-129-12");
        ONLY_HOUSE.add("44-46-129-13");
        ONLY_HOUSE.add("44-46-52-4174");
        ONLY_HOUSE.add("2-55-81-6022");
        ONLY_HOUSE.add("44-46-140-4302");
        ONLY_HOUSE.add("44-46-52-3075");
        ONLY_HOUSE.add("2-56-381");
        ONLY_HOUSE.add("2-56-383");
        ONLY_HOUSE.add("44-21-30-1");
        ONLY_HOUSE.add("2-56-372");
        ONLY_HOUSE.add("44-46-136-27");
        ONLY_HOUSE.add("44-46-136-23");
        ONLY_HOUSE.add("2-54-164-2042");
        ONLY_HOUSE.add("2-56-385-1");
        ONLY_HOUSE.add("44-46-136-12");
        ONLY_HOUSE.add("2-56-385-5");
        ONLY_HOUSE.add("44-46-136-13");
        ONLY_HOUSE.add("2-56-385-4");
        ONLY_HOUSE.add("2-56-385-3");
        ONLY_HOUSE.add("2-56-385-2");
        ONLY_HOUSE.add("2-56-385-9");
        ONLY_HOUSE.add("2-56-385-8");
        ONLY_HOUSE.add("2-56-385-7");
        ONLY_HOUSE.add("44-46-136-11");
        ONLY_HOUSE.add("2-55-107-1042");
        ONLY_HOUSE.add("44-40-19-2102");
        ONLY_HOUSE.add("44-51-25-18");
        ONLY_HOUSE.add("44-40-19-2132");
        ONLY_HOUSE.add("44-40-19-2122");
        ONLY_HOUSE.add("44-40-19-2151");
        ONLY_HOUSE.add("44-40-19-2152");
        ONLY_HOUSE.add("44-40-3-1222");
        ONLY_HOUSE.add("44-40-3-1221");
        ONLY_HOUSE.add("44-40-3-1212");
        ONLY_HOUSE.add("2-54-78-2051");
        ONLY_HOUSE.add("44-40-3-1213");
        ONLY_HOUSE.add("44-40-3-1214");
        ONLY_HOUSE.add("44-46-52-2263");
        ONLY_HOUSE.add("44-40-3-1233");
        ONLY_HOUSE.add("44-40-3-1232");
        ONLY_HOUSE.add("44-40-3-1231");
        ONLY_HOUSE.add("44-40-3-1223");
        ONLY_HOUSE.add("44-40-3-1224");
        ONLY_HOUSE.add("2-54-78-2061");
        ONLY_HOUSE.add("44-40-19-2171");
        ONLY_HOUSE.add("44-40-19-2172");
        ONLY_HOUSE.add("44-40-3-1203");
        ONLY_HOUSE.add("44-40-3-1204");
        ONLY_HOUSE.add("44-40-3-1202");
        ONLY_HOUSE.add("44-46-141-4054");
        ONLY_HOUSE.add("44-46-141-4051");
        ONLY_HOUSE.add("44-46-141-4053");
        ONLY_HOUSE.add("44-46-141-4052");
        ONLY_HOUSE.add("44-46-141-4044");
        ONLY_HOUSE.add("44-46-141-4043");
        ONLY_HOUSE.add("44-46-141-4042");
        ONLY_HOUSE.add("44-46-141-4041");
        ONLY_HOUSE.add("44-46-52-4244");
        ONLY_HOUSE.add("2-54-78-1023");
        ONLY_HOUSE.add("7-75-3");
        ONLY_HOUSE.add("44-46-141-4071");
        ONLY_HOUSE.add("7-75-2");
        ONLY_HOUSE.add("44-46-141-4073");
        ONLY_HOUSE.add("44-46-141-4072");
        ONLY_HOUSE.add("7-75-5");
        ONLY_HOUSE.add("44-40-3-1234");
        ONLY_HOUSE.add("44-46-141-4074");
        ONLY_HOUSE.add("44-40-3-1243");
        ONLY_HOUSE.add("44-40-3-1242");
        ONLY_HOUSE.add("2-54-139-3022");
        ONLY_HOUSE.add("44-46-140-4291");
        ONLY_HOUSE.add("44-46-140-4294");
        ONLY_HOUSE.add("44-46-140-4293");
        ONLY_HOUSE.add("44-46-141-4064");
        ONLY_HOUSE.add("44-46-141-4063");
        ONLY_HOUSE.add("44-46-141-4062");
        ONLY_HOUSE.add("44-46-141-4061");
        ONLY_HOUSE.add("44-40-3-1252");
        ONLY_HOUSE.add("44-40-3-1253");
        ONLY_HOUSE.add("44-40-3-1254");
        ONLY_HOUSE.add("44-40-3-1251");
        ONLY_HOUSE.add("44-46-141-4094");
        ONLY_HOUSE.add("44-40-5-5152");
        ONLY_HOUSE.add("44-40-5-5151");
        ONLY_HOUSE.add("44-46-141-4091");
        ONLY_HOUSE.add("44-46-141-4092");
        ONLY_HOUSE.add("44-46-141-4093");
        ONLY_HOUSE.add("72-56-11-1061");
        ONLY_HOUSE.add("44-46-140-4271");
        ONLY_HOUSE.add("44-46-140-4273");
        ONLY_HOUSE.add("44-40-5-5142");
        ONLY_HOUSE.add("44-40-5-5141");
        ONLY_HOUSE.add("44-46-141-4083");
        ONLY_HOUSE.add("44-46-141-4084");
        ONLY_HOUSE.add("44-46-141-4081");
        ONLY_HOUSE.add("44-46-141-4082");
        ONLY_HOUSE.add("2-54-78-1063");
        ONLY_HOUSE.add("44-40-7-1171");
        ONLY_HOUSE.add("44-46-140-4251");
        ONLY_HOUSE.add("44-46-140-4253");
        ONLY_HOUSE.add("44-40-5-5161");
        ONLY_HOUSE.add("44-40-5-5162");
        ONLY_HOUSE.add("2-54-139-3052");
        ONLY_HOUSE.add("44-46-140-4261");
        ONLY_HOUSE.add("44-46-140-4263");
        ONLY_HOUSE.add("44-40-3-2172");
        ONLY_HOUSE.add("44-46-140-1222");
        ONLY_HOUSE.add("44-40-3-2171");
        ONLY_HOUSE.add("44-46-140-1212");
        ONLY_HOUSE.add("44-46-140-1214");
        ONLY_HOUSE.add("44-46-140-1232");
        ONLY_HOUSE.add("44-40-3-2182");
        ONLY_HOUSE.add("44-40-3-2181");
        ONLY_HOUSE.add("44-46-140-1224");
        ONLY_HOUSE.add("2-54-67-1041");
        ONLY_HOUSE.add("44-40-3-2191");
        ONLY_HOUSE.add("44-40-3-2193");
        ONLY_HOUSE.add("44-40-3-2192");
        ONLY_HOUSE.add("44-46-140-1242");
        ONLY_HOUSE.add("44-46-140-1244");
        ONLY_HOUSE.add("44-46-140-1234");
        ONLY_HOUSE.add("2-54-171-1061");
        ONLY_HOUSE.add("44-46-140-1254");
        ONLY_HOUSE.add("44-46-140-1252");
        ONLY_HOUSE.add("2-54-157-1061");
        ONLY_HOUSE.add("2-54-103-1042");
        ONLY_HOUSE.add("2-54-157-1052");
        ONLY_HOUSE.add("2-54-103-1013");
        ONLY_HOUSE.add("44-46-140-1204");
        ONLY_HOUSE.add("44-46-140-1202");
        ONLY_HOUSE.add("2-54-24-5073");
        ONLY_HOUSE.add("44-46-140-1264");
        ONLY_HOUSE.add("44-46-140-1262");
        ONLY_HOUSE.add("44-46-140-1274");
        ONLY_HOUSE.add("44-46-140-1272");
        ONLY_HOUSE.add("44-46-140-1293");
        ONLY_HOUSE.add("44-46-140-1292");
        ONLY_HOUSE.add("44-46-140-1294");
        ONLY_HOUSE.add("2-54-82-1062");
        ONLY_HOUSE.add("44-46-140-1291");
        ONLY_HOUSE.add("44-40-19-3132");
        ONLY_HOUSE.add("44-40-19-3131");
        ONLY_HOUSE.add("44-21-20-39");
        ONLY_HOUSE.add("44-46-3-3042");
        ONLY_HOUSE.add("44-40-19-3142");
        ONLY_HOUSE.add("44-40-19-3141");
        ONLY_HOUSE.add("44-40-19-2092");
        ONLY_HOUSE.add("2-54-82-4012");
        ONLY_HOUSE.add("44-40-19-2062");
        ONLY_HOUSE.add("44-40-19-3111");
        ONLY_HOUSE.add("44-40-3-2201");
        ONLY_HOUSE.add("2-54-129-1061");
        ONLY_HOUSE.add("44-40-19-3101");
        ONLY_HOUSE.add("44-40-19-3102");
        ONLY_HOUSE.add("2-54-82-4022");
        ONLY_HOUSE.add("44-40-19-2072");
        ONLY_HOUSE.add("44-40-19-3122");
        ONLY_HOUSE.add("44-40-19-3121");
        ONLY_HOUSE.add("44-40-19-3112");
        ONLY_HOUSE.add("44-40-3-2221");
        ONLY_HOUSE.add("44-40-3-2223");
        ONLY_HOUSE.add("44-40-19-2042");
        ONLY_HOUSE.add("44-40-19-2041");
        ONLY_HOUSE.add("44-40-3-2222");
        ONLY_HOUSE.add("44-40-3-2202");
        ONLY_HOUSE.add("44-40-3-2203");
        ONLY_HOUSE.add("44-40-19-2052");
        ONLY_HOUSE.add("44-40-3-2211");
        ONLY_HOUSE.add("44-40-19-2051");
        ONLY_HOUSE.add("44-40-3-2241");
        ONLY_HOUSE.add("44-40-3-2243");
        ONLY_HOUSE.add("44-40-19-3152");
        ONLY_HOUSE.add("44-40-19-2021");
        ONLY_HOUSE.add("44-40-19-3161");
        ONLY_HOUSE.add("44-40-19-3162");
        ONLY_HOUSE.add("44-40-19-2031");
        ONLY_HOUSE.add("44-40-3-2233");
        ONLY_HOUSE.add("44-40-3-2232");
        ONLY_HOUSE.add("44-40-3-2231");
        ONLY_HOUSE.add("44-46-140-3292");
        ONLY_HOUSE.add("44-46-140-3291");
        ONLY_HOUSE.add("44-40-3-2253");
        ONLY_HOUSE.add("44-40-19-2011");
        ONLY_HOUSE.add("44-40-3-2251");
        ONLY_HOUSE.add("44-40-3-2252");
        ONLY_HOUSE.add("44-40-3-2072");
        ONLY_HOUSE.add("44-40-3-2071");
        ONLY_HOUSE.add("44-46-140-1122");
        ONLY_HOUSE.add("44-46-122-19");
        ONLY_HOUSE.add("44-40-3-2073");
        ONLY_HOUSE.add("44-46-122-20");
        ONLY_HOUSE.add("44-46-122-23");
        ONLY_HOUSE.add("44-46-122-24");
        ONLY_HOUSE.add("44-46-122-25");
        ONLY_HOUSE.add("44-46-140-1114");
        ONLY_HOUSE.add("44-40-3-2083");
        ONLY_HOUSE.add("44-40-3-2082");
        ONLY_HOUSE.add("44-40-3-2081");
        ONLY_HOUSE.add("44-46-140-1134");
        ONLY_HOUSE.add("44-46-140-1132");
        ONLY_HOUSE.add("44-46-122-11");
        ONLY_HOUSE.add("44-46-122-12");
        ONLY_HOUSE.add("44-46-122-15");
        ONLY_HOUSE.add("44-46-122-16");
        ONLY_HOUSE.add("44-46-122-13");
        ONLY_HOUSE.add("44-46-122-14");
        ONLY_HOUSE.add("44-46-140-1124");
        ONLY_HOUSE.add("2-54-171-2091");
        ONLY_HOUSE.add("44-46-140-1112");
        ONLY_HOUSE.add("44-40-3-2061");
        ONLY_HOUSE.add("44-40-3-2063");
        ONLY_HOUSE.add("44-46-63-18");
        ONLY_HOUSE.add("44-46-140-1104");
        ONLY_HOUSE.add("44-46-140-1102");
        ONLY_HOUSE.add("44-46-140-2291");
        ONLY_HOUSE.add("44-46-140-2292");
        ONLY_HOUSE.add("2-55-67-3061");
        ONLY_HOUSE.add("44-40-3-2091");
        ONLY_HOUSE.add("44-40-3-2093");
        ONLY_HOUSE.add("2-54-171-2053");
        ONLY_HOUSE.add("2-54-110-2032");
        ONLY_HOUSE.add("44-69-3");
        ONLY_HOUSE.add("44-46-64-6");
        ONLY_HOUSE.add("44-46-64-7");
        ONLY_HOUSE.add("2-54-157-2012");
        ONLY_HOUSE.add("44-40-5-1");
        ONLY_HOUSE.add("44-46-106-14");
        ONLY_HOUSE.add("44-46-106-13");
        ONLY_HOUSE.add("44-40-5-3");
        ONLY_HOUSE.add("44-40-5-2");
        ONLY_HOUSE.add("44-46-140-1184");
        ONLY_HOUSE.add("44-46-140-1182");
        ONLY_HOUSE.add("2-54-128-2032");
        ONLY_HOUSE.add("44-40-5-9");
        ONLY_HOUSE.add("44-40-5-8");
        ONLY_HOUSE.add("44-46-106-19");
        ONLY_HOUSE.add("44-40-5-5");
        ONLY_HOUSE.add("44-46-106-18");
        ONLY_HOUSE.add("44-40-5-4");
        ONLY_HOUSE.add("44-40-5-7");
        ONLY_HOUSE.add("44-46-106-15");
        ONLY_HOUSE.add("44-40-5-6");
        ONLY_HOUSE.add("44-40-8-1061");
        ONLY_HOUSE.add("44-46-140-1194");
        ONLY_HOUSE.add("2-54-128-2041");
        ONLY_HOUSE.add("44-46-140-1192");
        ONLY_HOUSE.add("2-54-171-1113");
        ONLY_HOUSE.add("44-46-140-1164");
        ONLY_HOUSE.add("44-46-140-1172");
        ONLY_HOUSE.add("44-46-140-1174");
        ONLY_HOUSE.add("2-55-109-2023");
        ONLY_HOUSE.add("2-55-87-2021");
        ONLY_HOUSE.add("44-46-140-1144");
        ONLY_HOUSE.add("44-46-140-1142");
        ONLY_HOUSE.add("2-55-75-3022");
        ONLY_HOUSE.add("2-54-171-1152");
        ONLY_HOUSE.add("2-54-171-1151");
        ONLY_HOUSE.add("44-46-140-1154");
        ONLY_HOUSE.add("44-46-140-1152");
        ONLY_HOUSE.add("2-54-155-1062");
        ONLY_HOUSE.add("2-55-103-1023");
        ONLY_HOUSE.add("2-55-86-1051");
        ONLY_HOUSE.add("2-55-86-1042");
        ONLY_HOUSE.add("2-55-86-1072");
        ONLY_HOUSE.add("2-54-169-2072");
        ONLY_HOUSE.add("44-46-22-64");
        ONLY_HOUSE.add("44-46-4-1202");
        ONLY_HOUSE.add("2-55-136-2091");
        ONLY_HOUSE.add("2-54-85-2062");
        ONLY_HOUSE.add("44-56-1-1377");
        ONLY_HOUSE.add("2-55-136-2071");
        ONLY_HOUSE.add("44-40-3-2122");
        ONLY_HOUSE.add("44-40-19-3031");
        ONLY_HOUSE.add("44-40-3-2121");
        ONLY_HOUSE.add("44-40-19-3032");
        ONLY_HOUSE.add("44-40-3-2103");
        ONLY_HOUSE.add("44-40-19-3041");
        ONLY_HOUSE.add("44-40-3-2112");
        ONLY_HOUSE.add("44-40-19-3042");
        ONLY_HOUSE.add("44-40-3-2111");
        ONLY_HOUSE.add("44-40-3-2101");
        ONLY_HOUSE.add("44-40-19-3051");
        ONLY_HOUSE.add("44-40-19-3052");
        ONLY_HOUSE.add("2-55-136-2042");
        ONLY_HOUSE.add("44-40-19-3062");
        ONLY_HOUSE.add("44-40-19-3061");
        ONLY_HOUSE.add("44-40-3-2161");
        ONLY_HOUSE.add("44-40-19-3072");
        ONLY_HOUSE.add("44-40-19-3071");
        ONLY_HOUSE.add("44-40-3-2162");
        ONLY_HOUSE.add("44-46-22-45");
        ONLY_HOUSE.add("44-40-3-2152");
        ONLY_HOUSE.add("44-40-19-3082");
        ONLY_HOUSE.add("44-40-3-2151");
        ONLY_HOUSE.add("44-40-3-2142");
        ONLY_HOUSE.add("44-40-19-3092");
        ONLY_HOUSE.add("44-40-19-3091");
        ONLY_HOUSE.add("44-46-60-13");
        ONLY_HOUSE.add("2-54-85-3021");
        ONLY_HOUSE.add("2-54-141-2011");
        ONLY_HOUSE.add("2-54-171-2141");
        ONLY_HOUSE.add("44-56-10-2112");
        ONLY_HOUSE.add("44-56-10-2115");
        ONLY_HOUSE.add("44-56-10-2102");
        ONLY_HOUSE.add("744-55-3");
        ONLY_HOUSE.add("744-55-4");
        ONLY_HOUSE.add("744-55-5");
        ONLY_HOUSE.add("744-55-6");
        ONLY_HOUSE.add("744-55-2");
        ONLY_HOUSE.add("2-54-55-1032");
        ONLY_HOUSE.add("2-54-109-4012");
        ONLY_HOUSE.add("2-54-187-1142");
        ONLY_HOUSE.add("44-46-140-1044");
        ONLY_HOUSE.add("44-46-140-1052");
        ONLY_HOUSE.add("44-46-140-1054");
        ONLY_HOUSE.add("2-54-187-1133");
        ONLY_HOUSE.add("44-46-140-1064");
        ONLY_HOUSE.add("44-46-140-1062");
        ONLY_HOUSE.add("44-46-140-1072");
        ONLY_HOUSE.add("2-55-63-4021");
        ONLY_HOUSE.add("44-46-140-1074");
        ONLY_HOUSE.add("2-54-187-1112");
        ONLY_HOUSE.add("2-54-187-1111");
        ONLY_HOUSE.add("2-54-54-5071");
        ONLY_HOUSE.add("2-54-118-1061");
        ONLY_HOUSE.add("44-46-130-10");
        ONLY_HOUSE.add("44-46-140-1082");
        ONLY_HOUSE.add("44-46-140-1084");
        ONLY_HOUSE.add("2-54-187-1104");
        ONLY_HOUSE.add("2-54-187-1102");
        ONLY_HOUSE.add("2-54-118-1092");
        ONLY_HOUSE.add("44-46-140-1092");
        ONLY_HOUSE.add("44-46-140-1094");
        ONLY_HOUSE.add("2-54-171-2103");
        ONLY_HOUSE.add("2-54-118-1083");
        ONLY_HOUSE.add("2-54-73-1051");
        ONLY_HOUSE.add("44-51-19-11");
        ONLY_HOUSE.add("44-51-19-16");
        ONLY_HOUSE.add("44-40-17-5112");
        ONLY_HOUSE.add("44-40-17-5111");
        ONLY_HOUSE.add("44-46-52-1072");
        ONLY_HOUSE.add("2-54-187-1072");
        ONLY_HOUSE.add("44-40-9-1051");
        ONLY_HOUSE.add("2-54-187-1084");
        ONLY_HOUSE.add("2-54-26-3031");
        ONLY_HOUSE.add("44-56-10-2157");
        ONLY_HOUSE.add("2-54-105-1063");
        ONLY_HOUSE.add("44-56-10-2192");
        ONLY_HOUSE.add("44-56-10-2184");
        ONLY_HOUSE.add("44-46-19-66");
        ONLY_HOUSE.add("44-56-10-2172");
        ONLY_HOUSE.add("2-54-170-2121");
        ONLY_HOUSE.add("2-54-80-1031");
        ONLY_HOUSE.add("2-54-80-1012");
        ONLY_HOUSE.add("2-54-170-2141");
        ONLY_HOUSE.add("2-54-141-1011");
        ONLY_HOUSE.add("2-54-80-1051");
        ONLY_HOUSE.add("44-56-1-1112");
        ONLY_HOUSE.add("44-56-1-1113");
        ONLY_HOUSE.add("2-54-170-2131");
        ONLY_HOUSE.add("2-54-170-2151");
        ONLY_HOUSE.add("2-54-132-2041");
        ONLY_HOUSE.add("44-46-139-2085");
        ONLY_HOUSE.add("44-46-139-2083");
        ONLY_HOUSE.add("44-51-8-4");
        ONLY_HOUSE.add("44-46-139-2093");
        ONLY_HOUSE.add("44-46-139-2045");
        ONLY_HOUSE.add("2-54-82-4062");
        ONLY_HOUSE.add("44-46-139-2043");
        ONLY_HOUSE.add("2-55-82-3033");
        ONLY_HOUSE.add("2-55-82-3032");
        ONLY_HOUSE.add("44-46-139-2053");
        ONLY_HOUSE.add("2-54-153-2051");
        ONLY_HOUSE.add("2-55-82-3021");
        ONLY_HOUSE.add("2-55-82-3023");
        ONLY_HOUSE.add("44-46-139-2065");
        ONLY_HOUSE.add("2-54-141-1051");
        ONLY_HOUSE.add("44-46-139-2063");
        ONLY_HOUSE.add("44-46-139-2075");
        ONLY_HOUSE.add("44-40-6-1152");
        ONLY_HOUSE.add("44-56-8");
        ONLY_HOUSE.add("44-56-7");
        ONLY_HOUSE.add("44-56-9");
        ONLY_HOUSE.add("44-56-4");
        ONLY_HOUSE.add("44-56-3");
        ONLY_HOUSE.add("44-56-6");
        ONLY_HOUSE.add("44-46-139-2073");
        ONLY_HOUSE.add("44-56-5");
        ONLY_HOUSE.add("2-55-74-3051");
        ONLY_HOUSE.add("2-54-140-3032");
        ONLY_HOUSE.add("2-54-188-2052");
        ONLY_HOUSE.add("2-55-106-1042");
        ONLY_HOUSE.add("2-55-120-1011");
        ONLY_HOUSE.add("2-54-109-4023");
        ONLY_HOUSE.add("2-54-140-3062");
        ONLY_HOUSE.add("2-54-151-2011");
        ONLY_HOUSE.add("2-54-85-4042");
        ONLY_HOUSE.add("44-46-52-1193");
        ONLY_HOUSE.add("2-54-118-1113");
        ONLY_HOUSE.add("44-46-133-13");
        ONLY_HOUSE.add("44-46-133-10");
        ONLY_HOUSE.add("44-46-133-11");
        ONLY_HOUSE.add("2-55-63-1063");
        ONLY_HOUSE.add("44-46-133-28");
        ONLY_HOUSE.add("44-46-133-32");
        ONLY_HOUSE.add("44-46-133-31");
        ONLY_HOUSE.add("2-55-63-1031");
        ONLY_HOUSE.add("44-46-133-30");
        ONLY_HOUSE.add("2-55-135-1061");
        ONLY_HOUSE.add("44-46-133-22");
        ONLY_HOUSE.add("44-46-133-21");
        ONLY_HOUSE.add("44-46-133-20");
        ONLY_HOUSE.add("44-46-133-27");
        ONLY_HOUSE.add("44-46-133-26");
        ONLY_HOUSE.add("44-56-1-1128");
        ONLY_HOUSE.add("44-56-1-1129");
        ONLY_HOUSE.add("44-56-10-2063");
        ONLY_HOUSE.add("2-55-75-1062");
        ONLY_HOUSE.add("44-56-10-2061");
        ONLY_HOUSE.add("44-56-10-2054");
        ONLY_HOUSE.add("44-56-1-1122");
        ONLY_HOUSE.add("2-54-26-2011");
        ONLY_HOUSE.add("2-55-75-1041");
        ONLY_HOUSE.add("44-46-52-1121");
        ONLY_HOUSE.add("44-46-52-1123");
        ONLY_HOUSE.add("2-54-105-2032");
        ONLY_HOUSE.add("2-54-105-2022");
        ONLY_HOUSE.add("2-54-105-2013");
        ONLY_HOUSE.add("2-54-178-1062");
        ONLY_HOUSE.add("2-54-63-3012");
        ONLY_HOUSE.add("44-46-160-19");
        ONLY_HOUSE.add("2-54-36-1021");
        ONLY_HOUSE.add("44-46-160-15");
        ONLY_HOUSE.add("44-46-160-16");
        ONLY_HOUSE.add("44-46-160-17");
        ONLY_HOUSE.add("44-46-160-18");
        ONLY_HOUSE.add("44-46-160-11");
        ONLY_HOUSE.add("44-46-160-12");
        ONLY_HOUSE.add("44-46-160-13");
        ONLY_HOUSE.add("44-46-160-14");
        ONLY_HOUSE.add("44-46-160-21");
        ONLY_HOUSE.add("44-46-160-20");
        ONLY_HOUSE.add("44-46-99-4");
        ONLY_HOUSE.add("44-46-99-8");
        ONLY_HOUSE.add("44-46-160-10");
        ONLY_HOUSE.add("2-54-178-1021");
        ONLY_HOUSE.add("44-46-160-24");
        ONLY_HOUSE.add("44-46-160-25");
        ONLY_HOUSE.add("44-46-160-22");
        ONLY_HOUSE.add("44-46-160-23");
        ONLY_HOUSE.add("44-46-160-26");
        ONLY_HOUSE.add("2-55-134-4022");
        ONLY_HOUSE.add("44-46-139-2265");
        ONLY_HOUSE.add("44-46-139-2263");
        ONLY_HOUSE.add("44-46-110-11");
        ONLY_HOUSE.add("44-46-139-2273");
        ONLY_HOUSE.add("44-46-139-2271");
        ONLY_HOUSE.add("44-46-139-2243");
        ONLY_HOUSE.add("2-54-147-1051");
        ONLY_HOUSE.add("44-46-139-2252");
        ONLY_HOUSE.add("44-46-139-2253");
        ONLY_HOUSE.add("44-46-139-2255");
        ONLY_HOUSE.add("2-54-147-1062");
        ONLY_HOUSE.add("44-46-139-2224");
        ONLY_HOUSE.add("44-46-139-2225");
        ONLY_HOUSE.add("44-46-139-2223");
        ONLY_HOUSE.add("44-46-139-2233");
        ONLY_HOUSE.add("44-46-139-2235");
        ONLY_HOUSE.add("44-21-24-7");
        ONLY_HOUSE.add("44-46-139-2203");
        ONLY_HOUSE.add("44-46-139-2204");
        ONLY_HOUSE.add("44-46-139-2213");
        ONLY_HOUSE.add("44-46-131-11");
        ONLY_HOUSE.add("44-46-131-12");
        ONLY_HOUSE.add("2-54-104-4051");
        ONLY_HOUSE.add("2-55-84-2022");
        ONLY_HOUSE.add("44-46-131-10");
        ONLY_HOUSE.add("44-40-11-1012");
        ONLY_HOUSE.add("2-54-108-5033");
        ONLY_HOUSE.add("2-54-108-5021");
        ONLY_HOUSE.add("2-54-148-2022");
        ONLY_HOUSE.add("44-46-139-2294");
        ONLY_HOUSE.add("44-46-139-2293");
        ONLY_HOUSE.add("2-54-138-2051");
        ONLY_HOUSE.add("44-46-139-2292");
        ONLY_HOUSE.add("44-46-139-2291");
        ONLY_HOUSE.add("44-46-139-2295");
        ONLY_HOUSE.add("44-46-139-2296");
        ONLY_HOUSE.add("44-40-20-3061");
        ONLY_HOUSE.add("2-54-73-4062");
        ONLY_HOUSE.add("744-47-3");
        ONLY_HOUSE.add("744-47-1");
        ONLY_HOUSE.add("2-54-174-5");
        ONLY_HOUSE.add("744-47-4");
        ONLY_HOUSE.add("2-54-73-4012");
        ONLY_HOUSE.add("44-40-11-2061");
        ONLY_HOUSE.add("2-54-73-4021");
        ONLY_HOUSE.add("44-46-131-17");
        ONLY_HOUSE.add("44-46-131-18");
        ONLY_HOUSE.add("2-54-73-4031");
        ONLY_HOUSE.add("2-54-57-1042");
        ONLY_HOUSE.add("2-60-9-2095");
        ONLY_HOUSE.add("2-60-9-2096");
        ONLY_HOUSE.add("2-60-9-2093");
        ONLY_HOUSE.add("2-60-9-2094");
        ONLY_HOUSE.add("2-60-9-2091");
        ONLY_HOUSE.add("2-60-9-2092");
        ONLY_HOUSE.add("2-60-9-2090");
        ONLY_HOUSE.add("2-60-9-2099");
        ONLY_HOUSE.add("2-60-9-2097");
        ONLY_HOUSE.add("2-60-9-2098");
        ONLY_HOUSE.add("44-46-21-57");
        ONLY_HOUSE.add("2-60-9-2069");
        ONLY_HOUSE.add("2-60-9-2068");
        ONLY_HOUSE.add("2-60-9-2065");
        ONLY_HOUSE.add("2-60-9-2064");
        ONLY_HOUSE.add("2-60-9-2067");
        ONLY_HOUSE.add("2-60-9-2066");
        ONLY_HOUSE.add("2-60-9-2061");
        ONLY_HOUSE.add("2-60-9-2060");
        ONLY_HOUSE.add("2-60-9-2063");
        ONLY_HOUSE.add("2-60-9-2062");
        ONLY_HOUSE.add("2-60-9-2059");
        ONLY_HOUSE.add("2-60-9-2058");
        ONLY_HOUSE.add("2-60-9-2057");
        ONLY_HOUSE.add("2-60-9-2056");
        ONLY_HOUSE.add("2-60-9-2055");
        ONLY_HOUSE.add("2-60-9-2054");
        ONLY_HOUSE.add("2-60-9-2053");
        ONLY_HOUSE.add("2-60-9-2052");
        ONLY_HOUSE.add("2-60-9-2051");
        ONLY_HOUSE.add("2-60-9-2050");
        ONLY_HOUSE.add("44-46-21-38");
        ONLY_HOUSE.add("44-46-21-20");
        ONLY_HOUSE.add("2-60-9-2087");
        ONLY_HOUSE.add("2-60-9-2086");
        ONLY_HOUSE.add("2-60-9-2089");
        ONLY_HOUSE.add("2-60-9-2088");
        ONLY_HOUSE.add("2-60-9-2081");
        ONLY_HOUSE.add("2-60-9-2080");
        ONLY_HOUSE.add("2-60-9-2083");
        ONLY_HOUSE.add("2-60-9-2082");
        ONLY_HOUSE.add("2-60-9-2085");
        ONLY_HOUSE.add("2-60-9-2084");
        ONLY_HOUSE.add("2-60-9-2078");
        ONLY_HOUSE.add("2-60-9-2077");
        ONLY_HOUSE.add("2-60-9-2076");
        ONLY_HOUSE.add("2-60-9-2075");
        ONLY_HOUSE.add("44-46-21-19");
        ONLY_HOUSE.add("2-60-9-2079");
        ONLY_HOUSE.add("2-60-9-2070");
        ONLY_HOUSE.add("2-60-9-2074");
        ONLY_HOUSE.add("2-60-9-2073");
        ONLY_HOUSE.add("2-60-9-2072");
        ONLY_HOUSE.add("2-60-9-2071");
        ONLY_HOUSE.add("44-46-139-2123");
        ONLY_HOUSE.add("2-60-9-2026");
        ONLY_HOUSE.add("2-60-9-2027");
        ONLY_HOUSE.add("2-60-9-2024");
        ONLY_HOUSE.add("2-60-9-2025");
        ONLY_HOUSE.add("2-60-9-2022");
        ONLY_HOUSE.add("2-60-9-2023");
        ONLY_HOUSE.add("2-60-9-2020");
        ONLY_HOUSE.add("2-60-9-2021");
        ONLY_HOUSE.add("2-60-9-2029");
        ONLY_HOUSE.add("2-60-9-2028");
        ONLY_HOUSE.add("44-46-139-2133");
        ONLY_HOUSE.add("44-46-139-2135");
        ONLY_HOUSE.add("2-60-9-2013");
        ONLY_HOUSE.add("2-60-9-2014");
        ONLY_HOUSE.add("2-60-9-2015");
        ONLY_HOUSE.add("2-60-9-2016");
        ONLY_HOUSE.add("2-60-9-2010");
        ONLY_HOUSE.add("2-60-9-2011");
        ONLY_HOUSE.add("2-60-9-2012");
        ONLY_HOUSE.add("2-60-9-2018");
        ONLY_HOUSE.add("2-60-9-2017");
        ONLY_HOUSE.add("2-54-80-2022");
        ONLY_HOUSE.add("2-60-9-2019");
        ONLY_HOUSE.add("2-60-9-2040");
        ONLY_HOUSE.add("44-46-139-2143");
        ONLY_HOUSE.add("2-60-9-2041");
        ONLY_HOUSE.add("2-60-9-2044");
        ONLY_HOUSE.add("2-60-9-2045");
        ONLY_HOUSE.add("2-60-9-2042");
        ONLY_HOUSE.add("2-60-9-2043");
        ONLY_HOUSE.add("2-60-9-2048");
        ONLY_HOUSE.add("2-60-9-2049");
        ONLY_HOUSE.add("2-60-9-2046");
        ONLY_HOUSE.add("2-60-9-2047");
        ONLY_HOUSE.add("2-54-102-1061");
        ONLY_HOUSE.add("2-54-102-1062");
        ONLY_HOUSE.add("44-46-139-2153");
        ONLY_HOUSE.add("44-46-139-2152");
        ONLY_HOUSE.add("44-46-139-2154");
        ONLY_HOUSE.add("2-60-9-2030");
        ONLY_HOUSE.add("2-60-9-2031");
        ONLY_HOUSE.add("2-60-9-2032");
        ONLY_HOUSE.add("2-60-9-2033");
        ONLY_HOUSE.add("2-60-9-2034");
        ONLY_HOUSE.add("2-60-9-2035");
        ONLY_HOUSE.add("2-60-9-2036");
        ONLY_HOUSE.add("2-60-9-2037");
        ONLY_HOUSE.add("2-60-9-2038");
        ONLY_HOUSE.add("2-60-9-2039");
        ONLY_HOUSE.add("44-46-139-2151");
        ONLY_HOUSE.add("2-60-9-2001");
        ONLY_HOUSE.add("2-60-9-2005");
        ONLY_HOUSE.add("2-60-9-2004");
        ONLY_HOUSE.add("2-60-9-2003");
        ONLY_HOUSE.add("44-46-139-2105");
        ONLY_HOUSE.add("2-60-9-2002");
        ONLY_HOUSE.add("44-46-139-2103");
        ONLY_HOUSE.add("2-60-9-2008");
        ONLY_HOUSE.add("2-60-9-2009");
        ONLY_HOUSE.add("2-60-9-2006");
        ONLY_HOUSE.add("2-60-9-2007");
        ONLY_HOUSE.add("44-46-139-2113");
        ONLY_HOUSE.add("44-46-139-2111");
        ONLY_HOUSE.add("2-54-72-3041");
        ONLY_HOUSE.add("2-55-134-1052");
        ONLY_HOUSE.add("2-54-72-3052");
        ONLY_HOUSE.add("2-54-102-4021");
        ONLY_HOUSE.add("44-21-22-8");
        ONLY_HOUSE.add("44-46-139-2173");
        ONLY_HOUSE.add("2-54-72-3012");
        ONLY_HOUSE.add("44-46-139-2163");
        ONLY_HOUSE.add("44-46-139-2165");
        ONLY_HOUSE.add("44-46-139-2194");
        ONLY_HOUSE.add("44-46-139-2193");
        ONLY_HOUSE.add("44-46-139-2196");
        ONLY_HOUSE.add("44-46-139-2183");
        ONLY_HOUSE.add("2-55-134-4061");
        ONLY_HOUSE.add("44-4-40");
        ONLY_HOUSE.add("2-55-93-2061");
        ONLY_HOUSE.add("44-4-35");
        ONLY_HOUSE.add("44-4-36");
        ONLY_HOUSE.add("44-4-37");
        ONLY_HOUSE.add("44-4-38");
        ONLY_HOUSE.add("44-4-39");
        ONLY_HOUSE.add("44-40-20-4052");
        ONLY_HOUSE.add("44-4-12");
        ONLY_HOUSE.add("2-54-88-1041");
        ONLY_HOUSE.add("44-40-20-4062");
        ONLY_HOUSE.add("2-55-134-1021");
        ONLY_HOUSE.add("44-46-144-1131");
        ONLY_HOUSE.add("44-46-144-1132");
        ONLY_HOUSE.add("2-54-69-2061");
        ONLY_HOUSE.add("44-46-108-15");
        ONLY_HOUSE.add("2-54-113-1061");
        ONLY_HOUSE.add("44-46-108-14");
        ONLY_HOUSE.add("44-46-108-10");
        ONLY_HOUSE.add("44-46-108-17");
        ONLY_HOUSE.add("44-46-108-18");
        ONLY_HOUSE.add("44-46-108-20");
        ONLY_HOUSE.add("44-46-144-1162");
        ONLY_HOUSE.add("44-46-144-1161");
        ONLY_HOUSE.add("44-46-144-1171");
        ONLY_HOUSE.add("2-54-112-1092");
        ONLY_HOUSE.add("44-46-144-1141");
        ONLY_HOUSE.add("44-46-144-1142");
        ONLY_HOUSE.add("2-54-69-2051");
        ONLY_HOUSE.add("44-46-144-1153");
        ONLY_HOUSE.add("2-54-183-3032");
        ONLY_HOUSE.add("2-55-97-2051");
        ONLY_HOUSE.add("44-46-105-11");
        ONLY_HOUSE.add("44-46-105-10");
        ONLY_HOUSE.add("44-46-105-12");
        ONLY_HOUSE.add("44-46-105-19");
        ONLY_HOUSE.add("44-46-105-18");
        ONLY_HOUSE.add("44-46-105-14");
        ONLY_HOUSE.add("2-54-169-1151");
        ONLY_HOUSE.add("2-54-169-1162");
        ONLY_HOUSE.add("2-54-145-2012");
        ONLY_HOUSE.add("2-54-169-1161");
        ONLY_HOUSE.add("2-54-169-1142");
        ONLY_HOUSE.add("44-40-23-1021");
        ONLY_HOUSE.add("2-54-142-2062");
        ONLY_HOUSE.add("2-60-9-3799");
        ONLY_HOUSE.add("2-60-9-3790");
        ONLY_HOUSE.add("2-60-9-3798");
        ONLY_HOUSE.add("2-60-9-3797");
        ONLY_HOUSE.add("44-40-23-1011");
        ONLY_HOUSE.add("2-60-9-3796");
        ONLY_HOUSE.add("2-60-9-3795");
        ONLY_HOUSE.add("2-60-9-3794");
        ONLY_HOUSE.add("2-60-9-3793");
        ONLY_HOUSE.add("2-60-9-3792");
        ONLY_HOUSE.add("2-60-9-3791");
        ONLY_HOUSE.add("2-60-9-3788");
        ONLY_HOUSE.add("2-60-9-3789");
        ONLY_HOUSE.add("2-60-9-3785");
        ONLY_HOUSE.add("2-60-9-3784");
        ONLY_HOUSE.add("2-60-9-3787");
        ONLY_HOUSE.add("2-60-9-3786");
        ONLY_HOUSE.add("2-60-9-3781");
        ONLY_HOUSE.add("2-60-9-3780");
        ONLY_HOUSE.add("2-60-9-3783");
        ONLY_HOUSE.add("2-60-9-3782");
        ONLY_HOUSE.add("2-60-9-3885");
        ONLY_HOUSE.add("2-60-9-3886");
        ONLY_HOUSE.add("2-60-9-3883");
        ONLY_HOUSE.add("2-60-9-3884");
        ONLY_HOUSE.add("2-60-9-3881");
        ONLY_HOUSE.add("2-60-9-3882");
        ONLY_HOUSE.add("2-60-9-3880");
        ONLY_HOUSE.add("2-54-163-2031");
        ONLY_HOUSE.add("2-55-81-1042");
        ONLY_HOUSE.add("2-54-169-1103");
        ONLY_HOUSE.add("44-46-14-20");
        ONLY_HOUSE.add("44-40-5-3101");
        ONLY_HOUSE.add("44-40-5-3103");
        ONLY_HOUSE.add("2-60-9-3889");
        ONLY_HOUSE.add("2-60-9-3888");
        ONLY_HOUSE.add("2-60-9-3887");
        ONLY_HOUSE.add("2-60-9-3894");
        ONLY_HOUSE.add("2-60-9-3895");
        ONLY_HOUSE.add("44-46-14-38");
        ONLY_HOUSE.add("2-60-9-3896");
        ONLY_HOUSE.add("2-60-9-3897");
        ONLY_HOUSE.add("2-60-9-3890");
        ONLY_HOUSE.add("2-60-9-3891");
        ONLY_HOUSE.add("2-60-9-3892");
        ONLY_HOUSE.add("2-60-9-3893");
        ONLY_HOUSE.add("44-40-6-2011");
        ONLY_HOUSE.add("2-54-163-2022");
        ONLY_HOUSE.add("44-40-5-3111");
        ONLY_HOUSE.add("2-60-9-3899");
        ONLY_HOUSE.add("2-60-9-3898");
        ONLY_HOUSE.add("2-60-9-3860");
        ONLY_HOUSE.add("2-60-9-3863");
        ONLY_HOUSE.add("2-60-9-3864");
        ONLY_HOUSE.add("2-60-9-3861");
        ONLY_HOUSE.add("2-60-9-3862");
        ONLY_HOUSE.add("2-55-81-1012");
        ONLY_HOUSE.add("2-60-9-3868");
        ONLY_HOUSE.add("2-60-9-3867");
        ONLY_HOUSE.add("2-60-9-3866");
        ONLY_HOUSE.add("2-60-9-3865");
        ONLY_HOUSE.add("2-60-9-3869");
        ONLY_HOUSE.add("44-46-14-19");
        ONLY_HOUSE.add("2-60-9-3870");
        ONLY_HOUSE.add("2-60-9-3871");
        ONLY_HOUSE.add("2-60-9-3872");
        ONLY_HOUSE.add("2-60-9-3873");
        ONLY_HOUSE.add("2-60-9-3874");
        ONLY_HOUSE.add("2-60-9-3875");
        ONLY_HOUSE.add("2-60-9-3877");
        ONLY_HOUSE.add("2-60-9-3876");
        ONLY_HOUSE.add("2-60-9-3879");
        ONLY_HOUSE.add("2-60-9-3878");
        ONLY_HOUSE.add("2-60-9-3842");
        ONLY_HOUSE.add("2-60-9-3841");
        ONLY_HOUSE.add("2-60-9-3840");
        ONLY_HOUSE.add("2-60-9-3849");
        ONLY_HOUSE.add("2-60-9-3847");
        ONLY_HOUSE.add("2-60-9-3848");
        ONLY_HOUSE.add("2-60-9-3845");
        ONLY_HOUSE.add("2-60-9-3846");
        ONLY_HOUSE.add("2-60-9-3843");
        ONLY_HOUSE.add("2-60-9-3844");
        ONLY_HOUSE.add("44-40-7-5");
        ONLY_HOUSE.add("44-40-7-4");
        ONLY_HOUSE.add("44-40-7-3");
        ONLY_HOUSE.add("44-40-7-2");
        ONLY_HOUSE.add("44-40-7-9");
        ONLY_HOUSE.add("44-40-7-8");
        ONLY_HOUSE.add("44-40-7-7");
        ONLY_HOUSE.add("44-40-7-6");
        ONLY_HOUSE.add("2-60-9-3851");
        ONLY_HOUSE.add("2-60-9-3850");
        ONLY_HOUSE.add("2-60-9-3853");
        ONLY_HOUSE.add("44-40-6-2052");
        ONLY_HOUSE.add("2-60-9-3852");
        ONLY_HOUSE.add("44-40-7-1");
        ONLY_HOUSE.add("2-60-9-3858");
        ONLY_HOUSE.add("2-60-9-3859");
        ONLY_HOUSE.add("2-60-9-3854");
        ONLY_HOUSE.add("2-60-9-3855");
        ONLY_HOUSE.add("2-60-9-3856");
        ONLY_HOUSE.add("2-60-9-3857");
        ONLY_HOUSE.add("44-40-6-2021");
        ONLY_HOUSE.add("2-60-9-3820");
        ONLY_HOUSE.add("2-60-9-3823");
        ONLY_HOUSE.add("2-60-9-3824");
        ONLY_HOUSE.add("2-60-9-3821");
        ONLY_HOUSE.add("2-60-9-3822");
        ONLY_HOUSE.add("2-60-9-3827");
        ONLY_HOUSE.add("2-60-9-3828");
        ONLY_HOUSE.add("2-60-9-3825");
        ONLY_HOUSE.add("2-60-9-3826");
        ONLY_HOUSE.add("2-60-9-3829");
        ONLY_HOUSE.add("44-46-75-3");
        ONLY_HOUSE.add("2-60-9-3831");
        ONLY_HOUSE.add("2-60-9-3830");
        ONLY_HOUSE.add("44-40-6-2031");
        ONLY_HOUSE.add("2-60-9-3832");
        ONLY_HOUSE.add("2-60-9-3834");
        ONLY_HOUSE.add("2-60-9-3835");
        ONLY_HOUSE.add("2-60-9-3836");
        ONLY_HOUSE.add("2-60-9-3837");
        ONLY_HOUSE.add("2-60-9-3838");
        ONLY_HOUSE.add("2-60-9-3839");
        ONLY_HOUSE.add("2-60-9-3808");
        ONLY_HOUSE.add("2-60-9-3807");
        ONLY_HOUSE.add("2-60-9-3809");
        ONLY_HOUSE.add("2-60-9-3804");
        ONLY_HOUSE.add("44-46-2-2162");
        ONLY_HOUSE.add("2-60-9-3803");
        ONLY_HOUSE.add("2-60-9-3806");
        ONLY_HOUSE.add("2-60-9-3805");
        ONLY_HOUSE.add("2-54-102-2041");
        ONLY_HOUSE.add("2-60-9-3802");
        ONLY_HOUSE.add("2-60-9-3801");
        ONLY_HOUSE.add("2-54-141-3042");
        ONLY_HOUSE.add("2-60-9-3819");
        ONLY_HOUSE.add("2-60-9-3818");
        ONLY_HOUSE.add("2-60-9-3817");
        ONLY_HOUSE.add("2-60-9-3816");
        ONLY_HOUSE.add("2-60-9-3815");
        ONLY_HOUSE.add("2-60-9-3814");
        ONLY_HOUSE.add("2-60-9-3813");
        ONLY_HOUSE.add("2-60-9-3812");
        ONLY_HOUSE.add("2-60-9-3811");
        ONLY_HOUSE.add("2-60-9-3810");
        ONLY_HOUSE.add("44-40-5-3161");
        ONLY_HOUSE.add("44-40-15-2041");
        ONLY_HOUSE.add("44-40-5-3171");
        ONLY_HOUSE.add("44-40-5-3172");
        ONLY_HOUSE.add("2-54-141-3012");
        ONLY_HOUSE.add("2-54-102-2051");
        ONLY_HOUSE.add("44-46-144-1182");
        ONLY_HOUSE.add("44-40-15-2053");
        ONLY_HOUSE.add("44-40-5-3151");
        ONLY_HOUSE.add("44-40-5-3121");
        ONLY_HOUSE.add("2-54-163-2012");
        ONLY_HOUSE.add("44-28-13");
        ONLY_HOUSE.add("2-55-133-3053");
        ONLY_HOUSE.add("2-54-169-2102");
        ONLY_HOUSE.add("2-55-113-2043");
        ONLY_HOUSE.add("44-46-116-21");
        ONLY_HOUSE.add("44-46-116-24");
        ONLY_HOUSE.add("2-54-169-1071");
        ONLY_HOUSE.add("44-46-116-26");
        ONLY_HOUSE.add("2-55-113-2033");
        ONLY_HOUSE.add("2-54-148-1063");
        ONLY_HOUSE.add("44-40-15-2121");
        ONLY_HOUSE.add("44-46-116-16");
        ONLY_HOUSE.add("2-54-169-1082");
        ONLY_HOUSE.add("44-46-139-2301");
        ONLY_HOUSE.add("2-54-142-3062");
        ONLY_HOUSE.add("44-46-144-1053");
        ONLY_HOUSE.add("44-46-144-1052");
        ONLY_HOUSE.add("44-46-22-7");
        ONLY_HOUSE.add("44-46-22-8");
        ONLY_HOUSE.add("44-51-44-13");
        ONLY_HOUSE.add("2-54-85-1062");
        ONLY_HOUSE.add("2-54-169-1052");
        ONLY_HOUSE.add("2-54-83-3013");
        ONLY_HOUSE.add("44-46-98-4");
        ONLY_HOUSE.add("44-46-98-5");
        ONLY_HOUSE.add("44-46-98-7");
        ONLY_HOUSE.add("44-46-98-1");
        ONLY_HOUSE.add("44-46-98-3");
        ONLY_HOUSE.add("44-46-98-8");
        ONLY_HOUSE.add("44-46-98-9");
        ONLY_HOUSE.add("2-54-66-1021");
        ONLY_HOUSE.add("2-54-83-3051");
        ONLY_HOUSE.add("2-54-83-3061");
        ONLY_HOUSE.add("44-46-139-1204");
        ONLY_HOUSE.add("44-46-139-1203");
        ONLY_HOUSE.add("2-54-63-2052");
        ONLY_HOUSE.add("2-60-9-3742");
        ONLY_HOUSE.add("2-60-9-3743");
        ONLY_HOUSE.add("2-60-9-3740");
        ONLY_HOUSE.add("2-60-9-3741");
        ONLY_HOUSE.add("2-60-9-3747");
        ONLY_HOUSE.add("2-60-9-3746");
        ONLY_HOUSE.add("2-60-9-3745");
        ONLY_HOUSE.add("2-60-9-3744");
        ONLY_HOUSE.add("99-99-99-3041");
        ONLY_HOUSE.add("2-60-9-3749");
        ONLY_HOUSE.add("2-60-9-3748");
        ONLY_HOUSE.add("44-21-12-10");
        ONLY_HOUSE.add("44-21-12-11");
        ONLY_HOUSE.add("2-60-9-3750");
        ONLY_HOUSE.add("2-60-9-3751");
        ONLY_HOUSE.add("2-60-9-3752");
        ONLY_HOUSE.add("2-54-109-2042");
        ONLY_HOUSE.add("2-60-9-3753");
        ONLY_HOUSE.add("2-60-9-3754");
        ONLY_HOUSE.add("2-60-9-3756");
        ONLY_HOUSE.add("2-60-9-3755");
        ONLY_HOUSE.add("2-60-9-3757");
        ONLY_HOUSE.add("2-60-9-3759");
        ONLY_HOUSE.add("44-40-6-2122");
        ONLY_HOUSE.add("2-60-9-3764");
        ONLY_HOUSE.add("44-46-139-1224");
        ONLY_HOUSE.add("2-60-9-3765");
        ONLY_HOUSE.add("44-46-139-1223");
        ONLY_HOUSE.add("2-60-9-3762");
        ONLY_HOUSE.add("2-60-9-3763");
        ONLY_HOUSE.add("2-60-9-3760");
        ONLY_HOUSE.add("2-60-9-3761");
        ONLY_HOUSE.add("2-60-9-3769");
        ONLY_HOUSE.add("2-60-9-3768");
        ONLY_HOUSE.add("2-60-9-3767");
        ONLY_HOUSE.add("2-60-9-3766");
        ONLY_HOUSE.add("2-54-63-2062");
        ONLY_HOUSE.add("44-46-139-1214");
        ONLY_HOUSE.add("2-60-9-3773");
        ONLY_HOUSE.add("2-60-9-3774");
        ONLY_HOUSE.add("2-60-9-3775");
        ONLY_HOUSE.add("44-46-139-1213");
        ONLY_HOUSE.add("2-60-9-3776");
        ONLY_HOUSE.add("2-60-9-3770");
        ONLY_HOUSE.add("2-60-9-3771");
        ONLY_HOUSE.add("2-60-9-3772");
        ONLY_HOUSE.add("2-60-9-3778");
        ONLY_HOUSE.add("2-60-9-3777");
        ONLY_HOUSE.add("2-60-9-3779");
        ONLY_HOUSE.add("2-54-183-4042");
        ONLY_HOUSE.add("44-40-6-2");
        ONLY_HOUSE.add("2-60-9-3719");
        ONLY_HOUSE.add("44-46-105-20");
        ONLY_HOUSE.add("2-54-102-3022");
        ONLY_HOUSE.add("2-60-9-3718");
        ONLY_HOUSE.add("44-40-6-2162");
        ONLY_HOUSE.add("2-60-9-3721");
        ONLY_HOUSE.add("2-60-9-3720");
        ONLY_HOUSE.add("44-40-6-2161");
        ONLY_HOUSE.add("2-60-9-3728");
        ONLY_HOUSE.add("2-60-9-3729");
        ONLY_HOUSE.add("2-60-9-3726");
        ONLY_HOUSE.add("2-60-9-3727");
        ONLY_HOUSE.add("2-60-9-3724");
        ONLY_HOUSE.add("2-60-9-3725");
        ONLY_HOUSE.add("2-54-102-3012");
        ONLY_HOUSE.add("2-60-9-3722");
        ONLY_HOUSE.add("2-60-9-3723");
        ONLY_HOUSE.add("2-60-9-3730");
        ONLY_HOUSE.add("2-60-9-3732");
        ONLY_HOUSE.add("2-60-9-3731");
        ONLY_HOUSE.add("2-54-109-2022");
        ONLY_HOUSE.add("2-60-9-3737");
        ONLY_HOUSE.add("2-60-9-3738");
        ONLY_HOUSE.add("2-60-9-3739");
        ONLY_HOUSE.add("2-60-9-3733");
        ONLY_HOUSE.add("2-60-9-3734");
        ONLY_HOUSE.add("2-60-9-3735");
        ONLY_HOUSE.add("2-60-9-3736");
        ONLY_HOUSE.add("44-46-144-1072");
        ONLY_HOUSE.add("44-46-144-1061");
        ONLY_HOUSE.add("44-46-144-1062");
        ONLY_HOUSE.add("44-46-139-1274");
        ONLY_HOUSE.add("44-50-9");
        ONLY_HOUSE.add("44-50-6");
        ONLY_HOUSE.add("44-50-8");
        ONLY_HOUSE.add("44-50-7");
        ONLY_HOUSE.add("44-46-76-6");
        ONLY_HOUSE.add("44-46-144-1092");
        ONLY_HOUSE.add("44-46-139-1292");
        ONLY_HOUSE.add("44-46-139-1293");
        ONLY_HOUSE.add("44-46-144-1082");
        ONLY_HOUSE.add("44-46-139-1295");
        ONLY_HOUSE.add("44-46-139-1294");
        ONLY_HOUSE.add("44-46-98-128");
        ONLY_HOUSE.add("44-46-139-1242");
        ONLY_HOUSE.add("44-46-139-1244");
        ONLY_HOUSE.add("44-46-98-121");
        ONLY_HOUSE.add("44-46-98-116");
        ONLY_HOUSE.add("44-46-139-1234");
        ONLY_HOUSE.add("2-54-80-4061");
        ONLY_HOUSE.add("2-55-78-3041");
        ONLY_HOUSE.add("44-46-139-1263");
        ONLY_HOUSE.add("44-46-139-1264");
        ONLY_HOUSE.add("44-46-139-1254");
        ONLY_HOUSE.add("44-46-139-1252");
        ONLY_HOUSE.add("44-46-139-1253");
        ONLY_HOUSE.add("44-40-18-1071");
        ONLY_HOUSE.add("44-40-18-1061");
        ONLY_HOUSE.add("44-40-18-1062");
        ONLY_HOUSE.add("2-54-75-1111");
        ONLY_HOUSE.add("44-40-18-1091");
        ONLY_HOUSE.add("44-40-15-1112");
        ONLY_HOUSE.add("44-40-5-4093");
        ONLY_HOUSE.add("44-46-144-1301");
        ONLY_HOUSE.add("44-46-144-1302");
        ONLY_HOUSE.add("44-46-144-1303");
        ONLY_HOUSE.add("44-40-18-1081");
        ONLY_HOUSE.add("44-40-18-1082");
        ONLY_HOUSE.add("44-40-15-1103");
        ONLY_HOUSE.add("44-46-108-1");
        ONLY_HOUSE.add("44-40-5-4073");
        ONLY_HOUSE.add("44-40-5-4071");
        ONLY_HOUSE.add("44-34-34");
        ONLY_HOUSE.add("44-34-35");
        ONLY_HOUSE.add("44-40-5-4083");
        ONLY_HOUSE.add("44-40-15-1122");
        ONLY_HOUSE.add("44-34-31");
        ONLY_HOUSE.add("44-34-30");
        ONLY_HOUSE.add("44-34-33");
        ONLY_HOUSE.add("44-40-25-1162");
        ONLY_HOUSE.add("44-34-32");
        ONLY_HOUSE.add("44-40-5-4051");
        ONLY_HOUSE.add("44-40-5-4061");
        ONLY_HOUSE.add("44-40-9-3012");
        ONLY_HOUSE.add("44-34-16");
        ONLY_HOUSE.add("44-34-17");
        ONLY_HOUSE.add("44-34-15");
        ONLY_HOUSE.add("44-21-29-30");
        ONLY_HOUSE.add("2-55-133-1062");
        ONLY_HOUSE.add("44-40-25-1122");
        ONLY_HOUSE.add("44-46-139-1191");
        ONLY_HOUSE.add("2-55-134-2061");
        ONLY_HOUSE.add("44-46-139-1194");
        ONLY_HOUSE.add("44-46-108-6");
        ONLY_HOUSE.add("44-46-108-4");
        ONLY_HOUSE.add("44-46-108-3");
        ONLY_HOUSE.add("44-46-108-2");
        ONLY_HOUSE.add("44-21-29-29");
        ONLY_HOUSE.add("2-55-87-4023");
        ONLY_HOUSE.add("44-21-29-27");
        ONLY_HOUSE.add("44-21-29-21");
        ONLY_HOUSE.add("2-60-9-899");
        ONLY_HOUSE.add("2-60-9-896");
        ONLY_HOUSE.add("2-60-9-895");
        ONLY_HOUSE.add("2-60-9-898");
        ONLY_HOUSE.add("2-60-9-897");
        ONLY_HOUSE.add("2-60-9-892");
        ONLY_HOUSE.add("2-60-9-891");
        ONLY_HOUSE.add("2-60-9-894");
        ONLY_HOUSE.add("2-60-9-893");
        ONLY_HOUSE.add("2-60-9-890");
        ONLY_HOUSE.add("2-60-9-889");
        ONLY_HOUSE.add("2-60-9-887");
        ONLY_HOUSE.add("2-60-9-886");
        ONLY_HOUSE.add("2-60-9-885");
        ONLY_HOUSE.add("2-60-9-884");
        ONLY_HOUSE.add("2-60-9-883");
        ONLY_HOUSE.add("2-60-9-882");
        ONLY_HOUSE.add("2-60-9-881");
        ONLY_HOUSE.add("2-60-9-880");
        ONLY_HOUSE.add("2-55-133-1031");
        ONLY_HOUSE.add("44-21-12-9");
        ONLY_HOUSE.add("44-21-12-8");
        ONLY_HOUSE.add("44-21-12-2");
        ONLY_HOUSE.add("44-21-12-1");
        ONLY_HOUSE.add("2-60-9-960");
        ONLY_HOUSE.add("2-60-9-961");
        ONLY_HOUSE.add("2-60-9-962");
        ONLY_HOUSE.add("2-60-9-963");
        ONLY_HOUSE.add("44-21-13-4");
        ONLY_HOUSE.add("44-46-139-1134");
        ONLY_HOUSE.add("44-46-139-1133");
        ONLY_HOUSE.add("2-60-9-964");
        ONLY_HOUSE.add("2-60-9-965");
        ONLY_HOUSE.add("2-60-9-966");
        ONLY_HOUSE.add("2-60-9-967");
        ONLY_HOUSE.add("2-60-9-968");
        ONLY_HOUSE.add("2-60-9-969");
        ONLY_HOUSE.add("2-60-9-970");
        ONLY_HOUSE.add("2-60-9-971");
        ONLY_HOUSE.add("2-60-9-974");
        ONLY_HOUSE.add("2-60-9-975");
        ONLY_HOUSE.add("2-60-9-972");
        ONLY_HOUSE.add("2-60-9-973");
        ONLY_HOUSE.add("2-60-9-978");
        ONLY_HOUSE.add("2-60-9-979");
        ONLY_HOUSE.add("2-60-9-976");
        ONLY_HOUSE.add("2-60-9-977");
        ONLY_HOUSE.add("44-21-22-15");
        ONLY_HOUSE.add("2-60-9-980");
        ONLY_HOUSE.add("2-60-9-981");
        ONLY_HOUSE.add("2-60-9-982");
        ONLY_HOUSE.add("2-60-9-987");
        ONLY_HOUSE.add("2-60-9-988");
        ONLY_HOUSE.add("2-60-9-989");
        ONLY_HOUSE.add("44-46-139-1114");
        ONLY_HOUSE.add("2-60-9-983");
        ONLY_HOUSE.add("2-60-9-984");
        ONLY_HOUSE.add("2-60-9-985");
        ONLY_HOUSE.add("2-60-9-986");
        ONLY_HOUSE.add("2-60-9-992");
        ONLY_HOUSE.add("2-60-9-993");
        ONLY_HOUSE.add("2-60-9-990");
        ONLY_HOUSE.add("2-60-9-991");
        ONLY_HOUSE.add("2-60-9-998");
        ONLY_HOUSE.add("2-60-9-999");
        ONLY_HOUSE.add("2-60-9-996");
        ONLY_HOUSE.add("44-46-139-1123");
        ONLY_HOUSE.add("44-46-139-1122");
        ONLY_HOUSE.add("2-60-9-997");
        ONLY_HOUSE.add("2-60-9-994");
        ONLY_HOUSE.add("2-60-9-995");
        ONLY_HOUSE.add("44-46-139-1124");
        ONLY_HOUSE.add("2-60-9-920");
        ONLY_HOUSE.add("2-60-9-922");
        ONLY_HOUSE.add("2-60-9-921");
        ONLY_HOUSE.add("2-60-9-924");
        ONLY_HOUSE.add("44-46-139-1173");
        ONLY_HOUSE.add("44-46-139-1174");
        ONLY_HOUSE.add("2-60-9-923");
        ONLY_HOUSE.add("2-60-9-925");
        ONLY_HOUSE.add("2-60-9-926");
        ONLY_HOUSE.add("2-60-9-927");
        ONLY_HOUSE.add("2-60-9-928");
        ONLY_HOUSE.add("2-60-9-929");
        ONLY_HOUSE.add("2-60-9-931");
        ONLY_HOUSE.add("2-60-9-930");
        ONLY_HOUSE.add("44-46-139-1184");
        ONLY_HOUSE.add("2-60-9-935");
        ONLY_HOUSE.add("2-60-9-934");
        ONLY_HOUSE.add("2-60-9-933");
        ONLY_HOUSE.add("2-60-9-932");
        ONLY_HOUSE.add("2-54-80-3012");
        ONLY_HOUSE.add("2-60-9-938");
        ONLY_HOUSE.add("2-60-9-939");
        ONLY_HOUSE.add("2-60-9-936");
        ONLY_HOUSE.add("2-60-9-937");
        ONLY_HOUSE.add("2-60-9-944");
        ONLY_HOUSE.add("2-60-9-943");
        ONLY_HOUSE.add("2-60-9-946");
        ONLY_HOUSE.add("2-60-9-945");
        ONLY_HOUSE.add("2-60-9-940");
        ONLY_HOUSE.add("2-60-9-942");
        ONLY_HOUSE.add("2-60-9-941");
        ONLY_HOUSE.add("2-60-9-947");
        ONLY_HOUSE.add("2-60-9-948");
        ONLY_HOUSE.add("2-60-9-949");
        ONLY_HOUSE.add("2-60-9-957");
        ONLY_HOUSE.add("2-60-9-956");
        ONLY_HOUSE.add("44-46-139-1164");
        ONLY_HOUSE.add("2-60-9-955");
        ONLY_HOUSE.add("2-60-9-954");
        ONLY_HOUSE.add("2-60-9-953");
        ONLY_HOUSE.add("2-60-9-952");
        ONLY_HOUSE.add("2-60-9-951");
        ONLY_HOUSE.add("2-60-9-950");
        ONLY_HOUSE.add("2-60-9-958");
        ONLY_HOUSE.add("2-60-9-959");
        ONLY_HOUSE.add("2-54-92-1");
        ONLY_HOUSE.add("44-40-5-4031");
        ONLY_HOUSE.add("44-40-18-1101");
        ONLY_HOUSE.add("44-40-18-1102");
        ONLY_HOUSE.add("2-60-9-909");
        ONLY_HOUSE.add("2-60-9-908");
        ONLY_HOUSE.add("2-60-9-907");
        ONLY_HOUSE.add("2-60-9-906");
        ONLY_HOUSE.add("2-60-9-905");
        ONLY_HOUSE.add("2-60-9-904");
        ONLY_HOUSE.add("2-60-9-903");
        ONLY_HOUSE.add("2-60-9-901");
        ONLY_HOUSE.add("2-60-9-902");
        ONLY_HOUSE.add("44-40-18-1111");
        ONLY_HOUSE.add("2-60-9-919");
        ONLY_HOUSE.add("2-60-9-918");
        ONLY_HOUSE.add("2-60-9-915");
        ONLY_HOUSE.add("2-60-9-914");
        ONLY_HOUSE.add("2-60-9-917");
        ONLY_HOUSE.add("2-60-9-916");
        ONLY_HOUSE.add("2-55-70-2021");
        ONLY_HOUSE.add("2-54-29-2071");
        ONLY_HOUSE.add("2-60-9-910");
        ONLY_HOUSE.add("2-60-9-911");
        ONLY_HOUSE.add("2-60-9-912");
        ONLY_HOUSE.add("2-60-9-913");
        ONLY_HOUSE.add("44-40-18-1121");
        ONLY_HOUSE.add("44-40-18-1122");
        ONLY_HOUSE.add("44-40-18-1141");
        ONLY_HOUSE.add("44-40-18-1132");
        ONLY_HOUSE.add("44-40-18-1152");
        ONLY_HOUSE.add("44-40-18-1151");
        ONLY_HOUSE.add("44-46-139-1102");
        ONLY_HOUSE.add("44-40-18-1142");
        ONLY_HOUSE.add("44-46-139-1104");
        ONLY_HOUSE.add("44-40-18-1162");
        ONLY_HOUSE.add("44-40-18-1161");
        ONLY_HOUSE.add("44-40-18-1172");
        ONLY_HOUSE.add("44-40-18-1171");
        ONLY_HOUSE.add("44-46-14-57");
        ONLY_HOUSE.add("44-46-109-11");
        ONLY_HOUSE.add("44-46-109-10");
        ONLY_HOUSE.add("44-46-144-1212");
        ONLY_HOUSE.add("44-46-109-16");
        ONLY_HOUSE.add("44-46-109-19");
        ONLY_HOUSE.add("44-40-25-1072");
        ONLY_HOUSE.add("44-40-5-4171");
        ONLY_HOUSE.add("44-40-5-4172");
        ONLY_HOUSE.add("2-54-122-1031");
        ONLY_HOUSE.add("2-54-122-1032");
        ONLY_HOUSE.add("44-40-5-3081");
        ONLY_HOUSE.add("44-46-139-1084");
        ONLY_HOUSE.add("44-40-6-3031");
        ONLY_HOUSE.add("44-40-6-3032");
        ONLY_HOUSE.add("44-46-144-1272");
        ONLY_HOUSE.add("44-46-139-1071");
        ONLY_HOUSE.add("44-40-6-3042");
        ONLY_HOUSE.add("44-40-6-3041");
        ONLY_HOUSE.add("44-46-139-1074");
        ONLY_HOUSE.add("44-46-144-1261");
        ONLY_HOUSE.add("44-46-144-1262");
        ONLY_HOUSE.add("44-40-6-3051");
        ONLY_HOUSE.add("44-46-144-1291");
        ONLY_HOUSE.add("2-54-113-1113");
        ONLY_HOUSE.add("44-46-144-1292");
        ONLY_HOUSE.add("44-46-144-1293");
        ONLY_HOUSE.add("44-46-107-7");
        ONLY_HOUSE.add("44-46-139-1091");
        ONLY_HOUSE.add("44-46-139-1093");
        ONLY_HOUSE.add("44-40-6-3061");
        ONLY_HOUSE.add("44-46-52-3226");
        ONLY_HOUSE.add("44-46-144-1283");
        ONLY_HOUSE.add("44-46-144-1281");
        ONLY_HOUSE.add("44-46-144-1282");
        ONLY_HOUSE.add("2-60-9-791");
        ONLY_HOUSE.add("2-60-9-790");
        ONLY_HOUSE.add("2-60-9-793");
        ONLY_HOUSE.add("2-60-9-792");
        ONLY_HOUSE.add("2-60-9-795");
        ONLY_HOUSE.add("2-60-9-794");
        ONLY_HOUSE.add("2-60-9-797");
        ONLY_HOUSE.add("2-60-9-796");
        ONLY_HOUSE.add("2-60-9-799");
        ONLY_HOUSE.add("2-60-9-798");
        ONLY_HOUSE.add("44-40-6-3071");
        ONLY_HOUSE.add("44-46-144-1232");
        ONLY_HOUSE.add("2-60-9-780");
        ONLY_HOUSE.add("2-60-9-784");
        ONLY_HOUSE.add("2-60-9-783");
        ONLY_HOUSE.add("2-60-9-782");
        ONLY_HOUSE.add("2-60-9-781");
        ONLY_HOUSE.add("2-60-9-788");
        ONLY_HOUSE.add("2-60-9-787");
        ONLY_HOUSE.add("2-60-9-786");
        ONLY_HOUSE.add("2-60-9-785");
        ONLY_HOUSE.add("44-46-144-1223");
        ONLY_HOUSE.add("44-46-144-1222");
        ONLY_HOUSE.add("2-60-9-789");
        ONLY_HOUSE.add("2-60-9-771");
        ONLY_HOUSE.add("2-60-9-770");
        ONLY_HOUSE.add("2-60-9-773");
        ONLY_HOUSE.add("2-60-9-772");
        ONLY_HOUSE.add("2-60-9-779");
        ONLY_HOUSE.add("44-46-144-1252");
        ONLY_HOUSE.add("2-60-9-778");
        ONLY_HOUSE.add("2-60-9-775");
        ONLY_HOUSE.add("2-60-9-774");
        ONLY_HOUSE.add("2-60-9-777");
        ONLY_HOUSE.add("2-60-9-776");
        ONLY_HOUSE.add("2-60-9-762");
        ONLY_HOUSE.add("2-60-9-761");
        ONLY_HOUSE.add("2-60-9-760");
        ONLY_HOUSE.add("2-60-9-769");
        ONLY_HOUSE.add("2-60-9-768");
        ONLY_HOUSE.add("44-46-144-1242");
        ONLY_HOUSE.add("2-60-9-767");
        ONLY_HOUSE.add("2-60-9-766");
        ONLY_HOUSE.add("2-60-9-765");
        ONLY_HOUSE.add("2-60-9-764");
        ONLY_HOUSE.add("2-60-9-763");
        ONLY_HOUSE.add("2-60-9-866");
        ONLY_HOUSE.add("2-60-9-867");
        ONLY_HOUSE.add("2-60-9-868");
        ONLY_HOUSE.add("2-60-9-869");
        ONLY_HOUSE.add("2-60-9-862");
        ONLY_HOUSE.add("2-60-9-863");
        ONLY_HOUSE.add("2-60-9-864");
        ONLY_HOUSE.add("2-60-9-865");
        ONLY_HOUSE.add("2-60-9-860");
        ONLY_HOUSE.add("2-60-9-861");
        ONLY_HOUSE.add("2-60-9-879");
        ONLY_HOUSE.add("2-60-9-877");
        ONLY_HOUSE.add("2-60-9-878");
        ONLY_HOUSE.add("2-60-9-875");
        ONLY_HOUSE.add("2-60-9-876");
        ONLY_HOUSE.add("2-60-9-873");
        ONLY_HOUSE.add("2-60-9-874");
        ONLY_HOUSE.add("2-60-9-871");
        ONLY_HOUSE.add("2-60-9-872");
        ONLY_HOUSE.add("2-60-9-870");
        ONLY_HOUSE.add("2-60-9-840");
        ONLY_HOUSE.add("2-60-9-841");
        ONLY_HOUSE.add("2-60-9-842");
        ONLY_HOUSE.add("2-54-132-1041");
        ONLY_HOUSE.add("2-60-9-843");
        ONLY_HOUSE.add("2-60-9-844");
        ONLY_HOUSE.add("2-60-9-845");
        ONLY_HOUSE.add("2-60-9-846");
        ONLY_HOUSE.add("2-60-9-847");
        ONLY_HOUSE.add("44-40-25-2121");
        ONLY_HOUSE.add("2-54-27-3031");
        ONLY_HOUSE.add("2-60-9-849");
        ONLY_HOUSE.add("2-60-9-848");
        ONLY_HOUSE.add("2-60-9-853");
        ONLY_HOUSE.add("2-60-9-854");
        ONLY_HOUSE.add("2-60-9-851");
        ONLY_HOUSE.add("2-60-9-852");
        ONLY_HOUSE.add("2-60-9-857");
        ONLY_HOUSE.add("2-60-9-858");
        ONLY_HOUSE.add("2-60-9-855");
        ONLY_HOUSE.add("2-60-9-856");
        ONLY_HOUSE.add("2-60-9-850");
        ONLY_HOUSE.add("2-60-9-859");
        ONLY_HOUSE.add("2-60-9-823");
        ONLY_HOUSE.add("2-60-9-822");
        ONLY_HOUSE.add("2-60-9-825");
        ONLY_HOUSE.add("2-60-9-824");
        ONLY_HOUSE.add("2-60-9-821");
        ONLY_HOUSE.add("2-60-9-820");
        ONLY_HOUSE.add("2-60-9-3910");
        ONLY_HOUSE.add("2-60-9-826");
        ONLY_HOUSE.add("2-60-9-827");
        ONLY_HOUSE.add("2-60-9-828");
        ONLY_HOUSE.add("2-60-9-829");
        ONLY_HOUSE.add("2-60-9-3919");
        ONLY_HOUSE.add("2-54-113-1103");
        ONLY_HOUSE.add("44-40-25-2172");
        ONLY_HOUSE.add("2-60-9-836");
        ONLY_HOUSE.add("2-60-9-835");
        ONLY_HOUSE.add("2-60-9-834");
        ONLY_HOUSE.add("2-60-9-833");
        ONLY_HOUSE.add("44-46-139-1044");
        ONLY_HOUSE.add("2-60-9-832");
        ONLY_HOUSE.add("2-60-9-831");
        ONLY_HOUSE.add("2-60-9-830");
        ONLY_HOUSE.add("2-60-9-3901");
        ONLY_HOUSE.add("2-60-9-3902");
        ONLY_HOUSE.add("2-60-9-839");
        ONLY_HOUSE.add("2-60-9-837");
        ONLY_HOUSE.add("2-60-9-838");
        ONLY_HOUSE.add("2-60-9-3909");
        ONLY_HOUSE.add("2-60-9-3930");
        ONLY_HOUSE.add("44-40-5-22");
        ONLY_HOUSE.add("2-60-9-801");
        ONLY_HOUSE.add("44-46-139-1054");
        ONLY_HOUSE.add("44-40-5-21");
        ONLY_HOUSE.add("2-60-9-803");
        ONLY_HOUSE.add("2-60-9-802");
        ONLY_HOUSE.add("44-40-5-20");
        ONLY_HOUSE.add("2-60-9-804");
        ONLY_HOUSE.add("2-60-9-3937");
        ONLY_HOUSE.add("44-40-5-19");
        ONLY_HOUSE.add("2-60-9-3938");
        ONLY_HOUSE.add("2-60-9-805");
        ONLY_HOUSE.add("2-60-9-3935");
        ONLY_HOUSE.add("2-60-9-806");
        ONLY_HOUSE.add("2-60-9-807");
        ONLY_HOUSE.add("2-60-9-3936");
        ONLY_HOUSE.add("44-40-5-15");
        ONLY_HOUSE.add("2-60-9-808");
        ONLY_HOUSE.add("2-60-9-3933");
        ONLY_HOUSE.add("2-60-9-3934");
        ONLY_HOUSE.add("44-40-5-16");
        ONLY_HOUSE.add("2-60-9-809");
        ONLY_HOUSE.add("44-40-5-17");
        ONLY_HOUSE.add("44-40-5-18");
        ONLY_HOUSE.add("44-40-25-2171");
        ONLY_HOUSE.add("2-60-9-3939");
        ONLY_HOUSE.add("44-40-5-14");
        ONLY_HOUSE.add("2-60-9-810");
        ONLY_HOUSE.add("44-40-5-13");
        ONLY_HOUSE.add("44-40-5-12");
        ONLY_HOUSE.add("44-40-5-11");
        ONLY_HOUSE.add("44-40-5-10");
        ONLY_HOUSE.add("2-60-9-814");
        ONLY_HOUSE.add("2-60-9-813");
        ONLY_HOUSE.add("44-46-139-1064");
        ONLY_HOUSE.add("44-46-144-2301");
        ONLY_HOUSE.add("2-60-9-812");
        ONLY_HOUSE.add("2-60-9-811");
        ONLY_HOUSE.add("2-60-9-3924");
        ONLY_HOUSE.add("2-60-9-817");
        ONLY_HOUSE.add("2-60-9-818");
        ONLY_HOUSE.add("2-60-9-3925");
        ONLY_HOUSE.add("2-60-9-3926");
        ONLY_HOUSE.add("2-60-9-815");
        ONLY_HOUSE.add("2-60-9-3927");
        ONLY_HOUSE.add("2-60-9-816");
        ONLY_HOUSE.add("2-60-9-3920");
        ONLY_HOUSE.add("2-60-9-3921");
        ONLY_HOUSE.add("2-60-9-819");
        ONLY_HOUSE.add("2-60-9-3922");
        ONLY_HOUSE.add("2-60-9-3923");
        ONLY_HOUSE.add("2-60-9-3928");
        ONLY_HOUSE.add("2-60-9-3929");
        ONLY_HOUSE.add("2-60-9-3958");
        ONLY_HOUSE.add("44-40-5-4143");
        ONLY_HOUSE.add("2-60-9-3959");
        ONLY_HOUSE.add("44-40-5-4131");
        ONLY_HOUSE.add("2-60-9-3945");
        ONLY_HOUSE.add("2-60-9-3944");
        ONLY_HOUSE.add("2-60-9-3943");
        ONLY_HOUSE.add("2-60-9-3942");
        ONLY_HOUSE.add("2-60-9-3949");
        ONLY_HOUSE.add("2-60-9-3948");
        ONLY_HOUSE.add("2-60-9-3947");
        ONLY_HOUSE.add("2-60-9-3946");
        ONLY_HOUSE.add("2-60-9-3940");
        ONLY_HOUSE.add("2-60-9-3941");
        ONLY_HOUSE.add("44-40-5-4161");
        ONLY_HOUSE.add("44-40-5-4163");
        ONLY_HOUSE.add("2-60-9-3979");
        ONLY_HOUSE.add("2-60-9-3976");
        ONLY_HOUSE.add("2-60-9-3975");
        ONLY_HOUSE.add("2-60-9-3978");
        ONLY_HOUSE.add("2-60-9-3977");
        ONLY_HOUSE.add("2-60-9-3971");
        ONLY_HOUSE.add("2-60-9-3972");
        ONLY_HOUSE.add("2-60-9-3973");
        ONLY_HOUSE.add("2-60-9-3974");
        ONLY_HOUSE.add("2-60-9-3970");
        ONLY_HOUSE.add("44-40-5-4153");
        ONLY_HOUSE.add("2-54-75-1082");
        ONLY_HOUSE.add("2-60-9-3969");
        ONLY_HOUSE.add("2-60-9-3968");
        ONLY_HOUSE.add("2-54-75-1084");
        ONLY_HOUSE.add("2-60-9-3967");
        ONLY_HOUSE.add("2-60-9-3966");
        ONLY_HOUSE.add("2-60-9-3965");
        ONLY_HOUSE.add("2-60-9-3964");
        ONLY_HOUSE.add("2-60-9-3962");
        ONLY_HOUSE.add("2-60-9-3963");
        ONLY_HOUSE.add("2-60-9-3960");
        ONLY_HOUSE.add("2-60-9-3961");
        ONLY_HOUSE.add("2-60-9-3997");
        ONLY_HOUSE.add("44-40-5-4103");
        ONLY_HOUSE.add("2-60-9-3998");
        ONLY_HOUSE.add("2-60-9-3999");
        ONLY_HOUSE.add("44-40-5-4101");
        ONLY_HOUSE.add("2-60-9-3990");
        ONLY_HOUSE.add("2-60-9-3992");
        ONLY_HOUSE.add("2-60-9-3991");
        ONLY_HOUSE.add("2-60-9-3994");
        ONLY_HOUSE.add("2-60-9-3993");
        ONLY_HOUSE.add("2-60-9-3996");
        ONLY_HOUSE.add("2-60-9-3995");
        ONLY_HOUSE.add("2-60-9-3988");
        ONLY_HOUSE.add("2-60-9-3989");
        ONLY_HOUSE.add("2-60-9-3986");
        ONLY_HOUSE.add("2-60-9-3987");
        ONLY_HOUSE.add("2-60-9-3981");
        ONLY_HOUSE.add("2-60-9-3980");
        ONLY_HOUSE.add("2-60-9-3985");
        ONLY_HOUSE.add("2-60-9-3984");
        ONLY_HOUSE.add("2-60-9-3983");
        ONLY_HOUSE.add("2-60-9-3982");
        ONLY_HOUSE.add("44-40-5-4121");
        ONLY_HOUSE.add("44-40-5-4123");
        ONLY_HOUSE.add("44-40-5-4113");
        ONLY_HOUSE.add("44-40-5-4111");
        ONLY_HOUSE.add("2-55-90-3042");
        ONLY_HOUSE.add("44-40-5-2061");
        ONLY_HOUSE.add("44-40-5-2062");
        ONLY_HOUSE.add("44-40-5-2091");
        ONLY_HOUSE.add("44-46-144-2292");
        ONLY_HOUSE.add("44-40-5-2072");
        ONLY_HOUSE.add("44-40-5-2071");
        ONLY_HOUSE.add("44-46-144-2281");
        ONLY_HOUSE.add("44-46-144-2283");
        ONLY_HOUSE.add("44-40-5-2082");
        ONLY_HOUSE.add("44-40-5-2081");
        ONLY_HOUSE.add("44-21-25-12");
        ONLY_HOUSE.add("2-60-9-658");
        ONLY_HOUSE.add("2-60-9-657");
        ONLY_HOUSE.add("2-60-9-659");
        ONLY_HOUSE.add("2-60-9-654");
        ONLY_HOUSE.add("2-60-9-653");
        ONLY_HOUSE.add("2-60-9-656");
        ONLY_HOUSE.add("2-60-9-655");
        ONLY_HOUSE.add("2-60-9-650");
        ONLY_HOUSE.add("2-60-9-652");
        ONLY_HOUSE.add("2-60-9-651");
        ONLY_HOUSE.add("2-60-9-649");
        ONLY_HOUSE.add("2-60-9-648");
        ONLY_HOUSE.add("2-60-9-647");
        ONLY_HOUSE.add("2-60-9-646");
        ONLY_HOUSE.add("2-60-9-645");
        ONLY_HOUSE.add("2-60-9-644");
        ONLY_HOUSE.add("2-60-9-643");
        ONLY_HOUSE.add("2-60-9-641");
        ONLY_HOUSE.add("2-60-9-640");
        ONLY_HOUSE.add("2-60-9-676");
        ONLY_HOUSE.add("2-60-9-675");
        ONLY_HOUSE.add("2-60-9-678");
        ONLY_HOUSE.add("2-60-9-677");
        ONLY_HOUSE.add("2-60-9-679");
        ONLY_HOUSE.add("2-60-9-670");
        ONLY_HOUSE.add("2-60-9-672");
        ONLY_HOUSE.add("44-46-91-20");
        ONLY_HOUSE.add("2-60-9-671");
        ONLY_HOUSE.add("44-46-91-21");
        ONLY_HOUSE.add("2-60-9-674");
        ONLY_HOUSE.add("2-60-9-673");
        ONLY_HOUSE.add("2-54-77-1102");
        ONLY_HOUSE.add("2-60-9-667");
        ONLY_HOUSE.add("2-60-9-666");
        ONLY_HOUSE.add("2-60-9-665");
        ONLY_HOUSE.add("44-46-91-12");
        ONLY_HOUSE.add("44-46-91-13");
        ONLY_HOUSE.add("2-60-9-664");
        ONLY_HOUSE.add("2-60-9-669");
        ONLY_HOUSE.add("2-60-9-668");
        ONLY_HOUSE.add("2-60-9-663");
        ONLY_HOUSE.add("2-60-9-662");
        ONLY_HOUSE.add("2-60-9-661");
        ONLY_HOUSE.add("2-60-9-660");
        ONLY_HOUSE.add("2-60-9-693");
        ONLY_HOUSE.add("2-60-9-694");
        ONLY_HOUSE.add("2-60-9-695");
        ONLY_HOUSE.add("2-60-9-696");
        ONLY_HOUSE.add("2-60-9-690");
        ONLY_HOUSE.add("2-60-9-691");
        ONLY_HOUSE.add("2-60-9-692");
        ONLY_HOUSE.add("2-60-9-697");
        ONLY_HOUSE.add("2-60-9-698");
        ONLY_HOUSE.add("2-60-9-699");
        ONLY_HOUSE.add("2-60-9-684");
        ONLY_HOUSE.add("2-60-9-685");
        ONLY_HOUSE.add("2-60-9-682");
        ONLY_HOUSE.add("2-60-9-683");
        ONLY_HOUSE.add("2-60-9-680");
        ONLY_HOUSE.add("2-60-9-681");
        ONLY_HOUSE.add("2-60-9-688");
        ONLY_HOUSE.add("2-60-9-689");
        ONLY_HOUSE.add("2-60-9-686");
        ONLY_HOUSE.add("2-60-9-687");
        ONLY_HOUSE.add("44-40-6-3151");
        ONLY_HOUSE.add("2-55-105-3031");
        ONLY_HOUSE.add("44-40-6-3161");
        ONLY_HOUSE.add("2-54-57-4031");
        ONLY_HOUSE.add("2-55-105-3021");
        ONLY_HOUSE.add("44-40-6-3141");
        ONLY_HOUSE.add("44-40-6-3131");
        ONLY_HOUSE.add("2-60-9-709");
        ONLY_HOUSE.add("2-60-9-705");
        ONLY_HOUSE.add("2-60-9-706");
        ONLY_HOUSE.add("2-60-9-707");
        ONLY_HOUSE.add("2-60-9-708");
        ONLY_HOUSE.add("2-60-9-702");
        ONLY_HOUSE.add("2-60-9-701");
        ONLY_HOUSE.add("2-60-9-704");
        ONLY_HOUSE.add("2-60-9-703");
        ONLY_HOUSE.add("44-40-6-3121");
        ONLY_HOUSE.add("2-60-9-718");
        ONLY_HOUSE.add("2-60-9-719");
        ONLY_HOUSE.add("2-60-9-716");
        ONLY_HOUSE.add("2-60-9-717");
        ONLY_HOUSE.add("2-60-9-715");
        ONLY_HOUSE.add("2-60-9-714");
        ONLY_HOUSE.add("2-60-9-713");
        ONLY_HOUSE.add("2-60-9-712");
        ONLY_HOUSE.add("2-60-9-711");
        ONLY_HOUSE.add("2-60-9-710");
        ONLY_HOUSE.add("44-40-25-2051");
        ONLY_HOUSE.add("44-40-6-3111");
        ONLY_HOUSE.add("2-60-9-728");
        ONLY_HOUSE.add("2-60-9-727");
        ONLY_HOUSE.add("2-60-9-729");
        ONLY_HOUSE.add("44-21-32-1");
        ONLY_HOUSE.add("44-40-6-3101");
        ONLY_HOUSE.add("2-60-9-720");
        ONLY_HOUSE.add("2-60-9-721");
        ONLY_HOUSE.add("2-60-9-722");
        ONLY_HOUSE.add("2-60-9-723");
        ONLY_HOUSE.add("2-60-9-724");
        ONLY_HOUSE.add("2-60-9-725");
        ONLY_HOUSE.add("2-60-9-726");
        ONLY_HOUSE.add("2-60-9-739");
        ONLY_HOUSE.add("2-60-9-738");
        ONLY_HOUSE.add("2-60-9-732");
        ONLY_HOUSE.add("2-60-9-733");
        ONLY_HOUSE.add("2-60-9-730");
        ONLY_HOUSE.add("2-60-9-731");
        ONLY_HOUSE.add("2-60-9-736");
        ONLY_HOUSE.add("2-60-9-737");
        ONLY_HOUSE.add("2-60-9-734");
        ONLY_HOUSE.add("2-60-9-735");
        ONLY_HOUSE.add("2-60-9-749");
        ONLY_HOUSE.add("44-40-25-2021");
        ONLY_HOUSE.add("2-60-9-740");
        ONLY_HOUSE.add("2-60-9-746");
        ONLY_HOUSE.add("2-60-9-747");
        ONLY_HOUSE.add("2-60-9-748");
        ONLY_HOUSE.add("2-60-9-741");
        ONLY_HOUSE.add("2-60-9-742");
        ONLY_HOUSE.add("2-60-9-743");
        ONLY_HOUSE.add("2-55-16-2011");
        ONLY_HOUSE.add("2-55-119-3051");
        ONLY_HOUSE.add("2-60-9-750");
        ONLY_HOUSE.add("2-60-9-751");
        ONLY_HOUSE.add("44-40-25-2011");
        ONLY_HOUSE.add("2-60-9-758");
        ONLY_HOUSE.add("2-60-9-759");
        ONLY_HOUSE.add("2-54-180-2011");
        ONLY_HOUSE.add("2-60-9-756");
        ONLY_HOUSE.add("2-60-9-757");
        ONLY_HOUSE.add("2-60-9-754");
        ONLY_HOUSE.add("2-60-9-755");
        ONLY_HOUSE.add("2-60-9-752");
        ONLY_HOUSE.add("2-60-9-753");
        ONLY_HOUSE.add("44-46-144-2253");
        ONLY_HOUSE.add("44-51-37-12");
        ONLY_HOUSE.add("2-55-64-3041");
        ONLY_HOUSE.add("44-46-144-2211");
        ONLY_HOUSE.add("2-55-133-4041");
        ONLY_HOUSE.add("44-46-2-1113");
        ONLY_HOUSE.add("2-55-72-2012");
        ONLY_HOUSE.add("2-54-77-1062");
        ONLY_HOUSE.add("44-40-5-2152");
        ONLY_HOUSE.add("44-40-5-2151");
        ONLY_HOUSE.add("2-55-112-4013");
        ONLY_HOUSE.add("44-46-2-1183");
        ONLY_HOUSE.add("44-40-5-2162");
        ONLY_HOUSE.add("2-54-23-4021");
        ONLY_HOUSE.add("44-40-5-2172");
        ONLY_HOUSE.add("44-40-5-2171");
        ONLY_HOUSE.add("44-21-18-15");
        ONLY_HOUSE.add("44-21-18-10");
        ONLY_HOUSE.add("44-21-18-11");
        ONLY_HOUSE.add("44-46-109-4");
        ONLY_HOUSE.add("44-46-109-6");
        ONLY_HOUSE.add("44-40-20-6042");
        ONLY_HOUSE.add("44-46-144-2161");
        ONLY_HOUSE.add("2-60-9-551");
        ONLY_HOUSE.add("2-60-9-550");
        ONLY_HOUSE.add("2-60-9-553");
        ONLY_HOUSE.add("2-60-9-555");
        ONLY_HOUSE.add("2-60-9-554");
        ONLY_HOUSE.add("2-60-9-557");
        ONLY_HOUSE.add("2-60-9-556");
        ONLY_HOUSE.add("2-60-9-559");
        ONLY_HOUSE.add("2-60-9-558");
        ONLY_HOUSE.add("2-60-9-542");
        ONLY_HOUSE.add("2-60-9-541");
        ONLY_HOUSE.add("2-60-9-540");
        ONLY_HOUSE.add("2-60-9-546");
        ONLY_HOUSE.add("2-60-9-545");
        ONLY_HOUSE.add("2-60-9-544");
        ONLY_HOUSE.add("2-60-9-543");
        ONLY_HOUSE.add("2-60-9-549");
        ONLY_HOUSE.add("2-60-9-548");
        ONLY_HOUSE.add("2-60-9-547");
        ONLY_HOUSE.add("2-60-9-531");
        ONLY_HOUSE.add("2-60-9-530");
        ONLY_HOUSE.add("2-60-9-537");
        ONLY_HOUSE.add("2-60-9-536");
        ONLY_HOUSE.add("2-60-9-539");
        ONLY_HOUSE.add("2-60-9-538");
        ONLY_HOUSE.add("2-60-9-533");
        ONLY_HOUSE.add("2-60-9-532");
        ONLY_HOUSE.add("2-60-9-535");
        ONLY_HOUSE.add("2-60-9-534");
        ONLY_HOUSE.add("2-60-9-520");
        ONLY_HOUSE.add("2-60-9-528");
        ONLY_HOUSE.add("2-60-9-527");
        ONLY_HOUSE.add("2-60-9-526");
        ONLY_HOUSE.add("2-60-9-525");
        ONLY_HOUSE.add("2-60-9-524");
        ONLY_HOUSE.add("2-60-9-523");
        ONLY_HOUSE.add("2-60-9-522");
        ONLY_HOUSE.add("2-60-9-521");
        ONLY_HOUSE.add("2-60-9-529");
        ONLY_HOUSE.add("44-40-5-2102");
        ONLY_HOUSE.add("2-54-154-1062");
        ONLY_HOUSE.add("2-60-9-598");
        ONLY_HOUSE.add("2-60-9-599");
        ONLY_HOUSE.add("2-60-9-590");
        ONLY_HOUSE.add("44-40-6-1012");
        ONLY_HOUSE.add("2-60-9-591");
        ONLY_HOUSE.add("2-60-9-592");
        ONLY_HOUSE.add("2-60-9-593");
        ONLY_HOUSE.add("2-60-9-594");
        ONLY_HOUSE.add("2-60-9-595");
        ONLY_HOUSE.add("2-60-9-596");
        ONLY_HOUSE.add("2-60-9-597");
        ONLY_HOUSE.add("44-40-5-2112");
        ONLY_HOUSE.add("44-40-5-2111");
        ONLY_HOUSE.add("2-60-9-589");
        ONLY_HOUSE.add("2-60-9-587");
        ONLY_HOUSE.add("2-60-9-588");
        ONLY_HOUSE.add("2-60-9-581");
        ONLY_HOUSE.add("2-60-9-582");
        ONLY_HOUSE.add("44-40-6-1022");
        ONLY_HOUSE.add("2-60-9-580");
        ONLY_HOUSE.add("2-60-9-585");
        ONLY_HOUSE.add("2-60-9-586");
        ONLY_HOUSE.add("2-60-9-583");
        ONLY_HOUSE.add("2-60-9-584");
        ONLY_HOUSE.add("44-40-5-2122");
        ONLY_HOUSE.add("2-60-9-576");
        ONLY_HOUSE.add("2-60-9-577");
        ONLY_HOUSE.add("2-60-9-578");
        ONLY_HOUSE.add("2-60-9-579");
        ONLY_HOUSE.add("2-60-9-572");
        ONLY_HOUSE.add("2-60-9-573");
        ONLY_HOUSE.add("2-60-9-574");
        ONLY_HOUSE.add("2-60-9-575");
        ONLY_HOUSE.add("2-60-9-570");
        ONLY_HOUSE.add("2-60-9-571");
        ONLY_HOUSE.add("2-54-154-1033");
        ONLY_HOUSE.add("44-40-5-2131");
        ONLY_HOUSE.add("44-40-5-2132");
        ONLY_HOUSE.add("44-51-21-29");
        ONLY_HOUSE.add("2-60-9-569");
        ONLY_HOUSE.add("2-60-9-567");
        ONLY_HOUSE.add("2-60-9-568");
        ONLY_HOUSE.add("2-60-9-565");
        ONLY_HOUSE.add("2-60-9-566");
        ONLY_HOUSE.add("2-60-9-563");
        ONLY_HOUSE.add("2-60-9-564");
        ONLY_HOUSE.add("2-60-9-561");
        ONLY_HOUSE.add("2-60-9-562");
        ONLY_HOUSE.add("2-60-9-560");
        ONLY_HOUSE.add("44-40-5-2142");
        ONLY_HOUSE.add("44-40-5-2141");
        ONLY_HOUSE.add("2-55-72-1053");
        ONLY_HOUSE.add("2-60-9-629");
        ONLY_HOUSE.add("2-60-9-628");
        ONLY_HOUSE.add("2-60-9-624");
        ONLY_HOUSE.add("2-60-9-625");
        ONLY_HOUSE.add("2-60-9-626");
        ONLY_HOUSE.add("2-60-9-627");
        ONLY_HOUSE.add("2-60-9-620");
        ONLY_HOUSE.add("2-60-9-621");
        ONLY_HOUSE.add("2-60-9-622");
        ONLY_HOUSE.add("2-60-9-623");
        ONLY_HOUSE.add("2-60-9-639");
        ONLY_HOUSE.add("2-60-9-637");
        ONLY_HOUSE.add("2-60-9-638");
        ONLY_HOUSE.add("2-60-9-635");
        ONLY_HOUSE.add("2-60-9-636");
        ONLY_HOUSE.add("2-60-9-633");
        ONLY_HOUSE.add("2-60-9-634");
        ONLY_HOUSE.add("2-60-9-631");
        ONLY_HOUSE.add("2-60-9-632");
        ONLY_HOUSE.add("2-60-9-630");
        ONLY_HOUSE.add("2-60-9-607");
        ONLY_HOUSE.add("2-60-9-606");
        ONLY_HOUSE.add("2-60-9-608");
        ONLY_HOUSE.add("2-60-9-601");
        ONLY_HOUSE.add("2-60-9-602");
        ONLY_HOUSE.add("2-60-9-603");
        ONLY_HOUSE.add("2-60-9-604");
        ONLY_HOUSE.add("2-60-9-605");
        ONLY_HOUSE.add("2-54-163-1051");
        ONLY_HOUSE.add("2-60-9-619");
        ONLY_HOUSE.add("2-60-9-611");
        ONLY_HOUSE.add("2-60-9-612");
        ONLY_HOUSE.add("2-60-9-610");
        ONLY_HOUSE.add("2-60-9-615");
        ONLY_HOUSE.add("2-60-9-616");
        ONLY_HOUSE.add("2-60-9-613");
        ONLY_HOUSE.add("2-60-9-614");
        ONLY_HOUSE.add("2-54-163-1031");
        ONLY_HOUSE.add("44-46-144-2112");
        ONLY_HOUSE.add("44-46-132-35");
        ONLY_HOUSE.add("44-46-132-34");
        ONLY_HOUSE.add("44-46-132-36");
        ONLY_HOUSE.add("44-46-2-1224");
        ONLY_HOUSE.add("44-46-144-2133");
        ONLY_HOUSE.add("44-21-20-5");
        ONLY_HOUSE.add("44-46-144-2143");
        ONLY_HOUSE.add("44-21-20-6");
        ONLY_HOUSE.add("44-21-20-8");
        ONLY_HOUSE.add("2-54-138-4042");
        ONLY_HOUSE.add("44-40-15-1063");
        ONLY_HOUSE.add("2-54-138-4031");
        ONLY_HOUSE.add("2-54-138-4032");
        ONLY_HOUSE.add("44-46-132-12");
        ONLY_HOUSE.add("44-46-132-14");
        ONLY_HOUSE.add("44-46-132-15");
        ONLY_HOUSE.add("44-46-132-11");
        ONLY_HOUSE.add("44-46-132-16");
        ONLY_HOUSE.add("44-46-132-25");
        ONLY_HOUSE.add("44-46-132-26");
        ONLY_HOUSE.add("44-46-132-24");
        ONLY_HOUSE.add("44-46-132-21");
        ONLY_HOUSE.add("2-60-9-2898");
        ONLY_HOUSE.add("2-60-9-2897");
        ONLY_HOUSE.add("2-60-9-2899");
        ONLY_HOUSE.add("44-46-99-24");
        ONLY_HOUSE.add("44-46-99-23");
        ONLY_HOUSE.add("44-46-99-26");
        ONLY_HOUSE.add("44-46-99-25");
        ONLY_HOUSE.add("2-60-9-2890");
        ONLY_HOUSE.add("2-60-9-2891");
        ONLY_HOUSE.add("2-60-9-2892");
        ONLY_HOUSE.add("2-60-9-2893");
        ONLY_HOUSE.add("2-60-9-2894");
        ONLY_HOUSE.add("2-60-9-2895");
        ONLY_HOUSE.add("2-60-9-2896");
        ONLY_HOUSE.add("44-46-99-19");
        ONLY_HOUSE.add("44-46-99-17");
        ONLY_HOUSE.add("2-54-70-3052");
        ONLY_HOUSE.add("44-46-99-13");
        ONLY_HOUSE.add("44-46-99-12");
        ONLY_HOUSE.add("44-46-99-10");
        ONLY_HOUSE.add("2-55-90-1042");
        ONLY_HOUSE.add("2-55-90-1052");
        ONLY_HOUSE.add("44-46-153-39");
        ONLY_HOUSE.add("44-46-153-40");
        ONLY_HOUSE.add("2-60-9-2853");
        ONLY_HOUSE.add("2-60-9-2854");
        ONLY_HOUSE.add("2-60-9-2855");
        ONLY_HOUSE.add("2-60-9-2856");
        ONLY_HOUSE.add("2-60-9-2857");
        ONLY_HOUSE.add("2-60-9-2858");
        ONLY_HOUSE.add("2-60-9-2859");
        ONLY_HOUSE.add("2-60-9-2850");
        ONLY_HOUSE.add("2-60-9-2852");
        ONLY_HOUSE.add("2-60-9-2851");
        ONLY_HOUSE.add("2-60-9-2866");
        ONLY_HOUSE.add("2-60-9-2867");
        ONLY_HOUSE.add("2-60-9-2864");
        ONLY_HOUSE.add("2-60-9-2865");
        ONLY_HOUSE.add("2-60-9-2868");
        ONLY_HOUSE.add("2-60-9-2869");
        ONLY_HOUSE.add("2-60-9-2863");
        ONLY_HOUSE.add("2-60-9-2862");
        ONLY_HOUSE.add("2-60-9-2861");
        ONLY_HOUSE.add("2-60-9-2860");
        ONLY_HOUSE.add("2-60-9-2879");
        ONLY_HOUSE.add("2-60-9-2875");
        ONLY_HOUSE.add("2-60-9-2876");
        ONLY_HOUSE.add("2-60-9-2877");
        ONLY_HOUSE.add("2-60-9-2878");
        ONLY_HOUSE.add("2-54-71-4053");
        ONLY_HOUSE.add("2-60-9-2872");
        ONLY_HOUSE.add("2-60-9-2871");
        ONLY_HOUSE.add("2-60-9-2874");
        ONLY_HOUSE.add("2-60-9-2873");
        ONLY_HOUSE.add("2-60-9-2870");
        ONLY_HOUSE.add("2-60-9-2888");
        ONLY_HOUSE.add("2-60-9-2889");
        ONLY_HOUSE.add("2-60-9-2886");
        ONLY_HOUSE.add("2-60-9-2887");
        ONLY_HOUSE.add("2-60-9-2885");
        ONLY_HOUSE.add("2-60-9-2884");
        ONLY_HOUSE.add("2-60-9-2883");
        ONLY_HOUSE.add("2-60-9-2882");
        ONLY_HOUSE.add("2-60-9-2881");
        ONLY_HOUSE.add("2-60-9-2880");
        ONLY_HOUSE.add("44-46-103-4");
        ONLY_HOUSE.add("2-60-9-2819");
        ONLY_HOUSE.add("2-60-9-2818");
        ONLY_HOUSE.add("2-60-9-2817");
        ONLY_HOUSE.add("2-60-9-2812");
        ONLY_HOUSE.add("2-60-9-2811");
        ONLY_HOUSE.add("2-60-9-2810");
        ONLY_HOUSE.add("2-60-9-2816");
        ONLY_HOUSE.add("2-60-9-2815");
        ONLY_HOUSE.add("2-60-9-2814");
        ONLY_HOUSE.add("2-60-9-2813");
        ONLY_HOUSE.add("2-60-9-2829");
        ONLY_HOUSE.add("2-60-9-2828");
        ONLY_HOUSE.add("2-60-9-2821");
        ONLY_HOUSE.add("44-46-136-8");
        ONLY_HOUSE.add("2-60-9-2820");
        ONLY_HOUSE.add("44-46-136-7");
        ONLY_HOUSE.add("2-60-9-2823");
        ONLY_HOUSE.add("2-60-9-2822");
        ONLY_HOUSE.add("2-60-9-2825");
        ONLY_HOUSE.add("2-60-9-2824");
        ONLY_HOUSE.add("2-60-9-2827");
        ONLY_HOUSE.add("2-60-9-2826");
        ONLY_HOUSE.add("2-60-9-2830");
        ONLY_HOUSE.add("2-60-9-2839");
        ONLY_HOUSE.add("2-60-9-2838");
        ONLY_HOUSE.add("2-60-9-2837");
        ONLY_HOUSE.add("2-60-9-2836");
        ONLY_HOUSE.add("2-60-9-2835");
        ONLY_HOUSE.add("2-60-9-2834");
        ONLY_HOUSE.add("2-60-9-2833");
        ONLY_HOUSE.add("2-60-9-2832");
        ONLY_HOUSE.add("2-60-9-2831");
        ONLY_HOUSE.add("2-60-9-2840");
        ONLY_HOUSE.add("2-60-9-2841");
        ONLY_HOUSE.add("2-60-9-2847");
        ONLY_HOUSE.add("2-60-9-2846");
        ONLY_HOUSE.add("2-60-9-2849");
        ONLY_HOUSE.add("2-60-9-2848");
        ONLY_HOUSE.add("2-60-9-2843");
        ONLY_HOUSE.add("44-46-103-9");
        ONLY_HOUSE.add("2-60-9-2842");
        ONLY_HOUSE.add("2-60-9-2845");
        ONLY_HOUSE.add("44-46-103-8");
        ONLY_HOUSE.add("2-60-9-2844");
        ONLY_HOUSE.add("44-46-153-27");
        ONLY_HOUSE.add("44-46-153-26");
        ONLY_HOUSE.add("44-46-153-28");
        ONLY_HOUSE.add("44-46-153-24");
        ONLY_HOUSE.add("44-46-153-25");
        ONLY_HOUSE.add("44-46-153-23");
        ONLY_HOUSE.add("2-54-42-8022");
        ONLY_HOUSE.add("44-40-21-5062");
        ONLY_HOUSE.add("44-40-21-5061");
        ONLY_HOUSE.add("2-60-9-2802");
        ONLY_HOUSE.add("2-60-9-2803");
        ONLY_HOUSE.add("2-60-9-2804");
        ONLY_HOUSE.add("2-60-9-2805");
        ONLY_HOUSE.add("2-60-9-2801");
        ONLY_HOUSE.add("2-60-9-2806");
        ONLY_HOUSE.add("2-60-9-2807");
        ONLY_HOUSE.add("2-60-9-2808");
        ONLY_HOUSE.add("2-60-9-2809");
        ONLY_HOUSE.add("2-60-9-4207");
        ONLY_HOUSE.add("2-60-9-4206");
        ONLY_HOUSE.add("44-40-24-2032");
        ONLY_HOUSE.add("2-60-9-4209");
        ONLY_HOUSE.add("2-60-9-4208");
        ONLY_HOUSE.add("2-60-9-4202");
        ONLY_HOUSE.add("2-60-9-4203");
        ONLY_HOUSE.add("2-60-9-4204");
        ONLY_HOUSE.add("2-60-9-4205");
        ONLY_HOUSE.add("2-60-9-4201");
        ONLY_HOUSE.add("2-56-385-14");
        ONLY_HOUSE.add("2-56-385-13");
        ONLY_HOUSE.add("2-56-385-15");
        ONLY_HOUSE.add("2-56-385-10");
        ONLY_HOUSE.add("2-56-385-11");
        ONLY_HOUSE.add("2-56-385-12");
        ONLY_HOUSE.add("2-55-101-3062");
        ONLY_HOUSE.add("2-60-9-4229");
        ONLY_HOUSE.add("2-60-9-4228");
        ONLY_HOUSE.add("44-40-19-5");
        ONLY_HOUSE.add("44-40-19-4");
        ONLY_HOUSE.add("2-60-9-4221");
        ONLY_HOUSE.add("2-60-9-4222");
        ONLY_HOUSE.add("2-60-9-4223");
        ONLY_HOUSE.add("2-60-9-4224");
        ONLY_HOUSE.add("2-60-9-4225");
        ONLY_HOUSE.add("2-60-9-4226");
        ONLY_HOUSE.add("2-60-9-4227");
        ONLY_HOUSE.add("2-60-9-4210");
        ONLY_HOUSE.add("2-55-65-1061");
        ONLY_HOUSE.add("44-50-12");
        ONLY_HOUSE.add("44-50-11");
        ONLY_HOUSE.add("44-50-10");
        ONLY_HOUSE.add("2-54-174-11");
        ONLY_HOUSE.add("2-54-174-18");
        ONLY_HOUSE.add("44-40-24-3172");
        ONLY_HOUSE.add("44-46-104-5");
        ONLY_HOUSE.add("44-46-3-1081");
        ONLY_HOUSE.add("44-40-5-1113");
        ONLY_HOUSE.add("44-22-15");
        ONLY_HOUSE.add("44-40-5-1153");
        ONLY_HOUSE.add("2-60-9-4236");
        ONLY_HOUSE.add("2-60-9-4235");
        ONLY_HOUSE.add("2-60-9-4238");
        ONLY_HOUSE.add("2-60-9-4237");
        ONLY_HOUSE.add("2-60-9-4232");
        ONLY_HOUSE.add("2-60-9-4231");
        ONLY_HOUSE.add("2-60-9-4234");
        ONLY_HOUSE.add("2-60-9-4233");
        ONLY_HOUSE.add("2-60-9-4230");
        ONLY_HOUSE.add("2-60-9-4239");
        ONLY_HOUSE.add("44-40-5-1172");
        ONLY_HOUSE.add("44-40-5-1173");
        ONLY_HOUSE.add("44-40-24-3101");
        ONLY_HOUSE.add("44-46-114-22");
        ONLY_HOUSE.add("44-46-114-20");
        ONLY_HOUSE.add("44-46-19-4");
        ONLY_HOUSE.add("2-60-9-4250");
        ONLY_HOUSE.add("2-60-9-4251");
        ONLY_HOUSE.add("44-46-114-13");
        ONLY_HOUSE.add("44-46-114-17");
        ONLY_HOUSE.add("44-46-114-19");
        ONLY_HOUSE.add("44-40-5-1163");
        ONLY_HOUSE.add("2-54-70-5042");
        ONLY_HOUSE.add("2-55-91-2042");
        ONLY_HOUSE.add("2-54-180-1032");
        ONLY_HOUSE.add("2-60-9-2996");
        ONLY_HOUSE.add("2-60-9-2997");
        ONLY_HOUSE.add("2-60-9-2998");
        ONLY_HOUSE.add("2-60-9-2999");
        ONLY_HOUSE.add("2-60-9-2993");
        ONLY_HOUSE.add("2-60-9-2992");
        ONLY_HOUSE.add("2-60-9-2995");
        ONLY_HOUSE.add("2-60-9-2994");
        ONLY_HOUSE.add("2-60-9-2991");
        ONLY_HOUSE.add("2-60-9-2990");
        ONLY_HOUSE.add("44-46-125-10");
        ONLY_HOUSE.add("2-60-9-2974");
        ONLY_HOUSE.add("2-60-9-2975");
        ONLY_HOUSE.add("2-60-9-2976");
        ONLY_HOUSE.add("2-55-115-4021");
        ONLY_HOUSE.add("2-60-9-2977");
        ONLY_HOUSE.add("2-60-9-2978");
        ONLY_HOUSE.add("44-46-125-19");
        ONLY_HOUSE.add("2-60-9-2979");
        ONLY_HOUSE.add("44-46-125-18");
        ONLY_HOUSE.add("44-46-125-16");
        ONLY_HOUSE.add("2-60-9-4193");
        ONLY_HOUSE.add("2-60-9-4192");
        ONLY_HOUSE.add("2-60-9-4191");
        ONLY_HOUSE.add("2-60-9-4190");
        ONLY_HOUSE.add("2-60-9-4197");
        ONLY_HOUSE.add("2-60-9-2971");
        ONLY_HOUSE.add("2-60-9-4196");
        ONLY_HOUSE.add("2-60-9-2970");
        ONLY_HOUSE.add("2-60-9-2973");
        ONLY_HOUSE.add("2-60-9-4195");
        ONLY_HOUSE.add("2-60-9-2972");
        ONLY_HOUSE.add("2-60-9-4194");
        ONLY_HOUSE.add("2-60-9-4199");
        ONLY_HOUSE.add("2-60-9-4198");
        ONLY_HOUSE.add("2-54-34-1");
        ONLY_HOUSE.add("2-60-9-2987");
        ONLY_HOUSE.add("2-60-9-2988");
        ONLY_HOUSE.add("2-60-9-2985");
        ONLY_HOUSE.add("2-60-9-2986");
        ONLY_HOUSE.add("2-60-9-2989");
        ONLY_HOUSE.add("2-60-9-2980");
        ONLY_HOUSE.add("2-60-9-2984");
        ONLY_HOUSE.add("2-60-9-2983");
        ONLY_HOUSE.add("2-60-9-2982");
        ONLY_HOUSE.add("2-60-9-2981");
        ONLY_HOUSE.add("2-60-9-2950");
        ONLY_HOUSE.add("44-46-135-6");
        ONLY_HOUSE.add("44-46-102-4");
        ONLY_HOUSE.add("2-60-9-2951");
        ONLY_HOUSE.add("44-46-102-7");
        ONLY_HOUSE.add("44-46-102-6");
        ONLY_HOUSE.add("44-46-135-5");
        ONLY_HOUSE.add("44-46-102-1");
        ONLY_HOUSE.add("44-46-102-2");
        ONLY_HOUSE.add("2-60-9-2959");
        ONLY_HOUSE.add("2-60-9-2958");
        ONLY_HOUSE.add("2-60-9-2957");
        ONLY_HOUSE.add("2-60-9-2956");
        ONLY_HOUSE.add("2-60-9-2955");
        ONLY_HOUSE.add("2-60-9-2954");
        ONLY_HOUSE.add("2-60-9-2953");
        ONLY_HOUSE.add("2-60-9-2952");
        ONLY_HOUSE.add("2-60-9-2960");
        ONLY_HOUSE.add("2-60-9-2961");
        ONLY_HOUSE.add("2-60-9-2962");
        ONLY_HOUSE.add("2-60-9-2968");
        ONLY_HOUSE.add("2-60-9-2967");
        ONLY_HOUSE.add("2-60-9-2969");
        ONLY_HOUSE.add("44-46-125-28");
        ONLY_HOUSE.add("44-46-135-9");
        ONLY_HOUSE.add("2-60-9-2964");
        ONLY_HOUSE.add("44-46-125-25");
        ONLY_HOUSE.add("44-46-125-26");
        ONLY_HOUSE.add("44-46-135-8");
        ONLY_HOUSE.add("2-60-9-2963");
        ONLY_HOUSE.add("44-46-125-23");
        ONLY_HOUSE.add("2-60-9-2966");
        ONLY_HOUSE.add("44-46-125-24");
        ONLY_HOUSE.add("2-60-9-2965");
        ONLY_HOUSE.add("44-46-125-21");
        ONLY_HOUSE.add("44-46-125-22");
        ONLY_HOUSE.add("2-60-9-2933");
        ONLY_HOUSE.add("2-60-9-2932");
        ONLY_HOUSE.add("2-60-9-2931");
        ONLY_HOUSE.add("2-60-9-2930");
        ONLY_HOUSE.add("2-55-76-2011");
        ONLY_HOUSE.add("2-60-9-2937");
        ONLY_HOUSE.add("2-60-9-2936");
        ONLY_HOUSE.add("2-60-9-2935");
        ONLY_HOUSE.add("2-60-9-2934");
        ONLY_HOUSE.add("2-60-9-2939");
        ONLY_HOUSE.add("2-60-9-2938");
        ONLY_HOUSE.add("2-60-9-2940");
        ONLY_HOUSE.add("2-60-9-2942");
        ONLY_HOUSE.add("2-60-9-2941");
        ONLY_HOUSE.add("2-60-9-2944");
        ONLY_HOUSE.add("2-55-76-2021");
        ONLY_HOUSE.add("2-60-9-2943");
        ONLY_HOUSE.add("2-60-9-2946");
        ONLY_HOUSE.add("2-60-9-2945");
        ONLY_HOUSE.add("2-60-9-2948");
        ONLY_HOUSE.add("2-60-9-2947");
        ONLY_HOUSE.add("2-60-9-2949");
        ONLY_HOUSE.add("2-55-97-1062");
        ONLY_HOUSE.add("2-60-9-2918");
        ONLY_HOUSE.add("2-60-9-2919");
        ONLY_HOUSE.add("2-60-9-2916");
        ONLY_HOUSE.add("2-60-9-2917");
        ONLY_HOUSE.add("2-60-9-2914");
        ONLY_HOUSE.add("2-60-9-2915");
        ONLY_HOUSE.add("2-60-9-2912");
        ONLY_HOUSE.add("2-60-9-2913");
        ONLY_HOUSE.add("2-60-9-2910");
        ONLY_HOUSE.add("2-60-9-2911");
        ONLY_HOUSE.add("2-55-72-4063");
        ONLY_HOUSE.add("2-60-9-2927");
        ONLY_HOUSE.add("2-60-9-2928");
        ONLY_HOUSE.add("2-60-9-2929");
        ONLY_HOUSE.add("2-60-9-2923");
        ONLY_HOUSE.add("2-60-9-2924");
        ONLY_HOUSE.add("2-60-9-2925");
        ONLY_HOUSE.add("2-60-9-2926");
        ONLY_HOUSE.add("2-55-72-4053");
        ONLY_HOUSE.add("2-60-9-2920");
        ONLY_HOUSE.add("2-60-9-2921");
        ONLY_HOUSE.add("2-55-76-2042");
        ONLY_HOUSE.add("2-60-9-2922");
        ONLY_HOUSE.add("44-40-21-3062");
        ONLY_HOUSE.add("44-46-120-4");
        ONLY_HOUSE.add("44-46-120-2");
        ONLY_HOUSE.add("44-46-120-1");
        ONLY_HOUSE.add("2-60-9-2905");
        ONLY_HOUSE.add("2-60-9-2906");
        ONLY_HOUSE.add("2-60-9-2907");
        ONLY_HOUSE.add("2-60-9-2908");
        ONLY_HOUSE.add("2-60-9-2909");
        ONLY_HOUSE.add("44-46-102-8");
        ONLY_HOUSE.add("2-60-9-2901");
        ONLY_HOUSE.add("2-60-9-2902");
        ONLY_HOUSE.add("2-60-9-2903");
        ONLY_HOUSE.add("2-60-9-2904");
        ONLY_HOUSE.add("2-60-9-4108");
        ONLY_HOUSE.add("2-60-9-4107");
        ONLY_HOUSE.add("2-60-9-4109");
        ONLY_HOUSE.add("2-60-9-4101");
        ONLY_HOUSE.add("2-60-9-4102");
        ONLY_HOUSE.add("2-60-9-4103");
        ONLY_HOUSE.add("2-60-9-4104");
        ONLY_HOUSE.add("2-60-9-4105");
        ONLY_HOUSE.add("2-60-9-4106");
        ONLY_HOUSE.add("44-46-115-16");
        ONLY_HOUSE.add("2-55-90-2063");
        ONLY_HOUSE.add("2-55-90-2061");
        ONLY_HOUSE.add("44-46-115-12");
        ONLY_HOUSE.add("44-46-115-11");
        ONLY_HOUSE.add("2-55-90-2053");
        ONLY_HOUSE.add("44-40-24-3031");
        ONLY_HOUSE.add("744-61-1");
        ONLY_HOUSE.add("744-61-2");
        ONLY_HOUSE.add("744-61-3");
        ONLY_HOUSE.add("2-55-90-2031");
        ONLY_HOUSE.add("44-46-115-20");
        ONLY_HOUSE.add("44-46-115-21");
        ONLY_HOUSE.add("44-46-115-22");
        ONLY_HOUSE.add("44-46-115-26");
        ONLY_HOUSE.add("44-51-45-31");
        ONLY_HOUSE.add("44-51-45-32");
        ONLY_HOUSE.add("44-46-3-1171");
        ONLY_HOUSE.add("2-60-9-4189");
        ONLY_HOUSE.add("2-60-9-4187");
        ONLY_HOUSE.add("2-60-9-4188");
        ONLY_HOUSE.add("2-60-9-4181");
        ONLY_HOUSE.add("2-54-116-1083");
        ONLY_HOUSE.add("44-46-3-1181");
        ONLY_HOUSE.add("2-60-9-4182");
        ONLY_HOUSE.add("2-60-9-4180");
        ONLY_HOUSE.add("2-60-9-4185");
        ONLY_HOUSE.add("2-60-9-4186");
        ONLY_HOUSE.add("2-60-9-4183");
        ONLY_HOUSE.add("2-60-9-4184");
        ONLY_HOUSE.add("2-60-9-4176");
        ONLY_HOUSE.add("2-60-9-4177");
        ONLY_HOUSE.add("2-60-9-4178");
        ONLY_HOUSE.add("2-60-9-4179");
        ONLY_HOUSE.add("2-60-9-4171");
        ONLY_HOUSE.add("2-60-9-4172");
        ONLY_HOUSE.add("2-60-9-4173");
        ONLY_HOUSE.add("2-60-9-4174");
        ONLY_HOUSE.add("2-60-9-4175");
        ONLY_HOUSE.add("2-60-9-4167");
        ONLY_HOUSE.add("2-60-9-4166");
        ONLY_HOUSE.add("2-60-9-4163");
        ONLY_HOUSE.add("2-60-9-4162");
        ONLY_HOUSE.add("2-60-9-4158");
        ONLY_HOUSE.add("2-60-9-4154");
        ONLY_HOUSE.add("2-60-9-4155");
        ONLY_HOUSE.add("2-60-9-4156");
        ONLY_HOUSE.add("2-60-9-4157");
        ONLY_HOUSE.add("2-60-9-4150");
        ONLY_HOUSE.add("2-60-9-4151");
        ONLY_HOUSE.add("2-60-9-4152");
        ONLY_HOUSE.add("2-60-9-4153");
        ONLY_HOUSE.add("2-60-9-4146");
        ONLY_HOUSE.add("2-60-9-4145");
        ONLY_HOUSE.add("2-60-9-4144");
        ONLY_HOUSE.add("2-60-9-4147");
        ONLY_HOUSE.add("2-55-104-4033");
        ONLY_HOUSE.add("2-55-105-2042");
        ONLY_HOUSE.add("2-60-9-4120");
        ONLY_HOUSE.add("2-60-9-4127");
        ONLY_HOUSE.add("2-60-9-4126");
        ONLY_HOUSE.add("2-60-9-4125");
        ONLY_HOUSE.add("2-60-9-4124");
        ONLY_HOUSE.add("2-60-9-4121");
        ONLY_HOUSE.add("2-60-9-4115");
        ONLY_HOUSE.add("2-60-9-4114");
        ONLY_HOUSE.add("2-60-9-4116");
        ONLY_HOUSE.add("2-60-9-4111");
        ONLY_HOUSE.add("2-60-9-4110");
        ONLY_HOUSE.add("2-60-9-4113");
        ONLY_HOUSE.add("2-60-9-4112");
        ONLY_HOUSE.add("2-55-104-4052");
        ONLY_HOUSE.add("2-60-9-4119");
        ONLY_HOUSE.add("2-60-9-4079");
        ONLY_HOUSE.add("2-60-9-4072");
        ONLY_HOUSE.add("44-46-134-2");
        ONLY_HOUSE.add("2-60-9-4071");
        ONLY_HOUSE.add("44-46-134-1");
        ONLY_HOUSE.add("44-46-134-4");
        ONLY_HOUSE.add("2-60-9-4070");
        ONLY_HOUSE.add("2-60-9-4076");
        ONLY_HOUSE.add("44-46-134-6");
        ONLY_HOUSE.add("2-60-9-4075");
        ONLY_HOUSE.add("44-46-134-8");
        ONLY_HOUSE.add("2-60-9-4074");
        ONLY_HOUSE.add("2-60-9-4073");
        ONLY_HOUSE.add("44-46-134-7");
        ONLY_HOUSE.add("44-46-141-1171");
        ONLY_HOUSE.add("44-46-141-1173");
        ONLY_HOUSE.add("44-46-141-1172");
        ONLY_HOUSE.add("44-40-18-3072");
        ONLY_HOUSE.add("44-46-141-1174");
        ONLY_HOUSE.add("2-60-9-4089");
        ONLY_HOUSE.add("2-60-9-4088");
        ONLY_HOUSE.add("2-60-9-4081");
        ONLY_HOUSE.add("2-60-9-4080");
        ONLY_HOUSE.add("2-60-9-4083");
        ONLY_HOUSE.add("2-60-9-4082");
        ONLY_HOUSE.add("2-60-9-4085");
        ONLY_HOUSE.add("2-60-9-4084");
        ONLY_HOUSE.add("2-60-9-4087");
        ONLY_HOUSE.add("2-60-9-4086");
        ONLY_HOUSE.add("44-46-134-9");
        ONLY_HOUSE.add("44-46-141-1182");
        ONLY_HOUSE.add("44-46-141-1181");
        ONLY_HOUSE.add("2-60-9-4090");
        ONLY_HOUSE.add("44-40-18-3062");
        ONLY_HOUSE.add("44-46-141-1184");
        ONLY_HOUSE.add("44-46-141-1183");
        ONLY_HOUSE.add("2-60-9-4099");
        ONLY_HOUSE.add("2-60-9-4098");
        ONLY_HOUSE.add("2-60-9-4097");
        ONLY_HOUSE.add("2-60-9-4096");
        ONLY_HOUSE.add("2-60-9-4095");
        ONLY_HOUSE.add("2-60-9-4094");
        ONLY_HOUSE.add("2-60-9-4093");
        ONLY_HOUSE.add("2-60-9-4092");
        ONLY_HOUSE.add("2-60-9-4091");
        ONLY_HOUSE.add("44-46-141-1191");
        ONLY_HOUSE.add("44-46-141-1193");
        ONLY_HOUSE.add("44-46-141-1192");
        ONLY_HOUSE.add("44-40-18-3052");
        ONLY_HOUSE.add("44-46-141-1194");
        ONLY_HOUSE.add("44-46-123-5");
        ONLY_HOUSE.add("44-46-123-6");
        ONLY_HOUSE.add("44-46-123-7");
        ONLY_HOUSE.add("44-46-123-1");
        ONLY_HOUSE.add("44-46-123-2");
        ONLY_HOUSE.add("2-60-9-2648");
        ONLY_HOUSE.add("2-60-9-2649");
        ONLY_HOUSE.add("2-60-9-2647");
        ONLY_HOUSE.add("2-60-9-2650");
        ONLY_HOUSE.add("2-60-9-2651");
        ONLY_HOUSE.add("2-60-9-2652");
        ONLY_HOUSE.add("2-60-9-2653");
        ONLY_HOUSE.add("2-60-9-2654");
        ONLY_HOUSE.add("44-40-18-3031");
        ONLY_HOUSE.add("44-40-18-3032");
        ONLY_HOUSE.add("2-60-9-2656");
        ONLY_HOUSE.add("2-60-9-2655");
        ONLY_HOUSE.add("2-60-9-2658");
        ONLY_HOUSE.add("2-60-9-2657");
        ONLY_HOUSE.add("44-51-44-7");
        ONLY_HOUSE.add("2-60-9-2659");
        ONLY_HOUSE.add("2-60-9-2660");
        ONLY_HOUSE.add("2-60-9-2661");
        ONLY_HOUSE.add("2-60-9-2664");
        ONLY_HOUSE.add("2-60-9-2665");
        ONLY_HOUSE.add("2-60-9-2662");
        ONLY_HOUSE.add("2-60-9-2663");
        ONLY_HOUSE.add("2-60-9-2669");
        ONLY_HOUSE.add("2-60-9-2668");
        ONLY_HOUSE.add("2-60-9-2667");
        ONLY_HOUSE.add("2-60-9-2666");
        ONLY_HOUSE.add("2-60-9-2673");
        ONLY_HOUSE.add("2-60-9-2674");
        ONLY_HOUSE.add("2-60-9-2675");
        ONLY_HOUSE.add("2-60-9-2676");
        ONLY_HOUSE.add("2-60-9-2670");
        ONLY_HOUSE.add("2-60-9-2671");
        ONLY_HOUSE.add("2-60-9-2672");
        ONLY_HOUSE.add("2-60-9-2678");
        ONLY_HOUSE.add("2-60-9-2677");
        ONLY_HOUSE.add("2-60-9-2679");
        ONLY_HOUSE.add("2-60-9-2686");
        ONLY_HOUSE.add("2-60-9-2687");
        ONLY_HOUSE.add("2-60-9-2684");
        ONLY_HOUSE.add("2-60-9-2685");
        ONLY_HOUSE.add("2-60-9-2682");
        ONLY_HOUSE.add("2-60-9-2683");
        ONLY_HOUSE.add("2-60-9-2680");
        ONLY_HOUSE.add("2-60-9-2681");
        ONLY_HOUSE.add("2-60-9-2689");
        ONLY_HOUSE.add("2-60-9-2688");
        ONLY_HOUSE.add("2-60-9-1579");
        ONLY_HOUSE.add("2-60-9-1578");
        ONLY_HOUSE.add("2-60-9-1577");
        ONLY_HOUSE.add("2-60-9-1576");
        ONLY_HOUSE.add("2-60-9-1589");
        ONLY_HOUSE.add("2-60-9-1588");
        ONLY_HOUSE.add("2-54-176-2102");
        ONLY_HOUSE.add("2-60-9-1584");
        ONLY_HOUSE.add("2-60-9-1585");
        ONLY_HOUSE.add("2-60-9-1586");
        ONLY_HOUSE.add("2-60-9-1587");
        ONLY_HOUSE.add("2-60-9-1580");
        ONLY_HOUSE.add("2-60-9-1581");
        ONLY_HOUSE.add("2-60-9-1582");
        ONLY_HOUSE.add("2-60-9-1583");
        ONLY_HOUSE.add("2-60-9-1599");
        ONLY_HOUSE.add("2-60-9-1590");
        ONLY_HOUSE.add("2-60-9-1597");
        ONLY_HOUSE.add("2-60-9-1598");
        ONLY_HOUSE.add("2-60-9-1595");
        ONLY_HOUSE.add("2-60-9-1596");
        ONLY_HOUSE.add("2-60-9-1593");
        ONLY_HOUSE.add("2-60-9-1594");
        ONLY_HOUSE.add("2-60-9-1591");
        ONLY_HOUSE.add("2-60-9-1592");
        ONLY_HOUSE.add("44-40-21-1032");
        ONLY_HOUSE.add("44-46-123-9");
        ONLY_HOUSE.add("2-54-151-4012");
        ONLY_HOUSE.add("44-52-20");
        ONLY_HOUSE.add("44-52-21");
        ONLY_HOUSE.add("44-52-22");
        ONLY_HOUSE.add("44-52-18");
        ONLY_HOUSE.add("44-52-19");
        ONLY_HOUSE.add("2-54-151-4031");
        ONLY_HOUSE.add("44-52-16");
        ONLY_HOUSE.add("44-52-17");
        ONLY_HOUSE.add("44-40-18-3082");
        ONLY_HOUSE.add("44-52-10");
        ONLY_HOUSE.add("44-52-11");
        ONLY_HOUSE.add("2-54-125-3011");
        ONLY_HOUSE.add("44-52-14");
        ONLY_HOUSE.add("44-52-15");
        ONLY_HOUSE.add("44-52-12");
        ONLY_HOUSE.add("44-52-13");
        ONLY_HOUSE.add("2-54-109-1031");
        ONLY_HOUSE.add("44-46-106-7");
        ONLY_HOUSE.add("44-46-106-6");
        ONLY_HOUSE.add("2-54-38-2063");
        ONLY_HOUSE.add("2-54-68-2012");
        ONLY_HOUSE.add("2-54-109-1061");
        ONLY_HOUSE.add("2-54-72-4031");
        ONLY_HOUSE.add("2-54-65-3011");
        ONLY_HOUSE.add("44-37-13");
        ONLY_HOUSE.add("44-37-14");
        ONLY_HOUSE.add("2-55-89-3043");
        ONLY_HOUSE.add("44-37-25");
        ONLY_HOUSE.add("2-54-176-2042");
        ONLY_HOUSE.add("2-55-89-3032");
        ONLY_HOUSE.add("44-37-17");
        ONLY_HOUSE.add("44-37-30");
        ONLY_HOUSE.add("44-46-118-27");
        ONLY_HOUSE.add("44-46-141-1201");
        ONLY_HOUSE.add("44-46-118-23");
        ONLY_HOUSE.add("44-46-118-25");
        ONLY_HOUSE.add("44-46-118-26");
        ONLY_HOUSE.add("44-37-29");
        ONLY_HOUSE.add("44-37-28");
        ONLY_HOUSE.add("44-46-118-20");
        ONLY_HOUSE.add("44-37-27");
        ONLY_HOUSE.add("44-46-118-21");
        ONLY_HOUSE.add("44-37-26");
        ONLY_HOUSE.add("44-46-118-22");
        ONLY_HOUSE.add("44-46-106-2");
        ONLY_HOUSE.add("44-46-3-1235");
        ONLY_HOUSE.add("44-46-118-18");
        ONLY_HOUSE.add("44-46-118-19");
        ONLY_HOUSE.add("44-46-118-11");
        ONLY_HOUSE.add("44-46-141-1221");
        ONLY_HOUSE.add("44-46-141-1223");
        ONLY_HOUSE.add("44-46-141-1222");
        ONLY_HOUSE.add("2-60-9-4008");
        ONLY_HOUSE.add("2-60-9-4009");
        ONLY_HOUSE.add("2-60-9-4007");
        ONLY_HOUSE.add("2-60-9-4006");
        ONLY_HOUSE.add("2-60-9-4005");
        ONLY_HOUSE.add("2-60-9-4004");
        ONLY_HOUSE.add("2-60-9-4003");
        ONLY_HOUSE.add("44-46-141-1213");
        ONLY_HOUSE.add("2-60-9-4002");
        ONLY_HOUSE.add("44-46-141-1214");
        ONLY_HOUSE.add("2-60-9-4001");
        ONLY_HOUSE.add("44-46-141-1212");
        ONLY_HOUSE.add("44-46-141-1211");
        ONLY_HOUSE.add("44-46-141-1204");
        ONLY_HOUSE.add("44-40-18-3102");
        ONLY_HOUSE.add("44-46-141-1202");
        ONLY_HOUSE.add("44-46-141-1203");
        ONLY_HOUSE.add("44-21-13-13");
        ONLY_HOUSE.add("44-46-141-1244");
        ONLY_HOUSE.add("2-60-9-4025");
        ONLY_HOUSE.add("2-60-9-4024");
        ONLY_HOUSE.add("2-60-9-4023");
        ONLY_HOUSE.add("2-60-9-4022");
        ONLY_HOUSE.add("2-60-9-4029");
        ONLY_HOUSE.add("2-60-9-4028");
        ONLY_HOUSE.add("2-60-9-4027");
        ONLY_HOUSE.add("2-60-9-4026");
        ONLY_HOUSE.add("2-60-9-4021");
        ONLY_HOUSE.add("2-60-9-4020");
        ONLY_HOUSE.add("2-60-9-4019");
        ONLY_HOUSE.add("44-46-141-1234");
        ONLY_HOUSE.add("44-46-141-1233");
        ONLY_HOUSE.add("44-46-141-1232");
        ONLY_HOUSE.add("44-46-141-1231");
        ONLY_HOUSE.add("2-60-9-4012");
        ONLY_HOUSE.add("2-60-9-4011");
        ONLY_HOUSE.add("2-60-9-4014");
        ONLY_HOUSE.add("44-46-141-1224");
        ONLY_HOUSE.add("2-60-9-4013");
        ONLY_HOUSE.add("2-60-9-4016");
        ONLY_HOUSE.add("2-60-9-4015");
        ONLY_HOUSE.add("2-60-9-4018");
        ONLY_HOUSE.add("2-60-9-4017");
        ONLY_HOUSE.add("2-60-9-4010");
        ONLY_HOUSE.add("2-60-9-4042");
        ONLY_HOUSE.add("2-60-9-4043");
        ONLY_HOUSE.add("2-60-9-4040");
        ONLY_HOUSE.add("2-60-9-4041");
        ONLY_HOUSE.add("2-60-9-4048");
        ONLY_HOUSE.add("2-60-9-4049");
        ONLY_HOUSE.add("2-60-9-4046");
        ONLY_HOUSE.add("2-60-9-4047");
        ONLY_HOUSE.add("2-60-9-4044");
        ONLY_HOUSE.add("2-60-9-4045");
        ONLY_HOUSE.add("2-60-9-4030");
        ONLY_HOUSE.add("2-60-9-4031");
        ONLY_HOUSE.add("2-60-9-4032");
        ONLY_HOUSE.add("2-60-9-4037");
        ONLY_HOUSE.add("2-60-9-4038");
        ONLY_HOUSE.add("2-60-9-4039");
        ONLY_HOUSE.add("2-60-9-4033");
        ONLY_HOUSE.add("2-60-9-4034");
        ONLY_HOUSE.add("2-55-88-3022");
        ONLY_HOUSE.add("2-60-9-4035");
        ONLY_HOUSE.add("2-60-9-4036");
        ONLY_HOUSE.add("2-60-9-4060");
        ONLY_HOUSE.add("2-60-9-4061");
        ONLY_HOUSE.add("2-60-9-4064");
        ONLY_HOUSE.add("2-60-9-4065");
        ONLY_HOUSE.add("2-60-9-4062");
        ONLY_HOUSE.add("2-60-9-4063");
        ONLY_HOUSE.add("2-60-9-4068");
        ONLY_HOUSE.add("2-60-9-4069");
        ONLY_HOUSE.add("2-60-9-4066");
        ONLY_HOUSE.add("2-60-9-4067");
        ONLY_HOUSE.add("2-54-109-1012");
        ONLY_HOUSE.add("2-54-109-1011");
        ONLY_HOUSE.add("2-54-65-3051");
        ONLY_HOUSE.add("2-60-9-4050");
        ONLY_HOUSE.add("2-60-9-4051");
        ONLY_HOUSE.add("2-60-9-4052");
        ONLY_HOUSE.add("2-60-9-4053");
        ONLY_HOUSE.add("2-60-9-4054");
        ONLY_HOUSE.add("2-60-9-4055");
        ONLY_HOUSE.add("2-60-9-4056");
        ONLY_HOUSE.add("2-60-9-4057");
        ONLY_HOUSE.add("2-60-9-4058");
        ONLY_HOUSE.add("2-60-9-4059");
        ONLY_HOUSE.add("2-60-9-1620");
        ONLY_HOUSE.add("44-46-133-6");
        ONLY_HOUSE.add("44-46-133-9");
        ONLY_HOUSE.add("44-46-133-8");
        ONLY_HOUSE.add("44-46-133-2");
        ONLY_HOUSE.add("44-46-133-5");
        ONLY_HOUSE.add("44-46-133-4");
        ONLY_HOUSE.add("44-46-133-1");
        ONLY_HOUSE.add("2-55-85-1012");
        ONLY_HOUSE.add("44-46-141-1074");
        ONLY_HOUSE.add("44-40-18-1031");
        ONLY_HOUSE.add("44-46-141-1073");
        ONLY_HOUSE.add("2-60-9-1629");
        ONLY_HOUSE.add("44-40-18-3172");
        ONLY_HOUSE.add("2-60-9-1628");
        ONLY_HOUSE.add("2-60-9-2758");
        ONLY_HOUSE.add("2-60-9-2759");
        ONLY_HOUSE.add("2-60-9-1627");
        ONLY_HOUSE.add("44-46-141-1072");
        ONLY_HOUSE.add("2-60-9-1626");
        ONLY_HOUSE.add("44-46-141-1071");
        ONLY_HOUSE.add("2-60-9-1625");
        ONLY_HOUSE.add("2-60-9-1624");
        ONLY_HOUSE.add("2-60-9-1623");
        ONLY_HOUSE.add("2-60-9-2755");
        ONLY_HOUSE.add("2-60-9-2756");
        ONLY_HOUSE.add("2-60-9-1622");
        ONLY_HOUSE.add("2-60-9-1621");
        ONLY_HOUSE.add("2-60-9-2757");
        ONLY_HOUSE.add("2-60-9-2764");
        ONLY_HOUSE.add("2-60-9-2763");
        ONLY_HOUSE.add("2-60-9-1630");
        ONLY_HOUSE.add("2-60-9-2762");
        ONLY_HOUSE.add("2-60-9-1631");
        ONLY_HOUSE.add("2-60-9-2761");
        ONLY_HOUSE.add("2-60-9-2760");
        ONLY_HOUSE.add("44-40-18-3162");
        ONLY_HOUSE.add("44-46-141-1084");
        ONLY_HOUSE.add("2-60-9-1637");
        ONLY_HOUSE.add("44-46-141-1083");
        ONLY_HOUSE.add("44-46-141-1082");
        ONLY_HOUSE.add("2-60-9-1636");
        ONLY_HOUSE.add("44-46-141-1081");
        ONLY_HOUSE.add("2-60-9-1639");
        ONLY_HOUSE.add("2-60-9-2769");
        ONLY_HOUSE.add("2-60-9-1638");
        ONLY_HOUSE.add("2-60-9-2767");
        ONLY_HOUSE.add("2-60-9-1633");
        ONLY_HOUSE.add("2-60-9-2768");
        ONLY_HOUSE.add("2-60-9-1632");
        ONLY_HOUSE.add("2-55-77-5021");
        ONLY_HOUSE.add("2-60-9-1635");
        ONLY_HOUSE.add("2-60-9-2765");
        ONLY_HOUSE.add("2-60-9-1634");
        ONLY_HOUSE.add("2-60-9-2766");
        ONLY_HOUSE.add("2-60-9-2731");
        ONLY_HOUSE.add("2-60-9-2730");
        ONLY_HOUSE.add("44-46-141-1052");
        ONLY_HOUSE.add("2-60-9-1609");
        ONLY_HOUSE.add("44-46-141-1051");
        ONLY_HOUSE.add("44-46-141-1054");
        ONLY_HOUSE.add("2-60-9-1608");
        ONLY_HOUSE.add("2-60-9-1607");
        ONLY_HOUSE.add("44-46-141-1053");
        ONLY_HOUSE.add("2-60-9-2732");
        ONLY_HOUSE.add("2-60-9-1602");
        ONLY_HOUSE.add("2-60-9-2733");
        ONLY_HOUSE.add("2-60-9-1601");
        ONLY_HOUSE.add("2-60-9-2734");
        ONLY_HOUSE.add("2-60-9-2735");
        ONLY_HOUSE.add("2-60-9-1606");
        ONLY_HOUSE.add("2-60-9-2736");
        ONLY_HOUSE.add("2-60-9-2737");
        ONLY_HOUSE.add("2-60-9-1605");
        ONLY_HOUSE.add("2-60-9-2738");
        ONLY_HOUSE.add("2-60-9-1604");
        ONLY_HOUSE.add("2-60-9-2739");
        ONLY_HOUSE.add("2-60-9-1603");
        ONLY_HOUSE.add("2-60-9-2742");
        ONLY_HOUSE.add("2-60-9-2741");
        ONLY_HOUSE.add("2-60-9-2740");
        ONLY_HOUSE.add("2-60-9-1619");
        ONLY_HOUSE.add("2-60-9-1618");
        ONLY_HOUSE.add("44-46-141-1064");
        ONLY_HOUSE.add("44-46-141-1063");
        ONLY_HOUSE.add("44-46-141-1062");
        ONLY_HOUSE.add("2-60-9-1611");
        ONLY_HOUSE.add("2-60-9-2745");
        ONLY_HOUSE.add("2-60-9-1610");
        ONLY_HOUSE.add("2-60-9-2743");
        ONLY_HOUSE.add("2-60-9-1613");
        ONLY_HOUSE.add("2-60-9-1612");
        ONLY_HOUSE.add("2-60-9-2744");
        ONLY_HOUSE.add("2-60-9-1615");
        ONLY_HOUSE.add("44-46-141-1061");
        ONLY_HOUSE.add("2-60-9-1614");
        ONLY_HOUSE.add("2-60-9-1617");
        ONLY_HOUSE.add("2-60-9-1616");
        ONLY_HOUSE.add("44-40-18-3122");
        ONLY_HOUSE.add("2-60-9-2794");
        ONLY_HOUSE.add("2-60-9-2795");
        ONLY_HOUSE.add("2-60-9-2796");
        ONLY_HOUSE.add("2-60-9-2797");
        ONLY_HOUSE.add("2-60-9-2790");
        ONLY_HOUSE.add("44-56-11-1053");
        ONLY_HOUSE.add("2-60-9-2791");
        ONLY_HOUSE.add("2-60-9-2792");
        ONLY_HOUSE.add("2-60-9-2793");
        ONLY_HOUSE.add("2-54-125-2012");
        ONLY_HOUSE.add("2-60-9-2799");
        ONLY_HOUSE.add("2-60-9-2798");
        ONLY_HOUSE.add("44-40-18-3132");
        ONLY_HOUSE.add("44-46-120-16");
        ONLY_HOUSE.add("44-46-120-17");
        ONLY_HOUSE.add("44-46-120-19");
        ONLY_HOUSE.add("44-40-18-3112");
        ONLY_HOUSE.add("44-46-120-21");
        ONLY_HOUSE.add("44-46-120-24");
        ONLY_HOUSE.add("44-46-120-28");
        ONLY_HOUSE.add("44-46-120-27");
        ONLY_HOUSE.add("44-46-120-26");
        ONLY_HOUSE.add("2-60-9-2770");
        ONLY_HOUSE.add("2-60-9-2771");
        ONLY_HOUSE.add("2-60-9-2772");
        ONLY_HOUSE.add("2-60-9-2773");
        ONLY_HOUSE.add("2-60-9-2774");
        ONLY_HOUSE.add("2-60-9-2775");
        ONLY_HOUSE.add("2-60-9-2777");
        ONLY_HOUSE.add("2-60-9-2776");
        ONLY_HOUSE.add("2-60-9-2779");
        ONLY_HOUSE.add("2-60-9-2778");
        ONLY_HOUSE.add("44-46-141-1091");
        ONLY_HOUSE.add("44-46-141-1092");
        ONLY_HOUSE.add("44-46-141-1093");
        ONLY_HOUSE.add("44-46-141-1094");
        ONLY_HOUSE.add("44-40-18-3152");
        ONLY_HOUSE.add("44-40-18-3151");
        ONLY_HOUSE.add("2-60-9-2781");
        ONLY_HOUSE.add("2-60-9-2782");
        ONLY_HOUSE.add("2-60-9-2780");
        ONLY_HOUSE.add("2-60-9-2785");
        ONLY_HOUSE.add("2-60-9-2786");
        ONLY_HOUSE.add("2-60-9-2783");
        ONLY_HOUSE.add("2-60-9-2784");
        ONLY_HOUSE.add("2-60-9-2789");
        ONLY_HOUSE.add("2-60-9-2788");
        ONLY_HOUSE.add("2-55-69-4032");
        ONLY_HOUSE.add("2-60-9-2787");
        ONLY_HOUSE.add("2-54-125-2021");
        ONLY_HOUSE.add("2-55-78-2021");
        ONLY_HOUSE.add("2-54-65-4021");
        ONLY_HOUSE.add("2-55-69-4052");
        ONLY_HOUSE.add("2-60-9-1688");
        ONLY_HOUSE.add("2-60-9-1687");
        ONLY_HOUSE.add("2-60-9-1689");
        ONLY_HOUSE.add("2-60-9-1680");
        ONLY_HOUSE.add("2-60-9-1681");
        ONLY_HOUSE.add("2-60-9-1682");
        ONLY_HOUSE.add("2-60-9-1683");
        ONLY_HOUSE.add("2-60-9-1684");
        ONLY_HOUSE.add("2-60-9-1685");
        ONLY_HOUSE.add("2-60-9-1686");
        ONLY_HOUSE.add("2-60-9-1699");
        ONLY_HOUSE.add("2-60-9-1698");
        ONLY_HOUSE.add("2-60-9-1692");
        ONLY_HOUSE.add("2-60-9-1693");
        ONLY_HOUSE.add("2-60-9-1690");
        ONLY_HOUSE.add("2-60-9-1691");
        ONLY_HOUSE.add("2-60-9-1696");
        ONLY_HOUSE.add("2-60-9-1697");
        ONLY_HOUSE.add("2-60-9-1694");
        ONLY_HOUSE.add("2-60-9-1695");
        ONLY_HOUSE.add("2-60-9-1669");
        ONLY_HOUSE.add("2-60-9-2717");
        ONLY_HOUSE.add("2-60-9-2716");
        ONLY_HOUSE.add("2-60-9-2715");
        ONLY_HOUSE.add("2-54-65-4061");
        ONLY_HOUSE.add("2-60-9-2714");
        ONLY_HOUSE.add("2-60-9-1665");
        ONLY_HOUSE.add("2-60-9-2713");
        ONLY_HOUSE.add("2-60-9-2712");
        ONLY_HOUSE.add("2-60-9-1666");
        ONLY_HOUSE.add("2-60-9-1667");
        ONLY_HOUSE.add("2-60-9-2711");
        ONLY_HOUSE.add("2-55-96-2021");
        ONLY_HOUSE.add("2-60-9-2710");
        ONLY_HOUSE.add("2-60-9-1668");
        ONLY_HOUSE.add("2-60-9-2719");
        ONLY_HOUSE.add("2-60-9-2718");
        ONLY_HOUSE.add("2-60-9-1662");
        ONLY_HOUSE.add("2-60-9-1661");
        ONLY_HOUSE.add("2-60-9-1664");
        ONLY_HOUSE.add("2-60-9-1663");
        ONLY_HOUSE.add("2-60-9-1660");
        ONLY_HOUSE.add("2-60-9-2726");
        ONLY_HOUSE.add("2-60-9-2725");
        ONLY_HOUSE.add("2-54-90-1064");
        ONLY_HOUSE.add("2-60-9-2728");
        ONLY_HOUSE.add("2-60-9-2727");
        ONLY_HOUSE.add("2-60-9-2722");
        ONLY_HOUSE.add("2-60-9-1678");
        ONLY_HOUSE.add("2-60-9-1679");
        ONLY_HOUSE.add("2-60-9-2721");
        ONLY_HOUSE.add("2-60-9-2724");
        ONLY_HOUSE.add("2-60-9-1676");
        ONLY_HOUSE.add("2-60-9-1677");
        ONLY_HOUSE.add("2-60-9-2723");
        ONLY_HOUSE.add("2-60-9-2729");
        ONLY_HOUSE.add("2-60-9-1675");
        ONLY_HOUSE.add("2-60-9-1674");
        ONLY_HOUSE.add("2-60-9-1673");
        ONLY_HOUSE.add("2-60-9-1672");
        ONLY_HOUSE.add("2-60-9-2720");
        ONLY_HOUSE.add("2-60-9-1671");
        ONLY_HOUSE.add("2-60-9-1670");
        ONLY_HOUSE.add("2-60-9-1643");
        ONLY_HOUSE.add("2-60-9-1644");
        ONLY_HOUSE.add("2-60-9-1645");
        ONLY_HOUSE.add("2-60-9-1646");
        ONLY_HOUSE.add("2-60-9-1647");
        ONLY_HOUSE.add("2-60-9-1648");
        ONLY_HOUSE.add("2-60-9-1649");
        ONLY_HOUSE.add("2-60-9-1640");
        ONLY_HOUSE.add("2-60-9-1642");
        ONLY_HOUSE.add("2-60-9-1641");
        ONLY_HOUSE.add("2-60-9-1656");
        ONLY_HOUSE.add("44-46-104-8");
        ONLY_HOUSE.add("2-60-9-1657");
        ONLY_HOUSE.add("44-46-104-6");
        ONLY_HOUSE.add("2-60-9-2702");
        ONLY_HOUSE.add("2-60-9-1654");
        ONLY_HOUSE.add("2-60-9-1655");
        ONLY_HOUSE.add("2-60-9-2701");
        ONLY_HOUSE.add("2-60-9-2704");
        ONLY_HOUSE.add("2-60-9-2703");
        ONLY_HOUSE.add("2-60-9-2706");
        ONLY_HOUSE.add("2-60-9-1658");
        ONLY_HOUSE.add("2-60-9-1659");
        ONLY_HOUSE.add("2-60-9-2705");
        ONLY_HOUSE.add("2-60-9-2708");
        ONLY_HOUSE.add("2-60-9-2707");
        ONLY_HOUSE.add("2-60-9-2709");
        ONLY_HOUSE.add("44-21-14-7");
        ONLY_HOUSE.add("44-40-20-5042");
        ONLY_HOUSE.add("44-21-14-3");
        ONLY_HOUSE.add("2-54-81-3062");
        ONLY_HOUSE.add("2-60-9-1653");
        ONLY_HOUSE.add("2-60-9-1652");
        ONLY_HOUSE.add("2-60-9-1651");
        ONLY_HOUSE.add("2-60-9-1650");
        ONLY_HOUSE.add("44-46-105-6");
        ONLY_HOUSE.add("44-46-105-5");
        ONLY_HOUSE.add("44-46-105-9");
        ONLY_HOUSE.add("2-54-125-3062");
        ONLY_HOUSE.add("2-54-125-3061");
        ONLY_HOUSE.add("44-46-105-4");
        ONLY_HOUSE.add("44-46-105-1");
        ONLY_HOUSE.add("44-46-105-2");
        ONLY_HOUSE.add("44-46-144-1");
        ONLY_HOUSE.add("2-54-84-3041");
        ONLY_HOUSE.add("44-40-5-1083");
        ONLY_HOUSE.add("44-46-141-1122");
        ONLY_HOUSE.add("44-46-141-1121");
        ONLY_HOUSE.add("44-46-141-1124");
        ONLY_HOUSE.add("44-46-141-1123");
        ONLY_HOUSE.add("44-21-19-14");
        ONLY_HOUSE.add("44-46-141-1114");
        ONLY_HOUSE.add("2-54-144-1032");
        ONLY_HOUSE.add("44-46-141-1113");
        ONLY_HOUSE.add("44-46-141-1112");
        ONLY_HOUSE.add("44-46-141-1111");
        ONLY_HOUSE.add("44-46-141-1103");
        ONLY_HOUSE.add("44-46-141-1104");
        ONLY_HOUSE.add("44-46-141-1102");
        ONLY_HOUSE.add("44-46-141-1101");
        ONLY_HOUSE.add("2-54-98-1073");
        ONLY_HOUSE.add("44-21-19-38");
        ONLY_HOUSE.add("2-55-96-3051");
        ONLY_HOUSE.add("2-54-84-3033");
        ONLY_HOUSE.add("2-60-9-2699");
        ONLY_HOUSE.add("2-60-9-2698");
        ONLY_HOUSE.add("2-60-9-2697");
        ONLY_HOUSE.add("2-60-9-2696");
        ONLY_HOUSE.add("2-60-9-2695");
        ONLY_HOUSE.add("2-60-9-2694");
        ONLY_HOUSE.add("2-60-9-2693");
        ONLY_HOUSE.add("2-60-9-2692");
        ONLY_HOUSE.add("2-60-9-2691");
        ONLY_HOUSE.add("2-60-9-2690");
        ONLY_HOUSE.add("44-46-141-1161");
        ONLY_HOUSE.add("44-46-141-1162");
        ONLY_HOUSE.add("44-46-141-1163");
        ONLY_HOUSE.add("44-46-141-1164");
        ONLY_HOUSE.add("44-46-141-1152");
        ONLY_HOUSE.add("44-46-141-1153");
        ONLY_HOUSE.add("44-46-141-1151");
        ONLY_HOUSE.add("44-46-141-1154");
        ONLY_HOUSE.add("44-46-141-1143");
        ONLY_HOUSE.add("44-46-141-1144");
        ONLY_HOUSE.add("44-46-141-1141");
        ONLY_HOUSE.add("44-46-141-1142");
        ONLY_HOUSE.add("44-46-141-1134");
        ONLY_HOUSE.add("44-46-141-1132");
        ONLY_HOUSE.add("44-46-141-1133");
        ONLY_HOUSE.add("44-46-141-1131");
        ONLY_HOUSE.add("44-46-160-6");
        ONLY_HOUSE.add("44-46-160-7");
        ONLY_HOUSE.add("44-46-160-4");
        ONLY_HOUSE.add("44-46-160-5");
        ONLY_HOUSE.add("44-46-160-2");
        ONLY_HOUSE.add("44-46-160-3");
        ONLY_HOUSE.add("44-46-160-1");
        ONLY_HOUSE.add("44-46-160-8");
        ONLY_HOUSE.add("44-46-160-9");
        ONLY_HOUSE.add("44-46-51-2234");
        ONLY_HOUSE.add("2-54-81-1013");
        ONLY_HOUSE.add("44-46-51-2212");
        ONLY_HOUSE.add("44-40-18-2171");
        ONLY_HOUSE.add("44-40-18-2172");
        ONLY_HOUSE.add("2-55-106-4051");
        ONLY_HOUSE.add("2-55-77-6021");
        ONLY_HOUSE.add("2-55-77-6022");
        ONLY_HOUSE.add("44-40-18-2162");
        ONLY_HOUSE.add("44-40-8-5151");
        ONLY_HOUSE.add("44-46-103-14");
        ONLY_HOUSE.add("44-46-103-12");
        ONLY_HOUSE.add("44-46-103-17");
        ONLY_HOUSE.add("2-55-78-4032");
        ONLY_HOUSE.add("44-46-132-6");
        ONLY_HOUSE.add("44-46-132-5");
        ONLY_HOUSE.add("44-46-132-7");
        ONLY_HOUSE.add("44-46-132-9");
        ONLY_HOUSE.add("44-40-8-5161");
        ONLY_HOUSE.add("44-40-8-5173");
        ONLY_HOUSE.add("2-54-168-2032");
        ONLY_HOUSE.add("2-55-78-4011");
        ONLY_HOUSE.add("2-60-9-3105");
        ONLY_HOUSE.add("2-60-9-3104");
        ONLY_HOUSE.add("2-60-9-3103");
        ONLY_HOUSE.add("2-60-9-3102");
        ONLY_HOUSE.add("2-60-9-3101");
        ONLY_HOUSE.add("2-60-9-3108");
        ONLY_HOUSE.add("2-60-9-3109");
        ONLY_HOUSE.add("2-60-9-3106");
        ONLY_HOUSE.add("2-60-9-3107");
        ONLY_HOUSE.add("2-60-9-3114");
        ONLY_HOUSE.add("2-60-9-3113");
        ONLY_HOUSE.add("2-60-9-3116");
        ONLY_HOUSE.add("2-60-9-3115");
        ONLY_HOUSE.add("2-60-9-3110");
        ONLY_HOUSE.add("2-60-9-3112");
        ONLY_HOUSE.add("2-60-9-3111");
        ONLY_HOUSE.add("2-60-9-3117");
        ONLY_HOUSE.add("2-60-9-3118");
        ONLY_HOUSE.add("2-60-9-3119");
        ONLY_HOUSE.add("2-60-9-3123");
        ONLY_HOUSE.add("2-60-9-3122");
        ONLY_HOUSE.add("2-60-9-3121");
        ONLY_HOUSE.add("2-60-9-3120");
        ONLY_HOUSE.add("2-60-9-3127");
        ONLY_HOUSE.add("2-60-9-3126");
        ONLY_HOUSE.add("2-60-9-3125");
        ONLY_HOUSE.add("2-60-9-3124");
        ONLY_HOUSE.add("2-60-9-3128");
        ONLY_HOUSE.add("2-60-9-3129");
        ONLY_HOUSE.add("2-60-9-3132");
        ONLY_HOUSE.add("2-60-9-3131");
        ONLY_HOUSE.add("2-60-9-3134");
        ONLY_HOUSE.add("2-60-9-3133");
        ONLY_HOUSE.add("2-60-9-3136");
        ONLY_HOUSE.add("2-60-9-3135");
        ONLY_HOUSE.add("2-60-9-3138");
        ONLY_HOUSE.add("2-60-9-3137");
        ONLY_HOUSE.add("2-60-9-3130");
        ONLY_HOUSE.add("2-60-9-3139");
        ONLY_HOUSE.add("44-56-11-2202");
        ONLY_HOUSE.add("2-55-99-1043");
        ONLY_HOUSE.add("2-60-9-3140");
        ONLY_HOUSE.add("2-60-9-3141");
        ONLY_HOUSE.add("2-60-9-3146");
        ONLY_HOUSE.add("2-60-9-3147");
        ONLY_HOUSE.add("2-60-9-3148");
        ONLY_HOUSE.add("2-60-9-3149");
        ONLY_HOUSE.add("2-60-9-3142");
        ONLY_HOUSE.add("2-60-9-3143");
        ONLY_HOUSE.add("2-60-9-3144");
        ONLY_HOUSE.add("2-60-9-3145");
        ONLY_HOUSE.add("2-60-9-3151");
        ONLY_HOUSE.add("2-60-9-3152");
        ONLY_HOUSE.add("2-60-9-3150");
        ONLY_HOUSE.add("2-60-9-3159");
        ONLY_HOUSE.add("2-60-9-3157");
        ONLY_HOUSE.add("2-60-9-3158");
        ONLY_HOUSE.add("2-60-9-3155");
        ONLY_HOUSE.add("2-60-9-3156");
        ONLY_HOUSE.add("2-60-9-3153");
        ONLY_HOUSE.add("2-60-9-3154");
        ONLY_HOUSE.add("2-55-99-1061");
        ONLY_HOUSE.add("2-60-9-3160");
        ONLY_HOUSE.add("2-60-9-3161");
        ONLY_HOUSE.add("2-60-9-3162");
        ONLY_HOUSE.add("2-60-9-3163");
        ONLY_HOUSE.add("2-60-9-3164");
        ONLY_HOUSE.add("2-60-9-3165");
        ONLY_HOUSE.add("2-60-9-3166");
        ONLY_HOUSE.add("2-60-9-3167");
        ONLY_HOUSE.add("2-60-9-3168");
        ONLY_HOUSE.add("2-60-9-3169");
        ONLY_HOUSE.add("44-56-11-2232");
        ONLY_HOUSE.add("2-60-9-3170");
        ONLY_HOUSE.add("2-60-9-3173");
        ONLY_HOUSE.add("2-60-9-3174");
        ONLY_HOUSE.add("2-60-9-3171");
        ONLY_HOUSE.add("2-60-9-3172");
        ONLY_HOUSE.add("2-60-9-3177");
        ONLY_HOUSE.add("2-60-9-3178");
        ONLY_HOUSE.add("2-60-9-3175");
        ONLY_HOUSE.add("2-60-9-3176");
        ONLY_HOUSE.add("2-60-9-3179");
        ONLY_HOUSE.add("2-60-9-3182");
        ONLY_HOUSE.add("2-60-9-3181");
        ONLY_HOUSE.add("44-56-11-2242");
        ONLY_HOUSE.add("2-60-9-3180");
        ONLY_HOUSE.add("44-21-28-7");
        ONLY_HOUSE.add("2-55-133-2053");
        ONLY_HOUSE.add("44-21-28-2");
        ONLY_HOUSE.add("44-56-11-2265");
        ONLY_HOUSE.add("2-54-151-3022");
        ONLY_HOUSE.add("2-54-66-3032");
        ONLY_HOUSE.add("44-46-51-2144");
        ONLY_HOUSE.add("44-46-2-2041");
        ONLY_HOUSE.add("44-46-2-2072");
        ONLY_HOUSE.add("2-55-94-8");
        ONLY_HOUSE.add("2-55-94-2");
        ONLY_HOUSE.add("2-55-94-1");
        ONLY_HOUSE.add("44-46-102-17");
        ONLY_HOUSE.add("44-46-102-13");
        ONLY_HOUSE.add("44-46-102-14");
        ONLY_HOUSE.add("44-40-8-5053");
        ONLY_HOUSE.add("44-46-51-2183");
        ONLY_HOUSE.add("44-46-131-2");
        ONLY_HOUSE.add("44-46-102-19");
        ONLY_HOUSE.add("44-46-131-5");
        ONLY_HOUSE.add("44-46-131-6");
        ONLY_HOUSE.add("44-40-8-5033");
        ONLY_HOUSE.add("44-46-51-1074");
        ONLY_HOUSE.add("44-40-8-5043");
        ONLY_HOUSE.add("2-54-151-3052");
        ONLY_HOUSE.add("44-40-8-5071");
        ONLY_HOUSE.add("2-54-151-3042");
        ONLY_HOUSE.add("44-40-14-1131");
        ONLY_HOUSE.add("44-40-8-5081");
        ONLY_HOUSE.add("2-55-72-3032");
        ONLY_HOUSE.add("2-60-9-3002");
        ONLY_HOUSE.add("2-60-9-3001");
        ONLY_HOUSE.add("2-60-9-3006");
        ONLY_HOUSE.add("2-60-9-3005");
        ONLY_HOUSE.add("2-60-9-3004");
        ONLY_HOUSE.add("2-60-9-3003");
        ONLY_HOUSE.add("2-60-9-3009");
        ONLY_HOUSE.add("2-60-9-3007");
        ONLY_HOUSE.add("2-60-9-3008");
        ONLY_HOUSE.add("2-55-106-3043");
        ONLY_HOUSE.add("44-40-21-4062");
        ONLY_HOUSE.add("2-60-9-3011");
        ONLY_HOUSE.add("2-60-9-3010");
        ONLY_HOUSE.add("2-60-9-3013");
        ONLY_HOUSE.add("2-60-9-3012");
        ONLY_HOUSE.add("2-60-9-3015");
        ONLY_HOUSE.add("2-60-9-3014");
        ONLY_HOUSE.add("2-60-9-3017");
        ONLY_HOUSE.add("2-60-9-3016");
        ONLY_HOUSE.add("2-60-9-3018");
        ONLY_HOUSE.add("2-60-9-3019");
        ONLY_HOUSE.add("2-55-106-3032");
        ONLY_HOUSE.add("44-40-8-5121");
        ONLY_HOUSE.add("44-46-119-18");
        ONLY_HOUSE.add("44-46-119-19");
        ONLY_HOUSE.add("44-46-119-14");
        ONLY_HOUSE.add("44-46-119-16");
        ONLY_HOUSE.add("44-46-119-24");
        ONLY_HOUSE.add("44-46-119-25");
        ONLY_HOUSE.add("44-46-119-26");
        ONLY_HOUSE.add("44-46-119-27");
        ONLY_HOUSE.add("2-60-9-3043");
        ONLY_HOUSE.add("2-60-9-3044");
        ONLY_HOUSE.add("2-60-9-3045");
        ONLY_HOUSE.add("2-60-9-3046");
        ONLY_HOUSE.add("44-40-8-4051");
        ONLY_HOUSE.add("2-60-9-3047");
        ONLY_HOUSE.add("2-60-9-3048");
        ONLY_HOUSE.add("2-60-9-3049");
        ONLY_HOUSE.add("44-40-8-4052");
        ONLY_HOUSE.add("2-60-9-3040");
        ONLY_HOUSE.add("2-60-9-3041");
        ONLY_HOUSE.add("2-60-9-3042");
        ONLY_HOUSE.add("2-60-9-3056");
        ONLY_HOUSE.add("2-60-9-3057");
        ONLY_HOUSE.add("2-60-9-3054");
        ONLY_HOUSE.add("2-60-9-3055");
        ONLY_HOUSE.add("44-40-8-4042");
        ONLY_HOUSE.add("44-40-8-4041");
        ONLY_HOUSE.add("2-60-9-3058");
        ONLY_HOUSE.add("2-60-9-3059");
        ONLY_HOUSE.add("2-60-9-3052");
        ONLY_HOUSE.add("2-60-9-3053");
        ONLY_HOUSE.add("2-60-9-3050");
        ONLY_HOUSE.add("2-60-9-3051");
        ONLY_HOUSE.add("2-60-9-3029");
        ONLY_HOUSE.add("2-60-9-3025");
        ONLY_HOUSE.add("2-60-9-3026");
        ONLY_HOUSE.add("44-40-8-4031");
        ONLY_HOUSE.add("2-60-9-3027");
        ONLY_HOUSE.add("2-60-9-3028");
        ONLY_HOUSE.add("2-60-9-3021");
        ONLY_HOUSE.add("44-40-8-4032");
        ONLY_HOUSE.add("2-60-9-3022");
        ONLY_HOUSE.add("2-60-9-3023");
        ONLY_HOUSE.add("2-60-9-3024");
        ONLY_HOUSE.add("2-60-9-3020");
        ONLY_HOUSE.add("2-60-9-3038");
        ONLY_HOUSE.add("2-60-9-3039");
        ONLY_HOUSE.add("2-60-9-3036");
        ONLY_HOUSE.add("2-54-106-4052");
        ONLY_HOUSE.add("2-60-9-3037");
        ONLY_HOUSE.add("2-60-9-3034");
        ONLY_HOUSE.add("2-60-9-3035");
        ONLY_HOUSE.add("2-60-9-3032");
        ONLY_HOUSE.add("44-51-40-23");
        ONLY_HOUSE.add("2-60-9-3033");
        ONLY_HOUSE.add("2-60-9-3030");
        ONLY_HOUSE.add("2-60-9-3031");
        ONLY_HOUSE.add("2-60-9-3080");
        ONLY_HOUSE.add("2-60-9-3082");
        ONLY_HOUSE.add("2-60-9-3081");
        ONLY_HOUSE.add("2-60-9-3084");
        ONLY_HOUSE.add("2-60-9-3083");
        ONLY_HOUSE.add("2-60-9-3086");
        ONLY_HOUSE.add("2-60-9-3085");
        ONLY_HOUSE.add("2-60-9-3088");
        ONLY_HOUSE.add("2-60-9-3087");
        ONLY_HOUSE.add("2-60-9-3089");
        ONLY_HOUSE.add("2-60-9-3093");
        ONLY_HOUSE.add("2-60-9-3092");
        ONLY_HOUSE.add("2-60-9-3091");
        ONLY_HOUSE.add("2-60-9-3090");
        ONLY_HOUSE.add("2-60-9-3097");
        ONLY_HOUSE.add("2-60-9-3096");
        ONLY_HOUSE.add("2-60-9-3095");
        ONLY_HOUSE.add("44-21-14-16");
        ONLY_HOUSE.add("2-60-9-3094");
        ONLY_HOUSE.add("2-60-9-3099");
        ONLY_HOUSE.add("2-60-9-3098");
        ONLY_HOUSE.add("2-60-9-3062");
        ONLY_HOUSE.add("2-60-9-3061");
        ONLY_HOUSE.add("2-60-9-3064");
        ONLY_HOUSE.add("2-60-9-3063");
        ONLY_HOUSE.add("2-60-9-3060");
        ONLY_HOUSE.add("2-60-9-3069");
        ONLY_HOUSE.add("2-60-9-3066");
        ONLY_HOUSE.add("2-60-9-3065");
        ONLY_HOUSE.add("2-60-9-3068");
        ONLY_HOUSE.add("2-60-9-3067");
        ONLY_HOUSE.add("2-60-9-3075");
        ONLY_HOUSE.add("2-60-9-3074");
        ONLY_HOUSE.add("2-60-9-3073");
        ONLY_HOUSE.add("2-60-9-3072");
        ONLY_HOUSE.add("2-60-9-3071");
        ONLY_HOUSE.add("2-60-9-3070");
        ONLY_HOUSE.add("2-60-9-3079");
        ONLY_HOUSE.add("2-60-9-3078");
        ONLY_HOUSE.add("2-60-9-3077");
        ONLY_HOUSE.add("2-60-9-3076");
        ONLY_HOUSE.add("44-46-101-1");
        ONLY_HOUSE.add("44-46-101-5");
        ONLY_HOUSE.add("44-46-101-9");
        ONLY_HOUSE.add("2-54-136-2");
        ONLY_HOUSE.add("2-54-136-3");
        ONLY_HOUSE.add("2-54-136-1");
        ONLY_HOUSE.add("2-54-136-8");
        ONLY_HOUSE.add("2-54-136-9");
        ONLY_HOUSE.add("2-54-136-6");
        ONLY_HOUSE.add("2-54-136-7");
        ONLY_HOUSE.add("2-54-136-4");
        ONLY_HOUSE.add("2-54-136-5");
        ONLY_HOUSE.add("2-55-73-2021");
        ONLY_HOUSE.add("2-55-83-2041");
        ONLY_HOUSE.add("2-55-83-2042");
        ONLY_HOUSE.add("2-55-83-2051");
        ONLY_HOUSE.add("2-55-83-2052");
        ONLY_HOUSE.add("2-54-147-3041");
        ONLY_HOUSE.add("2-55-83-2071");
        ONLY_HOUSE.add("44-40-22-1012");
        ONLY_HOUSE.add("44-40-22-1061");
        ONLY_HOUSE.add("2-54-147-3021");
        ONLY_HOUSE.add("44-46-130-3");
        ONLY_HOUSE.add("2-54-57-4");
        ONLY_HOUSE.add("2-60-9-2109");
        ONLY_HOUSE.add("2-60-9-2106");
        ONLY_HOUSE.add("2-60-9-2105");
        ONLY_HOUSE.add("2-60-9-2108");
        ONLY_HOUSE.add("2-60-9-2107");
        ONLY_HOUSE.add("44-40-7-3091");
        ONLY_HOUSE.add("2-60-9-2101");
        ONLY_HOUSE.add("2-60-9-2102");
        ONLY_HOUSE.add("2-60-9-2103");
        ONLY_HOUSE.add("2-60-9-2104");
        ONLY_HOUSE.add("44-40-7-3062");
        ONLY_HOUSE.add("44-40-7-3061");
        ONLY_HOUSE.add("2-60-9-2110");
        ONLY_HOUSE.add("2-60-9-2111");
        ONLY_HOUSE.add("2-60-9-2112");
        ONLY_HOUSE.add("44-40-7-3071");
        ONLY_HOUSE.add("44-40-7-3072");
        ONLY_HOUSE.add("44-56-11-2057");
        ONLY_HOUSE.add("44-46-51-3131");
        ONLY_HOUSE.add("2-55-68-1031");
        ONLY_HOUSE.add("44-46-51-3111");
        ONLY_HOUSE.add("44-40-24-1171");
        ONLY_HOUSE.add("44-40-24-1172");
        ONLY_HOUSE.add("2-60-9-1003");
        ONLY_HOUSE.add("2-60-9-1004");
        ONLY_HOUSE.add("2-60-9-1001");
        ONLY_HOUSE.add("2-60-9-1002");
        ONLY_HOUSE.add("44-40-7-3041");
        ONLY_HOUSE.add("44-40-7-3042");
        ONLY_HOUSE.add("2-60-9-1009");
        ONLY_HOUSE.add("2-60-9-1008");
        ONLY_HOUSE.add("2-60-9-1007");
        ONLY_HOUSE.add("2-54-156-2063");
        ONLY_HOUSE.add("2-60-9-1006");
        ONLY_HOUSE.add("2-60-9-1005");
        ONLY_HOUSE.add("2-60-9-1012");
        ONLY_HOUSE.add("2-60-9-1013");
        ONLY_HOUSE.add("2-60-9-1014");
        ONLY_HOUSE.add("2-60-9-1015");
        ONLY_HOUSE.add("2-60-9-1010");
        ONLY_HOUSE.add("2-60-9-1011");
        ONLY_HOUSE.add("44-40-7-3051");
        ONLY_HOUSE.add("44-40-7-3052");
        ONLY_HOUSE.add("2-60-9-1017");
        ONLY_HOUSE.add("2-60-9-1016");
        ONLY_HOUSE.add("2-60-9-1019");
        ONLY_HOUSE.add("2-60-9-1018");
        ONLY_HOUSE.add("2-60-9-1021");
        ONLY_HOUSE.add("2-60-9-1022");
        ONLY_HOUSE.add("2-60-9-1020");
        ONLY_HOUSE.add("2-60-9-1025");
        ONLY_HOUSE.add("2-60-9-1026");
        ONLY_HOUSE.add("2-60-9-1023");
        ONLY_HOUSE.add("2-60-9-1024");
        ONLY_HOUSE.add("2-54-188-1162");
        ONLY_HOUSE.add("2-54-188-1164");
        ONLY_HOUSE.add("2-60-9-1029");
        ONLY_HOUSE.add("2-60-9-1028");
        ONLY_HOUSE.add("2-60-9-1027");
        ONLY_HOUSE.add("2-60-9-1030");
        ONLY_HOUSE.add("2-60-9-1031");
        ONLY_HOUSE.add("2-60-9-1032");
        ONLY_HOUSE.add("2-60-9-1033");
        ONLY_HOUSE.add("2-60-9-1034");
        ONLY_HOUSE.add("2-60-9-1035");
        ONLY_HOUSE.add("2-60-9-1036");
        ONLY_HOUSE.add("2-60-9-1037");
        ONLY_HOUSE.add("44-46-51-3194");
        ONLY_HOUSE.add("44-40-7-3032");
        ONLY_HOUSE.add("44-40-7-3031");
        ONLY_HOUSE.add("2-60-9-1039");
        ONLY_HOUSE.add("2-60-9-1038");
        ONLY_HOUSE.add("2-54-108-4011");
        ONLY_HOUSE.add("44-46-100-7");
        ONLY_HOUSE.add("44-46-100-6");
        ONLY_HOUSE.add("44-46-100-8");
        ONLY_HOUSE.add("44-46-100-3");
        ONLY_HOUSE.add("44-46-100-5");
        ONLY_HOUSE.add("44-46-100-1");
        ONLY_HOUSE.add("44-40-18-2051");
        ONLY_HOUSE.add("2-54-160-4062");
        ONLY_HOUSE.add("2-54-33-11");
        ONLY_HOUSE.add("2-55-73-2112");
        ONLY_HOUSE.add("44-44-10");
        ONLY_HOUSE.add("2-54-53-5051");
        ONLY_HOUSE.add("2-54-136-10");
        ONLY_HOUSE.add("2-54-136-11");
        ONLY_HOUSE.add("44-40-18-2092");
        ONLY_HOUSE.add("2-54-108-4042");
        ONLY_HOUSE.add("2-55-107-4021");
        ONLY_HOUSE.add("44-44-48");
        ONLY_HOUSE.add("44-44-49");
        ONLY_HOUSE.add("44-44-43");
        ONLY_HOUSE.add("44-44-42");
        ONLY_HOUSE.add("44-44-41");
        ONLY_HOUSE.add("44-44-40");
        ONLY_HOUSE.add("44-44-39");
        ONLY_HOUSE.add("44-44-37");
        ONLY_HOUSE.add("44-44-38");
        ONLY_HOUSE.add("2-55-85-5052");
        ONLY_HOUSE.add("44-44-30");
        ONLY_HOUSE.add("44-44-31");
        ONLY_HOUSE.add("44-44-22");
        ONLY_HOUSE.add("44-44-23");
        ONLY_HOUSE.add("44-44-24");
        ONLY_HOUSE.add("44-44-26");
        ONLY_HOUSE.add("44-44-29");
        ONLY_HOUSE.add("44-44-21");
        ONLY_HOUSE.add("44-44-13");
        ONLY_HOUSE.add("44-44-14");
        ONLY_HOUSE.add("44-44-11");
        ONLY_HOUSE.add("44-44-12");
        ONLY_HOUSE.add("44-44-15");
        ONLY_HOUSE.add("44-44-16");
        ONLY_HOUSE.add("44-46-117-11");
        ONLY_HOUSE.add("44-46-117-12");
        ONLY_HOUSE.add("44-46-117-19");
        ONLY_HOUSE.add("44-46-117-17");
        ONLY_HOUSE.add("44-46-117-18");
        ONLY_HOUSE.add("44-21-18-9");
        ONLY_HOUSE.add("44-44-64");
        ONLY_HOUSE.add("44-44-62");
        ONLY_HOUSE.add("44-44-63");
        ONLY_HOUSE.add("44-46-117-21");
        ONLY_HOUSE.add("44-46-117-25");
        ONLY_HOUSE.add("44-46-117-22");
        ONLY_HOUSE.add("44-46-117-23");
        ONLY_HOUSE.add("44-46-117-28");
        ONLY_HOUSE.add("44-46-117-26");
        ONLY_HOUSE.add("44-40-24-2172");
        ONLY_HOUSE.add("44-40-24-2171");
        ONLY_HOUSE.add("2-54-188-1091");
        ONLY_HOUSE.add("2-54-188-1093");
        ONLY_HOUSE.add("44-40-13-6031");
        ONLY_HOUSE.add("2-54-147-2012");
        ONLY_HOUSE.add("2-54-108-5063");
        ONLY_HOUSE.add("2-55-73-2092");
        ONLY_HOUSE.add("2-60-9-4304");
        ONLY_HOUSE.add("2-60-9-4306");
        ONLY_HOUSE.add("2-60-9-4305");
        ONLY_HOUSE.add("2-60-9-4308");
        ONLY_HOUSE.add("2-60-9-4309");
        ONLY_HOUSE.add("44-40-24-1041");
        ONLY_HOUSE.add("44-56-11-2122");
        ONLY_HOUSE.add("44-21-29-1");
        ONLY_HOUSE.add("44-21-29-2");
        ONLY_HOUSE.add("44-40-16-4");
        ONLY_HOUSE.add("44-40-16-6");
        ONLY_HOUSE.add("44-56-11-2134");
        ONLY_HOUSE.add("2-54-154-2011");
        ONLY_HOUSE.add("44-56-11-2137");
        ONLY_HOUSE.add("44-40-16-3");
        ONLY_HOUSE.add("44-40-16-2");
        ONLY_HOUSE.add("2-60-9-4333");
        ONLY_HOUSE.add("44-40-18-2112");
        ONLY_HOUSE.add("2-60-9-4334");
        ONLY_HOUSE.add("2-60-9-4339");
        ONLY_HOUSE.add("2-60-9-4342");
        ONLY_HOUSE.add("2-60-9-4341");
        ONLY_HOUSE.add("2-60-9-4344");
        ONLY_HOUSE.add("44-40-18-2102");
        ONLY_HOUSE.add("2-60-9-4343");
        ONLY_HOUSE.add("2-60-9-4348");
        ONLY_HOUSE.add("2-60-9-4347");
        ONLY_HOUSE.add("44-56-11-2112");
        ONLY_HOUSE.add("2-60-9-4340");
        ONLY_HOUSE.add("2-60-9-4349");
        ONLY_HOUSE.add("2-60-9-4315");
        ONLY_HOUSE.add("2-60-9-4314");
        ONLY_HOUSE.add("2-60-9-4313");
        ONLY_HOUSE.add("2-60-9-4312");
        ONLY_HOUSE.add("2-60-9-4311");
        ONLY_HOUSE.add("2-60-9-4310");
        ONLY_HOUSE.add("2-60-9-4318");
        ONLY_HOUSE.add("2-60-9-4319");
        ONLY_HOUSE.add("2-60-9-4316");
        ONLY_HOUSE.add("2-60-9-4317");
        ONLY_HOUSE.add("2-60-9-4324");
        ONLY_HOUSE.add("2-60-9-4323");
        ONLY_HOUSE.add("2-60-9-4325");
        ONLY_HOUSE.add("2-60-9-4320");
        ONLY_HOUSE.add("44-40-18-2122");
        ONLY_HOUSE.add("2-60-9-4322");
        ONLY_HOUSE.add("2-60-9-4321");
        ONLY_HOUSE.add("2-54-180-3031");
        ONLY_HOUSE.add("44-46-104-11");
        ONLY_HOUSE.add("44-46-104-12");
        ONLY_HOUSE.add("44-46-104-14");
        ONLY_HOUSE.add("2-54-147-2061");
    }

    private static Set<String> EXCEPTION_HOUSE_CODE = new HashSet<>();

    static {
        EXCEPTION_HOUSE_CODE.add("44-46-125-27");

        EXCEPTION_HOUSE_CODE.add("2-60-9-3932");

        EXCEPTION_HOUSE_CODE.add("2-60-9-3904");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3903");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4211");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4216");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4220");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4214");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4213");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4218");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4212");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4215");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4219");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4217");
        EXCEPTION_HOUSE_CODE.add("2-60-9-642");
        EXCEPTION_HOUSE_CODE.add("2-60-9-642");
        EXCEPTION_HOUSE_CODE.add("2-60-9-642");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4247");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4241");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4246");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4249");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4242");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4243");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4240");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4244");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4245");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4248");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3955");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3950");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3951");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3952");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3953");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3957");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3954");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3956");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4137");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4141");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4138");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4142");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4139");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4143");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4140");
        EXCEPTION_HOUSE_CODE.add("44-40-7-3131");
        EXCEPTION_HOUSE_CODE.add("44-40-7-3081");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3908");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3905");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3906");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3907");
        EXCEPTION_HOUSE_CODE.add("2-60-9-618");
        EXCEPTION_HOUSE_CODE.add("2-60-9-617");
        EXCEPTION_HOUSE_CODE.add("44-40-3-3123");
        EXCEPTION_HOUSE_CODE.add("44-40-18-3092");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4123");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4122");
        EXCEPTION_HOUSE_CODE.add("2-60-9-745");
        EXCEPTION_HOUSE_CODE.add("2-60-9-744");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4077");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4078");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3911");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3916");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3915");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3913");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3912");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3917");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3914");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3918");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4148");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4149");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4327");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4330");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4326");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4331");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4329");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4332");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4328");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4164");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4165");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4135");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4136");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4159");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4160");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4161");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4117");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4118");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4170");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4168");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4169");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4131");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4130");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4129");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4134");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4128");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4133");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4132");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4336");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4337");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4338");
        EXCEPTION_HOUSE_CODE.add("2-60-9-4335");
        EXCEPTION_HOUSE_CODE.add("2-60-9-3931");

        EXCEPTION_HOUSE_CODE.add("2-54-57-9");
        EXCEPTION_HOUSE_CODE.add("2-54-26-2051");
        EXCEPTION_HOUSE_CODE.add("44-51-21-28");
        EXCEPTION_HOUSE_CODE.add("44-46-3-2061");
        EXCEPTION_HOUSE_CODE.add("44-46-140-4063");

    }



    private static Connection houseConn;

    private static Connection sharkConn;

    private static Connection recordConn;

    private static BufferedWriter errorWriter;

    private static BufferedWriter successWriter;

    private static BufferedWriter patchWriter;

    private static BufferedWriter sqlWriter;

    private static int count;

    private static int curCount;


    private static void begin() {

        curCount = 0;


        try {

            Statement statement = recordConn.createStatement();


            ResultSet bizRs = statement.executeQuery("SELECT count(DISTINCT HouseHistroy.NO) FROM HouseHistroy LEFT JOIN Business ON Business.ID = HouseHistroy.Business WHERE HouseHistroy.Business is not null and Business.b >= '" + BEGIN_DATE + "'");
            bizRs.next();
            count = bizRs.getInt(1);
            bizRs.close();
            statement.close();
            statement = recordConn.createStatement();

            bizRs = statement.executeQuery("SELECT DISTINCT HouseHistroy.NO FROM HouseHistroy LEFT JOIN Business ON Business.ID = HouseHistroy.Business WHERE HouseHistroy.Business is not null and Business.b >= '" + BEGIN_DATE + "'" );
            Set<String> houseCodes = new HashSet<>();


            while (bizRs.next()) {
                //不导的房屋
                if (! EXCEPTION_HOUSE_CODE.contains(bizRs.getString(1).trim())){
                    houseCodes.add(bizRs.getString(1));
                }
                count = houseCodes.size();

            }
            //String houseCode: houseCodes
            //houseCodes = ONLY_HOUSE;
            for(String houseCode: houseCodes) {
                long time = new java.util.Date().getTime();
                try {

                    sqlWriter.write(business(houseCode));
                    //sqlWriter.write(business("16629"));

                    sqlWriter.flush();
                    sqlWriter.newLine();


                    System.out.println(String.valueOf(count) + "/" + curCount + "    " + houseCode + "   " + (new java.util.Date().getTime() - time) + "ms" + "  " + (new Double(curCount).doubleValue() / new Double(count).doubleValue() * 100) + "%");
                } catch (NoSelectBizException e) {
                    errorWriter.write(houseCode + ">" + e.bizId + ">" + "SelectBizNotFound");



                    errorWriter.newLine();

                    sqlWriter.write("INSERT INTO LOCKED_HOUSE(TYPE,EMP_CODE,EMP_NAME,LOCKED_TIME,ID,HOUSE_CODE,DESCRIPTION) VALUES ('HOUSE_LOCKED','system','system','2016-4-20' ," + Q.p("E" + houseCode) + "," + Q.p(houseCode) + "," + Q.p("旧系统中业务无法导入,原因: 没有选择业务") + ");");

                    sqlWriter.flush();
                    sqlWriter.newLine();


                } catch (MustHaveSelectBizException e) {
                    errorWriter.write(houseCode + ">" + e.bizId + ">" + "MustHaveSelectBizException");
                    errorWriter.newLine();
                    sqlWriter.write("INSERT INTO LOCKED_HOUSE(TYPE,EMP_CODE,EMP_NAME,LOCKED_TIME,ID,HOUSE_CODE,DESCRIPTION) VALUES ('HOUSE_LOCKED','system','system','2016-4-20' ," + Q.p("E" + houseCode) + "," + Q.p(houseCode) + "," + Q.p("旧系统中业务无法导入,原因: 没有选择业务") + ");");

                    sqlWriter.flush();
                    sqlWriter.newLine();
                } catch (MainOwnerNotFoundException e) {
                    errorWriter.write(houseCode + ">" + e.bizId + ">" + "MainOwnerNotFoundException");
                    errorWriter.newLine();
                    sqlWriter.write("INSERT INTO LOCKED_HOUSE(TYPE,EMP_CODE,EMP_NAME,LOCKED_TIME,ID,HOUSE_CODE,DESCRIPTION) VALUES ('HOUSE_LOCKED','system','system','2016-4-20' ," + Q.p("E" + houseCode) + "," + Q.p(houseCode) + "," + Q.p("旧系统中业务无法导入,原因: 没有产权人") + ");");

                    sqlWriter.flush();
                    sqlWriter.newLine();
                } catch (MustMOBizException e) {
                    errorWriter.write(houseCode + ">" + e.bizId + ">" + "MustMOBizException");
                    errorWriter.newLine();
                    sqlWriter.write("INSERT INTO LOCKED_HOUSE(TYPE,EMP_CODE,EMP_NAME,LOCKED_TIME,ID,HOUSE_CODE,DESCRIPTION) VALUES ('HOUSE_LOCKED','system','system','2016-4-20' ," + Q.p("E" + houseCode) + "," + Q.p(houseCode) + "," + Q.p("旧系统中业务无法导入,原因: 抵押业务没有权证") + ");");

                    sqlWriter.flush();
                    sqlWriter.newLine();
                }

                System.out.println("dump house:" + houseCode);
                curCount++;

            }
            bizRs.close();
            statement.close();
            System.out.print("is OK...oooooooooooooooooooooo");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("查询业务出错");
            return;
        }catch (IOException e) {
            e.printStackTrace();
            System.out.print("写文件出错");
            return;
        }

    }


    private static String svs(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueVCHAR from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
           return rs.getString(1);
        }
        return null;
    }

    private static Timestamp svt(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueDATE from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
            return rs.getTimestamp(1);
        }
        return null;
    }

    private static Double svd(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueDBL from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
            return rs.getDouble(1);
        }
        return null;
    }

    private static Long svl(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueLONG from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
            return rs.getLong(1);
        }
        return null;
    }


    private static String business(String houseCode) throws SQLException, NoSelectBizException, MustHaveSelectBizException, IOException, MainOwnerNotFoundException, MustMOBizException {

        System.out.println("start:" + houseCode);
        Map<String, ReadyBusiness> result = new HashMap<String, ReadyBusiness>();

        ReadyBusiness first = null;

        int lastOldState = 0;

        Statement statement = recordConn.createStatement();

        ResultSet bizRs = statement.executeQuery("SELECT  Business.RecordBizNO,Business.Memo,Business.SelectBiz,Business.FinalTime,Business.RegisterTime, Case CONVERT(VARCHAR(8),Business.BOTime,108) WHEN '00:00:00' THEN Convert(DATETIME,  CONVERT(VARCHAR(10),Business.BOTime ,23) + ' ' +CONVERT(VARCHAR(8),HouseHistroy.ChangeDate,108) ) ELSE  Business.BOTime END as FULLBOTIME , Business.ID, Business.NameID " +
                //9
                " ,HouseHistroy.HouseOrder,HouseHistroy.UnitName,HouseHistroy.InFloorName,HouseHistroy.HouseArea,HouseHistroy.UseArea,HouseHistroy.CommArea,HouseHistroy.ShineArea," +
                "HouseHistroy.LoftArea,HouseHistroy.CommParam,HouseHistroy.HouseType,HouseHistroy.UseType,HouseHistroy.Structure,HouseHistroy.KnotSize,HouseHistroy.HouseStation," +
                "HouseHistroy.EastWall,HouseHistroy.WestWall,HouseHistroy.SouthWall,HouseHistroy.NorthWall,HouseHistroy.MappingDate,HouseHistroy.Direction , HouseHistroy.No ," +
                "HouseHistroy.BuildID ,HouseHistroy.MainOwner, HouseHistroy.PoolMemo, Business.WorkID, HouseHistroy.HouseState, Business.b, Business.NameID FROM HouseHistroy LEFT JOIN Business ON Business.ID = HouseHistroy.Business WHERE HouseHistroy.Business is not null and Business.ID is not null and HouseHistroy.NO = '" + houseCode + "' order by Business.BOTime, HouseHistroy.ChangeDate");

        while (bizRs.next()) {

            lastOldState = bizRs.getInt(34);
            String oldid = bizRs.getString(7);
            String id = bizRs.getString(1);
           // String nameId =  bizRs.getString(8);
            if (result.get(oldid) == null) {

                ReadyBusiness selectBiz = null;
                String selectBizId = bizRs.getString(3);
                if (selectBizId != null && !selectBizId.trim().equals("")) {
                    selectBiz = result.get(selectBizId);
                    if (selectBiz == null) {
                        throw new NoSelectBizException(oldid);
                    }
                }

//
                ReadyBusiness biz = new ReadyBusiness(new Date(bizRs.getTimestamp(35).getTime()).after(CONTINUE_DATE) ,
                        houseCode, first, bizRs.getString(8), id, bizRs.getString(2), (selectBiz == null) ? null : selectBiz.getId(),
                        bizRs.getTimestamp(4), bizRs.getTimestamp(5), bizRs.getTimestamp(6),bizRs.getString("NameID"));

                if (MUST_HAVE_SELECT_LIST.contains(biz.getDefineId())) {
                    if (selectBiz == null) {
                        throw new MustHaveSelectBizException(oldid);
                    }
                    selectBiz.setStatus("COMPLETE_CANCEL");
                }


                Statement sD = sharkConn.createStatement();
                Statement hD = houseConn.createStatement();

                ResultSet rs;

//                if (biz.getDefineId().equals("WP42")){
//                    biz.setContractOwner();
//                }else if (biz.getDefineId().equals("WP43") || biz.getDefineId().equals("WP41") || biz.getDefineId().equals("WP101")){
//                    biz.setContractOwner("",null);
//                }

                if (biz.getDefineId().equals("WP1") || biz.getDefineId().equals("WP2") || biz.getDefineId().equals("WP3")  || biz.getDefineId().equals("WP44") || biz.getDefineId().equals("WP45") || biz.getDefineId().equals("WP47") ){


                    rs = sD.executeQuery("select dhc.NO,dhc.CardNO,dhc.memo,dhc.printTime from DGHouseRecord..Business as db left join DGHouseInfo..houseCard dhc on db.id=dhc.bizid " +
                            "where dhc.type=198 " +
                            "and RecordBizNO=" + Q.p(id));

                    String card = null;
                    if (rs.next()) {
                        card = "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                                Q.v(
                                        Q.p("n-" + id), Q.pm(rs.getString(1)), Q.p("NOTICE")
                                        , Q.p(id), Q.p(true)
                                )

                                + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                                Q.v(Q.p("n-" + id), Q.pm(rs.getString(2)), Q.p(rs.getString(3)), Q.p(rs.getTimestamp(4)))

                                + ");";
                    }

                    rs = sD.executeQuery(
                            "  select VariableValueVCHAR from " +
                                    "    (select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                                    "      left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                                    "    left join shark..SHKProcessData as spd on a.oid=spd.process " +
                                    "  where spd.VariableDefinitionId = 'pre_buy_people' and VariableValueVCHAR is not null and VariableValueVCHAR<>'' and a.RecordBizNo =" + Q.p(id));



                    if (rs.next()) {

                        String ovalue = ownerInfoByNo(rs.getString(1));
                        String nowner = "INSERT INTO  BUSINESS_OWNER(ID,BUSINESS,NAME,ID_TYPE,ID_NO,PHONE,ROOT_ADDRESS,ADDRESS,OWNER_CARD) VALUES(" +
                                Q.v(Q.p("n-" + id),Q.p(id),ovalue , card == null ? "NULL" : Q.p(id))

                                + ");";
                        biz.setNoticeOwner("n-" + id,card == null ? nowner : card + nowner);

                    }




                }else if (biz.getDefineId().equals("WP46") || biz.getDefineId().equals("WP41")){
                    biz.setNoticeOwner("",null);
                }

                if (biz.getDefineId().equals("WP40")){

                    rs = sD.executeQuery("select dhc.NO,dhc.CardNO,dhc.memo,dhc.printTime from DGHouseRecord..Business as db left join DGHouseInfo..houseCard dhc on db.id=dhc.bizid " +
                            "where dhc.type=110 " +
                            "and RecordBizNO=" + Q.p(id));

                    String card = null;
                    if (rs.next()) {
                        card = "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                                Q.v(
                                        Q.p("d-" + id), Q.pm(rs.getString(1)), Q.p("OWNER_RSHIP")
                                        , Q.p(id), Q.p(true)
                                )

                                + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                                Q.v(Q.p("d-" + id), Q.pm(rs.getString(2)), Q.p(rs.getString(3)), Q.p(rs.getTimestamp(4)))

                                + ");";
                    }

                    rs = hD.executeQuery("select d.Name, d.ID from House h left join Build b on b.ID = h.BuildID left join project p on p.ID = b.ProjectID left join Developer d on d.ID = p.DeveloperID where h.NO = " + Q.p(houseCode));

                    if (rs.next()) {

                        String owner = "INSERT INTO  BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,BUSINESS,OWNER_CARD) VALUES(" +
                                Q.v(Q.p("d-" + id), Q.pm(rs.getString(1)), Q.p("OTHER"), Q.pm(rs.getString(2)), Q.p(id), card == null ? "NULL" : Q.p(id))

                                + ");";
                        biz.setDeveloperOwner("d-" + id,card == null ? owner : card + owner);
                    }


                }else if (biz.getDefineId().equals("WP41")){
                    biz.setDeveloperOwner("",null);
                }




                String businessOtherInfo = "";




                rs = sD.executeQuery("select NO,Cabinet,box from " +
                        "       (select db.RecordBizNO,db.box,db.Cabinet,rb.Business,rb.Record from " +
                        "              DGHouseRecord..Business as db left join DGHouseRecord..RecordandBiz as rb on db.id = rb.business) as a " +
                        "       left join DGHouseRecord..Record as rd on a.Record=rd.id WHERE RecordBizNo = " + Q.p(id));

                String recordCode = null;
                String cab = null;
                String box = null;
                if (rs.next()){
                    recordCode = rs.getString(1);
                    cab = rs.getString(2);
                    box = rs.getString(3);

                    businessOtherInfo += "INSERT INTO RECORD_STORE(ID,BUSINESS,RECORD_CODE,IN_ROOM,CREATE_TIME) VALUES("
                            + Q.v(Q.p(id),Q.p(id),Q.pm(recordCode),Q.p(cab != null && box != null),Q.pm(bizRs.getTimestamp(6)))
                            + ");";
                }

                rs = sD.executeQuery("select ID,DocType from shark..DGBizDoc " +
                        "where bizid = " + Q.p(bizRs.getString(8)));

                int pi = 1;
                while (rs.next()){
                    businessOtherInfo += "INSERT INTO BUSINESS_FILE(ID,BUSINESS_ID,NAME,NO_FILE,IMPORTANT,PRIORITY,RECORD_STORE) VALUES(" +

                            Q.v(Q.p(rs.getString(1)) , Q.p(id), Q.p(rs.getString(2)), Q.p(false), Q.p(false), String.valueOf(pi++),(recordCode == null ? "NULL": Q.p(id)))
                            + ");";
                    if (cab != null && box != null)
                        businessOtherInfo += "INSERT INTO RECORD_LOCAL(ID,FRAME,CABINET,BOX,RECORD_CODE) VALUES("+
                            Q.v(Q.p(rs.getString(1)), Q.p("1"),Q.pm(cab),Q.pm(box),Q.p(recordCode + (pi - 1)))
                            +");";
                    ResultSet rs2 = hD.executeQuery("select FileName,e.Name,e.NO,MD5Code,bf.ID,UpdateDate from shark..DGBizFile bf LEFT JOIN shark..DGEmployee e on e.ID = bf.EmployeeID where DocId =" + Q.p(rs.getString(1)));
                    while (rs2.next()){
                        businessOtherInfo += "INSERT INTO UPLOAD_FILE(FILE_NAME,EMP_NAME,EMP_CODE,MD5,BUSINESS_FILE_ID,ID,UPLOAD_TIME) VALUES(" +

                                Q.v(Q.p(rs2.getString(1)), Q.p(rs2.getString(2)), Q.p(rs2.getString(3)), Q.pm(rs2.getString(4)),Q.p(rs.getString(1)),Q.p(rs2.getString(5)),Q.p(rs2.getTimestamp(6)))
                                + ");";
                    }

                }


                rs = sD.executeQuery("select e.NO,e.Name,RegisterTime,e2.NO,e2.Name,FinalTime from DGHouseRecord..Business b left join DGEmployee e on e.ID = b.Finalworker left join DGEmployee e2 on e2.ID = b.enrolworker where (Finalworker is not null or enrolworker is not null) and  RecordBizNO = " + Q.p(id));

                if (rs.next()){
                    businessOtherInfo += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("

                    + Q.v(Q.p("c-" + id), Q.p("CHECK_EMP"),Q.p(rs.getString(1)),Q.p(rs.getString(2)),Q.p(id),Q.p(rs.getTimestamp(3)))
                    + "); INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES(" +
                        Q.v(Q.p("f-" + id),Q.p("REG_EMP"),Q.p(rs.getString(4)), Q.p(rs.getString(5)), Q.p(id),Q.p(rs.getTimestamp(6)))
                            + ");";
                }


                if (biz.getDefineId().equals("WP73")) {
                    String ownerId = "" + svs(sD, "close_people", id);

                    businessOtherInfo += "INSERT INTO CLOSE_HOUSE(ID,BUSINESS_ID,CLOSE_DOWN_CLOUR,CLOSE_DATE,LEGAL_DOCUMENTS,EXECUTION_NOTICE,SEND_PEOPLE,PHONE,EXECUTION_CARD_NO,WORK_CARD_NO) " +
                            " VALUES(" +
                                Q.v(Q.p(id),Q.p(id),Q.p(svs(sD,"closeDown_clour",id)), Q.pm(svt(sD,"close_date",id)), Q.p(svs(sD,"open_cardId",id)), Q.p(svs(sD, "open_file",id)), Q.p(ownerNameByNo(ownerId)),Q.p(ownerPhoneByNo(ownerId)),Q.p(svs(sD,"mark_workNo",id)),Q.p(svs(sD,"workNo",id)))
                            +");";
                }

                if (biz.getDefineId().equals("WP74")) {
                    String ownerId = "" + svs(sD, "open_people", id);
                    businessOtherInfo += "INSERT INTO HOUSE_CLOSE_CANCEL(ID,BUSINESS_ID,CANCEL_DATE,CANCEL_DOWN_CLOUR,LEGAL_DOCUMENTS,EXECUTION_NOTICE,SEND_PEOPLE,PHONE) " +
                            " VALUES(" +
                                Q.v(Q.p(id),Q.p(id),Q.pm(svt(sD,"open_date",id)),Q.pm(svs(sD,"open_clour",id)),Q.p(svs(sD,"open_cardId",id)), Q.p(svs(sD, "open_file",id)), Q.p(ownerNameByNo(ownerId)),Q.p(ownerPhoneByNo(ownerId)))
                            +");";
                }




                rs = sD.executeQuery("select VariableDefinitionId ,VariableValueVCHAR from " +
                        "(select db.RecordBizNO,db.nameid,sp.id,sp.oid from DGHouseRecord..Business as db " +
                        "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                        "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                        "where (spd.VariableDefinitionId = 'terrible_relation_people' " +
                        "or spd.VariableDefinitionId = 'correct_people' " +
                        "or spd.VariableDefinitionId = 'correct_people' " +
                        "or spd.VariableDefinitionId = 'entrust_deputy' " +
                        "or spd.VariableDefinitionId = 'mortgage_proxy' " +
                        "or spd.VariableDefinitionId = 'mortgage_obligee_proxy' " +
                        "or spd.VariableDefinitionId = 'pre_sell_proxy' " +
                        "or spd.VariableDefinitionId = 'pre_buy_proxy' " +
                        "or spd.VariableDefinitionId = 'record_agent' " +
                        "or spd.VariableDefinitionId = 'developer_proxy' " +
                        "or spd.VariableDefinitionId = 'buyer_proxy' " +
                        "or spd.VariableDefinitionId = 'ancester_proxy' " +
                        "or spd.VariableDefinitionId = 'heir_proxy' " +
                        "or spd.VariableDefinitionId = 'old_owner_proxy' " +
                        "or spd.VariableDefinitionId = 'new_owner_proxy' " +
                        "or spd.VariableDefinitionId = 'new_owner_proxy' " +
                        "or spd.VariableDefinitionId = 'accuser' " +
                        "or spd.VariableDefinitionId = 'open_accuser') " +
                        "and VariableValueVCHAR<>'' and RecordBizNO =" + Q.p(id));


                int ii = 1;
                while (rs.next()){
                    String persionType = rs.getString(1);
                    String type = null;
                    if (persionType=="terrible_relation_people") {
                        type="TERRIBLE_RELATION";
                    }

                    if ((persionType=="correct_people") || (persionType=="accuser")  || (persionType=="open_accuser")) {
                        type = "CORRECT";
                    }

                    if ((persionType=="entrust_deputy") || (persionType=="record_agent")) {
                        type = "OWNER_ENTRUST";
                    }

                    if (persionType=="mortgage_proxy") {
                        type = "MORTGAGE";
                    }

                    if (persionType=="mortgage_obligee_proxy") {
                        type = "MORTGAGE_OBLIGEE";
                    }

                    if ((persionType=="pre_sell_proxy") || (persionType=="developer_proxy")) {
                        type = "PRE_SALE_ENTRUST";
                    }
                    if (persionType=="pre_buy_proxy") {
                        type = "PRE_BUY_ENTRUST";
                    }


                    if ((persionType=="sellers_agent") || (persionType=="old_owner_proxy")  || (persionType=="ancester_proxy")) {
                        type = "SELL_ENTRUST";
                    }

                    if ((persionType=="buyer_proxy") || (persionType=="new_owner_proxy")  || (persionType=="heir_proxy")) {
                        type = "BUY_ENTRUST";
                    }



                    Statement sss = houseConn.createStatement();
                    ResultSet rsss = sss.executeQuery(" SELECT Name,IDType,IDNO,Phone,Address FROM OwnerInfo WHERE NO = '" + rs.getString(2) + "'");

                    String oss = null;
                    if (rsss.next()) {
                        oss = Q.v(Q.p(rsss.getString(3)), Q.pCardType(rsss.getInt(2)), Q.pm(rsss.getString(1)), Q.p(rsss.getString(4)));
                    }

                    if (type != null && oss != null){
                        businessOtherInfo += "INSERT INTO BUSINESS_PERSION(ID,BUSINESS_ID,TYPE,ID_NO,ID_TYPE,NAME,PHONE) VALUES("
                            + Q.v(Q.p(id+ "-" + ii),Q.p(id),Q.p(type)) + oss +
                                ");";
                    }
                }





                biz.setOtherBizInfo(businessOtherInfo);


                if (!biz.getDefineId().equals("WP40")) {

                    String oldOwnerId = bizRs.getString(31);

                    if (oldOwnerId == null || oldOwnerId.trim().equals("")) {
                        if (TAKE_LAST_OWNER_BIZ_LIST.contains(biz.getDefineId())) {
                            successWriter.write("MUST OWNER -->" + id);
                            successWriter.newLine();
                        }

                        biz.setOwnerId(null, null, null, false);
                    } else {
                        String oldCardId = null;
                        String owner = "";


                        rs = hD.executeQuery("select ID,NO,Type,Cancel,CardNO,Memo,PrintTime from HouseCard WHERE (Type = 111 or Type= 198) and BizID = '" + oldid + "' and ((OwnerID = '" + oldOwnerId + "') or (Type = 198))");


                        if (rs.next()) {
                            oldCardId = rs.getString(1);
                            owner = " INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                                    Q.v(
                                            Q.p(id), Q.pm(rs.getString(2)), Q.p(rs.getInt(3) == 111 ? "OWNER_RSHIP" : "NOTICE")

                                            , Q.p(id), Q.p(rs.getBoolean(4))

                                    )

                                    + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                                    Q.v(Q.p(id), Q.pm(rs.getString(5)), Q.p(rs.getString(6)), Q.p(rs.getTimestamp(7)))

                                    + ");";
                        }
                        rs.close();

//                        patchWriter.write(owner);
//                        patchWriter.write("UPDATE BUSINESS_OWNER set OWNER_CARD =" +Q.p(id) + " where ID = " +  Q.p(id) + ";");
//                        patchWriter.newLine();
//                        patchWriter.flush();
                        String ovalue = ownerInfo(oldOwnerId);

                        if (ovalue != null) {
                            //owner += "UPDATE BUSINESS_OWNER set OWNER_CARD =" +Q.p(id) + " where ID = " +  Q.p(id) + ";\n";

                            owner += "INSERT INTO  BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,PHONE,ROOT_ADDRESS,ADDRESS,BUSINESS,OWNER_CARD) VALUES(" +
                                    Q.v(Q.p(id), ovalue, Q.p(id), oldCardId == null ? "NULL" : Q.p(id))

                                    + ");";
                        }else
                            throw new MainOwnerNotFoundException(id);


                        biz.setOwnerId(oldOwnerId, oldCardId, owner, TAKE_LAST_OWNER_BIZ_LIST.contains(biz.getDefineId()));



                    }

                }



                rs = sD.executeQuery("select MEMO from DGBiz where code='" + bizRs.getString(33) + "'");
                rs.next();
                biz.setDefineName(rs.getString(1));
                rs.close();
                //sD.close();
                //sD = sharkConn.createStatement();
                rs = sD.executeQuery("SELECT dateadd(s,(cast(left(cast(A.LastStateTime as varchar(20)),10) as int(4))+8*60*60),'1970-01-01 00:00:00') as creaetime" +
                        " FROM SHKActivities AS A WHERE "
                        + " name = '启动' and processid='" + bizRs.getString(8)  + "'");

                if (rs.next())
                    biz.setApplyTime(rs.getTimestamp(1));

                rs.close();
                //sD.close();


                String startHouse = "INSERT INTO HOUSE(ID,HOUSE_ORDER,HOUSE_UNIT_NAME,IN_FLOOR_NAME,HOUSE_AREA," +
                        "USE_AREA,COMM_AREA,SHINE_AREA,LOFT_AREA,COMM_PARAM," +
                        "HOUSE_TYPE,USE_TYPE,STRUCTURE,KNOT_SIZE,ADDRESS," +
                        "EAST_WALL,WEST_WALL,SOUTH_WALL,NORTH_WALL,MAP_TIME," +
                        "DIRECTION,HOUSE_CODE,HAVE_DOWN_ROOM,    BUILD_CODE,MAP_NUMBER," +
                        "BLOCK_NO,BUILD_NO,STREET_CODE,DOOR_NO,UP_FLOOR_COUNT," +
                        "FLOOR_COUNT,DOWN_FLOOR_COUNT,BUILD_TYPE,PROJECT_CODE,PROJECT_NAME," +
                        "COMPLETE_DATE,DEVELOPER_CODE,DEVELOPER_NAME,SECTION_CODE,SECTION_NAME," +
                        "DISTRICT_CODE,DISTRICT_NAME,BUILD_NAME,BUILD_DEVELOPER_NUMBER)  " +
                        "VALUES(" + Q.v( Q.p(id + "-s"), Q.pm(bizRs.getString(9)), Q.p(bizRs.getString(10)),
                        Q.pm(bizRs.getString(11)), Q.pm(bizRs.getBigDecimal(12)),
                        Q.p(bizRs.getBigDecimal(13)), Q.p(bizRs.getBigDecimal(14)), Q.p(bizRs.getBigDecimal(15)),
                        Q.p(bizRs.getBigDecimal(16)), Q.p(bizRs.getBigDecimal(17)), Q.pmwc(bizRs.getString(18)),
                        Q.pmw(bizRs.getString(19), "808"), Q.pmw(bizRs.getString(20), "827"), Q.p(bizRs.getString(21))
                        , Q.pm(bizRs.getString(22)), Q.p(bizRs.getString(23)), Q.p(bizRs.getString(24)),
                        Q.p(bizRs.getString(25)), Q.p(bizRs.getString(26)), Q.p(bizRs.getTimestamp(27)),
                        Q.p(bizRs.getString(28)), Q.p(bizRs.getString(29)), "FALSE");


                String house = "INSERT INTO HOUSE(ID,HOUSE_ORDER,HOUSE_UNIT_NAME,IN_FLOOR_NAME,HOUSE_AREA," +
                        "USE_AREA,COMM_AREA,SHINE_AREA,LOFT_AREA,COMM_PARAM," +
                        "HOUSE_TYPE,USE_TYPE,STRUCTURE,KNOT_SIZE,ADDRESS," +
                        "EAST_WALL,WEST_WALL,SOUTH_WALL,NORTH_WALL,MAP_TIME," +
                        "DIRECTION,HOUSE_CODE,HAVE_DOWN_ROOM,    BUILD_CODE,MAP_NUMBER," +
                        "BLOCK_NO,BUILD_NO,STREET_CODE,DOOR_NO,UP_FLOOR_COUNT," +
                        "FLOOR_COUNT,DOWN_FLOOR_COUNT,BUILD_TYPE,PROJECT_CODE,PROJECT_NAME," +
                        "COMPLETE_DATE,DEVELOPER_CODE,DEVELOPER_NAME,SECTION_CODE,SECTION_NAME," +
                        "DISTRICT_CODE,DISTRICT_NAME,BUILD_NAME,BUILD_DEVELOPER_NUMBER,POOL_MEMO,MAIN_OWNER,REG_INFO,CONTRACT_OWNER)  " +
                        "VALUES(" + Q.v( Q.p(id), Q.pm(bizRs.getString(9)), Q.p(bizRs.getString(10)),
                        Q.pm(bizRs.getString(11)), Q.pm(bizRs.getBigDecimal(12)),
                        Q.p(bizRs.getBigDecimal(13)), Q.p(bizRs.getBigDecimal(14)), Q.p(bizRs.getBigDecimal(15)),
                        Q.p(bizRs.getBigDecimal(16)), Q.p(bizRs.getBigDecimal(17)), Q.pmwc(bizRs.getString(18)),
                        Q.pmw(bizRs.getString(19), "808"), Q.pmw(bizRs.getString(20), "827"), Q.p(bizRs.getString(21))
                        , Q.pm(bizRs.getString(22)), Q.p(bizRs.getString(23)), Q.p(bizRs.getString(24)),
                        Q.p(bizRs.getString(25)), Q.p(bizRs.getString(26)), Q.p(bizRs.getTimestamp(27)),
                        Q.p(bizRs.getString(28)), Q.p(bizRs.getString(29)), "FALSE");


                String buildId = bizRs.getString(30);
                //44

                //23
                //21 个
                String afterSql = null;

                if (buildId != null && !buildId.trim().equals("")) {
                    //sD = sharkConn.createStatement();

                    rs = hD.executeQuery("select c.BUILDNO, c.DoorNO,isNull(c.FloorCount,0),c.BuildType,c.ProjectID,projcetName, " +
                            "c.DeveloperID, dpr.name as developername,c.SectionID,c.sectionname,c.Districtno,c.Districtname,c.BuildName ,c.MapNO, c.BlockNO,c.BuildNO  from"
                            + " (select b.*,dd.name as Districtname,dd.no as Districtno from"
                            + " (select a.*,ds.name as sectionname,ds.DistrictID as sDistrictID from"
                            + " (select db.*,dp.name as projcetName,dp.sectionid as bsectionid,developerid,BuildSize"
                            + " from Build as db"
                            + " left join project as dp on db.projectid=dp.id) as a"
                            + " left join section as ds on a.bsectionid=ds.id) as b"
                            + " left join District as dd on b.sDistrictID=dd.id) as c"
                            + " left join Developer as dpr on c.developerid=dpr.id"
                            + " where c.id ='" + buildId + "'");

                    String developerName = null;
                    String developerId = null;
                    if (rs.next()) {
                        developerName = rs.getString(8);
                        developerId = rs.getString(7);
                        afterSql = "," + Q.v(Q.p(bizRs.getString(30)), Q.p(rs.getString(14)), Q.pm(rs.getString(15)), Q.pm(rs.getString(16))
                                , "NULL", Q.p(rs.getString(2)), String.valueOf(rs.getInt(3))
                                , String.valueOf(rs.getInt(3)), "0", Q.p(rs.getString(4)), Q.pmId(rs.getString(5)),
                                Q.pm(rs.getString(6)), "NULL", Q.p(rs.getString(7)), Q.p(rs.getString(8)),
                                Q.pmId(rs.getString(9)), Q.pm(rs.getString(10)), Q.pm(rs.getString(11)),
                                Q.pm(rs.getString(12)), Q.pm(rs.getString(13)), "NULL");


                    }
                    rs.close();

                    if (biz.getDefineId().equals("WP40")){
                        System.out.println(id);
                        rs = hD.executeQuery("select ID,NO,Type,Cancel,CardNO,Memo,PrintTime from HouseCard WHERE BizID = '" + oldid + "'");
                        String card = null;

                        String dOldCard = null;

                        if (rs.next()) {



                            card = "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                                    Q.v(
                                            Q.p(id), Q.pm(rs.getString(2)), Q.p("OWNER_RSHIP")

                                            , Q.p(id), Q.p(true)

                                    )

                                    + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                                    Q.v(Q.p(id), Q.pm(rs.getString(5)), Q.p(rs.getString(6)), Q.p(rs.getTimestamp(7)))

                                    + ");";

                            dOldCard = rs.getString(1);

                        }

                        String owner = "INSERT INTO  BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,BUSINESS,OWNER_CARD) VALUES(" +
                                Q.v(Q.p(id), Q.pm(developerName),Q.p("OTHER"),Q.pm(developerId), Q.p(id), card == null ? "NULL" : Q.p(id))

                                + ");";

                        if (card != null){
                            owner = card + owner;
                        }


                        biz.setOwnerId(developerId, dOldCard, owner, TAKE_LAST_OWNER_BIZ_LIST.contains(biz.getDefineId()));
                    }


                    //sD.close();
                }


                if (afterSql == null) {
                    afterSql = "," + Q.v("''", "NULL", "'未知'", "'未知'", "NULL", "NULL", "0"
                            , "0", "0", "NULL", "''",
                            "'未知'", "NULL", "NULL", "NULL",
                            "''", "'未知'", "'未知'",
                            "'未知'", "'未知'", "NULL");
                }

                house += afterSql;


                startHouse += afterSql + ");";

                biz.setStartHouse(startHouse);


                int poolType = bizRs.getInt(32);

                if (poolType == 221) {
                    house += "," + Q.p("SINGLE_OWNER");
                } else if (poolType == 222 || poolType == 218) {
                    house += "," + Q.p("TOGETHER_OWNER");
                } else if (poolType == 219) {
                    house += "," + Q.p("SHARE_OWNER");
                } else {
                    house += ",NULL";
                }


                house += biz.getNewOwnerId() == null ? ",NULL" : ",'" + biz.getNewOwnerId() + "'";


                rs = hD.executeQuery("select ho.Name,ho.IDType,ho.IDNO,hc.Relation,hc.PoolArea,hc.Perc,ho.Phone,hc.PrintTime,hc.Memo from houseCard hc left JOIN OwnerInfo ho on ho.ID = hc.OwnerID where hc.id is not null and (hc.type = 77) and (hc.no='' or hc.no is null) and bizid ='" + oldid + "'");

                int i = 0;

                // String poolOwner = "";
                while (rs.next()) {

                    biz.putPoolOwner(id + "-" + i, "INSERT INTO BUSINESS_POOL(ID,NAME,ID_TYPE,ID_NO,RELATION,POOL_AREA,PERC,PHONE,CREATE_TIME,MEMO,BUSINESS) VALUES(" +

                            Q.v(Q.p(id  + "-" + i), Q.p(rs.getString(1)), Q.pCardType(rs.getInt(2)), Q.pm(rs.getString(3)),
                                    Q.p(rs.getString(4)), Q.p(rs.getBigDecimal(5)), Q.p(rs.getString(6)),
                                    Q.p(rs.getString(7)), Q.pm(rs.getTimestamp(8)), Q.p(rs.getString(9)), Q.p(id))

                            + ");");

                    i++;


                }


                //biz.setPoolOwner(poolOwner);

                rs.close();


                int houseFrom = 0;
                int houseProperty = 0;

                rs = sD.executeQuery("SELECT VariableValueLONG from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'house_from' and sp.id = '" + bizRs.getString(8) + "'");

                if (rs.next()) {
                    houseFrom = rs.getInt(1);

                }

                rs.close();
                rs = sD.executeQuery("SELECT VariableValueLONG from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'house_property' and sp.id = '" + bizRs.getString(8) + "'");

                if (rs.next()) {
                    houseProperty = rs.getInt(1);
                }
                rs.close();

                if (houseFrom <= 0 && houseProperty <= 0) {
                    house += ",NULL";
                } else {
                    house = "INSERT INTO HOUSE_REG_INFO(ID,HOUSE_PORPERTY,HOUSE_FROM) VALUES(" +
                            Q.v(Q.p(id), houseProperty <= 0 ? "909" : String.valueOf(houseProperty)
                                    , houseFrom <= 0 ? "4270" : String.valueOf(houseFrom)

                            )

                            + ");" + house + "," + Q.p(id);
                }

                String contractOwner = null;
                String contractId = null;

                if (biz.getDefineId().equals("WP42")) {

                    rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'record_people' and sp.id = '" + bizRs.getString(8) + "'");
                    if (rs.next()) {

                        String owner = ownerInfoByNo(rs.getString(1));
                        if (owner != null) {


                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'house_remark_contract_id' and sp.id = '" + bizRs.getString(8) + "'");

                            if (rs.next() && rs.getString(1) != null && !rs.getString(1).trim().equals("")) {

                                contractId = Q.p(rs.getString(1));
                                rs.close();

                                String date = "'2000-1-1'";

                                rs = sD.executeQuery("SELECT VariableValueDATE from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'CONTRACT_DATE' and sp.id = '" + bizRs.getString(8) + "'");
                                if (rs.next()) {
                                    date = Q.pm(rs.getTimestamp(1));
                                }


                                contractOwner = "INSERT INTO CONTRACT_OWNER(CONTRACT_NUMBER,NAME,ID_TYPE,ID_NO,PHONE,ROOT_ADDRESS,ADDRESS,BUSINESS,CONTRACT_DATE,TYPE,HOUSE_CODE,ID)  VALUES(" +
                                        Q.v(contractId, owner, Q.p(id), date, "'MAP_SELL'", Q.p(houseCode),Q.p(id))
                                        + "); ";

                            }


                        }
                        rs.close();
                        //rs = sD.executeQuery()
                    }

                }

                if (contractOwner != null) {
                    house = contractOwner + house + "," + Q.p(id) + ");";
                } else {
                    house += ",NULL);";
                }


                biz.setHouse(house);


                rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'mortgage_obligee' and sp.id = '" + bizRs.getString(8) + "'");

                if (rs.next()) {
                    if (rs.getString(1) != null || !rs.getString(1).trim().equals("")) {

                        String fincNo = rs.getString(1);
                        rs.close(); //FINANCE_CORP
                        rs = hD.executeQuery("select Name,Phone from FinancialInfo WHERE NO ='" + fincNo + "'");
                        if (rs.next()) {

                            SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String finalSql = "INSERT INTO FINANCIAL(ID,NAME,PHONE,FINANCIAL_TYPE,CREATE_TIME, CARD) VALUES(" +
                                    Q.v(Q.p(id), Q.pm(rs.getString(1)), Q.p(rs.getString(2)), "'FINANCE_CORP'",
                                            Q.p(f.format(new Date())));

                            rs.close();
                            rs = hD.executeQuery("select ID,NO,Type,Cancel,CardNO,Memo,PrintTime from HouseCard WHERE  BizID = '" + oldid + "'");


                            String m_cardId = null;
                            if (rs.next()) {
                                finalSql = "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                                        Q.v(
                                                Q.p(id + "-t"), Q.pm(rs.getString(2)), Q.p("MORTGAGE_CARD")

                                                , Q.p(id), Q.p(rs.getBoolean(4))

                                        )

                                        + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                                        Q.v(Q.p(id + "-t"), Q.pm(rs.getString(5)), Q.p(rs.getString(6)), Q.p(rs.getTimestamp(7)))

                                        + ");" + finalSql + ",'" + id + "-t" +

                                        "');";
                                m_cardId = id + "-t";
                            } else {

                                if (biz.getDefineId().equals("WP83")) {
                                    throw new MustMOBizException(id, houseCode);
                                }

                                finalSql += ",NULL);";
                            }


                            String m1 = "0";
                            String m2 = "NULL";
                            String m3 = "'power.type.other'";
                            String m4 = "'2000-1-1'";
                            String m5 = "'2000-1-1'";
                            String m6 = "0";
                            rs = sD.executeQuery("SELECT VariableValueDBL from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'highest_mount_money' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next()) {
                                m1 = String.valueOf(rs.getDouble(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'warrant_scope' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && rs.getString(1) != null && !rs.getString(1).trim().equals("")) {
                                m2 = Q.p(rs.getString(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueLONG from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'interest_type' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && (rs.getInt(1) > 0)) {
                                m3 = Q.p(String.valueOf(rs.getInt(1)));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueDATE from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'mortgage_due_time_s' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && rs.getTimestamp(1) != null) {
                                m4 = Q.p(rs.getTimestamp(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueDATE from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'mortgage_due_time_e' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && rs.getTimestamp(1) != null) {
                                m5 = Q.p(rs.getTimestamp(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueDBL from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'MORTGAGE_AREA' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next()) {
                                m6 = String.valueOf(rs.getDouble(1));
                            }



                            String m_ownerId = biz.getNewOwnerId();
                            if (m_ownerId == null && (biz.getDefineId().equals("WP83") || biz.getDefineId().equals("WP84"))){


                                rs = hD.executeQuery("select d.Name, d.ID from House h left join Build b on b.ID = h.BuildID left join project p on p.ID = b.ProjectID left join Developer d on d.ID = p.DeveloperID where h.NO = " + Q.p(houseCode));


                                if (rs.next()) {
                                    finalSql += "INSERT INTO  BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,BUSINESS,OWNER_CARD) VALUES(" +
                                            Q.v(Q.p(id), Q.pm(rs.getString(1)), Q.p("OTHER"), Q.pm(rs.getString(2)), Q.p(id), m_cardId == null ? "NULL" : Q.p(m_cardId))

                                            + ");";
                                }else {
                                    finalSql += "INSERT INTO  BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,BUSINESS,OWNER_CARD) VALUES(" +
                                            Q.v(Q.p(id), Q.pm("未知"), Q.p("OTHER"), Q.pm("未知"), Q.p(id), m_cardId == null ? "NULL" : Q.p(m_cardId))

                                            + ");";
                                }
                                rs.close();
                                m_ownerId = id;
                            }else  if (m_ownerId == null){
                                throw new MainOwnerNotFoundException(id);
                            }

                            finalSql += "INSERT INTO MORTGAEGE_REGISTE(ID,HIGHEST_MOUNT_MONEY,WARRANT_SCOPE,INTEREST_TYPE,MORTGAGE_DUE_TIME_S,MORTGAGE_TIME,MORTGAGE_AREA,BUSINESS_ID,FIN,OWNER,ORG_NAME)  VALUES(" +
                                    Q.v(Q.p(id), m1, m2, m3, m4, m5, m6, Q.p(id), Q.p(id), Q.p(m_ownerId), Q.p("鞍山市经济开发区房产局"))
                             + ");";


                            biz.setMortgaeg(finalSql);


                            //
                        }

                        rs.close();


                    }
                }


                result.put(oldid, biz);
                first = biz;


            }
        }
        bizRs.close();
        statement.close();

        if (first != null) {
            Integer lastOldStatus = first.getOldStatus();
            boolean pass = false;
            if (lastOldStatus == null && (lastOldState == 127 || lastOldState == 118) ) {
                pass = true;
            }else if (lastOldStatus != null && lastOldStatus.equals(lastOldState)){
                pass = true;
            }
            if (!pass){
                successWriter.write(houseCode + ":old:" + lastOldState + "calc:" + first.getMainStatus());
                successWriter.newLine();
            }

            return first.run();
        }else
            return "";

    }


    private static String ownerInfo(String ownerId) throws SQLException {
        Statement statement = houseConn.createStatement();
        ResultSet rs = statement.executeQuery(" SELECT Name,IDType,IDNO,Phone,City,Address FROM OwnerInfo WHERE ID = '" + ownerId + "'");
        if (rs.next()) {
            return Q.v(Q.pm(rs.getString(1)), Q.pCardType(rs.getInt(2)), Q.pm(rs.getString(3)), Q.p(rs.getString(4)),
                    Q.p(rs.getString(5)), Q.p(rs.getString(6)));
        } else {
            return null;
        }
    }

    private static String ownerInfoByNo(String ownerId) throws SQLException {
        Statement statement = houseConn.createStatement();
        ResultSet rs = statement.executeQuery(" SELECT Name,IDType,IDNO,Phone,City,Address FROM OwnerInfo WHERE NO = '" + ownerId + "'");
        if (rs.next()) {
            return Q.v(Q.p(rs.getString(1)), Q.pCardType(rs.getInt(2)), Q.pm(rs.getString(3)), Q.p(rs.getString(4)),
                    Q.p(rs.getString(5)), Q.p(rs.getString(6)));
        } else {
            return null;
        }
    }

    private static String ownerNameByNo(String ownerId) throws SQLException {
        if (ownerId == null) return null;
        Statement statement = houseConn.createStatement();
        ResultSet rs = statement.executeQuery(" SELECT Name FROM OwnerInfo WHERE NO = '" + ownerId + "'");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }

    private static String ownerPhoneByNo(String ownerId) throws SQLException {
        if (ownerId == null) return null;
        Statement statement = houseConn.createStatement();
        ResultSet rs = statement.executeQuery(" SELECT Phone FROM OwnerInfo WHERE NO = '" + ownerId + "'");
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return null;
        }
    }


    public static void main(String[] args) {


        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.print("数据库驱动加载失败");
            e.printStackTrace();
            return;
        }
        try {
            houseConn = DriverManager.getConnection(HOUSE_DB_URL, "sa", "dgsoft");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("DGHouseInfo 连接失败");
            return;
        }
        try {
            sharkConn = DriverManager.getConnection(SHARK_DB_URL, "sa", "dgsoft");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("shark 连接失败");
            return;
        }
        try {
            recordConn = DriverManager.getConnection(RECORD_DB_URL, "sa", "dgsoft");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("record 连接失败");
            return;
        }

        System.out.println("数据库连接成功");


        File file = new File(OUT_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("sql 文件创建失败");
            e.printStackTrace();
            return;
        }


        file = new File(ERROR_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            errorWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("error 文件创建失败");
            e.printStackTrace();
            return;
        }


        file = new File(SUCCESS_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            successWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("success 文件创建失败");
            e.printStackTrace();
            return;
        }

                file = new File(PATCH_OUT_FILE_PATH);
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            patchWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("success 文件创建失败");
            e.printStackTrace();
            return;
        }
        try {
            begin();
        } finally {

            try {
                patchWriter.flush();
                patchWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                successWriter.flush();
                successWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                sqlWriter.flush();
                sqlWriter.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            try {
                errorWriter.flush();
                errorWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                houseConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                sharkConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                recordConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public static class NoSelectBizException extends Exception {
        private String bizId;

        public NoSelectBizException(String bizId) {
            this.bizId = bizId;
        }
    }


    public static class MainOwnerNotFoundException extends Exception {
        private String bizId;

        public MainOwnerNotFoundException(String bizId) {
            this.bizId = bizId;
        }
    }

    public static class MustHaveSelectBizException extends Exception {
        private String bizId;

        public MustHaveSelectBizException(String bizId) {
            this.bizId = bizId;
        }
    }


    public static class MustMOBizException extends Exception {

        private String bizId;

        private String houseCode;

        public MustMOBizException(String bizId,String houseCode) {
            this.bizId = bizId;
            this.houseCode = houseCode;
        }
    }

}
