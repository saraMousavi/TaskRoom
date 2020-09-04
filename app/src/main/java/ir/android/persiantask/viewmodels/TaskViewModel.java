package ir.android.persiantask.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.repository.TasksRepository;

public class TaskViewModel extends AndroidViewModel {
    private TasksRepository tasksRepository;
    private LiveData<List<Tasks>> allTasks;

    public TaskViewModel(@NonNull Application application, Integer projectID) {
        super(application);
        tasksRepository = new TasksRepository(application, projectID);
        allTasks = tasksRepository.getAllTasks();
    }

    public void insert(Tasks tasks){
        tasksRepository.insert(tasks);
    }

    public void update(Tasks tasks){
        tasksRepository.update(tasks);
    }

    public void delete(Tasks tasks){
        tasksRepository.delete(tasks);
    }

    public LiveData<List<Tasks>> getAllTasks(){
        return allTasks;
    }
}
