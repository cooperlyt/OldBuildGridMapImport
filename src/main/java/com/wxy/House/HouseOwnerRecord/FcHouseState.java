package com.wxy.House.HouseOwnerRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2017-07-20.
 */
public class FcHouseState {
    private static final String OUT_PATH_FILE = "/FcHouseState.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE = "/FcHouseStateError.sql";

    //private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://192.168.1.7:3306/HOUSE_OWNER_RECORD";


    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;


    private static Statement statementOwnerRecord;

    private static Statement statementOwnerRecordch;
    private static Statement statementOwnerRecordch2;
    private static ResultSet fangChanResultSet;


    private static ResultSet recordResultSet;

    private static ResultSet resultSetHouseRecord;

    private static String DEFINE_ID;

    private static String houseState;

    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();

    private static boolean isFirst;

    private static Set<String> DEAL_DEFINE_ID= new HashSet<>();

    private static Set<String> MORTGAEGE_DEFINE_ID = new HashSet<>();

    public static void main(String agr[]) throws SQLException {

        recordFile = new File(OUT_PATH_FILE);
        if (recordFile.exists()){
            recordFile.delete();
        }

        houseOwnerErrorFile = new File(OUT_PATH_HouseOwnerError_FILE);
        if(houseOwnerErrorFile.exists()){
            houseOwnerErrorFile.delete();
        }




        try {
            recordFile.createNewFile();
            FileWriter fw = new FileWriter(recordFile.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);

            FileWriter houseOwnerErrorFileWriter = new FileWriter(houseOwnerErrorFile.getAbsoluteFile());
            houseOwnerError =new BufferedWriter(houseOwnerErrorFileWriter);
            sqlWriter.write("USE HOUSE_OWNER_RECORD;");

            sqlWriter.newLine();
            sqlWriter.flush();
        } catch (IOException e) {
            System.out.println("sqlWriter 文件创建失败");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "isNull");
            //ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "dgsoft");
            statementOwnerRecord = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementOwnerRecordch = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementOwnerRecordch2 = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("ownerRecordConnection successful");
        } catch (Exception e) {
            System.out.println("ownerRecordConnection is errer");
            e.printStackTrace();
            return;
        }



        try {
            resultSetHouseRecord = statementOwnerRecord.executeQuery("SELECT BUSINESS_HOUSE.HOUSE_CODE FROM OWNER_BUSINESS LEFT JOIN BUSINESS_HOUSE ON OWNER_BUSINESS.ID=BUSINESS_HOUSE.BUSINESS_ID" +
                    "  LEFT JOIN ADD_HOUSE_STATUS ON BUSINESS_HOUSE.ID=ADD_HOUSE_STATUS.BUSINESS" +
                    " WHERE DEFINE_ID<>'WP50' AND ADD_HOUSE_STATUS.ID IS NOT NULL" +
                    " AND (OWNER_BUSINESS.STATUS='COMPLETE') AND IS_REMOVE=0  GROUP BY BUSINESS_HOUSE.HOUSE_CODE");
            //AND BUSINESS_HOUSE.HOUSE_CODE='18224'

//            System.out.println("SELECT BUSINESS_HOUSE.HOUSE_CODE FROM OWNER_BUSINESS LEFT JOIN BUSINESS_HOUSE ON OWNER_BUSINESS.ID=BUSINESS_HOUSE.BUSINESS_ID" +
//                    "  LEFT JOIN ADD_HOUSE_STATUS ON BUSINESS_HOUSE.ID=ADD_HOUSE_STATUS.BUSINESS" +
//                    " WHERE DEFINE_ID<>'WP50' AND ADD_HOUSE_STATUS.ID IS NOT NULL" +
//                    " AND (OWNER_BUSINESS.STATUS='COMPLETE') AND IS_REMOVE=0 AND BUSINESS_HOUSE.HOUSE_CODE='47643' GROUP BY BUSINESS_HOUSE.HOUSE_CODE");
            while(resultSetHouseRecord.next()){

                houseState = null;
                ResultSet resultSet=statementOwnerRecordch.executeQuery("SELECT BUSINESS_HOUSE.*,ADD_HOUSE_STATUS.* FROM OWNER_BUSINESS LEFT JOIN BUSINESS_HOUSE ON OWNER_BUSINESS.ID=BUSINESS_HOUSE.BUSINESS_ID" +
                        " LEFT JOIN ADD_HOUSE_STATUS ON BUSINESS_HOUSE.ID=ADD_HOUSE_STATUS.BUSINESS" +
                        " WHERE DEFINE_ID<>'WP50' AND ADD_HOUSE_STATUS.ID IS NOT NULL" +
                        " AND BUSINESS_HOUSE.HOUSE_CODE ='"+resultSetHouseRecord.getString("HOUSE_CODE")+"'" +
                        " AND (OWNER_BUSINESS.STATUS='COMPLETE') AND IS_REMOVE=0 ORDER BY BUSINESS_HOUSE.HOUSE_CODE,OWNER_BUSINESS.RECORD_TIME");

//                System.out.println("SELECT BUSINESS_HOUSE.*,ADD_HOUSE_STATUS.* FROM OWNER_BUSINESS LEFT JOIN BUSINESS_HOUSE ON OWNER_BUSINESS.ID=BUSINESS_HOUSE.BUSINESS_ID" +
//                        " LEFT JOIN ADD_HOUSE_STATUS ON BUSINESS_HOUSE.ID=ADD_HOUSE_STATUS.BUSINESS" +
//                        " WHERE DEFINE_ID<>'WP50' AND ADD_HOUSE_STATUS.ID IS NOT NULL" +
//                        " AND BUSINESS_HOUSE.HOUSE_CODE ='"+resultSetHouseRecord.getString("HOUSE_CODE")+"'" +
//                        " AND (OWNER_BUSINESS.STATUS='COMPLETE') AND IS_REMOVE=0 ORDER BY BUSINESS_HOUSE.HOUSE_CODE,OWNER_BUSINESS.RECORD_TIME");

                int i=0,j=0;
                while (resultSet.next()){
                    String addHouseSate = resultSet.getString("STATUS");
                    if (addHouseSate.equals("INIT_REG")){
                        i=1;
                    }
                    if (addHouseSate.equals("CONTRACTS_RECORD")){
                        i=2;
                    }
                    if (addHouseSate.equals("OWNERED")){
                        i=3;
                    }

                    if (addHouseSate.equals("PROJECT_PLEDGE")){
                        i=4;
                    }
                    if (i>j){
                        j=i;
                    }
                }
                if(j==1){
                    houseState="INIT_REG";
                }
                if(j==2){
                    houseState="CONTRACTS_RECORD";
                }
                if(j==3){
                    houseState="OWNERED";
                }
                if(j==4){
                    houseState="PROJECT_PLEDGE";
                }
                if (i!=0){
                    ResultSet resultSetRecord =statementOwnerRecordch2.executeQuery("SELECT * FROM HOUSE_RECORD WHERE HOUSE_CODE='"+resultSetHouseRecord.getString("HOUSE_CODE")+"'");
                    if (resultSetRecord.next()){
                        if (resultSetRecord.getString("HOUSE_STATUS")!=null && !resultSetRecord.getString("HOUSE_STATUS").equals(houseState)){
                            sqlWriter.write("UPDATE HOUSE_RECORD SET HOUSE_STATUS='"+houseState+"' WHERE HOUSE_CODE='" +resultSetHouseRecord.getString("HOUSE_CODE")+"';");
                            sqlWriter.newLine();

                        }
                       // System.out.println(resultSetRecord.getString("HOUSE_STATUS"));
                        if (resultSetRecord.getString("HOUSE_STATUS") == null ){
                            sqlWriter.write("UPDATE HOUSE_RECORD SET HOUSE_STATUS='"+houseState+"' WHERE HOUSE_CODE='" +resultSetHouseRecord.getString("HOUSE_CODE")+"';");
                            sqlWriter.newLine();

                        }
                    }
                }
                sqlWriter.flush();



            }


        } catch (Exception e) {
            System.out.println("HOUSE_CODE is errer-----"+resultSetHouseRecord.getString("HOUSE_CODE"));
            //System.out.println("yw_houseid is errer-----"+fangChanResultSet.getString("yw_houseid"));
            e.printStackTrace();
            return;
        }


    }
}
