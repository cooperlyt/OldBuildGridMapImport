package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;
import com.scoopit.weedfs.client.net.Result;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wxy on 2016-11-17.
 * 凤城倒库
 */
public class FcHouseOwnerRecord {

    private static final String BEGIN_DATE = "2016-09-30";

    private static final String OUT_PATH_FILE = "/FChouseOwnerRecord.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE = "/FCHouseOwnerError.sql";

    private static final String DB_FANG_CHAN_URL = "jdbc:jtds:sqlserver://127.0.0.1:1433/fang_chan_fc";

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

    private static ResultSet recordResultSet;

    private static String DEFINE_ID;


    private static Set<String> NO_EXCEPTION_DEFINE_ID = new HashSet<>();




    private static boolean isFirst;

    private static Set<String> DEAL_DEFINE_ID= new HashSet<>();

    private static Set<String> MORTGAEGE_DEFINE_ID = new HashSet<>();

    public static void main(String agr[]) throws SQLException {


        //不导入的业务编号
        //NO_EXCEPTION_DEFINE_ID.add("WP75");//集资建房初始登记
        NO_EXCEPTION_DEFINE_ID.add("WP76");//租赁登记
        NO_EXCEPTION_DEFINE_ID.add("WP77");//地役权登记
        NO_EXCEPTION_DEFINE_ID.add("WP78");//地役权变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP79");//地役权转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP80");//地役权注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP48");//预购商品房预告异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP49");//预购商品房预告异议注销登记

        NO_EXCEPTION_DEFINE_ID.add("WP36");//所有权异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP37");//所有权异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP38");//注销登记(灭籍)
        NO_EXCEPTION_DEFINE_ID.add("WP34");//声明作废
        NO_EXCEPTION_DEFINE_ID.add("WP39");//解除声明作废


        NO_EXCEPTION_DEFINE_ID.add("WP23");//他项权异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP24");//他项权异议注销登记
        //NO_EXCEPTION_DEFINE_ID.add("WP25");//他项权遗失补照
        NO_EXCEPTION_DEFINE_ID.add("WP26");//他项权预告登记证明补证
        NO_EXCEPTION_DEFINE_ID.add("WP27");//他项权在建工程遗失补证
        NO_EXCEPTION_DEFINE_ID.add("WP28");//在建工程抵押权异议登记
        NO_EXCEPTION_DEFINE_ID.add("WP29");//在建工程抵押权异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP29");//在建工程抵押权异议注销登记
        NO_EXCEPTION_DEFINE_ID.add("WP18");//在建工程抵押权设定登记
        NO_EXCEPTION_DEFINE_ID.add("WP19");//在建工程抵押权设定变更登记
        NO_EXCEPTION_DEFINE_ID.add("WP20");//在建工程抵押权设定转移登记

        NO_EXCEPTION_DEFINE_ID.add("WP73");//房屋查封登记
        NO_EXCEPTION_DEFINE_ID.add("WP74");//房屋查封解除登记
        NO_EXCEPTION_DEFINE_ID.add("WP81");//工程查封登记
        NO_EXCEPTION_DEFINE_ID.add("WP82");//工程查封解除登记
        NO_EXCEPTION_DEFINE_ID.add("WP88");//房屋续封登记
        NO_EXCEPTION_DEFINE_ID.add("WP11");//房屋所有权抵押转移登记


        NO_EXCEPTION_DEFINE_ID.add("WP14");//最高额抵押权确定登记
        NO_EXCEPTION_DEFINE_ID.add("WP16");//最高额抵押权设定转移登记
        NO_EXCEPTION_DEFINE_ID.add("WP7");//房屋抵押权预告转移登记

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

        //抵押登记

        MORTGAEGE_DEFINE_ID.add("WP9");// 房屋所有权抵押登记
        MORTGAEGE_DEFINE_ID.add("WP10");//房屋所有权抵押变更登记
        MORTGAEGE_DEFINE_ID.add("WP12");//房屋所有权抵押注销登记
        MORTGAEGE_DEFINE_ID.add("WP13");//最高额抵押登记
        MORTGAEGE_DEFINE_ID.add("WP15");//最高额抵押权设定变更登记
        MORTGAEGE_DEFINE_ID.add("WP17");//最高额抵押权设定注销登记
        MORTGAEGE_DEFINE_ID.add("WP22");//他项权更正登记
        MORTGAEGE_DEFINE_ID.add("WP25");//他项权遗失补照
        MORTGAEGE_DEFINE_ID.add("WP171");//抵押注销登记

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
            ownerRecordConnection = DriverManager.getConnection(DB_HOUSE_OWNER_RECORD_URL, "root", "dgsoft");
            statementOwnerRecord = ownerRecordConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            System.out.println("ownerRecordConnection successful");
        } catch (Exception e) {
            System.out.println("ownerRecordConnection is errer");
            e.printStackTrace();
            return;
        }



        try {

            fangChanResultSet = statementFangchan.executeQuery("select y.keycode,y.yw_houseid,y.yw_mingcheng,y.yw_mc_biaoshi,y.yw_jieduan,y.yw_jd_biaoshi,y.yw_cqr,y.yw_cqr_card_type,y.yw_cqr_card, " +
                    "y.yw_cqr_dianhua,y.yw_zuoluo,sl_kaifagongsi,sl_ycqr,sl_ycqr_card_type,sl_ycqr_card,sl_ycqr_dianhua,sl_ycq_zheng,ch_qiuhao, " +
                    "ch_zhuanghao,ch_fanghao,ch_jiegou,ch_laiyuan,ch_shejiyongtu,ch_mj_jianzhu,ch_jianzhuNianFen,ch_zongceng,ch_ceng,ch_chanbie,sf_goufangkuan, " +
                    "sf_goufangkuan,sf_pinggujia,sz_taxiangzheng,sz_zhenghao,sz_zjgczh,sz_ygdjh,sl_str6,sz_beizhu,sl_beizhu,sl_date,fs_date,sl_hth,useType " +
                    "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
                    "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                    "and y.keycode=sz.keycode and  y.keycode=pg.keycode " +
                    "and yw_mingcheng <>'租赁登记' and yw_jieduan not like '%受理%' and yw_jieduan not like '%登记%' " +
                    "and yw_jieduan not like '%审批%' and yw_jieduan not like '%复审' and yw_jieduan not like '%评估%' " +
                    "and yw_jieduan not like '%测绘%' and( y.keycode like '2017%' " +
                    "or y.keycode like '2016%' or y.keycode like '2015%') " +
                    "and  y.yw_mc_biaoshi<>'13' and y.yw_mc_biaoshi<>'12' and y.yw_mc_biaoshi<>'14' and y.yw_mc_biaoshi<>'15' "+
                    "and y.yw_mc_biaoshi<>'199' and y.yw_mc_biaoshi<>'810' and y.yw_mc_biaoshi<>'53' and y.yw_mc_biaoshi<>'54' "+
                    "and y.yw_mc_biaoshi<>'81' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                    "and y.yw_mc_biaoshi<>'399' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                    //"and (y.yw_houseid='101003' or y.yw_houseid='42844' or y.keycode='200706110010') " +
                    "order by yw_houseid,y.keycode DESC " );



            fangChanResultSet.last();
            int recordCount = fangChanResultSet.getRow();
            System.out.println("count-"+recordCount);
            sqlWriter.newLine();

            if (recordCount>0){
                fangChanResultSet.beforeFirst();
                int i=0;
                String oldhouseid = "";
                String stratHouseId = "",afterHouseId="";
                        while (fangChanResultSet.next()) {

                            ResultSet resultSetOwnerRecordCh = statementOwnerRecord.executeQuery("SELECT * FROM OWNER_BUSINESS WHERE ID='" + fangChanResultSet.getString("keycode") + "'");
                            resultSetOwnerRecordCh.last();
                            if (resultSetOwnerRecordCh.getRow() <= 0) {//说明HOUSE_OWNER_RECORD 没有，需要从fangchan_fc导入

                                //OWNER_BUSINESS
                                DEFINE_ID = Q.changeDefineID(fangChanResultSet.getInt("yw_mc_biaoshi"));
                                sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                                        " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), "0", "'BIZ_IMPORT'", Q.p(fangChanResultSet.getString("sl_beizhu"))
                                        , "'COMPLETE'", Q.pm(fangChanResultSet.getString("yw_mingcheng")), Q.pm(DEFINE_ID), "0", "Null", Q.p(fangChanResultSet.getTimestamp("sl_date"))
                                        , Q.p(fangChanResultSet.getTimestamp("sl_date")), "Null", Q.p(fangChanResultSet.getTimestamp("fs_date")), Q.p(fangChanResultSet.getTimestamp("fs_date")), "False", "'NORMAL_BIZ'") + ");");
                                sqlWriter.newLine();

                                //CARD 业务证书号,
                                String cardType = "OTHER_CARD";
                                String number = null;
                                if (DEAL_DEFINE_ID.contains(DEFINE_ID)) {
                                    cardType = "OWNER_RSHIP";
                                    number = fangChanResultSet.getString("sz_zhenghao");
                                    if (!DEFINE_ID.equals("WP30") && !DEFINE_ID.equals("WP31") && !DEFINE_ID.equals("WP75")) {
                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode"))+"-1", Q.pm("OLD_OWNER_RSHIP"),
                                                Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                "NUll" + ");"));
                                        sqlWriter.newLine();
                                    }
                                }

                                if (MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)) {


                                    //房屋所有权证号
                                    if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                     || DEFINE_ID.equals("WP171") || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("WP25")){
                                        cardType = "MORTGAGE";
                                        number = fangChanResultSet.getString("sz_taxiangzheng");
                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode"))+"-1", Q.pm("OWNER_RSHIP"),
                                                Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                "NUll" + ");"));
                                        sqlWriter.newLine();
                                    }
                                    // 他项权证号 注销登记
                                    if(DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP171") ){
                                        cardType = "MORTGAGE";
                                        number = fangChanResultSet.getString("sl_str6");

                                    }
                                    // 原他项权证号 变更
                                    if(DEFINE_ID.equals("WP10")){

                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode"))+"-2", Q.pm("OLD_MORTGAGE"),
                                                Q.pm1(fangChanResultSet.getString("sl_str6")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                "NUll" + ");"));
                                        sqlWriter.newLine();

                                    }
                                    //在建工程登记证明
                                    if (DEFINE_ID.equals("WP18") || DEFINE_ID.equals("WP19")) {
                                        cardType = "PROJECT_MORTGAGE";
                                        number = fangChanResultSet.getString("sz_zjgczh");
                                        if(DEFINE_ID.equals("WP19")){
                                            sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                            sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode"))+"-2", Q.pm("OLD_PROJECT_MORTGAGE"),
                                                    Q.pm1(fangChanResultSet.getString("sl_str6")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                    "NUll" + ");"));
                                            sqlWriter.newLine();
                                        }

                                    }
                                    //在建抵押注销登记
                                    if (DEFINE_ID.equals("WP21")){
                                        cardType = "PROJECT_MORTGAGE";
                                        number = fangChanResultSet.getString("sz_taxiangzheng");

                                    }
                                    //预告抵押
                                    if (DEFINE_ID.equals("WP1")) {
                                        cardType = "NOTICE_MORTGAGE";
                                        number = fangChanResultSet.getString("sz_ygdjh");

                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode"))+"-1", Q.pm("NOTICE"),
                                                Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                "NUll" + ");"));
                                        sqlWriter.newLine();


                                    }
                                    if (DEFINE_ID.equals("WP4")) {
                                        cardType = "NOTICE_MORTGAGE";
                                        number = fangChanResultSet.getString("sl_ycq_zheng");
                                    }

                                }

                                if (DEFINE_ID.equals("WP44")) {
                                    cardType = "NOTICE";
                                    number = fangChanResultSet.getString("sz_ygdjh");
                                }

                                if (DEFINE_ID.equals("WP46") || DEFINE_ID.equals("WP44")) {
                                    sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                    sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode"))+"-1", Q.pm("CONTRACT_NO"),
                                            Q.pm1(fangChanResultSet.getString("sl_hth")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                            "NUll" + ");"));
                                    sqlWriter.newLine();

                                }

                                sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(cardType),
                                        Q.pm1(number), Q.pm(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("sz_beizhu")),
                                        "NUll" + ");"));
                                sqlWriter.newLine();





                                // house
                                String houseid = fangChanResultSet.getString("yw_houseid");
                                if (houseid == null || houseid.trim().equals("")) {
                                    houseid = "";
                                }
                                if (oldhouseid.equals("") || !oldhouseid.equals(houseid)) {
                                    stratHouseId = "";
                                    if (!houseid.equals("")) {
                                        ResultSet resultSetOwnerRecord = statementOwnerRecord.executeQuery("SELECT * FROM HOUSE_RECORD WHERE HOUSE_CODE='" + houseid + "'");
                                        if (resultSetOwnerRecord.next()) {
                                            stratHouseId = resultSetOwnerRecord.getString("HOUSE");

                                        }
                                    }
                                }
                                if (stratHouseId.equals("")) {//库里没有hosued需要自动生成一个
                                    stratHouseId = fangChanResultSet.getString("keycode") + "-s";
                                    System.out.println("stratHouseId-" + stratHouseId);
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

                                    sqlWriter.write("(" + Q.v(Q.p(stratHouseId), Q.pm1(fangChanResultSet.getString("ch_fanghao"))
                                            , "Null", Q.pm1(fangChanResultSet.getString("ch_ceng"))
                                            , Q.pm(fangChanResultSet.getBigDecimal("ch_mj_jianzhu")), "0"
                                            , "0", "0", "0", "0"
                                            , Q.changeHouseTypeFc(fangChanResultSet.getInt("yw_mc_biaoshi")), Q.changeDesignUseType(fangChanResultSet.getString("useType"))
                                            , Q.changeStructureFc(fangChanResultSet.getString("ch_jiegou"))
                                            , Q.pm1(fangChanResultSet.getString("yw_zuoluo")), Q.pm(fangChanResultSet.getString("sl_date"))
                                            , Q.pm(fangChanResultSet.getString("keycode")), "False", "'未知'", "'未知'", Q.pm1(fangChanResultSet.getString("ch_qiuhao"))
                                            , Q.pm1(fangChanResultSet.getString("ch_zhuanghao")), "'未知'", Q.pm1(fangChanResultSet.getString("ch_zongceng"))
                                            , Q.pm1(fangChanResultSet.getString("ch_zongceng")), "0", "Null", "'未知'", "'未知'"
                                            , Q.pm1(fangChanResultSet.getString("ch_jianzhuNianFen"))
                                            , "'未知'", Q.pm1(fangChanResultSet.getString("sl_kaifagongsi")), "'未知'"
                                            , "'未知'", "'未知'", "'未知'", "'未知'", "Null", "Null", "Null", "Null", "Null", Q.pm1(fangChanResultSet.getString("ch_shejiyongtu")), "''" + ");"));
                                    sqlWriter.newLine();
                                }

                                afterHouseId = fangChanResultSet.getString("keycode");
                                ResultSet resultSetBusnessHouse = statementOwnerRecord.executeQuery("SELECT * FROM HOUSE WHERE  HOUSE_CODE='" + houseid + "' AND ID NOT LIKE '%-t%'");
                                if (resultSetBusnessHouse.next()) {//有HOUSEID
                                    resultSetBusnessHouse.last();
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
                                    sqlWriter.write("(" + Q.v(Q.p(afterHouseId), Q.pm1(resultSetBusnessHouse.getString("HOUSE_ORDER"))
                                            , Q.p(resultSetBusnessHouse.getString("HOUSE_UNIT_NAME")), Q.pm1(resultSetBusnessHouse.getString("IN_FLOOR_NAME"))
                                            , Q.pm(resultSetBusnessHouse.getBigDecimal("HOUSE_AREA")), Q.pm(resultSetBusnessHouse.getBigDecimal("USE_AREA"))
                                            , Q.pm(resultSetBusnessHouse.getBigDecimal("COMM_AREA")), Q.pm(resultSetBusnessHouse.getBigDecimal("SHINE_AREA"))
                                            , Q.pm(resultSetBusnessHouse.getBigDecimal("LOFT_AREA")), Q.pm(resultSetBusnessHouse.getBigDecimal("COMM_PARAM"))
                                            , Q.changeHouseTypeFc(fangChanResultSet.getInt("yw_mc_biaoshi")), Q.changeDesignUseType(resultSetBusnessHouse.getString("USE_TYPE"))
                                            , Q.changeStructureFc(fangChanResultSet.getString("ch_jiegou")), Q.pm1(fangChanResultSet.getString("yw_zuoluo"))
                                            , Q.pm(resultSetBusnessHouse.getTimestamp("MAP_TIME")), Q.pm(houseid)
                                            , "False", Q.pm(resultSetBusnessHouse.getString("BUILD_CODE")), Q.pm(resultSetBusnessHouse.getString("MAP_NUMBER"))
                                            , Q.pm(resultSetBusnessHouse.getString("BLOCK_NO")), Q.pm1(resultSetBusnessHouse.getString("BUILD_NO"))
                                            , Q.pm(resultSetBusnessHouse.getString("DOOR_NO")), Q.pm1(fangChanResultSet.getString("ch_zongceng"))
                                            , Q.pm1(fangChanResultSet.getString("ch_zongceng")), "0", "Null"
                                            , Q.pm1(resultSetBusnessHouse.getString("PROJECT_CODE")), Q.pm1(resultSetBusnessHouse.getString("PROJECT_NAME"))
                                            , Q.p(resultSetBusnessHouse.getString("COMPLETE_DATE")), Q.pm1(resultSetBusnessHouse.getString("DEVELOPER_CODE"))
                                            , Q.pm1(resultSetBusnessHouse.getString("DEVELOPER_NAME"))
                                            , Q.pm1(resultSetBusnessHouse.getString("SECTION_CODE")), Q.pm1(resultSetBusnessHouse.getString("SECTION_NAME"))
                                            , Q.pm1(resultSetBusnessHouse.getString("DISTRICT_CODE")), Q.pm1(resultSetBusnessHouse.getString("DISTRICT_NAME"))
                                            , Q.pm1(resultSetBusnessHouse.getString("BUILD_NAME")), Q.pm1(resultSetBusnessHouse.getString("BUILD_DEVELOPER_NUMBER"))
                                            , "Null", "Null"
                                            , "Null", "Null", Q.pm1(fangChanResultSet.getString("ch_shejiyongtu")), "''" + ");"));
                                    sqlWriter.newLine();
                                } else {//无HOUSEID
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
                                    sqlWriter.write("(" + Q.v(Q.p(afterHouseId), Q.pm1(fangChanResultSet.getString("ch_fanghao"))
                                            , "Null", Q.pm1(fangChanResultSet.getString("ch_ceng"))
                                            , Q.pm(fangChanResultSet.getBigDecimal("ch_mj_jianzhu")), "0"
                                            , "0", "0", "0", "0"
                                            , Q.changeHouseTypeFc(fangChanResultSet.getInt("yw_mc_biaoshi")), Q.changeDesignUseType(fangChanResultSet.getString("useType"))
                                            , Q.changeStructureFc(fangChanResultSet.getString("ch_jiegou"))
                                            , Q.pm1(fangChanResultSet.getString("yw_zuoluo")), Q.pm(fangChanResultSet.getString("sl_date"))
                                            , Q.pm(fangChanResultSet.getString("keycode")), "False", "'未知'", "'未知'", Q.pm1(fangChanResultSet.getString("ch_qiuhao"))
                                            , Q.pm1(fangChanResultSet.getString("ch_zhuanghao")), "'未知'", Q.pm1(fangChanResultSet.getString("ch_zongceng"))
                                            , Q.pm1(fangChanResultSet.getString("ch_zongceng")), "0", "Null", "'未知'", "'未知'"
                                            , Q.pm1(fangChanResultSet.getString("ch_jianzhuNianFen"))
                                            , "'未知'", Q.pm1(fangChanResultSet.getString("sl_kaifagongsi")), "'未知'"
                                            , "'未知'", "'未知'", "'未知'", "'未知'", "Null", "Null", "Null", "Null", "Null", Q.pm1(fangChanResultSet.getString("ch_shejiyongtu")), "''" + ");"));
                                    sqlWriter.newLine();
                                }

                                //--- BUSINESS_HOUSE
                                String no;
                                if (houseid != null && !houseid.equals("")) {
                                    no = houseid;
                                } else {
                                    no = fangChanResultSet.getString("keycode");
                                }
                                sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED,SEARCH_KEY,DISPLAY) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(no)
                                        , Q.pm(fangChanResultSet.getString("keycode")), Q.p(stratHouseId), Q.p(afterHouseId), "True", "''", "''" + ");"));
                                sqlWriter.newLine();

                                // 房屋状态 ===ADD_HOUSE_STATUS 交易备案
                                String lastState = null;
                                if (DEAL_DEFINE_ID.contains(DEFINE_ID)) {
                                    sqlWriter.write("INSERT ADD_HOUSE_STATUS (ID, BUSINESS, STATUS, IS_REMOVE) VALUES  ");

                                    sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("keycode"))
                                            , Q.p("OWNERED"), Q.p(false) + ");"));

                                    sqlWriter.newLine();
                                    lastState = "OWNERED";
                                }


                                i++;
                                System.out.println(i + "/" + String.valueOf(recordCount));
                                sqlWriter.flush();

                                oldhouseid = fangChanResultSet.getString("yw_houseid");
                                if (oldhouseid == null || oldhouseid.trim().equals("")) {
                                    oldhouseid = "";
                                }
                                stratHouseId = afterHouseId;
                            }
                        }


            }


        } catch (Exception e) {
            System.out.println("keycode is errer-----"+fangChanResultSet.getString("keycode"));
            System.out.println("yw_houseid is errer-----"+fangChanResultSet.getString("yw_houseid"));
            e.printStackTrace();
            return;
        }

    }







}
