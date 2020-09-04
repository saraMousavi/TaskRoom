package ir.android.persiantask.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import ir.android.persiantask.ui.fragment.AddProjectBottomSheetFragment;

public class ProjectsAdapter extends ListAdapter<Projects, RecyclerView.ViewHolder> {
    private ProjectsAdapter.OnItemClickListener listener;
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_ADD = 1;
    private FragmentManager mFragmentManager;
    private FragmentActivity mFragmentActivity;
    private int clickedPosition = 0;

    private static final DiffUtil.ItemCallback<Projects> DIFF_CALLBACK = new DiffUtil.ItemCallback<Projects>() {
        @Override
        public boolean areItemsTheSame(@NonNull Projects oldItem, @NonNull Projects newItem) {
            return oldItem.getProject_id() == newItem.getProject_id();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Projects oldItem, @NonNull Projects newItem) {
            return oldItem.getProjects_title().equals(newItem.getProjects_title());
        }
    };


    public ProjectsAdapter(FragmentManager fragmentManager, FragmentActivity fragmentActivity) {
        super(DIFF_CALLBACK);
        mFragmentManager = fragmentManager;
        mFragmentActivity = fragmentActivity;
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout addProjects;
        public ImageButton addPrjectIcon;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            addPrjectIcon = itemView.findViewById(R.id.addPrjectIcon);
            addProjects = itemView.findViewById(R.id.addProjects);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView prjCategory;
        public TextView projectsTitle, tasksNumVal, tasknum;
        public ConstraintLayout projectsBox;

        public ItemViewHolder(View itemView) {
            super(itemView);
            projectsBox = itemView.findViewById(R.id.projectsBox);
            prjCategory = itemView.findViewById(R.id.prjCategory);
            projectsTitle = itemView.findViewById(R.id.projectsTitle);
            tasksNumVal = itemView.findViewById(R.id.tasksNumVal);
            tasknum = itemView.findViewById(R.id.tasknum);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADD) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View projectsView = inflater.inflate(R.layout.projects_item_add, parent, false);
            return new AddViewHolder(projectsView);
        } else if (viewType == VIEW_TYPE_ITEM) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View projectsView = inflater.inflate(R.layout.projects_item_recyclerview, parent, false);
            return new ItemViewHolder(projectsView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Projects mProjects = getItem(position);
            itemViewHolder.projectsTitle.setText(mProjects.getProjects_title());

            itemViewHolder.projectsBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPosition = position;
                    notifyDataSetChanged();
                }
            });
            if(clickedPosition == position){
                itemViewHolder.projectsBox.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.dark_blue_corner_shape));
                itemViewHolder.projectsTitle.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
                itemViewHolder.tasksNumVal.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
                itemViewHolder.tasknum.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
            } else {
                itemViewHolder.projectsBox.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.light_gray_corner_shape));
                itemViewHolder.projectsTitle.setTextColor(mFragmentActivity.getResources().getColor(R.color.black));
                itemViewHolder.tasksNumVal.setTextColor(mFragmentActivity.getResources().getColor(R.color.black));
                itemViewHolder.tasknum.setTextColor(mFragmentActivity.getResources().getColor(R.color.black));
            }
        } else if (holder instanceof AddViewHolder) {
            final AddViewHolder addViewHolder = (AddViewHolder) holder;
            addViewHolder.addPrjectIcon.setVisibility(View.VISIBLE);
            addViewHolder.addProjects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddProjectBottomSheetFragment addProjectBottomSheetFragment = new AddProjectBottomSheetFragment();
                    addProjectBottomSheetFragment.show(mFragmentManager, "");
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position + 1 == getItemCount() ? VIEW_TYPE_ADD : VIEW_TYPE_ITEM;
    }

    public Projects getProjectAt(int position) {
        return getItem(position);
    }

    public interface OnItemClickListener {
        void OnItemClick(Projects projects);
    }

    public void setOnItemClickListener(ProjectsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
