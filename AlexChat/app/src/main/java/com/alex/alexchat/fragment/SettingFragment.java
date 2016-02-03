package com.alex.alexchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.MainActivity;
import com.alex.alexchat.activity.chatroom.ChatRoomActivity;
import com.alex.alexchat.bean.UserBean;

import github.common.rongim.RongUtil;
import github.common.rongim.RongUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.socks.library.KLog;

import github.common.utils.ViewUtil;
import github.common.utils.XUtil;
import github.wasabeef.glide.transformations.BlurTransformation;
import github.wasabeef.glide.transformations.CropCircleTransformation;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * <pre>设置界面
 * 启动者：{@link ChatRoomActivity}
 * 启动：RegisterActivity
 * 启动：LoginActivity
 * </pre>
 * */
public class SettingFragment extends BaseFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedIdnstanceState)
	{
		if(rootView == null){
			rootView = inflater.inflate(R.layout.fragment_setting, null);
			initView();
		}
		return rootView;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void initView()
	{
		ImageView ivUser = (ImageView) rootView.findViewById(R.id.iv_user);
		ImageView ivBackground = (ImageView) rootView.findViewById(R.id.iv_background);
		String portraitUri = RongUser.getValue(getActivity(), RongUser.portraitUri);
		Glide.with(context).load(portraitUri).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(context)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(ivUser);
		Glide.with(context).load(portraitUri).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new BlurTransformation(context)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(ivBackground);
		rootView.findViewById(R.id.bt_logout).setOnClickListener(new MyOnClickListener());
		ViewUtil.setText2TextView(rootView.findViewById(R.id.tv_user_name), RongUser.getValue(context, RongUser.userName));
	}
	private final class MyOnClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v) {
			RongIMClient.getInstance().logout();
			RongIMClient.getInstance().disconnect();
			Conversation.ConversationType conversationTypes[] = {Conversation.ConversationType.CHATROOM, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.GROUP, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.SYSTEM};

			RongIMClient.getInstance().clearConversations(new MyResultCallback(), conversationTypes);
		}
	}

	private final class MyResultCallback extends RongIMClient.ResultCallback<Boolean>
	{
		@Override
		public void onSuccess(Boolean result) {
			KLog.e("清空会话 result = "+result);
			RongUser.logout(context);
			startActivity(new Intent(context, MainActivity.class));
			getActivity().finish();
		}
		@Override
		public void onError(RongIMClient.ErrorCode errorCode) {
			KLog.e("errorCode = " + RongUtil.errorCode2String(errorCode));
			getActivity().finish();
		}
	}

}
