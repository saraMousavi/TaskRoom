package ir.android.taskroom.ui.activity.reminder;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
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
import ir.android.taskroom.data.db.entity.Reminders;
import ir.android.taskroom.data.db.factory.AttachmentsViewModelFactory;
import ir.android.taskroom.databinding.RemindersAddActivityBinding;
import ir.android.taskroom.ui.adapters.AttachmentsAdapter;
import ir.android.taskroom.ui.fragment.TasksPriorityTypeBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatDayBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatPeriodBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatTypeBottomSheetFragment;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.calender.TimePickerDialog;
import ir.android.taskroom.viewmodels.AttachmentsViewModel;
import ir.android.taskroom.viewmodels.ReminderViewModel;

public class AddEditReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener
        , TasksRepeatTypeBottomSheetFragment.RepeatTypeClickListener
        , TasksRepeatDayBottomSheetFragment.RepeatDayClickListener
        , TasksRepeatPeriodBottomSheetFragment.RepeatPeriodClickListener
        , TasksPriorityTypeBottomSheetFragment.PriorityTypeClickListener {
    private TextInputEditText reminderNameEdit, reminderComment;
    private FloatingActionButton fabInsertReminders, fabInsertReminders2;
    private ConstraintLayout startDateConstraint,
            repeatTypeConstraint, priorityTypeContraint, uploadFileContraint,
            reminderTimeConstraint, reminderTypeConstraint;
    private TextView reminderTime, repeatTypeVal, priorityVal;
    private AppBarLayout mAppBarLayout;
    private RemindersAddActivityBinding remindersAddActivityBinding;
    private String datepickerVal;
    private ReminderViewModel reminderViewModel;
    private SharedPreferences sharedPreferences;
    private String completedDateVal = "";
    private RadioGroup reminderTypeGroup;
    private Long tempReminderID;
    private boolean isEditActivity = false, isActive = true;
    private Reminders clickedReminder;
    private SwitchCompat reminders_active;
    private JobScheduler mScheduler;
    private boolean isReminerTimeChange = false;
    private LinearLayout uploadChoose;
    private ImageView cameraIcon, storageIcon, priorityIcon;
    private LifeCycleCallBackManager lifeCycleCallBackManager;
    private RecyclerView attachedRecyclerView;
    private AttachmentsAdapter attachmentsAdapter;
    private AttachmentsViewModel attachmentsViewModel;
    private Integer reminderTypeVal;
    private DateTime calenderClickedDate = null;
    private CollapsingToolbarLayout toolBarLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setMasterTheme();
        super.onCreate(savedInstanceState);
        init();
        onClickListener();
        initSpinners();
        insertTempReminder();
        initRecyclerViews();
    }

    private void initRecyclerViews() {
        AttachmentsViewModelFactory attachmentFactory = new AttachmentsViewModelFactory(getApplication(), tempReminderID);
        attachmentsViewModel = ViewModelProviders.of(this, attachmentFactory).get(AttachmentsViewModel.class);
        attachmentsAdapter = new AttachmentsAdapter(attachmentsViewModel, AddEditReminderActivity.this);
        attachmentsViewModel.getAllRemindersAttachments().observe(this, new Observer<List<Attachments>>() {
            @Override
            public void onChanged(List<Attachments> attachments) {
                attachmentsAdapter.submitList(attachments);
                attachedRecyclerView.setAdapter(attachmentsAdapter);
            }
        });
        attachedRecyclerView.setLayoutManager(new GridLayoutManager(AddEditReminderActivity.this, 3, RecyclerView.VERTICAL, false));
    }

    private void initSpinners() {
        ArrayList<String> remindTimeArray = new ArrayList<>();
        remindTimeArray.add(getString(R.string.dontRemind));
        remindTimeArray.add(getString(R.string.remindInEndDate));
        //@TODO change this translation
        remindTimeArray.add(getString(R.string.remindInAdvance));
        ArrayAdapter<String> remindTimeAdapter = new ArrayAdapter<>(AddEditReminderActivity.this,
                android.R.layout.simple_spinner_dropdown_item, remindTimeArray);
    }


    private void onClickListener() {
        fabInsertReminders.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    insertReminder();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        fabInsertReminders2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    insertReminder();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;
            boolean isShow = false;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //@TODO add slide down animation
                    fabInsertReminders2.setVisibility(View.VISIBLE);
                    toolBarLayout.setTitle(reminderNameEdit.getText().toString());
                    toolbar.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    //@TODO add slide up animation
                    fabInsertReminders2.setVisibility(View.GONE);
                    toolBarLayout.setTitle(" ");
                    toolbar.setVisibility(View.INVISIBLE);
                    isShow = false;
                }
            }
        });
        startDateConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime dateTime = new DateTime();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        AddEditReminderActivity.this,
                        dateTime.getHourOfDay(),
                        dateTime.getMinuteOfHour(),
                        true
                );
                timePickerDialog.show(getSupportFragmentManager(), "startTimePickerDialog");

            }
        });

        repeatTypeConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksRepeatTypeBottomSheetFragment tasksRepeatTypeDialog = new TasksRepeatTypeBottomSheetFragment();
                tasksRepeatTypeDialog.show(getSupportFragmentManager(), "tag");
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
        reminders_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isActive = isChecked;
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
                FilePickUtils filePickUtils = new FilePickUtils(AddEditReminderActivity.this, onFileChoose);
                lifeCycleCallBackManager = filePickUtils.getCallBackManager();
                filePickUtils.requestImageCamera(FilePickUtils.CAMERA_PERMISSION, true, true);
//                requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
            }
        });
        storageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleAnimation(false);
                FilePickUtils filePickUtils = new FilePickUtils(AddEditReminderActivity.this, onFileChoose);
                lifeCycleCallBackManager = filePickUtils.getCallBackManager();
                filePickUtils.requestImageGallery(FilePickUtils.STORAGE_PERMISSION_IMAGE, true, false, true);
            }
        });
    }

    private FilePickUtils.OnFileChoose onFileChoose = new FilePickUtils.OnFileChoose() {
        @Override
        public void onFileChoose(String fileUri, int requestCode, int size) {
            File imgFile = new File(fileUri);
            if (imgFile.exists()) {
                Attachments attachments = new Attachments("jpg", fileUri, 0L, tempReminderID, 0L);
                attachmentsViewModel.insert(attachments);
                attachmentsAdapter.notifyDataSetChanged();

            }

        }
    };

    private void insertTempReminder() {
        if(getIntent().getExtras() != null && getIntent().getExtras().getString("calenderClickedDate") != null) {
            calenderClickedDate = new DateTime(getIntent().getExtras().getString("calenderClickedDate"));
        }
        Reminders reminders = new Reminders(0, "", "",
                0, "", "", 0, 0, 1, "0", false);

        try {
            if (isEditActivity) {
                tempReminderID = clickedReminder.getReminders_id();
            } else {
                tempReminderID = reminderViewModel.insert(reminders);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("tempReminderID");
            editor.putLong("tempReminderID", tempReminderID);
            editor.apply();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        remindersAddActivityBinding = DataBindingUtil.setContentView(AddEditReminderActivity.this, R.layout.reminders_add_activity);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AddEditReminderActivity.this);
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        remindersAddActivityBinding.setReminderViewModel(reminderViewModel);
        fabInsertReminders = findViewById(R.id.fabInsertReminders);
        fabInsertReminders2 = findViewById(R.id.fabInsertReminders2);
        reminderNameEdit = findViewById(R.id.reminderNameEdit);
        reminderComment = findViewById(R.id.reminderComment);
        startDateConstraint = findViewById(R.id.startDateConstraint);
        reminderTime = findViewById(R.id.reminderTimeVal);
        uploadChoose = findViewById(R.id.uploadChoose);
        cameraIcon = findViewById(R.id.cameraIcon);
        storageIcon = findViewById(R.id.storageIcon);
        priorityIcon = findViewById(R.id.priorityIcon);
        //@TODO why hour and minute  has inversed in ui
        reminderTime.setText(Init.getCurrentTime());
        repeatTypeVal = findViewById(R.id.repeatTypeVal);
        priorityVal = findViewById(R.id.priorityVal);
        reminderTimeConstraint = findViewById(R.id.reminderTimeConstraint);
        reminderTypeConstraint = findViewById(R.id.reminderTypeConstraint);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        repeatTypeConstraint = findViewById(R.id.repeatTypeConstraint);
        priorityTypeContraint = findViewById(R.id.priorityTypeContraint);
        uploadFileContraint = findViewById(R.id.uploadFileContraint);
        reminderTypeGroup = findViewById(R.id.reminderTypeGroup);
        reminders_active = findViewById(R.id.reminders_active);
        attachedRecyclerView = findViewById(R.id.attachedRecyclerView);
        toolBarLayout = findViewById(R.id.toolbar_layout);
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        datepickerVal = Init.getCurrentTime();
        RadioButton radioButton = reminderTypeGroup.findViewWithTag("notification");
        if (radioButton.isChecked()) {
            radioButton.setChecked(true);
        }

        Intent intent = getIntent();

        if (intent.hasExtra("clickedReminder")) {
            clickedReminder = (Reminders) intent.getExtras().getSerializable("clickedReminder");
            editableRemindersFields();
        }
//        List<Map<View, Boolean>> views = new ArrayList<>();
//        Map<View, Boolean> viewMap = new HashMap<>();
//        viewMap.put(mAppBarLayout, true);
//        views.add(viewMap);
//        viewMap = new HashMap<>();
//        viewMap.put(reminderNameEdit, true);
//        views.add(viewMap);
//        viewMap = new HashMap<>();
//        viewMap.put(fabInsertReminders, false);
//        views.add(viewMap);
//        viewMap = new HashMap<>();
//        viewMap.put(fabInsertReminders2, false);
//        views.add(viewMap);
//        Init.setViewBackgroundDependOnTheme(views, AddEditReminderActivity.this, sharedPreferences.getBoolean("NIGHT_MODE", false));
    }

    private void editableRemindersFields() {
        reminderNameEdit.setText(clickedReminder.getReminders_title());
        reminderTime.setText(clickedReminder.getReminders_time());
        priorityIcon.setImageResource(R.drawable.ic_priority);
        String priorityStringVal = getString(R.string.nonePriority);
        priorityVal.setVisibility(View.VISIBLE);
        if (clickedReminder.getReminders_priority() == 1) {
            priorityStringVal = getString(R.string.low);
            priorityIcon.setImageResource(R.drawable.ic_low_yellow_priority);
        } else if (clickedReminder.getReminders_priority() == 2) {
            priorityStringVal = getString(R.string.medium);
            priorityIcon.setImageResource(R.drawable.ic_medium_orange_priority);
        } else if (clickedReminder.getReminders_priority() == 3) {
            priorityStringVal = getString(R.string.high);
            priorityIcon.setImageResource(R.drawable.ic_high_green_priority);
        }
        priorityVal.setText(priorityStringVal);
        repeatTypeVal.setVisibility(View.VISIBLE);
        repeatTypeVal.setText(clickedReminder.getReminders_repeatedday());
        reminderComment.setText(clickedReminder.getReminders_comment());
        ((RadioButton) reminderTypeGroup.getChildAt(clickedReminder.getReminders_type())).setChecked(true);
        reminders_active.setChecked(clickedReminder.getReminders_active() == 1 ? true : false);
        isEditActivity = true;
        datepickerVal = clickedReminder.getReminders_time();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void insertReminder() throws ExecutionException, InterruptedException {
        if (reminderNameEdit.getText().toString().isEmpty()) {
            Snackbar
                    .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.enterReminderName), Snackbar.LENGTH_LONG)
                    .show();
            return;
        }
        Integer priorityIntVal = 0;
        if (priorityVal.getText().toString().equals(getString(R.string.low))) {
            priorityIntVal = 1;
        } else if (priorityVal.getText().toString().equals(getString(R.string.medium))) {
            priorityIntVal = 2;
        } else if (priorityVal.getText().toString().equals(getString(R.string.high))) {
            priorityIntVal = 3;
        }

        RadioButton reminderType = findViewById(reminderTypeGroup.getCheckedRadioButtonId());
        reminderTypeVal = reminderType == null ? 0 : reminderType.getText().toString().equals(getString(R.string.notification)) ? 0 : 1;
        String workID = "0";
        if (isActive) {
            String id = createWorkRequest();
            if (id.equals("-1")) {
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.validtimepast), Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            workID = id;
        }
        Reminders reminders = new Reminders(reminderTypeVal
                , reminderComment.getText().toString(), reminderTime.getText().toString(), priorityIntVal, reminderNameEdit.getText().toString(),
                repeatTypeVal.getText().toString(), 0, isActive ? 1 : 0,
                0, workID, attachmentsAdapter.getItemCount() > 0);

        if (isEditActivity) {
            if (isReminerTimeChange || !isActive) {
                if (clickedReminder.getWork_id().contains(",")) {
                    for (String requestId : clickedReminder.getWork_id().split(",")) {
                        WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(requestId));
                    }
                } else if (!clickedReminder.getWork_id().equals("0")) {
                    WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(clickedReminder.getWork_id()));
                }
            }
            reminders.setReminders_update(Init.convertDateTimeToInteger(Init.getCurrentDateTimeWithSecond()));
            reminders.setReminders_crdate(clickedReminder.getReminders_crdate());
        } else {
            if (getIntent().getExtras() == null) {
                DateTime dateTime1 = Init.getCurrentDateTimeWithSecond();
                DateTime crDate = Init.getTodayDateTimeWithTime(datepickerVal, 0, false);
                if (Integer.parseInt(datepickerVal.replaceAll(":", "")) < Integer.parseInt(dateTime1.getHourOfDay()
                        + "" + (dateTime1.getMinuteOfHour() < 10 ? "0" + dateTime1.getMinuteOfHour() : dateTime1.getMinuteOfHour()) +
                        "" + (dateTime1.getSecondOfMinute() < 10 ? "0" + dateTime1.getSecondOfMinute() : dateTime1.getSecondOfMinute()))) {
                    crDate = Init.getTodayDateTimeWithTime(datepickerVal, 1, false);
                }
                reminders.setReminders_crdate(Init.convertDateTimeToInteger(crDate));
            } else {
                calenderClickedDate = new DateTime(getIntent().getExtras().getString("calenderClickedDate"));
                reminders.setReminders_crdate(Init.convertDateTimeToInteger(calenderClickedDate));
            }

        }
        reminders.setReminders_id(tempReminderID);
        reminderViewModel.update(reminders);
        setResult(RESULT_OK);
        finish();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private String createWorkRequest() {
        DateTime dateTime1 = Init.getCurrentDateTimeWithSecond();
        DateTime dateTime2;
        //@todo count time in clicked date in calender
        if (calenderClickedDate == null) {
            dateTime2 = Init.getTodayDateTimeWithTime(datepickerVal, 0, false);
        } else {
            dateTime2 = calenderClickedDate;
            long selectedDate = Long.parseLong(calenderClickedDate.getYear() + "" +
                    (calenderClickedDate.getMonthOfYear() < 10 ? "0" + calenderClickedDate.getMonthOfYear() : calenderClickedDate.getMonthOfYear())
                    + "" + (calenderClickedDate.getDayOfMonth() < 10 ? "0" + calenderClickedDate.getDayOfMonth() : calenderClickedDate.getDayOfMonth())
                    + "" + (calenderClickedDate.getHourOfDay() < 10 ? "0" + calenderClickedDate.getHourOfDay() : calenderClickedDate.getHourOfDay())
                    + "" + (calenderClickedDate.getMinuteOfHour() < 10 ? "0" + calenderClickedDate.getMinuteOfHour() : calenderClickedDate.getMinuteOfHour())
                    + "" + (calenderClickedDate.getSecondOfMinute() < 10 ? "0" + calenderClickedDate.getSecondOfMinute() : calenderClickedDate.getSecondOfMinute()));
            long stringStartedDate = Long.parseLong(dateTime1.getYear() + "" +
                    (dateTime1.getMonthOfYear() < 10 ? "0" + dateTime1.getMonthOfYear() : dateTime1.getMonthOfYear()) + "" +
                    (dateTime1.getDayOfMonth() < 10 ? "0" + dateTime1.getDayOfMonth() : dateTime1.getDayOfMonth()) + "" +
                    (dateTime1.getHourOfDay() < 10 ? "0" + dateTime1.getHourOfDay() : dateTime1.getHourOfDay())
                    + "" + (dateTime1.getMinuteOfHour() < 10 ? "0" + dateTime1.getMinuteOfHour() : dateTime1.getMinuteOfHour()) +
                    "" + (dateTime1.getSecondOfMinute() < 10 ? "0" + dateTime1.getSecondOfMinute() : dateTime1.getSecondOfMinute()));
            if (selectedDate < stringStartedDate) {
                return "-1";//zaman entekhab shode gozashte ast
            }
        }
        if (Integer.parseInt(datepickerVal.replaceAll(":", "")) < Integer.parseInt(dateTime1.getHourOfDay()
                + "" + (dateTime1.getMinuteOfHour() < 10 ? "0" + dateTime1.getMinuteOfHour() : dateTime1.getMinuteOfHour()) +
                "" + (dateTime1.getSecondOfMinute() < 10 ? "0" + dateTime1.getSecondOfMinute() : dateTime1.getSecondOfMinute()))) {
            dateTime2 = Init.getTodayDateTimeWithTime(datepickerVal, 1, false);
        }
        Interval interval = new Interval(dateTime1, dateTime2);
        long hour = interval.toDuration().getStandardMinutes() / 60;
        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
        long second = 0;
        if (minute == 0 && hour == 0) {
            second = interval.toDuration().getStandardSeconds();
        }

        Toast.makeText(getApplicationContext(), getString(R.string.remindeTime) + hour + ":" + minute + ":" + second, Toast.LENGTH_LONG).show();
        return Init.requestWork(getApplicationContext(), reminderNameEdit.getText().toString(), reminderTypeVal,
                Init.getWorkRequestPeriodicIntervalMillis(getResources(), repeatTypeVal.getText().toString()),
                interval.toDurationMillis(), !repeatTypeVal.getText().toString().isEmpty(), true);
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute) {
        datepickerVal = (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
        reminderTime.setText(datepickerVal);
        isReminerTimeChange = true;
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

    public void setMasterTheme() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AddEditReminderActivity.this);
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
