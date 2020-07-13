package igniter.views.main.AccountKit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.accountkit.*;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.Gson;


import java.util.HashMap;

import javax.inject.Inject;

import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.LoginModel;
import igniter.datamodels.main.NumberValidationModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;
import igniter.views.main.HomeActivity;

import static igniter.configs.Constants.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static igniter.configs.Constants.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;
import static igniter.utils.Enums.REQ_VERIFY_NUMBER;


public class FacebookAccountKitActivity extends AppCompatActivity implements ServiceListener {

    public @Inject
    SessionManager sessionManager;

    public @Inject
    CommonMethods commonMethods;

    public @Inject
    ApiService apiService;

    public @Inject
    Gson gson;

    public @Inject
    CustomDialog customDialog;


    public AlertDialog dialog;

    public final int FACEBOOK_ACCOUNTKIT_REQUEST_CODE = 157;

    String facebookVerifiedPhoneNumber, facebookVerifiedCountryCode;
    private HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppController.getAppComponent().inject(this);

        startCallingFacebookKit();
    }

    public static void openFacebookAccountKitActivity(Activity activity) {
        Intent intent = activity.getIntent();
        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
        if (hashMap != null)
            Log.v("HashMapTest", hashMap.get("auth_id"));

        Intent facebookIntent = new Intent(activity, FacebookAccountKitActivity.class);
        facebookIntent.putExtra("map",hashMap);
        activity.startActivityForResult(facebookIntent, Constants.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT);
    }


    private void startCallingFacebookKit() {

        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        // PhoneNumber phoneNumber = new PhoneNumber(sessionManager.getTemporaryCountryCode(), sessionManager.getTemporaryPhonenumber());
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, FACEBOOK_ACCOUNTKIT_REQUEST_CODE);
        //overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACEBOOK_ACCOUNTKIT_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null || loginResult.wasCancelled()) {
                //showErrorMessageAndCloseActivity();
                finish();

            } else {
                getPhoneNumber();
            }
        }
    }

    public void getPhoneNumber() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                String phoneNumbers, countryCode, phoneNumberWihtoutPlusSign, temporaryPhoneNumber;

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                phoneNumbers = phoneNumber.getPhoneNumber().toString();
                phoneNumberWihtoutPlusSign = phoneNumbers.replace("+", "");
                countryCode = phoneNumber.getCountryCode();
                callPhoneNumberValidationAPI(phoneNumberWihtoutPlusSign, countryCode);
            }

            @Override
            public void onError(final AccountKitError error) {
                showErrorMessageAndCloseActivity();

                // Handle Error
            }
        });
    }

    /*public void phoneNumberChangedErrorMessage() {
        //commonMethods.showMessage(this, dialog, getString(R.string.InvalidMobileNumber));
        Toast.makeText(this, getString(R.string.InvalidMobileNumber), Toast.LENGTH_SHORT).show();
    }
*/

    /*void facebookAccountKitNumberVerificationSuccess() {
        setResult(ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_SUCCESS);
        finish();
    }

    void facebookAccountKitNumberVerificationFailure() {
        setResult(ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_FAILURE);
        finish();
    }*/

    // api call
    void callPhoneNumberValidationAPI(String facebookVerifiedPhoneNumber, String facebookVerifiedCountryCode) {
        this.facebookVerifiedCountryCode = facebookVerifiedCountryCode;
        this.facebookVerifiedPhoneNumber = facebookVerifiedPhoneNumber;
        sessionManager.setCountryCode(facebookVerifiedCountryCode);
        sessionManager.setPhoneNumber(facebookVerifiedPhoneNumber);
        commonMethods.showProgressDialog(this, customDialog);
        String fbId="";
        if(sessionManager.getIsFBUser()) {
            Intent intent = getIntent();
            HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
            if (hashMap != null) {
                Log.v("HashMapTest", hashMap.get("auth_id"));
                fbId = hashMap.get("auth_id");
            }
        }
        apiService.verifyPhoneNumber(facebookVerifiedPhoneNumber, facebookVerifiedCountryCode,"","").enqueue(new RequestCallback(REQ_VERIFY_NUMBER, this));
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, facebookVerifiedPhoneNumber);
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, facebookVerifiedCountryCode);


        NumberValidationModel numberValidationModel = gson.fromJson(jsonResp.getStrResponse(), NumberValidationModel.class);
        final boolean isAlreadyUser = !TextUtils.isEmpty(numberValidationModel.getAlreadyUser()) && numberValidationModel.getAlreadyUser().equals("true");
        if (isAlreadyUser) {
            commonMethods.showProgressDialog(this, customDialog);
            String phone = sessionManager.getPhoneNumber();
            String country = sessionManager.getCountryCode();
            System.out.println("Phone " + phone);
            System.out.println("Country " + country);

            LoginModel loginModel = numberValidationModel.getLoginModel();
            if (loginModel != null) {
                if (!TextUtils.isEmpty(loginModel.getAccessToken())) {
                    sessionManager.setToken(loginModel.getAccessToken());
                }
                if (!TextUtils.isEmpty(loginModel.getUserImageUrl())) {
                    sessionManager.setProfileImg(loginModel.getUserImageUrl());
                }
                sessionManager.setUserId(loginModel.getUserId());
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        } else {
            returnIntent.putExtra("isAlreadyUser", Boolean.valueOf(isAlreadyUser));
            setResult(Constants.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER, returnIntent);
            finish();
        }



    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        showErrorMessageAndCloseActivity();
    }

    private void showErrorMessageAndCloseActivity() {
        CommonMethods.showServerInternalErrorMessage(this);
        finish();
    }
}

