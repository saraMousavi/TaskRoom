package ir.android.persiantask.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.ui.fragment.SubTaskFragment;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.viewmodels.TaskViewModel;

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
            return oldItem.getTasks_title().equals(newItem.getTasks_title());
        }
    };


    public TasksAdapter(TaskViewModel taskViewModel, FragmentActivity activity, FragmentManager fragmentManager) {
        super(DIFF_CALLBACK);
        mFragmentActivity = activity;
        this.taskViewModel = taskViewModel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle, tasks_enddate;
        public ImageView tasksIsCompleted, reminder_time, reminder_type, tasks_comment, task_priority;
        public ConstraintLayout subtaskConstarint;


        public ViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tasks_title);
            tasks_enddate = itemView.findViewById(R.id.tasks_enddate);
            tasksIsCompleted = itemView.findViewById(R.id.tasks_iscompleted);
            reminder_time = itemView.findViewById(R.id.reminder_time);
            reminder_type = itemView.findViewById(R.id.reminder_type);
            tasks_comment = itemView.findViewById(R.id.tasks_comment);
            task_priority = itemView.findViewById(R.id.task_priority);
            subtaskConstarint = itemView.findViewById(R.id.subtaskConstarint);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
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

        holder.tasks_enddate.setText(tasks.getTasks_enddate());

        holder.tasksIsCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tasks task = new Tasks(tasks.getTasks_title(), tasks.getTasks_priority(),0,tasks.getTasks_repeatedtype(),tasks.getProjects_id(),tasks.getTasks_startdate(),tasks.getTasks_remindertype()
                ,tasks.getTasks_remindertime(), tasks.getTasks_repeateddays(), tasks.getTasks_enddate(), tasks.getLabel_id(), tasks.getTasks_comment(), tasks.getWork_id(), tasks.getHas_attach());
                if(tasks.getTasks_iscompleted() == 0){
                    task.setTasks_iscompleted(1);
                    task.setTasks_enddate(mFragmentActivity.getString(R.string.inDate) + " " + Init.getCurrentDate() +
                            " " + mFragmentActivity.getString(R.string.completed));
                } else {
                    task.setTasks_iscompleted(0);
                    //@TODO add column to task for save last enddate
                    task.setTasks_enddate(Init.getCurrentDate());
                }
                task.setTasks_id(tasks.getTasks_id());
                taskViewModel.update(task);
                notifyDataSetChanged();
            }
        });
        //remind me in advance
        if(tasks.getTasks_remindertime() != 0){
            holder.reminder_time.setVisibility(View.VISIBLE);
        }
        //reminder type == alarm
        if(tasks.getTasks_remindertype() != null &&  tasks.getTasks_remindertype() == 1){
            holder.reminder_type.setVisibility(View.VISIBLE);
        }
        //set comment for task
        if(!tasks.getTasks_comment().isEmpty()){
            holder.tasks_comment.setVisibility(View.VISIBLE);
        }
        //set priority colorflag
        if(tasks.getTasks_priority() != 0){
            holder.task_priority.setVisibility(View.VISIBLE);
        }
        switch (tasks.getTasks_priority()){
            case 1:
                holder.task_priority.setImageResource(R.drawable.ic_low_yellow_priority);
                break;
            case 2:
                holder.task_priority.setImageResource(R.drawable.ic_medium_orange_priority);
                break;
            case 3:
                holder.task_priority.setImageResource(R.drawable.ic_high_green_priority);
                break;
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

    private void fragmentJump(Tasks tasks, int newContainerID) {
        SubTaskFragment subTaskFragment = new SubTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("taskID", tasks.getTasks_id());
        subTaskFragment.setArguments(bundle);
        if(taskClickListener != null) {
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

}
