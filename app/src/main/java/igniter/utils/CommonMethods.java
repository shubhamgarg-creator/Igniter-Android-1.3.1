package igniter.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category CommonMethods
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import igniter.R;
import igniter.adapters.main.DropDownMenuAdapter;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.SessionManager;
import igniter.interfaces.DropDownClickListener;
import igniter.views.customize.CustomDialog;

/*****************************************************************
 CommonMethods
 ****************************************************************/
public class CommonMethods {

    public static PopupWindow popupWindow = null;
    public static CustomDialog progressDialog;
    @Inject
    SessionManager sessionManager;

    public CommonMethods() {
        AppController.getAppComponent().inject(this);
    }

    public void showProgressDialog(AppCompatActivity mActivity, CustomDialog customDialog) {
        if (mActivity == null || customDialog == null || (customDialog.getDialog() != null && customDialog.getDialog().isShowing()))
            return;
        progressDialog = new CustomDialog(true);
        progressDialog.show(mActivity.getSupportFragmentManager(), "");
    }

    public void hideProgressDialog() {
        if (progressDialog == null || progressDialog.getDialog() == null || !progressDialog.getDialog().isShowing())
            return;
        progressDialog.dismissAllowingStateLoss();
        progressDialog = null;
    }

    //Show Dialog with message
    public void showMessage(Context context, AlertDialog dialog, String msg) {
        if (context != null && dialog != null && !((Activity) context).isFinishing()) {
            dialog.setMessage(msg);
            dialog.show();
        }
    }

    //Create and Get Dialog
    public AlertDialog getAlertDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        return dialog;
    }

    public boolean isNotEmpty(Object s) {
        if (s == null) {
            return false;
        }
        if ((s instanceof String) && (((String) s).trim().length() > 0)) {
            return true;
        }
        if (s instanceof ArrayList) {
            return !((ArrayList<?>) s).isEmpty();
        }
        if (s instanceof Map) {
            return !((Map<?, ?>) s).isEmpty();
        }
        if (s instanceof List) {
            return !((List<?>) s).isEmpty();
        }

        if (s instanceof Object[]) {
            return !(((Object[]) s).length == 0);
        }
        return false;
    }

    public boolean isTaped() {
        if (SystemClock.elapsedRealtime() - Constants.mLastClickTime < 1000) return true;
        Constants.mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    public File cameraFilePath() {
        return new File(getDefaultCameraPath(), "Igniter_" + System.currentTimeMillis() + ".png");
    }

    public String getDefaultCameraPath() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (path.exists()) {
            File test1 = new File(path, "Camera/");
            if (test1.exists()) {
                path = test1;
            } else {
                File test2 = new File(path, "100MEDIA/");
                if (test2.exists()) {
                    path = test2;
                } else {
                    File test3 = new File(path, "100ANDRO/");
                    if (test3.exists()) {
                        path = test3;
                    } else {
                        test1.mkdirs();
                        path = test1;
                    }
                }
            }
        } else {
            path = new File(path, "Camera/");
            path.mkdirs();
        }
        return path.getPath();
    }

    public void refreshGallery(Context context, File file) {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file); //out is your file you saved/deleted/moved/copied
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////// CAMERA ///////////////////////////////////
    public File getDefaultFileName(Context context) {
        File imageFile;
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) { // External storage path
            imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        } else {  // Internal storage path
            imageFile = new File(context.getFilesDir() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        }
        return imageFile;
    }

    public void clearFileCache(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dropDownMenu(AppCompatActivity mActivity, View v, int[] images, final String[] listItems, final DropDownClickListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = layoutInflater.inflate(R.layout.drop_down_menu_list, null);
        ListView listView = (ListView) popupView.findViewById(R.id.drop_down_list);

        DropDownMenuAdapter adapter = new DropDownMenuAdapter(mActivity, R.layout.drop_down_menu_row, images, listItems);
        listView.setAdapter(adapter);
        listView.setSelected(false);

        Rect displayFrame = new Rect();
        v.getWindowVisibleDisplayFrame(displayFrame);

        int displayFrameWidth = displayFrame.right - displayFrame.left;
        int[] loc = new int[2];
        v.getLocationInWindow(loc);

        if (popupWindow == null) {
            int width = (int) (120 * Resources.getSystem().getDisplayMetrics().density);
            popupWindow = new PopupWindow(popupView, width, WindowManager.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (!popupWindow.isShowing()) {
            popupView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.popup_anim_in));
            int margin = (displayFrameWidth - (loc[0] + v.getWidth()));
            int xOff = (displayFrameWidth - margin - popupWindow.getWidth()) - loc[0];
            popupWindow.showAsDropDown(v, (int) (xOff / 0.8), 0);
            popupWindow.setAnimationStyle(R.anim.popup_anim_in);

        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popupWindow != null) popupWindow = null;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                listener.onDropDrownClick(listItems[position]);
            }
        });
    }

    public boolean isSupportTransition() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public boolean isNetWorkAvailable(Context context, AlertDialog dialog, String msg) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo == null || (netInfo != null && !netInfo.isConnected())) {
                showMessage(context, dialog, msg);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isOnline(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public Object getJsonValue(String jsonString, String key, Object object) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) object = jsonObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
        return object;
    }

    public String getJsonValueString(String jsonString, String key, Object object) {
        String values = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) values = jsonObject.get(key).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return values;
        }
        return values;
    }

    public static void showUserMessage(@Nullable String message) {
        try {
            if (!TextUtils.isEmpty(message)) {
                //Toast.makeText(AppController.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showServerInternalErrorMessage(Context context) {
        showUserMessage(context.getString(R.string.internal_server_error));

    }

    public String getAuthType(){
        if (sessionManager.getIsAppleUser()){
            return "apple";
        }else if (sessionManager.getIsFBUser()){
            return "facebook";
        }else {
            return "phone";
        }
    }

    public String getAuthId(){
        if (sessionManager.getIsAppleUser()){
            return sessionManager.getappleId();
        }else if (sessionManager.getIsFBUser()){
            return sessionManager.getFbId();
        }else {
            return "";
        }
    }

    public long getAnimationDuration(){
        return 500;
    }

    public int getAngle(){
        return 25;
    }

    public int getMaxCount(){
        return 1;
    }

    public float getScaleGap(){
        return 0.1f;
    }

    public int getTransYGap(){
        return 0;
    }
}

