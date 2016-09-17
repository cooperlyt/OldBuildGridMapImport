package com.wxy.House.HouseInfo;

import com.cooper.house.Q;
import com.wxy.House.PinyinTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2016-09-15.
 */
public class HouseInfo {

    private static final String OUT_FILE_PATH = "/houseInfo.sql";

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.0.200:1433/DGHouseInfo";

    private static Connection houseInfoConn;

    private static BufferedWriter sqlWriter;

    private static File file;

    private static Statement statement;

    private static ResultSet resultSet;

    private static Set<String> EXCEPTION_BUILD_NO = new HashSet<>();
    private static Set<String> EXCEPTION_HOUSE_NO = new HashSet<>();



    public static void main(String[] args){


        file = new File(OUT_FILE_PATH);

        if (file.exists()){
            file.delete();
        }

        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);
            sqlWriter.write("use HOUSE_INFO;");
            sqlWriter.newLine();
            sqlWriter.write("INSERT MAPPING_CORPORATION (ID, NAME, PYCODE, VERSION, ATTACH_ID, DESTROYED, CREATE_TIME) VALUES ('1','东港市测绘中心',null,0,null,false,'2016-09-07');");
            sqlWriter.flush();
        } catch (IOException e) {
            System.out.println("sql 文件创建失败");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            houseInfoConn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            statement = houseInfoConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Connection is errer");
            e.printStackTrace();
            return;
        }
// 行政区
        try {

            resultSet = statement.executeQuery("SELECT * FROM District");
            sqlWriter.newLine();
            sqlWriter.write("INSERT DISTRICT (ID, NAME, SHORT_NAME, VERSION, CREATE_TIME) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("No")), Q.p(resultSet.getString("Name")), Q.p("东"), Q.p("0"), Q.p(Q.nowFormatTime())));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("DISTRICT is complate");

        } catch (Exception e) {
            System.out.println("DISTRICT is errer");
            e.printStackTrace();
            return;

        }

//小区
        try {
            ResultSet srt = statement.executeQuery("SELECT d.no as did,s.no as sid,s.name,s.CreateDate,s.Address,s.memo from District as d left join Section as s on d.id=s.DistrictID");
            sqlWriter.newLine();
            sqlWriter.write("DROP INDEX NAME ON HOUSE_INFO.SECTION;");
            sqlWriter.newLine();
            sqlWriter.write("INSERT SECTION (ID, NAME, PYCODE, ADDRESS, VERSION, CREATE_TIME, DISTRICT) values ");
            while (srt.next()){
                sqlWriter.write("("+ Q.v(Q.p("N"+srt.getString("sid")), Q.p(srt.getString("Name")), Q.p(PinyinTools.getPinyinCode(srt.getString("Name"))),
                        Q.p(srt.getString("Address")),Q.p("0"), Q.p(srt.getTimestamp("CreateDate")),Q.p(srt.getString("did"))));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }



            sqlWriter.flush();
            System.out.println("Section is complate");
        } catch (Exception e) {
            System.out.println("Section is errer");
            e.printStackTrace();
            return;
        }
// 开发商的重业机构信息
        try {
            ResultSet srt = statement.executeQuery("select * from Developer");

            sqlWriter.newLine();
            sqlWriter.write("INSERT ATTACH_CORPORATION(ID, RECORD_DATE, TYPE, ENABLE, DATE_TO, ADDRESS, PHONE, OWNER_NAME, OWNER_CARD, FAX, MEMO, POST_CODE, LICENSE_NUMBER, TAX_LICENSE, COMPANY_CODE, MANAGER, OWNER_TEL) values ");
            while (srt.next()){
                sqlWriter.write("("+"N"+Q.v(Q.p(srt.getString("NO")), Q.p(Q.nowFormatTime()),Q.p("DEVELOPER"),"FALSE",Q.p(Q.nowFormatTime()),
                        Q.p(srt.getString("Address")),Q.p(srt.getString("PhoneNumber")), Q.p(srt.getString("OwnerName")),Q.p(srt.getString("OwnerCard")),
                        Q.p(srt.getString("Fax")),Q.p(srt.getString("Memo")),Q.p(srt.getString("PostCode")),Q.p(srt.getString("LicenseNO")),Q.p(srt.getString("TaxLicenseNO")),
                        Q.p(srt.getString("CompanyCode")),Q.p(srt.getString("Manager")),Q.p(srt.getString("PhoneNumber"))));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }
            sqlWriter.flush();
            System.out.println("ATTACH_CORPORATION is complate");
        } catch (Exception e) {
            System.out.println("ATTACH_CORPORATION is errer");
            e.printStackTrace();
            return;
        }

        // 开发商信息
        try {
            ResultSet srt = statement.executeQuery("select * from Developer");

            sqlWriter.newLine();
            sqlWriter.write("INSERT DEVELOPER (ID, NAME, PYCODE, VERSION, ATTACH_ID, DESTROYED, CREATE_TIME, DESCRIPTION) values ");
            while (srt.next()){
                sqlWriter.write("("+ Q.v(Q.p("N"+srt.getString("NO")), Q.p(srt.getString("Name")),Q.p(PinyinTools.getPinyinCode(srt.getString("Name"))),"0",Q.p("N"+srt.getString("No")),
                        "False",Q.p(Q.nowFormatTime()),Q.p(srt.getString("memo"))));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }
            sqlWriter.flush();
            System.out.println("Developer is complate");
        } catch (Exception e) {
            System.out.println("Developer is errer");
            e.printStackTrace();
            return;
        }

        // 开发商从业人员
        try {
            ResultSet srt = statement.executeQuery("select a.deno,a.memo,a.dno,o.name,o.idno,o.phone from (select de.no as deno,Owner,de.memo,d.no as dno " +
                    "from DEmployee as de left join Developer as d on de.Developer=d.id) as a left join OwnerInfo as o on a.owner=o.id");

            sqlWriter.newLine();
            sqlWriter.write("INSERT ATTACH_EMPLOYEE (ID,PHONE,NAME,CREDENTIALS_TYPE,CREDENTIALS_NUMBER,ENABLE,CREATE_TIME, CORP) values ");
            while (srt.next()){
                sqlWriter.write("("+ Q.v(Q.p("N"+srt.getString("deno")), Q.p(srt.getString("phone")),Q.p(srt.getString("name")),Q.p("MASTER_ID"),Q.p(srt.getString("idno")),
                        "True",Q.p(Q.nowFormatTime()),Q.p("N"+srt.getString("dno"))));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }
            sqlWriter.flush();
            System.out.println("ATTACH_EMPLOYEE is complate");
        } catch (Exception e) {
            System.out.println("ATTACH_EMPLOYEE is errer");
            e.printStackTrace();
            return;
        }



        // 共有建筑
        try {
            ResultSet srt = statement.executeQuery("SELECT P.*,S.NO FROM PoolBuild AS P LEFT JOIN SECTION AS S ON P.SECTIONID=S.ID");

            sqlWriter.newLine();
            sqlWriter.write("INSERT POOL_BUILD (ID, MAP_NUMBER, BLOCK_NO, BUILD_NO, HOUSE_NUMBER, BUILD_NAME, STRUCTURE," +
                    " ADDRESS, AREA, MEMO, REG_TIME, FLOOR_COUNT, SECTION_ID, CREATE_TIME) values ");
            while (srt.next()){
                sqlWriter.write("("+ Q.v(Q.p(srt.getString("ID")), Q.pm(srt.getString("MapNumber")),Q.pm(srt.getString("BlockNO")),
                        Q.pm(srt.getString("BuildNO")),Q.pm(srt.getString("HouseNO")),Q.pm(srt.getString("BuildName")),srt.getString("Structure")!=null?Q.pm(srt.getString("Structure")):"827",
                        Q.p(srt.getString("Address")),Q.pm(srt.getBigDecimal("BuildArea")),Q.p(srt.getString("Memo")),
                        Q.pm(srt.getTimestamp("RegisterTime")),Q.p("0"),Q.p('N'+srt.getString("No")),
                        Q.pm(srt.getTimestamp("RegisterTime"))));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }
            sqlWriter.flush();
            System.out.println("POOL_BUILD is complate");
        } catch (Exception e) {
            System.out.println("POOL_BUILD is errer");
            e.printStackTrace();
            return;
        }

        // 评估机构
        try {
            ResultSet srt = statement.executeQuery("SELECT * FROM EvaluateCorporation");

            sqlWriter.newLine();
            sqlWriter.write("INSERT EVALUATE_CORPORATION (ID, NAME, PYCODE, VERSION, DESTROYED, CREATE_TIME) values ");
            while (srt.next()){
                sqlWriter.write("("+ Q.v(Q.p("N"+srt.getString("No")), Q.p(srt.getString("NAME")),Q.p(PinyinTools.getPinyinCode(srt.getString("name"))),"0","False",
                        Q.p(Q.nowFormatTime())));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }
            sqlWriter.flush();
            System.out.println("EVALUATE_CORPORATION is complate");
        } catch (Exception e) {
            System.out.println("EVALUATE_CORPORATION is errer");
            e.printStackTrace();
            return;
        }

        // 项目信息
        try {
            ResultSet srt = statement.executeQuery("select a.*,d.no as dno from (select p.*,s.no as sno from project as p left join SECTION as s on p.SECTIONID = s.id) as a " +
                    "left join Developer as d on a.DeveloperID=d.id where a.sno is not null");

            sqlWriter.newLine();
            sqlWriter.write("DROP INDEX NAME ON HOUSE_INFO.PROJECT;");

            sqlWriter.newLine();
            sqlWriter.write("INSERT PROJECT (ID, NAME, SECTIONID, DEVELOPERID, ADDRESS, SUM_AREA, MEMO, VERSION, CREATE_TIME) values ");
            while (srt.next()){
                sqlWriter.write("("+ Q.v(Q.p("N"+srt.getString("No")), Q.p(srt.getString("NAME")),srt.getString("sno")!=null?Q.p("N"+srt.getString("sno")):"NULL"
                        ,srt.getString("dno")!=null?Q.p("N"+srt.getString("dno")):"NULL",Q.p(srt.getString("Address")),Q.pm(srt.getBigDecimal("SumArea"))
                        ,Q.p(srt.getString("MEMO")),"0",Q.p(Q.nowFormatTime())));
                if(srt.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }
            }
            sqlWriter.flush();
            System.out.println("PROJECT is complate");
        } catch (Exception e) {
            System.out.println("PROJECT is errer");
            e.printStackTrace();
            return;
        }


        // 楼幢信息
        try {
           // ResultSet srt = statement.executeQuery("select b.*,p.no as pno from build  as b left join project as p on b.projectid=p.id where b.no not in ('7884','7885')");

            //刨除项目没有挂小区的
            ResultSet srtB = statement.executeQuery("select p.no as pno,b.no as bno  from project as p left join build as b on p.id=b.projectid WHERE p.SECTIONID IS NULL");
            while (srtB.next()){
                if (!EXCEPTION_BUILD_NO.contains(srtB.getString("bno"))) {
                    EXCEPTION_BUILD_NO.add(srtB.getString("bno"));
                }
            }
            //刨除图丘幢重复
            ResultSet srtc = statement.executeQuery("select b.no from (select mapno,blockno,buildno,count(id)as sl from build group by mapno,blockno,buildno) as a left join build as b on a.mapno=b.mapno and a.blockno=b.blockno and a.buildno=b.buildno where a.sl>1");
            while (srtc.next()){
                if (!EXCEPTION_BUILD_NO.contains(srtc.getString("no"))) {
                    EXCEPTION_BUILD_NO.add(srtc.getString("no"));
                }
            }

            ResultSet srt = statement.executeQuery("select b.*,p.no as pno from build  as b left join project as p on b.projectid=p.id");
            sqlWriter.newLine();
            sqlWriter.write("DROP INDEX NAME ON HOUSE_INFO.BUILD;");
            sqlWriter.newLine();
            sqlWriter.write("ALTER TABLE HOUSE_INFO.BUILD CHANGE MAP_NUMBER MAP_NUMBER varchar(8);");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");


            sqlWriter.newLine();
            sqlWriter.write("INSERT BUILD(ID, MAP_NUMBER, BLOCK_NO, BUILD_NO,NAME, COMPLETE_DATE,PROJECT_ID, DOOR_NO, UNINT_COUNT, HOUSE_COUNT, AREA, BUILD_TYPE,STRUCTURE, VERSION, MEMO, NEXT_HOUSE_ORDER, UP_FLOOR_COUNT, DOWN_FLOOR_COUNT,CREATE_TIME, DEVELOPER_NUMBER,MAP_TIME,MAP_CORP) values ");
            while (srt.next()){

                if(!EXCEPTION_BUILD_NO.contains(srt.getString("No"))) {
                    sqlWriter.write("(" + Q.v(Q.p("N" + srt.getString("No")), Q.pm(srt.getString("MapNO")), Q.pm(srt.getString("BlockNO"))
                            , Q.pm(srt.getString("BuildNO")), Q.pm(srt.getString("BuildName")), srt.getTimestamp("FirmlyDate") != null ? Q.p(simpleDateFormat.format(srt.getTimestamp("FirmlyDate"))) : "NULL"
                            , Q.pm("N" + srt.getString("pno")), Q.p(srt.getString("DoorNO")), Q.p(srt.getString("UnintCount")),
                            Q.p(srt.getString("SumCount")), Q.p(srt.getBigDecimal("SumArea")), Q.p(srt.getString("BuildType")),
                            srt.getString("Structure") != null ? Q.p(srt.getString("Structure")) : "827", "0", Q.p(srt.getString("Memo")), "1", srt.getString("FloorCount") != null ? Q.pm(srt.getString("FloorCount")) : "1",
                            "0", Q.p(Q.nowFormatTime()), Q.p(srt.getString("DoorNO")), Q.p(Q.nowFormatTime()), "1"));
                    if (srt.isLast()) {
                        sqlWriter.write(");");
                    } else {
                        sqlWriter.write("),");
                    }
                }
            }
            sqlWriter.flush();
            System.out.println("BUILD is complate");
        } catch (Exception e) {
            System.out.println("BUILD is errer");
            e.printStackTrace();
            return;
        }


        // 房屋信息
        try {


            ResultSet srta =statement.executeQuery("select hh.no as hno,bb.no as bno" +
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


            ResultSet srt = statement.executeQuery("select h.*,b.no as bno from house as h left join build b on h.buildid=b.id");

            sqlWriter.newLine();
            sqlWriter.write("INSERT HOUSE (ID, BUILDID, HOUSE_ORDER, HOUSE_UNIT_NAME, IN_FLOOR_NAME, " +
                    "HOUSE_AREA, USE_AREA, COMM_AREA, SHINE_AREA, LOFT_AREA, COMM_PARAM, " +
                    "HOUSE_TYPE, USE_TYPE, STRUCTURE, ADDRESS, DATA_SOURCE, " +
                    "VERSION, " +
                    "MEMO, HAVE_DOWN_ROOM, CREATE_TIME, DELETED) values ");
            while (srt.next()){

                if(!EXCEPTION_BUILD_NO.contains(srt.getString("bno"))) {
                    if (!EXCEPTION_HOUSE_NO.contains(srt.getString("No"))) {

                        sqlWriter.write("(" + Q.v(Q.p(srt.getString("No")), Q.p(srt.getString("bno")), Q.p(srt.getString("HouseOrder"))
                                , Q.p(srt.getString("UnitName")), Q.pm(srt.getString("InFloorName")), Q.pm(srt.getBigDecimal("HouseArea"))
                                , Q.p(srt.getBigDecimal("UseArea")), Q.p(srt.getBigDecimal("CommArea")), Q.p(srt.getBigDecimal("ShineArea"))
                                , Q.p(srt.getBigDecimal("LoftArea")), Q.p(srt.getBigDecimal("CommParam"))
                                , Q.changeHouseType(srt.getInt("HouseType")), srt.getString("UseType") != null ? Q.changeUseType(srt.getInt("UseType")) : "'未知'"
                                , srt.getString("STRUCTURE") != null ? Q.changeStructure(srt.getInt("STRUCTURE")) : "'未知'"
                                , Q.pm(srt.getString("HouseStation")), "'IMPORT'", "0", Q.p(srt.getString("memo"))
                                , "False", Q.p(Q.nowFormatTime()), "False"));
                        if (srt.isLast()) {
                            sqlWriter.write(");");
                        } else {
                            sqlWriter.write("),");
                        }
                    }
                }

            }
            sqlWriter.flush();
            System.out.println("HOUSE is complate");
        } catch (Exception e) {
            System.out.println("HOUSE is errer");
            e.printStackTrace();
            return;
        }





    }






}
