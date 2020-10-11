package ir.android.taskroom.data.db.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ir.android.taskroom.viewmodels.SubTasksViewModel;

public class SubTasksViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private Long mID;//it can be both project_id or task_id


    public SubTasksViewModelFactory(Application application, Long id) {
        mApplication = application;
        mID = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return  (T) new SubTasksViewModel(mApplication, mID);
    }
}
