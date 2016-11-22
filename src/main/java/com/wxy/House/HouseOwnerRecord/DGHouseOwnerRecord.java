package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;
import com.scoopit.weedfs.client.net.Result;
import com.wxy.House.SlectInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
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


    private static Connection recordConnection;

    private static Connection houseConnection;

    private static Connection sharkConnection;

    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;


    private static Statement statementRecord;

    private static Statement statementRecordch;

    private static Statement statementHouse;

    private static Statement statementHousech;

    private static Statement statementShark;

    private static Statement statementOwnerRecord;

    private static ResultSet recordResultSet;

    private static ResultSet houseResultSet;

    private static Set<String> LOCKED_HOUSE_NO = new HashSet<>();

    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();

    private static Set<String> SELECT_DEFINE_ID = new HashSet<>();

    private static String DEFINE_ID;
    private static String DEFINE_NAME;
    private static String bizid;//业务编号

    private static String selectbizid;//bei

    public static void main(String agr[]){

        //有selectbiz业务ID
        SELECT_DEFINE_ID.add("WP10");//房屋所有权抵押变更登记
        SELECT_DEFINE_ID.add("WP11");//房屋所有权抵押转移登记
        SELECT_DEFINE_ID.add("WP12");//房屋所有权抵押注销登记
        SELECT_DEFINE_ID.add("WP14");//最高额抵押权确定登记
        SELECT_DEFINE_ID.add("WP15");//最高额抵押权设定变更登记
        SELECT_DEFINE_ID.add("WP16");//最高额抵押权设定转移登记
        SELECT_DEFINE_ID.add("WP17");//最高额抵押权设定注销登记
        SELECT_DEFINE_ID.add("WP84");//在建工程房屋抵押权注销登记
        SELECT_DEFINE_ID.add("WP2");//预购商品房抵押权预告变更登记
        SELECT_DEFINE_ID.add("WP3");//预购商品房抵押权预告转移登记
        SELECT_DEFINE_ID.add("WP4");//预购商品房抵押权预告注销登记
        SELECT_DEFINE_ID.add("WP6");//房屋抵押权预告变更登记
        SELECT_DEFINE_ID.add("WP7");//房屋抵押权预告转移登记
        SELECT_DEFINE_ID.add("WP8");//房屋抵押权预告注销登记
        //不导入的业务编号
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
            ResultSet rstHouse = statementHouse.executeQuery("select hd.no as hdno,hd.name as hdname,e.* from " +
                    " (select hs.no as hsno,hs.name as hsname,hs.DistrictID,d.* from" +
                    " (select p.no as pno,p.name as pname,c.* from" +
                    " (select hp.no as hpno,hp.name as hpnmae,hp.DeveloperID,hp.SectionID,a.* from"+
                    " (select b.no as bno,b.BuildName,b.DoorNO as bDoorNO,b.Mapno,b.blockNo,b.buildNo,b.BuildType,b.FirmlyDate,"+
                    " b.FloorCount,b.ProjectID,h.* from DGHouseInfo..house as h left join DGHouseInfo..Build as b on h.buildid=b.id"+
                    " ) as a"+
                    " left join project as hp on a.ProjectID=hp.id) as c"+
                    " left join Developer as p on c.DeveloperID=p.id) as d"+
                    " left join Section as hs on d.sectionid=hs.id) as e"+
//                    " left join District as hd on e.DistrictID = hd.id");
                    " left join District as hd on e.DistrictID = hd.id" +
                    " where (e.no='B174N1-6-04')");
            rstHouse.last();
            System.out.println("rstHouseCount-Start-:" + rstHouse.getRow());
            int sumCount = rstHouse.getRow();
            rstHouse.beforeFirst();
            sqlWriter.newLine();
            int i=0;
            while (rstHouse.next()){
                ResultSet rstRecortRecord = statementRecord.executeQuery("select * from" +
                        " (select hh.* from DGHouseRecord..HouseHistroy hh where hh.id in " +
                        " (select max(h1.id) as hhid from DGHouseRecord..HouseHistroy as h1 where inbizcode is not null and business is not null and h1.no='" +rstHouse.getString("No")+"'" +
                        " group by inbizcode) " +
                        " ) as a " +
                        " left join DGHouseRecord..Business as hb on a.inbizcode=hb.nameid " +
                        " where hb.workid not like '%WP50' and  hb.workid not like '%WP51' and  hb.workid not like '%WP85' and " +
                        " hb.workid not like '%WP42' and hb.workid not like '%WP43' " +
                        " and hb.workid not like '%WP52' and hb.workid not like '%WP53' and hb.workid not like '%WP54'" +
                        " and hb.workid not like '%WP55' and hb.workid not like '%WP76' and hb.workid not like '%WP77'" +
                        " and hb.workid not like '%WP78' and hb.workid not like '%WP79' and hb.workid not like '%WP80'" +
                        " and hb.workid not like '%WP44' and hb.workid not like '%WP45' and hb.workid not like '%WP46'" +
                        " and hb.workid not like '%WP47' and hb.workid not like '%WP48' and hb.workid not like '%WP49'" +
                        " and hb.workid not like '%WP32' and hb.workid not like '%WP33' and hb.workid not like '%WP35'" +
                        " and hb.workid not like '%WP36' and hb.workid not like '%WP37' and hb.workid not like '%WP38'" +
                        " and hb.workid not like '%WP34' and hb.workid not like '%WP39' and hb.workid not like '%WP69'" +
                        " and hb.workid not like '%WP70' and hb.workid not like '%WP22' and hb.workid not like '%WP23'" +
                        " and hb.workid not like '%WP24' and hb.workid not like '%WP25' and hb.workid not like '%WP26'" +
                        " and hb.workid not like '%WP27' and hb.workid not like '%WP28' and hb.workid not like '%WP29'" +
                        " and hb.workid not like '%WP29' and hb.workid not like '%WP18' and hb.workid not like '%WP19'" +
                        " and hb.workid not like '%WP20' and hb.workid not like '%WP21' and hb.workid not like '%WP21'" +
                        " and hb.workid not like '%WP73' and hb.workid not like '%WP74' and hb.workid not like '%WP81'" +
                        " and hb.workid not like '%WP82' and hb.workid not like '%WP88' and " +
                        " hb.b>='"+BEGIN_DATE+"'"+
                        " order by a.no,hb.Botime");


                rstRecortRecord.last();
                int recordCount = rstRecortRecord.getRow();
                if (recordCount > 0){//有需要导入的业务
                    rstRecortRecord.beforeFirst();
                    while (rstRecortRecord.next()){
                        String[] temp;
                        if (rstRecortRecord.getString("workid").contains("A")) {
                            temp = rstRecortRecord.getString("workid").split("_");

                        } else {
                            temp = rstRecortRecord.getString("workid").split("#");
                        }
                        DEFINE_ID = temp[temp.length - 1];//业务标识WP40

                        ResultSet resultSetbizNmae = statementShark.executeQuery("select memo from Shark..DGBiz where ID='"+rstRecortRecord.getString("BizID")+"'");
                        resultSetbizNmae.next();
                        if (resultSetbizNmae.getString("memo")!=null){
                            DEFINE_NAME = resultSetbizNmae.getString("memo");
                        }else{
                            DEFINE_NAME = "未知";
                        }


                        selectbizid = null;

                        if (SELECT_DEFINE_ID.contains(DEFINE_ID)){//判断是否导入selectbiz
                            System.out.println(DEFINE_ID);
                            System.out.println("1111-"+rstRecortRecord.getString("SelectBiz"));

                            if ((rstRecortRecord.wasNull())){
                                System.out.println(2222);

                            }else {
                                System.out.println(4444);
                            }
                            if (!(rstRecortRecord.wasNull())
                                    && !rstRecortRecord.getString("SelectBiz").equals("")){
                                selectbizid = rstRecortRecord.getString("SelectBiz");
                                System.out.println("SelectBiz--" + rstRecortRecord.getString("SelectBiz"));

                                ResultSet rstRecortbiz = statementRecordch.executeQuery("select RecordBizNO from DGHouseRecord..Business where id='" + rstRecortRecord.getString("SelectBiz") + "'");
                                rstRecortbiz.next();
                                System.out.println("select RecordBizNO from DGHouseRecord..Business where id='" + rstRecortRecord.getString("SelectBiz") + "'");
                                if (!(rstRecortbiz.wasNull()) && !rstRecortbiz.getString("RecordBizNO").equals("")) {
                                    selectbizid = rstRecortbiz.getString("RecordBizNO");
                                } else {
                                    selectbizid = null;
                                }
                            }
                        }
                        sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                                " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                        sqlWriter.write("(" + Q.v(Q.p(rstRecortRecord.getString("RecordBizNO")), "0", "'BIZ_IMPORT'", Q.p(rstRecortRecord.getString("MEMO"))
                                , "'COMPLETE'",DEFINE_NAME, Q.pm(DEFINE_ID), "0", Q.p(selectbizid), Q.p(rstRecortRecord.getTimestamp("BOTime"))
                                , Q.p(rstRecortRecord.getTimestamp("BOTime")), "Null", Q.p(rstRecortRecord.getTimestamp("BOTime")), Q.p(rstRecortRecord.getTimestamp("BOTime")), "False", "'NORMAL_BIZ'") + ");");
                        sqlWriter.newLine();







                    }

                }
                i++;
                System.out.println(i+"/"+String.valueOf(sumCount));
                sqlWriter.flush();
            }
            System.out.println("record is complate");
        } catch (Exception e) {
            System.out.println("record is errer");
            e.printStackTrace();
            return;
        }


    }






}
