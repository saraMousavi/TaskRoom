package ir.android.taskroom.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import ir.android.taskroom.R;
import ir.android.taskroom.utils.SettingUtil;
import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.data.db.entity.Subtasks;
import ir.android.taskroom.data.db.entity.Tasks;
import ir.android.taskroom.data.db.factory.ProjectsViewModelFactory;
import ir.android.taskroom.data.db.factory.SubTasksViewModelFactory;
import ir.android.taskroom.data.db.factory.TasksViewModelFactory;
import ir.android.taskroom.databinding.ProjectsFragmentBinding;
import ir.android.taskroom.ui.adapters.ProjectsAdapter;
import ir.android.taskroom.utils.EnglishInit;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.enums.ActionTypes;
import ir.android.taskroom.utils.enums.ShowCaseSharePref;
import ir.android.taskroom.utils.interfaces.ViewModelCallBackProjects;
import ir.android.taskroom.utils.interfaces.ViewModelCallBackSubTasks;
import ir.android.taskroom.utils.interfaces.ViewModelCallBackTasks;
import ir.android.taskroom.viewmodels.ProjectViewModel;
import ir.android.taskroom.viewmodels.SubTasksViewModel;
import ir.android.taskroom.viewmodels.TaskViewModel;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

/**
 * this fragment show when the project icon in bubble navigation clicking
 */
public class ProjectsFragment extends Fragment implements AddProjectBottomSheetFragment.SubmitClickListener {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private View inflatedView, projectsEmptyPage;
    private RecyclerView projectRecyclerView;
    private ProjectsAdapter projectsAdapter;
    private CollapsingToolbarLayout toolBarLayout;
    private ProjectsFragmentBinding projectsFragmentBinding;
    private ProjectViewModel projectViewModel;
    private SubTasksViewModel subTasksViewModel;
    private TaskViewModel tasksViewModel;
    private AppBarLayout mAppBarLayout;
    private Button firstAddProjectBtn;
    private HashMap<Long, Fragment> taskFragList;
    private SharedPreferences sharedPreferences;
    private List<Tasks> tempTaskList = new ArrayList<>();
    private Map<Tasks, List<Subtasks>> tempSubTaskMap = new HashMap<>();
    private ArrayList<Subtasks> tempSubtaskList = new ArrayList<>();
    private ProgressBar progressBar;
    private Integer tasknum, progressNum, progressPercent;
    private Projects tempProject;
    private boolean notUndo = true;


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
                if (projects != null) {
                    Fragment selectedFragment = taskFragList.get(projects.getProject_id());
                    if (selectedFragment != null) {
                        ft.replace(R.id.taskFragmentContainer, selectedFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }
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
                    bundle.putLong("projectID", project.getProject_id());
                    tasksFragment.setArguments(bundle);
                    taskFragList.put(project.getProject_id(), tasksFragment);
                }
                LinearLayout taskFragmentContainer = inflatedView.findViewById(R.id.taskFragmentContainer);
                if (projects.size() == 0) {
                    projectsEmptyPage.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getContext(),
                            R.anim.slide_left_700);
                    projectsEmptyPage.startAnimation(animation);
                    mAppBarLayout.setVisibility(View.GONE);
                    taskFragmentContainer.setVisibility(View.GONE);
                    taskFragList.clear();
                    String enterFirstProjectGuide = getString(R.string.enterFirstProjectGuide);
                    Init.initShowCaseView(getContext(), firstAddProjectBtn, enterFirstProjectGuide,
                            ShowCaseSharePref.FIRST_PROJECT_GUIDE.getValue(), null);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String enterSecondProjectGuide = getString(R.string.enterSecondProjectGuide);
                            Init.initShowCaseView(getContext(), projectRecyclerView.getChildAt(projectsAdapter.getItemCount() - 1),
                                    enterSecondProjectGuide, ShowCaseSharePref.MORE_PROJECT_GUIDE.getValue(), new GuideListener() {
                                        @Override
                                        public void onDismiss(View view) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.remove(ShowCaseSharePref.MORE_PROJECT_GUIDE.getValue());
                                            editor.putInt(ShowCaseSharePref.MORE_PROJECT_GUIDE.getValue(), 1);
                                            editor.apply();
                                            String deleteEditProjectGuide = getString(R.string.deleteEditProjectGuide);
                                            Init.initShowCaseView(getContext(), projectRecyclerView.getChildAt(0), deleteEditProjectGuide,
                                                    ShowCaseSharePref.EDIT_DELETE_PROJECT_GUIDE.getValue(), null);
                                        }
                                    });
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
                                Objects.requireNonNull(projectRecyclerView.findViewHolderForAdapterPosition(projectsAdapter.getClickedPosition())).itemView.performClick();
                            }
                        }
                    }, 400);
                }
                projectsAdapter.submitList(projects);
            }
        });
        projectRecyclerView.setAdapter(projectsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        projectRecyclerView.setLayoutManager(layoutManager);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        progressBar = inflatedView.findViewById(R.id.progressBar);


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
                try {
                    projectViewModel.insert(projects);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                msg = getString(R.string.successInsertProject);

                Snackbar snackbar = Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
                if(!SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                }
                snackbar.show();
                break;
            case EDIT:
                projectViewModel.update(projects);
                msg = getString(R.string.successUpdateProject);
                Snackbar snackbar1 = Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
                if(!SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                    ViewCompat.setLayoutDirection(snackbar1.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                }
                snackbar1.show();
                break;
            case DELETE:
                notUndo = true;
                tasknum = projects.getProjects_tasks_num();
//                progressNum = 0;
//                progressPercent = 100 / tasknum;
                deleteSubTasks(new ViewModelCallBackSubTasks() {
                    @Override
                    public void onSuccess() {
                        deleteTasks(new ViewModelCallBackTasks() {
                            @Override
                            public void onSuccess() {
                                deleteProject(new ViewModelCallBackProjects() {
                                    @Override
                                    public void onSuccess() {
                                        taskFragList.remove(tempProject.getProject_id());
                                        notUndo = false;
                                        undoDeleteProject();
                                    }
                                }, projects);
                            }
                        }, projects);
                    }
                }, projects);
                break;
        }


        projectsAdapter.notifyDataSetChanged();

    }

    private void undoDeleteProject() {

        String deleteProjectSnackbar = getString(R.string.successDeleteProject);
        String undo = getString(R.string.undo);
        Snackbar snackbar = Snackbar
                .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), deleteProjectSnackbar, Snackbar.LENGTH_LONG);
        if(!SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
            ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
        }
        snackbar.show();
        snackbar.setAction(undo, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //@TODO add timer for undo
                long prjID = 0;
                try {
                    prjID = projectViewModel.insert(tempProject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Tasks tasks : tempTaskList) {
                    tasks.setProjects_id(prjID);
                    try {
                        tasks.setWork_id(createWorkRequest(tasks, tasks.getTasks_iscompleted() == 0 ? true : false));
                        long newTaskID = tasksViewModel.insert(tasks);
                        for (Subtasks subtasks : tempSubtaskList) {
                            if (subtasks.getTasks_id().equals(tasks.getTasks_id())) {
                                subtasks.setTasks_id(newTaskID);
                                subTasksViewModel.insert(subtasks);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                snackbar.dismiss();
            }
        }).show();
    }

    private void deleteProject(ViewModelCallBackProjects callBackProjects, Projects projects) {
        tempProject = projects;
        projectViewModel.delete(projects);
        callBackProjects.onSuccess();
    }

    private void deleteTasks(ViewModelCallBackTasks callBackTasks, Projects projects) {
        TasksViewModelFactory taskfactory = new TasksViewModelFactory(getActivity().getApplication(), projects.getProject_id());
        tasksViewModel = ViewModelProviders.of(getActivity(), taskfactory).get(TaskViewModel.class);
        tasksViewModel.getAllProjectsTasks().observe(getViewLifecycleOwner(), new Observer<List<Tasks>>() {
            @Override
            public void onChanged(List<Tasks> tasks) {
                if (notUndo) {
                    tempTaskList = new ArrayList<>();
                    for (Tasks task : tasks) {
                        if (task.getWork_id().contains(",")) {
                            for (String requestId : task.getWork_id().split(",")) {
                                WorkManager.getInstance(getActivity()).cancelWorkById(UUID.fromString(requestId));
                            }
                        } else {
                            if (!task.getWork_id().equals("0") && !task.getWork_id().equals("-2")) {
                                WorkManager.getInstance(getActivity()).cancelWorkById(UUID.fromString(task.getWork_id()));
                            }
                        }
                        tasksViewModel.delete(task);
                        tempTaskList.add(task);
                    }
                    callBackTasks.onSuccess();
                }
            }
        });
    }

    private void deleteSubTasks(ViewModelCallBackSubTasks callBackSubTasks, Projects projects) {
        SubTasksViewModelFactory subfactory = new SubTasksViewModelFactory(getActivity().getApplication(), projects.getProject_id());
        subTasksViewModel = ViewModelProviders.of(getActivity(), subfactory).get(SubTasksViewModel.class);
        subTasksViewModel.getAllTasksSubTasksProjects().observe(getViewLifecycleOwner(), new Observer<List<Subtasks>>() {
            @Override
            public void onChanged(List<Subtasks> subtasks) {
                if (notUndo) {
                    tempSubtaskList = new ArrayList<>();
                    for (Subtasks subtask : subtasks) {
                        tempSubtaskList.add(subtask);
                        subTasksViewModel.delete(subtask);
                    }
                    callBackSubTasks.onSuccess();
                }
            }
        });
    }


    public String createWorkRequest(Tasks tasks, boolean isChecked) {
        if (isChecked) {
            DateTime dateTime1 = null;
            DateTime dateTime2 = null;
            if (tasks.getTasks_remindertime() == 1) {
                if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                    dateTime1 = Init.getCurrentDateTimeWithSecond();
                } else {
                    dateTime1 = EnglishInit.getCurrentDateTimeWithSecond();
                }

                dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_startdate()));
                if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                    if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                        dateTime2 = EnglishInit.getTodayDateTimeWithSelectedTime(tasks.getTasks_startdate(), 1, true);
                    } else {
                        dateTime2 = Init.getTodayDateTimeWithSelectedTime(tasks.getTasks_startdate(), 1, true);
                    }
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        return "-2";//start date past
                    }
                }
            } else if (!tasks.getTasks_repeateddays().isEmpty()) {
                if (tasks.getTasks_remindertime() == 3) {
                    if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                        dateTime1 = Init.getCurrentDateTimeWithSecond();
                    } else {
                        dateTime1 = EnglishInit.getCurrentDateTimeWithSecond();
                    }

                    dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_startdate()));
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                            dateTime2 = EnglishInit.getTodayDateTimeWithSelectedTime(tasks.getTasks_startdate(), 1, true);
                        } else {
                            dateTime2 = Init.getTodayDateTimeWithSelectedTime(tasks.getTasks_startdate(), 1, true);
                        }
                    }
                } else if (tasks.getTasks_remindertime() == 2) {
                    if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                        dateTime1 = EnglishInit.getCurrentDateTimeWithSecond();
                    } else {
                        dateTime1 = Init.getCurrentDateTimeWithSecond();
                    }

                    dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_enddate()));
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        return "-1";
                    }
                }
            } else {
                if (tasks.getTasks_remindertime() == 2) {
                    if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                        dateTime1 = EnglishInit.getCurrentDateTimeWithSecond();
                    } else {
                        dateTime1 = Init.getCurrentDateTimeWithSecond();
                    }
                    dateTime2 = Init.convertIntegerToDateTime(Init.integerFormatFromStringDate(tasks.getTasks_startdate()));
                    if (Init.convertDateTimeToInteger(dateTime2) < Init.convertDateTimeToInteger(dateTime1)) {
                        if (SettingUtil.getInstance(getContext()).isEnglishLanguage()) {
                            dateTime2 = EnglishInit.getTodayDateTimeWithSelectedTime(tasks.getTasks_startdate(), 1, true);
                        } else {
                            dateTime2 = Init.getTodayDateTimeWithSelectedTime(tasks.getTasks_startdate(), 1, true);
                        }

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
                    return Init.requestWork(getContext(), tasks.getTasks_title(), tasks.getTasks_comment(), tasks.getTasks_remindertype(),
                            Init.getWorkRequestPeriodicIntervalMillis(getContext().getResources(), tasks.getTasks_repeateddays()),
                            interval.toDurationMillis(), !tasks.getTasks_repeateddays().isEmpty(), false);
                }
            }
        } else {
            if (tasks.getWork_id().contains(",")) {
                for (String requestId : tasks.getWork_id().split(",")) {
                    WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(requestId));
                }
            } else {
                if (!tasks.getWork_id().equals("0") && !tasks.getWork_id().equals("-2")) {
                    WorkManager.getInstance(getContext()).cancelWorkById(UUID.fromString(tasks.getWork_id()));
                }
            }
        }

        return "0";
    }
}
