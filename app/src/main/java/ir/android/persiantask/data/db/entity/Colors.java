package ir.android.persiantask.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Table Builder
 * Colors table for save all color use for project and labels
 * */
@Entity(tableName = "Colors")
public class Colors {
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name="colors_id")
  private Integer colors_id;
  @ColumnInfo(name="colors_code")
  private String colors_code;
  public Colors(){
  }
  public Colors(String colors_code){
   this.colors_code=colors_code;
  }
  public void setColors_id(Integer colors_id){
   this.colors_id=colors_id;
  }
  public Integer getColors_id(){
   return colors_id;
  }
  public void setColors_code(String colors_code){
   this.colors_code=colors_code;
  }
  public String getColors_code(){
   return colors_code;
  }
}