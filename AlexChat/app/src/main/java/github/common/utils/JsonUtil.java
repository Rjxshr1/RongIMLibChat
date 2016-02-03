package github.common.utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.lidroid.xutils.util.LogUtils;
public class JsonUtil 
{
	/**通过 String，获取Json对象*/
	public static JSONObject getJsonObject(String jsonStr)
	{
		if(TextUtils.isEmpty(jsonStr)){
			return null;
		}
		try
		{
			return new JSONObject(jsonStr);
		} catch (JSONException e){		}
		return null;
	}
	/**通过 Json对象，获取Json对象*/
	public static JSONObject getJsonObject(JSONObject jsonObject,String key)
	{
		if(jsonObject == null || TextUtils.isEmpty(key)){
			return null;
		}
		try
		{
			return jsonObject.getJSONObject(key);
		} catch (JSONException e){		}
		return null;
	}
	/**通过 String，获取Json对象*/
	public static JSONObject getJsonObject(String jsonStr,String key)
	{
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)){
			LogUtils.e("jsonStr 为空");
			return null;
		}
		try
		{
			JSONObject jsonObject = new JSONObject(jsonStr);
			return jsonObject.getJSONObject(key);
		} catch (JSONException e){		}
		return null;
	}
	public static JSONObject getJsonObject(JSONArray jsonArray,int index)
	{
		if(jsonArray==null || jsonArray.length()<=0|| jsonArray.length()<=index){
			return null;
		}
		try
		{
			return jsonArray.getJSONObject(index);
		} catch (JSONException e){}
		return null;
	}
	public static JSONArray getJsonArray(String jsonStr)
	{
		if(TextUtils.isEmpty(jsonStr)){
			return null;
		}
		try{
			return new JSONArray(jsonStr);
		} catch (JSONException e){		}
		return null;
	}
	public static JSONArray getJsonArray(JSONArray jsonArray,int index)
	{
		if(jsonArray==null || jsonArray.length()<=0|| jsonArray.length()<=index){
			return null;
		}
		try{
			return jsonArray.getJSONArray(index);
		} catch (JSONException e){		}
		return null;
	}
	public static JSONArray getJsonArray(JSONObject jsonObject,String key)
	{
		if(jsonObject==null || TextUtils.isEmpty(key)){
			return null;
		}
		try{
			return jsonObject.getJSONArray(key);
		} catch (JSONException e){		}
		return null;
	}
	public static JSONArray getJsonArray(String jsonStr,String key)
	{
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)){
			return null;
		}
		try{
			JSONObject jsonObject = new JSONObject(jsonStr);
			return jsonObject.getJSONArray(key);
		} catch (JSONException e){		}
		return null;
	}
	public static String getString(JSONArray jsonArray,int index)
	{
		if(jsonArray==null || jsonArray.length()<=0|| jsonArray.length()<=index){
			return null;
		}
		try
		{
			return jsonArray.getString(index);
		} catch (JSONException e){}
		return null;
	}
	public static String getString(JSONObject jsonObject, String key)  
	{
		if(jsonObject==null || TextUtils.isEmpty(key)){
			return null;
		}
		try
		{
			return jsonObject.getString(key);
		} catch (Exception e){  }
		return null;
	}
	public static String getString(String jsonStr, String key)  
	{
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)){
			return null;
		}
		try
		{
			JSONObject jsonObject = new JSONObject(jsonStr);
			return jsonObject.getString(key);
		} catch (Exception e){  }
		return null;
	}
	public static int getInt(JSONObject jsonObject, String key)  
	{
		if(jsonObject == null || TextUtils.isEmpty(key)){
			return -1;
		}
		try
		{
			return jsonObject.getInt(key);
		} catch (Exception e){  }
		return -1;
	}
	public static int getInt(String jsonStr, String key)  
	{
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)){
			return -1;
		}
		try
		{
			JSONObject jsonObject = new JSONObject(jsonStr);
			return jsonObject.getInt(key);
		} catch (Exception e){  }
		return 0;
	}
	public static float getFloat(JSONObject jsonObject, String key)  
	{
		if(jsonObject==null || TextUtils.isEmpty(key)){
			return -1F;
		}
		try
		{
			return (float) jsonObject.getDouble(key);
		} catch (Exception e){  }
		return 0F;
	}
	public static boolean getBoolean(String jsonStr, String key)  
	{
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)){
			return false;
		}
		try
		{
			JSONObject jsonObject = new JSONObject(jsonStr);
			return jsonObject.getBoolean(key);
		} catch (Exception e){  }
		return false;
	}
	public static boolean getBoolean(JSONObject jsonObject, String key)  
	{
		if(isObjEmpty(jsonObject) || TextUtils.isEmpty(key)){
			return false;
		}
		try
		{
			return jsonObject.getBoolean(key);
		} catch (Exception e){  }
		return false;
	}
	/**判断 json数组为空*/
	public static boolean isArrayEmpty(JSONArray jsonArray){
		if(jsonArray == null || jsonArray.length()<=0){
			return true;
		}else{
			return false;
		}
	}
	/**判断 json对象为空*/
	public static boolean isObjEmpty(JSONObject jsonObject){
		return jsonObject == null;
	}
}
