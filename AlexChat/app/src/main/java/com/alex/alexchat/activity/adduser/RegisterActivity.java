package com.alex.alexchat.activity.adduser;
import github.common.listener.CommonClickListener;
import github.common.listener.clickcore.CommonClickType;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.alex.alexchat.R;
import com.alex.alexchat.activity.BaseActivity;
/**注册页面
 * 启动者：SettingFragment
 * */
@SuppressWarnings("unused")
public class RegisterActivity extends BaseActivity
{
	private EditText etUserName;
	private EditText etPwd;
	private EditText etPwd2;
	/**用户名的EditText*/
	private String userName;
	/**注册密码EditText*/
	private String pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		etUserName = (EditText) findViewById(R.id.et_userName);
		etPwd = (EditText) findViewById(R.id.et_pwd);
		etPwd2 = (EditText) findViewById(R.id.et_pwd2);
		findViewById(R.id.tv_register).setOnClickListener(new MyOnClickListener());
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.tv_register == v.getId())
			{
				userName = etUserName.getText().toString();
				pwd = etPwd.getText().toString();
			}
		}
	}
}
