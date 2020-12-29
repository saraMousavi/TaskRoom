package ir.android.taskroom.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.data.db.entity.Tasks;
import ir.android.taskroom.ui.fragment.SubTaskFragment;
import ir.android.taskroom.utils.EnglishInit;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.objects.TasksReminderActions;
import ir.android.taskroom.viewmodels.TaskViewModel;

public class TasksAdapter extends ListAdapter<Tasks, TasksAdapter.ViewHolder> {
    private FragmentActivity mFragmentActivity;
    private TaskClickListener taskClickListener;
    private TaskViewModel taskViewModel;

    private static final DiffUtil.ItemCallback<Tasks> DIFF_CALLBACK = new DiffUtil.ItemCallback<Tasks>() {
        @Override
        public boolean areItemsTheSame(@NonNull Tasks oldItem, @NonNull Tasks newItem) {
            return oldItem.getTasks_id() == newItem.getTasks_id();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Tasks oldItem, @NonNull Tasks newItem) {
            return oldItem.getTasks_title().equals(newItem.getTasks_title()) &&
                    oldItem.getTasks_startdate().equals(newItem.getTasks_startdate()) &&
                    oldItem.getTasks_enddate().equals(newItem.getTasks_enddate()) &&
                    oldItem.getTasks_priority().equals(newItem.getTasks_priority()) &&
                    oldItem.getTasks_iscompleted().equals(newItem.getTasks_iscompleted()) &&
                    oldItem.getWork_id().equals(newItem.getWork_id());
        }
    };
    private Context context;


    public TasksAdapter(TaskViewModel taskViewModel, FragmentActivity activity, FragmentManager fragmentManager) {
        super(DIFF_CALLBACK);
        mFragmentActivity = activity;
        this.taskViewModel = taskViewModel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle, tasks_enddate, tasks_startdate;
        public ImageView tasksIsCompleted, reminder_time, tasks_comment, reminder_attach;
        public ConstraintLayout subtaskConstarint;
        public View priorityView;


        public ViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tasks_title);
            tasks_startdate = itemView.findViewById(R.id.tasks_startdate);
            tasks_enddate = itemView.findViewById(R.id.tasks_enddate);
            if (SettingUtil.getInstance(mFragmentActivity.getApplicationContext()).isEnglishLanguage()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tasks_startdate.setTextAppearance(R.style.numberTextInput);
                    tasks_enddate.setTextAppearance(R.style.numberTextInput);
                }
            }
            tasksIsCompleted = itemView.findViewById(R.id.tasks_iscompleted);
            reminder_time = itemView.findViewById(R.id.reminder_time);
            tasks_comment = itemView.findViewById(R.id.tasks_comment);
            reminder_attach = itemView.findViewById(R.id.reminder_attach);
            priorityView = itemView.findViewById(R.id.priorityView);
            subtaskConstarint = itemView.findViewById(R.id.subtaskConstarint);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        this.context = context;
        setMasterTheme(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.tasks_item_recyclerview, parent, false);
        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Tasks tasks = getItem(position);
        if (SettingUtil.getInstance(mFragmentActivity.getApplicationContext()).isEnglishLanguage()) {
            //set priority colorflag
            if (tasks.getTasks_priority() == 1) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.yellow_priority_corner_shape_english));
            } else if (tasks.getTasks_priority() == 2) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.orange_priority_corner_shape_english));
            } else if (tasks.getTasks_priority() == 3) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.green_priority_corner_shape_english));
            } else {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.grey_priority_corner_shape_english));
            }
        } else {
            holder.taskTitle.setGravity(Gravity.RIGHT);
            //set priority colorflag
            if (tasks.getTasks_priority() == 1) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.yellow_priority_corner_shape_persian));
            } else if (tasks.getTasks_priority() == 2) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.orange_priority_corner_shape_persian));
            } else if (tasks.getTasks_priority() == 3) {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.green_priority_corner_shape_persian));
            } else {
                holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.grey_priority_corner_shape_persian));
            }
        }

        holder.taskTitle.setText(tasks.getTasks_title());

        Init.toggleCompleteCircle(holder.taskTitle, holder.tasksIsCompleted, tasks.getTasks_iscompleted());
        if (!tasks.getTasks_enddate().isEmpty()) {
            holder.tasks_enddate.setText(tasks.getTasks_enddate());
        }
        holder.tasks_startdate.setText(tasks.getTasks_startdate());

        holder.tasksIsCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tasks task = new Tasks(tasks.getTasks_title(), tasks.getTasks_priority(), 0, tasks.getTasks_repeatedtype(), tasks.getProjects_id(), tasks.getTasks_startdate(), tasks.getTasks_remindertype()
                        , tasks.getTasks_remindertime(), tasks.getTasks_repeateddays(), tasks.getTasks_enddate(),
                        tasks.getLabel_id(), tasks.getTasks_comment(), tasks.getWork_id(), tasks.getHas_attach(), tasks.getComplete_date());
                String workId = createWorkRequest(tasks, tasks.getTasks_iscompleted() == 0 ? false : true);
                if (tasks.getTasks_iscompleted() == 0) {
                    task.setTasks_iscompleted(1);
                    task.setComplete_date(mFragmentActivity.getString(R.string.inDate) + " " + EnglishInit.getCurrentDate() +
                            " " + mFragmentActivity.getString(R.string.completed));
                    Snackbar snackbar = Snackbar
                            .make(mFragmentActivity.getWindow().getDecorView().findViewById(android.R.id.content), mFragmentActivity.getString(R.string.disableReminderBecauseOfCompleted), Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    snackbar.show();
                } else {
                    task.setTasks_iscompleted(0);
                    //@TODO add column to task for save last enddate
                    task.setComplete_date("");
                }
                task.setTasks_id(tasks.getTasks_id());
                task.setWork_id(workId);
                taskViewModel.update(task);
                notifyDataSetChanged();
            }
        });
        //remind me in advance
        if (tasks.getTasks_remindertime() != 0) {
            holder.reminder_time.setVisibility(View.VISIBLE);
            if (tasks.getTasks_repeateddays().isEmpty()) {
                holder.reminder_time.setImageResource(R.drawable.ic_orange_alarm);
            } else {
                holder.reminder_time.setImageResource(R.drawable.ic_orange_repeat);
            }
        }
        //@TODO change icon to alarm off when alarm passed
        //set comment for task
        if (!tasks.getTasks_comment().isEmpty()) {
            holder.tasks_comment.setVisibility(View.VISIBLE);
        }
        //set attach for task
        if (tasks.getHas_attach()) {
            holder.reminder_attach.setVisibility(View.VISIBLE);
        }

        int newContainerID = View.generateViewId();
        holder.subtaskConstarint.setId(newContainerID);
        fragmentJump(tasks, newContainerID);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskClickListener.editTask(tasks);
            }
        });
    }

    public Tasks getTaskAt(int position) {
        return getItem(position);
    }


    public String createWorkRequest(Tasks tasks, boolean isChecked) {
        if (isChecked) {
            TasksReminderActions tasksReminderActions = Init.getDurationInWholeStateOfRemindersOrTasks(tasks, null, mFragmentActivity.getResources());
            if (tasksReminderActions.getRemainDuration() == -1) {
                return "-1";
            }
            if (tasksReminderActions.getRemainDuration() == -2) {
                Toast.makeText(mFragmentActivity.getApplicationContext(), mFragmentActivity.getString(R.string.validstartdatepast), Toast.LENGTH_LONG).show();
                return "-2";
            }
            if (tasksReminderActions.getRemainTime().isEmpty()) {
                //if remind time was dont remind
                return "0";
            } else {
                Toast.makeText(mFragmentActivity.getApplicationContext(), mFragmentActivity.getString(R.string.remindeTime) + tasksReminderActions.getRemainTime(), Toast.LENGTH_LONG).show();
                return Init.requestWork(mFragmentActivity.getApplicationContext(), tasks.getTasks_title(), tasks.getTasks_comment(), tasks.getTasks_remindertype(),
                        Init.getWorkRequestPeriodicIntervalMillis(mFragmentActivity.getResources(), tasks.getTasks_repeateddays()),
                        tasksReminderActions.getRemainDuration(), !tasks.getTasks_repeateddays().isEmpty(), false);
            }
        } else {
            if (tasks.getWork_id().contains(",")) {
                for (String requestId : tasks.getWork_id().split(",")) {
                    WorkManager.getInstance(mFragmentActivity.getApplicationContext()).cancelWorkById(UUID.fromString(requestId));
                }
            } else {
                if (!tasks.getWork_id().equals("0") && !tasks.getWork_id().equals("-2")) {
                    WorkManager.getInstance(mFragmentActivity.getApplicationContext()).cancelWorkById(UUID.fromString(tasks.getWork_id()));
                }
            }
        }

        return "0";
    }


    private void fragmentJump(Tasks tasks, int newContainerID) {
        SubTaskFragment subTaskFragment = new SubTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("taskID", tasks.getTasks_id());
        subTaskFragment.setArguments(bundle);
        if (taskClickListener != null) {
            taskClickListener.switchContent(newContainerID, subTaskFragment);
        }
    }

    public interface TaskClickListener {
        void switchContent(int subtaskConstarint, SubTaskFragment subTaskFragment);

        void editTask(Tasks tasks);
    }

    public void setOnItemClickListener(TaskClickListener listener) {
        this.taskClickListener = listener;
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
