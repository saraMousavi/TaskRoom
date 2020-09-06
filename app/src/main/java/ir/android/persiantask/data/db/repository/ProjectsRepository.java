package ir.android.persiantask.data.db.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import ir.android.persiantask.data.db.dao.ProjectsDao;
import ir.android.persiantask.data.db.database.PersianTaskDb;
import ir.android.persiantask.data.db.entity.Projects;

public class ProjectsRepository {
    private ProjectsDao projectsDao;
    private LiveData<List<Projects>> allProjects;
    private LiveData<Projects> projectByID;

    public ProjectsRepository(Application application, Integer projectID) {
        PersianTaskDb persianTaskDb = PersianTaskDb.getInstance(application);
        projectsDao = persianTaskDb.projectsDao();
        allProjects = projectsDao.getAllProjects();
        projectByID = projectsDao.getProjectsByID(projectID);
    }

    public void insert(Projects projects) {
        new ProjectsRepository.InsertProjectsAsyncTask(projectsDao).execute(projects);
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

    private static class InsertProjectsAsyncTask extends AsyncTask<Projects, Void, Void> {

        private ProjectsDao projectsDao;

        private InsertProjectsAsyncTask(ProjectsDao projectsDao) {
            this.projectsDao = projectsDao;
        }

        @Override
        protected Void doInBackground(Projects... projects) {
            projectsDao.insert(projects[0]);
            return null;
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
