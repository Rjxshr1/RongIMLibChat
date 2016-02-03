package com.alex.alexchat.application;
import github.ereza.CustomActivityOnCrash;
import github.ereza.ErrorActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;

import com.alex.alexchat.activity.MainActivity;
import com.alex.alexchat.config.SDK;

public class AlexApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		initCrash();
		initRongYunSDK();
	}
	private void initRongYunSDK()
	{
		RongIMClient.init(this);
		RongIM.init(this, SDK.rongAppKey);
	}
	/**版本要求 Android 4.0    api 14*/
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void initCrash()
	{
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			CustomActivityOnCrash.install(this);
			CustomActivityOnCrash.setEnableAppRestart(true);
			CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(true);
			CustomActivityOnCrash.setShowErrorDetails(true);
			CustomActivityOnCrash.setErrorActivityClass(ErrorActivity.class);
			CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
		}
	}
}
