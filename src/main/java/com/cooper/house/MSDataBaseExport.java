package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 明水
 * Created by cooper on 5/13/16.
 */
public class MSDataBaseExport {


    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.199.231:1433/fang_chan_dg";


    private static final String WARN_FILE_PATH="/Users/cooper/Documents/oldImport.log";

    private static final String SQL_FILE_PATH="/Users/cooper/Documents/oldRecord.sql";



    private static BufferedWriter sqlWriter;

    private static BufferedWriter warnWriter;

    private static Connection conn;

    public static void main(String args[]) {

        try {

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            System.out.println("Connection successful");
            Statement statement = conn.createStatement();
            ResultSet hs = statement.executeQuery("select count(*) FROM (select fj_qiuhao,fj_zhuanghao,fj_fanghao from c_fangji GROUP BY fj_qiuhao,fj_zhuanghao,fj_fanghao) v");
            hs.next();
            long count = hs.getLong(1);

            hs = statement.executeQuery("select fj_qiuhao,fj_zhuanghao,fj_fanghao from c_fangji GROUP BY fj_qiuhao,fj_zhuanghao,fj_fanghao");
            File file = new File(SQL_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                sqlWriter = new BufferedWriter(fw);
            } catch (IOException e) {
                System.out.println("sql 文件创建失败");
                e.printStackTrace();
                return;
            }


            file = new File(WARN_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                warnWriter = new BufferedWriter(fw);
            } catch (IOException e) {
                System.out.println("日志 文件创建失败");
                e.printStackTrace();
                return;
            }

            long curCount = 0;


            while (hs.next()) {
                long time = new java.util.Date().getTime();
                genRecord(hs.getString(1), hs.getString(2), hs.getString(3));
                curCount++;
                System.out.println(String.valueOf(count) + "/" + curCount + "    " + (hs.getString(1) + "-" + hs.getString(2) + "-" + hs.getString(3)) + "   " + (new java.util.Date().getTime() - time) + "ms" + "  " + (curCount / count * 100) + "%");



                break;
            }


        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }

    }


    private static void genRecord(String blockNumber, String buildNumber, String houseNumber) {
        try {
            String idCondition = " ";
            if (blockNumber == null || blockNumber.trim().equals("")) {
                idCondition += " and (ch_qiuhao is null  or LTRIM(RTRIM(ch_qiuhao)) = '') ";
            } else {
                idCondition += " and ch_qiuhao = " + Q.p(blockNumber);
            }

            if (buildNumber == null || buildNumber.trim().equals("")) {
                idCondition += " and (ch_zhuanghao is null  or LTRIM(RTRIM(ch_zhuanghao)) = '') ";
            } else {
                idCondition += " and ch_zhuanghao = " + Q.p(buildNumber);
            }

            if (houseNumber == null || houseNumber.trim().equals("")) {
                idCondition += " and (ch_fanghao is null  or LTRIM(RTRIM(ch_fanghao)) = '') ";
            } else {
                idCondition += " and ch_fanghao = " + Q.p(houseNumber);
            }

            idCondition += " order by gd_date";

            Statement statement = conn.createStatement();
            ResultSet hs = statement.executeQuery("select sl_beizhu as '业务备注 ',sl_ycq_zheng as '原产权证号',sl_ycqr as '原产权人',sl_ycqr_card as '原产权人（金融机构）证件号',sl_ycqr_card_type as '原产权人（金融机构）证件类型'," +
                    //6
                    " sl_tudishiyongzheng as '土地证号', sl_dlr as '原产权代理人(送达人姓名)',sl_dlr_card as  '原产权代理人证件号(执行公务证件号码)',sl_dlr_card_type as '原产权代理人证件类型()',sl_dlr_dianhua as '原产权代理人电话'," +
                    //11
                    " sl_worker as '受理人',sl_date as '受理日期(建立日期)',sl_faren as '法人',sl_dailiren as '现产权(抵押人)代理人',sl_dailiren_card as '现产权(抵押人)代理人证件号'," +
                    //16
                    " sl_dailiren_card_type as '现产权(抵押人)代理人证件类型',sl_dailiren_dianhua as '现产权(抵押人)代理人电话',ch_chanbie as '产别' ,ch_mj_jianzhu as '建筑面积',ch_mj_taonei as '套内面积'," +
                    //21
                    "       sl_taxiangquanren as '现金融机构',sl_taxiangquanren2 as '原金融机构',sl_diya_card_tpye2 as '原金融机构证件类型',sl_diya_card2 as '原金融机构证件号',sl_ytxqz as '原他项权证号'," +
                    //26
                    "      sl_quanlizhonglei as '权利种类',   sl_jiekuanren as '债务人',sl_jiekuanren_card as'债务人证件号',sl_jiekuanren_card_type as '债务人证件类型',sl_jiekuanren_dianhua as '债务人电话'," +
                    //31
                    "     sl_diyamudi as '抵押目的',sl_diyaqianxian1 as '抵押设定日期',  sl_diyaqianxian2 as '抵押时间',sl_danbaofanwei as '担保范围',sl_kaifagongsi as '开发商'," +
                    //36
                    "   sl_beianshijian as '备案日期(查封时间始)',sl_hjszd as '现产权人户籍所在地',sl_dyq_dlr as '现金融机构代理人', sl_dyq_dlr_card_type as '现金融机构代理人证件类型',sl_dyq_dlr_card as '现金融机构代理人证件号'," +
                    //41
                    "     sl_dyq_dlr_dianhua as '现金融机构代理人电话',sl_fyxztzs as '协助执行通知书',sl_tx_bgq as '查封法院',sl_fypjs as '法律文书',ch_qiuhao as '丘号'," +
                    //46
                    "ch_zhuanghao as '幢号',ch_fanghao as '房号',ch_jiegou as '结构',ch_zongceng as '总层数',ch_ceng as '所在层'," +
                    //51
                    "     ch_shejiyongtu as '设计用途',ch_laiyuan as '产权来源',ch_mj_fentan '分摊面积',ch_dymj as '抵押面积',y.keycode as '业务ID'," +
                    //56
                    "   yw_cqr as '产权人（抵押人）',yw_cqr_card as '产权人（抵押人）证件号', yw_cqr_card_type as '产权人（抵押人）证件类型',yw_cqr_dianhua as '产权人（抵押人）电话',yw_mingcheng  as '业务名称', " +
                    //61
                    "   yw_mc_biaoshi as '业务名称标识（需要转换）',yw_zuoluo as '房屋坐落',yw_oldkeycode as 'selectBiz',sl_GongyouGuanxi as '共有情况',sz_worker as '缮证人'," +
                    //66
                    "sz_date as '缮证时间',sz_taxiangzheng as '他项权证号',sz_zhenghao as '产权证号(预告登记号证明号)',sz_beizhu as '缮证备注',sz_quming as '区名称'," +
                    //71
                    "sz_zhengshuhao as '证书编号', qs_worker as '登薄人',qs_date as '登薄时间',qh_worker as '审核人',qh_date as '审核时间'," +
                    //76
                    "fs_worker as '初审人',fs_date '初审时间',fs_zhengjian as 'BUSINESS_FILE(业务提交要件名称)',sf_heji as '应收合计',sf_shishou as '实收合计'," +
                    //81
                    "       sf_worker as '收费人',sf_date as '收费时间',sf_pinggujia as '评估价格',sf_jiaoyiheji as '交易费',sf_dengjiheji as '登记费'," +
                    //86
                    "sf_gydj_hj as '工本费',sf_jiekuan as '债权数额', ch_td_hao as '地号',ch_td_xingzhi as '土地性质',ch_td_qdfs as '土地取得方式'," +
                    //91
                    "ch_td_synxs as '土地使用年限始',ch_td_synxz as '土地使用年限止', ch_td_mianji as '土地面积', gd_worker as  '归档人', gd_date as '归档时间'" +
                    //96
                    ",ch_downFloorCount as '地下总层数',ch_upFloorCount as '地上总层数',sl_dailiren as '现产权(抵押人)代理人' " +
                    "from c_shouli as sl,c_yewu as y,c_shoufei as sf, c_pinggu as pg,c_cehui as ch,c_shanzheng as sz,c_quanshu as qs,c_fushen as fs,c_GuiDang as gd " +
                    "where  y.keycode = sl.keycode and y.keycode = sf.keycode and  y.keycode=ch.keycode and y.keycode=fs.keycode and y.keycode=qs.keycode " +
                    "       and y.keycode=sz.keycode and  y.keycode=pg.keycode and y.keycode=gd.keycode and yw_mc_biaoshi <> '12' and yw_mc_biaoshi <> '14' and yw_jd_biaoshi = '0' " + idCondition);



            List<BizOut> out = new ArrayList<>();
            List<HouseStatus> statuses = new ArrayList<>();
            String sql = "";
            String lastHouseID =null;
            String houseCode = null;
            while (hs.next()) {
                BizOut biz = new BizOut(hs.getString(61).trim(),hs.getString(63));
                begin.doit(biz,hs,out);
                if(biz.houseStatus != null){
                    if (biz.isAddStatus){
                        statuses.add(biz.houseStatus);
                    }else {
                        for(int i = statuses.size() - 1; i >= 0 ; i--){
                            if(biz.houseStatus.equals(statuses.get(i))){
                                statuses.remove(i);
                                break;
                            }
                        }
                    }
                }
                out.add(biz);
                sql += biz.out;
                lastHouseID = hs.getString(55);


                houseCode = (hs.getString(45) == null ? "" : hs.getString(45).trim()) + "-" +
                        (hs.getString(46) == null ? "" : hs.getString(46).trim()) + "-" + (hs.getString(47) == null ? "" : hs.getString(47).trim());
            }
            if (lastHouseID != null) {

                String masterStatus = null;

                if (statuses.size() > 0){
                    Collections.sort(statuses, HouseStatus.StatusComparator.getInstance());
                    masterStatus = statuses.get(0).name();
                }
                sql += "INSERT INTO HOUSE_RECORD(HOUSE_CODE,HOUSE,HOUSE_STATUS) VALUES("
                 + Q.v(Q.p(houseCode),Q.p(lastHouseID),Q.p(masterStatus)) +
                ");";
            }


            System.out.println(sql);
            sqlWriter.write(sql);
            sqlWriter.newLine();
            sqlWriter.flush();




        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }


    }


    public static class BizOut {

        String defineId;

        String out ="";

        String keyCode;

        HouseStatus houseStatus = null;

        boolean isAddStatus = true;

        public BizOut(String d,String keyCode) {
            this.keyCode = keyCode;
            if ("11".equals(d)) {
                defineId = "WP41";
            } else if ("12".equals(d)) {
                defineId = "WP50";
            } else if ("21".equals(d)) {
                defineId = "WP56";
            } else if ("22".equals(d)) {
                defineId = "WP62";
            } else if ("23".equals(d)) {
                defineId = "WP54";
            } else if ("24".equals(d)) {
                defineId = "WP63";
            } else if ("32".equals(d)) {
                defineId = "WP9";
            } else if ("41".equals(d)) {
                defineId = "WP58";
            } else if ("42".equals(d)) {
                defineId = "WP59";
            } else if ("51".equals(d)) {
                defineId = "WP44";
            } else if ("53".equals(d)) {
                defineId = "WP69";
            } else if ("54".equals(d)) {
                defineId = "WP70";
            } else if ("55".equals(d)) {
                defineId = "WP35";
            } else if ("61".equals(d)) {
                defineId = "WP72";
            } else if ("62".equals(d)) {
                defineId = "WP72";
            } else if ("71".equals(d)) {
                defineId = "WP53";
            } else if ("80".equals(d)) {
                defineId = "WP67";
            } else if ("84".equals(d)) {
                defineId = "WP46";
            } else if ("91".equals(d)) {
                defineId = "WP60";
            } else if ("92".equals(d)) {
                defineId = "WP87";
            } else if ("101".equals(d)) {
                defineId = "WP71";
            } else if ("131".equals(d)) {
                defineId = "WP54";
            } else if ("141".equals(d)) {
                defineId = "WP55";
            } else if ("151".equals(d)) {
                defineId = "WP33";
            } else if ("161".equals(d)) {
                defineId = "WP35";
            } else if ("170".equals(d)) {
                defineId = "WP102";
            } else if ("171".equals(d)) {
                defineId = "WP52";
            } else if ("181".equals(d)) {
                defineId = "WP52";
            } else if ("192".equals(d)) {
                defineId = "WP53";
            } else if ("201".equals(d)) {
                defineId = "WP73";
            } else if ("202".equals(d)) {
                defineId = "WP74";
            } else if ("203".equals(d)) {
                defineId = "WPD203";
            } else if ("300".equals(d)) {
                defineId = "WP30";
            } else if ("301".equals(d)) {
                defineId = "WP38";
            } else if ("302".equals(d)) {
                defineId = "WP61";
            } else if ("322".equals(d)) {
                defineId = "WP12";
            } else if ("323".equals(d)) {
                defineId = "WP13";
            } else if ("324".equals(d)) {
                defineId = "WP15";
            } else if ("327".equals(d)) {
                defineId = "WP17";
            } else if ("328".equals(d)) {
                defineId = "WP18";
            } else if ("331".equals(d)) {
                defineId = "WP21";
            } else if ("336".equals(d)) {
                defineId = "WP1";
            } else if ("337".equals(d)) {
                defineId = "WP2";
            } else if ("339".equals(d)) {
                defineId = "WP4";
            } else if ("340".equals(d)) {
                defineId = "WP5";
            } else if ("343".equals(d)) {
                defineId = "WP8";
            } else if ("347".equals(d)) {
                defineId = "WP25";
            } else if ("350".equals(d)) {
                defineId = "WP57";
            } else if ("351".equals(d)) {
                defineId = "WP32";
            } else if ("355".equals(d)) {
                defineId = "WP72";
            } else if ("632".equals(d)) {
                defineId = "WP9";
            } else if ("635".equals(d)) {
                defineId = "WP12";
            } else if ("1010".equals(d)) {
                defineId = "WP1010";
            } else{
                throw new IllegalArgumentException("Unknow Define id:" + d);
            }

        }





    }

    public abstract static class TableGen{

        private TableGen next;


        public TableGen getNext() {
            return next;
        }

        public void setNext(TableGen next) {
            this.next = next;
        }

        public void doit(BizOut out, ResultSet rs,List<BizOut> befor) throws SQLException, IOException {
            gensql(out,rs,befor);
            if (next != null){
                next.doit(out,rs,befor);
            }
        }

        protected abstract void gensql(BizOut out, ResultSet rs,List<BizOut> befor) throws SQLException, IOException;
    }

    private static TableGen begin;

    static {
        begin = new OwnerBusinessGen();
        TableGen cur = begin;
        cur.setNext(new HouseBusinessGen());
        cur = cur.getNext();

        cur.setNext(new CurCloseGen());
        cur = cur.getNext();

        cur.setNext(new TaskOperGen());
        cur = cur.getNext();

//        cur.setNext(new FileGen());
//        cur = cur.getNext();

        cur.setNext(new PersonGen());
        cur = cur.getNext();

        cur.setNext(new FeeGen());
        cur = cur.getNext();

        cur.setNext(new SubstatusGen());
        cur = cur.getNext();

    }


    public static class SubstatusGen extends TableGen{

        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {
            String addStatus = null;
            boolean isAdd = true;
            if ("WP30".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP31".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP40".equals(out.defineId)){addStatus="INIT_REG";}
            else if("WP102".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP52".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP53".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP54".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP55".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP91".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP101".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP1010".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP104".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP41".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP56".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP57".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP58".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP59".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP60".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP61".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP62".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP63".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP65".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP66".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP67".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP71".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP72".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP86".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP87".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP110".equals(out.defineId)){addStatus="DESTROY";}
            else if("WP38".equals(out.defineId)){addStatus="DESTROY";}
            else if("WP9".equals(out.defineId)){addStatus="PLEDGE";}
            else if("WP12".equals(out.defineId)){isAdd=false;addStatus="PLEDGE";}
            else if("WP13".equals(out.defineId)){addStatus="PLEDGE";}
            else if("WP14".equals(out.defineId)){addStatus="PLEDGE";}
            else if("WP17".equals(out.defineId)){isAdd=false;addStatus="PLEDGE";}
            else if("WP18".equals(out.defineId)){addStatus="PROJECT_PLEDGE";}
            else if("WP21".equals(out.defineId)){isAdd=false;addStatus="PROJECT_PLEDGE";}
            else if("WP44".equals(out.defineId)){addStatus="SALE_REGISTER";}
            else if("WP46".equals(out.defineId)){isAdd=false;addStatus="SALE_REGISTER";}
            else if("WP1".equals(out.defineId)){addStatus="SALE_MORTGAGE_REGISTER";}
            else if("WP4".equals(out.defineId)){isAdd=false;addStatus="SALE_MORTGAGE_REGISTER";}
            else if("WP69".equals(out.defineId)){addStatus="DIVERT_REGISTER";}
            else if("WP70".equals(out.defineId)){isAdd=false;addStatus="DIVERT_REGISTER";}
            else if("WP5".equals(out.defineId)){addStatus="DIVERT_MORTGAGE_REGISTER";}
            else if("WP8".equals(out.defineId)){isAdd=false;addStatus="DIVERT_MORTGAGE_REGISTER";}
            else if("WPD203".equals(out.defineId)){addStatus="DESTROY";}
            else if("WP33".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP32".equals(out.defineId)){addStatus="OWNERED";}
            else if("WP73".equals(out.defineId)){addStatus="COURT_CLOSE";}
            else if("WP74".equals(out.defineId)){isAdd=false;addStatus="COURT_CLOSE";}
            else if("WP36".equals(out.defineId)){addStatus="DIFFICULTY";}
            else if("WP37".equals(out.defineId)){isAdd=false;addStatus="DIFFICULTY";}
            else if("WP23".equals(out.defineId)){addStatus="DIFFICULTY";}
            else if("WP24".equals(out.defineId)){isAdd=false;addStatus="DIFFICULTY";}
            else if("WP28".equals(out.defineId)){addStatus="DIFFICULTY";}
            else if("WP29".equals(out.defineId)){isAdd=false;addStatus="DIFFICULTY";}


            if (addStatus != null){
                out.out += "INSERT INTO ADD_HOUSE_STATUS(ID,BUSINESS,STATUS,IS_REMOVE) VALUES("
                        + Q.v(Q.p(rs.getString(55)),Q.p(rs.getString(55)),Q.p(addStatus),Q.p(isAdd)) +
                        ");";
                out.houseStatus = HouseStatus.valueOf(addStatus);
                out.isAddStatus = isAdd;
            }

        }
    }

    public static class CurCloseGen extends TableGen{

        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {
            if ("WP74".equals(out.defineId)){
                out.out += "INSERT INTO HOUSE_CLOSE_CANCEL(ID,CANCEL_DATE,BUSINESS_ID,CANCEL_DOWN_CLOUR,LEGAL_DOCUMENTS,EXECUTION_NOTICE,SEND_PEOPLE,EXECUTION_CARD_NO) VALUES("
                        + Q.v(Q.p(rs.getString(55)),Q.pm(rs.getTimestamp(36)),Q.pm(rs.getString(55)),Q.pm(rs.getString(43)),Q.p(rs.getString(44)),Q.p(rs.getString(42)),Q.p(rs.getString(7)),Q.p(rs.getString(8))) + ");";
            }else if ("WP73".equals(out.defineId)){
                out.out += "INSERT INTO CLOSE_HOUSE(ID,CLOSE_DOWN_CLOUR,CLOSE_DATE,BUSINESS_ID,LEGAL_DOCUMENTS,EXECUTION_NOTICE,SEND_PEOPLE,EXECUTION_CARD_NO) VALUES("
                        + Q.v(Q.p(rs.getString(55)),Q.pm(rs.getString(43)),Q.pm(rs.getTimestamp(36)),Q.pm(rs.getString(55)),Q.p(rs.getString(44)),Q.p(rs.getString(42)),Q.p(rs.getString(7)),Q.p(rs.getString(8))) + ");";
            }
        }
    }

    public static class TaskOperGen extends TableGen{

        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {
            if(rs.getString(65) != null && !rs.getString(65).trim().equals("")){
                out.out += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "sj"),Q.p("CARD_PRINTER"),Q.p("-"),Q.pm(rs.getString(65)),Q.pm(rs.getString(55)),Q.pm(rs.getTimestamp(66))) + ");";
            }


            if(rs.getString(11) != null && !rs.getString(11).trim().equals("")){
                out.out += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "jl"),Q.p("CREATE_EMP"),Q.p("-"),Q.pm(rs.getString(11)),Q.pm(rs.getString(55)),Q.pm(rs.getTimestamp(12))) + ");";

                out.out += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "sl"),Q.p("APPLY_EMP"),Q.p("-"),Q.pm(rs.getString(11)),Q.pm(rs.getString(55)),Q.pm(rs.getTimestamp(12))) + ");";
            }

            if(rs.getString(81) != null && !rs.getString(81).trim().equals("")){
                out.out += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "sf"),Q.p("MONEY_EMP"),Q.p("-"),Q.pm(rs.getString(81)),Q.pm(rs.getString(55)),Q.pm(rs.getTimestamp(82))) + ");";
            }

            if(rs.getString(72) != null && !rs.getString(72).trim().equals("")){
                out.out += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "db"),Q.p("REG_EMP"),Q.p("-"),Q.pm(rs.getString(72)),Q.pm(rs.getString(55)),Q.pm(rs.getTimestamp(73))) + ");";
            }

            if(rs.getString(74) != null && !rs.getString(74).trim().equals("")){
                out.out += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "sh"),Q.p("CHECK_EMP"),Q.p("-"),Q.pm(rs.getString(74)),Q.pm(rs.getString(55)),Q.pm(rs.getTimestamp(75))) + ");";
            }

            if(rs.getString(76) != null && !rs.getString(76).trim().equals("")){
                out.out += "INSERT INTO BUSINESS_EMP(ID,TYPE,EMP_CODE,EMP_NAME,BUSINESS_ID,OPER_TIME) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "cs"),Q.p("FIRST_CHECK"),Q.p("-"),Q.pm(rs.getString(76)),Q.pm(rs.getString(55)),Q.pm(rs.getTimestamp(77))) + ");";
            }

        }
    }

    public static class FileGen extends TableGen{

        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {
            if (rs.getString(78) != null && !"".equals(rs.getString(78).trim())){
                String[] files= rs.getString(78).split("\n");
                for(int i = 0 ; i < files.length; i++){
                    out.out += "INSERT INTO BUSINESS_FILE(ID,BUSINESS_ID,NAME,NO_FILE,IMPORTANT,PRIORITY) VALUES("
                            + Q.v(Q.p(rs.getString(55) + "-" + i),Q.p(rs.getString(55)), Q.p(files[i]) , "true","false",String.valueOf(i)) + ");";
                }

            }
        }
    }

    public static class FeeGen extends TableGen{

        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {
            if (rs.getBigDecimal(79) != null){
                out.out += "INSERT INTO FACT_MONEYINFO(ID,FACT_TIME,PAYMENT_NO,BUSINESS) VALUES("
                + Q.v(Q.p(rs.getString(55)),Q.pm(rs.getTimestamp(32)),Q.pm("-"),Q.p(rs.getString(55))) +
                        ");INSERT INTO BUSINESS_MONEY(ID,TYPE_NAME,MONEY_TYPE_ID,CHECK_MONEY,SHOULD_MONEY,FACT_MONEY,BUSINESS,FEE,PRI) VALUES("
                        + Q.v(Q.p(rs.getString(55)),Q.p("收费导入"),Q.p("BIZIMPORT"),Q.pm(rs.getBigDecimal(79)),Q.pm(rs.getBigDecimal(79)),Q.pm(rs.getBigDecimal(80)),Q.p(rs.getString(55)),Q.p(rs.getString(55)),String.valueOf(1)) + ");";
            }
        }
    }

    public static class PersonGen extends TableGen{


        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {

            if (!"WP73".equals(out.defineId) && !"WP74".equals(out.defineId) && rs.getString(7) != null && !"".equals(rs.getString(7).trim())){
                out.out += "INSERT INTO BUSINESS_PERSION(ID,ID_NO,ID_TYPE,NAME,TYPE,BUSINESS_ID,PHONE) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "-sp"),Q.pm(rs.getString(8)),Q.p("身份证".equals(rs.getString(9)) ? "MASTER_ID" : "OTHER"),Q.pm(rs.getString(7)),Q.p("SELL_ENTRUST"),Q.p(rs.getString(55)),Q.p(rs.getString(10))) + ");";
            }

            if (rs.getString(27) != null && !"".equals(rs.getString(27).trim())){
                out.out += "INSERT INTO BUSINESS_PERSION(ID,ID_NO,ID_TYPE,NAME,TYPE,BUSINESS_ID,PHONE) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "-zw"),
                        Q.pm(rs.getString(28)),
                        Q.p("身份证".equals(rs.getString(29)) ? "MASTER_ID" : ( "护照".equals(rs.getString(29)) ? "PASSPORT" : "OTHER") ),
                        Q.pm(rs.getString(27)),Q.p("MORTGAGE_OBLIGOR"),Q.p(rs.getString(55)),Q.p(rs.getString(30))) + ");";
            }

            if (rs.getString(38) != null && !"".equals(rs.getString(38).trim())){
                out.out += "INSERT INTO BUSINESS_PERSION(ID,ID_NO,ID_TYPE,NAME,TYPE,BUSINESS_ID,PHONE) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "-jr"),
                        Q.pm(rs.getString(40)),
                        Q.p("身份证".equals(rs.getString(39)) ? "MASTER_ID" : ( "护照".equals(rs.getString(39)) ? "PASSPORT" : "OTHER") ),
                        Q.pm(rs.getString(38)),Q.p("MORTGAGE_OBLIGEE"),Q.p(rs.getString(55)),Q.p(rs.getString(41))) + ");";
            }

            if (rs.getString(98) != null && !"".equals(rs.getString(98).trim())){
                String oType = "OWNER_ENTRUST" ;
                if (Arrays.asList(M_PERSON_BIZ_ID).contains(out.defineId)){
                    oType = "MORTGAGE";
                }

                out.out += "INSERT INTO BUSINESS_PERSION(ID,ID_NO,ID_TYPE,NAME,TYPE,BUSINESS_ID,PHONE) VALUES("
                        + Q.v(Q.p(rs.getString(55) + "-cd"),
                        Q.pm(rs.getString(15)),
                        Q.p("身份证".equals(rs.getString(16)) ? "MASTER_ID" : ( "护照".equals(rs.getString(16)) ? "PASSPORT" : "OTHER") ),
                        Q.pm(rs.getString(98)),Q.p(oType),Q.p(rs.getString(55)),Q.p(rs.getString(17))) + ");";
            }


        }

        private static final String[] M_PERSON_BIZ_ID ={
                "WP9","WP10","WP12","WP13","WP14","WP15","WP17","WP18","WP19","WP21","WP1","WP2","WP4","WP5","WP6","WP8"
        };
    }


    public static class OwnerBusinessGen extends TableGen{


        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {

            String selectKeyCode = rs.getString(63);
            if (selectKeyCode != null){
                boolean find = false;
                for(BizOut bb: befor){
                    if (bb.keyCode.equals(selectKeyCode)){
                        find = true;
                        break;
                    }
                }
                if (!find){
                    warnWriter.write(out.keyCode + " -- select code not found:" + selectKeyCode);
                    warnWriter.newLine();
                    warnWriter.flush();
                    selectKeyCode = null;
                }
            }
            out.out += " INSERT INTO OWNER_BUSINESS(ID,VERSION,SOURCE,MEMO,STATUS,DEFINE_NAME,DEFINE_ID,SELECT_BUSINESS,CREATE_TIME,APPLY_TIME,CHECK_TIME,REG_TIME,RECORD_TIME,RECORDED,TYPE) VALUES("
                    + Q.v(Q.pm(rs.getString(55)),"1",Q.pm("BIZ_IMPORT"),Q.p(rs.getString(1)), Q.pm("COMPLETE"),Q.pm(rs.getString(60)),Q.pm(out.defineId),Q.p(selectKeyCode),
                    Q.pm(rs.getTimestamp(12)),Q.pm(rs.getTimestamp(12)),Q.p(rs.getTimestamp(75)), Q.p(rs.getTimestamp(73)),Q.p(rs.getTimestamp(95)),Q.p(true), Q.pm("NORMAL_BIZ")) + ");";


            //评估
            if ((rs.getBigDecimal(83) != null) && (rs.getBigDecimal(83).compareTo(BigDecimal.ZERO) > 0))  {
                out.out += "INSERT INTO EVALUATE(ID,BUSINESS_ID,ASSESSMENT_PRICE) VALUES(" + Q.v(Q.p(rs.getString(55)),Q.p(rs.getString(55)),Q.pm(rs.getBigDecimal(83))) + ");";
            }
        }
    }


    public static class HouseBusinessGen extends TableGen{

        @Override
        protected void gensql(BizOut out, ResultSet rs, List<BizOut> befor) throws SQLException, IOException {

            String houseCode = (rs.getString(45) == null ? "" : rs.getString(45).trim()) + "-" +
                    (rs.getString(46) == null ? "" : rs.getString(46).trim()) + "-" + (rs.getString(47) == null ? "" : rs.getString(47).trim());




            String ouseType = rs.getString(51);
            String useType ;
            if (ouseType == null || ouseType.trim().equals("")){
                useType ="";
            }else if (ouseType.trim().equals("板框车间")){
                useType = "4171";
            }else if (ouseType.trim().equals("办公")){useType="788";
            }else if (ouseType.trim().equals("办公、车库")){useType="4339";
            }else if (ouseType.trim().equals("办公、车库、营房、食堂")){useType="4331";
            }else if (ouseType.trim().equals("办公、商服")){useType="4337";
            }else if (ouseType.trim().equals("办公楼")){useType="4176";
            }else if (ouseType.trim().equals("办公室")){useType="788";
            }else if (ouseType.trim().equals("包材库")){useType="4332";
            }else if (ouseType.trim().equals("泵房")){useType="4181";
            }else if (ouseType.trim().equals("泵房、办公")){useType="4336";
            }else if (ouseType.trim().equals("变电所")){useType="4172";
            }else if (ouseType.trim().equals("宾馆")){useType="92";
            }else if (ouseType.trim().equals("材料库")){useType="4247";
            }else if (ouseType.trim().equals("仓库")){useType="791";
            }else if (ouseType.trim().equals("仓库、防火库")){useType="4246";
            }else if (ouseType.trim().equals("厂房")){useType="3920";
            }else if (ouseType.trim().equals("厂房、车库")){useType="4177";
            }else if (ouseType.trim().equals("车间")){useType="4328";
            }else if (ouseType.trim().equals("车库")){useType="81";
            }else if (ouseType.trim().equals("车库、住宅")){useType="3887";
            }else if (ouseType.trim().equals("称房")){useType="4242";
            }else if (ouseType.trim().equals("成品库")){useType="4191";
            }else if (ouseType.trim().equals("成品库房")){useType="4191";
            }else if (ouseType.trim().equals("秤房")){useType="4239";
            }else if (ouseType.trim().equals("出渣间")){useType="4244";
            }else if (ouseType.trim().equals("除氧煤仓间、汽机房、锅炉房")){useType="4237";
            }else if (ouseType.trim().equals("除氧煤仓间汽机房锅炉房")){useType="4236";
            }else if (ouseType.trim().equals("传染病房")){useType="4243";
            }else if (ouseType.trim().equals("地磅房")){useType="4235";
            }else if (ouseType.trim().equals("电工室")){useType="4234";
            }else if (ouseType.trim().equals("电力车间")){useType="4199";
            }else if (ouseType.trim().equals("电站")){useType="4193";
            }else if (ouseType.trim().equals("淀粉车间")){useType="4225";
            }else if (ouseType.trim().equals("淀粉附房")){useType="4194";
            }else if (ouseType.trim().equals("淀粉主车间")){useType="4223";
            }else if (ouseType.trim().equals("动力车间")){useType="4189";
            }else if (ouseType.trim().equals("二车间")){useType="4219";
            }else if (ouseType.trim().equals("发酵车间")){useType="4201";
            }else if (ouseType.trim().equals("饭店")){useType="3918";
            }else if (ouseType.trim().equals("防火器材库")){useType="4196";
            }else if (ouseType.trim().equals("肥皂车间")){useType="4174";
            }else if (ouseType.trim().equals("风机房")){useType="4233";
            }else if (ouseType.trim().equals("服务业")){useType="800";
            }else if (ouseType.trim().equals("复合肥造粒车间")){useType="4178";
            }else if (ouseType.trim().equals("副产品车间")){useType="4182";
            }else if (ouseType.trim().equals("干燥厂房")){useType="4232";
            }else if (ouseType.trim().equals("岗楼")){useType="4195";
            }else if (ouseType.trim().equals("给水及循环水设施")){useType="4197";
            }else if (ouseType.trim().equals("工作塔")){useType="4231";
            }else if (ouseType.trim().equals("公办")){useType="4379";
            }else if (ouseType.trim().equals("供电机房")){useType="4228";
            }else if (ouseType.trim().equals("罐气车间")){useType="4183";
            }else if (ouseType.trim().equals("锅炉房")){useType="4173";
            }else if (ouseType.trim().equals("锅炉房、办公")){useType="4335";
            }else if (ouseType.trim().equals("锅炉房、车库")){useType="4169";
            }else if (ouseType.trim().equals("合成车间")){useType="4179";
            }else if (ouseType.trim().equals("化学水处理室、办公室")){useType="4333";
            }else if (ouseType.trim().equals("化学水处理室办公室")){useType="4333";
            }else if (ouseType.trim().equals("化学水处理站")){useType="4330";
            }else if (ouseType.trim().equals("会议室")){useType="4175";
            }else if (ouseType.trim().equals("机房")){useType="4380";
            }else if (ouseType.trim().equals("机房、车库")){useType="4381";
            }else if (ouseType.trim().equals("加油站")){useType="3882";
            }else if (ouseType.trim().equals("简易")){useType="4371";
            }else if (ouseType.trim().equals("浸出车间")){useType="3877";
            }else if (ouseType.trim().equals("浸泡车间")){useType="4184";
            }else if (ouseType.trim().equals("精炼车间")){useType="3880";
            }else if (ouseType.trim().equals("井房")){useType="4372";
            }else if (ouseType.trim().equals("酒精车间")){useType="4185";
            }else if (ouseType.trim().equals("俱乐部")){useType="4382";
            }else if (ouseType.trim().equals("颗粒柏车间")){useType="4186";
            }else if (ouseType.trim().equals("颗粒粕车间")){useType="4187";
            }else if (ouseType.trim().equals("控制室")){useType="4373";
            }else if (ouseType.trim().equals("库房")){useType="4217";
            }else if (ouseType.trim().equals("库房、住宅")){useType="4383";
            }else if (ouseType.trim().equals("库房楼")){useType="4384";
            }else if (ouseType.trim().equals("垃圾站")){useType="4385";
            }else if (ouseType.trim().equals("冷库")){useType="4374";
            }else if (ouseType.trim().equals("礼仪厅")){useType="4375";
            }else if (ouseType.trim().equals("磷霉素纳车间")){useType="4188";
            }else if (ouseType.trim().equals("磷霉素钠车间")){useType="4188";
            }else if (ouseType.trim().equals("磷脂车间")){useType="4190";
            }else if (ouseType.trim().equals("硫酸控制室")){useType="4376";
            }else if (ouseType.trim().equals("门卫")){useType="4220";
            }else if (ouseType.trim().equals("门卫室")){useType="4220";
            }else if (ouseType.trim().equals("门诊办公楼")){useType="4329";
            }else if (ouseType.trim().equals("南门卫室")){useType="4386";
            }else if (ouseType.trim().equals("配电室")){useType="4221";
            }else if (ouseType.trim().equals("配电室、电工室")){useType="4387";
            }else if (ouseType.trim().equals("粕车间")){useType="4218";
            }else if (ouseType.trim().equals("葡萄糖车间")){useType="4192";
            }else if (ouseType.trim().equals("启动锅炉房")){useType="4343";
            }else if (ouseType.trim().equals("清选间")){useType="4344";
            }else if (ouseType.trim().equals("燃料车间")){useType="4216";
            }else if (ouseType.trim().equals("溶媒车间")){useType="4215";
            }else if (ouseType.trim().equals("商店")){useType="4364";
            }else if (ouseType.trim().equals("商服")){useType="785";
            }else if (ouseType.trim().equals("商服、办公")){useType="4365";
            }else if (ouseType.trim().equals("商服、车库")){useType="4366";
            }else if (ouseType.trim().equals("商业服务站")){useType="4388";
            }else if (ouseType.trim().equals("上料间1")){useType="4367";
            }else if (ouseType.trim().equals("上料间2")){useType="4368";
            }else if (ouseType.trim().equals("生产车间")){useType="4224";
            }else if (ouseType.trim().equals("生化厂空压机房")){useType="4389";
            }else if (ouseType.trim().equals("生化合成车间")){useType="4212";
            }else if (ouseType.trim().equals("生活楼")){useType="4369";
            }else if (ouseType.trim().equals("食堂")){useType="4229";
            }else if (ouseType.trim().equals("食堂、车库")){useType="4390";
            }else if (ouseType.trim().equals("食杂店")){useType="4391";
            }else if (ouseType.trim().equals("守卫室")){useType="4392";
            }else if (ouseType.trim().equals("水泵房")){useType="4393";
            }else if (ouseType.trim().equals("水房")){useType="4394";
            }else if (ouseType.trim().equals("水暖车间")){useType="4209";
            }else if (ouseType.trim().equals("水塔")){useType="4370";
            }else if (ouseType.trim().equals("四车间")){useType="4208";
            }else if (ouseType.trim().equals("碎煤机室")){useType="4377";
            }else if (ouseType.trim().equals("托儿所")){useType="4395";
            }else if (ouseType.trim().equals("危险品库")){useType="4378";
            }else if (ouseType.trim().equals("污水车间")){useType="4206";
            }else if (ouseType.trim().equals("污水处理车间")){useType="4204";
            }else if (ouseType.trim().equals("污水处理站")){useType="4203";
            }else if (ouseType.trim().equals("西门卫室")){useType="4396";
            }else if (ouseType.trim().equals("消防泵房")){useType="4397";
            }else if (ouseType.trim().equals("写字楼")){useType="4398";
            }else if (ouseType.trim().equals("新发酵车间")){useType="4207";
            }else if (ouseType.trim().equals("新分汽站")){useType="4345";
            }else if (ouseType.trim().equals("新空压站")){useType="4346";
            }else if (ouseType.trim().equals("新四效车间")){useType="4202";
            }else if (ouseType.trim().equals("循环水泵房")){useType="4347";
            }else if (ouseType.trim().equals("压缩机房")){useType="4348";
            }else if (ouseType.trim().equals("业务楼")){useType="4399";
            }else if (ouseType.trim().equals("业务室")){useType="4400";
            }else if (ouseType.trim().equals("一车间")){useType="4349";
            }else if (ouseType.trim().equals("医疗卫生")){useType="4401";
            }else if (ouseType.trim().equals("引风机室1")){useType="4350";
            }else if (ouseType.trim().equals("引风机室2")){useType="4351";
            }else if (ouseType.trim().equals("印刷厂")){useType="4352";
            }else if (ouseType.trim().equals("营房")){useType="4238";
            }else if (ouseType.trim().equals("营业")){useType="82";
            }else if (ouseType.trim().equals("营业室")){useType="4402";
            }else if (ouseType.trim().equals("营业厅")){useType="4403";
            }else if (ouseType.trim().equals("预处理车间")){useType="4341";
            }else if (ouseType.trim().equals("员工宿舍")){useType="4404";
            }else if (ouseType.trim().equals("原粮库")){useType="4353";
            }else if (ouseType.trim().equals("原料仓库")){useType="4354";
            }else if (ouseType.trim().equals("原料科")){useType="4355";
            }else if (ouseType.trim().equals("原料科办公室")){useType="4250";
            }else if (ouseType.trim().equals("原料库")){useType="4354";
            }else if (ouseType.trim().equals("站房")){useType="4356";
            }else if (ouseType.trim().equals("招待所")){useType="4405";
            }else if (ouseType.trim().equals("制油车间")){useType="4340";
            }else if (ouseType.trim().equals("质检中心")){useType="4406";
            }else if (ouseType.trim().equals("中和水泵房")){useType="4357";
            }else if (ouseType.trim().equals("中转库")){useType="4358";
            }else if (ouseType.trim().equals("主厂房")){useType="4359";
            }else if (ouseType.trim().equals("主楼")){useType="4360";
            }else if (ouseType.trim().equals("住院处")){useType="4361";
            }else if (ouseType.trim().equals("住宅")){useType="80";
            }else if (ouseType.trim().equals("住宅、车库")){useType="4407";
            }else if (ouseType.trim().equals("住宅、车库、商服")){useType="4408";
            }else if (ouseType.trim().equals("住宅、库房")){useType="4409";
            }else if (ouseType.trim().equals("住宅、商服")){useType="789";
            }else if (ouseType.trim().equals("资料库")){useType="4362";
            }else if (ouseType.trim().equals("综合库房")){useType="4363";
            }else if (ouseType.trim().equals("综合楼")){useType="4245";}else{
                useType = "";
            }

            String ostructure = rs.getString(48);
            String structure;
            if (ostructure == null || ostructure.trim().equals("")){
                structure = "827";
            }else if (ostructure.trim().equals("钢")){
                structure = "88";
            }else if (ostructure.trim().equals("钢、钢混")){structure="825";
            }else if (ostructure.trim().equals("钢、混合")){structure="826";
            }else if (ostructure.trim().equals("钢混")){structure="823";
            }else if (ostructure.trim().equals("钢结构")){structure="88";
            }else if (ostructure.trim().equals("混合")){structure="824";
            }else if (ostructure.trim().equals("混合、钢混")){structure="888";
            }else if (ostructure.trim().equals("混合、钢结构")){structure="826";
            }else if (ostructure.trim().equals("混合、砖木")){structure="889";
            }else if (ostructure.trim().equals("混合砖木")){structure="889";
            }else if (ostructure.trim().equals("简易")){structure="890";
            }else if (ostructure.trim().equals("其它")){structure="827";
            }else if (ostructure.trim().equals("砖混")){structure="1559";
            }else if (ostructure.trim().equals("砖木")){structure="891";
            }else if (ostructure.trim().equals("砖木、钢、混合")){structure="892";
            }else if (ostructure.trim().equals("砖木、混合")){structure="893";}else {
                structure = "827";
            }

            String ofloorCount = rs.getString(49);
            int floorCount = 0;
            if (ofloorCount == null || ofloorCount.trim().equals("")){
                floorCount = 0;
            }else{
                ofloorCount = ofloorCount.replace("层","").trim();

                floorCount = chineseNumber2Int(ofloorCount);
            }

            String oPoolMemo = rs.getString(64);
            String poolMemo;
            if (oPoolMemo == null || oPoolMemo.trim().equals("")){
                poolMemo = null;
            }else if (oPoolMemo.trim().equals("按份共有")){
                poolMemo = "SHARE_OWNER";
            }else if (oPoolMemo.trim().equals("单独所有")){
                poolMemo = "SINGLE_OWNER";
            }else if (oPoolMemo.trim().equals("共同共有")){
                poolMemo = "TOGETHER_OWNER";
            }else{
                poolMemo = null;
            }

            // ----- start house
            String oOwnerID = null;
            if (rs.getString(3) != null && !rs.getString(3).trim().equals("")){
                oOwnerID = rs.getString(55)  + "-s";
                String cardId = null;
                if (rs.getString(2) != null && !"".equals(rs.getString(2))) {
                    out.out += "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" + Q.v(Q.p(rs.getString(55) + "-s"), Q.pm(rs.getString(2)), Q.p("OWNER_RSHIP"), Q.p(rs.getString(55)), "true") + "); " +
                            " INSERT INTO CARD_INFO(ID,MEMO) VALUES(" + Q.v( Q.p(rs.getString(55) + "-s") , Q.p(rs.getString(69)))+ ");";
                    cardId = rs.getString(55) + "-s";
                }

                out.out += "INSERT INTO BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,BUSINESS,OWNER_CARD) VALUES(" + Q.v(Q.p(rs.getString(55)  + "-s"), Q.pm(rs.getString(3)), Q.pm("身份证".equals(rs.getString(5)) ? "MASTER_ID" : "OTHER") ,
                            Q.pm(rs.getString(4)),Q.p(rs.getString(55)),Q.p(cardId)) + ");";

            }

            out.out += "INSERT INTO HOUSE(ID,HOUSE_ORDER,IN_FLOOR_NAME,HOUSE_AREA,USE_AREA,COMM_AREA,USE_TYPE,STRUCTURE,ADDRESS,HOUSE_CODE,HAVE_DOWN_ROOM,BUILD_CODE,BLOCK_NO,BUILD_NO,UP_FLOOR_COUNT,FLOOR_COUNT,DOWN_FLOOR_COUNT,PROJECT_CODE,PROJECT_NAME,DEVELOPER_CODE,DEVELOPER_NAME,SECTION_CODE,SECTION_NAME,DISTRICT_CODE,DISTRICT_NAME,BUILD_NAME,POOL_MEMO,MAIN_OWNER) " +
                    " VALUES(" +  Q.v(Q.p(rs.getString(55)  + "-s"), Q.pm(rs.getString(47)), Q.pm(rs.getString(50)), Q.pm(rs.getBigDecimal(19)),Q.p(rs.getBigDecimal(20)), Q.p(rs.getBigDecimal(53)),Q.pm(useType),Q.pm(structure),Q.pm(rs.getString(62)),Q.pm(houseCode),
                    Q.p(false),Q.p("-"),Q.pm(rs.getString(45)),Q.pm(rs.getString(46)),String.valueOf(floorCount),String.valueOf(floorCount),"0",Q.p("-"),Q.p("-"),Q.p("-"),Q.p(rs.getString(35)),Q.p("-"),Q.p("-"),Q.p("-"),Q.pm(rs.getString(70)),Q.p("-"),Q.p(poolMemo), Q.p(oOwnerID)) + ");";


            //-------


            //----- after house

            oOwnerID = null;

            if(rs.getString(56) != null && !rs.getString(56).trim().equals("")){
                String cardId = null;
                if (rs.getString(68) != null && !rs.getString(68).trim().equals("")){
                    cardId = rs.getString(55) + "-o";
                    out.out += "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" + Q.v(Q.p(cardId), Q.pm(rs.getString(68)), Q.p( Arrays.asList(PREPARE_BIZ).contains(out.defineId) ? "NOTICE" : (Arrays.asList(MO_PREPARE_BIZ).contains(out.defineId) ? "NOTICE_MORTGAGE" : "OWNER_RSHIP") ), Q.p(rs.getString(55)), "true") + "); " +
                            " INSERT INTO CARD_INFO(ID,CODE,MEMO) VALUES(" + Q.v(Q.p(cardId),Q.p(rs.getString(71)) ,Q.p(rs.getString(69)) ) + ");";
                }

                String oCardType = rs.getString(58);
                String CardType;
                if (oCardType == null || "".equals(oCardType.trim())){
                    CardType = "OTHER";
                }else if (oCardType.trim().equals("护照")){
                    CardType = "PASSPORT";
                }else if (oCardType.trim().equals("身份证")){
                    CardType = "MASTER_ID";
                }else if (oCardType.trim().equals("士兵证号")){
                    CardType = "SOLDIER_CARD";
                }else if (oCardType.trim().equals("组织机构代码证")){
                    CardType = "CORP_CODE";
                }else{
                    CardType = "OTHER";
                }
                out.out += "INSERT INTO BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,BUSINESS,OWNER_CARD,ROOT_ADDRESS) VALUES(" + Q.v(Q.p(rs.getString(55)), Q.pm(rs.getString(56)), Q.pm(CardType) ,
                        Q.pm(rs.getString(57)),Q.p(rs.getString(55)),Q.p(cardId),Q.p(rs.getString(37))) + ");";


                //抵押业务
                if (Arrays.asList(MO_BIZIDS).contains(out.defineId)){

                    String oldCardId = null;

                        if (Arrays.asList(OLD_CARD_BIN).contains(out.defineId)) {
                            if (rs.getString(25) != null &&  !"".equals(rs.getString(25).trim())) {
                                out.out += "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" + Q.v(Q.p(rs.getString(55) + "-t"), Q.pm(rs.getString(25)), Q.p( "MORTGAGE_CARD" ), Q.p(rs.getString(55)), "true") + "); " +
                                        " INSERT INTO CARD_INFO(ID,CODE,MEMO) VALUES(" + Q.v(Q.p(cardId),Q.p(rs.getString(71)) ,Q.p(rs.getString(69))) + ");";
                                oldCardId = rs.getString(55) + "-t";
                            }
                        } else {
                            if (rs.getString(67) != null &&  !"".equals(rs.getString(67).trim())) {
                                out.out += "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" + Q.v(Q.p(rs.getString(55) + "-t"), Q.pm(rs.getString(67)), Q.p( "MORTGAGE_CARD" ), Q.p(rs.getString(55)), "true") + "); " +
                                        " INSERT INTO CARD_INFO(ID,CODE,MEMO) VALUES(" + Q.v(Q.p(cardId),Q.p(rs.getString(71)) ,Q.p(rs.getString(69))) + ");";
                                oldCardId = rs.getString(55) + "-t";
                            }
                        }


                    if (rs.getString(21) == null || rs.getString(21).trim().equals("")){
                        out.out += "INSERT INTO FINANCIAL(ID,NAME,CODE,FINANCIAL_TYPE,ID_TYPE,CREATE_TIME,CARD) VALUES(" +
                                Q.v(Q.p(rs.getString(55)),Q.pm(rs.getString(21)),Q.pm(rs.getString(4)),Q.p("FINANCE_CORP"),Q.p("身份证".equals(rs.getString(5)) ? "MASTER_ID" : "OTHER"),Q.p(rs.getTimestamp(95)),Q.p(oldCardId)) + ");";
                    }else {
                        out.out += "INSERT INTO FINANCIAL(ID,NAME,CODE,FINANCIAL_TYPE,ID_TYPE,CREATE_TIME,CARD) VALUES(" +
                                Q.v(Q.p(rs.getString(55)),Q.pm("-"),Q.pm(rs.getString(4)),Q.p("FINANCE_CORP"),Q.p("身份证".equals(rs.getString(5)) ? "MASTER_ID" : "OTHER"),Q.p(rs.getTimestamp(95)),Q.p(oldCardId)) + ");";
                    }

                    out.out += "INSERT INTO MORTGAEGE_REGISTE(HIGHEST_MOUNT_MONEY,WARRANT_SCOPE,INTEREST_TYPE,MORTGAGE_DUE_TIME_S,MORTGAGE_TIME,MORTGAGE_AREA,TIME_AREA_TYPE,ID,BUSINESS_ID,FIN,OWNER,ORG_NAME) VALUES(" +
                            Q.v(Q.pm(rs.getBigDecimal(87)),Q.p(rs.getString(34)),Q.p(rs.getString(26)),Q.pm(rs.getTimestamp(32)),Q.pm(rs.getTimestamp(33)),Q.pm(rs.getBigDecimal(54)),Q.p("DATE_TIME"),Q.p(rs.getString(55)),Q.p(rs.getString(55)),Q.p(rs.getString(55)),Q.p("明水县房产管理处")) + ");";
                }

            }





            out.out += "INSERT INTO HOUSE(ID,HOUSE_ORDER,IN_FLOOR_NAME,HOUSE_AREA,USE_AREA,COMM_AREA,USE_TYPE,STRUCTURE,ADDRESS,HOUSE_CODE,HAVE_DOWN_ROOM,BUILD_CODE,BLOCK_NO,BUILD_NO,UP_FLOOR_COUNT,FLOOR_COUNT,DOWN_FLOOR_COUNT,PROJECT_CODE,PROJECT_NAME,DEVELOPER_CODE,DEVELOPER_NAME,SECTION_CODE,SECTION_NAME,DISTRICT_CODE,DISTRICT_NAME,BUILD_NAME,POOL_MEMO,MAIN_OWNER) " +
                    " VALUES(" +  Q.v(Q.p(rs.getString(55) ), Q.pm(rs.getString(47)), Q.pm(rs.getString(50)), Q.pm(rs.getBigDecimal(19)),Q.p(rs.getBigDecimal(20)), Q.p(rs.getBigDecimal(53)),Q.pm(useType),Q.pm(structure),Q.pm(rs.getString(62)),Q.pm(houseCode),
                    Q.p(false),Q.p("-"),Q.pm(rs.getString(45)),Q.pm(rs.getString(46)),String.valueOf(floorCount),String.valueOf(floorCount),"0",Q.p("-"),Q.p("-"),Q.p("-"),Q.p(rs.getString(35)),Q.p("-"),Q.p("-"),Q.p("-"),Q.pm(rs.getString(70)),Q.p("-"),Q.p(poolMemo), Q.p(oOwnerID)) + ");";

            Statement statement = conn.createStatement();
            ResultSet hs = statement.executeQuery("select keycode as '业务ID', gy_gongyouzheng as '共有权证号',gy_ren as '共有权人姓名', gy_beizhu as '共有权证备注',gy_fener as '所占份额'," +
                    "gy_guanxi as '共有关系',gy_card as '共有权人身份证号',gy_zhengshuhao as '共有权证证书编号'" +
                    " from c_gongyou where keycode =" + Q.p(rs.getString(55)));
            int i = 0;
            while (hs.next()){
                String cardId = null;
                if (hs.getString(2) != null && !hs.getString(2).trim().equals("")){

                    out.out += "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" + Q.v(Q.p(rs.getString(55) + "-" + i), Q.pm(hs.getString(2)), Q.p( "POOL_RSHIP" ), Q.p(rs.getString(55)), "true") + ") " +
                            " INSERT INTO CARD_INFO(ID,CODE,MEMO) VALUES(" + Q.v(Q.p(rs.getString(55) + "-" + i),Q.p(hs.getString(8)), Q.p(hs.getString(4)) ) + ");";
                }
                out.out += "INSERT INTO BUSINESS_POOL(ID,NAME,ID_TYPE,ID_NO,RELATION,PERC,CREATE_TIME,MEMO,BUSINESS,CARD) VALUES(" +
                        Q.v(Q.p(rs.getString(55) + "-g-" + i),Q.pm(hs.getString(3)),Q.p(hs.getString("MASTER_ID")),Q.pm(hs.getString(7)),Q.p(hs.getString(5)),Q.p(rs.getTimestamp(95)),Q.p(hs.getString(4)),Q.p(rs.getString(55)),Q.p(cardId) ) + ");" +
                        " INSERT INTO HOUSE_POOL(HOUSE,POOL) VALUES(" +
                        Q.v(Q.p(rs.getString(55)),Q.p(rs.getString(55) + "-g-" + i)) +");";


                i++;
            }

            if ((rs.getString(18) != null && !rs.getString(18).trim().equals("")) ||  (rs.getString(52) != null && !"".equals(rs.getString(52).trim())) ){

                String op;
                if (rs.getString(18) != null && !rs.getString(18).trim().equals("")){

                    if ("港澳台胞房产".equals(rs.getString(18).trim())) {
                        op = "822";
                    } else if ("个人独资".equals(rs.getString(18).trim())) {op = "811";
                    } else if ("公产".equals(rs.getString(18).trim())) {op = "818";
                    } else if ("股份制".equals(rs.getString(18).trim())) {op = "817";
                    } else if ("股份制房产".equals(rs.getString(18).trim())) {op = "817";
                    } else if ("国有".equals(rs.getString(18).trim())) {op = "820";
                    } else if ("回迁".equals(rs.getString(18).trim())) {op = "821";
                    } else if ("集体产".equals(rs.getString(18).trim())) {op = "812";
                    } else if ("集体所有制".equals(rs.getString(18).trim())) {op = "812";
                    } else if ("军产".equals(rs.getString(18).trim())) {op = "86";
                    } else if ("其它".equals(rs.getString(18).trim())) {op = "909";
                    } else if ("全民自管产".equals(rs.getString(18).trim())) {op = "85";
                    } else if ("三层".equals(rs.getString(18).trim())) {op = "909";
                    } else if ("涉外产".equals(rs.getString(18).trim())) {op = "814";
                    } else if ("私产".equals(rs.getString(18).trim())) {op = "87";
                    } else if ("一层".equals(rs.getString(18).trim())) {op = "909";
                    } else {op = "909";}
                }else{
                   op = "909";
                }

                String hf;
                if (rs.getString(52) != null && !"".equals(rs.getString(52).trim())){
                    if ("变更".equals(rs.getString(52).trim())) {hf = "860";}
                    else if ("补照".equals(rs.getString(52).trim())){ hf ="843"; }
                        else if ("补证".equals(rs.getString(52).trim())){ hf ="843"; }
                        else if ("财产分割".equals(rs.getString(52).trim())){ hf ="4075";}
                        else if ("裁定".equals(rs.getString(52).trim())){ hf ="834"; }
                        else if ("查封".equals(rs.getString(52).trim())){ hf ="4066"; }
                        else if ("法院裁决".equals(rs.getString(52).trim())){ hf ="834"; }
                        else if ("翻建".equals(rs.getString(52).trim())){ hf ="829"; }
                        else if ("翻扩建".equals(rs.getString(52).trim())){ hf ="829"; }
                        else if ("房改".equals(rs.getString(52).trim())){ hf ="4067"; }
                        else if ("分割".equals(rs.getString(52).trim())){ hf ="4068"; }
                        else if ("分照".equals(rs.getString(52).trim())){ hf ="4069"; }
                        else if ("更正".equals(rs.getString(52).trim())){ hf ="847"; }
                        else if ("购买".equals(rs.getString(52).trim())){ hf ="828"; }
                        else if ("合照".equals(rs.getString(52).trim())){ hf ="4064"; }
                        else if ("换照".equals(rs.getString(52).trim())){ hf ="844"; }
                        else if ("换证".equals(rs.getString(52).trim())){ hf ="844"; }
                        else if ("回迁".equals(rs.getString(52).trim())){ hf ="92"; }
                        else if ("集资".equals(rs.getString(52).trim())){ hf ="4070"; }
                        else if ("继承".equals(rs.getString(52).trim())){ hf ="93"; }
                        else if ("继承、赠予".equals(rs.getString(52).trim())){ hf ="4071"; }
                        else if ("交换".equals(rs.getString(52).trim())){ hf ="4072"; }
                        else if ("交易".equals(rs.getString(52).trim())){ hf ="828"; }
                        else if ("灭籍".equals(rs.getString(52).trim())){ hf ="4073"; }
                        else if ("拍卖".equals(rs.getString(52).trim())){ hf ="4074"; }
                        else if ("判决".equals(rs.getString(52).trim())){ hf ="834"; }
                        else if ("其他".equals(rs.getString(52).trim())){ hf ="4063"; }
                        else if ("商品".equals(rs.getString(52).trim())){ hf ="830"; }
                        else if ("协议离婚".equals(rs.getString(52).trim())){ hf ="840"; }
                        else if ("新建".equals(rs.getString(52).trim())){ hf ="4065"; }
                        else if ("赠予".equals(rs.getString(52).trim())){ hf ="833"; }
                        else if ("赠与".equals(rs.getString(52).trim())){ hf ="833"; }
                        else if ("自建".equals(rs.getString(52).trim())){ hf ="91"; }
                    else{hf = "4063";}

                }else{
                    hf = "4063";
                }

                out.out += "INSERT INTO HOUSE_REG_INFO(ID,HOUSE_PORPERTY,HOUSE_FROM) VALUES(" +
                        Q.v(Q.p(rs.getString(55)),Q.p(op),Q.p(hf)) + ");";
            }


            if (rs.getString(88) != null && !rs.getString(88).trim().equals("")){
                String oget;
                if (rs.getString(90) != null && !rs.getString(90).trim().equals("")){
                    if ("划拨".equals(rs.getString(90).trim())){
                        oget = "181";
                    }else if ("租赁".equals(rs.getString(90).trim())){
                        oget = "183";
                    }else {
                        oget = "185";
                    }
                }else{
                    oget = "185";
                }

                out.out += "INSERT INTO LAND_INFO(ID,LAND_CARD_NO,NUMBER,LAND_PROPERTY,BEGIN_USE_TIME,END_USE_TIME,LAND_GET_MODE,LAND_AREA) VALUES("
                + Q.v(Q.p(rs.getString(55)),Q.p(rs.getString(6)),Q.p(rs.getString(88)),Q.p("870"),Q.p(rs.getTimestamp(91)),Q.pm(rs.getTimestamp(92)),Q.p(oget),Q.p(rs.getBigDecimal(93))) + ");";
            }



            //---------------

            out.out += " INSERT INTO BUSINESS_HOUSE(ID,HOUSE_CODE,BUSINESS_ID,START_HOUSE,AFTER_HOUSE,CANCELED) VALUES("
                    + Q.v(Q.pm(rs.getString(55)),Q.pm(houseCode),Q.pm(rs.getString(55)),Q.pm(rs.getString(55)  + "-s"),Q.pm(rs.getString(55)),Q.p(false)) + ");";

        }

        //抵押业务
        private static final String[] MO_BIZIDS= {"WP9","WP10","WP12","WP13","WP14","WP15","WP17","WP18","WP19","WP21","WP1","WP2","WP4","WP5","WP6","WP8"};


        private static final String[] OWNER_CARD_BIZ ={
               "WP30","WP31","WP40","WP1010","WP101","WP41","WP56","WP59","WP58","WP60","WP86","WP87","WP90","WP72","WP61","WP62","WP63","WP65","WP66","WP67","WP99","WP57","WP71","WP52","WP102","WP53","WP91","WP54","WP55","WP33","WP32"
        };

        //预告业务
        private static final String[]  PREPARE_BIZ= {"WP103","WP44","WP45","WP69","WP111"};

        //预抵业务
        private static final String[] MO_PREPARE_BIZ= {"WP1","WP2","WP5","WP6"};


        //取原他项权证业务
        private static final String[] OLD_CARD_BIN = {"WP12","WP17","WP21","WP4","WP8"};
    }




    private static int chineseNumber2Int(String chineseNumber){
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        char[] cnArr = new char[]{'一','二','三','四','五','六','七','八','九'};
        char[] chArr = new char[]{'十','百','千','万','亿'};
        for (int i = 0; i < chineseNumber.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNumber.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNumber.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }


}
