package github.common.listener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import github.common.listener.clickcore.CommonClickType;

public class CommonClickListener implements OnClickListener
{
	private Context context;
	private CommonClickType type;
	private Class<?> cls;
	private Intent intent;
	public CommonClickListener(Context context,CommonClickType type) {
		this.context = context;
		this.type = type;
	}
	public CommonClickListener(Context context,CommonClickType type,Class<?> cls) {
		this.context = context;
		this.type = type;
		this.cls = cls;
	}
	public CommonClickListener(Context context,CommonClickType type,Intent intent) {
		this.context = context;
		this.type = type;
		this.intent = intent;
	}
	@Override
	public void onClick(View v)
	{
		//"返回"
		if(CommonClickType.back == type){
			((Activity)context).finish();
		}
		/*下一步，并结束掉自己*/
		else if(CommonClickType.cls2Killed == type){
			Intent intent = new Intent(context, cls);
			context.startActivity(intent);
			((Activity)context).finish();
		}
		/*下一步，不结束掉自己*/
		else if(CommonClickType.cls0Killed == type){
			Intent intent = new Intent(context, cls);
			context.startActivity(intent);
		}
		/*意图跳转，并结束掉自己*/
		else if(CommonClickType.intent2Killed == type){
			context.startActivity(intent);
			((Activity)context).finish();
		}
		/*意图跳转，不结束掉自己*/
		else if(CommonClickType.intent0Killed == type){
			context.startActivity(intent);
			((Activity)context).finish();
		}
	}
}
