package com.alex.alexchat.adapter.baseadapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.alex.alexchat.bean.CityBean;
import com.alex.alexchat.bean.UserBean;

/**
 * @Link https://github.com/leerduo/SortListView
 * @SDK 2.1
 * */
@SuppressLint("DefaultLocale")
public class HotCityAdapter extends BaseAdapter {

	private List<UserBean> list = null;

	private Context context;

	public HotCityAdapter(Context context){
		this.context = context;
		this.list = new ArrayList<UserBean>();
	}
	public void addItem(List<UserBean> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void updateItem(List<UserBean> list){
		this.list = list;
		notifyDataSetChanged();
	}
	public void refreshItem(List<UserBean> list){
		this.list.clear();
		this.list = list;
		notifyDataSetChanged();
	}
	public List<UserBean> getList(){
		return list;
	}
	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView== null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.headview_city_item, parent,false);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserBean bean = list.get(position);
		holder.tvTitle.setText(bean.name);
		return convertView;
	}
	private class ViewHolder{
		public TextView tvTitle;
	}
}
