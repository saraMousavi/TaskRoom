package ir.android.persiantask.ui.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.activity.reminder.AddEditReminderActivity;
import ir.android.persiantask.ui.services.AlarmJobService;

public class AlarmActivity extends AppCompatActivity {
    private ImageView alarmIcon;
    private SwitchCompat alarmActive;

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
                if (isChecked) {
                    AlarmJobService.ringtone.stop();
                    finish();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        setContentView(R.layout.alarm_activity);
        alarmIcon = findViewById(R.id.alarmIcon);
        alarmActive = findViewById(R.id.alarm_active);
        Animation animation = AnimationUtils.loadAnimation(AlarmActivity.this,
                R.anim.shake);
        alarmIcon.startAnimation(animation);
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
