package ir.android.persiantask.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Room Table Builder
 * Label table use for save all Label for Tasks
 * */
@Entity(foreignKeys = @ForeignKey(entity = Colors.class,
        parentColumns = "colors_id",
        childColumns = "color_id"),
        tableName = "Label")
public class Label {
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name="label_id")
  private Integer label_id;
  @ColumnInfo(name="color_id")
  private Integer color_id;
  @ColumnInfo(name="label_title")
  private String label_title;
  public Label(){
  }
  public Label(Integer color_id, String label_title){
   this.color_id=color_id;
   this.label_title=label_title;
  }
  public void setColor_id(Integer color_id){
   this.color_id=color_id;
  }
  public Integer getColor_id(){
   return color_id;
  }
  public void setLabel_title(String label_title){
   this.label_title=label_title;
  }
  public String getLabel_title(){
   return label_title;
  }
  public void setLabel_id(Integer label_id){
   this.label_id=label_id;
  }
  public Integer getLabel_id(){
   return label_id;
  }
}