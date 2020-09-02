package ir.android.persiantask.ui.activity.project;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import ir.android.persiantask.R;

public class ProjectActivity extends AppCompatActivity {

    public static final int ADD_PROJECT_REQUEST = 1;
    public static final int EDIT_PROJECT_REQUEST = 2;
//    private ActivityProjectsBinding activityProjectBinding;
    private ProjectViewModel projectViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
//        activityProjectBinding = DataBindingUtil.setContentView(ProjectActivity.this, R.layout.activity_projects);
//        activityProjectBinding.setProjectsViewModel(projectViewModel);
    }
}
