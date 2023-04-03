package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * 导入村镇预警信息
 */
public class DgTownsRecordRetain {
    private static final String BEGIN_DATA = "2023-03-17";
    private static final String OUT_PATH_FILE = "/DgTownsRetain.sql";
    private static final String DB_TOWNS_RECORD_URL="jdbc:mysql://127.0.0.1:3306/VILLAGES_TOWNS_HOUSES?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";
    private static Connection townsConnection;
    private static BufferedWriter sqlWriter;
    private static File retainFile;
    private static Statement statementRetain;
    private static ResultSet retainResultSet;

    public static void main(String agr[]) throws SQLException{

        retainFile = new File(OUT_PATH_FILE);
        if (retainFile.exists()){
            retainFile.delete();
        }

        try{
            retainFile.createNewFile();
            FileWriter fw = new FileWriter(retainFile.getAbsoluteFile());
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
            statementRetain = townsConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            System.out.println("townsConnection successful");
        }catch (Exception e){
            System.out.println("townsConnection is errer");
            e.printStackTrace();
            return;

        }

        try {
           // retainResultSet = statementRetain.executeQuery("select b.*,h.BUILDID from x_info_build b left join HOUSE_INFO.HOUSE h on b.house_key=h.ID where retain in('1','3','4','9','14') order by retain");
            retainResultSet = statementRetain.executeQuery("select b.*,h.BUILDID from x_info_build b left join HOUSE_INFO.HOUSE h on b.house_key=h.ID where retain in('11','12','13') order by retain");

            retainResultSet.last();
            int recordCount = retainResultSet.getRow();
            System.out.println(recordCount);
            retainResultSet.beforeFirst();
            int i=0;
            while (retainResultSet.next()){
                String textStr ="";
//                if(retainResultSet.getString("retain").equals("1")){
//                    textStr += "物业用房";
//                }
//                if(retainResultSet.getString("retain").equals("3")){
//                    textStr += "动迁保留";
//                    if (!retainResultSet.getString("retain_memo").equals("")
//                    &&  retainResultSet.getString("retain_memo")!=null) {
//                        textStr += ": " + retainResultSet.getString("retain_memo");
//                    }
//                }
//                if(retainResultSet.getString("retain").equals("4")){
//                    textStr="封存保留";
//                    if (!retainResultSet.getString("retain_memo").equals("")
//                            &&  retainResultSet.getString("retain_memo")!=null) {
//                        textStr += ": " + retainResultSet.getString("retain_memo");
//                    }
//                }
//                if(retainResultSet.getString("retain").equals("14")){
//                    textStr += "在不动产办理抵押登记";
//                    if (!retainResultSet.getString("retain_memo").equals("")
//                            &&  retainResultSet.getString("retain_memo")!=null) {
//                        textStr += ": " + retainResultSet.getString("retain_memo");
//                    }
//                }
//                if(retainResultSet.getString("retain").equals("9")){
//                    textStr += "分割排除";
//                }

                if(retainResultSet.getString("retain").equals("11")){
                    textStr += "原系统预警标识为11";
                    if (!retainResultSet.getString("retain_memo").equals("")
                            &&  retainResultSet.getString("retain_memo")!=null) {
                        textStr += ": " + retainResultSet.getString("retain_memo");
                    }
                }
                if(retainResultSet.getString("retain").equals("12")){
                    textStr += "原系统预警标识为12";
                    if (!retainResultSet.getString("retain_memo").equals("")
                            &&  retainResultSet.getString("retain_memo")!=null) {
                        textStr += ": " + retainResultSet.getString("retain_memo");
                    }
                }
                if(retainResultSet.getString("retain").equals("13")){
                    textStr += "原系统预警标识为13";
                    if (!retainResultSet.getString("retain_memo").equals("")
                            &&  retainResultSet.getString("retain_memo")!=null) {
                        textStr += ": " + retainResultSet.getString("retain_memo");
                    }
                }


                sqlWriter.write("INSERT LOCKED_HOUSE (HOUSE_CODE, DESCRIPTION, TYPE, EMP_CODE, EMP_NAME, ID, BUILD_CODE) VALUES ");
                sqlWriter.write("(" + Q.v(Q.p(retainResultSet.getString("house_key")),Q.p(textStr), "'HOUSE_LOCKED'","'root'"
                        , "'root'", Q.pm(retainResultSet.getString("house_key")+"BL"), Q.pm(retainResultSet.getString("BUILDID")) + ");"));
                sqlWriter.newLine();
                sqlWriter.flush();

                i++;
                System.out.println(i+"/"+String.valueOf(recordCount)+"--reg_code:"+retainResultSet.getString("house_key"));


            }


        }catch (Exception e){
            System.out.println("house_key is errer-----"+retainResultSet.getString("house_key"));
            e.printStackTrace();
            return;

        }


    }

}
