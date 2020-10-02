package ir.android.persiantask.ui.activity;

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
import ir.android.persiantask.ui.services.AlarmJobService;

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
                    AlarmJobService.ringtone.stop();
                    alarmTitle.setVisibility(View.GONE);
                    finish();
                }
            }
        });
    }

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
