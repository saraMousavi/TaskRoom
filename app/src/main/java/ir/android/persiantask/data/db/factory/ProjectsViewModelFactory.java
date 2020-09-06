package ir.android.persiantask.data.db.factory;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.android.persiantask.viewmodels.ProjectViewModel;
import ir.android.persiantask.viewmodels.TaskViewModel;

public class ProjectsViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private Integer mProjectsID;


    public ProjectsViewModelFactory(Application application, Integer projectsID) {
        mApplication = application;
        mProjectsID = projectsID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ProjectViewModel(mApplication, mProjectsID);
    }
}
