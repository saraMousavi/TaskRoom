package ir.android.persiantask.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Category;

public class AddCategoryBottomSheetFragment extends BottomSheetDialogFragment {
    private View inflatedView;
    private SubmitClickListener submitClickListener;
    private Button insertCategoryBtn;
    private TextInputEditText categoryTitle;
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

    private void init() {
        insertCategoryBtn = this.inflatedView.findViewById(R.id.insertCategoryBtn);
        categoryTitle = this.inflatedView.findViewById(R.id.categoryTitle);
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
