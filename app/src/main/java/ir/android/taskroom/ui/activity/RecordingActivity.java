package ir.android.taskroom.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Attachments;
import ir.android.taskroom.ui.activity.task.AddEditTaskActivity;

public class RecordingActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 200;
    private Chronometer timer;
    private ImageButton recordBtn;
    private String outputFile;
    private MediaRecorder mediaRecorder;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onClickEvents();
    }

    private void onClickEvents() {
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordBtn.getTag().equals("unclicked")) {
                    if (checkRequestPermission()) {
                        startRecording(true);
                        recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_off));
                        recordBtn.setTag("clicked");
                    } else {
                        requestPermission();
                    }
                } else {
                    startRecording(false);
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));
                    recordBtn.setTag("unclicked");
                    Intent intent = new Intent();
                    intent.putExtra("outputFile", outputFile);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void init() {
        setContentView(R.layout.activity_recording);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        timer = findViewById(R.id.recordingTimer);
        recordBtn = findViewById(R.id.recordBtn);
        if (!checkRequestPermission()) {
            requestPermission();
        }
        String path = Environment.getExternalStorageDirectory().toString() + "/TaskRoom";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        outputFile = path + "/recording" + new Date().getTime() + ".3gp";
    }

    private boolean checkRequestPermission() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(RecordingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(RecordingActivity.this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordingActivity.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(RecordingActivity.this, getResources().getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startRecording(boolean isRecord) {
        if (isRecord) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.setOutputFile(outputFile);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
        } else {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            timer.stop();
        }
    }
}