package ir.android.taskroom.data.db.factory;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.android.taskroom.viewmodels.AttachmentsViewModel;

public class AttachmentsViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private Long foreignID;


    public AttachmentsViewModelFactory(Application application, Long tasksID) {
        mApplication = application;
        foreignID = tasksID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AttachmentsViewModel(mApplication, foreignID);
    }
}
