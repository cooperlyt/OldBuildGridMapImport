package com.wxy.House.HouseOwnerRecord;

import com.cooper.house.Q;

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

    private static Statement statementOwnerRecord;

    private static ResultSet fangChanResultSet;

    private static ResultSet fangChanResultSetCH;

    private static ResultSet recordResultSet;

    private static String DEFINE_ID;

    private static String stratHouseId;

    private static String afterHouseId;

    private static Set<String> DEAL_DEFINE_ID= new HashSet<>();

    private static Set<String> MORTGAEGE_DEFINE_ID = new HashSet<>();

    public static void main(String agr[]) throws SQLException {

        //交易备案 业务DEFINE_ID
        DEAL_DEFINE_ID.add("WP41");//商品房交易
        DEAL_DEFINE_ID.add("WP56");//二手房交易
//        DEAL_DEFINE_ID.add("WP57");//企业改制
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
        DEAL_DEFINE_ID.add("WP243");//房屋调拨

        //变更登记导入已办产权
//        DEAL_DEFINE_ID.add("WP52");//名称变更登记
//        DEAL_DEFINE_ID.add("WP53");//自翻扩改
//        DEAL_DEFINE_ID.add("WP54");//分照
//        DEAL_DEFINE_ID.add("WP55");//合照
        //初始登记入已办产权
//        DEAL_DEFINE_ID.add("WP30");//新建房屋
//        DEAL_DEFINE_ID.add("WP31");//无籍房屋

//        DEAL_DEFINE_ID.add("WP32");//所有权遗失补照
//        DEAL_DEFINE_ID.add("WP33");//换照
//        DEAL_DEFINE_ID.add("WP35");//所有权更正登记


        MORTGAEGE_DEFINE_ID.add("WP9");//房屋所有权抵押登记
        MORTGAEGE_DEFINE_ID.add("WP10");//房屋所有权抵押变更登记
        MORTGAEGE_DEFINE_ID.add("WP13");//最高额抵押登记
        MORTGAEGE_DEFINE_ID.add("WP18");//在建工程抵押登记
        MORTGAEGE_DEFINE_ID.add("WP19");//在建工程抵押登记变更






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

            fangChanResultSet = statementFangchan.executeQuery("select y.keycode,y.yw_houseid,y.yw_mingcheng,y.yw_mc_biaoshi,y.yw_jieduan,y.yw_jd_biaoshi,y.yw_cqr,y.yw_cqr_card_type,y.yw_cqr_card," +
                    "y.yw_cqr_dianhua,y.yw_zuoluo,sl_kaifagongsi,sl_ycqr,sl_ycqr_card_type,sl_ycqr_card,sl_ycqr_dianhua,sl_ycq_zheng,ch_qiuhao," +
                    "ch_zhuanghao,ch_fanghao,ch_jiegou,ch_laiyuan,ch_shejiyongtu,ch_mj_jianzhu,ch_jianzhuNianFen,ch_zongceng,ch_ceng,ch_chanbie,sf_goufangkuan," +
                    "sf_goufangkuan,sf_pinggujia,sz_taxiangzheng,sz_zhenghao,sz_beizhu,sl_beizhu,sl_date,fs_date " +
                    "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs " +
                    "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                    "and y.keycode=sz.keycode and  y.keycode=pg.keycode and (yw_mc_biaoshi='11' or yw_mc_biaoshi='21' or yw_mc_biaoshi='808' or yw_mc_biaoshi='250' " +
                    "or yw_mc_biaoshi='43' or yw_mc_biaoshi='41' or yw_mc_biaoshi='42' or yw_mc_biaoshi='91' or yw_mc_biaoshi='101' or yw_mc_biaoshi='302' " +
                    "or yw_mc_biaoshi='303' or yw_mc_biaoshi='304' or yw_mc_biaoshi='50' or yw_mc_biaoshi='111' or yw_mc_biaoshi='31' or yw_mc_biaoshi='32' " +
                    "or yw_mc_biaoshi='812' or yw_mc_biaoshi='34' or yw_mc_biaoshi='811' or yw_mc_biaoshi='33' or yw_mc_biaoshi='305' or yw_mc_biaoshi='306' " +
                    "or yw_mc_biaoshi='35' or yw_mc_biaoshi='61') " +
                    "and yw_mc_biaoshi <> '3' and yw_mc_biaoshi <> '2' and yw_jd_biaoshi <> '6' and yw_jd_biaoshi <> '8' and yw_jd_biaoshi <> '7' " +
                    "and yw_jd_biaoshi <> '4'and yw_jd_biaoshi <> '10' and yw_jd_biaoshi <> '1' and y.keycode='201411280039' " +
                    "order by y.keycode");


            fangChanResultSet.last();
            int recordCount = fangChanResultSet.getRow();
            System.out.println(recordCount);
            sqlWriter.newLine();

            if (recordCount>0){
                fangChanResultSet.beforeFirst();
                int i=0;
                while (fangChanResultSet.next()){

                    //OWNER_BUSINESS
                    DEFINE_ID = Q.changeDefineID(fangChanResultSet.getInt("yw_mc_biaoshi"));
                    sqlWriter.write("INSERT OWNER_BUSINESS (ID, VERSION, SOURCE, MEMO, STATUS, DEFINE_NAME, DEFINE_ID, DEFINE_VERSION, SELECT_BUSINESS," +
                            " CREATE_TIME, APPLY_TIME, CHECK_TIME, REG_TIME, RECORD_TIME, RECORDED, TYPE) VALUES ");
                    sqlWriter.write("(" + Q.v(Q.p(fangChanResultSet.getString("keycode")), "0", "'BIZ_IMPORT'", Q.p(fangChanResultSet.getString("sl_beizhu"))
                            , "'COMPLETE'", Q.pm(fangChanResultSet.getString("yw_mingcheng")), Q.pm(DEFINE_ID), "0", "Null", Q.p(fangChanResultSet.getTimestamp("sl_date"))
                            , Q.p(fangChanResultSet.getTimestamp("sl_date")), "Null", Q.p(fangChanResultSet.getTimestamp("fs_date")), Q.p(fangChanResultSet.getTimestamp("fs_date")), "False", "'NORMAL_BIZ'") + ");");
                    sqlWriter.newLine();

                    //CARD 业务证书号,
                    String cardType="OTHER_CARD";
                    String number=null;
                    if (DEAL_DEFINE_ID.contains(DEFINE_ID)){
                        cardType = "OWNER_RSHIP";
                        number = fangChanResultSet.getString("sz_zhenghao");
                    }
                    if (MORTGAEGE_DEFINE_ID.contains(DEFINE_ID)){
                        cardType = "MORTGAGE";
                        number = fangChanResultSet.getString("sz_taxiangzheng");
                    }

                    sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                    sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")),Q.pm(cardType),
                            Q.pm1(number),Q.pm(fangChanResultSet.getString("keycode")),Q.p(fangChanResultSet.getString("sz_beizhu")),
                            "NUll" + ");"));
                    sqlWriter.newLine();

                    if (MORTGAEGE_DEFINE_ID.contains(DEFINE_ID) && !DEFINE_ID.equals("WP18") && !DEFINE_ID.equals("WP19")){
                        sqlWriter.write("INSERT CARD (ID, TYPE, NUMBER, BUSINESS_ID,MEMO,CODE) VALUE ");
                        sqlWriter.write("(" + Q.v(Q.pm(fangChanResultSet.getString("keycode")+"-1"),Q.pm("OWNER_RSHIP"),
                                Q.pm1(fangChanResultSet.getString("sl_ycq_zheng")),Q.pm(fangChanResultSet.getString("keycode")),"NUll",
                                "NUll" + ");"));
                        sqlWriter.newLine();
                    }



                    String houseid=fangChanResultSet.getString("yw_houseid");
                    if (houseid!=null && !houseid.equals("")){
                        ResultSet resultSetOwnerRecord = statementOwnerRecord.executeQuery("SELECT * FROM HOUSE_RECORD WHERE HOUSE_CODE='" + houseid + "'");


                    }







                    i++;
                    System.out.println(i+"/"+String.valueOf(recordCount));
                    sqlWriter.flush();
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
