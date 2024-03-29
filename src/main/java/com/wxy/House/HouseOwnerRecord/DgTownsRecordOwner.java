package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

/**
 * 东港村镇倒库
 *产权 备案
 */
public class DgTownsRecordOwner {

   private static final String BEGIN_DATE = "2023-03-09";
   private static final String OUT_PATH_FILE = "/DGTownsRecord.sql";
   private static final String OUT_PATH_ERROR_FILE = "/DGTownsRecordError.sql";
   private static final String DB_TOWNS_RECORD_URL = "jdbc:mysql://127.0.0.1:3306/VILLAGES_TOWNS_HOUSES";
   private static Connection townsConnection;
   private static BufferedWriter sqlWriter;
   private static BufferedWriter houseOwnerError;
   private static File recordFile;
   private static File houseOwnerErrorFile;
   private static Statement statementOwner;
   private static Statement statementHouse;
   private static ResultSet houseResultSet;
   private static ResultSet ownerResultSet;
   public static void main(String agr[]) throws SQLException {

       recordFile = new File(OUT_PATH_FILE);
       if(recordFile.exists()){
           recordFile.delete();
       }

       houseOwnerErrorFile = new File(OUT_PATH_ERROR_FILE);
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
           // 注册 JDBC 驱动
           Class.forName("com.mysql.jdbc.Driver");
           // 打开链接
           townsConnection = DriverManager.getConnection(DB_TOWNS_RECORD_URL, "root", "dgsoft");
           statementHouse = townsConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           statementOwner = townsConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           System.out.println("townsConnection successful");
       } catch (Exception e) {
           System.out.println("townsConnection is errer");
           e.printStackTrace();
           return;
       }

       try {
           //houseResultSet = statementHouse.executeQuery("select * from x_info_build where data_flag=0  and house_key="+" '210681104000042'"+" order by project_name,x_info_build.build_unit,house_key");
//           houseResultSet = statementHouse.executeQuery("select * from x_info_build where data_flag=0  order by project_name,x_info_build.build_unit,house_key");
           houseResultSet = statementHouse.executeQuery("SELECT D.ID AS DID,D.NAME AS DNAME,S.ID AS SID,S.NAME AS SNAME,P.ID AS PID,P.NAME AS PNAME,"
           + "DP.ID AS DPID,DP.NAME AS DPNAME,B.ID as BID,B.NAME AS BNAME,B.MAP_NUMBER,B.BLOCK_NO,B.BUILD_NO,B.DOOR_NO,B.BUILD_TYPE,B.COMPLETE_DATE,B.DEVELOPER_NUMBER,"
           + "B.UP_FLOOR_COUNT,B.DOWN_FLOOR_COUNT,H.* "
           + "FROM VILLAGES_TOWNS_HOUSES.x_info_build XB "
           + "LEFT JOIN HOUSE_INFO.HOUSE H ON XB.house_key = H.ID "
           + "LEFT JOIN HOUSE_INFO.BUILD B ON H.BUILDID=B.ID "
           + "LEFT JOIN HOUSE_INFO.PROJECT P ON B.PROJECT_ID=P.ID "
           + "LEFT JOIN HOUSE_INFO.SECTION S ON P.SECTIONID = S.ID "
           + "LEFT JOIN HOUSE_INFO.DISTRICT D ON S.DISTRICT = D.ID "
           + "LEFT JOIN HOUSE_INFO.DEVELOPER DP ON P.DEVELOPERID = DP.ID  "
           + "WHERE data_flag=0  AND H.ID IS NOT NULL and H.ID= '"+"21068110442589460238700391"+"'"
//           + "WHERE data_flag=0  AND H.ID IS NOT NULL "
           + "order by P.NAME,B.NAME,H.HOUSE_ORDER"
           );

           houseResultSet.last();
           int recordCount = houseResultSet.getRow();
           System.out.println(recordCount);
           houseResultSet.beforeFirst();
           sqlWriter.newLine();
           int i=0;
           // 建筑信息表所有启用的房子，data_flag=0,按reg_time取最后一条记录
           while (houseResultSet.next()){
//               ownerResultSet = statementOwner.executeQuery("select * from x_info_owner as o where o.data_flag=0 and house_key= "
//                       +"'"+houseResultSet.getString("ID")+"' "
//                       +"and o.reg_time = (select max(reg_time) from x_info_owner as o1 where o1.data_flag=0 and o1.house_key= "
//                       +"'"+houseResultSet.getString("ID")+"')"
//               );
               // 指定查一条

               ownerResultSet = statementOwner.executeQuery("select * from x_info_owner as o where reg_code='HT005003'"



               );

               ownerResultSet.last();
               if (ownerResultSet.getRow()>0){
                   ownerResultSet.first();
                   /**
                    * OWNER_BUSINESS
                    */
                   sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                           " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");


                   sqlWriter.write("(" + Q.v(Q.p(ownerResultSet.getString("reg_code")), "0", "'BIZ_IMPORT'", Q.p(ownerResultSet.getString("owner_memo"))
                           , "'COMPLETE'", Q.p("房屋备案档案补录"), Q.p("BL42"), "0", null, Q.p(ownerResultSet.getTimestamp("reg_time"))
                           , Q.p(ownerResultSet.getTimestamp("reg_time")), "Null", Q.p(ownerResultSet.getTimestamp("reg_time")), Q.p(ownerResultSet.getTimestamp("reg_time")), "False", "'NORMAL_BIZ'") + ");");
                   sqlWriter.newLine();

                   /**
                    * START_HOSUE
                    */
                   sqlWriter.write("INSERT HOUSE (ID, HOUSE_ORDER, HOUSE_UNIT_NAME, IN_FLOOR_NAME, " +
                           "HOUSE_AREA, USE_AREA, COMM_AREA, SHINE_AREA, LOFT_AREA, COMM_PARAM, " +
                           "HOUSE_TYPE, USE_TYPE, STRUCTURE, ADDRESS, " +
                           "MAP_TIME,HOUSE_CODE, " +
                           "HAVE_DOWN_ROOM, BUILD_CODE, MAP_NUMBER, BLOCK_NO, BUILD_NO, " +
                           "DOOR_NO, UP_FLOOR_COUNT, FLOOR_COUNT, DOWN_FLOOR_COUNT, BUILD_TYPE, " +
                           "PROJECT_CODE, PROJECT_NAME, COMPLETE_DATE, DEVELOPER_CODE, DEVELOPER_NAME," +
                           " SECTION_CODE, SECTION_NAME, DISTRICT_CODE, DISTRICT_NAME, BUILD_NAME, " +
                           "BUILD_DEVELOPER_NUMBER, POOL_MEMO, MAIN_OWNER, LAND_INFO, REG_INFO, DESIGN_USE_TYPE, " +
                           "UNIT_NUMBER) VALUES");
                   sqlWriter.write("(" + Q.v(Q.p(houseResultSet.getString("ID")+"-S1"), Q.pm(houseResultSet.getString("HOUSE_ORDER"))
                           , Q.pm(houseResultSet.getString("HOUSE_UNIT_NAME")), Q.pm(houseResultSet.getString("IN_FLOOR_NAME"))
                           , Q.pm(houseResultSet.getBigDecimal("HOUSE_AREA")), Q.pm(houseResultSet.getBigDecimal("USE_AREA"))
                           , Q.pm(houseResultSet.getBigDecimal("COMM_AREA")), Q.pm(houseResultSet.getBigDecimal("SHINE_AREA"))
                           , Q.pm(houseResultSet.getBigDecimal("LOFT_AREA")), Q.pm(houseResultSet.getBigDecimal("COMM_PARAM"))
                           , Q.p(houseResultSet.getString("HOUSE_TYPE")),Q.pm(houseResultSet.getString("USE_TYPE"))
                           , Q.pm(houseResultSet.getString("STRUCTURE"))
                           , Q.pm(houseResultSet.getString("ADDRESS")), Q.pm(houseResultSet.getString("CREATE_TIME"))
                           , Q.pm(houseResultSet.getString("ID")), "False", Q.pm(houseResultSet.getString("BID")), Q.p(houseResultSet.getString("MAP_NUMBER")), Q.pm(houseResultSet.getString("BLOCK_NO"))
                           , Q.pm(houseResultSet.getString("BUILD_NO")), Q.p(houseResultSet.getString("DOOR_NO")), Q.pm(houseResultSet.getString("UP_FLOOR_COUNT"))
                           , Q.pm(Integer.toString(houseResultSet.getInt("UP_FLOOR_COUNT")+houseResultSet.getInt("DOWN_FLOOR_COUNT"))),Q.pm(houseResultSet.getString("DOWN_FLOOR_COUNT"))
                           , Q.p(houseResultSet.getString("BUILD_TYPE")), Q.pm(houseResultSet.getString("PID")), Q.pm(houseResultSet.getString("PNAME"))
                           , Q.pm(houseResultSet.getString("COMPLETE_DATE"))
                           , Q.pm(houseResultSet.getString("DPID")), Q.pm(houseResultSet.getString("DPNAME")), Q.pm(houseResultSet.getString("SID"))
                           , Q.pm(houseResultSet.getString("SNAME")), Q.pm(houseResultSet.getString("DID")), Q.pm(houseResultSet.getString("DNAME"))
                           , Q.pm(houseResultSet.getString("BNAME")), Q.p(houseResultSet.getString("DEVELOPER_NUMBER"))
                           , "Null", "Null", "Null", "Null",Q.pm(houseResultSet.getString("DESIGN_USE_TYPE"))
                           , Q.p(houseResultSet.getString("UNIT_NUMBER")) + ");"));

                   sqlWriter.newLine();
                   /**
                    * AFTER_HOUSE
                    */
                   sqlWriter.write("INSERT HOUSE (ID, HOUSE_ORDER, HOUSE_UNIT_NAME, IN_FLOOR_NAME, " +
                           "HOUSE_AREA, USE_AREA, COMM_AREA, SHINE_AREA, LOFT_AREA, COMM_PARAM, " +
                           "HOUSE_TYPE, USE_TYPE, STRUCTURE, ADDRESS, " +
                           "MAP_TIME,HOUSE_CODE, " +
                           "HAVE_DOWN_ROOM, BUILD_CODE, MAP_NUMBER, BLOCK_NO, BUILD_NO, " +
                           "DOOR_NO, UP_FLOOR_COUNT, FLOOR_COUNT, DOWN_FLOOR_COUNT, BUILD_TYPE, " +
                           "PROJECT_CODE, PROJECT_NAME, COMPLETE_DATE, DEVELOPER_CODE, DEVELOPER_NAME," +
                           " SECTION_CODE, SECTION_NAME, DISTRICT_CODE, DISTRICT_NAME, BUILD_NAME, " +
                           "BUILD_DEVELOPER_NUMBER, POOL_MEMO, MAIN_OWNER, LAND_INFO, REG_INFO, DESIGN_USE_TYPE, " +
                           "UNIT_NUMBER) VALUES");
                   sqlWriter.write("(" + Q.v(Q.p(houseResultSet.getString("ID")+"-1"), Q.pm(houseResultSet.getString("HOUSE_ORDER"))
                           , Q.pm(houseResultSet.getString("HOUSE_UNIT_NAME")), Q.pm(houseResultSet.getString("IN_FLOOR_NAME"))
                           , Q.pm(houseResultSet.getBigDecimal("HOUSE_AREA")), Q.pm(houseResultSet.getBigDecimal("USE_AREA"))
                           , Q.pm(houseResultSet.getBigDecimal("COMM_AREA")), Q.pm(houseResultSet.getBigDecimal("SHINE_AREA"))
                           , Q.pm(houseResultSet.getBigDecimal("LOFT_AREA")), Q.pm(houseResultSet.getBigDecimal("COMM_PARAM"))
                           , Q.p(houseResultSet.getString("HOUSE_TYPE")),Q.pm(houseResultSet.getString("USE_TYPE"))
                           , Q.pm(houseResultSet.getString("STRUCTURE"))
                           , Q.pm(houseResultSet.getString("ADDRESS")), Q.pm(houseResultSet.getString("CREATE_TIME"))
                           , Q.pm(houseResultSet.getString("ID")), "False", Q.pm(houseResultSet.getString("BID")), Q.p(houseResultSet.getString("MAP_NUMBER")), Q.pm(houseResultSet.getString("BLOCK_NO"))
                           , Q.pm(houseResultSet.getString("BUILD_NO")), Q.p(houseResultSet.getString("DOOR_NO")), Q.pm(houseResultSet.getString("UP_FLOOR_COUNT"))
                           , Q.pm(Integer.toString(houseResultSet.getInt("UP_FLOOR_COUNT")+houseResultSet.getInt("DOWN_FLOOR_COUNT"))),Q.pm(houseResultSet.getString("DOWN_FLOOR_COUNT"))
                           , Q.p(houseResultSet.getString("BUILD_TYPE")), Q.pm(houseResultSet.getString("PID")), Q.pm(houseResultSet.getString("PNAME"))
                           , Q.pm(houseResultSet.getString("COMPLETE_DATE"))
                           , Q.pm(houseResultSet.getString("DPID")), Q.pm(houseResultSet.getString("DPNAME")), Q.pm(houseResultSet.getString("SID"))
                           , Q.pm(houseResultSet.getString("SNAME")), Q.pm(houseResultSet.getString("DID")), Q.pm(houseResultSet.getString("DNAME"))
                           , Q.pm(houseResultSet.getString("BNAME")), Q.p(houseResultSet.getString("DEVELOPER_NUMBER"))
                           , Q.p(ownerResultSet.getString("POOL_MEMO")), "Null", "Null", "Null",Q.pm(houseResultSet.getString("DESIGN_USE_TYPE"))
                           , Q.p(houseResultSet.getString("UNIT_NUMBER")) + ");"));
                   /**
                    * BUSINESS_HOUSE
                    */
                   KeyGeneratorHelper key = new KeyGeneratorHelper();
                   DescriptionDisplay businessDisplay = new DescriptionDisplay();
                   businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                   businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "房屋编号");
                   businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, houseResultSet.getString("ID"));
                   businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "合同编号");
                   businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,ownerResultSet.getString("reg_code"));
                   businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                   businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "房屋备案人");
                   String mainName="";
                   key.addWord(houseResultSet.getString("ID"));
                   key.addWord(ownerResultSet.getString("main_name"));
                   mainName = ownerResultSet.getString("main_name");
                   if (ownerResultSet.getString("ID_NO")!=null) {
                       key.addWord(ownerResultSet.getString("ID_NO"));
                       mainName = mainName+"["+ownerResultSet.getString("ID_NO")+"]";
                   }
                   if (ownerResultSet.getString("share_name")!=null
                           && !ownerResultSet.getString("share_name").equals("")
                           && !ownerResultSet.getString("share_name").equals("-")) {
                       key.addWord(ownerResultSet.getString("share_name"));
                       mainName = mainName+","+ownerResultSet.getString("share_name");
                   }
                   if (ownerResultSet.getString("SHARE_ID_NO")!=null) {
                       key.addWord(ownerResultSet.getString("SHARE_ID_NO"));
                       mainName = mainName+"["+ownerResultSet.getString("SHARE_ID_NO")+"]";
                   }
                   businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,mainName);


                   if (houseResultSet.getString("DPNAME")!=null
                           && !houseResultSet.getString("DPNAME").equals("")) {
                       businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "开发商");
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,houseResultSet.getString("DPNAME"));
                       key.addWord(houseResultSet.getString("DPNAME"));

                   }

                   businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);

                   if (houseResultSet.getString("ADDRESS")!=null && !houseResultSet.getString("ADDRESS").equals("")){
                       key.addWord(houseResultSet.getString("ADDRESS"));
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,houseResultSet.getString("ADDRESS"));
                   }
                   if (houseResultSet.getString("MAP_NUMBER")!=null && !houseResultSet.getString("MAP_NUMBER").equals("")){
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,houseResultSet.getString("MAP_NUMBER"));
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"图");
                   }

                   if (houseResultSet.getString("BLOCK_NO")!=null && !houseResultSet.getString("BLOCK_NO").equals("")){
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,houseResultSet.getString("BLOCK_NO"));
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"丘");
                   }
                   if (houseResultSet.getString("BUILD_NO")!=null && !houseResultSet.getString("BUILD_NO").equals("")){
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,houseResultSet.getString("BUILD_NO"));
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"幢");
                   }
                   if (houseResultSet.getString("HOUSE_ORDER")!=null && !houseResultSet.getString("HOUSE_ORDER").equals("")){
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,houseResultSet.getString("HOUSE_ORDER"));
                       businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"房");
                   }

                   sqlWriter.newLine();
                   sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED,SEARCH_KEY,DISPLAY) VALUES ");
                   sqlWriter.write("(" + Q.v(Q.pm(ownerResultSet.getString("reg_code")), Q.p(houseResultSet.getString("ID"))
                           , Q.pm(ownerResultSet.getString("reg_code")), Q.p(houseResultSet.getString("ID")+"-S1"), Q.p(houseResultSet.getString("ID")+"-1"),"FALSE",Q.pm(key.getKey()), Q.pm(DescriptionDisplay.toStringValue(businessDisplay)) + ");"));

                   /**
                    * ADD_HOUSE_STATUS
                    */
                   sqlWriter.newLine();
                   sqlWriter.write("INSERT ADD_HOUSE_STATUS (ID, BUSINESS, STATUS, IS_REMOVE) VALUES  ");
                   sqlWriter.write("(" + Q.v(Q.p(ownerResultSet.getString("reg_code")), Q.p(ownerResultSet.getString("reg_code"))
                           , Q.p("CONTRACTS_RECORD"), Q.p(false) + ");"));

                   /**
                     * HOUSE_RECORD
                    */
                   sqlWriter.newLine();
                   sqlWriter.write("INSERT HOUSE_RECORD (HOUSE_CODE, HOUSE, HOUSE_STATUS,DISPLAY,SEARCH_KEY) VALUES ");
                   sqlWriter.write("(" + Q.v(Q.p(houseResultSet.getString("ID")), Q.p(houseResultSet.getString("ID")+"-1")
                           ,Q.p("CONTRACTS_RECORD"),Q.pm(DescriptionDisplay.toStringValue(businessDisplay)),Q.pm(key.getKey())+");"));
                    /**
                    * main_owner pool HOUSE_OWNER POWER_OWNER
                    */
                   sqlWriter.newLine();
                   // main_owner
                   sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                           " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                   sqlWriter.write("(" + Q.v(Q.pm(ownerResultSet.getString("reg_code")+"-D"), Q.pm(ownerResultSet.getString("main_name"))
                           , Q.pm(ownerResultSet.getString("ID_TYPE")),Q.pm(ownerResultSet.getString("ID_NO")),Q.pm(ownerResultSet.getString("tel"))
                           , Q.p(ownerResultSet.getString("huji"))
                           , Q.p("CONTRACT"), "'0'"
                           , "Null","false","NULL" + ");"));
                   sqlWriter.newLine();
                   sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+(ownerResultSet.getString("reg_code")+"-D")+"' WHERE ID='"+(houseResultSet.getString("ID"))+"-1';");
                   sqlWriter.newLine();
                   //房屋与产权人关联
                   sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                   sqlWriter.write("(" + Q.v(Q.pm(houseResultSet.getString("ID")+"-1"),
                           Q.pm(ownerResultSet.getString("reg_code")+"-D")+");"));
                   sqlWriter.newLine();
                   // 共有权人
                   if(ownerResultSet.getString("POOL_MEMO")!=null
                           && (ownerResultSet.getString("POOL_MEMO").equals("TOGETHER_OWNER")
                           || ownerResultSet.getString("POOL_MEMO").equals("SHARE_OWNER"))
                           && ownerResultSet.getString("share_name")!=null
                           && !ownerResultSet.getString("share_name").equals("")){

                       sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                               " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                       sqlWriter.write("(" + Q.v(Q.pm(ownerResultSet.getString("reg_code")+"-DP"), Q.pm(ownerResultSet.getString("share_name"))
                               , Q.pm(ownerResultSet.getString("SHARE_ID_TYPE")),Q.pm(ownerResultSet.getString("SHARE_ID_NO")),Q.pm("未知")
                               , "NULL"
                               , Q.p("CONTRACT"), "'2'"
                               , "Null","false","NULL" + ");"));
                       sqlWriter.newLine();
                       sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                       sqlWriter.write("(" + Q.v(Q.pm(houseResultSet.getString("ID")+"-1"),
                               Q.pm(ownerResultSet.getString("reg_code")+"-DP")+");"));
                       sqlWriter.newLine();

                   }

                   /**
                    * HOUSE_CONTRACT
                    */
                   BigDecimal price = BigDecimal.valueOf(0);
                   if(ownerResultSet.getBigDecimal("price_house")!=null && ownerResultSet.getBigDecimal("price_house").compareTo(price)>0){
                       price =ownerResultSet.getBigDecimal("price_house");
                   }

                   sqlWriter.write("INSERT HOUSE_CONTRACT(ID, CONTRACT_NUMBER, CONTRACT_DATE, TYPE, PROJECT_RSHIP_NUMBER, PAY_TYPE," +
                           " SUM_PRICE, SALEAREA, LEAST_PROTECTED) VALUE ");
                   sqlWriter.write("(" + Q.v(Q.pm(ownerResultSet.getString("reg_code")), Q.pm(ownerResultSet.getString("reg_code"))
                           , Q.p(ownerResultSet.getTimestamp("reg_time")),Q.p("OTHER"),"Null"
                           , "NULL"
                           , Q.pm(price), Q.p(houseResultSet.getBigDecimal("HOUSE_AREA"))
                           , "Null" + ");"));

                   /**
                    * 受理备注 产权来源也放到备注里了
                    */
                   String resionText="";
                   if (ownerResultSet.getString("owner_memo")!=null && !ownerResultSet.getString("owner_memo").equals("")) {
                       resionText = ownerResultSet.getString("owner_memo");
                   }
                   if (ownerResultSet.getString("owner_source")!=null && !ownerResultSet.getString("owner_source").equals("")) {
                       resionText =resionText+"  产权来源:"+ownerResultSet.getString("owner_source");
                   }
                   if (ownerResultSet.getString("reg_organization")!=null && !ownerResultSet.getString("reg_organization").equals("")) {
                       resionText =resionText+"  登记单位:"+ownerResultSet.getString("reg_organization");
                   }
                   if(!resionText.equals("")) {
                       sqlWriter.newLine();
                       sqlWriter.write("INSERT REASON (ID, TYPE, REASON, BUISINESS_ID) VALUE ");
                       sqlWriter.write("(" + Q.v(Q.pm(ownerResultSet.getString("reg_code")), Q.pm("RECEIVE")
                               , Q.p(resionText)
                               , Q.pm(ownerResultSet.getString("reg_code")) + ");"));
                   }
                   /**
                    * 受理时间信息
                    */

                   if(ownerResultSet.getString("accept_name")!=null
                           && !ownerResultSet.getString("accept_name").equals("")
                           && (ownerResultSet.getString("accept_time")!=null
                           && !ownerResultSet.getString("accept_time").equals(""))) {
                       String acceptString = "受理人:"+ownerResultSet.getString("accept_name")+ " 受理时间:"+ownerResultSet.getString("accept_time");
                       sqlWriter.newLine();
                       sqlWriter.write("INSERT BUSINESS_EMP (ID, TYPE, EMP_CODE, BUSINESS_ID, OPER_TIME, COMMENTS, WINDOW_NO, EMP_NAME) VALUE ");
                       sqlWriter.write("(" + Q.v(Q.pm(ownerResultSet.getString("reg_code")), Q.pm("APPLY_EMP")
                               , Q.pm(ownerResultSet.getString("accept_name")), Q.pm(ownerResultSet.getString("reg_code"))
                               , Q.pm(ownerResultSet.getString("accept_time")),Q.p(acceptString),"NULL",Q.pm(ownerResultSet.getString("accept_name"))
                               +");"));
                   }

                   /**
                    * 登记时间时间信息
                    */
                    if(ownerResultSet.getString("reg_name")!=null
                           && !ownerResultSet.getString("reg_name").equals("")
                           && (ownerResultSet.getString("reg_time")!=null
                           && !ownerResultSet.getString("reg_time").equals(""))) {
                       String lastString = "登记人:"+ownerResultSet.getString("reg_name")+ " 登册时间:"+ownerResultSet.getString("reg_time");
                       sqlWriter.newLine();
                       sqlWriter.write("INSERT BUSINESS_EMP (ID, TYPE, EMP_CODE, BUSINESS_ID, OPER_TIME, COMMENTS, WINDOW_NO, EMP_NAME) VALUE ");
                       sqlWriter.write("(" + Q.v(Q.pm(ownerResultSet.getString("reg_code")+"L"), Q.pm("LAST_CHECK_EMP")
                               , Q.pm(ownerResultSet.getString("reg_name")), Q.pm(ownerResultSet.getString("reg_code"))
                               , Q.pm(ownerResultSet.getString("reg_time")),Q.p(lastString),"NULL",Q.pm(ownerResultSet.getString("reg_name"))
                                       +");"));
                   }


                   sqlWriter.flush();

               }else{
                   houseOwnerError.write("此房house_key没有产权信息---: "+houseResultSet.getString("ID"));
                   houseOwnerError.newLine();
                   houseOwnerError.flush();
               }


               i++;
               System.out.println(i+"/"+String.valueOf(recordCount)+"--house_key:"+houseResultSet.getString("ID"));

           }




       }catch(Exception e){
           System.out.println("house_key is errer-----"+houseResultSet.getString("ID"));
           e.printStackTrace();
           return;

       }


   }

}
