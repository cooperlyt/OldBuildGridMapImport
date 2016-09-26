package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;
import com.scoopit.weedfs.client.net.Result;
import com.wxy.House.SlectInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2016-09-18.
 */
public class HouseOwnerRecord {

    private static final String OUT_PATH_FILE = "/houseOwnerRecord.sql";

    private static final String OUT_PATH_HAVEHOUSESTATENOTBIZ_FILE = "/haveHouseStateNotBiz.sql";

    private static final String DB_RECORD_URL = "jdbc:jtds:sqlserver://192.168.0.200:1433/DGHouseRecord";

    private static final String DB_HOUSE_URL = "jdbc:jtds:sqlserver://192.168.0.200:1433/DGHouseInfo";

    private static Connection recordConnection;

    private static Connection houseConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter haveHouseStateNotBizWriter;

    private static File recordFile;

    private static File haveHouseStateNotBizFile;


    private static Statement statementRecord;

    private static Statement statementRecordch;



    private static Statement statementHouse;

    private static Statement statementHousech;

    private static ResultSet recordResultSet;

    private static ResultSet HouseResultSet;

    private static Set<String> EXCEPTION_biz_NO = new HashSet<>();

    private static Set<String> EXCEPTION_HOUSE_NO = new HashSet<>();

    private static Set<String> EXCEPTION_BUILD_NO = new HashSet<>();

    private static Set<String> LOCKED_HOUSE_NO = new HashSet<>();

    private static boolean isFirst;


    private static String DEFINE_ID;

    private static String bizid;

    private static String selectbizid;

    public static void main(String agr[]){

        recordFile = new File(OUT_PATH_FILE);
        if (recordFile.exists()){
            recordFile.delete();
        }
        haveHouseStateNotBizFile = new File(OUT_PATH_HAVEHOUSESTATENOTBIZ_FILE);
        if (haveHouseStateNotBizFile.exists()){
            haveHouseStateNotBizFile.delete();
        }

        try {
            recordFile.createNewFile();
            FileWriter fw = new FileWriter(recordFile.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);

            FileWriter haveHouseStateNotBizFw = new FileWriter(haveHouseStateNotBizFile.getAbsoluteFile());
            haveHouseStateNotBizWriter=new BufferedWriter(haveHouseStateNotBizFw);

            sqlWriter.write("USE HOUSE_OWNER_RECORD;");
            sqlWriter.newLine();
            sqlWriter.flush();
        } catch (IOException e) {
            System.out.println("sql 文件创建失败");
            e.printStackTrace();
            return;
        }


        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            recordConnection = DriverManager.getConnection(DB_RECORD_URL, "sa", "dgsoft");
            statementRecord = recordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementRecordch = recordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("recordConnection successful");
        } catch (Exception e) {
            System.out.println("recordConnection is errer");
            e.printStackTrace();
            return;
        }


        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            houseConnection = DriverManager.getConnection(DB_HOUSE_URL, "sa", "dgsoft");
            statementHouse = houseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementHousech = houseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("houseConnection successful");
        } catch (Exception e) {
            System.out.println("houseConnection is errer");
            e.printStackTrace();
            return;
        }



        try {
            //刨除项目没有挂小区的
            ResultSet srtB = statementHouse.executeQuery("select p.no as pno,b.no as bno  from project as p left join build as b on p.id=b.projectid WHERE p.SECTIONID IS NULL");
            while (srtB.next()){
                if (!EXCEPTION_BUILD_NO.contains(srtB.getString("bno"))) {
                    EXCEPTION_BUILD_NO.add(srtB.getString("bno"));
                }
            }
            //刨除图丘幢重复
            ResultSet srtc = statementHouse.executeQuery("select b.no from (select mapno,blockno,buildno,count(id)as sl from build group by mapno,blockno,buildno) as a left join build as b on a.mapno=b.mapno and a.blockno=b.blockno and a.buildno=b.buildno where a.sl>1");
            while (srtc.next()){
                if (!EXCEPTION_BUILD_NO.contains(srtc.getString("no"))) {
                    EXCEPTION_BUILD_NO.add(srtc.getString("no"));
                }
            }




            System.out.println("EXCEPTION_Build is complate");
        } catch (Exception e) {
            System.out.println("EXCEPTION_Build is is errer");
            e.printStackTrace();
            return;
        }



          try {
            //刨除重复的图丘幢房号
            ResultSet srta =statementHouse.executeQuery("select hh.no as hno,bb.no as bno" +
                    " from house as hh left join Build as bb on hh.buildid=bb.id where" +
                    " (bb.mapno+'-'+bb.blockno+'-'+bb.buildno+'-'+houseorder)" +
                    " in(select (b.mapno+'-'+b.blockno+'-'+b.buildno+'-'+a.HouseOrder) as c1 from" +
                    " (select BuildID,HouseOrder,count(id) as sl from house group" +
                    " by BuildID,HouseOrder) as a" +
                    " left join build as b on a.BuildID=b.id where a.sl >1)");

            while (srta.next()){
                if (!EXCEPTION_HOUSE_NO.contains(srta.getString("hno"))){
                    EXCEPTION_HOUSE_NO.add(srta.getString("hno"));
                }
            }
            //房屋状态为在建工程抵押，查封，不可售，异议，灭籍,声明作废,房屋已注销(灭籍) 导入预警 抵押(做完初始登记直接做的抵押)
//            ResultSet setd = statementHouse.executeQuery("select h.*,b.no as bno from DGHouseInfo..house as h left join DGHouseInfo..Build as b on h.buildid=b.id" +
//                    " where houseState=890 or houseState=99 or houseState=127 or houseState=116 or houseState=115 or houseState=117 or houseState=119");
//            sqlWriter.newLine();
//            sqlWriter.write("INSERT LOCKED_HOUSE(HOUSE_CODE, DESCRIPTION, TYPE, EMP_CODE, EMP_NAME, LOCKED_TIME, ID, BUILD_CODE) values ");
//            while (setd.next()){
//                if (!LOCKED_HOUSE_NO.contains(setd.getString("no"))){
//                    LOCKED_HOUSE_NO.add(setd.getString("no"));
//                }
//
//                sqlWriter.write("(" + Q.v(Q.p(setd.getString("no")), Q.lockHouseDescription(setd.getInt("houseState")), "'HOUSE_LOCKED'",
//                        "'未知'", "'管理员'", Q.p(Q.nowFormatTime()), Q.p(setd.getString("no")), Q.p("N" + setd.getString("bno"))));
//
//                if(setd.isLast()) {
//                    sqlWriter.write(");");
//                }else {
//                    sqlWriter.write("),");
//                }
//
//            }
//
//          sqlWriter.flush();
          System.out.println("LOCKED_HOUSE is complate");
        } catch (Exception e) {
            System.out.println("LOCKED_HOUSE is errer");
            e.printStackTrace();
            return;
        }


        try {
//            ResultSet rstHouse = statementHouse.executeQuery("select b.no as bno,h.* " +
//                    "from DGHouseInfo..house as h left join DGHouseInfo..Build as b on h.buildid=b.id" +
//                    " where houseState <>118 and houseState <>890 and houseState <>127 " +
//                    //"and (h.no='140076' OR h.no='19918' or b.no='12119')");
//                    //"and (b.no='7319')");
//                    "and (b.no='7319')");
            ResultSet rstHouse = statementHouse.executeQuery("select hd.no as hdno,hd.name as hdname,e.* from " +
                    " (select hs.no as hsno,hs.name as hsname,hs.DistrictID,d.* from" +
                    " (select p.no as pno,p.name as pname,c.* from" +
                    " (select hp.no as hpno,hp.name as hpnmae,hp.DeveloperID,hp.SectionID,a.* from"+
                    " (select b.no as bno,b.BuildName,b.DoorNO as bDoorNO,b.Mapno,b.blockNo,b.buildNo,b.BuildType,b.FirmlyDate,"+
                    " b.FloorCount,b.ProjectID,h.* from DGHouseInfo..house as h left join DGHouseInfo..Build as b on h.buildid=b.id"+
                    " where houseState <>118 and houseState <>890 and houseState <>127) as a"+
                    " left join project as hp on a.ProjectID=hp.id) as c"+
                    " left join Developer as p on c.DeveloperID=p.id) as d"+
                    " left join Section as hs on d.sectionid=hs.id) as e"+
                    " left join District as hd on e.DistrictID = hd.id" +
                    " where (e.no='140076')");


            rstHouse.last();
            System.out.print("rstHouseCount-Start-:" + rstHouse.getRow());
            int sumCount = rstHouse.getRow();
            rstHouse.beforeFirst();
            sqlWriter.newLine();
            int i=0;
            while (rstHouse.next()){

                if (!EXCEPTION_BUILD_NO.contains(rstHouse.getString("bno"))
                        && (!EXCEPTION_HOUSE_NO.contains(rstHouse.getString("No")))){//除出有问题的房屋



                    ResultSet rstRecortBa = statementRecord.executeQuery("select * from DGHouseRecord..Business as db left join " +
                            "HouseHistroy as hh on db.id=hh.Business where nameid not like '%WP50' and  " +
                            "nameid not like '%WP51' and  nameid not like '%WP85' and " +
                            "(workid like '%WP42' or workid like '%WP43' ) and hh.no= '"+rstHouse.getString("No")+"' order by botime");

                    rstRecortBa.last();
                    int baCount = rstRecortBa.getRow();
                    //System.out.println("baCount--"+baCount);
                    isFirst = true;
                    rstRecortBa.beforeFirst();
                    if (baCount>0) {//按房号循环，
                        while (rstRecortBa.next()) {

                            System.out.println("RecordBizNO" + rstRecortBa.getString("RecordBizNO"));


                            if (!EXCEPTION_biz_NO.contains(rstRecortBa.getString("RecordBizNO"))) {

                                String[] temp;
                                if (rstRecortBa.getString("workid").contains("A")){
                                    temp = rstRecortBa.getString("workid").split("_");

                                }else {
                                    temp = rstRecortBa.getString("workid").split("#");
                                }
                                DEFINE_ID = temp[temp.length - 1];
                                selectbizid = null;
                                if (DEFINE_ID.equals("WP42") || DEFINE_ID.equals("WP43")) {
                                    if (DEFINE_ID.equals("WP42")) {
                                        bizid = rstRecortBa.getString("RecordBizNO");
                                    }
                                    if (DEFINE_ID.equals("WP43")) {
                                        if (rstRecortBa.getString("SelectBiz") == null) {
                                            selectbizid = bizid;
                                        } else {
                                            ResultSet rstRecortbiz = statementRecordch.executeQuery("select RecordBizNO from DGHouseRecord..Business where id='" + rstRecortBa.getString("SelectBiz") + "'");
                                            rstRecortbiz.next();
                                            if (rstRecortbiz.getString("RecordBizNO") != null) {
                                                selectbizid = rstRecortbiz.getString("RecordBizNO");
                                            } else {
                                                System.out.println("not find SelectBiz---" + rstRecortBa.getString("SelectBiz"));
                                                haveHouseStateNotBizWriter.write("此SelectBiz没有找到对应的业务，为空--房屋编号"+rstRecortBa.getString("NO")+"业务编号--"+rstRecortBa.getString("RecordBizNO"));
                                                haveHouseStateNotBizWriter.newLine();
                                                haveHouseStateNotBizWriter.flush();
                                            }
                                        }
                                    }
                                    sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS, CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")), "0", "'BIZ_IMPORT'", Q.p(rstRecortBa.getString("MEMO"))
                                            , "'COMPLETE'", Q.defineName(DEFINE_ID), Q.pm(DEFINE_ID), "0", Q.p(selectbizid), Q.p(rstRecortBa.getTimestamp("BOTime"))
                                            , Q.p(rstRecortBa.getTimestamp("BOTime")), "Null", "Null", Q.p(rstRecortBa.getTimestamp("BOTime")), "False", "'NORMAL_BIZ'") + ");");
                                    sqlWriter.newLine();

                                }
                                //==========备案人=============================
                                System.out.println("备案人备案人---");
                                String barIDorNO;

                                if (DEFINE_ID.equals("WP42")) {//判断是备案人还是撤案人
                                    if (rstRecortBa.getString("workid").contains("WP")) {//判断是否老库导入数据
                                        barIDorNO = SlectInfo.svs(statementRecordch, "record_people", rstRecortBa.getString("RecordBizNO"));
                                    } else {
                                        barIDorNO = rstRecortBa.getString("MainOwner");
                                    }
                                }else {
                                    if (rstRecortBa.getString("workid").contains("WP")) {//判断是否老库导入数据
                                        barIDorNO = SlectInfo.svs(statementRecordch, "re_record_people", rstRecortBa.getString("RecordBizNO"));
                                    } else {
                                        barIDorNO = rstRecortBa.getString("MainOwner");
                                    }

                                }


                                System.out.println("备案人---"+barIDorNO);
                                ResultSet barResultSet = SlectInfo.bar(statementHousech,barIDorNO);
                                if (barResultSet!=null){

                                    sqlWriter.write("INSERT CONTRACT_OWNER (ID, CONTRACT_NUMBER, NAME, " +
                                            "ID_TYPE, ID_NO, PHONE, ADDRESS, " +
                                            "BUSINESS, CONTRACT_DATE, TYPE, HOUSE_CODE) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(rstRecortBa.getString("CompactNO"))
                                            ,Q.pm(barResultSet.getString("Name")),Q.pCardType(barResultSet.getInt("IDType")), Q.pm(barResultSet.getString("IDNO"))
                                            ,Q.p(barResultSet.getString("Phone")),Q.p(barResultSet.getString("Address"))
                                            ,Q.p(rstRecortBa.getString("RecordBizNO")), Q.p(rstRecortBa.getTimestamp("BOTime"))
                                            ,"'MAP_SELL'",Q.p(rstRecortBa.getString("No"))  + ");"));
                                    sqlWriter.newLine();

                                }else{
                                    haveHouseStateNotBizWriter.write("此业务没有查到备案人信息房屋编号--"+rstRecortBa.getString("NO")+"业务编号--"+rstRecortBa.getString("RecordBizNO"));
                                    haveHouseStateNotBizWriter.newLine();
                                    haveHouseStateNotBizWriter.flush();
                                }

                                //==========beforeHouse afterHouse=============================
                                String stratBizid,lastBizid = null;
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

                                if (isFirst){
                                    stratBizid = rstRecortBa.getString("RecordBizNO")+"-s";
                                    lastBizid =rstRecortBa.getString("RecordBizNO");

                                    sqlWriter.write("INSERT HOUSE (ID, HOUSE_ORDER, HOUSE_UNIT_NAME, IN_FLOOR_NAME, " +
                                            "HOUSE_AREA, USE_AREA, COMM_AREA, SHINE_AREA, LOFT_AREA, COMM_PARAM, " +
                                            "HOUSE_TYPE, USE_TYPE, STRUCTURE, ADDRESS, " +
                                            "MAP_TIME,HOUSE_CODE, " +
                                            "HAVE_DOWN_ROOM, BUILD_CODE, MAP_NUMBER, BLOCK_NO, BUILD_NO, " +
                                            "DOOR_NO, UP_FLOOR_COUNT, FLOOR_COUNT, DOWN_FLOOR_COUNT, BUILD_TYPE, " +
                                            "PROJECT_CODE, PROJECT_NAME, COMPLETE_DATE, DEVELOPER_CODE, DEVELOPER_NAME," +
                                            " SECTION_CODE, SECTION_NAME, DISTRICT_CODE, DISTRICT_NAME, BUILD_NAME, " +
                                            "BUILD_DEVELOPER_NUMBER, POOL_MEMO, MAIN_OWNER, LAND_INFO, REG_INFO, CONTRACT_OWNER, " +
                                            "OLD_OWNER, NOITCE_OWNER) VALUES");
                                    System.out.println("----"+rstHouse.getString("Structure"));
                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")+"-s"),Q.pm(rstRecortBa.getString("HouseOrder"))
                                            ,Q.pm(rstHouse.getString("UnitName")),Q.pm(rstHouse.getString("InFloorName"))
                                            ,Q.pm(rstHouse.getBigDecimal("HouseArea")),Q.pm(rstHouse.getBigDecimal("UseArea"))
                                            ,Q.pm(rstHouse.getBigDecimal("CommParam")),Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                            ,Q.pm(rstHouse.getBigDecimal("LoftArea")),Q.pm(rstHouse.getBigDecimal("CommArea"))
                                            ,Q.changeHouseType(rstHouse.getInt("HouseType")),rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                            ,rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                            ,Q.pm(rstRecortBa.getString("HouseStation")),Q.pm(rstRecortBa.getString("mappingDate")!=null?(rstRecortBa.getString("mappingDate")!="1899-12-30 00:00:00.0"?rstRecortBa.getString("mappingDate"):rstRecortBa.getString("ChangeDate")):rstRecortBa.getString("ChangeDate"))
                                            ,Q.pm(rstRecortBa.getString("No")),"False",Q.pm("N"+rstHouse.getString("bno")),Q.pm(rstHouse.getString("MapNo")),Q.pm(rstHouse.getString("BlockNo"))
                                            ,Q.pm(rstHouse.getString("BuildNo")),Q.pm(rstRecortBa.getString("DoorNo")),rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                            ,rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1","0"
                                            ,Q.p(rstHouse.getString("BuildType")),Q.pm("N"+rstHouse.getString("hpno")),Q.pm(rstHouse.getString("hpnmae"))
                                            ,rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                            ,Q.pm("N"+rstHouse.getString("pno")),Q.pm(rstHouse.getString("pname")),Q.pm("N"+rstHouse.getString("hsno"))
                                            ,Q.pm(rstHouse.getString("hsname")),Q.pm("N"+rstHouse.getString("hdno")),Q.pm(rstHouse.getString("hdname"))
                                            ,Q.pm(rstHouse.getString("BuildName")),"Null","Null","Null","Null","Null","Null","Null","Null"  + ");"));
                                    sqlWriter.newLine();


                                    sqlWriter.write("INSERT HOUSE (ID, HOUSE_ORDER, HOUSE_UNIT_NAME, IN_FLOOR_NAME, " +
                                            "HOUSE_AREA, USE_AREA, COMM_AREA, SHINE_AREA, LOFT_AREA, COMM_PARAM, " +
                                            "HOUSE_TYPE, USE_TYPE, STRUCTURE, ADDRESS, " +
                                            "MAP_TIME,HOUSE_CODE, " +
                                            "HAVE_DOWN_ROOM, BUILD_CODE, MAP_NUMBER, BLOCK_NO, BUILD_NO, " +
                                            "DOOR_NO, UP_FLOOR_COUNT, FLOOR_COUNT, DOWN_FLOOR_COUNT, BUILD_TYPE, " +
                                            "PROJECT_CODE, PROJECT_NAME, COMPLETE_DATE, DEVELOPER_CODE, DEVELOPER_NAME," +
                                            " SECTION_CODE, SECTION_NAME, DISTRICT_CODE, DISTRICT_NAME, BUILD_NAME, " +
                                            "BUILD_DEVELOPER_NUMBER, POOL_MEMO, MAIN_OWNER, LAND_INFO, REG_INFO, CONTRACT_OWNER, " +
                                            "OLD_OWNER, NOITCE_OWNER) VALUES");


                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(rstRecortBa.getString("HouseOrder"))
                                            ,Q.pm(rstHouse.getString("UnitName")),Q.pm(rstHouse.getString("InFloorName"))
                                            ,Q.pm(rstHouse.getBigDecimal("HouseArea")),Q.pm(rstHouse.getBigDecimal("UseArea"))
                                            ,Q.pm(rstHouse.getBigDecimal("CommParam")),Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                            ,Q.pm(rstHouse.getBigDecimal("LoftArea")),Q.pm(rstHouse.getBigDecimal("CommArea"))
                                            ,Q.changeHouseType(rstHouse.getInt("HouseType")),rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                            ,rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                            ,Q.pm(rstRecortBa.getString("HouseStation")),Q.pm(rstRecortBa.getString("mappingDate")!=null?(rstRecortBa.getString("mappingDate")!="1899-12-30 00:00:00.0"?rstRecortBa.getString("mappingDate"):rstRecortBa.getString("ChangeDate")):rstRecortBa.getString("ChangeDate"))
                                            ,Q.pm(rstRecortBa.getString("No")),"False",Q.pm("N"+rstHouse.getString("bno")),Q.pm(rstHouse.getString("MapNo")),Q.pm(rstHouse.getString("BlockNo"))
                                            ,Q.pm(rstHouse.getString("BuildNo")),Q.pm(rstRecortBa.getString("DoorNo")),rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                            ,rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1","0"
                                            ,Q.p(rstHouse.getString("BuildType")),Q.pm("N"+rstHouse.getString("hpno")),Q.pm(rstHouse.getString("hpnmae"))
                                            ,rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                            ,Q.pm("N"+rstHouse.getString("pno")),Q.pm(rstHouse.getString("pname")),Q.pm("N"+rstHouse.getString("hsno"))
                                            ,Q.pm(rstHouse.getString("hsname")),Q.pm("N"+rstHouse.getString("hdno")),Q.pm(rstHouse.getString("hdname"))
                                            ,Q.pm(rstHouse.getString("BuildName")),"Null","Null","Null","Null","Null",Q.p(rstRecortBa.getString("RecordBizNO")),"Null","Null"  + ");"));
                                    sqlWriter.newLine();



                                }else{
                                    stratBizid = lastBizid;
                                    lastBizid = rstRecortBa.getString("RecordBizNO");


                                    sqlWriter.write("INSERT HOUSE (ID, HOUSE_ORDER, HOUSE_UNIT_NAME, IN_FLOOR_NAME, " +
                                            "HOUSE_AREA, USE_AREA, COMM_AREA, SHINE_AREA, LOFT_AREA, COMM_PARAM, " +
                                            "HOUSE_TYPE, USE_TYPE, STRUCTURE, ADDRESS, " +
                                            "MAP_TIME,HOUSE_CODE, " +
                                            "HAVE_DOWN_ROOM, BUILD_CODE, MAP_NUMBER, BLOCK_NO, BUILD_NO, " +
                                            "DOOR_NO, UP_FLOOR_COUNT, FLOOR_COUNT, DOWN_FLOOR_COUNT, BUILD_TYPE, " +
                                            "PROJECT_CODE, PROJECT_NAME, COMPLETE_DATE, DEVELOPER_CODE, DEVELOPER_NAME," +
                                            " SECTION_CODE, SECTION_NAME, DISTRICT_CODE, DISTRICT_NAME, BUILD_NAME, " +
                                            "BUILD_DEVELOPER_NUMBER, POOL_MEMO, MAIN_OWNER, LAND_INFO, REG_INFO, CONTRACT_OWNER, " +
                                            "OLD_OWNER, NOITCE_OWNER) VALUES");

                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(rstRecortBa.getString("HouseOrder"))
                                            ,Q.pm(rstHouse.getString("UnitName")),Q.pm(rstHouse.getString("InFloorName"))
                                            ,Q.pm(rstHouse.getBigDecimal("HouseArea")),Q.pm(rstHouse.getBigDecimal("UseArea"))
                                            ,Q.pm(rstHouse.getBigDecimal("CommParam")),Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                            ,Q.pm(rstHouse.getBigDecimal("LoftArea")),Q.pm(rstHouse.getBigDecimal("CommArea"))
                                            ,Q.changeHouseType(rstHouse.getInt("HouseType")),rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                            ,rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                            ,Q.pm(rstRecortBa.getString("HouseStation")),Q.pm(rstRecortBa.getString("mappingDate")!=null?(rstRecortBa.getString("mappingDate")!="1899-12-30 00:00:00.0"?rstRecortBa.getString("mappingDate"):rstRecortBa.getString("ChangeDate")):rstRecortBa.getString("ChangeDate"))
                                            ,Q.pm(rstRecortBa.getString("No")),"False",Q.pm("N"+rstHouse.getString("bno")),Q.pm(rstHouse.getString("MapNo")),Q.pm(rstHouse.getString("BlockNo"))
                                            ,Q.pm(rstHouse.getString("BuildNo")),Q.pm(rstRecortBa.getString("DoorNo")),rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                            ,rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1","0"
                                            ,Q.p(rstHouse.getString("BuildType")),Q.pm("N"+rstHouse.getString("hpno")),Q.pm(rstHouse.getString("hpnmae"))
                                            ,rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                            ,Q.pm("N"+rstHouse.getString("pno")),Q.pm(rstHouse.getString("pname")),Q.pm("N"+rstHouse.getString("hsno"))
                                            ,Q.pm(rstHouse.getString("hsname")),Q.pm("N"+rstHouse.getString("hdno")),Q.pm(rstHouse.getString("hdname"))
                                            ,Q.pm(rstHouse.getString("BuildName")),"Null","Null","Null","Null","Null",Q.p(rstRecortBa.getString("RecordBizNO")),"Null","Null"  + ");"));
                                    sqlWriter.newLine();



                                }








                                isFirst = false;
                                EXCEPTION_biz_NO.add(rstRecortBa.getString("RecordBizNO"));
                                sqlWriter.flush();
                            }else{
                                haveHouseStateNotBizWriter.write("此房屋编号有重复HouseHistroy房屋编号--"+rstRecortBa.getString("NO")+"业务编号--"+rstRecortBa.getString("RecordBizNO"));
                                haveHouseStateNotBizWriter.newLine();
                                haveHouseStateNotBizWriter.flush();
                                System.out.println("此房屋编号有重复HouseHistroy房屋编号--"+rstRecortBa.getString("NO")+"业务编号--"+rstRecortBa.getString("RecordBizNO"));
                            }
                        }


                    }else{
                        rstRecortBa = statementRecord.executeQuery("select * from DGHouseRecord..Business as db left join " +
                                "HouseHistroy as hh on db.id=hh.Business where nameid not like '%WP50' and  " +
                                "nameid not like '%WP51' and  nameid not like '%WP85' and " +
                                "(workid NOT like '%WP42' AND  workid NOT like '%WP43') and hh.no= '"+rstHouse.getString("No")+"' order by botime");
                        rstRecortBa.last();
                       int ybaCount = rstRecortBa.getRow();
                        if (ybaCount>0){
                            sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS, CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                            sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")), "0", "'BIZ_IMPORT'", Q.p(rstRecortBa.getString("MEMO"))
                                    , "'COMPLETE'","'已备案'", "'WPYBA'", "0", "Null", Q.p(rstRecortBa.getTimestamp("BOTime"))
                                    , Q.p(rstRecortBa.getTimestamp("BOTime")), "Null", "Null", Q.p(rstRecortBa.getTimestamp("BOTime")), "False", "'NORMAL_BIZ'")+");");
                            sqlWriter.newLine();


//                      ==================备案人=====================
                         ResultSet barResultSet = SlectInfo.bar(statementHousech,rstHouse.getString("MainOwner"));
                         if (barResultSet!=null){

                                sqlWriter.write("INSERT CONTRACT_OWNER (ID, CONTRACT_NUMBER, NAME, " +
                                        "ID_TYPE, ID_NO, PHONE, ADDRESS, " +
                                        "BUSINESS, CONTRACT_DATE, TYPE, HOUSE_CODE) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(rstRecortBa.getString("CompactNO"))
                                        ,Q.pm(barResultSet.getString("Name")),Q.pCardType(barResultSet.getInt("IDType")), Q.pm(barResultSet.getString("IDNO"))
                                        ,Q.p(barResultSet.getString("Phone")),Q.p(barResultSet.getString("Address"))
                                        ,Q.p(rstRecortBa.getString("RecordBizNO")), Q.p(rstRecortBa.getTimestamp("BOTime"))
                                        ,"'MAP_SELL'",Q.p(rstRecortBa.getString("No"))  + ");"));
                                sqlWriter.newLine();

                         }else{
                             haveHouseStateNotBizWriter.write("此业务没有查到'已备案'房屋产权人信息房屋编号--"+rstRecortBa.getString("NO")+"业务编号--"+rstRecortBa.getString("RecordBizNO"));
                             haveHouseStateNotBizWriter.newLine();
                             haveHouseStateNotBizWriter.flush();
                         }







                              sqlWriter.flush();
                        }else {
                            if (!LOCKED_HOUSE_NO.contains(rstHouse.getString("No"))) {
                                System.out.println("此房屋编号有状态没业务--" + rstHouse.getString("No"));
                                haveHouseStateNotBizWriter.write("此房屋编号有状态没业务--" + rstHouse.getString("No"));
                                haveHouseStateNotBizWriter.newLine();
                                haveHouseStateNotBizWriter.flush();
                            }

                        }






                    }
                }
                i++;
                System.out.println(i+"/"+String.valueOf(sumCount));

            }
            sqlWriter.flush();
            System.out.println("record is complate");
        } catch (Exception e) {
            System.out.println("record is errer");
            e.printStackTrace();
            return;
        }


    }
}