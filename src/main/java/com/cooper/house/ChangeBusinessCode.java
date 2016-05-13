package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by cooper on 11/14/15.
 */
public class ChangeBusinessCode {


    private static final String RECORD_DB_URL = "jdbc:jtds:sqlserver://192.168.1.200:1433/DGHOUSEINFO";

    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/RECORDBIZ.sql";
    private static Connection houseConn;

    private static BufferedWriter sqlWriter;

    private static String csql(String table, String field, String old , String id){
        return "UPDATE " + table + " set " + field + "=" + Q.p(id) + " where " + field + "=" + Q.p(old) + ";";
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
            houseConn = DriverManager.getConnection(RECORD_DB_URL, "sa", "dgsoft");
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


        try {


            Statement statement = houseConn.createStatement();


            ResultSet bizRs = statement.executeQuery("SELECT  ID,NO FROM DEVELOPER ");






            while (bizRs.next()) {

                String id = "D" + bizRs.getString(2);
                String oid = bizRs.getString(1);


                sqlWriter.write(csql("HOUSE","DEVELOPER_CODE",oid,id));








                sqlWriter.flush();
                sqlWriter.newLine();

            }


            bizRs.close();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
