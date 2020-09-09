package ir.android.persiantask.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.entity.Subtasks;
import ir.android.persiantask.data.db.entity.Tasks;
import ir.android.persiantask.data.db.repository.SubtasksRepository;
import ir.android.persiantask.data.db.repository.TasksRepository;

public class SubTasksViewModel extends AndroidViewModel {
    private SubtasksRepository subTasksRepository;
    private LiveData<List<Subtasks>> allSubTasks;

    public SubTasksViewModel(@NonNull Application application, Integer taskID) {
        super(application);
        subTasksRepository = new SubtasksRepository(application, taskID);
        allSubTasks = subTasksRepository.getAllSubtasks();
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

    public LiveData<List<Subtasks>> getAllSubtasks(){
        return allSubTasks;
    }
}
