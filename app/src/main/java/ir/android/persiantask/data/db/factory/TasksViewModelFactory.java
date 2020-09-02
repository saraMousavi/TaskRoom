package ir.android.persiantask.data.db.factory;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TasksViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private Integer mProjectsID;


    public TasksViewModelFactory(Application application, Integer projectsID) {
        mApplication = application;
        mProjectsID = projectsID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return null;//(T) new TasksViewModelFactory(mApplication, mProjectsID);
    }
}
