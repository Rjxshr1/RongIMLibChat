package com.alex.alexchat.adapter.baseadapter;
import github.common.rongim.RongUtil;
import github.common.utils.TimeUtils;
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
import com.alex.alexchat.adapter.baseadapter.ConversationAdapter.MyViewHolder;
import com.alex.alexchat.config.App;
import github.common.rongim.RongUser;
import io.rong.imlib.model.Conversation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lidroid.xutils.util.LogUtils;
import com.socks.library.KLog;

public class ConversationAdapter extends RecyclerView.Adapter<MyViewHolder>
{
	public Context context;
	/**会话列表*/
	private List<Conversation> list;
	public ConversationAdapter(Context context) {
		this.context = context;
		this.list = new ArrayList<Conversation>();
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position)
	{
		Conversation conversation = list.get(position);
		int count = conversation.getUnreadMessageCount();
		if(count>=1){
			ViewUtil.setText2TextView(holder.tvCount, conversation.getUnreadMessageCount()+"");
		}else{
			holder.tvCount.setVisibility(View.GONE);
		}
		ViewUtil.setText2TextView(holder.tvContent, RongUtil.getMessageSummary(conversation.getLatestMessage()));
		ViewUtil.setText2TextView(holder.tvMessageType, conversation.getConversationType().getName());
		ViewUtil.setText2TextView(holder.tvUserName, RongUtil.getUserName(conversation));
		ViewUtil.setText2TextView(holder.tvTime, TimeUtils.getTimeLable(conversation.getReceivedTime(), "MM月dd日 HH:mm:ss"));
		Glide.with(context).load(RongUtil.getPortraitUri(conversation)).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(context)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.iv);
		holder.layoutBody.setOnClickListener(new MyOnClickListener(position));
	}
	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_conversation, parent, false);
		return new MyViewHolder(view);
	}
	private final class MyOnClickListener implements View.OnClickListener {
		private int position;
		public MyOnClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			Conversation conversation = list.get(position);
			Intent intent = new Intent(context, SingleChatActivity.class);
			intent.putExtra(App.keyChatType, App.getChatType(conversation.getConversationType().getValue()));
			intent.putExtra(App.keyTargetId, conversation.getSenderUserId());
			intent.putExtra(App.keyTargetName, conversation.getSenderUserName());
			intent.putExtra(App.keyTargetPortraitUri, RongUtil.getPortraitUri(conversation));
			intent.putExtra(App.keyRyUserId, RongUser.getValue(context, RongUser.userId));
			intent.putExtra(App.keyRyUserName, RongUser.getValue(context, RongUser.userName));
			intent.putExtra(App.keyRyUserToken, RongUser.getValue(context, RongUser.userToken));
			intent.putExtra(App.keyRyPortraitUri, RongUser.getValue(context, RongUser.portraitUri));
			intent.putExtra(App.keyMustConnectRongIMServer, false);
			context.startActivity(intent);
		}
	}
	public final class MyViewHolder extends RecyclerView.ViewHolder
	{
		public TextView tvMessageType;
		public TextView tvUserName;
		public ImageView iv;
		public TextView tvContent;
		public TextView tvTime;
		public TextView tvCount;
		public View layoutBody;
		public MyViewHolder(View view)
		{
			super(view);
			tvCount  = (TextView) view.findViewById(R.id.tv_count);
			tvContent = (TextView) view.findViewById(R.id.tv_content);
			tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			tvTime = (TextView) view.findViewById(R.id.tv_time);
			layoutBody = view.findViewById(R.id.layout_body);
			iv = (ImageView) view.findViewById(R.id.iv);
		}
	}
	@Override
	public int getItemCount(){
		return (list==null) ? 0:list.size();
	}
	public void addItem(Conversation obj){
		if(this.list==null){
			return ;
		}
		this.list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<Conversation> list){
		LogUtils.e("list.size = "+list.size());
		if(this.list==null){
			return ;
		}
		this.list.addAll(list);
		LogUtils.e("list.size = "+this.list.size());
		notifyDataSetChanged();
	}
	public void refreshItem(List<Conversation> list){
		if((this.list==null) || (list == null) || (list.size()<=0)){
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
	public List<Conversation> getList(){
		return list;
	}
}
