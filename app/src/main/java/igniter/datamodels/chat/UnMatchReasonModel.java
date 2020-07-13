package igniter.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category UnMatchReasonModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 UnMatchReasonModel
 ****************************************************************/
public class UnMatchReasonModel {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;

    @SerializedName("reasons")
    @Expose
    private ArrayList<ReasonModel> reasons = new ArrayList<>();

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public ArrayList<ReasonModel> getResonModels() {
        return reasons;
    }

    public void setReasonModel(ArrayList<ReasonModel> reasons) {
        this.reasons = reasons;
    }

}
