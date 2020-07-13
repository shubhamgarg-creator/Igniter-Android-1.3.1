package igniter.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category Enums
 * @author Trioangle Product Team
 * @version 1.0
 **/

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*****************************************************************
 Enums
 ****************************************************************/

public class Enums {
    public static final String MY_PHONE_NUMBER = "igniter.views.signup.PhoneNumberFragment";
    public static final String ONE_TIME_PWD = "igniter.views.signup.OneTimePwdFragment";
    public static final String GENDER = "igniter.views.signup.GenderFragment";
    public static final String FIRST_NAME = "igniter.views.signup.FirstNameFragment";
    public static final String LAST_NAME = "igniter.views.signup.LastNameFragment";
    public static final String PASSWORD = "igniter.views.signup.PasswordFragment";
    public static final String BIRTHDAY = "igniter.views.signup.BirthdayFragment";
    public static final String EMAIL = "igniter.views.signup.EmailFragment";
    public static final String PROFILE_PICK = "igniter.views.signup.ProfilePickFragment";

    public static final int REQ_GET_LOGIN_SLIDER = 100;
    public static final int REQ_FB_SIGNUP = 101;
    public static final int REQ_VERIFY_NUMBER = 102;
    public static final int REQ_NUMBER_SIGNUP = 103;
    public static final int REQ_GET_EDIT_PROFILE = 104;
    public static final int REQ_UPLOAD_PROFILE_IMG = 105;
    public static final int REQ_UPDATE_PROFILE = 106;
    public static final int REQ_GET_MY_PROFILE = 107;
    public static final int REQ_GET_SETTINGS = 108;
    public static final int REQ_UPDATE_SETTINGS = 109;
    public static final int REQ_GET_HOME = 110;
    public static final int REQ_GET_OTHER_PROFILE = 111;
    public static final int REQ_GET_CHAT = 112;
    public static final int REQ_GET_MATCH_LIST = 113;
    public static final int REQ_SWIPE_MATCH = 114;
    public static final int REQ_UPDATE_LOCATION = 115;
    public static final int REQ_SHOW_ALL_MATCHES = 116;
    public static final int REQ_LOGOUT = 117;
    public static final int REQ_MSG_CONVERSATION = 118;
    public static final int REQ_SEND_MSG = 119;
    public static final int REQ_UPDATE_DEVICE_ID = 120;
    public static final int REQ_UNMATCH_DETAILS = 121;
    public static final int REQ_UNMATCH_USER = 122;
    public static final int REQ_REMOVE_IMAGE = 123;
    public static final int REQ_ADD_LOCATION = 124;
    public static final int REQ_UPDATE_DEFAULT_LOCATION = 124;
    public static final int REQ_UPDATE_TRANSACTION = 125;
    public static final int REQ_GET_PLAN = 126;
    public static final int REQ_UPDATE_PLUS_SETTINGS = 127;
    public static final int REQ_GET_PLUS_SETTINGS = 128;
    public static final int REQ_UPDATE_BOOST_USER = 129;
    public static final int REQ_REPORT_DETAILS = 130;
    public static final int REQ_REPORT_DETAILS_PROFILE = 131;
    public static final int REQ_BLOCK_DETAILS = 132;
    public static final int REQ_SEND_OTP = 133;
    public static final int REQ_SWIPE_USER = 134;
    public static final int REQ_LIKED_USER = 135;
    public static final int REQ_SWIPE_ENLARGE = 136;


    public static final String MATCH_LIKE = "like";
    public static final String MATCH_SUPER_LIKE = "super like";
    public static final String MATCH_NOPE = "nope";
    public static final String MATCH_REWIND = "rewind";

    @StringDef({MY_PHONE_NUMBER, ONE_TIME_PWD, GENDER, FIRST_NAME, LAST_NAME, PASSWORD, EMAIL, BIRTHDAY, PROFILE_PICK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Frag {
    }

    @IntDef({REQ_GET_LOGIN_SLIDER, REQ_FB_SIGNUP, REQ_VERIFY_NUMBER, REQ_NUMBER_SIGNUP, REQ_GET_EDIT_PROFILE, REQ_UPLOAD_PROFILE_IMG,
            REQ_UPDATE_PROFILE, REQ_GET_MY_PROFILE, REQ_GET_SETTINGS, REQ_UPDATE_SETTINGS, REQ_GET_HOME, REQ_GET_OTHER_PROFILE, REQ_GET_CHAT, REQ_GET_MATCH_LIST,
            REQ_SWIPE_MATCH, REQ_UPDATE_LOCATION, REQ_SHOW_ALL_MATCHES, REQ_LOGOUT, REQ_MSG_CONVERSATION, REQ_SEND_MSG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestType {
    }

    @StringDef({MATCH_LIKE, MATCH_SUPER_LIKE, MATCH_NOPE, MATCH_REWIND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MatchType {
    }
}
