package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category PhoneNumberFragment
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.interfaces.ServiceListener;
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.CommonMethods;
import igniter.utils.Enums;
import igniter.views.main.VerificationActivity;

import static android.text.Html.fromHtml;

/*****************************************************************
 Signup and signin get User phone number page
 ****************************************************************/

public class PhoneNumberFragment extends Fragment implements View.OnClickListener, ServiceListener {

    @Inject
    CommonMethods commonMethods;
    @Inject
    SessionManager sessionManager;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private CountryCodePicker countryCodePicker;
    private CustomEditText edtPhoneNumber;
    private CustomTextView tvNext, tvCountry, tvPhoneKitDesc, tvPhoneNumberTitle;
    private AlertDialog dialog;
    private String countryCode = "", phoneNumber = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.phone_number_fragment, container, false);
            initView();
        }

        return view;
    }

    private void initView() {
        tvNext = (CustomTextView) view.findViewById(R.id.tv_next);
        //tvCountry = (CustomTextView) view.findViewById(R.id.tv_country);
        countryCodePicker = (CountryCodePicker) view.findViewById(R.id.mobile_code);
        tvPhoneNumberTitle = (CustomTextView) view.findViewById(R.id.tv_phone_number_title);
        tvPhoneKitDesc = (CustomTextView) view.findViewById(R.id.tv_phone_account_kit_desc);
        edtPhoneNumber = (CustomEditText) view.findViewById(R.id.edt_phone_number);
        InputMethodManager imm = (InputMethodManager)
                getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtPhoneNumber,
                InputMethodManager.SHOW_IMPLICIT);

        dialog = commonMethods.getAlertDialog(mActivity);

        tvNext.setEnabled(false);
        tvNext.setOnClickListener(this);
        /*tvCountry.setOnClickListener(this);
        tvCountry.setText(res.getString(R.string.country_code));*/
        countryCode = "91";

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCodePicker.getSelectedCountryName();

                countryCode = countryCodePicker.getSelectedCountryCode();
            }
        });

        if (Build.VERSION.SDK_INT >= 24) {
            tvPhoneKitDesc.setText(fromHtml(res.getString(R.string.phone_desc), 0));
        } else {
            tvPhoneKitDesc.setText(fromHtml(res.getString(R.string.phone_desc)));
        }

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.leanhowfb)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.text_light_gray));
                ds.bgColor = Color.WHITE;


            }
        };



        /*makeLinks(tvPhoneKitDesc, new String[] { "Learn how Facebook uses your info" }, new ClickableSpan[] {
                termsOfServicesClick
        });*/

        initTextChangeListener();
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

    private void initTextChangeListener() {
        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtPhoneNumber.getText().toString().trim().length() >= 1) {
                    tvNext.setBackgroundResource(R.drawable.rect_accent_btn);
                    tvNext.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    tvNext.setEnabled(true);
                } else {
                    tvNext.setBackgroundResource(R.drawable.rect_gray_btn);
                    tvNext.setTextColor(ContextCompat.getColor(mActivity, R.color.text_light_gray));
                    tvNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
            case R.id.tv_next:
                phoneNumber = edtPhoneNumber.getText().toString().trim();
                if (phoneNumber.length() >= 1) {
                    Intent intent = new Intent(mActivity, VerificationActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("countryCode", countryCode.replace("+", ""));
                    intent.putExtra("navType", 0);
                    startActivityForResult(intent, 200);
                }
                break;
            /*case R.id.tv_country:
                Intent intent = new Intent(mActivity, CountryCodeActivity.class);
                startActivityForResult(intent, 100);
                break;*/
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
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            if (!TextUtils.isEmpty(data.getStringExtra("countryFlag"))) {
                String countryFlag = data.getStringExtra("countryFlag");
            }
            if (!TextUtils.isEmpty(data.getStringExtra("countryCode"))) {
                countryCode = data.getStringExtra("countryCode");
                tvCountry.setText(countryCode);
                countryCode = countryCode.replace("+", "");
            }
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            if (!TextUtils.isEmpty(data.getStringExtra("otp"))) {
                String otp = data.getStringExtra("otp");
                boolean isAlreadyUser = data.getBooleanExtra("isAlreadyUser", false);

                Bundle bundle = new Bundle();
                bundle.putString("otp", otp);
                bundle.putInt("navType", 0);
                bundle.putBoolean("isAlreadyUser", isAlreadyUser);
                mActivity.changeFragment(Enums.ONE_TIME_PWD, bundle, false);
                mActivity.putHashMap("verification_code", otp);
                mActivity.putHashMap("phone_number", phoneNumber);
                mActivity.putHashMap("country_code", countryCode);
                sessionManager.setPhoneNumber(phoneNumber);
                sessionManager.setCountryCode(countryCode);
            } else {
                tvPhoneNumberTitle.setText(res.getString(R.string.valid_phone_number_alert));
            }
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
    }
}
