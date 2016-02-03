package github.common.utils;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import android.text.TextUtils;
public class JsonSmartUtil
{
	public static JSONObject getJsonObject(String jsonStr){
		if(TextUtils.isEmpty(jsonStr)|| (!JSONValue.isValidJson(jsonStr))){
			return null;
		}
		Object object = JSONValue.parse(jsonStr);
		if(object instanceof JSONObject){
			return (JSONObject) object;
		}
		return null;
	}
	public static JSONObject getJsonObject(JSONObject jsonObject,String key){
		if(isJsonObjectEmpty(jsonObject)|| TextUtils.isEmpty(key) ){
			return null;
		}
		Object object = jsonObject.get(key);
		if(object instanceof JSONObject){
			return (JSONObject) object;
		}
		return null;
	}
	public static JSONObject getJsonObject(String jsonStr,String key){
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key) || (!JSONValue.isValidJson(jsonStr))){
			return null;
		}
		Object objectRoot =  JSONValue.parse(jsonStr);
		if(!(objectRoot instanceof JSONObject)){
			return null;
		}
		JSONObject jsonObject = (JSONObject) objectRoot;
		Object objectSub = jsonObject.get(key);
		if(objectSub instanceof JSONObject){
			return (JSONObject) objectSub;
		}
		return null;
	}
	public static JSONObject getJsonObject(JSONArray jsonArray, int index){
		if(isJsonArrayEmpty(jsonArray) || (index>=jsonArray.size())){
			return null;
		}
		Object object = jsonArray.get(index);
		if(object instanceof JSONObject){
			return (JSONObject) object;
		}
		return null;
	}
	public static JSONArray getJsonArray(String jsonStr){
		if(TextUtils.isEmpty(jsonStr)|| (!JSONValue.isValidJson(jsonStr))){
			return null;
		}
		Object object = JSONValue.parse(jsonStr);
		if(object instanceof JSONArray){
			return (JSONArray) object;
		}
		return null;
	}
	public static JSONArray getJsonArray(String jsonStr,String key){
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)|| (!JSONValue.isValidJson(jsonStr))){
			return null;
		}
		Object objectRoot = JSONValue.parse(jsonStr);
		if((objectRoot==null) || (!(objectRoot instanceof JSONObject))){
			return null;
		}
		JSONObject jsonObject = (JSONObject) objectRoot;
		Object object = jsonObject.get(key);
		if(object instanceof JSONArray){
			return (JSONArray) object;
		}
		return null;
	}
	public static JSONArray getJsonArray(JSONObject jsonObject,String key){
		if(isJsonObjectEmpty(jsonObject) || TextUtils.isEmpty(key)){
			return null;
		}
		Object object = jsonObject.get(key);
		if(object instanceof JSONArray){
			return (JSONArray) object;
		}
		return null;
	}
	public static String getString(String jsonStr, String key){
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)|| (!JSONValue.isValidJson(jsonStr))){
			return null;
		}
		Object objectRoot = JSONValue.parse(jsonStr);
		if(!(objectRoot instanceof JSONObject)){
			return null;
		}
		JSONObject jsonObject = (JSONObject) objectRoot;
		Object obj = jsonObject.get(key);
		if(obj!=null){
			return obj.toString();
		}
		return null;
	}
	public static String getString(JSONObject jsonObject, String key){
		if(isJsonObjectEmpty(jsonObject)|| TextUtils.isEmpty(key)){
			return null;
		}
		Object obj = jsonObject.get(key);
		if(obj!=null){
			return obj.toString();
		}
		return null;
	}
	public static Object get(JSONObject jsonObject, String key){
		if(isJsonObjectEmpty(jsonObject)|| TextUtils.isEmpty(key)){
			return null;
		}
		Object obj = jsonObject.get(key);
		return obj;
	}
	public static String getString(JSONArray jsonArray, int index){
		if(isJsonArrayEmpty(jsonArray) || (jsonArray.size()<=index)){
			return null;
		}
		Object obj = jsonArray.get(index);
		if(obj!=null){
			return obj.toString();
		}
		return null;
	}
	public static boolean getBoolean(String jsonStr, String key){
		if(TextUtils.isEmpty(jsonStr) || TextUtils.isEmpty(key)|| (!JSONValue.isValidJson(jsonStr))){
			return false;
		}
		Object objectRoot = JSONValue.parse(jsonStr);
		if(!(objectRoot instanceof JSONObject)){
			return false;
		}
		JSONObject jsonObject = (JSONObject) objectRoot;
		Object object = jsonObject.get(key);
		if((object!=null) && (object instanceof Boolean)){
			return (Boolean) object;
		}
		return false;
	}
	public static int getInt(JSONObject jsonObject, String key){
		if(isJsonObjectEmpty(jsonObject)|| TextUtils.isEmpty(key)){
			return -1;
		}
		Object obj = jsonObject.get(key);
		if((obj!=null) && (obj instanceof Integer)){
			return (Integer)obj;
		}
		return -1;
	}
	public static boolean isJsonObjectEmpty(JSONObject jsonObject){
		if((jsonObject==null) || jsonObject.isEmpty()){
			return true;
		}
		return false;
	}
	public static boolean isJsonArrayEmpty(JSONArray jsonArray){
		if(jsonArray == null){
			return true;
		}
		if(jsonArray.isEmpty()){
			return true;
		}
		return false;
	}
}
