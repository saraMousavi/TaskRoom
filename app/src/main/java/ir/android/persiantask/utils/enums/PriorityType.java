package ir.android.persiantask.utils.enums;

import android.content.res.Resources;

import ir.android.persiantask.R;

public enum PriorityType {
    HIGH(Resources.getSystem().getString(R.string.high)),
    MEDIUM(Resources.getSystem().getString(R.string.medium)),
    LOW(Resources.getSystem().getString(R.string.low)),
    NONEPRIORITY(Resources.getSystem().getString(R.string.nonePriority));

    private String value;
    PriorityType(String value) {
        this.value = value;
    }

    public String getValue(){return value;}
}
