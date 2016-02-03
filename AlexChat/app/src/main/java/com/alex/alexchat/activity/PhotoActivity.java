package com.alex.alexchat.activity;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.alex.alexchat.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
public class PhotoActivity extends Activity 
{
	private Context context;
	private String imageUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		imageUrl = getIntent().getStringExtra("imageUrl");
		context = this;
		initView();
	}
	private void initView() {
		Glide.with(context).load(imageUrl).placeholder(R.drawable.logo_empty).priority(Priority.HIGH).error(R.drawable.logo_empty).into((ImageView) findViewById(R.id.pv));
	}
}
