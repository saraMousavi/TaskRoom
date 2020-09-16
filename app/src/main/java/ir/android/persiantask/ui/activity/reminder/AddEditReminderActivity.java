package ir.android.persiantask.ui.activity.reminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.databinding.RemindersAddActivityBinding;
import ir.android.persiantask.ui.fragment.TasksPriorityTypeBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatDayBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatPeriodBottomSheetFragment;
import ir.android.persiantask.ui.fragment.TasksRepeatTypeBottomSheetFragment;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.utils.calender.TimePickerDialog;
import ir.android.persiantask.viewmodels.ReminderViewModel;

public class AddEditReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener
        , TasksRepeatTypeBottomSheetFragment.RepeatTypeClickListener
        , TasksRepeatDayBottomSheetFragment.RepeatDayClickListener
        , TasksRepeatPeriodBottomSheetFragment.RepeatPeriodClickListener
        , TasksPriorityTypeBottomSheetFragment.PriorityTypeClickListener {
    private TextInputEditText reminderNameEdit, reminderComment;
    private FloatingActionButton fabInsertReminders, fabInsertReminders2;
    private ConstraintLayout startDateConstraint,
            repeatTypeConstraint, priorityTypeContraint,
            reminderTimeConstraint, reminderTypeConstraint;
    private TextView startTextVal, repeatTypeVal, priorityVal;
    private AppBarLayout mAppBarLayout;
    private RemindersAddActivityBinding remindersAddActivityBinding;
    private String datepickerVal;
    private ReminderViewModel reminderViewModel;
    private SharedPreferences sharedPreferences;
    private String completedDateVal = "";
    private RadioGroup reminderTypeGroup;
    private Integer tempReminderID;
    private boolean isEditActivity = false, isActive = false;
    private Reminders clickedReminder;
    private SwitchCompat reminders_active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onClickListener();
        initSpinners();
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
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    //@TODO add slide down animation
                    fabInsertReminders2.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    //@TODO add slide up animation
                    fabInsertReminders2.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
        startDateConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GregorianCalendar galena = new GregorianCalendar();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                        AddEditReminderActivity.this,
                        galena.get(Calendar.HOUR),
                        galena.get(Calendar.MINUTE),
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
        reminderComment = findViewById(R.id.reminderComment);
        startDateConstraint = findViewById(R.id.startDateConstraint);
        startTextVal = findViewById(R.id.startTextVal);
        //@TODO why hour and minute  has inversed in ui
        startTextVal.setText(Init.getCurrentTime());
        repeatTypeVal = findViewById(R.id.repeatTypeVal);
        priorityVal = findViewById(R.id.priorityVal);
        reminderTimeConstraint = findViewById(R.id.reminderTimeConstraint);
        reminderTypeConstraint = findViewById(R.id.reminderTypeConstraint);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        repeatTypeConstraint = findViewById(R.id.repeatTypeConstraint);
        priorityTypeContraint = findViewById(R.id.priorityTypeContraint);
        reminderTypeGroup = findViewById(R.id.reminderTypeGroup);
        reminders_active = findViewById(R.id.reminders_active);

        Intent intent = getIntent();

        if (intent.hasExtra("clickedReminder")) {
            clickedReminder = (Reminders) intent.getExtras().getSerializable("clickedReminder");
            editableRemindersFields();
        }
    }

    private void editableRemindersFields() {
        reminderNameEdit.setText(clickedReminder.getReminders_title());
        isEditActivity = true;
    }

    private void insertReminder() {
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
        Reminders reminders = new Reminders(reminderType.getText().toString().equals(getString(R.string.push)) ? 0 : 1
                , reminderComment.getText().toString(), startTextVal.getText().toString(), priorityIntVal, reminderNameEdit.getText().toString(),
                "", 0, isActive ? 1 : 0, 0);
//        reminders.setReminders_id(tempReminderID);
        if (isEditActivity) {
            reminders.setReminders_id(clickedReminder.getReminders_id());
            reminderViewModel.update(reminders);
        } else {
            reminderViewModel.insert(reminders);
        }

        setResult(RESULT_OK);
        finish();
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute) {
        datepickerVal = hourOfDay + ":" + minute;
        startTextVal.setText(datepickerVal);
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