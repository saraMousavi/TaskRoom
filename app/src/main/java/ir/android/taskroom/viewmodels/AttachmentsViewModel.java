package ir.android.taskroom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.taskroom.data.db.entity.Attachments;
import ir.android.taskroom.data.db.repository.AttachmentsRepository;

public class AttachmentsViewModel extends AndroidViewModel {
    private AttachmentsRepository attachmentsRepository;
    private LiveData<List<Attachments>> allTaskssAttachments;
    private LiveData<List<Attachments>> allRemindersAttachments;
    private LiveData<List<Attachments>> allCategoryAttachments;

    public AttachmentsViewModel(@NonNull Application application, Long foreignID) {
        super(application);
        attachmentsRepository = new AttachmentsRepository(application, foreignID);
        allTaskssAttachments = attachmentsRepository.getAllTaskAttachments();
        allRemindersAttachments = attachmentsRepository.getAllRemindersAttachments();
        allCategoryAttachments = attachmentsRepository.getAllCategoryAttachments();
    }

    public void insert(Attachments attachments) {
        attachmentsRepository.insert(attachments);
    }

    public void update(Attachments attachments) {
        attachmentsRepository.update(attachments);
    }

    public void delete(Attachments attachments) {
        attachmentsRepository.delete(attachments);
    }

    public LiveData<List<Attachments>> getAllTasksAttachments() {
        return allTaskssAttachments;
    }

    public LiveData<List<Attachments>> getAllRemindersAttachments() {
        return allRemindersAttachments;
    }

    public LiveData<List<Attachments>> getAllCategoryAttachments() {
        return allCategoryAttachments;
    }
}
