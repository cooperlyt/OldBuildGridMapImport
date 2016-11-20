package com.wxy.House.HouseOwnerRecord;

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

    private static Connection recordConnection;

    private static Connection houseConnection;

    private static Connection sharkConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;


    private static Statement statementRecord;

    private static Statement statementRecordch;

    private static Statement statementHouse;

    private static Statement statementHousech;

    private static Statement statementShark;

    private static ResultSet recordResultSet;

    private static ResultSet houseResultSet;

    private static Set<String> LOCKED_HOUSE_NO = new HashSet<>();

    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();


    public static void main(String agr[]){
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
                    " left join District as hd on e.DistrictID = hd.id");
//                    " left join District as hd on e.DistrictID = hd.id" +
//                    " where (e.no='137034')");
            rstHouse.last();
            System.out.print("rstHouseCount-Start-:" + rstHouse.getRow());
            int sumCount = rstHouse.getRow();
            rstHouse.beforeFirst();
            sqlWriter.newLine();
            int i=0;
            while (rstHouse.next()){




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
