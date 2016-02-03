package com.alex.alexchat.activity;
import github.common.listener.CommonOnKeyListener;
import github.common.utils.XUtil;
import github.jgilfelt.systembartint.SystemBarTintManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.adduser.LoginActivity;
import com.alex.alexchat.config.App;
import com.alex.alexchat.config.Cache;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.util.LogUtils;
public abstract class BaseActivity extends Activity
{
	protected Context context;
	protected Activity activity;
	protected Dialog dialogLoading;
	protected HttpUtils httpUtils;
	protected DbUtils dbUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;
		activity = this;
		initSystemBar();
		initDialogLoading();
		initHttputils();
	}
	/**初始化视图*/
	public abstract void initView();
	/**初始化Loading对话框！*/
	private void initDialogLoading()
	{
		dialogLoading = new Dialog(context, R.style.dialog_loading);
		dialogLoading.setContentView(R.layout.loading_circle_orange);
		dialogLoading.setCancelable(true);
		dialogLoading.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialogLoading.setCanceledOnTouchOutside(false);
		dialogLoading.setOnKeyListener(new CommonOnKeyListener(activity));
	}
	/**初始化 xUtils.HttpUtils*/
	private void initHttputils()
	{
		httpUtils = XUtil.getHttpUtilInstance();
		dbUtils = XUtil.getDbUtilsInstance(context, Cache.dbDir, Cache.dbName, Cache.dbVersion);
	}
	@TargetApi(19)
	private void initSystemBar()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintColor(getResources().getColor(R.color.subzero_title_color_status_bar));
			tintManager.setStatusBarTintEnabled(true);
		}
	}
	/**跳到 LoginActivity 不结束自己
	 * @param mustkillSelf 必须结束自己*/
	public void startLoginActvitiy(Activity activity, boolean mustkillSelf)
	{
		Intent intent = new Intent(activity, LoginActivity.class);
		LogUtils.e("开始跳转 Activity = "+activity.getClass().getSimpleName());
		intent.putExtra(App.keyModuleName, activity.getClass().getSimpleName());
		startActivity(intent);
		if(mustkillSelf){
			activity.finish();
		}
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		dialogLoading.dismiss();
	}
}
