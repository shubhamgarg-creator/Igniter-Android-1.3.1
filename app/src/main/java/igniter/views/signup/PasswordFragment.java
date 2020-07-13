package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category PasswordFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.Enums;
import igniter.utils.Validator;

/*****************************************************************
 Signup password page
 ****************************************************************/

public class PasswordFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    @Inject
    Validator validator;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private CustomTextView tvBackArrow, tv_password_restriction;
    private CustomButton btnContinue;
    private CustomEditText edtPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.my_password_fragment, container, false);
            initView();
        }


        return view;
    }

    private void initView() {
        tvBackArrow = (CustomTextView) view.findViewById(R.id.tv_left_arrow);
        btnContinue = (CustomButton) view.findViewById(R.id.btn_continue);
        edtPassword = (CustomEditText) view.findViewById(R.id.edt_password);
        tv_password_restriction = (CustomTextView) view.findViewById(R.id.tv_password_restriction);
        tvBackArrow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnContinue.setEnabled(false);
        edtPassword.requestFocus();
        /*InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtPassword, InputMethodManager.SHOW_FORCED);*/
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        edtPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtPassword.requestFocus();
                imm.showSoftInput(edtPassword, 0);
            }
        }, 100);

        initTextChangeListener();
    }

    private void initTextChangeListener() {
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validator.isValidatePwd(edtPassword.getText().toString().trim())) {
                    btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                    btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    btnContinue.setEnabled(true);

                    tv_password_restriction.setText(getResources().getString(R.string.password_valid));
                    tv_password_restriction.setTextColor(getResources().getColor(R.color.btn_green));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        edtPassword.setBackgroundTintList(getResources().getColorStateList(R.color.btn_green));
                    }

                } else {
                    btnContinue.setBackgroundResource(R.drawable.oval_btn_gray);
                    btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_btn_text));
                    btnContinue.setEnabled(false);

                    tv_password_restriction.setText(getResources().getString(R.string.password_restriction));
                    tv_password_restriction.setTextColor(getResources().getColor(R.color.text_light_gray));
                   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        edtPassword.setBackgroundTintList(getResources().getColorStateList(R.color.red));
                    }*/
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
            case R.id.tv_left_arrow:
                mActivity.onBackPressed();
                break;
            case R.id.btn_continue:
                mActivity.putHashMap("password", edtPassword.getText().toString().trim());
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
