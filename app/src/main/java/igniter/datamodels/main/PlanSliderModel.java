package igniter.datamodels.main;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.main
 * @category PlanSliderModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/*****************************************************************
 Plan Slider Model
 ****************************************************************/
public class PlanSliderModel {

    @SerializedName("status_message")
    @Expose
    private String message;
    @SerializedName("status_code")
    @Expose
    private String code;
    @SerializedName("plan_images")
    @Expose
    private ArrayList<ImageListModel> imageList = new ArrayList<>();
    @SerializedName("plan_detail")
    @Expose
    private ArrayList<PlanListModel> planList = new ArrayList<>();


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

    public ArrayList<ImageListModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageListModel> imageList) {
        this.imageList = imageList;
    }

    public ArrayList<PlanListModel> getPlanList() {
        return planList;
    }

    public void setPlanList(ArrayList<PlanListModel> planList) {
        this.planList = planList;
    }

}
