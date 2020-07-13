package igniter.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category NewMatchProfileModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*****************************************************************
 NewMatchProfileModel
 ****************************************************************/
public class NewMatchProfileModel {

    @SerializedName("user_image_url")
    @Expose
    private String userImgUrl;
    @SerializedName("like_status")
    @Expose
    private String likeStatus;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("match_id")
    @Expose
    private Integer matchId;
    @SerializedName("read_status")
    @Expose
    private String readStatus;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("message")
    @Expose
    private String lastMessage;
    @SerializedName("is_reply")
    @Expose
    private String isReply;

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

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getIsReply() {
        return isReply;
    }

    public void setIsReply(String isReply) {
        this.isReply = isReply;
    }
}
