package ir.android.persiantask.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.dao.SubtasksDao;
import ir.android.persiantask.data.db.database.PersianTaskDb;
import ir.android.persiantask.data.db.entity.Subtasks;

public class SubtasksRepository {
    private SubtasksDao subtasksDao;
    private LiveData<List<Subtasks>> allSubtasks;

    public SubtasksRepository(Application application, Long tasksID) {
        PersianTaskDb persianTaskDb = PersianTaskDb.getInstance(application);
        subtasksDao = persianTaskDb.subtasksDao();
        allSubtasks = subtasksDao.getAllSubtasks(tasksID);
    }

    public void insert(Subtasks subtasks) {
        new SubtasksRepository.InsertSubtasksAsyncTask(subtasksDao).execute(subtasks);
    }

    public void update(Subtasks subtasks) {
        new SubtasksRepository.UpdateSubtasksAsyncTask(subtasksDao).execute(subtasks);
    }

    public void delete(Subtasks subtasks) {
        new SubtasksRepository.DeleteProjectAsyncTask(subtasksDao).execute(subtasks);
    }

    public LiveData<List<Subtasks>> getAllSubtasks() {
        return allSubtasks;
    }

    private static class InsertSubtasksAsyncTask extends AsyncTask<Subtasks, Void, Void> {

        private SubtasksDao subtasksDao;

        private InsertSubtasksAsyncTask(SubtasksDao subtasksDao) {
            this.subtasksDao = subtasksDao;
        }

        @Override
        protected Void doInBackground(Subtasks... subtasks) {
            subtasksDao.insert(subtasks[0]);
            return null;
        }
    }

    private static class UpdateSubtasksAsyncTask extends AsyncTask<Subtasks, Void, Void> {

        private SubtasksDao subtasksDao;

        private UpdateSubtasksAsyncTask(SubtasksDao subtasksDao) {
            this.subtasksDao = subtasksDao;
        }

        @Override
        protected Void doInBackground(Subtasks... subtasks) {
            subtasksDao.update(subtasks[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Subtasks, Void, Void> {

        private SubtasksDao subtasksDao;

        private DeleteProjectAsyncTask(SubtasksDao subtasksDao) {
            this.subtasksDao = subtasksDao;
        }

        @Override
        protected Void doInBackground(Subtasks... subtasks) {
            subtasksDao.delete(subtasks[0]);
            return null;
        }
    }
}
