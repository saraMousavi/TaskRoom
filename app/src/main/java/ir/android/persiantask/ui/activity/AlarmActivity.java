package ir.android.persiantask.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AnalogClock;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.workers.AlarmWorker;

public class AlarmActivity extends AppCompatActivity {
    private ImageView alarmIcon;
    private AnalogClock alarmClockIcon;
    private SwitchCompat alarmActive;
    private TextView alarmTitle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onClickListener();
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
        alarmIcon = findViewById(R.id.alarmIcon);
        alarmClockIcon = findViewById(R.id.analogClock);
        alarmActive = findViewById(R.id.alarm_active);
        alarmTitle = findViewById(R.id.alarmTitle);
        Animation animation = AnimationUtils.loadAnimation(AlarmActivity.this,
                R.anim.shake);
        alarmIcon.startAnimation(animation);
        alarmClockIcon.startAnimation(animation);
//        Intent intent = new Intent(AlarmActivity.this, AlarmJobService.class);
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getService(AlarmActivity.this, 0 , intent, 0);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
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
