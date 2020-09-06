package ir.android.persiantask.ui.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import ir.android.persiantask.R;

public class AddEditTaskActivity extends AppCompatActivity {
    public static final String EXTRA_ID=
            "ir.android.data.db.entity.projects.id";
    public static final String EXTRA_NAME =
            "ir.android.data.db.entity.projects.title";
    TextInputEditText taskNameEdit;
    FloatingActionButton fabInsertTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ID)){
            setTitle("Edit Project");
            taskNameEdit.setText(intent.getStringExtra(EXTRA_NAME));
        } else {
            setTitle("Add Project");
        }
        fabInsertTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveProject();
            }
        });
    }

    private void init() {
        setContentView(R.layout.tasks_add_activity);
        fabInsertTask = findViewById(R.id.fabInsertTask);
        taskNameEdit = findViewById(R.id.taskNameEdit);
    }

    private void SaveProject(){
        String name = taskNameEdit.getText().toString();
        if(name.trim().isEmpty()){
            Toast.makeText(this, "please insert name", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NAME, name);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id != -1){
            intent.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}
