package com.cooper.house;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cooper on 10/13/15.
 */
public class ReadyBusiness {




    private static final String[] NO_RECORD = {
            "WP42","WP43","WP50","WP51","WP85"
    };

    private static final String[] BEFOR_POOL = {

            "WP9",
            "WP10",
            "WP11",
            "WP12",
            "WP13",
            "WP14",
            "WP15",
            "WP16",
            "WP17",

            "WP25",
            "WP26",

            "WP73",
            "WP74",

            "WP35",
            "WP36",
            "WP37",

            "WP48",
            "WP49",
            "WP22",
            "WP23",
            "WP24",

            "WP45",

            "WP1",
            "WP2",
            "WP3",
            "WP4",
            "WP5",
            "WP6",
            "WP7",
            "WP8" };




    private static final List<String> NO_RECORD_LIST = Arrays.asList(NO_RECORD);

    private static final List<String> BEFOR_POOL_LIST = Arrays.asList(BEFOR_POOL);

    private Map<String ,String> poolOwner = new HashMap<String, String>();

    private ReadyBusiness befor;

    private ReadyBusiness after;

    private String id;

    private String memo;

    private String status;

    private String selectBusiness;

    private String defineName;

    private String defineId;


    private java.sql.Timestamp applyTime;

    private java.sql.Timestamp  checkTime;

    private java.sql.Timestamp  regTime;

    private java.sql.Timestamp  recordTime;

    private String house;

    private String oldOwnerId;

    private String oldCardId;

    private String owner;


    private String mortgaeg;

    private String startHouse ;

    private String houseCode;

    public void setMortgaeg(String mortgaeg) {
        this.mortgaeg = mortgaeg;
    }

    public void setStartHouse(String startHouse) {
        this.startHouse = startHouse;
    }

    public void putPoolOwner(String id,String poolOwner) {
        this.poolOwner.put(id,poolOwner);
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public void setApplyTime(java.sql.Timestamp  applyTime) {
        this.applyTime = applyTime;
    }

    public void setDefineName(String defineName) {
        this.defineName = defineName;
    }

    public void setOwnerId(String oldOwnerId, String oldCardId, String owner) {
        this.oldCardId = oldCardId;
        this.oldOwnerId = oldOwnerId;
        if (oldOwnerId == null){
            newOwnerId = null;
            this.owner = null;
        }else if (befor != null  &&  befor.oldOwnerId!= null &&  befor.oldOwnerId.equals(oldOwnerId) && ( (befor.oldCardId == null && oldCardId == null) || (befor.oldCardId != null && befor.oldCardId.equals(oldCardId)))){
            newOwnerId = befor.newOwnerId;
            this.owner = null;
        }else{
            newOwnerId = this.id;
            this.owner = owner;
        }
    }

    private String newOwnerId;

    public String getNewOwnerId() {
        return newOwnerId;
    }

    public String getDefineId() {
        return defineId;
    }

    public String getId() {
        return id;
    }


    public ReadyBusiness(String houseCode, ReadyBusiness start, String workId, String id, String memo, String selectBusiness, java.sql.Timestamp  checkTime, java.sql.Timestamp  regTime, java.sql.Timestamp  recordTime) {

        if (start != null) {
            start.after = this;
            this.befor = start;
        }

        this.houseCode = houseCode;
        this.id = id;
        this.memo = memo;
        this.selectBusiness = selectBusiness;
        this.checkTime = checkTime;
        this.regTime = regTime;
        this.recordTime = recordTime;

        String[] temp = workId.split("_");

        defineId = temp[temp.length-1];
        status = "COMPLETE";

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String run(){
        if (befor == null){
            return genHouseBusinessSql();
        }else{
            return befor.run();
        }
    }

    private String genHouseBusinessSql(){
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String result =  "INSERT INTO OWNER_BUSINESS(ID,VERSION,SOURCE,MEMO,STATUS,DEFINE_NAME,DEFINE_ID,DEFINE_VERSION,SELECT_BUSINESS,CREATE_TIME,APPLY_TIME,CHECK_TIME,REG_TIME,RECORD_TIME,RECORDED,TYPE) VALUES(" +
                 Q.v( Q.p(id) , "1" , "'BIZ_IMPORT'", Q.p(memo) , Q.p(status) , Q.p(defineName), Q.p(defineId), "NULL", Q.p(selectBusiness), Q.p(f.format(new Date())),
                         Q.p(applyTime == null ? recordTime : applyTime) , Q.p(checkTime == null ? recordTime : checkTime) , Q.p(regTime == null ? recordTime :regTime ) ,Q.p(recordTime) , NO_RECORD_LIST.contains(getDefineId()) ? "FALSE" : "TRUE" ,"'NORMAL_BIZ'") +
                ");";
        if (owner != null)
            result += owner;

        if (mortgaeg != null){
            result += mortgaeg;
        }

        if (befor == null){
            result += startHouse;
        }

        result += house + "INSERT INTO BUSINESS_HOUSE(ID,HOUSE_CODE,BUSINESS_ID,START_HOUSE,AFTER_HOUSE,CANCELED) VALUES("

        + Q.v( Q.p(id), Q.p(houseCode), Q.p(id), Q.p((befor == null) ? id + "-s" : befor.id),
            Q.p(id),"FALSE"
        ) +
        ");";

        if (!poolOwner.isEmpty()){
            for(String pool : poolOwner.values()){
                result += pool;
            }
        }
        result += linkPoolOwner(id);


        if (after != null){
            result += after.genHouseBusinessSql();
        }else{
            result += "INSERT INTO HOUSE_RECORD(HOUSE_CODE,HOUSE) VALUES("+

                    Q.v(Q.p(houseCode), Q.p(id))

                    +");";
        }



        return result;
    }

    private String linkPoolOwner(String bizID){
        String result = "";
        if (!poolOwner.isEmpty()){
            for(Map.Entry<String,String> entry: poolOwner.entrySet()){
                result += "INSERT INTO HOUSE_POOL(HOUSE, POOL) VALUES(" + Q.v(

                        Q.p(bizID),Q.p(entry.getKey())

                ) + ");";
            }
        }else if (befor != null && BEFOR_POOL_LIST.contains(getDefineId())){
            result = befor.linkPoolOwner(bizID);
        }

        return result;


    }


    //



 //   保留上一个共有权人



}
