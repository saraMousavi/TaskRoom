package ir.android.taskroom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.taskroom.data.db.entity.Subtasks;
import ir.android.taskroom.data.db.repository.SubtasksRepository;

public class SubTasksViewModel extends AndroidViewModel {
    private SubtasksRepository subTasksRepository;
    private LiveData<List<Subtasks>> allTasksSubTasks;
    private LiveData<List<Subtasks>> allTasksSubTasksProjects;
    private LiveData<List<Subtasks>> allSubTasks;

    public SubTasksViewModel(@NonNull Application application, Long taskID) {
        super(application);
        subTasksRepository = new SubtasksRepository(application, taskID);
        allTasksSubTasks = subTasksRepository.getAllTasksSubtasks();
        allSubTasks = subTasksRepository.getAllSubtasks();
        allTasksSubTasksProjects = subTasksRepository.getAllSubtasksProjects();
    }

    public void insert(Subtasks subTasks){
        subTasksRepository.insert(subTasks);
    }

    public void update(Subtasks subTasks){
        subTasksRepository.update(subTasks);
    }

    public void delete(Subtasks subTasks){
        subTasksRepository.delete(subTasks);
    }

    public LiveData<List<Subtasks>> getAllTasksSubtasks(){
        return allTasksSubTasks;
    }

    public LiveData<List<Subtasks>> getAllTasksSubTasksProjects(){
        return allTasksSubTasksProjects;
    }

    public LiveData<List<Subtasks>> getAllSubtasks(){
        return allSubTasks;
    }
}
