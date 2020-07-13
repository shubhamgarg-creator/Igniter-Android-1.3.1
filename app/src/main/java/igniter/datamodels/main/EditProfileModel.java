package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category EditProfileModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 Edit Profile Model
 ****************************************************************/
public class EditProfileModel {

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
    @SerializedName("image_url")
    @Expose
    private ArrayList<ImageModel> imageList = new ArrayList<>(5);
    @SerializedName("about")
    @Expose
    private String about;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("college")
    @Expose
    private String college;
    @SerializedName("instagram_id")
    @Expose
    private String instagramId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("show_my_age")
    @Expose
    private String showMyAge;
    @SerializedName("distance_invisible")
    @Expose
    private String distanceInvisible;
    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("is_order")
    @Expose
    private String isOrder;
    @SerializedName("image_id")
    @Expose
    private String image_id;


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

    public ArrayList<ImageModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageModel> imageList) {
        this.imageList = imageList;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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

    public String getInstagramId() {
        return instagramId;
    }

    public void setInstagramId(String instagramId) {
        this.instagramId = instagramId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getShowMyAge() {
        return showMyAge;
    }

    public void setShowMyAge(String showMyAge) {
        this.showMyAge = showMyAge;
    }

    public String getDistanceInvisible() {
        return distanceInvisible;
    }

    public void setDistanceInvisible(String distanceInvisible) {
        this.distanceInvisible = distanceInvisible;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
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
}
