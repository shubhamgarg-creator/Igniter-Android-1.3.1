package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category GenderFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.obs.CustomButton;
import com.obs.CustomTextView;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.SignUpModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.CommonMethods;
import igniter.utils.Enums;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;
import igniter.views.main.HomeActivity;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/*
 * Created by Ganesh K on 12-09-2017.
 */

public class GenderFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener ,ServiceListener {

    @Inject
    SessionManager sessionManager;
    @Inject
    ApiService apiService;
    @Inject
    Gson gson;
    @Inject
    CustomDialog customDialog;

    @Inject
    CommonMethods commonMethods;

    private AlertDialog dialog;

    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;

    private CustomTextView tvBackArrow;
    private CustomButton btnContinue;
    private RadioGroup rdgGender;
    private String gender;

    HashMap<String, String> hashMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.gender_fragment, container, false);
            initView();
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            hashMap = (HashMap<String, String>) bundle.getSerializable("map");
            if (hashMap != null)
                Log.v("HashMapTest", hashMap.get("auth_id"));
        }
    }

    private void initView() {
        dialog = commonMethods.getAlertDialog(mActivity);
        tvBackArrow = (CustomTextView) view.findViewById(R.id.tv_left_arrow);
        btnContinue = (CustomButton) view.findViewById(R.id.btn_continue);
        rdgGender = (RadioGroup) view.findViewById(R.id.rdg_gender);

        tvBackArrow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        rdgGender.setOnCheckedChangeListener(this);

        //gender = res.getString(R.string.men);
        btnContinue.setEnabled(false);

    }

    private void init() {
        AppController.getAppComponent().inject(this);
        if (listener == null) return;
        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (SignUpActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_arrow:
                mActivity.onBackPressed();
                break;
            case R.id.btn_continue:
                if (sessionManager.getIsFBUser()) {
                    hashMap.put("gender", gender);
                    commonMethods.showProgressDialog(mActivity, customDialog);
                    apiService.signUp(gotUserDetailsFromFacebook(hashMap)).enqueue(new RequestCallback(this));
                }else {
                    mActivity.putHashMap("gender", gender);
                    mActivity.changeFragment(Enums.PROFILE_PICK, null, false);
                }
                break;
            default:
                break;
        }
    }

    private RequestBody gotUserDetailsFromFacebook(HashMap<String,String> userData){
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);
        try {
            multipartBody.addFormDataPart("image","");
            for (Map.Entry<String, String> entry : userData.entrySet()) {
                if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                    multipartBody.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipartBody.build();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SignUpActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + Constants.listenerSignUpException);
        }
    }

    @Override
    public void onDetach() {
        if (listener != null) listener = null;
        super.onDetach();
    }

    /**
     * onCreateAnimation is used to perform the animation while sliding or
     * automatic Slideshow in the image gallery.
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (Constants.isDisableFragmentAnimations) {
            Animation a = new Animation() {
            };
            a.setDuration(0);
            return a;
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_man:
                gender = res.getString(R.string.men);
                btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                btnContinue.setEnabled(true);
                break;
            case R.id.rb_woman:
                gender = res.getString(R.string.women);
                btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                btnContinue.setEnabled(true);
                break;
            default:
                break;
        }
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(mActivity, dialog, data);
            return;
        }

        if (jsonResp.isSuccess()) {
            onSuccessLogin(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(mActivity, dialog, data);
    }

    private void onSuccessLogin(JsonResponse jsonResp) {
        SignUpModel signUpModel = gson.fromJson(jsonResp.getStrResponse(), SignUpModel.class);
        if (signUpModel != null) {
            if (!TextUtils.isEmpty(signUpModel.getAccessToken())) {
                sessionManager.setToken(signUpModel.getAccessToken());
            }
            if (!TextUtils.isEmpty(signUpModel.getUserImageUrl())) {
                sessionManager.setProfileImg(signUpModel.getUserImageUrl());
            }
            sessionManager.setUserId(signUpModel.getUserId());
            Intent intent = new Intent(mActivity, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            mActivity.finish();
        }
    }
}
