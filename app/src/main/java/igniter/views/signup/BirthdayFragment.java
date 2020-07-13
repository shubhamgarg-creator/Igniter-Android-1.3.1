package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category BirthdayFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;

import com.obs.CustomButton;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import java.util.Calendar;
import java.util.HashMap;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.SessionManager;
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.Enums;

/*****************************************************************
 Signup Gender page
 ****************************************************************/

public class BirthdayFragment extends Fragment implements View.OnClickListener {

    @Inject
    SessionManager sessionManager;

    public int currentYear;
    public int day, month1, leapyear;
    public String Days;
    public int year;
    boolean check = false;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private TextWatcher textChangeListener;
    private CustomTextView tvBackArrow, tvBirthdayDesc;
    private StringBuilder sb;
    private CustomButton btnContinue;
    private CustomEditText edtBirthday, edt_code1, edt_code2, edt_code4, edt_code5, edt_code7, edt_code8, edt_code9, edt_code10;
    private int min = 18, max = 100;
    HashMap<String, String> hashMap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.birthday_fragment, container, false);
            initView();
        }
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.showSoftInput(edt_code1, InputMethodManager.SHOW_IMPLICIT);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            min = bundle.getInt("min", 18);
            max = bundle.getInt("max", 100);

            tvBirthdayDesc.setText(String.format(getString(R.string.birthday_description), min));

            hashMap = (HashMap<String, String>) bundle.getSerializable("map");
            if (hashMap != null)
                Log.v("HashMapTest", hashMap.get("auth_id"));
        }

    }

    private void initView() {
        tvBirthdayDesc = (CustomTextView) view.findViewById(R.id.tv_birthday_description);
        tvBackArrow = (CustomTextView) view.findViewById(R.id.tv_left_arrow);
        btnContinue = (CustomButton) view.findViewById(R.id.btn_continue);
        edtBirthday = (CustomEditText) view.findViewById(R.id.edt_birthday);
        edt_code1 = (CustomEditText) view.findViewById(R.id.edt_code1);
        edt_code2 = (CustomEditText) view.findViewById(R.id.edt_code2);
        edt_code4 = (CustomEditText) view.findViewById(R.id.edt_code4);
        edt_code5 = (CustomEditText) view.findViewById(R.id.edt_code5);
        edt_code7 = (CustomEditText) view.findViewById(R.id.edt_code7);
        edt_code8 = (CustomEditText) view.findViewById(R.id.edt_code8);
        edt_code9 = (CustomEditText) view.findViewById(R.id.edt_code9);
        edt_code10 = (CustomEditText) view.findViewById(R.id.edt_code10);

        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        edt_code1.postDelayed(new Runnable() {
            @Override
            public void run() {
                edt_code1.requestFocus();
                imm.showSoftInput(edt_code1, 0);
            }
        }, 100);


        tvBackArrow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        btnContinue.setEnabled(false);

        initTextChangeListener();
        //initTextChanger();
        edt_code1.addTextChangedListener(new NameTextWatcher(edt_code1));
        edt_code2.addTextChangedListener(new NameTextWatcher(edt_code2));
        edt_code4.addTextChangedListener(new NameTextWatcher(edt_code4));
        edt_code5.addTextChangedListener(new NameTextWatcher(edt_code5));
        edt_code7.addTextChangedListener(new NameTextWatcher(edt_code7));
        edt_code8.addTextChangedListener(new NameTextWatcher(edt_code8));
        edt_code9.addTextChangedListener(new NameTextWatcher(edt_code9));
        edt_code10.addTextChangedListener(new NameTextWatcher(edt_code10));
        delKey();
        requesteditfocus();

    }

    private void delKey() {
        edt_code2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edt_code2.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edt_code2.getText().toString().length() == 0) {
                    edt_code1.requestFocus();
                    edt_code1.getText().clear();
                }
                return false;
            }
        });
        edt_code4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edt_code4.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edt_code4.getText().toString().length() == 0) {
                    edt_code2.requestFocus();
                    edt_code2.getText().clear();
                }
                return false;
            }
        });
        edt_code5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edt_code5.requestFocus();

                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edt_code5.getText().toString().length() == 0) {
                    edt_code4.requestFocus();
                    edt_code4.getText().clear();
                }
                return false;
            }
        });
        edt_code7.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edt_code7.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edt_code7.getText().toString().length() == 0) {
                    edt_code5.requestFocus();
                    edt_code5.getText().clear();
                }
                return false;
            }
        });
        edt_code8.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edt_code8.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edt_code8.getText().toString().length() == 0) {
                    edt_code7.requestFocus();
                    edt_code7.getText().clear();
                }
                return false;
            }
        });
        edt_code9.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edt_code9.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edt_code9.getText().toString().length() == 0) {
                    edt_code8.requestFocus();
                    edt_code8.getText().clear();
                }
                return false;
            }
        });
        edt_code10.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_


                //this is for backspace
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                    edt_code10.requestFocus();
                }
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                        && edt_code10.getText().toString().length() == 0) {
                    edt_code9.requestFocus();
                    edt_code9.getText().clear();
                }

                //}
                return false;
            }
        });
    }

    private void requesteditfocus() {
        edt_code1.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (edt_code1.getText().toString().equals("")) {
                    edt_code1.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code1, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code2.getText().toString() != "") {
                    edt_code2.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code2, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
            }
        });

        edt_code4.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (edt_code4.getText().toString().equals("")) {
                    edt_code4.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code4, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code5.getText().toString() != "") {
                    edt_code5.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code5, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
            }
        });


        edt_code7.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (edt_code7.getText().toString().equals("")) {
                    edt_code7.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code7, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code8.getText().toString().equals("")) {

                    edt_code8.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code8, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code9.getText().toString().equals("")) {
                    edt_code9.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code10, InputMethodManager.SHOW_IMPLICIT);

                } else if (!edt_code10.getText().toString().equals("")) {

                    edt_code10.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code10, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
            }
        });


        edt_code8.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (edt_code7.getText().toString().equals("")) {
                    edt_code7.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code7, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code8.getText().toString().equals("")) {

                    edt_code8.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code8, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code9.getText().toString().equals("")) {

                    edt_code9.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code9, InputMethodManager.SHOW_IMPLICIT);
                } else if (!edt_code10.getText().toString().equals("")) {

                    edt_code10.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code10, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
            }
        });

        edt_code9.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (edt_code7.getText().toString().equals("")) {

                    edt_code7.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code7, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code8.getText().toString().equals("")) {

                    edt_code8.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code8, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code9.getText().toString().equals("")) {

                    edt_code9.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code9, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code10.getText().toString() != "") {
                    edt_code10.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code10, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
            }
        });

        edt_code10.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (edt_code7.getText().toString().equals("")) {

                    edt_code7.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code7, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code8.getText().toString().equals("")) {

                    edt_code8.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code8, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code9.getText().toString().equals("")) {

                    edt_code9.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code9, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code10.getText().toString() == "") {
                    edt_code10.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code10, InputMethodManager.SHOW_IMPLICIT);
                } else if (edt_code10.getText().toString() != "") {
                    edt_code10.requestFocus();
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.showSoftInput(edt_code10, InputMethodManager.SHOW_IMPLICIT);
                }
                return true;
            }
        });
    }

    public int firstDigit(int x) {
        if (x == 0) return 0;
        x = Math.abs(x);
        return (int) Math.floor(x / Math.pow(10, Math.floor(Math.log10(x))));
    }

    String getSecondDigit(int number) {
        String second = Integer.toString(number);
        second = second.substring(1, 2);

        return second;
    }

    String getThirdDigit(int number) {
        String third = Integer.toString(number);
        third = third.substring(2, 3);

        return third;
    }

    String getFourthDigit(int number) {
        String third = Integer.toString(number);
        third = third.substring(3, 4);

        return third;
    }

    private void isValidDob() {
        String edtcode1 = edt_code1.getText().toString();
        String edtcode2 = edt_code2.getText().toString().trim();
        String edtcode4 = edt_code4.getText().toString().trim();
        String edtcode5 = edt_code5.getText().toString().trim();
        String edtcode7 = edt_code7.getText().toString().trim();
        String edtcode8 = edt_code8.getText().toString().trim();
        final String edtcode9 = edt_code9.getText().toString().trim();
        String edtcode10 = edt_code10.getText().toString().trim();
        sb = new StringBuilder();
        if (!TextUtils.isEmpty(edtcode1)) {
            sb.append(edtcode1);
        } else {
            edt_code1.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(edtcode2)) {
            sb.append(edtcode2);
        } else {
            edt_code2.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(edtcode4)) {
            sb.append(edtcode4);
        } else {
            edt_code4.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(edtcode5)) {
            sb.append(edtcode5);
        } else {
            edt_code5.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(edtcode7)) {
            sb.append(edtcode7);
        } else {
            edt_code7.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(edtcode8)) {
            sb.append(edtcode8);
        } else {
            edt_code8.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(edtcode9)) {
            sb.append(edtcode9);
        } else {
            edt_code9.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(edtcode10)) {
            sb.append(edtcode10);
        } else {
            edt_code10.requestFocus();
            return;
        }

        /*//if ((edtcode1!="")&&(edtcode2!="")&&(edtcode4!="")&&(edtcode5!="")&&(edtcode7!="")&&(edtcode8!="")&&(edtcode9!="")&&(edtcode10!="")) {
        if(sb.length()==8){

        }else{

        }*/

    }
   /* private void initTextChanger() {
        textChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();

                switch(view.getId()){
                    case R.id.edt_code1:
                        break;
                    case R.id.edt_code2:

                        break;
                    case R.id.edt_code4:

                        break;
                    case R.id.edt_code5:

                        break;
                    case R.id.edt_code7:

                        break;
                    case R.id.edt_code8:

                        break;
                    case R.id.edt_code9:

                        break;
                    case R.id.edt_code10:

                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isValidDob();
            }
        };
    }*/

    private void initTextChangeListener() {
        edtBirthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String working = s.toString();
                boolean isValid = true;
                try {
                    /*day = Integer.parseInt(working.substring(0, 2));
                    month1 = Integer.parseInt(working.substring(3, 5));
                    if (day >= 29 &&month1 ==2 && leapyear != 0) {
                        edtBirthday.setText(working.substring(0, 1)+working.substring(0, 1));
                    }*/
                    if (working.length() == 2 && before == 0) {
                        if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 31) {
                            edtBirthday.setText(working.substring(0, 1));
                            edtBirthday.setSelection(1);
                            isValid = false;
                        } else {
                            isValid = false;
                            day = Integer.parseInt(working);
                            day = Integer.parseInt(working);
                            /*if (day >= 29&&month1==2 && leapyear != 0) {

                                edtBirthday.setText("");
                            } else{*/

                            working += "/";
                            edtBirthday.setText(working);
                            edtBirthday.setSelection(working.length());
                            //}
                        }
                    } else if (working.length() == 5 && before == 0) {
                        String month = working.substring(3);
                        if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
                            isValid = false;
                            edtBirthday.setText(working.substring(0, 4));
                            edtBirthday.setSelection(4);
                        } else {
                            month1 = Integer.parseInt(working.substring(3, 5));
                            if (month1 == 2 && day > 29) {
                                edtBirthday.setText(working.substring(0, 4));
                                edtBirthday.setSelection(4);
                                isValid = false;
                            } else if (day > 30 && (month1 == 2 || month1 == 4 || month1 == 6 || month1 == 9 || month1 == 11)) {
                                edtBirthday.setText(working.substring(0, 4));
                                edtBirthday.setSelection(4);
                                isValid = false;
                            } else {
                                isValid = false;
                                working += "/";
                                edtBirthday.setText(working);
                                edtBirthday.setSelection(working.length());
                            }
                        }
                    } else if (working.length() == 10 && before == 0) {
                        String enteredYear = working.substring(6);
                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        leapyear = Integer.parseInt(enteredYear) % 4;
                        if (day >= 29 && month1 == 2 && leapyear != 0) {
                            edtBirthday.setText(working.substring(0, 9));
                            edtBirthday.setSelection(9);
                            isValid = false;
                        } else {
                            if (Integer.parseInt(enteredYear) > currentYear - min) {
                                isValid = false;

                                edtBirthday.setText(working.substring(0, 9));
                                edtBirthday.setSelection(9);
                            }
                        }
                    } else if (working.length() != 10) {
                        isValid = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*if (!isValid) {
                    btnContinue.setEnabled(false);
                    btnContinue.setBackgroundResource(R.drawable.oval_btn_gray);
                    btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_btn_text));
                } else {
                    btnContinue.setEnabled(true);
                    btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                    btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));


                }*/
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
                String dob = sb.substring(0, 2) + "-" + sb.substring(2, 4) + "-" + sb.substring(4, 8);
                if (sessionManager.getIsFBUser()) {
                    hashMap.put("dob", dob);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("map", hashMap);
                    mActivity.changeFragment(Enums.GENDER, bundle, false);
                }else {
                    mActivity.putHashMap("dob", dob);
                    mActivity.changeFragment(Enums.GENDER, null, false);
                }
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

    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String working = charSequence.toString();
            check = false;
            switch (view.getId()) {
                case R.id.edt_code1:
                    try {
                        if (Integer.parseInt(working) == 3 && Integer.parseInt(edt_code4.getText().toString()) == 0 && Integer.parseInt(edt_code5.getText().toString()) == 2) {

                            edt_code1.setText("");

                        } else {
                            if (Integer.parseInt(working) == 0 || Integer.parseInt(working) == 1 || Integer.parseInt(working) == 2 || Integer.parseInt(working) == 3) {
                                //edt_code1.setText(working);
                            } else {
                                edt_code1.setText("");
                            }
                        }


                    } catch (NumberFormatException nfe) {

                    }
                    break;
                case R.id.edt_code2:

                    try {
                        if (Integer.parseInt(edt_code1.getText().toString()) == 3) {


                            if (edt_code4.getText().toString().equals("") && edt_code5.getText().toString().equals("") && Integer.parseInt(edt_code1.getText().toString()) == 3 && (Integer.parseInt(working) == 0 || Integer.parseInt(working) == 1)) {
                                //edt_code2.setText(working);
                            } else if (Integer.parseInt(edt_code1.getText().toString()) == 3 && Integer.parseInt(working) == 1 && Integer.parseInt(edt_code4.getText().toString()) == 0 && (Integer.parseInt(edt_code5.getText().toString()) == 0 || Integer.parseInt(edt_code5.getText().toString()) == 2 || Integer.parseInt(edt_code5.getText().toString()) == 4 || Integer.parseInt(edt_code5.getText().toString()) == 6 || Integer.parseInt(edt_code5.getText().toString()) == 9 || (Integer.parseInt(edt_code4.getText().toString()) == 1 && Integer.parseInt(edt_code5.getText().toString()) == 1))) {
                                edt_code2.setText("");
                            } else if (Integer.parseInt(edt_code1.getText().toString()) == 3 && (Integer.parseInt(working) == 0 || Integer.parseInt(working) == 1)) {
                                //edt_code2.setText("");
                            } else {
                                edt_code2.setText("");
                            }
                        } else if ((Integer.parseInt(edt_code1.getText().toString()) == 2) && (Integer.parseInt(working) == 9) && (Integer.parseInt(edt_code4.getText().toString()) == 0) && (Integer.parseInt(edt_code5.getText().toString()) == 2) && (year % 4) != 0) {

                            edt_code2.setText("");

                        } else {
                            //edt_code2.setText("working);
                        }

                        if (edt_code1.getText().toString().equals("0") && edt_code2.getText().toString().equals("0")) {
                            edt_code2.setText("");
                        }

                    } catch (NumberFormatException nfe) {

                    }
                    break;
                case R.id.edt_code4:
                    try {
                        if (Integer.parseInt(working) == 0 || Integer.parseInt(working) == 1) {
                        } else {
                            edt_code4.setText("");
                        }
                    } catch (NumberFormatException nfe) {


                    }
                    break;
                case R.id.edt_code5:
                    try {
                        if (Integer.parseInt(edt_code1.getText().toString()) == 3 && Integer.parseInt(edt_code4.getText().toString()) == 0 && Integer.parseInt(working) == 2) {
                            edt_code5.setText("");

                        } else if (Integer.parseInt(edt_code1.getText().toString()) == 3 && Integer.parseInt(edt_code2.getText().toString()) == 1 && Integer.parseInt(edt_code4.getText().toString()) == 0 && (Integer.parseInt(working) == 0 || Integer.parseInt(working) == 2 || Integer.parseInt(working) == 4 || Integer.parseInt(working) == 6 || Integer.parseInt(working) == 9 || (Integer.parseInt(edt_code4.getText().toString()) == 1 && Integer.parseInt(working) == 1))) {
                            edt_code5.setText("");

                        } else if (Integer.parseInt(edt_code1.getText().toString()) == 3 && Integer.parseInt(edt_code2.getText().toString()) == 1 && Integer.parseInt(edt_code4.getText().toString()) == 1 && (Integer.parseInt(edt_code4.getText().toString()) == 1 && Integer.parseInt(working) == 1)) {
                            edt_code5.setText("");
                        } else if (Integer.parseInt(working) > 2 && Integer.parseInt(edt_code4.getText().toString()) == 1) {
                            edt_code5.setText("");
                        } else if ((Integer.parseInt(edt_code1.getText().toString()) == 2) && (Integer.parseInt(edt_code2.getText().toString()) == 9) && (Integer.parseInt(edt_code4.getText().toString()) == 0) && (Integer.parseInt(working) == 2) && (year % 4) != 0) {
                            edt_code5.setText("");
                        } else {
                            //edt_code5.setText(working);

                        }

                        if (edt_code4.getText().toString().equals("0") && edt_code5.getText().toString().equals("0")) {
                            edt_code5.setText("");
                        }
                    } catch (NumberFormatException nfe) {

                    }
                    break;
                case R.id.edt_code7:
                    try {
                        currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        int AgeYear = currentYear - min;
                        int minYear = currentYear - max;

                        int firstcurr = firstDigit(AgeYear);
                        int firstmin = firstDigit(minYear);

                        if ((Integer.parseInt(working) == firstmin) || (Integer.parseInt(working) == firstcurr)) {

                        } else {
                            edt_code7.setText("");
                        }
                        /*if(Integer.parseInt(working)==1||Integer.parseInt(working)==2){
                            //edt_code7.setText(working);
                        }else{
                            edt_code7.setText("");
                        }*/
                    } catch (NumberFormatException nfe) {

                    }
                    break;
                case R.id.edt_code8:
                    try {
                        currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        int AgeYear = currentYear - min;
                        int MinYear = currentYear - max;
                        String ageyear = getSecondDigit(AgeYear);
                        String minyear = getSecondDigit(MinYear);
                        int secondcurr = Integer.parseInt(ageyear);
                        int secondmin = Integer.parseInt(minyear);

                        if ((Integer.parseInt(working) >= secondmin) && Integer.parseInt(edt_code7.getText().toString()) == 1 || ((Integer.parseInt(working) <= secondcurr) && Integer.parseInt(edt_code7.getText().toString()) == 2)) {

                        } else {
                            edt_code8.setText("");
                        }

                      /*  if(Integer.parseInt(working)==9&&Integer.parseInt(edt_code7.getText().toString())==1){
                            //edt_code8.setText(working);
                        }else{
                            edt_code8.setText("");
                        }*/
                    } catch (NumberFormatException nfe) {

                    }
                    break;
                case R.id.edt_code9:
                    try {
                        currentYear = Calendar.getInstance().get(Calendar.YEAR);
                        int AgeYear = currentYear - min;
                        int MinYear = currentYear - max;
                        //third digit year

                        String ageyear = getThirdDigit(AgeYear);
                        String minyear = getThirdDigit(MinYear);
                        int thirdcurr = Integer.parseInt(ageyear);
                        int thirdmin = Integer.parseInt(minyear);

                        //first digit year
                        int firstcurr = firstDigit(AgeYear);
                        int firstmin = firstDigit(MinYear);

                        //second digit year
                        String ageyear2 = getSecondDigit(AgeYear);
                        String minyear2 = getSecondDigit(MinYear);
                        int secondcurr = Integer.parseInt(ageyear2);
                        int secondmin = Integer.parseInt(minyear2);

                        if ((Integer.parseInt(edt_code7.getText().toString()) >= firstmin && Integer.parseInt(edt_code8.getText().toString()) >= secondmin && Integer.parseInt(working) >= thirdmin) || (Integer.parseInt(edt_code7.getText().toString()) >= firstcurr && Integer.parseInt(edt_code8.getText().toString()) >= secondcurr && Integer.parseInt(working) <= thirdcurr)) {


                        } else {
                            edt_code9.setText("");
                        }

//                        if((Integer.parseInt(working)>=thirdmin)&&Integer.parseInt(working)<=thirdcurr){
//
//                        }else{
//                            edt_code9.setText("");
//                        }

//                        if(Integer.parseInt(edt_code7.getText().toString())==1&&Integer.parseInt(edt_code8.getText().toString())==9&&Integer.parseInt(working)==0){
//                            edt_code9.setText("");
//                        }else if(Integer.parseInt(edt_code7.getText().toString())==2&&Integer.parseInt(edt_code8.getText().toString())==0&&Integer.parseInt(working)>1){
//                            edt_code9.setText("");
//                        }
                    } catch (NumberFormatException nfe) {

                    }
                    break;
                case R.id.edt_code10:
                    if (working.length() > 0) {
                        try {

                            currentYear = Calendar.getInstance().get(Calendar.YEAR);
                            int AgeYear = currentYear - min;
                            //int MinYear = AgeYear-max;
                            int MinYear = currentYear - max;
                            //third digit year
                            String ageyear = getThirdDigit(AgeYear);
                            String minyear = getThirdDigit(MinYear);
                            int thirdcurr = Integer.parseInt(ageyear);
                            int thirdmin = Integer.parseInt(minyear);

                            //first digit year
                            int firstcurr = firstDigit(AgeYear);
                            int firstmin = firstDigit(MinYear);

                            //second digit year
                            String ageyear2 = getSecondDigit(AgeYear);
                            String minyear2 = getSecondDigit(MinYear);
                            int secondcurr = Integer.parseInt(ageyear2);
                            int secondmin = Integer.parseInt(minyear2);

                            //four digit year
                            String ageyear4 = getFourthDigit(AgeYear);
                            String minyear4 = getFourthDigit(MinYear);
                            int fourthcurr = Integer.parseInt(ageyear4);
                            int fourthmin = Integer.parseInt(minyear4);


                            String s = edt_code7.getText().toString() + edt_code8.getText().toString() + edt_code9.getText().toString() + working;
                            year = Integer.parseInt(s);

                            if ((Integer.parseInt(edt_code1.getText().toString()) == 2) && (Integer.parseInt(edt_code2.getText().toString()) == 9) && (Integer.parseInt(edt_code4.getText().toString()) == 0) && (Integer.parseInt(edt_code5.getText().toString()) == 2) && (year % 4) != 0) {

                                edt_code10.setText("");

                            } else {
                                if ((Integer.parseInt(edt_code7.getText().toString()) >= firstmin && Integer.parseInt(edt_code8.getText().toString()) >= secondmin && Integer.parseInt(edt_code9.getText().toString()) >= thirdmin && Integer.parseInt(working) >= fourthmin) || (Integer.parseInt(edt_code7.getText().toString()) >= firstcurr && Integer.parseInt(edt_code8.getText().toString()) >= secondcurr && Integer.parseInt(edt_code9.getText().toString()) <= thirdcurr && Integer.parseInt(working) <= fourthcurr)) {


                                } else if ((Integer.parseInt(edt_code7.getText().toString()) >= firstmin && Integer.parseInt(edt_code8.getText().toString()) >= secondmin && Integer.parseInt(edt_code9.getText().toString()) > thirdmin) || (Integer.parseInt(edt_code7.getText().toString()) >= firstcurr && Integer.parseInt(edt_code8.getText().toString()) >= secondcurr && Integer.parseInt(edt_code9.getText().toString()) < thirdcurr)) {

                                } else {
                                    edt_code10.setText("");
                                }

                            }


//                        currentYear = Calendar.getInstance().get(Calendar.YEAR);
//                        int AgeYear = currentYear - 18;
//                        int MinYear = AgeYear-82;
//                        String ageyear = getFourthDigit(AgeYear);
//                        String minyear = getFourthDigit(MinYear);
//                        int ageyear1 = firstDigit(AgeYear);
//                        int thirdcurr=Integer.parseInt(ageyear);
//                        int thirdmin=Integer.parseInt(minyear);
//
//                        currentYear = Calendar.getInstance().get(Calendar.YEAR);
//                            String s = edt_code7.getText().toString() + edt_code8.getText().toString() + edt_code9.getText().toString() + working;
//                            int year = Integer.parseInt(s);
//                            if(Integer.parseInt(edt_code2.getText().toString())==9&&Integer.parseInt(edt_code5.getText().toString())==2){
//                            if ((year % 4) == 0) {
//
//                            } else  {
//                                edt_code10.setText("");
//                            }}else{
//
//                            }

                        } catch (NumberFormatException nfe) {

                        }
                    }
                    //if(Integer.parseInt(edt_code7)&&Integer.parseInt(edt_code8)&&Integer.parseInt(edt_code9))
                    break;
            }

        }


        public void afterTextChanged(Editable editable) {


            if ((!edt_code1.getText().toString().equals("")) && (!edt_code2.getText().toString().equals("")) && (!edt_code4.getText().toString().equals("")) && (!edt_code5.getText().toString().equals("")) && (!edt_code7.getText().toString().equals("")) && (!edt_code8.getText().toString().equals("")) && (!edt_code9.getText().toString().equals("")) && (!edt_code10.getText().toString().equals(""))) {
                btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                btnContinue.setEnabled(true);
            } else {
                btnContinue.setEnabled(false);
                btnContinue.setBackgroundResource(R.drawable.oval_btn_gray);
                btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_btn_text));
            }

            isValidDob();

           /* switch (view.getId()) {
                case R.id.edt_code1:
                    if(check)
                    {
                        edt_code1.setText(editable);
                    }else {
                        edt_code1.setText("");
                    }

            }*/

        }
    }
}
