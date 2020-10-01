package ir.android.persiantask.ui.activity.task;

import android.annotation.SuppressLint;
import android.app.job.JobScheduler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import androidx.viewbinding.ViewBinding;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.imagepicker.FilePickUtils;
import com.imagepicker.LifeCycleCallBackManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Attachments;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.factory.AttachmentsViewModelFactory;
import ir.android.persiantask.data.db.factory.ProjectsViewModelFactory;
import ir.android.persiantask.data.db.factory.SubTasksViewModelFactory;
import ir.android.persiantask.data.db.factory.TasksViewModelFactory;
import ir.android.persiantask.databinding.TasksAddActivityBinding;
import ir.android.persiantask.ui.adapters.AttachmentsAdapter;
import ir.android.persiantask.ui.adapters.SubTasksAdapter;
import ir.android.persiantask.ui.fragment.TasksPriorityTypeBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatDayBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatPeriodBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatTypeBottomSheetFragment;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.utils.calender.DatePickerDialog;
import ir.android.persiantask.utils.calender.PersianCalendar;
import ir.android.persiantask.utils.calender.TimePickerDialog;
import ir.android.persiantask.viewmodels.AttachmentsViewModel;
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
    private String datepickerVal;
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
    private JobScheduler mScheduler;
    private int lastProjectID;
    private CollapsingToolbarLayout toolBarLayout;
    private LifeCycleCallBackManager lifeCycleCallBackManager;
    private RecyclerView attachedRecyclerView;
    private AttachmentsAdapter attachmentsAdapter;
    private AttachmentsViewModel attachmentsViewModel;
    private LinearLayout uploadChoose;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        Tasks tasks = new Tasks("", 0, 0, 0,
                selectedProject == null ? lastProjectID : selectedProject.getProject_id(), "", 0, 0,
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
                    lastProjectID = project.getProject_id();
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
        AttachmentsViewModelFactory attachmentFactory = new AttachmentsViewModelFactory(getApplication(), tempTaskID);
        attachmentsViewModel = ViewModelProviders.of(this, attachmentFactory).get(AttachmentsViewModel.class);
        attachmentsAdapter = new AttachmentsAdapter(attachmentsViewModel, AddEditTaskActivity.this);
        attachmentsViewModel.getAllTasksAttachments().observe(this, new Observer<List<Attachments>>() {
            @Override
            public void onChanged(List<Attachments> attachments) {
                System.out.println("attachments.size() = " + attachments.size());
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

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //@TODO add slide down animation
                    ViewCompat.animate(fabInsertTask2).scaleX(1).scaleY(1).start();
                    toolBarLayout.setTitle(taskNameEdit.getText().toString());
                    isShow = true;
                } else if (isShow) {
                    toolBarLayout.setTitle(" ");
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
                if(uploadChoose.getVisibility() == View.VISIBLE){
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
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
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
        if(visible){
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
                Attachments attachments = new Attachments("jpg", fileUri, tempTaskID);
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
        startTextVal = findViewById(R.id.startTextVal);
        //@TODO why hour and minute  has inversed in ui
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
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
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

    private void viewsAnimation() {
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        fabInsertTask.startAnimation(logoMoveAnimation);
    }

    private void editableTaskFields() {
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
        Tasks tasks = new Tasks(taskNameEdit.getText().toString(), priorityIntVal, isCompleted ? 1 : 0, 0,
                selectedProject.getProject_id(), startTextVal.getText().toString(),
                reminderType == null ? null : (reminderType.getText().toString().equals(getString(R.string.notification)) ? 0 : 1),
                reminderTime.getSelectedItemPosition(), repeatTypeVal.getText().toString(),
                completedDateVal.isEmpty() ? endTextVal.getText().toString() : completedDateVal, 1,
                tasksComment.getText().toString());
        tasks.setTasks_id(tempTaskID);
        taskViewModel.update(tasks);
//        int diffDay =Init.integerFormatFromStringDate(datepickerVal) - Init.integerFormatFromStringDate(Init.getCurrentTime());
//        int diffTime =Init.integerFormatFromStringTime(datepickerVal) - Init.integerFormatFromStringTime(Init.getCurrentTime());
//        int diff = diffDay * 24 * 60 * 60 ;
//        Init.scheduleJob(mScheduler, getPackageName(), mScheduler.getAllPendingJobs().size(), diff * 60);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
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
        datepickerVal += " " + (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay)
                + ":" + (minute < 10 ? "0" + minute : minute);
        if (view.getTag().equals("startTimePickerDialog")) {
            startTextVal.setText(datepickerVal);
            startTextVal.setVisibility(View.VISIBLE);
        } else if (view.getTag().equals("endTimePickerDialog")) {
            endTextVal.setText(datepickerVal);
            endTextVal.setVisibility(View.VISIBLE);
            reminderTimeConstraint.setVisibility(View.VISIBLE);
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
        if (lifeCycleCallBackManager != null) {
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
//                CropImage.activity().setAllowRotation(true).setCropShape(CropImageView.CropShape.OVAL)
//                        .setGuidelinesColor(R.color.red)
//                        .setAllowCounterRotation(true)
//                        .setCropMenuCropButtonIcon(R.drawable.about_calender)
//                        .setAutoZoomEnabled(true).start(AddEditTaskActivity.this);
//            } else {
//                Toast.makeText(AddEditTaskActivity.this, "cancel", Toast.LENGTH_LONG);
//            }
//        }
//        if(requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE){
//            if(mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                System.out.println("mCropImageUri = " + mCropImageUri);
//            } else {
//                Toast.makeText(AddEditTaskActivity.this, "cancel pick", Toast.LENGTH_LONG);
//            }
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main,menu);
//        return true;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK){
//            Uri imageUri = CropImage.getPickImageResultUri(AddEditTaskActivity.this, data);
////            CropImage.activity(imageUri).setAllowRotation(true).setGuidelines(CropImageView.Guidelines.ON)
////                    .setAutoZoomEnabled(true).setActivityMenuIconColor(getColor(R.color.black)).setGuidelinesColor(getColor(R.color.red))
////                    .setCropShape(CropImageView.CropShape.OVAL);
//
//            if(CropImage.isReadExternalStoragePermissionsRequired(AddEditTaskActivity.this, imageUri)) {
//                mCropImageUri = imageUri;
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
//            } else {
//                pickedImage.setImageUriAsync(imageUri);
//            }
//        }
//    }

    public interface ClickAddSubTaskListener {
        void addSubTaskListener();
    }
}
