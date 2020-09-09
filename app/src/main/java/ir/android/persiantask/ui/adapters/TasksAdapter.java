package ir.android.persiantask.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.utils.Init;

public class TasksAdapter extends ListAdapter<Tasks, TasksAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private FragmentActivity mFragmentActivity;

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


    public TasksAdapter(FragmentActivity activity) {
        super(DIFF_CALLBACK);
        mFragmentActivity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle, tasks_enddate;
        public ImageView tasksIsCompleted, reminder_time, reminder_type, tasks_comment;

        public ViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tasks_title);
            tasks_enddate = itemView.findViewById(R.id.tasks_enddate);
            tasksIsCompleted = itemView.findViewById(R.id.tasks_iscompleted);
            tasksIsCompleted.setTag(1);
            reminder_time = itemView.findViewById(R.id.reminder_time);
            reminder_type = itemView.findViewById(R.id.reminder_type);
            tasks_comment = itemView.findViewById(R.id.tasks_comment);
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
        if(tasks.getTasks_iscompleted() == 1){
            holder.tasksIsCompleted.setImageResource(R.drawable.ic_radio_button_checked_green);
            holder.tasksIsCompleted.setTag(R.drawable.ic_radio_button_checked_green);
            holder.tasksIsCompleted.setVisibility(View.VISIBLE);
            holder.taskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tasks_enddate.setText(mFragmentActivity.getString(R.string.inDate) + " " + tasks.getTasks_enddate() +
                    " " + mFragmentActivity.getString(R.string.completed));
        } else {
            holder.tasks_enddate.setText(tasks.getTasks_enddate());
        }

        holder.tasksIsCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((Integer) holder.tasksIsCompleted.getTag() != R.drawable.ic_radio_button_checked_green){
                    holder.tasksIsCompleted.setImageResource(R.drawable.ic_radio_button_checked_green);
                    holder.tasksIsCompleted.setTag(R.drawable.ic_radio_button_checked_green);
                    holder.tasksIsCompleted.setVisibility(View.VISIBLE);
                    holder.tasks_enddate.setText(mFragmentActivity.getString(R.string.inDate) + " " + Init.getCurrentDate() +
                            " " + mFragmentActivity.getString(R.string.completed));
                    holder.taskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    holder.tasksIsCompleted.setImageResource(R.drawable.ic_black_circle);
                    holder.tasksIsCompleted.setTag(R.drawable.ic_black_circle);
                    holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.tasks_enddate.setText(tasks.getTasks_enddate());
                }
            }
        });
        //remind me in advance
        if(tasks.getTasks_remindertime() == 2){
            holder.reminder_time.setVisibility(View.VISIBLE);
        }
        //reminder type == alarm
        if(tasks.getTasks_remindertype() == 1){
            holder.reminder_type.setVisibility(View.VISIBLE);
        }
        //set comment for task
        if(!tasks.getTasks_comment().isEmpty()){
            holder.tasks_comment.setVisibility(View.VISIBLE);
        }
    }


    public Tasks getTaskAt(int position) {
        return getItem(position);
    }

    public interface OnItemClickListener {
        void OnItemClick(Tasks task);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
