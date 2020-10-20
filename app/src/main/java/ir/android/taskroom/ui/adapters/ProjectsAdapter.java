package ir.android.taskroom.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Category;
import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.ui.fragment.AddProjectBottomSheetFragment;
import ir.android.taskroom.viewmodels.CategoryViewModel;

public class ProjectsAdapter extends ListAdapter<Projects, RecyclerView.ViewHolder> {
    private ProjectsAdapter.OnItemClickListener listener;
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_ADD = 1;
    private FragmentManager mFragmentManager;
    private FragmentActivity mFragmentActivity;
    private int clickedPosition = 0;
    private SharedPreferences sharedPreferences;

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
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mFragmentActivity);
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
        Context context = parent.getContext();
        setMasterTheme(context);
        if (viewType == VIEW_TYPE_ADD) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View projectsView = inflater.inflate(R.layout.projects_item_add, parent, false);
            return new AddViewHolder(projectsView);
        } else if (viewType == VIEW_TYPE_ITEM) {
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
            //show icon depend on category_id
            CategoryViewModel categoryViewModel = ViewModelProviders.of(mFragmentActivity).get(CategoryViewModel.class);
            categoryViewModel.getAllCategory().observe(mFragmentActivity, new Observer<List<Category>>() {
                @Override
                public void onChanged(List<Category> categories) {
                    for(Category category: categories){
                        if(category.getCategory_id().equals(getProjectAt(position).getCategory_id())){
                            itemViewHolder.prjCategory.setImageResource(mFragmentActivity.getResources().getIdentifier(category.getCategory_white_image(),"xml", null));
                        }
                    }
                }
            });

//            Init.setProjectCategory(itemViewHolder.prjCategory, getProjectAt(position).getCategory_id(), true);
            itemViewHolder.projectsBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPosition = position;
                    listener.OnItemClick(getProjectAt(position));
                    notifyDataSetChanged();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String jsonProject = gson.toJson(getProjectAt(position));
                    editor.remove("selectedProject");
                    editor.putString("selectedProject", jsonProject);
                    editor.apply();
                }
            });

            itemViewHolder.projectsBox.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AddProjectBottomSheetFragment editProjectBottomSheetFragment = new AddProjectBottomSheetFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isEditProjects", true);
                    bundle.putSerializable("clickedProject", (Serializable) getProjectAt(position));
                    editProjectBottomSheetFragment.setArguments(bundle);
                    editProjectBottomSheetFragment.show(mFragmentManager, "");
                    return false;
                }
            });

            if (clickedPosition == position) {
                Drawable drawable = null;
                if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
                    drawable = mFragmentActivity.getResources().getDrawable(R.drawable.dark_selected_corner_shape);
                } else {
                    TypedArray array = mFragmentActivity.getTheme().obtainStyledAttributes(getAppThemeStyle(mFragmentActivity), new int[]{R.attr.selectedBox});
                    int attrResourceId = array.getResourceId(0, 0);
                    drawable = ResourcesCompat.getDrawable(mFragmentActivity.getResources(), attrResourceId, null);
                }
                itemViewHolder.projectsBox.setBackground(drawable);
                itemViewHolder.projectsTitle.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
                itemViewHolder.tasksNumVal.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
                itemViewHolder.tasknum.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
            } else {
                if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
                    itemViewHolder.projectsBox.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.dark_item_corner_shape));
                    itemViewHolder.projectsTitle.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
                    itemViewHolder.tasksNumVal.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
                    itemViewHolder.tasknum.setTextColor(mFragmentActivity.getResources().getColor(R.color.white));
                } else {
                    itemViewHolder.projectsBox.setBackground(mFragmentActivity.getResources().getDrawable(R.drawable.light_gray_corner_shape));
                    itemViewHolder.projectsTitle.setTextColor(mFragmentActivity.getResources().getColor(R.color.black));
                    itemViewHolder.tasksNumVal.setTextColor(mFragmentActivity.getResources().getColor(R.color.black));
                    itemViewHolder.tasknum.setTextColor(mFragmentActivity.getResources().getColor(R.color.black));
                }
            }
            itemViewHolder.tasksNumVal.setText(String.valueOf(getProjectAt(position).getProjects_tasks_num()));
        } else if (holder instanceof AddViewHolder) {
            final AddViewHolder addViewHolder = (AddViewHolder) holder;
            addViewHolder.addProjects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddProjectBottomSheetFragment addProjectBottomSheetFragment = new AddProjectBottomSheetFragment();

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isEditProjects", false);
                    addProjectBottomSheetFragment.setArguments(bundle);
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

    public int getAppThemeStyle(Context context) {
        switch (getFlag(context)) {
            case 2:
                return R.style.AppTheme2;
            case 3:
                return R.style.AppTheme3;
            case 4:
                return R.style.AppTheme4;
            case 5:
                return R.style.AppTheme5;
            case 6:
                return R.style.AppTheme6;
            default:
                return R.style.AppTheme;
        }
    }
}
