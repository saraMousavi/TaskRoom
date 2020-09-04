package ir.android.persiantask.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.android.persiantask.data.db.entity.Projects;

@Dao
public interface ProjectsDao {

    @Insert
    long insert(Projects projects);

    @Update
    void update(Projects projects);

    @Delete
    void delete(Projects projects);

    @Query("SELECT * FROM Projects ORDER BY project_id")
    LiveData<List<Projects>> getAllProjects();

    @Query("SELECT * FROM Projects WHERE projects_cruer=:projects_cruer")
    LiveData<List<Projects>> getProjects(Integer projects_cruer);

}
