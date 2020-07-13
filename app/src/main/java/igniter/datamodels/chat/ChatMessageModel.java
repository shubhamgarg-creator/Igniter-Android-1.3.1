package igniter.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category ChatMessageModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 Chat Message Model
 ****************************************************************/
public class ChatMessageModel {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("liked_image_url")
    @Expose
    private String likedImageUrl;
    @SerializedName("user_image_url")
    @Expose
    private String userImgUrl;
    @SerializedName("liked_username")
    @Expose
    private String likedUsername;
    @SerializedName("like_status")
    @Expose
    private String likeStatus;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("liked_id")
    @Expose
    private Integer likedId;
    @SerializedName("match_id")
    @Expose
    private Integer matchId;
    @SerializedName("messages")
    @Expose
    private ArrayList<MessageModel> messageModels = new ArrayList<>();

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

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public ArrayList<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public String getLikedImageUrl() {
        return likedImageUrl;
    }

    public void setLikedImageUrl(String likedImageUrl) {
        this.likedImageUrl = likedImageUrl;
    }

    public String getLikedUsername() {
        return likedUsername;
    }

    public void setLikedUsername(String likedUsername) {
        this.likedUsername = likedUsername;
    }

    public Integer getLikedId() {
        return likedId;
    }

    public void setLikedId(Integer likedId) {
        this.likedId = likedId;
    }
}
