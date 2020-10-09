package ir.android.taskroom.utils.enums;

public enum ShowCaseSharePref {
    FIRST_PROJECT_GUIDE("firstProjectGuide"),

    MORE_PROJECT_GUIDE("moreProjectGuide"),

    FIRST_REMINDER_GUIDE("firstReminderGuide"),

    FIRST_TASK_GUIDE("firstTaskGuide"),

    EDIT_DELETE_PROJECT_GUIDE("editDeleteProjectGuide"),

    EDIT_DELETE_TASK_GUIDE("editDeleteTaskGuide"),

    EDIT_DELETE_REMINDER_GUIDE("editDeleteReminderGuide"),

    FIRST_CALENDER_GUIDE("firstCalenderGuide");

    private String value;

    ShowCaseSharePref(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
