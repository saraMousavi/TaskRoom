package ir.android.persiantask.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Category;
import ir.android.persiantask.ui.activity.category.CategoryActivity;
import ir.android.persiantask.utils.Init;

public class AddCategoryBottomSheetFragment extends BottomSheetDialogFragment {
    private View inflatedView;
    private SubmitClickListener submitClickListener;
    private Button insertCategoryBtn;
    private TextInputEditText categoryTitle;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.category_add_fragment, container, false);
        this.inflatedView = inflatedView;
        init();
        insertCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category category = new Category(categoryTitle.getText().toString());
                submitClickListener.onClickSubmit(category);
                dismiss();
            }
        });
        return inflatedView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        insertCategoryBtn = this.inflatedView.findViewById(R.id.insertCategoryBtn);
        categoryTitle = this.inflatedView.findViewById(R.id.categoryTitle);
        List<Map<View, Boolean>> views = new ArrayList<>();
        Map<View, Boolean> viewMap = new HashMap<>();
        viewMap.put(insertCategoryBtn, true);
        views.add(viewMap);
        Init.setViewBackgroundDependOnTheme(views, getContext());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        submitClickListener = (SubmitClickListener) context;
    }

    public interface SubmitClickListener{
        void onClickSubmit(Category category);
    }
}
