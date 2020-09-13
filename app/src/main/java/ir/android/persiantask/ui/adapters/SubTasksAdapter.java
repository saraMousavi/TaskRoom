package ir.android.persiantask.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.ui.activity.task.AddEditTaskActivity;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.viewmodels.SubTasksViewModel;

public class SubTasksAdapter extends ListAdapter<Subtasks, RecyclerView.ViewHolder> implements AddEditTaskActivity.ClickAddSubTaskListener {
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_ADD = 1;
    private SubTasksAddItemViewHolder subTasksAddItemViewHolder;
    private FragmentActivity mFragmentActivity;
    private SharedPreferences sharedPreferences;
    private SubTasksViewModel subTasksViewModel;
    private static final DiffUtil.ItemCallback<Subtasks> DIFF_CALLBACK = new DiffUtil.ItemCallback<Subtasks>() {
        @Override
        public boolean areItemsTheSame(@NonNull Subtasks oldItem, @NonNull Subtasks newItem) {
            return oldItem.getSubtasks_id() == newItem.getSubtasks_id();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Subtasks oldItem, @NonNull Subtasks newItem) {
            return oldItem.getSubtasks_title().equals(newItem.getSubtasks_title());
        }
    } ;
    public SubTasksAdapter(FragmentActivity fragmentActivity, SubTasksViewModel subTasksViewModel){
        super(DIFF_CALLBACK);
        this.mFragmentActivity = fragmentActivity;
        this.subTasksViewModel = subTasksViewModel;
        this.sharedPreferences  = PreferenceManager
                .getDefaultSharedPreferences(mFragmentActivity);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View subtasksView = inflater.inflate(R.layout.subtasks_item_recyclerview, parent, false);
            return new SubTasksItemViewHolder(subtasksView);
        } else if(viewType == VIEW_TYPE_ADD){
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View addSubtasksView = inflater.inflate(R.layout.subtasks_item_add, parent, false);
            return new SubTasksAddItemViewHolder(addSubtasksView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SubTasksItemViewHolder) {
            SubTasksItemViewHolder subTasksItemViewHolder = (SubTasksItemViewHolder) holder;
            Subtasks subtasks = getItem(position);
            Init.toggleCompleteCircle(subTasksItemViewHolder.subtasksTitle, subTasksItemViewHolder.subtasksCompletedIcon, subtasks.getSubtasks_iscompleted());

            subTasksItemViewHolder.subtasksTitle.setText(subtasks.getSubtasks_title());
            subTasksItemViewHolder.subtasksCompletedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Subtasks newSubtasks = new Subtasks(subtasks.getSubtasks_title(), subtasks.getSubtasks_iscompleted() == 1 ? 0 : 1, subtasks.getTasks_id());
                    newSubtasks.setSubtasks_id(subtasks.getSubtasks_id());
                    subTasksViewModel.update(newSubtasks);
                    notifyDataSetChanged();
                }
            });
            subTasksItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subTasksItemViewHolder.editSubtasksEditText.setText(subtasks.getSubtasks_title());
                    subTasksItemViewHolder.editSubtaskConstarintLayout.setVisibility(View.VISIBLE);
                    subTasksItemViewHolder.subtaskRow.setVisibility(View.GONE);
                    subTasksItemViewHolder.editSubstasksIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Subtasks subtask = new Subtasks(subTasksItemViewHolder.editSubtasksEditText.getText().toString(),
                                    subtasks.getSubtasks_iscompleted(), subtasks.getTasks_id());
                            subtask.setSubtasks_id(subtasks.getSubtasks_id());
                            subTasksViewModel.update(subtask);
                            notifyDataSetChanged();
                            subTasksItemViewHolder.editSubtaskConstarintLayout.setVisibility(View.GONE);
                            subTasksItemViewHolder.subtaskRow.setVisibility(View.VISIBLE);
                        }
                    });
                    subTasksItemViewHolder.removeEditRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            subTasksItemViewHolder.editSubtaskConstarintLayout.setVisibility(View.GONE);
                            subTasksItemViewHolder.subtaskRow.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } else if(holder instanceof SubTasksAddItemViewHolder){
            subTasksAddItemViewHolder = (SubTasksAddItemViewHolder) holder;
            subTasksAddItemViewHolder.itemView.setVisibility(View.VISIBLE);
            subTasksAddItemViewHolder.removeAddRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //@TODO add animation
                    View parentView = (View) subTasksAddItemViewHolder.itemView;
                    parentView.setVisibility(View.GONE);
                }
            });

            subTasksAddItemViewHolder.insertSubstasksIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Subtasks subtasks = new Subtasks(subTasksAddItemViewHolder.addNewSubtasks.getText().toString(), 0, sharedPreferences.getLong("tempTaskID", 0));
                    subTasksViewModel.insert(subtasks);
                }
            });
        }

    }

    @Override
    public void addSubTaskListener() {
        subTasksAddItemViewHolder.itemView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        return position + 1 == getItemCount() ? VIEW_TYPE_ADD : VIEW_TYPE_ITEM;
    }

    public class SubTasksItemViewHolder extends RecyclerView.ViewHolder{
        ImageView subtasksCompletedIcon;
        TextView subtasksTitle;
        ConstraintLayout editSubtaskConstarintLayout, subtaskRow;
        EditText editSubtasksEditText;
        ImageView editSubstasksIcon, removeEditRow;
        public SubTasksItemViewHolder(@NonNull View itemView) {
            super(itemView);
            subtasksCompletedIcon = itemView.findViewById(R.id.subtasksCompletedIcon);
            editSubtaskConstarintLayout = itemView.findViewById(R.id.editSubtaskConstarintLayout);
            subtaskRow = itemView.findViewById(R.id.subtaskRow);
            editSubtasksEditText = itemView.findViewById(R.id.editSubtasksEditText);
            editSubstasksIcon = itemView.findViewById(R.id.editSubstasksIcon);
            removeEditRow = itemView.findViewById(R.id.removeEditRow);
            subtasksTitle = itemView.findViewById(R.id.subtasksTitle);
        }
    }

    public class SubTasksAddItemViewHolder extends RecyclerView.ViewHolder{
        ImageView removeAddRow;
        ImageView insertSubstasksIcon;
        EditText addNewSubtasks;
        public SubTasksAddItemViewHolder(@NonNull View itemView) {
            super(itemView);
            removeAddRow = itemView.findViewById(R.id.removeAddRow);
            addNewSubtasks = itemView.findViewById(R.id.addNewSubtasks);
            insertSubstasksIcon = itemView.findViewById(R.id.insertSubstasksIcon);
        }
    }

    public Subtasks getSubTaskAt(int position){
        return getItem(position);
    }
}
