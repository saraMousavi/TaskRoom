package ir.android.persiantask.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.dao.TasksDao;
import ir.android.persiantask.data.db.database.PersianTaskDb;
import ir.android.persiantask.data.db.entity.Tasks;

public class TasksRepository {
    private TasksDao tasksDao;
    private LiveData<List<Tasks>> allTasks;

    public TasksRepository(Application application, Integer projectID) {
        PersianTaskDb persianTaskDb = PersianTaskDb.getInstance(application);
        tasksDao = persianTaskDb.tasksDao();
        allTasks = tasksDao.getAllTasks(projectID);
    }

    public void insert(Tasks tasks) {
        new TasksRepository.InsertTasksAsyncTask(tasksDao).execute(tasks);
    }

    public void update(Tasks tasks) {
        new TasksRepository.UpdateTasksAsyncTask(tasksDao).execute(tasks);
    }

    public void delete(Tasks tasks) {
        new TasksRepository.DeleteProjectAsyncTask(tasksDao).execute(tasks);
    }

    public LiveData<List<Tasks>> getAllTasks() {
        return allTasks;
    }

    private static class InsertTasksAsyncTask extends AsyncTask<Tasks, Void, Void> {

        private TasksDao tasksDao;

        private InsertTasksAsyncTask(TasksDao tasksDao) {
            this.tasksDao = tasksDao;
        }

        @Override
        protected Void doInBackground(Tasks... tasks) {
            tasksDao.insert(tasks[0]);
            return null;
        }
    }

    private static class UpdateTasksAsyncTask extends AsyncTask<Tasks, Void, Void> {

        private TasksDao tasksDao;

        private UpdateTasksAsyncTask(TasksDao tasksDao) {
            this.tasksDao = tasksDao;
        }

        @Override
        protected Void doInBackground(Tasks... tasks) {
            tasksDao.update(tasks[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Tasks, Void, Void> {

        private TasksDao tasksDao;

        private DeleteProjectAsyncTask(TasksDao tasksDao) {
            this.tasksDao = tasksDao;
        }

        @Override
        protected Void doInBackground(Tasks... tasks) {
            tasksDao.delete(tasks[0]);
            return null;
        }
    }
}
