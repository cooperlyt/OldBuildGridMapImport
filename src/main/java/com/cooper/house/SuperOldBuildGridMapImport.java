package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by cooper on 1/3/16.
 */
public class SuperOldBuildGridMapImport {

    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.11.152:1433/DGHouseInfo";

    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/oldBuildGridImport.sql";

    private static Connection conn;

    private static BufferedWriter writer;



    private static void readBuild(String buildId) throws SQLException, IOException {
        writer.newLine();
        writer.newLine();
        writer.write("/*  import build:" + buildId + "*/");
        writer.newLine();


        Statement statement = conn.createStatement();





        ResultSet floorRs = statement.executeQuery("select inFloor ,Max(inFloorName) as InFloorName from House "+
                "where Buildid = " + buildId + "  group by inFloor order by infloor desc");

        Map<Integer,String> floorMap = new HashMap<Integer, String>();

        int max=0;
        int min=9999999;
        while (floorRs.next()){
            int floor = floorRs.getInt(1);
            if (floor > max){
                max = floor;
            }
            if (floor < min){
                min = floor;
            }

            floorMap.put(floor,floorRs.getString(2));
        }

        floorRs.close();

        if (min==9999999 && max==0){
            return;
        }

//表头
        writer.write("INSERT INTO BUILD_GRID_MAP(ID,BUILD_ID,NAME,_ORDER) VALUES('I-" + buildId + "','" + buildId + "','第1页'," + 0 +");");
        writer.newLine();



        ResultSet unitRs = statement.executeQuery("select d.HouseUnit ,( select max(a.HouseCount) from " +
                "(select sum(c.Html_colspan) as HouseCount from " +
                "(select BuildID,Html_Order,HouseUnit,infloor,Html_RowSpan,html_ColSpan "+
                "from House where Html_UnionUnit = 0 and Buildid = " + buildId + " union all " +
                "select BuildID,HouseOrder,HouseUnit,Infloor,RowSpan,colspan from EmptyHouse "+
                "where Buildid = " + buildId + ") as  c where c.HouseUnit = d.HouseUnit  group by infloor) "+
                "as a) as MaxCount from House as d where buildID = " + buildId + " group by d.HouseUnit order by d.HouseUnit");

        int i = 0;

        writer.write("INSERT INTO HOUSE_GRID_TITLE(ID,_ORDER,TITLE,COLSPAN,GRILD_ID) VALUES('I-" + buildId + "-" +
                i++ + "',0,'',1,'I-" +  buildId +  "');");

        while (unitRs.next()) {
            writer.write("INSERT INTO HOUSE_GRID_TITLE(ID,_ORDER,TITLE,COLSPAN,GRILD_ID) VALUES('I-" + buildId + "-" +
                    i + "'," + i + ",'"+ unitRs.getString(1)  + "单元',"+ unitRs.getInt(2) +",'I-" +  buildId +  "');");
            i++;
        }

        unitRs.close();
//--------------------


        ResultSet blockRs = statement.executeQuery( "select * from " +
                "(select HouseID,HouseOrder,"+
                "InFloor,HouseState, Html_Order,Html_RowSpan,Html_ColSpan,HouseUnit from House where buildid = " + buildId +
                " union all " +
                "Select ID ,'',"+
                "Infloor,-1,HouseOrder,RowSpan,ColSpan,HouseUnit  from EmptyHouse "+
                "where UseType = 0 and buildid = " + buildId + ")  as a order by inFloor desc , Html_Order");


        Map<Integer,List<BlockData>> blocks = new HashMap<Integer, List<BlockData>>();
        while (blockRs.next()){
            BlockData data = new BlockData(blockRs);
            List<BlockData> bl = blocks.get(data.getInfloor());
            if (bl == null){
                bl = new ArrayList<BlockData>();
                blocks.put(data.getInfloor(),bl);
            }
            bl.add(data);
        }

        for(List<BlockData> vv: blocks.values()){
            Collections.sort(vv, new Comparator<BlockData>() {
                public int compare(BlockData o1, BlockData o2) {
                    return Integer.valueOf(o1.getOrder()).compareTo(o2.getOrder());
                }
            });
        }

        blockRs.close();

        List<Integer> floorList = new ArrayList<Integer>(floorMap.keySet());

        Collections.sort(floorList, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });

        i = 0;
        for(Integer floor: floorList){
            writer.write("INSERT INTO GRID_ROW(ID,TITLE,_ORDER,FLOOR_INDEX,GRID_ID) VALUES('I-" + buildId + "-" +
                    i  + "','" + floorMap.get(floor)  + "'," + i + "," + floor +
                    ",'I-" + buildId + "');");

            int j = 0;
            for(BlockData data : blocks.get(floor)){
                writer.write(data.getInsertSql("I-" + buildId + "-" + i,j++));
            }

            i++;

        }



    }

    private static class BlockData{

        private String houseId;

        private String houseOrder;

        private int order;

        private int rowspan;

        private int colspan;

        private int infloor;

        private int unit;


        public BlockData(ResultSet rs) throws SQLException {
            if (rs.getInt(4) != -1){
                houseId = rs.getString(1);
                houseOrder = rs.getString(2);
            }
            order = rs.getInt(5);
            rowspan = rs.getInt(6);
            colspan = rs.getInt(7);
            infloor = rs.getInt(3);
            unit = rs.getInt(8);
          //  if (rowspan > 1){
           //     infloor = infloor - (rowspan - 1);
           // }
        }

        public String getInsertSql( String rowId , int order){
            if (houseId == null) {
                return "INSERT INTO GRID_BLOCK(ID,ROW_ID,_ORDER,COLSPAN,ROWSPAN,UNIT_INDEX,UNIT_NAME,HAVE_DOWN_ROOM) VALUES(" +
                        "'" + rowId + "-" + order + "','" + rowId + "'," + order + "," + colspan + "," + rowspan +
                        "," + unit + ",'" + unit + "单元',FALSE   );";
            }else{
                return "INSERT INTO GRID_BLOCK(ID,ROW_ID,_ORDER,COLSPAN,ROWSPAN,UNIT_INDEX,UNIT_NAME,HAVE_DOWN_ROOM,HOUSE_CODE,HOUSE_ORDER) VALUES(" +
                        "'" + rowId + "-" + order + "','" + rowId + "'," + order + "," + colspan + "," + rowspan +
                        "," + unit + ",'" + unit + "单元',FALSE,'" + houseId +"','" + houseOrder + "');";
            }
        }

        public int getOrder() {
            return order;
        }

        public int getInfloor() {
            return infloor;
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            System.out.println("Connection successful");
            Statement statement = conn.createStatement();
            ResultSet buildRS = statement.executeQuery("SELECT BUILDID FROM Build ");


            File file = new File(OUT_FILE_PATH);
            if (file.exists()){
                file.delete();
            }
            file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            writer = new BufferedWriter(fw);

            writer.write("DELETE FROM HOUSE_GRID_TITLE;");
            writer.write("DELETE FROM GRID_BLOCK;");
            writer.write("DELETE FROM GRID_ROW;");
            writer.write("DELETE FROM BUILD_GRID_MAP;");
            writer.newLine();
            writer.newLine();
            writer.newLine();

           // while (buildRS.next()) {
           //     readBuild(buildRS.getString(1).trim());
           // }
            readBuild("755");
            writer.flush();
            writer.close();
            fw.close();

            statement.close();
            conn.close();
            conn = null;

            System.out.println("complete");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
