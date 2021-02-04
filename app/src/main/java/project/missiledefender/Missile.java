package project.missiledefender;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

class Missile {

    private MainActivity mainActivity;
    private ImageView imageView;
    private AnimatorSet aSet = new AnimatorSet();
    private int screenHeight;
    private int screenWidth;
    private long screenTime;
    private static final String TAG = "Missile";
    private boolean hit = false;

    Missile(int screenWidth, int screenHeight, long screenTime, final MainActivity mainActivity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenTime = screenTime;
        this.mainActivity = mainActivity;
        imageView = new ImageView(mainActivity);
        imageView.setX(-500);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.layout.addView(imageView);
            }
        });

    }

    AnimatorSet setData(final int drawId) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(drawId);
            }
        });
        final int startX = (int) (Math.random() * screenHeight * 0.8);
        final int endX = (startX + (Math.random() < 0.5 ? 500 : -500));
        final int startY = -200;
        final int endY = (int) (screenHeight * 0.90);
        ObjectAnimator xAnim = ObjectAnimator.ofFloat(imageView, "x", startX, endX);
        xAnim.setInterpolator(new LinearInterpolator());
        xAnim.setDuration(screenTime);
        xAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!hit) {
                            if (mainActivity.nearbyBase(endX, endY)) {
                                makeBaseBlast();
                                makeGroundBlast();
                                mainActivity.layout.removeView(imageView);
                                mainActivity.removeMissile(Missile.this);
                            } else {
                                makeGroundBlast();
                                mainActivity.layout.removeView(imageView);
                                mainActivity.removeMissile(Missile.this);
                            }
                            //check to se if hit base and blow up here
                        } else {
                            mainActivity.layout.removeView(imageView);
                            mainActivity.removeMissile(Missile.this);
                        }
                        Log.d(TAG, "run: NUM VIEWS " +
                                mainActivity.layout.getChildCount());
                    }
                });

            }
        });

        ObjectAnimator yAnim = ObjectAnimator.ofFloat(imageView, "y", startY, endY);
        yAnim.setInterpolator(new LinearInterpolator());
        yAnim.setDuration(screenTime);
        float a = calculateAngle(startX, startY, endX, endY);
        imageView.setRotation(a);
        aSet.playTogether(xAnim, yAnim);
        return aSet;

    }

    void stop() {
        aSet.cancel();
    }

    float getX() {
        return imageView.getX();
    }

    float getY() {
        return imageView.getY();
    }

    float getWidth() {
        return imageView.getWidth();
    }

    float getHeight() {
        return imageView.getHeight();
    }

    void interceptorBlast(float x, float y) {
        final ImageView iv = new ImageView(mainActivity);
        iv.setImageResource(R.drawable.explode);

        iv.setTransitionName("Missile Intercepted Blast");

        int w = imageView.getDrawable().getIntrinsicWidth();
        int offset = (int) (w * 0.5);

        iv.setX(x - offset);
        iv.setY(y - offset);
        iv.setRotation((float) (360.0 * Math.random()));

        aSet.cancel();

        mainActivity.layout.removeView(imageView);
        mainActivity.layout.addView(iv);

        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.layout.removeView(imageView);
            }
        });
        alpha.start();
    }

    private float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    private void makeGroundBlast() {
        //SoundPlayer.getInstance().start("interceptor_blast");
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.explode);
        explodeView.setTransitionName("Ground blast");
        float w = explodeView.getDrawable().getIntrinsicWidth();
        explodeView.setX(this.getX() - (w / 4));
        explodeView.setY(this.getY() - (w / 4));
        explodeView.setZ(-15);
        mainActivity.layout.addView(explodeView);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(1500);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.layout.removeView(explodeView);
            }
        });
        alpha.start();

    }

    private void makeBaseBlast() {
        SoundPlayer.getInstance().start("base_blast");
        final ImageView explodeView = new ImageView(mainActivity);
        explodeView.setImageResource(R.drawable.blast);
        explodeView.setTransitionName("Base blast");
        float w = explodeView.getDrawable().getIntrinsicWidth();
        explodeView.setX(MainActivity.lastBlownBase.getBase().getX() - (w / 4));
        explodeView.setY(MainActivity.lastBlownBase.getBase().getY() - (w / 4));
        explodeView.setZ(-15);
        mainActivity.layout.addView(explodeView);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(explodeView, "alpha", 0.0f);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(1500);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mainActivity.layout.removeView(explodeView);
            }
        });
        alpha.start();

    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}