package ir.android.persiantask.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import net.vrgsoft.layoutmanager.RollingLayoutManager;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.factory.ProjectsViewModelFactory;
import ir.android.persiantask.data.db.factory.SubTasksViewModelFactory;
import ir.android.persiantask.data.db.factory.TasksViewModelFactory;
import ir.android.persiantask.databinding.TasksFragmentBinding;
import ir.android.persiantask.ui.activity.task.AddEditTaskActivity;
import ir.android.persiantask.ui.adapters.TasksAdapter;
import ir.android.persiantask.viewmodels.ProjectViewModel;
import ir.android.persiantask.viewmodels.SubTasksViewModel;
import ir.android.persiantask.viewmodels.TaskViewModel;

import static android.app.Activity.RESULT_OK;

public class TasksFragment extends Fragment{

    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    private TasksFragmentBinding tasksFragmentBinding;
    private TaskViewModel taskViewModel;
    private ProjectViewModel projectViewModel;
    private Projects selectedProject;
    private View inflatedView;
    private RecyclerView taskRecyclerView;
    private TasksAdapter taskAdapter;
    private FloatingActionButton addTaskBtn;
    private Button firstAddTaskBtn;
    private TasksViewModelFactory factory;
    private ProjectsViewModelFactory projectFactory;
    private Integer tasksNum;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tasksFragmentBinding = DataBindingUtil.inflate(
                inflater, R.layout.tasks_fragment, container, false);
        View view = tasksFragmentBinding.getRoot();
        this.inflatedView = view;
        init();

        tasksFragmentBinding.setTaskViewModel(taskViewModel);

        tasksRecyclerView();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Tasks selectedTask = taskAdapter.getTaskAt(viewHolder.getAdapterPosition());
                SubTasksViewModelFactory subfactory = new SubTasksViewModelFactory(getActivity().getApplication(), selectedTask.getTasks_id());
                SubTasksViewModel subTasksViewModel = ViewModelProviders.of(getActivity(), subfactory).get(SubTasksViewModel.class);
                subTasksViewModel.getAllSubtasks().observeForever(new Observer<List<Subtasks>>() {
                    @Override
                    public void onChanged(List<Subtasks> subtasks) {
                        for (Subtasks subtask: subtasks){
                            subTasksViewModel.delete(subtask);
                        }
                        taskViewModel.delete(selectedTask);
                    }
                });
                Projects projects = selectedProject;
                projects.setProjects_tasks_num(tasksNum - 1);
                projects.setProject_id(selectedProject.getProject_id());
                projectViewModel.update(projects);

                Snackbar
                        .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteTask), Snackbar.LENGTH_LONG)
                        .show();
            }
        }).attachToRecyclerView(taskRecyclerView);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });

        firstAddTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });

        taskAdapter.setOnItemClickListener(new TasksAdapter.TaskClickListener() {
            @Override
            public void switchContent(int subtaskConstarint, SubTaskFragment subTaskFragment) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(subtaskConstarint, subTaskFragment, subTaskFragment.toString());
                ft.addToBackStack(null);
                ft.commit();
            }

            @Override
            public void editTask(Tasks tasks) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("clickedTask", (Serializable) tasks);
                intent.putExtras(bundle);
                startActivityForResult(intent, EDIT_TASK_REQUEST);
            }
        });
        return view;
    }

    private void init() {
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String projectJson = sharedPreferences.getString("selectedProject", "");
        selectedProject = gson.fromJson(projectJson, Projects.class);
        factory = new TasksViewModelFactory(getActivity().getApplication(), selectedProject.getProject_id());
        projectFactory = new ProjectsViewModelFactory(getActivity().getApplication(), selectedProject.getProject_id());
        taskViewModel = ViewModelProviders.of(this, factory).get(TaskViewModel.class);
        projectViewModel = ViewModelProviders.of(this, projectFactory).get(ProjectViewModel.class);
        taskRecyclerView = this.inflatedView.findViewById(R.id.taskRecyclerView);
        firstAddTaskBtn = this.inflatedView.findViewById(R.id.firstAddTaskBtn);
        taskAdapter = new TasksAdapter(taskViewModel, getActivity(), getFragmentManager());
        addTaskBtn = getActivity().findViewById(R.id.addTaskBtn);
    }


    /**
     * show tasks data depend on selected project in vertical recyclerview
     */
    private void tasksRecyclerView() {
        taskViewModel.getAllTasks().observe(this, new Observer<List<Tasks>>() {
            @Override
            public void onChanged(List<Tasks> tasks) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                View taskList = (View) inflatedView.findViewById(R.id.taskList);
                View taskEmptyList = (View) inflatedView.findViewById(R.id.taskEmptyList);
                tasksNum = tasks.size();
                if (tasksNum == 0) {
                    taskList.setVisibility(View.GONE);
                    taskEmptyList.setVisibility(View.VISIBLE);
                    addTaskBtn.setVisibility(View.GONE);
                } else {
                    taskList.setVisibility(View.VISIBLE);
                    taskEmptyList.setVisibility(View.GONE);
                    addTaskBtn.setVisibility(View.VISIBLE);
                }
                taskAdapter.submitList(tasks);
            }
        });
        taskRecyclerView.setAdapter(taskAdapter);
        taskRecyclerView.setLayoutManager(new RollingLayoutManager(getContext()));
        taskRecyclerView.setNestedScrollingEnabled(false);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            Projects projects = selectedProject;
            projects.setProjects_tasks_num(tasksNum);
            projects.setProject_id(selectedProject.getProject_id());
            projectViewModel.update(projects);
            Snackbar
                    .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertTask), Snackbar.LENGTH_LONG)
                    .show();
        }
    }

}
