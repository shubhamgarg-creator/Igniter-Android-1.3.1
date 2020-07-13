package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category PlusSettingsModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 Plan Plus Setting Model
 ****************************************************************/
public class PlusSettingsModel {

    @SerializedName("status_message")
    @Expose
    private String message;
    @SerializedName("status_code")
    @Expose
    private String code;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("unlimited_likes")
    @Expose
    private String unLimitedLikes;
    @SerializedName("boost_plan")
    @Expose
    private String boostPlan;
    @SerializedName("show_my_age")
    @Expose
    private String showMyAge;
    @SerializedName("show_my_distance")
    @Expose
    private String showMyDistance;
    @SerializedName("extra_super_likes")
    @Expose
    private String extraSuperLikes;
    @SerializedName("rewind")
    @Expose
    private String rewind;
    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("is_order")
    @Expose
    private String isOrder;
    @SerializedName("search_location")
    @Expose
    private String searchLocation;
    @SerializedName("location")
    @Expose
    private ArrayList<LocationModel> locationModels = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ArrayList<LocationModel> getLocationModels() {
        return locationModels;
    }

    public void setLocationModels(ArrayList<LocationModel> locationModels) {
        this.locationModels = locationModels;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(String isOrder) {
        this.isOrder = isOrder;
    }

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public String getRewind() {
        return rewind;
    }

    public void setRewind(String rewind) {
        this.rewind = rewind;
    }

    public String getBoostPlan() {
        return boostPlan;
    }

    public void setBoostPlan(String boostPlan) {
        this.boostPlan = boostPlan;
    }

    public String getExtraSuperLikes() {
        return extraSuperLikes;
    }

    public void setExtraSuperLikes(String extraSuperLikes) {
        this.extraSuperLikes = extraSuperLikes;
    }

    public String getUnLimitedLikes() {
        return unLimitedLikes;
    }

    public void setUnLimitedLikes(String unLimitedLikes) {
        this.unLimitedLikes = unLimitedLikes;
    }

    public String getShowMyAge() {
        return showMyAge;
    }

    public void setShowMyAge(String showMyAge) {
        this.showMyAge = showMyAge;
    }

    public String getShowMyDistance() {
        return showMyDistance;
    }

    public void setShowMyDistance(String showMyDistance) {
        this.showMyDistance = showMyDistance;
    }
}
