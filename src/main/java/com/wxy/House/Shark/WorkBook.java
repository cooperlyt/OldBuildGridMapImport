package com.wxy.House.Shark;


import com.cooper.house.Q;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wxy on 2016-09-15.
 */
public class WorkBook {

    private static final String OUT_FILE_PATH = "/word.sql";

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.3.200:1433/Shark";

    private static Connection connection;

    private static BufferedWriter sqlWriter;

    private static File file;

    private static Statement statement;

    private static ResultSet resultSet;


    public static void main(String[] args){

        file = new File(OUT_FILE_PATH);
        if (file.exists()){
            file.delete();
        }

        try {
            file.createNewFile();
            FileWriter fw =new FileWriter(file.getAbsoluteFile());
            sqlWriter = new BufferedWriter(fw);

        } catch (IOException e) {
            System.out.println("sql 文件创建失败");
            e.printStackTrace();
            return;
        }


        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Connection is errer");
            e.printStackTrace();
            return;
        }

        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='25' ORDER BY PRIORITY");
            sqlWriter.write("use DB_PLAT_SYSTEM;");
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='house.useType';");//设计用途
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='28';");//产权来源
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='house.structure';");//结构
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='26';");//产别
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='house.houseType';");//房屋性质
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='67';");//共有权人与产权人关系
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='55';");//土地取得方式
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='32';");//土地性质
            sqlWriter.newLine();
            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='54';");//付款方式---枚举
            sqlWriter.newLine();
//            sqlWriter.write("DELETE FROM WORD WHERE CATEGORY='70';");//开发商资质等级 不需要
//            sqlWriter.newLine();



            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("house.useType"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("house.useType is complate");

        } catch (Exception e) {
            System.out.println("house.useType is errer");
            e.printStackTrace();
            return;

        }


        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='28' ORDER BY PRIORITY");
            sqlWriter.newLine();
            sqlWriter.write("-- 产权来源" );
            sqlWriter.newLine();
            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("28"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("28产权来源 is complate");

        } catch (Exception e) {
            System.out.println("28产权来源 is errer");
            e.printStackTrace();
            return;

        }


        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='27' ORDER BY PRIORITY");
            sqlWriter.newLine();
            sqlWriter.write("-- 结构" );
            sqlWriter.newLine();
            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("house.structure"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("house.structure结构 is complate");

        } catch (Exception e) {
            System.out.println("house.structure结构 is errer");
            e.printStackTrace();
            return;

        }

        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='26' ORDER BY PRIORITY");
            sqlWriter.newLine();
            sqlWriter.write("-- 产别" );
            sqlWriter.newLine();
            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("26"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("26产别 is complate");

        } catch (Exception e) {
            System.out.println("26产别 is errer");
            e.printStackTrace();
            return;

        }

        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='64' ORDER BY PRIORITY");
            sqlWriter.newLine();
            sqlWriter.write("-- 房屋性质" );
            sqlWriter.newLine();
            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("house.houseType"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("house.houseType房屋性质 is complate");

        } catch (Exception e) {
            System.out.println("house.houseType房屋性质 is errer");
            e.printStackTrace();
            return;

        }


        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='67' ORDER BY PRIORITY");
            sqlWriter.newLine();
            sqlWriter.write("-- 共有权人与产权人关系" );
            sqlWriter.newLine();
            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("67"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("67共有权人与产权人关系 is complate");

        } catch (Exception e) {
            System.out.println("67共有权人与产权人关系 is errer");
            e.printStackTrace();
            return;

        }



        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='55' ORDER BY PRIORITY");
            sqlWriter.newLine();
            sqlWriter.write("-- 土地取得方式" );
            sqlWriter.newLine();
            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("55"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("55土地取得方式 is complate");

        } catch (Exception e) {
            System.out.println("55土地取得方式 is errer");
            e.printStackTrace();
            return;

        }


        try {

            resultSet = statement.executeQuery("SELECT * FROM DGWordBook where TypeID='32' ORDER BY PRIORITY");
            sqlWriter.newLine();
            sqlWriter.write("-- 土地性质" );
            sqlWriter.newLine();
            sqlWriter.write("INSERT WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES " );
            while (resultSet.next()){
                sqlWriter.write("("+ Q.v(Q.p(resultSet.getString("ID")), Q.p("0"), Q.p(resultSet.getString("Value")), Q.p("32"), Q.p(""),Q.p(resultSet.getString("Priority")),"True"));
                if(resultSet.isLast()) {
                    sqlWriter.write(");");
                }else {
                    sqlWriter.write("),");
                }

            }
            sqlWriter.flush();
            System.out.println("32土地性质 is complate");

        } catch (Exception e) {
            System.out.println("32土地性质 is errer");
            e.printStackTrace();
            return;

        }


    }



}
