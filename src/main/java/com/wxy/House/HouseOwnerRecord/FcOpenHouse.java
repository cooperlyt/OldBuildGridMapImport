package com.wxy.House.HouseOwnerRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2017-08-05.
 */
public class FcOpenHouse {

    private static final String OUT_PATH_FILE = "/FcOpenHouse.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE = "/FcOpenHouseError.sql";

    //private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";


    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;


    private static Statement statementOwnerRecord;

    private static Statement statementOwnerRecordch;
    private static Statement statementOwnerRecordch2;

    private static ResultSet fangChanResultSet;

    private static ResultSet resultSetPerson;

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
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "dgsoft");
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
            resultSetHouseRecord = statementOwnerRecord.executeQuery("SELECT OWNER_BUSINESS.ID AS BID,HOUSE_CLOSE_CANCEL.*,HOUSE.ID as HID,HOUSE.ADDRESS,HOUSE.HOUSE_AREA,HOUSE.DESIGN_USE_TYPE,BUSINESS_HOUSE.HOUSE_CODE FROM OWNER_BUSINESS LEFT JOIN BUSINESS_HOUSE ON OWNER_BUSINESS.ID=BUSINESS_HOUSE.BUSINESS_ID" +
                    " LEFT JOIN HOUSE ON BUSINESS_HOUSE.AFTER_HOUSE=HOUSE.ID LEFT JOIN HOUSE_CLOSE_CANCEL ON OWNER_BUSINESS.ID=HOUSE_CLOSE_CANCEL.BUSINESS_ID" +
                    " WHERE OWNER_BUSINESS.STATUS<>'ABORT' AND OWNER_BUSINESS.STATUS<>'CANCEL'" +
                    // " AND (OWNER_BUSINESS.DEFINE_ID='WP73' or OWNER_BUSINESS.DEFINE_ID='OC') AND CLOSE_HOUSE.ID IS NOT NULL AND OWNER_BUSINESS.ID='WP73-201708020151'");
                    " AND (OWNER_BUSINESS.DEFINE_ID='WP74') AND HOUSE_CLOSE_CANCEL.ID IS NOT NULL");


            while(resultSetHouseRecord.next()){

                KeyGeneratorHelper key = new KeyGeneratorHelper();

                DescriptionDisplay businessDisplay = new DescriptionDisplay();
                businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "房屋编号");
                businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,resultSetHouseRecord.getString("HOUSE_CODE"));
                businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "查封法院");
                businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, resultSetHouseRecord.getString("CANCEL_DOWN_CLOUR"));




                if (resultSetHouseRecord.getString("BID").contains("WP")){
                    resultSetPerson = statementOwnerRecordch.executeQuery("SELECT * FROM BUSINESS_PERSION WHERE BUSINESS_ID ='"+resultSetHouseRecord.getString("BID")+"'");
                    if (resultSetPerson.next()){
                        key.addWord(resultSetPerson.getString("NAME"));
                        key.addWord(resultSetPerson.getString("ID_NO"));

                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "产权备案人 ");
                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,resultSetPerson.getString("NAME")+"["+resultSetPerson.getString("ID_NO")+"]");
                    }


                }else{
                    System.out.println(resultSetHouseRecord.getString("HID"));
                    resultSetPerson = statementOwnerRecordch.executeQuery("SELECT * FROM HOUSE_OWNER LEFT JOIN POWER_OWNER ON HOUSE_OWNER.POOL=POWER_OWNER.ID" +
                            " WHERE HOUSE_OWNER.HOUSE='"+ resultSetHouseRecord.getString("HID")+"' ORDER BY POWER_OWNER.PRI");
                    String contractPersonNames = "";
                    if (resultSetPerson.next()){
                        if (resultSetPerson.getString("Name")!=null){
                            key.addWord(resultSetPerson.getString("NAME"));
                        }
                        if(resultSetPerson.getString("ID_NO")!=null){
                            key.addWord(resultSetPerson.getString("ID_NO"));
                        }

                        if (!"".equals(contractPersonNames)){
                            contractPersonNames += ",";
                        }

                        contractPersonNames += resultSetPerson.getString("NAME")+'['+resultSetPerson.getString("ID_NO")+']';;
                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "产权备案人 ");
                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,contractPersonNames);
                    }
                }
                businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);


                if (resultSetHouseRecord.getString("LEGAL_DOCUMENTS")!=null){
                    key.addWord(resultSetHouseRecord.getString("LEGAL_DOCUMENTS"));

                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "法律文书");
                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, resultSetHouseRecord.getString("LEGAL_DOCUMENTS"));
                }
                if (resultSetHouseRecord.getString("EXECUTION_NOTICE")!=null){
                    key.addWord(resultSetHouseRecord.getString("EXECUTION_NOTICE"));
                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "协助执行通知书");
                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, resultSetHouseRecord.getString("EXECUTION_NOTICE"));

                }

                if(resultSetHouseRecord.getString("HOUSE_CODE")!=null){
                    key.addWord(resultSetHouseRecord.getString("HOUSE_CODE"));

                }
                if(resultSetHouseRecord.getString("HOUSECARDNO")!=null){
                    key.addWord(resultSetHouseRecord.getString("HOUSECARDNO"));

                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "所有权证号");
                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, resultSetHouseRecord.getString("HOUSECARDNO"));
                }

                resultSetPerson = statementOwnerRecordch.executeQuery("SELECT * from CARD where BUSINESS_ID='"+resultSetHouseRecord.getString("BID")+"'");
                while (resultSetPerson.next()){
                    if (resultSetPerson.getString("TYPE")!=null && resultSetPerson.getString("TYPE").equals("OLD_OWNER_RSHIP")){
                        if (resultSetPerson.getString("NUMBER")!=null) {
                            key.addWord(resultSetPerson.getString("NUMBER"));

                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "原权证号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, resultSetPerson.getString("NUMBER"));
                        }

                    }

                    if (resultSetPerson.getString("TYPE")!=null && resultSetPerson.getString("TYPE").equals("OWNER_RSHIP")){
                        if (resultSetPerson.getString("NUMBER")!=null) {
                            key.addWord(resultSetPerson.getString("NUMBER"));

                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "现权证号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, resultSetPerson.getString("NUMBER"));
                        }

                    }

                }




                businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);

                if(resultSetHouseRecord.getString("ADDRESS")!=null){
                    key.addWord(resultSetHouseRecord.getString("ADDRESS"));
                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,"房屋落座 " +resultSetHouseRecord.getString("ADDRESS"));
                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,"建筑面积 " +resultSetHouseRecord.getString("HOUSE_AREA"));
                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,"房屋用途 " +resultSetHouseRecord.getString("DESIGN_USE_TYPE"));
                }


                sqlWriter.write("UPDATE HOUSE_RECORD SET SEARCH_KEY='"+key.getKey()+"' WHERE HOUSE='" +resultSetHouseRecord.getString("HID")+"' AND HOUSE_CODE='"+resultSetHouseRecord.getString("HOUSE_CODE")+"';");
                sqlWriter.newLine();

                sqlWriter.write("UPDATE BUSINESS_HOUSE SET SEARCH_KEY='"+key.getKey()+"',DISPLAY='"+DescriptionDisplay.toStringValue(businessDisplay)+"' WHERE BUSINESS_ID='"+resultSetHouseRecord.getString("BID")+"';");
                sqlWriter.newLine();

                sqlWriter.flush();
                //System.out.println(DescriptionDisplay.toStringValue(businessDisplay));

            }







        } catch (Exception e) {
            System.out.println("KEYCODE is errer-----"+resultSetHouseRecord.getString("BID"));
            //System.out.println("yw_houseid is errer-----"+fangChanResultSet.getString("yw_houseid"));
            e.printStackTrace();
            return;
        }


    }
}
