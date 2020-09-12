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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
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
    public static final String EXTRA_ID =
            "ir.android.data.db.entity.projects.id";
    public static final String EXTRA_NAME =
            "ir.android.data.db.entity.projects.title";
    private TextInputEditText taskNameEdit, tasksComment;
    private FloatingActionButton fabInsertTask, fabInsertTask2;
    private ConstraintLayout startDateConstraint, endDateConstraint, subfirstRow,
            repeatTypeConstraint, priorityTypeContraint;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onClickListener();
        initSpinners();
    }

    private void insertTempTask() {
        Tasks tasks = new Tasks("", 0, 0, 0,
                selectedProject.getProject_id() , "", 0, 0,
                "", "", 0, "");
        try {
            tempTaskID = taskViewModel.insert(tasks);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("tempTaskID");
            editor.putLong("tempTaskID",tempTaskID);
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
                    if (project.getProject_id() == sharedPreferences.getInt("selectedProjectID", 0)) {
                        selectedProject = project;
                        projectCategory.post(new Runnable() {
                            @Override
                            public void run() {
                                projectCategory.setSelection(projects.indexOf(project));
                                Init.setProjectCategory(projectIcon, project.getCategory_id(), false);
                            }
                        });
                        //for inserting subtask we need task foreign key
                        insertTempTask();
                        initRecyclerViews();
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

        insertSubtasksBtn.setOnClickListener(new View.OnClickListener() {
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
                if((Integer) completeIcon.getTag() != R.drawable.ic_radio_button_checked_green){
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
    }

    private void init() {
        tasksAddActivityBinding = DataBindingUtil.setContentView(AddEditTaskActivity.this, R.layout.tasks_add_activity);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AddEditTaskActivity.this);
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
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        subfirstRow = findViewById(R.id.subfirstRow);
        repeatTypeConstraint = findViewById(R.id.repeatTypeConstraint);
        priorityTypeContraint = findViewById(R.id.priorityTypeContraint);
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
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Project");
            taskNameEdit.setText(intent.getStringExtra(EXTRA_NAME));
        } else {
            setTitle("Add Project");
        }
    }

    private void insertTasks() {
        String name = taskNameEdit.getText().toString();
        if (name.trim().isEmpty()) {
            Toast.makeText(this, "please insert name", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer priorityIntVal = 0;
        if (priorityVal.getText().toString().equals(getString(R.string.low))){
            priorityIntVal = 1;
        } else if(priorityVal.getText().toString().equals(getString(R.string.medium))){
            priorityIntVal = 2;
        } else if(priorityVal.getText().toString().equals(getString(R.string.high))) {
            priorityIntVal = 3;
        }
        RadioButton reminderType = findViewById(reminderTypeGroup.getCheckedRadioButtonId());
        //@TODO get repeat type val from bottom sheet
        Tasks tasks = new Tasks(name, priorityIntVal, isCompleted ? 1: 0, 0, selectedProject.getProject_id(),
                startTextVal.getText().toString(), reminderType.getText().toString().equals(getString(R.string.push)) ? 0 : 1, reminderTime.getSelectedItemPosition(),
                repeatTypeVal.getText().toString(), completedDateVal.isEmpty() ? endTextVal.getText().toString() : completedDateVal, 1, tasksComment.getText().toString());
        tasks.setTasks_id(tempTaskID);
        taskViewModel.update(tasks);
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        datepickerVal = "";
        datepickerVal += year + "/" + monthOfYear + "/" + dayOfMonth;
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
        if(isGone){
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
