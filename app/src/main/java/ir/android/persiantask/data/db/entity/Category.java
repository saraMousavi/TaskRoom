package ir.android.persiantask.data.db.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Table Builder
 * CategoryDao table for save all CategoryDao of Projects
 * */
@Entity(tableName = "Category")
public class Category {
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name="category_id")
  private Integer category_id;
  @ColumnInfo(name="category_title")
  private String category_title;

  public Category(){
  }
  public Category(String category_title){
   this.category_title=category_title;
  }
  public void setCategory_title(String category_title){
   this.category_title=category_title;
  }
  public String getCategory_title(){
   return category_title;
  }
  public void setCategory_id(Integer category_id){
   this.category_id=category_id;
  }
  public Integer getCategory_id(){
   return category_id;
  }
}