package ir.android.persiantask.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Room Table Builder
 * Attachment table for save attachment path and type for each task
 *
 * */
@Entity(foreignKeys = @ForeignKey(entity = Tasks.class,
        parentColumns = "tasks_id",
        childColumns = "tasks_id"),
        tableName = "Attachments")
public class Attachments {
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name="attachments_id")
  private Integer attachments_id;
  @ColumnInfo(name="attachments_type")
  private Integer attachments_type;
  @ColumnInfo(name="attachments_path")
  private Integer attachments_path;
  @ColumnInfo(name="tasks_id")
  private Integer tasks_id;
  public Attachments(){
  }
  public Attachments(Integer attachments_type, Integer attachments_path, Integer tasks_id){
   this.attachments_type=attachments_type;
   this.attachments_path=attachments_path;
   this.tasks_id=tasks_id;
  }
  public void setAttachments_type(Integer attachments_type){
   this.attachments_type=attachments_type;
  }
  public Integer getAttachments_type(){
   return attachments_type;
  }
  public void setAttachments_path(Integer attachments_path){
   this.attachments_path=attachments_path;
  }
  public Integer getAttachments_path(){
   return attachments_path;
  }
  public void setTasks_id(Integer tasks_id){
   this.tasks_id=tasks_id;
  }
  public Integer getTasks_id(){
   return tasks_id;
  }
  public void setAttachments_id(Integer attachments_id){
   this.attachments_id=attachments_id;
  }
  public Integer getAttachments_id(){
   return attachments_id;
  }
}