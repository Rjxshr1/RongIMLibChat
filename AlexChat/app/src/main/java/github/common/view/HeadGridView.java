package github.common.view;

/**
 * Created by Administrator on 2015/6/29.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class HeadGridView extends GridView
{
    public HeadGridView(Context paramContext)
    {
        super(paramContext);
    }

    public HeadGridView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    public HeadGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
      super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
