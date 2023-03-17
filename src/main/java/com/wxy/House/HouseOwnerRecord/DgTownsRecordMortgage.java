package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * Created by wxy on 2023-03-17.
 * 东港倒库程序，到所有抵押业务的最后有效的，倒成预警
 */
public class DgTownsRecordMortgage {

    private static final String BEGIN_DATE = "2023-03-17";
    private static final String OUT_PATH_FILE = "/DGTownsMortgage.sql";
    private static final String OUT_PATH_ERROR_FILE = "/DGTownsMortgageError.sql";
    private static final String DB_TOWNS_RECORD_URL = "jdbc:mysql://127.0.0.1:3306/VILLAGES_TOWNS_HOUSES?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
    private static Connection townsConnection;
    private static BufferedWriter sqlWriter;
    private static BufferedWriter mortgageError;
    private static File mortgageFile;
    private static File mortgageErrorFile;
    private static Statement statementMortgage;
    private static ResultSet mortgageResultSet;


    public static void main(String agr[]) throws SQLException {

        mortgageFile = new File(OUT_PATH_FILE);
        if (mortgageFile.exists()){
            mortgageFile.delete();
        }
        mortgageErrorFile = new File(OUT_PATH_ERROR_FILE);
        if(mortgageErrorFile.exists()){
            mortgageErrorFile.delete();
        }

        try{
            mortgageFile.createNewFile();
            FileWriter fw = new FileWriter(mortgageFile.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);

            FileWriter mortgageErrorFileWriter = new FileWriter(mortgageErrorFile.getAbsoluteFile());
            mortgageError =new BufferedWriter(mortgageErrorFileWriter);
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
            statementMortgage = townsConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            System.out.println("townsConnection successful");
        }catch (Exception e){
            System.out.println("townsConnection is errer");
            e.printStackTrace();
            return;

        }

        try {
            mortgageResultSet = statementMortgage.executeQuery("select M.*,H.BUILDID from x_info_mort M left join HOUSE_INFO.HOUSE H on M.house_key_sub = H.ID"
//            + " where H.ID is null"+" and house_key_sub = '210681101Z016112'");
                    + " where H.ID is not null");
            mortgageResultSet.last();
            int recordCount = mortgageResultSet.getRow();
            System.out.println(recordCount);
            mortgageResultSet.beforeFirst();
            int i=0;
            while (mortgageResultSet.next()){
                String textStr ="业务编号:"+mortgageResultSet.getString("reg_code")
                        +" 抵押人:"+mortgageResultSet.getString("o_name")
                        +" 抵押金额:"+mortgageResultSet.getString("money_m");
                if(mortgageResultSet.getString("m_type_process")!=null){
                    textStr += " 业务类型:"+ mortgageResultSet.getString("m_type_process");
                }
                if(mortgageResultSet.getString("m_name")!=null){
                    textStr += " 抵押权人:"+ mortgageResultSet.getString("m_name");
                }
                if(mortgageResultSet.getTimestamp("m_date_s")!=null){
                    textStr += " 抵押日期始:"+ mortgageResultSet.getString("m_date_s");
                }
                if(mortgageResultSet.getString("m_date_e")!=null){
                    textStr += " 至:"+ mortgageResultSet.getString("m_date_e");
                }
                if(mortgageResultSet.getString("c_reg_date")!=null){
                    textStr += " 登记时间:"+ mortgageResultSet.getString("c_reg_date");
                }
                if(mortgageResultSet.getString("c_reg_name")!=null){
                    textStr += " 登记人:"+ mortgageResultSet.getString("c_reg_name");
                }

                sqlWriter.write("INSERT LOCKED_HOUSE (HOUSE_CODE, DESCRIPTION, TYPE, EMP_CODE, EMP_NAME, ID, BUILD_CODE) VALUES ");
                sqlWriter.write("(" + Q.v(Q.p(mortgageResultSet.getString("house_key_sub")),Q.p(textStr), "'MORTGAGE_REEG'","'root'"
                        , "'root'", Q.pm(mortgageResultSet.getString("reg_code")), Q.pm(mortgageResultSet.getString("BUILDID")) + ");"));
                sqlWriter.newLine();
                sqlWriter.flush();

                i++;
                System.out.println(i+"/"+String.valueOf(recordCount)+"--reg_code:"+mortgageResultSet.getString("reg_code"));
            }

        }catch (Exception e){
            System.out.println("house_key is errer-----"+mortgageResultSet.getString("house_key_sub"));
            e.printStackTrace();
            return;

        }






    }

}
