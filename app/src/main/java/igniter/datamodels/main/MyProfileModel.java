package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category MyProfileModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 User Profile Model
 ****************************************************************/
public class MyProfileModel {

    @SerializedName("status_message")
    @Expose
    private String message;
    @SerializedName("status_code")
    @Expose
    private String code;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("images")
    @Expose
    private ArrayList<String> images = new ArrayList<>();
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("college")
    @Expose
    private String college;
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("job_title")//kilometer
    @Expose
    private String jobTitle;
    @SerializedName("kilometer")
    @Expose
    private String kilometer;
    @SerializedName("distance_type")
    @Expose
    private String distanceType;
    @SerializedName("matching_profile")
    @Expose
    private String matchingProfile;
    @SerializedName("is_order")
    @Expose
    private String isOrder;
    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("is_super_liked")
    @Expose
    private boolean isSuperLiked;
    @SerializedName("remaining_slikes_count")
    @Expose
    private String remainingLikesCount;
    @SerializedName("remaining_likes_count")
    @Expose
    private String remainingLike;
    @SerializedName("is_likes_limited")
    @Expose
    private String isLikesLimited;

    @SerializedName("remaining_boost_count")
    @Expose
    private String remainingBoostCount;
    @SerializedName("search_location")
    @Expose
    private String searchLocation;
    @SerializedName("unread_count")
    @Expose
    private Integer unReadCount;

    public boolean isSuperLiked() {
        return isSuperLiked;
    }

    public void setSuperLiked(boolean superLiked) {
        isSuperLiked = superLiked;
    }

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getKilometer() {
        return kilometer;
    }

    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(String distanceType) {
        this.distanceType = distanceType;
    }

    public String getMatchingProfile() {
        return matchingProfile;
    }

    public void setMatchingProfile(String matchingProfile) {
        this.matchingProfile = matchingProfile;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(String isOrder) {
        this.isOrder = isOrder;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getRemainingLikesCount() {
        return remainingLikesCount;
    }

    public void setRemainingLikesCount(String remainingLikesCount) {
        this.remainingLikesCount = remainingLikesCount;
    }

    public String getRemainingBoostCount() {
        return remainingBoostCount;
    }

    public void setRemainingBoostCount(String remainingBoostCount) {
        this.remainingBoostCount = remainingBoostCount;
    }

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }

    public Integer getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(Integer unReadCount) {
        this.unReadCount = unReadCount;
    }

    public String getRemainingLike() {
        return remainingLike;
    }

    public void setRemainingLike(String remainingLike) {
        this.remainingLike = remainingLike;
    }

    public String getIsLikesLimited() {
        return isLikesLimited;
    }

    public void setIsLikesLimited(String isLikesLimited) {
        this.isLikesLimited = isLikesLimited;
    }
}
