package ir.android.taskroom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingUtil {
    private static SettingUtil instance = null;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private SettingUtil(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public static SettingUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SettingUtil(context);
        }
        return instance;
    }

    public boolean isDarkTheme() {
        return sharedPreferences.getBoolean(Constant.NIGHT_MODE, false);
    }

    public void setDarkTheme(boolean darkState) {
        editor.putBoolean(Constant.NIGHT_MODE,darkState).apply();
    }

    public boolean isEnglishLanguage(){
        return sharedPreferences.getBoolean(Constant.ENGLISH_LANGUAGE,false);
    }

    public void setEnglishLanguage(boolean languageState){
        editor.putBoolean(Constant.ENGLISH_LANGUAGE, languageState).apply();
    }
}
