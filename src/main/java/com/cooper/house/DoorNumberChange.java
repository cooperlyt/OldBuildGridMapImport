package com.cooper.house;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cooper on 11/3/15.
 */
public class DoorNumberChange {


    private static final String OLD_HOUSE_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";


    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/REDOORNO.sql";

    private static final String ID_LINK_FILE = "/Users/cooper/Documents/ONBUILDID.txt";


    private static Connection conn;



    private static BufferedWriter sqlWriter;
    private static Map<String,String> buildIdMap = new HashMap<String, String>();


    public static void readFileByLines() {
        File file = new File(ID_LINK_FILE);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号


                String[] ss = tempString.split(" \\| ");
                buildIdMap.put(ss[1].trim(),ss[0].trim());
                System.out.println(tempString);
                System.out.println(ss[1] + "=" +ss[0]);

                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    public static void main(String arg[]) {
        readFileByLines();

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(OLD_HOUSE_DB_URL, "sa", "dgsoft");
        } catch (ClassNotFoundException e) {
            System.out.print("数据库驱动加载失败");
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            System.out.println("database driver fail");
            return;
        }

        System.out.println("数据库连接成功");


        File file = new File(OUT_FILE_PATH);


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

        try {

            sqlWriter.write("ALTER TABLE HOUSE_INFO.BUILD CHANGE DOOR_NO DOOR_NO VARCHAR(32);");
            sqlWriter.newLine();
            Statement statement = conn.createStatement();
            ResultSet bizRs = statement.executeQuery("SELECT ID,DoorNO FROM Build");
            while (bizRs.next()) {
                sqlWriter.write("UPDATE BUILD SET DOOR_NO =" + Q.p(bizRs.getString(2)) + " WHERE ID=" + Q.p(buildIdMap.get(bizRs.getString(1)) ) + ";");
                sqlWriter.newLine();
            }

            sqlWriter.flush();

        }     catch (SQLException e) {
        e.printStackTrace();
    } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
