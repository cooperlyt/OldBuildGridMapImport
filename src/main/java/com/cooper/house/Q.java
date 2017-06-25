package com.cooper.house;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cooper on 10/13/15.
 */
public class Q {


    public static String p(boolean v){
        if (v){
            return "TRUE";
        }else
            return "FALSE";
    }

    public static String filterErrorChar(String s){
        char[] cc = s.toCharArray();
        for(int i = 0; i < cc.length; i++){
            if(cc[i] == 0)
                cc[i] = ' ';
        }
        return String.valueOf(cc);
    }

    public static String p(String s){
        if (s == null) {
            return "NULL";
        }else{
            return "'" + filterErrorChar(s) + "'";
        }
    }

    public static String pm(String s){
        if (s == null) {
            return "'未知'";
        }else{
            return "'" + filterErrorChar(s) + "'";
        }
    }

    public static String pm1(String s){
        if (s == null || s.equals("")) {
            return "'未知'";
        }else{
            return "'" + filterErrorChar(s) + "'";
        }
    }

    public static String pmZc(String s){
        if (s == null || s.equals("")) {
            return "'0'";
        }else{
            return "'" + filterErrorChar(s) + "'";
        }
    }

    public static String pmId(String s){
        if (s == null) {
            return "''";
        }else{
            return "'" + s + "'";
        }
    }

    public static String p(java.sql.Timestamp d){
        if (d == null){
            return "NULL";
        }else{
            return "'" + d.toString() + "'";
        }
    }

    public static String pm(java.sql.Timestamp d){
        if (d == null){
            return "'1801-1-1'";
        }else{

            return "'" + d.toString() + "'";
        }
    }

    public static String pm(BigDecimal b){
        if (b == null || b.equals("")) {
            return "0";
        }else{
            return b.stripTrailingZeros().toPlainString();
        }
    }

    public static String p(BigDecimal b){
        if (b == null || b.equals("")){
            return "NULL";
        }else{
            return b.stripTrailingZeros().toPlainString();
        }
    }

    public static String pmw(String s , String dv){
        if (s == null){
            return "'" + dv + "'";
        }else{
            return "'" + s + "'";
        }
    }

    public static String pmwc(String s){
        if (s == null || s.trim().equals("2773") || s.trim().equals("205")){
            return "NULL";
        }else{
            return "'" + s + "'";
        }
    }

    public static String pCardType(int s){
        if( s==4 ){
            return "'MASTER_ID'";
        }else if( s==5 ){
            return "'SOLDIER_CARD'";
        }else if( s==6 ){
            return "'PASSPORT'";
        }else if( s==208 ){
            return "'OTHER'";
        }else if( s==1000 ) {
            return "'OTHER'";
        } else
            return "'OTHER'";
    }

    public static String changeHouseType(int HouseType){

        if( HouseType==205){
            return "'SALE_HOUSE'";
        }else if( HouseType==1946){
            return "'BACK_HOUSE'";
        }else if( HouseType==206){
            return "'WELFARE_HOUSE'";
        }else if( HouseType==2773 || HouseType==784){
            return "'OTHER'";
        }else if(HouseType==781) {
            return "'GOV_SALE_HOUSE'";
        } else if(HouseType==782){
            return "'GOV_RENT'";
        }else if(HouseType==783){
            return "'GROUP_HOUSE'";
        }else
            return "'SALE_HOUSE'";

    }
    public static String changeHouseTypeFc(int ywid){
        if( ywid==61){
            return "'BACK_HOUSE'";
        }else if(ywid==142){
            return "'GROUP_HOUSE'";
        }else if(ywid==300){
            return "'SELF_CREATE'";
        }
            return "'SALE_HOUSE'";
    }

    public static String changePayType(int HouseType){
        if( HouseType==177 ){
            return "'ALL_PAY'";
        }else if( HouseType==178){
            return "'DEBIT_PAY'";
        }else if( HouseType==179){
            return "'PART_PAY'";
        }else
            return "NULL";
    }


    public static String changePoolMemo(int poolmemo){
        if( poolmemo==218){
            return "'TOGETHER_OWNER'";
        }else if( poolmemo==219){
            return "'SHARE_OWNER'";
        }else if( poolmemo==221){
            return "'SINGLE_OWNER'";
        }else if (poolmemo==222){
            return "'TOGETHER_OWNER'";
        }else
            return "NULL";
    }
    public static String changeBusinessEmpType(String jdname){
        if (jdname.equals("受理")){
            return "'APPLY_EMP'";
        }else if (jdname.equals("复审")){
            return "'FIRST_CHECK'";
        }else if (jdname.equals("审批")){
            return "'CHECK_EMP'";
        }else if (jdname.equals("归档")){
            return "'RECORD_EMP'";
        }else {
            return "'未知'";
        }
    }




    public static String lockHouseDescription(int HouseState){

        if( HouseState==99 ){
            return "'在老系统中房屋状态为：查封'";
        }else if( HouseState==116){
            return "'在老系统中房屋状态为：房屋已注销(灭籍)'";
        }else if( HouseState==115){
            return "'在老系统中房屋状态为：声明作废'";
        }else if( HouseState==119){
            return "'在老系统中房屋状态为：异议'";
        }else if(HouseState==890) {
            return "'在老系统中房屋状态为：在建工程抵押'";
        } else if(HouseState==117){
            return "'在老系统中房屋状态为：房屋状态为抵押'";
        }else if(HouseState==127) {
            return "'在老系统中房屋状态为：房屋状态为不可售'";
        }else if(HouseState==132) {
            return "'在老系统中房屋状态为：房屋状态为预告商品房抵押'";
        }else if(HouseState==133) {
            return "'在老系统中房屋状态为：房屋状态为房屋转移预告抵押'";
        }else {
            return "'未知'";
        }

    }

    public static String defineName(String defineID){

        if( defineID.equals("WP42") ){
            return "'商品房合同备案登记'";
        }else if( defineID.equals("WP43")){
            return "'撤销商品房合同备案登记'";
        }else
            return "'未知'";

    }

    public static String changeUseType(int UseType){

        if( UseType==0){
            return "'未知'";
        }else
            return "'"+String.valueOf(UseType)+"'";

    }

    public static String changeUseType(String UseType){

        if( UseType==null || UseType.equals("")){
            return "'未知'";
        }else
            return "'"+UseType+"'";

    }
    public static String changeDesignUseType(String DesignUseType){

        if(DesignUseType==null || DesignUseType.equals("")){
            return "'OTHER'";
        }else
            return "'"+DesignUseType+"'";

    }
    public static String changeCardType(int Type) {
        if (Type == 111) {
            return "OWNER_RSHIP";
        }else if(Type == 110){
            return "MORTGAGE";
        }else if (Type == 198) {
            return "NOTICE_MORTGAGE";
        }else if (Type == 214) {
            return "PROJECT_MORTGAGE";
        }else if (Type == 3849) {
            return "OTHER_CARD";
        }else{
            return "OTHER_CARD";
        }

    }

    public static String changeStructure(int Structure){

        if( Structure==0){
            return "'未知'";
        }else
            return "'"+String.valueOf(Structure)+"'";

    }

    public static String v(String... values){
        String result = null;
        for(String value: values){
            if (result == null){
                result = value;
            }else{
                result += "," + value;
            }
        }
        return result;

    }
    public static String nowFormatTime(){
        Date createTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(createTime);

    }

    public static String interest_type(String type){
        if (type ==null){
            return "'power.type.other'";
        }
        if (type.equals("按揭贷款")){
            return "'121'";
        }else if (type.equals("抵押贷款")){
            return "'122'";
        }else{
            return "'power.type.other'";
        }
    }

    public static String changeDefineID(int id) {
        String wpid=null;
//        System.out.println("aaa-"+id);
        switch (id){
            case 11 : wpid="WP41";break;//商品房交易
            case 21 : wpid="WP56";break;//二手房交易
            case 808 : wpid="WP68";break;//分照交易
            case 250 : wpid="WP64";break;//使用权交易
            case 43 : wpid="WP243";break;//房屋调拨
            case 41 : wpid="WP58";break;//赠与
            case 42 : wpid="WP59";break;//继承
            case 91 : wpid="WP60";break;//法院判决
            case 101 : wpid="WP71";break;//房屋交换
            case 302 : wpid="WP61";break;//房屋拍卖
            case 303 : wpid="WP62";break;//投资入股
            case 304 : wpid="WP63";break;//兼并合并
            case 50 : wpid="WP65";break;//抵债业务
            case 111 : wpid="WP66";break;//政府奖励
            case 61 : wpid="WP72";break;//回迁房屋
            case 80 : wpid="WP67";break;//

            case 31 : wpid="WP9";break;//单位抵押
            case 32 : wpid="WP9";break;//个人抵押
            case 33 : wpid="WP9";break;//商品房按揭贷款
            case 812 : wpid="WP10";break;//房屋所有权抵押变更登记
            case 35 : wpid="WP13";break;//最高额抵押权设定登记
            case 34 : wpid="WP18";break;//在建工程抵押权设定登记
            case 811 : wpid="WP19";break;//在建工程抵押权设定变更登记
            case 123 : wpid="WP21";break;//按揭与在建工程注销登记
            case 171 : wpid="WP171";break;//抵押注销登记


            case 51 : wpid="WP44";break;//预购商品房
            case 55 : wpid="WP46";break;//预购商品房预告注销登记
            case 52 : wpid="WP1";break;//预购商品房设定抵押
            case 56 : wpid="WP4";break;//预购商品房设定抵押注销登记



            case 161: wpid="WP52";break;//房屋变更
            case 131: wpid="WP54";break;//分照
            case 170: wpid="WP102";break;//改变用途
            case 141: wpid="WP55";break;//合照
            case 71: wpid="WP53";break;//	自翻扩改

            case 300: wpid="WP30";break;//新建房屋
            case 121: wpid="WP32";break;//遗失补照
            case 301: wpid="WP38";break;//房屋灭籍
            case 151: wpid="WP33";break;//换照
            case 220: wpid="WP34";break;//声明作废
            case 2002: wpid="WP35";break;//所有权更正登记
            case 142: wpid="WP75";break;//集资建房
            case 201: wpid="WP73";break;//查封
            case 202: wpid="WP74";break;//查封解除


        }
        return wpid;

    }
    public static String changeStructureFc(String structure){
        if (structure == null){
            return "'827'";
        }
        if (structure.equals("混合")){
            return "'824'";
        }else if (structure.equals("钢、钢混")){
            return "'822'";
        }else if (structure.equals("钢")){
            return "'88'";
        }else if (structure.equals("砖木")){
            return "'825'";
        }else if (structure.equals("简易")){
            return "'5000'";
        }else if (structure.equals("钢结构")){
            return "'5001'";
        }else if (structure.equals("框架")){
            return "'915'";
        }else if (structure.equals("荫道")){
            return "'5002'";
        }else if (structure.equals("钢屋架")){
            return "'5003'";
        }else if (structure.equals("钢混、钢构")){
            return "'5004'";
        }else if (structure.equals("混合.砖木")||structure.equals("砖木.混合") || structure.equals("砖木、混合")){
            return "'5005'";
        }else if (structure.equals("钢混")){
            return "'823'";
        }else if (structure.equals("砖混")){
            return "'5006'";
        }else if (structure.equals("钢混、钢架")) {
            return "'5007'";
        }else if (structure.equals("石木")) {
            return "'5008'";
        }else{
            return "'827'";
        }
    }

    public static String changePoolMemoFc(String poolmemo){
        if(poolmemo.trim().equals("共同共有")){
            return "'TOGETHER_OWNER'";
        }else if( poolmemo.trim().equals("按份共有")){
            return "'SHARE_OWNER'";
        }else if( poolmemo.trim().equals("单独所有")){
            return "'SINGLE_OWNER'";
        }else {
            return "NULL";
        }
    }

    public static String fcCardType(String s){
        if (s==null || s.equals("")){
            return "'OTHER'";

        }
        if( s.equals("身份证") || s.equals("临时身份证") || s.equals("户口簿") ){
            return "'MASTER_ID'";
        }else if(s.equals("营业执照")){
            return "'COMPANY_CODE'";
        }else if(s.equals("军官证") || s.equals("士兵证") ){
            return "'SOLDIER_CARD'";
        }else if(s.equals("机构代码证") || s.equals("组织机构代码证")){
            return "'CORP_CODE'";
        }else if(s.equals("港澳居民通行证") ) {
            return "'TW_ID'";
        } else
            return "'OTHER'";
    }

    public static String fcRelation(String s){
        if (s==null || s.equals("")){
            return "NULL";
        }
        if( s.equals(" 夫妻") || s.equals("夫妻") || s.equals("妻子")){
            return "'215'";
        }else if(s.equals("兄弟") || s.equals("弟弟")){
            return "'216'";
        }else if(s.equals("姐妹") || s.equals("姐姐")){
            return "'217'";
        }else if(s.equals("母女")){
            return "'3850'";
        }else if(s.equals("母子") ) {
            return "'3851'";
        } else if(s.equals("父子") ) {
            return "'3852'";
        } else if(s.equals("父女") ) {
            return "'3853'";
        } else if(s.equals("父亲") ) {
            return "'3854'";
        } else if(s.equals("母亲") ) {
            return "'3855'";
        } else
            return "'3856'";
    }




}
