package github.common.rongim;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.lidroid.xutils.util.LogUtils;
public class RongUser
{
	private static String fileName = "ryConfig";
	public static final String userName = "userName";
	public static final String userToken = "userToken";
	public static final String userId = "userId";
	public static final String portraitUri = "portraitUri";

	public static String getValue(Context context, String key){
		return getString(context, key);
	}
	public static void setValue(Context context, String key, String value){
		putString(context, key, value);
	}
	public static void logout(Context context){
		List<String> listFields = getFieldsList(RongUser.class);
		for (int i = 0; (listFields!=null) && (i<listFields.size()); i++)
		{
			String key = listFields.get(i);
			removeString(context,key);
		}
	}
	/**获取 JavaBean的成员变量的 个数
	 * @param bean 例如 MessageType.class
	 * @return  成员变量的 个数*/
	private static List<String> getFieldsList(Class<?> bean)
	{
		List<String> list = new ArrayList<String>();
		Field[] fields = bean.getFields();
		if(fields!=null){
			for (int i = 0; i < fields.length; i++){
				list.add(fields[i].getName());
			}
		}
		return list;
	}
	/**取出String, 失败, 返回 defaultValue
	 * @param key 键, !null, !""*/
	private  static String getString(Context context, String key) {
		SharedPreferences settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return settings.getString(key, null);
	}
	private static boolean removeString(Context context, String key){
		SharedPreferences settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		return  editor.commit();
	}
	private  static boolean putString(Context context, String key, String value)
	{
		if(TextUtils.isEmpty(key) || TextUtils.isEmpty(value)){
			return false;
		}
		SharedPreferences settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		return editor.commit();
	}
}
