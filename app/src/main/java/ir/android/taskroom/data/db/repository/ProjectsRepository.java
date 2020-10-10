package ir.android.taskroom.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ir.android.taskroom.data.db.dao.ProjectsDao;
import ir.android.taskroom.data.db.database.TaskRoomDb;
import ir.android.taskroom.data.db.entity.Projects;

public class ProjectsRepository {
    private ProjectsDao projectsDao;
    private LiveData<List<Projects>> allProjects;
    private LiveData<Projects> projectByID;

    public ProjectsRepository(Application application, Long projectID) {
        TaskRoomDb taskRoomDb = TaskRoomDb.getInstance(application);
        projectsDao = taskRoomDb.projectsDao();
        allProjects = projectsDao.getAllProjects();
        projectByID = projectsDao.getProjectsByID(projectID);
    }

    public long insert(Projects projects) throws ExecutionException, InterruptedException {
        return new ProjectsRepository.InsertProjectsAsyncTask(projectsDao).execute(projects).get();
    }

    public void update(Projects projects) {
        new ProjectsRepository.UpdateProjectsAsyncTask(projectsDao).execute(projects);
    }

    public void delete(Projects projects) {
        new ProjectsRepository.DeleteProjectAsyncTask(projectsDao).execute(projects);
    }

    public LiveData<List<Projects>> getAllProjects() {
        return allProjects;
    }

    public LiveData<Projects> getProjectsByID() {
        return projectByID;
    }

    private static class InsertProjectsAsyncTask extends AsyncTask<Projects, Void, Long> {

        private ProjectsDao projectsDao;

        private InsertProjectsAsyncTask(ProjectsDao projectsDao) {
            this.projectsDao = projectsDao;
        }

        @Override
        protected Long doInBackground(Projects... projects) {
            return projectsDao.insert(projects[0]);
        }
    }

    private static class UpdateProjectsAsyncTask extends AsyncTask<Projects, Void, Void> {

        private ProjectsDao projectsDao;

        private UpdateProjectsAsyncTask(ProjectsDao projectsDao) {
            this.projectsDao = projectsDao;
        }

        @Override
        protected Void doInBackground(Projects... projects) {
            projectsDao.update(projects[0]);
            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<Projects, Void, Void> {

        private ProjectsDao projectsDao;

        private DeleteProjectAsyncTask(ProjectsDao projectsDao) {
            this.projectsDao = projectsDao;
        }

        @Override
        protected Void doInBackground(Projects... projects) {
            projectsDao.delete(projects[0]);
            return null;
        }
    }
}
