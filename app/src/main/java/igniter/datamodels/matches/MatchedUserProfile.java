package igniter.datamodels.matches;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.matches
 * @category MatchedUserProfile
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*****************************************************************
 Matched User Profile
 ****************************************************************/
public class MatchedUserProfile {
    @SerializedName("match_user_name")
    @Expose
    private String name;
    @SerializedName("match_user_image_url")
    @Expose
    private String images;
    @SerializedName("match_status")
    @Expose
    private String matchStatus;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("matched_user_id")
    @Expose
    private String matchedUserId;
    @SerializedName("match_id")
    @Expose
    private Integer matchId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public String getMatchedUserId() {
        return matchedUserId;
    }

    public void setMatchedUserId(String matchedUserId) {
        this.matchedUserId = matchedUserId;
    }
}
