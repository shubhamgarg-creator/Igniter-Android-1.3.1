package igniter.views.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category EnlargeProfileActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.obs.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import igniter.R;
import igniter.adapters.chat.UnmatchReasonListAdapter;
import igniter.adapters.profile.EnlargeSliderAdapter;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.chat.ChatMessageModel;
import igniter.datamodels.chat.ReasonModel;
import igniter.datamodels.chat.UnMatchReasonModel;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.MyProfileModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.DropDownClickListener;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.Enums;
import igniter.utils.ImageUtils;
import igniter.utils.RequestCallback;
import igniter.views.customize.CirclePageIndicator;
import igniter.views.customize.CustomDialog;
import igniter.views.customize.CustomLayoutManager;
import igniter.views.customize.CustomRecyclerView;
import igniter.views.customize.IgniterViewPager;

import static igniter.utils.Enums.MATCH_LIKE;
import static igniter.utils.Enums.MATCH_NOPE;
import static igniter.utils.Enums.MATCH_SUPER_LIKE;
import static igniter.utils.Enums.REQ_GET_OTHER_PROFILE;
import static igniter.utils.Enums.REQ_REPORT_DETAILS_PROFILE;
import static igniter.utils.Enums.REQ_SWIPE_ENLARGE;

/*****************************************************************
 User or other user view profile and show other details also
 ****************************************************************/
public class EnlargeProfileActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener, UnmatchReasonListAdapter.OnItemClickListener {

    @Inject
    ApiService apiService;
    @Inject
    CommonMethods commonMethods;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson;
    @Inject
    ImageUtils imageUtils;

    private String reportId;
    private ArrayList<ReasonModel> reasonModels = new ArrayList<>();
    private Dialog unMatchDialog;
    private EditText edtInfo;
    private Scene mScene1;
    private UnmatchReasonListAdapter unmatchReasonListAdapter;
    private Scene mScene2;
    private ViewGroup mSceneRoot;
    private CustomLayoutManager linearLayoutManager;
    private LinearLayout llUpdate;
    String reportType;
    private ChatMessageModel chatMessageModel;
    private UnMatchReasonModel unMatchReasonModel;


    //private UnMatchReasonModel unMatchReasonModel;

    DropDownClickListener listener = new DropDownClickListener() {
        @Override
        public void onDropDrownClick(String value) {
            if (value.equalsIgnoreCase(getString(R.string.MUTE_NOTIFICATION))){

            }else if(value.equalsIgnoreCase(getString(R.string.REPORT))){

            }else if(value.equalsIgnoreCase(getString(R.string.UNMATCH))){

            }
        }
    };
    private CustomTextView tvReportUser;
    private RelativeLayout rltReportUser;
    private IgniterViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    private CustomTextView tvLikedUserIcon, tvUserNameAge, tvShareIcon, tvMenuIcon, tvDesignation, tvProfession, tvLocation, tvAbout, tvConnectedFriend;
    private CustomTextView tvRecommendFriend, tvRecommendDescription, tvToFriend, tvEditProfileIcon;
    private LinearLayout lltDesignation, lltProfession, lltLocation, lltAbout, lltRecommend, lltConnectFriends;
    private RelativeLayout rltBottomIcon, rtl_arrow_down;
    private View viewBottom, viewCenter;
    private RelativeLayout rltLike, rltSuperLike, rltUnLike;
    private int navType = 0, currUser = 0, userId = 0,subscriptionId = 0;
    private AlertDialog dialog;
    private MyProfileModel myProfileModel;
    private ImageView superlike;
    private String likeStatus="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enlarge_profile_activity);
        AppController.getAppComponent().inject(this);
        initView();
        getIntentValues();
        initPageIndicator();
    }

    private void initView() {
        pageIndicator = (CirclePageIndicator) findViewById(R.id.cpi_enlarge_profile);
        viewPager = (IgniterViewPager) findViewById(R.id.vp_enlarge_profile);

        viewPager.setAdapter(new EnlargeSliderAdapter(this));
        pageIndicator.setViewPager(viewPager, 0);

        tvLikedUserIcon = (CustomTextView) findViewById(R.id.tv_liked_user_icon);
        tvShareIcon = (CustomTextView) findViewById(R.id.tv_share_icon);
        tvMenuIcon = (CustomTextView) findViewById(R.id.tv_menu_icon);
        rltReportUser = (RelativeLayout) findViewById(R.id.rlt_report_user);

        rltLike = (RelativeLayout) findViewById(R.id.rlt_like_lay);
        rltSuperLike = (RelativeLayout) findViewById(R.id.rlt_superlike_lay);
        rltUnLike = (RelativeLayout) findViewById(R.id.rlt_unlike_lay);
        rtl_arrow_down = (RelativeLayout) findViewById(R.id.rtl_arrow_down);
        tvEditProfileIcon = (CustomTextView) findViewById(R.id.tv_profile_edit_icon);

        tvUserNameAge = (CustomTextView) findViewById(R.id.tv_user_name_age);
        tvDesignation = (CustomTextView) findViewById(R.id.tv_designation);
        tvProfession = (CustomTextView) findViewById(R.id.tv_profession);
        tvLocation = (CustomTextView) findViewById(R.id.tv_location);
        tvAbout = (CustomTextView) findViewById(R.id.tv_about_user);

        tvRecommendFriend = (CustomTextView) findViewById(R.id.tv_friends_count);
        tvRecommendDescription = (CustomTextView) findViewById(R.id.tv_friends_description);
        tvConnectedFriend = (CustomTextView) findViewById(R.id.tv_connect_friends);
        tvToFriend = (CustomTextView) findViewById(R.id.tv_to_friends);
        tvReportUser = (CustomTextView) findViewById(R.id.tv_report_user);

        lltDesignation = (LinearLayout) findViewById(R.id.llt_designation);
        lltProfession = (LinearLayout) findViewById(R.id.llt_profession);
        lltLocation = (LinearLayout) findViewById(R.id.llt_location);
        lltAbout = (LinearLayout) findViewById(R.id.llt_about);
        lltRecommend = (LinearLayout) findViewById(R.id.llt_recommend);
        lltConnectFriends = (LinearLayout) findViewById(R.id.llt_connect_friends);
        rltBottomIcon = (RelativeLayout) findViewById(R.id.rlt_bottom_icons);
        superlike = (ImageView) findViewById(R.id.superlike);

        viewBottom = findViewById(R.id.view_bottom);
        viewCenter = findViewById(R.id.view_center);

        dialog = commonMethods.getAlertDialog(this);

        initClickListener();

    }

    private void initClickListener() {
        tvShareIcon.setOnClickListener(this);
        tvReportUser.setOnClickListener(this);
        tvMenuIcon.setOnClickListener(this);
        tvEditProfileIcon.setOnClickListener(this);
        lltRecommend.setOnClickListener(this);
        lltConnectFriends.setOnClickListener(this);
        rtl_arrow_down.setOnClickListener(this);
        rltLike.setOnClickListener(this);
        rltUnLike.setOnClickListener(this);
        rltSuperLike.setOnClickListener(this);
    }

    private void getIntentValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            navType = bundle.getInt("navType", 0);
            currUser = bundle.getInt("currentUser", 0);
            userId = bundle.getInt("userId", 0);
            subscriptionId = bundle.getInt("subscriptionId", 0);
            //getProfileDetails();
            setBtnVisibility();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileDetails();
    }

    private void setBtnVisibility() {

        if (currUser == 0) {
            rltReportUser.setVisibility(View.VISIBLE);
        } else {
            rltReportUser.setVisibility(View.GONE);
        }

        switch (navType) {
            case 0:  // My Profile
                viewBottom.setVisibility(View.GONE);
                viewCenter.setVisibility(View.GONE);
                tvShareIcon.setVisibility(View.GONE);
                tvMenuIcon.setVisibility(View.GONE);
                lltRecommend.setVisibility(View.GONE);
                lltConnectFriends.setVisibility(View.GONE);
                rltBottomIcon.setVisibility(View.GONE);
                lltConnectFriends.setVisibility(View.GONE);
                tvEditProfileIcon.setVisibility(View.VISIBLE);
                break;
            case 1:   // Igniter Profile
                tvLikedUserIcon.setVisibility(View.GONE);
                lltRecommend.setVisibility(View.GONE);
                rltBottomIcon.setVisibility(View.VISIBLE);
                tvEditProfileIcon.setVisibility(View.GONE);
                lltConnectFriends.setVisibility(View.GONE);
                break;
            case 3:   // Other Profile
                tvLikedUserIcon.setVisibility(View.GONE);
                lltRecommend.setVisibility(View.GONE);
                //lltConnectFriends.setVisibility(View.VISIBLE);
                lltConnectFriends.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
                tvEditProfileIcon.setVisibility(View.GONE);
                rltBottomIcon.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void getProfileDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.otherProfileView(sessionManager.getToken(), userId).enqueue(new RequestCallback(REQ_GET_OTHER_PROFILE, this));
    }

    private void initPageIndicator() {
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(3 * density);
        pageIndicator.setPageColor(ContextCompat.getColor(this, R.color.gray_text_color));
        pageIndicator.setFillColor(ContextCompat.getColor(this, R.color.color_accent));
        pageIndicator.setStrokeColor(ContextCompat.getColor(this, R.color.gray_text_color));
        pageIndicator.setOnClickListener(null);
        pageIndicator.setExtraSpacing((float) (1.5 * density));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share_icon:
                break;
            case R.id.tv_menu_icon:
                commonMethods.dropDownMenu(this, v, null, getResources().getStringArray(R.array.QUICK_MENU_TITLE), listener);
                break;
            case R.id.tv_profile_edit_icon:
                if (myProfileModel != null && !TextUtils.isEmpty(myProfileModel.getName())) {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    intent.putExtra("userName", myProfileModel.getName());
                    startActivity(intent);
                }
                break;
            case R.id.rtl_arrow_down:
                onBackPressed();
                break;
            case R.id.tv_report_user:
                getReportDetails();
                break;
            case R.id.rlt_unlike_lay:
                if(sessionManager.getIsFromLikedPage()){
                    likeStatus = MATCH_NOPE;
                    swipeProfile(MATCH_NOPE);
                }else{
                    sessionManager.setSwipeType("UnLike");
                    onBackPressed();
                }
                break;
            case R.id.rlt_like_lay:
                if(sessionManager.getIsFromLikedPage()){
                    likeStatus = MATCH_LIKE;
                    swipeProfile(MATCH_LIKE);
                }else{
                    sessionManager.setSwipeType("Like");
                    onBackPressed();
                }
                break;
            case R.id.rlt_superlike_lay:
                if(sessionManager.getIsFromLikedPage()){
                    likeStatus = MATCH_SUPER_LIKE;
                    swipeProfile(MATCH_SUPER_LIKE);
                }else{
                    sessionManager.setSwipeType("SuperLike");
                    onBackPressed();
                }
                break;
            case R.id.llt_recommend:
                String message = "";
                String title = getResources().getString(R.string.recommend_friend) + " " + myProfileModel.getName() + ", " + getResources().getString(R.string.recommend_to_friends);
                if (myProfileModel.getName().equals("women")) {
                    message = getResources().getString(R.string.share_girl) + " \n\n" + getResources().getString(R.string.share_url);
                } else {
                    message = getResources().getString(R.string.share_guy) + " \n\n" + getResources().getString(R.string.share_url);
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, title));
                break;
            default:
                break;
        }
    }

    private void getReportDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.unMatchDetails(sessionManager.getToken(), "report").enqueue(new RequestCallback(REQ_REPORT_DETAILS_PROFILE, this));
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_GET_OTHER_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessGetOtherProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_REPORT_DETAILS_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessReportList(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_UNMATCH_USER:
                if (jsonResp.isSuccess()) {
                    onSuccessUnmatchUser(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_SWIPE_ENLARGE:
                if (jsonResp.isSuccess()) {
                    onSuccessSwipe(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;

            default:
                break;
        }
    }

    private void onSuccessSwipe(JsonResponse jsonResp) {

        String likeStatusMessage = "";
        if(likeStatus.equals(MATCH_LIKE)){
            likeStatusMessage = getResources().getString(R.string.liked);
        }else if(likeStatus.equals(MATCH_NOPE)){
            likeStatusMessage = getResources().getString(R.string.noped);
        }else if(likeStatus.equals(MATCH_SUPER_LIKE)){
            likeStatusMessage = getResources().getString(R.string.super_liked);
        }


        Toast.makeText(this,likeStatusMessage,Toast.LENGTH_SHORT).show();
        likeStatus = "";

        sessionManager.setReloadLikedPage(true);
        onBackPressed();
    }


    /**
     * Get response after un match user
     */
    private void onSuccessUnmatchUser(JsonResponse jsonResp) {
        unMatchDialog.dismiss();
        sessionManager.setSwipeType("UnLike");
        sessionManager.setReported(true);
        onBackPressed();
    }

    private void onSuccessReportList(JsonResponse jsonResp) {
        reportType = "report";
        unMatchReasonModel = gson.fromJson(jsonResp.getStrResponse(), UnMatchReasonModel.class);
        if (unMatchReasonModel != null) {
            updateUnMatchView();
        }
    }

    /**
     * Update unmatch reason view
     */
    private void updateUnMatchView() {
        if (unMatchReasonModel.getResonModels() != null && unMatchReasonModel.getResonModels().size() > 0) {
            reasonModels.clear();
            reasonModels.addAll(unMatchReasonModel.getResonModels());
            showUnMatchDialog(getResources().getString(R.string.areyousure), getResources().getString(R.string.tell_us_why), 0, reasonModels);
        } else {
            //rltEmptyChat.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Call swipe profile API
     */
    private void swipeProfile( String matchType) {

        commonMethods.showProgressDialog(this, customDialog);
        if(userId!=0)
            apiService.swipeProfile(sessionManager.getToken(), userId, matchType, subscriptionId).enqueue(new RequestCallback(REQ_SWIPE_ENLARGE, this));
    }


    /**
     * Show unmatch reason dialog
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showUnMatchDialog(String title, String message, int type, ArrayList<ReasonModel> reasonModel) {
        unMatchDialog = new Dialog(EnlargeProfileActivity.this);
        unMatchDialog.setContentView(R.layout.activity_cucustomcurve_dialog); //layout for dialog
        //dialog.setTitle("Add a new friend");
        unMatchDialog.setCancelable(true); //none-dismiss when touching outside Dialog
        unMatchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set the custom dialog components - texts and image
        RelativeLayout rltReportImage = unMatchDialog.findViewById(R.id.rlt_report_image);
        llUpdate = unMatchDialog.findViewById(R.id.ll_update);
        CustomTextView tvDialogTitle = unMatchDialog.findViewById(R.id.tv_dialog_title);
        CustomTextView tvMessage = unMatchDialog.findViewById(R.id.tv_message);
        CustomTextView tvUpdate = unMatchDialog.findViewById(R.id.tv_update);
        CustomTextView tvCancel = unMatchDialog.findViewById(R.id.tv_cancel);
        CustomRecyclerView rvReasonUnmatchList = unMatchDialog.findViewById(R.id.rv_reason_unmatch_list);


        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                unMatchUser(edtInfo.getText().toString());

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.go(mScene1);
                llUpdate.animate().translationY(llUpdate.getHeight());
                llUpdate.setVisibility(View.GONE);

            }
        });
        if (type == 0) {
            rltReportImage.setVisibility(View.GONE);
            llUpdate.animate().translationY(llUpdate.getHeight());
            llUpdate.setVisibility(View.GONE);
            rvReasonUnmatchList.setVisibility(View.VISIBLE);
            tvDialogTitle.setText(title);
            tvMessage.setText(message);
            tvUpdate.setText(getResources().getString(R.string.unmatch));

            linearLayoutManager = new CustomLayoutManager(this);
            rvReasonUnmatchList.setLayoutManager(linearLayoutManager);

            if (reasonModel.size() > 0) {
                unmatchReasonListAdapter = new UnmatchReasonListAdapter(this, reasonModel, this);
                rvReasonUnmatchList.setAdapter(unmatchReasonListAdapter);
                linearLayoutManager = (CustomLayoutManager) rvReasonUnmatchList.getLayoutManager();
            } else {
                unmatchReasonListAdapter = new UnmatchReasonListAdapter(this, this);
                rvReasonUnmatchList.setAdapter(unmatchReasonListAdapter);
            }
        }


        View myView = unMatchDialog.findViewById(R.id.rl_parent_lay);
        // Check if the runtime version is at least Lollipop
        mSceneRoot = (ViewGroup) unMatchDialog.findViewById(R.id.scene_root);


        mScene1 = new Scene(mSceneRoot, (ViewGroup) unMatchDialog.findViewById(R.id.rv_reason_unmatch_list));
        mScene2 = Scene.getSceneForLayout(mSceneRoot, R.layout.tell_us_why, this);


        unMatchDialog.show();

        myView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                v.removeOnLayoutChangeListener(this);
                circularRevealAnimation(myView);

            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealAnimation(View myView) {
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius);

        // make the view visible and start the getUnMatchReason
        myView.setVisibility(View.VISIBLE);
        anim.start();


    }


    /**
     * Get unmatch user using webservice
     */
    private void unMatchUser(String userMessage) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.unMatchUser(getUnMatchedParams(userMessage)).enqueue(new RequestCallback(Enums.REQ_UNMATCH_USER, this));
    }


    private HashMap<String, String> getUnMatchedParams(String userMessage) {


        HashMap<String, String> getUnMatchedHashMap = new HashMap<>();
        getUnMatchedHashMap.put("token", sessionManager.getToken());
        getUnMatchedHashMap.put("report_id", String.valueOf(reportId));
        getUnMatchedHashMap.put("reporter_id", String.valueOf(myProfileModel.getUserId()));
        getUnMatchedHashMap.put("message", userMessage);
        getUnMatchedHashMap.put("type", reportType);

        return getUnMatchedHashMap;
    }


    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessGetOtherProfile(JsonResponse jsonResp) {
        myProfileModel = gson.fromJson(jsonResp.getStrResponse(), MyProfileModel.class);
        if (myProfileModel != null) {
            updateView();
        }
    }

    private void updateView() {

        if (myProfileModel == null) return;

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(myProfileModel.getName())) {
            sb.append(myProfileModel.getName());
            tvReportUser.setText(getResources().getString(R.string.report) + myProfileModel.getName());
            if (myProfileModel.getAge() > 0) {
                sb.append(", ");
                sb.append(myProfileModel.getAge());
            }
            tvUserNameAge.setText(sb.toString());
            tvRecommendFriend.setVisibility(View.VISIBLE);
            tvToFriend.setVisibility(View.VISIBLE);
            tvRecommendFriend.setText(getResources().getString(R.string.recommend_friend) + " " + myProfileModel.getName());
        }

        if(myProfileModel.isSuperLiked()){
            superlike.setVisibility(View.VISIBLE);
        }else{
            superlike.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(myProfileModel.getCollege())) {
            tvProfession.setText(myProfileModel.getCollege());
            lltProfession.setVisibility(View.VISIBLE);
        } else {
            lltProfession.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getWork())) {
            if (myProfileModel.getJobTitle().equals("")) {
                tvDesignation.setText(myProfileModel.getWork());
            } else {
                tvDesignation.setText(myProfileModel.getJobTitle() + " at " + myProfileModel.getWork());
            }
            lltDesignation.setVisibility(View.VISIBLE);
        } else {
            lltDesignation.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getKilometer()) && !TextUtils.isEmpty(myProfileModel.getDistanceType())) {
            if (myProfileModel.getDistanceType().contains("km")) {
                if (!TextUtils.isEmpty(myProfileModel.getKilometer()) && Float.valueOf(myProfileModel.getKilometer()) < 1)
                    tvLocation.setText(getResources().getString(R.string.less_km_away));
                else
                    tvLocation.setText(myProfileModel.getKilometer() + " " + getResources().getString(R.string.km_away));
            } else {
                if (!TextUtils.isEmpty(myProfileModel.getKilometer()) && Float.valueOf(myProfileModel.getKilometer()) < 1)
                    tvLocation.setText(getResources().getString(R.string.less_mi_away));
                else
                    tvLocation.setText(myProfileModel.getKilometer() + " " + getResources().getString(R.string.miles_away));
            }

            lltLocation.setVisibility(View.VISIBLE);
        } else {
            lltLocation.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getAbout())) {
            tvAbout.setText(myProfileModel.getAbout());
            lltAbout.setVisibility(View.VISIBLE);
            viewCenter.setVisibility(View.VISIBLE);
        } else {
            lltAbout.setVisibility(View.GONE);
        }
        if (myProfileModel.getImages() != null && myProfileModel.getImages().size() > 0) {
            viewPager.setAdapter(new EnlargeSliderAdapter(this, myProfileModel.getImages()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getUnMatchReason() {
        llUpdate.setVisibility(View.VISIBLE);
        llUpdate.animate().translationY(0);


        TransitionManager.go(mScene2);
        edtInfo = (EditText) mSceneRoot.findViewById(R.id.edt_info);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sessionManager.setIsFromLikedPage(false);
        overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClicked(int position, boolean isMessageReq) {
        reportId = String.valueOf(reasonModels.get(position).getReasonId());

        if (isMessageReq)
            getUnMatchReason();
        else
            unMatchUser("");
    }
}
