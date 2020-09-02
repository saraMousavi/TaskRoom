package ir.android.persiantask.data.db.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SubTasksViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private Integer mTasksID;


    public SubTasksViewModelFactory(Application application, Integer tasksID) {
        mApplication = application;
        mTasksID = tasksID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return null;// (T) new TasksViewModelFactory(mApplication, mTasksID);
    }
}
