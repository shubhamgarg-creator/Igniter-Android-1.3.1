package igniter.datamodels.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NumberValidationModel {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;

    @SerializedName("already_user")
    @Expose
    private String alreadyUser;

    @SerializedName("user_data")
    @Expose
    private LoginModel loginModel;

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

    public String getAlreadyUser() {
        return alreadyUser;
    }

    public void setAlreadyUser(String alreadyUser) {
        this.alreadyUser = alreadyUser;
    }

    public LoginModel getLoginModel() {
        return loginModel;
    }

    public void setLoginModel(LoginModel loginModel) {
        this.loginModel = loginModel;
    }
}