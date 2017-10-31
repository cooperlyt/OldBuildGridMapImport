package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_ATOPPeer;

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
 * Created by wxy on 2017-10-27.
 * 青冈倒库
 */
public class QgHouseOwnerRecord {

    private static final String OUT_PATH_FILE="/QGhouseOwnerRecord.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE="/QGHouseOwnerError.sql";

    private static final String DB_FANG_CHAN_URL="jdbc:jtds:sqlserver://127.0.0.1:1433/fang_chan_qg";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static Connection fangchanConnection;

    private static Connection ownerRecordConnection;

    private static BufferedWriter sqlWriter;

    private static BufferedWriter houseOwnerError;

    private static File recordFile;

    private static File houseOwnerErrorFile;

    private static Statement statementFangchan;

    private static Statement statementFangchanCH;

    private static Statement statementFangchanCH1;

    private static Statement statementOwnerRecord;

    private static ResultSet fangChanResultSet;

    private static ResultSet fangChanResultSetCH;
    private static ResultSet fangChanResultSetCH1;

    private static ResultSet recordResultSet;

    private static ResultSet resultSetHouseRecord;

    private static String DEFINE_ID;

    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();

    private static Set<String> DEAL_DEFINE_ID= new HashSet<>();

    private static Set<String> MORTGAEGE_DEFINE_ID = new HashSet<>();

    public static void main(String agr[]) throws SQLException {

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
        DEAL_DEFINE_ID.add("WP71");//房屋交换
        DEAL_DEFINE_ID.add("WP143");//房屋调拨


        //变更登记导入已办产权
        DEAL_DEFINE_ID.add("WP52");//名称变更登记(房屋变更)
        DEAL_DEFINE_ID.add("WP53");//自翻扩改（面积变更）
        DEAL_DEFINE_ID.add("WP102");//坐落、用途、土地信息变更（用途变更）
        DEAL_DEFINE_ID.add("WP54");//分照
        DEAL_DEFINE_ID.add("WP55");//合照

        //初始登记入已办产权
        DEAL_DEFINE_ID.add("WP30");//新建房屋
        DEAL_DEFINE_ID.add("WP31");//无籍房屋
        DEAL_DEFINE_ID.add("WP32");//所有权遗失补照
        DEAL_DEFINE_ID.add("WP33");//换照
        DEAL_DEFINE_ID.add("WP35");//所有权更正登记
        DEAL_DEFINE_ID.add("WP75");//集资建房
        DEAL_DEFINE_ID.add("BL1");//产权补录档案

        //抵押登记
        MORTGAEGE_DEFINE_ID.add("WP9");// 房屋所有权抵押登记
        MORTGAEGE_DEFINE_ID.add("WP10");//房屋所有权抵押变更登记
        MORTGAEGE_DEFINE_ID.add("WP14");//最高额抵押权确定登记
        MORTGAEGE_DEFINE_ID.add("WP12");//房屋所有权抵押注销登记
        MORTGAEGE_DEFINE_ID.add("BL2");//房屋抵押权补录档案
        MORTGAEGE_DEFINE_ID.add("WP13");//最高额抵押登记
        MORTGAEGE_DEFINE_ID.add("WP15");//最高额抵押权设定变更登记
        MORTGAEGE_DEFINE_ID.add("WP17");//最高额抵押权设定注销登记
        MORTGAEGE_DEFINE_ID.add("WP22");//他项权更正登记
        MORTGAEGE_DEFINE_ID.add("WP25");//他项权遗失补照
        MORTGAEGE_DEFINE_ID.add("OM2");//抵押注销登记

        MORTGAEGE_DEFINE_ID.add("WP18");//在建工程抵押登记
        MORTGAEGE_DEFINE_ID.add("WP21");//在建工程抵押权设定注销登记
        MORTGAEGE_DEFINE_ID.add("WP19");//在建工程房屋抵押权注销登记

        MORTGAEGE_DEFINE_ID.add("WP1");//预购商品房抵押权预告登记
        MORTGAEGE_DEFINE_ID.add("WP2");//预购商品房抵押权预告变更登记
        MORTGAEGE_DEFINE_ID.add("WP4");//预购商品房抵押权预告注销登记


        MORTGAEGE_DEFINE_ID.add("WP5");//房屋抵押权预告登记
        MORTGAEGE_DEFINE_ID.add("WP6");//房屋抵押权预告变更登记
        MORTGAEGE_DEFINE_ID.add("WP8");//房屋抵押权预告注销登记

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
            fangchanConnection = DriverManager.getConnection(DB_FANG_CHAN_URL, "sa", "dgsoft");
            statementFangchan = fangchanConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementFangchanCH = fangchanConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statementFangchanCH1 = fangchanConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("fangchanConnection successful");
        } catch (Exception e) {
            System.out.println("fangchanConnection is errer");
            e.printStackTrace();
            return;
        }


        try {
            Class.forName("com.mysql.jdbc.Driver");
            //ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "isNull");
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "dgsoft");
            statementOwnerRecord = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            System.out.println("ownerRecordConnection successful");
        } catch (Exception e) {
            System.out.println("ownerRecordConnection is errer");
            e.printStackTrace();
            return;
        }
        try {
            fangChanResultSet = statementFangchan.executeQuery("select a.*,sl.sl_tx_bgq,sl.sl_yingyezhizhao,sl.sl_ycqr_card from " +
                    "(select jb.*,sz.sz_beizhu from c_jiben jb left join c_shanzheng sz on jb.keycode=sz.keycode) as a " +
                    "left join c_shouli sl on a.keycode=sl.keycode " +
                    "where a.keycode='201004070040' order by a.ywmc_bs,a.keycode");
            fangChanResultSet.last();
            int recordCount = fangChanResultSet.getRow();
            System.out.println("count-"+recordCount);
            sqlWriter.newLine();
            if (recordCount>0){
                fangChanResultSet.beforeFirst();
                int i=0;
                String oldhouseid = "";
                String stratHouseId = "",afterHouseId="";
                String keycode = null,lastHouseState=null;
                String houseCodeDisplay =null;
                while (fangChanResultSet.next()) {
                    ResultSet resultSetOwnerRecordCh = statementOwnerRecord.executeQuery("SELECT * FROM OWNER_BUSINESS WHERE ID='" + fangChanResultSet.getString("keycode") + "'");
                    resultSetOwnerRecordCh.last();
                    if (resultSetOwnerRecordCh.getRow() <= 0) {//说明HOUSE_OWNER_RECORD 没有，需要从fangchan_qg导入
                        //OWNER_BUSINESS
                        DEFINE_ID = fangChanResultSet.getString("defineID");

                        sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                                " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                        sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), "0", "'BIZ_IMPORT'", Q.p(fangChanResultSet.getString("jbxx_fj"))
                                , "'COMPLETE'", Q.pm(fangChanResultSet.getString("ywmc")), Q.pm(DEFINE_ID), "0", "Null", Q.p(fangChanResultSet.getTimestamp("CREATE_TIME"))
                                , Q.p(fangChanResultSet.getTimestamp("CREATE_TIME")), Q.p(fangChanResultSet.getTimestamp("CHECK_TIME")), Q.p(fangChanResultSet.getTimestamp("REG_TIME")), Q.p(fangChanResultSet.getTimestamp("RECORD_TIME")), "True", "'NORMAL_BIZ'") + ");");
                        sqlWriter.newLine();

                        //CARD 业务证书号,
                        String cardType = "OTHER_CARD";
                        String number = null;
                        if (DEAL_DEFINE_ID.contains(DEFINE_ID)) { //带原产权证号的
                            cardType = "OWNER_RSHIP";
                            number = fangChanResultSet.getString("chanquanzheng");
                            if (!DEFINE_ID.equals("WP30") && !DEFINE_ID.equals("WP31") && !DEFINE_ID.equals("WP75")) {
                                sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("OLD_OWNER_RSHIP"),
                                        Q.pm1(fangChanResultSet.getString("y_chanquanzheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                        "NUll" + ");"));
                                sqlWriter.newLine();
                            }
                        }
                        if(DEFINE_ID.contains("WP73")||DEFINE_ID.contains("WP74") || DEFINE_ID.equals("WP38")|| DEFINE_ID.equals("WP34") ||DEFINE_ID.equals("WP36")  ){//查封，查封解除,灭籍，声明作废
                            cardType = "OWNER_RSHIP";
                            number = fangChanResultSet.getString("cqzh");

                        }
                        //抵押登记所有权证号
                        if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                || DEFINE_ID.equals("WP14") || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("BL2")){
                            cardType = "MORTGAGE";
                            number = fangChanResultSet.getString("taxiangzhenghao");
                            sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                            sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("OWNER_RSHIP"),
                                    Q.pm1(fangChanResultSet.getString("y_chanquanzheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                    "NUll" + ");"));
                            sqlWriter.newLine();
                        }

                        //在建抵押
                        if (DEFINE_ID.equals("WP18") || DEFINE_ID.equals("WP19") || DEFINE_ID.equals("WP21")){
                            cardType = "PROJECT_MORTGAGE";
                            number = fangChanResultSet.getString("chanquanzheng");

                        }
                        //预告抵押
                        if (DEFINE_ID.equals("WP1") || DEFINE_ID.equals("WP5")) {
                            cardType = "NOTICE_MORTGAGE";
                            number = fangChanResultSet.getString("chanquanzheng");
                        }
                        //预告抵押注销
                        if (DEFINE_ID.equals("WP4") ||  DEFINE_ID.equals("WP8")) {
                            cardType = "NOTICE_MORTGAGE";
                            number = fangChanResultSet.getString("y_chanquanzheng");
                        }

                        if (DEFINE_ID.equals("WP44")) {
                            cardType = "NOTICE";
                            number = fangChanResultSet.getString("chanquanzheng");
                        }

                        if (DEFINE_ID.equals("WP46")) {
                            cardType = "NOTICE";
                            number = fangChanResultSet.getString("y_chanquanzheng");
                        }
                        if (!DEFINE_ID.equals("WP40")) {
                            sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                            sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(cardType),
                                    Q.pm1(number), Q.pm(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("sz_beizhu")),
                                    "NUll" + ");"));
                            sqlWriter.newLine();
                        }
                        //startHouse
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

                        //System.out.println(Q.pmZc(fangChanResultSet.getString("ch_zongceng")));
                        String kfs=null;
                        if(DEFINE_ID.equals("WP41")){
                            if(fangChanResultSet.getString("y_cqr")!=null && !fangChanResultSet.getString("y_cqr").equals("")){
                                kfs = fangChanResultSet.getString("y_cqr");
                            }
                        }

                        sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")+"-F"), Q.pm1(fangChanResultSet.getString("fanghao"))
                                , "Null", Q.pm1(fangChanResultSet.getString("ceng"))
                                , Q.pm(fangChanResultSet.getBigDecimal("mj_jianzhu")), "0"
                                , "0", "0", "0", "0"
                                , Q.p(fangChanResultSet.getString("HOUSE_TYPE")), Q.p(fangChanResultSet.getString("DesignUseType"))
                                , Q.p(fangChanResultSet.getString("structure"))
                                , Q.pm1(fangChanResultSet.getString("zuoluoxx")), Q.pm(fangChanResultSet.getString("CREATE_TIME"))
                                , Q.pm(fangChanResultSet.getString("keycode")), "False", "'未知'", "'未知'", Q.pm1(fangChanResultSet.getString("qiuhao"))
                                , Q.pm1(fangChanResultSet.getString("zhuanghao")), "'未知'", Q.pmZc(fangChanResultSet.getString("zongceng"))
                                , Q.pmZc(fangChanResultSet.getString("zongceng")), "0", "Null", "'未知'", "'未知'"
                                , "Null"
                                , "'未知'", Q.pm1(kfs), "'未知'"
                                , "'未知'", "'未知'", "'未知'", "'未知'", "Null", "Null", "Null", "Null", "Null", Q.pm1(fangChanResultSet.getString("yongtu")), "''" + ");"));
                        sqlWriter.newLine();

                        //afteferHouse
                        sqlWriter.write("INSERT HOUSE (ID, HOUSE_ORDER, HOUSE_UNIT_NAME, IN_FLOOR_NAME, " +
                                "HOUSE_AREA, USE_AREA, COMM_AREA, SHINE_AREA, LOFT_AREA, COMM_PARAM, " +
                                "HOUSE_TYPE, USE_TYPE, STRUCTURE, ADDRESS, " +
                                "MAP_TIME,HOUSE_CODE, " +
                                "HAVE_DOWN_ROOM, BUILD_CODE, MAP_NUMBER, BLOCK_NO, BUILD_NO, " +
                                "DOOR_NO, UP_FLOOR_COUNT, FLOOR_COUNT, DOWN_FLOOR_COUNT, BUILD_TYPE, " +
                                "PROJECT_CODE, PROJECT_NAME, COMPLETE_DATE, DEVELOPER_CODE, DEVELOPER_NAME," +
                                "SECTION_CODE, SECTION_NAME, DISTRICT_CODE, DISTRICT_NAME, BUILD_NAME, " +
                                "BUILD_DEVELOPER_NUMBER, POOL_MEMO, MAIN_OWNER, LAND_INFO, REG_INFO, DESIGN_USE_TYPE, " +
                                "UNIT_NUMBER) VALUES");
                        sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), Q.pm1(fangChanResultSet.getString("fanghao"))
                                , "Null", Q.pm1(fangChanResultSet.getString("ceng"))
                                , Q.pm(fangChanResultSet.getBigDecimal("mj_jianzhu")), "0"
                                , "0", "0", "0", "0"
                                , Q.p(fangChanResultSet.getString("HOUSE_TYPE")), Q.p(fangChanResultSet.getString("DesignUseType"))
                                , Q.p(fangChanResultSet.getString("structure"))
                                , Q.pm1(fangChanResultSet.getString("zuoluoxx")), Q.pm(fangChanResultSet.getString("CREATE_TIME"))
                                , Q.pm(fangChanResultSet.getString("keycode")), "False", "'未知'", "'未知'", Q.pm1(fangChanResultSet.getString("qiuhao"))
                                , Q.pm1(fangChanResultSet.getString("zhuanghao")), "'未知'", Q.pmZc(fangChanResultSet.getString("zongceng"))
                                , Q.pmZc(fangChanResultSet.getString("zongceng")), "0", "Null", "'未知'", "'未知'"
                                , "Null"
                                , "'未知'", Q.pm1(kfs), "'未知'"
                                , "'未知'", "'未知'", "'未知'", "'未知'", "Null", Q.p(fangChanResultSet.getString("PoolMemo")), "Null", "Null", "Null", Q.pm1(fangChanResultSet.getString("yongtu")), "''" + ");"));
                        sqlWriter.newLine();

                        //--- BUSINESS_HOUSE
                        KeyGeneratorHelper key = new KeyGeneratorHelper();
                        key.addWord(fangChanResultSet.getString("cqr")); //产权人
                        key.addWord(fangChanResultSet.getString("cqr_card"));//产权人身份证号

                        ResultSet resultSetFangchanGy= statementFangchanCH.executeQuery("SELECT * FROM c_gongyou WHERE keycode='" + fangChanResultSet.getString("keycode") + "'");
                        resultSetFangchanGy.last();
                        int gysl=resultSetFangchanGy.getRow(),k=2;
                        if (gysl>0){
                            resultSetFangchanGy.beforeFirst();
                            while(resultSetFangchanGy.next()){
                                if(resultSetFangchanGy.getString("gy_ren")!=null && !resultSetFangchanGy.getString("gy_ren").equals("")) {
                                    key.addWord((resultSetFangchanGy.getString("gy_ren").trim())); //共有权人
                                }
                                if (resultSetFangchanGy.getString("gy_card")!=null && !resultSetFangchanGy.getString("gy_card").equals("")) {
                                    key.addWord(resultSetFangchanGy.getString("gy_card").trim()); //身份证号
                                }

                                sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-"+Integer.toString(k)+"G"), Q.pm("POOL_RSHIP"),
                                        Q.pm1(resultSetFangchanGy.getString("gy_gongyouzheng")), Q.pm(fangChanResultSet.getString("keycode")),Q.p(resultSetFangchanGy.getString("gy_beizhu")),
                                        "NUll" + ");"));
                                sqlWriter.newLine();
                                k++;

                            }
                        }

                        if (DEAL_DEFINE_ID.contains(DEFINE_ID)){
                            if (fangChanResultSet.getString("y_cqr") != null && !fangChanResultSet.getString("y_cqr").equals("")) {
                                key.addWord((fangChanResultSet.getString("y_cqr").trim())); //原产权人
                            }
                            if (fangChanResultSet.getString("sl_ycqr_card")!=null && !fangChanResultSet.getString("sl_ycqr_card").equals("")){
                                key.addWord((fangChanResultSet.getString("sl_ycqr_card").trim())); //原产权人身份证号
                            }
                            if (fangChanResultSet.getString("y_chanquanzheng")!=null && !fangChanResultSet.getString("y_chanquanzheng").equals("")){
                                key.addWord((fangChanResultSet.getString("y_chanquanzheng").trim())); //原产权证号
                            }
                        }
                        if (DEFINE_ID.contains("WP73")){

                            if(fangChanResultSet.getString("jb_cffy")!=null && !fangChanResultSet.getString("jb_cffy").equals("")){
                                key.addWord((fangChanResultSet.getString("jb_cffy").trim())); //查封法院
                            }
                            if(fangChanResultSet.getString("jb_cfwh")!=null && !fangChanResultSet.getString("jb_cfwh").equals("")){
                                key.addWord((fangChanResultSet.getString("jb_cfwh").trim())); //查封文号
                            }
                        }
                        if (DEFINE_ID.contains("WP74")){
                            if(fangChanResultSet.getString("sl_tx_bgq")!=null && !fangChanResultSet.getString("sl_tx_bgq").equals("")){
                                key.addWord((fangChanResultSet.getString("sl_tx_bgq").trim())); //解封法院
                            }
                            if(fangChanResultSet.getString("sl_yingyezhizhao")!=null && !fangChanResultSet.getString("sl_yingyezhizhao").equals("")){
                                key.addWord((fangChanResultSet.getString("sl_yingyezhizhao").trim())); //解封文号
                            }
                        }
                        if(DEFINE_ID.equals("WP38")|| DEFINE_ID.equals("WP34") ||DEFINE_ID.equals("WP36")  ){//灭籍，声明作废

                        }
                        //抵押登记所有权证号
                        if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                || DEFINE_ID.equals("WP14") || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("BL2")){
                            if(fangChanResultSet.getString("y_chanquanzheng")!=null && !fangChanResultSet.getString("y_chanquanzheng").equals("")) {
                                key.addWord((fangChanResultSet.getString("y_chanquanzheng").trim()));
                            }

                            if(fangChanResultSet.getString("diyaquanren")!=null && !fangChanResultSet.getString("diyaquanren").equals("")) {
                                key.addWord((fangChanResultSet.getString("diyaquanren").trim()));
                            }

                        }

                        //在建抵押 预告抵押 预告抵押注销
                        if (DEFINE_ID.equals("WP18") || DEFINE_ID.equals("WP19") || DEFINE_ID.equals("WP21")
                                || DEFINE_ID.equals("WP1") || DEFINE_ID.equals("WP4") || DEFINE_ID.equals("WP5") || DEFINE_ID.equals("WP8")){
                            if(fangChanResultSet.getString("diyaquanren")!=null && !fangChanResultSet.getString("diyaquanren").equals("")) {
                                key.addWord((fangChanResultSet.getString("diyaquanren").trim()));
                            }
                        }
                        //丘幢号 houseCodeDisplay
                        String blackno=null,buildno=null,houseorder=null;
                        houseCodeDisplay = null;
                        if (fangChanResultSet.getString("qiuhao")!=null && !fangChanResultSet.getString("qiuhao").equals("")){
                            blackno = fangChanResultSet.getString("qiuhao").trim();
                        }
                        if (fangChanResultSet.getString("zhuanghao")!=null && !fangChanResultSet.getString("zhuanghao").equals("")){
                            buildno = fangChanResultSet.getString("zhuanghao").trim();
                        }
                        if (fangChanResultSet.getString("fanghao")!=null && !fangChanResultSet.getString("fanghao").equals("")){
                            houseorder = fangChanResultSet.getString("fanghao").trim();
                        }
                        if (blackno!=null && buildno!=null && houseorder!=null) {
                            houseCodeDisplay = blackno + "-" + buildno + "-" + houseorder;
                        }
                        if (blackno!=null && buildno==null && houseorder==null) {
                            houseCodeDisplay = blackno;
                        }
                        if (blackno==null && buildno!=null && houseorder==null) {
                            houseCodeDisplay = buildno;
                        }
                        if (blackno==null && buildno==null && houseorder!=null) {
                            houseCodeDisplay = houseorder;
                        }
                        if (blackno!=null && buildno!=null && houseorder==null) {
                            houseCodeDisplay = blackno + "-" + buildno;
                        }
                        if (blackno==null && buildno!=null && houseorder!=null) {
                            houseCodeDisplay = buildno+"-"+houseorder;
                        }
                        if (blackno!=null && buildno==null && houseorder!=null) {
                            houseCodeDisplay = blackno+"-"+houseorder;
                        }

                        key.addWord(houseCodeDisplay);//产籍号
                        if (number!=null && !number.equals("")) {
                            key.addWord(number);//权证号
                        }
                        key.addWord(fangChanResultSet.getString("zuoluoxx"));//房屋坐落
                        System.out.println(key.getKey());



                        DescriptionDisplay businessDisplay = new DescriptionDisplay();
                        businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "房屋编号");
                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,houseCodeDisplay);
                        String cqr="",gyqr="";
                        if(fangChanResultSet.getString("cqr_card")!=null && !fangChanResultSet.getString("cqr_card").equals("")){
                            cqr = fangChanResultSet.getString("cqr")+"["+fangChanResultSet.getString("cqr_card")+"]";
                        }else{
                            cqr = fangChanResultSet.getString("cqr");
                        }

                        if(DEAL_DEFINE_ID.contains(DEFINE_ID)){ //产权业务
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "现在产权备案人");
                        }else if(MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)){
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "抵押备案人");
                        }else if (DEFINE_ID.equals("WP46") || DEFINE_ID.equals("WP44")){
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "预告备案人");
                        }else{
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "产权案备案人");
                        }
                        if (gysl>0){
                            resultSetFangchanGy.beforeFirst();
                            while (resultSetFangchanGy.next()){
                                if (resultSetFangchanGy.getString("gy_ren")!=null && resultSetFangchanGy.getString("gy_card")!=null) {
                                    gyqr = "," + resultSetFangchanGy.getString("gy_ren").trim() + "[" + resultSetFangchanGy.getString("gy_card").trim() + "]";
                                }
                                if (resultSetFangchanGy.getString("gy_ren")!=null && resultSetFangchanGy.getString("gy_card")==null) {
                                    gyqr = "," + resultSetFangchanGy.getString("gy_ren").trim();
                                }
                            }
                            cqr = cqr+gyqr;
                        }
                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, cqr);
                        businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);

                        if (DEAL_DEFINE_ID.contains(DEFINE_ID)){
                            if(!DEFINE_ID.equals("WP40")) {
                                if (fangChanResultSet.getString("y_cqr") != null && !fangChanResultSet.getString("y_cqr").equals("")) {
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "原产权备案人");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("y_cqr").trim()+"["+fangChanResultSet.getString("sl_ycqr_card").trim()+"]");
                                }
                            }
                        }
                        if (DEAL_DEFINE_ID.contains(DEFINE_ID)) {
                            if (fangChanResultSet.getString("y_chanquanzheng")!=null && !fangChanResultSet.getString("y_chanquanzheng").equals("")){
                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "原权证号");
                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("y_chanquanzheng"));
                            }
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "权证号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,number);
                        }

                        if(DEFINE_ID.contains("WP73")||DEFINE_ID.contains("WP74") || DEFINE_ID.equals("WP38")|| DEFINE_ID.equals("WP34")){//查封，查封解除
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "权证号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,number);

                        }

                        if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                || DEFINE_ID.equals("WP14") || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("BL2")){
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "权证号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("y_chanquanzheng"));

                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "他项权证号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, number);
                        }

                        //在建工程登记证明 在建抵押注销登记
                        if (DEFINE_ID.equals("WP18") || DEFINE_ID.equals("WP19") || DEFINE_ID.equals("WP21")) {
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "在建工程抵押证明号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, number);
                        }
                        //预告抵押
                        if (DEFINE_ID.equals("WP1") || DEFINE_ID.equals("WP4") || DEFINE_ID.equals("WP5") || DEFINE_ID.equals("WP8")) {
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "抵押预告登记证明号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, number);

                        }
                        if (DEFINE_ID.equals("WP44") || DEFINE_ID.equals("WP46")) {
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "预告登记证明号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, number);
                        }


                        businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("zuoluoxx"));

                        if (fangChanResultSet.getString("qiuhao")!=null && !fangChanResultSet.getString("qiuhao").equals("")) {
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT, fangChanResultSet.getString("qiuhao"));
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE, "丘");
                        }
                        if (fangChanResultSet.getString("zhuanghao")!=null && !fangChanResultSet.getString("zhuanghao").equals("")) {
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT, fangChanResultSet.getString("zhuanghao"));
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE, "幢");
                        }
                        if (fangChanResultSet.getString("fanghao")!=null && !fangChanResultSet.getString("fanghao").equals("")){
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT, fangChanResultSet.getString("fanghao"));
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE, "房");
                        }

                        if(MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)){//抵押业务
                            businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "抵押权人");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("diyaquanren"));

                        }
                        if (DEFINE_ID.equals("WP73")){
                            businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "查封法院");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("jb_cffy"));

                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "查封文号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("jb_cfwh"));

                        }

                        if (DEFINE_ID.equals("WP74")){
                            businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "解封法院");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_tx_bgq"));
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "解封文号");
                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_yingyezhizhao"));

                        }

                        boolean cancelen=false;
                        if (DEFINE_ID.equals("WP73") ||DEFINE_ID.equals("WP74") || DEFINE_ID.equals("WP18") || DEFINE_ID.equals("WP19") || DEFINE_ID.equals("WP21")){
                            cancelen=true;

                        }
                        sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED,SEARCH_KEY,DISPLAY) VALUES ");
                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(fangChanResultSet.getString("keycode"))
                                , Q.pm(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("keycode")+"-F"), Q.p(fangChanResultSet.getString("keycode")), Q.p(cancelen), Q.pm(key.getKey()),Q.pm(DescriptionDisplay.toStringValue(businessDisplay)) + ");"));
                        sqlWriter.newLine();










                        i++;
                        System.out.println(i + "/" + String.valueOf(recordCount));
                        sqlWriter.flush();


                    }
                }
            }





        } catch (Exception e) {
        System.out.println("keycode is errer-----"+fangChanResultSet.getString("keycode"));

        e.printStackTrace();
        return;
    }






    }




}
