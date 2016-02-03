package com.alex.alexchat.adapter.baseadapter;
import github.common.utils.ViewUtil;
import github.wasabeef.glide.transformations.CropCircleTransformation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.chatroom.SingleChatActivity;
import com.alex.alexchat.adapter.baseadapter.ContactAdapter.MyViewHolder;
import com.alex.alexchat.bean.UserBean;
import com.alex.alexchat.config.App;
import github.common.rongim.RongUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ContactAdapter extends RecyclerView.Adapter<MyViewHolder>
{
	public Context context;
	public  List<UserBean> list;
	public ContactAdapter(Context context) {
		this.context = context;
		this.list = new ArrayList<UserBean>();
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position)
	{
		UserBean bean = list.get(position);
		ViewUtil.setText2TextView(holder.tvUserName, bean.name);
		Glide.with(context).load(bean.portraitUri).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(context)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.iv);
		holder.layoutBody.setOnClickListener(new MyOnClickListener(position));
	}
	private final class MyOnClickListener implements View.OnClickListener
	{
		private int position;
		public MyOnClickListener(int position){
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			UserBean bean = list.get(position);
			Intent intent = new Intent(context, SingleChatActivity.class);
			intent.putExtra(App.keyChatType, App.chatTypeSingle);
			intent.putExtra(App.keyTargetId, bean.id);
			intent.putExtra(App.keyTargetName, bean.name);
			intent.putExtra(App.keyTargetPortraitUri, bean.portraitUri);
			intent.putExtra(App.keyRyUserId, RongUser.getValue(context, RongUser.userId));
			intent.putExtra(App.keyRyUserName, RongUser.getValue(context, RongUser.userName));
			intent.putExtra(App.keyRyUserToken, RongUser.getValue(context, RongUser.userToken));
			intent.putExtra(App.keyRyPortraitUri, RongUser.getValue(context, RongUser.portraitUri));
			intent.putExtra(App.keyMustConnectRongIMServer, false);
			context.startActivity(intent);
		}
	}
	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_contact, parent, false);
		return new MyViewHolder(view);
	}
	public final class MyViewHolder extends RecyclerView.ViewHolder
	{
		public TextView tvUserName;
		public ImageView iv;
		public View layoutBody;
		public MyViewHolder(View view)
		{
			super(view);
			tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			iv = (ImageView) view.findViewById(R.id.iv_user);
			layoutBody = view.findViewById(R.id.layout_body);
		}
	}
	@Override
	public int getItemCount(){
		return (list==null) ? 0:list.size();
	}
	public void addItem(UserBean obj){
		if(this.list==null){
			return ;
		}
		this.list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<UserBean> list){
		if(this.list==null){
			return ;
		}
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void refreshItem(List<UserBean> list){
		if(this.list==null){
			return ;
		}
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void clearItem(){
		if(list == null){
			return ;
		}
		list.clear();
		notifyDataSetChanged();
	}
	public void removeItem(int position){
		if((list == null) || (list.size()<=position)){
			return;
		}
		list.remove(position);
		notifyDataSetChanged();
	}
	public List<UserBean> getList(){
		return list;
	}
}
