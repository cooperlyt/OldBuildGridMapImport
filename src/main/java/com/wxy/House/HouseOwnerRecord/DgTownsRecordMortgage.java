package com.wxy.House.HouseOwnerRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by wxy on 2023-03-17.
 * 东港倒库程序，到所有抵押业务的最后有效的，倒成预警
 */
public class DgTownsRecordMortgage {

    private static final String BEGIN_DATE = "2023-03-17";
    private static final String OUT_PATH_FILE = "/DGTownsMortgage.sql";
    private static final String OUT_PATH_ERROR_FILE = "/DGTownsMortgageError.sql";
    private static final String DB_TOWNS_RECORD_URL = "jdbc:mysql://127.0.0.1:3306/VILLAGES_TOWNS_HOUSES";
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

    }

}
