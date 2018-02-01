package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;
import com.scoopit.weedfs.client.net.Result;

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
 * 凤城倒库
 */
public class FcHouseOwnerRecord {

    private static final String BEGIN_DATE = "2016-09-30";

    private static final String OUT_PATH_FILE = "/FChouseOwnerRecord.sql";

    private static final String OUT_PATH_HouseOwnerError_FILE = "/FCHouseOwnerError.sql";

    //private static final String DB_FANG_CHAN_URL = "jdbc:jtds:sqlserver://192.168.1.2:1433/fang_chan";
    private static final String DB_FANG_CHAN_URL = "jdbc:jtds:sqlserver://127.0.0.1:1433/fang_chan_fc";

    private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    //private static final String DB_HOUSE_OWNER_RECORD_URL="jdbc:mysql://192.168.1.7:3306/HOUSE_OWNER_RECORD";

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
//        NO_EXCEPTION_DEFINE_ID.add("WP81");//工程查封登记
//        NO_EXCEPTION_DEFINE_ID.add("WP82");//工程查封解除登记
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

        //总层数需要手动处理，结构，设计用途
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

//            fangChanResultSet = statementFangchan.executeQuery("select y.keycode,y.yw_houseid,y.yw_mingcheng,y.yw_mc_biaoshi,y.yw_jieduan,y.yw_jd_biaoshi,y.yw_cqr,y.yw_cqr_card_type,y.yw_cqr_card, " +
//                    "y.yw_cqr_dianhua,y.yw_zuoluo,sl_kaifagongsi,sl_ycqr,sl_ycqr_card_type,sl_ycqr_card,sl_ycqr_dianhua,sl_ycq_zheng,ch_qiuhao, " +
//                    "ch_zhuanghao,ch_fanghao,ch_jiegou,ch_laiyuan,ch_shejiyongtu,ch_mj_jianzhu,ch_jianzhuNianFen,ch_zongceng,ch_ceng,ch_chanbie,sf_goufangkuan,sf_chengjiaojia, " +
//                    "sf_goufangkuan,sf_pinggujia,sz_taxiangzheng,sz_zhenghao,sz_zjgczh,sz_ygdjh,sl_str6,sz_beizhu,sl_beizhu,sl_date,fs_date,sl_hth,useType,sz_gongyouqingkuang,sl_taxiangquanren " +
//                    "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
//                    "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
//                    "and y.keycode=sz.keycode and  y.keycode=pg.keycode " +
//                    "and yw_mingcheng <>'租赁登记' and yw_jieduan not like '%受理%' and yw_jieduan not like '%登记%' " +
//                    "and yw_jieduan not like '%复审' and yw_jieduan not like '%评估%' " +
//                    "and yw_jieduan not like '%测绘%' and( y.keycode like '2017%' " +
//                    "or y.keycode like '2016%') " +
//
//                    "and  y.yw_mc_biaoshi<>'13' and y.yw_mc_biaoshi<>'12' and y.yw_mc_biaoshi<>'14' and y.yw_mc_biaoshi<>'15' "+
//                    "and y.yw_mc_biaoshi<>'199' and y.yw_mc_biaoshi<>'810' and y.yw_mc_biaoshi<>'53' and y.yw_mc_biaoshi<>'54' "+
//                    "and y.yw_mc_biaoshi<>'81' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
//                    "and y.yw_mc_biaoshi<>'399' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
//                   // "and (y.yw_houseid='67559') " +
//                  //  "and (y.keycode='201706280002') " +
//                    "order by yw_houseid,y.keycode " );

            fangChanResultSet = statementFangchan.executeQuery("select y.keycode,y.yw_houseid,y.yw_mingcheng,y.yw_mc_biaoshi,y.yw_jieduan,y.yw_jd_biaoshi,y.yw_cqr,y.yw_cqr_card_type,y.yw_cqr_card, " +
                    "y.yw_cqr_dianhua,y.yw_zuoluo,sl_kaifagongsi,sl_ycqr,sl_ycqr_card_type,sl_ycqr_card,sl_ycqr_dianhua,sl_ycq_zheng,ch_qiuhao, " +
                    "ch_zhuanghao,ch_fanghao,ch_jiegou,ch_laiyuan,ch_shejiyongtu,ch_mj_jianzhu,ch_jianzhuNianFen,ch_zongceng,ch_ceng,ch_chanbie,sf_goufangkuan,sf_chengjiaojia, " +
                    "sf_goufangkuan,sf_pinggujia,sz_taxiangzheng,sz_zhenghao,sz_zjgczh,sz_ygdjh,sl_str6,sz_beizhu,sl_beizhu,sl_date,fs_date,sl_hth,useType,sz_gongyouqingkuang,sl_taxiangquanren " +
                    "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
                    "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                    "and y.keycode=sz.keycode and  y.keycode=pg.keycode " +
                    "and yw_mingcheng <>'租赁登记' and yw_jieduan not like '%受理%' and yw_jieduan not like '%登记%' " +
                    "and yw_jieduan not like '%复审' and yw_jieduan not like '%评估%' " +
                    "and yw_jieduan not like '%测绘%' " +
                    //"and  (y.yw_mc_biaoshi='811' or y.yw_mc_biaoshi='34' or y.yw_mc_biaoshi='35' or y.yw_mc_biaoshi='31' or y.yw_mc_biaoshi='32' or y.yw_mc_biaoshi='300' or y.yw_mc_biaoshi='201') "+
                    "and  y.yw_mc_biaoshi<>'13' and y.yw_mc_biaoshi<>'12' and y.yw_mc_biaoshi<>'14' and y.yw_mc_biaoshi<>'15' "+
                    "and y.yw_mc_biaoshi<>'199' and y.yw_mc_biaoshi<>'810' and y.yw_mc_biaoshi<>'53' and y.yw_mc_biaoshi<>'54' "+
                    "and y.yw_mc_biaoshi<>'81' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                    "and y.yw_mc_biaoshi<>'399' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                    //"and (y.yw_houseid='67559') " +
                    "and (y.keycode='201508170001') " +
                    "order by yw_houseid,y.keycode" );


            System.out.println("select y.keycode,y.yw_houseid,y.yw_mingcheng,y.yw_mc_biaoshi,y.yw_jieduan,y.yw_jd_biaoshi,y.yw_cqr,y.yw_cqr_card_type,y.yw_cqr_card, " +
                    "y.yw_cqr_dianhua,y.yw_zuoluo,sl_kaifagongsi,sl_ycqr,sl_ycqr_card_type,sl_ycqr_card,sl_ycqr_dianhua,sl_ycq_zheng,ch_qiuhao, " +
                    "ch_zhuanghao,ch_fanghao,ch_jiegou,ch_laiyuan,ch_shejiyongtu,ch_mj_jianzhu,ch_jianzhuNianFen,ch_zongceng,ch_ceng,ch_chanbie,sf_goufangkuan,sf_chengjiaojia, " +
                    "sf_goufangkuan,sf_pinggujia,sz_taxiangzheng,sz_zhenghao,sz_zjgczh,sz_ygdjh,sl_str6,sz_beizhu,sl_beizhu,sl_date,fs_date,sl_hth,useType,sz_gongyouqingkuang,sl_taxiangquanren " +
                    "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
                    "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                    "and y.keycode=sz.keycode and  y.keycode=pg.keycode " +
                    "and yw_mingcheng <>'租赁登记' and yw_jieduan not like '%受理%' and yw_jieduan not like '%登记%' " +
                    "and yw_jieduan not like '%复审' and yw_jieduan not like '%评估%' " +
                    "and yw_jieduan not like '%测绘%' " +
                    //"and  (y.yw_mc_biaoshi='811' or y.yw_mc_biaoshi='34' or y.yw_mc_biaoshi='35' or y.yw_mc_biaoshi='31' or y.yw_mc_biaoshi='32' or y.yw_mc_biaoshi='300' or y.yw_mc_biaoshi='201') "+
                    "and  y.yw_mc_biaoshi<>'13' and y.yw_mc_biaoshi<>'12' and y.yw_mc_biaoshi<>'14' and y.yw_mc_biaoshi<>'15' "+
                    "and y.yw_mc_biaoshi<>'199' and y.yw_mc_biaoshi<>'810' and y.yw_mc_biaoshi<>'53' and y.yw_mc_biaoshi<>'54' "+
                    "and y.yw_mc_biaoshi<>'81' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                    "and y.yw_mc_biaoshi<>'399' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                    //"and (y.yw_houseid='67559') " +
                    "and (y.keycode='2015110120090') " +
                    "order by yw_houseid,y.keycode");


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
                                        , Q.p(fangChanResultSet.getTimestamp("sl_date")), "Null", Q.p(fangChanResultSet.getTimestamp("fs_date")), Q.p(fangChanResultSet.getTimestamp("fs_date")), "True", "'NORMAL_BIZ'") + ");");
                                sqlWriter.newLine();

                                //CARD 业务证书号,
                                String cardType = "OTHER_CARD";
                                String number = null;
                                if (DEAL_DEFINE_ID.contains(DEFINE_ID)) {
                                    cardType = "OWNER_RSHIP";
                                    number = fangChanResultSet.getString("sz_zhenghao");
                                    if (!DEFINE_ID.equals("WP30") && !DEFINE_ID.equals("WP31") && !DEFINE_ID.equals("WP75")) {
                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("OLD_OWNER_RSHIP"),
                                                Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                "NUll" + ");"));
                                        sqlWriter.newLine();
                                    }
                                }
                                if(DEAL_DEFINE_ID.contains("WP73")||DEAL_DEFINE_ID.contains("WP74")){//查封，查封解除

                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("OWNER_RSHIP"),
                                                Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                "NUll" + ");"));
                                        sqlWriter.newLine();

                                }

                                if (DEFINE_ID.equals("WP38")|| DEFINE_ID.equals("WP34")){

                                    sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                    sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("OWNER_RSHIP"),
                                            Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                            "NUll" + ");"));
                                    sqlWriter.newLine();

                                }


                                if (MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)) {


                                    //房屋所有权证号
                                    if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                     || DEFINE_ID.equals("OM2") || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("WP25")){
                                        cardType = "MORTGAGE";
                                        number = fangChanResultSet.getString("sz_taxiangzheng");
                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("OWNER_RSHIP"),
                                                Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                                "NUll" + ");"));
                                        sqlWriter.newLine();
                                    }



                                    // 他项权证号 注销登记
                                    if(DEFINE_ID.equals("WP12") || DEFINE_ID.equals("OM2") ){
                                        cardType = "MORTGAGE";
                                        number = fangChanResultSet.getString("sl_str6");

                                    }
                                    // 原他项权证号 变更
                                    if(DEFINE_ID.equals("WP10")){

                                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-2"), Q.pm("OLD_MORTGAGE"),
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
                                            sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-2"), Q.pm("OLD_PROJECT_MORTGAGE"),
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
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("NOTICE"),
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
                                    sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"), Q.pm("CONTRACT_NO"),
                                            Q.pm1(fangChanResultSet.getString("sl_hth")), Q.pm(fangChanResultSet.getString("keycode")),"NULL",
                                            "NUll" + ");"));
                                    sqlWriter.newLine();

                                }

                                if (!DEFINE_ID.equals("WP40")) {
                                    sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                                    sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(cardType),
                                            Q.pm1(number), Q.pm(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("sz_beizhu")),
                                            "NUll" + ");"));
                                    sqlWriter.newLine();
                                }





                                // house
                                String houseCode = fangChanResultSet.getString("yw_houseid");
                                if (houseCode == null || houseCode.trim().equals("") || houseCode.equals("0")) {
                                    houseCode = fangChanResultSet.getString("keycode");
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

                                sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")+"-F"), Q.pm1(fangChanResultSet.getString("ch_fanghao"))
                                        , "Null", Q.pm1(fangChanResultSet.getString("ch_ceng"))
                                        , Q.pm(fangChanResultSet.getBigDecimal("ch_mj_jianzhu")), "0"
                                        , "0", "0", "0", "0"
                                        , Q.changeHouseTypeFc(fangChanResultSet.getInt("yw_mc_biaoshi")), Q.changeDesignUseType(fangChanResultSet.getString("useType"))
                                        , Q.changeStructureFc(fangChanResultSet.getString("ch_jiegou"))
                                        , Q.pm1(fangChanResultSet.getString("yw_zuoluo")), Q.pm(fangChanResultSet.getString("sl_date"))
                                        , Q.pm(houseCode), "False", "'未知'", "'未知'", Q.pm1(fangChanResultSet.getString("ch_qiuhao"))
                                        , Q.pm1(fangChanResultSet.getString("ch_zhuanghao")), "'未知'", Q.pmZc(fangChanResultSet.getString("ch_zongceng"))
                                        , Q.pmZc(fangChanResultSet.getString("ch_zongceng")), "0", "Null", "'未知'", "'未知'"
                                        , Q.pm1(fangChanResultSet.getString("ch_jianzhuNianFen"))
                                        , "'未知'", Q.pm1(fangChanResultSet.getString("sl_kaifagongsi")), "'未知'"
                                        , "'未知'", "'未知'", "'未知'", "'未知'", "Null", "Null", "Null", "Null", "Null", Q.pm1(fangChanResultSet.getString("ch_shejiyongtu")), "''" + ");"));
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
                                sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), Q.pm1(fangChanResultSet.getString("ch_fanghao"))
                                        , "Null", Q.pm1(fangChanResultSet.getString("ch_ceng"))
                                        , Q.pm(fangChanResultSet.getBigDecimal("ch_mj_jianzhu")), "0"
                                        , "0", "0", "0", "0"
                                        , Q.changeHouseTypeFc(fangChanResultSet.getInt("yw_mc_biaoshi")), Q.changeDesignUseType(fangChanResultSet.getString("useType"))
                                        , Q.changeStructureFc(fangChanResultSet.getString("ch_jiegou"))
                                        , Q.pm1(fangChanResultSet.getString("yw_zuoluo")), Q.pm(fangChanResultSet.getString("sl_date"))
                                        , Q.pm(houseCode), "False", "'未知'", "'未知'", Q.pm1(fangChanResultSet.getString("ch_qiuhao"))

                                        , Q.pm1(fangChanResultSet.getString("ch_zhuanghao")), "'未知'", Q.pmZc(fangChanResultSet.getString("ch_zongceng"))
                                        , Q.pmZc(fangChanResultSet.getString("ch_zongceng")), "0", "Null", "'未知'", "'未知'"
                                        , Q.pm1(fangChanResultSet.getString("ch_jianzhuNianFen"))
                                        , "'未知'", Q.pm1(fangChanResultSet.getString("sl_kaifagongsi")), "'未知'"
                                        , "'未知'", "'未知'", "'未知'", "'未知'", "Null", Q.changePoolMemoFc(fangChanResultSet.getString("sz_gongyouqingkuang")), "Null", "Null", "Null", Q.pm1(fangChanResultSet.getString("ch_shejiyongtu")), "''" + ");"));
                                sqlWriter.newLine();


                                //--- BUSINESS_HOUSE



                                KeyGeneratorHelper key = new KeyGeneratorHelper();
                                key.addWord(fangChanResultSet.getString("yw_cqr")); //产权人
                                key.addWord(fangChanResultSet.getString("yw_cqr_card"));//产权人身份证号

                                ResultSet resultSetFangchanGy= statementFangchanCH.executeQuery("SELECT * FROM c_gongyou WHERE keycode='" + fangChanResultSet.getString("keycode") + "'");
                                resultSetFangchanGy.last();
                                int gysl=resultSetFangchanGy.getRow();
                                if (gysl>0){
                                    resultSetFangchanGy.beforeFirst();
                                    while(resultSetFangchanGy.next()){
                                        if(resultSetFangchanGy.getString("gy_ren")!=null) {
                                            key.addWord((resultSetFangchanGy.getString("gy_ren").trim())); //共有权人
                                        }
                                        if (resultSetFangchanGy.getString("gy_card")!=null) {
                                            key.addWord(resultSetFangchanGy.getString("gy_card").trim()); //身份证号
                                        }
                                    }
                                }
                                if (DEAL_DEFINE_ID.contains(DEFINE_ID)){
                                    if(!DEFINE_ID.equals("WP40")) {
                                        if (fangChanResultSet.getString("sl_ycqr") != null && !fangChanResultSet.getString("sl_ycqr").equals("")) {
                                            key.addWord((fangChanResultSet.getString("sl_ycqr").trim())); //原产权人

                                        }
                                    }else {
                                        if (fangChanResultSet.getString("sl_kaifagongsi") != null && !fangChanResultSet.getString("sl_kaifagongsi").equals("")) {
                                            key.addWord((fangChanResultSet.getString("sl_kaifagongsi").trim())); //原产权人

                                        }
                                    }

                                    if (fangChanResultSet.getString("sl_ycqr_card")!=null && !fangChanResultSet.getString("sl_ycqr_card").equals("")){
                                        key.addWord((fangChanResultSet.getString("sl_ycqr_card").trim())); //原产权人身份证号
                                    }
                                    if (fangChanResultSet.getString("sl_ycq_zheng")!=null && !fangChanResultSet.getString("sl_ycq_zheng").equals("")){
                                        key.addWord((fangChanResultSet.getString("sl_ycq_zheng").trim())); //原产权证号
                                    }
                                }

                                if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12")
                                        || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                        || DEFINE_ID.equals("OM2") || DEFINE_ID.equals("WP17")
                                        || DEFINE_ID.equals("WP25") || DEFINE_ID.equals("WP73")
                                        || DEFINE_ID.equals("WP74") || DEFINE_ID.equals("WP1") || DEFINE_ID.equals("WP4")){//抵押查封 把产权证号导入进去
                                    if (fangChanResultSet.getString("sl_ycq_zheng")!=null && !fangChanResultSet.getString("sl_ycq_zheng").equals("")){
                                        key.addWord((fangChanResultSet.getString("sl_ycq_zheng").trim())); //现产权证号,预抵的预告登记号
                                    }
                                }

                                if(DEFINE_ID.equals("WP12") || DEFINE_ID.equals("OM2") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP19")){
                                    if (fangChanResultSet.getString("sl_str6")!=null && !fangChanResultSet.getString("sl_str6").equals("")){
                                        key.addWord((fangChanResultSet.getString("sl_str6").trim()));
                                    }

                                }
                                if (DEFINE_ID.equals("WP21")){
                                    if (fangChanResultSet.getString("sz_taxiangzheng")!=null && !fangChanResultSet.getString("sz_taxiangzheng").equals("")){
                                        key.addWord((fangChanResultSet.getString("sz_taxiangzheng").trim()));
                                    }
                                }




                                key.addWord(houseCode);//房屋编号 业务编号
                                key.addWord(number);//权证号
                                key.addWord(fangChanResultSet.getString("yw_zuoluo"));//房屋坐落

                                //System.out.println(key.getKey());
                                DescriptionDisplay businessDisplay = new DescriptionDisplay();
                                businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "房屋编号");
                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,houseCode);
                                String cqr = fangChanResultSet.getString("yw_cqr")+"["+fangChanResultSet.getString("yw_cqr_card")+"]",gyqr="";
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
                                        if (fangChanResultSet.getString("sl_ycqr") != null && !fangChanResultSet.getString("sl_ycqr").equals("")) {
                                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "原产权备案人");
                                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_ycqr").trim());
                                        }
                                    }else {
                                        if (fangChanResultSet.getString("sl_kaifagongsi") != null && !fangChanResultSet.getString("sl_kaifagongsi").equals("")) {
                                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "开发商");
                                            businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_kaifagongsi").trim());

                                        }
                                    }


                                }




                                if (DEAL_DEFINE_ID.contains(DEFINE_ID)) {

                                    if (fangChanResultSet.getString("sl_ycq_zheng")!=null && !fangChanResultSet.getString("sl_ycq_zheng").equals("")){
                                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "原权证号");
                                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_ycq_zheng"));
                                    }

                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "权证号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,number);




                                }
                                if(DEAL_DEFINE_ID.contains("WP73")||DEAL_DEFINE_ID.contains("WP74") || DEFINE_ID.equals("WP38")|| DEFINE_ID.equals("WP34")){//查封，查封解除
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "权证号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_ycq_zheng"));

                                }
                                if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                        || DEFINE_ID.equals("OM2") || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("WP25")){
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "权证号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_ycq_zheng"));

                                    if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10")  || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                            || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("WP25")) {
                                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "他项权证号");
                                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sz_taxiangzheng"));
                                    }
                                    if(DEFINE_ID.equals("WP12") || DEFINE_ID.equals("OM2") ){
                                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "他项权证号");
                                        businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sl_str6"));
                                    }
                                }

                                //在建工程登记证明
                                if (DEFINE_ID.equals("WP18") || DEFINE_ID.equals("WP19")) {
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "在建工程抵押证明号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sz_zjgczh"));
                                }
                                //在建抵押注销登记
                                if (DEFINE_ID.equals("WP21")){
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "在建工程抵押证明号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sz_taxiangzheng"));
                                }
                                //预告抵押
                                if (DEFINE_ID.equals("WP1")) {
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "抵押预告登记证明号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sz_ygdjh"));
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "预告登记证明号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sl_ycq_zheng"));
                                }
                                if (DEFINE_ID.equals("WP4")) {
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "抵押预告登记证明号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sl_ycq_zheng"));
                                }


                                if (DEFINE_ID.equals("WP44")) {
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "预告登记证明号");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH, fangChanResultSet.getString("sz_ygdjh"));
                                }




                                businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("yw_zuoluo"));
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,"未知");
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"图");
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,fangChanResultSet.getString("ch_qiuhao"));
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"丘");
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,fangChanResultSet.getString("ch_zhuanghao"));
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"幢");
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.IMPORTANT,fangChanResultSet.getString("ch_zhuanghao"));
//                                businessDisplay.addData(DescriptionDisplay.DisplayStyle.DECORATE,"房");

                                if(MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)){//抵押业务
                                    businessDisplay.newLine(DescriptionDisplay.DisplayStyle.NORMAL);
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.LABEL, "抵押权人");
                                    businessDisplay.addData(DescriptionDisplay.DisplayStyle.PARAGRAPH,fangChanResultSet.getString("sl_taxiangquanren"));

                                }
                                //System.out.println(DescriptionDisplay.toStringValue(businessDisplay));


                                sqlWriter.write("INSERT BUSINESS_HOUSE (ID, HOUSE_CODE, BUSINESS_ID, START_HOUSE, AFTER_HOUSE, CANCELED,SEARCH_KEY,DISPLAY) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(houseCode)
                                        , Q.pm(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("keycode")+"-F"), Q.p(fangChanResultSet.getString("keycode")), "False", Q.pm(key.getKey()),Q.pm(DescriptionDisplay.toStringValue(businessDisplay)) + ");"));
                                sqlWriter.newLine();

                                // 房屋状态 ===ADD_HOUSE_STATUS 交易备案 初始登记 查封
                                String houseState = null;
                                if (DEAL_DEFINE_ID.contains(DEFINE_ID) || DEFINE_ID.equals("WP40")){
                                    sqlWriter.write("INSERT ADD_HOUSE_STATUS (ID, BUSINESS, STATUS, IS_REMOVE) VALUES  ");
                                    if(!DEFINE_ID.equals("WP40")){
                                        sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("keycode"))
                                                , Q.p("OWNERED"), Q.p(false) + ");"));
                                        houseState = "OWNERED";

                                    }else {
                                        sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("keycode"))
                                                , Q.p("INIT_REG"), Q.p(false) + ");"));
                                        houseState = "INIT_REG";

                                    }
                                    sqlWriter.newLine();
                                }



                                // 房屋状态 === 交易备案 有housecode连接起来 计算HOUSERECORD


                                KeyGeneratorHelper keyRecord = new KeyGeneratorHelper();
                                keyRecord.addWord(fangChanResultSet.getString("yw_cqr")); //产权人
                                keyRecord.addWord(fangChanResultSet.getString("yw_cqr_card"));//产权人身份证号

                                resultSetFangchanGy= statementFangchanCH.executeQuery("SELECT * FROM c_gongyou WHERE keycode='" + fangChanResultSet.getString("keycode") + "'");
                                resultSetFangchanGy.last();
                                gysl=resultSetFangchanGy.getRow();
                                if (gysl>0){
                                    resultSetFangchanGy.beforeFirst();
                                    while(resultSetFangchanGy.next()){
                                        keyRecord.addWord((resultSetFangchanGy.getString("gy_ren").trim())); //共有权人
                                        if (resultSetFangchanGy.getString("gy_card")!=null) {
                                            keyRecord.addWord(resultSetFangchanGy.getString("gy_card").trim()); //身份证号
                                        }
                                    }
                                }
                                if (DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12")
                                        || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                        || DEFINE_ID.equals("OM2") || DEFINE_ID.equals("WP17")
                                        || DEFINE_ID.equals("WP25") || DEFINE_ID.equals("WP73")
                                        || DEFINE_ID.equals("WP74") ){//抵押查封 把产权证号导入进去
                                    if (fangChanResultSet.getString("sl_ycq_zheng")!=null && !fangChanResultSet.getString("sl_ycq_zheng").equals("")){
                                        keyRecord.addWord((fangChanResultSet.getString("sl_ycq_zheng").trim())); //现产权证号,预抵的预告登记号
                                    }
                                }
                                if(DEFINE_ID.equals("WP12") || DEFINE_ID.equals("OM2") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP19")){
                                    if (fangChanResultSet.getString("sl_str6")!=null && !fangChanResultSet.getString("sl_str6").equals("")){
                                        keyRecord.addWord((fangChanResultSet.getString("sl_str6").trim()));
                                    }

                                }
                                if (DEFINE_ID.equals("WP21")){
                                    if (fangChanResultSet.getString("sz_taxiangzheng")!=null && !fangChanResultSet.getString("sz_taxiangzheng").equals("")){
                                        keyRecord.addWord((fangChanResultSet.getString("sz_taxiangzheng").trim()));
                                    }
                                }


                                keyRecord.addWord(houseCode);//房屋编号 业务编号
                                keyRecord.addWord(number);//权证号
                                keyRecord.addWord(fangChanResultSet.getString("yw_zuoluo"));//房屋坐落




                                System.out.println("houseid222--"+fangChanResultSet.getString("yw_houseid"));
                               // if(fangChanResultSet.getString("yw_houseid")==null || fangChanResultSet.getString("yw_houseid").equals("0")
                               //         || fangChanResultSet.getString("yw_houseid").equals("")){// 没有houseCode keycode=houseCode

                                    sqlWriter.write("INSERT HOUSE_RECORD (HOUSE_CODE, HOUSE, HOUSE_STATUS,DISPLAY,SEARCH_KEY) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), Q.p(fangChanResultSet.getString("keycode"))
                                            , Q.p(houseState), Q.pm(DescriptionDisplay.toStringValue(businessDisplay)), Q.pm(keyRecord.getKey()) + ");"));
                                    sqlWriter.newLine();
                               // }


                                if(fangChanResultSet.getString("yw_houseid")!=null && !fangChanResultSet.getString("yw_houseid").equals("0")
                                        && !fangChanResultSet.getString("yw_houseid").equals("")) {// 有houseCode 判断是否最后一手 房屋状态
                                    String houseid = fangChanResultSet.getString("yw_houseid");

                                    ResultSet resultSetOwnerRecordCh2 = statementOwnerRecord.executeQuery("SELECT OWNER_BUSINESS.* FROM OWNER_BUSINESS LEFT JOIN BUSINESS_HOUSE ON " +
                                            "OWNER_BUSINESS.ID=BUSINESS_HOUSE.BUSINESS_ID LEFT JOIN HOUSE_RECORD ON BUSINESS_HOUSE.AFTER_HOUSE=HOUSE_RECORD.HOUSE " +
                                            "WHERE HOUSE_RECORD.HOUSE_CODE='" + houseid + "'");
                                    //时间判断 fs_date>REG_TIMEX修改HOUSE_RECORD
                                    System.out.println("houseid--"+houseid);

                                    if (resultSetOwnerRecordCh2.next()) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        java.util.Date fsDate = new java.util.Date(fangChanResultSet.getTimestamp("fs_date").getTime());   // sql -> util
                                        java.util.Date dbDate = new java.util.Date(resultSetOwnerRecordCh2.getTimestamp("REG_TIME").getTime());   // sql -> util
                                        if (fsDate.after(dbDate)) {

//                                            System.out.println("3333");
//                                            System.out.println(fsDate.before(dbDate));

                                            if (houseState != null) { //计算房屋主状态
                                                if (houseState.equals("INIT_REG")) {//房屋
                                                    lastHouseState = "INIT_REG";
                                                }
                                                if (houseState.equals("OWNERED")) {//房屋
                                                    lastHouseState = "OWNERED";
                                                }
                                            }

                                            if (oldhouseid.equals("") || !oldhouseid.equals(houseid)) {// 同一个房子判断
                                                fangChanResultSetCH1 = statementFangchanCH1.executeQuery("select y.keycode,y.yw_houseid,y.yw_mingcheng,y.yw_mc_biaoshi,y.yw_jieduan,y.yw_jd_biaoshi,y.yw_cqr,y.yw_cqr_card_type,y.yw_cqr_card, " +
                                                        "y.yw_cqr_dianhua,y.yw_zuoluo,sl_kaifagongsi,sl_ycqr,sl_ycqr_card_type,sl_ycqr_card,sl_ycqr_dianhua,sl_ycq_zheng,ch_qiuhao, " +
                                                        "ch_zhuanghao,ch_fanghao,ch_jiegou,ch_laiyuan,ch_shejiyongtu,ch_mj_jianzhu,ch_jianzhuNianFen,ch_zongceng,ch_ceng,ch_chanbie,sf_goufangkuan, " +
                                                        "sf_goufangkuan,sf_pinggujia,sz_taxiangzheng,sz_zhenghao,sz_zjgczh,sz_ygdjh,sl_str6,sz_beizhu,sl_beizhu,sl_date,fs_date,sl_hth,useType,sz_gongyouqingkuang,sl_taxiangquanren " +
                                                        "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
                                                        "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                                                        "and y.keycode=sz.keycode and  y.keycode=pg.keycode " +
                                                        "and yw_mingcheng <>'租赁登记' and yw_jieduan not like '%受理%' and yw_jieduan not like '%登记%' " +
                                                        "and yw_jieduan not like '%复审' and yw_jieduan not like '%评估%' " +
                                                        "and yw_jieduan not like '%测绘%' and( y.keycode like '2017%' " +
                                                        "or y.keycode like '2016%') " +
                                                        "and  y.yw_mc_biaoshi<>'13' and y.yw_mc_biaoshi<>'12' and y.yw_mc_biaoshi<>'14' and y.yw_mc_biaoshi<>'15' " +
                                                        "and y.yw_mc_biaoshi<>'199' and y.yw_mc_biaoshi<>'810' and y.yw_mc_biaoshi<>'53' and y.yw_mc_biaoshi<>'54' " +
                                                        "and y.yw_mc_biaoshi<>'81' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' " +
                                                        "and y.yw_mc_biaoshi<>'399' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' " +
                                                        "and (y.yw_houseid='" + houseid + "') " +
                                                        "order by yw_houseid,y.keycode ");

                                                fangChanResultSetCH1.last();
                                                keycode = fangChanResultSetCH1.getString("keycode");
                                                System.out.println("Last---" + keycode);
                                            }

                                            if (fangChanResultSet.getString("keycode").equals(keycode)) {//判断一个房子的最后一手 keycode

                                                //查询HouseRecord是否有相同
                                                resultSetHouseRecord = statementOwnerRecord.executeQuery("SELECT * FROM HOUSE_RECORD WHERE HOUSE_CODE<>'0' AND HOUSE_CODE='" + houseid + "'");
                                                String recordHouseState = null;
                                                if (resultSetHouseRecord.next()) {
                                                    recordHouseState = resultSetHouseRecord.getString("HOUSE_STATUS"); //PROJECT_PLEDGE
                                                    if (recordHouseState!=null && recordHouseState.equals("PROJECT_PLEDGE")) {
                                                        lastHouseState = "PROJECT_PLEDGE";
                                                    }
                                                    sqlWriter.write("DELETE FROM HOUSE_RECORD WHERE HOUSE_CODE='" + houseid + "';");
                                                    sqlWriter.newLine();

                                                }
                                                sqlWriter.write("INSERT HOUSE_RECORD (HOUSE_CODE, HOUSE, HOUSE_STATUS,DISPLAY,SEARCH_KEY) VALUES ");
                                                sqlWriter.write("(" + Q.v(Q.p(houseid), Q.p(fangChanResultSet.getString("keycode"))
                                                        , Q.p(lastHouseState), Q.pm(DescriptionDisplay.toStringValue(businessDisplay)), Q.pm(keyRecord.getKey()) + ");"));
                                                sqlWriter.newLine();

                                                lastHouseState = null;
                                            }
                                        }
                                    }

                                }




                                //产权人，共有人，预告人，初始登记人，
                                if (!DEFINE_ID.equals("WP18") && !DEFINE_ID.equals("WP19") && !DEFINE_ID.equals("WP21")) {
                                    String personType = null;
                                    if (DEFINE_ID.equals("WP40")) {
                                        personType = "INIT";
                                    }
                                    if (DEAL_DEFINE_ID.contains(DEFINE_ID) || DEFINE_ID.equals("WP9") || DEFINE_ID.equals("WP10") || DEFINE_ID.equals("WP12") || DEFINE_ID.equals("WP13") || DEFINE_ID.equals("WP15")
                                            || DEFINE_ID.equals("OM2") || DEFINE_ID.equals("WP17") || DEFINE_ID.equals("WP25")
                                            || DEFINE_ID.equals("WP73") || DEFINE_ID.equals("WP74") || DEFINE_ID.equals("WP38") || DEFINE_ID.equals("WP34")) {
                                        personType = "OWNER";
                                    }
                                    if (DEAL_DEFINE_ID.contains(DEFINE_ID)){//原产权人
                                        sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                                " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                        String ycqr;
                                        String ypersonType;
                                        if (DEFINE_ID.equals("WP41")){
                                            ycqr = fangChanResultSet.getString("sl_kaifagongsi");
                                            ypersonType ="INIT";

                                        }else{
                                            ycqr = fangChanResultSet.getString("sl_ycqr");
                                            ypersonType = "OWNER";
                                        }

                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"Y"), Q.pm(ycqr)
                                                , Q.fcCardType(fangChanResultSet.getString("sl_ycqr_card_type")), Q.pm(fangChanResultSet.getString("sl_ycqr_card")), Q.pm(fangChanResultSet.getString("sl_ycqr_dianhua"))
                                                , Q.p("未知")
                                                , Q.p(ypersonType), "'1'"
                                                , "Null", "True", "NULL" + ");"));
                                        sqlWriter.newLine();

                                        //房屋与产权人关联
                                        sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),
                                                Q.pm(fangChanResultSet.getString("keycode")+"Y") + ");"));
                                        sqlWriter.newLine();


                                    }
                                    if (DEFINE_ID.equals("WP46") || DEFINE_ID.equals("WP44") || DEFINE_ID.equals("WP1") || DEFINE_ID.equals("WP4")) {
                                        personType = "PREPARE";
                                    }

                                    sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO,PHONE,ADDRESS," +
                                            " TYPE, PRI, CARD, OLD, PROXY_PERSON) VALUE ");
                                    sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(fangChanResultSet.getString("yw_cqr"))
                                            , Q.fcCardType(fangChanResultSet.getString("yw_cqr_card_type")), Q.pm(fangChanResultSet.getString("yw_cqr_card")), Q.pm(fangChanResultSet.getString("yw_cqr_dianhua"))
                                            , Q.p("未知")
                                            , Q.p(personType), "'1'"
                                            , "Null", "false", "NULL" + ");"));
                                    sqlWriter.newLine();

                                    //修改afterhouse产权人
                                    sqlWriter.write("UPDATE HOUSE SET MAIN_OWNER = '" + fangChanResultSet.getString("keycode") + "' WHERE ID='" + fangChanResultSet.getString("keycode") + "';");
                                    sqlWriter.newLine();

                                    //房屋与产权人关联
                                    sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                    sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),
                                            Q.pm(fangChanResultSet.getString("keycode")) + ");"));
                                    sqlWriter.newLine();


                                    // 共有权人
                                    ResultSet fcgyResultSet = statementFangchanCH1.executeQuery("select * from c_gongyou where gy_ren is not null and gy_ren<>'' " +
                                            "and keycode='" + fangChanResultSet.getString("keycode") + "'");
//                                    System.out.println("select * from c_gongyou where gy_ren is not null and gy_ren<>'' " +
//                                            "and keycode='" + fangChanResultSet.getString("keycode") + "'");
                                    int j = 1;
                                    if (fcgyResultSet.next()) {

                                        sqlWriter.write("INSERT POWER_OWNER (ID, NAME, ID_TYPE, ID_NO, RELATION, POOL_AREA, " +
                                                "PHONE,  ADDRESS, TYPE, PRI," +
                                                " CARD, OLD, PROXY_PERSON) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode") + "-" + String.valueOf(j + 1)),
                                                Q.pm(fcgyResultSet.getString("gy_ren").trim()), "'MASTER_ID'",
                                                Q.pm(fcgyResultSet.getString("gy_card")),
                                                Q.fcRelation(fcgyResultSet.getString("gy_guanxi")), Q.pm(fcgyResultSet.getBigDecimal("gy_fener")), "'未知'", "Null",
                                                Q.pm(personType), Q.p(String.valueOf(j + 1)), "Null", "false", "Null" + ");"));
                                        sqlWriter.newLine();
                                        sqlWriter.write("INSERT HOUSE_OWNER (HOUSE,POOL) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),
                                                Q.pm(fangChanResultSet.getString("keycode") + "-" + String.valueOf(j + 1)) + ");"));
                                        sqlWriter.newLine();
                                        j++;
                                    }
                                }
                                //抵押登记 抵押信息，金融机构  在建工程抵押PROJECT_MORTGAGE
                                String financialNo=null;
                                boolean finisFind=false;
                                if (MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)){
                                    ResultSet fcdyResultSet=statementFangchanCH.executeQuery("select sl_taxiangquanren,sl_diya_dianhua,sl_date,sl_quanlizhonglei,sl_diyaqianxian1, " +
                                            "sl_diyaqianxian2,ch_mj_jianzhu,sf_jiekuan,sl_jiekuanren,sl_ycqr_card,sl_ycqr_card_type,sl_ycqr_dianhua,sl_jiekuanren_dianhua from " +
                                            "c_yewu as y,c_shouli as s,c_shoufei as sf,c_cehui as c where y.keycode=s.keycode and y.keycode=sf.keycode and y.keycode=c.keycode " +
                                            "and sl_taxiangquanren is not null and sl_taxiangquanren<>'' " +
                                            "and y.keycode='"+fangChanResultSet.getString("keycode")+"'");
                                    if(fcdyResultSet.next()){
                                        sqlWriter.write("INSERT FINANCIAL (ID, NAME, CODE, PHONE, FINANCIAL_TYPE, ID_TYPE, " +
                                                "BANK, CREATE_TIME, CARD, PROXY_PERSON) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(fcdyResultSet.getString("sl_taxiangquanren")),
                                                Q.p(fcdyResultSet.getString("sl_ycqr_card")), Q.p(fcdyResultSet.getString("sl_ycqr_dianhua")), "'FINANCE_CORP'", "Null", "Null",
                                                Q.p(fcdyResultSet.getTimestamp("sl_date")), "Null", "Null"
                                                        + ");"));
                                        sqlWriter.newLine();


                                        sqlWriter.write("INSERT MORTGAEGE_REGISTE (HIGHEST_MOUNT_MONEY, WARRANT_SCOPE, INTEREST_TYPE, " +
                                                "MORTGAGE_DUE_TIME_S, MORTGAGE_TIME, MORTGAGE_AREA, " +
                                                "TIME_AREA_TYPE, ID, BUSINESS_ID, OLD_FIN, FIN, ORG_NAME) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fcdyResultSet.getBigDecimal("sf_jiekuan")), "Null", Q.pm(fcdyResultSet.getString("sl_quanlizhonglei")),
                                                Q.pm(fcdyResultSet.getTimestamp("sl_diyaqianxian1")), Q.pm(fcdyResultSet.getTimestamp("sl_diyaqianxian2")), Q.pm(fcdyResultSet.getBigDecimal("ch_mj_jianzhu")),
                                                "'DATE_TIME'", fangChanResultSet.getString("keycode"), fangChanResultSet.getString("keycode"),
                                                "Null", fangChanResultSet.getString("keycode"), "'凤城市房地产管理处'"
                                                        + ");"));
                                        sqlWriter.newLine();


                                        //债务人
                                        if (fcdyResultSet.getString("sl_jiekuanren")!=null){
                                            sqlWriter.write("INSERT BUSINESS_PERSION (ID, ID_NO, ID_TYPE, NAME, TYPE, BUSINESS_ID, PHONE) VALUE ");
                                            sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm("未知")
                                                    , "'OTHER'", Q.pm(fcdyResultSet.getString("sl_jiekuanren")), "'MORTGAGE_OBLIGOR'"
                                                    , Q.pm(fangChanResultSet.getString("keycode")), Q.pm(fcdyResultSet.getString("sl_jiekuanren_dianhua"))
                                                    + ");"));
                                            sqlWriter.newLine();
                                        }
                                    }else{
                                        sqlWriter.write("INSERT FINANCIAL (ID, NAME, CODE, PHONE, FINANCIAL_TYPE, ID_TYPE, " +
                                                "BANK, CREATE_TIME, CARD, PROXY_PERSON) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),"'未知'",
                                                "Null","Null", "'FINANCE_CORP'", "Null", "Null",
                                                Q.p(fangChanResultSet.getTimestamp("sl_date")), "Null", "Null"
                                                        + ");"));
                                        sqlWriter.newLine();


                                        sqlWriter.write("INSERT MORTGAEGE_REGISTE (HIGHEST_MOUNT_MONEY, WARRANT_SCOPE, INTEREST_TYPE, " +
                                                "MORTGAGE_DUE_TIME_S, MORTGAGE_TIME, MORTGAGE_AREA, " +
                                                "TIME_AREA_TYPE, ID, BUSINESS_ID, OLD_FIN, FIN, ORG_NAME) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(new BigDecimal(0)), "'未知'", "'未知'",
                                                "'2000-1-1'","'2000-1-1'","0",
                                                "'DATE_TIME'", fangChanResultSet.getString("keycode"), fangChanResultSet.getString("keycode"),
                                                "Null", fangChanResultSet.getString("keycode"), "'凤城市房地产管理处'"
                                                        + ");"));
                                        sqlWriter.newLine();
                                    }
                                    if (DEFINE_ID.equals("WP18")||DEFINE_ID.equals("WP19") ||DEFINE_ID.equals("WP21")) {
                                        if (fangChanResultSet.getString("yw_cqr")!=null && !fangChanResultSet.getString("yw_cqr").equals("")){
                                            sqlWriter.write("INSERT PROJECT_MORTGAGE (ID,DEVELOPER_NAME,DEVELOPER_CODE) VALUE ");
                                            sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),
                                                    Q.pm(fangChanResultSet.getString("yw_cqr")), Q.pm("未知") + ");"));
                                            sqlWriter.newLine();
                                        }else{
                                            sqlWriter.write("INSERT PROJECT_MORTGAGE (ID,DEVELOPER_NAME,DEVELOPER_CODE) VALUE ");
                                            sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),
                                                    "'未知'", "'未知'" + ");"));
                                            sqlWriter.newLine();

                                        }
                                    }
                                }
                                // EVALUATE
                                if(fangChanResultSet.getBigDecimal("sf_pinggujia")!=null){
                                    sqlWriter.write("INSERT EVALUATE (EVALUATE_CORP_NAME, EVALUATE_CORP_N0, ASSESSMENT_PRICE, ID, BUSINESS_ID) VALUE ");
                                    sqlWriter.write("(" + Q.v(Q.pm("未知"), Q.p("未知")
                                            , Q.pm(fangChanResultSet.getBigDecimal("sf_pinggujia")),Q.pm(fangChanResultSet.getString("keycode")),
                                            Q.pm(fangChanResultSet.getString("keycode"))
                                                    + ");"));
                                    sqlWriter.newLine();
                                }
                                //HOUSE_REG_INFO 产别 产权来源
                                sqlWriter.write("INSERT HOUSE_REG_INFO (ID, HOUSE_PORPERTY, HOUSE_FROM) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),Q.pm(fangChanResultSet.getString("ch_chanbie"))
                                        ,Q.pm(fangChanResultSet.getString("ch_laiyuan"))+ ");"));
                                sqlWriter.newLine();
                                sqlWriter.write("UPDATE HOUSE SET REG_INFO = '"+fangChanResultSet.getString("keycode")+"' WHERE ID='"+fangChanResultSet.getString("keycode")+"';");
                                sqlWriter.newLine();

                                //SALE_INFO 购房款
                                BigDecimal gkf = new BigDecimal(0);
                                if (fangChanResultSet.getBigDecimal("sf_goufangkuan")!=null){
                                    gkf = fangChanResultSet.getBigDecimal("sf_goufangkuan");
                                }

                                if ((fangChanResultSet.getBigDecimal("sf_goufangkuan")==null || fangChanResultSet.getBigDecimal("sf_goufangkuan").compareTo(BigDecimal.ZERO)==0)
                                        && fangChanResultSet.getBigDecimal("sf_chengjiaojia")!=null){
                                    gkf = fangChanResultSet.getBigDecimal("sf_chengjiaojia");
                                }

                                sqlWriter.write("INSERT SALE_INFO (ID, PAY_TYPE, SUM_PRICE, HOUSEID) VALUES ");
                                sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),"NULL"
                                        ,Q.pm(gkf),Q.pm(fangChanResultSet.getString("keycode"))+ ");"));
                                sqlWriter.newLine();

                                if (DEFINE_ID.equals("WP73")){ //查封
                                    ResultSet cfResultSet = statementFangchanCH.executeQuery("select y.keycode,sl_str2 as cffy,sl_str5 as cxsx,sl_str3 as cfqssj,sl_str6 as cfzzsj, " +
                                            "sl_fypjs as flws,sl_fyxztzs as xztzs,sl_dailiren as sdrxm,sl_dailiren_dianhua as lxdh,sl_dailiren_card as zxgwzjh,sl_str4 as gzzjh, " +
                                            "sl_ycq_zheng ,sl_hth,sl_yingyezhizhao " +
                                            "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
                                            "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                                            "and y.keycode=sz.keycode and  y.keycode=pg.keycode " +
                                            "and yw_mingcheng <>'租赁登记' and yw_jieduan not like '%受理%' and yw_jieduan not like '%登记%' " +
                                            "and yw_jieduan not like '%复审' and yw_jieduan not like '%评估%' " +
                                            "and yw_jieduan not like '%测绘%' " +

                                            "and  y.yw_mc_biaoshi<>'13' and y.yw_mc_biaoshi<>'12' and y.yw_mc_biaoshi<>'14' and y.yw_mc_biaoshi<>'15' "+
                                            "and y.yw_mc_biaoshi<>'199' and y.yw_mc_biaoshi<>'810' and y.yw_mc_biaoshi<>'53' and y.yw_mc_biaoshi<>'54' "+
                                            "and y.yw_mc_biaoshi<>'81' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                                            "and y.yw_mc_biaoshi<>'399' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                                            "and (y.keycode='"+fangChanResultSet.getString("keycode") +"') " +
                                            "order by yw_houseid,y.keycode ");
                                    if (cfResultSet.next()) {
                                        sqlWriter.write("INSERT CLOSE_HOUSE (ID, CLOSE_DOWN_CLOUR, ACTION, CLOSE_DATE, TO_DATE, BUSINESS_ID, LEGAL_DOCUMENTS, EXECUTION_NOTICE, SEND_PEOPLE, PHONE, " +
                                                "EXECUTION_CARD_NO, WORK_CARD_NO, HOUSECARDNO, CONTRACTCODE, PROJECTRSIHP, TIME_AREA_TYPE) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(cfResultSet.getString("cffy")), Q.pm(cfResultSet.getString("cxsx"))
                                                , Q.pm(cfResultSet.getTimestamp("cfqssj")), Q.pm(cfResultSet.getTimestamp("cfzzsj")), Q.pm(cfResultSet.getString("keycode"))
                                                , Q.pm(cfResultSet.getString("flws")), Q.p(cfResultSet.getString("xztzs")), Q.p(cfResultSet.getString("sdrxm")), Q.p(cfResultSet.getString("lxdh"))
                                                , Q.p(cfResultSet.getString("zxgwzjh")), Q.p(cfResultSet.getString("gzzjh")), Q.p(cfResultSet.getString("sl_ycq_zheng")), Q.p(cfResultSet.getString("sl_hth"))
                                                , Q.p(cfResultSet.getString("sl_yingyezhizhao"))
                                                , Q.p("DATE_TIME") + ");"));
                                        sqlWriter.newLine();
                                    }

                                    if(fangChanResultSet.getString("yw_houseid")!=null && !fangChanResultSet.getString("yw_houseid").equals("0")
                                            && !fangChanResultSet.getString("yw_houseid").equals("")){


                                        sqlWriter.write("INSERT LOCKED_HOUSE(HOUSE_CODE, DESCRIPTION, TYPE, EMP_CODE, EMP_NAME, LOCKED_TIME, ID, BUILD_CODE) values ("
                                                + Q.v(Q.p(fangChanResultSet.getString("keycode")),"'在老系统中房屋状态为：查封'", "'HOUSE_LOCKED'",
                                                "'未知'", "'管理员'", Q.p(Q.nowFormatTime()), Q.p(fangChanResultSet.getString("keycode")), Q.p("未知")) + ");");
                                        sqlWriter.newLine();

                                    }





                                }
                                if (DEFINE_ID.equals("WP74")){ //解封
                                    ResultSet cfjcResultSet = statementFangchanCH.executeQuery("select y.keycode,sl_gyd as jffy,sl_yingyezhizhao as jfwh,sl_beianshijian as jfsj,sl_anzhixiaoqu as jfwj, " +
                                            "sl_ycq_zheng " +
                                            "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
                                            "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                                            "and y.keycode=sz.keycode and  y.keycode=pg.keycode " +
                                            "and yw_mingcheng <>'租赁登记' and yw_jieduan not like '%受理%' and yw_jieduan not like '%登记%' " +
                                            "and yw_jieduan not like '%复审' and yw_jieduan not like '%评估%' " +
                                            "and yw_jieduan not like '%测绘%' " +

                                            "and  y.yw_mc_biaoshi<>'13' and y.yw_mc_biaoshi<>'12' and y.yw_mc_biaoshi<>'14' and y.yw_mc_biaoshi<>'15' "+
                                            "and y.yw_mc_biaoshi<>'199' and y.yw_mc_biaoshi<>'810' and y.yw_mc_biaoshi<>'53' and y.yw_mc_biaoshi<>'54' "+
                                            "and y.yw_mc_biaoshi<>'81' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                                            "and y.yw_mc_biaoshi<>'399' and y.yw_mc_biaoshi<>'82' and y.yw_mc_biaoshi<>'305' and y.yw_mc_biaoshi<>'306' "+
                                            "and (y.keycode='"+fangChanResultSet.getString("keycode") +"') " +
                                            "order by yw_houseid,y.keycode ");

                                    if (cfjcResultSet.next()) {
                                        sqlWriter.write("INSERT HOUSE_CLOSE_CANCEL (ID, CANCEL_DATE, BUSINESS_ID, CANCEL_DOWN_CLOUR, LEGAL_DOCUMENTS, EXECUTION_NOTICE, HOUSECARDNO) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")), Q.pm(cfjcResultSet.getTimestamp("jfsj")), Q.pm(fangChanResultSet.getString("keycode"))
                                                , Q.pm(cfjcResultSet.getString("jffy")), Q.pm(cfjcResultSet.getString("jfwj")), Q.pm(cfjcResultSet.getString("jfwh")),Q.p(fangChanResultSet.getString("sl_ycq_zheng"))
                                               + ");"));
                                        sqlWriter.newLine();
                                    }
                                }

                                //业务要件
                                //BUSINESS_FILE
                                ResultSet fileResulset = statementFangchanCH.executeQuery("select * from c_ftp where BusinessId='"+fangChanResultSet.getString("keycode")+"'");
                                fileResulset.last();
                                int feilesl=fileResulset.getRow();
                                if (feilesl>0){
                                    fileResulset.beforeFirst();
                                    while (fileResulset.next()){
                                        sqlWriter.write("INSERT BUSINESS_FILE (ID, BUSINESS_ID, NAME,  NO_FILE, PRIORITY, TYPE) VALUES ");
                                        sqlWriter.write("(" + Q.v(Q.p("F-"+fileResulset.getString("id")),Q.pm(fileResulset.getString("BusinessId"))
                                                ,Q.pm(fileResulset.getString("FileName")),
                                                "True",Q.pm(fileResulset.getString("PRIORITY")),Q.p("ADDITIONAL")+ ");"));
                                        sqlWriter.newLine();



                                        sqlWriter.write("INSERT UPLOAD_FILE (FILE_NAME, EMP_NAME, EMP_CODE, MD5, BUSINESS_FILE_ID, ID,  UPLOAD_TIME) VALUE ");
                                        sqlWriter.write("(" + Q.v(Q.p(fileResulset.getString("FileDir")),Q.pm("ADMIN"),Q.pm("ROOT"),Q.pm(""),
                                                Q.p("F-"+fileResulset.getString("id")),Q.p("F-"+fileResulset.getString("id")),Q.pm(fangChanResultSet.getTimestamp("fs_date"))
                                                + ");"));
                                        sqlWriter.newLine();
                                    }
                                }

                                i++;
                                System.out.println(i + "/" + String.valueOf(recordCount));
                                sqlWriter.flush();

                                oldhouseid  = fangChanResultSet.getString("yw_houseid");
                                if (oldhouseid==null || oldhouseid.trim().equals("") || oldhouseid.trim().equals("0")){
                                    oldhouseid = "";
                                }
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
