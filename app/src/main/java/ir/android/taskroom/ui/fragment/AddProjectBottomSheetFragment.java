package ir.android.taskroom.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Category;
import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.enums.ActionTypes;
import ir.android.taskroom.viewmodels.CategoryViewModel;

public class AddProjectBottomSheetFragment extends BottomSheetDialogFragment {
    private AppCompatSpinner projectCategory;
    private View inflatedView;
    private SubmitClickListener submitClickListener;
    private Button insertProjectBtn;
    private Button deleteProjectBtn;
    private TextInputEditText projectsTitle;
    private TextInputLayout editText;
    private Category selectedCategory = null;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.projects_add_fragment, container, false);
        this.inflatedView = inflatedView;
        init();
        Bundle bundle = getArguments();
        Projects selectedProject = (Projects) bundle.getSerializable("clickedProject");
        if (bundle.getBoolean("isEditProjects")) {
            insertProjectBtn.setText(getString(R.string.edit));
            deleteProjectBtn.setVisibility(View.VISIBLE);
            projectsTitle.setText(selectedProject.getProjects_title());
            CategoryViewModel categoryViewModel = new CategoryViewModel(getActivity().getApplication());
            categoryViewModel.getAllCategory().observe(this, new Observer<List<Category>>() {
                @Override
                public void onChanged(List<Category> categories) {
                    for(Category category:categories){
                        if(category.getCategory_id().equals(selectedProject.getCategory_id())){
                            projectCategory.post(new Runnable() {
                                @Override
                                public void run() {
                                    selectedCategory = category;
                                    projectCategory.setSelection(categories.indexOf(category));
                                }
                            });
                        }
                    }
                }
            });

            insertProjectBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Projects projects = new Projects(1, selectedCategory.getCategory_id(), projectsTitle.getText().toString(), 1, selectedProject.getProjects_tasks_num());
                    projects.setProject_id(selectedProject.getProject_id());
                    submitClickListener.onClickSubmit(projects, ActionTypes.EDIT);
                    dismiss();
                }
            });
            deleteProjectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitClickListener.onClickSubmit(selectedProject, ActionTypes.DELETE);
                    dismiss();
                }
            });
        } else {
            insertProjectBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    Projects projects = new Projects(1, selectedCategory.getCategory_id(), projectsTitle.getText().toString(), 1, 0);
                    submitClickListener.onClickSubmit(projects, ActionTypes.ADD);
                    dismiss();
                }
            });
        }
        return inflatedView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        submitClickListener = (SubmitClickListener) getParentFragment();
    }

    public interface SubmitClickListener {
        void onClickSubmit(Projects projects, ActionTypes actionTypes);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        projectCategory = this.inflatedView.findViewById(R.id.projectCategory);
        insertProjectBtn = this.inflatedView.findViewById(R.id.insertProjectBtn);
        projectsTitle = this.inflatedView.findViewById(R.id.projectsTitle);
        editText = this.inflatedView.findViewById(R.id.editText);
        deleteProjectBtn = this.inflatedView.findViewById(R.id.deleteProjectBtn);
        CategoryViewModel categoryViewModel = new CategoryViewModel(getActivity().getApplication());
        categoryViewModel.getAllCategory().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                ArrayList<Category> spinnerArray = new ArrayList<>(categories);
                ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                projectCategory.setAdapter(categoryArrayAdapter);
            }
        });
        projectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (Category) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<Map<View, Boolean>> views = new ArrayList<>();
        Map<View, Boolean> viewMap = new HashMap<>();
        viewMap.put(insertProjectBtn, true);
        views.add(viewMap);
        Init.setViewBackgroundDependOnTheme(views, getContext(), sharedPreferences.getBoolean("NIGHT_MODE", false));
    }
}
