package ir.android.taskroom.data.db.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Room Table Builder
 * CategoryDao table for save all CategoryDao of Projects
 */
@Entity(tableName = "Category")
public class Category implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    private Integer category_id;
    @ColumnInfo(name = "category_title")
    private String category_title;
    @ColumnInfo(name = "category_white_image")
    private String category_white_image;
    @ColumnInfo(name = "category_black_image")
    private String category_black_image;


    public Category(String category_title, String category_white_image, String category_black_image) {
        this.category_title = category_title;
        this.category_white_image = category_white_image;
        this.category_black_image = category_black_image;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public String getCategory_white_image() {
        return category_white_image;
    }

    public void setCategory_white_image(String category_white_image) {
        this.category_white_image = category_white_image;
    }

    public String getCategory_black_image() {
        return category_black_image;
    }

    public void setCategory_black_image(String category_black_image) {
        this.category_black_image = category_black_image;
    }

    @Override
    public String toString() {
        return category_title;
    }
}