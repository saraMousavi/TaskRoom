package ir.android.persiantask.ui.activity.category;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.android.persiantask.R;
import ir.android.persiantask.data.db.entity.Category;
import ir.android.persiantask.databinding.CategoryActivityBinding;
import ir.android.persiantask.ui.activity.reminder.AddEditReminderActivity;
import ir.android.persiantask.ui.adapters.CategoryAdapter;
import ir.android.persiantask.ui.fragment.AddCategoryBottomSheetFragment;
import ir.android.persiantask.utils.Init;
import ir.android.persiantask.viewmodels.CategoryViewModel;

public class CategoryActivity extends AppCompatActivity implements AddCategoryBottomSheetFragment.SubmitClickListener {
    private CategoryActivityBinding categoryActivityBinding;
    private CategoryViewModel categoryViewModel;
    private FloatingActionButton addCategoryBtn;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategoryBottomSheetFragment addCategoryBottomSheetFragment = new AddCategoryBottomSheetFragment();
                addCategoryBottomSheetFragment.show(getSupportFragmentManager(), "");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(){
        categoryActivityBinding = DataBindingUtil.setContentView(CategoryActivity.this, R.layout.category_activity);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        categoryActivityBinding.setCategoryViewModel(categoryViewModel);
        addCategoryBtn = findViewById(R.id.addCategoryBtn);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryAdapter = new CategoryAdapter();
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
        Init.setViewBackgroundDependOnTheme(views, CategoryActivity.this);
    }

    @Override
    public void onClickSubmit(Category category) {
        categoryViewModel.insert(category);
        Snackbar
                .make(getWindow().getDecorView().findViewById(android.R.id.content), getString(R.string.successInsertCategory), Snackbar.LENGTH_LONG)
                .show();
    }
}
