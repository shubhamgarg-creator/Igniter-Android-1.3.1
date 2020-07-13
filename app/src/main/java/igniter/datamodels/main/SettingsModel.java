package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category SettingsModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 Search Setting Model
 ****************************************************************/
public class SettingsModel {

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
    /*@SerializedName("location")
    @Expose
    private String location;*/
    @SerializedName("minimum_age")
    @Expose
    private Integer minimumAge;
    @SerializedName("maximum_age")
    @Expose
    private Integer maximumAge;
    @SerializedName("minimum_distance")
    @Expose
    private Integer minimumDistance;
    @SerializedName("maximum_distance")
    @Expose
    private Integer maximumDistance;
    @SerializedName("max_distance")
    @Expose
    private Integer maxDistance;

    @SerializedName("min_age")
    @Expose
    private Integer minAge;
    @SerializedName("max_age")
    @Expose
    private Integer maxAge;
    @SerializedName("show_me")
    @Expose
    private String showMe;
    @SerializedName("username")
    @Expose
    private String userName;
    @SerializedName("matching_profile")
    @Expose
    private String matchingProfile;
    @SerializedName("new_match")
    @Expose
    private String newMatch;
    @SerializedName("receiving_message")
    @Expose
    private String receivingMessage;
    @SerializedName("message_likes")
    @Expose
    private String messageLikes;
    @SerializedName("super_likes")
    @Expose
    private String superLikes;
    @SerializedName("distance_type")
    @Expose
    private String distanceType;
    @SerializedName("distance_unit")
    @Expose
    private String distanceUnit;
    @SerializedName("profile_url")
    @Expose
    private String profileUrl;
    @SerializedName("help_url")
    @Expose
    private String helpUrl;
    @SerializedName("license_url")
    @Expose
    private String licenseUrl;
    @SerializedName("privacy_policy_url")
    @Expose
    private String privacyPolicyUrl;
    @SerializedName("terms_of_service_url")
    @Expose
    private String termsOfServiceUrl;
    @SerializedName("community_url")
    @Expose
    private String communityUrl;
    @SerializedName("safety_url")
    @Expose
    private String safetyUrl;
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

    /*public String getLocation() {
        return location;
    }*/

    /*public void setLocation(String location) {
        this.location = location;
    }*/

    public String getMatchingProfile() {
        return matchingProfile;
    }

    public void setMatchingProfile(String matchingProfile) {
        this.matchingProfile = matchingProfile;
    }

    public Integer getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Integer maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getShowMe() {
        return showMe;
    }

    public void setShowMe(String showMe) {
        this.showMe = showMe;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewMatch() {
        return newMatch;
    }

    public void setNewMatch(String newMatch) {
        this.newMatch = newMatch;
    }

    public String getReceivingMessage() {
        return receivingMessage;
    }

    public void setReceivingMessage(String receivingMessage) {
        this.receivingMessage = receivingMessage;
    }

    public String getMessageLikes() {
        return messageLikes;
    }

    public void setMessageLikes(String messageLikes) {
        this.messageLikes = messageLikes;
    }

    public String getSuperLikes() {
        return superLikes;
    }

    public void setSuperLikes(String superLikes) {
        this.superLikes = superLikes;
    }

    public String getAdminDistanceType() {
        return distanceUnit;
    }

    public void setAdminDistanceType(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(String distanceType) {
        this.distanceType = distanceType;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getCommunityUrl() {
        return communityUrl;
    }

    public void setCommunityUrl(String communityUrl) {
        this.communityUrl = communityUrl;
    }

    public String getSafetyUrl() {
        return safetyUrl;
    }

    public void setSafetyUrl(String safetyUrl) {
        this.safetyUrl = safetyUrl;
    }

    public ArrayList<LocationModel> getLocationModels() {
        return locationModels;
    }

    public void setLocationModels(ArrayList<LocationModel> locationModels) {
        this.locationModels = locationModels;
    }

    public Integer getMaximumDistance() {
        return maximumDistance;
    }

    public void setMaximumDistance(Integer maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    public Integer getMinimumDistance() {
        return minimumDistance;
    }

    public void setMinimumDistance(Integer minimumDistance) {
        this.minimumDistance = minimumDistance;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
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
}
