package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;
import com.scoopit.weedfs.client.net.Result;
import com.wxy.House.SlectInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2016-11-17.
 * 东港倒库程序，没有备案和预售
 */
public class DGHouseOwnerRecord {

    private static final String BEGIN_DATE = "2016-09-30";

    private static final String OUT_PATH_FILE = "/houseOwnerRecord.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE = "/HouseOwnerError.sql";

    private static final String DB_RECORD_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/DGHouseRecord";

    private static final String DB_SHARK_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/shark";

    private static final String DB_HOUSE_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/DGHouseInfo";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static final String DB_FANG_CHAN_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/fang_chan_dg";

    private static Connection recordConnection;

    private static Connection houseConnection;

    private static Connection sharkConnection;

    private static Connection ownerRecordConnection;

    private static Connection fangchanConnection;


    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;


    private static Statement statementRecord;

    private static Statement statementRecordch;

    private static Statement statementHouse;

    private static Statement statementHousech;

    private static Statement statementHousech2;

    private static Statement statementShark;

    private static Statement statementOwnerRecord;

    private static Statement statementFangchan;

    private static ResultSet recordResultSet;

    private static ResultSet houseResultSet;

    private static ResultSet fangChanResultSet;

    private static Set<String> LOCKED_HOUSE_NO = new HashSet<>();

    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();

    private static Set<String> SELECT_DEFINE_ID = new HashSet<>();

    private static Set<String> REPEAT_NAMEID = new HashSet<>();

    private static Set<String> HOUSE_ERROR = new HashSet<>();

    private static Set<String> DEAL_DEFINE_ID= new HashSet<>();

    private static Set<String> POOL_OWNER_ID = new HashSet<>();

    private static Set<String> MORTGAEGE_DEFINE_ID = new HashSet<>();

    private static String DEFINE_ID;
    private static String DEFINE_NAME;
    private static String NAME_ID;
    private static String bizid;//业务编号

    private static String selectbizid;//bei

    private static boolean haveRepeat;

    private static String stratHouseId;

    private static String afterHouseId;

    private static boolean isFirst;

    private static boolean addStateisFirst;
    private static ResultSet rstHouse;

    public static void main(String agr[]) throws SQLException {


        //有问题房屋编号，不导入
        HOUSE_ERROR.add("118058");
        HOUSE_ERROR.add("118057");
        HOUSE_ERROR.add("128518");
        HOUSE_ERROR.add("144016");
        //有selectbiz业务ID




        //不导入的业务编号
        NO_EXCEPTION_DEFINE_ID.add("WP30");//新建房屋
        NO_EXCEPTION_DEFINE_ID.add("WP31");//无籍房屋
        NO_EXCEPTION_DEFINE_ID.add("WP75");//集资建房初始登记

        NO_EXCEPTION_DEFINE_ID.add("WP35");//所有权更正登记
        NO_EXCEPTION_DEFINE_ID.add("WP35");//所有权更正登记

        NO_EXCEPTION_DEFINE_ID.add("WP52");//名称变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP53");//自翻扩改
        NO_EXCEPTION_DEFINE_ID.add("WP54");//分照
        NO_EXCEPTION_DEFINE_ID.add("WP55");//合照
        NO_EXCEPTION_DEFINE_ID.add("WP76");//租赁登记
        NO_EXCEPTION_DEFINE_ID.add("WP77");//地役权登记
        NO_EXCEPTION_DEFINE_ID.add("WP78");//地役权变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP79");//地役权转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP80");//地役权注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP44");//预购商品房预告登记
        NO_EXCEPTION_DEFINE_ID.add("WP45");//预购商品房预告变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP46");//预购商品房预告注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP47");//预购商品房预告更正登记
        NO_EXCEPTION_DEFINE_ID.add("WP48");//预购商品房预告异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP49");//预购商品房预告异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP32");//所有权遗失补照
        NO_EXCEPTION_DEFINE_ID.add("WP33");//换照
        NO_EXCEPTION_DEFINE_ID.add("WP35");//所有权更正登记
        NO_EXCEPTION_DEFINE_ID.add("WP36");//所有权异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP37");//所有权异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP38");//注销登记(灭籍)
        NO_EXCEPTION_DEFINE_ID.add("WP34");//声明作废
        NO_EXCEPTION_DEFINE_ID.add("WP39");//解除声明作废
        NO_EXCEPTION_DEFINE_ID.add("WP69");//房屋所有权转移预告登记
        NO_EXCEPTION_DEFINE_ID.add("WP70");//房屋所有权转移预告注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP22");//他项权更正登记
        NO_EXCEPTION_DEFINE_ID.add("WP23");//他项权异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP24");//他项权异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP25");//他项权遗失补照
        NO_EXCEPTION_DEFINE_ID.add("WP26");//他项权预告登记证明补证
        NO_EXCEPTION_DEFINE_ID.add("WP27");//他项权在建工程遗失补证
        NO_EXCEPTION_DEFINE_ID.add("WP28");//在建工程抵押权异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP29");//在建工程抵押权异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP29");//在建工程抵押权异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP18");//在建工程抵押权设定登记
        NO_EXCEPTION_DEFINE_ID.add("WP19");//在建工程抵押权设定变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP20");//在建工程抵押权设定转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP21");//在建工程抵押权设定注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP21");//在建工程抵押权设定注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP73");//房屋查封登记
        NO_EXCEPTION_DEFINE_ID.add("WP74");//房屋查封解除登记
        NO_EXCEPTION_DEFINE_ID.add("WP81");//工程查封登记
        NO_EXCEPTION_DEFINE_ID.add("WP82");//工程查封解除登记
        NO_EXCEPTION_DEFINE_ID.add("WP88");//房屋续封登记
        NO_EXCEPTION_DEFINE_ID.add("WP11");//房屋所有权抵押转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP12");//房屋所有权抵押注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP10");//房屋所有权抵押变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP14");//最高额抵押权确定登记
        NO_EXCEPTION_DEFINE_ID.add("WP15");//最高额抵押权设定变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP16");//最高额抵押权设定转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP17");//最高额抵押权设定注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP84");//在建工程房屋抵押权注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP2");//预购商品房抵押权预告变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP3");//预购商品房抵押权预告转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP4");//预购商品房抵押权预告注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP6");//房屋抵押权预告变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP7");//房屋抵押权预告转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP8");//房屋抵押权预告注销登记
        //交易备案 业务DEFINE_ID
        DEAL_DEFINE_ID.add("WP41");//商品房交易
        DEAL_DEFINE_ID.add("WP56");//二手房交易
        DEAL_DEFINE_ID.add("WP57");//企业改制
        DEAL_DEFINE_ID.add("WP58");//赠与
        DEAL_DEFINE_ID.add("WP59");//继承(直系/非直系)
        DEAL_DEFINE_ID.add("WP60");//判决(调解、裁定、仲裁、协议离婚)
        DEAL_DEFINE_ID.add("WP61");//房屋拍卖
        DEAL_DEFINE_ID.add("WP62");//投资入股
        DEAL_DEFINE_ID.add("WP63");//兼并合并
        DEAL_DEFINE_ID.add("WP64");//使用权交易
        DEAL_DEFINE_ID.add("WP65");//抵债业务
        DEAL_DEFINE_ID.add("WP66");//政府奖励
        DEAL_DEFINE_ID.add("WP67");//房改房屋
        DEAL_DEFINE_ID.add("WP68");//分照交易
        DEAL_DEFINE_ID.add("WP72");//回迁房屋
        //抵押登记
        MORTGAEGE_DEFINE_ID.add("WP1");//预购商品房预告登记
        MORTGAEGE_DEFINE_ID.add("WP5");//房屋抵押权预告登记
        MORTGAEGE_DEFINE_ID.add("WP9");//房屋所有权抵押登记
        MORTGAEGE_DEFINE_ID.add("WP13");//最高额抵押登记
        MORTGAEGE_DEFINE_ID.add("WP83");//在建工程抵押登记


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
            //sqlWriter.write("ALTER TABLE HOUSE_OWNER_RECORD.OWNER_BUSINESS ADD NAMEID VARBINARY (25) NULL;");
            sqlWriter.newLine();
            sqlWriter.flush();
        } catch (IOException e) {
            System.out.println("sqlWriter 文件创建失败");
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
            System.out.println("sharkConnection successful");
        } catch (Exception e) {
            System.out.println("sharkConnection is errer");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            houseConnection = DriverManager.getConnection(DB_HOUSE_URL, "sa", "dgsoft");
            statementHouse = houseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementHousech = houseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementHousech2 = houseConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("houseConnection successful");
        } catch (Exception e) {
            System.out.println("houseConnection is errer");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "dgsoft");
            statementOwnerRecord = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("ownerRecordConnection successful");
        } catch (Exception e) {
            System.out.println("ownerRecordConnection is errer");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            fangchanConnection = DriverManager.getConnection(DB_FANG_CHAN_URL, "sa", "dgsoft");
            statementFangchan = fangchanConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("fangchanConnection successful");
        } catch (Exception e) {
            System.out.println("fangchanConnection is errer");
            e.printStackTrace();
            return;
        }


        try {
//             rstHouse = statementHouse.executeQuery("select hd.no as hdno,hd.name as hdname,e.* from " +
//                    " (select hs.no as hsno,hs.name as hsname,hs.DistrictID,d.* from" +
//                    " (select p.no as pno,p.name as pname,c.* from" +
//                    " (select hp.no as hpno,hp.name as hpnmae,hp.DeveloperID,hp.SectionID,a.* from"+
//                    " (select b.no as bno,b.BuildName,b.DoorNO as bDoorNO,b.Mapno,b.blockNo,b.buildNo,b.BuildType,b.FirmlyDate,"+
//                    " b.FloorCount,b.ProjectID,h.* from DGHouseInfo..house as h left join DGHouseInfo..Build as b on h.buildid=b.id"+
//                    " ) as a"+
//                    " left join project as hp on a.ProjectID=hp.id) as c"+
//                    " left join Developer as p on c.DeveloperID=p.id) as d"+
//                    " left join Section as hs on d.sectionid=hs.id) as e"+
////                    " left join District as hd on e.DistrictID = hd.id");
//                    " left join District as hd on e.DistrictID = hd.id" +
//                    " where (e.no='53951')"); // 单笔116146 多69759 初始 B94N1-5-02 预告登记97793 产权共有权人46343 房屋预抵B860N3-1-01

            rstHouse = statementHouse.executeQuery("select f.*,sw.value as useTypevalue,sw.memo as designUseType from" +
                    " (select hd.no as hdno,hd.name as hdname,e.* from" +
                    " (select hs.no as hsno,hs.name as hsname,hs.DistrictID,d.* from" +
                    " (select p.no as pno,p.name as pname,c.* from" +
                    " (select hp.no as hpno,hp.name as hpnmae,hp.DeveloperID,hp.SectionID,a.* from" +
                    " (select b.no as bno,b.BuildName,b.DoorNO as bDoorNO,b.Mapno,b.blockNo,b.buildNo,b.BuildType,b.FirmlyDate," +
                    " b.FloorCount,b.ProjectID,h.* from DGHouseInfo..house as h" +
                    " left join DGHouseInfo..Build as b on h.buildid=b.id) as a" +
                    " left join DGHouseInfo..project as hp on a.ProjectID=hp.id) as c" +
                    " left join DGHouseInfo..Developer as p on c.DeveloperID=p.id) as d" +
                    " left join DGHouseInfo..Section as hs on d.sectionid=hs.id) as e" +
                    " left join DGHouseInfo..District as hd on e.DistrictID = hd.id) as f" +
                    " left join Shark..DGWordBook as sw on f.useType= sw.id where f.no='69759'");



            rstHouse.last();
            System.out.println("rstHouseCount-Start-:" + rstHouse.getRow());
            int sumCount = rstHouse.getRow();
            rstHouse.beforeFirst();
            sqlWriter.newLine();
            int i=0;
            while (rstHouse.next()){
                ResultSet rstRecortRecord = statementRecord.executeQuery("select a.*,sw.value as useTypevalue,sw.memo as designUseType from " +
                        "(select hb.id as dbid,hb.NameID,hb.WorkID,hb.BizID,hb.BOTime,hb.RegisterTime," +
                        "hb.FinalWorker,hb.EnrolWorker,hb.Memo as dbmemo,hb.FinalTime,hb.AddBizTime," +
                        "hb.SelectBiz,hb.RecordBizNO,hb.ProjectID,hb.b,hh.* from DGHouseRecord..Business as hb left join " +
                        "HouseHistroy as hh on hb.id=hh.Business where " +
                        "hb.workid not like '%WP50' and  hb.workid not like '%WP51' and  hb.workid not like '%WP85' " +
                        "and hb.workid not like '%WP42' and hb.workid not like '%WP43' " +
                        "and hb.workid not like '%WP52' and hb.workid not like '%WP53' and hb.workid not like '%WP54' " +
                        "and hb.workid not like '%WP55' and hb.workid not like '%WP76' and hb.workid not like '%WP77' " +
                        "and hb.workid not like '%WP78' and hb.workid not like '%WP79' and hb.workid not like '%WP80' " +
                        "and hb.workid not like '%WP44' and hb.workid not like '%WP45' and hb.workid not like '%WP46' " +
                        "and hb.workid not like '%WP47' and hb.workid not like '%WP48' and hb.workid not like '%WP49' " +
                        "and hb.workid not like '%WP32' and hb.workid not like '%WP33' and hb.workid not like '%WP35' " +
                        "and hb.workid not like '%WP36' and hb.workid not like '%WP37' and hb.workid not like '%WP38' " +
                        "and hb.workid not like '%WP34' and hb.workid not like '%WP39' and hb.workid not like '%WP69' " +
                        "and hb.workid not like '%WP70' and hb.workid not like '%WP22' and hb.workid not like '%WP23' " +
                        "and hb.workid not like '%WP24' and hb.workid not like '%WP25' and hb.workid not like '%WP26' " +
                        "and hb.workid not like '%WP27' and hb.workid not like '%WP28' and hb.workid not like '%WP29' " +
                        "and hb.workid not like '%WP29' and hb.workid not like '%WP18' and hb.workid not like '%WP19' " +
                        "and hb.workid not like '%WP20' and hb.workid not like '%WP21' and hb.workid not like '%WP21' " +
                        "and hb.workid not like '%WP73' and hb.workid not like '%WP74' and hb.workid not like '%WP81' " +
                        "and hb.workid not like '%WP11' and hb.workid not like '%WP12' and hb.workid not like '%WP10' " +
                        "and hb.workid not like '%WP14' and hb.workid not like '%WP15' and hb.workid not like '%WP16' " +
                        "and hb.workid not like '%WP17' and hb.workid not like '%WP84' and hb.workid not like '%WP3' " +
                        "and hb.workid not like '%WP4' and hb.workid not like '%WP6' and hb.workid not like '%WP7' " +
                        "and hb.workid not like '%WP8' and hb.workid not like '%WP2' and hb.workid not like '%WP82'" +
                        "and hb.workid not like '%WP20' and hb.workid not like '%WP21' and hb.workid not like '%WP30' " +
                        "and hb.workid not like '%WP31' and hb.workid not like '%WP75' "+
                        "and hb.workid not like '%WP88' and hb.b>'" +BEGIN_DATE+"' "+
                        "and hh.no='" +rstHouse.getString("no")+"' "+
                        "and hh.id is not null ) as a " +
                        "left join Shark..DGWordBook as sw on a.useType= sw.id "+
                        "order by a.no,a.botime");




                rstRecortRecord.last();
                int recordCount = rstRecortRecord.getRow();
                System.out.println(recordCount);
                if (recordCount > 0) {//有需要导入的业务
                    isFirst = true;
                    addStateisFirst = true;
                    String lastState=null;
                    rstRecortRecord.beforeFirst();

                    while (rstRecortRecord.next()) {
                        if (!REPEAT_NAMEID.contains(rstRecortRecord.getString("Nameid"))) {//重复的业务判断
                            REPEAT_NAMEID.add(rstRecortRecord.getString("Nameid"));
                            haveRepeat = false;

                        }else {
                            haveRepeat = true;
                        }

                            if (!haveRepeat){

                                if (!HOUSE_ERROR.contains(rstHouse.getString("no"))) {

                                    String[] temp;
                                    if (rstRecortRecord.getString("workid").contains("A")) {
                                        temp = rstRecortRecord.getString("workid").split("_");
                                    } else {
                                        temp = rstRecortRecord.getString("workid").split("#");
                                    }
                                    DEFINE_ID = temp[temp.length - 1];//业务标识WP40

                                    ResultSet resultSetbizNmae = statementShark.executeQuery("select memo from Shark..DGBiz where ID='" + rstRecortRecord.getString("BizID") + "'");
                                    resultSetbizNmae.next();
                                    if (resultSetbizNmae.getString("memo") != null) {
                                        DEFINE_NAME = resultSetbizNmae.getString("memo");
                                    } else {
                                        DEFINE_NAME = "未知";
                                    }
                                    selectbizid = null;
                                    if (SELECT_DEFINE_ID.contains(DEFINE_ID)) {//判断是否导入selectbiz
                                        if (rstRecortRecord.getString("SelectBiz") != null) {
                                            selectbizid = rstRecortRecord.getString("SelectBiz");
                                            System.out.println("SelectBiz--" + rstRecortRecord.getString("SelectBiz"));
                                            ResultSet rstRecortbiz = statementRecordch.executeQuery("select RecordBizNO from DGHouseRecord..Business where id='" + rstRecortRecord.getString("SelectBiz") + "'");
                                            if (rstRecortbiz.next() && !rstRecortbiz.getString("RecordBizNO").equals("")) {
                                                selectbizid = rstRecortbiz.getString("RecordBizNO");
                                            } else {
                                                selectbizid = null;
                                            }
                                        }
                                    }
                                    sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                                            " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE,NAMEID) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")), "0", "'BIZ_IMPORT'", Q.p(rstRecortRecord.getString("dbmemo"))
                                            , "'COMPLETE'", Q.p(DEFINE_NAME), Q.pm(DEFINE_ID), "0", Q.p(selectbizid), Q.p(rstRecortRecord.getTimestamp("BOTime"))
                                            , Q.p(rstRecortRecord.getTimestamp("BOTime")), "Null", Q.p(rstRecortRecord.getTimestamp("BOTime")), Q.p(rstRecortRecord.getTimestamp("BOTime")), "False", "'NORMAL_BIZ'",Q.p(rstRecortRecord.getString("Nameid"))) + ");");
                                    sqlWriter.newLine();

                                    if (selectbizid != null) {
                                        sqlWriter.write("UPDATE OWNER_BUSINESS SET STATUS='COMPLETE_CANCEL' WHERE ID='" + selectbizid + "';");
                                        sqlWriter.newLine();
                                    }


                                    //查找新库按房屋编号查取HouseRecord，取得startHouseid
                                    ResultSet resultSetOwnerRecord = statementOwnerRecord.executeQuery("SELECT * FROM HOUSE_RECORD WHERE HOUSE_CODE='" + rstRecortRecord.getString("no") + "'");

                                    if (resultSetOwnerRecord.next() && isFirst) {

                                        stratHouseId = resultSetOwnerRecord.getString("HOUSE");
                                        afterHouseId = rstRecortRecord.getString("RecordBizNO");
                                        isFirst = false;
                                        System.out.println("aaaa");
                                    } else {
                                        if (isFirst) {
                                            stratHouseId = rstRecortRecord.getString("RecordBizNO") + "-s";
                                            afterHouseId = rstRecortRecord.getString("RecordBizNO");
                                            isFirst = true;
                                            System.out.println("bbbb");
                                        } else {
                                            stratHouseId = afterHouseId;
                                            afterHouseId = rstRecortRecord.getString("RecordBizNO");
                                            isFirst = false;
                                            System.out.println("ccccc");
                                        }
                                    }
                                    System.out.println("stratHouseId-" + stratHouseId);
                                    System.out.println("afterHouseId-" + afterHouseId);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");


                                    if (isFirst) {
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
                                        sqlWriter.write("(" + Q.v(Q.p(stratHouseId), Q.pm(rstRecortRecord.getString("HouseOrder"))
                                                , Q.pm(rstRecortRecord.getString("UnitName")), Q.pm(rstRecortRecord.getString("InFloorName"))
                                                , Q.pm(rstRecortRecord.getBigDecimal("HouseArea")), Q.pm(rstRecortRecord.getBigDecimal("UseArea"))
                                                , Q.pm(rstRecortRecord.getBigDecimal("CommParam")), Q.pm(rstRecortRecord.getBigDecimal("ShineArea"))
                                                , Q.pm(rstRecortRecord.getBigDecimal("LoftArea")), Q.pm(rstRecortRecord.getBigDecimal("CommArea"))
                                                , Q.changeHouseType(rstRecortRecord.getInt("HouseType")),Q.changeDesignUseType(rstRecortRecord.getString("designUseType"))
                                                , rstRecortRecord.getString("Structure") != null ? Q.changeStructure(rstRecortRecord.getInt("Structure")) : "'未知'"
                                                , Q.pm(rstRecortRecord.getString("HouseStation")), Q.pm(rstRecortRecord.getString("mappingDate") != null ? (!rstRecortRecord.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortRecord.getString("mappingDate") : rstRecortRecord.getString("ChangeDate")) : rstRecortRecord.getString("ChangeDate"))
                                                , Q.pm(rstRecortRecord.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                                , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstHouse.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                                , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                                , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                                , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                                , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                                , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                                , Q.pm(rstHouse.getString("BuildName")), "Null", "Null", "Null", "Null", "Null",Q.changeUseType(rstRecortRecord.getString("useTypevalue")), "''" + ");"));
                                        sqlWriter.newLine();

                                        if (rstRecortRecord.isLast() == false) {//是否是最后一手

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

                                            sqlWriter.write("(" + Q.v(Q.p(afterHouseId), Q.pm(rstRecortRecord.getString("HouseOrder"))
                                                    , Q.pm(rstRecortRecord.getString("UnitName")), Q.pm(rstRecortRecord.getString("InFloorName"))
                                                    , Q.pm(rstRecortRecord.getBigDecimal("HouseArea")), Q.pm(rstRecortRecord.getBigDecimal("UseArea"))
                                                    , Q.pm(rstRecortRecord.getBigDecimal("CommParam")), Q.pm(rstRecortRecord.getBigDecimal("ShineArea"))
                                                    , Q.pm(rstRecortRecord.getBigDecimal("LoftArea")), Q.pm(rstRecortRecord.getBigDecimal("CommArea"))
                                                    , Q.changeHouseType(rstRecortRecord.getInt("HouseType")), Q.changeDesignUseType(rstRecortRecord.getString("designUseType"))
                                                    , rstRecortRecord.getString("Structure") != null ? Q.changeStructure(rstRecortRecord.getInt("Structure")) : "'未知'"
                                                    , Q.pm(rstRecortRecord.getString("HouseStation")), Q.pm(rstRecortRecord.getString("mappingDate") != null ? (!rstRecortRecord.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortRecord.getString("mappingDate") : rstRecortRecord.getString("ChangeDate")) : rstRecortRecord.getString("ChangeDate"))
                                                    , Q.pm(rstRecortRecord.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                                    , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstHouse.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                                    , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                                    , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                                    , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                                    , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                                    , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                                    , Q.pm(rstHouse.getString("BuildName")), "Null",rstRecortRecord.getString("PoolMemo")!=null?Q.changePoolMemo(rstRecortRecord.getInt("PoolMemo")):"Null", "Null", "Null", "Null", Q.changeUseType(rstRecortRecord.getString("useTypevalue")), "''" + ");"));
                                            sqlWriter.newLine();
                                        }else{//是最后一手
                                            System.out.println("单个最后"+rstRecortRecord.getString("RecordBizNO"));
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

                                            sqlWriter.write("(" + Q.v(Q.p(afterHouseId), Q.pm(rstHouse.getString("HouseOrder"))
                                                    , Q.pm(rstHouse.getString("UnitName")), Q.pm(rstHouse.getString("InFloorName"))
                                                    , Q.pm(rstHouse.getBigDecimal("HouseArea")), Q.pm(rstHouse.getBigDecimal("UseArea"))
                                                    , Q.pm(rstHouse.getBigDecimal("CommParam")), Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                                    , Q.pm(rstHouse.getBigDecimal("LoftArea")), Q.pm(rstHouse.getBigDecimal("CommArea"))
                                                    , Q.changeHouseType(rstHouse.getInt("HouseType")), Q.changeDesignUseType(rstHouse.getString("designUseType"))
                                                    , rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                                    , Q.pm(rstHouse.getString("HouseStation")), Q.pm(rstRecortRecord.getString("mappingDate") != null ? (!rstRecortRecord.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortRecord.getString("mappingDate") : rstRecortRecord.getString("ChangeDate")) : rstRecortRecord.getString("ChangeDate"))
                                                    , Q.pm(rstRecortRecord.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                                    , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstHouse.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                                    , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                                    , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                                    , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                                    , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                                    , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                                    , Q.pm(rstHouse.getString("BuildName")), "Null",rstHouse.getString("PoolMemo")!=null?Q.changePoolMemo(rstHouse.getInt("PoolMemo")):"Null", "Null", "Null", "Null", Q.changeUseType(rstHouse.getString("useTypevalue")), "''" + ");"));
                                            sqlWriter.newLine();

                                        }


                                    } else {
                                        if (rstRecortRecord.isLast() == false) {//是否是最后一手
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

                                            sqlWriter.write("(" + Q.v(Q.p(afterHouseId), Q.pm(rstRecortRecord.getString("HouseOrder"))
                                                    , Q.pm(rstRecortRecord.getString("UnitName")), Q.pm(rstRecortRecord.getString("InFloorName"))
                                                    , Q.pm(rstRecortRecord.getBigDecimal("HouseArea")), Q.pm(rstRecortRecord.getBigDecimal("UseArea"))
                                                    , Q.pm(rstRecortRecord.getBigDecimal("CommParam")), Q.pm(rstRecortRecord.getBigDecimal("ShineArea"))
                                                    , Q.pm(rstRecortRecord.getBigDecimal("LoftArea")), Q.pm(rstRecortRecord.getBigDecimal("CommArea"))
                                                    , Q.changeHouseType(rstRecortRecord.getInt("HouseType")), Q.changeDesignUseType(rstRecortRecord.getString("designUseType"))
                                                    , rstRecortRecord.getString("Structure") != null ? Q.changeStructure(rstRecortRecord.getInt("Structure")) : "'未知'"
                                                    , Q.pm(rstRecortRecord.getString("HouseStation")), Q.pm(rstRecortRecord.getString("mappingDate") != null ? (!rstRecortRecord.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortRecord.getString("mappingDate") : rstRecortRecord.getString("ChangeDate")) : rstRecortRecord.getString("ChangeDate"))
                                                    , Q.pm(rstRecortRecord.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                                    , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstHouse.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                                    , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                                    , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                                    , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                                    , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                                    , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                                    , Q.pm(rstHouse.getString("BuildName")), "Null",rstRecortRecord.getString("PoolMemo")!=null?Q.changePoolMemo(rstRecortRecord.getInt("PoolMemo")):"Null", "Null", "Null", "Null", Q.changeUseType(rstRecortRecord.getString("useTypevalue")), "''" + ");"));
                                            sqlWriter.newLine();
                                        }else{
                                            System.out.println("多笔业务最后---"+rstRecortRecord.getString("RecordBizNO"));
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

                                            sqlWriter.write("(" + Q.v(Q.p(afterHouseId), Q.pm(rstHouse.getString("HouseOrder"))
                                                    , Q.pm(rstHouse.getString("UnitName")), Q.pm(rstHouse.getString("InFloorName"))
                                                    , Q.pm(rstHouse.getBigDecimal("HouseArea")), Q.pm(rstHouse.getBigDecimal("UseArea"))
                                                    , Q.pm(rstHouse.getBigDecimal("CommParam")), Q.pm(rstHouse.getBigDecimal("ShineArea"))
                                                    , Q.pm(rstHouse.getBigDecimal("LoftArea")), Q.pm(rstHouse.getBigDecimal("CommArea"))
                                                    , Q.changeHouseType(rstHouse.getInt("HouseType")),Q.changeDesignUseType(rstHouse.getString("designUseType"))
                                                    , rstHouse.getString("Structure") != null ? Q.changeStructure(rstHouse.getInt("Structure")) : "'未知'"
                                                    , Q.pm(rstHouse.getString("HouseStation")), Q.pm(rstRecortRecord.getString("mappingDate") != null ? (!rstRecortRecord.getString("mappingDate").equals("1899-12-30 00:00:00.0") ? rstRecortRecord.getString("mappingDate") : rstRecortRecord.getString("ChangeDate")) : rstRecortRecord.getString("ChangeDate"))
                                                    , Q.pm(rstRecortRecord.getString("No")), "False", Q.pm("N" + rstHouse.getString("bno")), Q.pm(rstHouse.getString("MapNo")), Q.pm(rstHouse.getString("BlockNo"))
                                                    , Q.pm(rstHouse.getString("BuildNo")), Q.pm(rstHouse.getString("DoorNo")), rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1"
                                                    , rstHouse.getString("FloorCount") != null ? Q.pm(rstHouse.getString("FloorCount")) : "1", "0"
                                                    , Q.p(rstHouse.getString("BuildType")), Q.pm("N" + rstHouse.getString("hpno")), Q.pm(rstHouse.getString("hpnmae"))
                                                    , rstHouse.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(rstHouse.getTimestamp("FirmlyDate"))) : "NULL"
                                                    , Q.pm("N" + rstHouse.getString("pno")), Q.pm(rstHouse.getString("pname")), Q.pm("N" + rstHouse.getString("hsno"))
                                                    , Q.pm(rstHouse.getString("hsname")), Q.pm("N" + rstHouse.getString("hdno")), Q.pm(rstHouse.getString("hdname"))
                                                    , Q.pm(rstHouse.getString("BuildName")), "Null",rstHouse.getString("PoolMemo")!=null?Q.changePoolMemo(rstHouse.getInt("PoolMemo")):"Null", "Null", "Null", "Null", Q.changeUseType(rstHouse.getString("useTypevalue")), "''" + ");"));
                                            sqlWriter.newLine();

                                        }
                                    }
                                    //--- BUSINESS_HOUSE
                                    sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED,SEARCH_KEY,DISPLAY) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(rstRecortRecord.getString("NO"))
                                            , Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.p(stratHouseId), Q.p(afterHouseId), "True", "''", "''" + ");"));
                                    sqlWriter.newLine();

                                    // 房屋状态 ===ADD_HOUSE_STATUS 交易备案 初始登记


                                    if (DEAL_DEFINE_ID.contains(DEFINE_ID) || DEFINE_ID.contains("WP40")){
                                        sqlWriter.write("INSERT ADD_HOUSE_STATUS (ID, BUSINESS, STATUS, IS_REMOVE) VALUES  ");
                                        if(!DEFINE_ID.contains("WP40")){
                                            sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")), Q.p(rstRecortRecord.getString("RecordBizNO"))
                                                    , Q.p("OWNERED"), Q.p(false) + ");"));

                                        }else {
                                            sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")), Q.p(rstRecortRecord.getString("RecordBizNO"))
                                                    , Q.p("INIT_REG"), Q.p(false) + ");"));
                                            lastState = "INIT_REG";
                                        }

                                        sqlWriter.newLine();
                                    }

                                    if (DEAL_DEFINE_ID.contains(DEFINE_ID)){
                                        lastState="OWNERED";

                                    }
                                    if (lastState== null && DEFINE_ID.contains("WP40")){
                                        lastState = "INIT_REG";
                                    }

                                    // ===HOUSE_RECORD 房屋主状态表
                                    if (rstRecortRecord.isLast() == true) {//判断是不是最后一手
                                        System.out.println("lastState-" + lastState);
                                        if (lastState != null) {
                                            ResultSet resultSetLast = statementOwnerRecord.executeQuery("SELECT * FROM HOUSE_RECORD WHERE HOUSE_CODE='" + rstRecortRecord.getString("no") + "'");
                                            if (resultSetLast.next()) {//删除第一次导入的房屋状态
                                                if (lastState.equals("OWNERED")) {
                                                    sqlWriter.write("DELETE FROM HOUSE_RECORD WHERE HOUSE_CODE='" + rstRecortRecord.getString("no") + "';");
                                                    sqlWriter.newLine();
                                                    sqlWriter.write("INSERT HOUSE_RECORD (HOUSE_CODE, HOUSE, HOUSE_STATUS,DISPLAY,SEARCH_KEY) VALUES ");
                                                    sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("No")), Q.p(afterHouseId)
                                                            , Q.p(lastState), "''", "''" + ");"));
                                                    sqlWriter.newLine();
                                                }
                                            } else {
                                                sqlWriter.write("INSERT HOUSE_RECORD (HOUSE_CODE, HOUSE, HOUSE_STATUS,DISPLAY,SEARCH_KEY) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("No")), Q.p(afterHouseId)
                                                        , Q.p(lastState), "''", "''" + ");"));
                                                sqlWriter.newLine();

                                            }
                                            System.out.println("afterHouseId+lastState--" + afterHouseId + ":" + lastState);
                                        }

                                    }





                                    //产权人，共有人，预告人，初始登记人，
                                    boolean isFind = false;
                                    if (rstRecortRecord.getString("Nameid").contains("WP")){//判断是否老库读取
                                        //初始登记导入开发商 //导入产权证 初始登记人 判断nameid 带不带wp 不带
                                        if (DEFINE_ID.equals("WP40")){
                                            houseResultSet = statementHousech.executeQuery("select hc.* from DGHouseRecord..Business as hb left join" +
                                                    " DGHouseInfo..HouseCard as hc on hb.id=hc.bizid and hc.id is not null where hb.id='"+rstRecortRecord.getString("dbid")+"'");
                                            if (houseResultSet.next()){
                                                sqlWriter.write("INSERT MAKE_CARD (ID, NUMBER, TYPE, BUSINESS_ID, ENABLE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        Q.pm(houseResultSet.getString("no")),"'OWNER_RSHIP'",
                                                        Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        "True"+ ");"));
                                                sqlWriter.newLine();



                                            }else{
                                                sqlWriter.write("INSERT MAKE_CARD (ID, NUMBER, TYPE, BUSINESS_ID, ENABLE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        "''","'OWNER_RSHIP'",
                                                        Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        "True"+ ");"));
                                                sqlWriter.newLine();

                                            }
                                            //初始登记代理人
                                            String dlrid = SlectInfo.svs(statementRecordch, "sellers_agent", rstRecortRecord.getString("RecordBizNO"));
                                            String dlrbizid=null;
                                            if (dlrid!=null){
                                                ResultSet dlrResultSet = SlectInfo.bar(statementHousech2, dlrid);
                                                if (dlrResultSet!=null){

                                                    sqlWriter.write("INSERT PROXY_PERSON (ID, TYPE, NAME, ID_TYPE, ID_NO, PHONE) VALUE ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm("ENTRUSTED")
                                                            , Q.pm(dlrResultSet.getString("Name")), Q.pCardType(dlrResultSet.getInt("IDType"))
                                                            , Q.pm(dlrResultSet.getString("IDNO")), Q.pm(dlrResultSet.getString("Phone")) + ");"));
                                                    sqlWriter.newLine();
                                                    dlrbizid = rstRecortRecord.getString("RecordBizNO");
                                                }
                                            }
                                            //将开发商添加城产权人
                                            sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO," +
                                                    " PHONE," +
                                                    " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(rstHouse.getString("pname"))
                                                    , Q.pm("COMPANY_CODE"),Q.pm("未知") , Q.pm("未知")
                                                    , Q.p("INIT"), "'1'"
                                                    , Q.p(rstRecortRecord.getString("RecordBizNO")),"false",Q.p(dlrbizid) + ");"));
                                            sqlWriter.newLine();

                                            //修改afterhouse产权人
                                            sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                            sqlWriter.newLine();

                                            //房屋与产权人关联
                                            sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                    Q.pm(rstRecortRecord.getString("RecordBizNO"))+");"));
                                            sqlWriter.newLine();
                                        }else if (DEFINE_ID.equals("WP1") || DEFINE_ID.equals("WP5")){//预告人
                                            String ygrID = null;
                                            if (rstRecortRecord.getString("Mainowner")!=null && !rstRecortRecord.getString("Mainowner").equals("")){
                                                ygrID = rstRecortRecord.getString("Mainowner");
                                            }else{
                                                ygrID = SlectInfo.svs(statementRecordch, "dept_people", rstRecortRecord.getString("RecordBizNO"));
                                            }

                                            if (ygrID!=null){
                                                ResultSet ygrResultSet = SlectInfo.bar(statementHousech2,ygrID);
                                                if (ygrResultSet!=null){
                                                    sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                            " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(ygrResultSet.getString("Name"))
                                                            , Q.pCardType(ygrResultSet.getInt("IDType")),Q.pm(ygrResultSet.getString("IDNO")),Q.pm(ygrResultSet.getString("Phone"))
                                                            , Q.p(ygrResultSet.getString("Address"))
                                                            , Q.p("PREPARE"), "'1'"
                                                            , "Null","false","NULL" + ");"));
                                                    sqlWriter.newLine();
                                                    isFind = true;
                                                }
                                            }
                                            if (!isFind){//什么都没有查询到，添一个空记录
                                                sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE" +
                                                        " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),"'未知'"
                                                        , "'OTHER'","'未知'","'未知'"
                                                        , Q.p("PREPARE"), "'1'"
                                                        , "Null","false","NULL" + ");"));
                                                sqlWriter.newLine();


                                            }


                                            sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                            sqlWriter.newLine();
                                            //房屋与产权人关联
                                            sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                    Q.pm(rstRecortRecord.getString("RecordBizNO"))+");"));
                                            sqlWriter.newLine();
                                            // 共有权人
                                            String skgyqr = null;
                                            skgyqr = SlectInfo.svs(statementRecordch, "owners_no", rstRecortRecord.getString("RecordBizNO"));
                                            if (skgyqr!=null){
                                                int j=0;
                                                for (String str : skgyqr.split(";")) {
                                                    ResultSet gyrResultSet = SlectInfo.bar(statementHousech2,str.split(",")[0]);
                                                    if (gyrResultSet!=null){
                                                        sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, " +
                                                                "PHONE,  ADDRESS, TYPE, PRI," +
                                                                " CARD, OLD, PROXY_PERSON) VALUE ");
                                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                                Q.pm(gyrResultSet.getString("Name")), Q.pCardType(gyrResultSet.getInt("IDType")),
                                                                Q.pm(gyrResultSet.getString("IDNO")),
                                                                str.split(",")[3] != null ? (!str.split(",")[3].equals("0") ? str.split(",")[3] : "NULL") : "Null",
                                                                Q.p(str.split(",")[2]),
                                                                Q.pm(gyrResultSet.getString("phone")), Q.pm(gyrResultSet.getString("Address")),
                                                                Q.pm("OWNER"),Q.p(String.valueOf(j+2)),"Null","false","Null" + ");"));
                                                        sqlWriter.newLine();
                                                        sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                                        sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                                Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                        sqlWriter.newLine();
                                                        j++;
                                                    }
                                                }
                                            }

                                        }else{
                                            if (rstRecortRecord.isLast() == false) {
                                                if (rstRecortRecord.getString("mainowner")!=null &&
                                                        !rstRecortRecord.getString("mainowner").equals("")){
                                                    ResultSet cqrResult = SlectInfo.bar(statementHousech2,rstRecortRecord.getString("mainowner"));
                                                    if (cqrResult!=null){
                                                        sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                                " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(cqrResult.getString("Name"))
                                                                , Q.pCardType(cqrResult.getInt("IDType")),Q.pm(cqrResult.getString("IDNO")),Q.pm(cqrResult.getString("Phone"))
                                                                , Q.p(cqrResult.getString("Address"))
                                                                , Q.p("OWNER"), "'1'"
                                                                , "Null","false","NULL" + ");"));
                                                        sqlWriter.newLine();
                                                        isFind = true;
                                                    }
                                                }
                                                if (!isFind){//查找没有的，添加一个空的
                                                    sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE" +
                                                            " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),"'未知'"
                                                            , "'OTHER'","'未知'","'未知'"
                                                            , Q.p("OWNER"), "'1'"
                                                            , "Null","false","NULL" + ");"));
                                                    sqlWriter.newLine();

                                                }

                                                sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                                sqlWriter.newLine();

                                                //房屋与产权人关联
                                                sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                        Q.pm(rstRecortRecord.getString("RecordBizNO"))+");"));
                                                sqlWriter.newLine();

                                                //共有权人
                                                ResultSet gyqrResultSet=statementHousech2.executeQuery("select ho.*,hc.Relation,hc.PoolArea from DGHouseInfo..housecard as hc left join DGHouseInfo..ownerinfo as ho " +
                                                        "on hc.ownerid=ho.id where hc.type=77 and ho.id is not null and hc.bizid='"+rstRecortRecord.getString("dbid")+"'");
                                                gyqrResultSet.last();
                                                if (gyqrResultSet.getRow()>0){
                                                    gyqrResultSet.beforeFirst();
                                                    int j=1;
                                                    while (gyqrResultSet.next()){
                                                        if (!POOL_OWNER_ID.contains(gyqrResultSet.getString("id"))) {
                                                            POOL_OWNER_ID.add(gyqrResultSet.getString("id"));


                                                            sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, " +
                                                                    "PHONE,  ADDRESS, TYPE, PRI," +
                                                                    " CARD, OLD, PROXY_PERSON) VALUE ");
                                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                                    Q.pm(gyqrResultSet.getString("Name")), Q.pCardType(gyqrResultSet.getInt("IDType")),
                                                                    Q.pm(gyqrResultSet.getString("IDNO")),
                                                                    gyqrResultSet.getString("Relation") != null ? (!gyqrResultSet.getString("Relation").equals("0") ? gyqrResultSet.getString("Relation") : "NULL") : "Null",
                                                                    Q.p(gyqrResultSet.getBigDecimal("PoolArea")),
                                                                    Q.pm(gyqrResultSet.getString("phone")), Q.pm(gyqrResultSet.getString("Address")),
                                                                    Q.pm("OWNER"), Q.p(String.valueOf(j + 1)), "Null", "false", "Null" + ");"));
                                                            sqlWriter.newLine();
                                                            sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                                            sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                                    Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                            sqlWriter.newLine();
                                                            j++;
                                                        }
                                                    }
                                                }

                                            } else {//取空间库的产权人
                                                if (rstHouse.getString("mainowner")!=null &&
                                                        !rstHouse.getString("mainowner").equals("")){
                                                    ResultSet cqrResult = SlectInfo.bar(statementHousech2,rstHouse.getString("mainowner"));
                                                    if (cqrResult!=null){
                                                        sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                                " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(cqrResult.getString("Name"))
                                                                , Q.pCardType(cqrResult.getInt("IDType")),Q.pm(cqrResult.getString("IDNO")),Q.pm(cqrResult.getString("Phone"))
                                                                , Q.p(cqrResult.getString("Address"))
                                                                , Q.p("OWNER"), "'1'"
                                                                , "Null","false","NULL" + ");"));
                                                        sqlWriter.newLine();
                                                        isFind = true;
                                                    }
                                                }
                                                if (!isFind){//查找没有的，添加一个空的
                                                    sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE" +
                                                            " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),"'未知'"
                                                            , "'OTHER'","'未知'","'未知'"
                                                            , Q.p("OWNER"), "'1'"
                                                            , "Null","false","NULL" + ");"));
                                                    sqlWriter.newLine();

                                                }

                                                sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                                sqlWriter.newLine();

                                                //房屋与产权人关联
                                                sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                        Q.pm(rstRecortRecord.getString("RecordBizNO"))+");"));
                                                sqlWriter.newLine();

                                                //共有权人
                                                ResultSet gyqrResultSet=statementHousech2.executeQuery("select ho.*,hc.Relation,hc.PoolArea from DGHouseInfo..housecard as hc left join DGHouseInfo..ownerinfo as ho " +
                                                        "on hc.ownerid=ho.id where hc.type=77 and ho.id is not null and hc.bizid='"+rstRecortRecord.getString("dbid")+"'");
                                                gyqrResultSet.last();
                                                if (gyqrResultSet.getRow()>0){
                                                    gyqrResultSet.beforeFirst();
                                                    int j=1;
                                                    while (gyqrResultSet.next()){
                                                        if (!POOL_OWNER_ID.contains(gyqrResultSet.getString("id"))) {
                                                            POOL_OWNER_ID.add(gyqrResultSet.getString("id"));


                                                            sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, " +
                                                                    "PHONE,  ADDRESS, TYPE, PRI," +
                                                                    " CARD, OLD, PROXY_PERSON) VALUE ");
                                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                                    Q.pm(gyqrResultSet.getString("Name")), Q.pCardType(gyqrResultSet.getInt("IDType")),
                                                                    Q.pm(gyqrResultSet.getString("IDNO")),
                                                                    gyqrResultSet.getString("Relation") != null ? (!gyqrResultSet.getString("Relation").equals("0") ? gyqrResultSet.getString("Relation") : "NULL") : "Null",
                                                                    Q.p(gyqrResultSet.getBigDecimal("PoolArea")),
                                                                    Q.pm(gyqrResultSet.getString("phone")), Q.pm(gyqrResultSet.getString("Address")),
                                                                    Q.pm("OWNER"), Q.p(String.valueOf(j + 1)), "Null", "false", "Null" + ");"));
                                                            sqlWriter.newLine();
                                                            sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                                            sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                                    Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                            sqlWriter.newLine();
                                                            j++;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }else {//老库导入的数据
                                        String personType=null;
                                        if (DEFINE_ID.equals("WP40")){
                                            personType = "INIT";
                                            ResultSet fcResultSet=statementFangchan.executeQuery("select y.yw_cqr,sz.sz_zhenghao from fang_chan_dg..c_yewu as y,fang_chan_dg..c_shanzheng as sz " +
                                                    "where y.keycode=sz.keycode and y.yw_mc_biaoshi ='16' and sz_zhenghao is not null and sz_zhenghao <>'' and y.keycode='"+rstRecortRecord.getString("Nameid")+"'");

                                            if (fcResultSet.next()) {
                                                sqlWriter.write("INSERT MAKE_CARD (ID, NUMBER, TYPE, BUSINESS_ID, ENABLE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        Q.pm(fcResultSet.getString("sz_zhenghao")), "'OWNER_RSHIP'",
                                                        Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        "True" + ");"));
                                                sqlWriter.newLine();
                                            }else {
                                                sqlWriter.write("INSERT MAKE_CARD (ID, NUMBER, TYPE, BUSINESS_ID, ENABLE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        "''", "'OWNER_RSHIP'",
                                                        Q.p(rstRecortRecord.getString("RecordBizNO")),
                                                        "True" + ");"));
                                                sqlWriter.newLine();
                                            }
                                            //将开发商添加城产权人
                                            sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO," +
                                                    " PHONE," +
                                                    " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(rstHouse.getString("pname"))
                                                    , Q.pm("COMPANY_CODE"),Q.pm("未知") , Q.pm("未知")
                                                    , Q.p("INIT"), "'1'"
                                                    , Q.p(rstRecortRecord.getString("RecordBizNO")),"false","Null" + ");"));
                                            sqlWriter.newLine();

                                            //修改afterhouse产权人
                                            sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                            sqlWriter.newLine();

                                            //房屋与产权人关联
                                            sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                    Q.pm(rstRecortRecord.getString("RecordBizNO"))+");"));
                                            sqlWriter.newLine();



                                        }else if(DEFINE_ID.equals("WP1") || DEFINE_ID.equals("WP5") ){
                                            personType = "PREPARE";
                                            ResultSet fcResultSet=statementFangchan.executeQuery("select y.yw_cqr from fang_chan_dg..c_yewu as y " +
                                                    "where y.yw_cqr is not null and y.yw_cqr<>'' and (y.yw_mc_biaoshi ='336' or y.yw_mc_biaoshi ='340') and y.keycode='"+rstRecortRecord.getString("Nameid")+"'");

                                            if (fcResultSet.next()){
                                                sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                        " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(fcResultSet.getString("yw_cqr"))
                                                        , "'OTHER'","'未知'","'未知'"
                                                        ,"'未知'"
                                                        , Q.p("PREPARE"), "'1'"
                                                        , "Null","false","NULL" + ");"));
                                                sqlWriter.newLine();

                                            }else{
                                                sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                        " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),"'未知'"
                                                        , "'OTHER'","'未知'","'未知'"
                                                        ,"'未知'"
                                                        , Q.p("PREPARE"), "'1'"
                                                        , "Null","false","NULL" + ");"));
                                                sqlWriter.newLine();

                                            }

                                            sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                            sqlWriter.newLine();
                                            //房屋与产权人关联
                                            sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                    Q.pm(rstRecortRecord.getString("RecordBizNO"))+");"));
                                            sqlWriter.newLine();
                                            // 共有权人
                                            ResultSet fcgyResultSet=statementFangchan.executeQuery("select * from fang_chan_dg..c_gongyou where gy_ren is not null and gy_ren<>'' " +
                                                    "and keycode='"+rstRecortRecord.getString("Nameid")+"'");
                                            int j=0;
                                            if (fcgyResultSet.next()){
                                                sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, " +
                                                        "PHONE,  ADDRESS, TYPE, PRI," +
                                                        " CARD, OLD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                        Q.pm(fcgyResultSet.getString("gy_ren")), "'MASTER_ID'",
                                                        Q.pm(fcgyResultSet.getString("gy_card")),
                                                        "Null","Null","'未知'","Null",
                                                        Q.pm("OWNER"),Q.p(String.valueOf(j+1)),"Null","false","Null" + ");"));
                                                sqlWriter.newLine();
                                                sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                        Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                sqlWriter.newLine();
                                                j++;

                                            }
                                        }else{
                                            personType="OWNER";
                                            ResultSet fcResultSet=statementFangchan.executeQuery("select y.yw_cqr,y.yw_cqr_card,y.yw_cqr_dianhua from fang_chan_dg..c_yewu as y " +
                                                    "where y.yw_cqr is not null and y.yw_cqr<>'' and y.keycode='"+rstRecortRecord.getString("Nameid")+"'");
                                            if(fcResultSet.next()){
                                                sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                        " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(fcResultSet.getString("yw_cqr"))
                                                        , "'OTHER'",Q.pm(fcResultSet.getString("yw_cqr_card")),Q.pm(fcResultSet.getString("yw_cqr_dianhua"))
                                                        ,"'未知'"
                                                        , Q.p("PREPARE"), "'1'"
                                                        , "Null","false","NULL" + ");"));
                                                sqlWriter.newLine();
                                            }else {
                                                sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                        " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),"'未知'"
                                                        , "'OTHER'","'未知'","'未知'"
                                                        ,"'未知'"
                                                        , Q.p("PREPARE"), "'1'"
                                                        , "Null","false","NULL" + ");"));
                                                sqlWriter.newLine();
                                            }

                                            sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                            sqlWriter.newLine();
                                            //房屋与产权人关联
                                            sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                    Q.pm(rstRecortRecord.getString("RecordBizNO"))+");"));
                                            sqlWriter.newLine();

                                            // 共有权人
                                            ResultSet fcgyResultSet=statementFangchan.executeQuery("select * from fang_chan_dg..c_gongyou where gy_ren is not null and gy_ren<>'' " +
                                                    "and keycode='"+rstRecortRecord.getString("Nameid")+"'");
                                            int j=0;
                                            if (fcgyResultSet.next()){
                                                sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, " +
                                                        "PHONE,  ADDRESS, TYPE, PRI," +
                                                        " CARD, OLD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)),
                                                        Q.pm(fcgyResultSet.getString("gy_ren")), "'MASTER_ID'",
                                                        Q.pm(fcgyResultSet.getString("gy_card")),
                                                        "Null","Null","'未知'","Null",
                                                        Q.pm("OWNER"),Q.p(String.valueOf(j+1)),"Null","false","Null" + ");"));
                                                sqlWriter.newLine();
                                                sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.pm(afterHouseId),
                                                        Q.pm(rstRecortRecord.getString("RecordBizNO") + "-" + String.valueOf(j)) + ");"));
                                                sqlWriter.newLine();
                                                j++;

                                            }
                                        }

                                    }

                                    //抵押登记 抵押信息，金融机构  在建工程抵押PROJECT_MORTGAGE
                                    String financialNo=null;
                                    boolean finisFind=false;
                                    if (MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)){
                                        if (rstRecortRecord.getString("Nameid").contains("WP")) {

                                            financialNo = SlectInfo.svs(statementRecordch, "mortgage_obligee", rstRecortRecord.getString("RecordBizNO"));
                                            if (financialNo != null) {
                                                ResultSet fResultSet = SlectInfo.Financia(statementHousech2, financialNo);
                                                if (fResultSet != null) {
                                                    sqlWriter.write("INSERT FINANCIAL (ID, NAME, CODE, PHONE, FINANCIAL_TYPE, ID_TYPE, " +
                                                            "BANK, CREATE_TIME, CARD, PROXY_PERSON) VALUE ");
                                                    sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(fResultSet.getString("Name")),
                                                            Q.p(fResultSet.getString("No")), Q.p(fResultSet.getString("Phone")), "'FINANCE_CORP'", "Null", "Null",
                                                            Q.p(rstRecortRecord.getTimestamp("BOTime")), "Null", "Null"
                                                                    + ");"));
                                                    sqlWriter.newLine();
                                                    finisFind = true;
                                                }
                                            }
                                            if (!finisFind) {
                                                sqlWriter.write("INSERT FINANCIAL (ID, NAME, CODE, PHONE, FINANCIAL_TYPE, ID_TYPE, " +
                                                        "BANK, CREATE_TIME, CARD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), "'未知'",
                                                        "Null", "Null", "'FINANCE_CORP'", "Null", "Null",
                                                        Q.p(rstRecortRecord.getTimestamp("BOTime")), "Null", "Null"
                                                                + ");"));
                                                sqlWriter.newLine();
                                            }

                                            if (DEFINE_ID.equals("WP83")) {
                                                sqlWriter.write("INSERT PROJECT_MORTGAGE (ID,DEVELOPER_NAME,DEVELOPER_CODE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),
                                                        Q.pm(rstHouse.getString("pname")), Q.pm(rstHouse.getString("pno")) + ");"));
                                                sqlWriter.newLine();
                                            }

                                            //债权数额
                                            Double zqse = SlectInfo.svd(statementShark, "highest_mount_money", rstRecortRecord.getString("RecordBizNO"));
                                            if (zqse == null) {
                                                zqse = 0.0;
                                            }
                                            //担保范围
                                            String dbfw = SlectInfo.svs(statementShark, "warrant_scope", rstRecortRecord.getString("RecordBizNO"));
                                            //权利种类
                                            String qlzl = String.valueOf(SlectInfo.svl(statementShark, "interest_type", rstRecortRecord.getString("RecordBizNO")));

                                            if (!qlzl.equals("121") && !qlzl.equals("122")) {
                                                qlzl = "power.type.other";
                                            }
                                            //抵押时间始
                                            Timestamp dysjs = SlectInfo.svt(statementShark, "mortgage_due_time_s", rstRecortRecord.getString("RecordBizNO"));
                                            //抵押时间止
                                            Timestamp dysjz = SlectInfo.svt(statementShark, "mortgage_due_time_e", rstRecortRecord.getString("RecordBizNO"));
                                            //抵押面积 mortgage_area
                                            Double dymj = SlectInfo.svd(statementShark, "mortgage_area", rstRecortRecord.getString("RecordBizNO"));
                                            if (dymj == null) {
                                                dymj = 0.0;
                                            }
                                            sqlWriter.write("INSERT MORTGAEGE_REGISTE (HIGHEST_MOUNT_MONEY, WARRANT_SCOPE, INTEREST_TYPE, " +
                                                    "MORTGAGE_DUE_TIME_S, MORTGAGE_TIME, MORTGAGE_AREA, " +
                                                    "TIME_AREA_TYPE, ID, BUSINESS_ID, OLD_FIN, FIN, ORG_NAME) VALUE ");
                                            sqlWriter.write("(" + Q.v(Q.pm(new BigDecimal(zqse)), Q.pm(dbfw), Q.pm(qlzl),
                                                    Q.pm(dysjs), Q.pm(dysjz), Q.pm(new BigDecimal(dymj)),
                                                    "'DATE_TIME'", rstRecortRecord.getString("RecordBizNO"), rstRecortRecord.getString("RecordBizNO"),
                                                    "Null", rstRecortRecord.getString("RecordBizNO"), "'东港市房地产管理处'"
                                                            + ");"));
                                            sqlWriter.newLine();
                                            //债务人
                                            if (DEFINE_ID.contains("WP13")||DEFINE_ID.contains("WP9")) {
                                                String zwrId = SlectInfo.svs(statementShark, "mortgage_work", rstRecortRecord.getString("RecordBizNO"));
                                                if (zwrId != null) {
                                                    ResultSet zwrResltSet = SlectInfo.bar(statementHousech2, zwrId);
                                                    if (zwrResltSet != null) {
                                                            sqlWriter.write("INSERT BUSINESS_PERSION (ID, ID_NO, ID_TYPE, NAME, TYPE, BUSINESS_ID, PHONE) VALUE ");
                                                            sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(zwrResltSet.getString("IDNO"))
                                                                    , Q.pCardType(zwrResltSet.getInt("IDType")), Q.pm(zwrResltSet.getString("Name")), "'MORTGAGE_OBLIGOR'"
                                                                    , Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(zwrResltSet.getString("Phone"))
                                                                    + ");"));
                                                            sqlWriter.newLine();

                                                    }
                                                }
                                            }
                                            //评估机构、评估价格
                                            if (DEFINE_ID.contains("WP13")||DEFINE_ID.contains("WP9")||DEFINE_ID.contains("WP83")) {
                                                String pgjgNo = SlectInfo.svs(statementRecordch, "assessment_agencies", rstRecortRecord.getString("RecordBizNO"));
                                                Double pgjg = SlectInfo.svd(statementShark, "assessment_price", rstRecortRecord.getString("RecordBizNO"));
                                                boolean pgjgisFind = false;
                                                if (pgjg == null) {
                                                    pgjg = 0.0;
                                                }
                                                if (pgjgNo != null) {
                                                    ResultSet pgResultSet = SlectInfo.evaluateCorporation(statementHousech2, pgjgNo);
                                                    if (pgResultSet != null) {
                                                        sqlWriter.write("INSERT EVALUATE (EVALUATE_CORP_NAME, EVALUATE_CORP_N0, ASSESSMENT_PRICE, ID, BUSINESS_ID) VALUE ");
                                                        sqlWriter.write("(" + Q.v(Q.pm(pgResultSet.getString("Name")), Q.p(pgResultSet.getString("No"))
                                                                , Q.pm(new BigDecimal(pgjg)),Q.pm(rstRecortRecord.getString("RecordBizNO")),
                                                                Q.pm(rstRecortRecord.getString("RecordBizNO"))
                                                                + ");"));
                                                        sqlWriter.newLine();
                                                        pgjgisFind = true;
                                                    }
                                                }
                                                if (!pgjgisFind) {
                                                    sqlWriter.write("INSERT EVALUATE (EVALUATE_CORP_NAME, EVALUATE_CORP_N0, ASSESSMENT_PRICE, ID, BUSINESS_ID) VALUE ");
                                                    sqlWriter.write("(" + Q.v(Q.pm("未知"), Q.pm("未知"),
                                                            Q.pm(new BigDecimal(pgjg)),Q.pm(rstRecortRecord.getString("RecordBizNO")),
                                                            Q.pm(rstRecortRecord.getString("RecordBizNO"))
                                                                    + ");"));
                                                    sqlWriter.newLine();
                                                }
                                            }


                                        }else {//从老库读取
                                            ResultSet fcdyResultSet=statementFangchan.executeQuery("select sl_taxiangquanren,sl_diya_dianhua,sl_date,sl_quanlizhonglei,sl_diyaqianxian1, " +
                                                    "sl_diyaqianxian2,sl_danbaofanwei,sf_jiekuan,sl_jiekuanren,sl_dlr_dianhua,ch_dymj from " +
                                                    "c_yewu as y,c_shouli as s,c_shoufei as sf,c_cehui as c where y.keycode=s.keycode and y.keycode=sf.keycode and y.keycode=c.keycode " +
                                                    "and sl_taxiangquanren is not null and sl_taxiangquanren<>'' and y.yw_mc_biaoshi in ('336','340','32','323','328') " +
                                                    "and y.keycode='"+rstRecortRecord.getString("Nameid")+"'");
                                            if(fcdyResultSet.next()){
                                                sqlWriter.write("INSERT FINANCIAL (ID, NAME, CODE, PHONE, FINANCIAL_TYPE, ID_TYPE, " +
                                                        "BANK, CREATE_TIME, CARD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(fcdyResultSet.getString("sl_taxiangquanren")),
                                                        "Null", Q.p(fcdyResultSet.getString("sl_diya_dianhua")), "'FINANCE_CORP'", "Null", "Null",
                                                        Q.p(fcdyResultSet.getTimestamp("sl_date")), "Null", "Null"
                                                                + ");"));
                                                sqlWriter.newLine();


                                                sqlWriter.write("INSERT MORTGAEGE_REGISTE (HIGHEST_MOUNT_MONEY, WARRANT_SCOPE, INTEREST_TYPE, " +
                                                        "MORTGAGE_DUE_TIME_S, MORTGAGE_TIME, MORTGAGE_AREA, " +
                                                        "TIME_AREA_TYPE, ID, BUSINESS_ID, OLD_FIN, FIN, ORG_NAME) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(fcdyResultSet.getBigDecimal("sf_jiekuan")), Q.pm(fcdyResultSet.getString("sl_danbaofanwei")), Q.interest_type(fcdyResultSet.getString("sl_quanlizhonglei")),
                                                        Q.pm(fcdyResultSet.getTimestamp("sl_diyaqianxian1")), Q.pm(fcdyResultSet.getTimestamp("sl_diyaqianxian2")), Q.pm(fcdyResultSet.getBigDecimal("ch_dymj")),
                                                        "'DATE_TIME'", rstRecortRecord.getString("RecordBizNO"), rstRecortRecord.getString("RecordBizNO"),
                                                        "Null", rstRecortRecord.getString("RecordBizNO"), "'东港市房地产管理处'"
                                                                + ");"));
                                                sqlWriter.newLine();



                                            }else{

                                                sqlWriter.write("INSERT FINANCIAL (ID, NAME, CODE, PHONE, FINANCIAL_TYPE, ID_TYPE, " +
                                                        "BANK, CREATE_TIME, CARD, PROXY_PERSON) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),"'未知'",
                                                        "Null","Null", "'FINANCE_CORP'", "Null", "Null",
                                                        Q.p(rstRecortRecord.getTimestamp("BOTime")), "Null", "Null"
                                                                + ");"));
                                                sqlWriter.newLine();


                                                sqlWriter.write("INSERT MORTGAEGE_REGISTE (HIGHEST_MOUNT_MONEY, WARRANT_SCOPE, INTEREST_TYPE, " +
                                                        "MORTGAGE_DUE_TIME_S, MORTGAGE_TIME, MORTGAGE_AREA, " +
                                                        "TIME_AREA_TYPE, ID, BUSINESS_ID, OLD_FIN, FIN, ORG_NAME) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(new BigDecimal(0)), "'未知'", "'未知'",
                                                                "'2000-1-1'","'2000-1-1'","0",
                                                        "'DATE_TIME'", rstRecortRecord.getString("RecordBizNO"), rstRecortRecord.getString("RecordBizNO"),
                                                        "Null", rstRecortRecord.getString("RecordBizNO"), "'东港市房地产管理处'"
                                                                + ");"));
                                                sqlWriter.newLine();

                                            }
                                            if (DEFINE_ID.equals("WP83")) {
                                                sqlWriter.write("INSERT PROJECT_MORTGAGE (ID,DEVELOPER_NAME,DEVELOPER_CODE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),
                                                        Q.pm(rstHouse.getString("pname")), Q.pm(rstHouse.getString("pno")) + ");"));
                                                sqlWriter.newLine();
                                            }
                                            //债务人
                                            if (fcdyResultSet.getString("sl_jiekuanren")!=null){
                                                sqlWriter.write("INSERT BUSINESS_PERSION (ID, ID_NO, ID_TYPE, NAME, TYPE, BUSINESS_ID, PHONE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm("未知")
                                                        , "'OTHER'", Q.pm(fcdyResultSet.getString("sl_jiekuanren")), "'MORTGAGE_OBLIGOR'"
                                                        , Q.pm(rstRecortRecord.getString("RecordBizNO")), Q.pm(fcdyResultSet.getString("sl_dlr_dianhua"))
                                                        + ");"));
                                                sqlWriter.newLine();
                                            }

                                        }
                                    }
                                    //相关业务人
                                    if (rstRecortRecord.getString("Nameid").contains("WP")){
                                        ResultSet empResultSet = statementShark.executeQuery("SELECT a.resourceid,a.LastStateTime,a.name as jdname,de.name as dename" +
                                                " FROM shark..SHKActivities as a left join shark..DGEmployee as de " +
                                                "on a.resourceid =de.no where ProcessId ='"+rstRecortRecord.getString("Nameid") +"' and (a.name='受理' or a.name='复审' or a.name='审批' or a.name='归档')");
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
                                                sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                                        Q.changeBusinessEmpType(empResultSet.getString("jdname")),
                                                        Q.pm(empResultSet.getString("resourceid")),
                                                        Q.pm(empResultSet.getString("dename")),Q.pm(rstRecortRecord.getString("RecordBizNO")),Q.pm(res)
                                                                + ");"));

                                                sqlWriter.newLine();
                                                //===TASK_OPER
                                                sqlWriter.write("INSERT TASK_OPER (ID, BUSINESS, OPER_TIME, EMP_CODE, EMP_NAME, TASK_NAME, OPER_TYPE) VALUE ");
                                                sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")+"-"+empResultSet.getRow()),
                                                        Q.pm(rstRecortRecord.getString("RecordBizNO")),Q.pm(res),Q.pm(empResultSet.getString("resourceid")),
                                                        Q.pm(empResultSet.getString("dename")),Q.pm(empResultSet.getString("jdname")),"'NEXT'"
                                                                + ");"));
                                                sqlWriter.newLine();


                                                if (empResultSet.getString("jdname").equals("受理")){
                                                    sqlWriter.write("UPDATE OWNER_BUSINESS SET CREATE_TIME='"+res+"',APPLY_TIME='"+res+"' WHERE ID='"+rstRecortRecord.getString("RecordBizNO")+"';");
                                                    sqlWriter.newLine();
                                                }

                                                if (empResultSet.getString("jdname").equals("复审")){
                                                    sqlWriter.write("UPDATE OWNER_BUSINESS SET CHECK_TIME='"+res+"' WHERE ID='"+rstRecortRecord.getString("RecordBizNO")+"';");
                                                    sqlWriter.newLine();
                                                }
                                                if (empResultSet.getString("jdname").equals("审批")){
                                                    sqlWriter.write("UPDATE OWNER_BUSINESS SET REG_TIME='"+res+"' WHERE ID='"+rstRecortRecord.getString("RecordBizNO")+"';");
                                                    sqlWriter.newLine();
                                                }
                                            }
                                        }
                                    }

                                    //HOUSE_REG_INFO 产别 产权来源
                                    if (rstRecortRecord.isLast() == false){
                                        sqlWriter.write("INSERT HOUSE_REG_INFO (ID, HOUSE_PORPERTY, HOUSE_FROM) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),Q.pm(rstRecortRecord.getString("HousePorperty"))
                                                ,Q.pm(rstRecortRecord.getString("HouseFrom"))+ ");"));
                                        sqlWriter.newLine();
                                        sqlWriter.write("UPDATE HOUSE SET REG_INFO = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                        sqlWriter.newLine();
                                    }else {
                                        sqlWriter.write("INSERT HOUSE_REG_INFO (ID, HOUSE_PORPERTY, HOUSE_FROM) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),Q.pm(rstHouse.getString("HousePorperty"))
                                                ,Q.pm(rstHouse.getString("HouseFrom"))+ ");"));
                                        sqlWriter.newLine();
                                        sqlWriter.write("UPDATE HOUSE SET REG_INFO = '"+rstRecortRecord.getString("RecordBizNO")+"' WHERE ID='"+afterHouseId+"';");
                                        sqlWriter.newLine();

                                    }
                                    //SALE_INFO 购房款
                                    if(rstRecortRecord.isLast() == false){
                                        sqlWriter.write("INSERT SALE_INFO (ID, PAY_TYPE, SUM_PRICE, HOUSEID) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),rstRecortRecord.getString("PayType")!=null?Q.changePayType(rstRecortRecord.getInt("PayType")):"NULL"
                                                ,Q.pm(rstRecortRecord.getBigDecimal("SumPrice")),Q.pm(afterHouseId)+ ");"));
                                        sqlWriter.newLine();

                                    }else {
                                        sqlWriter.write("INSERT SALE_INFO (ID, PAY_TYPE, SUM_PRICE, HOUSEID) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(rstRecortRecord.getString("RecordBizNO")),rstHouse.getString("PayType")!=null?Q.changePayType(rstHouse.getInt("PayType")):"NULL"
                                                ,Q.pm(rstHouse.getBigDecimal("SumPrice")),Q.pm(afterHouseId)+ ");"));
                                        sqlWriter.newLine();

                                    }
                                    //业务要件
                                    //BUSINESS_FILE
                                    ResultSet fileResulset = statementShark.executeQuery("select * from DGBizDoc where BizID='"+rstRecortRecord.getString("nameid")+"'");
                                    fileResulset.last();
                                    int feilesl=fileResulset.getRow();
                                    if (feilesl>0){
                                        fileResulset.beforeFirst();
                                        while (fileResulset.next()){
                                            sqlWriter.write("INSERT BUSINESS_FILE(ID, BUSINESS_ID, NAME, IMPORTANT_CODE, NO_FILE, IMPORTANT, PRIORITY) VALUES ");
                                            sqlWriter.write("(" + Q.v(Q.p("N"+rstRecortRecord.getString("RecordBizNO")+"-"+fileResulset.getRow()),
                                                    Q.pm(rstRecortRecord.getString("RecordBizNO")),Q.pm(fileResulset.getString("DocType")),
                                                    "'未知'","True","False",Q.pm(String.valueOf(fileResulset.getRow()))+ ");"));
                                            sqlWriter.newLine();
                                        }
                                    }



                                    isFirst = false;
                                    addStateisFirst= false;
                                }

                        }
                    }
                }
                i++;
                System.out.println(i+"/"+String.valueOf(sumCount));
                sqlWriter.flush();
            }
            System.out.println("record is complate");
        } catch (Exception e) {
            System.out.println("record is errer-----"+rstHouse.getString("No"));
            e.printStackTrace();
            return;
        }
    }






}
