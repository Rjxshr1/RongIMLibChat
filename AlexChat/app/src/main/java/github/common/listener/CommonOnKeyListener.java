package github.common.listener;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
public class CommonOnKeyListener implements OnKeyListener
{
	private Activity activity;
	public CommonOnKeyListener(Activity activity) {
		this.activity = activity;
	}
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	{
		dialog.dismiss();
		activity.finish();
		return true;
	}
}