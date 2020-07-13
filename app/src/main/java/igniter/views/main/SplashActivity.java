package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category SplashActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.views.chat.ChatConversationActivity;

/*****************************************************************
 Application splash screen
 ****************************************************************/
public class SplashActivity extends AppCompatActivity {

    @Inject
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppController.getAppComponent().inject(this);
        getIntentValues();
    }

    private void getIntentValues() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callActivityIntent();
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void callActivityIntent() {
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("MainActivity: ", "Key: " + key + " Value: " + value);
            }
            if (getIntent().hasExtra("custom") && !TextUtils.isEmpty(sessionManager.getToken())) {
                String status = "";
                try {
                    JSONObject custom = new JSONObject(getIntent().getStringExtra("custom"));

                    System.out.println("Json object : String "+custom.toString());

                    if (custom.has("match_status")){
                        System.out.println("Json object : String "+custom.getString("match_status"));
                       goToChatFragment();
                    }else{
                        System.out.println("Json object : No Match status");

                        status = custom.getJSONObject("chat_status").getString("status");
                        getToIntent(status, custom);
                    }


                } catch (JSONException e) {
                    goNormalIntent();
                }
            } else {
                goNormalIntent();
            }
        } else {
            goNormalIntent();
        }
    }

    private void goToChatFragment() {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.putExtra("matchStatus", true);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(notificationIntent);
    }

    public void goNormalIntent() {
        if (TextUtils.isEmpty(sessionManager.getToken())) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("isFromSignUp", false);
            startActivity(intent);
        }
    }

    public void getToIntent(String status, JSONObject jsonObject) {

        Intent notificationIntent;
        notificationIntent = new Intent(getApplicationContext(), ChatConversationActivity.class);
        if (status.equals("New message")) {
            notificationIntent = newMessage(jsonObject);
        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(notificationIntent);
        finish();
    }

    public Intent newMessage(JSONObject jsons) {
        try {
            //JSONObject data = new JSONObject(String.valueOf(jsons));
            Intent notificationIntent = new Intent(getApplicationContext(), ChatConversationActivity.class);
            notificationIntent.putExtra("matchId", Integer.valueOf(jsons.getJSONObject("chat_status").getString("match_id")));
            notificationIntent.putExtra("userId", Integer.valueOf(jsons.getJSONObject("chat_status").getString("sender_id")));
            return notificationIntent;
        } catch (JSONException e) {

        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
