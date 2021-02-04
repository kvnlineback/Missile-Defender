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

public class GetAllScoresHandler extends AsyncTask<String, Void, String> {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    @SuppressLint("StaticFieldLeak")
    private MainActivity context;
    private static String dbURL;
    private Connection conn;
    private static final String TAG = "ScoreBoardHandler";
    private static final String APP_TABLE = "AppScores";
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());


    GetAllScoresHandler(MainActivity ctx) {
        context = ctx;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    protected String doInBackground(String... values) {


        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");
            final StringBuilder sb = new StringBuilder();
            sb.append(getAllScores());
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
        context.showAllScores(s);
    }


    private String getAllScores() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from " + APP_TABLE + " order by Score desc limit 10";
        StringBuilder sb = new StringBuilder();
        ResultSet rs = stmt.executeQuery(sql);
        //sb.append("#     Init   Level   Score   Date/Time\n");
        sb.append(String.format(Locale.getDefault(), "%-3s  %-4s   %-6s   %-6s   %s\n", "#", "Init", "Level", "Score", "Date/Time"));
        int count = 1;
        while (rs.next()) {
            String initials = rs.getString("Initials");
            int score = rs.getInt("Score");
            int level = rs.getInt("Level");
            long millis = rs.getLong("DateTime");
            sb.append(String.format(Locale.getDefault(), "  %-3s  %-4s   %-6s   %-6s   %s\n", count, initials, level, score, sdf.format(new Date(millis))));
            count++;
        }
        rs.close();
        stmt.close();

        return sb.toString();
    }


}
