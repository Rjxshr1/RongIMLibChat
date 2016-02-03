package github.dkmeteor.waterdrop;
import github.dkmeteor.waterdrop.DropCover.OnDragCompeteListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
/**
 * @link https://github.com/dkmeteor/Bubble-Notification
 * */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WaterDrop extends RelativeLayout 
{
	private Paint mPaint = new Paint();
	private TextView mTextView;
	private DropCover.OnDragCompeteListener mOnDragCompeteListener;
	private boolean mHolderEventFlag;
	private int backgroundColor = 0xFF5722;
	public WaterDrop(Context context) {
		super(context);
		init();
	}
	public WaterDrop(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public void setText(String str) {
		mTextView.setText(str);
	}
	public void setTextSize(int size) {
		mTextView.setTextSize(size);
	}
	public void setBackgroundColor(int color){
		this.backgroundColor = color;
	}
	private void init() {
		mPaint.setAntiAlias(true);
		if (VERSION.SDK_INT > 11) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mTextView = new TextView(getContext());
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		mTextView.setTextSize(13);
		mTextView.setTextColor(Color.parseColor("#FFFFFF"));
		mTextView.setLayoutParams(params);
		addView(mTextView);
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		mPaint.setColor(backgroundColor);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
		super.dispatchDraw(canvas);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		ViewGroup parent = getScrollableParent();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mHolderEventFlag = !CoverManager.getInstance().isRunning();
			if (mHolderEventFlag) {
				if (parent != null)
					parent.requestDisallowInterceptTouchEvent(true);
				CoverManager.getInstance().start(this, event.getRawX(), event.getRawY(), mOnDragCompeteListener);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mHolderEventFlag) {
				CoverManager.getInstance().update(event.getRawX(), event.getRawY());
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mHolderEventFlag) {
				if (parent != null)
					parent.requestDisallowInterceptTouchEvent(false);
				CoverManager.getInstance().finish(this, event.getRawX(), event.getRawY());
			}
			break;
		}

		return true;
	}

	private ViewGroup getScrollableParent() {
		View target = this;
		while (true) {
			View parent;
			try {
				parent = (View) target.getParent();
			} catch (Exception e) {
				return null;
			}
			if (parent == null)
				return null;
			if (parent instanceof ListView || parent instanceof ScrollView) {
				return (ViewGroup) parent;
			}
			target = parent;
		}

	}
	public void setOnDragCompeteListener(OnDragCompeteListener onDragCompeteListener) {
		mOnDragCompeteListener = onDragCompeteListener;
	}
}
