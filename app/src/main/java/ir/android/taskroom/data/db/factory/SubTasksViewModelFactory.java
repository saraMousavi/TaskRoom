package ir.android.taskroom.data.db.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.android.taskroom.viewmodels.SubTasksViewModel;

public class SubTasksViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private Long mTasksID;


    public SubTasksViewModelFactory(Application application, Long tasksID) {
        mApplication = application;
        mTasksID = tasksID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return  (T) new SubTasksViewModel(mApplication, mTasksID);
    }
}
