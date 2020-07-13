package igniter.views.profile;

/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category EditProfileActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;
import com.obs.image_cropping.CropImage;
import com.obs.image_cropping.ImageMinimumSizeCalculator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import igniter.BuildConfig;
import igniter.R;
import igniter.adapters.profile.EditProfileImageListAdapter;
import igniter.backgroundtask.ImageCompressAsyncTask;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.RunTimePermission;
import igniter.configs.SessionManager;
import igniter.datamodels.main.EditProfileModel;
import igniter.datamodels.main.ImageModel;
import igniter.datamodels.main.JsonResponse;
import igniter.interfaces.ApiService;
import igniter.interfaces.ImageListener;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.ImageUtils;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;
import igniter.views.customize.CustomRecyclerView;
import igniter.views.main.IgniterPlusDialogActivity;
import okhttp3.RequestBody;

import static igniter.utils.Enums.REQ_GET_EDIT_PROFILE;
import static igniter.utils.Enums.REQ_REMOVE_IMAGE;
import static igniter.utils.Enums.REQ_UPDATE_PROFILE;
import static igniter.utils.Enums.REQ_UPLOAD_PROFILE_IMG;

/*import instagram.InstagramHelper;
import instagram.InstagramHelperConstants;
import instagram.model.InstagramUser;*/

/*****************************************************************
 User edit profile
 ****************************************************************/

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, ServiceListener, CompoundButton.OnCheckedChangeListener, ImageListener {

    private static final String TAG = "EditProfileActivity";
    ImageView updateImageView;
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
    @Inject
    RunTimePermission runTimePermission;
    private CustomTextView tvHeader, tvBackArrow, tvConnect, tvInstagram, tvAboutUserName, tvAboutCount,tvAppPlus;
    private CustomEditText edtAbout, edtJobTitle, edtCompany, edtSchool;
    private RelativeLayout rltProfileImageOne, rltProfileImageTwo, rltProfileImageThree, rltProfileImageFour, rltProfileImageFive, rltProfileImageSix;
    private RadioGroup rgGender;
    private RadioButton rbMan, rbWoman;
    private SwitchCompat swHideAge, swHideDistance, swSmartPhotos;
    private CustomRecyclerView rvEditProfileList;
    private EditProfileImageListAdapter imageListAdapter;
    private AlertDialog dialog;
    private EditProfileModel editProfileModel;
    private boolean isDelete = false;
    private ImageView ivUserImageOne, ivUserImageTwo, ivUserImageThree, ivUserImageFour, ivUserImageFive, ivUserImageSix;
    private ImageView tvAddIconOne, tvAddIconTwo, tvAddIconThree, tvAddIconFour, tvAddIconFive, tvAddIconSix;
    private ImageView tvCloseIconOne, tvCloseIconTwo, tvCloseIconThree, tvCloseIconFour, tvCloseIconFive, tvCloseIconSix;
    private String about = "", jobTitle = "", company = "", school = "", gender = "", hideMyAge = "", distanceInvisible = "", imageUrl = "", instagramUserName = "";
    private File imageFile = null;
    private Uri imageUri;
    private String imagePath = "";

    // private InstagramHelper instagramHelper;
    private int clickPos = 1;
    private String userName = "";
    private RelativeLayout hideage, hidedistance;
    private String img;
    private String img_id;
    private ArrayList<String> image_id;
    private boolean onBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        AppController.getAppComponent().inject(this);
        image_id = new ArrayList<String>();
        //instagramHelper = AppController.getInstagramHelper();
        initView();
        getIntentValues();
//        initRecyclerView();
        getEditProfileDetails();
    }

    private void initView() {
        tvHeader = (CustomTextView) findViewById(R.id.tv_header_title);
        tvBackArrow = (CustomTextView) findViewById(R.id.tv_left_arrow);
        tvInstagram = (CustomTextView) findViewById(R.id.tv_instagram);
        tvConnect = (CustomTextView) findViewById(R.id.tv_connect);
        tvAboutUserName = (CustomTextView) findViewById(R.id.tv_about_username);
        tvAboutCount = (CustomTextView) findViewById(R.id.tv_about_count);
        tvAppPlus=(CustomTextView)findViewById(R.id.tvAppPlus);
        tvAppPlus.setText(getResources().getString(R.string.app_name).charAt(0)+"" +getResources().getString(R.string.plus));
        tvAboutCount.setVisibility(View.GONE);

        edtAbout = (CustomEditText) findViewById(R.id.edt_about);
        edtJobTitle = (CustomEditText) findViewById(R.id.edt_job_title);
        edtCompany = (CustomEditText) findViewById(R.id.edt_company);
        edtSchool = (CustomEditText) findViewById(R.id.edt_school);

        rgGender = (RadioGroup) findViewById(R.id.rdg_gender);
        rbMan = (RadioButton) findViewById(R.id.rb_man);
        rbWoman = (RadioButton) findViewById(R.id.rb_woman);
        rvEditProfileList = (CustomRecyclerView) findViewById(R.id.rv_edit_profile_list);

        swSmartPhotos = (SwitchCompat) findViewById(R.id.switch_smart_photos);

        swHideAge = (SwitchCompat) findViewById(R.id.switch_hide_age);
        swHideDistance = (SwitchCompat) findViewById(R.id.switch_hide_distance);

        swHideAge.setChecked(false);    //newly added
        swHideDistance.setChecked(false);    //newly added

        dialog = commonMethods.getAlertDialog(this);

        rltProfileImageOne = (RelativeLayout) findViewById(R.id.rlt_profile_image_one);
        rltProfileImageTwo = (RelativeLayout) findViewById(R.id.rlt_profile_image_two);
        rltProfileImageThree = (RelativeLayout) findViewById(R.id.rlt_profile_image_three);
        rltProfileImageFour = (RelativeLayout) findViewById(R.id.rlt_profile_image_four);
        rltProfileImageFive = (RelativeLayout) findViewById(R.id.rlt_profile_image_five);
        rltProfileImageSix = (RelativeLayout) findViewById(R.id.rlt_profile_image_six);
        hideage = (RelativeLayout) findViewById(R.id.hide_age);
        hidedistance = (RelativeLayout) findViewById(R.id.hide_distance);

        ivUserImageOne = (ImageView) rltProfileImageOne.findViewById(R.id.iv_user_image);
        ivUserImageTwo = (ImageView) rltProfileImageTwo.findViewById(R.id.iv_user_image);
        ivUserImageThree = (ImageView) rltProfileImageThree.findViewById(R.id.iv_user_image);
        ivUserImageFour = (ImageView) rltProfileImageFour.findViewById(R.id.iv_user_image);
        ivUserImageFive = (ImageView) rltProfileImageFive.findViewById(R.id.iv_user_image);
        ivUserImageSix = (ImageView) rltProfileImageSix.findViewById(R.id.iv_user_image);

        tvAddIconOne = (ImageView) rltProfileImageOne.findViewById(R.id.tv_add_icon);
        tvAddIconTwo = (ImageView) rltProfileImageTwo.findViewById(R.id.tv_add_icon);
        tvAddIconThree = (ImageView) rltProfileImageThree.findViewById(R.id.tv_add_icon);
        tvAddIconFour = (ImageView) rltProfileImageFour.findViewById(R.id.tv_add_icon);
        tvAddIconFive = (ImageView) rltProfileImageFive.findViewById(R.id.tv_add_icon);
        tvAddIconSix = (ImageView) rltProfileImageSix.findViewById(R.id.tv_add_icon);

        tvCloseIconOne = (ImageView) rltProfileImageOne.findViewById(R.id.tv_close_icon);
        tvCloseIconTwo = (ImageView) rltProfileImageTwo.findViewById(R.id.tv_close_icon);
        tvCloseIconThree = (ImageView) rltProfileImageThree.findViewById(R.id.tv_close_icon);
        tvCloseIconFour = (ImageView) rltProfileImageFour.findViewById(R.id.tv_close_icon);
        tvCloseIconFive = (ImageView) rltProfileImageFive.findViewById(R.id.tv_close_icon);
        tvCloseIconSix = (ImageView) rltProfileImageSix.findViewById(R.id.tv_close_icon);


        edtAbout.clearFocus();

        tvAddIconOne.setTag(1);
        tvAddIconTwo.setTag(2);
        tvAddIconThree.setTag(3);
        tvAddIconFour.setTag(4);
        tvAddIconFive.setTag(5);
        tvAddIconSix.setTag(6);

        tvCloseIconOne.setTag(1);
        tvCloseIconTwo.setTag(2);
        tvCloseIconThree.setTag(3);
        tvCloseIconFour.setTag(4);
        tvCloseIconFive.setTag(5);
        tvCloseIconSix.setTag(6);

        setImageViewCount();
        iniTextChangeListener();

        tvHeader.setTextColor(getResources().getColor(R.color.black));
        tvHeader.setText(getString(R.string.header_edit_info));

        tvBackArrow.setOnClickListener(this);
        tvConnect.setOnClickListener(this);
        swHideDistance.setOnCheckedChangeListener(this);
        swHideAge.setOnCheckedChangeListener(this);
        rgGender.setOnCheckedChangeListener(this);

        tvAddIconOne.setOnClickListener(this);
        tvAddIconTwo.setOnClickListener(this);
        tvAddIconThree.setOnClickListener(this);
        tvAddIconFour.setOnClickListener(this);
        tvAddIconFive.setOnClickListener(this);
        tvAddIconSix.setOnClickListener(this);

        tvCloseIconOne.setOnClickListener(this);
        tvCloseIconTwo.setOnClickListener(this);
        tvCloseIconThree.setOnClickListener(this);
        tvCloseIconFour.setOnClickListener(this);
        tvCloseIconFive.setOnClickListener(this);
        tvCloseIconSix.setOnClickListener(this);
        hideage.setOnClickListener(this);
        hidedistance.setOnClickListener(this);
        swHideAge.setOnClickListener(this);
        swHideDistance.setOnClickListener(this);
    }

    private void setImageViewCount() {
        String[] count = this.getResources().getStringArray(R.array.photo_count);
        CustomTextView tvImageCount1 = (CustomTextView) rltProfileImageOne.findViewById(R.id.tv_count);
        tvImageCount1.setText(count[0]);
        CustomTextView tvImageCount2 = (CustomTextView) rltProfileImageTwo.findViewById(R.id.tv_count);
        tvImageCount2.setText(count[1]);
        CustomTextView tvImageCount3 = (CustomTextView) rltProfileImageThree.findViewById(R.id.tv_count);
        tvImageCount3.setText(count[2]);
        CustomTextView tvImageCount4 = (CustomTextView) rltProfileImageFour.findViewById(R.id.tv_count);
        tvImageCount4.setText(count[3]);
        CustomTextView tvImageCount5 = (CustomTextView) rltProfileImageFive.findViewById(R.id.tv_count);
        tvImageCount5.setText(count[4]);
        CustomTextView tvImageCount6 = (CustomTextView) rltProfileImageSix.findViewById(R.id.tv_count);
        tvImageCount6.setText(count[5]);
    }

    private void iniTextChangeListener() {
        edtAbout.setCursorVisible(false);

        edtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtAbout.setCursorVisible(true);
            }
        });

        edtAbout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //edtAbout.setCursorVisible(false);
                tvAboutCount.setText(String.valueOf(500 - s.length()));
                tvAboutCount.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initRecyclerView() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = metrics.widthPixels;
        params.height = metrics.widthPixels;
        rvEditProfileList.setLayoutParams(params);

//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
//        rvEditProfileList.setLayoutManager(layoutManager);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });

        rvEditProfileList.setLayoutManager(manager);

        imageListAdapter = new EditProfileImageListAdapter(this, new ArrayList<String>());
        rvEditProfileList.setAdapter(imageListAdapter);
    }

    private void getEditProfileDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.getEditProfileDetail(sessionManager.getToken()).enqueue(new RequestCallback(REQ_GET_EDIT_PROFILE, this));
    }

    private void getIntentValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !TextUtils.isEmpty(bundle.getString("userName"))) {
            tvAboutUserName.setText(String.format(getString(R.string.about), bundle.getString("userName")));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_arrow:
                updateProfile();
                break;
            case R.id.tv_connect:
                //instagramHelper.loginFromActivity(EditProfileActivity.this);
                break;
            case R.id.tv_add_icon:
                clickPos = (int) v.getTag();
                //pickProfileImg(false);
                isDelete = false;
                checkAllPermission(Constants.PERMISSIONS_PHOTO, isDelete);
                break;
            case R.id.tv_close_icon:
                clickPos = (int) v.getTag();
                if (clickPos == 1 && image_id.size() == 1) {
                    isDelete = true;
                    checkAllPermission(Constants.PERMISSIONS_PHOTO, isDelete);
                    //pickProfileImg(true);
                } else {
                    commonMethods.showProgressDialog(this, customDialog);
                    apiService.remove_profile_image(Integer.valueOf(image_id.get(clickPos - 1)), sessionManager.getToken()).enqueue(new RequestCallback(REQ_REMOVE_IMAGE, this));
                }
                break;
            case R.id.hide_age:
                checkPurchase(1);
                break;
            case R.id.hide_distance:
                checkPurchase(0);
                break;
            case R.id.switch_hide_age:
                swHideAge.setChecked(!swHideAge.isChecked());
                checkPurchase(1);
                break;
            case R.id.switch_hide_distance:
                swHideDistance.setChecked(!swHideDistance.isChecked());
                checkPurchase(0);
                break;
            default:
                break;
        }
    }

    private HashMap<String, String> getParams() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", sessionManager.getToken());
        hashMap.put("gender", gender);
        hashMap.put("job_title", jobTitle);
        hashMap.put("distance_invisible", distanceInvisible);
        hashMap.put("show_my_age", hideMyAge);
        hashMap.put("about", about);
        hashMap.put("college", school);
        hashMap.put("work", company);
        //hashMap.put("instagram_id", instagramUserName);

        return hashMap;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_man:
                gender = "Men";
                break;
            case R.id.rb_woman:
                gender = "Women";
                break;
            default:
                break;
        }
    }

    public void pickProfileImg(boolean isDelete) {
        this.isDelete = isDelete;
        View view = getLayoutInflater().inflate(R.layout.camera_dialog_layout, null);
        LinearLayout lltCamera = (LinearLayout) view.findViewById(R.id.llt_camera);
        LinearLayout lltLibrary = (LinearLayout) view.findViewById(R.id.llt_library);

        final Dialog bottomSheetDialog = new Dialog(this, R.style.MaterialDialogSheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        if (bottomSheetDialog.getWindow() == null) return;
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        lltCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = commonMethods.cameraFilePath();
                imageUri = FileProvider.getUriForFile(EditProfileActivity.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);

                try {
                    List<ResolveInfo> resolvedIntentActivities = EditProfileActivity.this.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                        String packageName = resolvedIntentInfo.activityInfo.packageName;
                        grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    cameraIntent.putExtra("return-data", true);
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 1);
                commonMethods.refreshGallery(EditProfileActivity.this, imageFile);
            }
        });

        lltLibrary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                imageFile = commonMethods.getDefaultFileName(EditProfileActivity.this);

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.REQUEST_CODE_GALLERY);
            }
        });
    }

    public void loadImages() {
        switch (clickPos) {
            case 1:
                getImageFromUrl(ivUserImageOne);
                tvAddIconOne.setVisibility(View.GONE);
                tvCloseIconOne.setVisibility(View.VISIBLE);
                break;
            case 2:
                getImageFromUrl(ivUserImageTwo);
                tvAddIconTwo.setVisibility(View.GONE);
                tvCloseIconTwo.setVisibility(View.VISIBLE);
                break;
            case 3:
                getImageFromUrl(ivUserImageThree);
                tvAddIconThree.setVisibility(View.GONE);
                tvCloseIconThree.setVisibility(View.VISIBLE);
                break;
            case 4:
                getImageFromUrl(ivUserImageFour);
                tvAddIconFour.setVisibility(View.GONE);
                tvCloseIconFour.setVisibility(View.VISIBLE);
                break;
            case 5:
                getImageFromUrl(ivUserImageFive);
                tvAddIconFive.setVisibility(View.GONE);
                tvCloseIconFive.setVisibility(View.VISIBLE);
                break;
            case 6:
                getImageFromUrl(ivUserImageSix);
                tvAddIconSix.setVisibility(View.GONE);
                tvCloseIconSix.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_GET_EDIT_PROFILE:
                if (jsonResp.isSuccess()) {
                    onSuccessGetEditProfile(jsonResp);
                } else {
                }
                break;
            case REQ_UPDATE_PROFILE:
                if (jsonResp.isSuccess()) {
                    onBackPressed = true;
                    onBackPressed();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_UPLOAD_PROFILE_IMG:
                if (jsonResp.isSuccess()) {
                    if (isDelete) {
                        commonMethods.showProgressDialog(this, customDialog);
                        apiService.remove_profile_image(Integer.valueOf(image_id.get(clickPos - 1)), sessionManager.getToken()).enqueue(new RequestCallback(REQ_REMOVE_IMAGE, this));
                    } else {
                        onSuccessGetEditProfile(jsonResp);
                    }
                } else {
                }
                break;
            case REQ_REMOVE_IMAGE:
                if (jsonResp.isSuccess()) {
                    onSuccessGetEditProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            default:
                break;
        }
    }

    public void getImageFromUrl(ImageView imageView) {
        Glide.with(this)
                .load(img)
                .transforms(new CenterCrop(), new RoundedCorners(20))
                //.bitmapTransform(new RoundedCornersTransformation(this, 5, 1))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessGetEditProfile(JsonResponse jsonResp) {
        getImageId(jsonResp);
        editProfileModel = gson.fromJson(jsonResp.getStrResponse(), EditProfileModel.class);
        if (editProfileModel != null) {
            updateView();
        }
    }

    private void updateProfile() {
        about = edtAbout.getText().toString().trim();
        jobTitle = edtJobTitle.getText().toString().trim();
        company = edtCompany.getText().toString().trim();
        school = edtSchool.getText().toString().trim();

        commonMethods.showProgressDialog(this, customDialog);
        apiService.updateProfile(getParams()).enqueue(new RequestCallback(REQ_UPDATE_PROFILE, this));
    }

    @Override
    public void onBackPressed() {
        if (onBackPressed)
            super.onBackPressed();
        else {
            updateProfile();
        }
    }

    private void updateView() {

        if (!TextUtils.isEmpty(editProfileModel.getIsOrder()) && editProfileModel.getIsOrder().equalsIgnoreCase("Yes")) {
            sessionManager.setIsOrder(true);
            sessionManager.setPlanType(editProfileModel.getPlanType());
            swHideAge.setClickable(true);
            swHideDistance.setClickable(true);
        } else {
            sessionManager.setIsOrder(false);
            sessionManager.setPlanType(editProfileModel.getPlanType());
            swHideAge.setClickable(false);
            swHideDistance.setClickable(false);
        }

        if (!TextUtils.isEmpty(editProfileModel.getAbout())) {
            edtAbout.setText(editProfileModel.getAbout());
            about = editProfileModel.getAbout();
        }
        if (!TextUtils.isEmpty(editProfileModel.getWork())) {
            edtCompany.setText(editProfileModel.getWork());
            company = editProfileModel.getWork();
        }
        if (!TextUtils.isEmpty(editProfileModel.getJobTitle())) {
            edtJobTitle.setText(editProfileModel.getJobTitle());
            jobTitle = editProfileModel.getJobTitle();
        }
        if (!TextUtils.isEmpty(editProfileModel.getCollege())) {
            edtSchool.setText(editProfileModel.getCollege());
            school = editProfileModel.getCollege();
        }
        if (!TextUtils.isEmpty(editProfileModel.getInstagramId())) {
            tvInstagram.setText(editProfileModel.getInstagramId());
            tvConnect.setText(getString(R.string.disconnect));
            instagramUserName = editProfileModel.getInstagramId();
        } else {
            instagramUserName = "";
            tvConnect.setText(getString(R.string.connect));
            tvInstagram.setText(getString(R.string.instagram));
        }
        if (!TextUtils.isEmpty(editProfileModel.getGender())) {
            gender = editProfileModel.getGender();
            if (gender.equalsIgnoreCase("Men")) {
                rbMan.setChecked(true);
            } else {
                rbWoman.setChecked(true);
            }
        } else {
            gender = "Men";
            rbMan.setChecked(true);
        }
        if (!TextUtils.isEmpty(editProfileModel.getShowMyAge())) {
            hideMyAge = editProfileModel.getShowMyAge();
            if (hideMyAge.equalsIgnoreCase("no")) {
                swHideAge.setChecked(true);
            } else {
                swHideAge.setChecked(false);
            }
        } else {
            swHideAge.setChecked(false);
        }
        if (!TextUtils.isEmpty(editProfileModel.getDistanceInvisible())) {
            distanceInvisible = editProfileModel.getDistanceInvisible();
            if (distanceInvisible.equalsIgnoreCase("no")) {
                swHideDistance.setChecked(true);
            } else {
                swHideDistance.setChecked(false);
            }
        } else {
            swHideDistance.setChecked(false);
        }
        ArrayList<ImageModel> imageList = editProfileModel.getImageList();
        if (imageList.size() > 0) {
            if (imageList.get(0) != null) {
                imageUtils.loadImageCurve(this, ivUserImageOne, imageList.get(0).getSmallImageUrl(), imageList.get(0).getImageId());
                tvAddIconOne.setVisibility(View.GONE);
                tvCloseIconOne.setVisibility(View.VISIBLE);
            } else {
                tvAddIconOne.setVisibility(View.VISIBLE);
                tvCloseIconOne.setVisibility(View.GONE);
                ivUserImageOne.setImageDrawable(null);
                tvAddIconTwo.setVisibility(View.VISIBLE);
                tvCloseIconTwo.setVisibility(View.GONE);
                ivUserImageTwo.setImageDrawable(null);
                tvAddIconThree.setVisibility(View.VISIBLE);
                tvCloseIconThree.setVisibility(View.GONE);
                ivUserImageThree.setImageDrawable(null);
                tvAddIconFour.setVisibility(View.VISIBLE);
                tvCloseIconFour.setVisibility(View.GONE);
                ivUserImageFour.setImageDrawable(null);
                tvAddIconFive.setVisibility(View.VISIBLE);
                tvCloseIconFive.setVisibility(View.GONE);
                ivUserImageFive.setImageDrawable(null);
                tvAddIconSix.setVisibility(View.VISIBLE);
                tvCloseIconSix.setVisibility(View.GONE);
                ivUserImageSix.setImageDrawable(null);
            }
            if (imageList.size() > 1) {
                imageUtils.loadImageCurve(this, ivUserImageTwo, imageList.get(1).getSmallImageUrl(), imageList.get(1).getImageId());
                tvAddIconTwo.setVisibility(View.GONE);
                tvCloseIconTwo.setVisibility(View.VISIBLE);
            } else {
                tvAddIconTwo.setVisibility(View.VISIBLE);
                tvCloseIconTwo.setVisibility(View.GONE);
                ivUserImageTwo.setImageDrawable(null);
                tvAddIconThree.setVisibility(View.VISIBLE);
                tvCloseIconThree.setVisibility(View.GONE);
                ivUserImageThree.setImageDrawable(null);
                tvAddIconFour.setVisibility(View.VISIBLE);
                tvCloseIconFour.setVisibility(View.GONE);
                ivUserImageFour.setImageDrawable(null);
                tvAddIconFive.setVisibility(View.VISIBLE);
                tvCloseIconFive.setVisibility(View.GONE);
                ivUserImageFive.setImageDrawable(null);
                tvAddIconSix.setVisibility(View.VISIBLE);
                tvCloseIconSix.setVisibility(View.GONE);
                ivUserImageSix.setImageDrawable(null);
            }
            if (imageList.size() > 2) {
                imageUtils.loadImageCurve(this, ivUserImageThree, imageList.get(2).getSmallImageUrl(), imageList.get(2).getImageId());
                tvAddIconThree.setVisibility(View.GONE);
                tvCloseIconThree.setVisibility(View.VISIBLE);
            } else {
                tvAddIconThree.setVisibility(View.VISIBLE);
                tvCloseIconThree.setVisibility(View.GONE);
                ivUserImageThree.setImageDrawable(null);
                tvAddIconFour.setVisibility(View.VISIBLE);
                tvCloseIconFour.setVisibility(View.GONE);
                ivUserImageFour.setImageDrawable(null);
                tvAddIconFive.setVisibility(View.VISIBLE);
                tvCloseIconFive.setVisibility(View.GONE);
                ivUserImageFive.setImageDrawable(null);
                tvAddIconSix.setVisibility(View.VISIBLE);
                tvCloseIconSix.setVisibility(View.GONE);
                ivUserImageSix.setImageDrawable(null);
            }
            if (imageList.size() > 3) {
                imageUtils.loadImageCurve(this, ivUserImageFour, imageList.get(3).getSmallImageUrl(), imageList.get(3).getImageId());
                tvAddIconFour.setVisibility(View.GONE);
                tvCloseIconFour.setVisibility(View.VISIBLE);
            } else {
                tvAddIconFour.setVisibility(View.VISIBLE);
                tvCloseIconFour.setVisibility(View.GONE);
                ivUserImageFour.setImageDrawable(null);
                tvAddIconFive.setVisibility(View.VISIBLE);
                tvCloseIconFive.setVisibility(View.GONE);
                ivUserImageFive.setImageDrawable(null);
                tvAddIconSix.setVisibility(View.VISIBLE);
                tvCloseIconSix.setVisibility(View.GONE);
                ivUserImageSix.setImageDrawable(null);
            }
            if (imageList.size() > 4) {
                imageUtils.loadImageCurve(this, ivUserImageFive, imageList.get(4).getSmallImageUrl(), imageList.get(4).getImageId());
                tvAddIconFive.setVisibility(View.GONE);
                tvCloseIconFive.setVisibility(View.VISIBLE);
            } else {
                tvAddIconFive.setVisibility(View.VISIBLE);
                tvCloseIconFive.setVisibility(View.GONE);
                ivUserImageFive.setImageDrawable(null);
                tvAddIconSix.setVisibility(View.VISIBLE);
                tvCloseIconSix.setVisibility(View.GONE);
                ivUserImageSix.setImageDrawable(null);
            }
            if (imageList.size() > 5) {
                imageUtils.loadImageCurve(this, ivUserImageSix, imageList.get(5).getSmallImageUrl(), imageList.get(5).getImageId());
                tvAddIconSix.setVisibility(View.GONE);
                tvCloseIconSix.setVisibility(View.VISIBLE);
            } else {
                tvAddIconSix.setVisibility(View.VISIBLE);
                tvCloseIconSix.setVisibility(View.GONE);
                ivUserImageSix.setImageDrawable(null);
            }

        }
    }

    public void getImageId(JsonResponse jsonResp) {
        Log.e(TAG, "getImageId: "+jsonResp.getUrl());
        image_id.clear();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonResp.getStrResponse());
            JSONArray array = jsonObject.getJSONArray("image_url");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                img_id = object.getString("image_id");
                image_id.add(img_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_hide_age:
                hideMyAge = isChecked ? "No" : "Yes";
                System.out.println("Hide My Age " + hideMyAge);
                break;
            case R.id.switch_hide_distance:
                distanceInvisible = isChecked ? "No" : "Yes";
                System.out.println("Hide My distance " + distanceInvisible);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    startCropImage();
                    break;
                case Constants.REQUEST_CODE_GALLERY:
                    try {
                        InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                        copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        if (inputStream != null) inputStream.close();
                        startCropImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    try {
                        imagePath = result.getUri().getPath();
                        if (!TextUtils.isEmpty(imagePath)) {
                            Bitmap mbitmap = BitmapFactory.decodeFile(imagePath);
                            Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                            Canvas canvas = new Canvas(imageRounded);
                            Paint mpaint = new Paint();
                            mpaint.setAntiAlias(true);
                            mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                            canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 25, 25, mpaint);// Round Image Corner 100 100 100 100
                            loadImages();
                            commonMethods.showProgressDialog(this, customDialog);
                            new ImageCompressAsyncTask(EditProfileActivity.this, imagePath, this, "").execute();
                        }
                    } catch (OutOfMemoryError | Exception e) {
                        e.printStackTrace();
                    }
                    break;
               /* case InstagramHelperConstants.INSTA_LOGIN:
                    InstagramUser user = instagramHelper.getInstagramUser(this);
                    if (user != null && user.getData() != null && !TextUtils.isEmpty(user.getData().getUsername())) {
                        tvInstagram.setText(user.getData().getUsername());
                        tvConnect.setText(getString(R.string.disconnect));
                        instagramUserName = user.getData().getUsername();
                    }
                    break;*/
                default:
                    break;
            }
        }
    }


    private void copyStream(InputStream input, FileOutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private void startCropImage() {
        if (imageFile == null) return;
        int[] minimumSquareDimen = ImageMinimumSizeCalculator.getMinSquarDimension(Uri.fromFile(imageFile), this);
        CropImage.activity(Uri.fromFile(imageFile))
                .setDefaultlyCropEnabled(true)
                .setAspectRatio(10, 10)
                .setOutputCompressQuality(100)
                .setMinCropResultSize(minimumSquareDimen[0], minimumSquareDimen[1])
                .start(this);
    }

    @Override
    public void onImageCompress(String filePath, RequestBody requestBody) {
        commonMethods.hideProgressDialog();
        if (!TextUtils.isEmpty(filePath) && requestBody != null) {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.uploadProfileImg(requestBody).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));
        }
    }

    public void checkPurchase(int type) {
        if (sessionManager.getIsOrder()) {
            if (type == 0) {
                swHideDistance.setChecked(!swHideDistance.isChecked());
            } else {
                swHideAge.setChecked(!swHideAge.isChecked());
            }
        } else {
            if (type == 0) {
                swHideDistance.setChecked(false);
            } else {
                swHideAge.setChecked(false);
            }

            Intent intent = new Intent(EditProfileActivity.this, IgniterPlusDialogActivity.class);
            intent.putExtra("startwith", "");
            intent.putExtra("type", "plus");
            startActivity(intent);
        }
    }

    protected void onResume() {
        super.onResume();
        if (sessionManager.getIsOrder()) {
            swHideAge.setClickable(true);
            swHideDistance.setClickable(true);
        } else {
            swHideAge.setClickable(false);
            swHideDistance.setClickable(false);
        }
    }

    private void checkAllPermission(String[] permission, boolean isDelete) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(this, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            boolean isBlocked = runTimePermission.isPermissionBlocked(this, blockedPermission.toArray(new String[blockedPermission.size()]));
            if (isBlocked) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        showEnablePermissionDailog(0, getString(R.string.please_enable_permissions));
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, permission, 300);
            }
        } else {
            pickProfileImg(isDelete);
            //checkGpsEnable();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf, isDelete);
        } else {
            pickProfileImg(isDelete);
        }
    }

    private void showEnablePermissionDailog(final int type, String message) {
        if (!customDialog.isVisible()) {
            customDialog = new CustomDialog(message, getString(R.string.okay), new CustomDialog.btnAllowClick() {
                @Override
                public void clicked() {
                    if (type == 0)
                        callPermissionSettings();
                    else
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
                }
            });
            customDialog.show(getSupportFragmentManager(), "");
        }
    }

    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }
}
