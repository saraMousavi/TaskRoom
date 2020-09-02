package ir.android.persiantask.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.databinding.ProjectsFragmentBinding;
import ir.android.persiantask.ui.activity.project.ProjectViewModel;
import ir.android.persiantask.ui.activity.task.AddEditTaskActivity;
import ir.android.persiantask.ui.adapters.TasksAdapter;
import kotlin.jvm.JvmStatic;


public class ProjectsFragment extends Fragment {
    public static final int ADD_TASK_REQUEST = 1;
    public static final int EDIT_TASK_REQUEST = 2;
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_BG_COLOR = "arg_bg_color";
    private String title = "Default title.";
    private int bgColorResId = R.color.white;
    private View inflatedView;
    private HorizontalScrollView projectScrollView;
    private RecyclerView taskRecyclerView;
    private TasksAdapter taskAdapter;
    private CollapsingToolbarLayout toolBarLayout;
    private FloatingActionButton addTaskBtn;
    private String selectedProjectTitle = "";
    private ProjectsFragmentBinding projectsFragmentBinding;
    private ProjectViewModel projectViewModel;


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
        projectViewModel = ViewModelProviders.of(this).get(ProjectViewModel.class);
        projectsFragmentBinding.setProjectsViewModel(projectViewModel);
        this.inflatedView = view;

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.inflatedView.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(this.getContext()), this.bgColorResId));
        init();

        final LinearLayout linearLayout = (LinearLayout) projectScrollView.getChildAt(0);
        final int count = linearLayout.getChildCount();
        for(int i = 0 ;i < count - 1 ;i++){
            final ConstraintLayout constraintLayout = (ConstraintLayout) linearLayout.getChildAt(i);
            final int finalI = i;
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    constraintLayout.setBackground(getResources().getDrawable(R.drawable.dark_blue_corner_shape));
                    int textCount = constraintLayout.getChildCount();
                    TextView titleTextView = (TextView) constraintLayout.getChildAt(1);
                    selectedProjectTitle = (String) titleTextView.getText();
                    for(int z = 1 ; z < textCount ; z++){
                        TextView textView = (TextView) constraintLayout.getChildAt(z);
                        textView.setTextColor(getResources().getColor(R.color.white));
                    }
                    for(int j = 0 ; j < count - 1 ; j++){
                        if( j != finalI){
                            ConstraintLayout otherConstraint = (ConstraintLayout) linearLayout.getChildAt(j);
                            int otherConstraintCount = otherConstraint.getChildCount();
                            for(int z = 1 ; z < otherConstraintCount ; z++){
                                TextView textView = (TextView) otherConstraint.getChildAt(z);
                                textView.setTextColor(getResources().getColor(R.color.black));
                            }
                            linearLayout.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.light_gray_corner_shape));
                        }
                    }
                }
            });
        }

        ConstraintLayout addConstraintLayout = (ConstraintLayout) linearLayout.getChildAt(count - 1);
        addConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProjectBottomSheetFragment projectFragment = new AddProjectBottomSheetFragment();
                projectFragment.show(getFragmentManager(), "");
            }
        });

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        }).attachToRecyclerView(taskRecyclerView);

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

        AppBarLayout mAppBarLayout = (AppBarLayout) this.inflatedView.findViewById(R.id.app_bar);
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

    private void init() {
        projectScrollView = this.inflatedView.findViewById(R.id.projectScrollView);

        taskRecyclerView = this.inflatedView.findViewById(R.id.taskRecyclerView);
        addTaskBtn = this.inflatedView.findViewById(R.id.addTaskBtn);

        List<Tasks> tasks = new ArrayList<>();
        Tasks task = new Tasks(1, 1, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task1 = new Tasks(1, 0, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task2 = new Tasks(1, 1, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task3 = new Tasks(1, 0, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        Tasks task4 = new Tasks(1, 1, 1, 1, getString(R.string.task1), 0, 13990611, 0, 0, "", 13990614, 0, "");
        tasks.add(task);
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        taskAdapter = new TasksAdapter(getActivity(), tasks);
        taskRecyclerView.setAdapter(taskAdapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskRecyclerView.setNestedScrollingEnabled(false);

        final Toolbar toolbar = (Toolbar) this.inflatedView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolBarLayout = (CollapsingToolbarLayout) this.inflatedView.findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(" ");
//        toolbar.hideOverflowMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("requestCode = " + requestCode);
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
