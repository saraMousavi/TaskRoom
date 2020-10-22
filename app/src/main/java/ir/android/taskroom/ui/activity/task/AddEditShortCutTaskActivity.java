package ir.android.taskroom.ui.activity.task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.data.db.factory.ProjectsViewModelFactory;
import ir.android.taskroom.ui.activity.MainActivity;
import ir.android.taskroom.viewmodels.ProjectViewModel;

public class AddEditShortCutTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectsViewModelFactory projectFactory = new ProjectsViewModelFactory(getApplication(), null);
        ProjectViewModel projectViewModel = ViewModelProviders.of(this, projectFactory).get(ProjectViewModel.class);

        projectViewModel.getAllProjects().observe(this, new Observer<List<Projects>>() {
            @Override
            public void onChanged(List<Projects> projects) {
                if(projects.size() == 0){
                    Snackbar snackbar = Snackbar
                            .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.firstdefineProject), Snackbar.LENGTH_LONG);
                    ViewCompat.setLayoutDirection(snackbar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
                    snackbar.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(AddEditShortCutTaskActivity.this, MainActivity.class));
                            finish();
                        }
                    }, 1000);
                } else {
                    startActivity(new Intent(AddEditShortCutTaskActivity.this, AddEditTaskActivity.class));
                    finish();
                }
            }
        });
    }
}