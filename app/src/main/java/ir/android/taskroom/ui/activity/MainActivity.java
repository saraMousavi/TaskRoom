package ir.android.taskroom.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;


import java.util.Locale;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMasterTheme();
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation_view_linear);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintMain);
        loadLocal();
        if (SettingUtil.getInstance(MainActivity.this).isEnglishLanguage()) {
            constraintLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            navView.inflateMenu(R.menu.bottom_nav_english_menu);
        } else {
            constraintLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            navView.inflateMenu(R.menu.bottom_nav_menu);
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        if (SettingUtil.getInstance(MainActivity.this).isEnglishLanguage()) {
            navController.setGraph(R.navigation.mobile_navigation_english);
        }

        NavigationUI.setupWithNavController(navView, navController);
    }

    public void setMasterTheme() {
        if (SettingUtil.getInstance(MainActivity.this).isDarkTheme()) {
            setTheme(R.style.FeedActivityThemeDark);
            return;
        }
        switch (getFlag()) {
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            case 3:
                setTheme(R.style.AppTheme3);
                break;
            case 4:
                setTheme(R.style.AppTheme4);
                break;
            case 5:
                setTheme(R.style.AppTheme5);
                break;
            case 6:
                setTheme(R.style.AppTheme6);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }

    private void loadLocal() {
        Locale locale = new Locale(SettingUtil.getInstance(MainActivity.this).isEnglishLanguage() ? "en" : "fa");
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    public Integer getFlag() {
        SharedPreferences sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedpreferences.getInt("theme", 1);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
