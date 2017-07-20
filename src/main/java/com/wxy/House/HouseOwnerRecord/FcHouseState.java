package com.wxy.House.HouseOwnerRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2017-07-20.
 */
public class FcHouseState {
    private static final String OUT_PATH_FILE = "/FCBusinessFile.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE = "/FCBusinessFileError.sql";

    //private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://192.168.1.7:3306/HOUSE_OWNER_RECORD";


    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;


    private static Statement statementOwnerRecord;

    private static ResultSet fangChanResultSet;


    private static ResultSet recordResultSet;

    private static ResultSet resultSetHouseRecord;

    private static String DEFINE_ID;

    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();

    private static boolean isFirst;

    private static Set<String> DEAL_DEFINE_ID= new HashSet<>();

    private static Set<String> MORTGAEGE_DEFINE_ID = new HashSet<>();


}
