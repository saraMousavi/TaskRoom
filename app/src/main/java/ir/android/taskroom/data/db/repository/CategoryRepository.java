package ir.android.taskroom.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.taskroom.data.db.dao.CategoryDao;
import ir.android.taskroom.data.db.database.TaskRoomDb;
import ir.android.taskroom.data.db.entity.Category;

public class CategoryRepository {
    private CategoryDao categoryDao;
    private LiveData<List<Category>> allCategory;

    public CategoryRepository(Application application) {
        TaskRoomDb chopTimedb = TaskRoomDb.getInstance(application);
        categoryDao = chopTimedb.categoryDao();
        allCategory = categoryDao.getAllCategory();
    }

    public void insert(Category category) {
        new CategoryRepository.InsertCategoryAsyncTask(categoryDao).execute(category);
    }

    public void update(Category category) {
        new CategoryRepository.UpdateCategoryAsyncTask(categoryDao).execute(category);
    }

    public void delete(Category category) {
        new CategoryRepository.DeleteProjectAsyncTask(categoryDao).execute(category);
    }

    public LiveData<List<Category>> getAllCategory() {
        return allCategory;
    }

    private static class InsertCategoryAsyncTask extends AsyncTask<Category, Void, Void> {

        private CategoryDao categoryDao;

        private InsertCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... category) {
            categoryDao.insert(category[0]);
            return null;
        }
    }

    private static class UpdateCategoryAsyncTask extends AsyncTask<Category, Void, Void> {

        private CategoryDao categoryDao;

        private UpdateCategoryAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... category) {
            categoryDao.update(category[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Category, Void, Void> {

        private CategoryDao categoryDao;

        private DeleteProjectAsyncTask(CategoryDao categoryDao) {
            this.categoryDao = categoryDao;
        }

        @Override
        protected Void doInBackground(Category... category) {
            categoryDao.delete(category[0]);
            return null;
        }
    }
}
