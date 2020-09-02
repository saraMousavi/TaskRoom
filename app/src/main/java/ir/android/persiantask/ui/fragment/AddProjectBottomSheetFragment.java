package ir.android.persiantask.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.adapters.SpinnerProjectCategoryAdapter;

public class AddProjectBottomSheetFragment extends BottomSheetDialogFragment {
    private AppCompatSpinner projectCategory;
    private View inflatedView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.projects_add_fragment, container, false);
        this.inflatedView = inflatedView;
        init();
        return inflatedView;
    }

    private void init() {
        projectCategory = this.inflatedView.findViewById(R.id.projectCategory);
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getResources().getString(R.string.art));
        categories.add(getResources().getString(R.string.sports));
        SpinnerProjectCategoryAdapter spPrjectCategory = new SpinnerProjectCategoryAdapter(getContext(), R.layout.projects_category_spinner_row,categories);

        projectCategory.setAdapter(spPrjectCategory);
    }
}
