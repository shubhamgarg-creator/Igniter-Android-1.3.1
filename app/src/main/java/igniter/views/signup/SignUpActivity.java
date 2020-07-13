package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category SignUpActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.RunTimePermission;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.SignUpModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.interfaces.SignUpActivityListener;
import igniter.utils.CommonMethods;
import igniter.utils.Enums;
import igniter.views.customize.CustomDialog;
import igniter.views.main.AccountKit.TwilioAccountKitActivity;
import igniter.views.main.HomeActivity;

import static igniter.configs.Constants.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT;
import static igniter.utils.Enums.BIRTHDAY;
import static igniter.utils.Enums.EMAIL;
import static igniter.utils.Enums.FIRST_NAME;
import static igniter.utils.Enums.GENDER;
import static igniter.utils.Enums.LAST_NAME;
import static igniter.utils.Enums.PASSWORD;
import static igniter.utils.Enums.PROFILE_PICK;


/*****************************************************************
 Signup home page contain all signup fragment page
 ****************************************************************/
public class SignUpActivity extends AppCompatActivity implements SignUpActivityListener, ServiceListener {

    @Enums.Frag
    String currentFrag = FIRST_NAME;
    @Inject
    RunTimePermission runTimePermission;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    ApiService apiService;
    @Inject
    Gson gson;

    @Inject
    CommonMethods commonMethods;
    int min, max;
    HashMap<String, String> hashMap;
    private ProfilePickFragment profilePickFragment = null;
    public HashMap<String, String> signUp = new HashMap<>();

    public AlertDialog dialog;

    boolean isEmailFrag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);
        Intent intent = getIntent();
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
        if (hashMap != null)
            Log.v("HashMapTest", hashMap.get("auth_id"));

        min = Integer.parseInt(sessionManager.getMinAge());
        max = Integer.parseInt(sessionManager.getMaxAge());

        //changeFragment(MY_PHONE_NUMBER, null, false);
        TwilioAccountKitActivity.openTwilioAccountKitActivity(this);
    }

    /**
     * changeFragment method is used to change the fragment.
     *
     * @param currentFrag  which is represent calling fragment.
     * @param bundle       which contains arguments.
     * @param isReplaceAll which represent need to refresh or not.
     */
    public void changeFragment(@Enums.Frag final String currentFrag, Bundle bundle, boolean isReplaceAll) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (currentFrag) {
            case FIRST_NAME:
                FirstNameFragment firstNameFragment = new FirstNameFragment();
                firstNameFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, FIRST_NAME, firstNameFragment);
                break;
            case LAST_NAME:
                LastNameFragment lastNameFragment = new LastNameFragment();
                lastNameFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, LAST_NAME, lastNameFragment);
                break;
            case GENDER:
                GenderFragment genderFragment = new GenderFragment();
                genderFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, GENDER, genderFragment);
                break;
            case BIRTHDAY:
                BirthdayFragment birthdayFragment = new BirthdayFragment();
                if (bundle != null) {
                    bundle.putInt("min", min);
                    bundle.putInt("max", max);
                } else {
                    Bundle bundles = new Bundle();
                    bundles.putInt("min", min);
                    bundles.putInt("max", max);
                    bundle = bundles;
                }
                birthdayFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, BIRTHDAY, birthdayFragment);
                break;
            case EMAIL:
                EmailFragment emailFragment = new EmailFragment();
                emailFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, EMAIL, emailFragment);
                break;
            case PROFILE_PICK:
                profilePickFragment = new ProfilePickFragment();
                profilePickFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, PROFILE_PICK, profilePickFragment);
                break;
            case PASSWORD:
                PasswordFragment passwordFragment = new PasswordFragment();
                passwordFragment.setArguments(bundle);
                addBackStack(isReplaceAll, ft, PASSWORD, passwordFragment);
                break;
            default:
                break;
        }
    }

    /**
     * addBackStack method is used to maintain the back stack.
     *
     * @param isReplaceAll    true if need replace previous else false.
     * @param ft              fragment to transmit
     * @param backStackName   back stack name
     * @param callingFragment calling fragment name.
     */
    private void addBackStack(boolean isReplaceAll, final FragmentTransaction ft, final String backStackName, final Fragment callingFragment) {
        setCurrentFrag(backStackName);

        isReplaceAll = true;
        if (isReplaceAll) {
            replaceFragment(backStackName);
        }
        /*if (MY_PHONE_NUMBER.equals(backStackName)) {
            ft.replace(R.id.main_content, callingFragment, backStackName);
            ft.commitAllowingStateLoss();
        } else {*/
        ft.replace(R.id.main_content, callingFragment, backStackName);
        ft.addToBackStack(backStackName);
        ft.commitAllowingStateLoss();
//        }
    }

    /**
     * Method called for replacing the fragment.
     */
    private void replaceFragment(final String backStackName) {

        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> listOfFragments = manager.getFragments();
        for (int i = (listOfFragments.size() - 1); i > 0; i--) {
            Constants.isDisableFragmentAnimations = true;
            if (listOfFragments.get(i) != null && listOfFragments.get(i).getFragmentManager() != null && !backStackName.equals(listOfFragments.get(i).getTag())) {
                listOfFragments.get(i).getFragmentManager().popBackStackImmediate();
            }
            Constants.isDisableFragmentAnimations = false;
        }
    }

    @Override
    public Resources getRes() {
        return this.getResources();
    }

    @Override
    public SignUpActivity getInstance() {
        return SignUpActivity.this;
    }

    public void setCurrentFrag(String currentFrag) {
        this.currentFrag = currentFrag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkAllPermission(Constants.PERMISSIONS_STORAGE);
    }

    private void checkAllPermission(String[] permission) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(SignUpActivity.this, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            boolean isBlocked = runTimePermission.isPermissionBlocked(SignUpActivity.this, blockedPermission.toArray(new String[blockedPermission.size()]));
            if (isBlocked) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        //showEnablePermissionDialog();
                    }
                });
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this, permission, 150);
            }
        }
    }

    private void showEnablePermissionDialog() {
        if (!customDialog.isVisible()) {
            customDialog = new CustomDialog(getString(R.string.please_enable_permissions), getString(R.string.okay), new CustomDialog.btnAllowClick() {
                @Override
                public void clicked() {
                    callPermissionSettings();
                }
            });
            customDialog.show(getSupportFragmentManager(), "");
        }
    }

    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", SignUpActivity.this.getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf);
        }*/
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (profilePickFragment != null)
            profilePickFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void putHashMap(String key, String value) {
        if (signUp.containsKey(key)) signUp.remove(key);

        signUp.put(key, value);
    }

    public void removeHashMap(String key) {
        if (signUp.containsKey(key)) signUp.remove(key);
    }

    public HashMap<String, String> getHashMap() {
        return signUp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            if (currentFrag.equalsIgnoreCase(PROFILE_PICK) && profilePickFragment != null) {
                profilePickFragment.onActivityResult(requestCode, resultCode, data);
            } else {
                finish();
            }
            return;
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 300) {
            checkAllPermission(Constants.PERMISSIONS_STORAGE);
        } else if (currentFrag.equalsIgnoreCase(PROFILE_PICK) && profilePickFragment != null) {
            profilePickFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) {
            if (resultCode == Constants.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER) {
                if (sessionManager.getIsFBUser()) {
                    hashMap.put("phone_number", sessionManager.getPhoneNumber());
                    hashMap.put("country_code", sessionManager.getCountryCode());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("map", hashMap);
                    changeFragment(Enums.BIRTHDAY, bundle, false);
                    //apiService.fbPhoneSignup(hashMap).enqueue(new RequestCallback(this));
                } else {
                    putHashMap("phone_number", sessionManager.getPhoneNumber());
                    putHashMap("country_code", sessionManager.getCountryCode());
                    putHashMap("auth_id", commonMethods.getAuthId());
                    putHashMap("auth_type", commonMethods.getAuthType());
                    changeFragment(Enums.EMAIL, null, false);
                }
            } else if (resultCode == Constants.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER) {
                //openPasswordResetActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY),data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
            }
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }

        if (jsonResp.isSuccess()) {
            onSuccessLogin(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessLogin(JsonResponse jsonResp) {
        SignUpModel signUpModel = gson.fromJson(jsonResp.getStrResponse(), SignUpModel.class);
        if (signUpModel != null) {
            if (!TextUtils.isEmpty(signUpModel.getAccessToken())) {
                sessionManager.setToken(signUpModel.getAccessToken());
            }
            if (!TextUtils.isEmpty(signUpModel.getUserImageUrl())) {
                sessionManager.setProfileImg(signUpModel.getUserImageUrl());
            }
            sessionManager.setUserId(signUpModel.getUserId());
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

}
