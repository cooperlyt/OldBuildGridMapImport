package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cooper on 11/18/15.
 */
public class ImportContract {

    private static Connection conn;

    private static BufferedWriter writer;

    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/oldContract.sql";


    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";

    public static void main(String[] args) {
        try {

        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
        System.out.println("Connection successful");
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("select b.houseNO,b.no,b.ownerName,b.IDType,b.IDNO,b.Phone,b.Address,b.RecordDate from  (select a.*,ho.name as ownerName,ho.IDNO,ho.IDType,ho.phone,ho.address from (select hn.*,hh.no as houseNo,hh.buildid from DGHouseInfo..NewHouseContract as hn left join DGHouseInfo..House as hh on hh.id=hn.house  where state=916 and housestate=131 and inbizcode is null) as a left join DGHouseInfo..OwnerInfo as ho on a.owner= ho.id) as b left join shark..DGWordBook as wb on b.idtype = wb.id where b.id <> '402881864b525acf014bfd16660f0130' and b.id <> '4028818645dea6190146d2a8d5d80b7b'");
        File file = new File(OUT_FILE_PATH);
        if (file.exists()){
            file.delete();
        }
        file.createNewFile();


            Set<String> houseCodeSet = new HashSet<String>();
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        writer = new BufferedWriter(fw);

            writer.write("ALTER TABLE HOUSE_OWNER_RECORD.CONTRACT_OWNER MODIFY COLUMN BUSINESS VARCHAR(32) NULL;");


            writer.newLine();
            while (rs.next()) {
                if (!houseCodeSet.contains(rs.getString(1))) {

                    writer.write("INSERT INTO CONTRACT_OWNER (ID,CONTRACT_NUMBER,NAME,ID_TYPE,ID_NO,PHONE,ADDRESS,CONTRACT_DATE,TYPE,HOUSE_CODE) VALUES(" +
                            Q.v(Q.p(rs.getString(1)), Q.p(rs.getString(2)), Q.p(rs.getString(3)),
                                    Q.p((rs.getInt(4) == 4) ? "MASTER_ID" : (rs.getInt(4) == 5) ? "SOLDIER_CARD" : "OTHER"),
                                    Q.p(rs.getString(5)), Q.p((rs.getString(6).length() > 14) ? rs.getString(6).substring(14) : rs.getString(6)), Q.p(rs.getString(7)), Q.p(rs.getTimestamp(8)), Q.p("MAP_SELL"), Q.p(rs.getString(1)))

                            + ");");



                    writer.newLine();
                    writer.flush();
                    houseCodeSet.add(rs.getString(1));
                }
            }


        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }
    }

}
