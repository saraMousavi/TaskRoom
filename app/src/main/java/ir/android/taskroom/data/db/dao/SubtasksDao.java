package ir.android.taskroom.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.android.taskroom.data.db.entity.Subtasks;

@Dao
public interface SubtasksDao {
    @Insert
    long insert(Subtasks subtasks);

    @Update
    void update(Subtasks subtasks);

    @Delete
    void delete(Subtasks subtasks);

    @Query("SELECT * FROM Subtasks ORDER BY subtasks_id")
    LiveData<List<Subtasks>> getAllSubtasks();

    @Query("SELECT * FROM Subtasks where projects_id=:projects_id ORDER BY subtasks_id")
    LiveData<List<Subtasks>> getAllSubtasksProjects(Long projects_id);

    @Query("SELECT * FROM Subtasks WHERE tasks_id=:tasks_id ORDER BY subtasks_id")
    LiveData<List<Subtasks>> getAllSubtasks(Long tasks_id);
}
