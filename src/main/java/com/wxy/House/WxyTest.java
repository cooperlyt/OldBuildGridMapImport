package com.wxy.House;

import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wxy on 2016-09-14.
 */
public class WxyTest {


    private static final String OUT_FILE_PATH = "/WXY.sql";

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.200:1433/DGHouseInfo";

    private static Connection conn;

    private static BufferedWriter sqlWriter;

    private static Connection houseConn;

    private static File file;



    public static void main(String[] args){

        file = new File(OUT_FILE_PATH);

        if (file.exists()) {
            file.delete();
        }


        try {//创建文件
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
            conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            System.out.println("Connection successful");
            Statement statement = conn.createStatement();
            ResultSet districtRs = statement.executeQuery("SELECT * FROM District");

//            Date CreateTime = new Date();
//            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//            sFormat.format(CreateTime);

            sqlWriter.write("INSERT DISTRICT (ID, NAME, SHORT_NAME, VERSION, CREATE_TIME) VALUES " );
            while (districtRs.next()){
                sqlWriter.write("("+Q.v(Q.p(districtRs.getString("No")),Q.p(districtRs.getString("Name")),Q.p("东"),Q.p("0"),Q.p(Q.nowFormatTime())));
               if(districtRs.isLast()) {
                   sqlWriter.write(");");
               }else {
                   sqlWriter.write("),");
               }

            }





            sqlWriter.flush();



            } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
