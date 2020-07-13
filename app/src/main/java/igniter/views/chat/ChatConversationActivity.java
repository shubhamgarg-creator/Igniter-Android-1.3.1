package igniter.views.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage view.chat
 * @category MatchUsersActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import igniter.R;
import igniter.adapters.chat.ChatConversationListAdapter;
import igniter.adapters.chat.UnmatchReasonListAdapter;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.chat.ChatMessageModel;
import igniter.datamodels.chat.MessageModel;
import igniter.datamodels.chat.NewMatchProfileModel;
import igniter.datamodels.chat.ReasonModel;
import igniter.datamodels.chat.ReceiveDateModel;
import igniter.datamodels.chat.UnMatchReasonModel;
import igniter.datamodels.main.JsonResponse;
import igniter.interfaces.ApiService;
import igniter.interfaces.DropDownClickListener;
import igniter.interfaces.ServiceListener;
import igniter.pushnotification.Config;
import igniter.pushnotification.NotificationUtils;
import igniter.utils.CommonMethods;
import igniter.utils.DateTimeUtility;
import igniter.utils.Enums;
import igniter.utils.ImageUtils;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;
import igniter.views.customize.CustomLayoutManager;
import igniter.views.customize.CustomRecyclerView;
import igniter.views.profile.EnlargeProfileActivity;

/*****************************************************************
 User Chat conversation page
 ****************************************************************/
public class ChatConversationActivity extends AppCompatActivity implements View.OnClickListener, ServiceListener,
        UnmatchReasonListAdapter.OnItemClickListener {

    public static boolean isConversationActivity = false;
    @Inject
    ApiService apiService;
    @Inject
    CommonMethods commonMethods;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson, gson1;

    String reportType;

    @Inject
    ImageUtils imageUtils;
    @Inject
    DateTimeUtility dateTimeUtility;
    /**
     * Dropdown click listener
     */
    DropDownClickListener listener = new DropDownClickListener() {
        @Override
        public void onDropDrownClick(String value) {

            if (value.equalsIgnoreCase(getString(R.string.MUTE_NOTIFICATION))){

            }else if(value.equalsIgnoreCase(getString(R.string.BLOCK))){
                getUnmatchDetails();
            }else if(value.equalsIgnoreCase(getString(R.string.REPORT))){
                getReportDetails();
            }else if(value.equalsIgnoreCase(getString(R.string.UNMATCH))){
                getUnmatchDetails();
            }
        }
    };

    private void getReportDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.unMatchDetails(sessionManager.getToken(), "report").enqueue(new RequestCallback(Enums.REQ_REPORT_DETAILS, this));
    }

    private BroadcastReceiver mBroadcastReceiver;
    private CustomTextView tvHeader, tvGifIcon, tvLeftArrow, tvHeaderMenuIcon, tvName;
    private CircleImageView civHeaderImgOne, civHeaderImgTwo, civHeaderImgSingle, civEmptyChatImage;
    private RelativeLayout rltHeaderImg, rltEmptyChatConversation;
    private View rltEmptyChat;
    private CustomEditText edtChatMsg;
    private CustomRecyclerView rvChatConversationList;
    private Dialog unMatchDialog;

    private ImageView ivSendMsg;
    private ChatConversationListAdapter chatConversationListAdapter;
    private UnmatchReasonListAdapter unmatchReasonListAdapter;
    private CustomLayoutManager linearLayoutManager;
    private AlertDialog dialog;
    private int matchId, userId, userIds;
    private ChatMessageModel chatMessageModel;
    private NewMatchProfileModel newMatchProfileModel;
    private UnMatchReasonModel unMatchReasonModel;
    private ArrayList<MessageModel> messageModels = new ArrayList<>();
    private ArrayList<ReasonModel> reasonModels = new ArrayList<>();
    private String otherUserName = "";
    private ViewGroup mSceneRoot;
    private Scene mScene1;
    private Scene mScene2;
    private LinearLayout llUpdate;

    private EditText edtInfo;
    private String reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_conversation_layout);


        AppController.getAppComponent().inject(this);

        initView();
        msgEditorListener();
        getIntentValues();
    }

    /**
     * Declare variable for layout views
     */

    private void initView() {
        tvName = findViewById(R.id.tv_name);
        tvHeader = findViewById(R.id.tv_header_title);
        tvLeftArrow = findViewById(R.id.tv_left_arrow);
        tvGifIcon = findViewById(R.id.tv_gif_icon);
        ivSendMsg = findViewById(R.id.iv_send);
        rvChatConversationList = findViewById(R.id.rv_chat_conversation_list);
        rltEmptyChat = findViewById(R.id.rlt_empty_chat);
        edtChatMsg = findViewById(R.id.edt_new_msg);
        dialog = commonMethods.getAlertDialog(this);

        civHeaderImgOne = findViewById(R.id.civ_header_image_one);
        civHeaderImgTwo = findViewById(R.id.civ_header_image_two);
        civHeaderImgSingle = findViewById(R.id.civ_header_image_single);
        civEmptyChatImage = findViewById(R.id.civ_empty_chat_image);

        tvHeaderMenuIcon = findViewById(R.id.tv_menu_icon);
        rltHeaderImg = findViewById(R.id.rlt_header_image);

        setUpHeader();

        ivSendMsg.setEnabled(false);

        ivSendMsg.setOnClickListener(this);
        tvGifIcon.setOnClickListener(this);
        tvLeftArrow.setOnClickListener(this);
        tvHeaderMenuIcon.setOnClickListener(this);
        civEmptyChatImage.setOnClickListener(this);
        civHeaderImgOne.setOnClickListener(this);
        initRecyclerView();

        receivePushNotification();

    }

    /**
     * Get Intent values from previous activities
     */
    private void getIntentValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            matchId = bundle.getInt("matchId");
            userIds = bundle.getInt("userId");
            getChatConversationList();
        }
    }

    /**
     * Setup the header layout
     */
    private void setUpHeader() {
        //tvHeader.setText(getString(R.string.chat_conversation_title));
        tvHeader.setText("");
        rltHeaderImg.setVisibility(View.VISIBLE);
        civHeaderImgOne.setVisibility(View.VISIBLE);

        tvHeaderMenuIcon.setVisibility(View.VISIBLE);
    }

    /**
     * Declare recyclerview
     */
    private void initRecyclerView() {
        linearLayoutManager = new CustomLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        rvChatConversationList.setLayoutManager(linearLayoutManager);
        chatConversationListAdapter = new ChatConversationListAdapter(this);
        rvChatConversationList.setAdapter(chatConversationListAdapter);
    }

    /**
     * Textwatcher for message
     */
    private void msgEditorListener() {
        edtChatMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    ivSendMsg.setEnabled(true);
                else
                    ivSendMsg.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Get conversation details using webservice
     */
    private void getChatConversationList() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.messageConversation(sessionManager.getToken(), matchId,TimeZone.getDefault().getID()).enqueue(new RequestCallback(Enums.REQ_MSG_CONVERSATION, this));
    }

    /**
     * Default on click method
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_left_arrow:
                onBackPressed();
                break;
            case R.id.iv_send:

                String msg = edtChatMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(msg) && userId > 0) {
                    MessageModel messageModel = new MessageModel();
                    messageModel.setMessage(msg);
                    messageModel.setSenderId(userId);
                    messageModel.setLikeStatus("");
                    messageModel.setMessageId(10);
                    messageModel.setSenderImageUrl(chatMessageModel.getUserImgUrl());

                    messageModels.add(messageModel);
                    rltEmptyChat.setVisibility(View.GONE);
                    setChatListAdapter();
                    edtChatMsg.setText("");
                    ivSendMsg.setEnabled(false);
                    //commonMethods.showProgressDialog(this, customDialog);
                    apiService.sendMessage(sessionManager.getToken(), matchId, msg, TimeZone.getDefault().getID()).enqueue(new RequestCallback(Enums.REQ_SEND_MSG, this));
                }
                break;
            case R.id.tv_gif_icon:
                break;
            case R.id.tv_menu_icon:
                commonMethods.dropDownMenu(this, view, null, getResources().getStringArray(R.array.QUICK_CHAT_MENU_TITLE), listener);
                break;
            case R.id.civ_empty_chat_image:
                Intent intent = new Intent(this, EnlargeProfileActivity.class);
                intent.putExtra("navType", 3);
                intent.putExtra("userId", userIds);
                startActivity(intent);
                overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
                break;
            case R.id.civ_header_image_one:
                Intent intent1 = new Intent(this, EnlargeProfileActivity.class);
                intent1.putExtra("navType", 3);
                intent1.putExtra("userId", userIds);
                startActivity(intent1);
                overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
                break;

            default:
                break;
        }
    }

    /**
     * Get unmatch details using webservice
     */
    private void getUnmatchDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.unMatchDetails(sessionManager.getToken(), "unmatch").enqueue(new RequestCallback(Enums.REQ_UNMATCH_DETAILS, this));
    }

    /**
     * Get unmatch details using webservice
     */

    private void getBlockDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.unMatchDetails(sessionManager.getToken(), "block").enqueue(new RequestCallback(Enums.REQ_BLOCK_DETAILS, this));
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
        getUnMatchedHashMap.put("reporter_id", String.valueOf(chatMessageModel.getLikedId()));
        getUnMatchedHashMap.put("message", userMessage);
        getUnMatchedHashMap.put("type", reportType);

        return getUnMatchedHashMap;
    }

    /**
     * Set chat conversation list adapter
     */
    private void setChatListAdapter() {
        if (messageModels.size() > 0) {
            chatConversationListAdapter = new ChatConversationListAdapter(this, messageModels, otherUserName);
            rvChatConversationList.setAdapter(chatConversationListAdapter);
            linearLayoutManager = (CustomLayoutManager) rvChatConversationList.getLayoutManager();
        } else {
            chatConversationListAdapter = new ChatConversationListAdapter(this);
            rvChatConversationList.setAdapter(chatConversationListAdapter);
        }
    }


    /**
     * API response success
     */
    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case Enums.REQ_MSG_CONVERSATION:
                if (jsonResp.isSuccess()) {
                    onSuccessGetMsgList(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_SEND_MSG:
                if (jsonResp.isSuccess()) {

                    ReceiveDateModel receiveDateModel;
                    String dateResp = "";
                    String json = jsonResp.getStrResponse();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        dateResp = String.valueOf(jsonObject.getJSONObject("messages").getJSONObject("received_date_time"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    receiveDateModel = gson.fromJson(dateResp, ReceiveDateModel.class);
                    messageModels.get(messageModels.size() - 1).setReceivedDate(receiveDateModel);
                    rltEmptyChat.setVisibility(View.GONE);
                    setChatListAdapter();
                    //getChatConversationList();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_UNMATCH_DETAILS:
                if (jsonResp.isSuccess()) {
                    onSuccessGetUnmatchDetails(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_BLOCK_DETAILS:
                if (jsonResp.isSuccess()) {
                    onSuccessGetBlockDetails(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case Enums.REQ_REPORT_DETAILS:
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
            default:
                break;
        }
    }

    private void onSuccessReportList(JsonResponse jsonResp) {
        reportType = "report";
        unMatchReasonModel = gson.fromJson(jsonResp.getStrResponse(), UnMatchReasonModel.class);
        if (unMatchReasonModel != null) {
            updateUnMatchView();
        }
    }

    /**
     * API response failure
     */
    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
    }

    /**
     * Get Message list after API success
     */
    private void onSuccessGetMsgList(JsonResponse jsonResp) {
        chatMessageModel = gson.fromJson(jsonResp.getStrResponse(), ChatMessageModel.class);
        newMatchProfileModel = gson1.fromJson(jsonResp.getStrResponse(), NewMatchProfileModel.class);
        if (chatMessageModel != null) {
            updateView();
        }
    }

    /**
     * Get unmatch reason list after API success
     */
    private void onSuccessGetUnmatchDetails(JsonResponse jsonResp) {
        reportType = "unmatch";
        unMatchReasonModel = gson.fromJson(jsonResp.getStrResponse(), UnMatchReasonModel.class);
        if (unMatchReasonModel != null) {
            updateUnMatchView();
        }
    }

    /**
     * Get block reason list after API success
     */
    private void onSuccessGetBlockDetails(JsonResponse jsonResp) {
        reportType = "block";
        unMatchReasonModel = gson.fromJson(jsonResp.getStrResponse(), UnMatchReasonModel.class);
        if (unMatchReasonModel != null) {
            updateUnMatchView();
        }
    }

    /**
     * Get response after un match user
     */
    private void onSuccessUnmatchUser(JsonResponse jsonResp) {
        unMatchDialog.dismiss();
        if (reportType.equals("unmatch") || reportType.equals("block")) {
            if (unMatchDialog.isShowing())
                unMatchDialog.dismiss();
            sessionManager.setRefreshChatFragment(true);
            finish();
        }
        if (reportType.equals("report")) {
            if (unMatchDialog.isShowing())
                unMatchDialog.dismiss();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
//            finish();
        }


    }

    /**
     * Update views based on the response
     */
    private void updateView() {

        userId = chatMessageModel.getUserId();
        if (sessionManager.getUserId() == 0)
            sessionManager.setUserId(userId);
        if (!TextUtils.isEmpty(chatMessageModel.getLikedImageUrl())) {
            imageUtils.loadCircleImage(this, civHeaderImgOne, chatMessageModel.getLikedImageUrl());
            imageUtils.loadCircleImage(this, civEmptyChatImage, chatMessageModel.getLikedImageUrl());
        }

        /*if (!TextUtils.isEmpty(chatMessageModel.getLikedImageUrl())) {
            imageUtils.loadCircleImage(this, civHeaderImgTwo, chatMessageModel.getLikedImageUrl());
        }*/

        if (!TextUtils.isEmpty(chatMessageModel.getLikedUsername())) {
            otherUserName = chatMessageModel.getLikedUsername();
            tvHeader.setText(String.format(getString(R.string.chat_conversation_title), otherUserName));
            tvName.setText(otherUserName);
        }
        if (chatMessageModel.getMessageModels() != null && chatMessageModel.getMessageModels().size() > 0) {
            rltEmptyChat.setVisibility(View.GONE);
            messageModels.clear();
            messageModels.addAll(chatMessageModel.getMessageModels());
            setChatListAdapter();
        } else {
            rltEmptyChat.setVisibility(View.VISIBLE);
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
     * Show unmatch reason dialog
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showUnMatchDialog(String title, String message, int type, ArrayList<ReasonModel> reasonModel) {
        unMatchDialog = new Dialog(ChatConversationActivity.this);
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
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        //reasonLay = inflater.inflate(R.layout.tell_us_why, null);


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


        mScene1 = new Scene(mSceneRoot,unMatchDialog.findViewById(R.id.rv_reason_unmatch_list));
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getUnMatchReason() {
        llUpdate.setVisibility(View.VISIBLE);
        llUpdate.animate().translationY(0);

        TransitionManager.go(mScene2);
        edtInfo = (EditText) mSceneRoot.findViewById(R.id.edt_info);

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
     * Get notification from Firebase broadcast
     */
    public void receivePushNotification() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    String JSON_DATA = sessionManager.getPushNotification();

                    try {
                        JSONObject jsonObject = new JSONObject(JSON_DATA);

                        if (jsonObject.getJSONObject("custom").has("chat_status") && jsonObject.getJSONObject("custom").getJSONObject("chat_status").getString("status").equals("New message")) {

                            //getChatConversationList();
                            String cumessage = jsonObject.getJSONObject("custom").getJSONObject("chat_status").getString("message");
                            ReceiveDateModel receiveDateModel;//=new ReceiveDateModel();
                            String dateResp = String.valueOf(jsonObject.getJSONObject("custom").getJSONObject("chat_status").getJSONObject("received_date_time"));
                            receiveDateModel = gson.fromJson(dateResp, ReceiveDateModel.class);

                            MessageModel messageModel = new MessageModel();
                            messageModel.setMessage(cumessage);
                            messageModel.setSenderId(userIds);
                            messageModel.setReceivedDate(receiveDateModel);
                            messageModel.setLikeStatus("");
                            messageModel.setMessageId(10);
                            messageModel.setSenderImageUrl(chatMessageModel.getLikedImageUrl());

                            messageModels.add(messageModel);
                            rltEmptyChat.setVisibility(View.GONE);

                            setChatListAdapter();
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        isConversationActivity = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        isConversationActivity = true;
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
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