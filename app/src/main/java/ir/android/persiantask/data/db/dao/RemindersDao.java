package ir.android.persiantask.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.android.persiantask.data.db.entity.Reminders;

@Dao
public interface RemindersDao {

    @Insert
    long insert(Reminders reminders);

    @Update
    void update(Reminders reminders);

    @Delete
    void delete(Reminders reminders);

    @Query("SELECT * FROM Reminders ORDER BY reminders_id")
    LiveData<List<Reminders>> getAllReminders();

}
