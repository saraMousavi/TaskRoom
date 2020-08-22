package ir.android.persiantask.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.android.persiantask.data.db.entity.Label;

@Dao
public interface LabelDao {

    @Insert
    long insert(Label label);

    @Update
    void update(Label label);

    @Delete
    void delete(Label label);

    @Query("SELECT * FROM Label ORDER BY label_id")
    LiveData<List<Label>> getAllLabel();

    @Query("SELECT * FROM Label WHERE label_id=:label_id")
    LiveData<List<Label>> getLabel(Integer label_id);
}
