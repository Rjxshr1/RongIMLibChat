package com.alex.alexchat.bean;

import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

/**位置信息 实体类*/
@Table
public class UserBean 
{
	/**在数据库 中的 id(禁止自增)   这个一定要被 赋值*/
	@NoAutoIncrement
	public String id;
	public String name;
	public String portraitUri;
	public String token;
}
