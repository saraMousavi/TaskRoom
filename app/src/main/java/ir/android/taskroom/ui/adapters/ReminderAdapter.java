package ir.android.taskroom.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ir.android.taskroom.R;
import ir.android.taskroom.SettingUtil;
import ir.android.taskroom.data.db.entity.Reminders;
import ir.android.taskroom.utils.EnglishInit;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.objects.TasksReminderActions;
import ir.android.taskroom.viewmodels.ReminderViewModel;

public class ReminderAdapter extends ListAdapter<Reminders, ReminderAdapter.ViewHolder> {
    private ReminderAdapter.OnItemClickListener listener;
    private FragmentActivity mFragmentActivity;
    private ReminderViewModel reminderViewModel;

    private static final DiffUtil.ItemCallback<Reminders> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminders>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminders oldItem, @NonNull Reminders newItem) {
            return oldItem.getReminders_id() == newItem.getReminders_id();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Reminders oldItem, @NonNull Reminders newItem) {
            return oldItem.getReminders_title().equals(newItem.getReminders_title()) &&
                    oldItem.getReminders_time().equals(newItem.getReminders_time()) &&
                    oldItem.getReminders_repeatedtype().equals(newItem.getReminders_repeatedtype()) &&
                    oldItem.getReminders_repeatedday().equals(newItem.getReminders_repeatedday()) &&
                    oldItem.getReminders_active().equals(newItem.getReminders_active()) &&
                    oldItem.getWork_id().equals(newItem.getWork_id());
        }
    };
    private Context context;


    public ReminderAdapter(FragmentActivity activity, ReminderViewModel reminderViewModel) {
        super(DIFF_CALLBACK);
        mFragmentActivity = activity;
        this.reminderViewModel = reminderViewModel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView remindersTitle, remindertime, remindersRepeat;
        public SwitchCompat remindersActive;
        public View priorityView;
        public ImageView reminderComment, repeatedIcon, reminderAttachment;

        public ViewHolder(View itemView) {
            super(itemView);
            remindersTitle = itemView.findViewById(R.id.reminders_title);
            remindertime = itemView.findViewById(R.id.remindertime);

            if (SettingUtil.getInstance(mFragmentActivity.getApplicationContext()).isEnglishLanguage()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    remindertime.setTextAppearance(R.style.numberTextInput);
                }
            }
            remindersRepeat = itemView.findViewById(R.id.reminders_repeat);
            remindersActive = itemView.findViewById(R.id.reminders_active);
            reminderComment = itemView.findViewById(R.id.reminderComment);
            repeatedIcon = itemView.findViewById(R.id.repeatedIcon);
            reminderAttachment = itemView.findViewById(R.id.reminderAttachment);
            priorityView = itemView.findViewById(R.id.priorityView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(getItem(position));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;
        setMasterTheme(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.reminder_item_recyclerview, parent, false);
        ReminderAdapter.ViewHolder viewHolder = new ReminderAdapter.ViewHolder(taskView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Reminders reminder = getItem(position);
        if (SettingUtil.getInstance(context).isEnglishLanguage()) {
            //set priority colorflag
            if (reminder.getReminders_priority() == 1) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.yellow_priority_corner_shape_english));
            } else if (reminder.getReminders_priority() == 2) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.orange_priority_corner_shape_english));
            } else if (reminder.getReminders_priority() == 3) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.green_priority_corner_shape_english));
            } else {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.grey_priority_corner_shape_english));
            }
        } else {
            holder.remindersTitle.setGravity(Gravity.RIGHT);
            //set priority colorflag
            if (reminder.getReminders_priority() == 1) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.yellow_priority_corner_shape_persian));
            } else if (reminder.getReminders_priority() == 2) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.orange_priority_corner_shape_persian));
            } else if (reminder.getReminders_priority() == 3) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.green_priority_corner_shape_persian));
            } else {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.grey_priority_corner_shape_persian));
            }
        }
        holder.remindersTitle.setText(reminder.getReminders_title());
        holder.remindersActive.setChecked(reminder.getReminders_active() == 1 ? true : false);
        holder.remindertime.setText(reminder.getReminders_time());
        if (reminder.getReminders_comment().isEmpty()) {
            holder.reminderComment.setVisibility(View.GONE);
        } else {
            holder.reminderComment.setVisibility(View.VISIBLE);
        }
        if (reminder.getReminders_repeatedday().isEmpty()) {
            holder.repeatedIcon.setVisibility(View.GONE);
        } else {
            holder.repeatedIcon.setVisibility(View.VISIBLE);
            holder.remindersRepeat.setText("," + reminder.getReminders_repeatedday());
        }
        if (reminder.getHas_attach()) {
            holder.reminderAttachment.setVisibility(View.VISIBLE);
        } else {
            holder.reminderAttachment.setVisibility(View.GONE);
        }

        if (SettingUtil.getInstance(context).isDarkTheme()) {
            holder.remindersTitle.setTextColor(context.getResources().getColor(R.color.white));
            holder.remindersRepeat.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            ArrayList<Map<View, Boolean>> viewList = new ArrayList<>();
            Map<View, Boolean> mapView = new HashMap<>();
            mapView.put(holder.remindersTitle, false);
            viewList.add(mapView);
            mapView = new HashMap<>();
            mapView.put(holder.remindersRepeat, false);
            viewList.add(mapView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Init.setViewBackgroundDependOnTheme(viewList, context);
            }
        }

        holder.remindersActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String workId = cancelOrCreateRequest(getReminderAt(position), isChecked);
                if (workId.equals("-1")) {
                    String validtimepast = mFragmentActivity.getString(R.string.validtimepast);
                    if (SettingUtil.getInstance(context).isEnglishLanguage()) {
                        validtimepast = mFragmentActivity.getString(R.string.validtimepast);
                    }
                    Snackbar snackbar = Snackbar
                            .make(mFragmentActivity.getWindow().getDecorView().findViewById(android.R.id.content), validtimepast, Snackbar.LENGTH_LONG);
                    if (!SettingUtil.getInstance(context).isEnglishLanguage()) {
                        ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    }
                    snackbar.show();
                    return;
                }
                reminder.setReminders_active(isChecked ? 1 : 0);
                if (!isChecked) {
                    reminder.setReminders_update(Init.convertDateTimeToInteger(EnglishInit.getCurrentDateTimeWithSecond()));
                }
                reminder.setReminders_id(reminder.getReminders_id());
                reminder.setWork_id(workId);
                reminderViewModel.update(reminder);
            }
        });
    }


    private String cancelOrCreateRequest(Reminders reminders, boolean isChecked) {
        String datepickerVal = reminders.getReminders_time();
        if (!datepickerVal.isEmpty() && isChecked) {
            TasksReminderActions tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(reminders, Init.convertIntegerToDateTime(reminders.getReminders_crdate()), mFragmentActivity.getResources());
            if (tasksReminderActions.getRemainDuration() == -1) {
                return "-1";
            }
            String remindeTime = mFragmentActivity.getString(R.string.remindeTime);
            if (SettingUtil.getInstance(context).isEnglishLanguage()) {
                remindeTime = mFragmentActivity.getString(R.string.remindeTime);
            }
            Toast.makeText(mFragmentActivity.getApplicationContext(), remindeTime + tasksReminderActions.getRemainTime(), Toast.LENGTH_LONG).show();
            return Init.requestWork(mFragmentActivity.getApplicationContext(), reminders.getReminders_title(), reminders.getReminders_comment(), reminders.getReminders_type(),
                    Init.getWorkRequestPeriodicIntervalMillis(mFragmentActivity.getResources(), reminders.getReminders_repeatedday()),
                    tasksReminderActions.getRemainDuration(), !reminders.getReminders_repeatedday().isEmpty(), true);
        } else if (!datepickerVal.isEmpty()) {
            if (reminders.getWork_id().contains(",")) {
                for (String requestId : reminders.getWork_id().split(",")) {
                    WorkManager.getInstance(mFragmentActivity.getApplicationContext()).cancelWorkById(UUID.fromString(requestId));
                }
            } else if (!reminders.getWork_id().equals("0")) {
                WorkManager.getInstance(mFragmentActivity.getApplicationContext()).cancelWorkById(UUID.fromString(reminders.getWork_id()));
            }
        }
        return "0";
    }

    public Reminders getReminderAt(int position) {
        return getItem(position);
    }

    public interface OnItemClickListener {
        void OnItemClick(Reminders reminders);
    }

    public void setOnItemClickListener(ReminderAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public void setMasterTheme(Context context) {
        if (SettingUtil.getInstance(context).isDarkTheme()) {
            context.setTheme(R.style.FeedActivityThemeDark);
            return;
        }
        switch (getFlag(context)) {
            case 2:
                context.setTheme(R.style.AppTheme2);
                break;
            case 3:
                context.setTheme(R.style.AppTheme3);
                break;
            case 4:
                context.setTheme(R.style.AppTheme4);
                break;
            case 5:
                context.setTheme(R.style.AppTheme5);
                break;
            case 6:
                context.setTheme(R.style.AppTheme6);
                break;
            default:
                context.setTheme(R.style.AppTheme);
                break;
        }
    }


    public Integer getFlag(Context context) {
        SharedPreferences sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedpreferences.getInt("theme", 1);
    }
}