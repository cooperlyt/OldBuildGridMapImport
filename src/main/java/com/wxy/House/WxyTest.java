package com.wxy.House;

import com.cooper.house.Q;
import com.wxy.House.HouseInfo.HouseInfo;
import com.wxy.House.HouseOwnerRecord.DGHouseOwnerRecord;
import com.wxy.House.HouseOwnerRecord.FcHouseOwnerRecord;
import com.wxy.House.HouseOwnerRecord.HouseOwnerRecord;
import com.wxy.House.Shark.WorkBook;

import oracle.jdbc.oci.OracleOCIConnection;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleOCIConnectionPool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by wxy on 2016-09-14.
 */
public class WxyTest {

    public enum Clocr{
        red,yewwol,block,white

    }







    public static void main(String[] args) throws SQLException, ParseException {
//        WorkBook.main(null);
//        HouseInfo.main(args);
//        HouseOwnerRecord.main(null);

        BigDecimal b = new BigDecimal(1.5456);
        BigDecimal a = new BigDecimal(2.5456);
        BigDecimal c = new BigDecimal(0);
        c=a.multiply(b);
        b=b.setScale(0,BigDecimal.ROUND_HALF_UP);
        System.out.print(c.setScale(0,BigDecimal.ROUND_HALF_UP));

//        String systemDateStr = "2018-08-01";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date();
//        date = sdf.parse(systemDateStr);
//        System.out.println(sdf.format(date));
//
//        Date date1 = new Date();
//
//        System.out.println(sdf.format(date1));
//        System.out.println(date.compareTo(date1));
//        if (date1.compareTo(date)>0){
//           System.out.println("111");
//
//        }else {
//            System.out.println("222");
//        }
//        Connection conn = null ;        // 数据库连接
//        try {
//            String DBURL = "jdbc:oracle:thin:@localhost:1521:orcl";
//            String DBUSER = "system";
//            String DBPASS = "Brush23504653";
//            Class.forName("oracle.jdbc.driver.OracleDriver");   // 加载驱动程序
//            conn = DriverManager.getConnection(DBURL,DBUSER,DBPASS);
//            Statement statementShark = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            ResultSet setd = statementShark.executeQuery("select * from Pjlr1.TD_ZC_GX");
//            setd.last();
//            int f=setd.getRow();
//            System.out.println(f);
//            System.out.println("houseConnection successful");
//
//        } catch (ClassNotFoundException e) {
//            System.out.println("houseConnection is errer");
//            e.printStackTrace();
//        }




      //  FcHouseOwnerRecord.main(null);


    }
}
