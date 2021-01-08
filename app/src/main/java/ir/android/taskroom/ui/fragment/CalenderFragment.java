package ir.android.taskroom.ui.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianCustomMarks;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianViewPagerType;
import com.mohamadian.persianhorizontalexpcalendar.model.CustomGradientDrawable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.data.db.entity.Reminders;
import ir.android.taskroom.data.db.entity.Subtasks;
import ir.android.taskroom.data.db.entity.Tasks;
import ir.android.taskroom.data.db.factory.ProjectsViewModelFactory;
import ir.android.taskroom.data.db.factory.SubTasksViewModelFactory;
import ir.android.taskroom.data.db.factory.TasksViewModelFactory;
import ir.android.taskroom.ui.activity.reminder.AddEditReminderActivity;
import ir.android.taskroom.ui.activity.task.AddEditTaskActivity;
import ir.android.taskroom.ui.adapters.ReminderAdapter;
import ir.android.taskroom.ui.adapters.TasksAdapter;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.calender.CalendarTool;
import ir.android.taskroom.utils.objects.TasksReminderActions;
import ir.android.taskroom.viewmodels.ProjectViewModel;
import ir.android.taskroom.viewmodels.ReminderViewModel;
import ir.android.taskroom.viewmodels.SubTasksViewModel;
import ir.android.taskroom.viewmodels.TaskViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CalenderFragment extends Fragment {
    private static final String TAG = "TAG";
    private View inflater;
    private PersianHorizontalExpCalendar persianHorizontalExpCalendar;
    private RecyclerView recyclerView;
    private FloatingActionButton addTaskBtn;
    private LinearLayout fab1, fab2;
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    public static final int ADD_REMINDER_REQUEST = 3;
    public static final int EDIT_REMINDER_REQUEST = 4;
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
    private FrameLayout disableBackground;
    private static final int REQUEST_PERMISSION_CODE = 420;
    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Events._ID,                           // 0
            CalendarContract.Events.TITLE,                  // 1
            CalendarContract.Events.ORGANIZER,         // 2
            CalendarContract.Events.DTSTART,                  // 3
            CalendarContract.Events.RRULE                  // 3
    };
    private ArrayList<Reminders> reminderCalenderList = new ArrayList<>();


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
        markDaysThatHaveReminder();
        onClickListener();
        onTouchListener();
        persianHorizontalExpCalendar.setPersianHorizontalExpCalListener(new PersianHorizontalExpCalendar.PersianHorizontalExpCalListener() {
            @Override
            public void onCalendarScroll(DateTime dateTime) {
                Log.i(TAG, "onCalendarScroll: " + dateTime.toString());
            }

            @Override
            public void onDateSelected(DateTime dateTime) {
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
            }
        });

        if (checkRequestPermission()) {
            readCalendar(getContext());
        } else {
            requestPermission();
        }
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

    private void markDaysThatHaveReminder() {
        reminderViewModel.getAllReminders().observe(getViewLifecycleOwner(), new Observer<List<Reminders>>() {

            @Override
            public void onChanged(List<Reminders> reminders) {
                for (Reminders reminder : reminders) {
                    TasksReminderActions tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(reminder, clickedDateTime, getResources());
                    ArrayList<DateTime> markedDateTime = tasksReminderActions.getDateTimesThatShouldMarkInCalender();

                    if (markedDateTime != null) {
                        for (DateTime dateTime : markedDateTime) {
                            markVerticalSomeDays(dateTime);
                        }
                    }
                }
            }
        });
    }

    private void initTaskRecyclerView() {
        if (clickedDateTime != null) {
            DateTime tempClickDateTime = clickedDateTime;
            taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Tasks>>() {
                @Override
                public void onChanged(List<Tasks> tasks) {
                    List<Tasks> filteredTasks = new ArrayList<>();
                    for (Tasks task : tasks) {
                        if (Integer.parseInt(task.getTasks_startdate().split("/")[0]) > 2000) {
                            CalendarTool calendarTool = new CalendarTool();
                            calendarTool.setIranianDate(clickedDateTime.getYear(), clickedDateTime.getMonthOfYear(), clickedDateTime.getDayOfMonth());
                            clickedDateTime = new DateTime(Integer.parseInt(calendarTool.getGregorianDate().split("/")[0]),
                                    Integer.parseInt(calendarTool.getGregorianDate().split("/")[1]),
                                    Integer.parseInt(calendarTool.getGregorianDate().split("/")[2]), 0, 0);
                        } else {
                            clickedDateTime = tempClickDateTime;
                        }

                        TasksReminderActions tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(task, clickedDateTime, getResources());
                        if (tasksReminderActions.isInRecyclerView()) {
                            filteredTasks.add(task);
                        }
                        clickedDateTime = tempClickDateTime;
                    }
                    tasksAdapter.submitList(filteredTasks);
                    recyclerView.setAdapter(tasksAdapter);
                }
            });
        }
    }

    private void initReminderRecyclerView() {
        if (clickedDateTime != null) {
            DateTime tempClickDateTime = clickedDateTime;
            reminderViewModel.getAllReminders().observe(CalenderFragment.this, new Observer<List<Reminders>>() {

                @Override
                public void onChanged(List<Reminders> reminders) {
                    List<Reminders> filterReminders = new ArrayList<>();
                    for (Reminders reminder : reminderCalenderList) {
                        if (reminder.getReminders_crdate() / 10000000000L > 2000) {
                            CalendarTool calendarTool = new CalendarTool();
                            calendarTool.setIranianDate(clickedDateTime.getYear(), clickedDateTime.getMonthOfYear(), clickedDateTime.getDayOfMonth());
                            clickedDateTime = new DateTime(Integer.parseInt(calendarTool.getGregorianDate().split("/")[0]),
                                    Integer.parseInt(calendarTool.getGregorianDate().split("/")[1]),
                                    Integer.parseInt(calendarTool.getGregorianDate().split("/")[2]), 0, 0);
                        } else {
                            clickedDateTime = tempClickDateTime;
                        }
                        TasksReminderActions tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(reminder, clickedDateTime, getResources());
                        if (tasksReminderActions.isInRecyclerView()) {
                            filterReminders.add(reminder);
                        }
                        clickedDateTime = tempClickDateTime;
                    }
                    for (Reminders reminder : reminders) {
                        if (reminder.getReminders_crdate() / 10000000000L > 2000) {
                            CalendarTool calendarTool = new CalendarTool();
                            calendarTool.setIranianDate(clickedDateTime.getYear(), clickedDateTime.getMonthOfYear(), clickedDateTime.getDayOfMonth());
                            clickedDateTime = new DateTime(Integer.parseInt(calendarTool.getGregorianDate().split("/")[0]),
                                    Integer.parseInt(calendarTool.getGregorianDate().split("/")[1]),
                                    Integer.parseInt(calendarTool.getGregorianDate().split("/")[2]), 0, 0);
                        } else {
                            clickedDateTime = tempClickDateTime;
                        }
                        TasksReminderActions tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(reminder, clickedDateTime, getResources());
                        if (tasksReminderActions.isInRecyclerView()) {
                            filterReminders.add(reminder);
                        }
                        clickedDateTime = tempClickDateTime;
                    }
                    reminderAdapter.submitList(filterReminders);
                    recyclerView.setAdapter(reminderAdapter);
                    clickedDateTime = tempClickDateTime;
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
                if (taskList.getTag().equals("clicked")) {
                    Tasks selectedTask = tasksAdapter.getTaskAt(viewHolder.getAdapterPosition());
                    SubTasksViewModelFactory subfactory = new SubTasksViewModelFactory(getActivity().getApplication(), selectedTask.getTasks_id());
                    SubTasksViewModel subTasksViewModel = ViewModelProviders.of(getActivity(), subfactory).get(SubTasksViewModel.class);
                    subTasksViewModel.getAllTasksSubtasks().observe(getViewLifecycleOwner(), new Observer<List<Subtasks>>() {
                        @Override
                        public void onChanged(List<Subtasks> subtasks) {
                            for (Subtasks subtask : subtasks) {
                                subTasksViewModel.delete(subtask);
                            }
                            if (selectedTask.getWork_id().contains(",")) {
                                for (String requestId : selectedTask.getWork_id().split(",")) {
                                    WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(requestId));
                                }
                            } else {
                                if (!selectedTask.getWork_id().equals("0") && !selectedTask.getWork_id().equals("-2")) {
                                    WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(selectedTask.getWork_id()));
                                }
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
                    if (selectedTask.getWork_id().contains(",")) {
                        for (String requestId : selectedTask.getWork_id().split(",")) {
                            WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(requestId));
                        }
                    } else {
                        if (!selectedTask.getWork_id().equals("0") && !selectedTask.getWork_id().equals("-2")) {
                            WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(selectedTask.getWork_id()));
                        }
                    }
                    Snackbar snackbar = Snackbar
                            .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteTask), Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    snackbar.show();
                } else if (reminderList.getTag().equals("clicked")) {
                    Reminders selectedReminder = reminderAdapter.getReminderAt(viewHolder.getAdapterPosition());
                    if (selectedReminder.getReminders_id() > 1000) {
                        deleteCalendarEntry(selectedReminder.getReminders_id());
                    } else {
                        reminderViewModel.delete(selectedReminder);
                        if (selectedReminder.getWork_id().contains(",")) {
                            for (String requestId : selectedReminder.getWork_id().split(",")) {
                                WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(requestId));
                            }
                        } else if (!selectedReminder.getWork_id().equals("0")) {
                            WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(selectedReminder.getWork_id()));
                        }
                    }
                    Snackbar snackbar = Snackbar
                            .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteReminder), Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    snackbar.show();
                }
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
                intent.putExtra("calenderClickedDate", clickedDateTime.toString());
                startActivityForResult(intent, ADD_REMINDER_REQUEST);
            }
        });
        tasksAdapter.setOnItemClickListener(new TasksAdapter.TaskClickListener() {
            @Override
            public void switchContent(int subtaskConstarint, SubTaskFragment subTaskFragment) {
                //@TODO
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(subtaskConstarint, subTaskFragment, subTaskFragment.toString());
//                ft.addToBackStack(null);
//                ft.commit();
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
        reminderAdapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Reminders reminders) {
                Intent intent = new Intent(getActivity(), AddEditReminderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("clickedReminder", (Serializable) reminders);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_REMINDER_REQUEST);
            }
        });
        taskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList.setTag("clicked");
                Init.setBackgroundRightHeaderButton(sharedPreferences, getContext(), taskList);
                reminderList.setTag("unclicked");
                reminderList.setTextColor(getContext().getResources().getColor(R.color.black));
                reminderList.setBackground(getContext().getResources().getDrawable(R.drawable.unselected_left_corner_button_theme1));
                initTaskRecyclerView();
            }
        });
        reminderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList.setTag("unclicked");
                reminderList.setTag("clicked");
                taskList.setBackground(getContext().getResources().getDrawable(R.drawable.unselected_right_corner_button_theme1));
                taskList.setTextColor(getContext().getResources().getColor(R.color.black));
                Init.setBackgroundLeftHeaderButton(getContext(), reminderList);
                initReminderRecyclerView();
            }
        });
    }

    /**
     * mark days that have task(day that are between start date and end date of the task)
     */
    private void markDaysThatHaveTask() {
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Tasks>>() {
            @Override
            public void onChanged(List<Tasks> tasks) {
                for (Tasks task : tasks) {
                    TasksReminderActions tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(task, clickedDateTime, getResources());
                    ArrayList<DateTime> markedDateTime = tasksReminderActions.getDateTimesThatShouldMarkInCalender();

                    if (markedDateTime != null) {
                        for (DateTime dateTime : markedDateTime) {
                            markSomeDays(dateTime);
                        }
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
        projectFactory = new ProjectsViewModelFactory(getActivity().getApplication(), selectedProject == null ? null : selectedProject.getProject_id());
        projectViewModel = ViewModelProviders.of(this, projectFactory).get(ProjectViewModel.class);

        recyclerView = this.inflater.findViewById(R.id.recyclerView);
        addTaskBtn = this.inflater.findViewById(R.id.addTaskBtn);
        reminderList = this.inflater.findViewById(R.id.reminderList);
        taskList = this.inflater.findViewById(R.id.taskList);
        Init.setBackgroundRightHeaderButton(sharedPreferences, getContext(), taskList);
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
        clickedDateTime = Init.getCurrentDateWhitoutTime();
        disableBackground = inflater.findViewById(R.id.disableBackground);

        Init.initShowCaseView(getContext(), this.inflater.findViewById(R.id.persianCalendar),
                getString(R.string.seeListOfTaskAndReminderInCalender), "firstCalenderGuide", null);
        persianHorizontalExpCalendar = (PersianHorizontalExpCalendar) this.inflater.findViewById(R.id.persianCalendar);
        persianHorizontalExpCalendar.setTodayButtonTextSize(10);
        persianHorizontalExpCalendar.performClick();

        if (!checkRequestPermission()) {
            requestPermission();
        }

        //Only main FAB is visible in the beginning
        closeSubMenusFab();
    }


    public void markSomeDays(DateTime perChr) {
        this.persianHorizontalExpCalendar.markDate(new DateTime(perChr), PersianCustomMarks.SmallOval_Bottom, Color.RED).updateMarks();
    }

    public void markVerticalSomeDays(DateTime perChr) {
        this.persianHorizontalExpCalendar.markDate(new DateTime(perChr), PersianCustomMarks.VerticalLine_Right, Color.YELLOW).updateMarks();
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
        disableBackground.setVisibility(View.INVISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        animation.setDuration(1000);
        disableBackground.startAnimation(animation);
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        fabExpanded = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.fab_margin1));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.fab_margin2));
        taskText.setVisibility(View.VISIBLE);
        reminderText.setVisibility(View.VISIBLE);
        addTaskBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_white_close));
        disableBackground.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        animation.setDuration(1000);
        disableBackground.startAnimation(animation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_OK) {
            Snackbar snackbar = Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertReminder), Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            snackbar.show();
        } else if (requestCode == ADD_REMINDER_REQUEST && resultCode == RESULT_CANCELED) {
            Reminders reminders = new Reminders(0, "", "",
                    0, "", "", 0, 0, 1, "", false);

            reminders.setReminders_id(sharedPreferences.getLong("tempReminderID", 0));
            reminderViewModel.delete(reminders);
        }
        if (requestCode == EDIT_REMINDER_REQUEST && resultCode == RESULT_OK) {
            Snackbar snackbar = Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successEditReminder), Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            snackbar.show();
            reminderAdapter.notifyDataSetChanged();
        }
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            Projects projects = selectedProject;
            projects.setProjects_tasks_num(selectedProject.getProjects_tasks_num() + 1);
            projects.setProject_id(selectedProject.getProject_id());
            projectViewModel.update(projects);
            Snackbar snackbar = Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertTask), Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            snackbar.show();
        } else if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_CANCELED) {
            if (selectedProject != null) {
                Tasks tasks = new Tasks("", 0, 0, 0,
                        selectedProject.getProject_id(), "", 0, 0,
                        "", "", 0, "", "", false, "");
                tasks.setTasks_id(sharedPreferences.getLong("tempTaskID", 0));
                taskViewModel.delete(tasks);
            }
        }
        if (requestCode == EDIT_TASK_REQUEST && resultCode == RESULT_OK) {
            tasksAdapter.notifyDataSetChanged();
            Snackbar snackbar = Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successEditTask), Snackbar.LENGTH_LONG);
            ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            snackbar.show();
        }
        closeSubMenusFab();
    }

    private boolean checkRequestPermission() {
        int read_calendar = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR);
        int write_calendar = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR);
        return read_calendar == PackageManager.PERMISSION_GRANTED && write_calendar == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Fragment fragment = getFragmentManager().findFragmentById(R.id.calendarFragment);
                    final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(fragment);
                    fragmentTransaction.attach(fragment);
                    fragmentTransaction.commit();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.permissionDenied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void readCalendar(Context context) {

        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.EventsEntity.CONTENT_URI;
        String selection = "(("
                + CalendarContract.Events.ORGANIZER + " = ? ) or (" + CalendarContract.Events.ORGANIZER + " like '%@gmail.com'))";
        String[] selectionArgs = new String[]{"Phone"};

// Submit the query and get a Cursor object back.
        cursor = contentResolver.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);
        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Long id = 1000 + Long.parseLong(cursor.getString(0));
                    String displayName = cursor.getString(1);
                    String accountName = cursor.getString(2);
                    String date = cursor.getString(3);
                    String rRule = cursor.getString(4);
                    StringBuilder reminderRepeatDay = new StringBuilder();
                    if (rRule != null && !rRule.isEmpty()) {
                        String repeatValue = ((rRule.split(";").length > 2 ? (rRule.split(";")[2])
                                : (rRule.split(";")[0])).split("=")[1]);
                        String[] repeatDays = repeatValue.contains(",") ? repeatValue.split(",") : new String[0];
                        if (repeatValue.contains(",")) {
                            for (String repeatDay : repeatDays) {
                                if (repeatDay.equals(getString(R.string.sa))) {
                                    reminderRepeatDay.append(getString(R.string.saterday)).append(",");
                                } else if (repeatDay.equals(getString(R.string.su))) {
                                    reminderRepeatDay.append(getString(R.string.sunday)).append(",");
                                } else if (repeatDay.equals(getString(R.string.mo))) {
                                    reminderRepeatDay.append(getString(R.string.monday)).append(",");
                                } else if (repeatDay.equals(getString(R.string.tu))) {
                                    reminderRepeatDay.append(getString(R.string.tuesday)).append(",");
                                } else if (repeatDay.equals(getString(R.string.we))) {
                                    reminderRepeatDay.append(getString(R.string.wednesday)).append(",");
                                } else if (repeatDay.equals(getString(R.string.th))) {
                                    reminderRepeatDay.append(getString(R.string.thursday)).append(",");
                                } else if (repeatDay.equals(getString(R.string.fr))) {
                                    reminderRepeatDay.append(getString(R.string.friday)).append(",");
                                }
                            }
                        } else {
                            if (repeatValue.equals(getString(R.string.DAILY))) {
                                reminderRepeatDay.append(getString(R.string.daily));
                            } else if (repeatValue.equals(getString(R.string.WEEKLY))) {
                                reminderRepeatDay.append(getString(R.string.weekly));
                            } else if (repeatValue.equals(getString(R.string.MONTHLY))) {
                                reminderRepeatDay.append(getString(R.string.monthly));
                            } else if (repeatValue.equals(getString(R.string.YEARLY))) {
                                reminderRepeatDay.append(getString(R.string.yearly));
                            }
                        }
                    }

                    // Create a calendar object that will convert the date and time value in milliseconds to date.
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(date));
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    Reminders reminders = new Reminders(0, "",
                            (hour < 10 ? "0" + hour : hour) + ":"
                                    + (minute < 10 ? "0" + minute : minute) + ":"
                                    + (second < 10 ? "0" + second : second), 0, displayName, reminderRepeatDay.toString(), 0, 1, 0, "0", false);
                    reminders.setReminders_id(id);
                    CalendarTool calendarTool = new CalendarTool();
                    calendarTool.setGregorianDate(year, month, day);
                    int persianYear = Integer.parseInt(calendarTool.getIranianDate().split("/")[0]);
                    int persianMonth = Integer.parseInt(calendarTool.getIranianDate().split("/")[1]);
                    int persianDay = Integer.parseInt(calendarTool.getIranianDate().split("/")[2]);
                    reminders.setReminders_crdate(Long.parseLong(persianYear + "" +
                            (persianMonth < 10 ? "0" + persianMonth : persianMonth) + ""
                            + (persianDay < 10 ? "0" + persianDay : persianDay) + ""
                            + (hour < 10 ? "0" + hour : hour) + ""
                            + (minute < 10 ? "0" + minute : minute) + ""
                            + (second < 10 ? "0" + second : second)));
                    reminderCalenderList.add(reminders);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            markVerticalSomeDays(new DateTime(persianYear, persianMonth, persianDay, 0, 0));

                        }
                    }, 1500);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int deleteCalendarEntry(long entryID) {
        int iNumRowsDeleted = 0;

        Uri eventUri = ContentUris
                .withAppendedId(CalendarContract.Events.CONTENT_URI, entryID - 1000);
        iNumRowsDeleted = getContext().getContentResolver().delete(eventUri, null, null);
        return iNumRowsDeleted;
    }
}
