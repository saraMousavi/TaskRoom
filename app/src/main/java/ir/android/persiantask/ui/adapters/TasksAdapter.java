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

import java.util.List;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Tasks;

public class TasksAdapter extends ListAdapter<Tasks, TasksAdapter.ViewHolder> {
    private List<Tasks> mTasks;
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


    public TasksAdapter(FragmentActivity activity, List<Tasks> tasks) {
        super(DIFF_CALLBACK);
        mTasks = tasks;
        mFragmentActivity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle;
        public ImageView tasksIsCompleted;

        public ViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tasks_title);
            tasksIsCompleted = itemView.findViewById(R.id.tasks_iscompleted);
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
        View taskView = inflater.inflate(R.layout.taks_item_recyclerview, parent, false);
        ViewHolder viewHolder = new ViewHolder(taskView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Tasks tasks = mTasks.get(position);
        holder.taskTitle.setText(tasks.getTasks_title());
        holder.tasksIsCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tasksIsCompleted.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.ic_radio_button_checked_green));
                holder.taskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
        });
    }

    @Override
    public int getItemCount() {
        System.out.println("mTasks.size() = " + mTasks.size());
        return mTasks.size();
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
