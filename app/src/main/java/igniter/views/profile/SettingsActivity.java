package igniter.views.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category EditProfileActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
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
import igniter.datamodels.main.SettingsModel;
import igniter.iaputils.IabBroadcastReceiver;
import igniter.iaputils.IabHelper;
import igniter.iaputils.IabResult;
import igniter.iaputils.Inventory;
import igniter.iaputils.Purchase;
import igniter.interfaces.ApiService;
import igniter.interfaces.OnRangeSeekBarChangeListener;
import igniter.interfaces.OnRangeSeekBarFinalValueListener;
import igniter.interfaces.OnSeekBarChangeListener;
import igniter.interfaces.OnSeekBarFinalValueListener;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;
import igniter.views.customize.CustomLayoutManager;
import igniter.views.customize.CustomRecyclerView;
import igniter.views.customize.CustomSeekBar;
import igniter.views.customize.RangeSeekBar;
import igniter.views.main.IgniterPlusDialogActivity;
import igniter.views.main.LoginActivity;
import igniter.views.main.UserNameActivity;

import static igniter.utils.Enums.REQ_ADD_LOCATION;
import static igniter.utils.Enums.REQ_GET_SETTINGS;
import static igniter.utils.Enums.REQ_LOGOUT;
import static igniter.utils.Enums.REQ_UPDATE_SETTINGS;

/*****************************************************************
 User match profile search setting pageNSLocalizedString
 ****************************************************************/
public class SettingsActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener, ServiceListener, CompoundButton.OnCheckedChangeListener {

    // Debug tag, for logging
    static final String TAG = "Boost In App Purchase";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 101;
    // SKU for our subscription (infinite Boost)
    static String SKU_INFINITE_ONE_BOOST = "";
    static String SKU_INFINITE_FIVE_BOOST = "";
    static String SKU_INFINITE_TEN_BOOST = "";
    static String SKU_INFINITE_5_SL = "";
    static String SKU_INFINITE_25_SL = "";
    static String SKU_INFINITE_60_SL = "";
    static String SKU_INFINITE_1_IG = "";
    static String SKU_INFINITE_6_IG = "";
    static String SKU_INFINITE_12_IG = "";
    static String SKU_INFINITE_1_IP = "";
    static String SKU_INFINITE_6_IP = "";
    static String SKU_INFINITE_12_IP = "";
    RelativeLayout women_layout, men_layout;
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
    String type;
    // Used to select between purchasing Boost on a monthly ,half yearly or yearly basis
    String mSelectedSubscriptionPeriod = "";
    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    String payload = "";
    // Does the user have an active subscription to the infinite Boost plan?
    boolean mSubscribedToBoost = false;
    // Does the user have an active subscription to the infinite Super like plan?
    boolean mSubscribedToSuperLike = false;
    // Does the user have an active subscription to the infinite Igniter Plus plan?
    boolean mSubscribedToPlus = false;
    // Does the user have an active subscription to the infinite Igniter Gold plan?
    boolean mSubscribedToGold = false;
    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    // Tracks the currently owned infinite Boost SKU, and the options in the Manage dialog
    String mInfiniteBoostSku = "";
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.e(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain(getString(R.string.failed_query_inv)+": " + result);
                return;
            }

            Log.e(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            /*// Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.e(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            */

            // First find out which subscription is auto renewing
            Purchase oneBoost = inventory.getPurchase(SKU_INFINITE_ONE_BOOST);
            Purchase fiveBoost = inventory.getPurchase(SKU_INFINITE_FIVE_BOOST);
            Purchase tenBoost = inventory.getPurchase(SKU_INFINITE_TEN_BOOST);

            Purchase SL5 = inventory.getPurchase(SKU_INFINITE_5_SL);
            Purchase SL25 = inventory.getPurchase(SKU_INFINITE_25_SL);
            Purchase SL60 = inventory.getPurchase(SKU_INFINITE_60_SL);

            Purchase IP1 = inventory.getPurchase(SKU_INFINITE_1_IP);
            Purchase IP6 = inventory.getPurchase(SKU_INFINITE_6_IP);
            Purchase IP12 = inventory.getPurchase(SKU_INFINITE_12_IP);

            Purchase IG1 = inventory.getPurchase(SKU_INFINITE_1_IG);
            Purchase IG6 = inventory.getPurchase(SKU_INFINITE_6_IG);
            Purchase IG12 = inventory.getPurchase(SKU_INFINITE_12_IG);

            if (oneBoost != null && oneBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_ONE_BOOST;
                mAutoRenewEnabled = true;
            } else if (fiveBoost != null && fiveBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_FIVE_BOOST;
                mAutoRenewEnabled = true;
            } else if (tenBoost != null && tenBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_TEN_BOOST;
                mAutoRenewEnabled = true;
            } else if (SL5 != null && SL5.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_5_SL;
                mAutoRenewEnabled = true;
            } else if (SL25 != null && SL25.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_25_SL;
                mAutoRenewEnabled = true;
            } else if (SL60 != null && SL60.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_60_SL;
                mAutoRenewEnabled = true;
            } else if (IP1 != null && IP1.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_1_IP;
                mAutoRenewEnabled = true;
            } else if (IP6 != null && IP6.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_6_IP;
                mAutoRenewEnabled = true;
            } else if (IP12 != null && IP12.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_12_IP;
                mAutoRenewEnabled = true;
            } else if (IG1 != null && IG1.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_1_IG;
                mAutoRenewEnabled = true;
            } else if (IG6 != null && IG6.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_6_IG;
                mAutoRenewEnabled = true;
            } else if (IG12 != null && IG12.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_12_IG;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteBoostSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            if (type.equals("boost"))
                mSubscribedToBoost = (oneBoost != null && verifyDeveloperPayload(oneBoost))
                        || (fiveBoost != null && verifyDeveloperPayload(fiveBoost)
                        || (tenBoost != null && verifyDeveloperPayload(tenBoost)));
            else if (type.equals("super_like"))
                mSubscribedToSuperLike = (SL5 != null && verifyDeveloperPayload(SL5))
                        || (SL25 != null && verifyDeveloperPayload(SL25)
                        || (SL60 != null && verifyDeveloperPayload(SL60)));
            else if (type.equals("plus"))
                mSubscribedToPlus = (IP1 != null && verifyDeveloperPayload(IP1))
                        || (IP6 != null && verifyDeveloperPayload(IP6)
                        || (IP12 != null && verifyDeveloperPayload(IP12)));
            else if (type.equals("gold"))
                mSubscribedToGold = (IG1 != null && verifyDeveloperPayload(IG1))
                        || (IG6 != null && verifyDeveloperPayload(IG6)
                        || (IG12 != null && verifyDeveloperPayload(IG12)));

            Log.e(TAG, "User " + (mSubscribedToBoost ? "HAS" : "DOES NOT HAVE")
                    + " infinite Boost subscription.");
            //if (mSubscribedToInfiniteBoost) mTank = TANK_MAX;


            // Check for Boost delivery -- if we own Boost, we should fill up the tank immediately
            /*Purchase BoostPurchase = inventory.getPurchase(SKU_INFINITE_FIVE_BOOST);
            if (BoostPurchase != null && verifyDeveloperPayload(BoostPurchase)) {
                Log.d(TAG, "We have Boost. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_INFINITE_FIVE_BOOST), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming Boost. Another async operation in progress.");
                }
                return;
            }
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");*/
        }
    };
    private CustomTextView tvHeader, tvBackArrow;
    private CardView cvGetIgniterPlus, cvGetMoreBoost, cvHelpSupport, cvLogout, cvDeleteAccount, cvGetSuperLikes, cvGetIgniterGold;
    private CustomTextView tvCurrentLocation, tvMaxDistance, tvAgeRange, tvClaimYours, tvAddNewLocation;
    private CustomTextView tvClaimYoursIcon, tvViewProfile, tvShareMyUrl;
    private CustomTextView tvShowDistance, tvShareIgniter, tvRestorePurchase;
    private CustomTextView tvLicense, tvPrivacyPolicy, tvTermsService, men_women;
    private LinearLayout lltAddNewLocation;
    private SwitchCompat swMen, swWomen, swShowMeOnIgniter;
    private SwitchCompat swNewMatches, swMessages, swMessageLike, swSuperLike;
    private CustomSeekBar sbMaxDistance;
    private RangeSeekBar sbAgeRange;
    private RadioGroup rgShowDistance;
    private RadioButton rbMi, rbKm;
    private AlertDialog dialog;
    private SettingsModel settingsModel;
    private CustomRecyclerView rvLocationList;
    private CustomTextView tv_user_name, tv_men, tv_women;
    private String location = "", showMe = "", userName = "", newMatch = "", receivingMsg = "", msgLikes = "", superLike = "", adminDistanceType = "", distanceType = "", showMen = "", showWomen = "", matchingProfile = "";
    private String profileUrl = "", helpUrl = "", licenseUrl = "", privacyPolicyUrl = "", termsOfServiceUrl = "", communityUrl = "", safetyUrl = "", message = "";
    private String maxDistance, minAge, maxAge;
    private LocationListAdapter locationListAdapter;
    private LocationModel locationModel;
    private ArrayList<LocationModel> locationModels = new ArrayList<>();
    private CustomLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        AppController.getAppComponent().inject(this);

        SKU_INFINITE_ONE_BOOST = getResources().getString(R.string.iap_boost_1);
        SKU_INFINITE_FIVE_BOOST = getResources().getString(R.string.iap_boost_5);
        SKU_INFINITE_TEN_BOOST = getResources().getString(R.string.iap_boost_10);

        SKU_INFINITE_5_SL = getResources().getString(R.string.iap_superlike_5);
        SKU_INFINITE_25_SL = getResources().getString(R.string.iap_superlike_25);
        SKU_INFINITE_60_SL = getResources().getString(R.string.iap_superlike_60);

        SKU_INFINITE_1_IG = getResources().getString(R.string.iap_gold_1);
        SKU_INFINITE_6_IG = getResources().getString(R.string.iap_gold_6);
        SKU_INFINITE_12_IG = getResources().getString(R.string.iap_gold_12);

        SKU_INFINITE_1_IP = getResources().getString(R.string.iap_plus_1);
        SKU_INFINITE_6_IP = getResources().getString(R.string.iap_plus_6);
        SKU_INFINITE_12_IP = getResources().getString(R.string.iap_plus_12);

        getIntentValues();
        initView();
        initClickListeners();
        initSeekBarChangeListener();
        getSettingsDetails();

        setupHelper();
        ;
        type = "super_like";
        if (type.equals("boost"))
            mSelectedSubscriptionPeriod = SKU_INFINITE_FIVE_BOOST;
        else if (type.equals("super_like"))
            mSelectedSubscriptionPeriod = SKU_INFINITE_25_SL;
        else if (type.equals("plus"))
            mSelectedSubscriptionPeriod = SKU_INFINITE_6_IP;
        else if (type.equals("gold"))
            mSelectedSubscriptionPeriod = SKU_INFINITE_6_IG;
    }

    private void initView() {

        helpUrl = getResources().getString(R.string.redirect_url) + "" + getResources().getString(R.string.help_url);
        privacyPolicyUrl = getResources().getString(R.string.redirect_url) + "" + getResources().getString(R.string.privacy_url);
        termsOfServiceUrl = getResources().getString(R.string.redirect_url) + "" + getResources().getString(R.string.terms_url);

        System.out.println("Help url " + helpUrl);
        System.out.println("privacyPolicy url " + privacyPolicyUrl);
        System.out.println("termsOfService url " + termsOfServiceUrl);

        tvHeader = (CustomTextView) findViewById(R.id.tv_header_title);
        tvBackArrow = (CustomTextView) findViewById(R.id.tv_left_arrow);

        rvLocationList = (CustomRecyclerView) findViewById(R.id.rv_location_list);
        tvCurrentLocation = (CustomTextView) findViewById(R.id.tv_current_location);
        tvAddNewLocation = (CustomTextView) findViewById(R.id.tv_add_new_location);
        tvMaxDistance = (CustomTextView) findViewById(R.id.tv_max_distance);
        tvAgeRange = (CustomTextView) findViewById(R.id.tv_age);
        tvClaimYours = (CustomTextView) findViewById(R.id.tv_claim_yours);
        tvClaimYoursIcon = (CustomTextView) findViewById(R.id.tv_claim_yours_icon);
        tvViewProfile = (CustomTextView) findViewById(R.id.tv_view_profile);
        tvShareMyUrl = (CustomTextView) findViewById(R.id.tv_share_my_url);
        tv_user_name = (CustomTextView) findViewById(R.id.tv_user_name);

        tvShowDistance = (CustomTextView) findViewById(R.id.tv_show_distance);
        tvShareIgniter = (CustomTextView) findViewById(R.id.tv_share_igniter);
        tvRestorePurchase = (CustomTextView) findViewById(R.id.tv_restore_purchase);

        tvLicense = (CustomTextView) findViewById(R.id.tv_license);
        tvPrivacyPolicy = (CustomTextView) findViewById(R.id.tv_privacy_policy);
        tvTermsService = (CustomTextView) findViewById(R.id.tv_terms_service);
        men_women = (CustomTextView) findViewById(R.id.men_women);
        tv_men = (CustomTextView) findViewById(R.id.tv_men);
        tv_women = (CustomTextView) findViewById(R.id.tv_women);
        women_layout = (RelativeLayout) findViewById(R.id.women_layout);
        men_layout = (RelativeLayout) findViewById(R.id.men_layout);


        lltAddNewLocation = (LinearLayout) findViewById(R.id.llt_add_new_location);

        swMen = (SwitchCompat) findViewById(R.id.switch_men);
        swWomen = (SwitchCompat) findViewById(R.id.switch_women);
        swShowMeOnIgniter = (SwitchCompat) findViewById(R.id.switch_show_me);
        swNewMatches = (SwitchCompat) findViewById(R.id.switch_new_matches);
        swMessages = (SwitchCompat) findViewById(R.id.switch_messages);
        swMessageLike = (SwitchCompat) findViewById(R.id.switch_message_likes);
        swSuperLike = (SwitchCompat) findViewById(R.id.switch_super_likes);


        rgShowDistance = (RadioGroup) findViewById(R.id.rdg_distance);
        rbMi = (RadioButton) findViewById(R.id.rb_mi);
        rbKm = (RadioButton) findViewById(R.id.rb_km);

        sbAgeRange = (RangeSeekBar) findViewById(R.id.sb_age_range);
        sbMaxDistance = (CustomSeekBar) findViewById(R.id.sb_max_distance);

        cvGetIgniterPlus = (CardView) findViewById(R.id.cv_get_igniter_plus);
        cvGetMoreBoost = (CardView) findViewById(R.id.cv_get_more_boost);
        cvHelpSupport = (CardView) findViewById(R.id.cv_help_support);
        cvLogout = (CardView) findViewById(R.id.cv_logout);
        cvDeleteAccount = (CardView) findViewById(R.id.cv_delete_account);
        cvGetSuperLikes = (CardView) findViewById(R.id.cv_get_super_likes);
        cvGetIgniterGold = (CardView) findViewById(R.id.cv_get_igniter_gold);

        dialog = commonMethods.getAlertDialog(this);
        updateView();
        matchingProfile(getIntent().getStringExtra("matching_profile"));
        initRecyclerView();

    }

    private void initRecyclerView() {
        linearLayoutManager = new CustomLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        rvLocationList.setLayoutManager(linearLayoutManager);
        locationListAdapter = new LocationListAdapter(this);
        rvLocationList.setAdapter(locationListAdapter);
    }

    private void initClickListeners() {
        tvBackArrow.setOnClickListener(this);
        tvCurrentLocation.setOnClickListener(this);
        tvAddNewLocation.setOnClickListener(this);
        tvClaimYours.setOnClickListener(this);
        tvViewProfile.setOnClickListener(this);
        tvShareMyUrl.setOnClickListener(this);
        tvShareIgniter.setOnClickListener(this);
        tvRestorePurchase.setOnClickListener(this);
        tvLicense.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
        tvTermsService.setOnClickListener(this);
        tv_user_name.setOnClickListener(this);
        men_layout.setOnClickListener(this);
        women_layout.setOnClickListener(this);

        cvGetIgniterPlus.setOnClickListener(this);
        cvGetMoreBoost.setOnClickListener(this);
        cvHelpSupport.setOnClickListener(this);
        cvLogout.setOnClickListener(this);
        cvDeleteAccount.setOnClickListener(this);
        cvGetSuperLikes.setOnClickListener(this);
        cvGetIgniterGold.setOnClickListener(this);


        rgShowDistance.setOnCheckedChangeListener(this);
        swMen.setOnCheckedChangeListener(this);
        swWomen.setOnCheckedChangeListener(this);
        swShowMeOnIgniter.setOnCheckedChangeListener(this);
        swNewMatches.setOnCheckedChangeListener(this);
        swMessages.setOnCheckedChangeListener(this);
        swMessageLike.setOnCheckedChangeListener(this);
        swSuperLike.setOnCheckedChangeListener(this);


    }

    private void initSeekBarChangeListener() {
        sbMaxDistance.setOnSeekbarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                sessionManager.setSettingUpdate(true);
                if (adminDistanceType.equals(distanceType)) {
                    if (distanceType.equalsIgnoreCase("mi")) {
                        tvMaxDistance.setText(String.format(getString(R.string.miles), String.valueOf(value)));
                        maxDistance = String.valueOf(value);
                    } else {
                        tvMaxDistance.setText(String.format(getString(R.string.kilometer), String.valueOf(value)));
                        maxDistance = String.valueOf(value);
                    }
                } else {
                    if (distanceType.equalsIgnoreCase("mi") && adminDistanceType.equalsIgnoreCase("km")) {
                        double a = Integer.parseInt(value.toString()) / 1.60934;
                        int b = (int) a;
                        tvMaxDistance.setText(String.format(getString(R.string.miles), String.valueOf(b)));
                        maxDistance = String.valueOf(value);
                    } else {
                        double a = Integer.parseInt(value.toString()) * 1.60934;
                        int b = (int) a;
                        tvMaxDistance.setText(String.format(getString(R.string.kilometer), String.valueOf(b)));
                        maxDistance = String.valueOf(value);
                    }
                }

            }
        });

        sbMaxDistance.setOnSeekbarFinalValueListener(new OnSeekBarFinalValueListener() {
            @Override
            public void finalValue(Number value) {

            }
        });

        sbAgeRange.setOnRangeSeekbarChangeListener(new OnRangeSeekBarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                sessionManager.setSettingUpdate(true);
                String max = String.valueOf(maxValue);
                minAge = String.valueOf(minValue);
                maxAge = max;
                if (max.equals("55")) max = max + "+";

                tvAgeRange.setText(String.format(getString(R.string.age), minValue, max));
            }
        });

        sbAgeRange.setOnRangeSeekbarFinalValueListener(new OnRangeSeekBarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {

            }
        });
    }

    private void getIntentValues() {
    }

    private void getSettingsDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.getUserSettings(sessionManager.getToken()).enqueue(new RequestCallback(REQ_GET_SETTINGS, this));
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_left_arrow:
                commonMethods.showProgressDialog(this, customDialog);
                apiService.updateSettings(getParams()).enqueue(new RequestCallback(REQ_UPDATE_SETTINGS, this));
                break;
            case R.id.tv_current_location:
                tvCurrentLocation.setVisibility(View.GONE);
                lltAddNewLocation.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_share_igniter:
                break;
            case R.id.cv_get_super_likes:
                if (!mSubscribedToSuperLike) {
                    intent = new Intent(SettingsActivity.this, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "super_like");
                    startActivity(intent);
                } else {
                    mSubscribedToSuperLike = false;
                    complain(getString(R.string.super_like_already_purchased));
                }
                break;
            case R.id.cv_get_more_boost:
                if (!mSubscribedToBoost) {
                    intent = new Intent(SettingsActivity.this, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "boost");
                    startActivity(intent);
                } else {
                    mSubscribedToBoost = false;
                    complain(getString(R.string.boost_already_purchased));
                }
                break;
            case R.id.cv_get_igniter_plus:
                if (!mSubscribedToPlus) {
                    intent = new Intent(SettingsActivity.this, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "plus");
                    startActivity(intent);
                } else {
                    mSubscribedToGold = false;
                    //complain("Igniter Plus already purchased...");
                }
                break;
            case R.id.cv_get_igniter_gold:
                if (!mSubscribedToGold) {
                    intent = new Intent(SettingsActivity.this, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "gold");
                    startActivity(intent);
                } else {
                    mSubscribedToGold = false;
                    // complain("Igniter Gold already purchased...");
                }
                break;
            case R.id.tv_add_new_location:
                if (sessionManager.getIsOrder()) {
                    intent = new Intent(SettingsActivity.this, AddLocationActivity.class);
                    startActivityForResult(intent, REQ_ADD_LOCATION);
                } else {
                    intent = new Intent(SettingsActivity.this, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "plus");
                    startActivity(intent);
                }
                break;
            case R.id.tv_claim_yours:
                intent = new Intent(SettingsActivity.this, UserNameActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_user_name:
                intent = new Intent(SettingsActivity.this, UserNameActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.tv_view_profile:
                loadUrl(profileUrl);
                break;
            case R.id.tv_share_my_url:
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setType("message/rfc822");
                mailIntent.setType("text/html");
                mailIntent.putExtra(Intent.EXTRA_TEXT, userName);
                try {
                    startActivity(Intent.createChooser(mailIntent, getString(R.string.user_name)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, getString(R.string.user_name), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_restore_purchase:
                break;
            case R.id.tv_license:
                //loadUrl(licenseUrl);
                break;
            case R.id.tv_privacy_policy:
                loadUrl(privacyPolicyUrl);
                break;
            case R.id.tv_terms_service:
                loadUrl(termsOfServiceUrl);
                break;
            case R.id.cv_help_support:
                System.out.println("Help url " + helpUrl);
                System.out.println("privacyPolicy url " + privacyPolicyUrl);
                System.out.println("termsOfService url " + termsOfServiceUrl);
                loadUrl(helpUrl);
                break;
            case R.id.cv_logout:
                String msg = getResources().getString(R.string.suretolog);
                String title = getResources().getString(R.string.logout) + "?";
                String btnText = getResources().getString(R.string.logout);
                showDialog(title, msg, btnText, 0);
                break;
            case R.id.cv_delete_account:
                break;
            case R.id.men_layout:
                swMen.setChecked(!swMen.isChecked());
                switchMen(swMen.isChecked());
                break;
            case R.id.women_layout:
                swWomen.setChecked(!swWomen.isChecked());
                switchWomen(swWomen.isChecked());
                break;
            default:
                break;
        }
    }

    private void logout() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.logout(sessionManager.getToken()).enqueue(new RequestCallback(REQ_LOGOUT, this));
    }

    private void showDialog(String title, String msg, String buttonText, final int index) {

        customDialog = new CustomDialog(index, title, msg, buttonText, getResources().getString(R.string.cancel), new CustomDialog.btnAllowClick() {
            @Override
            public void clicked() {
                logout();
            }

        }, null);
        customDialog.show(SettingsActivity.this.getSupportFragmentManager(), "");

    }

    private void loadUrl(String url) {
        System.out.println("url " + url);
        if (!TextUtils.isEmpty(url)) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", sessionManager.getToken());
        hashMap.put("matching_profile", matchingProfile);
        hashMap.put("distance", maxDistance);
        hashMap.put("min_age", minAge);
        hashMap.put("max_age", maxAge);
        hashMap.put("show_me", showMe);
        hashMap.put("distance_type", distanceType);
        hashMap.put("new_matches", newMatch);
        hashMap.put("messages", message);
        hashMap.put("message_likes", msgLikes);
        hashMap.put("super_likes", superLike);
        /*hashMap.put("latitude", "100");
        hashMap.put("longitude", "97");*/

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
            case REQ_GET_SETTINGS:
                if (jsonResp.isSuccess()) onSuccessGetSettings(jsonResp);
                break;
            case REQ_UPDATE_SETTINGS:
                if (jsonResp.isSuccess()) {
                    onBackPressed();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_LOGOUT:
                if (jsonResp.isSuccess()) {
                    sessionManager.clearToken();
                    sessionManager.clearAll();
                    finishAffinity();
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    sessionManager.clearToken();
                    sessionManager.clearAll();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessGetSettings(JsonResponse jsonResp) {

        settingsModel = gson.fromJson(jsonResp.getStrResponse(), SettingsModel.class);
        //locationModel = gson.fromJson(jsonResp.getStrResponse(), LocationModel.class);
        updateView();
    }

    private void updateView() {
        tvHeader.setText(getString(R.string.header_settings));
        if (settingsModel == null) return;

        if (!TextUtils.isEmpty(settingsModel.getIsOrder()) && settingsModel.getIsOrder().equalsIgnoreCase("Yes")) {
            sessionManager.setIsOrder(true);
            sessionManager.setPlanType(settingsModel.getPlanType());
        } else {
            sessionManager.setIsOrder(false);
            sessionManager.setPlanType(settingsModel.getPlanType());
        }

        if (sessionManager.getIsOrder()) {
            if (!TextUtils.isEmpty(sessionManager.getPlanType()) && sessionManager.getPlanType().equalsIgnoreCase("gold")) {
                cvGetIgniterGold.setVisibility(View.GONE);
                cvGetIgniterPlus.setVisibility(View.GONE);
            } else if (!TextUtils.isEmpty(sessionManager.getPlanType()) && sessionManager.getPlanType().equalsIgnoreCase("plus")) {
                cvGetIgniterGold.setVisibility(View.VISIBLE);
                cvGetIgniterPlus.setVisibility(View.GONE);
            } else {
                cvGetIgniterGold.setVisibility(View.VISIBLE);
                cvGetIgniterPlus.setVisibility(View.VISIBLE);
            }
        } else {
            cvGetIgniterGold.setVisibility(View.VISIBLE);
            cvGetIgniterPlus.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(settingsModel.getSearchLocation())) {
            tvCurrentLocation.setText(settingsModel.getSearchLocation());
        } else {
            tvCurrentLocation.setText(settingsModel.getSearchLocation());
        }

        swMen.setChecked(false);
        swMen.setChecked(false);
        swWomen.setChecked(false);
        sbAgeRange.setMaxValue(settingsModel.getMaximumAge()).setMinValue(settingsModel.getMinimumAge()).apply();
        sbAgeRange.setMaxStartValue(settingsModel.getMaxAge()).setMinStartValue(settingsModel.getMinAge()).apply();

        sbMaxDistance.setMinValue(settingsModel.getMinimumDistance()).setMaxValue(settingsModel.getMaximumDistance()).apply();
        sbMaxDistance.setMinStartValue(settingsModel.getMaxDistance()).apply();

        tvMaxDistance.setText(String.format(getString(R.string.miles), "" + settingsModel.getMaxDistance()));
        tvAgeRange.setText(String.format(getString(R.string.age), "" + settingsModel.getMinAge(), "" + settingsModel.getMaxAge()));

        if (!TextUtils.isEmpty(settingsModel.getShowMe())) {
            showMe = settingsModel.getShowMe();
            if (showMe.equalsIgnoreCase("yes")) {
                swShowMeOnIgniter.setChecked(true);
            } else {
                swShowMeOnIgniter.setChecked(false);
            }
        } else {
            showMe = "no";
            swShowMeOnIgniter.setChecked(false);
        }
        if (!TextUtils.isEmpty(settingsModel.getUserName())) {
            userName = settingsModel.getUserName();
            tvClaimYours.setText(userName);
            tvClaimYoursIcon.setVisibility(View.GONE);
            tvShareMyUrl.setVisibility(View.VISIBLE);
            tvViewProfile.setVisibility(View.VISIBLE);
        } else {
            userName = "";
            tvClaimYours.setText(getString(R.string.claim_your));
            tvClaimYoursIcon.setVisibility(View.VISIBLE);
            tvShareMyUrl.setVisibility(View.GONE);
            tvViewProfile.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(settingsModel.getNewMatch())) {
            newMatch = settingsModel.getNewMatch();
            if (newMatch.equalsIgnoreCase("yes")) {
                swNewMatches.setChecked(true);
            } else {
                swNewMatches.setChecked(false);
            }
        } else {
            newMatch = "no";
            swNewMatches.setChecked(false);
        }

        if (!TextUtils.isEmpty(settingsModel.getReceivingMessage())) {
            receivingMsg = settingsModel.getReceivingMessage();
            if (receivingMsg.equalsIgnoreCase("yes")) {
                swMessages.setChecked(true);
            } else {
                swMessages.setChecked(false);
            }
        } else {
            receivingMsg = "no";
            swMessages.setChecked(false);
        }

        if (!TextUtils.isEmpty(settingsModel.getMessageLikes())) {
            msgLikes = settingsModel.getMessageLikes();
            if (msgLikes.equalsIgnoreCase("yes")) {
                swMessageLike.setChecked(true);
            } else {
                swMessageLike.setChecked(false);
            }
        } else {
            msgLikes = "no";
            swMessageLike.setChecked(false);
        }
        if (!TextUtils.isEmpty(settingsModel.getSuperLikes())) {
            superLike = settingsModel.getSuperLikes();
            if (superLike.equalsIgnoreCase("yes")) {
                swSuperLike.setChecked(true);
            } else {
                swSuperLike.setChecked(false);
            }
        } else {
            superLike = "no";
            swSuperLike.setChecked(false);
        }

        /**
         * Matching profile
         */
        matchingProfile(settingsModel.getMatchingProfile());

        if (!TextUtils.isEmpty(settingsModel.getDistanceType())) {
            adminDistanceType = settingsModel.getAdminDistanceType();
            distanceType = settingsModel.getDistanceType();

            if (adminDistanceType.equals(distanceType)) {
                if (distanceType.equalsIgnoreCase("mi")) {
                    tvMaxDistance.setText(String.format(getString(R.string.miles), String.valueOf(maxDistance)));
                    maxDistance = String.valueOf(maxDistance);
                    rbMi.setChecked(true);
                    tvShowDistance.setText(getString(R.string.mile));

                } else {
                    tvMaxDistance.setText(String.format(getString(R.string.kilometer), String.valueOf(maxDistance)));
                    maxDistance = String.valueOf(maxDistance);

                    tvShowDistance.setText(getString(R.string.km));
                    rbKm.setChecked(true);
                }
            } else {
                if (distanceType.equalsIgnoreCase("mi") && adminDistanceType.equalsIgnoreCase("km")) {
                    double a = Integer.parseInt(maxDistance) / 1.60934;
                    int b = (int) a;
                    rbMi.setChecked(true);
                    tvShowDistance.setText(getString(R.string.mile));
                    tvMaxDistance.setText(getString(R.string.miles, String.valueOf(b)));
                } else {
                    tvShowDistance.setText(getString(R.string.km));
                    double a = Integer.parseInt(maxDistance) * 1.60934;
                    int b = (int) a;
                    tvMaxDistance.setText(getString(R.string.kilometer, String.valueOf(b)));
                    rbKm.setChecked(true);
                }
            }
        } else {
            distanceType = "mi";
            rbMi.setChecked(true);
            tvShowDistance.setText(getString(R.string.mile));
        }
        /*if (!TextUtils.isEmpty(settingsModel.getLocation())) {
            location = settingsModel.getLocation();
        }*/
        /*if (!TextUtils.isEmpty(settingsModel.getProfileUrl())) {
            profileUrl = settingsModel.getProfileUrl();
        }
        if (!TextUtils.isEmpty(settingsModel.getHelpUrl())) {
            helpUrl = settingsModel.getHelpUrl();
        }
        if (!TextUtils.isEmpty(settingsModel.getLicenseUrl())) {
            licenseUrl = settingsModel.getLicenseUrl();
        }
        if (!TextUtils.isEmpty(settingsModel.getPrivacyPolicyUrl())) {
            privacyPolicyUrl = settingsModel.getPrivacyPolicyUrl();
        }
        if (!TextUtils.isEmpty(settingsModel.getTermsOfServiceUrl())) {
            termsOfServiceUrl = settingsModel.getTermsOfServiceUrl();
        }
        if (!TextUtils.isEmpty(settingsModel.getCommunityUrl())) {
            communityUrl = settingsModel.getCommunityUrl();
        }
        if (!TextUtils.isEmpty(settingsModel.getSafetyUrl())) {
            safetyUrl = settingsModel.getSafetyUrl();
        }*/

        if (settingsModel.getLocationModels() != null && settingsModel.getLocationModels().size() > 0) {
            locationModels.clear();
            locationModels.addAll(settingsModel.getLocationModels());
            setLocationListAdapter();
        } else {
            //rltEmptyChat.setVisibility(View.VISIBLE);
        }
    }

    public void matchingProfile(String matchingProfiles) {
        swMen.setChecked(false);
        swWomen.setChecked(false);
        if (!TextUtils.isEmpty(matchingProfiles)) {
            matchingProfile = matchingProfiles;
            if (matchingProfile.equals("Men")) {
                swMen.setChecked(true);
                swWomen.setChecked(false);
                men_women.setText(getString(R.string.men));
            } else if (matchingProfile.equals("Women")) {
                swWomen.setChecked(true);
                swMen.setChecked(false);
                men_women.setText(getString(R.string.women));
            }
            //men_women.setText(matchingProfile);
            else if (matchingProfile.equals("Both")) {
                swMen.setChecked(true);
                swWomen.setChecked(true);
                men_women.setText(getString(R.string.men)+","+getString(R.string.women));
            }

        } else {
            matchingProfile = "men";
            swWomen.setChecked(false);
            swMen.setChecked(true);
            men_women.setText(getString(R.string.men));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sessionManager.setSettingUpdate(true);
        switch (buttonView.getId()) {
            case R.id.switch_men:
                switchMen(isChecked);
                break;
            case R.id.switch_women:
                switchWomen(isChecked);
                break;
            case R.id.switch_show_me:
                showMe = isChecked ? "yes" : "no";
                break;
            case R.id.switch_new_matches:
                newMatch = isChecked ? "yes" : "no";
                break;
            case R.id.switch_messages:
                message = isChecked ? "yes" : "no";
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        sessionManager.setSettingUpdate(true);

        switch (checkedId) {
            case R.id.rb_mi:
                distanceType = "mi";
                tvShowDistance.setText(getString(R.string.mile));
                updateDistance();
                break;
            case R.id.rb_km:
                distanceType = "km";
                tvShowDistance.setText(getString(R.string.km));
                updateDistance();
                break;
            default:
                break;
        }
    }

    public void updateDistance() {
        double a;
        int b;
        if (adminDistanceType.equals(distanceType)) {
            if (distanceType.equalsIgnoreCase("mi")) {
                tvMaxDistance.setText(String.format(getString(R.string.miles), String.valueOf(maxDistance)));
            } else {
                tvMaxDistance.setText(String.format(getString(R.string.kilometer), String.valueOf(maxDistance)));
            }
        } else {
            if (distanceType.equalsIgnoreCase("mi") && adminDistanceType.equalsIgnoreCase("km")) {
                a = Integer.parseInt(maxDistance) / 1.60934;
                b = (int) a;
                tvMaxDistance.setText(String.format(getString(R.string.miles), String.valueOf(b)));
            } else {
                a = Integer.parseInt(maxDistance) * 1.60934;
                b = (int) a;
                tvMaxDistance.setText(String.format(getString(R.string.kilometer), String.valueOf(b)));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Settings", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            boolean isSuccess = data.getBooleanExtra("isUserNameAdded", false);
            String claimedName = data.getStringExtra("claimedName");  //Username
            if (isSuccess) {
                tvClaimYours.setText(claimedName);
                tvClaimYoursIcon.setVisibility(View.GONE);
                tvViewProfile.setVisibility(View.VISIBLE);
                tvShareMyUrl.setVisibility(View.VISIBLE);
            } else {
                tvClaimYours.setText(getString(R.string.claim_your));
                tvClaimYoursIcon.setVisibility(View.VISIBLE);
                tvViewProfile.setVisibility(View.GONE);
                tvShareMyUrl.setVisibility(View.GONE);
            }
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

    public void switchMen(boolean isChecked) {
        showMen = isChecked ? "yes" : "no";
        if (swMen.isChecked() && swWomen.isChecked()) {
            men_women.setText(getString(R.string.men)+","+getString(R.string.women));
            matchingProfile = "Both";
        } else {
            matchingProfile = isChecked ? "Men" : "Women";
            if (matchingProfile.equals("Men")) {
                men_women.setText(getString(R.string.women));
                matchingProfile = "women";
            } else {
                men_women.setText(matchingProfile);
            }
        }
        if (!swMen.isChecked() && !swWomen.isChecked()) {
            swWomen.setChecked(true);
            men_women.setText(getString(R.string.women));
            matchingProfile = "women";
        }
    }

    public void switchWomen(boolean isChecked) {
        showWomen = isChecked ? "yes" : "no";
        if (swMen.isChecked() && swWomen.isChecked()) {
            showWomen = "yes";
            men_women.setText(getString(R.string.men)+","+getString(R.string.women));
            matchingProfile = "Both";
        } else {
            matchingProfile = isChecked ? "Men" : "Women";
            if (matchingProfile.equals("Women")) {
                men_women.setText(getString(R.string.men));
                matchingProfile = "men";
            } else {
                men_women.setText(matchingProfile);
            }
        }
        if (!swMen.isChecked() && !swWomen.isChecked()) {
            swMen.setChecked(true);
            men_women.setText(getString(R.string.men));
            matchingProfile = "men";
        }
    }
/* ***********************************************************************************
                                      In APP Purchase Start
    ************************************************************************************** */

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

    void setupHelper() {
        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        String base64EncodedPublicKey = getResources().getString(R.string.google_play_publish_key);

        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }
        if (getPackageName().startsWith("com.example")) {
            //throw new RuntimeException("Please change the sample's package name! See README.");
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.e(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.e(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.e(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain(getString(R.string.pbm_inapp_set)+": " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(SettingsActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.e(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain(getString(R.string.er_query_inventry));
                }
            }
        });
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert(getString(R.string.error)+": " + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton(getString(R.string.ok), null);
        Log.e(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d("Boost Dialog", "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain(getString(R.string.er_query_inventry));
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getIsOrder()) {
            if (!TextUtils.isEmpty(sessionManager.getPlanType()) && sessionManager.getPlanType().equalsIgnoreCase("gold")) {
                cvGetIgniterGold.setVisibility(View.GONE);
                cvGetIgniterPlus.setVisibility(View.GONE);
            } else if (!TextUtils.isEmpty(sessionManager.getPlanType()) && sessionManager.getPlanType().equalsIgnoreCase("plus")) {
                cvGetIgniterGold.setVisibility(View.VISIBLE);
                cvGetIgniterPlus.setVisibility(View.GONE);
            } else {
                cvGetIgniterGold.setVisibility(View.VISIBLE);
                cvGetIgniterPlus.setVisibility(View.VISIBLE);
            }
        } else {
            cvGetIgniterGold.setVisibility(View.VISIBLE);
            cvGetIgniterPlus.setVisibility(View.VISIBLE);
        }
    }
}

