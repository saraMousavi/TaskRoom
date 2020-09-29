package ir.android.persiantask.data.db.factory;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.android.persiantask.viewmodels.AttachmentsViewModel;
import ir.android.persiantask.viewmodels.TaskViewModel;

public class AttachmentsViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private Long mTasksID;


    public AttachmentsViewModelFactory(Application application, Long tasksID) {
        mApplication = application;
        mTasksID = tasksID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AttachmentsViewModel(mApplication, mTasksID);
    }
}
