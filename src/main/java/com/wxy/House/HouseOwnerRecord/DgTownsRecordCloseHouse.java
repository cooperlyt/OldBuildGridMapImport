package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * 东港村镇查封信息
 */
public class DgTownsRecordCloseHouse {
    private static final String BEGIN_DATE = "2023-03-17";
    private static final String OUT_PATH_FILE="/DGTownsCloseHouse.sql";
    private static final String DB_TOWNS_RECORD_URL = "jdbc:mysql://127.0.0.1:3306/VILLAGES_TOWNS_HOUSES?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
    private static Connection townsConnection;
    private static BufferedWriter sqlWriter;
    private static File closeFile;
    private static Statement statementClose;
    private static ResultSet closeResultSet;

    public static void main(String agr[]) throws SQLException {
        closeFile = new File(OUT_PATH_FILE);
        if (closeFile.exists()){
            closeFile.delete();
        }

        try{
            closeFile.createNewFile();
            FileWriter fw = new FileWriter(closeFile.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);

            sqlWriter.write("USE HOUSE_OWNER_RECORD;");
            sqlWriter.newLine();
            sqlWriter.flush();

        }catch (IOException e){
            System.out.println("sqlWriter 文件创建失败");
            e.printStackTrace();
            return;
        }

        try{
            Class.forName("com.mysql.jdbc.Driver");
            townsConnection = DriverManager.getConnection(DB_TOWNS_RECORD_URL,"root","dgsoft");
            statementClose = townsConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            System.out.println("townsConnection successful");
        }catch (Exception e){
            System.out.println("townsConnection is errer");
            e.printStackTrace();
            return;

        }



        try {
            closeResultSet = statementClose.executeQuery("select f.*,h.BUILDID from x_info_frozen f left join HOUSE_INFO.HOUSE h on f.house_key=h.ID"
                    + " where h.ID is not null");
            closeResultSet.last();
            int recordCount = closeResultSet.getRow();
            System.out.println(recordCount);
            closeResultSet.beforeFirst();
            int i=0;
            while (closeResultSet.next()){
                String textStr ="业务编号:"+closeResultSet.getString("reg_code");
                if(closeResultSet.getString("apply_name")!=null){
                    textStr += " 申请人:"+ closeResultSet.getString("apply_name");
                }
                if(closeResultSet.getTimestamp("frozen_start")!=null){
                    textStr += " 冻结日期始:"+ closeResultSet.getString("frozen_start");
                }
                if(closeResultSet.getString("frozen_end")!=null){
                    textStr += " 至:"+ closeResultSet.getString("frozen_end");
                }
                if(closeResultSet.getString("file_code")!=null){
                    textStr += " 查封文号:"+ closeResultSet.getString("file_code");
                }
                if(closeResultSet.getString("reg_time")!=null){
                    textStr += " 登记时间:"+ closeResultSet.getString("reg_time");
                }
                if(closeResultSet.getString("reg_name")!=null){
                    textStr += " 登记人:"+ closeResultSet.getString("reg_name");
                }

                sqlWriter.write("INSERT LOCKED_HOUSE (HOUSE_CODE, DESCRIPTION, TYPE, EMP_CODE, EMP_NAME, ID, BUILD_CODE) VALUES ");
                sqlWriter.write("(" + Q.v(Q.p(closeResultSet.getString("house_key")),Q.p(textStr), "'CLOSE_REG'","'root'"
                        , "'root'", Q.pm(closeResultSet.getString("reg_code")+"BL"), Q.pm(closeResultSet.getString("BUILDID")) + ");"));
                sqlWriter.newLine();
                sqlWriter.flush();

                i++;
                System.out.println(i+"/"+String.valueOf(recordCount)+"--reg_code:"+closeResultSet.getString("reg_code"));


            }


        }catch (Exception e){
            System.out.println("house_key is errer-----"+closeResultSet.getString("house_key"));
            e.printStackTrace();
            return;

        }


    }

}
