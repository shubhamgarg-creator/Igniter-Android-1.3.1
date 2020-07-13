package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category Plan List Model
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*****************************************************************
 Plan List Model
 ****************************************************************/
public class PlanListModel {

    @SerializedName("plan_id")
    @Expose
    private int planId;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("month")
    @Expose
    private String count;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("save")
    @Expose
    private String save;
    @SerializedName("boost_minitues")
    @Expose
    private String boostMinitues;

    public String getSave() {
        return save;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBoostMinitues() {
        return boostMinitues;
    }

    public void setBoostMinitues(String boostMinitues) {
        this.boostMinitues = boostMinitues;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }
}
