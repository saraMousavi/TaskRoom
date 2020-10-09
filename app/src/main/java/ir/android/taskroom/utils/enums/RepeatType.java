package ir.android.taskroom.utils.enums;

import android.content.res.Resources;

import ir.android.taskroom.R;

public enum  RepeatType {
    DAY(Resources.getSystem().getString(R.string.day)),
    WEEK(Resources.getSystem().getString(R.string.month)),
    MONTH(Resources.getSystem().getString(R.string.month)),
    YEAR(Resources.getSystem().getString(R.string.year));

    private String value;

    RepeatType(String value) {
        this.value = value;
    }


    public String getValue() {
        return this.value;
    }
}
