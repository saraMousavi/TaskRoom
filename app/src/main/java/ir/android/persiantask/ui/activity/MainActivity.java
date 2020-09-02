package ir.android.persiantask.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import java.util.ArrayList;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.adapters.MainAdapter;
import ir.android.persiantask.ui.fragment.CalenderFragment;
import ir.android.persiantask.ui.fragment.ProjectsFragment;
import ir.android.persiantask.ui.fragment.ReminderFragment;
import ir.android.persiantask.ui.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Fragment> fragList = new ArrayList<>();
        fragList.add(ProjectsFragment.newInstance(getString(R.string.Projects), R.color.white));
        fragList.add(ReminderFragment.newInstance(getString(R.string.Reminders), R.color.white));
        fragList.add(CalenderFragment.newInstance(getString(R.string.Calender), R.color.white));
        fragList.add(SettingFragment.newInstance(getString(R.string.Setting), R.color.white));

        MainAdapter pagerAdapter = new MainAdapter(fragList, getSupportFragmentManager());

        final BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);
//        bubbleNavigationLinearView.setTypeface(Typeface.createFromAsset(getAssets(), "rubik.ttf"));


        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                bubbleNavigationLinearView.setCurrentActiveItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });
    }
}
