package ir.android.taskroom.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.ui.activity.MainActivity;
import ir.android.taskroom.ui.activity.setting.AboutAppActivity;

public class LanguageDialog extends Dialog {
    private Button englishLanguage, persianLanguage;
    private SharedPreferences sharedPreferences;
    private Context mContext;

    public LanguageDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_dialog);
        englishLanguage = findViewById(R.id.englishLanguage);
        persianLanguage = findViewById(R.id.persianLanguage);
        if(SettingUtil.getInstance(getContext()).isEnglishLanguage()){
            englishLanguage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext,R.drawable.flag_english_icon), null, ContextCompat.getDrawable(mContext,R.drawable.ic_radio_check), null);
            englishLanguage.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
        } else {
            persianLanguage.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext,R.drawable.flag_persian_icon), null, ContextCompat.getDrawable(mContext,R.drawable.ic_radio_check), null);
            persianLanguage.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.white)));
        }
        clickEvent();
    }

    private void clickEvent() {
        englishLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity("en");
            }
        });
        persianLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity("fa");
            }
        });
    }

    private void startActivity(String language) {
        setLocale(language);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Intent intent;
        if (sharedPreferences.getInt("isFirstInstall", 0) == 1) {
            intent = new Intent(mContext, MainActivity.class);
        } else {
            intent = new Intent(mContext, AboutAppActivity.class);
            intent.putExtra("isFirstInvoke", 1);
            editor.putInt("isFirstInstall", 1);
        }
        SettingUtil.getInstance(getContext()).setEnglishLanguage(language.equals("en"));
        editor.apply();
        dismiss();
        mContext.startActivity(intent);
    }

    private void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getContext().getResources().updateConfiguration(configuration, getContext().getResources().getDisplayMetrics());
    }
}
