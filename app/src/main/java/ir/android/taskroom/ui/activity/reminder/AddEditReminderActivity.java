package ir.android.taskroom.ui.activity.reminder;

import android.Manifest;
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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
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
import com.google.android.material.textfield.TextInputLayout;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
//import com.imagepicker.FilePickUtils;
//import com.imagepicker.LifeCycleCallBackManager;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import ir.android.taskroom.R;
import ir.android.taskroom.ui.activity.task.AddEditTaskActivity;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.data.db.entity.Attachments;
import ir.android.taskroom.data.db.entity.Reminders;
import ir.android.taskroom.data.db.factory.AttachmentsViewModelFactory;
import ir.android.taskroom.databinding.RemindersAddActivityBinding;
import ir.android.taskroom.ui.activity.DrawingActivity;
import ir.android.taskroom.ui.activity.RecordingActivity;
import ir.android.taskroom.ui.adapters.AttachmentsAdapter;
import ir.android.taskroom.ui.fragment.TasksPriorityTypeBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatDayBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatPeriodBottomSheetFragment;
import ir.android.taskroom.ui.fragment.TasksRepeatTypeBottomSheetFragment;
import ir.android.taskroom.utils.EnglishInit;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.objects.TasksReminderActions;
import ir.android.taskroom.viewmodels.AttachmentsViewModel;
import ir.android.taskroom.viewmodels.ReminderViewModel;

public class AddEditReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener
        , TasksRepeatTypeBottomSheetFragment.RepeatTypeClickListener
        , TasksRepeatDayBottomSheetFragment.RepeatDayClickListener
        , TasksRepeatPeriodBottomSheetFragment.RepeatPeriodClickListener
        , TasksPriorityTypeBottomSheetFragment.PriorityTypeClickListener {
    private TextInputEditText reminderNameEdit, reminderComment;
    private TextInputLayout reminderName;
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
    private boolean isReminerTimeChange = false;
    private LinearLayout uploadChoose;
    private ImageView cameraIcon, storageIcon, priorityIcon, recordIcon, drawIcon;
    private RecyclerView attachedRecyclerView;
    private AttachmentsAdapter attachmentsAdapter;
    private AttachmentsViewModel attachmentsViewModel;
    private Integer reminderTypeVal;
    private DateTime calenderClickedDate = null;
    private CollapsingToolbarLayout toolBarLayout;

    private static final int WRITE_EXTERNAL_STORAGE_DRAW = 201;
    private static final int RESUEST_RECORD_AUDIO = 100;
    private static final int REQUEST_CAMERA_ACTION = 200;
    private static final int DRAW_REQUEST = 300;
    private static final int WRITE_EXTERNAL_STORAGE_CAMERA = 400;
    private static final int FILE_PICKER_REQUEST_CODE = 500;
    private static final int WRITE_EXTERNAL_STORAGE_STORAGE = 600;
    private Uri imageUri;

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
            @Override
            public void onClick(View v) {
                insertReminder();
            }
        });
        fabInsertReminders2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertReminder();
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEditReminderActivity.this, AddEditReminderActivity.this,
                        dateTime.getHourOfDay(),
                        dateTime.getMinuteOfHour(),
                        true
                );
                timePickerDialog.show();

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
                ActivityCompat.requestPermissions(AddEditReminderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CAMERA);
            }
        });
        storageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleAnimation(false);
                ActivityCompat.requestPermissions(AddEditReminderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_STORAGE);
            }
        });

        recordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddEditReminderActivity.this, RecordingActivity.class), RESUEST_RECORD_AUDIO);
            }
        });
        drawIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(AddEditReminderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_DRAW);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_STORAGE:
                new MaterialFilePicker()
                        .withActivity(AddEditReminderActivity.this)
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
                    startActivityForResult(new Intent(AddEditReminderActivity.this, DrawingActivity.class), DRAW_REQUEST);
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

    private void insertTempReminder() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("calenderClickedDate") != null) {
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


    private void init() {
        remindersAddActivityBinding = DataBindingUtil.setContentView(AddEditReminderActivity.this, R.layout.reminders_add_activity);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(AddEditReminderActivity.this);
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        remindersAddActivityBinding.setReminderViewModel(reminderViewModel);
        fabInsertReminders = findViewById(R.id.fabInsertReminders);
        fabInsertReminders2 = findViewById(R.id.fabInsertReminders2);
        reminderNameEdit = findViewById(R.id.reminderNameEdit);
        reminderName = findViewById(R.id.reminderName);
        reminderComment = findViewById(R.id.reminderComment);
        startDateConstraint = findViewById(R.id.startDateConstraint);
        reminderTime = findViewById(R.id.reminderTimeVal);
        uploadChoose = findViewById(R.id.uploadChoose);
        cameraIcon = findViewById(R.id.cameraIcon);
        storageIcon = findViewById(R.id.storageIcon);
        recordIcon = findViewById(R.id.recordIcon);
        drawIcon = findViewById(R.id.drawIcon);
        priorityIcon = findViewById(R.id.priorityIcon);
        //@TODO why hour and minute  has inversed in ui
        reminderTime.setText(EnglishInit.getCurrentTime());
        repeatTypeVal = findViewById(R.id.repeatTypeVal);
        priorityVal = findViewById(R.id.priorityVal);
        reminderTimeConstraint = findViewById(R.id.reminderTimeConstraint);
        reminderTypeConstraint = findViewById(R.id.reminderTypeConstraint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            reminderTypeConstraint.setVisibility(View.GONE);
        }

        if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                reminderTime.setTextAppearance(R.style.numberTextInput);
            }
        }
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        repeatTypeConstraint = findViewById(R.id.repeatTypeConstraint);
        priorityTypeContraint = findViewById(R.id.priorityTypeContraint);
        uploadFileContraint = findViewById(R.id.uploadFileContraint);
        reminderTypeGroup = findViewById(R.id.reminderTypeGroup);
        reminders_active = findViewById(R.id.reminders_active);
        attachedRecyclerView = findViewById(R.id.attachedRecyclerView);
        toolBarLayout = findViewById(R.id.toolbar_layout);
        datepickerVal = EnglishInit.getCurrentTime();
        RadioButton radioButton = reminderTypeGroup.findViewWithTag("notification");
        if (radioButton.isChecked()) {
            radioButton.setChecked(true);
        }

        Intent intent = getIntent();

        if (intent.hasExtra("clickedReminder")) {
            clickedReminder = (Reminders) intent.getExtras().getSerializable("clickedReminder");
            editableRemindersFields();
        }
    }

    private void editableRemindersFields() {
        reminderNameEdit.setText(clickedReminder.getReminders_title());
        reminderTime.setText(clickedReminder.getReminders_time());
        priorityIcon.setImageResource(R.drawable.ic_priority);
        priorityVal.setVisibility(View.VISIBLE);
        String priorityStringVal = getString(R.string.nonePriority);
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

    private void insertReminder() {
        if (reminderNameEdit.getText().toString().isEmpty()) {
            String enterReminderName = getString(R.string.enterReminderName);
            if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                enterReminderName = getString(R.string.enterReminderName);
            }
            Snackbar snackbar = Snackbar
                    .make(getWindow().getDecorView().findViewById(android.R.id.content), enterReminderName, Snackbar.LENGTH_LONG);
            if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
            }
            snackbar.show();
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
                String validtimepast = getString(R.string.validtimepast);
                if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    validtimepast = getString(R.string.validtimepast);
                }
                Snackbar snackbar = Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), validtimepast, Snackbar.LENGTH_LONG);
                if (!SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                }
                snackbar.show();
                return;
            }
            workID = id;
        }
        Reminders reminders = new Reminders(reminderTypeVal
                , reminderComment.getText().toString(), reminderTime.getText().toString(), priorityIntVal, reminderNameEdit.getText().toString(),
                repeatTypeVal.getText().toString(), 0, isActive ? 1 : 0,
                0, workID, attachmentsAdapter.getItemCount() > 0);
        if (!isActive) {
            reminders.setReminders_update(Init.convertDateTimeToInteger(EnglishInit.getCurrentDateTimeWithSecond()));
        }

        if (isEditActivity) {
            if (clickedReminder.getWork_id().contains(",")) {
                for (String requestId : clickedReminder.getWork_id().split(",")) {
                    WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(requestId));
                }
            } else if (!clickedReminder.getWork_id().equals("0")) {
                WorkManager.getInstance(getApplicationContext()).cancelWorkById(UUID.fromString(clickedReminder.getWork_id()));
            }
            reminders.setReminders_crdate(Long.parseLong(clickedReminder.getReminders_crdate() / 1000000 + "" + reminderTime.getText().toString().replaceAll(":", "")));
        } else {
            if (getIntent().getExtras() == null) {
                DateTime dateTime1 = EnglishInit.getCurrentDateTimeWithSecond();
                DateTime crDate = EnglishInit.getTodayDateTimeWithSelectedTime(datepickerVal, 0, false);
                if (Integer.parseInt(datepickerVal.replaceAll(":", "")) < Integer.parseInt(dateTime1.getHourOfDay()
                        + "" + (dateTime1.getMinuteOfHour() < 10 ? "0" + dateTime1.getMinuteOfHour() : dateTime1.getMinuteOfHour()) +
                        "" + (dateTime1.getSecondOfMinute() < 10 ? "0" + dateTime1.getSecondOfMinute() : dateTime1.getSecondOfMinute()))) {
                    crDate = EnglishInit.getTodayDateTimeWithSelectedTime(datepickerVal, 1, false);
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

    private String createWorkRequest() {
        Reminders reminders = new Reminders(0, "", reminderTime.getText().toString(), 0, "", repeatTypeVal.getText().toString(), 0, 0, 0, "", false);
        TasksReminderActions tasksReminderActions = null;
        if(SettingUtil.getInstance(AddEditReminderActivity.this).isEnglishLanguage()){
            tasksReminderActions = EnglishInit.getDurationInWholeStateOfRemindersOrTasks(reminders, calenderClickedDate, getResources());
        } else {
            tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(reminders, calenderClickedDate, getResources());
        }
        if (tasksReminderActions.getRemainDuration() == -1) {
            return "-1";
        }
        String remindeTime = getString(R.string.remindeTime);
        if (SettingUtil.getInstance(getApplicationContext()).isEnglishLanguage()) {
            remindeTime = getString(R.string.remindeTime);
        }
        Toast.makeText(getApplicationContext(), remindeTime + tasksReminderActions.getRemainTime(), Toast.LENGTH_LONG).show();
        return Init.requestWork(getApplicationContext(), reminderNameEdit.getText().toString(), reminderComment.getText().toString(), reminderTypeVal,
                Init.getWorkRequestPeriodicIntervalMillis(getResources(), repeatTypeVal.getText().toString()),
                tasksReminderActions.getRemainDuration(), !repeatTypeVal.getText().toString().isEmpty(), true);
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
        if (SettingUtil.getInstance(AddEditReminderActivity.this).isDarkTheme()) {
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
                    Attachments fileAttachment = new Attachments("jpg", storageFile.getAbsolutePath(), 0L, tempReminderID, 0L);
                    attachmentsViewModel.insert(fileAttachment);
                    attachmentsAdapter.notifyDataSetChanged();
                    break;
                case RESUEST_RECORD_AUDIO:
                    Attachments attachments = new Attachments("3gp", data.getStringExtra("outputFile"), 0L, tempReminderID, 0L);
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
                    Attachments attach = new Attachments("jpg", file.getAbsolutePath(), 0L, tempReminderID, 0L);
                    attachmentsViewModel.insert(attach);
                    attachmentsAdapter.notifyDataSetChanged();

                    break;
                case DRAW_REQUEST:
                    Attachments attachment = new Attachments("jpg", data.getExtras().getString("drawPath"), 0L, tempReminderID, 0L);
                    attachmentsViewModel.insert(attachment);
                    attachmentsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        datepickerVal = (hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute) + ":00";
        reminderTime.setText(datepickerVal);
        isReminerTimeChange = true;
    }

    public interface ClickAddSubTaskListener {
        void addSubTaskListener();
    }
}
