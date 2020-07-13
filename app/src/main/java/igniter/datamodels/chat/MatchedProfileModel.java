package igniter.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category MatchedProfileModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 Matched Profile Model
 ****************************************************************/
public class MatchedProfileModel {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("likes_count")
    @Expose
    private Integer likesCount;
    @SerializedName("new_match_count")
    @Expose
    private Integer newMatchCount;
    @SerializedName("unread_count")
    @Expose
    private Integer unReadCount;
    @SerializedName("gold_likes_status")
    @Expose
    private String goldLikeStatus;
    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("is_order")
    @Expose
    private String isOrder;
    @SerializedName("has_gold_plan")
    @Expose
    private Boolean hasGoldPlan = false;
    @SerializedName("conversation_not_started_array")
    @Expose
    private ArrayList<NewMatchProfileModel> newMatchProfile = new ArrayList<>();
    @SerializedName("conversation_started_array")
    @Expose
    private ArrayList<NewMatchProfileModel> message = new ArrayList<>();

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public Integer getNewMatchCount() {
        return newMatchCount;
    }

    public void setNewMatchCount(Integer newMatchCount) {
        this.newMatchCount = newMatchCount;
    }

    public Integer getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(Integer unReadCount) {
        this.unReadCount = unReadCount;
    }


    public String getGoldLikeStatus() {
        return goldLikeStatus;
    }

    public void setGoldLikeStatus(String goldLikeStatus) {
        this.goldLikeStatus = goldLikeStatus;
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

    public ArrayList<NewMatchProfileModel> getNewMatchProfile() {
        return newMatchProfile;
    }

    public void setNewMatchProfile(ArrayList<NewMatchProfileModel> newMatchProfile) {
        this.newMatchProfile = newMatchProfile;
    }

    public ArrayList<NewMatchProfileModel> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<NewMatchProfileModel> message) {
        this.message = message;
    }

    public Boolean getHasGoldPlan() {
        return hasGoldPlan;
    }

    public void setHasGoldPlan(Boolean hasGoldPlan) {
        this.hasGoldPlan = hasGoldPlan;
    }
}
