package igniter.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category MessageModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*****************************************************************
 Message Model
 ****************************************************************/
public class MessageModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sender_id")
    @Expose
    private Integer senderId;
    @SerializedName("like_status")
    @Expose
    private String likeStatus;
    @SerializedName("message_id")
    @Expose
    private Integer messageId;
    @SerializedName("sender_image_url")
    @Expose
    private String senderImageUrl;
    @SerializedName("first_message")
    @Expose
    private String firstMessage;
    @SerializedName("received_date_time")
    @Expose
    private ReceiveDateModel receivedDate;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getSenderImageUrl() {
        return senderImageUrl;
    }

    public void setSenderImageUrl(String senderImageUrl) {
        this.senderImageUrl = senderImageUrl;
    }

    public String getIsFirstMessage() {
        return firstMessage;
    }

    public void setIsFirstMessage(String firstMessage) {
        this.firstMessage = firstMessage;
    }


    public ReceiveDateModel getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(ReceiveDateModel receivedDate) {
        this.receivedDate = receivedDate;
    }
}
