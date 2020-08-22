package ir.android.persiantask.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.android.persiantask.data.db.entity.Colors;

@Dao
public interface ColorsDao {
    @Insert
    long insert(Colors colors);

    @Update
    void update(Colors colors);

    @Delete
    void delete(Colors colors);

    @Query("SELECT * FROM Colors ORDER BY colors_id DESC")
    LiveData<List<Colors>> getAllColors();

    @Query("SELECT * FROM Colors WHERE colors_id=:colors_id")
    LiveData<List<Colors>> getColor(Integer colors_id);
}
