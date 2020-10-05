package ir.android.persiantask.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.dao.AttachmentsDao;
import ir.android.persiantask.data.db.database.PersianTaskDb;
import ir.android.persiantask.data.db.entity.Attachments;

public class AttachmentsRepository {
    private AttachmentsDao attachmentsDao;
    private LiveData<List<Attachments>> allTaskAttachments;
    private LiveData<List<Attachments>> allRemindersAttachments;
    private LiveData<List<Attachments>> allCategoryAttachments;

    public AttachmentsRepository(Application application, Long foreignID) {
        PersianTaskDb chopTimedb = PersianTaskDb.getInstance(application);
        attachmentsDao = chopTimedb.attachmentsDao();
        allTaskAttachments = attachmentsDao.getAllTasksAttachments(foreignID);
        allRemindersAttachments = attachmentsDao.getAllRemindersAttachments(foreignID);
        allCategoryAttachments = attachmentsDao.getAllCategoryAttachments(foreignID);
    }

    public void insert(Attachments attachments) {
        new InsertAttachmentsAsyncTask(attachmentsDao).execute(attachments);
    }

    public void update(Attachments attachments) {
        new UpdateAttachmentsAsyncTask(attachmentsDao).execute(attachments);
    }

    public void delete(Attachments attachments) {
        new DeleteProjectAsyncTask(attachmentsDao).execute(attachments);
    }

    public LiveData<List<Attachments>> getAllTaskAttachments() {
        return allTaskAttachments;
    }

    public LiveData<List<Attachments>> getAllRemindersAttachments() {
        return allRemindersAttachments;
    }

    public LiveData<List<Attachments>> getAllCategoryAttachments() {
        return allCategoryAttachments;
    }

    private static class InsertAttachmentsAsyncTask extends AsyncTask<Attachments, Void, Void> {

        private AttachmentsDao attachmentsDao;

        private InsertAttachmentsAsyncTask(AttachmentsDao attachmentsDao) {
            this.attachmentsDao = attachmentsDao;
        }

        @Override
        protected Void doInBackground(Attachments... attachments) {
            attachmentsDao.insert(attachments[0]);
            return null;
        }
    }

    private static class UpdateAttachmentsAsyncTask extends AsyncTask<Attachments, Void, Void> {

        private AttachmentsDao attachmentsDao;

        private UpdateAttachmentsAsyncTask(AttachmentsDao attachmentsDao) {
            this.attachmentsDao = attachmentsDao;
        }

        @Override
        protected Void doInBackground(Attachments... attachments) {
            attachmentsDao.update(attachments[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Attachments, Void, Void> {

        private AttachmentsDao attachmentsDao;

        private DeleteProjectAsyncTask(AttachmentsDao attachmentsDao) {
            this.attachmentsDao = attachmentsDao;
        }

        @Override
        protected Void doInBackground(Attachments... attachments) {
            attachmentsDao.delete(attachments[0]);
            return null;
        }
    }
}
