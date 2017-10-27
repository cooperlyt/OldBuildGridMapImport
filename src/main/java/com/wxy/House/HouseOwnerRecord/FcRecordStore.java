package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2017-09-02.
 */
public class FcRecordStore {
    private static final String OUT_PATH_FILE = "/FcRecordStore.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE = "/FcRecordStoreError.sql";

    private static final String DB_FANG_CHAN_URL = "jdbc:jtds:sqlserver://192.168.1.2:1433/fang_chan";

    //private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://192.168.1.7:3306/HOUSE_OWNER_RECORD";

    private static Connection fangchanConnection;

    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;

    private static Statement statementFangchan;

    private static Statement statementFangchanCH;

    private static Statement statementFangchanCH1;

    private static Statement statementOwnerRecord;

    private static ResultSet fangChanResultSet;

    private static ResultSet fangChanResultSetCH;
    private static ResultSet fangChanResultSetCH1;

    private static ResultSet recordResultSet;

    private static ResultSet resultSetHouseRecord;

    private static String DEFINE_ID;

    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();

    private static boolean isFirst;

    private static Set<String> DEAL_DEFINE_ID= new HashSet<>();

    private static Set<String> MORTGAEGE_DEFINE_ID = new HashSet<>();

    public static void main(String agr[]) throws SQLException {

        recordFile = new File(OUT_PATH_FILE);
        if (recordFile.exists()){
            recordFile.delete();
        }

        houseOwnerErrorFile = new File(OUT_PATH_HouseOwnerError_FILE);
        if(houseOwnerErrorFile.exists()){
            houseOwnerErrorFile.delete();
        }

        try {
            recordFile.createNewFile();
            FileWriter fw = new FileWriter(recordFile.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);

            FileWriter houseOwnerErrorFileWriter = new FileWriter(houseOwnerErrorFile.getAbsoluteFile());
            houseOwnerError =new BufferedWriter(houseOwnerErrorFileWriter);
            sqlWriter.write("USE HOUSE_OWNER_RECORD;");

            sqlWriter.newLine();
            sqlWriter.flush();
        } catch (IOException e) {
            System.out.println("sqlWriter 文件创建失败");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            fangchanConnection = DriverManager.getConnection(DB_FANG_CHAN_URL, "sa", "dgsoft");
            statementFangchan = fangchanConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementFangchanCH = fangchanConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementFangchanCH1 = fangchanConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("fangchanConnection successful");
        } catch (Exception e) {
            System.out.println("fangchanConnection is errer");
            e.printStackTrace();
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "isNull");
            //ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "dgsoft");
            statementOwnerRecord = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            System.out.println("ownerRecordConnection successful");
        } catch (Exception e) {
            System.out.println("ownerRecordConnection is errer");
            e.printStackTrace();
            return;
        }


        try {
            //fangChanResultSet = statementFangchan.executeQuery("select * from RECORD_STORE WHERE KEYCODE='201608100087' OR KEYCODE='201611240070'");
            fangChanResultSet = statementFangchan.executeQuery("select * from RECORD_STORE");
            while(fangChanResultSet.next()){
                recordResultSet = statementOwnerRecord.executeQuery("SELECT * FROM OWNER_BUSINESS WHERE ID='"+fangChanResultSet.getString("keycode")+"'");
                if (recordResultSet.next()){
                    sqlWriter.write("INSERT RECORD_STORE (ID, RECORD_CODE, BUSINESS, VERSION, CREATE_TIME, CABINET, FRAME, BOX, ROOM) VALUE ");
                    sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("KEYCODE")),Q.pm(fangChanResultSet.getString("RECORD_CODE")),Q.pm(fangChanResultSet.getString("KEYCODE")),Q.pm("1"),
                            Q.p(fangChanResultSet.getTimestamp("CREATE_TIME")),Q.p(fangChanResultSet.getString("CABINET")),Q.p(fangChanResultSet.getString("FRAME"))
                            ,Q.p(fangChanResultSet.getString("BOX")),Q.p(fangChanResultSet.getString("FRAME"))
                                    + ");"));
                    sqlWriter.newLine();
                }
                recordResultSet = statementOwnerRecord.executeQuery("SELECT * FROM BUSINESS_FILE WHERE BUSINESS_ID='"+fangChanResultSet.getString("keycode")+"'");
                if(recordResultSet.next()){
                    sqlWriter.write("UPDATE BUSINESS_FILE SET RECORD_STORE='"+fangChanResultSet.getString("keycode")+"' WHERE BUSINESS_ID='"+fangChanResultSet.getString("keycode")+"';");
                    sqlWriter.newLine();
                }

                sqlWriter.flush();
            }







        } catch (Exception e) {
            System.out.println("keycode is errer-----"+fangChanResultSet.getString("statementFangchan"));
            e.printStackTrace();
            return;
        }





    }




}
