package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category BoostDialogActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.obs.CustomButton;
import com.obs.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import igniter.R;
import igniter.configs.AppController;
import igniter.iaputils.IabBroadcastReceiver;
import igniter.iaputils.IabHelper;
import igniter.iaputils.IabResult;
import igniter.iaputils.Inventory;
import igniter.iaputils.Purchase;

/*****************************************************************
 Show Boost plan dialog page (Now its dynamic in IgniterPlusDialogActivity)
 ****************************************************************/
public class BoostDialogActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener, View.OnClickListener {

    // Debug tag, for logging
    static final String TAG = "Boost In App Purchase";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    // SKU for our subscription (infinite Boost)
    static String SKU_INFINITE_ONE_BOOST = "";
    static String SKU_INFINITE_FIVE_BOOST = "";
    static String SKU_INFINITE_TEN_BOOST = "";
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
            Purchase tenBoost = inventory.getPurchase(SKU_INFINITE_FIVE_BOOST);
            if (oneBoost != null && oneBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_ONE_BOOST;
                mAutoRenewEnabled = true;
            } else if (fiveBoost != null && fiveBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_FIVE_BOOST;
                mAutoRenewEnabled = true;
            } else if (tenBoost != null && tenBoost.isAutoRenewing()) {
                mInfiniteBoostSku = SKU_INFINITE_TEN_BOOST;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteBoostSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteBoost = (oneBoost != null && verifyDeveloperPayload(oneBoost))
                    || (fiveBoost != null && verifyDeveloperPayload(fiveBoost)
                    || (tenBoost != null && verifyDeveloperPayload(tenBoost)));
            Log.e(TAG, "User " + (mSubscribedToInfiniteBoost ? "HAS" : "DOES NOT HAVE")
                    + " infinite Boost subscription.");
            //if (mSubscribedToInfiniteBoost) mTank = TANK_MAX;


            /*// Check for Boost delivery -- if we own Boost, we should fill up the tank immediately
            Purchase BoostPurchase = inventory.getPurchase(SKU_Boost);
            if (BoostPurchase != null && verifyDeveloperPayload(BoostPurchase)) {
                Log.d(TAG, "We have Boost. Consuming it.");
                try {
                    mHelper.consumeAsync(inventory.getPurchase(SKU_Boost), mConsumeFinishedListener);
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

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain(getString(R.string.error_purchasing)+": " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain(getString(R.string.er_verfn_failed));
                return;
            }

            Log.e(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_INFINITE_ONE_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_FIVE_BOOST)
                    || purchase.getSku().equals(SKU_INFINITE_TEN_BOOST)) {
                // bought the infinite Boost subscription
                alert(getString(R.string.subscribed_boost));
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mInfiniteBoostSku = purchase.getSku();
            }
        }
    };
    private LinearLayout lltTwelveMonth, lltSixMonth, lltOneMonth;
    private CustomTextView tvSixMonthSave, tvTwelveMonthSave;
    private CustomTextView tvTwelveMonth, tvSixMonth, tvOneMonth;
    private CustomTextView tvPerYear, tvPerSixMonth, tvPerOneMonth;
    private CustomTextView tvYearPrice, tvSixMonthPrice, tvOneMonthPrice;
    private RelativeLayout rltTwelveMonth, rltSixMonth, rltOneMonth;
    private CustomButton btnBoostMe;
    private LinearLayout lltGetIgniterPlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);

        setContentView(R.layout.boost_dialog_activity);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawableResource(R.color.transparent);

        SKU_INFINITE_ONE_BOOST = getResources().getString(R.string.iap_boost_1);
        SKU_INFINITE_FIVE_BOOST = getResources().getString(R.string.iap_boost_5);
        SKU_INFINITE_TEN_BOOST = getResources().getString(R.string.iap_boost_10);

        initView();
        initClickListener();
        setupHelper();
        mSelectedSubscriptionPeriod = SKU_INFINITE_FIVE_BOOST;
    }

    private void initView() {
        lltGetIgniterPlus = (LinearLayout) findViewById(R.id.llt_get_igniter_plus);
        btnBoostMe = (CustomButton) findViewById(R.id.btn_boost_me);

        lltOneMonth = (LinearLayout) findViewById(R.id.llt_one_month);
        lltSixMonth = (LinearLayout) findViewById(R.id.llt_six_month);
        lltTwelveMonth = (LinearLayout) findViewById(R.id.llt_twelve_month);

        rltOneMonth = (RelativeLayout) findViewById(R.id.rlt_one_month);
        rltSixMonth = (RelativeLayout) findViewById(R.id.rlt_six_month);
        rltTwelveMonth = (RelativeLayout) findViewById(R.id.rlt_twelve_month);

        tvOneMonth = (CustomTextView) findViewById(R.id.tv_one_month);
        tvSixMonth = (CustomTextView) findViewById(R.id.tv_six_month);
        tvTwelveMonth = (CustomTextView) findViewById(R.id.tv_twelve_month);

        tvOneMonthPrice = (CustomTextView) findViewById(R.id.tv_month_price);
        tvSixMonthPrice = (CustomTextView) findViewById(R.id.tv_six_month_price);
        tvYearPrice = (CustomTextView) findViewById(R.id.tv_year_price);

        tvPerOneMonth = (CustomTextView) findViewById(R.id.tv_per_month);
        tvPerSixMonth = (CustomTextView) findViewById(R.id.tv_per_six_month);
        tvPerYear = (CustomTextView) findViewById(R.id.tv_per_year);

        tvSixMonthSave = (CustomTextView) findViewById(R.id.tv_six_month_save);
        tvTwelveMonthSave = (CustomTextView) findViewById(R.id.tv_one_year_save);
    }

    private void initClickListener() {
        lltOneMonth.setOnClickListener(this);
        lltSixMonth.setOnClickListener(this);
        lltTwelveMonth.setOnClickListener(this);
        btnBoostMe.setOnClickListener(this);
        lltGetIgniterPlus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Animation animZoomOutIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_in);
        switch (v.getId()) {
            case R.id.llt_one_month:
                mSelectedSubscriptionPeriod = SKU_INFINITE_ONE_BOOST;
                changeViewBg(1);
                changeViewColor(1);
                lltOneMonth.startAnimation(animZoomOutIn);
                break;
            case R.id.llt_six_month:
                mSelectedSubscriptionPeriod = SKU_INFINITE_FIVE_BOOST;
                changeViewBg(6);
                changeViewColor(6);
                lltSixMonth.startAnimation(animZoomOutIn);
                break;
            case R.id.llt_twelve_month:
                mSelectedSubscriptionPeriod = SKU_INFINITE_TEN_BOOST;
                changeViewBg(12);
                changeViewColor(12);
                lltTwelveMonth.startAnimation(animZoomOutIn);
                break;
            case R.id.btn_boost_me:
                callInAppPurchase();
                break;
            case R.id.llt_get_igniter_plus:
                break;
            default:
                break;
        }
    }

    private void changeViewColor(int value) {
        tvOneMonth.setTextColor(getTextColor(value, 1));
        tvOneMonthPrice.setTextColor(getTextColor(value, 1));
        tvPerOneMonth.setTextColor(getTextColor(value, 1));
        tvSixMonth.setTextColor(getTextColor(value, 6));
        tvSixMonthPrice.setTextColor(getTextColor(value, 6));
        tvPerSixMonth.setTextColor(getTextColor(value, 6));
        tvTwelveMonth.setTextColor(getTextColor(value, 12));
        tvYearPrice.setTextColor(getTextColor(value, 12));
        tvPerYear.setTextColor(getTextColor(value, 12));
    }

    private int getTextColor(int value1, int value2) {
        return (value1 == value2) ? ContextCompat.getColor(this, R.color.swipearround_gradient_start) : ContextCompat.getColor(this, R.color.black_text_color);
    }

    private void changeViewBg(int value) {
        rltOneMonth.setBackgroundResource((value == 1) ? R.drawable.rect_accent_border : R.drawable.rect_gray_top_bottom_line);
        rltSixMonth.setBackgroundResource((value == 6) ? R.drawable.rect_accent_border : R.drawable.rect_gray_top_bottom_line);
        rltTwelveMonth.setBackgroundResource((value == 12) ? R.drawable.rect_accent_border : R.drawable.rect_gray_top_bottom_line);
        tvSixMonthSave.setVisibility((value == 6) ? View.VISIBLE : View.INVISIBLE);
        tvTwelveMonthSave.setVisibility((value == 12) ? View.VISIBLE : View.INVISIBLE);
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
                mBroadcastReceiver = new IabBroadcastReceiver(BoostDialogActivity.this);
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
            mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                    oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain(getString(R.string.er_lanch_another_process));
        }
    }

    void complain(String message) {
        alert(getString(R.string.error)+": " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton(getString(R.string.ok), null);
        bld.create().show();
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
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
