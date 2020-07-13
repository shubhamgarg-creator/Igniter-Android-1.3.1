package igniter.adapters.infowindow;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.infowindow
 * @category InfoWindowModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

/**************************************************************
 Info Window for add location model
 ****************************************************************/
public class InfoWindowModel {
    private String address;
    private int type;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
