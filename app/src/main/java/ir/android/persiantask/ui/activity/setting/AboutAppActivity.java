package ir.android.persiantask.ui.activity.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.ramotion.paperonboarding.PaperOnboardingEngine;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnChangeListener;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;
import ir.android.persiantask.R;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app_activity);
        PaperOnboardingEngine engine = new PaperOnboardingEngine(findViewById(R.id.onboardingRootView), getDataForOnboarding(), getApplicationContext());

        engine.setOnChangeListener(new PaperOnboardingOnChangeListener() {
            @Override
            public void onPageChanged(int oldElementIndex, int newElementIndex) {
            }
        });

        engine.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                // Probably here will be your exit action
            }
        });

    }
    // Just example data for Onboarding
    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage(getResources().getString(R.string.aboutAppProjectTitle), getResources().getString(R.string.aboutAppProjectDescription),
                Color.parseColor("#678FB4"), R.drawable.about_projects, R.drawable.ic_white_projects);
        PaperOnboardingPage scr2 = new PaperOnboardingPage(getResources().getString(R.string.aboutAppReminderTitle), getResources().getString(R.string.aboutAppReminderDescription),
                Color.parseColor("#65B0B4"), R.drawable.about_reminder, R.drawable.ic_black_reminder_active);
        PaperOnboardingPage scr3 = new PaperOnboardingPage(getResources().getString(R.string.aboutAppCalenderTitle), getResources().getString(R.string.aboutAppCalenderDescription),
                Color.parseColor("#9B90BC"), R.drawable.about_calender, R.drawable.ic_calendar);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }
}