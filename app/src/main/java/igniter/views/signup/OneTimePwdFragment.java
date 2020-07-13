package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category OneTimePwdFragmentFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import java.util.HashMap;

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
import igniter.views.main.VerificationActivity;

import static android.text.Html.fromHtml;

/*****************************************************************
 Signup and signin One Time Password page
 ****************************************************************/

public class OneTimePwdFragment extends Fragment implements View.OnClickListener, ServiceListener {

    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiService apiService;
    @Inject
    Gson gson;
    @Inject
    CustomDialog customDialog;
    HashMap<String, String> hashMap;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private CustomEditText edtCode1, edtCode2, edtCode3, edtCode4, edtCode5, edtCode6;
    private CustomTextView tvContinue, tvMobileNumber, tvResendCode, tvOtpTitle, tv_accept_terms;
    private TextWatcher textChangeListener;
    private AlertDialog dialog;
    private String otp = "";
    private StringBuilder sb;
    private int navType = 0;
    private boolean isAlreadyUser = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.otp_fragment, container, false);
            initView();
        }
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(edtCode1, InputMethodManager.SHOW_IMPLICIT);
        return view;
    }

    private void initView() {
        tvMobileNumber = (CustomTextView) view.findViewById(R.id.tv_mobile_number);
        tvContinue = (CustomTextView) view.findViewById(R.id.tv_continue);
        tvResendCode = (CustomTextView) view.findViewById(R.id.tv_resend_code);
        tvOtpTitle = (CustomTextView) view.findViewById(R.id.tv_otp_title);
        tv_accept_terms = (CustomTextView) view.findViewById(R.id.tv_accept_terms);

        edtCode1 = (CustomEditText) view.findViewById(R.id.edt_code1);
        edtCode2 = (CustomEditText) view.findViewById(R.id.edt_code2);
        edtCode3 = (CustomEditText) view.findViewById(R.id.edt_code3);
        edtCode4 = (CustomEditText) view.findViewById(R.id.edt_code4);
        edtCode5 = (CustomEditText) view.findViewById(R.id.edt_code5);
        edtCode6 = (CustomEditText) view.findViewById(R.id.edt_code6);

        delKey();

        tvContinue.setOnClickListener(this);
        tvResendCode.setOnClickListener(this);
        tvContinue.setEnabled(false);

        if (Build.VERSION.SDK_INT >= 24) {
            tv_accept_terms.setText(fromHtml(res.getString(R.string.accept_term_policys), 0));
        } else {
            tv_accept_terms.setText(fromHtml(res.getString(R.string.accept_term_policys)));
        }

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.redirect_url) + getString(R.string.terms_url)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;

            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.redirect_url) + getString(R.string.privacy_url)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;
            }
        };

        ClickableSpan cookieuse = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(getString(R.string.cookieuse)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;
            }
        };

        ClickableSpan DataPolicy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.redirect_url) + getString(R.string.privacy_url)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;
            }
        };

        ClickableSpan Terms = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse(getString(R.string.leanhowfb)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;
            }
        };



        /*makeLinks(tv_accept_terms, new String[] { "Terms of Service", "Privacy Policy","cookie use","Data Policy","Terms" }, new ClickableSpan[] {
                termsOfServicesClick, privacyPolicyClick, cookieuse, DataPolicy,Terms
        });*/
        makeLinks(tv_accept_terms, new String[]{mActivity.getString(R.string.terms_service), mActivity.getString(R.string.privacy_policy)}, new ClickableSpan[]{
                termsOfServicesClick, privacyPolicyClick,});
        dialog = commonMethods.getAlertDialog(mActivity);

        initTextChangeListener();
        edtCode1.addTextChangedListener(textChangeListener);
        edtCode2.addTextChangedListener(textChangeListener);
        edtCode3.addTextChangedListener(textChangeListener);
        edtCode4.addTextChangedListener(textChangeListener);
        edtCode5.addTextChangedListener(textChangeListener);
        edtCode6.addTextChangedListener(textChangeListener);

        tvMobileNumber.setText("+" + sessionManager.getCountryCode() + " " + sessionManager.getPhoneNumber());
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setTextColor(getResources().getColor(R.color.text_light_gray));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    private void delKey() {
        edtCode2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edtCode2.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edtCode2.getText().toString().length() == 0) {
                    edtCode1.requestFocus();
                    edtCode1.getText().clear();
                }
                return false;
            }
        });
        edtCode3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edtCode3.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edtCode3.getText().toString().length() == 0) {
                    edtCode2.requestFocus();
                    edtCode2.getText().clear();
                }
                return false;
            }
        });
        edtCode4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edtCode4.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edtCode4.getText().toString().length() == 0) {
                    edtCode3.requestFocus();
                    edtCode3.getText().clear();
                }
                return false;
            }
        });
        edtCode5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edtCode5.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edtCode5.getText().toString().length() == 0) {
                    edtCode4.requestFocus();
                    edtCode4.getText().clear();
                }
                return false;
            }
        });
        edtCode6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_


                //this is for backspace
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edtCode6.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edtCode6.getText().toString().length() == 0) {
                    edtCode5.requestFocus();
                    edtCode5.getText().clear();
                }

                //}
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBundleValues();
    }

    private void getBundleValues() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (!TextUtils.isEmpty(bundle.getString("otp"))) {
                otp = bundle.getString("otp");
                // IN Live comment OTP and in demo uncomment OTP
                Toast.makeText(getContext(), getString(R.string.otp)+" : " + otp, Toast.LENGTH_LONG).show();
                /*if(otp.length()>0) {
                    edtCode1.setText(otp.charAt(0));
                    edtCode2.setText(otp.charAt(1));
                    edtCode3.setText(otp.charAt(2));
                    edtCode4.setText(otp.charAt(3));
                    edtCode5.setText(otp.charAt(4));
                    edtCode6.setText(otp.charAt(5));
                }*/
            }
            navType = bundle.getInt("navType", 0);
            isAlreadyUser = bundle.getBoolean("isAlreadyUser", false);
        }
        switch (navType) {
            case 0:
            case 1:
                tvOtpTitle.setText(res.getString(R.string.otp_title));
                break;
            case 2:
                break;
            case 3:
                tvOtpTitle.setText(res.getString(R.string.code_from_voice_call));
                break;
            default:
                break;
        }
    }

    private void initTextChangeListener() {

        textChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidOtp();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void isValidOtp() {
        tvContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.text_light_gray));
        tvContinue.setBackgroundResource(R.drawable.rect_gray_btn);
        tvContinue.setEnabled(false);
        String code1 = edtCode1.getText().toString().trim();
        String code2 = edtCode2.getText().toString().trim();
        String code3 = edtCode3.getText().toString().trim();
        String code4 = edtCode4.getText().toString().trim();
        String code5 = edtCode5.getText().toString().trim();
        String code6 = edtCode6.getText().toString().trim();
        sb = new StringBuilder();
        if (!TextUtils.isEmpty(code1)) {
            sb.append(code1);
        } else {
            edtCode1.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(code2)) {
            sb.append(code2);
        } else {
            edtCode2.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(code3)) {
            sb.append(code3);
        } else {
            edtCode3.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(code4)) {
            sb.append(code4);
        } else {
            edtCode4.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(code5)) {
            sb.append(code5);
        } else {
            edtCode5.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(code6)) {
            sb.append(code6);
        } else {
            edtCode6.requestFocus();
        }

        if (sb.length() == 6) {
            tvContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            tvContinue.setBackgroundResource(R.drawable.rect_accent_btn);
            tvContinue.setEnabled(true);
        }

    }

    private void init() {
        AppController.getAppComponent().inject(this);
        if (listener == null) return;
        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (SignUpActivity) getActivity();
        AppController.getAppComponent().inject(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_continue:
                Intent intent = new Intent(mActivity, VerificationActivity.class);
                intent.putExtra("correctOtp", otp);
                intent.putExtra("enteredOtp", sb.toString());
                intent.putExtra("navType", 1);
                mActivity.startActivityForResult(intent, 200);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearOtp();
                    }
                }, 1000);
                break;
            default:
                break;
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("isSuccess", false)) {
                if (isAlreadyUser && !sessionManager.getIsFBUser()) {
                    commonMethods.showProgressDialog(mActivity, customDialog);
                    String phone = sessionManager.getPhoneNumber();
                    String country = sessionManager.getCountryCode();
                    System.out.println("Phone " + phone);
                    System.out.println("Country " + country);
                    apiService.login(sessionManager.getPhoneNumber(), sessionManager.getCountryCode()).enqueue(new RequestCallback(this));
                } else {
                    if (sessionManager.getIsFBUser()) {
                        hashMap.put("phone_number", sessionManager.getPhoneNumber());
                        hashMap.put("country_code", sessionManager.getCountryCode());
                        apiService.fbPhoneSignup(hashMap).enqueue(new RequestCallback(this));
                    } else {
                        mActivity.changeFragment(Enums.EMAIL, null, false);
                    }
                }
            } else {
                tvOtpTitle.setText(res.getString(R.string.verify_code_error_msg));
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            hashMap = (HashMap<String, String>) bundle.getSerializable("map");
            if (hashMap != null)
                Log.v("HashMapTest", hashMap.get("auth_id"));
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
            if (!mActivity.isFinishing()) mActivity.finish();
        }
    }

    public void clearOtp() {


        if (!otp.equals(sb.toString())) {
            edtCode1.setText("");
            edtCode2.setText("");
            edtCode3.setText("");
            edtCode4.setText("");
            edtCode5.setText("");
            edtCode6.setText("");

            edtCode1.requestFocus();
        }
    }
}