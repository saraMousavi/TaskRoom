package ir.android.persiantask.ui.activity;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import ir.android.persiantask.utils.TypefaceUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "Samim-FD.ttf");
//        Cheshmak.with(this);
//        Cheshmak.initTracker("Ky/5nzFcDs1Hx2zWrVf9Kg==");//don't forget to replace it with your own
    }
}
