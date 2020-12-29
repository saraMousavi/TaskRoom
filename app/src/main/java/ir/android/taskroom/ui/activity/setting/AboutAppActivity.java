package ir.android.taskroom.ui.activity.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import ir.android.taskroom.R;
import ir.android.taskroom.ui.activity.MainActivity;

public class AboutAppActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app_activity);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AboutAppActivity.this);
        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
            }
        });

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                if (getIntent().getExtras().getInt("isFirstInvoke") == 1) {
                    startActivity(new Intent(AboutAppActivity.this, MainActivity.class));
                } else {
                    finish();
                }
                // Probably here will be your exit action
            }
        });

    }

    // Just example data for Onboarding
    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage(getResources().getString(R.string.aboutAppProjectTitle), getResources().getString(R.string.aboutAppProjectDescription),
                Color.parseColor("#678FB4"), R.drawable.about_projects, R.drawable.ic_white_projects);
        PaperOnboardingPage scr2 = new PaperOnboardingPage(getResources().getString(R.string.aboutAppReminderTitle), getResources().getString(R.string.aboutAppReminderDescription),
                Color.parseColor("#65B0B4"), R.drawable.about_reminder, R.drawable.ic_black_reminder_active);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(getResources().getString(R.string.aboutAppCalenderTitle), getResources().getString(R.string.aboutAppCalenderDescription),
                Color.parseColor("#9B90BC"), R.drawable.about_calender, R.drawable.ic_calendar);
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }
}