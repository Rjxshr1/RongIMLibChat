package com.alex.alexchat.adapter.baseadapter;
import github.common.dialog.BaseDialog.AnimType;
import github.common.helper.MediaHelper;
import github.common.helper.MediaHelper.MediaStatus;
import github.common.helper.MediaHelper.OnPlayerListener;
import github.common.utils.ScreenUtil;
import github.common.utils.ViewUtil;
import github.pnikosis.ProgressWheel;
import github.wasabeef.glide.transformations.CropCircleTransformation;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.PhotoActivity;
import com.alex.alexchat.activity.chatroom.SingleChatActivity;
import com.alex.alexchat.adapter.baseadapter.SingleChatAdapter.MyViewHolder;
import com.alex.alexchat.bean.MessageBean;
import com.alex.alexchat.bean.MessageType;
import com.alex.alexchat.dialog.CommonDialog;
import com.alex.alexchat.dialog.CommonDialog.OnDialogClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lidroid.xutils.util.LogUtils;
import com.socks.library.KLog;

/**单聊界面 适配器
 * 宿主：SingleChatActivity
 * */
public class SingleChatAdapter extends RecyclerView.Adapter<MyViewHolder>
{
	public Context context;
	public  List<MessageBean> list;
	private CommonDialog commonDialog;
	private int position;
	private boolean canDisplaySenderUserName;
	private AnimationDrawable playerAnim;
	public SingleChatAdapter(Context context) {
		list = new ArrayList<MessageBean>();
		this.context = context;
		commonDialog = new CommonDialog(context);
		commonDialog.setOnDialogClickListener(new MyOnDialogClickListener());
	}
	/**更新适配器*/
	public void updateItemByMillisTime(MessageBean obj)
	{
		for (int i = (list.size()-1); i >=0 ; i--) 
		{
			MessageBean bean = list.get(i);
			if(bean.millisTime.equalsIgnoreCase(obj.millisTime)){
				list.set(i, obj);
				break;
			}
		}
		notifyDataSetChanged();
	}
	public void dismissDialog(boolean mustRemoreItem){
		if(mustRemoreItem){
			removeItem(position);
		}
		commonDialog.dismiss();
	}
	/**设置 可以显示 用户名称*/
	public void setCanDisplaySenderUserName(boolean canDisplaySenderUserName){
		this.canDisplaySenderUserName = canDisplaySenderUserName;
	}
	@Override
	public int getItemViewType(int position) {
		if(list == null || list.size()<=0){
			return 0;
		}
		return list.get(position).messageType;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position)
	{
		int messageType = list.get(position).messageType;
		MessageBean bean = list.get(position);
		/*解决 ProgressView 错位问题*/
		holder.pw.setTag(bean.millisTime);
		String  timeMillisTag = (String) holder.pw.getTag();
		if(bean.millisTime.equalsIgnoreCase(timeMillisTag))
		{
			if(bean.isFinished){
				holder.pw.setVisibility(View.INVISIBLE);
			}else {
				holder.pw.setVisibility(View.VISIBLE);
			}
		}
		String userUri = "";
		if(MessageType.textLeft == messageType){
			ViewUtil.setText2TextView(holder.tvContent, bean.text);
		}else if(MessageType.textRight == messageType){
			ViewUtil.setText2TextView(holder.tvContent, bean.text);
		}else if(MessageType.imageLeft == messageType){
			displayImageView(position, holder);
			holder.ivContent.setOnClickListener(new MyOnClickListener(position));
			holder.ivContent.setOnLongClickListener(new MyOnLongClickListener(position));
		}else if(MessageType.imageRight == messageType){
			displayImageView(position, holder);
			holder.ivContent.setOnClickListener(new MyOnClickListener(position));
			holder.ivContent.setOnLongClickListener(new MyOnLongClickListener(position));
		}else if(MessageType.locationLeft == messageType){
			ViewUtil.setText2TextView(holder.tvContent, bean.address);
			Glide.with(context).load(bean.imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivContent);
			holder.layoutBody.setOnClickListener(new MyOnClickListener(position));
		}else if(MessageType.locationRight == messageType){
			ViewUtil.setText2TextView(holder.tvContent, bean.address);
			Glide.with(context).load(bean.imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivContent);
			holder.layoutBody.setOnClickListener(new MyOnClickListener(position));
		}else if(MessageType.voiceLeft == messageType){
			ViewUtil.setText2TextView(holder.tvContent, bean.duration+"秒");
			holder.layoutBody.setOnClickListener(new MyOnClickListener(position, holder.ivPlayer));
		}else if(MessageType.voiceRight == messageType){
			ViewUtil.setText2TextView(holder.tvContent, bean.duration+"秒");
			holder.layoutBody.setOnClickListener(new MyOnClickListener(position, holder.ivPlayer));
		}
		if(canDisplaySenderUserName){
			ViewUtil.setText2TextView(holder.tvUser, bean.senderUserName);
		}else{
			holder.tvUser.setVisibility(View.INVISIBLE);
		}

		Glide.with(context).load(bean.senderPortraitUri).bitmapTransform(new CropCircleTransformation(context)).placeholder(R.drawable.logo_user).error(R.drawable.logo_user).into(holder.ivUser);
		holder.layoutBody.setOnLongClickListener(new MyOnLongClickListener(position));
	}
	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = null;
		if(MessageType.textLeft == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_text_left, parent, false);
		}else if(MessageType.textRight == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_text_right, parent, false);
		}else if(MessageType.imageLeft == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_image_left, parent, false);
		}else if(MessageType.imageRight == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_image_right, parent, false);
		}else if(MessageType.locationLeft == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_location_left, parent, false);
		}else if(MessageType.locationRight == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_location_right, parent, false);
		}else if(MessageType.voiceLeft == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_voice_left, parent, false);
		}else if(MessageType.voiceRight == viewType){
			view = LayoutInflater.from(context).inflate(R.layout.item_single_chat_voice_right, parent, false);
		}
		return new MyViewHolder(view,viewType);
	}
	/**展示 ImageView 并处理 图片错位 问题
	 * */
	private void displayImageView(int position, MyViewHolder holder) {
		MessageBean bean = list.get(position);
		LayoutParams params = null;
		int height = 0;
		int width = 0;
		if("fasle".equalsIgnoreCase(bean.extra)){
			//LogUtils.e("收到的图片  较宽"+position+" bean.extra = "+bean.extra);
			height = (int)ScreenUtil.dpToPx(context, 60);
			width = (int)ScreenUtil.dpToPx(context, 120);
		}else{
			//LogUtils.e("收到的图片  较高 "+position+" bean.extra = "+bean.extra);
			height = (int)ScreenUtil.dpToPx(context, 160);
			width = (int)ScreenUtil.dpToPx(context, 120);
		}
		params = new RelativeLayout.LayoutParams(width,height);
		holder.ivContent.setScaleType(ScaleType.CENTER_CROP);
		holder.ivContent.setLayoutParams(params);
		Glide.with(context).load(list.get(position).imageUri).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivContent);
	}
	private final class MyOnLongClickListener implements OnLongClickListener
	{
		private int position;
		public MyOnLongClickListener(int position) {
			this.position = position;
		}
		@Override
		public boolean onLongClick(View v)
		{
			LogUtils.e("position = "+position);
			commonDialog.show(AnimType.centerScale);
			SingleChatAdapter.this.position = position;
			return true;
		}
	}
	private final class MyOnDialogClickListener implements OnDialogClickListener
	{
		@Override
		public void onDialogClick(int gravity)
		{
			if(Gravity.LEFT == gravity){
				((SingleChatActivity)context).deleteMessages(list.get(position).messageId);
			}else if(Gravity.RIGHT== gravity){
				commonDialog.dismiss();
			}		
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private int position;
		private ImageView ivPlayer;
		public MyOnClickListener(int position) {
			this.position = position;
		}
		public MyOnClickListener(int position, ImageView player) {
			this.position = position;
			this.ivPlayer = player;
		}
		@Override
		public void onClick(View v) 
		{
			MessageBean bean = list.get(position);
			if(R.id.iv == v.getId()){
				Intent intent = new Intent(context, PhotoActivity.class);
				intent.putExtra("imageUrl", bean.imageUri.toString());
				context.startActivity(intent);
			}else if((MessageType.voiceRight == bean.messageType) ||(MessageType.voiceLeft == bean.messageType)){
				new MediaHelper(context, bean.voiceUri,new MyOnPlayerListener()).startPlay();
				if(playerAnim!=null){
					playerAnim.stop();
					playerAnim = null;
				}
				playerAnim = (AnimationDrawable) (ivPlayer).getDrawable();
				playerAnim.start();
			}
		}
	}
	private final class MyOnPlayerListener implements OnPlayerListener
	{
		@Override
		public void onPlayer(MediaStatus mediaStatus, int code, String message)
		{
			LogUtils.e("mediaStatus = "+mediaStatus+" code = "+code+" message = "+message);
			if((MediaStatus.playComplete == mediaStatus) && (playerAnim!=null)){
				playerAnim.stop();
			}
		}
	}
	public  class MyViewHolder extends RecyclerView.ViewHolder
	{
		/**文本内容  文本描述*/
		public TextView tvContent;
		/**文本内容  文本描述*/
		public TextView tvUser;
		public ImageView ivUser;
		/**图片内容*/
		public ImageView ivContent;
		/**播放效果  小喇叭*/
		public ImageView ivPlayer;
		public ProgressWheel pw;
		public View layoutBody;
		public MyViewHolder(View view,int viewType)
		{
			super(view);
			if(MessageType.textLeft == viewType){
				tvContent = (TextView) view.findViewById(R.id.tv_content);
			}else if(MessageType.textRight == viewType){
				tvContent = (TextView) view.findViewById(R.id.tv_content);
			}else if(MessageType.imageLeft == viewType){
				ivContent = (ImageView) view.findViewById(R.id.iv_content);
			}else if(MessageType.imageRight == viewType){
				ivContent = (ImageView) view.findViewById(R.id.iv_content);
			}else if(MessageType.locationLeft == viewType){
				tvContent = (TextView) view.findViewById(R.id.tv_content);
				ivContent = (ImageView) view.findViewById(R.id.iv_content);
			}else if(MessageType.locationRight == viewType){
				tvContent = (TextView) view.findViewById(R.id.tv_content);
				ivContent = (ImageView) view.findViewById(R.id.iv_content);
			}else if(MessageType.voiceLeft == viewType){
				tvContent = (TextView) view.findViewById(R.id.tv_content);
				ivPlayer = (ImageView) view.findViewById(R.id.iv_player);
			}else if(MessageType.voiceRight == viewType) {
				tvContent = (TextView) view.findViewById(R.id.tv_content);
				ivPlayer = (ImageView) view.findViewById(R.id.iv_player);
			}
			ivUser = (ImageView) view.findViewById(R.id.iv_user);
			tvUser = (TextView) view.findViewById(R.id.tv_user_name);
			layoutBody = view.findViewById(R.id.layout_body);
			pw = (ProgressWheel) view.findViewById(R.id.pw);
		}
	}
	@Override
	public int getItemCount(){
		return (list==null) ? 0:list.size();
	}
	public void addItem(MessageBean obj){
		if(this.list==null){
			return ;
		}
		this.list.add(obj);
		notifyDataSetChanged();
	}
	public void addItem(List<MessageBean> list){
		LogUtils.e("list.size = "+list.size());
		if(this.list==null){
			return ;
		}
		this.list.addAll(list);
		LogUtils.e("list.size = "+this.list.size());
		notifyDataSetChanged();
	}
	public void refreshItem(List<MessageBean> list){
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
	public List<MessageBean> getList(){
		return list;
	}
}
