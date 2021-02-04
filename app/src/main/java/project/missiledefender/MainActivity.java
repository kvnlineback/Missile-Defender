package project.missiledefender;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static ConstraintLayout layout;
    public static int screenHeight;
    public static int screenWidth;
    private ImageView shootFromBase;
    private static final String TAG = "MainActivity";
    private int scoreValue;
    private TextView score;
    private TextView level;
    private MissileMaker missileMaker;
    public static ArrayList<Base> bases = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Base lastBlownBase;
    public MainActivity activity = this;
    public static int activeInterceptors = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFullScreen();
        getScreenDimensions();
        layout = findViewById(R.id.layout);
        score = findViewById(R.id.score);
        level = findViewById(R.id.level);
        SoundPlayer.getInstance().setupSound(this, "background", R.raw.background);
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.title);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        iv.setLayoutParams(layoutParams);
        layout.addView(iv);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0, 1);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startBackground();
                layout.removeView(iv);
            }
        });
        alpha.start();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void startBackground() {
        SoundPlayer.getInstance().setupSound(this, "interceptor_blast", R.raw.interceptor_blast);
        SoundPlayer.getInstance().setupSound(this, "launch_interceptor", R.raw.launch_interceptor);
        SoundPlayer.getInstance().setupSound(this, "interceptor_hit_missile", R.raw.interceptor_hit_missile);
        SoundPlayer.getInstance().setupSound(this, "base_blast", R.raw.base_blast);
        SoundPlayer.getInstance().setupSound(this, "launch_missile", R.raw.launch_missile);
        Base base1 = new Base((ImageView) findViewById(R.id.base1));
        base1.getBase().setVisibility(View.VISIBLE);
        Base base2 = new Base((ImageView) findViewById(R.id.base2));
        base2.getBase().setVisibility(View.VISIBLE);
        Base base3 = new Base((ImageView) findViewById(R.id.base3));
        base3.getBase().setVisibility(View.VISIBLE);
        bases.add(base1);
        bases.add(base2);
        bases.add(base3);
        new ScrollingBackground(this, layout, R.drawable.clouds, 4000);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    handleTouch(motionEvent.getX(), motionEvent.getY());
                }
                return false;
            }
        });
        missileMaker = new MissileMaker(this, screenWidth, screenHeight);
        new Thread(missileMaker).start();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setupFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void getScreenDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    public void applyInterceptorBlast(Interceptor interceptor, int id) {
        missileMaker.applyInterceptorBlast(interceptor, id);
    }

    public void handleTouch(float x1, float y1) {
        if (bases.isEmpty()) {
            return;
        }
        if (activeInterceptors <= 2) {

            if (bases.size() == 1) {
                shootFromBase = bases.get(0).getBase();
            }
            if (bases.size() == 2) {
                float x2 = (int) (bases.get(0).getBase().getX() + (0.5 * bases.get(0).getBase().getWidth()));
                float y2 = (int) (bases.get(0).getBase().getY() + (0.5 * bases.get(0).getBase().getHeight()));
                float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

                float x3 = (int) (bases.get(1).getBase().getX() + (0.5 * bases.get(1).getBase().getWidth()));
                float y3 = (int) (bases.get(1).getBase().getY() + (0.5 * bases.get(1).getBase().getHeight()));
                float f2 = (float) Math.sqrt((y3 - y1) * (y3 - y1) + (x3 - x1) * (x3 - x1));
                if (f < f2) {
                    shootFromBase = bases.get(0).getBase();
                } else {
                    shootFromBase = bases.get(1).getBase();
                }
            }
            if (bases.size() == 3) {
                float x2 = (int) (bases.get(0).getBase().getX() + (0.5 * bases.get(0).getBase().getWidth()));
                float y2 = (int) (bases.get(0).getBase().getY() + (0.5 * bases.get(0).getBase().getHeight()));
                float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

                float x3 = (int) (bases.get(1).getBase().getX() + (0.5 * bases.get(1).getBase().getWidth()));
                float y3 = (int) (bases.get(1).getBase().getY() + (0.5 * bases.get(1).getBase().getHeight()));
                float f2 = (float) Math.sqrt((y3 - y1) * (y3 - y1) + (x3 - x1) * (x3 - x1));

                float x4 = (int) (bases.get(2).getBase().getX() + (0.5 * bases.get(2).getBase().getWidth()));
                float y4 = (int) (bases.get(2).getBase().getY() + (0.5 * bases.get(2).getBase().getHeight()));
                float f3 = (float) Math.sqrt((y4 - y1) * (y4 - y1) + (x4 - x1) * (x4 - x1));

                if (f < f2 && f < f3) {
                    shootFromBase = bases.get(0).getBase();
                }
                if (f2 < f && f2 < f3) {
                    shootFromBase = bases.get(1).getBase();
                }
                if (f3 < f2 && f3 < f) {
                    shootFromBase = bases.get(2).getBase();
                }
            }
            double startX = shootFromBase.getX() + (0.5 * shootFromBase.getWidth());
            double startY = shootFromBase.getY() + (0.5 * shootFromBase.getHeight());
            float a = calculateAngle(startX, startY, x1, y1);
            Log.d(TAG, "handleTouch: " + a);
            Interceptor i = new Interceptor(this, (float) (startX - 10), (float) (startY - 30), x1, y1);
            SoundPlayer.getInstance().start("launch_interceptor");
            i.launch();
            activeInterceptors++;
        }
    }

    public float calculateAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
        // Keep angle between 0 and 360
        angle = angle + Math.ceil(-angle / 360) * 360;
        return (float) (190.0f - angle);
    }

    public void removeMissile(Missile p) {
        missileMaker.removeMissile(p);
    }

    public void incrementScore() {
        scoreValue++;
        score.setText(String.format(Locale.getDefault(), "%d", scoreValue));
    }

    public void setLevel(final int value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                level.setText(String.format(Locale.getDefault(), "Level: %d", value));
            }
        });
    }

    public boolean nearbyBase(int x1, int y1) {
        boolean nearby = false;
        ArrayList<Base> tempList2 = new ArrayList<>(bases);
        for (Base b : tempList2) {
            float x2 = (int) (b.getBase().getX() + (0.5 * b.getBase().getWidth()));
            float y2 = (int) (b.getBase().getY() + (0.5 * b.getBase().getHeight()));
            float f = (float) Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
            if (f < 160) {
                nearby = true;
                b.getBase().setVisibility(View.INVISIBLE);
                lastBlownBase = b;
                bases.remove(b);
                if (bases.size() == 0) {
                    endGame();
                }
            }
        }
        return nearby;
    }

    private void endGame() {
        MissileMaker.isRunning = false;
        showEndMessage();
    }

    private void showEndMessage() {
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.game_over);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        iv.setLayoutParams(layoutParams);
        layout.addView(iv);
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(iv, "alpha", 0, 1);
        alpha.setInterpolator(new LinearInterpolator());
        alpha.setDuration(3000);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layout.removeView(iv);
                queryDatabase();
            }
        });
        alpha.start();
    }

    private void queryDatabase() {
        new HighScoreHandler(this).execute(score.getText().toString());
    }

    public void getHighScore(String s) {
        if (s.equals("false")) {
            new GetAllScoresHandler(this).execute();
        } else {
            showHighScoreDialogue();
        }
    }

    public void showAllScores(String s) {
        Intent intent = new Intent(this, ScoresActivity.class);
        intent.putExtra("scores", s);
        startActivity(intent);
    }


    public void showHighScoreDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);
        int maxLength = 3;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        et.setFilters(fArray);
        builder.setTitle("You are a Top-Player!");
        builder.setMessage("Please enter your initials(up to 3 characters");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String time = String.valueOf(System.currentTimeMillis());
                String[] levels = level.getText().toString().split(" ");
                String level2 = levels[1];
                String score2 = score.getText().toString();
                new ScoreBoardHandler(activity).execute(time, score2, level2, et.getText().toString());
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new GetAllScoresHandler(activity).execute();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showNewHighScores(String s) {
        Intent intent = new Intent(this, ScoresActivity.class);
        intent.putExtra("scores", s);
        startActivity(intent);
    }
}
