package project.missiledefender;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HighScoreHandler extends AsyncTask<String, Void, String> {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    @SuppressLint("StaticFieldLeak")
    private MainActivity context;
    private static String dbURL;
    private Connection conn;
    private static final String TAG = "ScoreBoardHandler";
    private static final String APP_TABLE = "AppScores";
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());


    HighScoreHandler(MainActivity ctx) {
        context = ctx;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    protected String doInBackground(String... values) {
        final int score = Integer.parseInt(values[0]);
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");
            final StringBuilder sb = new StringBuilder();
            int lastScore = getLastScore();
            if (score > lastScore)
                sb.append("true");
            else
                sb.append("false");


            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: " + s);
        context.getHighScore(s);
    }


    private int getLastScore() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from " + APP_TABLE + " order by Score desc limit 10";
        StringBuilder sb = new StringBuilder();
        ResultSet rs = stmt.executeQuery(sql);
        rs.last();
        int score = rs.getInt("Score");
        rs.close();
        stmt.close();
        return score;

    }


}