package ir.android.taskroom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.taskroom.data.db.entity.Category;
import ir.android.taskroom.data.db.repository.CategoryRepository;

public class CategoryViewModel extends AndroidViewModel {
    private CategoryRepository categoryRepository;
    private LiveData<List<Category>> allCategory;
    public CategoryViewModel(@NonNull Application application) {
        super(application);
        categoryRepository = new CategoryRepository(application);
        allCategory = categoryRepository.getAllCategory();
    }
    
    public void insert(Category category){categoryRepository.insert(category);}

    public void update(Category category){
        categoryRepository.update(category);
    }

    public void delete(Category category){
        categoryRepository.delete(category);
    }

    public LiveData<List<Category>> getAllCategory(){
        return allCategory;
    }
}
