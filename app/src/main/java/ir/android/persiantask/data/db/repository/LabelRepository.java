package ir.android.persiantask.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.dao.LabelDao;
import ir.android.persiantask.data.db.database.PersianTaskDb;
import ir.android.persiantask.data.db.entity.Label;

public class LabelRepository {
    private LabelDao labelDao;
    private LiveData<List<Label>> allLabel;

    public LabelRepository(Application application) {
        PersianTaskDb persianTaskDb = PersianTaskDb.getInstance(application);
        labelDao = persianTaskDb.labelDao();
        allLabel = labelDao.getAllLabel();
    }

    public void insert(Label label) {
        new LabelRepository.InsertLabelAsyncTask(labelDao).execute(label);
    }

    public void update(Label label) {
        new LabelRepository.UpdateLabelAsyncTask(labelDao).execute(label);
    }

    public void delete(Label label) {
        new LabelRepository.DeleteProjectAsyncTask(labelDao).execute(label);
    }

    public LiveData<List<Label>> getAllLabel() {
        return allLabel;
    }

    private static class InsertLabelAsyncTask extends AsyncTask<Label, Void, Void> {

        private LabelDao labelDao;

        private InsertLabelAsyncTask(LabelDao labelDao) {
            this.labelDao = labelDao;
        }

        @Override
        protected Void doInBackground(Label... label) {
            labelDao.insert(label[0]);
            return null;
        }
    }

    private static class UpdateLabelAsyncTask extends AsyncTask<Label, Void, Void> {

        private LabelDao labelDao;

        private UpdateLabelAsyncTask(LabelDao labelDao) {
            this.labelDao = labelDao;
        }

        @Override
        protected Void doInBackground(Label... label) {
            labelDao.update(label[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Label, Void, Void> {

        private LabelDao labelDao;

        private DeleteProjectAsyncTask(LabelDao labelDao) {
            this.labelDao = labelDao;
        }

        @Override
        protected Void doInBackground(Label... label) {
            labelDao.delete(label[0]);
            return null;
        }
    }
}
