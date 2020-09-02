package ir.android.persiantask.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianViewPagerType;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;


import java.util.ArrayList;
import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.ui.activity.task.AddEditTaskActivity;
import ir.android.persiantask.ui.adapters.TasksAdapter;
import kotlin.jvm.JvmStatic;

public class CalenderFragment extends Fragment {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private static final String TAG = "TAG";
    private View inflater;
    private PersianHorizontalExpCalendar persianHorizontalExpCalendar;
    private RecyclerView taskRecyclerView;
    private TasksAdapter taskAdapter;
    private FloatingActionButton addTaskBtn;
    private CollapsingToolbarLayout toolBarLayout;
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender_fragment, container, false);
        this.inflater = view;
        init();
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

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });



        return view;
    }

    private void init(){
        taskRecyclerView = this.inflater.findViewById(R.id.taskRecyclerView);
        addTaskBtn = this.inflater.findViewById(R.id.addTaskBtn);
        List<Tasks> tasks = new ArrayList<>();
        Tasks task = new Tasks(1, 1, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task1 = new Tasks(1, 0, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task2 = new Tasks(1, 1, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task3 = new Tasks(1, 0, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task4 = new Tasks(1, 1, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        tasks.add(task);
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        taskAdapter = new TasksAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(taskAdapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
