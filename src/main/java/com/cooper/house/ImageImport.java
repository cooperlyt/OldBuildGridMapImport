package com.cooper.house;

import com.scoopit.weedfs.client.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

/**
 * Created by cooper on 4/8/16.
 */
public class ImageImport {


    private static final String MASTER_ADDRESS = "http://192.168.1.220:9333";

    private static final String HOUSE_DB_URL = "jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";

    private static final String OUT_FILE_PATH = "/root/Documents/FileReplace.sql";

    private static final String OLD_PATH="E:/Release/DGHIS/uploadFile/";

    private static final String NEW_PATH="/root/Documents/files/";

    private static final String ERROR_FILE="/root/Documents/fileError.log";

    private static BufferedWriter sqlWriter;

    private static BufferedWriter errorWriter;

    private static void replaceFile(String id, String path){


        try {



            File f = new File(path);

            if (!f.exists()) {
                errorWriter.write("file not exists:" + f.getAbsolutePath() + ">" + id);
                errorWriter.newLine();
                errorWriter.flush();
                return;
            }
                WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(new URL(MASTER_ADDRESS)).build();
                Assignation a = client.assign(new AssignParams());
                client.write(a.weedFSFile, a.location, f);

                sqlWriter.write("UPDATE UPLOAD_FILE set ID=" + Q.p(a.weedFSFile.fid) + " where ID=" + Q.p(id) + ";");
                sqlWriter.newLine();

                sqlWriter.flush();


        } catch (MalformedURLException e) {
            System.out.println("地址错误");
            e.printStackTrace();
        } catch (WeedFSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Connection houseConn;


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

        File erroLog = new File(ERROR_FILE);

        if (erroLog.exists()) {
            erroLog.delete();
        }
        try {
            file.createNewFile();
            FileWriter fwe = new FileWriter(erroLog.getAbsoluteFile());
            errorWriter = new BufferedWriter(fwe);
        } catch (IOException e) {
            System.out.println("日志 文件创建失败");
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");


            houseConn = DriverManager.getConnection(HOUSE_DB_URL, "root", "isNull");

            System.out.println( "connection success");


            Statement statement = houseConn.createStatement();
            ResultSet rs = statement.executeQuery("select COUNT(ID) from UPLOAD_FILE where not (ID like '%,%')");
            Long count = new Long(0);
            if (rs.next()){
                count = rs.getLong(1);
            }
            ResultSet bizRs = statement.executeQuery("select ID,FILE_NAME from UPLOAD_FILE where not (ID like '%,%')");

            int i = 1;

            while (bizRs.next()){

                System.out.println("" + count + "/" + i  + "              " + ( new Double(i).doubleValue() / new Double(count).doubleValue()  * 100) + "%");


                String srcPath = bizRs.getString(2).replace(OLD_PATH,NEW_PATH);
                replaceFile(bizRs.getString(1),srcPath);
                i++;
            }

            sqlWriter.flush();
            sqlWriter.close();
            //
            errorWriter.flush();
            errorWriter.close();
        } catch (ClassNotFoundException e) {
            System.out.println( "database driver fail");
            return;
        } catch (SQLException e) {
            System.out.println("sql Exception driver fail");
            return;
        } catch (IOException e) {
            System.out.println("file error");
            e.printStackTrace();
        }


    }
}
