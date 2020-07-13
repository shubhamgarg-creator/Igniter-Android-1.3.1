package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category EmailFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.obs.CustomButton;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.SessionManager;
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.Enums;
import igniter.utils.Validator;
import igniter.views.customize.CustomDialogBox;
import igniter.views.main.LoginActivity;

/*****************************************************************
 Signup Email page (optional)
 ****************************************************************/

public class EmailFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    CustomDialogBox custom;
    @Inject
    Validator validator;
    @Inject
    SessionManager sessionManager;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private CustomTextView tvClose, tvSkip;
    private CustomButton btnSignUp;
    private CustomEditText edtEmailAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.email_fragment, container, false);
            initView();
        }

        return view;
    }

    private void initView() {
        tvClose = (CustomTextView) view.findViewById(R.id.tv_close);
        tvSkip = (CustomTextView) view.findViewById(R.id.tv_skip);
        btnSignUp = (CustomButton) view.findViewById(R.id.btn_signup);
        edtEmailAddress = (CustomEditText) view.findViewById(R.id.edt_email);
        if (sessionManager.getIsAppleUser()&&!sessionManager.getSocialMail().isEmpty()){
            edtEmailAddress.setFocusable(false);
            edtEmailAddress.setEnabled(true);
            edtEmailAddress.setText(sessionManager.getSocialMail());
            btnSignUp.setBackgroundResource(R.drawable.oval_gradient_btn);
            btnSignUp.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            btnSignUp.setEnabled(true);
            tvSkip.setVisibility(View.GONE);
        }else {
            edtEmailAddress.requestFocus();
            btnSignUp.setEnabled(false);
        }

        /*InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtEmailAddress, 0);*/


        tvSkip.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);


        initTextChangeListener();

    }

    private void initTextChangeListener() {
        edtEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validator.isValidateEmail(edtEmailAddress.getText().toString().trim())) {
                    btnSignUp.setBackgroundResource(R.drawable.oval_gradient_btn);
                    btnSignUp.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    btnSignUp.setEnabled(true);
                } else {
                    btnSignUp.setBackgroundResource(R.drawable.oval_btn_gray);
                    btnSignUp.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_btn_text));
                    btnSignUp.setEnabled(false);
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
            case R.id.tv_close:
                custom = new CustomDialogBox(getString(R.string.areyousure), getString(R.string.Youwillexit), getString(R.string.yes), res.getString(R.string.no), new CustomDialogBox.btnAllowClick() {
                    @Override
                    public void clicked() {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }, null);
                //custom.setCancelable(true);
                custom.show(mActivity.getSupportFragmentManager(), "");
                custom.setCancelable(true);
                break;
            case R.id.btn_signup:
                mActivity.putHashMap("email_id", edtEmailAddress.getText().toString().trim());
                mActivity.putHashMap("password", "");
                mActivity.changeFragment(Enums.FIRST_NAME, null, false);
                break;
            case R.id.tv_skip:
                /*mActivity.removeHashMap("email_id");
                custom = new CustomDialogBox(getString(R.string.areyousure), getString(R.string.Youwillexit), getString(R.string.yes),  res.getString(R.string.no), new CustomDialogBox.btnAllowClick() {
                    @Override
                    public void clicked() {
                        mActivity.changeFragment(Enums.PASSWORD, null, false);

                    }
                }, null);
                custom.show(mActivity.getSupportFragmentManager(), "");
                custom.setCancelable(true);*/
                mActivity.putHashMap("email_id", "");
                mActivity.putHashMap("password", "");
                mActivity.changeFragment(Enums.FIRST_NAME, null, false);

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
    public void onResume() {
        super.onResume();
        if(view!=null) {
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            getActivity().finish();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            mActivity.finish();
                            return true;
                        }
                    }
                    return false;
                }
            });
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

    }
}
