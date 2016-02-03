package com.alex.alexchat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.chatroom.ChatRoomActivity;
import com.alex.alexchat.config.App;

/**
 * 重启被回收的 Activity
 * 将他们需要的参数，传递过去
 */
public class StartSelfActivity extends Activity {
    public String moduleName;
    private Context context;
    private Intent newIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_self);
        context = this;
        Intent getIntent = getIntent();
        newIntent = null;
        moduleName = getIntent.getStringExtra(App.keyModuleName);
        if (ChatRoomActivity.class.getSimpleName().equalsIgnoreCase(moduleName)) {
            String userName = getIntent.getStringExtra(App.keyRyUserName);
            String userToken = getIntent.getStringExtra(App.keyRyUserToken);
            String userId = getIntent.getStringExtra(App.keyRyUserId);
            String portraitUri = getIntent.getStringExtra(App.keyTargetPortraitUri);
            newIntent = new Intent(context, ChatRoomActivity.class);
            newIntent.putExtra(App.keyRyUserName, userName);
            newIntent.putExtra(App.keyRyUserToken, userToken);
            newIntent.putExtra(App.keyRyUserId, userId);
            newIntent.putExtra(App.keyTargetPortraitUri, portraitUri);
        } else if (MainActivity.class.getSimpleName().equalsIgnoreCase(moduleName)) {
            newIntent = new Intent(context, MainActivity.class);
        }
        new StartTask().execute();
    }
    private final class StartTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            SystemClock.sleep(2000);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            startActivity(newIntent);
            finish();
        }
    }
}
