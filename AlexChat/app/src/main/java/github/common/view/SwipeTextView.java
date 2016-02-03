package github.common.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
public class SwipeTextView extends TextView
{
	private OnSwipeListener onSwipeListener;
	private int distance = 100;
	public SwipeTextView(Context context) {
		super(context);
		setOnTouchListener(new MyOnTouchListener());
	}
	public SwipeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnTouchListener(new MyOnTouchListener());
	}
	private final class MyOnTouchListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			int startY = 0;
			if(MotionEvent.ACTION_DOWN == event.getAction()){
				setSelected(true);
				startY = (int) event.getY();
				if(onSwipeListener!=null){
					onSwipeListener.onStart((SwipeTextView) v);
				}
			}else if(MotionEvent.ACTION_MOVE == event.getAction()){
				setSelected(true);
				/*up的时候x的位置*/
				int secondY = (int)event.getY();
				int delta = secondY - startY;
				//LogUtils.e("delta = "+delta);
				if(delta < -distance){
					if(onSwipeListener!=null){
						onSwipeListener.onSwipe((SwipeTextView) v,SwipeType.isAboved);
					}
				}else{
					if(onSwipeListener!=null){
						onSwipeListener.onSwipe((SwipeTextView) v,SwipeType.isNotAboved);
					}
				}
			}else if(MotionEvent.ACTION_UP == event.getAction()){
				setSelected(false);
				int secondY = (int)event.getY();
				int delta = secondY - startY;
				//LogUtils.e("delta = "+delta);
				if(delta < -distance){
					if(onSwipeListener!=null){
						onSwipeListener.onStop((SwipeTextView) v,SwipeType.isAboved);
					}
				}else{
					if(onSwipeListener!=null){
						onSwipeListener.onStop((SwipeTextView) v,SwipeType.isNotAboved);
					}
				}
			}
			return false;
		}
	}
	public void setOnSwipeListener(OnSwipeListener onSwipeListener){
		this.onSwipeListener = onSwipeListener;
	}
	public interface OnSwipeListener
	{
		public void onStart(SwipeTextView swipeTextView);
		public void onSwipe(SwipeTextView swipeTextView,SwipeType swipeType);
		public void onStop(SwipeTextView swipeTextView,SwipeType swipeType);
	}
	public enum SwipeType{
		/**手指的位置 处于 第一次 按压的 上方，且有一段距离了*/
		isAboved,
		/**手指的位置 不在 第一次 按压的 上方*/
		isNotAboved
	}
}
