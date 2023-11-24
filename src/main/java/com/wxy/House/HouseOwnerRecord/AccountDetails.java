package com.wxy.House.HouseOwnerRecord;

import java.sql.*;

public class AccountDetails {
    private static Connection townsConnection;
    private static Statement statementClose;
    private static ResultSet closeResultSet;
    private static Statement statementblack;
    private static ResultSet blackResultSet;

    private static final String DB_TOWNS_RECORD_URL = "jdbc:mysql://127.0.0.1:3306/WXZJ?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";

    public static void main(String agr[]) throws SQLException {

        try{
            Class.forName("com.mysql.jdbc.Driver");
            townsConnection = DriverManager.getConnection(DB_TOWNS_RECORD_URL,"root","dgsoft");
            statementClose = townsConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            statementblack= townsConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            System.out.println("townsConnection successful");
        }catch (Exception e){
            System.out.println("townsConnection is errer");
            e.printStackTrace();
            return;

        }

        closeResultSet = statementClose.executeQuery("select sum(MONEY) as money,ACCOUNT_NUMBER from ACCOUNT_DETAILS group by ACCOUNT_NUMBER  order by ACCOUNT_NUMBER");
        closeResultSet.last();
        int sum = closeResultSet.getRow(),i=0;
        closeResultSet.beforeFirst();
        while (closeResultSet.next()){
            blackResultSet = statementblack.executeQuery("select * from ACCOUNT_DETAILS where ACCOUNT_NUMBER='"+closeResultSet.getString("ACCOUNT_NUMBER")+"' and BALANCE=(select max(BALANCE) from ACCOUNT_DETAILS where ACCOUNT_NUMBER='"+closeResultSet.getString("ACCOUNT_NUMBER")+"')");
            if(blackResultSet.next()){
               if(closeResultSet.getBigDecimal("money").compareTo(blackResultSet.getBigDecimal("BALANCE"))!=0){
                     System.out.println(closeResultSet.getString("ACCOUNT_NUMBER"));
                }
            }

            i++;
//            System.out.println(i+"/"+sum);
        }


    }

}
