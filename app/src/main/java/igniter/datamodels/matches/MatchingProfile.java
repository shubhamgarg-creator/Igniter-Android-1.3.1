package igniter.datamodels.matches;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.matches
 * @category Matching Profile
 * @author Trioangle Product Team
 * @version 1.0
 **/

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*****************************************************************
 Matching Profile
 ****************************************************************/
public class MatchingProfile {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("work")
    @Expose
    private String work;
    @SerializedName("college")
    @Expose
    private String college;
    @SerializedName("super_like")
    @Expose
    private String superLike;
    @SerializedName("kilometer")
    @Expose
    private String kilometer;
    @SerializedName("images")
    @Expose
    private String images;
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getKilometer() {
        return kilometer;
    }

    public void setKilometer(String kilometer) {
        this.kilometer = kilometer;
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

    public String getSuperLike() {
        return superLike;
    }

    public void setSuperLike(String superLike) {
        this.superLike = superLike;
    }
}
