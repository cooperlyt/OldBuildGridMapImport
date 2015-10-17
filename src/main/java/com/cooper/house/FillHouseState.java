package com.cooper.house;

import java.util.List;

/**
 * Created by wxy on 2015-10-16.
 */

public class FillHouseState {


    public static HouseState fillHouseState(String nameid){




        //备案
        String contractsRecord="WP42,";
        //备案解除
        String moveContractsRecord="WP43,";

        //预购商品房预告登记
        String saleRegister="WP44,";
        //预购商品房预告登记解除
        String moveSaleRegister="WP46,";

        //房屋转移预告登记
        String divertSaleRegister="WP69,";

        //房屋转移预告登记解除
        String moveDivertSaleRegister="WP70,";

        //预购商品房抵押
        String saleMortgageRegister="WP1,";

       //预购商品房抵押解除
        String moveSaleMortgageRegister="WP4,";

        //屋屋转移预告抵押
        String divertMortgageSaleRegister="WP5,";

        //屋屋转移预告抵押解除
        String moveDivertMortgageSaleRegister="WP8,";

        //已办产权
        String ownered = "WP33,WP35,WP52,WP102,WP53,WP91,WP54,WP55,WP30,WP31,WP41,WP56,WP57,WP58,WP59,WP60,WP61,WP62,WP63,WP64,WP65,WP66,WP67,WP68,WP71,WP72,WP75,WP90,WP86,WP87,";

        //抵押
        String pledge="WP9,WP13,";

        //抵押解除
        String movePledge="WP12,WP17,";

        //在建工程抵押
        String projectPledge="WP83,";

        //在建工程抵押解除
        String moveProjectPledge="WP84,";

        //异议登记
        String difficulty="WP36,WP48,WP23,WP28,";

        //异议登记解除
        String moveDifficulty="WP37,WP49,WP24,WP29,";

        //声明作废
        String declareCancel="WP34,";

        //声明作废解除
        String moveDeclareCancel="WP39,";

        //查封
        String courtClose="WP73,";

        //查封解除
        String moveCourtClose="WP74,";

        //房屋灭籍
        String destory ="WP38,";

        //商品房初始
        String initReg="WP40,";

        if (nameid!=null && !nameid.trim().equals("")){
            HouseState houseState =new HouseState();
            if (contractsRecord.indexOf(nameid)>=0){
                houseState.setState("CONTRACTS_RECORD");
                houseState.setEnable(true);
            }else if (moveContractsRecord.indexOf(nameid)>=0){
                houseState.setState("CONTRACTS_RECORD");
                houseState.setEnable(false);
            }else if (saleRegister.indexOf(nameid)>=0){
                houseState.setState("SALE_REGISTER");
                houseState.setEnable(true);
            }else if (moveSaleRegister.indexOf(nameid)>=0){
                houseState.setState("SALE_REGISTER");
                houseState.setEnable(false);
            }else if (divertSaleRegister.indexOf(nameid)>=0){
                houseState.setState("DIVERT_REGISTER");
                houseState.setEnable(true);
            }else if (moveDivertSaleRegister.indexOf(nameid)>=0){
                houseState.setState("DIVERT_REGISTER");
                houseState.setEnable(false);
            }else if (saleMortgageRegister.indexOf(nameid)>=0){
                houseState.setState("SALE_MORTGAGE_REGISTER");
                houseState.setEnable(true);
            }else if (moveSaleMortgageRegister.indexOf(nameid)>=0){
                houseState.setState("SALE_MORTGAGE_REGISTER");
                houseState.setEnable(false);
            }else if(divertMortgageSaleRegister.indexOf(nameid)>=0){
                houseState.setState("DIVERT_MORTGAGE_REGISTER");
                houseState.setEnable(true);
            }else if(moveDivertMortgageSaleRegister.indexOf(nameid)>=0){
                houseState.setState("DIVERT_MORTGAGE_REGISTER");
                houseState.setEnable(false);
            }else if (ownered.indexOf(nameid)>=0){
                houseState.setState("OWNERED");
                houseState.setEnable(true);
            }else if (pledge.indexOf(nameid)>=0){
                houseState.setState("PLEDGE");
                houseState.setEnable(true);
            }else if (movePledge.indexOf(nameid)>=0){
                houseState.setState("PLEDGE");
                houseState.setEnable(false);
            }else if (projectPledge.indexOf(nameid)>=0){
                houseState.setState("PROJECT_PLEDGE");
                houseState.setEnable(true);
            }else if (moveProjectPledge.indexOf(nameid)>=0){
                houseState.setState("PROJECT_PLEDGE");
                houseState.setEnable(false);
            }else if (difficulty.indexOf(nameid)>=0){
                houseState.setState("DIFFICULTY");
                houseState.setEnable(true);
            }else if (moveDifficulty.indexOf(nameid)>=0){
                houseState.setState("DIFFICULTY");
                houseState.setEnable(false);
            }else if (declareCancel.indexOf(nameid)>=0){
                houseState.setState("DECLARE_CANCEL");
                houseState.setEnable(true);
            }else if (moveDeclareCancel.indexOf(nameid)>=0){
                houseState.setState("DECLARE_CANCEL");
                houseState.setEnable(false);
            }else if(courtClose.indexOf(nameid)>=0){
                houseState.setState("COURT_CLOSE");
                houseState.setEnable(true);
            }else if(moveCourtClose.indexOf(nameid)>=0){
                houseState.setState("COURT_CLOSE");
                houseState.setEnable(false);
            }else if(destory.indexOf(nameid)>=0){
                houseState.setState("DESTROY");
                houseState.setEnable(true);
            }else if(initReg.indexOf(nameid)>=0){
                houseState.setState("INIT_REG");
                houseState.setEnable(true);
            }else{
                return null;
            }

            return houseState;
        }
        return null;
    }



}
