package github.jianghejie.xrecyclerview.progressindicator.indicator;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.animation.LinearInterpolator;

/**
 * Created by Jack on 2015/10/19.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BallScaleRippleMultipleIndicator extends BallScaleMultipleIndicator{


    @Override
    public void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        super.draw(canvas, paint);
    }

    @Override
    public List<Animator> createAnimation() {
        List<Animator> animators=new ArrayList<Animator>();
        long[] delays=new long[]{0, 200, 400};
        for (int i = 0; i < 3; i++) {
            final int index=i;
            ValueAnimator scaleAnim=ValueAnimator.ofFloat(0,1);
            scaleAnim.setInterpolator(new LinearInterpolator());
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleFloats[index] = (Float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            scaleAnim.setStartDelay(delays[i]);
            scaleAnim.start();

            ValueAnimator alphaAnim=ValueAnimator.ofInt(0,255);
            scaleAnim.setInterpolator(new LinearInterpolator());
            alphaAnim.setDuration(1000);
            alphaAnim.setRepeatCount(-1);
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    alphaInts[index] = (Integer) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            scaleAnim.setStartDelay(delays[i]);
            alphaAnim.start();

            animators.add(scaleAnim);
            animators.add(alphaAnim);
        }
        return animators;
    }

}
