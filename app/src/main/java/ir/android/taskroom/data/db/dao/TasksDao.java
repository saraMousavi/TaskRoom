package ir.android.taskroom.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.android.taskroom.data.db.entity.Tasks;

@Dao
public interface TasksDao {

    @Insert
    long insert(Tasks tasks);

    @Update
    void update(Tasks tasks);

    @Delete
    void delete(Tasks tasks);

    @Query("SELECT * FROM Tasks ORDER BY tasks_id")
    LiveData<List<Tasks>> getAllTasks();

    @Query("SELECT * FROM Tasks WHERE projects_id=:projects_id ORDER BY tasks_id")
    LiveData<List<Tasks>> getAllTasks(Long projects_id);
}
