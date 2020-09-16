package ir.android.persiantask.ui.activity.reminder;

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
import java.util.List;
import java.util.concurrent.ExecutionException;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Reminders;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.factory.SubTasksViewModelFactory;
import ir.android.persiantask.databinding.RemindersAddActivityBinding;
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
import ir.android.persiantask.viewmodels.ReminderViewModel;
import ir.android.persiantask.viewmodels.SubTasksViewModel;

public class AddEditReminderActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener
        , DatePickerDialog.OnDateSetListener
        , TasksRepeatTypeBottomSheetFragment.RepeatTypeClickListener
        , TasksRepeatDayBottomSheetFragment.RepeatDayClickListener
        , TasksRepeatPeriodBottomSheetFragment.RepeatPeriodClickListener
        , TasksPriorityTypeBottomSheetFragment.PriorityTypeClickListener {
    private TextInputEditText reminderNameEdit, reminderComment;
    private FloatingActionButton fabInsertReminders, fabInsertReminders2;
    private ConstraintLayout startDateConstraint, endDateConstraint,
            repeatTypeConstraint, priorityTypeContraint,
            reminderTimeConstraint, reminderTypeConstraint;
    private TextView startTextVal, endTextVal, repeatTypeVal, completedDate, priorityVal;
    private AppBarLayout mAppBarLayout;
    private RemindersAddActivityBinding remindersAddActivityBinding;
    private String datepickerVal;
    private ReminderViewModel reminderViewModel;
    private AppCompatSpinner  reminderTime;
    private SharedPreferences sharedPreferences;
    private ImageView completeIcon;
    private boolean isCompleted;
    private String completedDateVal = "";
    private RadioGroup reminderTypeGroup;
    private Integer tempReminderID;
    private boolean isEditActivity = false;
    private Reminders clickedReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onClickListener();
        initSpinners();
        //for inserting subtask we need task foreign key
        insertTempTask();
    }

    private void insertTempTask() {
            if (isEditActivity) {
                tempReminderID = clickedReminder.getReminders_id();
            } else {
//                tempReminderID = reminderViewModel.insert(remin);
            }
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.remove("tempTaskID");
//            editor.putLong("tempTaskID", tempReminderID);
//            editor.apply();

    }


    private void initSpinners() {
        ArrayList<String> remindTimeArray = new ArrayList<>();
        remindTimeArray.add(getString(R.string.dontRemind));
        remindTimeArray.add(getString(R.string.remindInEndDate));
        //@TODO change this translation
        remindTimeArray.add(getString(R.string.remindInAdvance));
        ArrayAdapter<String> remindTimeAdapter = new ArrayAdapter<>(AddEditReminderActivity.this,
                android.R.layout.simple_spinner_dropdown_item, remindTimeArray);
        reminderTime.setAdapter(remindTimeAdapter);
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
                PersianCalendar persianCalendar = new PersianCalendar();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        AddEditReminderActivity.this,
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
                        AddEditReminderActivity.this,
                        persianCalendar.getPersianYear(),
                        persianCalendar.getPersianMonth(),
                        persianCalendar.getPersianDay()
                );
                datePickerDialog.show(getSupportFragmentManager(), "endDatepickerdialog");
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
        repeatTypeConstraint = findViewById(R.id.repeatTypeConstraint);
        priorityTypeContraint = findViewById(R.id.priorityTypeContraint);
        reminderTime = findViewById(R.id.reminderTime);
        reminderTypeGroup = findViewById(R.id.reminderTypeGroup);
        completeIcon = findViewById(R.id.completeIcon);
        completeIcon.setTag(R.drawable.ic_black_circle);

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
                , reminderComment.getText().toString(), priorityIntVal, reminderNameEdit.getText().toString(),
                0,"" , 0, 0, 0);
//        reminders.setReminders_id(tempReminderID);
        if(isEditActivity){
            reminders.setReminders_id(clickedReminder.getReminders_id());
            reminderViewModel.update(reminders);
        } else {
            reminderViewModel.insert(reminders);
        }

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
                AddEditReminderActivity.this,
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
