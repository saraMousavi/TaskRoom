package ir.android.taskroom.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AnalogClock;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import ir.android.taskroom.R;
import ir.android.taskroom.ui.workers.AlarmWorker;
import ir.android.taskroom.utils.animation.CircleAnimation;
import ir.android.taskroom.utils.shape.Circle;

public class AlarmActivity extends AppCompatActivity {
    //    private ImageView alarmIcon;
    private AnalogClock alarmClockIcon;
    private SwitchCompat alarmActive;
    private TextView alarmTitle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().addFlags(flags);
    }

    private void onClickListener() {
        alarmActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AlarmWorker.ringtone.stop();
                    alarmTitle.setVisibility(View.GONE);
                    finish();
                }
            }
        });
    }

    @SuppressLint("ShortAlarm")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        setContentView(R.layout.alarm_activity);
//        alarmIcon = findViewById(R.id.alarmIcon);
        alarmClockIcon = findViewById(R.id.analogClock);
        alarmActive = findViewById(R.id.alarm_active);
        alarmTitle = findViewById(R.id.alarmTitle);
        alarmTitle.setText(getIntent().getExtras().getString("alarmTitle"));
        Circle circle = findViewById(R.id.alarmIcon);

        CircleAnimation circleAnimation = new CircleAnimation(circle, 360);
        circleAnimation.setDuration(1500);
        circleAnimation.setRepeatCount(Animation.INFINITE);
        circle.startAnimation(circleAnimation);
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