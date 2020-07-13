package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category MatchProfilesModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import igniter.datamodels.matches.MatchingProfile;

/*****************************************************************
 Match Profiles Model
 ****************************************************************/
public class MatchProfilesModel {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("is_order")
    @Expose
    private String isOrder;
    @SerializedName("tot_super_likes_per_day")
    @Expose
    private int totalSuperLikes;
    @SerializedName("remaining_slikes_count")
    @Expose
    private int remainingSuperLikes;
    @SerializedName("remaining_likes_hours")
    @Expose
    private String remainingSuperLikeHours;
    @SerializedName("tot_boost_count_per_month")
    @Expose
    private int totalBoost;
    @SerializedName("remaining_boost_count")
    @Expose
    private int remainingBoost;
    @SerializedName("remaining_boost_days")
    @Expose
    private String remainingBoostDay;
    @SerializedName("remaining_boost_hours")
    @Expose
    private String remainingBoostHours;
    @SerializedName("unread_count")
    @Expose
    private Integer unReadCount;
    @SerializedName("remaining_likes_count")
    @Expose
    private Integer remainingLike;
    @SerializedName("is_likes_limited")
    @Expose
    private String isLikeLimited;
    @SerializedName("matching_profile")
    @Expose
    private ArrayList<MatchingProfile> matchingProfile = new ArrayList<>();

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

    public ArrayList<MatchingProfile> getMatchingProfile() {
        return matchingProfile;
    }

    public void setMatchingProfile(ArrayList<MatchingProfile> matchingProfile) {
        this.matchingProfile = matchingProfile;
    }

    public int getTotalSuperLikes() {
        return totalSuperLikes;
    }

    public void setTotalSuperLikes(int totalSuperLikes) {
        this.totalSuperLikes = totalSuperLikes;
    }

    public int getRemainingSuperLikes() {
        return remainingSuperLikes;
    }

    public void setRemainingSuperLikes(int remainingSuperLikes) {
        this.remainingSuperLikes = remainingSuperLikes;
    }

    public String getRemainingSuperLikeHours() {
        return remainingSuperLikeHours;
    }

    public void setRemainingSuperLikeHours(String remainingSuperLikeHours) {
        this.remainingSuperLikeHours = remainingSuperLikeHours;
    }

    public int getTotalBoost() {
        return totalBoost;
    }

    public void setTotalBoost(int totalBoost) {
        this.totalBoost = totalBoost;
    }

    public int getRemainingBoost() {
        return remainingBoost;
    }

    public void setRemainingBoost(int remainingBoost) {
        this.remainingBoost = remainingBoost;
    }

    public String getRemainingBoostHours() {
        return remainingBoostHours;
    }

    public void setRemainingBoostHours(String remainingBoostHours) {
        this.remainingBoostHours = remainingBoostHours;
    }

    public String getRemainingBoostDay() {
        return remainingBoostDay;
    }

    public void setRemainingBoostDay(String remainingBoostDay) {
        this.remainingBoostDay = remainingBoostDay;
    }

    public Integer getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(Integer unReadCount) {
        this.unReadCount = unReadCount;
    }

    public Integer getRemainingLike() {
        return remainingLike;
    }

    public void setRemainingLike(Integer remainingLike) {
        this.remainingLike = remainingLike;
    }

    public String getIsLikeLimited() {
        return isLikeLimited;
    }

    public void setIsLikeLimited(String isLikeLimited) {
        this.isLikeLimited = isLikeLimited;
    }
}
