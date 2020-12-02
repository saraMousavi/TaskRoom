package ir.android.taskroom.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AnalogClock;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.analytics.FirebaseAnalytics;

import ir.android.taskroom.R;

public class AlarmActivity extends AppCompatActivity {
    public static AnalogClock alarmClockIcon;
    private SwitchCompat alarmActive;
    private TextView alarmTitle;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        turnOnScreen();
        onClickListener();
    }

    private void turnOnScreen() {
        int flags = WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().addFlags(flags);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                mediaPlayer.setLooping(false);
            }
        }, 180000);
    }

    private void onClickListener() {
        alarmActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer = null;
                    alarmTitle.setVisibility(View.GONE);
                    finish();
                }
            }
        });
    }

    private void init() {
        setContentView(R.layout.alarm_activity);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = MediaPlayer.create(AlarmActivity.this, R.raw.carol_of_the_bells_alarm);
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        }, 500);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        alarmClockIcon = findViewById(R.id.analogClock);
        alarmActive = findViewById(R.id.alarm_active);
        alarmTitle = findViewById(R.id.alarmTitle);
        alarmTitle.setText(getIntent().getExtras().getString("alarmTitle"));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
