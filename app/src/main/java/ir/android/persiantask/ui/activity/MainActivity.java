package ir.android.persiantask.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import java.util.ArrayList;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.adapters.ScreenSlidePageAdapterJ;
import ir.android.persiantask.ui.fragment.ScreenSlidePageFragmentJ;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<ScreenSlidePageFragmentJ> fragList = new ArrayList<>();
        fragList.add(ScreenSlidePageFragmentJ.newInstance(getString(R.string.Projects), R.color.white));
        fragList.add(ScreenSlidePageFragmentJ.newInstance(getString(R.string.Reminders), R.color.white));
        fragList.add(ScreenSlidePageFragmentJ.newInstance(getString(R.string.Calender), R.color.white));
        fragList.add(ScreenSlidePageFragmentJ.newInstance(getString(R.string.Setting), R.color.white));

        ScreenSlidePageAdapterJ pagerAdapter = new ScreenSlidePageAdapterJ(fragList, getSupportFragmentManager());

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
