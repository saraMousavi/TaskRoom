package ir.android.taskroom.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room Table Builder
 * Subtasks table save all Subtasks for each Tasks
 * <p>
 * if(subtasks_iscompleted == 1) check complete checkbox else if(subtasks_iscompleted == 0) uncheck complete checkbox
 */
@Entity(indices = {@Index("tasks_id"), @Index("projects_id")},
        foreignKeys = {@ForeignKey(entity = Tasks.class,
                parentColumns = "tasks_id",
                childColumns = "tasks_id"),@ForeignKey(entity = Projects.class,
                parentColumns = "project_id",
                childColumns = "projects_id")}, tableName = "Subtasks")
public class Subtasks {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subtasks_id")
    private Integer subtasks_id;
    @ColumnInfo(name = "subtasks_iscompleted")
    private Integer subtasks_iscompleted;
    @ColumnInfo(name = "subtasks_title")
    private String subtasks_title;
    @ColumnInfo(name = "tasks_id")
    private Long tasks_id;
    @ColumnInfo(name = "projects_id")
    private Long projects_id;

    public Subtasks(String subtasks_title, Integer subtasks_iscompleted, Long tasks_id, Long projects_id) {
        this.subtasks_iscompleted = subtasks_iscompleted;
        this.subtasks_title = subtasks_title;
        this.tasks_id = tasks_id;
        this.projects_id = projects_id;
    }

    public void setSubtasks_iscompleted(String subtasks_title, Integer subtasks_iscompleted) {
        this.subtasks_iscompleted = subtasks_iscompleted;
        this.subtasks_title = subtasks_title;
    }

    public Integer getSubtasks_iscompleted() {
        return subtasks_iscompleted;
    }

    public void setSubtasks_iscompleted(Integer subtasks_iscompleted) {
        this.subtasks_iscompleted = subtasks_iscompleted;
    }

    public void setSubtasks_title(String subtasks_title) {
        this.subtasks_title = subtasks_title;
    }

    public String getSubtasks_title() {
        return subtasks_title;
    }

    public void setSubtasks_id(Integer subtasks_id) {
        this.subtasks_id = subtasks_id;
    }

    public Integer getSubtasks_id() {
        return subtasks_id;
    }

    public void setTasks_id(Long tasks_id) {
        this.tasks_id = tasks_id;
    }

    public Long getTasks_id() {
        return tasks_id;
    }

    public Long getProjects_id() {
        return projects_id;
    }

    public void setProjects_id(Long projects_id) {
        this.projects_id = projects_id;
    }
}