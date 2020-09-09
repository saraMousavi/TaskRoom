package ir.android.persiantask.utils.enums;

import android.content.res.Resources;

import ir.android.persiantask.R;

public enum PriorityType {
    NONEPRIORITY(Resources.getSystem().getString(R.string.nonePriority)),
    LOW(Resources.getSystem().getString(R.string.low)),
    MEDIUM(Resources.getSystem().getString(R.string.medium)),
    HIGH(Resources.getSystem().getString(R.string.high));

    private String value;
    PriorityType(String value) {
        this.value = value;
    }

    public String getValue(){return value;}
}
