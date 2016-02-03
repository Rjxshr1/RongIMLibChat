package github.common.listener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
/**继承图片下载的回调*/
public class XUtilBitmapCallBack extends BitmapLoadCallBack<ImageView>
{
	/**加载成功  */
	@Override
	public void onLoadCompleted(ImageView imageView, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from){
		imageView.setImageBitmap(bitmap);
	}
	@Override
	public void onLoadFailed(ImageView imageView, String uri, Drawable drawable)
	{
		imageView.setImageDrawable(drawable);
	}
}