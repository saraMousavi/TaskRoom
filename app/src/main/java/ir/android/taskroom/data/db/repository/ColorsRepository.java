package ir.android.taskroom.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.taskroom.data.db.dao.ColorsDao;
import ir.android.taskroom.data.db.database.TaskRoomDb;
import ir.android.taskroom.data.db.entity.Colors;

public class ColorsRepository {
    private ColorsDao colorsDao;
    private LiveData<List<Colors>> allColors;

    public ColorsRepository(Application application) {
        TaskRoomDb chopTimedb = TaskRoomDb.getInstance(application);
        colorsDao = chopTimedb.colorsDao();
        allColors = colorsDao.getAllColors();
    }

    public void insert(Colors colors) {
        new ColorsRepository.InsertColorsAsyncTask(colorsDao).execute(colors);
    }

    public void update(Colors colors) {
        new ColorsRepository.UpdateColorsAsyncTask(colorsDao).execute(colors);
    }

    public void delete(Colors colors) {
        new ColorsRepository.DeleteProjectAsyncTask(colorsDao).execute(colors);
    }

    public LiveData<List<Colors>> getAllColors() {
        return allColors;
    }

    private static class InsertColorsAsyncTask extends AsyncTask<Colors, Void, Void> {

        private ColorsDao colorsDao;

        private InsertColorsAsyncTask(ColorsDao colorsDao) {
            this.colorsDao = colorsDao;
        }

        @Override
        protected Void doInBackground(Colors... colors) {
            colorsDao.insert(colors[0]);
            return null;
        }
    }

    private static class UpdateColorsAsyncTask extends AsyncTask<Colors, Void, Void> {

        private ColorsDao colorsDao;

        private UpdateColorsAsyncTask(ColorsDao colorsDao) {
            this.colorsDao = colorsDao;
        }

        @Override
        protected Void doInBackground(Colors... colors) {
            colorsDao.update(colors[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Colors, Void, Void> {

        private ColorsDao colorsDao;

        private DeleteProjectAsyncTask(ColorsDao colorsDao) {
            this.colorsDao = colorsDao;
        }

        @Override
        protected Void doInBackground(Colors... colors) {
            colorsDao.delete(colors[0]);
            return null;
        }
    }
}
