package ir.android.taskroom.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Category;
import ir.android.taskroom.utils.Init;
import ir.android.taskroom.utils.enums.ActionTypes;

public class AddCategoryBottomSheetFragment extends BottomSheetDialogFragment {
    private View inflatedView;
    private SubmitClickListener submitClickListener;
    private Button insertCategoryBtn;
    private TextInputEditText categoryTitle;
    private RadioGroup category_image_1, category_image_2;
    private RadioButton tempRadioButton = null;
    private Drawable TempDrawable;
    private String categoryImage = "ir.android.taskroom:drawable/ic_white_art";
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.category_add_fragment, container, false);
        this.inflatedView = inflatedView;
        init();
        onClickListener();
        setMasterTheme(getContext());
        return inflatedView;
    }

    private void onClickListener() {
        category_image_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    category_image_2.check(-1);
                    if (category_image_1.getCheckedRadioButtonId() != -1) {
                        RadioButton selectedIcon = inflatedView.findViewById(category_image_1.getCheckedRadioButtonId());
                        categoryImage = selectedIcon.getTag().toString();
                        switch (selectedIcon.getTag().toString()) {
                            case "ir.android.taskroom:drawable/ic_white_art":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_art));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_scientific":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_sientific));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_sports":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_sports));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_setting":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_setting));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_music":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_music));
                                break;
                        }

                        tempRadioButton = selectedIcon;
                    }

                }
            }
        });
        category_image_2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    category_image_1.check(-1);
                    if (category_image_2.getCheckedRadioButtonId() != -1) {
                        RadioButton selectedIcon = inflatedView.findViewById(category_image_2.getCheckedRadioButtonId());
                        categoryImage = selectedIcon.getTag().toString();
                        switch (selectedIcon.getTag().toString()) {
                            case "ir.android.taskroom:drawable/ic_white_chat":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_chat));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_restoorant":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_restoorant));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_airport":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_airport));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_flower":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_flower));
                                break;
                            case "ir.android.taskroom:drawable/ic_white_hospital":
                                if (tempRadioButton != null) tempRadioButton.setBackground(TempDrawable);
                                TempDrawable = selectedIcon.getBackground();
                                selectedIcon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_orange_hospital));
                                break;
                        }

                        tempRadioButton = selectedIcon;
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        insertCategoryBtn = this.inflatedView.findViewById(R.id.insertCategoryBtn);
        categoryTitle = this.inflatedView.findViewById(R.id.categoryTitle);
        category_image_1 = this.inflatedView.findViewById(R.id.category_image_1);
        category_image_2 = this.inflatedView.findViewById(R.id.category_image_2);
        List<Map<View, Boolean>> views = new ArrayList<>();
        Map<View, Boolean> viewMap = new HashMap<>();
        viewMap.put(insertCategoryBtn, true);
        views.add(viewMap);
        Init.setViewBackgroundDependOnTheme(views, getContext(), sharedPreferences.getBoolean("NIGHT_MODE", false));
        Bundle bundle = getArguments();
        if (bundle.getBoolean("isEditCategory")) {
            Category selectedCategory = (Category) bundle.getSerializable("clickedCategory");
            insertCategoryBtn.setText(getString(R.string.edit));
            categoryTitle.setText(selectedCategory.getCategory_title());
            insertCategoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category category = new Category(categoryTitle.getText().toString(), categoryImage,
                            categoryImage.replace("white", "black"));
                    category.setCategory_id(selectedCategory.getCategory_id());
                    submitClickListener.onClickSubmit(category, ActionTypes.EDIT);
                    dismiss();
                }
            });
        } else {
            insertCategoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (categoryTitle.getText().toString().isEmpty()) {
                        Snackbar
                                .make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.enterCategoryName), Snackbar.LENGTH_LONG)
                                .show();
                        return;
                    }
                    Category category = new Category(categoryTitle.getText().toString(), categoryImage,
                            categoryImage.replace("white", "black"));
                    submitClickListener.onClickSubmit(category, ActionTypes.ADD);
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        submitClickListener = (SubmitClickListener) context;
    }

    public interface SubmitClickListener {
        void onClickSubmit(Category category, ActionTypes actionTypes);
    }

    public void setMasterTheme(Context context) {
        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
            context.setTheme(R.style.FeedActivityThemeDark);
            return;
        }
        switch (getFlag(context)) {
            case 2:
                context.setTheme(R.style.AppTheme2);
                break;
            case 3:
                context.setTheme(R.style.AppTheme3);
                break;
            case 4:
                context.setTheme(R.style.AppTheme4);
                break;
            case 5:
                context.setTheme(R.style.AppTheme5);
                break;
            case 6:
                context.setTheme(R.style.AppTheme6);
                break;
            default:
                context.setTheme(R.style.AppTheme);
                break;
        }
    }


    public Integer getFlag(Context context) {
        SharedPreferences sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedpreferences.getInt("theme", 1);
    }
}
