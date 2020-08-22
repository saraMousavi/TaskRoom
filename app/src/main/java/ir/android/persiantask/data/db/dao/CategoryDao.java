package ir.android.persiantask.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ir.android.persiantask.data.db.entity.Category;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM Category ORDER BY category_id DESC")
    LiveData<List<Category>> getAllCategory();

    @Query("SELECT * FROM Category WHERE category_id=:category_id")
    LiveData<List<Category>> getCategory(Integer category_id);
}
