package ir.android.persiantask.ui.activity;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Category;
import ir.android.persiantask.utils.TypefaceUtil;
import ir.android.persiantask.viewmodels.CategoryViewModel;

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
                    Category artCategory = new Category(getString(R.string.art));
                    categoryViewModel.insert(artCategory);
                    Category sportCategory = new Category(getString(R.string.sports));
                    categoryViewModel.insert(sportCategory);
                    Category scientificCategory = new Category(getString(R.string.scientific));
                    categoryViewModel.insert(scientificCategory);
                }
            }
        });
//        Cheshmak.with(this);
//        Cheshmak.initTracker("Ky/5nzFcDs1Hx2zWrVf9Kg==");//don't forget to replace it with your own
    }
}
