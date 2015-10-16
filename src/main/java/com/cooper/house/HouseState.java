package com.cooper.house;

/**
 * Created by wxy on 2015-10-16.
 */
public class HouseState {

    public HouseState(){

    }

    private String state;

    private boolean isEnable;


    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


}
