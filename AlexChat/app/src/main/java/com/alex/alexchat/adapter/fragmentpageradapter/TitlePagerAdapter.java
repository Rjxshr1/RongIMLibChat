package com.alex.alexchat.adapter.fragmentpageradapter;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
public class TitlePagerAdapter extends FragmentPagerAdapter
{
	private List<Fragment> list;
	private List<String> listTitle;
	protected FragmentManager fm;
	protected FragmentTransaction fragmentTransaction;
	public TitlePagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
		list = new ArrayList<Fragment>();
	}
	public TitlePagerAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.fm = fm;
		this.list = list;
	}
	public TitlePagerAdapter(FragmentManager fm, List<Fragment> list, List<String> listTitle) {
		super(fm);
		this.fm = fm;
		this.list = list;
		this.listTitle = listTitle;
	}
	public void addItem(Fragment fragment)
	{
		list.add(fragment);
		notifyDataSetChanged();
	}
	public void addItem(List<Fragment> list)
	{
		this.list = list;
		notifyDataSetChanged();
	}
	@Override
	public Fragment getItem(int position){
		Fragment fragment = ((list == null) ? null:list.get(position));
		Bundle bundle = new Bundle();
		bundle.putInt("index", position);
		fragment.setArguments(bundle);
		return fragment;
	}
	@Override
	public int getCount(){
		return ((list == null) ? 0:list.size());
	}
	@Override
	public CharSequence getPageTitle(int position) {
		return (listTitle==null)? "":listTitle.get(position);
	}
}
