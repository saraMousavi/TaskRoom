package ir.android.taskroom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ir.android.taskroom.data.db.entity.Tasks;
import ir.android.taskroom.data.db.repository.TasksRepository;

public class TaskViewModel extends AndroidViewModel {
    private TasksRepository tasksRepository;
    private LiveData<List<Tasks>> allProjectsTasks, allTasks;

    public TaskViewModel(@NonNull Application application, Long projectID) {
        super(application);
        tasksRepository = new TasksRepository(application, projectID);
        allProjectsTasks = tasksRepository.getAllProjectsTasks();
        allTasks = tasksRepository.getAllTasks();
    }

    public Long insert(Tasks tasks) throws ExecutionException, InterruptedException {
        return tasksRepository.insert(tasks);
    }

    public void update(Tasks tasks){
        tasksRepository.update(tasks);
    }

    public void delete(Tasks tasks){
        tasksRepository.delete(tasks);
    }

    public LiveData<List<Tasks>> getAllProjectsTasks(){
        return allProjectsTasks;
    }

    public LiveData<List<Tasks>> getAllTasks() {
        return allTasks;
    }
}
