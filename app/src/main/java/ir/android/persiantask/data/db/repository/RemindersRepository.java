package ir.android.persiantask.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.dao.RemindersDao;
import ir.android.persiantask.data.db.database.PersianTaskDb;
import ir.android.persiantask.data.db.entity.Reminders;

public class RemindersRepository {
    private RemindersDao remindersDao;
    private LiveData<List<Reminders>> allReminders;

    public RemindersRepository(Application application) {
        PersianTaskDb persianTaskDb = PersianTaskDb.getInstance(application);
        remindersDao = persianTaskDb.remindersDao();
        allReminders = remindersDao.getAllReminders();
    }

    public void insert(Reminders reminders) {
        new RemindersRepository.InsertRemindersAsyncTask(remindersDao).execute(reminders);
    }

    public void update(Reminders reminders) {
        new RemindersRepository.UpdateRemindersAsyncTask(remindersDao).execute(reminders);
    }

    public void delete(Reminders reminders) {
        new RemindersRepository.DeleteProjectAsyncTask(remindersDao).execute(reminders);
    }

    public LiveData<List<Reminders>> getAllReminders() {
        return allReminders;
    }

    private static class InsertRemindersAsyncTask extends AsyncTask<Reminders, Void, Void> {

        private RemindersDao remindersDao;

        private InsertRemindersAsyncTask(RemindersDao remindersDao) {
            this.remindersDao = remindersDao;
        }

        @Override
        protected Void doInBackground(Reminders... reminders) {
            remindersDao.insert(reminders[0]);
            return null;
        }
    }

    private static class UpdateRemindersAsyncTask extends AsyncTask<Reminders, Void, Void> {

        private RemindersDao remindersDao;

        private UpdateRemindersAsyncTask(RemindersDao remindersDao) {
            this.remindersDao = remindersDao;
        }

        @Override
        protected Void doInBackground(Reminders... reminders) {
            remindersDao.update(reminders[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Reminders, Void, Void> {

        private RemindersDao remindersDao;

        private DeleteProjectAsyncTask(RemindersDao remindersDao) {
            this.remindersDao = remindersDao;
        }

        @Override
        protected Void doInBackground(Reminders... reminders) {
            remindersDao.delete(reminders[0]);
            return null;
        }
    }
}
