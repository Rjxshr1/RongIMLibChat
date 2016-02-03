package github.common.utils;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
public class ActivityUtil
{
	private static List<Activity> listActivity;
	public static void pushActivity(Activity activity){
		listActivity = (listActivity == null) ? new ArrayList<Activity>() : listActivity;
		listActivity.add(activity);
	}
	public static void killAllActivity()
	{
		for (int i = 0; listActivity!=null && i < listActivity.size(); i++){
			listActivity.get(i).finish();
		}
		if(listActivity!=null){
			listActivity.clear();
		}
		listActivity = null;
	}
	/**清空 Activity 集合，是否杀死Activity
	 * @param killAll true 杀死
	 * */
	public static void clearAllActivity(boolean killAll)
	{
		if(killAll){
			for (int i = 0; listActivity!=null && i < listActivity.size(); i++){
				listActivity.get(i).finish();
			}
		}
		if(listActivity!=null){
			listActivity.clear();
		}
		listActivity = null;
	}
	public static boolean isActivityOnForeground(Context context)
	{
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getPackageName();
		List<RecentTaskInfo> appTask = activityManager.getRecentTasks(Integer.MAX_VALUE, 1);
		if (appTask == null){
			return false;
		}
		if (appTask.get(0).baseIntent.toString().contains(packageName)){
			return true;
		}
		return false;
	}
}
