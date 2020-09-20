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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.activity.reminder.AddEditReminderActivity;
import ir.android.persiantask.ui.services.AlarmJobService;

public class AlarmActivity extends AppCompatActivity {
    private TextView helloAlarm;
    private JobScheduler mScheduler;
    private Intent stopIntent;
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        helloAlarm = findViewById(R.id.helloAlarm);
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AlarmActivity.this);
        helloAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmJobService.ringtone.stop();
                finish();
            }
        });
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
