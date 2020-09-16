package ir.android.persiantask.ui.activity.theme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioGroup;

import ir.android.persiantask.R;

public class ThemeActivity extends AppCompatActivity {
    private RadioGroup themeGroup1, themeGroup2, themeGroup3;
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
                }
            }
        });
        themeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId != -1){
                    themeGroup1.check(-1);
                    themeGroup3.check(-1);
                }
            }
        });
        themeGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId != -1){
                    themeGroup2.check(-1);
                    themeGroup1.check(-1);
                }
            }
        });
    }

    private void init() {
        themeGroup1  = findViewById(R.id.themeGroup1);
        themeGroup2  = findViewById(R.id.themeGroup2);
        themeGroup3  = findViewById(R.id.themeGroup3);
    }
}