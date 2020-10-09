package ir.android.taskroom.utils.enums;

public enum  CategoryType {
    ART(1),

    SPORT(2),

    SCIENTIFIC(3);

    private int value;

    CategoryType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
