package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category UserNameActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.SettingsModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;

/*****************************************************************
 In setting page chage weburl based on user name
 ****************************************************************/
public class UserNameActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener {

    ImageView usename_lke;
    String username1;
    @Inject
    ApiService apiService;
    @Inject
    CommonMethods commonMethods;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson;
    private CustomEditText edtUserName;
    private CustomTextView tvUserName, tvUserNameCount, tvHeader, tvBackArrow, del_user_name, errortext;
    private AlertDialog dialog;
    private SettingsModel settingsModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);
        setContentView(R.layout.username_activity);
        initView();
    }

    private void initView() {
        tvHeader = (CustomTextView) findViewById(R.id.tv_header_title);
        tvBackArrow = (CustomTextView) findViewById(R.id.tv_left_arrow);

        edtUserName = (CustomEditText) findViewById(R.id.edt_username);
        tvUserName = (CustomTextView) findViewById(R.id.tv_user_name);
        tvUserNameCount = (CustomTextView) findViewById(R.id.tv_username_length);
        usename_lke = (ImageView) findViewById(R.id.usename_lke);
        tvUserName.setEnabled(false);
        usename_lke.setVisibility(View.GONE);
        del_user_name = (CustomTextView) findViewById(R.id.del_user_name);
        errortext = (CustomTextView) findViewById(R.id.tv_username_error);
        errortext.setVisibility(View.GONE);
        tvUserNameCount.setVisibility(View.INVISIBLE);

        edtUserName.setText(getResources().getString(R.string.username_hint));

        // dialog = commonMethods.getAlertDialog(this);

        tvBackArrow.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        del_user_name.setOnClickListener(this);

        tvHeader.setText(getString(R.string.header_username));
        edtUserName.setSelection(edtUserName.getText().toString().trim().length());
        edtUserName.setText(getResources().getString(R.string.username_hint) + sessionManager.getUserName());

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvUserNameCount.setText(String.valueOf(33 - s.length()));
                if (s.length() > 1) {
                    tvUserName.setEnabled(true);
                } else {
                    tvUserName.setEnabled(false);
                }
                tvUserNameCount.setVisibility(View.VISIBLE);
                usename_lke.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith(getResources().getString(R.string.username_hint))) {
                    edtUserName.setText(getResources().getString(R.string.username_hint));
                    Selection.setSelection(edtUserName.getText(), edtUserName.getText().length());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_name:
                commonMethods.showProgressDialog(this, customDialog);

                username1 = edtUserName.getText().toString();
                username1 = username1.replaceAll("igniter.com/@", "");
                apiService.claimUserName(sessionManager.getToken(), username1).enqueue(new RequestCallback(this));
                //settingsModel.setUserName(username1);
                //onBackPressed();
                break;
            case R.id.tv_left_arrow:
                onBackPressed();
                break;
            case R.id.del_user_name:
                String msg = getResources().getString(R.string.suretodel);
                String title = getResources().getString(R.string.deleteusername);
                String btnText = getResources().getString(R.string.del);
                showDialog(title, msg, btnText, 0);
                break;
            default:
                break;
        }
    }

    private void showDialog(String title, String msg, String buttonText, final int index) {
        customDialog = new CustomDialog(index, title, msg, buttonText, getResources().getString(R.string.cancel), new CustomDialog.btnAllowClick() {
            @Override
            public void clicked() {
                edtUserName.setText("");
                del_user_name.setVisibility(View.GONE);
                errortext.setVisibility(View.GONE);
            }

        }, null);
        customDialog.show(UserNameActivity.this.getSupportFragmentManager(), "");
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }

        if (jsonResp.isSuccess()) {
            if (jsonResp.getStatusMsg().equals(getString(R.string.msg_user_claimed))) {
                usename_lke.setVisibility(View.VISIBLE);
                Snackbar.make(errortext, "Username Saved", 2000).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("isUserNameAdded", true);
                        returnIntent.putExtra("claimedName", username1);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }, 3000);

            }

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
        if (jsonResp.getStatusMsg().equals(getString(R.string.msg_user_already_taken))) {

            errortext.setVisibility(View.VISIBLE);
            //edtUserName.setError("Username already taken");
            usename_lke.setVisibility(View.VISIBLE);
            usename_lke.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlike));
        } else {

        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }
}

