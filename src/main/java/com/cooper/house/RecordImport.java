package com.cooper.house;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by cooper on 10/13/15.  WP83
 */
public class RecordImport {

    public static final String[] MUST_HAVE_SELECT = {
            "WP37", "WP43", "WP45", "WP46"
            , "WP47", "WP49", "WP2", "WP3"
            , "WP4", "WP6", "WP7", "WP8"
            , "WP10", "WP11", "WP12", "WP14"
            , "WP15", "WP16", "WP17", "WP21"
            , "WP24", "WP84", "WP100", "WP74"};
    private static final List<String> MUST_HAVE_SELECT_LIST = Arrays.asList(MUST_HAVE_SELECT);

    private static final String HOUSE_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHouseInfo";

    private static final String SHARK_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/shark";

    private static final String RECORD_DB_URL = "jdbc:jtds:sqlserver://192.168.1.4:1433/DGHOUSERECORD";

    private static final String OUT_FILE_PATH = "/Users/cooper/Documents/oldRecord.sql";

    private static final String ERROR_FILE_PATH = "/Users/cooper/Documents/oldRecordError.log";

    private static final String SUCCESS_FILE_PATH = "/Users/cooper/Documents/statusError.log";

    private static Connection houseConn;

    private static Connection sharkConn;

    private static Connection recordConn;

    private static BufferedWriter errorWriter;

    private static BufferedWriter successWriter;

    private static BufferedWriter sqlWriter;

    private static int count;

    private static int curCount;


    private static void begin() {

        curCount = 0;


        try {

            Statement statement = recordConn.createStatement();


            ResultSet bizRs = statement.executeQuery("SELECT count(DISTINCT HouseHistroy.NO) FROM HouseHistroy LEFT JOIN Business ON Business.ID = HouseHistroy.Business WHERE HouseHistroy.Business is not null");
            bizRs.next();
            count = bizRs.getInt(1);
            bizRs.close();
            statement.close();
            statement = recordConn.createStatement();

            bizRs = statement.executeQuery("SELECT DISTINCT HouseHistroy.NO FROM HouseHistroy LEFT JOIN Business ON Business.ID = HouseHistroy.Business WHERE HouseHistroy.Business is not null");

            //int testI = 0;

            while (bizRs.next()) {


                long time = new java.util.Date().getTime();
                try {
                  sqlWriter.write(business(bizRs.getString(1).trim()));
               //     sqlWriter.write(business("18292"));

                    sqlWriter.flush();
                    sqlWriter.newLine();
                    curCount ++;

                  System.out.println(String.valueOf(count) + "/" + curCount + "    " + bizRs.getString(1).trim() + "   " + (new java.util.Date().getTime() - time) + "ms"  +  "  " + ( new Double(curCount).doubleValue() / new Double(count).doubleValue()  * 100) + "%");
                } catch (NoSelectBizException e) {
                    errorWriter.write(bizRs.getString(1).trim() + ">" + e.bizId + ">" + "NoSelectBizException");

                } catch (MustHaveSelectBizException e) {
                    errorWriter.write(bizRs.getString(1).trim() + ">" + e.bizId + ">" + "MustHaveSelectBizException");
                }

                //TODO test
         //       if (++testI == 100){
         //           break;
         //       }


          }
            bizRs.close();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("查询业务出错");
            return;
        }catch (IOException e) {
            e.printStackTrace();
            System.out.print("写文件出错");
            return;
        }

    }


    private static String business(String houseCode) throws SQLException, NoSelectBizException, MustHaveSelectBizException, IOException {

        Map<String, ReadyBusiness> result = new HashMap<String, ReadyBusiness>();

        ReadyBusiness first = null;

        int lastOldState = 0;

        Statement statement = recordConn.createStatement();

        ResultSet bizRs = statement.executeQuery("SELECT  Business.NameID,Business.Memo,Business.SelectBiz,Business.FinalTime,Business.RegisterTime, Case CONVERT(VARCHAR(8),Business.BOTime,108) WHEN '00:00:00' THEN Convert(DATETIME,  CONVERT(VARCHAR(10),Business.BOTime ,23) + ' ' +CONVERT(VARCHAR(8),HouseHistroy.ChangeDate,108) ) ELSE  Business.BOTime END as FULLBOTIME , Business.ID, Business.NameID " +
                //9
                " ,HouseHistroy.HouseOrder,HouseHistroy.UnitName,HouseHistroy.InFloorName,HouseHistroy.HouseArea,HouseHistroy.UseArea,HouseHistroy.CommArea,HouseHistroy.ShineArea," +
                "HouseHistroy.LoftArea,HouseHistroy.CommParam,HouseHistroy.HouseType,HouseHistroy.UseType,HouseHistroy.Structure,HouseHistroy.KnotSize,HouseHistroy.HouseStation," +
                "HouseHistroy.EastWall,HouseHistroy.WestWall,HouseHistroy.SouthWall,HouseHistroy.NorthWall,HouseHistroy.MappingDate,HouseHistroy.Direction , HouseHistroy.No ," +
                "HouseHistroy.BuildID ,HouseHistroy.MainOwner, HouseHistroy.PoolMemo, Business.WorkID, HouseHistroy.HouseState FROM HouseHistroy LEFT JOIN Business ON Business.ID = HouseHistroy.Business WHERE HouseHistroy.Business is not null and HouseHistroy.NO = '" + houseCode + "' and (workid  not like '%WP83' and workid not like '%WP84') order by Business.BOTime, HouseHistroy.ChangeDate");

        while (bizRs.next()) {

            lastOldState = bizRs.getInt(34);
            String id = bizRs.getString(7);
            if (result.get(id) == null) {

                ReadyBusiness selectBiz = null;
                String selectBizId = bizRs.getString(3);
                if (selectBizId != null && !selectBizId.trim().equals("")) {
                    selectBiz = result.get(selectBizId);
                    if (selectBiz == null) {
                        throw new NoSelectBizException(bizRs.getString(1));
                    }
                }

                ReadyBusiness biz = new ReadyBusiness(houseCode, first, bizRs.getString(8), bizRs.getString(1), bizRs.getString(2), (selectBiz == null) ? null : selectBiz.getId(), bizRs.getTimestamp(4), bizRs.getTimestamp(5), bizRs.getTimestamp(6));

                if (MUST_HAVE_SELECT_LIST.contains(biz.getDefineId())) {
                    if (selectBiz == null) {
                        throw new MustHaveSelectBizException(id);
                    }
                    selectBiz.setStatus("COMPLETE_CANCEL");
                }


                Statement sD = sharkConn.createStatement();
                Statement hD = houseConn.createStatement();

                ResultSet rs;

                String oldOwnerId = bizRs.getString(31);

                if (oldOwnerId == null || oldOwnerId.trim().equals("")) {
                    biz.setOwnerId(null, null, null);
                } else {
                    String oldCardId = null;
                    String owner = "";


                    rs = hD.executeQuery("select ID,NO,Type,Cancel,CardNO,Memo,PrintTime from HouseCard WHERE (Type = 111 or Type= 198) and BizID = '" + id + "' and OwnerID = '" + oldOwnerId + "'");


                    if (rs.next()) {
                        oldCardId = rs.getString(1);
                        owner = "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                                Q.v(
                                        Q.p(bizRs.getString(1)), Q.pm(rs.getString(2)), Q.p(rs.getInt(3) == 111 ? "OWNER_RSHIP" : "NOTICE")

                                        , Q.p(bizRs.getString(1)), Q.p(rs.getBoolean(4))

                                )

                                + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                                Q.v(Q.p(bizRs.getString(1)), Q.pm(rs.getString(5)), Q.p(rs.getString(6)), Q.p(rs.getTimestamp(7)))

                                + ");";
                    }
                    rs.close();

                    String ovalue = ownerInfo(oldOwnerId);

                    if (ovalue != null) {
                        owner += "INSERT INTO  BUSINESS_OWNER(ID,NAME,ID_TYPE,ID_NO,PHONE,ROOT_ADDRESS,ADDRESS,BUSINESS,OWNER_CARD) VALUES(" +
                                Q.v(Q.p(bizRs.getString(1)), ovalue, Q.p(bizRs.getString(1)), oldCardId == null ? "NULL" : Q.p(bizRs.getString(1)))

                                + ");";
                    }


                    biz.setOwnerId(oldOwnerId, oldCardId, owner);
                }

                //System.out.println(bizRs.getString(1) );
                rs = sD.executeQuery("select MEMO from DGBiz where code='" + bizRs.getString(33) + "'");
                rs.next();
                biz.setDefineName(rs.getString(1));
                rs.close();
                //sD.close();
                //sD = sharkConn.createStatement();
                rs = sD.executeQuery("SELECT dateadd(s,(cast(left(cast(A.LastStateTime as varchar(20)),10) as int(4))+8*60*60),'1970-01-01 00:00:00') as creaetime" +
                        " FROM SHKActivities AS A WHERE "
                        + " name = '启动' and processid='" + bizRs.getString(1) + "'");

                if (rs.next())
                    biz.setApplyTime(rs.getTimestamp(1));

                rs.close();
                //sD.close();


                String startHouse = "INSERT INTO HOUSE(ID,HOUSE_ORDER,HOUSE_UNIT_NAME,IN_FLOOR_NAME,HOUSE_AREA," +
                        "USE_AREA,COMM_AREA,SHINE_AREA,LOFT_AREA,COMM_PARAM," +
                        "HOUSE_TYPE,USE_TYPE,STRUCTURE,KNOT_SIZE,ADDRESS," +
                        "EAST_WALL,WEST_WALL,SOUTH_WALL,NORTH_WALL,MAP_TIME," +
                        "DIRECTION,HOUSE_CODE,HAVE_DOWN_ROOM,    BUILD_CODE,MAP_NUMBER," +
                        "BLOCK_NO,BUILD_NO,STREET_CODE,DOOR_NO,UP_FLOOR_COUNT," +
                        "FLOOR_COUNT,DOWN_FLOOR_COUNT,BUILD_TYPE,PROJECT_CODE,PROJECT_NAME," +
                        "COMPLETE_DATE,DEVELOPER_CODE,DEVELOPER_NAME,SECTION_CODE,SECTION_NAME," +
                        "DISTRICT_CODE,DISTRICT_NAME,BUILD_NAME,BUILD_DEVELOPER_NUMBER)  " +
                        "VALUES(" + Q.v(Q.p(bizRs.getString(1) + "-s"), Q.pm(bizRs.getString(9)), Q.p(bizRs.getString(10)),
                        Q.pm(bizRs.getString(11)), Q.pm(bizRs.getBigDecimal(12)),
                        Q.p(bizRs.getBigDecimal(13)), Q.p(bizRs.getBigDecimal(14)), Q.p(bizRs.getBigDecimal(15)),
                        Q.p(bizRs.getBigDecimal(16)), Q.p(bizRs.getBigDecimal(17)), Q.pmwc(bizRs.getString(18)),
                        Q.pmw(bizRs.getString(19), "808"), Q.pmw(bizRs.getString(20), "827"), Q.p(bizRs.getString(21))
                        , Q.pm(bizRs.getString(22)), Q.p(bizRs.getString(23)), Q.p(bizRs.getString(24)),
                        Q.p(bizRs.getString(25)), Q.p(bizRs.getString(26)), Q.p(bizRs.getTimestamp(27)),
                        Q.p(bizRs.getString(28)), Q.p(bizRs.getString(29)), "FALSE");


                String house = "INSERT INTO HOUSE(HOUSE_STATUS,ID,HOUSE_ORDER,HOUSE_UNIT_NAME,IN_FLOOR_NAME,HOUSE_AREA," +
                        "USE_AREA,COMM_AREA,SHINE_AREA,LOFT_AREA,COMM_PARAM," +
                        "HOUSE_TYPE,USE_TYPE,STRUCTURE,KNOT_SIZE,ADDRESS," +
                        "EAST_WALL,WEST_WALL,SOUTH_WALL,NORTH_WALL,MAP_TIME," +
                        "DIRECTION,HOUSE_CODE,HAVE_DOWN_ROOM,    BUILD_CODE,MAP_NUMBER," +
                        "BLOCK_NO,BUILD_NO,STREET_CODE,DOOR_NO,UP_FLOOR_COUNT," +
                        "FLOOR_COUNT,DOWN_FLOOR_COUNT,BUILD_TYPE,PROJECT_CODE,PROJECT_NAME," +
                        "COMPLETE_DATE,DEVELOPER_CODE,DEVELOPER_NAME,SECTION_CODE,SECTION_NAME," +
                        "DISTRICT_CODE,DISTRICT_NAME,BUILD_NAME,BUILD_DEVELOPER_NUMBER,POOL_MEMO,MAIN_OWNER,REG_INFO,CONTRACT_OWNER)  " +
                        "VALUES(" + Q.v( biz.getMainStatus() , Q.p(bizRs.getString(1)), Q.pm(bizRs.getString(9)), Q.p(bizRs.getString(10)),
                        Q.pm(bizRs.getString(11)), Q.pm(bizRs.getBigDecimal(12)),
                        Q.p(bizRs.getBigDecimal(13)), Q.p(bizRs.getBigDecimal(14)), Q.p(bizRs.getBigDecimal(15)),
                        Q.p(bizRs.getBigDecimal(16)), Q.p(bizRs.getBigDecimal(17)), Q.pmwc(bizRs.getString(18)),
                        Q.pmw(bizRs.getString(19), "808"), Q.pmw(bizRs.getString(20), "827"), Q.p(bizRs.getString(21))
                        , Q.pm(bizRs.getString(22)), Q.p(bizRs.getString(23)), Q.p(bizRs.getString(24)),
                        Q.p(bizRs.getString(25)), Q.p(bizRs.getString(26)), Q.p(bizRs.getTimestamp(27)),
                        Q.p(bizRs.getString(28)), Q.p(bizRs.getString(29)), "FALSE");


                String buildId = bizRs.getString(30);
                //44

                //23
                //21 个
                String afterSql = null;

                if (buildId != null && !buildId.trim().equals("")) {
                    //sD = sharkConn.createStatement();

                    rs = hD.executeQuery("select c.BUILDNO, c.DoorNO,isNull(c.FloorCount,0),c.BuildType,c.ProjectID,projcetName, " +
                            "c.DeveloperID, dpr.name as developername,c.SectionID,c.sectionname,c.Districtno,c.Districtname,c.BuildName ,c.MapNO, c.BlockNO,c.BuildNO  from"
                            + " (select b.*,dd.name as Districtname,dd.no as Districtno from"
                            + " (select a.*,ds.name as sectionname,ds.DistrictID as sDistrictID from"
                            + " (select db.*,dp.name as projcetName,dp.sectionid as bsectionid,developerid,BuildSize"
                            + " from Build as db"
                            + " left join project as dp on db.projectid=dp.id) as a"
                            + " left join section as ds on a.bsectionid=ds.id) as b"
                            + " left join District as dd on b.sDistrictID=dd.id) as c"
                            + " left join Developer as dpr on c.developerid=dpr.id"
                            + " where c.id ='" + buildId + "'");
                    if (rs.next()) {
                        afterSql = "," + Q.v(Q.p(bizRs.getString(30)), Q.p(rs.getString(14)), Q.pm(rs.getString(15)), Q.pm(rs.getString(16))
                                , "NULL", Q.p(rs.getString(2)), String.valueOf(rs.getInt(3))
                                , String.valueOf(rs.getInt(3)), "0", Q.p(rs.getString(4)), Q.pmId(rs.getString(5)),
                                Q.pm(rs.getString(6)), "NULL", Q.p(rs.getString(7)), Q.p(rs.getString(8)),
                                Q.pmId(rs.getString(9)), Q.pm(rs.getString(10)), Q.pm(rs.getString(11)),
                                Q.pm(rs.getString(12)), Q.pm(rs.getString(13)), "NULL");


                    }
                    rs.close();
                    //sD.close();
                }


                if (afterSql == null) {
                    afterSql = "," + Q.v("''", "NULL", "'未知'", "'未知'", "NULL", "NULL", "0"
                            , "0", "0", "NULL", "''",
                            "'未知'", "NULL", "NULL", "NULL",
                            "''", "'未知'", "'未知'",
                            "'未知'", "'未知'", "NULL");
                }

                house += afterSql;


                startHouse += afterSql + ");";

                biz.setStartHouse(startHouse);


                int poolType = bizRs.getInt(32);

                if (poolType == 221) {
                    house += "," + Q.p("SINGLE_OWNER");
                } else if (poolType == 222 || poolType == 218) {
                    house += "," + Q.p("TOGETHER_OWNER");
                } else if (poolType == 219) {
                    house += "," + Q.p("SHARE_OWNER");
                } else {
                    house += ",NULL";
                }


                house += biz.getNewOwnerId() == null ? ",NULL" : ",'" + biz.getNewOwnerId() + "'";


                rs = hD.executeQuery("select ho.Name,ho.IDType,ho.IDNO,hc.Relation,hc.PoolArea,hc.Perc,ho.Phone,hc.PrintTime,hc.Memo from houseCard hc left JOIN OwnerInfo ho on ho.ID = hc.OwnerID where hc.id is not null and (hc.type = 77) and (hc.no='' or hc.no is null) and bizid ='" + id + "'");

                int i = 0;

                // String poolOwner = "";
                while (rs.next()) {

                    biz.putPoolOwner(bizRs.getString(1) + "-" + i, "INSERT INTO BUSINESS_POOL(ID,NAME,ID_TYPE,ID_NO,RELATION,POOL_AREA,PERC,PHONE,CREATE_TIME,MEMO,BUSINESS) VALUES(" +

                            Q.v(Q.p(bizRs.getString(1)  + "-" + i), Q.p(rs.getString(1)), Q.pCardType(rs.getInt(2)), Q.pm(rs.getString(3)),
                                    Q.p(rs.getString(4)), Q.p(rs.getBigDecimal(5)), Q.p(rs.getString(6)),
                                    Q.p(rs.getString(7)), Q.pm(rs.getTimestamp(8)), Q.p(rs.getString(9)), Q.p(bizRs.getString(1)))

                            + ");");

                    i++;


                }


                //biz.setPoolOwner(poolOwner);

                rs.close();


                int houseFrom = 0;
                int houseProperty = 0;

                rs = sD.executeQuery("SELECT VariableValueLONG from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'house_from' and sp.id = '" + bizRs.getString(8) + "'");

                if (rs.next()) {
                    houseFrom = rs.getInt(1);

                }

                rs.close();
                rs = sD.executeQuery("SELECT VariableValueLONG from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'house_property' and sp.id = '" + bizRs.getString(8) + "'");

                if (rs.next()) {
                    houseProperty = rs.getInt(1);
                }
                rs.close();

                if (houseFrom <= 0 && houseProperty <= 0) {
                    house += ",NULL";
                } else {
                    house = "INSERT INTO HOUSE_REG_INFO(ID,HOUSE_PORPERTY,HOUSE_FROM) VALUES(" +
                            Q.v(Q.p(bizRs.getString(1)), houseProperty <= 0 ? "909" : String.valueOf(houseProperty)
                                    , houseFrom <= 0 ? "4270" : String.valueOf(houseFrom)

                            )

                            + ");" + house + "," + Q.p(bizRs.getString(1));
                }

                String contractOwner = null;
                String contractId = null;

                if (biz.getDefineId().equals("WP42")) {

                    rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'record_people' and sp.id = '" + bizRs.getString(8) + "'");
                    if (rs.next()) {

                        String owner = ownerInfo(rs.getString(1));
                        if (owner != null) {


                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'house_remark_contract_id' and sp.id = '" + bizRs.getString(8) + "'");

                            if (rs.next() && rs.getString(1) != null && !rs.getString(1).trim().equals("")) {

                                contractId = Q.p(rs.getString(1));
                                rs.close();

                                String date = "'2000-1-1'";

                                rs = sD.executeQuery("SELECT VariableValueDATE from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'CONTRACT_DATE' and sp.id = '" + bizRs.getString(8) + "'");
                                if (rs.next()) {
                                    date = Q.pm(rs.getTimestamp(1));
                                }


                                contractOwner = "INSERT INTO CONTRACT_OWNER(ID,NAME,ID_TYPE,ID_NO,PHONE,ROOT_ADDRESS,ADDRESS,BUSINESS,CONTRACT_DATE,TYPE,HOUSE_CODE)  VALUES(" + Q.v(contractId, owner, Q.p(bizRs.getString(1)), date, "'MAP_SELL'", Q.p(houseCode))
                                        + "); ";

                            }


                        }
                        rs.close();
                        //rs = sD.executeQuery()
                    }

                }

                if (contractOwner != null) {
                    house = contractOwner + house + "," + contractId + ");";
                } else {
                    house += ",NULL);";
                }

                biz.setHouse(house);


                rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'mortgage_obligee' and sp.id = '" + bizRs.getString(8) + "'");

                if (rs.next()) {
                    if (rs.getString(1) != null || !rs.getString(1).trim().equals("")) {

                        String fincNo = rs.getString(1);
                        rs.close(); //FINANCE_CORP
                        rs = hD.executeQuery("select Name,Phone from FinancialInfo WHERE NO ='" + fincNo + "'");
                        if (rs.next()) {

                            SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String finalSql = "INSERT INTO FINANCIAL(ID,NAME,PHONE,FINANCIAL_TYPE,CREATE_TIME, CARD) VALUES(" +
                                    Q.v(Q.p(bizRs.getString(1)), Q.pm(rs.getString(1)), Q.p(rs.getString(2)), "'FINANCE_CORP'",
                                            Q.p(f.format(new Date())));

                            rs.close();
                            rs = hD.executeQuery("select ID,NO,Type,Cancel,CardNO,Memo,PrintTime from HouseCard WHERE Type = 110  and BizID = '" + id + "'");


                            if (rs.next()) {
                                finalSql = "INSERT INTO MAKE_CARD(ID,NUMBER,TYPE,BUSINESS_ID,ENABLE) VALUES(" +
                                        Q.v(
                                                Q.p(bizRs.getString(1) + "-t"), Q.pm(rs.getString(2)), Q.p("MORTGAGE_CARD")

                                                , Q.p(bizRs.getString(1)), Q.p(rs.getBoolean(4))

                                        )

                                        + ");INSERT INTO CARD_INFO(ID,CODE,MEMO,PRINT_TIME) VALUES(" +

                                        Q.v(Q.p(bizRs.getString(1) + "-t"), Q.pm(rs.getString(5)), Q.p(rs.getString(6)), Q.p(rs.getTimestamp(7)))

                                        + ");" + finalSql + ",'" + bizRs.getString(1) + "-t" +

                                        "');";
                            } else {
                                finalSql += ",NULL);";
                            }


                            String m1 = "0";
                            String m2 = "NULL";
                            String m3 = "'power.type.other'";
                            String m4 = "'2000-1-1'";
                            String m5 = "'2000-1-1'";
                            String m6 = "0";
                            rs = sD.executeQuery("SELECT VariableValueDBL from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'highest_mount_money' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next()) {
                                m1 = String.valueOf(rs.getDouble(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueVCHAR from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'warrant_scope' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && rs.getString(1) != null && !rs.getString(1).trim().equals("")) {
                                m2 = Q.p(rs.getString(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueLONG from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'interest_type' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && (rs.getInt(1) > 0)) {
                                m3 = Q.p(String.valueOf(rs.getInt(1)));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueDATE from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'mortgage_due_time_s' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && rs.getTimestamp(1) != null) {
                                m4 = Q.p(rs.getTimestamp(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueDATE from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'mortgage_due_time_e' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next() && rs.getTimestamp(1) != null) {
                                m5 = Q.p(rs.getTimestamp(1));
                            }
                            rs.close();
                            rs = sD.executeQuery("SELECT VariableValueDBL from  SHKProcesses sp LEFT JOIN  SHKProcessData spd on spd.process = sp.oid where spd.VariableDefinitionId = 'MORTGAGE_AREA' and sp.id = '" + bizRs.getString(8) + "'");
                            if (rs.next()) {
                                m6 = String.valueOf(rs.getDouble(1));
                            }


                            finalSql += "INSERT INTO MORTGAEGE_REGISTE(ID,HIGHEST_MOUNT_MONEY,WARRANT_SCOPE,INTEREST_TYPE,MORTGAGE_DUE_TIME_S,MORTGAGE_TIME,MORTGAGE_AREA,BUSINESS_ID,FIN,OWNER,ORG_NAME)  VALUES(" +
                                    Q.v(Q.p(bizRs.getString(1)), m1, m2, m3, m4, m5, m6, Q.p(bizRs.getString(1)), Q.p(bizRs.getString(1)), Q.p(biz.getNewOwnerId()), Q.p("开源市房产局"))
                             + ");";


                            biz.setMortgaeg(finalSql);


                            //
                        }

                        rs.close();


                    }
                }


                result.put(id, biz);
                first = biz;


            }
        }
        bizRs.close();
        statement.close();

        if (first != null) {
            Integer lastOldStatus = first.getOldStatus();
            boolean pass = false;
            if (lastOldStatus == null && (lastOldState == 127 || lastOldState == 118) ) {
                pass = true;
            }else if (lastOldStatus != null && lastOldStatus.equals(lastOldState)){
                pass = true;
            }
            if (!pass){
                successWriter.write(houseCode + ":old:" + lastOldState + "calc:" + first.getMainStatus());
                successWriter.newLine();
            }

            return first.run();
        }else
            return "";

    }


    private static String ownerInfo(String ownerId) throws SQLException {
        Statement statement = houseConn.createStatement();
        ResultSet rs = statement.executeQuery(" SELECT Name,IDType,IDNO,Phone,City,Address FROM OwnerInfo WHERE ID = '" + ownerId + "'");
        if (rs.next()) {
            return Q.v(Q.p(rs.getString(1)), Q.pCardType(rs.getInt(2)), Q.pm(rs.getString(3)), Q.p(rs.getString(4)),
                    Q.p(rs.getString(5)), Q.p(rs.getString(6)));
        } else {
            return null;
        }
    }


    public static void main(String[] args) {


        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.print("数据库驱动加载失败");
            e.printStackTrace();
            return;
        }
        try {
            houseConn = DriverManager.getConnection(HOUSE_DB_URL, "sa", "dgsoft");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("DGHouseInfo 连接失败");
            return;
        }
        try {
            sharkConn = DriverManager.getConnection(SHARK_DB_URL, "sa", "dgsoft");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("shark 连接失败");
            return;
        }
        try {
            recordConn = DriverManager.getConnection(RECORD_DB_URL, "sa", "dgsoft");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print("record 连接失败");
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


        file = new File(ERROR_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            errorWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("error 文件创建失败");
            e.printStackTrace();
            return;
        }


        file = new File(SUCCESS_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            successWriter = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.println("success 文件创建失败");
            e.printStackTrace();
            return;
        }

        try {
            begin();
        } finally {


            try {
                successWriter.flush();
                successWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                sqlWriter.flush();
                sqlWriter.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            try {
                errorWriter.flush();
                errorWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                houseConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                sharkConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                recordConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public static class NoSelectBizException extends Exception {
        private String bizId;

        public NoSelectBizException(String bizId) {
            this.bizId = bizId;
        }
    }


    public static class MainOwnerNotFoundException extends Exception {
        private String bizId;

        public MainOwnerNotFoundException(String bizId) {
            this.bizId = bizId;
        }
    }

    public static class MustHaveSelectBizException extends Exception {
        private String bizId;

        public MustHaveSelectBizException(String bizId) {
            this.bizId = bizId;
        }
    }
}
