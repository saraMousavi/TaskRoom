package ir.android.taskroom.utils.calender;

public interface DatePickerController {
    void onYearSelected(int year);

    void onDayOfMonthSelected(int year, int month, int day);

    void registerOnDateChangedListener(DatePickerDialogs.OnDateChangedListener listener);

    void unregisterOnDateChangedListener(DatePickerDialogs.OnDateChangedListener listener);

    MonthAdapter.CalendarDay getSelectedDay();

    boolean isThemeDark();

    PersianCalendar[] getHighlightedDays();

    PersianCalendar[] getSelectableDays();

    int getFirstDayOfWeek();

    int getMinYear();

    int getMaxYear();

    PersianCalendar getMinDate();

    PersianCalendar getMaxDate();

    void tryVibrate();

    void setTypeface(String fontName);

    String getTypeface();
}
