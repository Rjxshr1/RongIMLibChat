package github.common.utils;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
public class ViewUtil 
{
	/**
	 * 无效值
	 */
	public static final int INVALID = Integer.MIN_VALUE;
	/**
	 * 描述：重置AbsListView的高度. item 的最外层布局要用 RelativeLayout,如果计算的不准，就为RelativeLayout指定一个高度
	 * @param absListView the abs list view
	 * @param lineNumber 每行几个 ListView一行一个item
	 * @param verticalSpace the vertical space
	 */
	public static void setAbsListViewHeight(AbsListView absListView, int lineNumber, int verticalSpace)
	{
		int totalHeight = getAbsListViewHeight(absListView, lineNumber, verticalSpace);
		ViewGroup.LayoutParams params = absListView.getLayoutParams();
		params.height = totalHeight;
		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
		absListView.setLayoutParams(params);
	}

	/**
	 * 描述：获取AbsListView的高度.
	 * 
	 * @param absListView   the abs list view
	 * @param lineNumber 每行几个 ListView一行一个item
	 * @param verticalSpace the vertical space
	 * @return the abs list view height
	 */
	public static int getAbsListViewHeight(AbsListView absListView, int lineNumber, int verticalSpace)
	{
		int totalHeight = 0;
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		absListView.measure(w, h);
		ListAdapter mListAdapter = absListView.getAdapter();
		if (mListAdapter == null) {
			return totalHeight;
		}
		int count = mListAdapter.getCount();
		if (absListView instanceof ListView)
		{
			for (int i = 0; i < count; i++)
			{
				View listItem = mListAdapter.getView(i, null, absListView);
				listItem.measure(w, h);
				totalHeight += listItem.getMeasuredHeight();
			}
			if (count == 0) {
				totalHeight = verticalSpace;
			} else {
				totalHeight = totalHeight + (((ListView) absListView).getDividerHeight() * (count - 1));
			}
		} else if (absListView instanceof GridView) {
			int remain = count % lineNumber;
			if (remain > 0) {
				remain = 1;
			}
			if (mListAdapter.getCount() == 0) {
				totalHeight = verticalSpace;
			} else {
				View listItem = mListAdapter.getView(0, null, absListView);
				listItem.measure(w, h);
				int line = count / lineNumber + remain;
				totalHeight = line * listItem.getMeasuredHeight() + (line - 1) * verticalSpace;
			}
		}
		return totalHeight;

	}

	/**
	 * 测量这个view
	 * 最后通过getMeasuredWidth()获取宽度和高度.
	 * @param view 要测量的view
	 * @return 测量过的view
	 */
	public static void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 获得这个View的宽度
	 * 测量这个view，最后通过getMeasuredWidth()获取宽度.
	 * @param view 要测量的view
	 * @return 测量过的view的宽度
	 */
	public static int getViewWidth(View view) {
		measureView(view);
		return view.getMeasuredWidth();
	}

	/**
	 * 获得这个View的高度
	 * 测量这个view，最后通过getMeasuredHeight()获取高度.
	 * @param view 要测量的view
	 * @return 测量过的view的高度
	 */
	public static int getViewHeight(View view) {
		measureView(view);
		return view.getMeasuredHeight();
	}

	/**
	 * 从父亲布局中移除自己
	 * @param v
	 */
	public static void removeSelfFromParent(View v) {
		ViewParent parent = v.getParent();
		if(parent != null){
			if(parent instanceof ViewGroup){
				((ViewGroup)parent).removeView(v);
			}
		}
	}
	/**
	 * TypedValue官方源码中的算法，任意单位转换为PX单位
	 * @param unit  TypedValue.COMPLEX_UNIT_DIP
	 * @param value 对应单位的值
	 * @param metrics 密度
	 * @return px值
	 */
	public static float applyDimension(int unit, float value,
			DisplayMetrics metrics){
		switch (unit) {
		case TypedValue.COMPLEX_UNIT_PX:
			return value;
		case TypedValue.COMPLEX_UNIT_DIP:
			return value * metrics.density;
		case TypedValue.COMPLEX_UNIT_SP:
			return value * metrics.scaledDensity;
		case TypedValue.COMPLEX_UNIT_PT:
			return value * metrics.xdpi * (1.0f/72);
		case TypedValue.COMPLEX_UNIT_IN:
			return value * metrics.xdpi;
		case TypedValue.COMPLEX_UNIT_MM:
			return value * metrics.xdpi * (1.0f/25.4f);
		}
		return 0;
	}
	public static void setSelectionAtEndForEditText(View view){
		if(view instanceof EditText){
			EditText editText = (EditText) view;
			String text = editText.getText().toString();
			if(!TextUtils.isEmpty(text)){
				editText.setSelection(text.length());
			}
		}
	}
	/**给TextView设置Text*/
	public static void setText2TextView(View view, String text){
		TextView textView = (TextView)view;
		if((textView!=null) && (text!=null) && (!"null".equalsIgnoreCase(text))){
			textView.setVisibility(View.VISIBLE);
			textView.setText(text);
		}
	}
	/**给EditText设置Text*/
	public static void setText2EditText(View view, String text){
		EditText editText = (EditText)view;
		if((editText!=null) && (text!=null) && (!"null".equalsIgnoreCase(text))){
			editText.setVisibility(View.VISIBLE);
			editText.setText(text);
		}
	}
	public static int getYLocationOnScreen(View view){
		int[] location = new int[2]; 
		view.getLocationOnScreen(location);
		@SuppressWarnings("unused")
		int x = location[0];
		int y = location[1];
		LogUtils.e("y = "+y);
		return y;
	}
	/**给 View 加一个 外壳
	 * @param view 传过来的 View
	 * @param shellView 外壳
	 * @return view + shellView = bodyView
	 * */
	public static View addShell2View(View view,Context context, View shellView){
		if(context == null){
			return view;
		}
		LayoutParams lp = view.getLayoutParams();
		if(lp == null){
			return view;
		}
		ViewGroup vgParent = (ViewGroup)view.getParent();
		if(vgParent == null){
			return view;
		}
		int index = vgParent.indexOfChild(view);
		/*view只能有一个Parent，先移除后添加*/
		vgParent.removeView(view);
		final FrameLayout bodyView = new FrameLayout(context);
		/*FrameLayout 第一层视图*/
		bodyView.addView(view);
		final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		/*FrameLayout 第二层视图*/
		bodyView.addView(shellView, layoutParams);
		vgParent.addView(bodyView, index, lp);
		vgParent.invalidate();
		shellView.setVisibility(View.GONE);
		return bodyView;
	}
}
