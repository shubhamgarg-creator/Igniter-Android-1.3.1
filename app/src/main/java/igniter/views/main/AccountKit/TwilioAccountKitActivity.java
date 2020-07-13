package igniter.views.main.AccountKit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import javax.inject.Inject;

import igniter.R;
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
import static igniter.utils.Enums.REQ_SEND_OTP;
import static igniter.utils.Enums.REQ_VERIFY_NUMBER;


public class TwilioAccountKitActivity extends AppCompatActivity implements ServiceListener, View.OnClickListener {

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

    boolean isPhoneNumberLayoutIsVisible = true;
    TextView mobileNumberHeading;
    TextView tvResendOTPLabel;
    TextView tvResendOTPCountdown;
    TextView tvResendOTP;
    TextView tvOTPErrorMessage;
    ConstraintLayout ctlPhoneNumber;
    ConstraintLayout ctlOTP;
    ProgressBar pbNumberVerification;
    ImageView imgvArrow;
    RelativeLayout rlEdittexts;
    EditText edtxOne;
    EditText edtxTwo;
    EditText edtxThree;
    EditText edtxFour;
    EditText edtxPhoneNumber;
    CountryCodePicker ccp;
    CardView cvNext;
    TextView tvPhoneBack;
    TextView tvOTPback;

    private int isForForgotPassword = 0;
    private String otp = "";
    private String receivedOTPFromServer;
    private long resendOTPWaitingSecond = 120000;
    private CountDownTimer resentCountdownTimer, backPressCounter;
    private boolean isDeletable = true;


    public void startAnimationd() {
        //startAnimation();
        if (isPhoneNumberLayoutIsVisible && edtxPhoneNumber.getText().toString().length() > 5) {
            callSendOTPAPI();
        } else if (!isPhoneNumberLayoutIsVisible) {
            verifyOTP();

        }
        /*showOTPfield();
        showOTPMismatchIssue();*/

    }


    private void verifyOTP() {
        if (!TextUtils.isEmpty(otp)) {
            if (otp.equalsIgnoreCase(receivedOTPFromServer)) {
                callPhoneNumberValidationAPI(edtxPhoneNumber.getText().toString().trim(),
                        ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
            } else {
                showOTPMismatchIssue();
            }
        } else {
            commonMethods.showMessage(this, dialog, getString(R.string.otp_number_alert));
        }
    }


    private void shakeEdittexts() {
        TranslateAnimation shake = new TranslateAnimation(0, 20, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(3));
        rlEdittexts.startAnimation(shake);

    }

    public void showOTPMismatchIssue() {
        shakeEdittexts();
        tvOTPErrorMessage.setVisibility(View.VISIBLE);
    }

    public void runCountdownTimer() {
        tvResendOTP.setVisibility(View.GONE);
        tvResendOTPCountdown.setVisibility(View.VISIBLE);
        tvResendOTPLabel.setText(getResources().getString(R.string.send_OTP_again_in));
        if (resentCountdownTimer != null) {
            resentCountdownTimer.cancel();
        }
        resentCountdownTimer = new CountDownTimer(resendOTPWaitingSecond, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendOTPCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                tvResendOTPCountdown.setVisibility(View.GONE);
                tvResendOTPLabel.setText(getResources().getString(R.string.resend_otp));
                tvResendOTP.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    public void showPhoneNumberField() {
        cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
        ctlPhoneNumber.setVisibility(View.VISIBLE);
        ctlOTP.setVisibility(View.GONE);
        isPhoneNumberLayoutIsVisible = true;
        tvResendOTP.setVisibility(View.GONE);
        tvResendOTPLabel.setVisibility(View.GONE);
        tvResendOTPCountdown.setVisibility(View.GONE);
        resentCountdownTimer.cancel();
    }


    public void finishThisActivity() {
        super.onBackPressed();
    }

    public void showOTPfield() {
        ctlPhoneNumber.setVisibility(View.GONE);
        ctlOTP.setVisibility(View.VISIBLE);
        cvNext.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));
        isPhoneNumberLayoutIsVisible = false;

        runCountdownTimer();

        tvResendOTPLabel.setVisibility(View.VISIBLE);
    }


    public AlertDialog dialog;

    public final int FACEBOOK_ACCOUNTKIT_REQUEST_CODE = 157;

    String facebookVerifiedPhoneNumber, facebookVerifiedCountryCode;
    private HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number_verification);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        initViews();
        initOTPTextviewListener();
        //startCallingFacebookKit();
    }

    private void initViews() {
        getIntentValues();
        mobileNumberHeading = findViewById(R.id.tv_mobile_heading);
        tvResendOTPLabel = findViewById(R.id.tv_otp_resend_label);
        tvResendOTPCountdown = findViewById(R.id.tv_otp_resend_countdown);
        tvResendOTP = findViewById(R.id.tv_resend_button);
        tvOTPErrorMessage = findViewById(R.id.tv_otp_error_field);
        ctlPhoneNumber = findViewById(R.id.cl_phone_number_input);
        ctlOTP = findViewById(R.id.cl_otp_input);
        pbNumberVerification = findViewById(R.id.pb_number_verification);
        imgvArrow = findViewById(R.id.imgv_next);
        rlEdittexts = findViewById(R.id.rl_edittexts);
        edtxOne = findViewById(R.id.one);
        edtxTwo = findViewById(R.id.two);
        edtxThree = findViewById(R.id.three);
        edtxFour = findViewById(R.id.four);
        edtxPhoneNumber = findViewById(R.id.phone);
        ccp = findViewById(R.id.ccp);
        tvPhoneBack = findViewById(R.id.tv_back_phone_arrow);
        tvOTPback = findViewById(R.id.tv_back_otp_arrow);
        cvNext = findViewById(R.id.fab_verify);
        cvNext.setOnClickListener(this);
        tvResendOTP.setOnClickListener(this);
        tvOTPback.setOnClickListener(this);
        tvPhoneBack.setOnClickListener(this);
        cvNext.setOnClickListener(this);

        ccp.setAutoDetectedCountry(true);
        edtxPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtxPhoneNumber.getText().toString().length() > 5) {
                    cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
                } else {
                    cvNext.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));
                }
            }
        });

        initDirectionChanges();
    }

    private void initDirectionChanges() {
        String laydir = getResources().getString(R.string.layout_direction);

        if ("1".equals(laydir)) {
            cvNext.setRotation(180);
            tvPhoneBack.setRotation(180);
            tvOTPback.setRotation(180);

        }
    }

    private void getIntentValues() {
        try {
            isForForgotPassword = getIntent().getIntExtra("usableType", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initOTPTextviewListener() {
        edtxOne.addTextChangedListener(new OtpTextWatcher());
        edtxTwo.addTextChangedListener(new OtpTextWatcher());
        edtxThree.addTextChangedListener(new OtpTextWatcher());
        edtxFour.addTextChangedListener(new OtpTextWatcher());

        edtxOne.setOnKeyListener(new OtpTextBackWatcher());
        edtxTwo.setOnKeyListener(new OtpTextBackWatcher());
        edtxThree.setOnKeyListener(new OtpTextBackWatcher());
        edtxFour.setOnKeyListener(new OtpTextBackWatcher());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_resend_button:
                callSendOTPAPI();
                break;
            case R.id.tv_back_otp_arrow:
                showPhoneNumberField();
                break;
            case R.id.tv_back_phone_arrow:
                finishThisActivity();
                break;
            case R.id.fab_verify:
                startAnimationd();
                break;


        }
    }

    private class OtpTextWatcher implements TextWatcher {


        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (edtxOne.isFocused()) {
                if (edtxOne.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxTwo.requestFocus();
                    edtxTwo.setSelectAllOnFocus(true);
                    //one.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                }
            } else if (edtxTwo.isFocused()) {
                if (edtxTwo.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxThree.requestFocus();
                    edtxThree.setSelectAllOnFocus(true);
                    //two.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                } else {
                    edtxOne.requestFocus();
                    edtxOne.setSelectAllOnFocus(true);
                    // edtxOne.setSelection(1);
                }
            } else if (edtxThree.isFocused()) {
                if (edtxThree.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxFour.requestFocus();
                    edtxFour.setSelectAllOnFocus(true);
                    //three.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                } else {
                    edtxTwo.requestFocus();
                    edtxTwo.setSelectAllOnFocus(true);
                    //edtxTwo.setSelection(1);
                }
            } else if (edtxFour.isFocused()) {
                if (edtxFour.getText().toString().length() == 0) {
                    edtxThree.requestFocus();
                }
            }

            if (edtxOne.getText().toString().trim().length() > 0 && edtxTwo.getText().toString().trim().length() > 0 && edtxThree.getText().toString().trim().length() > 0 && edtxFour.getText().toString().trim().length() > 0) {
                otp = edtxOne.getText().toString().trim() + edtxTwo.getText().toString().trim() + edtxThree.getText().toString().trim() + edtxFour.getText().toString().trim();
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
            } else {
                otp = "";
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));
            }
            tvOTPErrorMessage.setVisibility(View.GONE);
        }

        public void afterTextChanged(Editable editable) {

        }
    }

    private class OtpTextBackWatcher implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && isDeletable) {
                switch (v.getId()) {
                    case R.id.one: {
                        edtxOne.getText().clear();
                        break;
                    }
                    case R.id.two: {
                        edtxTwo.getText().clear();
                        edtxOne.requestFocus();
                        edtxOne.setSelectAllOnFocus(true);
                        break;
                    }
                    case R.id.three: {
                        edtxThree.getText().clear();
                        edtxTwo.requestFocus();
                        edtxTwo.setSelectAllOnFocus(true);
                        break;
                    }
                    case R.id.four: {
                        edtxFour.getText().clear();
                        edtxThree.requestFocus();
                        edtxThree.setSelectAllOnFocus(true);
                        //edtxThree.setSelection(1);
                        break;
                    }

                }
                countdownTimerForOTPBackpress();
                return true;
            } else {
                return false;
            }

        }
    }

    public void countdownTimerForOTPBackpress() {
        isDeletable = false;
        if (backPressCounter != null) backPressCounter.cancel();
        backPressCounter = new CountDownTimer(100, 1000) {

            public void onTick(long millisUntilFinished) {
                //tvResendOTPCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                isDeletable = true;
            }
        }.start();
    }

    public void callSendOTPAPI() {
        showProgressBarAndHideArrow(true);
        apiService.numbervalidation(edtxPhoneNumber.getText().toString(),
                ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", "")).enqueue(new RequestCallback(REQ_SEND_OTP, this));
    }

    public void showProgressBarAndHideArrow(Boolean status) {
        if (status) {
            pbNumberVerification.setVisibility(View.VISIBLE);
            imgvArrow.setVisibility(View.GONE);
        } else {
            pbNumberVerification.setVisibility(View.GONE);
            imgvArrow.setVisibility(View.VISIBLE);
        }
    }


    public static void openTwilioAccountKitActivity(Activity activity) {
        Intent intent = activity.getIntent();
        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
        if (hashMap != null)
            Log.v("HashMapTest", hashMap.get("auth_id"));

        Intent facebookIntent = new Intent(activity, TwilioAccountKitActivity.class);
        facebookIntent.putExtra("map", hashMap);
        activity.startActivityForResult(facebookIntent, Constants.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT);
    }


    // api call
    void callPhoneNumberValidationAPI(String twPhoneNumber, String twCountryCode) {
        this.facebookVerifiedCountryCode = twCountryCode;
        this.facebookVerifiedPhoneNumber = twPhoneNumber;
        sessionManager.setCountryCode(twCountryCode);
        sessionManager.setPhoneNumber(twPhoneNumber);
        commonMethods.showProgressDialog(this, customDialog);
        String fbId = "";
        if (sessionManager.getIsFBUser()) {
            Intent intent = getIntent();
            HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
            if (hashMap != null) {
                Log.v("HashMapTest", hashMap.get("auth_id"));
                fbId = hashMap.get("auth_id");
            }
        }
        apiService.verifyPhoneNumber(twPhoneNumber, twCountryCode, commonMethods.getAuthId(),commonMethods.getAuthType()).enqueue(new RequestCallback(REQ_VERIFY_NUMBER, this));
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

        switch (jsonResp.getRequestCode()) {
            case REQ_SEND_OTP:
                showProgressBarAndHideArrow(false);
                if (jsonResp.isSuccess()) {
                    receivedOTPFromServer = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "otp", String.class);
                    showOTPfield();
                    //setOtp();

                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
//                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    showSettingsAlert(jsonResp.getStatusMsg());
                }
                break;
            case REQ_VERIFY_NUMBER:
                commonMethods.hideProgressDialog();
                if (jsonResp.isSuccess()) {
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
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;


        }


    }

    private void setOtp() {
        if (!TextUtils.isEmpty(receivedOTPFromServer)) {
            String[] otp = receivedOTPFromServer.split("");
            edtxOne.setText(otp[1]);
            edtxTwo.setText(otp[2]);
            edtxThree.setText(otp[3]);
            edtxFour.setText(otp[4]);
            cvNext.setCardBackgroundColor(getResources().getColor(R.color.light_blue_button_color));
        }
    }

    public void showSettingsAlert(String statusMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this);
        //alertDialog.setTitle(statusMsg);
        alertDialog.setMessage(statusMsg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(getResources().getString(R.string.okay),
                (dialog, which) -> finish());

        alertDialog.show();
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

    @Override
    public void onBackPressed() {
        if (isPhoneNumberLayoutIsVisible) {
            super.onBackPressed();
        } else {
            showPhoneNumberField();
        }
    }
}

