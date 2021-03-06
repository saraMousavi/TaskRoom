package ir.android.taskroom.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ir.android.taskroom.data.db.entity.Projects;
import ir.android.taskroom.data.db.repository.ProjectsRepository;

public class ProjectViewModel extends AndroidViewModel {
    private ProjectsRepository projectsRepository;
    private LiveData<List<Projects>> allProjects;
    private LiveData<Projects> projectByID;

    public ProjectViewModel(@NonNull Application application, Long projectID) {
        super(application);
        projectsRepository = new ProjectsRepository(application, projectID);
        allProjects = projectsRepository.getAllProjects();
        projectByID = projectsRepository.getProjectsByID();
    }

    public Long insert(Projects projects) throws ExecutionException, InterruptedException {
        return projectsRepository.insert(projects);
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

    public LiveData<Projects> getProjectsByID(){
        return projectByID;
    }
}
