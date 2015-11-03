package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by cooper on 11/2/15.
 */
public class Recode {


    private static final String HOUSE_DB_URL = "jdbc:mysql://127.0.0.1:3306/HOUSE_INFO";

    private static final String OUT_FILE_PATH = "/root/Documents/RECORD.sql";
    private static Connection houseConn;

    private static BufferedWriter sqlWriter;
    public static void  main(String[] args){

        try {
            Class.forName("com.mysql.jdbc.Driver");

            houseConn = DriverManager.getConnection(HOUSE_DB_URL, "root", "isNull");
        } catch (ClassNotFoundException e) {
            System.out.println( "database driver fail");
            return;
        } catch (SQLException e) {
            System.out.println("database driver fail");
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

        int i = 1;

        try {
            Statement statement = houseConn.createStatement();

            sqlWriter.write("SET SESSION FOREIGN_KEY_CHECKS=0;");
            ResultSet bizRs = statement.executeQuery("select ID from BUILD");

            while (bizRs.next()) {
                String newId = String.valueOf(i);
                while (newId.length() < 4){
                    newId = "0" + newId;
                }
                sqlWriter.write("update BUILD set ID='" + newId + "' where ID='" + bizRs.getString(1) + "';");
                sqlWriter.write("update BUILD_GRID_MAP set BUILD_ID='" + newId + "' where BUILD_ID='" + bizRs.getString(1) + "';"  );
                sqlWriter.write("update HOUSE set BUILDID='" + newId + "' where BUILDID='" + bizRs.getString(1) + "';");

                sqlWriter.write("update HOUSE_OWNER_RECORD.HOUSE set BUILD_CODE='" + newId + "' where BUILD_CODE='" + bizRs.getString(1) + "';");
                sqlWriter.write("update HOUSE_OWNER_RECORD.BUILD set BUILD_CODE='" + newId + "' where BUILD_CODE='" + bizRs.getString(1) + "';");
                sqlWriter.newLine();
                sqlWriter.flush();
                i++;
            }

            bizRs.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //SET SESSION FOREIGN_KEY_CHECKS=0;



        //
    }
}
