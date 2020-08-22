package ir.android.persiantask.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

/**
 * Room Table Builder
 * project table for save all project define with user that each project have multi task
 * */
@Entity(foreignKeys = {@ForeignKey(entity = Colors.class,
        parentColumns = "colors_id",
        childColumns = "colors_id"),
         @ForeignKey(entity = Category.class,
                 parentColumns = "category_id",
                 childColumns = "category_id")},
        tableName = "Projects")
public class Projects {
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name="project_id")
  private Integer project_id;
  @ColumnInfo(name="projects_crdate")
  private Integer projects_crdate;
  @ColumnInfo(name="projects_iscompleted")
  private Integer projects_iscompleted;
  @ColumnInfo(name="category_id")
  private Integer category_id;
  @ColumnInfo(name="projects_update")
  private Integer projects_update;
  @ColumnInfo(name="projects_upuser")
  private Integer projects_upuser;
  @ColumnInfo(name="colors_id")
  private Integer colors_id;
  @ColumnInfo(name="projects_cruer")
  private Integer projects_cruer;
  @ColumnInfo(name="projects_title")
  private String projects_title;
  public Projects(){
  }
  public Projects(Integer projects_crdate, Integer projects_iscompleted, Integer category_id, Integer projects_update, Integer projects_upuser, Integer colors_id, Integer projects_cruer, String projects_title){
   this.projects_crdate=projects_crdate;
   this.projects_iscompleted=projects_iscompleted;
   this.category_id=category_id;
   this.projects_update=projects_update;
   this.projects_upuser=projects_upuser;
   this.colors_id=colors_id;
   this.projects_cruer=projects_cruer;
   this.projects_title=projects_title;
  }
  public void setProjects_crdate(Integer projects_crdate){
   this.projects_crdate=projects_crdate;
  }
  public Integer getProjects_crdate(){
   return projects_crdate;
  }
  public void setProjects_iscompleted(Integer projects_iscompleted){
   this.projects_iscompleted=projects_iscompleted;
  }
  public Integer getProjects_iscompleted(){
   return projects_iscompleted;
  }
  public void setCategory_id(Integer category_id){
   this.category_id=category_id;
  }
  public Integer getCategory_id(){
   return category_id;
  }
  public void setProject_id(Integer project_id){
   this.project_id=project_id;
  }
  public Integer getProject_id(){
   return project_id;
  }
  public void setProjects_update(Integer projects_update){
   this.projects_update=projects_update;
  }
  public Object getProjects_update(){
   return projects_update;
  }
  public void setProjects_upuser(Integer projects_upuser){
   this.projects_upuser=projects_upuser;
  }
  public Integer getProjects_upuser(){
   return projects_upuser;
  }
  public void setColors_id(Integer colors_id){
   this.colors_id=colors_id;
  }
  public Integer getColors_id(){
   return colors_id;
  }
  public void setProjects_cruer(Integer projects_cruer){
   this.projects_cruer=projects_cruer;
  }
  public Integer getProjects_cruer(){
   return projects_cruer;
  }
  public void setProjects_title(String projects_title){
   this.projects_title=projects_title;
  }
  public String getProjects_title(){
   return projects_title;
  }
}