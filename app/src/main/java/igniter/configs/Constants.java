package igniter.configs;
/**
 * @package com.trioangle.igniter
 * @subpackage configs
 * @category Constants
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;

/*****************************************************************
 Constants
 ****************************************************************/
public class Constants {

    // Test server IP address Full
    public static final String CLIENT_ID = "14fa50fa2ea44c408fb1134ebc1c2721";
    public static final String REDIRECT_URL = "http://www.igniter.com";

    public static final String APP_NAME = "Igniter";
    public static final String FILE_NAME = "igniter";
    public static final String CAMERA_ROLL = "Camera Roll";
    public static final String ALL_IMAGES = "All Images";
    public static final String ALL_VIDEOS = "All Videos";
    public static final String[] SUPPORTED_VIDEO_FORMAT = {"MP4", "3GP", "AVI", "FLV", "MPEG-2"};


    public static final int ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT = 102;
    public static final String NUMBER_VALIDATION_API_RESULT_OLD_USER = "1";
    public static final String NUMBER_VALIDATION_API_RESULT_NEW_USER = "0";
    public static final int FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER = 157; // Number declared randomly, not specifically
    public static final int FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER = 158; // Number declared randomly, not specifically

    public static final String FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY="message";
    public static final String FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY="phoneNumber";
    public static final String FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY="countryCode";

    public static final String VP_LOGIN_SLIDER = "Login_Slider";
    public static final String VP_GET_PLUS_SLIDER = "Igniter_Plus_Slider";
    public static final String VP_GET_GOLD_SLIDER = "Igniter_Gold_Slider";

    public static final String STATUS_MSG = "status_message";
    public static final String STATUS_CODE = "status_code";
    public static final String OTP = "otp";
    public static final String TOKEN = "token";
    public static final String ALREADY_USER = "already_user";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String IMAGE_URL = "user_image_url";
    public static final String REFRESH_ACCESS_TOKEN = "refresh_token";
    public static final String MATCH_STATUS = "match_status";

    public static final String REMAINING_SUPER_LIKE = "remaining_slikes_count";
    public static final String REMAINING_LIKE = "remaining_likes_count";
    public static final String REMAINING_BOOST = "remaining_boost_count";
    public static final String ISLIKELIMITED = "is_likes_limited";

    public static final String SUBSCRIPTION_ID = "used_subscription_id";
    public static final String isOrder = "is_order";
    public static final String PlanType = "plan_type";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final int REQUEST_CODE_GALLERY = 5;

    public static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            //Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final String[] PERMISSIONS_PHOTO = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    public static final String[] PERMISSIONS_PHONE_STATE = {
            //Manifest.permission.READ_PHONE_STATE,
    };
    public static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
    public static final SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public static final SimpleDateFormat chatUtcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat galleryDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
    public static final String listenerSignUpException = "must implement SignUpActivityListener";
    public static final String listenerHomeException = "must implement ActivityListener";
    public static final String imageUrl = "http://smash.trioangle.com/igniter/public/image/users/%s/%s";
    public static final String userName = "http://www.";
    public static long mLastClickTime = 0;
    public static boolean isDisableFragmentAnimations = false;
}
