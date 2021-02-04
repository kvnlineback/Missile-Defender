package project.missiledefender;

import android.animation.AnimatorSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;


import static project.missiledefender.Interceptor.INTERCEPTOR_BLAST;

public class MissileMaker implements Runnable {

    private static final String TAG = "MissileMaker";
    private MainActivity mainActivity;
    public static boolean isRunning;
    private ArrayList<Missile> activeMissiles = new ArrayList<>();
    private int screenWidth, screenHeight;
    private static final int NUM_LEVELS = 5;
    private long delayBetweenMissiles;
    private int missileCount = 0;
    private int currentLevel = 1;

    MissileMaker(MainActivity mainActivity, int screenWidth, int screenHeight) {
        this.mainActivity = mainActivity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    void setRunning(boolean running) {
        isRunning = running;
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile p : temp) {
            p.stop();
        }
    }

    @Override
    public void run() {
        delayBetweenMissiles = NUM_LEVELS * 1000;
        try {
            Thread.sleep((long) (delayBetweenMissiles * 0.5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setRunning(true);
        while (isRunning) {
            int resId = R.drawable.missile;
            long time = (long) ((delayBetweenMissiles * 0.75) + (Math.random() * delayBetweenMissiles));
            if (time <= 1000) {
                time = 1000;
            }
            Log.d(TAG, "run: TIME: " + time);
            final Missile missile = new Missile(screenWidth, screenHeight, time, mainActivity);
            activeMissiles.add(missile);
            final AnimatorSet as = missile.setData(resId);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    as.start();
                    SoundPlayer.getInstance().start("launch_missile");
                    missileCount++;
                }
            });
            if (missileCount >= NUM_LEVELS) {
                currentLevel++;
                missileCount = 0;
                if (currentLevel <= 3)
                    delayBetweenMissiles -= 650;
                else if (currentLevel <= 6)
                    delayBetweenMissiles -= 400;
                else if (currentLevel <= 10)
                    delayBetweenMissiles -= 200;
                else
                    delayBetweenMissiles -= 150;
                mainActivity.setLevel(currentLevel);
                if (delayBetweenMissiles <= 0)
                    delayBetweenMissiles = 100;
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "run: DELAY: " + delayBetweenMissiles);
            }
            sleep();

        }
    }

    void removeMissile(Missile p) {
        activeMissiles.remove(p);
    }

    private void sleep() {
        try {
            double rand = Math.random();
            if (rand < 0.1) {
                Thread.sleep(1);
            } else if (rand < 0.2) {
                Thread.sleep((long) (0.5 * delayBetweenMissiles));
            } else {
                Thread.sleep(delayBetweenMissiles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void applyInterceptorBlast(Interceptor interceptor, int id) {
        Log.d(TAG, "applyInterceptorBlast: -------------------------- " + id);
        float x1 = interceptor.getX();
        float y1 = interceptor.getY();
        Log.d(TAG, "applyInterceptorBlast: INTERCEPTOR: " + x1 + ", " + y1);
        ArrayList<Missile> nowGone = new ArrayList<>();
        ArrayList<Missile> temp = new ArrayList<>(activeMissiles);
        for (Missile m : temp) {
            float x2 = (int) (m.getX() + (0.5 * m.getWidth()));
            float y2 = (int) (m.getY() + (0.5 * m.getHeight()));
            Log.d(TAG, "applyInterceptorBlast:    Missile: " + x2 + ", " + y2);
            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
            Log.d(TAG, "applyInterceptorBlast:    DIST: " + f);
            if (f < INTERCEPTOR_BLAST) {
                SoundPlayer.getInstance().start("interceptor_hit_missile");
                mainActivity.incrementScore();
                Log.d(TAG, "applyInterceptorBlast:    Hit: " + f);
                m.setHit(true);
                m.interceptorBlast(x2, y2);
                nowGone.add(m);
            }
            Log.d(TAG, "applyInterceptorBlast: --------------------------");
        }
        for (Missile m : nowGone) {
            activeMissiles.remove(m);
        }
    }

}

