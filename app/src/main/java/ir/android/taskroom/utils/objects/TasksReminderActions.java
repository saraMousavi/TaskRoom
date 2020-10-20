package ir.android.taskroom.utils.objects;

import org.joda.time.DateTime;

import java.util.ArrayList;

//@todo add dagger injection
public class TasksReminderActions {
    private ArrayList<DateTime> dateTimesThatShouldMarkInCalender;
    private boolean isInRecyclerView;
    String remainTime;
    long remainDuration;
    int intervalNum;

    public TasksReminderActions() {
    }

    public TasksReminderActions(ArrayList<DateTime> dateTimesThatShouldMarkInCalender, boolean isInRecyclerView, String remainTime, long remainDuration) {
        this.dateTimesThatShouldMarkInCalender = dateTimesThatShouldMarkInCalender;
        this.isInRecyclerView = isInRecyclerView;
        this.remainTime = remainTime;
        this.remainDuration = remainDuration;
    }

    public ArrayList<DateTime> getDateTimesThatShouldMarkInCalender() {
        return dateTimesThatShouldMarkInCalender;
    }

    public boolean isInRecyclerView() {
        return isInRecyclerView;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public long getRemainDuration() {
        return remainDuration;
    }

    public void setRemainDuration(long remainDuration) {
        this.remainDuration = remainDuration;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }

    public int getIntervalNum() {
        return intervalNum;
    }

    public void setIntervalNum(int intervalNum) {
        this.intervalNum = intervalNum;
    }
}
