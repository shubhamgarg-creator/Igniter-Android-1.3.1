package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category LastNameFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.obs.CustomButton;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import igniter.R;
import igniter.configs.Constants;
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.Enums;

/*****************************************************************
 Signup user LastName page
 ****************************************************************/

public class LastNameFragment extends Fragment implements View.OnClickListener {

    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;

    private CustomTextView tvBackArrow, tvLastNameTitle;
    private CustomButton btnContinue;
    private CustomEditText edtLastName;

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
        edtLastName = (CustomEditText) view.findViewById(R.id.edt_first_name);
        edtLastName.requestFocus();
        tvLastNameTitle = (CustomTextView) view.findViewById(R.id.tv_first_name_title);
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(edtLastName, InputMethodManager.SHOW_IMPLICIT);
        tvLastNameTitle.setText(res.getString(R.string.last_name_title));
        edtLastName.setHint(res.getString(R.string.last_name));

        tvBackArrow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnContinue.setEnabled(false);
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        edtLastName.postDelayed(new Runnable() {
            @Override
            public void run() {
                edtLastName.requestFocus();
                imm.showSoftInput(edtLastName, 0);
            }
        }, 100);
        initTextChangeListener();
    }

    private void initTextChangeListener() {
        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
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
                mActivity.putHashMap("last_name", edtLastName.getText().toString().trim());
                mActivity.changeFragment(Enums.BIRTHDAY, null, false);
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
