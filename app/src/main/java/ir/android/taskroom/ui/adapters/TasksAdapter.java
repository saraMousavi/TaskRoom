package ir.android.taskroom.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.UUID;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Tasks;
import ir.android.taskroom.ui.fragment.SubTaskFragment;
import ir.android.taskroom.utils.Init;
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
                    oldItem.getTasks_iscompleted().equals(newItem.getTasks_iscompleted());
        }
    };
    private SharedPreferences sharedPreferences;
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
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        setMasterTheme(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View taskView = inflater.inflate(R.layout.tasks_item_recyclerview, parent, false);
        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Tasks tasks = getItem(position);
        holder.taskTitle.setText(tasks.getTasks_title());

        Init.toggleCompleteCircle(holder.taskTitle, holder.tasksIsCompleted, tasks.getTasks_iscompleted());
        if (!tasks.getTasks_enddate().isEmpty()) {
            holder.tasks_enddate.setText(tasks.getTasks_enddate());
        }
        holder.tasks_startdate.setText(tasks.getTasks_startdate());

        holder.tasksIsCompleted.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Tasks task = new Tasks(tasks.getTasks_title(), tasks.getTasks_priority(), 0, tasks.getTasks_repeatedtype(), tasks.getProjects_id(), tasks.getTasks_startdate(), tasks.getTasks_remindertype()
                        , tasks.getTasks_remindertime(), tasks.getTasks_repeateddays(), tasks.getTasks_enddate(),
                        tasks.getLabel_id(), tasks.getTasks_comment(), tasks.getWork_id(), tasks.getHas_attach(), tasks.getComplete_date());
                String workId = createWorkRequest(tasks, tasks.getTasks_iscompleted() == 0 ? false:true);
                System.out.println("workId = " + workId);
                if (tasks.getTasks_iscompleted() == 0) {
                    task.setTasks_iscompleted(1);
                    task.setComplete_date(mFragmentActivity.getString(R.string.inDate) + " " + Init.getCurrentDate() +
                            " " + mFragmentActivity.getString(R.string.completed));
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
        //set priority colorflag
        if (tasks.getTasks_priority() == 1) {
            holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.yellow_priority_corner_shape));
        } else if (tasks.getTasks_priority() == 2) {
            holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.orange_priority_corner_shape));
        } else if (tasks.getTasks_priority() == 3) {
            holder.priorityView.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.green_priority_corner_shape));
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String createWorkRequest(Tasks tasks, boolean isChecked) {
        if(isChecked) {
            DateTime dateTime1 = null;
            DateTime dateTime2 = null;
            if (tasks.getTasks_remindertime() == 1) {
                dateTime1 = Init.getCurrentDateTimeWithSecond();
                dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_startdate()));
                if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                    dateTime2 = Init.getTodayDateTimeWithTime(tasks.getTasks_startdate(), 1, true);
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        return "-2";//start date past
                    }
                }
            } else if (!tasks.getTasks_repeateddays().isEmpty()) {
                if (tasks.getTasks_remindertime() == 3) {
                    dateTime1 = Init.getCurrentDateTimeWithSecond();
                    dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_startdate()));
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        dateTime2 = Init.getTodayDateTimeWithTime(tasks.getTasks_startdate(), 1, true);
                    }
                } else if (tasks.getTasks_remindertime() == 2) {
                    dateTime1 = Init.getCurrentDateTimeWithSecond();
                    dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_enddate()));
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        return "-1";
                    }
                }
            } else {
                if (tasks.getTasks_remindertime() == 2) {
                    dateTime1 = Init.getCurrentDateTimeWithSecond();
                    dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_startdate()));
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        dateTime2 = Init.getTodayDateTimeWithTime(tasks.getTasks_startdate(), 1, true);
                    }
                }
            }
            if (dateTime1 != null && dateTime2 != null) {
                if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                    return "-1";
                }
                if (tasks.getTasks_remindertime() != 0) {
                    Interval interval = new Interval(dateTime1, dateTime2);
//            long hour = interval.toDuration().getStandardMinutes() / 60;
//            long minute = interval.toDuration().getStandardMinutes() - hour * 60;
//            long second = 0;
//            if (minute == 0 && hour == 0) {
//                second = interval.toDuration().getStandardSeconds();
//            }
//            Toast.makeText(getApplicationContext(), getString(R.string.remindeTime) + hour + ":" + minute + ":" + second, Toast.LENGTH_LONG).show();
                    return Init.requestWork(mFragmentActivity.getApplicationContext(), tasks.getTasks_title(), tasks.getTasks_remindertype(),
                            Init.getWorkRequestPeriodicIntervalMillis(mFragmentActivity.getResources(), tasks.getTasks_repeateddays()),
                            interval.toDurationMillis(), !tasks.getTasks_repeateddays().isEmpty(), false);
                }
            }
        } else {
            if (tasks.getWork_id().contains(",")) {
                for (String requestId : tasks.getWork_id().split(",")) {
                    WorkManager.getInstance(mFragmentActivity.getApplicationContext()).cancelWorkById(UUID.fromString(requestId));
                }
            } else {
                if (!tasks.getWork_id().equals("0")) {
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
        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
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
