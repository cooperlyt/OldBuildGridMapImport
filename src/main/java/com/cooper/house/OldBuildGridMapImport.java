package com.cooper.house;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Created by cooper on 10/11/15.
 */
public class OldBuildGridMapImport {


    private static final String DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";

    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/oldBuildGridImport.sql";

    private static final String ID_LINK_FILE = "/Users/cooper/Documents/ONBUILDID.txt";


    private static final String HOUSE_SQL =       "SELECT Html_Page, Html_Order,HouseUnit, UnitName,      InFloor + (Html_RowSpan - 1) ,InFloorName, Html_ColSpan, Html_RowSpan,Html_UnionColSpan,Html_UnionColSpanLeft, h.NO, h.HouseOrder  FROM HOUSE h WHERE h.BuildID ='";

    private static final String EMPTY_HOUSE_SQL = "SELECT     Page, HouseOrder,     HouseUnit, HouseUnitName ,InFloor + (RowSpan - 1),       InFloorName, ColSpan      ,RowSpan,     UnionUnitColSpan, UnionColSpanLeft FROM EmptyHouse WHERE BuildID ='" ;

    private static long titleId, gridId, rowId, blockId;

    private static Connection conn;

    private static BufferedWriter writer;

    private static Set<String> error = new HashSet<String>();

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


    public static void main(String[] args) {

        readFileByLines();

        titleId = 1; gridId = 1; rowId = 1; blockId = 1;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, "sa", "dgsoft");
            System.out.println("Connection successful");
            Statement statement = conn.createStatement();
            ResultSet buildRS = statement.executeQuery("SELECT ID FROM Build ");
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

            while (buildRS.next()) {
                readBuild(buildRS.getString(1).trim());

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


    private static void readBuild(String buildId) throws SQLException, IOException {

        Map<Integer,List<HouseData>> gridMapData = new HashMap<Integer, List<HouseData>>();
        Statement statement = conn.createStatement();
        ResultSet houseRs = statement.executeQuery(HOUSE_SQL + buildId + "'");
        while (houseRs.next()){
            HouseData data = new HouseData(houseRs);
            data.houseCode = houseRs.getString(11);
            data.houseOrder = houseRs.getString(12);
            List<HouseData> datas = gridMapData.get(data.page);
            if (datas == null){
                datas = new ArrayList<HouseData>();
                gridMapData.put(data.page,datas);
            }
            datas.add(data);
        }
        statement.close();

        if (gridMapData.isEmpty()){
            return;
        }

        statement = conn.createStatement();
        ResultSet eHouseRs = statement.executeQuery(EMPTY_HOUSE_SQL + buildId + "'");
        while (eHouseRs.next()){
            HouseData data = new HouseData(eHouseRs);
            List<HouseData> datas = gridMapData.get(data.page);
            if (datas != null){
                datas.add(data);
            }
        }
        statement.close();

        List<Map.Entry<Integer,List<HouseData>>> mapDatas = new ArrayList<Map.Entry<Integer, List<HouseData>>>(gridMapData.entrySet());
        Collections.sort(mapDatas, new Comparator<Map.Entry<Integer, List<HouseData>>>() {
            public int compare(Map.Entry<Integer, List<HouseData>> o1, Map.Entry<Integer, List<HouseData>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        readBuildGridMap(buildId,mapDatas);

    }

    private static void readBuildGridMap(String buildId,List<Map.Entry<Integer,List<HouseData>>> datas) throws IOException {


        writer.newLine();
        writer.newLine();
        writer.write("/*  import build:" + buildId + "*/");
        writer.newLine();

        for(Map.Entry<Integer,List<HouseData>> data: datas){
            if (buildIdMap.get(buildId) == null){
                throw  new IllegalArgumentException("idError:" + buildId);
               // System.out.println("ID ERROR:" + buildId);
            }

            writer.write("INSERT INTO BUILD_GRID_MAP(ID,BUILD_ID,NAME,_ORDER) VALUES('I-" + gridId + "','" + buildIdMap.get(buildId) + "','第" + data.getKey() + "页'," + data.getKey() +");");
            writer.newLine();

            Map<Integer,String> unitMap = new HashMap<Integer, String>();


            Map<Integer,Map<Integer,List<HouseData>>> formatHouse = new HashMap<Integer, Map<Integer, List<HouseData>>>();
            for(HouseData houseData: data.getValue()){
                unitMap.put(houseData.unit, houseData.unitName);
                Map<Integer,List<HouseData>> floor = formatHouse.get(houseData.inFloor);
                if (floor == null){
                    floor = new HashMap<Integer, List<HouseData>>();
                    formatHouse.put(houseData.inFloor,floor);
                }

                List<HouseData> unit = floor.get(houseData.unit);
                if (unit == null){
                    unit = new ArrayList<HouseData>();
                    floor.put(houseData.unit,unit);
                }
                unit.add(houseData);
            }

            Map<Integer,Integer> unitColSpan = new HashMap<Integer, Integer>();
            for(Map<Integer,List<HouseData>> floorUnit: formatHouse.values()){

                for(Map.Entry<Integer,List<HouseData>> entry: floorUnit.entrySet()){
                    int colspan = 0;
                    for(HouseData houseData: entry.getValue()){
                        colspan += houseData.colspan - houseData.unioncol - houseData.unioncolLeft;
                    }
                    Integer col = unitColSpan.get(entry.getKey());
                    if ((col == null) || (col < colspan)){
                        unitColSpan.put(entry.getKey(),colspan);
                    }
                }
            }

            List<Map.Entry<Integer,Integer>> unitColEntryList = new ArrayList<Map.Entry<Integer, Integer>>(unitColSpan.entrySet());

            Collections.sort(unitColEntryList, new Comparator<Map.Entry<Integer, Integer>>() {
                public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });


            writer.write("INSERT INTO HOUSE_GRID_TITLE(ID,_ORDER,TITLE,COLSPAN,GRILD_ID) VALUES('I-" +
                    titleId++ + "',0,'',1,'I-" +  gridId +  "');");
            int k = 1;
            for (Map.Entry<Integer,Integer> unitColEntry: unitColEntryList){
                writer.write("INSERT INTO HOUSE_GRID_TITLE(ID,_ORDER,TITLE,COLSPAN,GRILD_ID) VALUES('I-" +
                        titleId++ + "'," + k++ + ",'" + unitMap.get(unitColEntry.getKey())  + "'," + unitColEntry.getValue() + ",'I-" +  gridId +  "');");

            }


            Map<Integer,List<List<HouseData>>> step1 = new HashMap<Integer, List<List<HouseData>>>();
            for(Map.Entry<Integer,Map<Integer,List<HouseData>>> floor: formatHouse.entrySet()){

                List<Map.Entry<Integer,List<HouseData>>> unitEntryList =
                        new ArrayList<Map.Entry<Integer, List<HouseData>>>(floor.getValue().entrySet());


                Collections.sort(unitEntryList, new Comparator<Map.Entry<Integer, List<HouseData>>>() {
                    public int compare(Map.Entry<Integer, List<HouseData>> o1, Map.Entry<Integer, List<HouseData>> o2) {
                        return o1.getKey().compareTo(o2.getKey());
                    }
                });
                List<List<HouseData>> unitList = new ArrayList<List<HouseData>>(unitEntryList.size());
                for(Map.Entry<Integer,List<HouseData>> entry: unitEntryList){
                    Collections.sort(entry.getValue(), new Comparator<HouseData>() {
                        public int compare(HouseData o1, HouseData o2) {
                            return Integer.valueOf(o1.order).compareTo(o2.order);
                        }
                    });
                    unitList.add(entry.getValue());
                }

                step1.put(floor.getKey(),unitList);
            }



            List<Map.Entry<Integer, List<List<HouseData>>>> floorEntryList = new ArrayList<Map.Entry<Integer, List<List<HouseData>>>>(step1.entrySet());

            Collections.sort(floorEntryList, new Comparator<Map.Entry<Integer, List<List<HouseData>>>>() {
                public int compare(Map.Entry<Integer, List<List<HouseData>>> o1, Map.Entry<Integer, List<List<HouseData>>> o2) {
                    return o2.getKey().compareTo(o1.getKey());
                }
            });

            List<List<List<HouseData>>> floorList = new ArrayList<List<List<HouseData>>>();

            for(Map.Entry<Integer, List<List<HouseData>>> entry: floorEntryList){
                floorList.add(entry.getValue());
            }

            //---m

            int maxRow = 0;

            for(List<List<HouseData>> floor: floorList){
                int count = 0;
                for (List<HouseData> fu: floor){
                    for(HouseData hd: fu){
                        count += hd.colspan;
                    }
                }
                if (count > maxRow){
                    maxRow = count;
                }
            }
            //System.out.println("Size:" + floorList.size() + "|" + maxRow);

            //maxRow = maxRow + 10;

            try {
                HouseData[][] dm = new HouseData[floorList.size()][maxRow];
                for (int i = 0; i < floorList.size(); i++) {
                    int j = 0;
                    for (List<HouseData> fu : floorList.get(i)) {
                        for (HouseData hd : fu) {
                            for (int x = 0; x < hd.colspan; x++) {
                                for (int y = 0; y < hd.rowspan; y++) {
                                    //System.out.print(String.valueOf(i + y) + "," + (j + x) );

                                    while (dm[i + y][j] != null) {
                                        j++;
                                    }
                                    dm[i + y][j] = hd;
                                    j = j++;
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < floorList.size(); i++) {
                    for (int j = 0; j < maxRow; j++) {
                      if (dm[i][j].index == null) {
                            dm[i][j].index = j;
                        }
                    }
                }

            }catch ( Exception e){

                for(List<List<HouseData>> floor: floorList){
                    int j = 0;
                    for (List<HouseData> fu: floor){
                        for(HouseData hd: fu){
                            hd.index = j++;
                        }
                    }

                }

                System.out.println("error:" + buildId);


            }

            //---

            int i = 0;
            for(List<List<HouseData>> floor: floorList){
                //int j = 0;
                writer.write("INSERT INTO GRID_ROW(ID,TITLE,_ORDER,FLOOR_INDEX,GRID_ID) VALUES('I-" +
                        rowId  + "','" + getFloorName(floor)  + "'," + i + "," + getFloorIndex(floor) +
                        ",'I-" + gridId + "');");

                writer.newLine();

                for(List<HouseData> houseDatas: floor)
                    for(HouseData houseData: houseDatas){
                        if (houseData.index == null){
                            System.out.println("------------null  :" + buildId);
                        }
                        if (houseData.houseCode != null) {
                            writer.write("INSERT INTO GRID_BLOCK(ID,ROW_ID,_ORDER,COLSPAN,ROWSPAN,UNIT_NAME,UNIT_INDEX,HAVE_DOWN_ROOM,HOUSE_CODE,HOUSE_ORDER) VALUES('I-" +
                                    blockId++ + "','I-" + rowId + "'," + houseData.index + "," + houseData.colspan + "," + houseData.rowspan + ",'" +
                                    houseData.unitName + "'," + houseData.unit + ",FALSE,'" + houseData.houseCode + "','" + houseData.houseOrder + "');");
                        }else{
                            writer.write("INSERT INTO GRID_BLOCK(ID,ROW_ID,_ORDER,COLSPAN,ROWSPAN,UNIT_NAME,UNIT_INDEX,HAVE_DOWN_ROOM) VALUES('I-" +
                                    blockId++ + "','I-" + rowId + "'," + houseData.index + "," + houseData.colspan + "," + houseData.rowspan + ",'" +
                                    houseData.unitName + "'," + houseData.unit + ",FALSE);");
                        }
                        writer.newLine();
                    }



                rowId++;
                i++;
            }


            gridId++;

        }
        writer.flush();

    }

    private static String getFloorName(List<List<HouseData>> datas){
        for(List<HouseData> data: datas){
            for(HouseData h: data){
                if (h.rowspan == 1){
                    return h.floorName;
                }
            }
        }
        return "";
    }

    private static int getFloorIndex(List<List<HouseData>> datas){
        for(List<HouseData> data: datas){
            for(HouseData h: data){
                if (h.rowspan == 1){
                    return h.inFloor;
                }
            }
        }
        return 0;
    }


    private static class HouseData {

        String houseCode;

        String houseOrder;

        int page;

        int order;

        int unit;

        String unitName;

        int inFloor;

        String floorName;

        int colspan;

        int rowspan;

        int unioncol;

        int unioncolLeft;

        Integer index = null;

        public HouseData(ResultSet rs) throws SQLException {
            this.page = rs.getInt(1);
            this.order = rs.getInt(2);
            this.unit = rs.getInt(3);
            this.unitName = rs.getString(4);
            this.inFloor = rs.getInt(5);
            this.floorName = rs.getString(6);
            this.colspan = rs.getInt(7);
            this.rowspan = rs.getInt(8);
            this.unioncol = rs.getInt(9);
            this.unioncolLeft = rs.getInt(10);
        }
    }


}
