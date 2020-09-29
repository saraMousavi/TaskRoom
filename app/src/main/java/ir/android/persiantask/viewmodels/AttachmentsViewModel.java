package ir.android.persiantask.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ir.android.persiantask.data.db.entity.Attachments;
import ir.android.persiantask.data.db.repository.AttachmentsRepository;

public class AttachmentsViewModel extends AndroidViewModel {
    private AttachmentsRepository attachmentsRepository;
    private LiveData<List<Attachments>> allTaskssAttachments;

    public AttachmentsViewModel(@NonNull Application application, Long tasksID) {
        super(application);
        attachmentsRepository = new AttachmentsRepository(application, tasksID);
        allTaskssAttachments = attachmentsRepository.getAllAttachments();
    }

    public void insert(Attachments attachments) {
        attachmentsRepository.insert(attachments);
    }

    public void update(Attachments attachments){
        attachmentsRepository.update(attachments);
    }

    public void delete(Attachments attachments){
        attachmentsRepository.delete(attachments);
    }

    public LiveData<List<Attachments>> getAllTasksAttachments(){
        return allTaskssAttachments;
    }
}
