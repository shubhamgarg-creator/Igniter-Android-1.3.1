package igniter.configs;
/**
 * @package com.trioangle.igniter
 * @subpackage configs
 * @category SessionManager
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.SharedPreferences;

import javax.inject.Inject;

/*****************************************************************
 Session manager to set and get glopal values
 ***************************************************************/
public class SessionManager {
    @Inject
    SharedPreferences sharedPreferences;

    public SessionManager() {
        AppController.getAppComponent().inject(this);
    }

    public String getToken() {
        return sharedPreferences.getString("token", "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString("token", token).apply();
    }

    public String getProfileImg() {
        return sharedPreferences.getString("imageUrl", "");
    }

    public void setProfileImg(String imageUrl) {
        sharedPreferences.edit().putString("imageUrl", imageUrl).apply();
    }

    public String getPhoneNumber() {
        return sharedPreferences.getString("phoneNumber", "");
    }

    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences.edit().putString("phoneNumber", phoneNumber).apply();
    }

    public boolean getIsSawLike() {
        return sharedPreferences.getBoolean("isSawLike", true);
    }

    public void setIsSawLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSawLike", isSaw).apply();
    }

    public boolean getIsSawUnLike() {
        return sharedPreferences.getBoolean("isSawUnLike", true);
    }

    public void setIsSawUnLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSawUnLike", isSaw).apply();
    }

    public boolean getIsSawSuperLike() {
        return sharedPreferences.getBoolean("isSawSuperLike", true);
    }

    public void setIsSawSuperLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSawSuperLike", isSaw).apply();
    }

    public boolean getIsSwipeLike() {
        return sharedPreferences.getBoolean("isSwipeLike", true);
    }

    public void setIsSwipeLike(Boolean isSaw) {
        sharedPreferences.edit().putBoolean("isSwipeLike", isSaw).apply();
    }

    public boolean getIsSwipeUnLike() {
        return sharedPreferences.getBoolean("isSwipeUnLike", true);
    }

    public void setIsSwipeUnLike(Boolean isSwipe) {
        sharedPreferences.edit().putBoolean("isSwipeUnLike", isSwipe).apply();
    }

    public boolean getIsSwipeSuperLike() {
        return sharedPreferences.getBoolean("isSwipeSuperLike", true);
    }

    public void setIsSwipeSuperLike(Boolean isSwipe) {
        sharedPreferences.edit().putBoolean("isSwipeSuperLike", isSwipe).apply();
    }

    public String getCountryCode() {
        return sharedPreferences.getString("countryCode", "");
    }

    public void setCountryCode(String countryCode) {
        sharedPreferences.edit().putString("countryCode", countryCode).apply();
    }

    public boolean getIsFromLikedPage() {
        return sharedPreferences.getBoolean("isFromLikePage", false);
    }

    public void setIsFromLikedPage(boolean isFromLikedPage) {
        sharedPreferences.edit().putBoolean("isFromLikePage", isFromLikedPage).apply();
    }

    public boolean getReloadLikedPage() {
        return sharedPreferences.getBoolean("isReloadLikedPage", false);
    }

    public void setReloadLikedPage(boolean isReloadLikedPage) {
        sharedPreferences.edit().putBoolean("isReloadLikedPage", isReloadLikedPage).apply();
    }

    public int getDeviceWidth() {
        return sharedPreferences.getInt("deviceWidth", 0);
    }

    public void setDeviceWidth(int width) {
        sharedPreferences.edit().putInt("deviceWidth", width).apply();
    }

    public void clearToken() {
        sharedPreferences.edit().putString("token", "").apply();
    }

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt("userId", 0);
    }

    public void setUserId(int userId) {
        sharedPreferences.edit().putInt("userId", userId).apply();
    }

    public String getSocialProfile() {
        return sharedPreferences.getString("SocialProfile", "");
    }

    public void setSocialProfile(String SocialProfile) {
        sharedPreferences.edit().putString("SocialProfile", SocialProfile).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString("userName", "");
    }

    public void setUserName(String userName) {
        sharedPreferences.edit().putString("userName", userName).apply();
    }

    public String getSwipeType() {
        return sharedPreferences.getString("swipeType", "");
    }

    public void setSwipeType(String swipeType) {
        sharedPreferences.edit().putString("swipeType", swipeType).apply();
    }

    public boolean isReported() {
        return sharedPreferences.getBoolean("swipeReason", false);
    }

    public void setReported(boolean swipeReason) {
        sharedPreferences.edit().putBoolean("swipeReason", swipeReason).apply();
    }

    public boolean getSettingUpdate() {
        return sharedPreferences.getBoolean("settingUpdate", false);
    }

    public void setSettingUpdate(boolean settingUpdate) {
        sharedPreferences.edit().putBoolean("settingUpdate", settingUpdate).apply();
    }

    public int getTouchX() {
        return sharedPreferences.getInt("touchX", 0);
    }

    public void setTouchX(int touchX) {
        sharedPreferences.edit().putInt("touchX", touchX).apply();
    }

    public int getTouchY() {
        return sharedPreferences.getInt("touchY", 0);
    }

    public void setTouchY(int touchY) {
        sharedPreferences.edit().putInt("touchY", touchY).apply();
    }

    public String getDeviceId() {
        return sharedPreferences.getString("deviceId", "");
    }

    public void setDeviceId(String deviceId) {
        sharedPreferences.edit().putString("deviceId", deviceId).apply();
    }

    public int getImageid() {
        return sharedPreferences.getInt("image_id", 0);
    }

    public void setImageid(int imageid) {
        sharedPreferences.edit().putInt("image_id", imageid).apply();
    }

    public Integer getUnMatchReasonId() {
        return sharedPreferences.getInt("unMatchReasonId", 0);
    }

    public void setUnMatchReasonId(Integer unMatchReasonId) {
        sharedPreferences.edit().putInt("unMatchReasonId", unMatchReasonId).apply();
    }

    public boolean getIsUnMatchReasonReq() {
        return sharedPreferences.getBoolean("IsUnMatchReasonReq", false);
    }

    public void setIsUnMatchReasonReq(boolean unMatchReasonId) {
        sharedPreferences.edit().putBoolean("IsUnMatchReasonReq", unMatchReasonId).apply();
    }

    public boolean getRefreshChatFragment() {
        return sharedPreferences.getBoolean("isRefreshChatFragment", false);
    }

    public void setRefreshChatFragment(Boolean isRefreshChatFragment) {
        sharedPreferences.edit().putBoolean("isRefreshChatFragment", isRefreshChatFragment).apply();
    }

    public String getPushNotification() {
        return sharedPreferences.getString("pushNotification", "");
    }

    public void setPushNotification(String pushNotification) {
        sharedPreferences.edit().putString("pushNotification", pushNotification).apply();
    }

    public boolean getIsOrder() {
        return sharedPreferences.getBoolean("isOrder", false);
    }

    public void setIsOrder(Boolean isOrder) {
        sharedPreferences.edit().putBoolean("isOrder", isOrder).apply();
    }

    public String getPlanType() {
        return sharedPreferences.getString("planType", "");
    }

    public void setPlanType(String planType) {
        sharedPreferences.edit().putString("planType", planType).apply();
    }

    public int getRemainingSuperLikes() {
        return sharedPreferences.getInt("remaningsuperlikes", 0);
    }

    public void setRemainingSuperLikes(int remainingSuperLikes) {
        sharedPreferences.edit().putInt("remaningsuperlikes", remainingSuperLikes).apply();
    }

    public int getRemainingLikes() {
        return sharedPreferences.getInt("remaninglikes", 20);
    }

    public void setRemainingLikes(int remainingLikes) {
        sharedPreferences.edit().putInt("remaninglikes", remainingLikes).apply();
    }

    public String getIsRemainingLikeLimited() {
        return sharedPreferences.getString("remaninglikeslimited", "");
    }

    public void setIsRemainingLikeLimited(String remainingLikeslimited) {
        sharedPreferences.edit().putString("remaninglikeslimited", remainingLikeslimited).apply();
    }

    public int getRemainingBoost() {
        return sharedPreferences.getInt("remainingBoost", 0);
    }

    public void setRemainingBoost(int remainingBoost) {
        sharedPreferences.edit().putInt("remainingBoost", remainingBoost).apply();
    }

    public boolean getIsFBUser() {
        return sharedPreferences.getBoolean("isFbUser", false);
    }

    public void setIsFBUser(boolean isFbUser) {
        sharedPreferences.edit().putBoolean("isFbUser", isFbUser).apply();
    }

    public boolean getIsAppleUser() {
        return sharedPreferences.getBoolean("IsAppleUser", false);
    }

    public void setIsAppleUser(boolean IsAppleUser) {
        sharedPreferences.edit().putBoolean("IsAppleUser", IsAppleUser).apply();
    }

    public boolean isPurchased() {
        return sharedPreferences.getBoolean("IsPurchased", false);
    }

    public void setPurchased(boolean isPurchased) {
        sharedPreferences.edit().putBoolean("IsPurchased", isPurchased).apply();
    }

    public String getFbId() {
        return sharedPreferences.getString("FbId", "");
    }

    public void setFbId(String FbId) {
        sharedPreferences.edit().putString("FbId", FbId).apply();
    }

    public String getappleId() {
        return sharedPreferences.getString("appleId", "");
    }

    public void setappleId(String appleId) {
        sharedPreferences.edit().putString("appleId", appleId).apply();
    }

    public String getSocialMail() {
        return sharedPreferences.getString("SocialMail", "");
    }

    public void setSocialMail(String SocialMail) {
        sharedPreferences.edit().putString("SocialMail", SocialMail).apply();
    }

    public String getMinAge() {
        return sharedPreferences.getString("minAge", "18");
    }

    public void setMinAge(String minAge) {
        sharedPreferences.edit().putString("minAge", minAge).apply();
    }

    public String getMaxAge() {
        return sharedPreferences.getString("maxAge", "100");
    }

    public void setMaxAge(String maxAge) {
        sharedPreferences.edit().putString("maxAge", maxAge).apply();
    }

    public String getMatched() {
        return sharedPreferences.getString("Matched", "0");
    }

    public void setMatched(String Matched) {
        sharedPreferences.edit().putString("Matched", Matched).apply();
    }

}
