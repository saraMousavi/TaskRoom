package ir.android.persiantask.utils.enums;

import android.content.res.Resources;

import ir.android.persiantask.R;

public enum ReminderType {
    PUSH(Resources.getSystem().getString(R.string.notification)),
    ALARM(Resources.getSystem().getString(R.string.alarm));

    private String value;

    ReminderType(String value) {
        this.value = value;
    }


    public String getValue() {
        return this.value;
    }
}
