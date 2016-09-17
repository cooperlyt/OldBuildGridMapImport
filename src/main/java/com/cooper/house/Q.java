package com.cooper.house;

import javafx.scene.input.DataFormat;

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
            return "'2000-1-1'";
        }else{

            return "'" + d.toString() + "'";
        }
    }

    public static String pm(BigDecimal b){
        if (b == null) {
            return "0";
        }else{
            return b.stripTrailingZeros().toPlainString();
        }
    }

    public static String p(BigDecimal b){
        if (b == null){
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

        if( HouseType==206 ){
            return "'206'";
        }else if( HouseType==781){
            return "'781'";
        }else if( HouseType==782){
            return "'782'";
        }else if( HouseType==783){
            return "'783'";
        }else if(HouseType==1946) {
            return "'1946'";
        } else if(HouseType==2773){
            return "'2773'";
        }else
            return "NULL";

    }

    public static String changeUseType(int UseType){

        if( UseType==0){
            return "'未知'";
        }else
            return "'"+String.valueOf(UseType)+"'";

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

}
