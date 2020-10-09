package ir.android.taskroom.data.db.factory;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.android.taskroom.viewmodels.TaskViewModel;

public class TasksViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private Integer mProjectsID;


    public TasksViewModelFactory(Application application, Integer projectsID) {
        mApplication = application;
        mProjectsID = projectsID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new TaskViewModel(mApplication, mProjectsID);
    }
}
