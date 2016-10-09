package com.wxy.House;

import com.cooper.house.Q;


import java.sql.*;

/**
 * Created by wxy on 2016-09-22.
 */
public class SlectInfo {

     public static String svs(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueVCHAR from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
            return rs.getString(1);
        }
        return null;
    }

    public static Timestamp svt(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueDATE from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
            return rs.getTimestamp(1);
        }
        return null;
    }

    public static Double svd(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueDBL from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
            return rs.getDouble(1);
        }
        return null;
    }

    public static Long svl(Statement sD, String varId, String RecordBizNo) throws SQLException {
        ResultSet rs = sD.executeQuery("select VariableValueLONG from " +
                "(select db.RecordBizNO,sp.id,sp.oid from DGHouseRecord..Business as db " +
                "left join shark..SHKProcesses as sp on db.nameid = sp.id) as a " +
                "left join shark..SHKProcessData as spd on a.oid=spd.process " +
                "where spd.VariableDefinitionId = " + Q.p(varId) +
                "and RecordBizNO=" + Q.p(RecordBizNo));
        if (rs.next()){
            return rs.getLong(1);
        }
        return null;
    }

    public static ResultSet bar(Statement sD,String IDorNo) throws SQLException{

        if (IDorNo!=null && !IDorNo.equals("")) {
            ResultSet hs = sD.executeQuery("select * from OwnerInfo where ID='" + IDorNo + "'");
            //System.out.println("select * from OwnerInfo where ID='" + IDorNo + "'");
            hs.last();
            int rsCount = hs.getRow();
            //System.out.println("rsCount--"+rsCount);
            if (rsCount > 0) {
                hs.first();
                return hs;
            } else {
                hs = sD.executeQuery("select * from OwnerInfo where No='" + IDorNo + "'");
                //System.out.println("select * from OwnerInfo where No='" + IDorNo + "'");
                hs.last();
                rsCount = hs.getRow();
               // System.out.println("rsCount--"+rsCount);
                if (rsCount > 0) {
                    hs.first();
                    return hs;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public static ResultSet gyr(Statement sD,String bizid) throws SQLException {
        if (bizid!=null && !bizid.equals("")) {
            ResultSet hs = sD.executeQuery("select hc.id as hcid,hc.bizid,hc.type as hcType,hc.no as hcno,hc.ownerid,hc.perc,hc.poolarea,hc.relation,"
                    +" ho.name,ho.idType,ho.idno,ho.address,ho.phone"
                    +" from DGHouseInfo..housecard as hc left join DGHouseInfo..OwnerInfo as ho"
                    +" on hc.ownerid=ho.id where hc.type = 77 and ho.id is not null and bizid = '"+bizid+"'");
            hs.last();
            int rsCount = hs.getRow();
            if (rsCount > 0) {
                hs.beforeFirst();
                return hs;
            }else{
                return null;
            }
        }
        return null;
    }

    public static ResultSet skgyr(Statement sD,String Nameid) throws SQLException {
        if (Nameid!=null && !Nameid.equals("")) {
            ResultSet hs = sD.executeQuery("SELECT SHKProcesses.Id AS Pro_Id, SHKProcesses.Name,"
                    +" SHKProcesses.State, SHKProcesses.PDefName,"
                    +" SHKProcessData.VariableValueVCHAR, SHKProcessData.oid"
                    +" FROM SHKProcesses INNER JOIN dbo.SHKProcessData ON SHKProcesses.oid = SHKProcessData.Process"
                    +" WHERE (SHKProcessData.VariableDefinitionId = N'owners_no') AND"
                    +" (SHKProcessData.VariableValueVCHAR <> '') and SHKProcesses.Id ='"+Nameid+"'");
            hs.last();
            int rsCount = hs.getRow();
            if (rsCount > 0) {
                hs.first();
                return hs;
            }else{
                return null;
            }
        }
        return null;
    }


}
