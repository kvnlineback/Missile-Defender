package project.missiledefender;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;



class ScrollingBackground {

    private Context context;
    private ViewGroup layout;
    private ImageView backImageA;
    private ImageView backImageB;
    private long duration;
    private int resId;

    ScrollingBackground(Context context, ViewGroup layout, int resId, long duration) {
        this.context = context;
        this.layout = layout;
        this.resId = resId;
        this.duration = duration;
        setupBackground();
    }

    private void setupBackground() {
        backImageA = new ImageView(context);
        backImageB = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout
                .LayoutParams(MainActivity.screenWidth + getBarHeight(), MainActivity.screenHeight);
        backImageA.setLayoutParams(params);
        backImageB.setLayoutParams(params);
        layout.addView(backImageA);
        layout.addView(backImageB);
        ObjectAnimator oa =
                ObjectAnimator.ofFloat(backImageA, "alpha", 0.25f, 0.9f);
        oa.setStartDelay(500);
        oa.setDuration(4000);
        oa.setRepeatMode(ValueAnimator.REVERSE);
        oa.setRepeatCount(ValueAnimator.INFINITE);
        oa.start();
        ObjectAnimator oa2 =
                ObjectAnimator.ofFloat(backImageB, "alpha", 0.25f, 0.9f);
        oa2.setStartDelay(500);
        oa2.setDuration(4000);
        oa2.setRepeatMode(ValueAnimator.REVERSE);
        oa2.setRepeatCount(ValueAnimator.INFINITE);
        oa2.start();
        Bitmap backBitmapA = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap backBitmapB = BitmapFactory.decodeResource(context.getResources(), resId);
        backImageA.setImageBitmap(backBitmapA);
        backImageB.setImageBitmap(backBitmapB);
        backImageA.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageB.setScaleType(ImageView.ScaleType.FIT_XY);
        backImageA.setZ(-1);
        backImageB.setZ(-1);
        animateBack();
    }

    private void animateBack() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                float width = MainActivity.screenWidth + getBarHeight();
                float a_translationX = width * progress;
                float b_translationX = width * progress - width;
                backImageA.setTranslationX(a_translationX);
                backImageB.setTranslationX(b_translationX);
                //Log.d(TAG, "onAnimationUpdate: A " + translationX + "   B " + (translationX - width));
                //Log.d(TAG, "onAnimationUpdate: A " + backImageA.getY() + "   B " + backImageB.getY());

            }
        });
        animator.start();
    }


    private int getBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
