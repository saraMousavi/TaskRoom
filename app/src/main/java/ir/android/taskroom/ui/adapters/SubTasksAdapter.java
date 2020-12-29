package ir.android.taskroom.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.data.db.entity.Subtasks;
import ir.android.taskroom.ui.activity.task.AddEditTaskActivity;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.viewmodels.SubTasksViewModel;

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
    };

    public SubTasksAdapter(FragmentActivity fragmentActivity, SubTasksViewModel subTasksViewModel) {
        super(DIFF_CALLBACK);
        this.mFragmentActivity = fragmentActivity;
        this.subTasksViewModel = subTasksViewModel;
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mFragmentActivity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        setMasterTheme(context);
        if (viewType == VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View subtasksView = inflater.inflate(R.layout.subtasks_item_recyclerview, parent, false);
            return new SubTasksItemViewHolder(subtasksView);
        } else if (viewType == VIEW_TYPE_ADD) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View addSubtasksView = inflater.inflate(R.layout.subtasks_item_add, parent, false);
            return new SubTasksAddItemViewHolder(addSubtasksView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SubTasksItemViewHolder) {
            SubTasksItemViewHolder subTasksItemViewHolder = (SubTasksItemViewHolder) holder;
            Subtasks subtasks = getItem(position);
            Init.toggleCompleteCircle(subTasksItemViewHolder.subtasksTitle, subTasksItemViewHolder.subtasksCompletedIcon, subtasks.getSubtasks_iscompleted());

            subTasksItemViewHolder.subtasksTitle.setText(subtasks.getSubtasks_title());
            subTasksItemViewHolder.subtasksCompletedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Subtasks newSubtasks = new Subtasks(subtasks.getSubtasks_title(), subtasks.getSubtasks_iscompleted() == 1 ? 0 : 1, subtasks.getTasks_id(), subtasks.getProjects_id());
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
                                    subtasks.getSubtasks_iscompleted(), subtasks.getTasks_id(), subtasks.getProjects_id());
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
                            subTasksViewModel.delete(getSubTaskAt(position));
                        }
                    });
                }
            });
        } else if (holder instanceof SubTasksAddItemViewHolder) {
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
                    if (subTasksAddItemViewHolder.addNewSubtasks.getText().toString().isEmpty()) {
                        Snackbar snackbar = Snackbar
                                .make(mFragmentActivity.getWindow().getDecorView().findViewById(android.R.id.content),
                                        mFragmentActivity.getString(R.string.enterSubTaskName), Snackbar.LENGTH_LONG);
                        ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                        snackbar.show();
                        return;
                    }
                    Gson gson = new Gson();
                    String projectJson = sharedPreferences.getString("selectedProject", "");
                    Projects projects = gson.fromJson(projectJson, Projects.class);
                    Subtasks subtasks = new Subtasks(subTasksAddItemViewHolder.addNewSubtasks.getText().toString(), 0, sharedPreferences.getLong("tempTaskID", 0), projects.getProject_id());
                    subTasksViewModel.insert(subtasks);
                    subTasksAddItemViewHolder.addNewSubtasks.setText("");
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

    public class SubTasksItemViewHolder extends RecyclerView.ViewHolder {
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

    public class SubTasksAddItemViewHolder extends RecyclerView.ViewHolder {
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

    public Subtasks getSubTaskAt(int position) {
        return getItem(position);
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
