package ir.android.persiantask.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Room Table Builder
 * Reminders table for save all reminder that don't need define in project
 * and it save separate from Projects tab
 * <p>
 * if (reminders_type == 0) Push else if (reminders_type == 1) Alarm else none
 * <p>
 * switch(reminders_time)
 * case 0:dont reminde me
 * case 1:remind me in end date
 * case 2:remind me in advance(repeatedtype)
 * switch(reminders_repeatedtype)
 * case 1:day;
 * case 2:week;
 * case 3:month;
 * case 4:custom
 * <p>
 * 'reminders_repeatedday' save all day that user choose in custom type and put them near each other with kama
 */

@Entity(tableName = "Reminders")
public class Reminders implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reminders_id")
    private Integer reminders_id;
    @ColumnInfo(name = "reminders_update")
    private Integer reminders_update;
    @ColumnInfo(name = "reminders_type")
    private Integer reminders_type;
    @ColumnInfo(name = "reminders_comment")
    private String reminders_comment;
    @ColumnInfo(name = "reminders_priority")
    private Integer reminders_priority;
    @ColumnInfo(name = "reminders_upuser")
    private Integer reminders_upuser;
    @ColumnInfo(name = "reminders_cruser")
    private Integer reminders_cruser;
    @ColumnInfo(name = "reminders_title")
    private String reminders_title;
    @ColumnInfo(name = "reminders_time")
    private Integer reminders_time;
    @ColumnInfo(name = "reminders_repeatedday")
    private String reminders_repeatedday;
    @ColumnInfo(name = "reminders_crdate")
    private Integer reminders_crdate;
    @ColumnInfo(name = "reminders_repeatedtype")
    private Integer reminders_repeatedtype;
    @ColumnInfo(name = "reminders_active")
    private Integer reminders_active;
    @ColumnInfo(name = "label_id")
    private Integer label_id;


    public Reminders(Integer reminders_type, String reminders_comment, Integer reminders_priority,
                     String reminders_title, Integer reminders_time, String reminders_repeatedday,
                     Integer reminders_repeatedtype, Integer reminders_active, Integer label_id) {
        this.reminders_type = reminders_type;
        this.reminders_comment = reminders_comment;
        this.reminders_priority = reminders_priority;
        this.reminders_title = reminders_title;
        this.reminders_time = reminders_time;
        this.reminders_repeatedday = reminders_repeatedday;
        this.reminders_repeatedtype = reminders_repeatedtype;
        this.reminders_active = reminders_active;
        this.label_id = label_id;
    }

    public void setReminders_update(Integer reminders_update) {
        this.reminders_update = reminders_update;
    }

    public Integer getReminders_update() {
        return reminders_update;
    }

    public void setReminders_type(Integer reminders_type) {
        this.reminders_type = reminders_type;
    }

    public Integer getReminders_type() {
        return reminders_type;
    }

    public void setReminders_comment(String reminders_comment) {
        this.reminders_comment = reminders_comment;
    }

    public String getReminders_comment() {
        return reminders_comment;
    }

    public void setReminders_id(Integer reminders_id) {
        this.reminders_id = reminders_id;
    }

    public Integer getReminders_id() {
        return reminders_id;
    }

    public void setReminders_priority(Integer reminders_priority) {
        this.reminders_priority = reminders_priority;
    }

    public Integer getReminders_priority() {
        return reminders_priority;
    }

    public void setReminders_upuser(Integer reminders_upuser) {
        this.reminders_upuser = reminders_upuser;
    }

    public Integer getReminders_upuser() {
        return reminders_upuser;
    }

    public void setReminders_cruser(Integer reminders_cruser) {
        this.reminders_cruser = reminders_cruser;
    }

    public Integer getReminders_cruser() {
        return reminders_cruser;
    }

    public void setReminders_title(String reminders_title) {
        this.reminders_title = reminders_title;
    }

    public String getReminders_title() {
        return reminders_title;
    }

    public void setReminders_time(Integer reminders_time) {
        this.reminders_time = reminders_time;
    }

    public Integer getReminders_time() {
        return reminders_time;
    }

    public void setReminders_repeatedday(String reminders_repeatedday) {
        this.reminders_repeatedday = reminders_repeatedday;
    }

    public String getReminders_repeatedday() {
        return reminders_repeatedday;
    }

    public void setReminders_crdate(Integer reminders_crdate) {
        this.reminders_crdate = reminders_crdate;
    }

    public Integer getReminders_crdate() {
        return reminders_crdate;
    }

    public void setReminders_repeatedtype(Integer reminders_repeatedtype) {
        this.reminders_repeatedtype = reminders_repeatedtype;
    }

    public Integer getReminders_repeatedtype() {
        return reminders_repeatedtype;
    }

    public Integer getReminders_active() {
        return reminders_active;
    }

    public void setReminders_active(Integer reminders_active) {
        this.reminders_active = reminders_active;
    }

    public void setLabel_id(Integer label_id) {
        this.label_id = label_id;
    }

    public Integer getLabel_id() {
        return label_id;
    }
}