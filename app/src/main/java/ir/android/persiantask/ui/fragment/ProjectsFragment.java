package ir.android.persiantask.ui.fragment;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
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
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.factory.ProjectsViewModelFactory;
import ir.android.persiantask.data.db.factory.SubTasksViewModelFactory;
import ir.android.persiantask.data.db.factory.TasksViewModelFactory;
import ir.android.persiantask.databinding.ProjectsFragmentBinding;
import ir.android.persiantask.ui.adapters.ProjectsAdapter;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.utils.enums.ActionTypes;
import ir.android.persiantask.utils.enums.ShowCaseSharePref;
import ir.android.persiantask.viewmodels.ProjectViewModel;
import ir.android.persiantask.viewmodels.SubTasksViewModel;
import ir.android.persiantask.viewmodels.TaskViewModel;
import kotlin.jvm.JvmStatic;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

/**
 * this fragment show when the project icon in bubble navigation clicking
 */
public class ProjectsFragment extends Fragment implements AddProjectBottomSheetFragment.SubmitClickListener {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private int bgColorResId = R.color.white;
    private View inflatedView, projectsEmptyPage;
    private RecyclerView projectRecyclerView;
    private ProjectsAdapter projectsAdapter;
    private CollapsingToolbarLayout toolBarLayout;
    private ProjectsFragmentBinding projectsFragmentBinding;
    private ProjectViewModel projectViewModel;
    private TaskViewModel taskViewModel;
    private SubTasksViewModel subTasksViewModel;
    private AppBarLayout mAppBarLayout;
    private Button firstAddProjectBtn;
    private HashMap<Integer, Fragment> taskFragList;
    private SharedPreferences sharedPreferences;
    private List<Tasks> tempTaskList = new ArrayList<>();
    private List<Subtasks> tempSubTaskList = new ArrayList<>();
    private boolean notUndoDelete = true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = this.getArguments();
        if (arguments != null) {
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

        onClickListener();

        return view;
    }

    /**
     * this function only invoke when user want to insert first project
     */
    private void onClickListener() {
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
    }

    /**
     * onclick event for each project recycler view item
     * for show the list of selected project's tasks in different
     * fragment
     */
    private void projectRecyclerViewItemOnclick() {
        projectsAdapter.setOnItemClickListener(new ProjectsAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Projects projects) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment selectedFragment = taskFragList.get(projects.getProject_id());
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
        projectViewModel.getAllProjects().observe(getViewLifecycleOwner(), new Observer<List<Projects>>() {
            @Override
            public void onChanged(List<Projects> projects) {
                taskFragList = new HashMap<>();
                for (Projects project : projects) {
                    TasksFragment tasksFragment = new TasksFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("projectID", project.getProject_id());
                    tasksFragment.setArguments(bundle);
                    taskFragList.put(project.getProject_id(), tasksFragment);
                }
                NestedScrollView taskFragmentContainer = inflatedView.findViewById(R.id.taskFragmentContainer);
                if (projects.size() == 0) {
                    projectsEmptyPage.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getContext(),
                            R.anim.slide_left_700);
                    projectsEmptyPage.startAnimation(animation);
                    mAppBarLayout.setVisibility(View.GONE);
                    taskFragmentContainer.setVisibility(View.GONE);
                    taskFragList.clear();
                    Init.initShowCaseView(getContext(), firstAddProjectBtn, getString(R.string.enterFirstProjectGuide),
                            ShowCaseSharePref.FIRST_PROJECT_GUIDE.getValue());
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Init.initShowCaseView(getContext(), projectRecyclerView.getChildAt(1),
                                    getString(R.string.enterSecondProjectGuide), ShowCaseSharePref.MORE_PROJECT_GUIDE.getValue());
                            if (projects.size() > 1) {
                                Init.initShowCaseView(getContext(), projectRecyclerView.getChildAt(0), getString(R.string.deleteEditProjectGuide),
                                        ShowCaseSharePref.EDIT_DELETE_PROJECT_GUIDE.getValue());
                            }
                        }
                    }, 1000);
                    //null added to list for add Btn at the end of recyclerview
                    projects.add(null);
                    projectsEmptyPage.setVisibility(View.GONE);
                    mAppBarLayout.setVisibility(View.VISIBLE);
                    taskFragmentContainer.setVisibility(View.VISIBLE);
                    projectRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (projectRecyclerView.findViewHolderForAdapterPosition(0) != null) {
                                Objects.requireNonNull(projectRecyclerView.findViewHolderForAdapterPosition(0)).itemView.performClick();
                            }
                        }
                    }, 40);
                }
                projectsAdapter.submitList(projects);
            }
        });
        projectRecyclerView.setAdapter(projectsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        projectRecyclerView.setLayoutManager(layoutManager);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.inflatedView.setBackgroundColor(ContextCompat.getColor(this.requireContext(), this.bgColorResId));
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    Gson gson = new Gson();
                    String projectJson = sharedPreferences.getString("selectedProject", "");
                    Projects selectedProject = gson.fromJson(projectJson, Projects.class);
                    toolBarLayout.setTitle(selectedProject.getProjects_title());
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
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        mAppBarLayout = (AppBarLayout) this.inflatedView.findViewById(R.id.app_bar);
        ProjectsViewModelFactory factory = new ProjectsViewModelFactory(getActivity().getApplication(), null);
        projectViewModel = ViewModelProviders.of(this, factory).get(ProjectViewModel.class);
        projectsFragmentBinding.setProjectsViewModel(projectViewModel);
        projectRecyclerView = this.inflatedView.findViewById(R.id.projectRecyclerView);
        firstAddProjectBtn = this.inflatedView.findViewById(R.id.firstAddProjectBtn);
        projectsAdapter = new ProjectsAdapter(getChildFragmentManager(), getActivity());
        projectsEmptyPage = inflatedView.findViewById(R.id.projectsEmptyPage);


        final Toolbar toolbar = (Toolbar) this.inflatedView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) this.inflatedView.findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(" ");
    }


    @Override
    public void onClickSubmit(Projects projects, ActionTypes actionTypes) {
        String msg = "";
        switch (actionTypes) {
            case ADD:
                projectViewModel.insert(projects);
                msg = getString(R.string.successInsertProject);

                Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                        .show();
                break;
            case EDIT:
                projectViewModel.update(projects);
                msg = getString(R.string.successUpdateProject);
                Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                        .show();
                break;
            case DELETE:
                msg = getString(R.string.successDeleteProject);
                Projects tempProject = projects;
                TasksViewModelFactory taskfactory = new TasksViewModelFactory(getActivity().getApplication(), projects.getProject_id());
                TaskViewModel tasksViewModel = ViewModelProviders.of(getActivity(), taskfactory).get(TaskViewModel.class);
                tasksViewModel.getAllTasks().observeForever(new Observer<List<Tasks>>() {
                    @Override
                    public void onChanged(List<Tasks> tasks) {
                        for (Tasks task : tasks) {
                            tempTaskList.add(task);
                            SubTasksViewModelFactory subfactory = new SubTasksViewModelFactory(getActivity().getApplication(), task.getTasks_id());
                            SubTasksViewModel subTasksViewModel = ViewModelProviders.of(getActivity(), subfactory).get(SubTasksViewModel.class);
                            subTasksViewModel.getAllSubtasks().observeForever(new Observer<List<Subtasks>>() {
                                @Override
                                public void onChanged(List<Subtasks> subtasks) {
                                    for (Subtasks subtask : subtasks) {
                                        tempSubTaskList.add(subtask);
                                        subTasksViewModel.delete(subtask);
                                    }
                                }
                            });
                            tasksViewModel.delete(task);
                        }
                        projectViewModel.delete(projects);
                    }
                });

                Snackbar snackbar = Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //@TODO add timer for undo
                        projectViewModel.insert(tempProject);
                        for (Tasks task : tempTaskList) {
                            try {
                                tasksViewModel.insert(task);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        for (Subtasks subtask : tempSubTaskList) {
                            subTasksViewModel.insert(subtask);
                        }
                        notUndoDelete = false;
                        snackbar.dismiss();
                    }
                }).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (notUndoDelete) {
                            taskFragList.remove(projects.getProject_id());
                        }
                    }
                }, 3000);
                break;
        }


        projectsAdapter.notifyDataSetChanged();

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
