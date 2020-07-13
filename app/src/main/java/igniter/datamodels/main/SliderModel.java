package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category SliderModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 Slider Model
 ****************************************************************/
public class SliderModel {

    @SerializedName("status_message")
    @Expose
    private String message;
    @SerializedName("status_code")
    @Expose
    private String code;
    @SerializedName("minimum_age")
    @Expose
    private String minimumAge;
    @SerializedName("maximum_age")
    @Expose
    private String maximumAge;
    @SerializedName("login_sliders")
    @Expose
    private ArrayList<ImageListModel> imageList = new ArrayList<>();

    @SerializedName("igniter_plus_sliders")
    @Expose
    private ArrayList<ImageListModel> igniterPlusImgList = new ArrayList<>();
    @SerializedName("apple_client_id")
    @Expose
    private String clientId;

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

    public String getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(String minimumAge) {
        this.minimumAge = minimumAge;
    }

    public String getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(String maximumAge) {
        this.maximumAge = maximumAge;
    }

    public ArrayList<ImageListModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageListModel> imageList) {
        this.imageList = imageList;
    }

    public ArrayList<ImageListModel> getIgniterPlusImgList() {
        return igniterPlusImgList;
    }

    public void setIgniterPlusImgList(ArrayList<ImageListModel> igniterPlusImgList) {
        this.igniterPlusImgList = igniterPlusImgList;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
