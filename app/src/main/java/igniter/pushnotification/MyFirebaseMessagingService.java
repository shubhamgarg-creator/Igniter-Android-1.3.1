package igniter.pushnotification;
/**
 * @package com.trioangle.igniter
 * @subpackage pushnotification
 * @category FirebaseMessagingService
 * @author Trioangle Product Team
 * @version 1.0
 */

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.views.chat.ChatConversationActivity;
import igniter.views.main.HomeActivity;

/**************************************************************
 Firebase Messaging Service to base push notification message to activity
 ****************************************************************/
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    @Inject
    SessionManager sessionManager;
    String message = "", status = "";

    @Override
    public void onCreate() {
        super.onCreate();
        AppController.getAppComponent().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "Message Notification remoteMessage: " + remoteMessage.toString());

        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());


            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
                if (remoteMessage.getNotification() != null) {
                    Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("custom");

            sessionManager.setPushNotification(json.toString());

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                Log.e(TAG, "IF: " + json.toString());
                Log.e(TAG, "Conversation : " + !ChatConversationActivity.isConversationActivity);
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                if (json.getJSONObject("custom").has("chat_status")) {

                    status = json.getJSONObject("custom").getJSONObject("chat_status").getString("status");
                }
                Log.e(TAG, "Conversation : " + !ChatConversationActivity.isConversationActivity);
                if (!ChatConversationActivity.isConversationActivity) {
                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                    notificationUtils.generateNotification(getApplicationContext(), message, status, json.toString());
                    try{
                        ((HomeActivity) getApplicationContext()).changeChatIcon(1);
                    }catch (Exception e){

                    }


                }
            } else {
                Log.e(TAG, "ELSE: " + json.toString());

                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                if (!sessionManager.getToken().equals("")) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }

                if (json.getJSONObject("custom").has("chat_status")) {

                    status = json.getJSONObject("custom").getJSONObject("chat_status").getString("status");
                }
                if (ChatConversationActivity.isConversationActivity) {
                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    notificationUtils.playNotificationSound();
                    notificationUtils.generateNotification(getApplicationContext(), message, status, json.toString());
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


}

