package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wxy on 2017-03-12.
 */
public class inBizBusiness {
    private static final String OUT_PATH_FILE = "/inbiz.sql";

    private static final String DB_HOUSE_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/DGHouseInfo";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static Connection houseConnection;

    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static File recordFile;

    private static Statement statementHouse;

    private static Statement statementOwnerRecord;

    public static void main(String agr[]){

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
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "dgsoft");
            statementOwnerRecord = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("ownerRecordConnection successful");
        } catch (Exception e) {
            System.out.println("ownerRecordConnection is errer");
            e.printStackTrace();
            return;
        }


        try {


            ResultSet setd = statementHouse.executeQuery("select b.*,oname.VariableValueVCHAR as sqr from " +
                    "(select a.*,at.name as jdname from " +
                    "(SELECT h.no,h.houseArea,h.houseStation,bn.name,bn.VariableValueVCHAR,bn.pro_id " +
                    "FROM House as h left join shark..record_biz_no as bn " +
                    "on h.inbizcode = bn.pro_id " +
                    "where h.InBiz = 1  and bn.pro_id is not null and bn.name not like '%补录%') " +
                    "as a left join shark..SHKActivities as at on a.pro_id=at.ProcessId " +
                    "where at.State=1000001 or at.State=1000003 or at.State=1000005) as b " +
                    "left join shark..owner_name as oname on b.pro_id=oname.pro_id " +
                    "where b.name <>'撤消商品房合同备案登记' and b.name <>'租赁登记' and b.name <>'商品房合同备案登记' " +
                    "order by b.name,b.jdname ");







            while (setd.next()) {
//                ResultSet setL = statementOwnerRecord.executeQuery("SELECT * FROM LOCKED_HOUSE WHERE HOUSE_CODE = '"+setd.getString("no")+"'");
//                if(setL.next() == false) {
                    sqlWriter.newLine();
                    sqlWriter.write("INSERT LOCKED_HOUSE(HOUSE_CODE, DESCRIPTION, TYPE, EMP_CODE, EMP_NAME, LOCKED_TIME, ID) values ("
                            + Q.v(Q.p(setd.getString("no")),Q.p("老系统业务未完成："+setd.getString("Name")), "'HOUSE_LOCKED'",
                            "'未知'", "'管理员'", Q.p(Q.nowFormatTime()), Q.p("A"+setd.getString("no"))) + ");");
                    sqlWriter.flush();
//                }

            }
            System.out.println("inbiz is complate");
        } catch (Exception e) {
            System.out.println("inbiz is errer");
            e.printStackTrace();
            return;
        }
    }


}
