package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category VerificationActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.obs.CustomTextView;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.datamodels.main.JsonResponse;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.RequestCallback;

import static igniter.utils.Enums.REQ_VERIFY_NUMBER;

/*****************************************************************
 Show phone number or OTP verfication status
 ****************************************************************/
public class VerificationActivity extends AppCompatActivity implements ServiceListener, View.OnClickListener {

    ImageView tvStatus;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiService apiService;
    private CustomTextView tvTitle, tvConfirm;
    private ProgressBar progressBar;
    private String correctOtp = "", enteredOtp = "", phoneNumber = "", countryCode = "", respOtp = "";
    private AlertDialog dialog;
    private int navType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_activity);
        AppController.getAppComponent().inject(this);
        initView();
        getIntentValues();
    }

    private void initView() {
        tvTitle = (CustomTextView) findViewById(R.id.tv_title);
        tvStatus = (ImageView) findViewById(R.id.tv_status);
        tvConfirm = (CustomTextView) findViewById(R.id.tv_confirm);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);
        dialog = commonMethods.getAlertDialog(this);
        tvConfirm.setOnClickListener(this);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getIntentValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!TextUtils.isEmpty(bundle.getString("correctOtp"))) {
                correctOtp = bundle.getString("correctOtp");
            }
            if (!TextUtils.isEmpty(bundle.getString("enteredOtp"))) {
                enteredOtp = bundle.getString("enteredOtp");
            }
            if (!TextUtils.isEmpty(bundle.getString("phoneNumber"))) {
                phoneNumber = bundle.getString("phoneNumber");
            }
            if (!TextUtils.isEmpty(bundle.getString("countryCode"))) {
                countryCode = bundle.getString("countryCode");
            }
            navType = bundle.getInt("navType");
            checkProgress();
        }
    }

    private void checkProgress() {
        switch (navType) {
            case 0:
                tvTitle.setText(getString(R.string.verifying_number));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        //tvStatus.setVisibility(View.VISIBLE);
                        if (phoneNumber.length() >= 6) {
                            //tvTitle.setText(getString(R.string.verified));
                            tvTitle.setText(getString(R.string.sent));
                            tvStatus.setVisibility(View.VISIBLE);
                            tvStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
                            api();
                        } else {
                            //progressBar.setVisibility(View.VISIBLE);
                            tvTitle.setText(getString(R.string.valid_phone_number_alert));
                            tvConfirm.setVisibility(View.VISIBLE);
                            tvConfirm.setText(getString(R.string.try_again));
                            tvStatus.setVisibility(View.VISIBLE);
                            tvStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_exclamatory));
                        }
                    }
                }, 1000);
                break;
            case 1:
                tvTitle.setText(getString(R.string.verifying_code));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        //tvStatus.setVisibility(View.VISIBLE);
                        if (correctOtp.equals(enteredOtp)) {
                            tvTitle.setText(getString(R.string.verified));
                            progressBar.setVisibility(View.GONE);
                            tvStatus.setVisibility(View.VISIBLE);
                            tvStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            /*tvTitle.setText(getString(R.string.verify_code_error_msg));
                            tvStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_exclamatory));*/
                        }
                    }
                }, 1000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("isSuccess", correctOtp.equals(enteredOtp));
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }, 4000);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }

        progressBar.setVisibility(View.GONE);
        tvStatus.setVisibility(View.VISIBLE);
        if (jsonResp.getRequestCode() == REQ_VERIFY_NUMBER && jsonResp.isSuccess()) {
            tvTitle.setText(getString(R.string.sent));
            tvStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
            onSuccessVerifyNumber(jsonResp);
        } else {
            /*tvTitle.setText(getString(R.string.valid_phone_number_alert));
            tvConfirm.setVisibility(View.VISIBLE);
            tvConfirm.setText(getString(R.string.try_again));*/

            tvTitle.setText(getString(R.string.sent));
            tvStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_tick));
            onSuccessVerifyNumber(jsonResp);
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        progressBar.setVisibility(View.GONE);
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessVerifyNumber(JsonResponse jsonResp) {
        final int otp = (int) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, Integer.class);
        String alreadyUser = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.ALREADY_USER, String.class);
        String statusMessage = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.STATUS_MSG, String.class);
        final boolean isAlreadyUser = !TextUtils.isEmpty(alreadyUser) && alreadyUser.equals("true");
        if (otp > 0) {
            respOtp = String.valueOf(otp);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("otp", respOtp);
                    returnIntent.putExtra("isAlreadyUser", Boolean.valueOf(isAlreadyUser));
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }, 2000);
        } else {
            Toast.makeText(this, statusMessage, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("otp", "");
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void api() {
        apiService.verifyPhoneNumber(phoneNumber, countryCode,"","").enqueue(new RequestCallback(REQ_VERIFY_NUMBER, this));
    }
}
