package ir.android.persiantask.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianCustomMarks;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianViewPagerType;
import com.mohamadian.persianhorizontalexpcalendar.model.CustomGradientDrawable;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.factory.ProjectsViewModelFactory;
import ir.android.persiantask.data.db.factory.SubTasksViewModelFactory;
import ir.android.persiantask.data.db.factory.TasksViewModelFactory;
import ir.android.persiantask.ui.activity.reminder.AddEditReminderActivity;
import ir.android.persiantask.ui.activity.task.AddEditTaskActivity;
import ir.android.persiantask.ui.adapters.ReminderAdapter;
import ir.android.persiantask.ui.adapters.TasksAdapter;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.viewmodels.ProjectViewModel;
import ir.android.persiantask.viewmodels.ReminderViewModel;
import ir.android.persiantask.viewmodels.SubTasksViewModel;
import ir.android.persiantask.viewmodels.TaskViewModel;
import kotlin.jvm.JvmStatic;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CalenderFragment extends Fragment {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private static final String TAG = "TAG";
    private View inflater;
    private PersianHorizontalExpCalendar persianHorizontalExpCalendar;
    private RecyclerView recyclerView;
    private FloatingActionButton addTaskBtn;
    private LinearLayout fab1, fab2;
    private CollapsingToolbarLayout toolBarLayout;
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    public static final int ADD_REMINDER_REQUEST = 1;
    public static final int EDIT_REMINDER_REQUEST = 2;
    private TasksViewModelFactory factory;
    private TaskViewModel taskViewModel;
    private ReminderViewModel reminderViewModel;
    private TasksAdapter tasksAdapter;
    private ReminderAdapter reminderAdapter;
    private TextView taskText, reminderText;
    private Projects selectedProject;
    private SharedPreferences sharedPreferences;
    private ProjectsViewModelFactory projectFactory;
    private ProjectViewModel projectViewModel;
    private TextView taskList, reminderList;
    private DateTime clickedDateTime = null;

    //boolean flag to know if main FAB is in open or closed state.
    private boolean fabExpanded = false;


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
        markDaysThatHaveTask();
        onClickListener();
        onTouchListener();
        persianHorizontalExpCalendar = (PersianHorizontalExpCalendar) this.inflater.findViewById(R.id.persianCalendar);
        persianHorizontalExpCalendar.setTodayButtonTextSize(10);
        persianHorizontalExpCalendar.performClick();
        persianHorizontalExpCalendar.setPersianHorizontalExpCalListener(new PersianHorizontalExpCalendar.PersianHorizontalExpCalListener() {
            @Override
            public void onCalendarScroll(DateTime dateTime) {
                Log.i(TAG, "onCalendarScroll: " + dateTime.toString());
            }

            @Override
            public void onDateSelected(DateTime dateTime) {
                Log.i(TAG, "onDateSelected: " + dateTime.toString());
                /**
                 * update recycler view with select each day and show task that the chosen day is between start
                 * date and end date of it
                 */
                clickedDateTime = dateTime;
                if (taskList.getTag().equals("clicked")) {
                    initTaskRecyclerView();
                } else if (reminderList.getTag().equals("clicked")) {
                    initReminderRecyclerView();
                }

            }

            @Override
            public void onChangeViewPager(PersianViewPagerType persianViewPagerType) {
                Log.i(TAG, "onChangeViewPager: " + persianViewPagerType.name());
            }
        });

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabExpanded == true) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });


        return view;
    }

    private void initTaskRecyclerView() {
        if (clickedDateTime != null) {
            taskViewModel.getAllTasks().observe(CalenderFragment.this, new Observer<List<Tasks>>() {
                @Override
                public void onChanged(List<Tasks> tasks) {
                    List<Tasks> filteredTasks = new ArrayList<>();
                    for (Tasks task : tasks) {
                        if (Init.integerFormatFromStringDate(task.getTasks_startdate()) <= Init.integerFormatDate(clickedDateTime) &&
                                Init.integerFormatDate(clickedDateTime) <= Init.integerFormatFromStringDate(task.getTasks_enddate())) {
                            filteredTasks.add(task);
                        }
                    }
                    tasksAdapter.submitList(filteredTasks);
                    recyclerView.setAdapter(tasksAdapter);
                }
            });
        }
    }

    private void initReminderRecyclerView() {
        if (clickedDateTime != null) {
            reminderViewModel.getAllReminders().observe(CalenderFragment.this, new Observer<List<Reminders>>() {

                @Override
                public void onChanged(List<Reminders> reminders) {
                    List<Tasks> filteredTasks = new ArrayList<>();
                    for (Reminders reminder : reminders) {
//                    if (Init.integerFormatFromStringDate(reminder.getTasks_startdate()) <= Init.integerFormatDate(clickedDateTime) &&
//                            Init.integerFormatDate(clickedDateTime) <= Init.integerFormatFromStringDate(task.getTasks_enddate())) {
//                        filteredTasks.add(task);
//                    }
                    }
                    reminderAdapter.submitList(reminders);
                    recyclerView.setAdapter(reminderAdapter);
                }
            });
        }
    }

    private void onTouchListener() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Tasks selectedTask = tasksAdapter.getTaskAt(viewHolder.getAdapterPosition());
                SubTasksViewModelFactory subfactory = new SubTasksViewModelFactory(getActivity().getApplication(), selectedTask.getTasks_id());
                SubTasksViewModel subTasksViewModel = ViewModelProviders.of(getActivity(), subfactory).get(SubTasksViewModel.class);
                subTasksViewModel.getAllSubtasks().observeForever(new Observer<List<Subtasks>>() {
                    @Override
                    public void onChanged(List<Subtasks> subtasks) {
                        for (Subtasks subtask : subtasks) {
                            subTasksViewModel.delete(subtask);
                        }
                        taskViewModel.delete(selectedTask);
                    }
                });
                Projects projects = selectedProject;
                projects.setProjects_tasks_num(projects.getProjects_tasks_num() - 1);
                projects.setProject_id(selectedProject.getProject_id());
                //@TODO bellow function don't work
                projectViewModel.update(projects);
                persianHorizontalExpCalendar.updateMarks();

                Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteTask), Snackbar.LENGTH_LONG)
                        .show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void onClickListener() {
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditReminderActivity.class);
                startActivityForResult(intent, ADD_REMINDER_REQUEST);
            }
        });
        tasksAdapter.setOnItemClickListener(new TasksAdapter.TaskClickListener() {
            @Override
            public void switchContent(int subtaskConstarint, SubTaskFragment subTaskFragment) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(subtaskConstarint, subTaskFragment, subTaskFragment.toString());
                ft.addToBackStack(null);
                ft.commit();
            }

            @Override
            public void editTask(Tasks tasks) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("clickedTask", (Serializable) tasks);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }
        });
        taskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList.setTag("clicked");
                reminderList.setTag("unclicked");
                initTaskRecyclerView();
            }
        });
        reminderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList.setTag("unclicked");
                reminderList.setTag("clicked");
                initReminderRecyclerView();
            }
        });
    }

    /**
     * mark days that have task(day that are between start date and end date of the task)
     */
    private void markDaysThatHaveTask() {
        taskViewModel.getAllTasks().observe(CalenderFragment.this, new Observer<List<Tasks>>() {
            @Override
            public void onChanged(List<Tasks> tasks) {
                for (Tasks task : tasks) {
                    for (int i = Init.integerFormatFromStringDate(task.getTasks_startdate()); i <= Init.integerFormatFromStringDate(task.getTasks_enddate()); i++) {
                        markSomeDays(Init.convertIntegerToDateTime(i));
                    }
                }
            }
        });
    }

    private void init() {
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String projectJson = sharedPreferences.getString("selectedProject", "");
        selectedProject = gson.fromJson(projectJson, Projects.class);
        projectFactory = new ProjectsViewModelFactory(getActivity().getApplication(), selectedProject.getProject_id());
        projectViewModel = ViewModelProviders.of(this, projectFactory).get(ProjectViewModel.class);

        recyclerView = this.inflater.findViewById(R.id.recyclerView);
        addTaskBtn = this.inflater.findViewById(R.id.addTaskBtn);
        reminderList = this.inflater.findViewById(R.id.reminderList);
        taskList = this.inflater.findViewById(R.id.taskList);
        factory = new TasksViewModelFactory(getActivity().getApplication(), null);
        taskViewModel = ViewModelProviders.of(CalenderFragment.this, factory).get(TaskViewModel.class);
        reminderViewModel = ViewModelProviders.of(CalenderFragment.this).get(ReminderViewModel.class);
        tasksAdapter = new TasksAdapter(taskViewModel, getActivity(), getFragmentManager());
        reminderAdapter = new ReminderAdapter(getActivity(), reminderViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fab1 = this.inflater.findViewById(R.id.fab1);
        fab2 = this.inflater.findViewById(R.id.fab2);
        taskText = this.inflater.findViewById(R.id.taskText);
        reminderText = this.inflater.findViewById(R.id.reminderText);

        //Only main FAB is visible in the beginning
        closeSubMenusFab();
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

    public void markSomeDays(DateTime perChr) {
        this.persianHorizontalExpCalendar.markDate(new DateTime(perChr), PersianCustomMarks.SmallOval_Bottom, Color.RED).updateMarks();
    }

    public void cutomMarkTodaySelectedDay() {
        persianHorizontalExpCalendar
                .setMarkTodayCustomGradientDrawable(new CustomGradientDrawable(GradientDrawable.OVAL, new int[]{Color.parseColor("#55fefcea"), Color.parseColor("#55f1da36"), Color.parseColor("#55fefcea")})
                        .setstroke(2, Color.parseColor("#EFCF00"))
                        .setTextColor(Color.parseColor("#E88C02")))

                .setMarkSelectedDateCustomGradientDrawable(new CustomGradientDrawable(GradientDrawable.OVAL, new int[]{Color.parseColor("#55f3e2c7"), Color.parseColor("#55b68d4c"), Color.parseColor("#55e9d4b3")})
                        .setstroke(2, Color.parseColor("#E89314"))
                        .setTextColor(Color.parseColor("#E88C02")))
                .updateMarks();
    }

    private void closeSubMenusFab() {
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        taskText.setVisibility(View.GONE);
        reminderText.setVisibility(View.GONE);
        addTaskBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        fabExpanded = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.fab_margin));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.fab_margin2));
        taskText.setVisibility(View.VISIBLE);
        reminderText.setVisibility(View.VISIBLE);
        addTaskBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_white_close));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_OK) {
            Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertReminder), Snackbar.LENGTH_LONG)
                    .show();
        } else if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_CANCELED) {
        }
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            Projects projects = selectedProject;
            projects.setProjects_tasks_num(selectedProject.getProjects_tasks_num() + 1);
            projects.setProject_id(selectedProject.getProject_id());
            projectViewModel.update(projects);
            Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertTask), Snackbar.LENGTH_LONG)
                    .show();
        } else if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_CANCELED) {
            Tasks tasks = new Tasks("", 0, 0, 0,
                    selectedProject.getProject_id(), "", 0, 0,
                    "", "", 0, "");
            tasks.setTasks_id(sharedPreferences.getLong("tempTaskID", 0));
            taskViewModel.delete(tasks);
        }
        closeSubMenusFab();
    }
}
