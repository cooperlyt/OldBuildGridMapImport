package com.cooper.house;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 明水
 * Created by cooper on 5/13/16.
 */
public class MSDataBaseExport {


    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.200:1433/DGHouseInfo";

    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/oldBuildGridImport.sql";

    private static Connection conn;

    public static void main(String args[]) {

        try {

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            System.out.println("Connection successful");
            Statement statement = conn.createStatement();
            ResultSet hs = statement.executeQuery("select fj_qiuhao,fj_zhuanghao,fj_fanghao from c_fangji GROUP BY fj_qiuhao,fj_zhuanghao,fj_fanghao");
            File file = new File(OUT_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();

            while (hs.next()){
                genRecord(hs.getString(1),hs.getString(2),hs.getString(3));
            }



        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }

    }


    private static void genRecord(String blockNumber, String buildNumber, String houseNumber){
        try {
            Statement statement = conn.createStatement();
            ResultSet hs = statement.executeQuery("select DISTINCT fj_qiuhao,fj_zhuanghao,fj_fanghao from c_fangji");
        }catch (Exception e){
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }


    }

}
