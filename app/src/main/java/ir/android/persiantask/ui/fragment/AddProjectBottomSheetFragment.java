package ir.android.persiantask.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.ui.adapters.SpinnerProjectCategoryAdapter;

public class AddProjectBottomSheetFragment extends BottomSheetDialogFragment {
    private AppCompatSpinner projectCategory;
    private View inflatedView;
    private SubmitClickListener submitClickListener;
    private Button insertProjectBtn;
    private TextInputEditText projectsTitle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.projects_add_fragment, container, false);
        this.inflatedView = inflatedView;
        init();
        Bundle bundle = getArguments();
        if(bundle.getBoolean("isEditProjects")){
            insertProjectBtn.setText(getString(R.string.edit));
            projectsTitle.setText(bundle.getString("projects_title"));
            insertProjectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Projects projects = new Projects(1,1,projectsTitle.getText().toString(), 1, 0);
                    submitClickListener.onClickSubmit(projects);
                    dismiss();
                }
            });
        } else {
            insertProjectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Projects projects = new Projects(1,1,projectsTitle.getText().toString(), 1, 0);
                    // TODO: 9/5/2020 change insert to update
                    submitClickListener.onClickSubmit(projects);
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

    public interface SubmitClickListener{
        void onClickSubmit(Projects projects);
    }


    private void init() {
        projectCategory = this.inflatedView.findViewById(R.id.projectCategory);
        insertProjectBtn = this.inflatedView.findViewById(R.id.insertProjectBtn);
        projectsTitle = this.inflatedView.findViewById(R.id.projectsTitle);
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getResources().getString(R.string.art));
        categories.add(getResources().getString(R.string.sports));
        SpinnerProjectCategoryAdapter spPrjectCategory = new SpinnerProjectCategoryAdapter(getContext(), R.layout.projects_category_spinner_row,categories);

        projectCategory.setAdapter(spPrjectCategory);
    }
}
