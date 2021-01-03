package ir.android.taskroom.ui.activity.task;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.data.db.entity.Attachments;
import ir.android.taskroom.data.db.entity.Category;
import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.data.db.entity.Subtasks;
import ir.android.taskroom.data.db.entity.Tasks;
import ir.android.taskroom.data.db.factory.AttachmentsViewModelFactory;
import ir.android.taskroom.data.db.factory.ProjectsViewModelFactory;
import ir.android.taskroom.data.db.factory.SubTasksViewModelFactory;
import ir.android.taskroom.data.db.factory.TasksViewModelFactory;
import ir.android.taskroom.databinding.TasksAddActivityBinding;
import ir.android.taskroom.ui.activity.DrawingActivity;
import ir.android.taskroom.ui.activity.RecordingActivity;
import ir.android.taskroom.ui.adapters.AttachmentsAdapter;
import ir.android.taskroom.ui.adapters.SubTasksAdapter;
import ir.android.taskroom.ui.fragment.TasksPriorityTypeBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatDayBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatPeriodBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatTypeBottomSheetFragment;
import ir.android.taskroom.utils.EnglishInit;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.calender.DatePickerDialogs;
import ir.android.taskroom.utils.calender.PersianCalendar;
import ir.android.taskroom.utils.calender.TimePickerDialogs;
import ir.android.taskroom.utils.objects.TasksReminderActions;
import ir.android.taskroom.viewmodels.AttachmentsViewModel;
import ir.android.taskroom.viewmodels.CategoryViewModel;
import ir.android.taskroom.viewmodels.ProjectViewModel;
import ir.android.taskroom.viewmodels.SubTasksViewModel;
import ir.android.taskroom.viewmodels.TaskViewModel;

public class AddEditTaskActivity extends AppCompatActivity implements
        TimePickerDialogs.OnTimeSetListener
        , DatePickerDialogs.OnDateSetListener
        , TasksRepeatTypeBottomSheetFragment.RepeatTypeClickListener
        , TasksRepeatDayBottomSheetFragment.RepeatDayClickListener
        , TasksRepeatPeriodBottomSheetFragment.RepeatPeriodClickListener
        , TasksPriorityTypeBottomSheetFragment.PriorityTypeClickListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;

    private static final int WRITE_EXTERNAL_STORAGE_DRAW = 201;
    private static final int RESUEST_RECORD_AUDIO = 100;
    private static final int REQUEST_CAMERA_ACTION = 200;
    private static final int DRAW_REQUEST = 300;
    private static final int WRITE_EXTERNAL_STORAGE_CAMERA = 400;
    private static final int FILE_PICKER_REQUEST_CODE = 500;
    private static final int WRITE_EXTERNAL_STORAGE_STORAGE = 600;
    private TextInputEditText taskNameEdit, tasksComment;
    private TextInputLayout taskName;
    private FloatingActionButton fabInsertTask, fabInsertTask2;
    private ConstraintLayout startDateConstraint, endDateConstraint, subfirstRow,
            repeatTypeConstraint, priorityTypeContraint, subTaskTitle,
            reminderTimeConstraint, reminderTypeConstraint, uploadFileContraint;
    private TextView startTextVal, endTextVal, repeatTypeVal, completedDate, priorityVal;
    private AppBarLayout mAppBarLayout;
    private ImageButton insertSubtasksBtn;
    private RecyclerView subtaskRecyclerView;
    private TasksAddActivityBinding tasksAddActivityBinding;
    private String startDatepickerVal, endDatepickerVal;
    private ProjectViewModel projectViewModel;
    private TaskViewModel taskViewModel;
    private AppCompatSpinner projectCategory, reminderTime;
    private SharedPreferences sharedPreferences;
    private ImageView projectIcon, completeIcon, priorityIcon, cameraIcon, storageIcon, recordIcon, drawIcon;
    private Projects selectedProject;
    private boolean isCompleted;
    private String completedDateVal = "";
    private RadioGroup reminderTypeGroup;
    private Long tempTaskID;
    private boolean isEditActivity = false;
    private Tasks clickedTask;
    private long lastProjectID;
    private CollapsingToolbarLayout toolBarLayout;
    //    private LifeCycleCallBackManager lifeCycleCallBackManager;
    private RecyclerView attachedRecyclerView;
    private AttachmentsAdapter attachmentsAdapter;
    private AttachmentsViewModel attachmentsViewModel;
    private LinearLayout uploadChoose;
    private Integer reminderTypeVal;
    private boolean isReminerTimeChange = false;
    private DateTime calenderClickedDate = null;
    private int attachmentSize = 0;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMasterTheme();
        super.onCreate(savedInstanceState);
        init();
        onClickEvents();
        initSpinners();
        //for inserting subtask we need task foreign key
        insertTempTask();
        initRecyclerViews();
//        viewsAnimation();
    }

    private void insertTempTask() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("calenderClickedDate") != null) {
            calenderClickedDate = new DateTime(getIntent().getExtras().getString("calenderClickedDate"));
        }
        Tasks tasks = new Tasks("", 0, 0, 0,
                selectedProject == null ? lastProjectID : selectedProject.getProject_id(), "", 0, 0,
                "", "", 0, "", "0", false, "");
        if (tasks.getProjects_id() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.firstdefineProject), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
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
        } catch (Exception e) {
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
                                //show icon depend on category_id
                                showCategoryIcon();
                            }
                        });
                    }
                    lastProjectID = project.getProject_id();
                }


            }
        });
        ArrayList<String> remindTimeArray = new ArrayList<>();
        if (endDatepickerVal == null || endDatepickerVal.isEmpty()) {
            remindTimeArray.add(getString(R.string.dontRemind));
            remindTimeArray.add(getString(R.string.remindInStartDate));
            remindTimeArray.add(getString(R.string.remindInAdvance));
        } else {
            remindTimeArray.add(getString(R.string.dontRemind));
            remindTimeArray.add(getString(R.string.remindInStartDate));
            remindTimeArray.add(getString(R.string.remindInEndDate));
            remindTimeArray.add(getString(R.string.remindInAdvance));
        }
        ArrayAdapter<String> remindTimeAdapter = new ArrayAdapter<>(AddEditTaskActivity.this,
                android.R.layout.simple_spinner_dropdown_item, remindTimeArray);
        reminderTime.setAdapter(remindTimeAdapter);
    }

    private void showCategoryIcon() {
        CategoryViewModel categoryViewModel = ViewModelProviders.of(AddEditTaskActivity.this).get(CategoryViewModel.class);
        categoryViewModel.getAllCategory().observe(AddEditTaskActivity.this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                for (Category category : categories) {
                    if (category.getCategory_id().equals(selectedProject.getCategory_id())) {
                        projectIcon.setImageResource(getResources().getIdentifier(category.getCategory_black_image(), "xml", null));
                    }
                }
            }
        });
    }


    private void initRecyclerViews() {
        SubTasksViewModelFactory factory = new SubTasksViewModelFactory(getApplication(), tempTaskID);
        SubTasksViewModel subTasksViewModel = ViewModelProviders.of(this, factory).get(SubTasksViewModel.class);
        SubTasksAdapter subTasksAdapter = new SubTasksAdapter(AddEditTaskActivity.this, subTasksViewModel);
        subTasksViewModel.getAllTasksSubtasks().observe(this, new Observer<List<Subtasks>>() {
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
                String successDeleteSubTask = getString(R.string.successDeleteSubTask);
                if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    successDeleteSubTask = getString(R.string.successDeleteSubTask);
                }
                Snackbar snackbar = Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), successDeleteSubTask, Snackbar.LENGTH_LONG);
                if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                }
                snackbar.show();
            }
        }).attachToRecyclerView(subtaskRecyclerView);
        AttachmentsViewModelFactory attachmentFactory = new AttachmentsViewModelFactory(getApplication(), tempTaskID);
        attachmentsViewModel = ViewModelProviders.of(this, attachmentFactory).get(AttachmentsViewModel.class);
        attachmentsAdapter = new AttachmentsAdapter(attachmentsViewModel, AddEditTaskActivity.this);
        attachmentsViewModel.getAllTasksAttachments().observe(this, new Observer<List<Attachments>>() {
            @Override
            public void onChanged(List<Attachments> attachments) {
                attachmentSize = attachments.size();
                attachmentsAdapter.submitList(attachments);
                attachedRecyclerView.setAdapter(attachmentsAdapter);
            }
        });
        attachedRecyclerView.setLayoutManager(new GridLayoutManager(AddEditTaskActivity.this, 3, RecyclerView.VERTICAL, false));
    }

    private void onClickEvents() {
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
                Toolbar toolbar = findViewById(R.id.toolbar);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //@TODO add slide down animation
                    ViewCompat.animate(fabInsertTask2).scaleX(1).scaleY(1).start();
                    toolBarLayout.setTitle(taskNameEdit.getText().toString());
                    toolbar.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    toolBarLayout.setTitle(" ");
                    toolbar.setVisibility(View.INVISIBLE);
                    ViewCompat.animate(fabInsertTask2).scaleX(0).scaleY(0).start();
                    isShow = false;
                }
            }
        });
        startDateConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            AddEditTaskActivity.this, startDateListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                } else {
                    PersianCalendar persianCalendar = new PersianCalendar();
                    DatePickerDialogs datePickerDialog = DatePickerDialogs.newInstance(
                            AddEditTaskActivity.this,
                            persianCalendar.getPersianYear(),
                            persianCalendar.getPersianMonth(),
                            persianCalendar.getPersianDay()
                    );
                    datePickerDialog.show(getSupportFragmentManager(), "startDatepickerdialog");
                }
            }
        });

        endDateConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            AddEditTaskActivity.this, endDateListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                } else {
                    PersianCalendar persianCalendar = new PersianCalendar();
                    DatePickerDialogs datePickerDialog = DatePickerDialogs.newInstance(
                            AddEditTaskActivity.this,
                            persianCalendar.getPersianYear(),
                            persianCalendar.getPersianMonth(),
                            persianCalendar.getPersianDay()
                    );
                    datePickerDialog.show(getSupportFragmentManager(), "endDatepickerdialog");
                }
            }
        });
        /**
         * show and hide add row for add new sub task
         */
        Animation slideDown = AnimationUtils.loadAnimation(AddEditTaskActivity.this, R.anim.slide_down);
        Animation slideUp = AnimationUtils.loadAnimation(AddEditTaskActivity.this, R.anim.slide_up);
        insertSubtasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subfirstRow.getVisibility() == View.GONE) {
                    subfirstRow.setVisibility(View.VISIBLE);

                    subfirstRow.startAnimation(slideDown);
                    if (subtaskRecyclerView.getChildCount() != 0) {
                        ConstraintLayout addRow = (ConstraintLayout) subtaskRecyclerView.getChildAt(subtaskRecyclerView.getChildCount() - 1);
                        addRow.setVisibility(View.VISIBLE);
                        addRow.startAnimation(slideDown);
                    }
                } else {
                    ConstraintLayout addRow = (ConstraintLayout) subtaskRecyclerView.getChildAt(subtaskRecyclerView.getChildCount() - 1);
                    addRow.setVisibility(View.VISIBLE);
                    addRow.startAnimation(slideDown);
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
                showCategoryIcon();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        repeatTypeConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksRepeatTypeBottomSheetFragment tasksRepeatTypeDialog = new TasksRepeatTypeBottomSheetFragment();
                if (isEditActivity) {
                    Bundle bundle = new Bundle();
                    bundle.putString("repeatDays", clickedTask.getTasks_repeateddays());
                    tasksRepeatTypeDialog.setArguments(bundle);
                }
                tasksRepeatTypeDialog.show(getSupportFragmentManager(), "tag");
            }
        });
        completeIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if ((Integer) completeIcon.getTag() != R.drawable.ic_radio_button_checked_green) {
                    completeIcon.setImageResource(R.drawable.ic_radio_button_checked_green);
                    completeIcon.setTag(R.drawable.ic_radio_button_checked_green);
                    completedDate.setVisibility(View.VISIBLE);
                    isCompleted = true;
                    completedDateVal = EnglishInit.getCurrentDate();
                    completedDate.setText(getString(R.string.inDate) + " " + completedDateVal + " " + getString(R.string.completed));
                    if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                        completedDate.setText(getString(R.string.completed) + " " + getString(R.string.inDate) + " " + completedDateVal);
                    }
                    String disableReminderBecauseOfCompleted = getString(R.string.disableReminderBecauseOfCompleted);
                    if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                        disableReminderBecauseOfCompleted = getString(R.string.disableReminderBecauseOfCompleted);
                    }
                    Snackbar snackbar = Snackbar
                            .make(getWindow().getDecorView().findViewById(android.R.id.content), disableReminderBecauseOfCompleted, Snackbar.LENGTH_LONG);
                    if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                        ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    }
                    snackbar.show();
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

                String chooseadvancerepeattype = getString(R.string.chooseadvancerepeattype);
                if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    chooseadvancerepeattype = getString(R.string.chooseadvancerepeattype);
                }
                switch (position) {
                    case 0:
                        reminderTypeConstraint.setVisibility(View.GONE);
                        repeatTypeConstraint.setVisibility(View.GONE);
                        repeatTypeVal.setText("");
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            reminderTypeConstraint.setVisibility(View.VISIBLE);
                        }
                        repeatTypeConstraint.setVisibility(View.GONE);
                        repeatTypeVal.setText("");
                        Init.fadeVisibelityView(reminderTypeConstraint);
                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            reminderTypeConstraint.setVisibility(View.VISIBLE);
                        }
                        repeatTypeConstraint.setVisibility(View.GONE);
                        repeatTypeVal.setText("");
                        Init.fadeVisibelityView(reminderTypeConstraint);
                        if (reminderTime.getAdapter().getCount() < 4) {
                            repeatTypeConstraint.setVisibility(View.VISIBLE);
                            Init.fadeVisibelityView(repeatTypeConstraint);

                            Snackbar snackbar = Snackbar
                                    .make(getWindow().getDecorView().findViewById(android.R.id.content), chooseadvancerepeattype, Snackbar.LENGTH_LONG);
                            if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                                ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                            }
                            snackbar.show();
                        }
                        break;
                    case 3:
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            reminderTypeConstraint.setVisibility(View.VISIBLE);
                        }
                        repeatTypeConstraint.setVisibility(View.VISIBLE);
                        Init.fadeVisibelityView(reminderTypeConstraint);
                        Init.fadeVisibelityView(repeatTypeConstraint);

                        Snackbar snackbar = Snackbar
                                .make(getWindow().getDecorView().findViewById(android.R.id.content), chooseadvancerepeattype, Snackbar.LENGTH_LONG);
                        if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                            ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                        }
                        snackbar.show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uploadFileContraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadChoose.getVisibility() == View.VISIBLE) {
                    scaleAnimation(false);
                } else {
                    scaleAnimation(true);
                }

            }
        });

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleAnimation(false);
                ActivityCompat.requestPermissions(AddEditTaskActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CAMERA);
            }
        });
        storageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleAnimation(false);
                ActivityCompat.requestPermissions(AddEditTaskActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_STORAGE);
            }
        });

        recordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddEditTaskActivity.this, RecordingActivity.class), RESUEST_RECORD_AUDIO);
            }
        });
        drawIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AddEditTaskActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_DRAW);
            }
        });
    }

    private void scaleAnimation(boolean visible) {
        if (visible) {
            Animation anim = new ScaleAnimation(
                    0, 1f, // Start and end values for the X axis scaling
                    1f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 1f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(500);
            uploadChoose.startAnimation(anim);
            uploadChoose.setVisibility(View.VISIBLE);
        } else {
            Animation anim = new ScaleAnimation(
                    1f, 0, // Start and end values for the X axis scaling
                    1f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 1f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(500);
            uploadChoose.startAnimation(anim);
            uploadChoose.setVisibility(View.GONE);
        }
    }

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
        taskName = findViewById(R.id.taskName);
        if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
            taskName.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            taskName.setHint(getString(R.string.taskTitle));
        }
        tasksComment = findViewById(R.id.tasksComment);
        startDateConstraint = findViewById(R.id.startDateConstraint);
        endDateConstraint = findViewById(R.id.endDateConstraint);
        startTextVal = findViewById(R.id.reminderTimeVal);
        endTextVal = findViewById(R.id.endTextVal);
        repeatTypeVal = findViewById(R.id.repeatTypeVal);
        completedDate = findViewById(R.id.completedDate);
        priorityVal = findViewById(R.id.priorityVal);
        reminderTimeConstraint = findViewById(R.id.reminderTimeConstraint);
        reminderTypeConstraint = findViewById(R.id.reminderTypeConstraint);
        uploadFileContraint = findViewById(R.id.uploadFileContraint);
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
        toolBarLayout = findViewById(R.id.toolbar_layout);
        completeIcon = findViewById(R.id.completeIcon);
        attachedRecyclerView = findViewById(R.id.attachedRecyclerView);
        priorityIcon = findViewById(R.id.priorityIcon);
        cameraIcon = findViewById(R.id.cameraIcon);
        storageIcon = findViewById(R.id.storageIcon);
        recordIcon = findViewById(R.id.recordIcon);
        drawIcon = findViewById(R.id.drawIcon);
        uploadChoose = findViewById(R.id.uploadChoose);
        completeIcon.setTag(R.drawable.ic_black_circle);
        if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startTextVal.setTextAppearance(R.style.numberTextInput);
                endTextVal.setTextAppearance(R.style.numberTextInput);
            }
            startDatepickerVal = EnglishInit.getCurrentDate();
            startTextVal.setText(EnglishInit.getCurrentDate());
        } else {
            startDatepickerVal = Init.getCurrentDate();
            startTextVal.setText(Init.getCurrentDate());
        }

        ProjectsViewModelFactory projectFactory = new ProjectsViewModelFactory(getApplication(), null);

        TasksViewModelFactory taskFactory = new TasksViewModelFactory(getApplication(), sharedPreferences.getLong("selectedProjectID", 0));
        projectViewModel = ViewModelProviders.of(this, projectFactory).get(ProjectViewModel.class);
        taskViewModel = ViewModelProviders.of(this, taskFactory).get(TaskViewModel.class);


        Intent intent = getIntent();

        if (intent.hasExtra("clickedTask")) {
            clickedTask = (Tasks) intent.getExtras().getSerializable("clickedTask");
            editableTaskFields();
        }


        if (SettingUtil.getInstance(AddEditTaskActivity.this).isDarkTheme()) {
            View someView = findViewById(R.id.nestedScroll);
            View root = someView.getRootView();
            root.setBackgroundColor(getResources().getColor(R.color.backgroundDarkWindow));
        }
    }

    private void viewsAnimation() {
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        fabInsertTask.startAnimation(logoMoveAnimation);
    }

    private void editableTaskFields() {
        startDatepickerVal = clickedTask.getTasks_startdate();
        endDatepickerVal = clickedTask.getTasks_enddate();
        taskNameEdit.setText(clickedTask.getTasks_title());
        startTextVal.setText(clickedTask.getTasks_startdate());
        if (!clickedTask.getTasks_enddate().isEmpty()) {
            reminderTimeConstraint.setVisibility(View.VISIBLE);
        }
        if (!clickedTask.getTasks_enddate().isEmpty()) {
            endTextVal.setText(clickedTask.getTasks_enddate());
            endTextVal.setVisibility(View.VISIBLE);
        }
        reminderTime.post(new Runnable() {
            @Override
            public void run() {
                reminderTime.setSelection(clickedTask.getTasks_remindertime());
            }
        });
        if (clickedTask.getTasks_remindertype() != null) {
            ((RadioButton) reminderTypeGroup.getChildAt(clickedTask.getTasks_remindertype())).setChecked(true);
        }
        repeatTypeVal.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                repeatTypeVal.setText(clickedTask.getTasks_repeateddays());
            }
        }, 1500);

        if (clickedTask.getTasks_iscompleted() == 1) {
            completeIcon.setImageResource(R.drawable.ic_radio_button_checked_green);
            completeIcon.setTag(R.drawable.ic_radio_button_checked_green);
            completedDate.setVisibility(View.VISIBLE);
            isCompleted = true;
            completedDate.setText(clickedTask.getComplete_date());
        }
        priorityVal.setVisibility(View.VISIBLE);
        String priorityStringVal = getString(R.string.nonePriority);
        priorityIcon.setImageResource(R.drawable.ic_priority);
        if (clickedTask.getTasks_priority() == 1) {
            priorityStringVal = getString(R.string.low);
            priorityIcon.setImageResource(R.drawable.ic_low_yellow_priority);
        } else if (clickedTask.getTasks_priority() == 2) {
            priorityStringVal = getString(R.string.medium);
            priorityIcon.setImageResource(R.drawable.ic_medium_orange_priority);
        } else if (clickedTask.getTasks_priority() == 3) {
            priorityStringVal = getString(R.string.high);
            priorityIcon.setImageResource(R.drawable.ic_high_green_priority);
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
        if (taskNameEdit.getText().toString().isEmpty()) {
            String enterTaskName = getString(R.string.enterTaskName);
            if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                enterTaskName = getString(R.string.enterTaskName);
            }
            Snackbar snackbar = Snackbar
                    .make(getWindow().getDecorView().findViewById(android.R.id.content), enterTaskName, Snackbar.LENGTH_LONG);
            if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            }
            snackbar.show();
            return;
        }
        reminderTypeVal = reminderType == null ? 0 : reminderType.getText().toString().equals(getString(R.string.notification)) ? 0 : 1;
        if (isEditActivity) {
            if (clickedTask.getWork_id().contains(",")) {
                for (String requestId : clickedTask.getWork_id().split(",")) {
                    WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(requestId));
                }
            } else {
                if (!clickedTask.getWork_id().equals("0") && !clickedTask.getWork_id().equals("-2")) {
                    WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(clickedTask.getWork_id()));
                }
            }
        }
        String workID = "0";
        if (!isCompleted) {
            workID = createWorkRequest();
            if (workID.equals("-1")) {

                String validstartdateandenddate = getString(R.string.validstartdateandenddate);
                if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    validstartdateandenddate = getString(R.string.validstartdateandenddate);
                }
                Snackbar snackbar = Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), validstartdateandenddate, Snackbar.LENGTH_LONG);
                if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                }
                snackbar.show();
                return;
            }
        }


        Tasks tasks = new Tasks(taskNameEdit.getText().toString(), priorityIntVal, isCompleted ? 1 : 0, 0,
                selectedProject.getProject_id(), startTextVal.getText().toString(),
                reminderTypeVal, reminderTime.getSelectedItemPosition(), repeatTypeVal.getText().toString(),
                endTextVal.getText().toString(), 1, tasksComment.getText().toString(),
                workID, attachmentsAdapter.getItemCount() > 0, completedDate.getText().toString());
        if (isEditActivity) {
            tasks.setTasks_crdate(clickedTask.getTasks_crdate());
        } else {
            DateTime crDate = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(startDatepickerVal));
            tasks.setTasks_crdate(Init.convertDateTimeToInteger(crDate));
        }
        tasks.setTasks_id(tempTaskID);
        taskViewModel.update(tasks);
        setResult(RESULT_OK);
        finish();
    }


    public String createWorkRequest() {
        Tasks tasks = new Tasks("", 0, 0, 0,
                selectedProject.getProject_id(), startTextVal.getText().toString(),
                reminderTypeVal, reminderTime.getSelectedItemPosition(), repeatTypeVal.getText().toString(),
                endTextVal.getText().toString(), 1, tasksComment.getText().toString(),
                "", attachmentsAdapter.getItemCount() > 0, completedDate.getText().toString());
        Tasks endTimeTask = new Tasks("", 0, 0, 0,
                selectedProject.getProject_id(), startTextVal.getText().toString(),
                reminderTypeVal, 2, "",
                endTextVal.getText().toString(), 1, tasksComment.getText().toString(),
                "", attachmentsAdapter.getItemCount() > 0, completedDate.getText().toString());
        TasksReminderActions tasksReminderActions = null;
        if (SettingUtil.getInstance(AddEditTaskActivity.this).isEnglishLanguage()) {
            tasksReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(tasks, calenderClickedDate, getResources());
        } else {
            tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(tasks, calenderClickedDate, getResources());
        }


        if (tasksReminderActions.getRemainDuration() == -1) {
            return "-1";
        }
        if (tasksReminderActions.getRemainDuration() == -2) {
            String validstartdatepast = getString(R.string.validstartdatepast);
            if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                validstartdatepast = getString(R.string.validstartdatepast);
            }
            Toast.makeText(getApplicationContext(), validstartdatepast, Toast.LENGTH_LONG).show();
            return "-2";
        }
        if (tasksReminderActions.getRemainTime().isEmpty()) {
            //if remind time was dont remind
            return "0";
        } else {

            String remindeTime = getString(R.string.remindeTime);
            if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                remindeTime = getString(R.string.remindeTime);
            }
            Toast.makeText(getApplicationContext(), remindeTime + tasksReminderActions.getRemainTime(), Toast.LENGTH_LONG).show();
            String workId = "";
            if (!tasks.getTasks_enddate().isEmpty() && !tasks.getTasks_repeateddays().isEmpty() && tasks.getTasks_iscompleted() == 0) {
                TasksReminderActions endTimeReminderActions = null;
                if(SettingUtil.getInstance(AddEditTaskActivity.this).isEnglishLanguage()){
                    endTimeReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(endTimeTask, calenderClickedDate, getResources());
                } else {
                    endTimeReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(endTimeTask, calenderClickedDate, getResources());
                }
                String msg = getResources().getString(R.string.taskTime)
                        + " " + taskNameEdit.getText().toString() + " " + getResources().getString(R.string.endTimeMessage);
                String msg2 = getResources().getString(R.string.alarmExpandableText);
                if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    msg = getResources().getString(R.string.taskTime)
                            + " " + taskNameEdit.getText().toString() + " " + getResources().getString(R.string.endTimeMessage);
                    msg2 = getResources().getString(R.string.alarmExpandableText);
                }
                workId = Init.requestWork(getApplicationContext(), msg, msg2
                        , 0,//notif
                        Init.getWorkRequestPeriodicIntervalMillis(getResources(), repeatTypeVal.getText().toString()),
                        endTimeReminderActions.getRemainDuration(), false, false) + ",";
            }
            workId += Init.requestWork(getApplicationContext(), taskNameEdit.getText().toString(), tasksComment.getText().toString(), reminderTypeVal,
                    Init.getWorkRequestPeriodicIntervalMillis(getResources(), repeatTypeVal.getText().toString()),
                    tasksReminderActions.getRemainDuration(), !repeatTypeVal.getText().toString().isEmpty(), false);
            return workId;
        }
    }

    @Override
    public void onDateSet(DatePickerDialogs view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        String date = year + "/"
                + (monthOfYear < 10 ? "0" + monthOfYear : monthOfYear)
                + "/" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
        DateTime dateTime = new DateTime();
        TimePickerDialogs timePickerDialog = TimePickerDialogs.newInstance(
                AddEditTaskActivity.this,
                dateTime.getHourOfDay(),
                dateTime.getMinuteOfHour(),
                true
        );
        if (view.getTag().equals("startDatepickerdialog")) {
            startDatepickerVal = "";
            startDatepickerVal += date;
            timePickerDialog.show(getSupportFragmentManager(), "startTimePickerDialog");
        } else if (view.getTag().equals("endDatepickerdialog")) {
            endDatepickerVal = "";
            endDatepickerVal += date;
            timePickerDialog.show(getSupportFragmentManager(), "endTimePickerDialog");
        }
        isReminerTimeChange = true;
    }

    @Override
    public void onTimeSet(TimePickerDialogs view, int hourOfDay, int minute) {
        String time = " " + (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay)
                + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
        if (view.getTag().equals("startTimePickerDialog")) {
            startDatepickerVal += time;
            startTextVal.setText(startDatepickerVal);
            startTextVal.setVisibility(View.VISIBLE);
        } else if (view.getTag().equals("endTimePickerDialog")) {
            endDatepickerVal += time;
            endTextVal.setText(endDatepickerVal);
            endTextVal.setVisibility(View.VISIBLE);
            ArrayList<String> remindTimeArray = new ArrayList<>();
            remindTimeArray.add(getString(R.string.dontRemind));
            remindTimeArray.add(getString(R.string.remindInStartDate));
            remindTimeArray.add(getString(R.string.remindInEndDate));
            //@TODO change this translation
            remindTimeArray.add(getString(R.string.remindInAdvance));
            ArrayAdapter<String> remindTimeAdapter = new ArrayAdapter<>(AddEditTaskActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, remindTimeArray);
            reminderTime.setAdapter(remindTimeAdapter);
            Init.fadeVisibelityView(reminderTimeConstraint);
        }
    }

    private DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month++;
            String date = year + "/"
                    + (month < 10 ? "0" + month : month)
                    + "/" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
            DateTime dateTime = new DateTime();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AddEditTaskActivity.this, startTimeListener,
                    dateTime.getHourOfDay(),
                    dateTime.getMinuteOfHour(),
                    true
            );
            startDatepickerVal = "";
            startDatepickerVal += date;
            timePickerDialog.show();
            isReminerTimeChange = true;
        }
    };
    private DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month++;
            String date = year + "/"
                    + (month < 10 ? "0" + month : month)
                    + "/" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
            DateTime dateTime = new DateTime();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    AddEditTaskActivity.this, endTimeListener,
                    dateTime.getHourOfDay(),
                    dateTime.getMinuteOfHour(),
                    true
            );
            endDatepickerVal = "";
            endDatepickerVal += date;
            timePickerDialog.show();
            isReminerTimeChange = true;
        }
    };

    private TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time = " " + (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay)
                    + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
            startDatepickerVal += time;
            startTextVal.setText(startDatepickerVal);
            startTextVal.setVisibility(View.VISIBLE);
        }
    };


    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String time = " " + (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay)
                    + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
            endDatepickerVal += time;
            endTextVal.setText(endDatepickerVal);
            endTextVal.setVisibility(View.VISIBLE);
            ArrayList<String> remindTimeArray = new ArrayList<>();
            remindTimeArray.add(getString(R.string.dontRemind));
            remindTimeArray.add(getString(R.string.remindInStartDate));
            remindTimeArray.add(getString(R.string.remindInEndDate));
            //@TODO change this translation
            remindTimeArray.add(getString(R.string.remindInAdvance));
            ArrayAdapter<String> remindTimeAdapter = new ArrayAdapter<>(AddEditTaskActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, remindTimeArray);
            reminderTime.setAdapter(remindTimeAdapter);
            Init.fadeVisibelityView(reminderTimeConstraint);
        }
    };

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
            priorityIcon.setImageResource(R.drawable.ic_priority);
        } else {
            priorityVal.setVisibility(View.VISIBLE);
            priorityVal.setText(priorityType);
            if (priorityVal.getText().toString().equals(getString(R.string.low)) || priorityVal.getText().toString().equals(getString(R.string.low))) {
                priorityIcon.setImageResource(R.drawable.ic_low_yellow_priority);
            } else if (priorityVal.getText().toString().equals(getString(R.string.medium)) || priorityVal.getText().toString().equals(getString(R.string.medium))) {
                priorityIcon.setImageResource(R.drawable.ic_medium_orange_priority);
            } else if (priorityVal.getText().toString().equals(getString(R.string.high)) || priorityVal.getText().toString().equals(getString(R.string.high))) {
                priorityIcon.setImageResource(R.drawable.ic_high_green_priority);
            } else {
                priorityIcon.setImageResource(R.drawable.ic_priority);
            }
        }
    }


    public void setMasterTheme() {
        if (SettingUtil.getInstance(AddEditTaskActivity.this).isDarkTheme()) {
            setTheme(R.style.FeedActivityThemeDark);
            return;
        }
        switch (getFlag()) {
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            case 3:
                setTheme(R.style.AppTheme3);
                break;
            case 4:
                setTheme(R.style.AppTheme4);
                break;
            case 5:
                setTheme(R.style.AppTheme5);
                break;
            case 6:
                setTheme(R.style.AppTheme6);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }


    public Integer getFlag() {
        SharedPreferences sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedpreferences.getInt("theme", 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_STORAGE:
                new MaterialFilePicker()
                        .withActivity(AddEditTaskActivity.this)
                        .withCloseMenu(true)
                        .withHiddenFiles(true)
                        .withFilter(Pattern.compile(".*\\.(jpg|jpeg|png)$"))
                        // Don't apply filter to directories names
                        .withFilterDirectories(false)
                        .withRequestCode(FILE_PICKER_REQUEST_CODE)
                        .start();
                break;
            case WRITE_EXTERNAL_STORAGE_DRAW:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(new Intent(AddEditTaskActivity.this, DrawingActivity.class), DRAW_REQUEST);
                }
                break;
            case WRITE_EXTERNAL_STORAGE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentValues contentValues = new ContentValues();
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA_ACTION);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FILE_PICKER_REQUEST_CODE:
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    String storagePath = Environment.getExternalStorageDirectory().toString() + "/TaskRoom";
                    File directoryApp = new File(storagePath);
                    if (!directoryApp.exists()) {
                        directoryApp.mkdirs();
                    }
                    File storageFile = new File(storagePath, "storage" + new Date().getTime() + ".png");
                    OutputStream storageOs = null;
                    try {
                        storageOs = new FileOutputStream(storageFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inSampleSize = 8;
                    FileInputStream fileInputStream;
                    Bitmap storageBitmap = null;
                    try {
                        fileInputStream = new FileInputStream(filePath);
                        storageBitmap = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    storageBitmap.compress(Bitmap.CompressFormat.PNG, 100, storageOs);
                    try {
                        storageOs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Attachments fileAttachment = new Attachments("jpg", storageFile.getAbsolutePath(), tempTaskID, 0L, 0L);
                    attachmentsViewModel.insert(fileAttachment);
                    attachmentsAdapter.notifyDataSetChanged();
                    break;
                case RESUEST_RECORD_AUDIO:
                    Attachments attachments = new Attachments("3gp", data.getStringExtra("outputFile"), tempTaskID, 0L, 0L);
                    attachmentsViewModel.insert(attachments);
                    attachmentsAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_CAMERA_ACTION:
                    String path = Environment.getExternalStorageDirectory().toString() + "/TaskRoom";
                    File directory = new File(path);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File file = new File(path, "camera" + new Date().getTime() + ".png");
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = null;
                    try {
                        bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Attachments atach = new Attachments("jpg", file.getAbsolutePath(), tempTaskID, 0L, 0L);
                    attachmentsViewModel.insert(atach);
                    attachmentsAdapter.notifyDataSetChanged();

                    break;
                case DRAW_REQUEST:
                    Attachments attachment = new Attachments("jpg", data.getExtras().getString("drawPath"), tempTaskID, 0L, 0L);
                    attachmentsViewModel.insert(attachment);
                    attachmentsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    public interface ClickAddSubTaskListener {
        void addSubTaskListener();
    }
}
