package igniter.datamodels.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage datamodels.chat
 * @category UserModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

/*****************************************************************
 UserModel
 ****************************************************************/
public class UserModel {

    private String imagePath;
    private String UserName;
    private Boolean isChecked;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
