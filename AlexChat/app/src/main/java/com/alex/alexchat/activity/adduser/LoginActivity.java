package com.alex.alexchat.activity.adduser;
import github.common.listener.CommonClickListener;
import github.common.listener.clickcore.CommonClickType;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.BaseActivity;
/**登录页面
 * 启动者：SettingFragment
 * */
public class LoginActivity extends BaseActivity
{
	private EditText etUserName;
	private EditText etPwd;
	/**用户名的EditText*/
	@SuppressWarnings("unused")
	private String userName;
	/**注册密码EditText*/
	@SuppressWarnings("unused")
	private String pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
	}
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		etUserName = (EditText) findViewById(R.id.et_userName);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		findViewById(R.id.tv_login).setOnClickListener(new MyOnClickListener());
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.tv_login == v.getId())
			{
				userName = etUserName.getText().toString();
				pwd = etPwd.getText().toString();
			}
		}
	}
}
