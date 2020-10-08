package ir.android.persiantask.ui.activity.category;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Category;
import ir.android.persiantask.databinding.CategoryActivityBinding;
import ir.android.persiantask.ui.activity.task.AddEditTaskActivity;
import ir.android.persiantask.ui.adapters.CategoryAdapter;
import ir.android.persiantask.ui.fragment.AddCategoryBottomSheetFragment;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.utils.enums.ActionTypes;
import ir.android.persiantask.viewmodels.CategoryViewModel;

public class CategoryActivity extends AppCompatActivity implements AddCategoryBottomSheetFragment.SubmitClickListener {
    private CategoryActivityBinding categoryActivityBinding;
    private CategoryViewModel categoryViewModel;
    private FloatingActionButton addCategoryBtn;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setMasterTheme();
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        init();
        onTouchListener();
        onClickListener();
    }

    private void onClickListener() {
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategoryBottomSheetFragment addCategoryBottomSheetFragment = new AddCategoryBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isEditCategory", false);
                addCategoryBottomSheetFragment.setArguments(bundle);
                addCategoryBottomSheetFragment.show(getSupportFragmentManager(), "");
            }
        });

        categoryAdapter.setOnItemClickListener(new CategoryAdapter.CategoryClickListener() {
            @Override
            public void editCategory(Category category) {
                if (category.getCategory_id() < 4) {
                    Snackbar
                            .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.cantEditDefaultCategory), Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }
                AddCategoryBottomSheetFragment editCategoryBottomSheetFragment = new AddCategoryBottomSheetFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("isEditCategory", true);
                bundle.putSerializable("clickedCategory", (Serializable) category);
                editCategoryBottomSheetFragment.setArguments(bundle);
                editCategoryBottomSheetFragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    private void onTouchListener() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Category selectedCategory = categoryAdapter.getCategoryAt(viewHolder.getAdapterPosition());
                if (selectedCategory.getCategory_id() < 4) {
                    Snackbar
                            .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.cantDeleteDefaultCategory), Snackbar.LENGTH_LONG)
                            .show();
                    categoryAdapter.notifyDataSetChanged();
                    return;
                }
                //@TODO cant delete category that use in project
                CategoryViewModel categoryViewModel = ViewModelProviders.of(CategoryActivity.this).get(CategoryViewModel.class);
                categoryViewModel.delete(selectedCategory);
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successDeleteCategory), Snackbar.LENGTH_LONG)
                        .show();
            }
        }).attachToRecyclerView(categoryRecyclerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        // Re-enter transition is executed when returning to this activity
        Slide slideTransition = new Slide();
        slideTransition.setSlideEdge(Gravity.LEFT);
        slideTransition.setDuration(1000);
        getWindow().setReenterTransition(slideTransition);
        getWindow().setExitTransition(slideTransition);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        categoryActivityBinding = DataBindingUtil.setContentView(CategoryActivity.this, R.layout.category_activity);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        categoryActivityBinding.setCategoryViewModel(categoryViewModel);
        addCategoryBtn = findViewById(R.id.addCategoryBtn);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryAdapter = new CategoryAdapter(getApplicationContext());
        categoryViewModel.getAllCategory().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                categoryAdapter.submitList(categories);
            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Map<View, Boolean>> views = new ArrayList<>();
        Map<View, Boolean> viewMap = new HashMap<>();
        viewMap.put(addCategoryBtn, false);
        views.add(viewMap);
        this.sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(CategoryActivity.this);
        Init.setViewBackgroundDependOnTheme(views, CategoryActivity.this,sharedPreferences.getBoolean("NIGHT_MODE", false));
    }

    @Override
    public void onClickSubmit(Category category, ActionTypes actionTypes) {
        switch (actionTypes){
            case ADD:
                categoryViewModel.insert(category);
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertCategory), Snackbar.LENGTH_LONG)
                        .show();
                break;
            case EDIT:
                categoryViewModel.update(category);
                Snackbar
                        .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successEditCategory), Snackbar.LENGTH_LONG)
                        .show();
                break;
        }
        categoryAdapter.notifyDataSetChanged();

    }

    public void setMasterTheme() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(CategoryActivity.this);
        if (sharedPreferences.getBoolean("NIGHT_MODE", false)) {
            setTheme(R.style.FeedActivityThemeDark);
            return;
        }
        switch (getFlag()) {
            case 2:
                setTheme(R.style.AppTheme2);
                break;
            case 3:
                setTheme(R.style.AppTheme3);
                break;
            case 4:
                setTheme(R.style.AppTheme4);
                break;
            case 5:
                setTheme(R.style.AppTheme5);
                break;
            case 6:
                setTheme(R.style.AppTheme6);
                break;
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }


    public Integer getFlag() {
        SharedPreferences sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedpreferences.getInt("theme", 1);
    }
}
