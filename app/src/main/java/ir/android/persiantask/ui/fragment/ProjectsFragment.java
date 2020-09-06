package ir.android.persiantask.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.factory.ProjectsViewModelFactory;
import ir.android.persiantask.databinding.ProjectsFragmentBinding;
import ir.android.persiantask.viewmodels.ProjectViewModel;
import ir.android.persiantask.ui.adapters.ProjectsAdapter;
import kotlin.jvm.JvmStatic;



public class ProjectsFragment extends Fragment implements AddProjectBottomSheetFragment.SubmitClickListener {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private String title = "Default title.";
    private int bgColorResId = R.color.white;
    private View inflatedView;
    private RecyclerView projectRecyclerView;
    private ProjectsAdapter projectsAdapter;
    private CollapsingToolbarLayout toolBarLayout;
    private String selectedProjectTitle = "";
    private ProjectsFragmentBinding projectsFragmentBinding;
    private ProjectViewModel projectViewModel;
    private AppBarLayout mAppBarLayout;
    private Button firstAddProjectBtn;
    private ConstraintLayout projectsEmptyPage;
    private List<Fragment> taskFragList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = this.getArguments();
        if (arguments != null) {
            this.title = arguments.getString(ARG_TITLE);
            this.bgColorResId = arguments.getInt(ARG_BG_COLOR);
        }
    }

    @Override
    @Nullable
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        projectsFragmentBinding = DataBindingUtil.inflate(
                inflater, R.layout.projects_fragment, container, false);
        View view = projectsFragmentBinding.getRoot();
        this.inflatedView = view;
        init();
        //show project horizontal recycler view
        projectsRecyclerView();

        //onclick event for each project recycler view item
        projectRecyclerViewItemOnclick();

        firstAddProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProjectBottomSheetFragment addProjectBottomSheetFragment = new AddProjectBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEditProjects", false);
                addProjectBottomSheetFragment.setArguments(bundle);
                addProjectBottomSheetFragment.show(getChildFragmentManager(), "");
            }
        });

        return view;
    }

    /**
     * onclick event for each project recycler view item
     */
    private void projectRecyclerViewItemOnclick() {
        projectsAdapter.setOnItemClickListener(new ProjectsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Projects projects) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment selectedFragment = taskFragList.get(projects.getProject_id() - 1);
                ft.replace(R.id.taskFragmentContainer, selectedFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    /**
     * show project data in horizontal recycler view
     */
    private void projectsRecyclerView() {
        taskFragList = new ArrayList<>();
        projectViewModel.getAllProjects().observe(this, new Observer<List<Projects>>() {
            @Override
            public void onChanged(List<Projects> projects) {

                for (Projects project : projects) {
                    TasksFragment tasksFragment = new TasksFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("projectID", project.getProject_id());
                    tasksFragment.setArguments(bundle);
                    taskFragList.add(tasksFragment);
                }
                if (projects.size() == 0) {
                    projectsEmptyPage.setVisibility(View.VISIBLE);
                    mAppBarLayout.setVisibility(View.GONE);
                } else {
                    //null added to list for add Btn at the end of recyclerview
                    projects.add(null);
                    projectsEmptyPage.setVisibility(View.GONE);
                    mAppBarLayout.setVisibility(View.VISIBLE);
                    projectRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (projectRecyclerView.findViewHolderForAdapterPosition(0) != null) {
//                                Objects.requireNonNull(projectRecyclerView.findViewHolderForAdapterPosition(0)).itemView.performClick();
                            }
                        }
                    }, 40);
                }
                projectsAdapter.submitList(projects);
            }
        });
        projectRecyclerView.setAdapter(projectsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        layoutManager.setReverseLayout(true);
        projectRecyclerView.setLayoutManager(layoutManager);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.inflatedView.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(this.getContext()), this.bgColorResId));



//        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener(){
//            @Override
//            public void OnItemClick(Tasks task) {
//                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
//                intent.putExtra(AddEditTaskActivity.EXTRA_ID, task.getTasks_id());
//                intent.putExtra(AddEditTaskActivity.EXTRA_NAME, task.getTasks_title());
//                startActivityForResult(intent, EDIT_TASK_REQUEST);
//
//            }
//        });
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolBarLayout.setTitle(selectedProjectTitle);
                    isShow = true;
                } else if (isShow) {
                    toolBarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    public void onDestroyView() {
        super.onDestroyView();
    }

    @JvmStatic
    @NotNull
    public static ProjectsFragment newInstance(@NotNull String title, int bgColorId) {
        return Companion.newInstance(title, bgColorId);
    }

    /**
     * initialize parameter and assign them to layout's element
     */
    private void init() {
        mAppBarLayout = (AppBarLayout) this.inflatedView.findViewById(R.id.app_bar);
        ProjectsViewModelFactory factory = new ProjectsViewModelFactory(getActivity().getApplication(), null);
        projectViewModel = ViewModelProviders.of(this, factory).get(ProjectViewModel.class);
        projectsFragmentBinding.setProjectsViewModel(projectViewModel);
        projectRecyclerView = this.inflatedView.findViewById(R.id.projectRecyclerView);
        firstAddProjectBtn = this.inflatedView.findViewById(R.id.firstAddProjectBtn);
        projectsAdapter = new ProjectsAdapter(getChildFragmentManager(), getActivity(), this);
        projectsEmptyPage = inflatedView.findViewById(R.id.projectsEmptyPage);


        final Toolbar toolbar = (Toolbar) this.inflatedView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) this.inflatedView.findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(" ");
    }


    @Override
    public void onClickSubmit(Projects projects) {
        projectViewModel.insert(projects);
        Snackbar
                .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertProject), Snackbar.LENGTH_LONG)
                .show();
    }

    public static final class Companion {
        @JvmStatic
        @NotNull
        public static ProjectsFragment newInstance(@NotNull String title, int bgColorId) {
            ProjectsFragment screenSlidePageFragmentJ = new ProjectsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ARG_TITLE, title);
            bundle.putInt(ARG_BG_COLOR, bgColorId);
            screenSlidePageFragmentJ.setArguments(bundle);
            return screenSlidePageFragmentJ;
        }

        private Companion() {
        }

    }

}
