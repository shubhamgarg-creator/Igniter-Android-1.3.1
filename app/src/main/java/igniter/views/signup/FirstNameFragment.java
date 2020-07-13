package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category FirstNameFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;

import com.obs.CustomButton;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import igniter.R;
import igniter.configs.Constants;
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.Enums;

/*****************************************************************
 Signup user first name page
 ****************************************************************/

public class FirstNameFragment extends Fragment implements View.OnClickListener {

    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;

    private CustomTextView tvBackArrow;
    private CustomButton btnContinue;
    private CustomEditText edtFirstName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.first_name_fragment, container, false);
            initView();
        }

        return view;
    }

    private void initView() {
        tvBackArrow = (CustomTextView) view.findViewById(R.id.tv_left_arrow);
        btnContinue = (CustomButton) view.findViewById(R.id.btn_continue);
        edtFirstName = (CustomEditText) view.findViewById(R.id.edt_first_name);

        tvBackArrow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnContinue.setEnabled(false);
        edtFirstName.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        edtFirstName.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtFirstName.requestFocus();
                imm.showSoftInput(edtFirstName, 0);
            }
        }, 100);
        initTextChangeListener();
    }

    private void initTextChangeListener() {
        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                    btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    btnContinue.setEnabled(true);
                } else {
                    btnContinue.setBackgroundResource(R.drawable.oval_btn_gray);
                    btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_btn_text));
                    btnContinue.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
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
                mActivity.putHashMap("first_name", edtFirstName.getText().toString().trim());
                mActivity.changeFragment(Enums.LAST_NAME, null, false);
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
}
