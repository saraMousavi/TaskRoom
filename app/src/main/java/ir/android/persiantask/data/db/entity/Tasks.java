package ir.android.persiantask.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room Table Builder
 * if(tasks_iscompleted == 1) check complete checkbox else if(tasks_iscompleted == 0) uncheck complete checkbox
 * <p>
 * if (tasks_remindertype == 1) Alarm else if (tasks_remindertype == 2) Notification else none
 * <p>
 * if(tasks_isrepeated ==0 ) only repeat once in reminders_time
 * switch(tasks_repeatedtype)
 * case 1:day;
 * case 2:week;
 * case 3:month;
 * case 4:custom
 * <p>
 * 'tasks_repeateddays' save all day that user choose in custom type and put them near each other with kama
 */
//@ForeignKey(entity = Label.class,
//        parentColumns = "label_id",
//        childColumns = "label_id"),
@Entity(indices = {@Index("projects_id")},
        foreignKeys = {@ForeignKey(entity = Projects.class,
                        parentColumns = "project_id",
                        childColumns = "projects_id")}, tableName = "Tasks")
public class Tasks {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tasks_id")
    private Integer tasks_id;
    @ColumnInfo(name = "tasks_priority")
    private Integer tasks_priority;
    @ColumnInfo(name = "tasks_iscompleted")
    private Integer tasks_iscompleted;
    @ColumnInfo(name = "tasks_repeatedtype")
    private Integer tasks_repeatedtype;
    @ColumnInfo(name = "projects_id")
    private Integer projects_id;
    @ColumnInfo(name = "tasks_title")
    private String tasks_title;
    @ColumnInfo(name = "tasks_isrepeated")
    private Integer tasks_isrepeated;
    @ColumnInfo(name = "tasks_startdate")
    private Integer tasks_startdate;
    @ColumnInfo(name = "tasks_remindertype")
    private Integer tasks_remindertype;
    @ColumnInfo(name = "tasks_remindertime")
    private Integer tasks_remindertime;
    @ColumnInfo(name = "tasks_repeateddays")
    private String tasks_repeateddays;
    @ColumnInfo(name = "tasks_enddate")
    private Integer tasks_enddate;
    @ColumnInfo(name = "label_id")
    private Integer label_id;
    @ColumnInfo(name = "tasks_comment")
    private String tasks_comment;


    public Tasks(Integer tasks_priority, Integer tasks_iscompleted, Integer tasks_repeatedtype, Integer projects_id, String tasks_title, Integer tasks_isrepeated, Integer tasks_startdate, Integer tasks_remindertype, Integer tasks_remindertime, String tasks_repeateddays, Integer tasks_enddate, Integer label_id, String tasks_comment) {
        this.tasks_priority = tasks_priority;
        this.tasks_iscompleted = tasks_iscompleted;
        this.tasks_repeatedtype = tasks_repeatedtype;
        this.projects_id = projects_id;
        this.tasks_title = tasks_title;
        this.tasks_isrepeated = tasks_isrepeated;
        this.tasks_startdate = tasks_startdate;
        this.tasks_remindertype = tasks_remindertype;
        this.tasks_remindertime = tasks_remindertime;
        this.tasks_repeateddays = tasks_repeateddays;
        this.tasks_enddate = tasks_enddate;
        this.label_id = label_id;
        this.tasks_comment = tasks_comment;
    }

    public void setTasks_priority(Integer tasks_priority) {
        this.tasks_priority = tasks_priority;
    }

    public Integer getTasks_priority() {
        return tasks_priority;
    }

    public void setTasks_iscompleted(Integer tasks_iscompleted) {
        this.tasks_iscompleted = tasks_iscompleted;
    }

    public Integer getTasks_iscompleted() {
        return tasks_iscompleted;
    }

    public void setTasks_repeatedtype(Integer tasks_repeatedtype) {
        this.tasks_repeatedtype = tasks_repeatedtype;
    }

    public Integer getTasks_repeatedtype() {
        return tasks_repeatedtype;
    }

    public void setProjects_id(Integer projects_id) {
        this.projects_id = projects_id;
    }

    public Integer getProjects_id() {
        return projects_id;
    }

    public void setTasks_title(String tasks_title) {
        this.tasks_title = tasks_title;
    }

    public String getTasks_title() {
        return tasks_title;
    }

    public void setTasks_isrepeated(Integer tasks_isrepeated) {
        this.tasks_isrepeated = tasks_isrepeated;
    }

    public Integer getTasks_isrepeated() {
        return tasks_isrepeated;
    }

    public void setTasks_startdate(Integer tasks_startdate) {
        this.tasks_startdate = tasks_startdate;
    }

    public Integer getTasks_startdate() {
        return tasks_startdate;
    }

    public void setTasks_remindertype(Integer tasks_remindertype) {
        this.tasks_remindertype = tasks_remindertype;
    }

    public Integer getTasks_remindertype() {
        return tasks_remindertype;
    }

    public void setTasks_id(Integer tasks_id) {
        this.tasks_id = tasks_id;
    }

    public Integer getTasks_id() {
        return tasks_id;
    }

    public void setTasks_remindertime(Integer tasks_remindertime) {
        this.tasks_remindertime = tasks_remindertime;
    }

    public Integer getTasks_remindertime() {
        return tasks_remindertime;
    }

    public void setTasks_repeateddays(String tasks_repeateddays) {
        this.tasks_repeateddays = tasks_repeateddays;
    }

    public String getTasks_repeateddays() {
        return tasks_repeateddays;
    }

    public void setTasks_enddate(Integer tasks_enddate) {
        this.tasks_enddate = tasks_enddate;
    }

    public Integer getTasks_enddate() {
        return tasks_enddate;
    }

    public void setLabel_id(Integer label_id) {
        this.label_id = label_id;
    }

    public Integer getLabel_id() {
        return label_id;
    }

    public void setTasks_comment(String tasks_comment) {
        this.tasks_comment = tasks_comment;
    }

    public String getTasks_comment() {
        return tasks_comment;
    }
}