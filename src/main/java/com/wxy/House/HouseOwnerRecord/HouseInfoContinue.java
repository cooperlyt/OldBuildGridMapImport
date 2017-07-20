package com.wxy.House.HouseOwnerRecord;
import com.cooper.house.Q;
import com.wxy.House.PinyinTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2017-01-06.
 */
public class HouseInfoContinue {

    private static final String OUT_PATH_FILE = "/notHouse.sql";

    private static final String DB_HOUSE_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/DGHouseInfo";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://192.168.248.246:3306/HOUSE_INFO";

    private static Connection houseConnection;

    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static File recordFile;

    private static Statement statementHouse;

    private static Statement statementOwnerRecord;

    private static ResultSet srta;


    public static void main(String agr[]) throws SQLException {

        recordFile = new File(OUT_PATH_FILE);
        if (recordFile.exists()){
            recordFile.delete();
        }

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            houseConnection = DriverManager.getConnection(DB_HOUSE_URL, "sa", "dgsoft");
            statementHouse = houseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //statementHousech = houseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("houseConnection successful");
        } catch (Exception e) {
            System.out.println("houseConnection is errer");
            e.printStackTrace();
            return;
        }

        try {
            recordFile.createNewFile();
            FileWriter fw = new FileWriter(recordFile.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);
            sqlWriter.write("USE HOUSE_OWNER_RECORD;");
            sqlWriter.newLine();

        } catch (IOException e) {
            System.out.println("sql 文件创建失败");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            //ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "isNull");
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "isNull");
            statementOwnerRecord = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("ownerRecordConnection successful");
        } catch (Exception e) {
            System.out.println("ownerRecordConnection is errer");
            e.printStackTrace();
            return;
        }


        try {

             srta =statementHouse.executeQuery("select a.*,w1.value,w1.memo as USE_TYPE from (select h.*,b.no as bno,b.buildname " +
                    " from house as h left join build b on h.buildid=b.id) as a " +
                    " left join shark..DGWordBook as w1 on a.UseType=w1.id WHERE a.no IS NOT NULL");


            while (srta.next()){
                  System.out.println(srta.getString("No"));
                  ResultSet srtb=statementOwnerRecord.executeQuery("select * from HOUSE where id ='"+srta.getString("No")+"'");

                if (srtb.next() == false){
                    System.out.println(srta.getString("bNo"));
                    sqlWriter.newLine();
                    sqlWriter.write("房屋ID:"+srta.getString("No")+"楼幢编号："+srta.getString("bno")+"楼幢名称："+srta.getString("buildname"));
                    sqlWriter.flush();
                }


            }
            System.out.println("notHouse is complate");
        } catch (Exception e) {

            System.out.println("notHouse is errer" +srta.getString("No"));
            e.printStackTrace();
            return;
        }
    }
}
