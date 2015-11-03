package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by cooper on 11/3/15.
 */
public class BuildConnect {

    private static final String OLD_HOUSE_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";

    private static final String HOUSE_DB_URL = "jdbc:mysql://127.0.0.1:3306/HOUSE_INFO";

    private static final String OUT_FILE_PATH = "/root/Documents/ONBUILDID.sql";
    private static final String ERROR_FILE_PATH = "/root/Documents/CONNERROR.sql";

    private static Connection houseConn;

    private static Connection old_houseConn;


    private static BufferedWriter sqlWriter;


    private static BufferedWriter errorWriter;
    public static void main(String arg[]){


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

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            old_houseConn =  DriverManager.getConnection(OLD_HOUSE_DB_URL, "sa", "dgsoft");
        } catch (ClassNotFoundException e) {
            System.out.print("数据库驱动加载失败");
            e.printStackTrace();
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


        file = new File(ERROR_FILE_PATH);

        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            errorWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("sql 文件创建失败");
            e.printStackTrace();
            return;
        }

        try {
            Statement statement  = houseConn.createStatement();
            ResultSet bizRs = statement.executeQuery("select ID,MAP_NUMBER,BLOCK_NO,BUILD_NO from BUILD");
            Statement os = old_houseConn.createStatement();

            while (bizRs.next()) {
                ResultSet oresult = os.executeQuery("SELECT ID,MapNO,BlockNO,BuildNO from Build where MapNO = " + Q.p(bizRs.getString(2)) + " and BlockNO=" + Q.p(bizRs.getString(3)) + " and BuildNO =" + Q.p(bizRs.getString(4)));
                if (oresult.next()){
                    if (!oresult.isLast()){

                        errorWriter.write(bizRs.getString(1) + "   " + "mulitError");
                        errorWriter.newLine();
                    }else{
                        sqlWriter.write(bizRs.getString(1) + " | " + oresult.getString(1));
                        sqlWriter.newLine();
                    }
                }else{
                    errorWriter.write(bizRs.getString(1) + "   " + "notfound error");
                    errorWriter.newLine();
                }
            }
            sqlWriter.flush();

            errorWriter.flush();
            bizRs.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
