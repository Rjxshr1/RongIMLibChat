package github.common.utils;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
public class ToastUtil
{
	public static void shortNormal(Context context, CharSequence text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	public static void shortAtCenter(Context context, CharSequence text){
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, -100);
		toast.show();
	}
	public static void shortAtTop(Context context, CharSequence text){
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 100);
		toast.show();
	}
	public static void shortAtTopInThread(final Context context, final CharSequence text){
		if (context == null){
			return;
		}
		((Activity) context).runOnUiThread(new Runnable() {
			public void run()
			{
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP, 0, 100);
				toast.show();
			}
		});
	}
	public static void longNormal(Context context, CharSequence text){
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	public static void longAtCenter(Context context, CharSequence text){
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, -100);
		toast.show();
	}
	public static void longAtTop(Context context, CharSequence text){
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 100);
		toast.show();
	}
	public static void longAtTopInThread(final Context context, final CharSequence text){
		if (context == null){
			return;
		}
		((Activity) context).runOnUiThread(new Runnable() {
			public void run()
			{
				Toast toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, -100);
				toast.show();
			}
		});
	}
	public static void shortInThread(final Context context, final String text) 
	{
		if (context == null){
			return;
		}
		((Activity) context).runOnUiThread(new Runnable() {
			public void run()
			{
				Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
		});
	}
	public static void shortAtCenterInThread(final Context context, final String text) 
	{
		if (context == null){
			return;
		}
		((Activity) context).runOnUiThread(new Runnable() {
			public void run()
			{
				Toast toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, -100);
				toast.show();
			}
		});
	}
	public static void longInThread(final Context context, final String text) 
	{
		if (context == null){
			return;
		}
		((Activity) context).runOnUiThread(new Runnable() {
			public void run()
			{
				Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG).show();
			}
		});
	}
	public static void longAtCenterInThread(final Context context, final String text) 
	{
		if (context == null){
			return;
		}
		((Activity) context).runOnUiThread(new Runnable() {
			public void run()
			{
				Toast toast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, -100);
				toast.show();
			}
		});
	}
}
