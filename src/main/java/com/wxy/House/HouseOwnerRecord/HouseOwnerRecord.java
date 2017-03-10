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
 * 导入
 */
public class HouseOwnerRecord {

    private static final String BEGIN_DATE = "2016-09-30";
//
//    private static final String OUT_PATH_FILE = "/root/Documents/houseOwnerRecord.sql";
//
//    private static final String OUT_PATH_PROJECTFILE = "/root/Documents/houseProjectRecord.sql";
//
//    private static final String OUT_PATH_PROJECTFILE_ERRER = "/root/Documents/houseProjectRecord_errer.sql";
//
 //   private static final String OUT_PATH_HAVEHOUSESTATENOTBIZ_FILE = "/root/Documents/haveHouseStateNotBiz.sql";

    private static final String OUT_PATH_FILE = "/houseOwnerRecord.sql";

    private static final String OUT_PATH_PROJECTFILE = "/houseProjectRecord.sql";

    private static final String OUT_PATH_PROJECTFILE_ERRER = "/houseProjectRecord_errer.sql";

    private static final String OUT_PATH_HAVEHOUSESTATENOTBIZ_FILE = "/haveHouseStateNotBiz.sql";

    private static final String DB_RECORD_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/DGHouseRecord";

    private static final String DB_HOUSE_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/DGHouseInfo";

    private static final String DB_SHARK_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/shark";


    private static Connection recordConnection;

    private static Connection houseConnection;

    private static Connection sharkConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter sqlPWriter;

    private static BufferedWriter haveHouseStateNotBizWriter;

    private static BufferedWriter sqlPWriterErree;

    private static File recordFile;

    private static File haveHouseStateNotBizFile;

    private static File recordProjectFile;

    private static File recordProjectFileErrer;

    private static Statement statementRecord;

    private static Statement statementRecordch;



    private static Statement statementHouse;

    private static Statement statementHousech;

    private static Statement statementShark;

    private static ResultSet recordResultSet;

    private static ResultSet HouseResultSet;

    private static Set<String> EXCEPTION_biz_NO = new HashSet<>();

    private static Set<String> EXCEPTION_HOUSE_NO = new HashSet<>();

    private static Set<String> EXCEPTION_BUILD_NO = new HashSet<>();

    private static Set<String> LOCKED_HOUSE_NO = new HashSet<>();

    private static boolean isFirst;


    private static String DEFINE_ID;

    private static String DEFINE_NAME;

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

        recordProjectFile = new File(OUT_PATH_PROJECTFILE);
        if (recordProjectFile.exists()){
            recordProjectFile.delete();
        }

        recordProjectFileErrer = new File(OUT_PATH_PROJECTFILE_ERRER);
        if(recordProjectFileErrer.exists()){
            recordProjectFileErrer.delete();
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
            recordProjectFile.createNewFile();
            FileWriter pfw = new FileWriter(recordProjectFile.getAbsoluteFile());
            FileWriter Pfwe = new FileWriter(recordProjectFileErrer.getAbsoluteFile());
            sqlPWriterErree = new BufferedWriter(Pfwe);
            sqlPWriter = new BufferedWriter(pfw);
            sqlPWriter.write("USE HOUSE_OWNER_RECORD;");
            sqlWriter.write("set global max_allowed_packet=1024*1024*2048;");
            sqlPWriter.newLine();
            sqlPWriter.flush();
        } catch (IOException e) {
            System.out.println("psql 文件创建失败");
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
            sharkConnection = DriverManager.getConnection(DB_SHARK_URL, "sa", "dgsoft");
            statementShark = sharkConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
//                    " where houseState=890 or houseState=99 or houseState=127 or houseState=116 or houseState=115 or houseState=117 or houseState=119 or houseState=4111");
              ResultSet setd = statementHouse.executeQuery("select h.*,b.no as bno from DGHouseInfo..house as h left join DGHouseInfo..Build as b on h.buildid=b.id" +
                      " where houseState=4111");

              sqlWriter.newLine();
              sqlWriter.write("INSERT LOCKED_HOUSE(HOUSE_CODE, DESCRIPTION, TYPE, EMP_CODE, EMP_NAME, LOCKED_TIME, ID, BUILD_CODE) values ");
            while (setd.next()){
                if (!LOCKED_HOUSE_NO.contains(setd.getString("no"))){
                    LOCKED_HOUSE_NO.add(setd.getString("no"));
                }

                sqlWriter.write("(" + Q.v(Q.p(setd.getString("no")), Q.lockHouseDescription(setd.getInt("houseState")), "'HOUSE_LOCKED'",
                        "'未知'", "'管理员'", Q.p(Q.nowFormatTime()), Q.p(setd.getString("no")), Q.p("N" + setd.getString("bno"))));

                if(setd.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }

         sqlWriter.flush();
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
//                    //"and (e.no='140076' OR h.no='19918' or b.no='12119')");
//                    //"and (e.bno='7319')");
//                    "and (e.bno='7319')");
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
            //        " left join District as hd on e.DistrictID = hd.id");
                    " left join District as hd on e.DistrictID = hd.id" +
                    " where (e.no='137034')");

            rstHouse.last();
            System.out.print("rstHouseCount-Start-:" + rstHouse.getRow());
            int sumCount = rstHouse.getRow();
            rstHouse.beforeFirst();
            sqlWriter.newLine();
            int i=0;
            while (rstHouse.next()){

                if (!EXCEPTION_BUILD_NO.contains(rstHouse.getString("bno"))
                        && (!EXCEPTION_HOUSE_NO.contains(rstHouse.getString("No")))){//除出有问题的房屋



                    ResultSet rstRecortBa = statementRecord.executeQuery("select db.id as dbid,* from DGHouseRecord..Business as db left join " +
                            "HouseHistroy as hh on db.id=hh.Business where nameid not like '%WP50' and  " +
                            "workid not like '%WP51' and  workid not like '%WP85' and " +
                            "(workid like '%WP42' or workid like '%WP43' ) and hh.no= '"+rstHouse.getString("No")+"' and db.b>='"+BEGIN_DATE+"' order by botime");

                    rstRecortBa.last();
                    int baCount = rstRecortBa.getRow();
                    System.out.println("baCount--"+baCount);
                    isFirst = true;
                    rstRecortBa.beforeFirst();
                    String stratBizid,lastBizid = null;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                    //按房号循环，
                    if (baCount>0)
                        while (rstRecortBa.next()) {

                       // System.out.println("RecordBizNO" + rstRecortBa.getString("RecordBizNO"));


                        if (!EXCEPTION_biz_NO.contains(rstRecortBa.getString("RecordBizNO"))) {

                            String[] temp;
                            if (rstRecortBa.getString("workid").contains("A")) {
                                temp = rstRecortBa.getString("workid").split("_");

                            } else {
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
                                            haveHouseStateNotBizWriter.write("此SelectBiz没有找到对应的业务，为空--房屋编号" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO"));
                                            haveHouseStateNotBizWriter.newLine();
                                            haveHouseStateNotBizWriter.flush();
                                        }
                                    }
                                }
                                sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                                        " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")), "0", "'BIZ_IMPORT'", Q.p(rstRecortBa.getString("MEMO"))
                                        , "'COMPLETE'", Q.defineName(DEFINE_ID), Q.pm(DEFINE_ID), "0", Q.p(selectbizid), Q.p(rstRecortBa.getTimestamp("BOTime"))
                                        , Q.p(rstRecortBa.getTimestamp("BOTime")), "Null", Q.p(rstRecortBa.getTimestamp("BOTime")), Q.p(rstRecortBa.getTimestamp("BOTime")), "False", "'NORMAL_BIZ'") + ");");
                                sqlWriter.newLine();

                            }
                            //==========备案人=============================

                            String barIDorNO;

                            if (DEFINE_ID.equals("WP42")) {//判断是备案人还是撤案人
                                if (rstRecortBa.getString("nameid").contains("WP")) {//判断是否老库导入数据
                                    barIDorNO = SlectInfo.svs(statementRecordch, "record_people", rstRecortBa.getString("RecordBizNO"));
                                } else {
                                    barIDorNO = rstRecortBa.getString("MainOwner");
                                }
                            } else {
                                if (rstRecortBa.getString("nameid").contains("WP")) {//判断是否老库导入数据
                                    barIDorNO = SlectInfo.svs(statementRecordch, "re_record_people", rstRecortBa.getString("RecordBizNO"));
                                } else {
                                    barIDorNO = rstRecortBa.getString("MainOwner");
                                }

                            }


                            //System.out.println("备案人---" + barIDorNO);
                            ResultSet barResultSet = SlectInfo.bar(statementHousech, barIDorNO);
                            if (barResultSet != null) {

                                sqlWriter.write("INSERT CONTRACT_OWNER (ID, CONTRACT_NUMBER, NAME, " +
                                        "ID_TYPE, ID_NO, PHONE, ADDRESS, " +
                                        "BUSINESS, CONTRACT_DATE, TYPE, HOUSE_CODE) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")), Q.pm(rstRecortBa.getString("CompactNO"))
                                        , Q.pm(barResultSet.getString("Name")), Q.pCardType(barResultSet.getInt("IDType")), Q.pm(barResultSet.getString("IDNO"))
                                        , Q.p(barResultSet.getString("Phone")), Q.p(barResultSet.getString("Address"))
                                        , Q.p(rstRecortBa.getString("RecordBizNO")), Q.p(rstRecortBa.getTimestamp("BOTime"))
                                        , "'MAP_SELL'", Q.p(rstRecortBa.getString("No")) + ");"));
                                sqlWriter.newLine();

                            } else {

                                haveHouseStateNotBizWriter.write("此业务没有查到备案人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO"));
                                haveHouseStateNotBizWriter.newLine();
                                haveHouseStateNotBizWriter.flush();
                                continue;
                            }

                            //==========beforeHouse afterHouse=============================


                            //stratBizid=rstRecortBa.getString("RecordBizNO");
                            //==========beforeHouse
                            if (isFirst) {
                                stratBizid = rstRecortBa.getString("RecordBizNO") + "-s";
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

                                sqlWriter.write("(" + Q.v(Q.p(stratBizid), Q.pm(rstRecortBa.getString("HouseOrder"))
                                        , Q.pm(rstHouse.getString("UnitName")), Q.pm(rstHouse.getString("InFloorName"))
                                        , Q.pm(rstHouse.getBigDecimal("HouseArea")), Q.pm(rstHouse.getBigDecimal("UseArea"))
                                        , Q.pm(rstHouse.getBigDecimal("CommParam")), Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                        , Q.pm(rstHouse.getBigDecimal("LoftArea")), Q.pm(rstHouse.getBigDecimal("CommArea"))
                                        , Q.changeHouseType(rstHouse.getInt("HouseType")), rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                        , rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                        , Q.pm(rstRecortBa.getString("HouseStation")), Q.pm(rstRecortBa.getString("mappingDate") != null ? (!rstRecortBa.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortBa.getString("mappingDate") : rstRecortBa.getString("ChangeDate")) : rstRecortBa.getString("ChangeDate"))
                                        , Q.pm(rstRecortBa.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                        , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstRecortBa.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                        , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                        , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                        , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                        , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                        , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                        , Q.pm(rstHouse.getString("BuildName")), "Null", "Null", "Null", "Null", "Null", "Null", "Null", "Null" + ");"));
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

                                sqlWriter.write("(" + Q.v(Q.p(lastBizid), Q.pm(rstRecortBa.getString("HouseOrder"))
                                        , Q.pm(rstHouse.getString("UnitName")), Q.pm(rstHouse.getString("InFloorName"))
                                        , Q.pm(rstHouse.getBigDecimal("HouseArea")), Q.pm(rstHouse.getBigDecimal("UseArea"))
                                        , Q.pm(rstHouse.getBigDecimal("CommParam")), Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                        , Q.pm(rstHouse.getBigDecimal("LoftArea")), Q.pm(rstHouse.getBigDecimal("CommArea"))
                                        , Q.changeHouseType(rstHouse.getInt("HouseType")), rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                        , rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                        , Q.pm(rstRecortBa.getString("HouseStation"))
                                        , Q.pm(rstRecortBa.getString("mappingDate") != null ? (!rstRecortBa.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortBa.getString("mappingDate") : rstRecortBa.getString("ChangeDate")) : rstRecortBa.getString("ChangeDate"))
                                        , Q.pm(rstRecortBa.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                        , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstRecortBa.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                        , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                        , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                        , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                        , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                        , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                        , Q.pm(rstHouse.getString("BuildName")), "Null", rstRecortBa.getString("PoolMemo") != null ? Q.changePoolMemo(rstRecortBa.getInt("PoolMemo")) : "Null", "Null", "Null", "Null", Q.p(rstRecortBa.getString("RecordBizNO")), "Null", "Null" + ");"));
                                sqlWriter.newLine();

                                //----HOUSE_REG_INFO
                                sqlWriter.write("INSERT HOUSE_REG_INFO (ID, HOUSE_PORPERTY, HOUSE_FROM) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")), Q.pm(rstRecortBa.getString("HousePorperty"))
                                        , Q.pm(rstRecortBa.getString("HouseFrom")) + ");"));
                                sqlWriter.newLine();

                                //---SALE_INFO

                                sqlWriter.write("INSERT SALE_INFO (ID, PAY_TYPE, SUM_PRICE, HOUSEID) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")), Q.changePayType(rstRecortBa.getInt("PayType"))
                                        , Q.pm(rstRecortBa.getBigDecimal("SumPrice")), Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                sqlWriter.newLine();


                                //--- BUSINESS_HOUSE
                                sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")), Q.pm(rstRecortBa.getString("NO"))
                                        , Q.pm(rstRecortBa.getString("RecordBizNO")), Q.p(stratBizid), Q.p(lastBizid), "True" + ");"));
                                sqlWriter.newLine();

                                //=------ BUSINESS_POOL HOUSE_POOL
                                if (rstRecortBa.getString("Poolmemo") != null &&
                                        !rstRecortBa.getString("Poolmemo").equals("")
                                        && rstRecortBa.getInt("Poolmemo") != 0 && rstRecortBa.getInt("Poolmemo") != 221) {

                                    String dbid;
                                    ResultSet gyrResultSet = null;
                                    if (rstRecortBa.getString("nameid").contains("WP")) {//判断是否倒库进来的
                                        dbid = rstRecortBa.getString("dbid");
                                    } else {
                                        dbid = rstRecortBa.getString("RecordBizNO");
                                    }
                                    gyrResultSet = SlectInfo.gyr(statementHousech, dbid);
                                    if (gyrResultSet != null) {//系统中houseCard和ownerinfo 有关联
                                        int j = 0;
                                        while (gyrResultSet.next()) {

                                            sqlWriter.write("INSERT BUSINESS_POOL (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, PERC, " +
                                                    "PHONE, CREATE_TIME, MEMO,BUSINESS) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                    Q.pm(gyrResultSet.getString("Name")), Q.pCardType(gyrResultSet.getInt("IDType")),
                                                    Q.pm(gyrResultSet.getString("IDNO")),
                                                    gyrResultSet.getString("RELATION") != null ? (gyrResultSet.getInt("RELATION") != 0 ? gyrResultSet.getString("RELATION") : "NULL") : "Null",
                                                    Q.p(gyrResultSet.getBigDecimal("PoolArea")), Q.p(gyrResultSet.getString("Perc")),
                                                    Q.p(gyrResultSet.getString("phone")), Q.pm(rstRecortBa.getString("Botime")),
                                                    Q.pm(rstRecortBa.getString("Memo")),
                                                    Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                            sqlWriter.newLine();
                                            sqlWriter.write("INSERT HOUSE_POOL (HOUSE, POOL) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),
                                                    Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                            sqlWriter.newLine();
                                            j++;
                                        }
                                    } else {//没有关联从工作流里取

                                        gyrResultSet = SlectInfo.skgyr(statementShark, rstRecortBa.getString("Nameid"));
                                        if (gyrResultSet != null) {
                                            String skgyqr = gyrResultSet.getString("VariableValueVCHAR");
                                            int j=0;
                                            for (String str : skgyqr.split(";")) {

                                                gyrResultSet = SlectInfo.bar(statementHousech,str.split(",")[0]);
                                                if (gyrResultSet!=null){
                                                    sqlWriter.write("INSERT BUSINESS_POOL (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, PERC, " +
                                                            "PHONE, CREATE_TIME, MEMO,BUSINESS) VALUES ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                            Q.pm(gyrResultSet.getString("Name")), Q.pCardType(gyrResultSet.getInt("IDType")),
                                                            Q.pm(gyrResultSet.getString("IDNO")),
                                                            str.split(",")[3] != null ? (!str.split(",")[3].equals("0") ? str.split(",")[3] : "NULL") : "Null",
                                                            Q.p(str.split(",")[2]), Q.p(str.split(",")[4]),
                                                            Q.p(gyrResultSet.getString("phone")), Q.pm(rstRecortBa.getString("Botime")),
                                                            Q.pm(rstRecortBa.getString("Memo")),
                                                            Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                                    sqlWriter.newLine();
                                                    sqlWriter.write("INSERT HOUSE_POOL (HOUSE, POOL) VALUES ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),
                                                            Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                    sqlWriter.newLine();
                                                }else{
                                                   // haveHouseStateNotBizWriter.write("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                                                   // haveHouseStateNotBizWriter.newLine();
                                                  //  haveHouseStateNotBizWriter.flush();
                                                  //  System.out.println("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));

                                                }
                                                j++;
                                            }
                                        } else {
                                           // haveHouseStateNotBizWriter.write("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                                           // haveHouseStateNotBizWriter.newLine();
                                          //  haveHouseStateNotBizWriter.flush();
                                          //  System.out.println("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));

                                        }
                                    }
                                }


                            } else {
                                stratBizid = lastBizid;
                                lastBizid = rstRecortBa.getString("RecordBizNO");
                                // afterHouse=============================

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

                                sqlWriter.write("(" + Q.v(Q.p(lastBizid), Q.pm(rstRecortBa.getString("HouseOrder"))
                                        , Q.pm(rstHouse.getString("UnitName")), Q.pm(rstHouse.getString("InFloorName"))
                                        , Q.pm(rstHouse.getBigDecimal("HouseArea")), Q.pm(rstHouse.getBigDecimal("UseArea"))
                                        , Q.pm(rstHouse.getBigDecimal("CommParam")), Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                        , Q.pm(rstHouse.getBigDecimal("LoftArea")), Q.pm(rstHouse.getBigDecimal("CommArea"))
                                        , Q.changeHouseType(rstHouse.getInt("HouseType")), rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                        , rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                        , Q.pm(rstRecortBa.getString("HouseStation")), Q.pm(rstRecortBa.getString("mappingDate") != null ? (!rstRecortBa.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortBa.getString("mappingDate") : rstRecortBa.getString("ChangeDate")) : rstRecortBa.getString("ChangeDate"))
                                        , Q.pm(rstRecortBa.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                        , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstRecortBa.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                        , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                        , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                        , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                        , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                        , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                        , Q.pm(rstHouse.getString("BuildName")), "Null", rstRecortBa.getString("PoolMemo") != null ? Q.changePoolMemo(rstRecortBa.getInt("PoolMemo")) : "Null", "Null", "Null", "Null", Q.p(rstRecortBa.getString("RecordBizNO")), "Null", "Null" + ");"));
                                sqlWriter.newLine();


                                //----HOUSE_REG_INFO
                                sqlWriter.write("INSERT HOUSE_REG_INFO (ID, HOUSE_PORPERTY, HOUSE_FROM) VALUES ");

                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")), Q.pm(rstRecortBa.getString("HousePorperty"))
                                        , Q.pm(rstRecortBa.getString("HouseFrom")) + ");"));
                                sqlWriter.newLine();


                                //---SALE_INFO

                                sqlWriter.write("INSERT SALE_INFO (ID, PAY_TYPE, SUM_PRICE, HOUSEID) VALUES ");

                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")), rstRecortBa.getString("PayType") != null ? Q.changePayType(rstRecortBa.getInt("PayType")) : "NULL"
                                        , Q.pm(rstRecortBa.getBigDecimal("SumPrice")), Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                sqlWriter.newLine();

                                //--- BUSINESS_HOUSE
                                sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED) VALUES ");

                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")), Q.pm(rstRecortBa.getString("NO"))
                                        , Q.pm(rstRecortBa.getString("RecordBizNO")), Q.p(stratBizid), Q.p(lastBizid), "True" + ");"));
                                sqlWriter.newLine();

                                //=------ BUSINESS_POOL HOUSE_POOL
                                if (rstRecortBa.getString("Poolmemo") != null &&
                                        !rstRecortBa.getString("Poolmemo").equals("")
                                        && rstRecortBa.getInt("Poolmemo") != 0 && rstRecortBa.getInt("Poolmemo") != 221) {

                                    String dbid;
                                    ResultSet gyrResultSet = null;
                                    if (rstRecortBa.getString("nameid").contains("WP")) {//判断是否倒库进来的
                                        dbid = rstRecortBa.getString("dbid");
                                    } else {
                                        dbid = rstRecortBa.getString("RecordBizNO");
                                    }
                                    gyrResultSet = SlectInfo.gyr(statementHousech, dbid);
                                    if (gyrResultSet != null) {//系统中houseCard和ownerinfo 有关联
                                        int j = 0;
                                        while (gyrResultSet.next()) {

                                            sqlWriter.write("INSERT BUSINESS_POOL (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, PERC, " +
                                                    "PHONE, CREATE_TIME, MEMO,BUSINESS) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                    Q.pm(gyrResultSet.getString("Name")), Q.pCardType(gyrResultSet.getInt("IDType")),
                                                    Q.pm(gyrResultSet.getString("IDNO")),
                                                    gyrResultSet.getString("RELATION") != null ? (gyrResultSet.getInt("RELATION") != 0 ? gyrResultSet.getString("RELATION") : "NULL") : "Null",
                                                    Q.p(gyrResultSet.getBigDecimal("PoolArea")), Q.p(gyrResultSet.getString("Perc")),
                                                    Q.p(gyrResultSet.getString("phone")), Q.pm(rstRecortBa.getString("Botime")),
                                                    Q.pm(rstRecortBa.getString("Memo")),
                                                    Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                            sqlWriter.newLine();
                                            sqlWriter.write("INSERT HOUSE_POOL (HOUSE, POOL) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),
                                                    Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                            sqlWriter.newLine();
                                            j++;
                                        }
                                    } else {//没有关联从工作流里取

                                        gyrResultSet = SlectInfo.skgyr(statementShark, rstRecortBa.getString("Nameid"));
                                        if (gyrResultSet != null) {
                                            String skgyqr = gyrResultSet.getString("VariableValueVCHAR");
                                            int j=0;
                                            for (String str : skgyqr.split(";")) {

                                                gyrResultSet = SlectInfo.bar(statementHousech,str.split(",")[0]);
                                                if (gyrResultSet!=null){
                                                    sqlWriter.write("INSERT BUSINESS_POOL (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, PERC, " +
                                                            "PHONE, CREATE_TIME, MEMO,BUSINESS) VALUES ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                            Q.pm(gyrResultSet.getString("Name")), Q.pCardType(gyrResultSet.getInt("IDType")),
                                                            Q.pm(gyrResultSet.getString("IDNO")),
                                                            str.split(",")[3] != null ? (!str.split(",")[3].equals("0") ? str.split(",")[3] : "NULL") : "Null",
                                                            Q.p(str.split(",")[2]), Q.p(str.split(",")[4]),
                                                            Q.p(gyrResultSet.getString("phone")), Q.pm(rstRecortBa.getString("Botime")),
                                                            Q.pm(rstRecortBa.getString("Memo")),
                                                            Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                                    sqlWriter.newLine();
                                                    sqlWriter.write("INSERT HOUSE_POOL (HOUSE, POOL) VALUES ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),
                                                            Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                    sqlWriter.newLine();
                                                }else{
                                                    //haveHouseStateNotBizWriter.write("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                                                   // haveHouseStateNotBizWriter.newLine();
                                                   // haveHouseStateNotBizWriter.flush();
                                                   // System.out.println("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));

                                                }
                                                j++;
                                            }
                                        } else {
                                           // haveHouseStateNotBizWriter.write("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                                          //  haveHouseStateNotBizWriter.newLine();
                                          //  haveHouseStateNotBizWriter.flush();
                                          //  System.out.println("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));

                                        }
                                    }
                                }


                            }
                            //===ADD_HOUSE_STATUS
                            boolean IS_REMOVE = true, HOUSE_STATUS = false;
                            if (DEFINE_ID.equals("WP42")) {
                                IS_REMOVE = false;
                                HOUSE_STATUS = true;
                            }
                            sqlWriter.write("INSERT ADD_HOUSE_STATUS (ID, BUSINESS, STATUS, IS_REMOVE) VALUES  ");

                            sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")), Q.p(rstRecortBa.getString("RecordBizNO"))
                                    , Q.p("CONTRACTS_RECORD"), Q.p(IS_REMOVE) + ");"));
                            sqlWriter.newLine();
                            System.out.println("bbbb--"+rstRecortBa.getRow());
                            System.out.println("aaa--"+rstRecortBa.isLast());
                            System.out.println(HOUSE_STATUS);
                            //==== HOUSE_RECORD
                            if (rstRecortBa.isLast() && HOUSE_STATUS) {
                                System.out.println("aaaaaaaaa");

                                sqlWriter.write("INSERT HOUSE_RECORD (HOUSE_CODE, HOUSE, HOUSE_STATUS) VALUES ");

                                sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("No")), Q.p(rstRecortBa.getString("RecordBizNO"))
                                        , Q.p("CONTRACTS_RECORD") + ");"));
                                sqlWriter.newLine();

                            }
                            //BUSINESS_EMP

                            ResultSet empResultSet = statementShark.executeQuery("SELECT a.resourceid,a.LastStateTime,a.name as jdname,de.name as dename" +
                                    " FROM shark..SHKActivities as a left join shark..DGEmployee as de " +
                                    "on a.resourceid =de.no where ProcessId ='"+rstRecortBa.getString("Nameid") +"' and (a.name='受理' or a.name='复审' or a.name='审批' or a.name='归档')");
                            empResultSet.last();
                            int sl = empResultSet.getRow();
                            String res;
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            if (sl>0){
                                empResultSet.beforeFirst();;
                                while (empResultSet.next()){
                                    long lt = new Long(empResultSet.getLong("LastStateTime"));
                                    Date date = new Date(lt);
                                    res = simpleDateFormat1.format(date);
                                    sqlWriter.write("INSERT BUSINESS_EMP (ID, TYPE, EMP_CODE, EMP_NAME, BUSINESS_ID, OPER_TIME) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                            Q.changeBusinessEmpType(empResultSet.getString("jdname")),
                                            Q.pm(empResultSet.getString("resourceid")),
                                            Q.pm(empResultSet.getString("dename")),Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(res)
                                                    + ");"));

                                    sqlWriter.newLine();


                                    //===TASK_OPER

                                    sqlWriter.write("INSERT TASK_OPER (ID, BUSINESS, OPER_TIME, EMP_CODE, EMP_NAME, TASK_NAME, OPER_TYPE) VALUE ");
                                    sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                            Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(res),Q.pm(empResultSet.getString("resourceid")),
                                            Q.pm(empResultSet.getString("dename")),Q.pm(empResultSet.getString("jdname")),"'NEXT'"
                                                    + ");"));
                                    sqlWriter.newLine();


                                    if (empResultSet.getString("jdname").equals("受理")){
                                       sqlWriter.write("UPDATE OWNER_BUSINESS SET CREATE_TIME='"+res+"',APPLY_TIME='"+res+"' WHERE ID='"+rstRecortBa.getString("RecordBizNO")+"';");
                                       sqlWriter.newLine();
                                    }

                                    if (empResultSet.getString("jdname").equals("复审")){
                                        sqlWriter.write("UPDATE OWNER_BUSINESS SET CHECK_TIME='"+res+"' WHERE ID='"+rstRecortBa.getString("RecordBizNO")+"';");
                                        sqlWriter.newLine();
                                    }

                                }

                            }
                            //BUSINESS_FILE
                            ResultSet fileResulset = statementShark.executeQuery("select * from DGBizDoc where BizID='"+rstRecortBa.getString("nameid")+"'");
                            fileResulset.last();
                            int feilesl=fileResulset.getRow();
                            if (feilesl>0){
                                fileResulset.beforeFirst();
                                while (fileResulset.next()){
                                    sqlWriter.write("INSERT BUSINESS_FILE(ID, BUSINESS_ID, NAME, IMPORTANT_CODE, NO_FILE, IMPORTANT, PRIORITY) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.p("N"+rstRecortBa.getString("RecordBizNO")+"-"+fileResulset.getRow()),
                                            Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(fileResulset.getString("DocType")),
                                            "'未知'","True","False",Q.pm(String.valueOf(fileResulset.getRow()))+ ");"));
                                    sqlWriter.newLine();
                                }

                            }





                            isFirst = false;
                            EXCEPTION_biz_NO.add(rstRecortBa.getString("RecordBizNO"));
                            sqlWriter.flush();
                        } else {
                            haveHouseStateNotBizWriter.write("此房屋编号有重复HouseHistroy房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                            haveHouseStateNotBizWriter.newLine();
                            haveHouseStateNotBizWriter.flush();
                            System.out.println("此房屋编号有重复HouseHistroy房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                        }
                    }else{
                        rstRecortBa = statementRecord.executeQuery("select db.id as dbid,* from DGHouseRecord..Business as db left join " +
                                "HouseHistroy as hh on db.id=hh.Business where nameid not like '%WP50' and  " +
                                "workid not like '%WP51' and  workid not like '%WP85' and " +
                                "(workid NOT like '%WP42' AND  workid NOT like '%WP43') and hh.no= '"+rstHouse.getString("No")+"' and db.b>='"+BEGIN_DATE+"' order by botime");

                        rstRecortBa.last();
                        int ybaCount = rstRecortBa.getRow();
                        if (ybaCount>0){
                            sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS, " +
                                    "CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                            sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")), "0", "'BIZ_IMPORT'", Q.p(rstRecortBa.getString("MEMO"))
                                    , "'COMPLETE'","'已备案'", "'WPYBA'", "0", "Null", Q.p(rstRecortBa.getTimestamp("BOTime"))
                                    , Q.p(rstRecortBa.getTimestamp("BOTime")), "Null", Q.p(rstRecortBa.getTimestamp("BOTime")), Q.p(rstRecortBa.getTimestamp("BOTime")), "False", "'NORMAL_BIZ'")+");");
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
                             haveHouseStateNotBizWriter.write("此业务没有查到'已备案'房屋产权人信息房屋编号--"+rstRecortBa.getString("NO")+"业务编号--"+rstRecortBa.getString("RecordBizNO")+"Nameid--"+rstRecortBa.getString("Nameid"));
                             haveHouseStateNotBizWriter.newLine();
                             haveHouseStateNotBizWriter.flush();
                             continue;
                         }

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

                            sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")+"-s"),Q.pm(rstRecortBa.getString("HouseOrder"))
                                    ,Q.pm(rstHouse.getString("UnitName")),Q.pm(rstHouse.getString("InFloorName"))
                                    ,Q.pm(rstHouse.getBigDecimal("HouseArea")),Q.pm(rstHouse.getBigDecimal("UseArea"))
                                    ,Q.pm(rstHouse.getBigDecimal("CommParam")),Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                    ,Q.pm(rstHouse.getBigDecimal("LoftArea")),Q.pm(rstHouse.getBigDecimal("CommArea"))
                                    ,Q.changeHouseType(rstHouse.getInt("HouseType")),rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                    ,rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                    ,Q.pm(rstRecortBa.getString("HouseStation")),Q.pm(rstRecortBa.getString("mappingDate")!=null?(!rstRecortBa.getString("mappingDate").equals("1899-12-30 00:00:00.0")?rstRecortBa.getString("mappingDate"):rstRecortBa.getString("ChangeDate")):rstRecortBa.getString("ChangeDate"))
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


                            sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")),Q.pm(rstRecortBa.getString("HouseOrder"))
                                    ,Q.pm(rstHouse.getString("UnitName")),Q.pm(rstHouse.getString("InFloorName"))
                                    ,Q.pm(rstHouse.getBigDecimal("HouseArea")),Q.pm(rstHouse.getBigDecimal("UseArea"))
                                    ,Q.pm(rstHouse.getBigDecimal("CommParam")),Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                    ,Q.pm(rstHouse.getBigDecimal("LoftArea")),Q.pm(rstHouse.getBigDecimal("CommArea"))
                                    ,Q.changeHouseType(rstHouse.getInt("HouseType")),rstHouse.getString("UseType") != null ? Q.changeUseType(rstHouse.getInt("UseType")) : "'未知'"
                                    ,rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                    ,Q.pm(rstRecortBa.getString("HouseStation")),Q.pm(rstRecortBa.getString("mappingDate")!=null?(!rstRecortBa.getString("mappingDate").equals("1899-12-30 00:00:00.0")?rstRecortBa.getString("mappingDate"):rstRecortBa.getString("ChangeDate")):rstRecortBa.getString("ChangeDate"))
                                    ,Q.pm(rstRecortBa.getString("No")),"False",Q.pm("N"+rstHouse.getString("bno")),Q.pm(rstHouse.getString("MapNo")),Q.pm(rstHouse.getString("BlockNo"))
                                    ,Q.pm(rstHouse.getString("BuildNo")),Q.pm(rstRecortBa.getString("DoorNo")),rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                    ,rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1","0"
                                    ,Q.p(rstHouse.getString("BuildType")),Q.pm("N"+rstHouse.getString("hpno")),Q.pm(rstHouse.getString("hpnmae"))
                                    ,rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                    ,Q.pm("N"+rstHouse.getString("pno")),Q.pm(rstHouse.getString("pname")),Q.pm("N"+rstHouse.getString("hsno"))
                                    ,Q.pm(rstHouse.getString("hsname")),Q.pm("N"+rstHouse.getString("hdno")),Q.pm(rstHouse.getString("hdname"))
                                    ,Q.pm(rstHouse.getString("BuildName")),"Null",rstRecortBa.getString("PoolMemo")!=null?Q.changePoolMemo(rstRecortBa.getInt("PoolMemo")):"Null","Null","Null","Null",Q.p(rstRecortBa.getString("RecordBizNO")),"Null","Null"  + ");"));
                            sqlWriter.newLine();

                            //----HOUSE_REG_INFO
                            sqlWriter.write("INSERT HOUSE_REG_INFO (ID, HOUSE_PORPERTY, HOUSE_FROM) VALUES ");

                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(rstRecortBa.getString("HousePorperty"))
                                    ,Q.pm(rstRecortBa.getString("HouseFrom"))+ ");"));
                            sqlWriter.newLine();

                            //---SALE_INFO

                            sqlWriter.write("INSERT SALE_INFO (ID, PAY_TYPE, SUM_PRICE, HOUSEID) VALUES ");

                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),rstRecortBa.getString("PayType")!=null?Q.changePayType(rstRecortBa.getInt("PayType")):"NULL"
                                    ,Q.pm(rstRecortBa.getBigDecimal("SumPrice")),Q.pm(rstRecortBa.getString("RecordBizNO"))+ ");"));
                            sqlWriter.newLine();

                            //--- BUSINESS_HOUSE
                            sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED) VALUES ");

                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(rstRecortBa.getString("NO"))
                                    ,Q.pm(rstRecortBa.getString("RecordBizNO")),Q.p(rstRecortBa.getString("RecordBizNO")+"-s"),Q.p(rstRecortBa.getString("RecordBizNO")),"True" + ");"));
                            sqlWriter.newLine();

                            //--- ADD_HOUSE_STATUS
                            sqlWriter.write("INSERT ADD_HOUSE_STATUS (ID, BUSINESS, STATUS, IS_REMOVE) VALUES  ");
                            sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")),Q.p(rstRecortBa.getString("RecordBizNO"))
                                    ,Q.p("CONTRACTS_RECORD"),"False" + ");"));
                            sqlWriter.newLine();

                            //==== HOUSE_RECORD


                            sqlWriter.write("INSERT HOUSE_RECORD (HOUSE_CODE, HOUSE, HOUSE_STATUS) VALUES ");
                            sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("No")),Q.p(rstRecortBa.getString("RecordBizNO")),Q.p("CONTRACTS_RECORD")+ ");"));
                            sqlWriter.newLine();


                            //=------ BUSINESS_POOL HOUSE_POOL
                            if (rstRecortBa.getString("Poolmemo") != null &&
                                    !rstRecortBa.getString("Poolmemo").equals("")
                                    && rstRecortBa.getInt("Poolmemo") != 0 && rstRecortBa.getInt("Poolmemo") != 221) {

                                String dbid;
                                ResultSet gyrResultSet = null;
                                if (rstRecortBa.getString("nameid").contains("WP")) {//判断是否倒库进来的
                                    dbid = rstRecortBa.getString("dbid");
                                } else {
                                    dbid = rstRecortBa.getString("RecordBizNO");
                                }
                                gyrResultSet = SlectInfo.gyr(statementHousech, dbid);
                                if (gyrResultSet != null) {//系统中houseCard和ownerinfo 有关联
                                    int j = 0;
                                    while (gyrResultSet.next()) {

                                        sqlWriter.write("INSERT BUSINESS_POOL (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, PERC, " +
                                                "PHONE, CREATE_TIME, MEMO,BUSINESS) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                Q.pm(gyrResultSet.getString("Name")), Q.pCardType(gyrResultSet.getInt("IDType")),
                                                Q.pm(gyrResultSet.getString("IDNO")),
                                                gyrResultSet.getString("RELATION") != null ? (gyrResultSet.getInt("RELATION") != 0 ? gyrResultSet.getString("RELATION") : "NULL") : "Null",
                                                Q.p(gyrResultSet.getBigDecimal("PoolArea")), Q.p(gyrResultSet.getString("Perc")),
                                                Q.p(gyrResultSet.getString("phone")), Q.pm(rstRecortBa.getString("Botime")),
                                                Q.pm(rstRecortBa.getString("Memo")),
                                                Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                        sqlWriter.newLine();
                                        sqlWriter.write("INSERT HOUSE_POOL (HOUSE, POOL) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),
                                                Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                        sqlWriter.newLine();
                                        j++;
                                    }
                                } else {//没有关联从工作流里取

                                    gyrResultSet = SlectInfo.skgyr(statementShark, rstRecortBa.getString("Nameid"));
                                    if (gyrResultSet != null) {
                                        String skgyqr = gyrResultSet.getString("VariableValueVCHAR");
                                        int j=0;
                                        for (String str : skgyqr.split(";")) {

                                            gyrResultSet = SlectInfo.bar(statementHousech,str.split(",")[0]);
                                            if (gyrResultSet!=null){
                                                sqlWriter.write("INSERT BUSINESS_POOL (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, PERC, " +
                                                        "PHONE, CREATE_TIME, MEMO,BUSINESS) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                        Q.pm(gyrResultSet.getString("Name")), Q.pCardType(gyrResultSet.getInt("IDType")),
                                                        Q.pm(gyrResultSet.getString("IDNO")),
                                                        str.split(",")[3] != null ? (!str.split(",")[3].equals("0") ? str.split(",")[3] : "NULL") : "Null",
                                                        Q.p(str.split(",")[2]), Q.p(str.split(",")[4]),
                                                        Q.p(gyrResultSet.getString("phone")), Q.pm(rstRecortBa.getString("Botime")),
                                                        Q.pm(rstRecortBa.getString("Memo")),
                                                        Q.pm(rstRecortBa.getString("RecordBizNO")) + ");"));
                                                sqlWriter.newLine();
                                                sqlWriter.write("INSERT HOUSE_POOL (HOUSE, POOL) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortBa.getString("RecordBizNO")),
                                                        Q.pm(rstRecortBa.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                sqlWriter.newLine();
                                            }else{
                                               // haveHouseStateNotBizWriter.write("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                                               // haveHouseStateNotBizWriter.newLine();
                                               /// haveHouseStateNotBizWriter.flush();
                                                //System.out.println("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));

                                            }
                                            j++;
                                        }
                                    } else {
                                      //  haveHouseStateNotBizWriter.write("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));
                                       // haveHouseStateNotBizWriter.newLine();
                                      //  haveHouseStateNotBizWriter.flush();
                                       // System.out.println("此业务有共有情况没有共有权人信息房屋编号--" + rstRecortBa.getString("NO") + "业务编号--" + rstRecortBa.getString("RecordBizNO")+"DEFINE_ID--"+rstRecortBa.getString("NAMEID"));

                                    }
                                }
                            }
                            //BUSINESS_EMP======
                            ResultSet empResultSet = statementShark.executeQuery("SELECT a.resourceid,a.LastStateTime,a.name as jdname,de.name as dename" +
                                    " FROM shark..SHKActivities as a left join shark..DGEmployee as de " +
                                    "on a.resourceid =de.no where ProcessId ='"+rstRecortBa.getString("Nameid") +"' and (a.name='受理' or a.name='复审' or a.name='审批' or a.name='归档')");
                            empResultSet.last();
                            int sl = empResultSet.getRow();
                            String res;
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            if (sl>0){
                                empResultSet.beforeFirst();;
                                while (empResultSet.next()){
                                    long lt = new Long(empResultSet.getLong("LastStateTime"));
                                    Date date = new Date(lt);
                                    res = simpleDateFormat1.format(date);
                                    sqlWriter.write("INSERT BUSINESS_EMP (ID, TYPE, EMP_CODE, EMP_NAME, BUSINESS_ID, OPER_TIME) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                            Q.changeBusinessEmpType(empResultSet.getString("jdname")),
                                            Q.pm(empResultSet.getString("resourceid")),
                                            Q.pm(empResultSet.getString("dename")),Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(res)
                                                    + ");"));
                                    sqlWriter.newLine();

                                    //===TASK_OPER

                                    sqlWriter.write("INSERT TASK_OPER (ID, BUSINESS, OPER_TIME, EMP_CODE, EMP_NAME, TASK_NAME, OPER_TYPE) VALUE ");
                                    sqlWriter.write("(" + Q.v(Q.p(rstRecortBa.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                            Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(res),Q.pm(empResultSet.getString("resourceid")),
                                            Q.pm(empResultSet.getString("dename")),Q.pm(empResultSet.getString("jdname")),"'NEXT'"
                                                    + ");"));
                                    sqlWriter.newLine();

                                    if (empResultSet.getString("jdname").equals("受理")){
                                        sqlWriter.write("UPDATE OWNER_BUSINESS set CREATE_TIME='"+res+"',APPLY_TIME='"+res+"' WHERE ID='"+rstRecortBa.getString("RecordBizNO")+"';");
                                        sqlWriter.newLine();
                                    }

                                    if (empResultSet.getString("jdname").equals("复审")){
                                        sqlWriter.write("UPDATE OWNER_BUSINESS set CHECK_TIME='"+res+"' WHERE ID='"+rstRecortBa.getString("RecordBizNO")+"';");
                                        sqlWriter.newLine();
                                    }

                                }

                            }

                            //BUSINESS_FILE
                            ResultSet fileResulset = statementShark.executeQuery("select * from DGBizDoc where BizID='"+rstRecortBa.getString("nameid")+"'");
                            fileResulset.last();
                            int feilesl=fileResulset.getRow();
                            if (feilesl>0){
                                fileResulset.beforeFirst();
                                while (fileResulset.next()){
                                    sqlWriter.write("INSERT BUSINESS_FILE(ID, BUSINESS_ID, NAME, IMPORTANT_CODE, NO_FILE, IMPORTANT, PRIORITY) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.p("N"+rstRecortBa.getString("RecordBizNO")+"-"+fileResulset.getRow()),
                                            Q.pm(rstRecortBa.getString("RecordBizNO")),Q.pm(fileResulset.getString("DocType")),
                                            "'未知'","True","False",Q.pm(String.valueOf(fileResulset.getRow()))+ ");"));
                                    sqlWriter.newLine();
                                }

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
   //  预售许可证信息


//预售许可证信息
/**
        try {
              ResultSet rstRecortProject = statementRecord.executeQuery("select d.*,p.name as pname,p.no as pno from " +
                    "(select b.*,dd.name as ddname,dd.no as ddno from " +
                    "(select a.*,ds.name as dsname,ds.no as dsno,ds.DistrictID from " +
                    "(select db.*,hp.id as hpid,hp.no as hpno,hp.name as hpname,hp.address as hpaddress,hp.sectionid,hp.developerid " +
                    "from DGHouseRecord..Business as db left join DGHouseInfo..Project as hp on db.projectid=hp.id) as a " +
                    "left join DGHouseInfo..Section as ds on a.sectionid=ds.id) as b " +
                    "left join DGHouseInfo..District as dd on b.DistrictID=dd.id) as d " +
                    "left join DGHouseInfo..Developer as p on d.developerid=p.id " +
                    "where (d.nameid like '%WP50' or d.nameid like '%WP51') " +
                    "and d.b>='"+BEGIN_DATE+"' order by botime");
                   // "and d.RecordBizNO='2013111461'");

            rstRecortProject.last();
            int pjCount=rstRecortProject.getRow(),pi=1;
            System.out.println(pjCount);
            if (pjCount>0){
                rstRecortProject.beforeFirst();
                while (rstRecortProject.next()){
                    String[] temp;
                    temp = rstRecortProject.getString("Nameid").split("_");
                    DEFINE_ID = temp[temp.length - 1];
                    DEFINE_NAME ="商品房预售（销售）许可证";
                    String selectBiz=null;
                    if (DEFINE_ID.equals("WP51")){
                        ResultSet rstRecortbiz = statementRecordch.executeQuery("select RecordBizNO from DGHouseRecord..Business where id='" + rstRecortProject.getString("selectbiz") + "'");
                        rstRecortbiz.last();
                        int pjSelectCount = rstRecortbiz.getRow();
                        if (pjSelectCount>0){
                            selectBiz = rstRecortbiz.getString("RecordBizNO");
                        }
                    }
                   //OWNER_BUSINESS======
                    sqlPWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                            " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                    sqlPWriter.write("(" + Q.v(Q.p(rstRecortProject.getString("RecordBizNO")), "0", "'BIZ_IMPORT'", Q.p(rstRecortProject.getString("MEMO"))
                            , "'COMPLETE'", Q.p(DEFINE_NAME), Q.p("WP50"), "0", Q.p(selectBiz), Q.p(rstRecortProject.getTimestamp("BOTime"))
                            , Q.p(rstRecortProject.getTimestamp("BOTime")), "Null", Q.p(rstRecortProject.getTimestamp("BOTime")), Q.p(rstRecortProject.getTimestamp("BOTime")), "False", "'NORMAL_BIZ'") + ");");
                    sqlPWriter.newLine();

                    if (selectBiz!=null){
                        sqlPWriter.write("UPDATE OWNER_BUSINESS SET STATUS='COMPLETE_CANCEL' WHERE ID='"+selectBiz+"';");
                        sqlPWriter.newLine();
                    }

                    //BUSINESS_EMP======
                    ResultSet empResultSet = statementShark.executeQuery("SELECT a.resourceid,a.LastStateTime,a.name as jdname,de.name as dename" +
                            " FROM shark..SHKActivities as a left join shark..DGEmployee as de " +
                            "on a.resourceid =de.no where ProcessId ='"+rstRecortProject.getString("Nameid") +"' and (a.name='受理' or a.name='复审' or a.name='审批' or a.name='归档')");


                    empResultSet.last();
                    int sl = empResultSet.getRow();
                    String res;
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (sl>0){
                        empResultSet.beforeFirst();;
                        while (empResultSet.next()){
                            long lt = new Long(empResultSet.getLong("LastStateTime"));
                            Date date = new Date(lt);
                            res = simpleDateFormat1.format(date);
                            sqlPWriter.write("INSERT BUSINESS_EMP (ID, TYPE, EMP_CODE, EMP_NAME, BUSINESS_ID, OPER_TIME) VALUES ");
                            sqlPWriter.write("(" + Q.v(Q.p(rstRecortProject.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                    Q.changeBusinessEmpType(empResultSet.getString("jdname")),
                                    Q.pm(empResultSet.getString("resourceid")),
                                    Q.pm(empResultSet.getString("dename")),Q.pm(rstRecortProject.getString("RecordBizNO")),Q.pm(res)
                                            + ");"));
                            sqlPWriter.newLine();

                            //===TASK_OPER

                            sqlPWriter.write("INSERT TASK_OPER (ID, BUSINESS, OPER_TIME, EMP_CODE, EMP_NAME, TASK_NAME, OPER_TYPE) VALUE ");
                            sqlPWriter.write("(" + Q.v(Q.p(rstRecortProject.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                    Q.pm(rstRecortProject.getString("RecordBizNO")),Q.pm(res),Q.pm(empResultSet.getString("resourceid")),
                                    Q.pm(empResultSet.getString("dename")),Q.pm(empResultSet.getString("jdname")),"'NEXT'"
                                            + ");"));
                            sqlPWriter.newLine();


                            if (empResultSet.getString("jdname").equals("受理")){
                                sqlPWriter.write("UPDATE OWNER_BUSINESS set CREATE_TIME='"+res+"',APPLY_TIME='"+res+"' WHERE ID='"+rstRecortProject.getString("RecordBizNO")+"';");
                                sqlPWriter.newLine();
                            }

                            if (empResultSet.getString("jdname").equals("复审")){
                                sqlPWriter.write("UPDATE OWNER_BUSINESS set CHECK_TIME='"+res+"' WHERE ID='"+rstRecortProject.getString("RecordBizNO")+"';");
                                sqlPWriter.newLine();
                            }


                        }

                    }
                    //PROJECT=====
                    sqlPWriter.write("INSERT PROJECT (ID, PROJECT_CODE, NAME, ADDRESS, DEVELOPER_NAME, DEVELOPER_CODE, SECTION_NAME, SECTION_CODE, DISTRICT_CODE, DISTRICT_NAME, BUSINESS) VALUES ");
                    sqlPWriter.write("(" + Q.v(Q.p(rstRecortProject.getString("RecordBizNO")),
                            Q.pm("N"+rstRecortProject.getString("hpno")),Q.pm(rstRecortProject.getString("hpname")),
                            Q.pm(rstRecortProject.getString("hpaddress")),Q.pm(rstRecortProject.getString("pname")),
                            Q.pm("N"+rstRecortProject.getString("pno")),Q.pm(rstRecortProject.getString("dsname")),
                            Q.pm("N"+rstRecortProject.getString("dsno")),Q.pm(rstRecortProject.getString("ddname")),
                            Q.pm(rstRecortProject.getString("ddno")),Q.p(rstRecortProject.getString("RecordBizNO"))
                            + ");"));
                    sqlPWriter.newLine();


                    //PROJECT_SELL_INFO=====

                    ResultSet rstRecortProjectbiz = statementRecordch.executeQuery("select a.*,hp.no as hpno,hp.LandCardNO,hp.LandProperty," +
                            "hp.luts,hp.luto,hp.landGetMode,hp.landArea,hp.BCLicence,hp.PClicence," +
                            "hp.USLLicence,hp.YYLicence from (select db.*,hpc.no as hpcno,hpc.printTime," +
                            "hpc.Cancel,hpc.houseCount,hpc.BuildCount,hpc.sumArea,hpc.useType," +
                            "hpc.Sellobject,hpc.yearNum,hpc.orderNum,cardNo from DGHouseRecord..Business as db " +
                            "left join DGHouseInfo..ProjectCard as hpc on db.id=hpc.bizid where (nameid like '%WP50' " +
                            "or  nameid like '%WP51') and hpc.id is not null) as a left join DGHouseInfo..Project as hp " +
                            "on a.projectid=hp.id where RecordBizNO='"+rstRecortProject.getString("RecordBizNO")+"'");
                    rstRecortProjectbiz.last();
                    int p=rstRecortProjectbiz.getRow();
                    if (p>0){
                        sqlPWriter.write("INSERT PROJECT_SELL_INFO (ID, HOUSE_COUNT, BUILD_COUNT, AREA, " +
                                "USE_TYPE, SELL_OBJECT, TYPE, LAND_CARD_NO, NUMBER, LAND_PROPERTY," +
                                " BEGIN_USE_TIME, END_USE_TIME, LAND_GET_MODE, LAND_AREA, " +
                                "LAND_CARD_TYPE, LAND_USE_TYPE, CREATE_CARD_CODE, " +
                                "CREATE_PREPARE_CARD_CODE, LICENSE_NUMBER, CREATE_LAND_CARD_CODE)" +
                                " VALUES ");
                        sqlPWriter.write("(" + Q.v(Q.p(rstRecortProject.getString("RecordBizNO")),
                                Q.p(rstRecortProjectbiz.getString("houseCount")),Q.p(rstRecortProjectbiz.getString("BuildCount")),
                                Q.p(rstRecortProjectbiz.getBigDecimal("sumArea")),Q.p(rstRecortProjectbiz.getString("useType")),
                                Q.p(rstRecortProjectbiz.getString("Sellobject")),"'MAP_SELL'",Q.p(rstRecortProjectbiz.getString("LandCardNO")),
                                "''",!rstRecortProjectbiz.getString("LandProperty").equals("0")?Q.p(rstRecortProjectbiz.getString("LandProperty")):"Null",
                                rstRecortProjectbiz.getString("luts")!=null?Q.p(rstRecortProjectbiz.getString("luts")):"'1970-01-01 08:00:00.0'",
                                rstRecortProjectbiz.getString("luto")!=null?Q.p(rstRecortProjectbiz.getString("luto")):"'1970-01-01 08:00:00.0'",
                                !rstRecortProjectbiz.getString("landGetMode").equals("0")?Q.p(rstRecortProjectbiz.getString("LandProperty")):"Null",
                                Q.p(rstRecortProjectbiz.getBigDecimal("landArea")),"'landCardType.stateOwned'",
                                rstRecortProjectbiz.getString("useType")!=null?Q.p(rstRecortProjectbiz.getString("useType")):"''",
                                rstRecortProjectbiz.getString("BCLicence")!=null?Q.pm(rstRecortProjectbiz.getString("BCLicence")):"''",
                                rstRecortProjectbiz.getString("PClicence")!=null?Q.pm(rstRecortProjectbiz.getString("PClicence")):"''",
                                Q.p(rstRecortProjectbiz.getString("YYLicence")),Q.p(rstRecortProjectbiz.getString("USLLicence"))
                                + ");"));
                        sqlPWriter.newLine();

                        // MAKE_CARD=====
                        sqlPWriter.write("INSERT MAKE_CARD (ID, NUMBER, TYPE, BUSINESS_ID, ENABLE) VALUE ");
                        sqlPWriter.write("(" + Q.v(Q.p(rstRecortProject.getString("RecordBizNO")),
                                Q.pm(rstRecortProjectbiz.getString("hpcno")),"'PROJECT_RSHIP'",
                                Q.p(rstRecortProject.getString("RecordBizNO")),
                                "True"
                                        + ");"));
                        sqlPWriter.newLine();


                       //PROJECT_CARD=====

                        empResultSet = statementShark.executeQuery("SELECT a.resourceid,a.LastStateTime,a.name as jdname,de.name as dename" +
                                " FROM shark..SHKActivities as a left join shark..DGEmployee as de " +
                                "on a.resourceid =de.no where ProcessId ='"+rstRecortProject.getString("Nameid") +"' and (a.name='缮证')");
                        empResultSet.last();
                        sl = empResultSet.getRow();
                        String MAKE_EMP_CODE=null,MAKE_EMP_NAME=null;
                        if (sl>0) {
                            MAKE_EMP_CODE = empResultSet.getString("resourceid");
                            MAKE_EMP_NAME = empResultSet.getString("dename");
                        }
                        sqlPWriter.write("INSERT PROJECT_CARD(ID, YEAR_NUMBER, ORDER_NUMBER, PRINT_TIME, MAKE_EMP_CODE, MAKE_EMP_NAME, PROJECT) VALUE ");
                        sqlPWriter.write("(" + Q.v(Q.p(rstRecortProject.getString("RecordBizNO")),
                                Q.p(rstRecortProjectbiz.getString("yearNum")),Q.p(rstRecortProjectbiz.getString("orderNum")),
                                rstRecortProjectbiz.getString("printTime")!=null?Q.p(rstRecortProjectbiz.getString("printTime")):"'1970-01-01 08:00:01.0'",
                                Q.p(MAKE_EMP_CODE),Q.p(MAKE_EMP_NAME),Q.p(rstRecortProject.getString("RecordBizNO"))
                                        + ");"));
                        sqlPWriter.newLine();





                     // BUILD ========

                        ResultSet rstRecortBuild = statementRecordch.executeQuery("select c.*,hb.no as hbno,hb.* from " +
                                "(select b.*,pcab.SumArea as bSumArea,pcab.sumCount as bSumCount,pcab.HomeArea as bHomeArea,pcab.HomeCount as bHomeCount,pcab.unhomeArea as bunhomeArea,pcab.unhomeCount as bunhomeCount,pcab.netPointArea as bnetPointArea,pcab.netPointCount as bnetPointCount,pcab.Build,pcab.projectCard from " +
                                "(select a.*,hp.no as hpno from " +
                                "(select db.*,hpc.id as hpcid " +
                                "from DGHouseRecord..Business as db left join " +
                                "DGHouseInfo..ProjectCard as hpc on db.id=hpc.bizid " +
                                "where (nameid like '%WP50' or  nameid like '%WP51') and hpc.id is not null) as a " +
                                "left join DGHouseInfo..Project as hp on a.projectid=hp.id) as b " +
                                "left join DGHouseInfo..PorjectCardAndBuild as pcab on b.hpcid=pcab.projectCard) c " +
                                "left join DGHouseInfo..Build as hb on c.build=hb.id " +
                                "where RecordBizNo='"+rstRecortProject.getString("RecordBizNO")+"' order by botime");

                        rstRecortBuild.last();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                        if (rstRecortBuild.getRow()>0){
                            rstRecortBuild.beforeFirst();
                            while (rstRecortBuild.next()){
                                sqlPWriter.write("INSERT BUILD (ID, MAP_NUMBER, BLOCK_NO, BUILD_NO, BUILD_CODE,NAME, " +
                                        "DOOR_NO, UNINT_COUNT, FLOOR_COUNT, UP_FLOOR_COUNT, DOWN_FLOOR_COUNT, HOUSE_COUNT, AREA, " +
                                        "BUILD_TYPE, STRUCTURE, HOME_COUNT, HOME_AREA, UNHOME_COUNT, UNHOME_AREA, SHOP_COUNT, " +
                                        "SHOP_AREA, PROJECT, COMPLETE_DATE, BUILD_DEVELOPER_NUMBER, MAP_TIME) VALUE ");

                                sqlPWriter.write("(" + Q.v(Q.p(rstRecortBuild.getString("RecordBizNO")+"-"+rstRecortBuild.getRow()),Q.pm(rstRecortBuild.getString("MapNO")),
                                        Q.pm(rstRecortBuild.getString("BlockNO")),Q.pm(rstRecortBuild.getString("BuildNO")),
                                        Q.pm("N"+rstRecortBuild.getString("hbno")),Q.pm(rstRecortBuild.getString("BuildName")),
                                        Q.p(rstRecortBuild.getString("DoorNO")),Q.p(rstRecortBuild.getString("UnintCount")),
                                        rstRecortBuild.getString("FloorCount") != null ? Q.pm(rstRecortBuild.getString("FloorCount")) : "0",
                                        rstRecortBuild.getString("FloorCount") != null ? Q.pm(rstRecortBuild.getString("FloorCount")) : "0",
                                        "0",Q.p(rstRecortBuild.getString("bSumCount")),Q.p(rstRecortBuild.getBigDecimal("bSumArea")),
                                        Q.p(rstRecortBuild.getString("BuildType")),rstRecortBuild.getString("Structure") != null ? Q.p(rstRecortBuild.getString("Structure")) : "827",
                                        Q.p(rstRecortBuild.getString("bHomeCount")),Q.p(rstRecortBuild.getBigDecimal("bHomeArea")),
                                        Q.p(rstRecortBuild.getString("bunhomeCount")),Q.p(rstRecortBuild.getBigDecimal("bunhomeArea")),
                                        Q.p(rstRecortBuild.getString("netPointCount")),Q.p(rstRecortBuild.getBigDecimal("bnetPointArea")),
                                        Q.pm(rstRecortProject.getString("RecordBizNO")),
                                        rstRecortBuild.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstRecortBuild.getTimestamp("FirmlyDate"))) : "NULL",
                                        Q.p(rstRecortBuild.getString("DoorNO")),Q.p(Q.nowFormatTime())
                                         + ");"));
                                sqlPWriter.newLine();
                            }
                        }
                    }else{
                        sqlPWriterErree.write("此业务没有查到预售许可证信息--Projectid-" + rstRecortProject.getString("Projectid") + "-业务编号--" + rstRecortProject.getString("RecordBizNO"));
                        sqlPWriterErree.newLine();
                        sqlPWriterErree.flush();
                    }




                    //BUSINESS_FILE
                    ResultSet fileResulset = statementShark.executeQuery("select * from DGBizDoc where BizID='"+rstRecortProject.getString("nameid")+"'");
                    fileResulset.last();
                    int feilesl=fileResulset.getRow();
                    if (feilesl>0){
                        fileResulset.beforeFirst();
                        while (fileResulset.next()){
                            sqlPWriter.write("INSERT BUSINESS_FILE(ID, BUSINESS_ID, NAME, IMPORTANT_CODE, NO_FILE, IMPORTANT, PRIORITY) VALUES ");
                            sqlPWriter.write("(" + Q.v(Q.p("N"+rstRecortProject.getString("RecordBizNO")+"-"+fileResulset.getRow()),
                                    Q.pm(rstRecortProject.getString("RecordBizNO")),Q.pm(fileResulset.getString("DocType")),
                                    "'未知'","True","False",Q.pm(String.valueOf(fileResulset.getRow()))+ ");"));
                            sqlPWriter.newLine();
                        }

                    }
                    sqlPWriter.flush();
                    pi++;
                    System.out.println(pi+"-"+pjCount);
                }
            }



            System.out.println("recortProject is complate");
        } catch (Exception e) {
            System.out.println("recortProject is errer");
            e.printStackTrace();
            return;
        }
**/

    }
}
