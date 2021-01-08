package ir.android.taskroom.ui.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import ir.android.taskroom.utils.EnglishInit;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.calender.CalendarTool;
import ir.android.taskroom.utils.objects.TasksReminderActions;
import ir.android.taskroom.viewmodels.ProjectViewModel;
import ir.android.taskroom.viewmodels.ReminderViewModel;
import ir.android.taskroom.viewmodels.SubTasksViewModel;
import ir.android.taskroom.viewmodels.TaskViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class EnglishCalenderFragment extends Fragment {
    private static final int REQUEST_PERMISSION_CODE = 420;
    private View inflater;
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
    private CalendarView calendarView;
    private ArrayList<DateTime> taskDateList = new ArrayList<>();
    private ArrayList<DateTime> reminderDateList = new ArrayList<>();
    private ArrayList<DateTime> reminderTaskDateList = new ArrayList<>();

    private ArrayList<Reminders> reminderCalenderList = new ArrayList<>();

    //boolean flag to know if main FAB is in open or closed state.
    private boolean fabExpanded = false;
    private FrameLayout disableBackground;

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] CALENDAR_PROJECTION = new String[]{
            CalendarContract.Events._ID,                           // 0
            CalendarContract.Events.TITLE,                  // 1
            CalendarContract.Events.ORGANIZER,         // 2
            CalendarContract.Events.DTSTART,                  // 3
            CalendarContract.Events.RRULE             // 3
    };
    private List<EventDay> markEvents = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender_english_fragment, container, false);
        this.inflater = view;
        init();
        if (checkRequestPermission()) {
            readCalendar(getContext());
        } else {
            requestPermission();
        }
        markDaysThatHaveEvent();
        onClickListener();
        onTouchListener();
        Init.initShowCaseView(getContext(), this.inflater.findViewById(R.id.englishCalendar),
                getString(R.string.seeListOfTaskAndReminderInCalender), "firstCalenderGuide", null);

        calendarView.setHeaderVisibility(0);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar calendar = eventDay.getCalendar();
                List<Calendar> selectedDate = new ArrayList<>();
                selectedDate.add(calendar);
                calendarView.setSelectedDates(selectedDate);
                clickedDateTime = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), 0, 0);
                if (taskList.getTag().equals("clicked")) {
                    initTaskRecyclerView();
                } else if (reminderList.getTag().equals("clicked")) {
                    initReminderRecyclerView();
                }
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

    private void markDaysThatHaveEvent() {
        taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Tasks>>() {
            @Override
            public void onChanged(List<Tasks> tasks) {
                for (Tasks task : tasks) {
                    TasksReminderActions tasksReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(task, clickedDateTime, getResources());
                    ArrayList<DateTime> markedDateTime = tasksReminderActions.getDateTimesThatShouldMarkInCalender();

                    if (markedDateTime != null) {
                        for (DateTime dateTime : markedDateTime) {
                            DateTime newDateTime = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), 0, 0);
                            taskDateList.add(newDateTime);
                        }
                    }
                }
            }
        });
        reminderViewModel.getAllReminders().observe(getViewLifecycleOwner(), new Observer<List<Reminders>>() {

            @Override
            public void onChanged(List<Reminders> reminders) {
                for (Reminders reminder : reminders) {
                    TasksReminderActions tasksReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(reminder, clickedDateTime, getResources());
                    ArrayList<DateTime> markedDateTime = tasksReminderActions.getDateTimesThatShouldMarkInCalender();
                    if (markedDateTime != null) {
                        for (DateTime dateTime : markedDateTime) {
                            DateTime newDateTime = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), 0, 0);
                            reminderDateList.add(newDateTime);
                        }
                    }
                }
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isBoth;
                for (DateTime taskDateTime : taskDateList) {
                    isBoth = false;
                    for (DateTime reminderDateTime : reminderDateList) {
                        if (taskDateTime.equals(reminderDateTime)) {
                            reminderTaskDateList.add(taskDateTime);
                            isBoth = true;
                        }
                    }
                    if (!isBoth) {
                        markTaskDays(taskDateTime);
                    }
                }

                for (DateTime reminderDateTime : reminderDateList) {
                    isBoth = false;
                    for (DateTime taskDateTime : taskDateList) {
                        if (taskDateTime.equals(reminderDateTime)) {
                            reminderTaskDateList.add(taskDateTime);
                            isBoth = true;
                        }
                    }
                    if (!isBoth) {
                        markRemindersDays(reminderDateTime);
                    }
                }
                for (DateTime bothDateTime : reminderTaskDateList) {
                    markTaskRemindersDays(bothDateTime);
                }
            }
        }, 1000);
    }

    private void initTaskRecyclerView() {
        if (clickedDateTime != null) {
            DateTime tempClickDateTime = clickedDateTime;
            taskViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<Tasks>>() {
                @Override
                public void onChanged(List<Tasks> tasks) {
                    List<Tasks> filteredTasks = new ArrayList<>();
                    for (Tasks task : tasks) {
                        if (Integer.parseInt(task.getTasks_startdate().split("/")[0]) < 1500) {
                            CalendarTool calendarTool = new CalendarTool();
                            calendarTool.setGregorianDate(clickedDateTime.getYear(), clickedDateTime.getMonthOfYear(), clickedDateTime.getDayOfMonth());
                            clickedDateTime = new DateTime(Integer.parseInt(calendarTool.getIranianDate().split("/")[0]),
                                    Integer.parseInt(calendarTool.getIranianDate().split("/")[1]),
                                    Integer.parseInt(calendarTool.getIranianDate().split("/")[2]), 0, 0);
                        } else {
                            clickedDateTime = tempClickDateTime;
                        }
                        TasksReminderActions tasksReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(task, clickedDateTime, getResources());
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
            reminderViewModel.getAllReminders().observe(EnglishCalenderFragment.this, new Observer<List<Reminders>>() {

                @Override
                public void onChanged(List<Reminders> reminders) {
                    List<Reminders> filterReminders = new ArrayList<>();
                    for (Reminders reminder : reminderCalenderList) {
                        if (reminder.getReminders_crdate() / 10000000000L < 1500) {
                            CalendarTool calendarTool = new CalendarTool();
                            calendarTool.setGregorianDate(clickedDateTime.getYear(), clickedDateTime.getMonthOfYear(), clickedDateTime.getDayOfMonth());
                            clickedDateTime = new DateTime(Integer.parseInt(calendarTool.getIranianDate().split("/")[0]),
                                    Integer.parseInt(calendarTool.getIranianDate().split("/")[1]),
                                    Integer.parseInt(calendarTool.getIranianDate().split("/")[2]), 0, 0);
                        } else {
                            clickedDateTime = tempClickDateTime;
                        }
                        TasksReminderActions tasksReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(reminder, clickedDateTime, getResources());
                        if (tasksReminderActions.isInRecyclerView()) {
                            filterReminders.add(reminder);
                        }
                        clickedDateTime = tempClickDateTime;
                    }
                    for (Reminders reminder : reminders) {
                        if (reminder.getReminders_crdate() != null && reminder.getReminders_crdate() / 10000000 < 1500) {
                            CalendarTool calendarTool = new CalendarTool();
                            calendarTool.setGregorianDate(clickedDateTime.getYear(), clickedDateTime.getMonthOfYear(), clickedDateTime.getDayOfMonth());
                            clickedDateTime = new DateTime(Integer.parseInt(calendarTool.getIranianDate().split("/")[0]),
                                    Integer.parseInt(calendarTool.getIranianDate().split("/")[1]),
                                    Integer.parseInt(calendarTool.getIranianDate().split("/")[2]), 0, 0);
                        } else {
                            clickedDateTime = tempClickDateTime;
                        }
                        TasksReminderActions tasksReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(reminder, clickedDateTime, getResources());
                        if (tasksReminderActions.isInRecyclerView()) {
                            filterReminders.add(reminder);
                        }
                        clickedDateTime = tempClickDateTime;
                    }
                    reminderAdapter.submitList(filterReminders);
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
                reminderList.setBackground(getContext().getResources().getDrawable(R.drawable.unselected_left_corner_button_theme_english));
                initTaskRecyclerView();
            }
        });
        reminderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList.setTag("unclicked");
                reminderList.setTag("clicked");
                taskList.setBackground(getContext().getResources().getDrawable(R.drawable.unselected_right_corner_button_theme_english));
                taskList.setTextColor(getContext().getResources().getColor(R.color.black));
                Init.setBackgroundLeftHeaderButton(getContext(), reminderList);
                initReminderRecyclerView();
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
        calendarView = this.inflater.findViewById(R.id.englishCalendar);
        recyclerView = this.inflater.findViewById(R.id.recyclerView);
        addTaskBtn = this.inflater.findViewById(R.id.addTaskBtn);
        reminderList = this.inflater.findViewById(R.id.reminderList);
        taskList = this.inflater.findViewById(R.id.taskList);
        Init.setBackgroundRightHeaderButton(sharedPreferences, getContext(), taskList);
        factory = new TasksViewModelFactory(getActivity().getApplication(), null);
        taskViewModel = ViewModelProviders.of(EnglishCalenderFragment.this, factory).get(TaskViewModel.class);
        reminderViewModel = ViewModelProviders.of(EnglishCalenderFragment.this).get(ReminderViewModel.class);
        tasksAdapter = new TasksAdapter(taskViewModel, getActivity(), getFragmentManager());
        reminderAdapter = new ReminderAdapter(getActivity(), reminderViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fab1 = this.inflater.findViewById(R.id.fab1);
        fab2 = this.inflater.findViewById(R.id.fab2);
        taskText = this.inflater.findViewById(R.id.taskText);
        reminderText = this.inflater.findViewById(R.id.reminderText);
        clickedDateTime = EnglishInit.getCurrentDateWhitoutTime();
        disableBackground = inflater.findViewById(R.id.disableBackground);

        if (!checkRequestPermission()) {
            requestPermission();
        }

        //Only main FAB is visible in the beginning
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


    public void markTaskDays(DateTime perChr) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, perChr.getYear());
        calendar.set(Calendar.MONTH, perChr.getMonthOfYear() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, perChr.getDayOfMonth());
        markEvents.add(new EventDay(calendar, R.drawable.ic_mark_task));
        calendarView.setEvents(markEvents);
    }

    public void markRemindersDays(DateTime perChr) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, perChr.getYear());
        calendar.set(Calendar.MONTH, perChr.getMonthOfYear() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, perChr.getDayOfMonth());
        markEvents.add(new EventDay(calendar, R.drawable.ic_mark_reminder));
        calendarView.setEvents(markEvents);
    }

    public void markTaskRemindersDays(DateTime perChr) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, perChr.getYear());
        calendar.set(Calendar.MONTH, perChr.getMonthOfYear() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, perChr.getDayOfMonth());
        markEvents.add(new EventDay(calendar, R.drawable.ic_mark_reminder_task));
        calendarView.setEvents(markEvents);
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
            snackbar.show();
        }
        closeSubMenusFab();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
                        System.out.println("rRule = " + rRule);
                        System.out.println("displayName = " + displayName);
                        String[] rRules = rRule.split(";");
                        Map<String, String> rRuleMap = new HashMap<>();
                        for(String rule: rRules){
                            rRuleMap.put(rule.split("=")[0].trim(), rule.split("=")[1].trim());
                        }
                        String repeatValue = rRuleMap.get("FREQ");
                        if(rRuleMap.get("BYDAY") == null){
                            if (repeatValue.equals(getString(R.string.DAILY))) {
                                reminderRepeatDay.append(getString(R.string.daily));
                            } else if (repeatValue.equals(getString(R.string.WEEKLY))) {
                                reminderRepeatDay.append(getString(R.string.weekly));
                            } else if (repeatValue.equals(getString(R.string.MONTHLY))) {
                                reminderRepeatDay.append(getString(R.string.monthly));
                            } else if (repeatValue.equals(getString(R.string.YEARLY))) {
                                reminderRepeatDay.append(getString(R.string.yearly));
                            }
                        } else {
                            String[] repeatDays = rRuleMap.get("BYDAY").split(",");
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
                            if(reminderRepeatDay.lastIndexOf(",") == reminderRepeatDay.length() - 1){
                                reminderRepeatDay = reminderRepeatDay.deleteCharAt(reminderRepeatDay.length() - 1);
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
                                    + (second < 10 ? "0" + second : second), 0, displayName, reminderRepeatDay.toString(), 0, 1, 0, "", false);
                    reminders.setReminders_id(id);
                    reminderDateList.add(new DateTime(year, month, day, 0, 0));
                    reminders.setReminders_crdate(Long.parseLong(year + "" +
                            (month < 10 ? "0" + month : month) + ""
                            + (day < 10 ? "0" + day : day) + ""
                            + (hour < 10 ? "0" + hour : hour) + ""
                            + (minute < 10 ? "0" + minute : minute) + ""
                            + (second < 10 ? "0" + second : second)));
                    reminderCalenderList.add(reminders);
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
