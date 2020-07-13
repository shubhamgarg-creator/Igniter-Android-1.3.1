package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category IgniterPlusDialogActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.obs.CustomButton;
import com.obs.CustomTextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import igniter.R;
import igniter.adapters.main.ViewPagerAdapter;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.SessionManager;
import igniter.datamodels.main.ImageListModel;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.PlanListModel;
import igniter.datamodels.main.PlanSliderModel;
import igniter.iaputils.IabBroadcastReceiver;
import igniter.iaputils.IabException;
import igniter.iaputils.IabHelper;
import igniter.iaputils.IabResult;
import igniter.iaputils.Inventory;
import igniter.iaputils.Purchase;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.Enums;
import igniter.utils.RequestCallback;
import igniter.views.customize.CirclePageIndicator;
import igniter.views.customize.CustomDialog;

import static igniter.R.anim;
import static igniter.R.color;
import static igniter.R.drawable;
import static igniter.R.id;
import static igniter.R.layout;
import static igniter.R.string;
import static igniter.utils.Enums.REQ_GET_PLAN;
import static igniter.utils.Enums.REQ_UPDATE_TRANSACTION;

/*****************************************************************
 Show All plan dialog page (dynamic from admin panel)
 ****************************************************************/
public class IgniterPlusDialogActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener, View.OnClickListener, ServiceListener {

    // Debug tag, for logging
    static final String TAG = "Boost In App Purchase";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
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
    int month1PlanId, month6PlanId, month12PlanId, currentPlanId;

    float month6Total, month12Total;
    String month1Name, month1Price, month1Type, month6Name, month6Price, month6Type, month12Name, month12Price, month12Type;
    // Used to select between purchasing Boost on a monthly ,half yearly or yearly basis
    String mSelectedSubscriptionPeriod = "";
    // The helper object
    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    String payload = "";
    // Does the user have an active subscription to the infinite Boost plan?
    boolean mSubscribedToInfiniteBoost = false;
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
            consume(oneBoost);
            consume(fiveBoost);
            consume(tenBoost);

            Purchase SL5 = inventory.getPurchase(SKU_INFINITE_5_SL);
            Purchase SL25 = inventory.getPurchase(SKU_INFINITE_25_SL);
            Purchase SL60 = inventory.getPurchase(SKU_INFINITE_60_SL);
            consume(SL5);
            consume(SL25);
            consume(SL60);

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
                mSubscribedToInfiniteBoost = (oneBoost != null && verifyDeveloperPayload(oneBoost))
                        || (fiveBoost != null && verifyDeveloperPayload(fiveBoost)
                        || (tenBoost != null && verifyDeveloperPayload(tenBoost)));
            else if (type.equals("super_like"))
                mSubscribedToInfiniteBoost = (SL5 != null && verifyDeveloperPayload(SL5))
                        || (SL25 != null && verifyDeveloperPayload(SL25)
                        || (SL60 != null && verifyDeveloperPayload(SL60)));
            else if (type.equals("plus"))
                mSubscribedToInfiniteBoost = (IP1 != null && verifyDeveloperPayload(IP1))
                        || (IP6 != null && verifyDeveloperPayload(IP6)
                        || (IP12 != null && verifyDeveloperPayload(IP12)));
            else if (type.equals("gold"))
                mSubscribedToInfiniteBoost = (IG1 != null && verifyDeveloperPayload(IG1))
                        || (IG6 != null && verifyDeveloperPayload(IG6)
                        || (IG12 != null && verifyDeveloperPayload(IG12)));

            Log.e(TAG, "User " + (mSubscribedToInfiniteBoost ? "HAS" : "DOES NOT HAVE")
                    + " infinite Boost subscription.");

            if (type.equalsIgnoreCase("plus") || type.equalsIgnoreCase("gold")) {
                if (mSubscribedToInfiniteBoost && !sessionManager.getIsOrder()) {
                    Toast.makeText(IgniterPlusDialogActivity.this, getResources().getString(string.already_purchased), Toast.LENGTH_SHORT).show();
                }
            }
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
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.e(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                //complain("Error purchasing: " + result.getMessageReq().);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain(getString(R.string.er_verfn_failed));
                return;
            }


            /*if (purchase.getSku().equals(SKU_INFINITE_ONE_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_FIVE_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_TEN_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_5_SL)
                    || purchase.getSku().equals(SKU_INFINITE_25_SL)
                    || purchase.getSku().equals(SKU_INFINITE_60_SL)){
                try {
                    mHelper.consume(purchase);
                } catch (IabException e) {
                    e.printStackTrace();
                }
            }*/

            if (purchase.getSku().equals(SKU_INFINITE_ONE_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_FIVE_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_TEN_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_5_SL)
                    || purchase.getSku().equals(SKU_INFINITE_25_SL)
                    || purchase.getSku().equals(SKU_INFINITE_60_SL)
                    || purchase.getSku().equals(SKU_INFINITE_1_IP)
                    || purchase.getSku().equals(SKU_INFINITE_6_IP)
                    || purchase.getSku().equals(SKU_INFINITE_12_IP)
                    || purchase.getSku().equals(SKU_INFINITE_1_IG)
                    || purchase.getSku().equals(SKU_INFINITE_6_IG)
                    || purchase.getSku().equals(SKU_INFINITE_12_IG)) {
                // bought the infinite Boost subscription
                Log.e(TAG, "Infinite Boost subscription purchased." + purchase.getToken());
                Log.e(TAG, "Infinite Boost subscription purchased." + purchase.getSignature());
                Log.e(TAG, "Infinite Boost subscription purchased." + purchase.getDeveloperPayload());
                Log.e(TAG, "Infinite Boost subscription purchased." + purchase.getOriginalJson());
                updateTransaction(purchase.getOrderId(), currentPlanId, type, purchase.getSku(), purchase.getToken());
                if (type.equals("plus") || type.equals("gold")) {
                    sessionManager.setIsOrder(true);
                    sessionManager.setPlanType(type);
                }

                mAutoRenewEnabled = purchase.isAutoRenewing();
                mInfiniteBoostSku = purchase.getSku();
            } else {
                alert(getString(string.er_purchase));
            }
        }
    };
    private ViewPagerAdapter PagerAdapter;
    private ViewPager viewPager;
    private CirclePageIndicator pageIndicator;
    private RelativeLayout rlt_tutorial, rlt_bottom, lltTwelveMonth, lltSixMonth, lltOneMonth;
    private CustomTextView tvSixMonthSave, tvTwelveMonthSave , tvOneMonthSave, tvMonthTotalPrice;
    private CustomTextView tvOneMonthBook,tvSixMonthBook, tvTwelveMonthBook;
    private CustomTextView tvTwelveMonth, tvSixMonth, tvOneMonth;
    private CustomTextView tvPerYear, tvPerSixMonth, tvPerOneMonth ;
    private CustomTextView  tvSixTotalPrice,tvTwelveTotalPrice;
    private CustomTextView tvYearPrice, tvSixMonthPrice, tvOneMonthPrice;
    private CustomTextView tvTitle;
    private RelativeLayout rltTwelveMonth, rltSixMonth, rltOneMonth;
    private CustomButton btnContinue;
    private AlertDialog dialog;
    private ArrayList<ImageListModel> imageList = new ArrayList<>();
    private String[] imageLists;
    private Handler handler;
    private Runnable runnable;
    private int delay = 3000; //milliseconds
    private int page = 0;
    private String month6Save,month12Save;
    private String currecySymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);
        setContentView(layout.igniter_plus_dialog_activity);
        //this.setFinishOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(color.transparent);

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

        type = getIntent().getStringExtra("type");
        initView();
        initClickListener();
        //setViewPagerAdapter();
        initPageIndicator();
        initViewPagerListener();
        getSliderImages();

        setupHelper();

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
        viewPager = (ViewPager) findViewById(id.vp_igniter_plus);
        pageIndicator = (CirclePageIndicator) findViewById(id.indicator);

        dialog = commonMethods.getAlertDialog(this);

        rlt_tutorial = (RelativeLayout) findViewById(id.rlt_tutorial);
        rlt_bottom = (RelativeLayout) findViewById(id.rlt_bottom);
        lltOneMonth = (RelativeLayout) findViewById(id.llt_one_month);
        lltSixMonth = (RelativeLayout) findViewById(id.llt_six_month);
        lltTwelveMonth = (RelativeLayout) findViewById(id.llt_twelve_month);

        rltOneMonth = (RelativeLayout) findViewById(id.rlt_one_month);
        rltSixMonth = (RelativeLayout) findViewById(id.rlt_six_month);
        rltTwelveMonth = (RelativeLayout) findViewById(id.rlt_twelve_month);

        tvTitle = (CustomTextView) findViewById(id.tv_igniter_plus_title);
        tvOneMonth = (CustomTextView) findViewById(id.tv_one_month);
        tvSixMonth = (CustomTextView) findViewById(id.tv_six_month);
        tvTwelveMonth = (CustomTextView) findViewById(id.tv_twelve_month);

        tvOneMonthPrice = (CustomTextView) findViewById(id.tv_month_price);
        tvSixMonthPrice = (CustomTextView) findViewById(id.tv_six_month_price);
        tvYearPrice = (CustomTextView) findViewById(id.tv_year_price);

        tvPerOneMonth = (CustomTextView) findViewById(id.tv_per_month);
        tvPerSixMonth = (CustomTextView) findViewById(id.tv_per_six_month);
        tvPerYear = (CustomTextView) findViewById(id.tv_per_year);

        tvSixTotalPrice = (CustomTextView) findViewById(id.tv_six_total_price);
        tvTwelveTotalPrice = (CustomTextView) findViewById(id.tv_year_total_price);
        tvPerYear = (CustomTextView) findViewById(id.tv_per_year);

        tvSixMonthSave = (CustomTextView) findViewById(id.tv_six_month_save);
        tvTwelveMonthSave = (CustomTextView) findViewById(id.tv_one_year_save);
        tvOneMonthSave = (CustomTextView) findViewById(id.tv_one_month_save);
        tvMonthTotalPrice = (CustomTextView) findViewById(id.tv_month_total_price);
        tvSixMonthBook = (CustomTextView) findViewById(id.tv_six_month_book);
        tvTwelveMonthBook = (CustomTextView) findViewById(id.tv_one_year_book);
        tvOneMonthBook = (CustomTextView) findViewById(id.tv_one_month_book);

        btnContinue = (CustomButton) findViewById(id.btn_continue);

        //rlt_tutorial.bringToFront();
        rlt_bottom.bringToFront();

        if (type.equals("plus")) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(getResources().getText(string.header_igniter_plus));
            tvTitle.setTextColor(getResources().getColor(color.white));
        } else if (type.equals("gold")) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(getResources().getText(string.header_igniter_gold));
            tvTitle.setTextColor(getResources().getColor(color.gold1));
        } else if (type.equals("boost")) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(getResources().getText(string.get_boosts));
            tvTitle.setTextColor(getResources().getColor(color.white));
        } else if (type.equals("super_like")) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(getResources().getText(string.get_super_likes));
            tvTitle.setTextColor(getResources().getColor(color.white));
        }
    }

    private void getSliderImages() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.igniterPlanSlider(type).enqueue(new RequestCallback(REQ_GET_PLAN, this));
    }

    private void updateTransaction(String orderId, int planId, String planType, String productId, String purchaseToken) {
        //commonMethods.showProgressDialog(this, customDialog);
        apiService.afterPayment(getResources().getString(string.payment_type), orderId, planId, planType, getResources().getString(string.package_name), productId, purchaseToken, sessionManager.getToken()).enqueue(new RequestCallback(REQ_UPDATE_TRANSACTION, this));
    }

    private void setViewPagerAdapter() {
        handler = new Handler();
        PagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Constants.VP_GET_PLUS_SLIDER, imageList.size(), imageList, type);
        viewPager.setAdapter(PagerAdapter);
        pageIndicator.setViewPager(viewPager);

        runnable = new Runnable() {
            public void run() {
                page = viewPager.getCurrentItem();
                if (PagerAdapter.getCount() - 1 == page) {
                    page = 0;
                } else {
                    page++;
                }
                /*if (getIntent().getStringExtra("startwith").equals("5")){
                    page=5;
                    viewPager.setCurrentItem(4, true);
                }else {
                    viewPager.setCurrentItem(page, true);
                }*/
                viewPager.setCurrentItem(page, true);
                handler.postDelayed(this, delay);
            }
        };
    }

    private void initClickListener() {
        lltOneMonth.setOnClickListener(this);
        lltSixMonth.setOnClickListener(this);
        lltTwelveMonth.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    /**
     * Method called for make circle page indicator setup.
     */
    private void initPageIndicator() {
        final float density = getResources().getDisplayMetrics().density;
        pageIndicator.setRadius(5 * density);
        pageIndicator.setPageColor(ContextCompat.getColor(this, color.transparent));
        if (type.equals("gold")) {
            pageIndicator.setFillColor(ContextCompat.getColor(this, color.gold1));
            pageIndicator.setStrokeColor(ContextCompat.getColor(this, color.text_light_gray));
        } else {
            pageIndicator.setFillColor(ContextCompat.getColor(this, color.white));
            pageIndicator.setStrokeColor(ContextCompat.getColor(this, color.light_gray1));
        }
        pageIndicator.setStrokeWidth(1);
        if (getIntent().getStringExtra("startwith").equals("5")) {
            page = 5;
            viewPager.setCurrentItem(4);
            pageIndicator.setCurrentItem(4);
            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_superlike));
        } else if (getIntent().getStringExtra("startwith").equals("6")) {
            page = 6;
            viewPager.setCurrentItem(5);
            pageIndicator.setCurrentItem(5);
            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_swipearround));
        } else if (getIntent().getStringExtra("startwith").equals("7")) {
            page = 6;
            viewPager.setCurrentItem(6);
            pageIndicator.setCurrentItem(6);
            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_likes));
        } else if (getIntent().getStringExtra("startwith").equals("2")) {
            viewPager.setCurrentItem(1);
            pageIndicator.setCurrentItem(1);
            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_boost));
        } else if (getIntent().getStringExtra("startwith").equals("3")) {
            viewPager.setCurrentItem(2);
            pageIndicator.setCurrentItem(2);
            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_choose));
        } else if (getIntent().getStringExtra("startwith").equals("4")) {
            viewPager.setCurrentItem(3);
            pageIndicator.setCurrentItem(3);
            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_control_profile));
        } else if (getIntent().getStringExtra("startwith").equals("8")) {
            viewPager.setCurrentItem(7);
            pageIndicator.setCurrentItem(7);
            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_turnoffads));
        } else {
            viewPager.setCurrentItem(0);
//            pageIndicator.setCurrentItem(0);
            if (type.equals("boost"))
                rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_boost));
            else if (type.equals("super_like"))
                rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_superlike));
            else if (type.equals("plus"))
                rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_swipearround));
            else if (type.equals("gold"))
                rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_gold));
            //viewPager.setCurrentItem(0);
        }
        //viewPager.setCurrentItem(0);
        pageIndicator.setOnClickListener(null);
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
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_swipearround));
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        pageIndicator.setCurrentItem(1);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_boost));
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        pageIndicator.setCurrentItem(2);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_control_profile));
                        break;
                    case 3:
                        viewPager.setCurrentItem(3);
                        pageIndicator.setCurrentItem(3);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_superlike));
                        break;
                    case 4:
                        viewPager.setCurrentItem(4);
                        pageIndicator.setCurrentItem(4);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_superlike));
                        break;
                    case 5:
                        viewPager.setCurrentItem(5);
                        pageIndicator.setCurrentItem(5);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_likes));
                        break;
                    case 6:
                        viewPager.setCurrentItem(6);
                        pageIndicator.setCurrentItem(6);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_rewind));
                        break;
                    case 7:
                        viewPager.setCurrentItem(7);
                        pageIndicator.setCurrentItem(7);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_turnoffads));
                        break;
                    default:
                        viewPager.setCurrentItem(position);
                        pageIndicator.setCurrentItem(position);
                        if (type.equals("plus"))
                            rlt_tutorial.setBackground(getApplicationContext().getResources().getDrawable(drawable.gradient_boost));
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

        changeViewBg(6);
        changeViewColor(6);
    }

    @Override
    public void onClick(View v) {
        Animation animZoomOutIn = AnimationUtils.loadAnimation(getApplicationContext(), anim.zoom_out_in);
        switch (v.getId()) {
            case id.llt_one_month:
                if (type.equals("boost"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_ONE_BOOST;
                else if (type.equals("super_like"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_5_SL;
                else if (type.equals("plus"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_1_IP;
                else if (type.equals("gold"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_1_IG;

                currentPlanId = month1PlanId;
                changeViewBg(1);
                changeViewColor(1);
                lltOneMonth.startAnimation(animZoomOutIn);
                break;
            case id.llt_six_month:
                if (type.equals("boost"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_FIVE_BOOST;
                else if (type.equals("super_like"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_25_SL;
                else if (type.equals("plus"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_6_IP;
                else if (type.equals("gold"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_6_IG;
                currentPlanId = month6PlanId;
                changeViewBg(6);
                changeViewColor(6);
                lltSixMonth.startAnimation(animZoomOutIn);
                break;
            case id.llt_twelve_month:
                if (type.equals("boost"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_TEN_BOOST;
                else if (type.equals("super_like"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_60_SL;
                else if (type.equals("plus"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_12_IP;
                else if (type.equals("gold"))
                    mSelectedSubscriptionPeriod = SKU_INFINITE_12_IG;
                currentPlanId = month12PlanId;
                changeViewBg(12);
                changeViewColor(12);
                lltTwelveMonth.startAnimation(animZoomOutIn);
                break;
            case id.btn_continue:
                /**
                 * Change update Transaction method while live release for without calling in app purchase
                 */
                if(getResources().getString(R.string.isLive).equals("true"))
                    callInAppPurchase();  // Call In App Purchase
                else
                    updateTransaction("Test Order",currentPlanId,type,"Test SKU","Test Purchase Token");
                break;
            default:
                break;
        }
    }

    private void changeViewColor(int value) {
        tvOneMonth.setTextColor(getTextColor(value, 1));
        tvOneMonthPrice.setTextColor(getTextColor(value, 1));
        tvPerOneMonth.setTextColor(getTextColor(value, 1));
        tvMonthTotalPrice.setTextColor(getTextColor(value, 1));
        tvOneMonthSave.setTextColor(getTextColor(value, 1));
        tvSixMonth.setTextColor(getTextColor(value, 6));
        tvSixMonthPrice.setTextColor(getTextColor(value, 6));
        tvPerSixMonth.setTextColor(getTextColor(value, 6));
        tvSixMonthSave.setTextColor(getTextColor(value, 6));
        tvSixTotalPrice.setTextColor(getTextColor(value, 6));
        tvTwelveMonth.setTextColor(getTextColor(value, 12));
        tvYearPrice.setTextColor(getTextColor(value, 12));
        tvTwelveTotalPrice.setTextColor(getTextColor(value, 12));
        tvPerYear.setTextColor(getTextColor(value, 12));
        tvTwelveMonthSave.setTextColor(getTextColor(value, 12));

    }

    private int getTextColor(int value1, int value2) {
        if (type.equals("gold"))
            return (value1 == value2) ? ContextCompat.getColor(this, color.gold1) : ContextCompat.getColor(this, color.text_light_gray);
        else
            return (value1 == value2) ? ContextCompat.getColor(this, color.blue) : ContextCompat.getColor(this, color.text_light_gray);
    }

    private void changeViewBg(int value) {
        if (type.equals("gold")) {
            rltOneMonth.setBackgroundResource((value == 1) ? drawable.rect_gold_border : drawable.bottom_right_line);
            rltSixMonth.setBackgroundResource((value == 6) ? drawable.rect_gold_border : drawable.bottom_right_line);
            rltTwelveMonth.setBackgroundResource((value == 12) ? drawable.rect_gold_border : drawable.bottom_right_line);
            tvSixMonthBook.setBackground(getResources().getDrawable(R.drawable.oval_gold_btn));
            tvTwelveMonthBook.setBackground(getResources().getDrawable(R.drawable.oval_gold_btn));
            btnContinue.setBackground(getResources().getDrawable(R.drawable.oval_gradient_gold_btn));
        } else {
            rltOneMonth.setBackgroundResource((value == 1) ? drawable.rect_blue_border : drawable.bottom_right_line);
            rltSixMonth.setBackgroundResource((value == 6) ? drawable.rect_blue_border : drawable.bottom_right_line);
            rltTwelveMonth.setBackgroundResource((value == 12) ? drawable.rect_blue_border : drawable.bottom_right_line);
            tvSixMonthBook.setBackground(getResources().getDrawable(R.drawable.oval_blue_btn));
            tvTwelveMonthBook.setBackground(getResources().getDrawable(R.drawable.oval_blue_btn));
            tvOneMonthBook.setBackground(getResources().getDrawable(R.drawable.oval_blue_btn));
            btnContinue.setBackground(getResources().getDrawable(R.drawable.oval_gradient_blue_btn));
        }
        // tvSixMonthSave.setVisibility((value == 6) ? View.VISIBLE : View.INVISIBLE);
        //tvTwelveMonthSave.setVisibility((value == 12) ? View.VISIBLE : View.INVISIBLE);
        tvSixMonthBook.setVisibility((value == 6) ? View.VISIBLE : View.INVISIBLE);

        if(type.equals("super_like"))
            tvOneMonthBook.setVisibility((value == 1) ? View.VISIBLE : View.INVISIBLE);
        else
            tvTwelveMonthBook.setVisibility((value == 12) ? View.VISIBLE : View.INVISIBLE);



    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case Enums.REQ_GET_PLAN:
                if (jsonResp.isSuccess()) {
                    onSuccessIgniterPlus(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_UPDATE_TRANSACTION:
                if (jsonResp.isSuccess()) {

                    String order = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.isOrder, String.class);
                    if (!TextUtils.isEmpty(order) && order.equalsIgnoreCase("Yes")){
                        sessionManager.setPurchased(true);
                        sessionManager.setIsOrder(true);
                    }

                    String plantype = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.PlanType, String.class);

                    if (plantype.equals("plus") || plantype.equals("gold")) {
                        sessionManager.setPlanType(plantype);
                    }

                    int remainingSuperLikecount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.REMAINING_SUPER_LIKE, Integer.class);
                    sessionManager.setRemainingSuperLikes(remainingSuperLikecount);

                    int remainingLikecount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.REMAINING_LIKE, Integer.class);
                    sessionManager.setRemainingLikes(remainingLikecount);

                    String isLikeLimited = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.ISLIKELIMITED, String.class);
                    sessionManager.setIsRemainingLikeLimited(isLikeLimited);

                    int remainingBoostCount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.REMAINING_BOOST, Integer.class);
                    sessionManager.setRemainingBoost(remainingBoostCount);

                    //alertPaymentSuccess(getString(string.subscribed_boost));
                    alertPaymentSuccess(jsonResp.getStatusMsg());
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
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

    private void onSuccessIgniterPlus(JsonResponse jsonResp) {
        PlanSliderModel planSliderModel = gson.fromJson(jsonResp.getStrResponse(), PlanSliderModel.class);
        if (planSliderModel != null && planSliderModel.getImageList() != null && planSliderModel.getImageList().size() > 0) {
            imageList.clear();
            imageList.addAll(planSliderModel.getImageList());
            setViewPagerAdapter();
        }

        updateView(planSliderModel.getPlanList(), type);
    }

    private void updateView(ArrayList<PlanListModel> planSliderModel, String type) {
        String per = "";
        String countMethod = "";
        if (type.equals("boost") || type.equals("super_like")) {
            per = "/each";
            if (type.equals("boost"))
                countMethod = getString(R.string.sub_boost);
            else
                countMethod = getString(R.string.sub_super_like);
        } else if (type.equals("plus") || type.equals("gold")) {
            per = "/month";
            countMethod = getString(R.string.sub_month);
        }


        currecySymbol = Html.fromHtml(planSliderModel.get(1).getCurrencySymbol())+"";

        if (type.equals("boost") || type.equals("plus") || type.equals("gold")) {
            month1Name = planSliderModel.get(2).getCount();
            month1PlanId = planSliderModel.get(2).getPlanId();
            if("free".equalsIgnoreCase(planSliderModel.get(2).getPrice()))
                month1Price= planSliderModel.get(2).getPrice();
            else
                month1Price = Html.fromHtml(planSliderModel.get(2).getCurrencySymbol()) + planSliderModel.get(2).getPrice() + per;


            if (Integer.valueOf(month1Name) > 1) {
                if (countMethod.equalsIgnoreCase(getString(string.sub_boost))) {
                    month1Type=getString(string.sub_boosts);
                }else if (countMethod.equalsIgnoreCase(getString(string.sub_super_like))) {
                    month1Type=getString(string.sub_super_likes);
                }else if(countMethod.equalsIgnoreCase(getString(string.sub_month))){
                    month1Type=getString(string.sub_months);
                }
            }else {
                month1Type=countMethod;
            }
//            month1Type = Integer.valueOf(month1Name) > 1 ? countMethod + "s" : countMethod;

            month6Name = planSliderModel.get(1).getCount();
            month6PlanId = planSliderModel.get(1).getPlanId();
            month6Save  = planSliderModel.get(1).getSave();

            if("free".equalsIgnoreCase(planSliderModel.get(1).getPrice())){
                month6Price= planSliderModel.get(1).getPrice();
            }
            else{
                month6Total = Float.valueOf(planSliderModel.get(1).getPrice())*Float.valueOf(planSliderModel.get(1).getCount());
                month6Price = Html.fromHtml(planSliderModel.get(1).getCurrencySymbol()) + planSliderModel.get(1).getPrice() + per;
            }

            if (Integer.valueOf(month6Name) > 1) {
                if (countMethod.equalsIgnoreCase(getString(string.sub_boost))) {
                    month6Type=getString(string.sub_boosts);
                }else if (countMethod.equalsIgnoreCase(getString(string.sub_super_like))) {
                    month6Type=getString(string.sub_super_likes);
                }else if(countMethod.equalsIgnoreCase(getString(string.sub_month))){
                    month6Type=getString(string.sub_months);
                }
            }else {
                month6Type=countMethod;
            }
            //            month6Type = Integer.valueOf(month6Name) > 1 ? countMethod + "s" : countMethod;

            month12Name = planSliderModel.get(0).getCount();
            month12PlanId = planSliderModel.get(0).getPlanId();
            month12Save = planSliderModel.get(0).getSave();

            if("free".equalsIgnoreCase(planSliderModel.get(0).getPrice()))
                month12Price= planSliderModel.get(0).getPrice();
            else{
                month12Price = Html.fromHtml(planSliderModel.get(0).getCurrencySymbol()) + planSliderModel.get(0).getPrice() + per;
                month12Total = Float.valueOf(planSliderModel.get(0).getPrice())*Float.valueOf(planSliderModel.get(0).getCount());
            }

            if (Integer.valueOf(month12Name) > 1) {
                if (countMethod.equalsIgnoreCase(getString(string.sub_boost))) {
                    month12Type=getString(string.sub_boosts);
                }else if (countMethod.equalsIgnoreCase(getString(string.sub_super_like))) {
                    month12Type=getString(string.sub_super_likes);
                }else if(countMethod.equalsIgnoreCase(getString(string.sub_month))){
                    month12Type=getString(string.sub_months);
                }
            }else {
                month12Type=countMethod;
            }
            //            month12Type = Integer.valueOf(month12Name) > 1 ? countMethod + "s" : countMethod;

        } else if (type.equals("super_like")) {
            month1Name = planSliderModel.get(0).getCount();
            month1PlanId = planSliderModel.get(0).getPlanId();
            if("free".equalsIgnoreCase(planSliderModel.get(0).getPrice()))
                month1Price= planSliderModel.get(0).getPrice();
            else
                month1Price = Html.fromHtml(planSliderModel.get(0).getCurrencySymbol()) + planSliderModel.get(0).getPrice() + per;

            if (Integer.valueOf(month1Name) > 1) {
                if (countMethod.equalsIgnoreCase(getString(string.sub_boost))) {
                    month1Type=getString(string.sub_boosts);
                }else if (countMethod.equalsIgnoreCase(getString(string.sub_super_like))) {
                    month1Type=getString(string.sub_super_likes);
                }else if(countMethod.equalsIgnoreCase(getString(string.sub_month))){
                    month1Type=getString(string.sub_months);
                }
            }else {
                month1Type=countMethod;
            }
            //            month1Type = Integer.valueOf(month1Name) > 1 ? countMethod + "s" : countMethod;


            month6Name = planSliderModel.get(1).getCount();
            month6Save = planSliderModel.get(1).getSave();
            month6PlanId = planSliderModel.get(1).getPlanId();


            if("free".equalsIgnoreCase(planSliderModel.get(1).getPrice()))
                month6Price= planSliderModel.get(1).getPrice();
            else{
                month6Price = Html.fromHtml(planSliderModel.get(1).getCurrencySymbol()) + planSliderModel.get(1).getPrice() + per;
                month6Total = Float.valueOf(planSliderModel.get(1).getPrice())*Float.valueOf(planSliderModel.get(1).getCount());
            }
            if (Integer.valueOf(month6Name) > 1) {
                if (countMethod.equalsIgnoreCase(getString(string.sub_boost))) {
                    month6Type=getString(string.sub_boosts);
                }else if (countMethod.equalsIgnoreCase(getString(string.sub_super_like))) {
                    month6Type=getString(string.sub_super_likes);
                }else if(countMethod.equalsIgnoreCase(getString(string.sub_month))){
                    month6Type=getString(string.sub_months);
                }
            }else {
                month6Type=countMethod;
            }
            //            month6Type = Integer.valueOf(month6Name) > 1 ? countMethod + "s" : countMethod;

            month12Name = planSliderModel.get(2).getCount();
            month12Save = planSliderModel.get(0).getSave();
            month12PlanId = planSliderModel.get(2).getPlanId();


            if("free".equalsIgnoreCase(planSliderModel.get(2).getPrice()))
                month12Price= planSliderModel.get(2).getPrice();
            else{
                month12Price = Html.fromHtml(planSliderModel.get(2).getCurrencySymbol()) + planSliderModel.get(2).getPrice() + per;
                month12Total = Float.valueOf(planSliderModel.get(0).getPrice())*Float.valueOf(planSliderModel.get(0).getCount());
            }
            if (Integer.valueOf(month12Name) > 1) {
                if (countMethod.equalsIgnoreCase(getString(string.sub_boost))) {
                    month12Type=getString(string.sub_boosts);
                }else if (countMethod.equalsIgnoreCase(getString(string.sub_super_like))) {
                    month12Type=getString(string.sub_super_likes);
                }else if(countMethod.equalsIgnoreCase(getString(string.sub_month))){
                    month12Type=getString(string.sub_months);
                }
            }else {
                month12Type=countMethod;
            }
            //            month12Type = Integer.valueOf(month12Name) > 1 ? countMethod + "s" : countMethod;
        }
        currentPlanId = month6PlanId;
        updateText(type);
    }

    private void updateText(String type) {
        tvOneMonth.setText(month1Name);
        tvOneMonthPrice.setText(month1Price);
        tvPerOneMonth.setText(month1Type);

        tvSixMonth.setText(month6Name);
        tvSixMonthPrice.setText(month6Price);
        tvPerSixMonth.setText(month6Type);

        tvTwelveMonth.setText(month12Name);
        tvYearPrice.setText(month12Price);
        tvPerYear.setText(month12Type);



        if (type.equals("super_like")) {
            try{
                tvMonthTotalPrice.setText(getResources().getString(R.string.total)+currecySymbol+new DecimalFormat("##.##").format(month12Total));
            }catch (Exception e){
                tvMonthTotalPrice.setText("");
            }
        }else{
            try{
                tvTwelveTotalPrice.setText(getResources().getString(R.string.total)+currecySymbol+new DecimalFormat("##.##").format(month12Total));
            }catch (Exception e){
                tvTwelveTotalPrice.setText("");
            }
        }


        try{
            tvSixTotalPrice.setText(getResources().getString(R.string.total)+currecySymbol+new DecimalFormat("##.##").format(month6Total));
        }catch(Exception e){
            tvSixTotalPrice.setText("");
        }






        if(!TextUtils.isEmpty(month6Save)){
            tvSixMonthSave.setText(getResources().getString(string.save)+Math.round(Float.parseFloat(month6Save))+"%");
        }


        if (type.equals("super_like")) {
            if(!TextUtils.isEmpty(month12Save))
                tvOneMonthSave.setText(getResources().getString(string.save)+Math.round(Float.parseFloat(month12Save))+"%");
        }else{
            if(!TextUtils.isEmpty(month12Save))
                tvTwelveMonthSave.setText(getResources().getString(string.save)+Math.round(Float.parseFloat(month12Save))+"%");
        }


    }

    /* ***********************************************************************************
                                      In APP Purchase Start
    ************************************************************************************** */

    @Override
    protected void onResume() {
        super.onResume();
        if (handler != null)
            handler.postDelayed(runnable, delay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null)
            handler.removeCallbacks(runnable);
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
        String base64EncodedPublicKey = getResources().getString(string.google_play_publish_key);

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
                    complain(getString(R.string.pbm_inapp_set) + ": " + result);
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
                mBroadcastReceiver = new IabBroadcastReceiver(IgniterPlusDialogActivity.this);
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

    void callInAppPurchase() {
        List<String> oldSkus = null;
        if (!TextUtils.isEmpty(mInfiniteBoostSku)
                && !mInfiniteBoostSku.equals(mSelectedSubscriptionPeriod)) {
            // The user currently has a valid subscription, any purchase action is going to
            // replace that subscription
            oldSkus = new ArrayList<String>();
            oldSkus.add(mInfiniteBoostSku);
        }

        Log.e(TAG, "Launching purchase flow for Boost subscription.");
        try {
            if (type.equals("plus") || type.equals("gold")) {
                mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                        oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
            } else {
                mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain(getString(string.er_lanch_another_process));
        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton(getString(R.string.ok), null);
        Log.e(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    void alertPaymentSuccess(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setCancelable(false);
        bld.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
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

    public void consume(Purchase purchase) {
        if (purchase != null) {
            try {
                if (purchase.getOrderId() != null)
                    mHelper.consume(purchase);
            } catch (IabException e) {
                e.printStackTrace();
            }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
