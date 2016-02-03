package github.leerduo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.alex.alexchat.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import github.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @Link https://github.com/leerduo/SortListView
 * @SDK 2.1
 * */
@SuppressLint("DefaultLocale")
public class SortAdapter extends BaseAdapter implements SectionIndexer {
	
	private List<SortModel> list = null;
	
	private Context context;
	
	public SortAdapter(Context context){
		this.context = context;
		this.list = new ArrayList<SortModel>();
	}
	public void addItem(List<SortModel> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}
	public List<SortModel> getList(){
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
		final SortModel mContent = list.get(position);
		if (convertView== null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.leerduo_item_list, null);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			holder.ivUser = (ImageView) convertView.findViewById(R.id.iv_user);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(mContent.letter);
		}else {
			holder.tvLetter.setVisibility(View.GONE);
		}
		SortModel bean = list.get(position);
		Glide.with(context).load(bean.portraitUri).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(context)).placeholder(R.drawable.logo_empty).error(R.drawable.logo_empty).into(holder.ivUser);

		holder.tvTitle.setText(bean.name);
		return convertView;
	}

	@Override
	public Object[] getSections() {
		return null;
	}
	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
	public int getPositionForSection(int sectionIndex) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).letter;
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == sectionIndex) {
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).letter.charAt(0);
	}
	
	final static class ViewHolder{
		TextView tvLetter;
		TextView tvTitle;
		ImageView ivUser;
	}
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

}
