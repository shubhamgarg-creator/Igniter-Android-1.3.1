package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category LoginActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.obs.CustomTextView;
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleCallback;
import com.willowtreeapps.signinwithapplebutton.SignInWithAppleConfiguration;
import com.willowtreeapps.signinwithapplebutton.view.BaseUrl;
import com.willowtreeapps.signinwithapplebutton.view.SignInWithAppleButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import igniter.BuildConfig;
import igniter.R;
import igniter.adapters.main.ViewPagerAdapter;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.JSONParser;
import igniter.configs.SessionManager;
import igniter.datamodels.main.ImageListModel;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.SliderModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.RequestCallback;
import igniter.views.customize.CirclePageIndicator;
import igniter.views.customize.CustomDialog;
import igniter.views.signup.SignUpActivity;

import static android.text.Html.fromHtml;
import static igniter.utils.Enums.REQ_FB_SIGNUP;
import static igniter.utils.Enums.REQ_GET_LOGIN_SLIDER;

/*****************************************************************
 User Login Activity
 ****************************************************************/
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener {

    JSONParser jsonparser = new JSONParser();
    JSONObject jobj = null;
    String firstName, lastName, fbId, fbPhone, socialEmail, fbGender, fbProfileImg, fbProfileImg1, fbWork, fbEducation, fbAge, fbjob_title;
    HashMap<String, String> hashMap = new HashMap<>();
    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiService apiService;
    @Inject
    CustomDialog customDialog;
    @Inject
    Gson gson;
    private ViewPagerAdapter PagerAdapter;
    private ViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    // private CustomTextView tvDotNotPostFb,tvTakePolicy,  tvLocationMsg ;
    private CustomTextView tvArrowTop, tvArrowBottom, tvLoginFb, tvLoginPhone, tvTermsPolicy;
    private RelativeLayout rltTutorial;
    private LinearLayout lltLoginBottom, arrow_bottom, signup_more, signup_less;
    private AlertDialog dialog;
    private ArrayList<ImageListModel> imageList = new ArrayList<>();
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private SignInWithAppleButton btnapplelogin;
    private String appleClientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);
        setContentView(R.layout.login_activity);

        getFbKeyHash(getApplicationContext().getResources().getString(R.string.package_name));
        initView();
        getSliderImageList();
        initViewPagerListener();
        initFacebookLogin();
        sessionManager.setIsFBUser(false);
        sessionManager.setIsAppleUser(false);
        onAppleSignIn();
    }

    /**
     * objectCreation method is used to create all objects.
     */
    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        tvLoginFb = (CustomTextView) findViewById(R.id.tv_login_facebook);
        tvLoginPhone = (CustomTextView) findViewById(R.id.tv_login_phone);

        tvArrowBottom = (CustomTextView) findViewById(R.id.tv_arrow_bottom);
        tvArrowTop = (CustomTextView) findViewById(R.id.tv_arrow_top);
        tvTermsPolicy = (CustomTextView) findViewById(R.id.tv_terms_condition);
       /* tvTakePolicy = (CustomTextView) findViewById(R.id.tv_take_policy);

        tvLocationMsg = (CustomTextView) findViewById(R.id.tv_location_not_shown);
        tvDotNotPostFb = (CustomTextView) findViewById(R.id.tv_do_not_fb);*/

        rltTutorial = (RelativeLayout) findViewById(R.id.rlt_tutorial);
        lltLoginBottom = (LinearLayout) findViewById(R.id.llt_login_bottom);
        arrow_bottom = (LinearLayout) findViewById(R.id.arrow_bottom);
        signup_less = (LinearLayout) findViewById(R.id.signup_less);
        signup_more = (LinearLayout) findViewById(R.id.signup_more);
        btnapplelogin = findViewById(R.id.btnapplelogin);
        dialog = commonMethods.getAlertDialog(this);

        tvLoginPhone.setOnClickListener(this);
        tvLoginFb.setOnClickListener(this);
        tvArrowBottom.setOnClickListener(this);
        tvArrowTop.setOnClickListener(this);
        arrow_bottom.setOnClickListener(this);

       /* tvDotNotPostFb.setTextSize(14);
        tvLocationMsg.setTextSize(14);
        tvTermsPolicy.setTextSize(14);*/

        if (Build.VERSION.SDK_INT >= 24) {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy), 0));
            tvTermsPolicy.setTextColor(getResources().getColor(R.color.light_gray));
        } else {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy)));
            tvTermsPolicy.setTextColor(getResources().getColor(R.color.light_gray));

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

        ClickableSpan PrivacyPolicy = new ClickableSpan() {
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

        makeLinks(tvTermsPolicy, new String[]{getString(R.string.terms_service), getString(R.string.privacy_policy)}, new ClickableSpan[]{
                termsOfServicesClick, PrivacyPolicy
        });
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

    private void initFacebookLogin() {
        sessionManager.setUserId(0);
        //AppEventsLogger.activateApp(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.e("accessToken", String.valueOf(accessToken.getToken()));
                getFacebookUserProfile(accessToken);
                Log.e("login result", String.valueOf(loginResult.getAccessToken()));
            }

            @Override
            public void onCancel() {
                System.out.println("On cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    private void getFacebookUserProfile(final AccessToken accessToken) {
        final GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject jsonObject = response.getJSONObject();
                try {

                    Log.e("jsonObject", String.valueOf(jsonObject));
                    firstName = jsonObject.getString("first_name");
                    lastName = jsonObject.getString("last_name");
                    fbId = jsonObject.getString("id");
                    fbGender = "";
                    socialEmail = "";
                    fbPhone = "";
                    fbProfileImg1 = "";
                    fbProfileImg = "https://graph.facebook.com/" + fbId + "/picture?type=large&redirect=true&width=600&height=600";
                    sessionManager.setSocialProfile(fbProfileImg);
                    fbWork = "";
                    fbjob_title = "";

                    sessionManager.setIsFBUser(true);
                    sessionManager.setFbId(fbId);
                    if (jsonObject.has("email")) {
                        socialEmail = jsonObject.getString("email");
                        sessionManager.setSocialMail(socialEmail);
                    }
                    if (jsonObject.has("mobile_phone")) {
                        fbPhone = jsonObject.getString("mobile_phone");
                    }
                    if (jsonObject.has("gender")) {
                        fbGender = jsonObject.getString("gender");
                        if (fbGender.equals("male"))
                            fbGender = "Men";
                        else
                            fbGender = "Women";
                    }

                    fbEducation = "";
                    fbAge = "";
                    if (jsonObject.has("work") || jsonObject.has("education") || jsonObject.has("birthday")) {
                        if (jsonObject.has("work")) {

                            if (jsonObject.getJSONArray("work").getJSONObject(0).has("position")) {
                                fbjob_title = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("position").getString("name");
                                fbWork = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("employer").getString("name");
                            } else {
                                fbWork = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("employer").getString("name");
                            }
                            /*fbWork = jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("position").getString("name");
                            fbWork = fbWork + " at " + jsonObject.getJSONArray("work").getJSONObject(0).getJSONObject("employer").getString("name");*/
                        }


                        if (jsonObject.has("education")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("education");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if (jsonArray.getJSONObject(i).getString("type").contains("College")) {
                                    if (fbEducation.equals(""))
                                        fbEducation = jsonArray.getJSONObject(i).getJSONObject("school").getString("name");
                                    else
                                        fbEducation = fbEducation + ", " + jsonArray.getJSONObject(i).getJSONObject("school").getString("name");
                                }
                            }
                        }


                        if (jsonObject.has("birthday")) {
                            if (!jsonObject.getString("birthday").equals("")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                                Date newDate = sdf.parse(jsonObject.getString("birthday"));
                                SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
                                fbAge = spf.format(newDate);
                                //String[] cal = jsonObject.getString("birthday").split("/");
                                //fbAge = getAge(Integer.valueOf(cal[2]), Integer.valueOf(cal[0]), Integer.valueOf(cal[1]));
                            }
                        }

                        Log.e("Age", fbAge + " " + fbWork + " " + fbEducation);
                   /* if (jsonObject.has("picture")) {
                        fbProfileImg = jsonObject.getJSONObject("picture").getJSONObject("data").getString("url");
                    }*/

                        // Get Facebook Album
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name");

                        new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/" + fbId + "/albums",
                                parameters,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
                                        Log.e("Album response", String.valueOf(response));
                                    /* handle the result */
                                        try {
                                            JSONObject jsonObject = response.getJSONObject();
                                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                                            if (jsonArray.length() > 0) {
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                                                    final String[] name = {jsonObj.getString("name")};
                                                    if (name[0].equals("Profile Pictures")) {
                                                        String albumid = jsonObj.getString("id");

                                                        Bundle parameters = new Bundle();
                                                        //picture.type(large)
                                                        parameters.putString("fields", "id,name,picture.type(large),source,target,images");

                                                        new GraphRequest(
                                                                AccessToken.getCurrentAccessToken(),
                                                                "/" + albumid + "/photos",
                                                                parameters,
                                                                HttpMethod.GET,
                                                                new GraphRequest.Callback() {
                                                                    public void onCompleted(GraphResponse response) {
                                                                        Log.e("Profile image response", String.valueOf(response));
                                                                /* handle the result */
                                                                        try {
                                                                            JSONObject jsonObject = response.getJSONObject();
                                                                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                                                                            int check = jsonArray.length();
                                                                            if (jsonArray.length() > 5)
                                                                                check = 5;

                                                                            for (int i = 0; i < check; i++) {
                                                                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                                                                if (fbProfileImg1.equals(""))
                                                                                    fbProfileImg1 = jsonObj.getString("source");
                                                                                else
                                                                                    fbProfileImg1 = fbProfileImg1 + "," + jsonObj.getString("source");

                                                                            }

                                                                            if (fbProfileImg1.equals(""))
                                                                                facebookSignUpApi(fbId, socialEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
                                                                            else
                                                                                facebookSignUpApi(fbId, socialEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg1, fbjob_title);
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                        ).executeAsync();
                                                        break;
                                                    }
                                                }
                                            } else {
                                                if (fbProfileImg1.equals(""))
                                                    facebookSignUpApi(fbId, socialEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
                                                else
                                                    facebookSignUpApi(fbId, socialEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg1, fbjob_title);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        ).executeAsync();


                    } else {
                        facebookSignUpApi(fbId, socialEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, first_name,last_name, picture.type(large),email,gender,work,birthday,education");
        //parameters.putString("fields", "id, name, first_name,last_name, email, picture.type(large),work,likes,friendlists,age_range,education");
        request.setParameters(parameters);
        request.executeAsync();

    }

    // Get Age from Date of Birth
    private String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }

    private void facebookSignUpApi(String fbId, String socialEmail, String fbPhone, String firstName, String lastName, String fbAge, String fbWork, String fbEducation, String fbGender, String fbProfileImg, String fbjob_title) {
        commonMethods.showProgressDialog(LoginActivity.this, customDialog);
        /*if(fbAge!=null&& !TextUtils.isEmpty(fbAge)&&fbGender!=null&& !TextUtils.isEmpty(fbGender)) {
            getParams(fbId, socialEmail, fbPhone, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
            apiService.facebookSignUp(fbId, socialEmail, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title).enqueue(new RequestCallback(REQ_FB_SIGNUP, this));
        }else{
            System.out.println("getting Empty");
        }*/
        LoginManager.getInstance().logOut();
        if(fbAge!=null&&fbGender!=null) {
            if (TextUtils.isEmpty(fbGender)){
                fbGender="Men";
            }
            getParams(socialEmail, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title);
            apiService.facebookSignUp(commonMethods.getAuthId(), commonMethods.getAuthType(), socialEmail, firstName, lastName, fbAge, fbWork, fbEducation, fbGender, fbProfileImg, fbjob_title).enqueue(new RequestCallback(REQ_FB_SIGNUP, this));
        }else{
            System.out.println("getting Empty");
        }
    }

    private HashMap<String, String> getParams(String socialEmail, String firstName, String lastName, String fbAge, String fbWork, String fbEducation, String fbGender, String fbProfileImg, String fbjob_title) {
        hashMap.put("auth_id", commonMethods.getAuthId());
        hashMap.put("auth_type", commonMethods.getAuthType());
        hashMap.put("email_id", socialEmail);
        hashMap.put("first_name", firstName);
        hashMap.put("last_name", lastName);
        hashMap.put("dob", fbAge);
        hashMap.put("work", fbWork);
        hashMap.put("college", fbEducation);
        hashMap.put("gender", fbGender);
        hashMap.put("image_url", sessionManager.getSocialProfile());
        hashMap.put("job_title", fbjob_title);
        return hashMap;
    }

    private void getSliderImageList() {
        commonMethods.showProgressDialog(LoginActivity.this, customDialog);
        apiService.getTutorialSliderImg().enqueue(new RequestCallback(REQ_GET_LOGIN_SLIDER, this));
    }

    /**
     * Method called for make circle page indicator setup.
     */
    private void initPageIndicator() {
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(4 * density);
        pageIndicator.setPageColor(ContextCompat.getColor(this, R.color.gray_indicator));
        pageIndicator.setFillColor(ContextCompat.getColor(this, R.color.color_accent));
        pageIndicator.setStrokeColor(ContextCompat.getColor(this, R.color.gray_indicator));
        viewPager.setCurrentItem(0);
        pageIndicator.setOnClickListener(null);
        pageIndicator.setExtraSpacing(0 * density);
    }

    public void onFbLoginClick() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(
                //"user_likes",
                "user_photos",
                "email",
                "public_profile",
                "user_mobile_phone",
                //"user_about_me",
                "user_birthday",
                "user_education_history",
                //"user_friends",
                //"user_location",
                //"user_relationships",
                "user_work_history"));
    }

    public void onPhoneLoginClick() {
        hashMap.put("auth_id", commonMethods.getAuthId());
        hashMap.put("auth_type", commonMethods.getAuthType());
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void onArrowTopClick() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        lltLoginBottom.setLayoutParams(params);

        rltTutorial.setVisibility(View.VISIBLE);
        tvArrowBottom.setVisibility(View.VISIBLE);
        tvArrowTop.setVisibility(View.GONE);
        signup_more.setVisibility(View.GONE);
        signup_less.setVisibility(View.VISIBLE);
       /* tvArrowTop.setVisibility(View.GONE);
        tvTakePolicy.setVisibility(View.GONE);
        tvLocationMsg.setVisibility(View.GONE);

        tvDotNotPostFb.setTextSize(14);
        tvLocationMsg.setTextSize(14);
        tvTermsPolicy.setTextSize(14);

        if (Build.VERSION.SDK_INT >= 24) {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy), 0));
        } else {
            tvTermsPolicy.setText(fromHtml(getResources().getString(R.string.login_terms_policy)));
        }*/
    }

    public void onArrowBottomClick() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lltLoginBottom.setLayoutParams(params);

        rltTutorial.setVisibility(View.GONE);
        tvArrowBottom.setVisibility(View.GONE);
        tvArrowTop.setVisibility(View.VISIBLE);
        signup_more.setVisibility(View.VISIBLE);
        signup_less.setVisibility(View.GONE);
       /* tvTakePolicy.setVisibility(View.VISIBLE);
        tvLocationMsg.setVisibility(View.VISIBLE);

        tvDotNotPostFb.setTextSize(16);
        tvLocationMsg.setTextSize(16);
        tvTermsPolicy.setTextSize(16);

        tvTermsPolicy.setText(getResources().getString(R.string.login_do_not_contact));*/
    }

    /**
     * Method called for initiate listener which triggered get tutorial page
     * navigation.
     */
    private void initViewPagerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        pageIndicator.setCurrentItem(0);
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        pageIndicator.setCurrentItem(1);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        pageIndicator.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_phone:
                onPhoneLoginClick();
                break;
            case R.id.tv_login_facebook:
                onFbLoginClick();
                break;
            case R.id.arrow_bottom:
                onArrowBottomClick();
                break;
            case R.id.tv_arrow_bottom:
                onArrowBottomClick();
                break;
            case R.id.tv_arrow_top:
                onArrowTopClick();
                break;
            default:
                break;
        }
    }

    private void onAppleSignIn() {
        BaseUrl.Companion.setAppleCallbackUrl(getResources().getString(R.string.apple_redirect_url));
        if (appleClientId == null || TextUtils.isEmpty(appleClientId)) {
            appleClientId = BuildConfig.APPLICATION_ID + ".clientid";
        }
        SignInWithAppleConfiguration configuration = new SignInWithAppleConfiguration.Builder()
                .clientId(appleClientId)
                .redirectUri(getResources().getString(R.string.apple_redirect_url))
                .scope("name email").build();

        SignInWithAppleCallback signInWithAppleCallback = new SignInWithAppleCallback() {
            @Override
            public void onSignInWithAppleSuccess(@NotNull String authorizationCode) {

            }

            @Override
            public void onSignInWithAppleFailure(@NotNull Throwable error) {

            }

            @Override
            public void onSignInWithAppleCancel() {

            }

            @Override
            public void onSuccessOnSignIn(@NotNull String response) {
                commonMethods.showProgressDialog(LoginActivity.this,customDialog);
                JSONObject json = new JSONObject();
                try{
                    json = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                }catch (Exception e){
                    e.printStackTrace();
                }

                String statuscode="";
                String statusmessage="";

                try {
                    statuscode = json.getString("status_code");
                    statusmessage = json.getString("status_message");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                commonMethods.hideProgressDialog();
                if (statuscode.equals("1")) {
                    //New User
                    createNewUser(response);
                } else if (statuscode.equals("2")) {
                    // Already user
                    onSuccessLogin(response,statusmessage);
                } else {
                    //Error or Other Response
                    Toast.makeText(LoginActivity.this, statusmessage, Toast.LENGTH_SHORT).show();
                }
            }
        };
        btnapplelogin.setUpSignInWithAppleOnClick(getSupportFragmentManager(), configuration, signInWithAppleCallback);
    }

    /**
     * Already Existing User
     */
    private void onSuccessLogin(String response,String statusMessage){
        String token = (String) commonMethods.getJsonValue(response, Constants.ACCESS_TOKEN, String.class);
        String imageUrl = (String) commonMethods.getJsonValue(response, Constants.IMAGE_URL, String.class);
        if (!TextUtils.isEmpty(token)) {
            sessionManager.setToken(token);
            if(!TextUtils.isEmpty(imageUrl))
                sessionManager.setProfileImg(imageUrl);
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            Toast.makeText(LoginActivity.this, statusMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        commonMethods.hideProgressDialog();
    }

    private void createNewUser(String response){
        sessionManager.setIsAppleUser(true);
        JSONObject json = new JSONObject();
        try{
            json = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            sessionManager.setappleId(json.getString("apple_id"));
            socialEmail = json.getString("email_id");
            sessionManager.setSocialMail(socialEmail);
        } catch (Exception e) {
            socialEmail="";
            e.printStackTrace();
        }
        getParams(socialEmail, "", "", "", "", "", "", "", "");
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        intent.putExtra("map", hashMap);
        startActivity(intent);
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(LoginActivity.this, dialog, data);
            return;
        }
        String statusCode = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "status_code", String.class);
        if (jsonResp.getRequestCode() == REQ_GET_LOGIN_SLIDER && jsonResp.isSuccess()) {
            onSuccessGetSliderImg(jsonResp);
        } else if (jsonResp.getRequestCode() == REQ_FB_SIGNUP && jsonResp.isSuccess()) {
            sessionManager.setProfileImg(fbProfileImg);
            LoginManager.getInstance().logOut();
            String token = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.ACCESS_TOKEN, String.class);
                String imageUrl = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.IMAGE_URL, String.class);
                if (!TextUtils.isEmpty(token)) {
                    sessionManager.setToken(token);
                    if(!TextUtils.isEmpty(imageUrl))
                        sessionManager.setProfileImg(imageUrl);
                    //sessionManager.setProfileImg();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
        } else if (statusCode.equalsIgnoreCase("2")){
            sessionManager.setIsFBUser(true);
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.putExtra("map", hashMap);
            startActivity(intent);
        } else {
            commonMethods.showMessage(LoginActivity.this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(LoginActivity.this, dialog, data);
    }

    private void onSuccessGetSliderImg(JsonResponse jsonResp) {
        SliderModel sliderModel = gson.fromJson(jsonResp.getStrResponse(), SliderModel.class);
        sessionManager.setMinAge(sliderModel.getMinimumAge());
        sessionManager.setMaxAge(sliderModel.getMaximumAge());
        appleClientId = sliderModel.getClientId();
        onAppleSignIn();
        if (sliderModel != null && sliderModel.getImageList() != null && sliderModel.getImageList().size() > 0) {
            imageList.clear();
            imageList.addAll(sliderModel.getImageList());
        }
        setViewPagerAdapter();
    }

    private void setViewPagerAdapter() {
        if (imageList.size() > 0) {
            PagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Constants.VP_LOGIN_SLIDER, imageList.size(), imageList);
            viewPager.setAdapter(PagerAdapter);
            pageIndicator.setViewPager(viewPager);
            initPageIndicator();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            //Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Create FB KeyHash
    public void getFbKeyHash(String packageName) {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                System.out.println("hash key value" + something);
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
}
