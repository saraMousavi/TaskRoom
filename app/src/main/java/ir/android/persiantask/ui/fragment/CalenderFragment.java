package ir.android.persiantask.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianViewPagerType;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;


import ir.android.persiantask.R;
import kotlin.jvm.JvmStatic;

public class CalenderFragment extends Fragment {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private static final String TAG = "TAG";
    private View inflater;
    private PersianHorizontalExpCalendar persianHorizontalExpCalendar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender_fragment, container, false);
        this.inflater = view;
        persianHorizontalExpCalendar = (PersianHorizontalExpCalendar)this.inflater.findViewById(R.id.persianCalendar);
        persianHorizontalExpCalendar.setPersianHorizontalExpCalListener(new PersianHorizontalExpCalendar.PersianHorizontalExpCalListener() {
            @Override
            public void onCalendarScroll(DateTime dateTime) {
                Log.i(TAG, "onCalendarScroll: " + dateTime.toString());
            }

            @Override
            public void onDateSelected(DateTime dateTime) {
                Log.i(TAG, "onDateSelected: " + dateTime.toString());
//                cutomMarkTodaySelectedDay();
//                markSomeDays();
            }

            @Override
            public void onChangeViewPager(PersianViewPagerType persianViewPagerType) {
                Log.i(TAG, "onChangeViewPager: " + persianViewPagerType.name());
            }
        });



        return view;
    }





    @JvmStatic
    @NotNull
    public static CalenderFragment newInstance(@NotNull String title, int bgColorId) {
        return CalenderFragment.Companion.newInstance(title, bgColorId);
    }

    public static final class Companion {
        @JvmStatic
        @NotNull
        public static CalenderFragment newInstance(@NotNull String title, int bgColorId) {
            CalenderFragment calenderFragment = new CalenderFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_TITLE, title);
            bundle.putInt(ARG_BG_COLOR, bgColorId);
            calenderFragment.setArguments(bundle);
            return calenderFragment;
        }

        private Companion() {
        }

    }
}
