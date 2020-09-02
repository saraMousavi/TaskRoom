package ir.android.persiantask.ui.activity.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import ir.android.persiantask.R;

public class AddEditProjectActivity extends AppCompatActivity {

    public static final String EXTRA_ID=
            "ir.mousavi.mvvm_project_test.ui.activity.project.id";
    public static final String EXTRA_NAME =
            "ir.mousavi.mvvm_project_test.ui.activity.project.name";
    TextInputEditText projectNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_add_activity);

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ID)){
            setTitle("Edit Project");
            projectNameEdit.setText(intent.getStringExtra(EXTRA_NAME));
        } else {
            setTitle("Add Project");
        }

    }

    private void SaveProject(){
        String name = projectNameEdit.getText().toString();
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
