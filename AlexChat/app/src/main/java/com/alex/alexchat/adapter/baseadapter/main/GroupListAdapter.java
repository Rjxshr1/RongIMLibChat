package com.alex.alexchat.adapter.baseadapter.main;
import github.common.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.alex.alexchat.adapter.baseadapter.main.GroupListAdapter.ViewHolder;
import com.alex.alexchat.bean.GroupBean;
public class GroupListAdapter extends RecyclerView.Adapter<ViewHolder>
{
	public Context context;
	public  List<GroupBean> list;
	public GroupListAdapter(Context context) {
		this.context = context;
		this.list = new ArrayList<GroupBean>();
	}
	@Override
	public void onBindViewHolder(ViewHolder holder, int position)
	{
		GroupBean groupBean = list.get(position);
		ViewUtil.setText2TextView(holder.tv, groupBean.groupName);
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int position)
	{
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_main_group, parent, false);
		return new ViewHolder(view);
	}
	public final class ViewHolder extends RecyclerView.ViewHolder
	{
		public TextView tv;
		public ViewHolder(View view)
		{
			super(view);
			tv = (TextView) view.findViewById(R.id.tv);
		}
	}
	@Override
	public int getItemCount(){
		return (list==null) ? 0:list.size();
	}

}
