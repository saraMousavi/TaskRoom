package ir.android.taskroom.ui.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.view.ViewCompat;
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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import ir.android.taskroom.ui.activity.RecordingActivity;
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
    private static final String TAG = "TAG";
    private static final int MY_CAL_WRITE_REQ = 120;
    private static final int MY_CAL_REQ = 220;
    private static final int REQUEST_PERMISSION_CODE = 420;
    private View inflater;
    private RecyclerView recyclerView;
    private FloatingActionButton addTaskBtn;
    private LinearLayout fab1, fab2;
    private CollapsingToolbarLayout toolBarLayout;
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
            CalendarContract.Events.EVENT_LOCATION                  // 3
    };
    private boolean dateHasTask = false;
    private boolean dateHasReminder = false;


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
                            dateHasTask = true;
                            markSomeDays(dateTime);
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
                            dateHasReminder = true;
                            markSomeDays(dateTime);
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
                        if (reminder.getReminders_crdate() / 10000000 < 1500) {
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
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    snackbar.show();
                } else if (reminderList.getTag().equals("clicked")) {
                    Reminders selectedReminder = reminderAdapter.getReminderAt(viewHolder.getAdapterPosition());
                    reminderViewModel.delete(selectedReminder);
                    if (selectedReminder.getWork_id().contains(",")) {
                        for (String requestId : selectedReminder.getWork_id().split(",")) {
                            WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(requestId));
                        }
                    } else if (!selectedReminder.getWork_id().equals("0")) {
                        WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(selectedReminder.getWork_id()));
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


    public void markSomeDays(DateTime perChr) {
        List<EventDay> markEvents = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, perChr.getYear());
        calendar.set(Calendar.MONTH, perChr.getMonthOfYear() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, perChr.getDayOfMonth());
        if (dateHasTask && dateHasReminder) {
            markEvents.add(new EventDay(calendar, R.drawable.ic_mark_reminder_task));
        } else if (dateHasReminder) {
            markEvents.add(new EventDay(calendar, R.drawable.ic_mark_reminder));
        } else if (dateHasTask) {
            markEvents.add(new EventDay(calendar, R.drawable.ic_mark_task));
        }


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

    public void addCalendar() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, "cal@zoftino.com");
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, "cal.zoftino.com");
        contentValues.put(CalendarContract.Calendars.NAME, "zoftino calendar");
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Zoftino.com Calendar");
        contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, "232323");
        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, "cal@zoftino.com");
        contentValues.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "METHOD_ALERT, METHOD_EMAIL, METHOD_ALARM");
        contentValues.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "TYPE_OPTIONAL, TYPE_REQUIRED, TYPE_RESOURCE");
        contentValues.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "AVAILABILITY_BUSY, AVAILABILITY_FREE, AVAILABILITY_TENTATIVE");


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, MY_CAL_WRITE_REQ);
        }

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        uri = uri.buildUpon().appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "cal@zoftino.com")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, "cal.zoftino.com").build();
        getContext().getContentResolver().insert(uri, contentValues);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("requestCode = " + requestCode);
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
        String myDate = "09/01/2019";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date1.getTime();
        String selection = "(("
                + CalendarContract.Events.ORGANIZER + " = ? ) or (" + CalendarContract.Events.ORGANIZER + " like '%@gmail.com'))";
        System.out.println("millis = " + millis);
        String[] selectionArgs = new String[]{"Phone"};

// Submit the query and get a Cursor object back.
        cursor = contentResolver.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, null);

        try {
            System.out.println("Count=" + cursor.getCount());
            if (cursor.getCount() > 0) {
                System.out.println("the control is just inside of the cursor.count loop");
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    String accountName = cursor.getString(2);
                    String date = cursor.getString(3);
                    String type = cursor.getString(4);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

                    // Create a calendar object that will convert the date and time value in milliseconds to date.
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(date));
                    Reminders reminders = new Reminders(0, "",
                            calendar.get(Calendar.HOUR_OF_DAY) +
                                    ":" + calendar.get(Calendar.MINUTE)
                                    + ":" + calendar.get(Calendar.SECOND), 0, displayName, "", 0, 1, 0, "", false);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    reminders.setReminders_crdate(Long.parseLong(year + "" +
                            (month < 10 ? "0" + month : month) + ""
                            + (day < 10 ? "0" + day : day) + ""
                            + (hour < 10 ? "0" + hour : hour) + ""
                            + (minute < 10 ? "0" + minute : minute) + ""
                            + (second < 10 ? "0" + second : second)));
                    reminderCalenderList.add(reminders);
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // For each calendar, display all the events from the previous week to the end of next week.
//        for (String id : calendarIds) {
//            Uri eventUri = CalendarContract.Events.CONTENT_URI;
//            Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
//            //Uri.Builder builder = Uri.parse("content://com.android.calendar/calendars").buildUpon();
//            long now = new Date().getTime();
//
//            ContentUris.appendId(builder, now - DateUtils.DAY_IN_MILLIS * 10000);
//            ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS * 10000);
//            String eventSelection = "((" + CalendarContract.Events.ACCOUNT_NAME + " = ?))";
//
//            String[] eventselectionArgs = new String[]{"sara.mousavi.90@gmail.com"};
//            Cursor eventCursor = contentResolver.query(eventUri,
//                    EVENT_PROJECTION, eventSelection,
//                    eventselectionArgs, null);
//
//            System.out.println("eventCursor count=" + eventCursor.getCount());
//            if (eventCursor.getCount() > 0) {
//
//                if (eventCursor.moveToFirst()) {
//                    do {
//                        Object mbeg_date, beg_date, beg_time, end_date, end_time;
//
//                        final Integer _id = eventCursor.getInt(0);
//                        final String title = eventCursor.getString(1);
////                        final Date begin = new Date(eventCursor.getLong(1));
////                        final Date end = new Date(eventCursor.getLong(2));
////                        final Boolean allDay = !eventCursor.getString(3).equals("0");
//
//                        /*  System.out.println("Title: " + title + " Begin: " + begin + " End: " + end +
//                                    " All Day: " + allDay);
//                        */
//                        System.out.println("ID:" + _id);
//                        System.out.println("Title:" + title);
////                        System.out.println("Begin:" + begin);
////                        System.out.println("End:" + end);
////                        System.out.println("All Day:" + allDay);
//
//                        /* the calendar control metting-begin events Respose  sub-string (starts....hare) */
//
////                        Pattern p = Pattern.compile(" ");
////                        String[] items = p.split(begin.toString());
////                        String scalendar_metting_beginday, scalendar_metting_beginmonth, scalendar_metting_beginyear, scalendar_metting_begindate, scalendar_metting_begintime, scalendar_metting_begingmt;
////
////                        scalendar_metting_beginday = items[0];
////                        scalendar_metting_beginmonth = items[1];
////                        scalendar_metting_begindate = items[2];
////                        scalendar_metting_begintime = items[3];
////                        scalendar_metting_begingmt = items[4];
////                        scalendar_metting_beginyear = items[5];
////
////
////                        String calendar_metting_beginday = scalendar_metting_beginday;
////                        String calendar_metting_beginmonth = scalendar_metting_beginmonth.toString().trim();
////
////                        int calendar_metting_begindate = Integer.parseInt(scalendar_metting_begindate.trim());
////
////                        String calendar_metting_begintime = scalendar_metting_begintime.toString().trim();
////                        String calendar_metting_begingmt = scalendar_metting_begingmt;
////                        int calendar_metting_beginyear = Integer.parseInt(scalendar_metting_beginyear.trim());
////
////
////                        System.out.println("calendar_metting_beginday=" + calendar_metting_beginday);
////
////                        System.out.println("calendar_metting_beginmonth =" + calendar_metting_beginmonth);
////
////                        System.out.println("calendar_metting_begindate =" + calendar_metting_begindate);
////
////                        System.out.println("calendar_metting_begintime=" + calendar_metting_begintime);
////
////                        System.out.println("calendar_metting_begingmt =" + calendar_metting_begingmt);
////
////                        System.out.println("calendar_metting_beginyear =" + calendar_metting_beginyear);
////
////                        /* the calendar control metting-begin events Respose  sub-string (starts....ends) */
////
////                        /* the calendar control metting-end events Respose  sub-string (starts....hare) */
////
////                        Pattern p1 = Pattern.compile(" ");
////                        String[] enditems = p.split(end.toString());
////                        String scalendar_metting_endday, scalendar_metting_endmonth, scalendar_metting_endyear, scalendar_metting_enddate, scalendar_metting_endtime, scalendar_metting_endgmt;
////
////                        scalendar_metting_endday = enditems[0];
////                        scalendar_metting_endmonth = enditems[1];
////                        scalendar_metting_enddate = enditems[2];
////                        scalendar_metting_endtime = enditems[3];
////                        scalendar_metting_endgmt = enditems[4];
////                        scalendar_metting_endyear = enditems[5];
////
////
////                        String calendar_metting_endday = scalendar_metting_endday;
////                        String calendar_metting_endmonth = scalendar_metting_endmonth.toString().trim();
////
////                        int calendar_metting_enddate = Integer.parseInt(scalendar_metting_enddate.trim());
////
////                        String calendar_metting_endtime = scalendar_metting_endtime.toString().trim();
////                        String calendar_metting_endgmt = scalendar_metting_endgmt;
////                        int calendar_metting_endyear = Integer.parseInt(scalendar_metting_endyear.trim());
////
////
////                        System.out.println("calendar_metting_beginday=" + calendar_metting_endday);
////
////                        System.out.println("calendar_metting_beginmonth =" + calendar_metting_endmonth);
////
////                        System.out.println("calendar_metting_begindate =" + calendar_metting_enddate);
////
////                        System.out.println("calendar_metting_begintime=" + calendar_metting_endtime);
////
////                        System.out.println("calendar_metting_begingmt =" + calendar_metting_endgmt);
////
////                        System.out.println("calendar_metting_beginyear =" + calendar_metting_endyear);
////
////                        /* the calendar control metting-end events Respose  sub-string (starts....ends) */
////
////                        System.out.println("only date begin of events=" + begin.getDate());
////                        System.out.println("only begin time of events=" + begin.getHours() + ":" + begin.getMinutes() + ":" + begin.getSeconds());
////
////
////                        System.out.println("only date begin of events=" + end.getDate());
////                        System.out.println("only begin time of events=" + end.getHours() + ":" + end.getMinutes() + ":" + end.getSeconds());
////
////                        beg_date = begin.getDate();
////                        mbeg_date = begin.getDate() + "/" + calendar_metting_beginmonth + "/" + calendar_metting_beginyear;
////                        beg_time = begin.getHours();
////
////                        System.out.println("the vaule of mbeg_date=" + mbeg_date.toString().trim());
////                        end_date = end.getDate();
////                        end_time = end.getHours();
//
//                    }
//                    while (eventCursor.moveToNext());
//                }
//            }
//            break;
//        }
    }


}
