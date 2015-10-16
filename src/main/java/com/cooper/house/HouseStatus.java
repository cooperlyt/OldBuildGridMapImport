package com.cooper.house;

import java.util.Comparator;

/**
 * Created by cooper on 9/4/15.
 */
public enum HouseStatus {
    //-- 不可售 CANTSALE 签约 CONTRACTS
    // 备案，已办产权，商品房预告登记，
    // 房屋转移预告登记，商品房预告抵押，屋屋转移预告抵押,
    // 抵押,在建工程抵押,异议,声明作废，查封,灭籍
    // 确权,初始登记
    CONTRACTS_RECORD(12,false),OWNERED(11,false),SALE_REGISTER(10,false),
    DIVERT_REGISTER(9,false),SALE_MORTGAGE_REGISTER(8,false),DIVERT_MORTGAGE_REGISTER(7,false),
    PLEDGE(5,true),PROJECT_PLEDGE(6,true),DIFFICULTY(4,false),DECLARE_CANCEL(3,false),COURT_CLOSE(2,true),DESTROY(1,false),
    INIT_REG_CONFIRM(14,false),INIT_REG(13,false);

    private int pri;
    private boolean allowRepeat;

    private HouseStatus(int pri, boolean allowRepeat){
        this.pri = pri;
        this.allowRepeat = allowRepeat;
    }

    public int getPri() {
        return pri;
    }

    public boolean isAllowRepeat() {
        return allowRepeat;
    }


    public static class StatusComparator implements Comparator<HouseStatus> {

        private static StatusComparator instance;

        public static StatusComparator getInstance(){
            if (instance == null){
                instance = new StatusComparator();
            }
            return instance;
        }


        public int compare(HouseStatus o1, HouseStatus o2) {
            return  Integer.valueOf(o1.getPri()).compareTo(o2.getPri());
        }
    }
}
