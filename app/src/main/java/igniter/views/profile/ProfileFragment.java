package igniter.views.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category ProfileFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.obs.CustomButton;
import com.obs.CustomTextView;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import igniter.R;
import igniter.adapters.profile.IgniterSliderAdapter;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.MyProfileModel;
import igniter.interfaces.ActivityListener;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.ImageUtils;
import igniter.utils.RequestCallback;
import igniter.views.customize.CirclePageIndicator;
import igniter.views.customize.CustomDialog;
import igniter.views.customize.IgniterViewPager;
import igniter.views.main.HomeActivity;
import igniter.views.main.IgniterPlusDialogActivity;

import static igniter.utils.Enums.REQ_GET_MY_PROFILE;
import static igniter.utils.Enums.REQ_UPDATE_PROFILE;

/*****************************************************************
 User home profile page (Contain settings and editprofile)
 ****************************************************************/
public class ProfileFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener, ServiceListener {
    /**
     * Injection for common class
     */
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


    /**
     * Variable for the profile page
     */
    private View view;
    private ActivityListener listener;
    private Resources res;
    private HomeActivity mActivity;
    private CircleImageView civUserImage;
    private CustomTextView tvUserNameAge, tvUserJob, tvUserSchool, tvUserSearchLocation, tvSuperLikesCount, tvBoostCount;
    private CustomButton btnIgniterPlus;
    private LinearLayout lltSettings, lltEditProfile, lltRemainingCount, lltSuperLike, lltBoost, lltUpgrade;
    private RelativeLayout rltViewPager;
    private IgniterViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    private boolean isSwiping = false;
    private AlertDialog dialog;
    private Timer swipeTimer = new Timer();
    private MyProfileModel myProfileModel;
    private boolean isFirst = true;

    /**
     * Hide keyboard after change made
     * @param ctx
     */
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAutoSwiping();
        try {
            listener = (ActivityListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Profile must implement ActivityListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        AppController.getAppComponent().inject(this);

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.profile_fragment, container, false);
        }

        return view;
    }

    /**
     * Fragment variable declare
     * @param v
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        lltSettings = (LinearLayout) view.findViewById(R.id.llt_settings);
        lltEditProfile = (LinearLayout) view.findViewById(R.id.llt_edit_profile);
        lltRemainingCount = (LinearLayout) view.findViewById(R.id.llt_remaining_count);
        lltSuperLike = (LinearLayout) view.findViewById(R.id.llt_super_like);
        lltBoost = (LinearLayout) view.findViewById(R.id.llt_boost);
        lltUpgrade = (LinearLayout) view.findViewById(R.id.llt_upgrade);

        rltViewPager = (RelativeLayout) view.findViewById(R.id.rlt_view_pager);

        tvSuperLikesCount = (CustomTextView) view.findViewById(R.id.tv_super_likes_count);
        tvBoostCount = (CustomTextView) view.findViewById(R.id.tv_boost_count);
        tvUserSearchLocation = (CustomTextView) view.findViewById(R.id.tv_user_search_location);
        tvUserNameAge = (CustomTextView) view.findViewById(R.id.tv_user_name_age);
        tvUserJob = (CustomTextView) view.findViewById(R.id.tv_user_job);
        tvUserSchool = (CustomTextView) view.findViewById(R.id.tv_user_school);

        civUserImage = (CircleImageView) view.findViewById(R.id.civ_profile_image);
        btnIgniterPlus = (CustomButton) view.findViewById(R.id.btn_igniter_plus);

        dialog = commonMethods.getAlertDialog(mActivity);

        viewPager = (IgniterViewPager) view.findViewById(R.id.viewpager);
        viewPager.setAdapter(new IgniterSliderAdapter(mActivity));

        pageIndicator = (CirclePageIndicator) view.findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager, 0);


        initListeners();
        initPageIndicator();

    }

    /**
     * Profile page API call
     */
    private void getProfileDetails() {
        apiService.getMyProfileDetail(sessionManager.getToken()).enqueue(new RequestCallback(REQ_GET_MY_PROFILE, this));
    }


    /**
     * Listeners
     */
    private void initListeners() {

        viewPager.addOnPageChangeListener(this);
        lltSettings.setOnClickListener(this);
        lltEditProfile.setOnClickListener(this);
        btnIgniterPlus.setOnClickListener(this);
        civUserImage.setOnClickListener(this);

        lltUpgrade.setOnClickListener(this);
        lltSuperLike.setOnClickListener(this);
        lltBoost.setOnClickListener(this);

    }

    /**
     * Call API for page resume
     */
    @Override
    public void onResume() {
        super.onResume();
        getProfileDetails();
    }

    /**
     * Page indicator design
     */
    private void initPageIndicator() {
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(3 * density);
        pageIndicator.setPageColor(ContextCompat.getColor(mActivity, R.color.gray_text_color));
        pageIndicator.setStrokeColor(ContextCompat.getColor(mActivity, R.color.gray_text_color));
        pageIndicator.setOnClickListener(null);
        pageIndicator.setExtraSpacing((float) (1.5 * density));
        updatePageIndicator(0);
    }

    /**
     * Auto swipe function
     */
    private void setupAutoSwiping() {
        final Handler handler = new Handler();
        final Runnable updateSlider = new Runnable() {
            public void run() {
                if (myProfileModel != null) {
                    if (isFirst && (myProfileModel.getIsOrder() != null || myProfileModel.getIsOrder().equalsIgnoreCase("yes"))) {
                        isFirst = false;
                        if (myProfileModel.getPlanType().equalsIgnoreCase("Gold"))
                            viewPager.setCurrentItem(0);
                        else
                            viewPager.setCurrentItem(1);
                    } else {
                        int currentPosition = viewPager.getCurrentItem() + 1;
                        if (currentPosition == 6) {
                            viewPager.setAdapter(new IgniterSliderAdapter(mActivity));
                            updatePageIndicator(0);
                        } else {
                            viewPager.setCurrentItem(currentPosition);
                        }
                    }
                } else {
                    int currentPosition = viewPager.getCurrentItem() + 1;
                    if (currentPosition == 6) {
                        viewPager.setAdapter(new IgniterSliderAdapter(mActivity));
                        updatePageIndicator(0);
                    } else {
                        viewPager.setCurrentItem(currentPosition);
                    }
                }

            }
        };

        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isSwiping) {
                    handler.post(updateSlider);
                }
            }
        }, 3000, 3000);

    }

    /**
     * Method called for make circle page indicator setup.
     */
    private void updatePageIndicator(int position) {
        if (myProfileModel != null) {
            if (myProfileModel.getIsOrder() != null && myProfileModel.getIsOrder().equalsIgnoreCase("yes")) {
                if (myProfileModel.getPlanType().equalsIgnoreCase("Gold")) {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_gold));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.btn_yellow));
                } else {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
                }
            } else {
                if (position == 0) {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.get_gold));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.btn_yellow));
                } else {
                    btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                    btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
                }
                btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
            }
        } else {
            if (position == 0) {
                btnIgniterPlus.setText(mActivity.getResources().getString(R.string.get_gold));
                btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.btn_yellow));
            } else {
                btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
                btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
            }
            btnIgniterPlus.setText(mActivity.getResources().getString(R.string.my_igniter_plus));
            btnIgniterPlus.setTextColor(mActivity.getResources().getColor(R.color.color_accent));
        }

        switch (position) {
            /*case 0:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_yellow));
            break;*/
            case 0:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_violet));
                break;
            case 1:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_blue));
                break;
            case 2:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.login_fb_bg));
                break;
            case 3:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.choose_gradient_end));
                break;
            case 4:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_yellow));
                break;
            case 5:
                pageIndicator.setFillColor(ContextCompat.getColor(mActivity, R.color.btn_green));
                break;
            default:
                break;
        }
    }

    private void init() {
        if (listener == null) return;
        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (HomeActivity) getActivity();
    }

    /**
     * Cancel swipe after destroy
     */
    @Override
    public void onDestroy() {
        if (listener != null) listener = null;
        super.onDestroy();
        swipeTimer.cancel();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updatePageIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * On Click action for profile page button and views
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.civ_profile_image:
                intent = new Intent(mActivity, EnlargeProfileActivity.class);
                intent.putExtra("navType", 3);
                intent.putExtra("currentUser",1);
                intent.putExtra("userId", myProfileModel.getUserId());
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);

                break;
            case R.id.llt_settings:
                intent = new Intent(mActivity, SettingsActivity.class);
                if (myProfileModel != null)
                    intent.putExtra("matching_profile", myProfileModel.getMatchingProfile());
                else
                    intent.putExtra("matching_profile", "");
                startActivity(intent);
                break;
            case R.id.llt_upgrade:
                callIntent("gold");
                break;
            case R.id.llt_super_like:
                callIntent("super_like");
                break;
            case R.id.llt_boost:
                callIntent("boost");
                break;
            case R.id.llt_edit_profile:
                if (myProfileModel != null && !TextUtils.isEmpty(myProfileModel.getName())) {
                    intent = new Intent(mActivity, EditProfileActivity.class);
                    intent.putExtra("userName", myProfileModel.getName());
                    startActivity(intent);
                }
                break;
            case R.id.btn_igniter_plus:
                if (btnIgniterPlus.getText().toString().equals(mActivity.getResources().getString(R.string.get_gold))) {
                    callIntent("gold");
                }
                if (btnIgniterPlus.getText().toString().equals(mActivity.getResources().getString(R.string.my_igniter_plus))) {
                    //callIntent("plus");
                    intent = new Intent(mActivity, GetIgniterPlusActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(mActivity, GetIgniterPlusActivity.class);
                    startActivity(intent);
                }

                break;
            default:
                break;
        }
    }


    /**
     * API success response
     * @param jsonResp
     * @param data
     */
    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(mActivity, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_UPDATE_PROFILE:
                break;
            case REQ_GET_MY_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessGetMyProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    /*if(jsonResp.getStatusMsg().equalsIgnoreCase("Token Expired")){
                        getProfileDetails();
                    }else {*/
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                    //}
                }
                break;
            default:
                break;
        }
    }

    /**
     * API failure response
     * @param jsonResp
     * @param data
     */

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(mActivity, dialog, data);
    }

    private void onSuccessGetMyProfile(JsonResponse jsonResp) {
        myProfileModel = gson.fromJson(jsonResp.getStrResponse(), MyProfileModel.class);

        if (myProfileModel != null) {
            updateView();
        }
    }

    /**
     * Update data in the view based on the API response
     */
    private void updateView() {

        if (myProfileModel == null) return;

        try{
            if (myProfileModel.getUnReadCount() != null && myProfileModel.getUnReadCount() > 0) {
                ((HomeActivity) getActivity()).changeChatIcon(1);
            } else {
                ((HomeActivity) getActivity()).changeChatIcon(0);
            }
        }catch(Exception e){

        }


        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(myProfileModel.getName())) {
            sb.append(myProfileModel.getName());
            sb.append(", ");
            sb.append(myProfileModel.getAge());
            tvUserNameAge.setText(sb.toString());
            sessionManager.setUserName(myProfileModel.getName());
        }

        if (!TextUtils.isEmpty(myProfileModel.getSearchLocation()) && !myProfileModel.getSearchLocation().equalsIgnoreCase(mActivity.getResources().getString(R.string.res_current_location))) {
            tvUserSearchLocation.setText(myProfileModel.getSearchLocation());
            tvUserSearchLocation.setVisibility(View.VISIBLE);
        } else {
            tvUserSearchLocation.setText(myProfileModel.getSearchLocation());
            tvUserSearchLocation.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(myProfileModel.getJobTitle()) && !TextUtils.isEmpty(myProfileModel.getWork())) {

            tvUserJob.setText(myProfileModel.getJobTitle() + " at " + myProfileModel.getWork());
        } else if (TextUtils.isEmpty(myProfileModel.getJobTitle())) {
            tvUserJob.setText(myProfileModel.getWork());
        } else if (TextUtils.isEmpty(myProfileModel.getWork())) {
            tvUserJob.setText(myProfileModel.getJobTitle());
        }
        if (!TextUtils.isEmpty(myProfileModel.getCollege())) {
            tvUserSchool.setText(myProfileModel.getCollege());
        } else {
            tvUserSchool.setText("");
        }
        if (myProfileModel.getImages() != null && myProfileModel.getImages().size() > 0 && !TextUtils.isEmpty(myProfileModel.getImages().get(0))) {
            imageUtils.loadCircleImage(mActivity, civUserImage, myProfileModel.getImages().get(0));
            sessionManager.setProfileImg(myProfileModel.getImages().get(0));
        }

        if (myProfileModel.getIsOrder() != null && myProfileModel.getIsOrder().equalsIgnoreCase("yes")) {
            rltViewPager.setVisibility(View.GONE);
            lltRemainingCount.setVisibility(View.VISIBLE);
            if (myProfileModel.getPlanType().equalsIgnoreCase("Gold")) {
                lltUpgrade.setVisibility(View.GONE);
            }
        } else {
            lltRemainingCount.setVisibility(View.GONE);
            rltViewPager.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(myProfileModel.getRemainingLikesCount())) {
            sessionManager.setRemainingSuperLikes(Integer.parseInt(myProfileModel.getRemainingLikesCount()));
            tvSuperLikesCount.setText(myProfileModel.getRemainingLikesCount());
        }

        if (!TextUtils.isEmpty(myProfileModel.getRemainingBoostCount())) {
            tvBoostCount.setText(myProfileModel.getRemainingBoostCount());
            sessionManager.setRemainingBoost(Integer.parseInt(myProfileModel.getRemainingBoostCount()));
        }

       /* if(!TextUtils.isEmpty(myProfileModel.getRemainingLike())) {
            sessionManager.setRemainingLikes(Integer.parseInt(myProfileModel.getRemainingLike()));
        }

        if(!TextUtils.isEmpty(myProfileModel.getIsLikesLimited())) {
            sessionManager.setIsRemainingLikeLimited(myProfileModel.getIsLikesLimited());
        }*/
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && view != null) {
            getProfileDetails();
            hideKeyboard(getContext());
        }
    }

    /**
     * Call plan intent
     * @param type
     */
    private void callIntent(String type) {
        Intent intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
        intent.putExtra("startwith", "");
        intent.putExtra("type", type);
        startActivity(intent);
    }

}
