package ir.android.taskroom.utils.enums;

public enum PersianDayOfWeeks {
    SATURDAY(1),

    SUNDAY(2),

    MONDAY(3),

    TUESDAY(4),

    WEDNESDAY(5),

    THURSDAY(6),

    FRIDAY(7);

    private int value;

    PersianDayOfWeeks(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
