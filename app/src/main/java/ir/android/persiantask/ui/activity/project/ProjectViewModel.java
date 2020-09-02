package ir.android.persiantask.ui.activity.project;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.entity.Projects;
import ir.android.persiantask.data.db.repository.ProjectsRepository;

public class ProjectViewModel extends AndroidViewModel {
    private ProjectsRepository projectsRepository;
    private LiveData<List<Projects>> allProjects;

    public ProjectViewModel(@NonNull Application application) {
        super(application);
        projectsRepository = new ProjectsRepository(application);
        allProjects = projectsRepository.getAllProjects();
    }

    public void insert(Projects projects){
        projectsRepository.insert(projects);
    }

    public void update(Projects projects){
        projectsRepository.update(projects);
    }

    public void delete(Projects projects){
        projectsRepository.delete(projects);
    }

    public LiveData<List<Projects>> getAllProjects(){
        return allProjects;
    }
}