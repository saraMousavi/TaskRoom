package ir.android.taskroom.data.db.factory;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.android.taskroom.viewmodels.ProjectViewModel;

public class ProjectsViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private Long mProjectsID;


    public ProjectsViewModelFactory(Application application, Long projectsID) {
        mApplication = application;
        mProjectsID = projectsID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ProjectViewModel(mApplication, mProjectsID);
    }
}
