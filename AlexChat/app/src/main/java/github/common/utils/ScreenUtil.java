package github.common.utils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
/**Screen工具类
 * <br/>1.1... 数据转换: px---->dp {@link #pxToDp(Context, float)}
 * <br/>1.2... 数据转换: dp---->px {@link #dpToPx(Context context, float dp)}
 * <br/>1.3... 数据转换: dp---->int {@link #dpToPxInt(Context context, float dp)}
 * <br/>1.4... 数据转换: px---->int {@link #pxToDpCeilInt(Context context, float px)}
 * <br/>2.1... 获得屏幕宽度 {@link #getScreenWidth(Context context)}
 * <br/>2.2... 获得屏幕高度 {@link #getScreenHeight(Context context)}
 * <br/>2.3... 获得状态栏的高度 {@link #getStatusBarHeight(Context context)}
 * <br/>2.4... 获得屏幕截图 {@link #getScreenshotWithStatusBar(Activity activity)
 * <br/>2.4... 获得屏幕截图 {@link #getScreenshotWithoutStatusBar(Activity activity)}
 * */
public class ScreenUtil
{
	private static final String TAG = "ScreenUtil";
	/**获得屏幕宽度*/
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
	/**获得屏幕高度*/
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
	/**获得状态栏的高度*/
	public static int getStatusBarHeight(Context context)
	{
		int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e){
			Log.e(TAG,e.getMessage());
		}
		return statusHeight;
	}
	/**获取当前屏幕截图, 包含状态栏
	 * <br/>得到Bitmap数据*/
	public static Bitmap getScreenshotWithStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;
	}
	/**获取当前屏幕截图, 不包含状态栏
	 * <br/>得到Bitmap数据
	 */
	public static Bitmap getScreenshotWithoutStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}
	/**数据转换: dp---->px*/
	public static float dpToPx(Context context, float dp) 
    {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }
	/**数据转换: px---->dp*/
    public static float pxToDp(Context context, float px) {
        if (context == null) {
            return -1;
        }
        return px / context.getResources().getDisplayMetrics().density;
    }
    /**数据转换: dp---->int*/
    public static float dpToPxInt(Context context, float dp) {
        return (int)(dpToPx(context, dp) + 0.5f);
    }
    /**数据转换: px---->int[向上取整]*/
    public static float pxToDpCeilInt(Context context, float px) {
        return (int)(pxToDp(context, px) + 0.5f);
    }
    
}
