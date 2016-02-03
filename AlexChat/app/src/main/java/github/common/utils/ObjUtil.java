package github.common.utils;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class ObjUtil
{
	/**List为空
	 * @param list 为null | 无元素，返回true*/
	public static boolean isListEmpty(List<?> list){
		if(list == null || list.isEmpty()){
			return true;
		}
		return false;
	}
	/**bitmap为空
	 * @param bitmap 为null | 无元素，返回true*/
	public static boolean isBitmapEmpty(Bitmap bitmap){
		if(bitmap == null || (bitmap.getHeight()<=0) || (bitmap.getWidth()<=0)){
			return true;
		}
		return false;
	}
	/**Drawable为空
	 * @param drawable 为null | 无元素，返回true*/
	public static boolean isDrawableEmpty(Drawable drawable){
		if(drawable == null || (drawable.getIntrinsicHeight()<=0) || (drawable.getIntrinsicWidth()<=0)){
			return true;
		}
		return false;
	}
}
