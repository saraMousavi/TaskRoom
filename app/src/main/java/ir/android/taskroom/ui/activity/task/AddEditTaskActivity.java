package ir.android.taskroom.ui.activity.task;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.gson.Gson;
import com.imagepicker.FilePickUtils;
import com.imagepicker.LifeCycleCallBackManager;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import ir.android.taskroom.R;
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
import ir.android.taskroom.ui.activity.SplashActivity;
import ir.android.taskroom.ui.adapters.AttachmentsAdapter;
import ir.android.taskroom.ui.adapters.SubTasksAdapter;
import ir.android.taskroom.ui.fragment.TasksPriorityTypeBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatDayBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatPeriodBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatTypeBottomSheetFragment;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.calender.DatePickerDialog;
import ir.android.taskroom.utils.calender.PersianCalendar;
import ir.android.taskroom.utils.calender.TimePickerDialog;
import ir.android.taskroom.viewmodels.AttachmentsViewModel;
import ir.android.taskroom.viewmodels.CategoryViewModel;
import ir.android.taskroom.viewmodels.ProjectViewModel;
import ir.android.taskroom.viewmodels.SubTasksViewModel;
import ir.android.taskroom.viewmodels.TaskViewModel;

public class AddEditTaskActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener
        , DatePickerDialog.OnDateSetListener
        , TasksRepeatTypeBottomSheetFragment.RepeatTypeClickListener
        , TasksRepeatDayBottomSheetFragment.RepeatDayClickListener
        , TasksRepeatPeriodBottomSheetFragment.RepeatPeriodClickListener
        , TasksPriorityTypeBottomSheetFragment.PriorityTypeClickListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private TextInputEditText taskNameEdit, tasksComment;
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
    private ImageView projectIcon, completeIcon, priorityIcon, cameraIcon, storageIcon;
    private Projects selectedProject;
    private boolean isCompleted;
    private String completedDateVal = "";
    private RadioGroup reminderTypeGroup;
    private Long tempTaskID;
    private boolean isEditActivity = false;
    private Tasks clickedTask;
    private long lastProjectID;
    private CollapsingToolbarLayout toolBarLayout;
    private LifeCycleCallBackManager lifeCycleCallBackManager;
    private RecyclerView attachedRecyclerView;
    private AttachmentsAdapter attachmentsAdapter;
    private AttachmentsViewModel attachmentsViewModel;
    private LinearLayout uploadChoose;
    private Integer reminderTypeVal;
    private boolean isReminerTimeChange = false;
    private DateTime calenderClickedDate = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteSubTask), Snackbar.LENGTH_LONG)
                        .show();
            }
        }).attachToRecyclerView(subtaskRecyclerView);
        AttachmentsViewModelFactory attachmentFactory = new AttachmentsViewModelFactory(getApplication(), tempTaskID);
        attachmentsViewModel = ViewModelProviders.of(this, attachmentFactory).get(AttachmentsViewModel.class);
        attachmentsAdapter = new AttachmentsAdapter(attachmentsViewModel, AddEditTaskActivity.this);
        attachmentsViewModel.getAllTasksAttachments().observe(this, new Observer<List<Attachments>>() {
            @Override
            public void onChanged(List<Attachments> attachments) {
                attachmentsAdapter.submitList(attachments);
                attachedRecyclerView.setAdapter(attachmentsAdapter);
            }
        });
        attachedRecyclerView.setLayoutManager(new GridLayoutManager(AddEditTaskActivity.this, 3, RecyclerView.VERTICAL, false));
    }

    private void onClickEvents() {
        fabInsertTask.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                insertTasks();
            }
        });
        fabInsertTask2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                        repeatTypeVal.setText("");
                        break;
                    case 1:
                        reminderTypeConstraint.setVisibility(View.VISIBLE);
                        repeatTypeConstraint.setVisibility(View.GONE);
                        repeatTypeVal.setText("");
                        Init.fadeVisibelityView(reminderTypeConstraint);
                        break;
                    case 2:
                        reminderTypeConstraint.setVisibility(View.VISIBLE);
                        repeatTypeConstraint.setVisibility(View.GONE);
                        repeatTypeVal.setText("");
                        Init.fadeVisibelityView(reminderTypeConstraint);
                        if (reminderTime.getAdapter().getCount() < 4) {
                            repeatTypeConstraint.setVisibility(View.VISIBLE);
                            Init.fadeVisibelityView(repeatTypeConstraint);
                            Snackbar
                                    .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.chooseadvancerepeattype), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                        break;
                    case 3:
                        reminderTypeConstraint.setVisibility(View.VISIBLE);
                        repeatTypeConstraint.setVisibility(View.VISIBLE);
                        Init.fadeVisibelityView(reminderTypeConstraint);
                        Init.fadeVisibelityView(repeatTypeConstraint);
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

        uploadFileContraint.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
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
                FilePickUtils filePickUtils = new FilePickUtils(AddEditTaskActivity.this, onFileChoose);
                lifeCycleCallBackManager = filePickUtils.getCallBackManager();
                filePickUtils.requestImageCamera(FilePickUtils.CAMERA_PERMISSION, true, true);
            }
        });
        storageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleAnimation(false);
                FilePickUtils filePickUtils = new FilePickUtils(AddEditTaskActivity.this, onFileChoose);
                lifeCycleCallBackManager = filePickUtils.getCallBackManager();
                filePickUtils.requestImageGallery(FilePickUtils.STORAGE_PERMISSION_IMAGE, false, false, true);
            }
        });
    }

    private void scaleAnimation(boolean visible) {
        if (visible) {
            Animation anim = new ScaleAnimation(
                    0, 1f, // Start and end values for the X axis scaling
                    1f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(500);
            uploadChoose.startAnimation(anim);
            uploadChoose.setVisibility(View.VISIBLE);
        } else {
            Animation anim = new ScaleAnimation(
                    1f, 0, // Start and end values for the X axis scaling
                    1f, 1f, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(500);
            uploadChoose.startAnimation(anim);
            uploadChoose.setVisibility(View.GONE);
        }
    }

    private FilePickUtils.OnFileChoose onFileChoose = new FilePickUtils.OnFileChoose() {
        @Override
        public void onFileChoose(String fileUri, int requestCode, int size) {
            File imgFile = new File(fileUri);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Attachments attachments = new Attachments("jpg", fileUri, tempTaskID, 0L, 0L);
                attachmentsViewModel.insert(attachments);
                attachmentsAdapter.notifyDataSetChanged();

            }

        }
    };

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
        startTextVal = findViewById(R.id.reminderTimeVal);
        startTextVal.setText(Init.getCurrentDate());
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
        uploadChoose = findViewById(R.id.uploadChoose);
        completeIcon.setTag(R.drawable.ic_black_circle);
        startDatepickerVal = Init.getCurrentDate();
        ProjectsViewModelFactory projectFactory = new ProjectsViewModelFactory(getApplication(), null);

        TasksViewModelFactory taskFactory = new TasksViewModelFactory(getApplication(), sharedPreferences.getLong("selectedProjectID", 0));
        projectViewModel = ViewModelProviders.of(this, projectFactory).get(ProjectViewModel.class);
        taskViewModel = ViewModelProviders.of(this, taskFactory).get(TaskViewModel.class);


        Intent intent = getIntent();

        if (intent.hasExtra("clickedTask")) {
            clickedTask = (Tasks) intent.getExtras().getSerializable("clickedTask");
            editableTaskFields();
        }
        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
            Snackbar
                    .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.enterTaskName), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        reminderTypeVal = reminderType == null ? 0 : reminderType.getText().toString().equals(getString(R.string.notification)) ? 0 : 1;
        if (isEditActivity) {
            if (isReminerTimeChange || isCompleted) {
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
        }
        String workID = "0";
        if (!isCompleted) {
            workID = createWorkRequest();
            if (workID.equals("-1")) {
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.validstartdateandenddate), Snackbar.LENGTH_LONG)
                        .show();
                return;
            } else if (workID.equals("-2")) {
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.validstartdatepast), Snackbar.LENGTH_LONG)
                        .show();
            }
        }

        Tasks tasks = new Tasks(taskNameEdit.getText().toString(), priorityIntVal, isCompleted ? 1 : 0, 0,
                selectedProject.getProject_id(), startTextVal.getText().toString(),
                reminderTypeVal, reminderTime.getSelectedItemPosition(), repeatTypeVal.getText().toString(),
                endTextVal.getText().toString(), 1, tasksComment.getText().toString(),
                workID, attachmentsAdapter.getItemCount() > 0, completedDate.getText().toString());
        System.out.println("tasks.getTasks_remindertime() = " + tasks.getTasks_remindertime());
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String createWorkRequest() {
        DateTime dateTime1 = null;
        DateTime dateTime2 = null;
        Long newStartInterval = null;
        if (reminderTime.getSelectedItemPosition() == 1) {
            dateTime1 = Init.getCurrentDateTimeWithSecond();
            dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(startDatepickerVal));
            if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                return "-2";//start date past
            }
        } else if (reminderTime.getAdapter().getCount() > 3) {
            if (reminderTime.getSelectedItemPosition() == 3) {
                dateTime1 = Init.getCurrentDateTimeWithSecond();
                dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(startDatepickerVal));
                //if start date past and reminder time was advance
                if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                    long passedInterval = new Interval(dateTime2, dateTime1).toDurationMillis();
                    String repeatType = repeatTypeVal.getText().toString();
                    if (!repeatType.contains(",") && !repeatType.isEmpty()) {
                        String[] repeatTypeSplit = repeatType.split(" ");
                        String[] typePeriodVal = new String[]{getResources().getString(R.string.day), getResources().getString(R.string.week),
                                getResources().getString(R.string.month), getResources().getString(R.string.year)};
                        if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 24 * 60 * 60 * 1000L;
                        }
                        if (typePeriodVal[1].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 7 * 24 * 60 * 60 * 1000L;
                        }
                        if (typePeriodVal[2].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 30 * 24 * 60 * 60 * 1000L;
                        }
                        if (typePeriodVal[3].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 365 * 24 * 60 * 60 * 1000L;
                        }
                    } else {
                        if (!repeatType.isEmpty()) {
                            //if start date past and reminder time was custom day
                            dateTime2 = Init.getTodayDateTimeWithTime(startDatepickerVal, 1, true);
                        }
                    }
                }

            } else if (reminderTime.getSelectedItemPosition() == 2) {
                dateTime1 = Init.getCurrentDateTimeWithSecond();
                dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(endDatepickerVal));
                if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                    dateTime1 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(startDatepickerVal));
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        //tarihk shoru az payan kuchektar ast
                        return "-1";
                    } else {
                        //tarikh shoru va payan har do gazashte and
                        return "-2";
                    }
                }
            }
        } else {
            if (reminderTime.getSelectedItemPosition() == 2) {
                dateTime1 = Init.getCurrentDateTimeWithSecond();
                dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(startDatepickerVal));
                //if start date past and reminder time was advance
                if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                    long passedInterval = new Interval(dateTime2, dateTime1).toDurationMillis();
                    String repeatType = repeatTypeVal.getText().toString();
                    if (!repeatType.contains(",") && !repeatType.isEmpty()) {
                        String[] repeatTypeSplit = repeatType.split(" ");
                        String[] typePeriodVal = new String[]{getResources().getString(R.string.day), getResources().getString(R.string.week),
                                getResources().getString(R.string.month), getResources().getString(R.string.year)};
                        if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 24 * 60 * 60 * 1000L;
                        }
                        if (typePeriodVal[1].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 7 * 24 * 60 * 60 * 1000L;
                        }
                        if (typePeriodVal[2].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 30 * 24 * 60 * 60 * 1000L;
                        }
                        if (typePeriodVal[3].equals(repeatTypeSplit[2])) {
                            newStartInterval = passedInterval + 365 * 24 * 60 * 60 * 1000L;
                        }
                    }
                }
                //if start date past and reminder time was custom day
                if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                    dateTime2 = Init.getTodayDateTimeWithTime(startDatepickerVal, 1, true);
                }
            }
        }
        if (dateTime1 != null && dateTime2 != null) {
            if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                return "-1";
            }
            if (reminderTime.getSelectedItemPosition() != 0) {
                long interval = 0;
                if (newStartInterval != null) {
                    interval = newStartInterval;
                } else {
                    interval = new Interval(dateTime1, dateTime2).toDurationMillis();
                }
//            long hour = interval.toDuration().getStandardMinutes() / 60;
//            long minute = interval.toDuration().getStandardMinutes() - hour * 60;
//            long second = 0;
//            if (minute == 0 && hour == 0) {
//                second = interval.toDuration().getStandardSeconds();
//            }
//            Toast.makeText(getApplicationContext(), getString(R.string.remindeTime) + hour + ":" + minute + ":" + second, Toast.LENGTH_LONG).show();
                return Init.requestWork(getApplicationContext(), taskNameEdit.getText().toString(), reminderTypeVal,
                        Init.getWorkRequestPeriodicIntervalMillis(getResources(), repeatTypeVal.getText().toString()),
                        interval, !repeatTypeVal.getText().toString().isEmpty(), false);
            }
        }


        return "0";
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        String date = year + "/"
                + (monthOfYear < 10 ? "0" + monthOfYear : monthOfYear)
                + "/" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
        DateTime dateTime = new DateTime();
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
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
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute) {
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
            if (priorityVal.getText().toString().equals(getString(R.string.low))) {
                priorityIcon.setImageResource(R.drawable.ic_low_yellow_priority);
            } else if (priorityVal.getText().toString().equals(getString(R.string.medium))) {
                priorityIcon.setImageResource(R.drawable.ic_medium_orange_priority);
            } else if (priorityVal.getText().toString().equals(getString(R.string.high))) {
                priorityIcon.setImageResource(R.drawable.ic_high_green_priority);
            } else {
                priorityIcon.setImageResource(R.drawable.ic_priority);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (lifeCycleCallBackManager != null && permissions.length != 0) {
            lifeCycleCallBackManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setMasterTheme() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AddEditTaskActivity.this);
        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
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

    public interface ClickAddSubTaskListener {
        void addSubTaskListener();
    }
}
