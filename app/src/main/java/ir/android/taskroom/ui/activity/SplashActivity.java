package ir.android.taskroom.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import ir.android.taskroom.R;
import ir.android.taskroom.ui.activity.setting.AboutAppActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private TextView mContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        mContentView = findViewById(R.id.fullscreen_content);
        ImageView splashImage = findViewById(R.id.splash_image);
        ViewCompat.animate(splashImage).scaleX(1).scaleY(1).setDuration(1500).start();
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,
                R.anim.fade_in);
        animation.setDuration(2000);
        mContentView.startAnimation(animation);
        mContentView.setVisibility(View.VISIBLE);

        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(SplashActivity.this);
        if(sharedPreferences.getBoolean("NIGHT_MODE", false)){
            ConstraintLayout splashRoot = findViewById(R.id.splash_root);
            splashRoot.setBackgroundColor(getResources().getColor(R.color.backgroundDarkWindow));
            mContentView.setTextColor(getResources().getColor(R.color.white));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (sharedPreferences.getInt("isFirstInstall", 0) == 1) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, AboutAppActivity.class);
                    intent.putExtra("isFirstInvoke", 1);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("isFirstInstall", 1);
                    editor.apply();
                }
                startActivity(intent);
                finish();
            }
        }, 3500);
    }

}