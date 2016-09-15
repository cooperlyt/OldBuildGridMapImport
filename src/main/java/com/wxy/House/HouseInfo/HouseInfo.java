package com.wxy.House.HouseInfo;

import com.cooper.house.Q;
import com.wxy.House.PinyinTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by wxy on 2016-09-15.
 */
public class HouseInfo {

    private static final String OUT_FILE_PATH = "/houseInfo.sql";

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.200:1433/DGHouseInfo";

    private static Connection houseInfoConn;

    private static BufferedWriter sqlWriter;

    private static File file;

    private static Statement statement;

    private static ResultSet resultSet;




    public static void main(String[] args){


        file = new File(OUT_FILE_PATH);

        if (file.exists()){
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

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            houseInfoConn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            statement = houseInfoConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Connection is errer");
            e.printStackTrace();
            return;
        }

        try {

            resultSet = statement.executeQuery("SELECT * FROM District");
            sqlWriter.write("use HOUSE_INFO;");
            sqlWriter.newLine();
            sqlWriter.write("INSERT DISTRICT (ID, NAME, SHORT_NAME, VERSION, CREATE_TIME) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("No")), Q.p(resultSet.getString("Name")), Q.p("东"), Q.p("0"), Q.p(Q.nowFormatTime())));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("DISTRICT is complate");

        } catch (Exception e) {
            System.out.println("DISTRICT is errer");
            e.printStackTrace();
            return;

        }


        try {
            ResultSet srt = statement.executeQuery("SELECT d.no as did,s.no as sid,s.name,s.CreateDate,s.Address,s.memo from District as d left join Section as s on d.id=s.DistrictID");
            sqlWriter.newLine();
            sqlWriter.write("DROP INDEX NAME ON HOUSE_INFO.SECTION;");
            sqlWriter.newLine();
            sqlWriter.write("INSERT SECTION (ID, NAME, PYCODE, ADDRESS, VERSION, CREATE_TIME, DISTRICT) values ");
            while (srt.next()){
                sqlWriter.write("("+ Q.v(Q.p("Ng6"+srt.getString("sid")), Q.p(srt.getString("Name")), Q.p(PinyinTools.getPinyinCode(srt.getString("Name"))),
                        Q.p(srt.getString("Address")),Q.p("0"), Q.p(srt.getTimestamp("CreateDate")),Q.p(srt.getString("did"))));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }



            sqlWriter.flush();
            System.out.println("Section is complate");
        } catch (Exception e) {
            System.out.println("Section is errer");
            e.printStackTrace();
            return;
        }


    }






}
