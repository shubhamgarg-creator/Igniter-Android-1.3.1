package igniter.views.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category GetIgniterPlusActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.Gson;
import com.obs.CustomButton;
import com.obs.CustomTextView;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import igniter.R;
import igniter.adapters.profile.LocationListAdapter;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.LocationModel;
import igniter.datamodels.main.PlusSettingsModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;
import igniter.views.customize.CustomLayoutManager;
import igniter.views.customize.CustomRecyclerView;
import igniter.views.main.IgniterPlusDialogActivity;

import static igniter.utils.Enums.REQ_ADD_LOCATION;
import static igniter.utils.Enums.REQ_GET_PLUS_SETTINGS;
import static igniter.utils.Enums.REQ_UPDATE_PLUS_SETTINGS;

/*****************************************************************
 Igniter plus plan details
 ****************************************************************/
public class GetIgniterPlusActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, ServiceListener {

    String unLimitedLikes = "", boostPlan = "", extraSuperLikes = "", rewind = "", showMyAge = "", showMyDistance;
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
    private CustomTextView tvHeader, tvBackArrow;
    private LinearLayout lltUnlimitedSwipe, lltSkipQueue, lltControlWhoSeeYou, lltWhoSeesView, lltLocationList, lltLocation, lltControlProfile, lltExtraSuperLike, lltPassport, lltRewind, lltHideAds;
    private RelativeLayout rltUnlimitedSwipe, rltIgniterBoost, rltBalanceRecommendation, rltRecentlyActive, rltStandard, rltPeopleLiked;
    private RelativeLayout rltHideAge, rltHideLocation, rltRewind, rltHideAds, rltExtraSuperLike;
    private CustomTextView tvLocation, tvMyCurrentLocation, tvAddNewLocation;
    private SwitchCompat swUnlimitedSwipe, swIgniterBoost, swHideAge, swHideLocation, swRewind, swHideAds, swExtraSuperLike;
    private ImageView ivGetPlus;
    private CustomButton btnGetPlus;
    private int clickPos = 1;
    private AlertDialog dialog;
    private PlusSettingsModel plusSettingsModel;
    private CustomRecyclerView rvLocationList;
    private LocationListAdapter locationListAdapter;
    private ArrayList<LocationModel> locationModels = new ArrayList<>();
    private CustomLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.igniter_plus_activity);
        AppController.getAppComponent().inject(this);
        getIntentValues();
        initView();
    }

    private void initView() {
        tvHeader = (CustomTextView) findViewById(R.id.tv_header_title);
        tvBackArrow = (CustomTextView) findViewById(R.id.tv_left_arrow);
        tvHeader.setText(getString(R.string.my_igniter_plus));

        tvBackArrow.setOnClickListener(this);

        initParentView();
        initChildView();
    }

    private void initParentView() {
        lltUnlimitedSwipe = (LinearLayout) findViewById(R.id.llt_unlimited_swipe);
        lltSkipQueue = (LinearLayout) findViewById(R.id.llt_skip_queue);
        lltControlWhoSeeYou = (LinearLayout) findViewById(R.id.llt_control_who_you_see);
        lltWhoSeesView = (LinearLayout) findViewById(R.id.llt_control_who_sees_view);
        lltControlProfile = (LinearLayout) findViewById(R.id.llt_control_profile);
        lltExtraSuperLike = (LinearLayout) findViewById(R.id.llt_extra_super_like);
        lltPassport = (LinearLayout) findViewById(R.id.llt_passport);
        lltRewind = (LinearLayout) findViewById(R.id.llt_rewind);
        lltHideAds = (LinearLayout) findViewById(R.id.llt_hide_ads);
        lltLocation = (LinearLayout) findViewById(R.id.llt_location);
        lltLocationList = (LinearLayout) findViewById(R.id.llt_location_list);

        rltUnlimitedSwipe = (RelativeLayout) findViewById(R.id.rlt_unlimited_swipe);
        rltIgniterBoost = (RelativeLayout) findViewById(R.id.rlt_igniter_boost);
        rltBalanceRecommendation = (RelativeLayout) findViewById(R.id.rlt_balanced_recommendation);
        rltPeopleLiked = (RelativeLayout) findViewById(R.id.rlt_people_liked);
        rltRecentlyActive = (RelativeLayout) findViewById(R.id.rlt_recently_active);
        rltExtraSuperLike = (RelativeLayout) findViewById(R.id.rlt_extra_super_like);
        rltHideAge = (RelativeLayout) findViewById(R.id.rlt_do_not_show_age);
        rltHideLocation = (RelativeLayout) findViewById(R.id.rlt_hide_location);
        rltRewind = (RelativeLayout) findViewById(R.id.rlt_rewind);
        rltStandard = (RelativeLayout) findViewById(R.id.rlt_standard);
        rltHideAds = (RelativeLayout) findViewById(R.id.rlt_hide_ad);

        ivGetPlus = (ImageView) findViewById(R.id.iv_get_plus);
        tvLocation = (CustomTextView) findViewById(R.id.tv_location);
        tvMyCurrentLocation = (CustomTextView) findViewById(R.id.tv_current_location);
        tvAddNewLocation = (CustomTextView) findViewById(R.id.tv_add_new_location);
        btnGetPlus = (CustomButton) findViewById(R.id.btn_get_plus);
        rltUnlimitedSwipe.setOnClickListener(this);
        rltIgniterBoost.setOnClickListener(this);
        rltBalanceRecommendation.setOnClickListener(this);
        rltRecentlyActive.setOnClickListener(this);
        rltStandard.setOnClickListener(this);
        rltPeopleLiked.setOnClickListener(this);
        rltHideAds.setOnClickListener(this);
        rltHideLocation.setOnClickListener(this);
        rltExtraSuperLike.setOnClickListener(this);
        rltRewind.setOnClickListener(this);
        rltHideAge.setOnClickListener(this);
        lltLocation.setOnClickListener(this);
        ivGetPlus.setOnClickListener(this);
        btnGetPlus.setOnClickListener(this);
        tvAddNewLocation.setOnClickListener(this);
        dialog = commonMethods.getAlertDialog(this);

    }

    private void initChildView() {
        // Unlimited swipe
        CustomTextView tvSwipeTitle = (CustomTextView) lltUnlimitedSwipe.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvSwipeSubTitle = (CustomTextView) lltUnlimitedSwipe.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivUnLimitedSwipe = (ImageView) lltUnlimitedSwipe.findViewById(R.id.iv_sub_view_image);

        ivUnLimitedSwipe.setImageResource(R.drawable.unlimited_swipe);
        tvSwipeTitle.setText(getString(R.string.unlimited_swipe_title));
        tvSwipeSubTitle.setText(getString(R.string.unlimited_swipe_subtitle));

        // Unlimited like
        CustomTextView tvLikeTitle = (CustomTextView) rltUnlimitedSwipe.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvLikeSubTitle = (CustomTextView) rltUnlimitedSwipe.findViewById(R.id.tv_toggle_view_subtitle);

        tvLikeTitle.setText(getString(R.string.unlimited_like_title));
        tvLikeSubTitle.setText(getString(R.string.unlimited_like_subtitle));

        swUnlimitedSwipe = (SwitchCompat) rltUnlimitedSwipe.findViewById(R.id.switch_toggle_view);
        swUnlimitedSwipe.setVisibility(View.VISIBLE);

        // Skip the queue
        CustomTextView tvSkipTitle = (CustomTextView) lltSkipQueue.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvSkipSubTitle = (CustomTextView) lltSkipQueue.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivSkipQueue = (ImageView) lltSkipQueue.findViewById(R.id.iv_sub_view_image);

        ivSkipQueue.setImageResource(R.drawable.thunder);
        tvSkipTitle.setText(getString(R.string.skip_queue_title));
        tvSkipSubTitle.setText(getString(R.string.skip_queue_subtitle));

        CustomTextView tvBoostTitle = (CustomTextView) rltIgniterBoost.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvBoostSubTitle = (CustomTextView) rltIgniterBoost.findViewById(R.id.tv_toggle_view_subtitle);

        tvBoostTitle.setText(getString(R.string.boost_title));
        tvBoostSubTitle.setText(getString(R.string.boost_subtitle));

        swIgniterBoost = (SwitchCompat) rltIgniterBoost.findViewById(R.id.switch_toggle_view);
        swIgniterBoost.setVisibility(View.VISIBLE);

        // Control who you see
        CustomTextView tvControlWhoYouSeeTitle = (CustomTextView) lltControlWhoSeeYou.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvControlWhoYouSeeSubTitle = (CustomTextView) lltControlWhoSeeYou.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivControlWhoYouSee = (ImageView) lltControlWhoSeeYou.findViewById(R.id.iv_sub_view_image);

        ivControlWhoYouSee.setImageResource(R.drawable.control_who_see_you);
        tvControlWhoYouSeeTitle.setText(getString(R.string.control_who_see_you_title));
        tvControlWhoYouSeeSubTitle.setText(getString(R.string.control_who_see_you_subtitle));

        CustomTextView tvBalancedRecommendTitle = (CustomTextView) rltBalanceRecommendation.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvBalancedRecommendSubTitle = (CustomTextView) rltBalanceRecommendation.findViewById(R.id.tv_toggle_view_subtitle);

        tvBalancedRecommendTitle.setText(getString(R.string.balanced_recommend_title));
        tvBalancedRecommendSubTitle.setText(getString(R.string.balanced_recommend_subtitle));

        /*CustomTextView tvBalancedTick = (CustomTextView) rltBalanceRecommendation.findViewById(R.id.tv_toggle_view_tick);
        tvBalancedTick.setVisibility(View.GONE);*/

        ImageView balancedtick = (ImageView) rltBalanceRecommendation.findViewById(R.id.check_mark);
        balancedtick.setVisibility(View.VISIBLE);

        // Recently Active
        CustomTextView tvRecentlyActiveTitle = (CustomTextView) rltRecentlyActive.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvRecentlyActiveSubTitle = (CustomTextView) rltRecentlyActive.findViewById(R.id.tv_toggle_view_subtitle);

        tvRecentlyActiveTitle.setText(getString(R.string.recent_active_title));
        tvRecentlyActiveSubTitle.setText(getString(R.string.recent_active_subtitle));

        // Control who sees you
        CustomTextView tvControlWhoSeesYouTitle = (CustomTextView) lltWhoSeesView.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvControlWhoSeesYouSubTitle = (CustomTextView) lltWhoSeesView.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivControlSeesYou = (ImageView) lltWhoSeesView.findViewById(R.id.iv_sub_view_image);

        ivControlSeesYou.setImageResource(R.drawable.control_who_sees_you);
        tvControlWhoSeesYouTitle.setText(getString(R.string.control_who_sees_you_title));
        tvControlWhoSeesYouSubTitle.setText(getString(R.string.control_who_sees_you_subtitle));

        CustomTextView tvStandardTitle = (CustomTextView) rltStandard.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvStandardSubTitle = (CustomTextView) rltStandard.findViewById(R.id.tv_toggle_view_subtitle);

        tvStandardTitle.setText(getString(R.string.standard_title));
        tvStandardSubTitle.setText(getString(R.string.standard_subtitle));

        /*CustomTextView tvStandardTick = (CustomTextView) rltStandard.findViewById(R.id.tv_toggle_view_tick);
        tvStandardTick.setVisibility(View.GONE);*/

        ImageView standred = (ImageView) rltStandard.findViewById(R.id.check_mark);
        standred.setVisibility(View.VISIBLE);

        // People liked
        CustomTextView tvPeopleLikedTitle = (CustomTextView) rltPeopleLiked.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvPeopleLikedSubTitle = (CustomTextView) rltPeopleLiked.findViewById(R.id.tv_toggle_view_subtitle);

        tvPeopleLikedTitle.setText(getString(R.string.liked_people_title));
        tvPeopleLikedSubTitle.setText(getString(R.string.liked_people_subtitle));

        // Control your profile
        CustomTextView tvControlProfileTitle = (CustomTextView) lltControlProfile.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvControlProfileSubTitle = (CustomTextView) lltControlProfile.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivControlYourProfile = (ImageView) lltControlProfile.findViewById(R.id.iv_sub_view_image);

        ivControlYourProfile.setImageResource(R.drawable.control_you_profile);
        tvControlProfileTitle.setText(getString(R.string.control_profile_title));
        tvControlProfileSubTitle.setText(getString(R.string.control_profile_subtitle));

        // Hide Age and Location
        CustomTextView tvHideAgeTitle = (CustomTextView) rltHideAge.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvHideAgeSubTitle = (CustomTextView) rltHideAge.findViewById(R.id.tv_toggle_view_subtitle);

        CustomTextView tvHideLocationTitle = (CustomTextView) rltHideLocation.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvHideLocationSubTitle = (CustomTextView) rltHideLocation.findViewById(R.id.tv_toggle_view_subtitle);

        tvHideAgeTitle.setText(getString(R.string.hide_my_age));
        tvHideLocationTitle.setText(getString(R.string.hide_my_distance));
        tvHideAgeSubTitle.setVisibility(View.GONE);
        tvHideLocationSubTitle.setVisibility(View.GONE);

        swHideAge = (SwitchCompat) rltHideAge.findViewById(R.id.switch_toggle_view);
        swHideAge.setVisibility(View.VISIBLE);
        swHideLocation = (SwitchCompat) rltHideLocation.findViewById(R.id.switch_toggle_view);
        swHideLocation.setVisibility(View.VISIBLE);

        // Extra Super Like
        CustomTextView tvSuperLikeTitle = (CustomTextView) lltExtraSuperLike.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvSuperLikeSubTitle = (CustomTextView) lltExtraSuperLike.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivSuperLikeQueue = (ImageView) lltExtraSuperLike.findViewById(R.id.iv_sub_view_image);

        ivSuperLikeQueue.setImageResource(R.drawable.paywall_super);
        tvSuperLikeTitle.setText(getString(R.string.super_like_title));
        tvSuperLikeSubTitle.setText(getString(R.string.super_like_subtitle));

        CustomTextView tvExtraSuperLikeTitle = (CustomTextView) rltExtraSuperLike.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvExtraSuperLikeSubTitle = (CustomTextView) rltExtraSuperLike.findViewById(R.id.tv_toggle_view_subtitle);

        tvExtraSuperLikeTitle.setText(getString(R.string.extra_super_like_title));
        tvExtraSuperLikeSubTitle.setText(getString(R.string.extra_super_like_subtitle));

        swExtraSuperLike = (SwitchCompat) rltExtraSuperLike.findViewById(R.id.switch_toggle_view);
        swExtraSuperLike.setVisibility(View.VISIBLE);

        // Passport any location
        CustomTextView tvPassportLocationTitle = (CustomTextView) lltPassport.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvPassportLocationSubTitle = (CustomTextView) lltPassport.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivPassportLocation = (ImageView) lltPassport.findViewById(R.id.iv_sub_view_image);

        ivPassportLocation.setImageResource(R.drawable.paywall_passport);
        tvPassportLocationTitle.setText(getString(R.string.passport_location_title));
        tvPassportLocationSubTitle.setText(getString(R.string.passport_location_subtitle));

        // Rewind your last swipe
        CustomTextView tvRewindTitle = (CustomTextView) lltRewind.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvRewindSubTitle = (CustomTextView) lltRewind.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivRewindLastSwipe = (ImageView) lltRewind.findViewById(R.id.iv_sub_view_image);

        ivRewindLastSwipe.setImageResource(R.drawable.rewind_last_swipe);
        tvRewindTitle.setText(getString(R.string.rewind_last_swipe_title));
        tvRewindSubTitle.setText(getString(R.string.rewind_last_swipe_subtitle));

        CustomTextView tvRewindSwipeTitle = (CustomTextView) rltRewind.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvRewindSwipeSubTitle = (CustomTextView) rltRewind.findViewById(R.id.tv_toggle_view_subtitle);

        tvRewindSwipeTitle.setText(getString(R.string.rewind_last_swipe));
        tvRewindSwipeSubTitle.setText(getString(R.string.rewind_swipe_toggle));

        swRewind = (SwitchCompat) rltRewind.findViewById(R.id.switch_toggle_view);
        swRewind.setVisibility(View.VISIBLE);

        // Hide Ads
        CustomTextView tvHideAdsTitle = (CustomTextView) lltHideAds.findViewById(R.id.tv_sub_view_title);
        CustomTextView tvHideAdsSubTitle = (CustomTextView) lltHideAds.findViewById(R.id.tv_sub_view_sub_title);
        ImageView ivHideAds = (ImageView) lltHideAds.findViewById(R.id.iv_sub_view_image);

        ivHideAds.setImageResource(R.drawable.paywall_ads);
        tvHideAdsTitle.setText(getString(R.string.hide_ads_title));
        tvHideAdsSubTitle.setText(getString(R.string.hide_ads_subtitle));

        CustomTextView tvAdsTitle = (CustomTextView) rltHideAds.findViewById(R.id.tv_toggle_view_title);
        CustomTextView tvAdsSubTitle = (CustomTextView) rltHideAds.findViewById(R.id.tv_toggle_view_subtitle);

        tvAdsTitle.setText(getString(R.string.hide_ads_toggle));
        tvAdsSubTitle.setVisibility(View.GONE);

        rvLocationList = (CustomRecyclerView) findViewById(R.id.rv_location_list);

        swHideAds = (SwitchCompat) rltHideAds.findViewById(R.id.switch_toggle_view);
        swHideAds.setVisibility(View.VISIBLE);

        swUnlimitedSwipe.setTag(1);
        swIgniterBoost.setTag(2);
        swHideAge.setTag(3);
        swHideLocation.setTag(4);
        swExtraSuperLike.setTag(5);
        swRewind.setTag(6);
        swHideAds.setTag(7);

        swUnlimitedSwipe.setOnClickListener(this);
        swIgniterBoost.setOnClickListener(this);
        swHideAge.setOnClickListener(this);
        swHideLocation.setOnClickListener(this);
        swRewind.setOnClickListener(this);
        swHideAds.setOnClickListener(this);
        swExtraSuperLike.setOnClickListener(this);

        swUnlimitedSwipe.setOnCheckedChangeListener(this);
        swIgniterBoost.setOnCheckedChangeListener(this);
        swHideAge.setOnCheckedChangeListener(this);
        swHideLocation.setOnCheckedChangeListener(this);
        swRewind.setOnCheckedChangeListener(this);
        swHideAds.setOnCheckedChangeListener(this);
        swExtraSuperLike.setOnCheckedChangeListener(this);

        swUnlimitedSwipe.setChecked(false);
        swIgniterBoost.setChecked(false);
        swHideAge.setChecked(false);
        swHideLocation.setChecked(false);
        swRewind.setChecked(false);
        swHideAds.setChecked(false);
        swExtraSuperLike.setChecked(false);


        initRecyclerView();
        commonMethods.showProgressDialog(this, customDialog);
    }

    private void getSettingsDetails() {

        apiService.getPlusSettings(sessionManager.getToken()).enqueue(new RequestCallback(REQ_GET_PLUS_SETTINGS, this));
    }

    private void getIntentValues() {
    }

    private void initRecyclerView() {
        linearLayoutManager = new CustomLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        rvLocationList.setLayoutManager(linearLayoutManager);
        locationListAdapter = new LocationListAdapter(this);
        rvLocationList.setAdapter(locationListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_arrow:
                commonMethods.showProgressDialog(this, customDialog);
                apiService.updatePlusSettings(getParams()).enqueue(new RequestCallback(REQ_UPDATE_PLUS_SETTINGS, this));
                //onBackPressed();
                break;
            case R.id.rlt_unlimited_swipe:
                //getDialog("7");
                break;
            case R.id.rlt_igniter_boost:
                getDialog("2");
                break;
            case R.id.rlt_standard:
            case R.id.rlt_people_liked:
                getDialog("3");
                break;
            case R.id.rlt_do_not_show_age:
            case R.id.rlt_hide_location:
                getDialog("4");
                break;
            case R.id.tv_add_new_location:
                if (sessionManager.getIsOrder()) {
                    Intent intent = new Intent(GetIgniterPlusActivity.this, AddLocationActivity.class);
                    startActivityForResult(intent, REQ_ADD_LOCATION);
                } else {
                    getDialog("5");
                }
                break;
            case R.id.rlt_extra_super_like:
                getDialog("5");
                break;
            case R.id.llt_location:
                tvMyCurrentLocation.setVisibility(View.INVISIBLE);
                lltLocationList.setVisibility(View.VISIBLE);
                break;
            case R.id.rlt_balanced_recommendation:
            case R.id.rlt_recently_active:
                getDialog("6");
                break;
            case R.id.rlt_rewind:
                getDialog("");
                break;
            case R.id.rlt_hide_ad:
                getDialog("8");
                break;
            case R.id.btn_get_plus:
                getDialog("10");
                break;

            default:
                break;
        }
    }

    public void getDialog(String index) {
        if (sessionManager.getIsOrder() && sessionManager.getPlanType().equalsIgnoreCase("gold")) {
            btnGetPlus.setVisibility(View.GONE);
        } else {
            Intent intent = new Intent(GetIgniterPlusActivity.this, IgniterPlusDialogActivity.class);
            intent.putExtra("startwith", "");
            if (sessionManager.getIsOrder() && sessionManager.getPlanType().equalsIgnoreCase("plus")) {
                intent.putExtra("type", "gold");
            } else {
                intent.putExtra("type", "plus");
            }
            startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*if (isChecked) {
            clickPos = (int) buttonView.getTag();
            switchPosition(isChecked);
        }*/
        clickPos = (int) buttonView.getTag();
        switchPosition(isChecked);
    }

    public void switchPosition(boolean isChecked) {
        switch (clickPos) {
            case 1:
                /*getDialog("7");
                swUnlimitedSwipe.setChecked(false);*/
                unLimitedLikes = isChecked ? "yes" : "no";
                break;
            case 2:
                /*getDialog("2");
                swIgniterBoost.setChecked(false);*/
                boostPlan = isChecked ? "yes" : "no";
                break;
            case 3:
                /*getDialog("4");
                swHideAge.setChecked(false);*/
                showMyAge = isChecked ? "no" : "yes";
                break;
            case 4:
                /*getDialog("4");
                swHideLocation.setChecked(false);*/
                showMyDistance = isChecked ? "no" : "yes";
                break;
            case 5:
                /*getDialog("5");
                swExtraSuperLike.setChecked(false);*/
                extraSuperLikes = isChecked ? "yes" : "no";
                break;
            case 6:
                /*getDialog("");
                swRewind.setChecked(false);*/
                rewind = isChecked ? "yes" : "no";
                break;
            case 7:
                /*getDialog("8");
                swHideAds.setChecked(false);*/
                break;
            default:
                break;
        }
    }

    private HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("unlimited_likes", unLimitedLikes);
        hashMap.put("boost_plan", boostPlan);
        hashMap.put("extra_super_likes", extraSuperLikes);
        hashMap.put("rewind", rewind);
        hashMap.put("show_my_age", showMyAge);
        hashMap.put("show_my_distance", showMyDistance);
        hashMap.put("token", sessionManager.getToken());

        return hashMap;
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_GET_PLUS_SETTINGS:
                if (jsonResp.isSuccess()) onSuccessGetSettings(jsonResp);
                break;
            case REQ_UPDATE_PLUS_SETTINGS:
                if (jsonResp.isSuccess()) {
                    onBackPressed();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessGetSettings(JsonResponse jsonResp) {

        plusSettingsModel = gson.fromJson(jsonResp.getStrResponse(), PlusSettingsModel.class);
        updateView();
    }

    public void updateView() {

        if (!TextUtils.isEmpty(plusSettingsModel.getSearchLocation())) {
            tvMyCurrentLocation.setText(plusSettingsModel.getSearchLocation());
        } else {
            tvMyCurrentLocation.setText(plusSettingsModel.getSearchLocation());
        }

        if (!TextUtils.isEmpty(plusSettingsModel.getIsOrder()) && plusSettingsModel.getIsOrder().equalsIgnoreCase("Yes")) {
            sessionManager.setIsOrder(true);
            sessionManager.setPlanType(plusSettingsModel.getPlanType());
        } else {
            sessionManager.setIsOrder(false);
            sessionManager.setPlanType(plusSettingsModel.getPlanType());
        }

        if (sessionManager.getIsOrder() && sessionManager.getPlanType().equalsIgnoreCase("gold")) {
            btnGetPlus.setVisibility(View.GONE);
        } else if (sessionManager.getIsOrder()) {
            btnGetPlus.setText(getResources().getString(R.string.header_igniter_gold));
        }


        if (!TextUtils.isEmpty(plusSettingsModel.getUnLimitedLikes())) {
            unLimitedLikes = plusSettingsModel.getUnLimitedLikes();
            if (unLimitedLikes.equalsIgnoreCase("yes")) {
                swUnlimitedSwipe.setChecked(true);
            } else {
                swUnlimitedSwipe.setChecked(false);
            }
        } else {
            unLimitedLikes = "no";
            swUnlimitedSwipe.setChecked(false);
        }

        if (!TextUtils.isEmpty(plusSettingsModel.getBoostPlan())) {
            boostPlan = plusSettingsModel.getBoostPlan();
            if (boostPlan.equalsIgnoreCase("yes")) {
                swIgniterBoost.setChecked(true);
            } else {
                swIgniterBoost.setChecked(false);
            }
        } else {
            boostPlan = "no";
            swIgniterBoost.setChecked(false);
        }

        if (!TextUtils.isEmpty(plusSettingsModel.getExtraSuperLikes())) {
            extraSuperLikes = plusSettingsModel.getExtraSuperLikes();
            if (extraSuperLikes.equalsIgnoreCase("yes")) {
                swExtraSuperLike.setChecked(true);
            } else {
                swExtraSuperLike.setChecked(false);
            }
        } else {
            extraSuperLikes = "no";
            swExtraSuperLike.setChecked(false);
        }

        if (!TextUtils.isEmpty(plusSettingsModel.getRewind())) {
            rewind = plusSettingsModel.getRewind();
            if (rewind.equalsIgnoreCase("yes")) {
                swRewind.setChecked(true);
            } else {
                swRewind.setChecked(false);
            }
        } else {
            rewind = "no";
            swRewind.setChecked(false);
        }

        swUnlimitedSwipe.setClickable(false);
        swIgniterBoost.setClickable(false);
        swExtraSuperLike.setClickable(false);
        swRewind.setClickable(false);

        if (sessionManager.getIsOrder()) {
            if (!TextUtils.isEmpty(plusSettingsModel.getShowMyAge())) {
                showMyAge = plusSettingsModel.getShowMyAge();
                if (showMyAge.equalsIgnoreCase("no")) {
                    swHideAge.setChecked(true);
                } else {
                    swHideAge.setChecked(false);
                }
            } else {
                showMyAge = "yes";
                swHideAge.setChecked(false);
            }

            if (!TextUtils.isEmpty(plusSettingsModel.getShowMyDistance())) {
                showMyDistance = plusSettingsModel.getShowMyDistance();
                if (showMyDistance.equalsIgnoreCase("no")) {
                    swHideLocation.setChecked(true);
                } else {
                    swHideLocation.setChecked(false);
                }
            } else {
                showMyDistance = "yes";
                swHideLocation.setChecked(false);
            }
        } else {
            showMyAge = "yes";
            swHideAge.setChecked(false);
            swHideAge.setClickable(false);
            showMyDistance = "yes";
            swHideLocation.setChecked(false);
            swHideLocation.setClickable(false);
        }
        if (plusSettingsModel.getLocationModels() != null && plusSettingsModel.getLocationModels().size() > 0) {
            locationModels.clear();
            locationModels.addAll(plusSettingsModel.getLocationModels());
            setLocationListAdapter();
        } else {
            //rltEmptyChat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Settings", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {

        }

        if (resultCode == REQ_ADD_LOCATION && data != null) {
            ArrayList<LocationModel> locationModel;
            locationModel = (ArrayList<LocationModel>) data.getSerializableExtra("location");
            if (locationModel != null && locationModel.size() > 0) {
                locationModels.clear();
                locationModels.addAll(locationModel);
                setLocationListAdapter();
            } else {
                //rltEmptyChat.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setLocationListAdapter() {
        if (locationModels.size() > 0) {
            locationListAdapter = new LocationListAdapter(this, locationModels);
            rvLocationList.setAdapter(locationListAdapter);
            linearLayoutManager = (CustomLayoutManager) rvLocationList.getLayoutManager();
        } else {
            locationListAdapter = new LocationListAdapter(this);
            rvLocationList.setAdapter(locationListAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSettingsDetails();
    }
}
