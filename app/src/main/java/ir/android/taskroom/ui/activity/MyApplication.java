package ir.android.taskroom.ui.activity;

import android.app.Application;

import androidx.lifecycle.Observer;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Category;
import ir.android.taskroom.utils.TypefaceUtil;
import ir.android.taskroom.viewmodels.CategoryViewModel;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/samim_fd.ttf");
        CategoryViewModel categoryViewModel = new CategoryViewModel(this);
        categoryViewModel.getAllCategory().observeForever(new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories.size() == 0) {
                    Category artCategory = new Category(getString(R.string.art), "ir.android.taskroom:drawable/ic_white_art");
                    categoryViewModel.insert(artCategory);
                    Category sportCategory = new Category(getString(R.string.sports), "ir.android.taskroom:drawable/ic_white_sports");
                    categoryViewModel.insert(sportCategory);
                    Category scientificCategory = new Category(getString(R.string.scientific), "ir.android.taskroom:drawable/ic_white_scientific");
                    categoryViewModel.insert(scientificCategory);
                }
            }
        });
//        Cheshmak.with(this);
//        Cheshmak.initTracker("Ky/5nzFcDs1Hx2zWrVf9Kg==");//don't forget to replace it with your own
    }
}
