package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by cooper on 11/24/15.
 */






public class ChangeInitOwner {

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";

    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/reInitOwner.sql";

    private static final String ID_LINK_FILE = "/Users/cooper/Documents/ONBUILDID.txt";

    private static Connection conn;

    private static BufferedWriter writer;

    public static void main(String[] args){

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            System.out.println("Connection successful");
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select db.RecordBizNO, hc.NO ,hc.CardNO,hc.Memo,hc.PrintTime from DGHouseRecord..Business as db left join " +
            " DGHouseInfo..houseCard as hc on db.id=hc.bizid " +
            " where db.nameid like '%WP40'");
            File file = new File(OUT_FILE_PATH);
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            writer = new BufferedWriter(fw);



            while (rs.next()) {

                writer.write("UPDATE BUSINESS_OWNER bo LEFT JOIN HOUSE h on h.MAIN_OWNER = bo.ID  set bo.OWNER_CARD = null,bo.ADDRESS=null,bo.LEGAL_PERSON=null,bo.ROOT_ADDRESS=null,bo.PHONE=null,bo.ID_NO= h.DEVELOPER_CODE , bo.ID_TYPE ='OTHER' , bo.NAME = h.DEVELOPER_NAME  WHERE bo.ID = " + Q.p(rs.getString(1)) + ";");
                writer.write("DELETE FROM CARD_INFO WHERE ID = " + Q.p(rs.getString(1)) + ";");
                writer.write("DELETE FROM MAKE_CARD WHERE ID = " + Q.p(rs.getString(1)) + ";");
                writer.write( "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                        Q.v(
                                Q.p(rs.getString(1)), Q.pm(rs.getString(2)), Q.p("OWNER_RSHIP")

                                , Q.p(rs.getString(1)), Q.p(true)

                        )

                        + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                        Q.v(Q.p(rs.getString(1)), Q.pm(rs.getString(3)), Q.p(rs.getString(4)), Q.p(rs.getTimestamp(5)))

                        + ");");

                writer.write("UPDATE BUSINESS_OWNER set OWNER_CARD = " + Q.p(rs.getString(1)) + " WHERE ID = " + Q.p(rs.getString(1)) + ";");
                writer.newLine();
                writer.flush();

            }

            writer.flush();
            writer.close();
            fw.close();

            statement.close();
            conn.close();
            conn = null;

            System.out.println("complete");
        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }
    }
}
