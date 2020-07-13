package igniter.datamodels.matches;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.matches
 * @category MatchesResponse
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/*****************************************************************
 Matches Response
 ****************************************************************/
public class MatchesResponse {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("matching_profile")
    @Expose
    private List<MatchingProfile> matchingProfile = null;

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

    public List<MatchingProfile> getMatchingProfile() {
        return matchingProfile;
    }

    public void setMatchingProfile(List<MatchingProfile> matchingProfile) {
        this.matchingProfile = matchingProfile;
    }
}
