package com.alex.alexchat.config;
import github.common.utils.ClassUtil;
import github.common.utils.SPUtil;

import java.util.List;

import android.content.Context;
public class User
{
	public static final String isOnline = "isOnline";
	public static final String userName = "userName";
	public static final String phoneNum = "phoneNum";
	public static final String userId = "userId";
	public static final String nickName = "nickName";
	public static final String pwd = "pwd";
	public static final String userToken = "userToken";
	public static final String sign = "sign";
	public static final String age = "age";
	public static final String gender = "gender";
	public static int pwdLengthMin = 6;
	public static int pwdLengthMax = 12;
	/**清空所有 共享存储数据*/
	public static void logout(Context context){
		List<String> list = ClassUtil.getFieldsList(User.class);
		for (int i = 0; (list!=null)&&(i < list.size()); i++){
			SPUtil.remove(context, list.get(i));
		}
	}
	/**当前用户在线*/
	public static boolean getIsOnline(Context context){
		return SPUtil.getBoolean(context, User.isOnline,false);
	}
	/**当前用户在线*/
	public static boolean setIsOnline(Context context, boolean isOnline){
		return SPUtil.putBoolean(context, User.isOnline,isOnline);
	}
	public static String getValue(Context context, String key){
		return SPUtil.getString(context, key);
	}
	public static void setValue(Context context, String key, String value){
		SPUtil.putString(context, key, value);
	}
}
