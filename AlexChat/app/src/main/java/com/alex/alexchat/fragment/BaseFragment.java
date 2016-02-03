package com.alex.alexchat.fragment;
import github.common.listener.CommonOnKeyListener;
import github.common.utils.ActivityUtil;
import github.common.utils.XUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.alex.alexchat.R;
import com.alex.alexchat.config.App;
import com.alex.alexchat.config.Cache;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
@SuppressLint("InflateParams") 
public abstract class BaseFragment extends Fragment
{
	protected Context context;
	protected Activity activity;
	protected View rootView;
	protected Dialog loadingDialog;
	protected HttpUtils httpUtils;
	protected DbUtils dbUtils;
	protected String loadType;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		this.activity = activity;
		loadType = App.loadTypeFirst;
		initDialogLoading();
		initHttputils();
	}
	/**初始化Loading对话框！*/
	private void initDialogLoading()
	{
		loadingDialog = new Dialog(context, R.style.dialog_loading);
		loadingDialog.setContentView(R.layout.loading_circle_orange);
		loadingDialog.setCancelable(true);
		loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setOnKeyListener(new CommonOnKeyListener(activity));
	}
	public void showDialog(Dialog dialog, Context context){
		if((context!=null) && ActivityUtil.isActivityOnForeground(context) && (dialog!=null)){
			loadingDialog.show();
		}
	}
	public void dismissDialog(Dialog dialog){
		if(dialog!=null){
			loadingDialog.dismiss();
		}
	}
	/**初始化 xUtils.HttpUtils*/
	private void initHttputils()
	{
		httpUtils = XUtil.getHttpUtilInstance();
		dbUtils = XUtil.getDbUtilsInstance(context, Cache.dbDir, Cache.dbName, Cache.dbVersion);
	}
	public abstract void initView();
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if(rootView!=null){
			((ViewGroup)rootView.getParent()).removeView(rootView);
		}
	}
}
