package ir.android.persiantask.ui.activity;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.view.ViewCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

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
                    Category artCategory = new Category(getString(R.string.art), "ir.android.persiantask:drawable/ic_white_art");
                    categoryViewModel.insert(artCategory);
                    Category sportCategory = new Category(getString(R.string.sports), "ir.android.persiantask:drawable/ic_white_sports");
                    categoryViewModel.insert(sportCategory);
                    Category scientificCategory = new Category(getString(R.string.scientific), "ir.android.persiantask:drawable/ic_white_scientific");
                    categoryViewModel.insert(scientificCategory);
                }
            }
        });
//        Cheshmak.with(this);
//        Cheshmak.initTracker("Ky/5nzFcDs1Hx2zWrVf9Kg==");//don't forget to replace it with your own
    }
}
