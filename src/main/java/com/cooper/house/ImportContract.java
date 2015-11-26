package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cooper on 11/18/15.
 */
public class ImportContract {

    private static Connection conn;

    private static BufferedWriter writer;

    private static final String OUT_FILE_PATH = "/root/Documents/contract_after.sql";


    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";

    private static void befor(){
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

    private static final String HOUSE_DB_URL = "jdbc:mysql://127.0.0.1:3306/HOUSE_OWNER_RECORD";
    private static Connection houseConn;


    private static void after(){

        try {
            Class.forName("com.mysql.jdbc.Driver");

            houseConn = DriverManager.getConnection(HOUSE_DB_URL, "root", "isNull");
        } catch (ClassNotFoundException e) {
            System.out.println( "database driver fail");
            return;
        } catch (SQLException e) {
            System.out.println("database driver fail");
            return;
        }
        try {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
        } catch (ClassNotFoundException e) {
            System.out.println( "database driver fail");
            return;
        } catch (SQLException e) {
            System.out.println("database driver fail");
        }
        System.out.println("Connection successful");

        int i = 0;

        try {


            File file = new File(OUT_FILE_PATH);
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            writer = new BufferedWriter(fw);

            Statement ns = houseConn.createStatement();
            ResultSet nrs = ns.executeQuery("SELECT a.CONTRACT_NUMBER, h.ID, b.ID FROM CONTRACT_OWNER a left join HOUSE h on h.CONTRACT_OWNER = a.ID  left join OWNER_BUSINESS b on a.BUSINESS = b.ID LEFT join HOUSE_CONTRACT cc on cc.id = a.ID where b.SOURCE = 'BIZ_OUTSIDE' and b.STATUS='RUNNING' and cc.id is null");

            Statement statement = conn.createStatement();
            while (nrs.next()){

                ResultSet rs = statement.executeQuery("select a.*,do.name,do.idno,do.phone,do.address,do.nation,do.idtype,"+
                        " (case when a.paytype=177 then 'ALL_PAY' when a.paytype=179 then 'PART_PAY' when a.paytype=178 then 'DEBIT_PAY'  end) as nowpaytype,"+
                    " (case when do.idtype=4 then 'MASTER_ID' when do.idtype=5 then 'SOLDIER_CARD' when do.idtype=6 then 'PASSPORT' when do.idtype=208 then 'OTHER' end) as nowidtype"+
                    " from "+
                    "        (select nc.no,nc.house,nc.TransactionPrice,nc.paytype,cao.ownerinfoid,cao.OwnerRelationship"+
                    "                from DGHouseInfo..NewHouseContract as nc"+
                    " left join DGHouseInfo..HouseContractAndOwner as cao on nc.id=cao.NewHouseContractID"+
                    " where nc.State=916) as a"+
                    " left join DGHouseInfo..ownerinfo as do on a.ownerinfoid=do.id where a.NO = " + Q.p(nrs.getString(1)));

                boolean first = true;



                while (rs.next()) {


                    String pid = "OLD-" + i;
                    boolean havePool = (rs.getString("ownerinfoid") != null);
                    if (havePool) {
                        writer.write("INSERT INTO BUSINESS_POOL(ID,NAME,ID_TYPE,ID_NO,RELATION,PHONE,CREATE_TIME,BUSINESS) VALUES("+
                                Q.v(Q.p(pid) , Q.p(rs.getString("name")),
                                        Q.p(rs.getString("nowidtype")),Q.p(rs.getString("idno")),Q.p(rs.getString("OwnerRelationship")) ,
                                        Q.p(rs.getString("phone")), Q.p(new Timestamp((new java.util.Date()).getTime())), Q.p(nrs.getString(3))  )
                                +");");

                        writer.write("INSERT INTO HOUSE_POOL(HOUSE,POOL) VALUES(" + Q.v(Q.p(nrs.getString(2)) ,Q.p(pid) ) + ");");
                    }

                    if (first) {

                       // System.out.println(rs);
                       // System.out.println( Q.p(nrs.getString(2)));
                        writer.write("INSERT INTO SALE_INFO(ID,PAY_TYPE,SUM_PRICE,HOUSEID) VALUES(" + Q.v(Q.p(nrs.getString(1)), Q.p(rs.getString("nowpaytype")), Q.pm(rs.getBigDecimal("TransactionPrice")) , Q.p(nrs.getString(2))) + ");");

                        if (havePool){
                            writer.write("UPDATE HOUSE set POOL_MEMO = 'TOGETHER_OWNER'  where ID=" + Q.p(nrs.getString(2)) + ";");
                        } else {
                            writer.write("UPDATE HOUSE set POOL_MEMO =  'SINGLE_OWNER'  where ID=" + Q.p(nrs.getString(2)) + ";");
                        }
                    }

                    first = false;
                    i++;
                }




                writer.newLine();
                writer.flush();

            }








        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        //step 1: befor

        //step 2: create business

        //step 3:
        after();


    }

}
