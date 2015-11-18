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


    private static final String RECORD_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHOUSERECORD";

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
            sqlWriter.write("SET SESSION FOREIGN_KEY_CHECKS=0;");

            Statement statement = houseConn.createStatement();


            ResultSet bizRs = statement.executeQuery("SELECT  Business.NameID, Business.RecordBizNO FROM Business");






            while (bizRs.next()) {

                String id = bizRs.getString(2);
                String oid = bizRs.getString(1);


                sqlWriter.write(csql("BUSINESS_OWNER","ID",oid,id));
                sqlWriter.write(csql("MORTGAEGE_REGISTE","OWNER",oid,id));
                sqlWriter.write(csql("HOUSE","MAIN_OWNER",oid,id));
                sqlWriter.write(csql("HOUSE","ID",oid,id));
                sqlWriter.write(csql("BUSINESS_HOUSE","START_HOUSE",oid,id));
                sqlWriter.write(csql("BUSINESS_HOUSE","AFTER_HOUSE",oid,id));
                sqlWriter.write(csql("HOUSE_RECORD","HOUSE",oid,id));
                sqlWriter.write(csql("HOUSE_POOL","HOUSE",oid,id));







//                sqlWriter.write(csql("TASK_OPER","BUSINESS",oid,id));
//
//                sqlWriter.write(csql("BUSINESS_EMP","BUSINESS_ID",oid,id));
//
//                sqlWriter.write(csql("PROCESS_MESSAGES","BUSINESS_ID",oid,id));
//
//                sqlWriter.write(csql("MAPPING_CORP","BUSINESS_ID",oid,id));
//
//
//                sqlWriter.write(csql("HOUSE_CLOSE_CANCEL","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("CLOSE_HOUSE","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("EVALUATE","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("PROJECT","BUSINESS",oid,id));
//                sqlWriter.write(csql("RECORD_STORE","BUSINESS",oid,id));
//
//
//                sqlWriter.write(csql("MAKE_CARD","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("MORTGAEGE_REGISTE","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("BUSINESS_OWNER","BUSINESS",oid,id));
//                sqlWriter.write(csql("BUSINESS_POOL","BUSINESS",oid,id));
//                sqlWriter.write(csql("HOUSE_CARD_PATCH","BUSINESS",oid,id));
//                sqlWriter.write(csql("PROJECT_MORTGAGE","BUSINESS",oid,id));
//                sqlWriter.write(csql("SALE_INFO","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("REASON","BUISINESS_ID",oid,id));
//                sqlWriter.write(csql("BUSINESS_PERSION","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("CONTRACT_OWNER","BUSINESS",oid,id));
//                sqlWriter.write(csql("BUSINESS_HOUSE","BUSINESS_ID",oid,id));
//
//                sqlWriter.write(csql("BUSINESS_MONEY","BUSINESS",oid,id));
//                sqlWriter.write(csql("FACT_MONEYINFO","BUSINESS",oid,id));
//                sqlWriter.write(csql("CARD","BUSINESS_ID",oid,id));
//                sqlWriter.write(csql("OWNER_BUSINESS","ID",oid,id));
//                sqlWriter.write(csql("OWNER_BUSINESS","SELECT_BUSINESS",oid,id));



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
