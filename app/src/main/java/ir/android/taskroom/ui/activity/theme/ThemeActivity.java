package ir.android.taskroom.ui.activity.theme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ir.android.taskroom.R;
import ir.android.taskroom.ui.activity.MainActivity;

public class ThemeActivity extends AppCompatActivity {
    private RadioGroup themeGroup1, themeGroup2, themeGroup3;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theme_activity);
        init();
        onClickListener();
    }
    private void onClickListener() {
        themeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId != -1){
                    themeGroup2.check(-1);
                    themeGroup3.check(-1);
                    RadioButton selectedTheme = findViewById(themeGroup1.getCheckedRadioButtonId());
                    if(selectedTheme.getTag().equals("theme_1")){
                        saveFlag(1);
                    } else if(selectedTheme.getTag().equals("theme_2")){
                        saveFlag(2);
                    }
                }
            }
        });
        themeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId != -1){
                    themeGroup1.check(-1);
                    themeGroup3.check(-1);
                    RadioButton selectedTheme = findViewById(themeGroup2.getCheckedRadioButtonId());
                    if(selectedTheme.getTag().equals("theme_3")){
                        saveFlag(3);
                    } else if(selectedTheme.getTag().equals("theme_4")){
                        saveFlag(4);
                    }
                }
            }
        });
        themeGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId != -1){
                    themeGroup2.check(-1);
                    themeGroup1.check(-1);
                    RadioButton selectedTheme = findViewById(themeGroup3.getCheckedRadioButtonId());
                    if(selectedTheme.getTag().equals("theme_5")){
                        saveFlag(5);
                    } else if(selectedTheme.getTag().equals("theme_6")){
                        saveFlag(6);
                    }
                }
            }
        });
    }

    private void init() {
        themeGroup1  = findViewById(R.id.themeGroup1);
        themeGroup2  = findViewById(R.id.themeGroup2);
        themeGroup3  = findViewById(R.id.themeGroup3);
    }

    private void saveFlag(Integer flag){
        sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("theme");
        editor.putInt("theme", flag);
        editor.remove("NIGHT_MODE");
        editor.putBoolean("NIGHT_MODE", false);
        editor.apply();
        startActivity(new Intent(ThemeActivity.this, MainActivity.class));
        finish();
    }
}