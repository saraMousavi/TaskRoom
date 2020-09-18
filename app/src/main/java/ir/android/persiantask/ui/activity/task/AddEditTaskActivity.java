package ir.android.persiantask.ui.activity.task;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.factory.ProjectsViewModelFactory;
import ir.android.persiantask.data.db.factory.SubTasksViewModelFactory;
import ir.android.persiantask.data.db.factory.TasksViewModelFactory;
import ir.android.persiantask.databinding.TasksAddActivityBinding;
import ir.android.persiantask.ui.adapters.SubTasksAdapter;
import ir.android.persiantask.ui.fragment.TasksPriorityTypeBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatDayBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatPeriodBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatTypeBottomSheetFragment;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.utils.calender.DatePickerDialog;
import ir.android.persiantask.utils.calender.PersianCalendar;
import ir.android.persiantask.utils.calender.TimePickerDialog;
import ir.android.persiantask.viewmodels.ProjectViewModel;
import ir.android.persiantask.viewmodels.SubTasksViewModel;
import ir.android.persiantask.viewmodels.TaskViewModel;

public class AddEditTaskActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener
        , DatePickerDialog.OnDateSetListener
        , TasksRepeatTypeBottomSheetFragment.RepeatTypeClickListener
        , TasksRepeatDayBottomSheetFragment.RepeatDayClickListener
        , TasksRepeatPeriodBottomSheetFragment.RepeatPeriodClickListener
        , TasksPriorityTypeBottomSheetFragment.PriorityTypeClickListener {
    private TextInputEditText taskNameEdit, tasksComment;
    private FloatingActionButton fabInsertTask, fabInsertTask2;
    private ConstraintLayout startDateConstraint, endDateConstraint, subfirstRow,
            repeatTypeConstraint, priorityTypeContraint, subTaskTitle,
            reminderTimeConstraint, reminderTypeConstraint;
    private TextView startTextVal, endTextVal, repeatTypeVal, completedDate, priorityVal;
    private AppBarLayout mAppBarLayout;
    private ImageButton insertSubtasksBtn;
    private RecyclerView subtaskRecyclerView;
    private TasksAddActivityBinding tasksAddActivityBinding;
    private String datepickerVal;
    private ProjectViewModel projectViewModel;
    private TaskViewModel taskViewModel;
    private AppCompatSpinner projectCategory, reminderTime;
    private SharedPreferences sharedPreferences;
    private ImageView projectIcon, completeIcon;
    private Projects selectedProject;
    private boolean isCompleted;
    private String completedDateVal = "";
    private RadioGroup reminderTypeGroup;
    private Long tempTaskID;
    private boolean isEditActivity = false;
    private Tasks clickedTask;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onClickListener();
        initSpinners();
        //for inserting subtask we need task foreign key
        insertTempTask();
        initRecyclerViews();
    }

    private void insertTempTask() {
        Tasks tasks = new Tasks("", 0, 0, 0,
                selectedProject.getProject_id(), "", 0, 0,
                "", "", 0, "");
        try {
            if (isEditActivity) {
                tempTaskID = clickedTask.getTasks_id();
            } else {
                tempTaskID = taskViewModel.insert(tasks);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("tempTaskID");
            editor.putLong("tempTaskID", tempTaskID);
            editor.apply();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void initSpinners() {
        projectViewModel.getAllProjects().observe(this, new Observer<List<Projects>>() {
            @Override
            public void onChanged(List<Projects> projects) {
                ArrayList<Projects> spinnerArray = new ArrayList<Projects>(projects);
                ArrayAdapter<Projects> projectsArrayAdapter = new ArrayAdapter<>(AddEditTaskActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                projectCategory.setAdapter(projectsArrayAdapter);
                for (Projects project : projects) {
                    if (project.getProject_id() == selectedProject.getProject_id()) {
                        selectedProject = project;
                        projectCategory.post(new Runnable() {
                            @Override
                            public void run() {
                                projectCategory.setSelection(projects.indexOf(selectedProject));
                                Init.setProjectCategory(projectIcon, selectedProject.getCategory_id(), false);
                            }
                        });
                    }
                }

            }
        });
        ArrayList<String> remindTimeArray = new ArrayList<>();
        remindTimeArray.add(getString(R.string.dontRemind));
        remindTimeArray.add(getString(R.string.remindInEndDate));
        //@TODO change this translation
        remindTimeArray.add(getString(R.string.remindInAdvance));
        ArrayAdapter<String> remindTimeAdapter = new ArrayAdapter<>(AddEditTaskActivity.this,
                android.R.layout.simple_spinner_dropdown_item, remindTimeArray);
        reminderTime.setAdapter(remindTimeAdapter);
    }


    private void initRecyclerViews() {
        SubTasksViewModelFactory factory = new SubTasksViewModelFactory(getApplication(), tempTaskID);
        SubTasksViewModel subTasksViewModel = ViewModelProviders.of(this, factory).get(SubTasksViewModel.class);
        SubTasksAdapter subTasksAdapter = new SubTasksAdapter(AddEditTaskActivity.this, subTasksViewModel);
        subTasksViewModel.getAllSubtasks().observe(this, new Observer<List<Subtasks>>() {
            @Override
            public void onChanged(List<Subtasks> subtasks) {
                subtasks.add(null);
                subTasksAdapter.submitList(subtasks);
            }
        });
        subtaskRecyclerView.setAdapter(subTasksAdapter);
        subtaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Subtasks selectedSubTask = subTasksAdapter.getSubTaskAt(viewHolder.getAdapterPosition());
                subTasksViewModel.delete(selectedSubTask);
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteSubTask), Snackbar.LENGTH_LONG)
                        .show();
            }
        }).attachToRecyclerView(subtaskRecyclerView);
    }

    private void onClickListener() {
        fabInsertTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTasks();
            }
        });
        fabInsertTask2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTasks();
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            boolean isShow = false;
            FloatingActionButton fabInsertTask2 = findViewById(R.id.fabInsertTask2);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //@TODO add slide down animation
                    fabInsertTask2.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    //@TODO add slide up animation
                    fabInsertTask2.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
        startDateConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        AddEditTaskActivity.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(getSupportFragmentManager(), "startDatepickerdialog");
            }
        });

        endDateConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        AddEditTaskActivity.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(getSupportFragmentManager(), "endDatepickerdialog");
            }
        });
        /**
         * show and hide add row for add new sub task
         */
        insertSubtasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subfirstRow.getVisibility() == View.GONE) {
                    subfirstRow.setVisibility(View.VISIBLE);
                    ConstraintLayout addRow = (ConstraintLayout) subtaskRecyclerView.getChildAt(subtaskRecyclerView.getChildCount() - 1);
                    addRow.setVisibility(View.VISIBLE);
                } else {
                    ConstraintLayout addRow = (ConstraintLayout) subtaskRecyclerView.getChildAt(subtaskRecyclerView.getChildCount() - 1);
                    addRow.setVisibility(View.VISIBLE);
                }
            }
        });
        /**
         * show and hide the sub task list
         */
        subTaskTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subfirstRow.getVisibility() == View.VISIBLE) {
                    subfirstRow.setVisibility(View.GONE);
                } else {
                    subfirstRow.setVisibility(View.VISIBLE);
                }
            }
        });

        projectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedProject = (Projects) parent.getItemAtPosition(position);
                Init.setProjectCategory(projectIcon, selectedProject.getCategory_id(), false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        repeatTypeConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksRepeatTypeBottomSheetFragment tasksRepeatTypeDialog = new TasksRepeatTypeBottomSheetFragment();
                tasksRepeatTypeDialog.show(getSupportFragmentManager(), "tag");
            }
        });
        completeIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if ((Integer) completeIcon.getTag() != R.drawable.ic_radio_button_checked_green) {
                    completeIcon.setImageResource(R.drawable.ic_radio_button_checked_green);
                    completeIcon.setTag(R.drawable.ic_radio_button_checked_green);
                    completedDate.setVisibility(View.VISIBLE);
                    isCompleted = true;
                    completedDateVal = Init.getCurrentDate();
                    completedDate.setText(getString(R.string.inDate) + " " + completedDateVal + " " + getString(R.string.completed));
                } else {
                    completeIcon.setImageResource(R.drawable.ic_black_circle);
                    completeIcon.setTag(R.drawable.ic_black_circle);
                    completedDate.setVisibility(View.GONE);
                    isCompleted = false;
                }
            }
        });
        priorityTypeContraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksPriorityTypeBottomSheetFragment tasksPriorityTypeBottomSheetFragment = new TasksPriorityTypeBottomSheetFragment();
                tasksPriorityTypeBottomSheetFragment.show(getSupportFragmentManager(), "Priority_Type");
            }
        });
        /**
         * toggle visibility of reminder type and repeat type view on reminder time(zamanhaye yadavari) change selection
         */
        reminderTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        reminderTypeConstraint.setVisibility(View.GONE);
                        repeatTypeConstraint.setVisibility(View.GONE);
                        break;
                    case 1:
                        reminderTypeConstraint.setVisibility(View.VISIBLE);
                        repeatTypeConstraint.setVisibility(View.GONE);
                        break;
                    case 2:
                        reminderTypeConstraint.setVisibility(View.VISIBLE);
                        repeatTypeConstraint.setVisibility(View.VISIBLE);
                        Snackbar
                                .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.chooseadvancerepeattype), Snackbar.LENGTH_LONG)
                                .show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        tasksAddActivityBinding = DataBindingUtil.setContentView(AddEditTaskActivity.this, R.layout.tasks_add_activity);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AddEditTaskActivity.this);
        Gson gson = new Gson();
        String projectJson = sharedPreferences.getString("selectedProject", "");
        selectedProject = gson.fromJson(projectJson, Projects.class);
        fabInsertTask = findViewById(R.id.fabInsertTask);
        fabInsertTask2 = findViewById(R.id.fabInsertTask2);
        insertSubtasksBtn = findViewById(R.id.insertSubtasksBtn);
        taskNameEdit = findViewById(R.id.taskNameEdit);
        tasksComment = findViewById(R.id.tasksComment);
        startDateConstraint = findViewById(R.id.startDateConstraint);
        endDateConstraint = findViewById(R.id.endDateConstraint);
        startTextVal = findViewById(R.id.startTextVal);
        //@TODO why hour and minute  has inversed in ui
        startTextVal.setText(Init.getCurrentDate());
        endTextVal = findViewById(R.id.endTextVal);
        repeatTypeVal = findViewById(R.id.repeatTypeVal);
        completedDate = findViewById(R.id.completedDate);
        priorityVal = findViewById(R.id.priorityVal);
        reminderTimeConstraint = findViewById(R.id.reminderTimeConstraint);
        reminderTypeConstraint = findViewById(R.id.reminderTypeConstraint);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        subfirstRow = findViewById(R.id.subfirstRow);
        repeatTypeConstraint = findViewById(R.id.repeatTypeConstraint);
        priorityTypeContraint = findViewById(R.id.priorityTypeContraint);
        subTaskTitle = findViewById(R.id.subTaskTitle);
        subtaskRecyclerView = findViewById(R.id.subtaskRecyclerView);
        projectCategory = findViewById(R.id.projectCategory);
        reminderTime = findViewById(R.id.reminderTime);
        projectIcon = findViewById(R.id.projectIcon);
        reminderTypeGroup = findViewById(R.id.reminderTypeGroup);
        completeIcon = findViewById(R.id.completeIcon);
        completeIcon.setTag(R.drawable.ic_black_circle);
        ProjectsViewModelFactory projectFactory = new ProjectsViewModelFactory(getApplication(), null);

        TasksViewModelFactory taskFactory = new TasksViewModelFactory(getApplication(), sharedPreferences.getInt("selectedProjectID", 0));
        projectViewModel = ViewModelProviders.of(this, projectFactory).get(ProjectViewModel.class);
        taskViewModel = ViewModelProviders.of(this, taskFactory).get(TaskViewModel.class);

        Intent intent = getIntent();

        if (intent.hasExtra("clickedTask")) {
            clickedTask = (Tasks) intent.getExtras().getSerializable("clickedTask");
            editableTaskFields();
        }
        List<Map<View, Boolean>> views = new ArrayList<>();
        Map<View, Boolean> viewMap = new HashMap<>();
        viewMap.put(mAppBarLayout, true);
        views.add(viewMap);
        viewMap = new HashMap<>();
        viewMap.put(taskNameEdit, true);
        views.add(viewMap);
        viewMap = new HashMap<>();
        viewMap.put(fabInsertTask, false);
        views.add(viewMap);
        viewMap = new HashMap<>();
        viewMap.put(fabInsertTask2, false);
        views.add(viewMap);
        Init.setViewBackgroundDependOnTheme(views, AddEditTaskActivity.this);
    }

    private void editableTaskFields() {
        taskNameEdit.setText(clickedTask.getTasks_title());
        startTextVal.setText(clickedTask.getTasks_startdate());
        endTextVal.setText(clickedTask.getTasks_enddate());
        endTextVal.setVisibility(View.VISIBLE);
        reminderTime.post(new Runnable() {
            @Override
            public void run() {
                reminderTime.setSelection(clickedTask.getTasks_remindertime());
            }
        });
        ((RadioButton) reminderTypeGroup.getChildAt(clickedTask.getTasks_remindertype())).setChecked(true);
        repeatTypeVal.setVisibility(View.VISIBLE);
        repeatTypeVal.setText(clickedTask.getTasks_repeateddays());
        if (clickedTask.getTasks_iscompleted() == 1) {
            completeIcon.setImageResource(R.drawable.ic_radio_button_checked_green);
            completeIcon.setTag(R.drawable.ic_radio_button_checked_green);
            completedDate.setVisibility(View.VISIBLE);
            isCompleted = true;
            completedDate.setText(clickedTask.getTasks_enddate());
        }
        priorityVal.setVisibility(View.VISIBLE);
        String priorityStringVal = getString(R.string.nonePriority);
        if (clickedTask.getTasks_priority() == 1) {
            priorityStringVal = getString(R.string.low);
        } else if (clickedTask.getTasks_priority() == 2) {
            priorityStringVal = getString(R.string.medium);
        } else if (clickedTask.getTasks_priority() == 3) {
            priorityStringVal = getString(R.string.high);
        }
        priorityVal.setText(priorityStringVal);
        tasksComment.setText(clickedTask.getTasks_comment());
        isEditActivity = true;
    }

    private void insertTasks() {
        Integer priorityIntVal = 0;
        if (priorityVal.getText().toString().equals(getString(R.string.low))) {
            priorityIntVal = 1;
        } else if (priorityVal.getText().toString().equals(getString(R.string.medium))) {
            priorityIntVal = 2;
        } else if (priorityVal.getText().toString().equals(getString(R.string.high))) {
            priorityIntVal = 3;
        }
        RadioButton reminderType = findViewById(reminderTypeGroup.getCheckedRadioButtonId());
        //@TODO get repeat type val from bottom sheet
        Tasks tasks = new Tasks(taskNameEdit.getText().toString(), priorityIntVal, isCompleted ? 1 : 0, 0,
                selectedProject.getProject_id(), startTextVal.getText().toString(), reminderType.getText().toString().equals(getString(R.string.push)) ? 0 : 1,
                reminderTime.getSelectedItemPosition(), repeatTypeVal.getText().toString(),
                completedDateVal.isEmpty() ? endTextVal.getText().toString() : completedDateVal, 1,
                tasksComment.getText().toString());
        tasks.setTasks_id(tempTaskID);
        taskViewModel.update(tasks);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        datepickerVal = "";
        datepickerVal += year + "/"
                + (monthOfYear < 10 ? "0" + monthOfYear : monthOfYear)
                + "/" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
        GregorianCalendar galena = new GregorianCalendar();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                AddEditTaskActivity.this,
                galena.get(Calendar.HOUR),
                galena.get(Calendar.MINUTE),
                true
        );
        if (view.getTag().equals("startDatepickerdialog")) {
            timePickerDialog.show(getSupportFragmentManager(), "startTimePickerDialog");
        } else if (view.getTag().equals("endDatepickerdialog")) {
            timePickerDialog.show(getSupportFragmentManager(), "endTimePickerDialog");
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute) {
        datepickerVal += " " + hourOfDay + ":" + minute;
        if (view.getTag().equals("startTimePickerDialog")) {
            startTextVal.setText(datepickerVal);
            startTextVal.setVisibility(View.VISIBLE);
        } else if (view.getTag().equals("endTimePickerDialog")) {
            endTextVal.setText(datepickerVal);
            endTextVal.setVisibility(View.VISIBLE);
            reminderTimeConstraint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClickRepeatType(String repeatType) {
        repeatTypeVal.setVisibility(View.VISIBLE);
        repeatTypeVal.setText(repeatType);
    }

    @Override
    public void onClickRepeatDay(String repeatDay) {
        repeatTypeVal.setVisibility(View.VISIBLE);
        repeatTypeVal.setText(repeatDay);
    }

    @Override
    public void onClickRepeatPeriod(String repeatPeriod) {
        repeatTypeVal.setVisibility(View.VISIBLE);
        repeatTypeVal.setText(repeatPeriod);
    }

    @Override
    public void onClickPriorityType(String priorityType, boolean isGone) {
        if (isGone) {
            priorityVal.setVisibility(View.GONE);
        } else {
            priorityVal.setVisibility(View.VISIBLE);
            priorityVal.setText(priorityType);
        }
    }

    public interface ClickAddSubTaskListener {
        void addSubTaskListener();
    }
}
