package ir.android.taskroom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ir.android.taskroom.R;
import ir.android.taskroom.data.db.entity.Reminders;
import ir.android.taskroom.data.db.entity.Tasks;
import ir.android.taskroom.ui.workers.AlarmWorker;
import ir.android.taskroom.utils.calender.LanguageUtils;
import ir.android.taskroom.utils.calender.PersianCalendar;
import ir.android.taskroom.utils.enums.CategoryType;
import ir.android.taskroom.utils.enums.EnglishDayOfWeeks;
import ir.android.taskroom.utils.enums.PersianDayOfWeeks;
import ir.android.taskroom.utils.objects.TasksReminderActions;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class EnglishInit {


    /**
     * current date with format slash and dot
     *
     * @return
     */
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int value = calendar.get(Calendar.HOUR_OF_DAY) % 24;
        int hour = Integer.parseInt(LanguageUtils.getLatinNumbers(String.format(Locale.getDefault(), "%d", value)));
        int minute = Integer.parseInt(LanguageUtils.getLatinNumbers(String.format(Locale.getDefault(), "%02d",
                calendar.get(Calendar.MINUTE) == 60 ? 0 : calendar.get(Calendar.MINUTE))));
        int second = calendar.get(Calendar.SECOND);
        return calendar.get(Calendar.YEAR) + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH))
                + " "
                + (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute)
                + ":" + (second < 10 ? "0" + second : second);
    }

    /**
     * current date time with second
     *
     * @return
     */
    public static DateTime getCurrentDateTimeWithSecond() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int value = calendar.get(Calendar.HOUR_OF_DAY) % 24;
        int hour = Integer.parseInt(LanguageUtils.getLatinNumbers(String.format(Locale.getDefault(), "%d", value)));
        int minute = Integer.parseInt(LanguageUtils.getLatinNumbers(String.format(Locale.getDefault(), "%02d",
                calendar.get(Calendar.MINUTE) == 60 ? 0 : calendar.get(Calendar.MINUTE))));
        int second = Integer.parseInt(LanguageUtils.getLatinNumbers(String.format(Locale.getDefault(), "%02d",
                calendar.get(Calendar.SECOND) == 60 ? 0 : calendar.get(Calendar.SECOND))));

        return convertIntegerToDateTime(integerFormatFromStringDate(calendar.get(Calendar.YEAR) + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH))
                + " "
                + (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute)
                + ":" + (second < 10 ? "0" + second : second)));
    }

    /**
     * current date time without hour
     *
     * @return
     */
    public static DateTime getCurrentDateWhitoutTime() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;

        return convertIntegerToDateTime(integerFormatFromStringDate(calendar.get(Calendar.YEAR) + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH))));
    }

    /**
     * current date time with second
     *
     * @return
     */
    public static DateTime getTodayDateTimeWithSelectedTime(String time, Integer nextDay, boolean isDate) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        if (isDate) {
            time = time.split(" ")[1].trim();
        }
        int hour = Integer.parseInt(time.trim().split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        int second = Integer.parseInt(time.split(":")[2]);
        //@TODO check today is last day of moth or not
        int day = nextDay == 0 ? calendar.get(Calendar.DAY_OF_MONTH) : ((calendar.get(Calendar.DAY_OF_MONTH) == 30 && month > 6 && month != 12)
                || (calendar.get(Calendar.DAY_OF_MONTH) == 31 && month < 7)
                || (calendar.get(Calendar.DAY_OF_MONTH) == 29 && month == 12) ? 1 : calendar.get(Calendar.DAY_OF_MONTH) + nextDay);
        if (nextDay != 0 && day == 1) {
            month = month + 1;
        }
        if (month == 13) {
            month = 1;
            year = year + 1;
        }
        return convertIntegerToDateTime((long) integerFormatFromStringDate(year + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (day < 10 ? "0" + day : day)
                + " "
                + (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute)
                + ":" + (second < 10 ? "0" + second : second)));
    }

    /**
     * current time with format dot
     *
     * @return
     */
    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int value = calendar.get(Calendar.HOUR_OF_DAY) % 24;
        int hour = Integer.parseInt(LanguageUtils.getLatinNumbers(String.format(Locale.getDefault(), "%d", value)));
        int minute = Integer.parseInt(LanguageUtils.getLatinNumbers(String.format(Locale.getDefault(), "%02d",
                calendar.get(Calendar.MINUTE))));
        int second = calendar.get(Calendar.SECOND);
        return (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute)
                + ":" + (second < 10 ? "0" + second : second);
    }

    /**
     * convert date time to string with slash and :
     *
     * @param dateTime
     * @return
     */
    public static String stringFormatDate(DateTime dateTime) {
        return dateTime.getYear() + "/" + (dateTime.getMonthOfYear() < 10 ? "0" + dateTime.getMonthOfYear() : dateTime.getMonthOfYear())
                + "/" + (dateTime.getDayOfMonth() < 10 ? "0" + dateTime.getDayOfMonth() : dateTime.getDayOfMonth());
//                + " " + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay())
//                + ":" + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour());
    }

    public static Integer integerFormatTime(DateTime dateTime) {
        return Integer.parseInt(dateTime.getHourOfDay() + ""
                + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour())
                + (dateTime.getSecondOfMinute() < 10 ? "0" + dateTime.getSecondOfMinute() : dateTime.getSecondOfMinute())
        );
    }

    /**
     * convert date time to string with slash and :
     *
     * @param dateTime
     * @return
     */
    public static String stringFormatDateTime(DateTime dateTime) {
        return dateTime.getYear() + "/" + (dateTime.getMonthOfYear() < 10 ? "0" + dateTime.getMonthOfYear() : dateTime.getMonthOfYear())
                + "/" + (dateTime.getDayOfMonth() < 10 ? "0" + dateTime.getDayOfMonth() : dateTime.getDayOfMonth())
                + " " + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay())
                + ":" + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour())
                + ":" + (dateTime.getSecondOfMinute() < 10 ? "0" + dateTime.getSecondOfMinute() : dateTime.getSecondOfMinute());
    }

    /**
     * convert date time to integer without time
     *
     * @param dateTime
     * @return
     */
    public static Long integerFormatDate(DateTime dateTime) {
        return Long.valueOf(dateTime.getYear() + ""
                + (dateTime.getMonthOfYear() < 10 ? "0" + dateTime.getMonthOfYear() : dateTime.getMonthOfYear())
                + "" + (dateTime.getDayOfMonth() < 10 ? "0" + dateTime.getDayOfMonth() : dateTime.getDayOfMonth()));
//                + "" + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay())
//                + "" + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour()) );
    }

    /**
     * convert date time to integer with time
     *
     * @param dateTime
     * @return
     */
    public static Long integerFormatDateTime(DateTime dateTime) {
        return Long.valueOf(dateTime.getYear() + ""
                + (dateTime.getMonthOfYear() < 10 ? "0" + dateTime.getMonthOfYear() : dateTime.getMonthOfYear())
                + "" + (dateTime.getDayOfMonth() < 10 ? "0" + dateTime.getDayOfMonth() : dateTime.getDayOfMonth())
                + "" + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay())
                + "" + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour())
                + "" + (dateTime.getSecondOfMinute() < 10 ? "0" + dateTime.getSecondOfMinute() : dateTime.getSecondOfMinute()));
    }

    /**
     * convert string date with slash and : to integer
     *
     * @param dateTime
     * @return date
     */
    public static Long integerFormatFromStringDate(String dateTime) {
        if (dateTime != null && dateTime.isEmpty()) {
            return 0L;
        }
        return Long.valueOf(dateTime.replaceAll("/", "").replaceAll(":", "").
                replaceAll(" ", ""));
    }

    /**
     * convert string date with slash and : to integer
     *
     * @param dateTime
     * @return time
     */
    public static Integer integerFormatFromStringTime(String dateTime) {
        if (dateTime.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(dateTime.replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(8, 12));
    }

    /**
     * convert integer date to date time type
     *
     * @param integerTime
     * @return
     */
    public static DateTime convertIntegerToDateTime(Long integerTime) {
        if (integerTime == null || integerTime < 1000000) {
            return null;
        }
        if (integerTime > 99999999) {
            long year = integerTime / 10000000000L;
            long month = (integerTime % 10000000000L) / 100000000L;
            long day = (integerTime % 100000000L) / 1000000L;
            long hour = (integerTime % 1000000L) / 10000L;
            long minute = (integerTime % 10000L) / 100L;
            long second = (integerTime % 100);
            return new DateTime((int) year, (int) month, (int) day, (int) hour, (int) minute, (int) second);
        }
        int year = (int) (long) integerTime / 10000;
        int month = ((int) (long) integerTime % 10000) / 100;
        int day = (int) (long) integerTime % 100;
        return new DateTime(year, month, day, 1, 1);
    }

    /**
     * change the image resource of complete icon and toggle paint flag
     *
     * @param title
     * @param isCompletedIcon
     * @param tasks_iscompleted
     */
    public static void toggleCompleteCircle(TextView title, ImageView isCompletedIcon, Integer tasks_iscompleted) {
        if (tasks_iscompleted == 1) {
            isCompletedIcon.setImageResource(R.drawable.ic_radio_button_checked_green);
            title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            isCompletedIcon.setImageResource(R.drawable.ic_orange_circle);
            title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setViewBackgroundDependOnTheme(List<Map<View, Boolean>> views, Context context, Boolean isNightMode) {

        for (Map<View, Boolean> view : views) {
            for (Map.Entry<View, Boolean> entry : view.entrySet()) {
                switch (getFlag(context)) {
                    case 2:
                        //if isPrimary
                        if (entry.getValue()) {
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary2)));
                            }
                        } else {
                            //if isAccent
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccentDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent2)));
                            }
                        }
                        break;
                    case 3:
                        //if isPrimary
                        if (entry.getValue()) {
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary3)));
                            }
                        } else {
                            //if isAccent
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccentDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent3)));
                            }
                        }
                        break;
                    case 4:
                        //if isPrimary
                        if (entry.getValue()) {
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary4)));
                            }
                        } else {
                            //if isAccent
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccentDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent4)));
                            }
                        }
                        break;
                    case 5:
                        //if isPrimary
                        if (entry.getValue()) {
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary5)));
                            }
                        } else {
                            //if isAccent
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccentDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent5)));
                            }
                        }
                        break;
                    case 6:
                        //if isPrimary
                        if (entry.getValue()) {
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary6)));
                            }
                        } else {
                            //if isAccent
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccentDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent6)));
                            }
                        }
                        break;
                    default:
                        //if isPrimary
                        if (entry.getValue()) {
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
                            }
                        } else {
                            //if isAccent
                            if (isNightMode) {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccentDark)));
                            } else {
                                entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                            }
                        }
                        break;
                }
            }
        }
    }

    public static Integer getFlag(Context context) {
        SharedPreferences sharedpreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedpreferences.getInt("theme", 1);
    }

    public static void setBackgroundRightHeaderButton(Context context, TextView textView) {
        textView.setTextColor(context.getResources().getColor(R.color.white));
        switch (getFlag(context)) {
            case 2:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_right_corner_button_theme2));
                break;
            case 3:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_right_corner_button_theme3));
                break;
            case 4:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_right_corner_button_theme4));
                break;
            case 5:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_right_corner_button_theme5));
                break;
            case 6:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_right_corner_button_theme6));
                break;
            default:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_right_corner_button_theme1));
                break;
        }
    }

    public static void setBackgroundLeftHeaderButton(Context context, TextView textView) {
        textView.setTextColor(context.getResources().getColor(R.color.white));
        switch (getFlag(context)) {
            case 2:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_left_corner_button_theme2));
                break;
            case 3:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_left_corner_button_theme3));
                break;
            case 4:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_left_corner_button_theme4));
                break;
            case 5:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_left_corner_button_theme5));
                break;
            case 6:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_left_corner_button_theme6));
                break;
            default:
                textView.setBackground(context.getResources().getDrawable(R.drawable.selected_left_corner_button_theme1));
                break;
        }
    }

    /**
     * create work request and add it two wrk manager
     */
    public static String requestWork(Context context, String alarmTitle, String alarmExpandableText, Integer reminderType, Map<Integer,
            Object> repeatIntervalMap, long duration, boolean isPeriodic, boolean isReminder) {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .build();
        Data data = new Data.Builder().putString("alarmTitle", alarmTitle)
                .putString("alarmExpandableText", alarmExpandableText)
                .putBoolean("isReminder", isReminder)
                .putInt("reminderType", reminderType).build();
        if (isPeriodic) {
            Integer repeatInterval = 1;
            long repeatIntervalLong = 0;
            TimeUnit repeatIntervalTimeUnit = TimeUnit.DAYS;
            ArrayList<Integer> repeatIntervalList = new ArrayList<>();
            for (Map.Entry map : repeatIntervalMap.entrySet()) {
                repeatInterval = (Integer) map.getKey();
                if (map.getValue() instanceof TimeUnit) {
                    repeatIntervalTimeUnit = (TimeUnit) map.getValue();
                } else if (map.getValue() instanceof ArrayList) {
                    repeatIntervalList = (ArrayList<Integer>) map.getValue();
                } else if (map.getValue() instanceof Long) {
                    repeatIntervalLong = (Long) map.getValue();
                }

            }
            if (repeatIntervalList.size() == 0) {
                //@intial delay from selected time
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(AlarmWorker.class,
                                repeatInterval, repeatIntervalTimeUnit)
                                .setConstraints(constraints)
                                .setInputData(data)
                                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                                .build();
                WorkManager
                        .getInstance(context)
                        .enqueue(periodicWorkRequest);
                return periodicWorkRequest.getId().toString();
            } else if (repeatIntervalLong != 0) {
                //monthly and yearly
                PeriodicWorkRequest periodicWorkRequest =
                        new PeriodicWorkRequest.Builder(AlarmWorker.class,
                                repeatIntervalLong, TimeUnit.MILLISECONDS)
                                .setConstraints(constraints)
                                .setInputData(data)
                                .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                                .build();
                WorkManager
                        .getInstance(context)
                        .enqueue(periodicWorkRequest);
                return periodicWorkRequest.getId().toString();
            } else {
                //custom day
                DateTime toDay = DateTime.now();
                StringBuilder periodRequestId = new StringBuilder();
                for (Integer repeatInteger : repeatIntervalList) {

                    DateTime currentTime = DateTime.now();
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[currentTime.getDayOfWeek() - 1];
                    int currentDay = PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue();
                    int diffDay = 0;
                    if (repeatInteger == 1) {
                        diffDay = PersianDayOfWeeks.SATURDAY.getValue() - currentDay;
                        if (diffDay < 0) {
                            diffDay = diffDay + 7;
                        }
                    } else if (repeatInteger == 2) {
                        diffDay = PersianDayOfWeeks.SUNDAY.getValue() - currentDay;
                        if (diffDay < 0) {
                            diffDay = diffDay + 7;
                        }
                    } else if (repeatInteger == 3) {
                        diffDay = PersianDayOfWeeks.MONDAY.getValue() - currentDay;
                        if (diffDay < 0) {
                            diffDay = diffDay + 7;
                        }
                    } else if (repeatInteger == 4) {
                        diffDay = PersianDayOfWeeks.TUESDAY.getValue() - currentDay;
                        if (diffDay < 0) {
                            diffDay = diffDay + 7;
                        }
                    } else if (repeatInteger == 5) {
                        diffDay = PersianDayOfWeeks.WEDNESDAY.getValue() - currentDay;
                        if (diffDay < 0) {
                            diffDay = diffDay + 7;
                        }
                    } else if (repeatInteger == 6) {
                        diffDay = PersianDayOfWeeks.THURSDAY.getValue() - currentDay;
                        if (diffDay < 0) {
                            diffDay = diffDay + 7;
                        }
                    } else if (repeatInteger == 7) {
                        diffDay = PersianDayOfWeeks.FRIDAY.getValue() - currentDay;
                        if (diffDay < 0) {
                            diffDay = diffDay + 7;
                        }
                    }
                    long newDuration = diffDay * 24 * 60 * 60 * 1000L + duration;
                    PeriodicWorkRequest periodicWorkRequest =
                            new PeriodicWorkRequest.Builder(AlarmWorker.class,
                                    7, TimeUnit.DAYS)
                                    .setConstraints(constraints)
                                    .setInputData(data)
                                    .setInitialDelay(newDuration, TimeUnit.MILLISECONDS)
                                    .build();
                    periodRequestId.append(periodicWorkRequest.getId()).append(",");
                    WorkManager
                            .getInstance(context)
                            .enqueue(periodicWorkRequest);
                }
                return periodRequestId.toString();
            }

        } else {
            OneTimeWorkRequest oneTimeWorkRequest =
                    new OneTimeWorkRequest.Builder(AlarmWorker.class)
                            .setConstraints(constraints)
                            .setInputData(data)
                            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                            .build();
            WorkManager
                    .getInstance(context)
                    .enqueue(oneTimeWorkRequest);
            return oneTimeWorkRequest.getId().toString();
        }
    }

    public static Map<Integer, Object> getWorkRequestPeriodicIntervalMillis(Resources mResourse, String repeatType) {
        Map<Integer, Object> intervalRepeat = new HashMap<>();
        if (repeatType.equals(mResourse.getString(R.string.daily))) {
            intervalRepeat.put(1, TimeUnit.DAYS);
        } else if (repeatType.equals(mResourse.getString(R.string.weekly))) {
            intervalRepeat.put(7, TimeUnit.DAYS);
        } else if (repeatType.equals(mResourse.getString(R.string.monthly))) {
            DateTime dateTime1 = EnglishInit.getCurrentDateTimeWithSecond();
            DateTime dateTime2 = new DateTime(EnglishInit.getCurrentDateTimeWithSecond().getYear(),
                    EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear() == 12 ? 1 : EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear() + 1,
                    EnglishInit.getCurrentDateTimeWithSecond().getDayOfMonth(), EnglishInit.getCurrentDateTimeWithSecond().getHourOfDay()
                    , EnglishInit.getCurrentDateTimeWithSecond().getMinuteOfHour(),
                    EnglishInit.getCurrentDateTimeWithSecond().getSecondOfMinute(), EnglishInit.getCurrentDateTimeWithSecond().getMillisOfSecond());
            Interval interval = new Interval(dateTime1, dateTime2);
            intervalRepeat.put(1, interval.toDurationMillis());
        } else if (repeatType.equals(mResourse.getString(R.string.yearly))) {
            DateTime dateTime1 = EnglishInit.getCurrentDateTimeWithSecond();
            DateTime dateTime2 = new DateTime(EnglishInit.getCurrentDateTimeWithSecond().getYear() + 1,
                    EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear(),
                    EnglishInit.getCurrentDateTimeWithSecond().getDayOfMonth(), EnglishInit.getCurrentDateTimeWithSecond().getHourOfDay()
                    , EnglishInit.getCurrentDateTimeWithSecond().getMinuteOfHour(),
                    EnglishInit.getCurrentDateTimeWithSecond().getSecondOfMinute(), EnglishInit.getCurrentDateTimeWithSecond().getMillisOfSecond());
            Interval interval = new Interval(dateTime1, dateTime2);
            intervalRepeat.put(1, interval.toDurationMillis());
        } else if (!repeatType.contains(mResourse.getString(R.string.each))) {
            ArrayList<Integer> repeatTypeInt = new ArrayList<>();
            for (String repeatTypeVal : repeatType.split(",")) {
                if (repeatTypeVal.equals(mResourse.getString(R.string.saterday))) {
                    repeatTypeInt.add(1);
                } else if (repeatTypeVal.equals(mResourse.getString(R.string.sunday))) {
                    repeatTypeInt.add(2);
                } else if (repeatTypeVal.equals(mResourse.getString(R.string.monday))) {
                    repeatTypeInt.add(3);
                } else if (repeatTypeVal.equals(mResourse.getString(R.string.tuesday))) {
                    repeatTypeInt.add(4);
                } else if (repeatTypeVal.equals(mResourse.getString(R.string.wednesday))) {
                    repeatTypeInt.add(5);
                } else if (repeatTypeVal.equals(mResourse.getString(R.string.thursday))) {
                    repeatTypeInt.add(6);
                } else if (repeatTypeVal.equals(mResourse.getString(R.string.friday))) {
                    repeatTypeInt.add(7);
                }

            }
            intervalRepeat.put(repeatType.split(",").length, repeatTypeInt);
        } else if (!repeatType.isEmpty()) {
            String[] repeatTypeSplit = repeatType.split(" ");
            String[] typePeriodVal = new String[]{mResourse.getString(R.string.day), mResourse.getString(R.string.week),
                    mResourse.getString(R.string.month), mResourse.getString(R.string.year)};
            if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
                intervalRepeat.put(Integer.parseInt(repeatTypeSplit[1]), TimeUnit.DAYS);
            }
            if (typePeriodVal[1].equals(repeatTypeSplit[2])) {
                intervalRepeat.put(7 * Integer.parseInt(repeatTypeSplit[1]), TimeUnit.DAYS);
            }
            if (typePeriodVal[2].equals(repeatTypeSplit[2])) {
                intervalRepeat.put(30 * Integer.parseInt(repeatTypeSplit[1]), TimeUnit.DAYS);
            }
            if (typePeriodVal[3].equals(repeatTypeSplit[2])) {
                intervalRepeat.put(365 * Integer.parseInt(repeatTypeSplit[1]), TimeUnit.DAYS);
            }
        }
        return intervalRepeat;
    }


    public static void fadeVisibelityView(View view) {
        Handler handler = new Handler();
        (new Thread() {
            @Override
            public void run() {
                for (int i = 100; i < 255; i++) {
                    int finalI = i;
                    handler.post(new Runnable() {
                        public void run() {
                            if (finalI > 200) {
                                view.setBackgroundColor(Color.argb(255 - finalI, finalI, 0, 0));
                            } else {
                                view.setBackgroundColor(Color.argb(100, finalI, 0, 0));
                            }

                        }
                    });
                    // next will pause the thread for some time
                    try {
                        sleep(8);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public static GuideView.Builder initShowCaseView(Context context, View targetView, String contentText, String sharedPrefContent, GuideListener guideListener) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        GuideView.Builder builder = null;
        if (sharedPreferences.getInt(sharedPrefContent, 0) != 1) {
            builder = new GuideView.Builder(context)
                    .setContentText(contentText)
                    .setDismissType(DismissType.anywhere)
                    .setTargetView(targetView);
            if (guideListener == null) {
                builder.setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(sharedPrefContent);
                        editor.putInt(sharedPrefContent, 1);
                        editor.apply();
                    }
                });
            } else {
                builder.setGuideListener(guideListener);
            }
            builder.build().show();
        }
        return builder;
    }

    public static Long convertDateTimeToInteger(DateTime dateTime) {
        return Long.parseLong(dateTime.getYear() + "" + (dateTime.getMonthOfYear() < 10 ? "0" + dateTime.getMonthOfYear() : dateTime.getMonthOfYear())
                + "" + (dateTime.getDayOfMonth() < 10 ? "0" + dateTime.getDayOfMonth() : dateTime.getDayOfMonth())
                + "" + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay())
                + "" + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour()) +
                "" + (dateTime.getSecondOfMinute() < 10 ? "0" + dateTime.getSecondOfMinute() : dateTime.getSecondOfMinute()));
    }

    public static boolean checkValidDate(DateTime currentDateTime) {
        int year = currentDateTime.getYear();
        int month = currentDateTime.getMonthOfYear();
        int day = currentDateTime.getDayOfMonth();
        if (month == 12 && day > 29) {
            return false;
        }
        if (month > 6 && day > 30) {
            return false;
        }
        return true;
    }

    public static boolean isSpecificDiffDayBetweenTwoDate(DateTime startDate, DateTime endDate, int diffDay) {
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        int startMonth = startDate.getMonthOfYear();
        int startDay = startDate.getDayOfMonth();
        int endMonth = endDate.getMonthOfYear();
        int endDay = endDate.getDayOfMonth();
        if (startYear == endYear) {
            if (startMonth == endMonth) {
                if (endDay >= startDay) {
                    if ((endDay - startDay) % diffDay == 0) {
                        return true;
                    }
                }
            } else {
                if (endMonth > startMonth) {
                    if (startMonth < 7 && endMonth < 7) {
                        if (((31 - startDay) + ((endMonth - startMonth - 1) * 31) + endDay) % diffDay == 0) {
                            return true;
                        }
                    } else if (startMonth < 7 && endMonth != 12) {
                        if (((31 - startDay) + ((6 - startMonth) * 31) + ((endMonth - 7) * 30) + endDay) % diffDay == 0) {
                            return true;
                        }
                    } else if (startMonth > 6 && endMonth != 12) {
                        if (((30 - startDay) + ((endMonth - startMonth - 1) * 30) + endDay) % diffDay == 0) {
                            return true;
                        }
                    }
                }
            }
        } else {
            //@todo
        }
        return false;
    }


    public static int getSpecificDiffDayBetweenTwoDate(DateTime startDate, DateTime endDate, int diffDay) {
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        int startMonth = startDate.getMonthOfYear();
        int startDay = startDate.getDayOfMonth();
        int endMonth = endDate.getMonthOfYear();
        int endDay = endDate.getDayOfMonth();
        int diffDayMode = 0;
        if (EnglishInit.integerFormatDate(startDate) > EnglishInit.integerFormatDate(endDate)) {
            DateTime tempDate = endDate;
            endDate = startDate;
            startDate = tempDate;
        }
        if (startYear == endYear) {
            if (startMonth == endMonth) {
                if (endDay >= startDay) {
                    diffDayMode = (endDay - startDay) % diffDay;
                }
            } else {
                if (endMonth > startMonth) {
                    if (startMonth < 7 && endMonth < 7) {
                        diffDayMode = ((31 - startDay) + ((endMonth - startMonth - 1) * 31) + endDay) % diffDay;
                    } else if (startMonth < 7 && endMonth != 12) {
                        diffDayMode = ((31 - startDay) + ((6 - startMonth) * 31) + ((endMonth - 7) * 30) + endDay) % diffDay;
                    } else if (startMonth > 6 && endMonth != 12) {
                        diffDayMode = ((30 - startDay) + ((endMonth - startMonth - 1) * 30) + endDay) % diffDay;
                    }
                }
            }
        } else {
            //@todo
        }
        return diffDayMode;
    }

    /**
     * only work for afterDayDuration <= 30
     *
     * @param currentDateTime
     * @param afterDayDuration
     * @return
     */
    public static DateTime dateTimeAfter7dayFromCurrent(DateTime currentDateTime, int afterDayDuration) {
        if (afterDayDuration == 0) {
            return currentDateTime;
        }
        int nextYear = currentDateTime.getYear();
        int nextMonth = currentDateTime.getMonthOfYear();
        int nextDay = currentDateTime.getDayOfMonth();
        switch (nextMonth) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (afterDayDuration <= 31 - nextDay) {
                    nextDay += afterDayDuration;
                } else {
                    if ((31 - nextDay) == 0) {
                        if (nextMonth == 12) {
                            nextYear += 1;
                            nextMonth = 1;
                        } else {
                            nextMonth += 1;
                        }
                        nextDay = 1;
                        afterDayDuration -= 1;
                    } else {
                        afterDayDuration -= (31 - nextDay);
                        nextDay = 31;
                    }
                    dateTimeAfter7dayFromCurrent(new DateTime(nextYear, nextMonth, nextDay, currentDateTime.getHourOfDay(), currentDateTime.getMinuteOfHour()), afterDayDuration);
                }
                break;
            case 2:
                if (afterDayDuration <= 28 - nextDay) {
                    nextDay += afterDayDuration;
                } else {
                    if ((28 - nextDay) == 0) {
                        nextMonth += 1;
                        nextDay = 1;
                        afterDayDuration -= 1;
                    } else {
                        afterDayDuration -= (28 - nextDay);
                        nextDay = 28;
                    }
                    dateTimeAfter7dayFromCurrent(new DateTime(nextYear, nextMonth, nextDay, currentDateTime.getHourOfDay(), currentDateTime.getMinuteOfHour()), afterDayDuration);
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (afterDayDuration <= 30 - nextDay) {
                    nextDay += afterDayDuration;
                } else {
                    if ((30 - nextDay) == 0) {
                        nextMonth += 1;
                        nextDay = 1;
                        afterDayDuration -= 1;
                    } else {
                        afterDayDuration -= (30 - nextDay);
                        nextDay = 30;
                    }
                    dateTimeAfter7dayFromCurrent(new DateTime(nextYear, nextMonth, nextDay, currentDateTime.getHourOfDay(), currentDateTime.getMinuteOfHour()), afterDayDuration);
                }
                break;
        }
        return new DateTime(nextYear, nextMonth, nextDay, currentDateTime.getHourOfDay(), currentDateTime.getMinuteOfHour());
    }

    public static TasksReminderActions getDurationInWholeStateOfRemindersOrTasks(Object reminderOrTask, DateTime selectedCalender, Resources resources) {
        ArrayList<DateTime> dateTimesThatShouldMarkInCalender = new ArrayList<>();
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        DateTime endDuration;
        long remainDuration = 0;
        boolean isInRecyclerView = false;
        String remainTime = "";
        if (reminderOrTask instanceof Reminders) {
            Reminders reminders = (Reminders) reminderOrTask;
            if (reminders.getReminders_time().isEmpty()) {
                return new TasksReminderActions();
            }
            if (reminders.getReminders_repeatedday().isEmpty()) {//remind once
                endDuration = EnglishInit.getTodayDateTimeWithSelectedTime(reminders.getReminders_time(), 0, false);
                if (reminders.getReminders_crdate() != null) {
                    endDuration = EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate());
                } else if (selectedCalender != null) {
                    endDuration = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(EnglishInit.stringFormatDate(selectedCalender) + " " + reminders.getReminders_time()));
                }
                if (reminders.getReminders_crdate() == null || reminders.getReminders_active() == 0) {
                    if (EnglishInit.convertDateTimeToInteger(endDuration) < EnglishInit.convertDateTimeToInteger(startDuration)) {
                        TasksReminderActions tasksReminderActions = new TasksReminderActions();
                        tasksReminderActions.setRemainDuration(-1);
                        return tasksReminderActions;//start date past
                    } else {
                        Interval interval = new Interval(startDuration, endDuration);
                        long day = interval.toDuration().getStandardHours() / 24;
                        long hour = interval.toDuration().getStandardMinutes() / 60;
                        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
                        long second = interval.toDuration().getStandardSeconds() - (hour * 60 * 60) - (minute * 60);
                        if (hour >= 24) {
                            hour = hour % 24;
                        }
                        remainTime = (day == 0 ? "" : day + resources.getString(R.string.day) + ",") + "(" + hour + ":" + minute + ":" + second + ")";
                        remainDuration = interval.toDurationMillis();
                    }
                }
                //if create date in future

                if (reminders.getReminders_crdate() != null && selectedCalender != null) {
                    if ((reminders.getReminders_crdate() / 1000000) == EnglishInit.integerFormatDate(selectedCalender)) {
                        isInRecyclerView = true;
                    }
                    endDuration = EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate());
                    dateTimesThatShouldMarkInCalender.add(endDuration);
                }

                return new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, remainTime, remainDuration);
            } else {//remind periodic
                return EnglishInit.remindTasksRemindersActionInAdvance(reminders, selectedCalender, resources);
            }
        }
        if (reminderOrTask instanceof Tasks) {
            Tasks tasks = (Tasks) reminderOrTask;
            switch (tasks.getTasks_remindertime()) {
                case 0://dont remind
                    if (tasks.getTasks_startdate().isEmpty()) {
                        return new TasksReminderActions();
                    }
                    Long startdate = EnglishInit.convertDateTimeToInteger(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate())));
                    if (tasks.getTasks_enddate().isEmpty()) {
                        if (selectedCalender != null) {
                            if (startdate / 1000000 == EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    } else {
                        Long endDate = EnglishInit.convertDateTimeToInteger(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_enddate())));

                        if (endDate < startdate) {
                            remainDuration = -1;
                        }
                        if (selectedCalender != null) {
                            if (startdate / 1000000 == EnglishInit.integerFormatDate(selectedCalender) || endDate / 1000000 == EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                        dateTimesThatShouldMarkInCalender.add(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_enddate())));
                    }
                    dateTimesThatShouldMarkInCalender.add(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate())));
                    return new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, "", remainDuration);
                case 1://remind in start day
                    endDuration = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()));
                    if (selectedCalender == null && EnglishInit.convertDateTimeToInteger(endDuration) < EnglishInit.convertDateTimeToInteger(startDuration)) {
                        TasksReminderActions tasksReminderActions = new TasksReminderActions();
                        tasksReminderActions.setRemainDuration(-2);
                        return tasksReminderActions;//start date past
                    }
                    if (selectedCalender == null) {
                        Interval interval = new Interval(startDuration, endDuration);
                        long day = interval.toDuration().getStandardHours() / 24;
                        long hour = interval.toDuration().getStandardMinutes() / 60;
                        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
                        long second = interval.toDuration().getStandardSeconds() - (hour * 60 * 60) - (minute * 60);
                        if (hour >= 24) {
                            hour = hour % 24;
                        }
                        remainTime = (day == 0 ? "" : day + resources.getString(R.string.day) + ",") + "(" + hour + ":" + minute + ":" + second + ")";
                        remainDuration = interval.toDurationMillis();
                    } else {
                        if ((EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()) / 1000000) == EnglishInit.integerFormatDate(selectedCalender)) {
                            isInRecyclerView = true;
                        }
                        dateTimesThatShouldMarkInCalender.add(endDuration);
                        if (!tasks.getTasks_enddate().isEmpty()) {
                            endDuration = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_enddate()));
                            if ((EnglishInit.integerFormatFromStringDate(tasks.getTasks_enddate()) / 1000000) == EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                            dateTimesThatShouldMarkInCalender.add(endDuration);
                        }

                    }
                    return new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, remainTime, remainDuration);
                case 2:
                    if (tasks.getTasks_repeateddays().isEmpty()) {//remind in end date

                        endDuration = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_enddate()));
                        if (selectedCalender == null && EnglishInit.convertDateTimeToInteger(endDuration) < EnglishInit.convertDateTimeToInteger(startDuration)) {
                            TasksReminderActions tasksReminderActions = new TasksReminderActions();
                            tasksReminderActions.setRemainDuration(-2);
                            return tasksReminderActions;//start date past
                        }
                        if (selectedCalender == null) {
                            Interval interval1 = new Interval(startDuration, endDuration);
                            long day1 = interval1.toDuration().getStandardHours() / 24;
                            long hour1 = interval1.toDuration().getStandardMinutes() / 60;
                            long minute1 = interval1.toDuration().getStandardMinutes() - hour1 * 60;
                            long second1 = interval1.toDuration().getStandardSeconds() - (hour1 * 60 * 60) - (minute1 * 60);
                            if (hour1 >= 24) {
                                hour1 = hour1 % 24;
                            }
                            remainTime = (day1 == 0 ? "" : day1 + resources.getString(R.string.day) + ",") + "(" + hour1 + ":" + minute1 + ":" + second1 + ")";
                            remainDuration = interval1.toDurationMillis();
                        } else {
                            if ((EnglishInit.integerFormatFromStringDate(tasks.getTasks_enddate()) / 1000000) == EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                            dateTimesThatShouldMarkInCalender.add(endDuration);
                            endDuration = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()));
                            dateTimesThatShouldMarkInCalender.add(endDuration);
                            if ((EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()) / 1000000) == EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                        return new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, remainTime, remainDuration);
                    } else {//remind in advance
                        return EnglishInit.remindTasksRemindersActionInAdvance(tasks, selectedCalender, resources);
                    }
                case 3://remind in advance
                    return EnglishInit.remindTasksRemindersActionInAdvance(tasks, selectedCalender, resources);
            }
        }
        return new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, remainTime, remainDuration);
    }

    private static int getDiffSelectedCustomDay(String day, Resources resources, int currentDay, boolean posetiveDiff) {
        int remainDay = 0;
        if (day.equals(resources.getString(R.string.saterday))) {
            if (posetiveDiff) {
                remainDay = PersianDayOfWeeks.SATURDAY.getValue() - currentDay;
            } else {
                remainDay = (PersianDayOfWeeks.SATURDAY.getValue() - currentDay + 7) % 7 - 1;
            }
        }
        if (day.equals(resources.getString(R.string.sunday))) {
            if (posetiveDiff) {
                remainDay = PersianDayOfWeeks.SUNDAY.getValue() - currentDay;
            } else {
                remainDay = (PersianDayOfWeeks.SUNDAY.getValue() - currentDay + 7) % 7 - 1;
            }
        }
        if (day.equals(resources.getString(R.string.monday))) {
            if (posetiveDiff) {
                remainDay = PersianDayOfWeeks.MONDAY.getValue() - currentDay;
            } else {
                remainDay = (PersianDayOfWeeks.MONDAY.getValue() - currentDay + 7) % 7 - 1;
            }
        }
        if (day.equals(resources.getString(R.string.tuesday))) {
            if (posetiveDiff) {
                remainDay = PersianDayOfWeeks.TUESDAY.getValue() - currentDay;
            } else {
                remainDay = (PersianDayOfWeeks.TUESDAY.getValue() - currentDay + 7) % 7 - 1;
            }
        }
        if (day.equals(resources.getString(R.string.wednesday))) {
            if (posetiveDiff) {
                remainDay = PersianDayOfWeeks.WEDNESDAY.getValue() - currentDay;
            } else {
                remainDay = (PersianDayOfWeeks.WEDNESDAY.getValue() - currentDay + 7) % 7 - 1;
            }
        }
        if (day.equals(resources.getString(R.string.thursday))) {
            if (posetiveDiff) {
                remainDay = PersianDayOfWeeks.THURSDAY.getValue() - currentDay;
            } else {
                remainDay = (PersianDayOfWeeks.THURSDAY.getValue() - currentDay + 7) % 7 - 1;
            }
        }
        if (day.equals(resources.getString(R.string.friday))) {
            if (posetiveDiff) {
                remainDay = PersianDayOfWeeks.FRIDAY.getValue() - currentDay;
            } else {
                remainDay = (PersianDayOfWeeks.FRIDAY.getValue() - currentDay + 7) % 7 - 1;
            }
        }
        return remainDay;
    }

    private static TasksReminderActions remindTasksRemindersActionInAdvance(Object tasksOrReminder, DateTime selectedCalender, Resources resources) {
        ArrayList<DateTime> dateTimesThatShouldMarkInCalender = new ArrayList<>();
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        long remainDuration = 0;
        boolean isInRecyclerView = false, isTask = false, isCreateReminder = false, isActive = true;
        String remainTime = "";
        String repeatType = "";
        long objectStartDate;
        DateTime endDate = new DateTime(EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear() == 12 ? EnglishInit.getCurrentDateTimeWithSecond().getYear() + 1 : EnglishInit.getCurrentDateTimeWithSecond().getYear(),
                EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear() == 12 ? 1 : EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear() + 1,
                EnglishInit.getCurrentDateTimeWithSecond().getDayOfMonth(), EnglishInit.getCurrentDateTimeWithSecond().getHourOfDay()
                , EnglishInit.getCurrentDateTimeWithSecond().getMinuteOfHour(),
                EnglishInit.getCurrentDateTimeWithSecond().getSecondOfMinute(), EnglishInit.getCurrentDateTimeWithSecond().getMillisOfSecond());
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            repeatType = tasks.getTasks_repeateddays();
            objectStartDate = EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate());
            isTask = true;
            if (tasks.getTasks_iscompleted() == 1) {
                isActive = false;
                endDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getComplete_date()
                        .replace(resources.getString(R.string.inDate), "").replace(resources.getString(R.string.completed), "")));
                if (EnglishInit.integerFormatTime(endDate) >= EnglishInit.integerFormatTime(EnglishInit.convertIntegerToDateTime(objectStartDate))) {
                    endDate = EnglishInit.dateTimeAfter7dayFromCurrent(endDate, 1);
                }
            }
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            repeatType = reminders.getReminders_repeatedday();
            isCreateReminder = reminders.getReminders_crdate() == null;
            objectStartDate = isCreateReminder ? Long.parseLong((selectedCalender == null ? EnglishInit.integerFormatDate(startDuration) : EnglishInit.integerFormatDate(selectedCalender)) + "" + reminders.getReminders_time().replaceAll(":", "")) :
                    Long.parseLong(reminders.getReminders_crdate() / 1000000 + "" + reminders.getReminders_time().replaceAll(":", ""));
            if (reminders.getReminders_active() == 0 && reminders.getReminders_update() != null) {
                isActive = false;
                endDate = EnglishInit.convertIntegerToDateTime(reminders.getReminders_update());
                if (EnglishInit.integerFormatTime(endDate) >= EnglishInit.integerFormatTime(EnglishInit.convertIntegerToDateTime(objectStartDate))) {
                    endDate = EnglishInit.dateTimeAfter7dayFromCurrent(endDate, 1);
                }
            }
        }
        int intervalNum = 0;
        boolean isNotCustomDayReminder = true;


        DateTime startDate = EnglishInit.convertIntegerToDateTime(objectStartDate);
        int duration = Days.daysBetween(startDate, endDate).getDays();
        if ((selectedCalender == null && isTask) || (isCreateReminder)) {//ijad || (selectedCalender != null && !isTask)
            if (repeatType.equals(resources.getString(R.string.daily))) {
                TasksReminderActions t = calculateRemainTimeInDaily(tasksOrReminder, selectedCalender, resources, 1);
                remainDuration = t.getRemainDuration();
                remainTime = t.getRemainTime();
            } else if (repeatType.equals(resources.getString(R.string.weekly))) {
                TasksReminderActions t = calculateRemainTimeInWeekly(tasksOrReminder, selectedCalender, resources, 1);
                remainDuration = t.getRemainDuration();
                remainTime = t.getRemainTime();
            } else if (repeatType.equals(resources.getString(R.string.monthly))) {
                TasksReminderActions t = calculateRemainTimeInMonthly(tasksOrReminder, selectedCalender, resources, 1);
                remainDuration = t.getRemainDuration();
                remainTime = t.getRemainTime();
            } else if (repeatType.equals(resources.getString(R.string.yearly))) {
                TasksReminderActions t = calculateRemainTimeInYearly(tasksOrReminder, selectedCalender, resources, 1);
                remainDuration = t.getRemainDuration();
                remainTime = t.getRemainTime();

            } else if (!repeatType.isEmpty() && !repeatType.contains(resources.getString(R.string.each))) {//custom
                TasksReminderActions t = calculateRemainTimeForCustomDay(tasksOrReminder, selectedCalender, resources);
                remainTime = t.getRemainTime();
                remainDuration = t.getRemainDuration();
            } else if (!repeatType.isEmpty()) {//advance
                TasksReminderActions t = calculateRemainTimeForAdvanceDay(tasksOrReminder, selectedCalender, resources);
                remainTime = t.getRemainTime();
                remainDuration = t.getRemainDuration();
            }

        } else {//show mark and recycler view
            if (repeatType.equals(resources.getString(R.string.daily))) {
                intervalNum = 1;
                if (objectStartDate / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                    if (isActive) {
                        isInRecyclerView = true;
                    } else {
                        if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                            isInRecyclerView = true;
                        }
                    }

                }
            } else if (repeatType.equals(resources.getString(R.string.weekly))) {
                intervalNum = 7;//@todo test
                if (objectStartDate / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                    if (EnglishInit.getSpecificDiffDayBetweenTwoDate(EnglishInit.convertIntegerToDateTime(objectStartDate), selectedCalender, 7) % 7 == 0) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
            } else if (repeatType.equals(resources.getString(R.string.monthly))) {
                intervalNum = 30;
                if (objectStartDate / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                    if (EnglishInit.getSpecificDiffDayBetweenTwoDate(EnglishInit.convertIntegerToDateTime(objectStartDate), selectedCalender, 30) % 30 == 0) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
            } else if (repeatType.equals(resources.getString(R.string.yearly))) {
                intervalNum = 365;
                if (EnglishInit.getSpecificDiffDayBetweenTwoDate(EnglishInit.convertIntegerToDateTime(objectStartDate), selectedCalender, 365) % 365 == 0) {
                    if (isActive) {
                        isInRecyclerView = true;
                    } else {
                        if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                            isInRecyclerView = true;
                        }
                    }
                }
            } else if (!repeatType.contains(resources.getString(R.string.each))) {//custom
                TasksReminderActions t = calculateMarkAndRecyclerViewForCustomDay(tasksOrReminder, selectedCalender, resources);
                dateTimesThatShouldMarkInCalender = t.getDateTimesThatShouldMarkInCalender();
                isInRecyclerView = t.isInRecyclerView();
                isNotCustomDayReminder = false;
            } else if (!repeatType.isEmpty()) {//advance
                TasksReminderActions t = calculateMarkAndRecyclerViewForAdvanceDay(tasksOrReminder, selectedCalender, resources);
                dateTimesThatShouldMarkInCalender = t.getDateTimesThatShouldMarkInCalender();
                isInRecyclerView = t.isInRecyclerView();
                intervalNum = t.getIntervalNum();
            }
            if (isNotCustomDayReminder) {
                for (int i = 0; i < duration; ) {
                    DateTime newDateTime = startDate.plusDays(i);
                    if (EnglishInit.checkValidDate(newDateTime)) {
                        if (isSpecificDiffDayBetweenTwoDate(startDate, newDateTime, intervalNum)) {
                            dateTimesThatShouldMarkInCalender.add(newDateTime);
                        }
                    }
                    i++;
                }
            }
        }
        return new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, remainTime, remainDuration);
    }

    private static TasksReminderActions calculateRemainTimeForCustomDay(Object tasksOrReminder, DateTime selectedCalender, Resources resources) {
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        int remainDay;
        String repeatType;
        long objectStartDate;
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            repeatType = tasks.getTasks_repeateddays();
            objectStartDate = EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate());
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            repeatType = reminders.getReminders_repeatedday();
            objectStartDate = selectedCalender == null ? Long.parseLong(EnglishInit.integerFormatDate(startDuration) + "" + reminders.getReminders_time().replaceAll(":", "")) :
                    Long.parseLong(EnglishInit.integerFormatDate(selectedCalender) + "" + reminders.getReminders_time().replaceAll(":", ""));
        }
        //sa@i ke gharare yadavari beshe
        long newTime = objectStartDate % 1000000;
        //sa@ alan
        long startTime = EnglishInit.integerFormatDateTime(startDuration) % 1000000;
        DateTime currentTime = DateTime.now();
        EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[currentTime.getDayOfWeek() - 1];
        remainDay = getDiffSelectedCustomDay(repeatType.split(",")[0], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), true);
        if ((remainDay == 0 && newTime < startTime && repeatType.split(",").length > 1) || (remainDay < 0 && repeatType.split(",").length > 1)) {
            remainDay = getDiffSelectedCustomDay(repeatType.split(",")[1], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), true);
            if ((remainDay == 0 && newTime < startTime && repeatType.split(",").length > 2) || (remainDay < 0 && repeatType.split(",").length > 2)) {
                remainDay = getDiffSelectedCustomDay(repeatType.split(",")[2], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), true);
                if ((remainDay == 0 && newTime < startTime && repeatType.split(",").length > 3) || (remainDay < 0 && repeatType.split(",").length > 3)) {
                    remainDay = getDiffSelectedCustomDay(repeatType.split(",")[3], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), true);
                    if ((remainDay == 0 && newTime < startTime && repeatType.split(",").length > 4) || (remainDay < 0 && repeatType.split(",").length > 4)) {
                        remainDay = getDiffSelectedCustomDay(repeatType.split(",")[4], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), true);
                        if ((remainDay == 0 && newTime < startTime && repeatType.split(",").length > 5) || (remainDay < 0 && repeatType.split(",").length > 5)) {
                            remainDay = getDiffSelectedCustomDay(repeatType.split(",")[5], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), true);
                            if ((remainDay == 0 && newTime < startTime && repeatType.split(",").length > 6) || (remainDay < 0 && repeatType.split(",").length > 6)) {
                                remainDay = getDiffSelectedCustomDay(repeatType.split(",")[6], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), true);
                            } else if (repeatType.split(",").length == 6 && remainDay < 0) {
                                remainDay = getDiffSelectedCustomDay(repeatType.split(",")[0], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), false);
                            }
                        } else if (repeatType.split(",").length == 5 && remainDay < 0) {
                            remainDay = getDiffSelectedCustomDay(repeatType.split(",")[0], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), false);
                        }
                    } else if (repeatType.split(",").length == 4 && remainDay < 0) {
                        remainDay = getDiffSelectedCustomDay(repeatType.split(",")[0], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), false);
                    }
                } else if (repeatType.split(",").length == 3 && remainDay < 0) {
                    remainDay = getDiffSelectedCustomDay(repeatType.split(",")[0], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), false);
                }

            } else if (repeatType.split(",").length == 2 && remainDay < 0) {
                remainDay = getDiffSelectedCustomDay(repeatType.split(",")[0], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), false);
            }
        } else if (repeatType.split(",").length == 1 && remainDay < 0) {
            remainDay = getDiffSelectedCustomDay(repeatType.split(",")[0], resources, PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue(), false);
        }
//if time was in range of 000000 until 095959
        StringBuilder zeroNum = new StringBuilder();
        String newTimeString = String.valueOf(newTime);
        while (newTimeString.length() < 6) {
            zeroNum.append("0");
            newTimeString = zeroNum + "" + newTime;
        }
        DateTime newStartDuration = EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + newTimeString));
        DateTime endDuration = EnglishInit.dateTimeAfter7dayFromCurrent(newStartDuration, remainDay);

        Interval interval = new Interval(startDuration, endDuration);
        long hour = interval.toDuration().getStandardMinutes() / 60;
        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
        long second = interval.toDuration().getStandardSeconds() - (hour * 60 * 60) - (minute * 60);
        if (hour >= 24) {
            hour = hour % 24;
        }
        if (startTime > newTime) {
            remainDay = remainDay - 1;
        }
        long duration = interval.toDurationMillis();
        String remainTime = ((remainDay) == 0 ? "" : (remainDay) + resources.getString(R.string.day) + ",") + "(" + hour + ":" + minute + ":" + second + ")";
        TasksReminderActions t = new TasksReminderActions();
        t.setRemainTime(remainTime);
        t.setRemainDuration(duration);
        return t;
    }

    private static TasksReminderActions calculateRemainTimeForAdvanceDay(Object reminderOrTasks, DateTime selectedCalender, Resources resources) {
        String repeatType = "";
        if (reminderOrTasks instanceof Reminders) {
            Reminders reminders = (Reminders) reminderOrTasks;
            repeatType = reminders.getReminders_repeatedday();
        } else {
            Tasks tasks = (Tasks) reminderOrTasks;
            repeatType = tasks.getTasks_repeateddays();
        }
        String remainTime = "";
        String[] repeatTypeSplit = repeatType.split(" ");
        String[] typePeriodVal = new String[]{resources.getString(R.string.day), resources.getString(R.string.week),
                resources.getString(R.string.month), resources.getString(R.string.year)};
        long newStartInterval = 0;
        if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
            TasksReminderActions t = calculateRemainTimeInDaily(reminderOrTasks, selectedCalender, resources, Integer.parseInt(repeatTypeSplit[1]));
            remainTime = t.getRemainTime();
            newStartInterval = t.getRemainDuration();
        }
        if (typePeriodVal[1].equals(repeatTypeSplit[2])) {
            TasksReminderActions t = calculateRemainTimeInWeekly(reminderOrTasks, selectedCalender, resources, Integer.parseInt(repeatTypeSplit[1]));
            remainTime = t.getRemainTime();
            newStartInterval = t.getRemainDuration();
        }
        if (typePeriodVal[2].equals(repeatTypeSplit[2])) {
            TasksReminderActions t = calculateRemainTimeInMonthly(reminderOrTasks, selectedCalender, resources, Integer.parseInt(repeatTypeSplit[1]));
            remainTime = t.getRemainTime();
            newStartInterval = t.getRemainDuration();
        }
        if (typePeriodVal[3].equals(repeatTypeSplit[2])) {
            TasksReminderActions t = calculateRemainTimeInYearly(reminderOrTasks, selectedCalender, resources, Integer.parseInt(repeatTypeSplit[1]));
            remainTime = t.getRemainTime();
            newStartInterval = t.getRemainDuration();
        }
        TasksReminderActions t = new TasksReminderActions();
        t.setRemainTime(remainTime);
        t.setRemainDuration(newStartInterval);
        return t;
    }

    private static TasksReminderActions calculateMarkAndRecyclerViewForAdvanceDay(Object tasksOrReminder, DateTime selectedCalender, Resources resources) {
        String repeatType = "";
        String startDate = "";
        boolean isActive = true;
        DateTime endDate = null;
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            repeatType = tasks.getTasks_repeateddays();
            startDate = tasks.getTasks_startdate();
            if (tasks.getTasks_iscompleted() == 1) {
                isActive = false;
                endDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getComplete_date()
                        .replace(resources.getString(R.string.inDate), "").replace(resources.getString(R.string.completed), "")));
                if (EnglishInit.integerFormatTime(endDate) >= EnglishInit.integerFormatTime(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(startDate)))) {
                    endDate = EnglishInit.dateTimeAfter7dayFromCurrent(endDate, 1);
                }

            }
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            repeatType = reminders.getReminders_repeatedday();
            startDate = EnglishInit.stringFormatDateTime(EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate()));
            if (reminders.getReminders_active() == 0 && reminders.getReminders_update() != null) {
                isActive = false;
                endDate = EnglishInit.convertIntegerToDateTime(reminders.getReminders_update());
                if (EnglishInit.integerFormatTime(endDate) >= EnglishInit.integerFormatTime(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(startDate)))) {
                    endDate = EnglishInit.dateTimeAfter7dayFromCurrent(endDate, 1);
                }
            }
        }
        ArrayList<DateTime> dateTimesThatShouldMarkInCalender = new ArrayList<>();
        int intervalNum = 0;
        boolean isInRecyclerView = false;
        String[] repeatTypeSplit = repeatType.split(" ");
        String[] typePeriodVal = new String[]{resources.getString(R.string.day), resources.getString(R.string.week),
                resources.getString(R.string.month), resources.getString(R.string.year)};
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
            intervalNum = Integer.parseInt(repeatTypeSplit[1]);
        }
        if (typePeriodVal[1].equals(repeatTypeSplit[2])) {
            intervalNum = 7 * Integer.parseInt(repeatTypeSplit[1]);
        }
        if (typePeriodVal[2].equals(repeatTypeSplit[2])) {
            intervalNum = 30 * Integer.parseInt(repeatTypeSplit[1]);
        }
        if (typePeriodVal[3].equals(repeatTypeSplit[2])) {
            intervalNum = 365 * Integer.parseInt(repeatTypeSplit[1]);
        }
        if (EnglishInit.integerFormatFromStringDate(startDate) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
            if (isSpecificDiffDayBetweenTwoDate(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(startDate)), selectedCalender, intervalNum)) {
                if (isActive) {
                    isInRecyclerView = true;
                } else {
                    if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                        isInRecyclerView = true;
                    }
                }
            }
        }
        TasksReminderActions t = new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, "", 0);
        t.setIntervalNum(intervalNum);
        return t;
    }

    private static TasksReminderActions calculateMarkAndRecyclerViewForCustomDay(Object tasksOrReminder, DateTime selectedCalender, Resources resources) {
        String repeatType = "";
        String startDateString = "";
        boolean isActive = true;
        DateTime endDate = new DateTime(EnglishInit.getCurrentDateTimeWithSecond().getYear(),
                EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear() == 12 ? 1 : EnglishInit.getCurrentDateTimeWithSecond().getMonthOfYear() + 1,
                EnglishInit.getCurrentDateTimeWithSecond().getDayOfMonth(), EnglishInit.getCurrentDateTimeWithSecond().getHourOfDay()
                , EnglishInit.getCurrentDateTimeWithSecond().getMinuteOfHour(),
                EnglishInit.getCurrentDateTimeWithSecond().getSecondOfMinute(), EnglishInit.getCurrentDateTimeWithSecond().getMillisOfSecond());
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            repeatType = tasks.getTasks_repeateddays();
            startDateString = tasks.getTasks_startdate();
            if (tasks.getTasks_iscompleted() == 1) {
                isActive = false;
                endDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getComplete_date()
                        .replace(resources.getString(R.string.inDate), "").replace(resources.getString(R.string.completed), "")));
                if (EnglishInit.integerFormatTime(endDate) >= EnglishInit.integerFormatTime(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(startDateString)))) {
                    endDate = EnglishInit.dateTimeAfter7dayFromCurrent(endDate, 1);
                }
            }
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            repeatType = reminders.getReminders_repeatedday();
            startDateString = EnglishInit.stringFormatDateTime(EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate()));
            if (reminders.getReminders_active() == 0) {
                isActive = false;
                endDate = EnglishInit.convertIntegerToDateTime(reminders.getReminders_update());
                if (EnglishInit.integerFormatTime(endDate) >= EnglishInit.integerFormatTime(EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(startDateString)))) {
                    endDate = EnglishInit.dateTimeAfter7dayFromCurrent(endDate, 1);
                }
            }
        }
        ArrayList<DateTime> dateTimesThatShouldMarkInCalender = new ArrayList<>();

        DateTime startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(startDateString));
        int duration = Days.daysBetween(startDate, endDate).getDays();
        boolean isInRecyclerView = false;
        for (String repeatTypeVal : repeatType.split(",")) {
            if (repeatTypeVal.equals(resources.getString(R.string.saterday))) {
                if (selectedCalender.getDayOfWeek() == 7) {
                    if (EnglishInit.integerFormatFromStringDate(startDateString) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < duration; ) {
                    DateTime dateAfterIday = EnglishInit.dateTimeAfter7dayFromCurrent(startDate, i);
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[dateAfterIday.getDayOfWeek() - 1];
                    //if today was saterday
                    if (PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue() == 1) {
                        dateTimesThatShouldMarkInCalender.add(dateAfterIday);
                        i = i + 7;
                    } else {
                        i++;
                    }
                }
            } else if (repeatTypeVal.equals(resources.getString(R.string.sunday))) {
                if (selectedCalender.getDayOfWeek() == 6) {
                    if (EnglishInit.integerFormatFromStringDate(startDateString) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < duration; ) {
                    DateTime dateAfterIday = EnglishInit.dateTimeAfter7dayFromCurrent(startDate, i);
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[dateAfterIday.getDayOfWeek() - 1];
                    //if today was sunday
                    if (PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue() == 2) {
                        dateTimesThatShouldMarkInCalender.add(dateAfterIday);
                        i = i + 7;
                    } else {
                        i++;
                    }
                }
            } else if (repeatTypeVal.equals(resources.getString(R.string.monday))) {
                if (selectedCalender.getDayOfWeek() == 1) {
                    if (EnglishInit.integerFormatFromStringDate(startDateString) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < duration; ) {
                    DateTime dateAfterIday = EnglishInit.dateTimeAfter7dayFromCurrent(startDate, i);
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[dateAfterIday.getDayOfWeek() - 1];
                    //if today was monday
                    if (PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue() == 3) {
                        dateTimesThatShouldMarkInCalender.add(dateAfterIday);
                        i = i + 7;
                    } else {
                        i++;
                    }
                }
            } else if (repeatTypeVal.equals(resources.getString(R.string.tuesday))) {
                if (selectedCalender.getDayOfWeek() == 2) {
                    if (EnglishInit.integerFormatFromStringDate(startDateString) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < duration; ) {
                    DateTime dateAfterIday = EnglishInit.dateTimeAfter7dayFromCurrent(startDate, i);
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[dateAfterIday.getDayOfWeek() - 1];
                    //if today was tuesday
                    if (PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue() == 4) {
                        dateTimesThatShouldMarkInCalender.add(dateAfterIday);
                        i = i + 7;
                    } else {
                        i++;
                    }
                }
            } else if (repeatTypeVal.equals(resources.getString(R.string.wednesday))) {
                if (selectedCalender.getDayOfWeek() == 3) {
                    if (EnglishInit.integerFormatFromStringDate(startDateString) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < duration; ) {
                    DateTime dateAfterIday = EnglishInit.dateTimeAfter7dayFromCurrent(startDate, i);
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[dateAfterIday.getDayOfWeek() - 1];
                    //if today was wednesday
                    if (PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue() == 5) {
                        dateTimesThatShouldMarkInCalender.add(dateAfterIday);
                        i = i + 7;
                    } else {
                        i++;
                    }
                }
            } else if (repeatTypeVal.equals(resources.getString(R.string.thursday))) {
                if (selectedCalender.getDayOfWeek() == 4) {
                    if (EnglishInit.integerFormatFromStringDate(startDateString) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < duration; ) {
                    DateTime dateAfterIday = EnglishInit.dateTimeAfter7dayFromCurrent(startDate, i);
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[dateAfterIday.getDayOfWeek() - 1];
                    //if today was thursday
                    if (PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue() == 6) {
                        dateTimesThatShouldMarkInCalender.add(dateAfterIday);
                        i = i + 7;
                    } else {
                        i++;
                    }
                }
            } else if (repeatTypeVal.equals(resources.getString(R.string.friday))) {
                if (selectedCalender.getDayOfWeek() == 5) {
                    if (EnglishInit.integerFormatFromStringDate(startDateString) / 1000000 <= EnglishInit.integerFormatDate(selectedCalender)) {
                        if (isActive) {
                            isInRecyclerView = true;
                        } else {
                            if (EnglishInit.integerFormatDate(endDate) > EnglishInit.integerFormatDate(selectedCalender)) {
                                isInRecyclerView = true;
                            }
                        }
                    }
                }
                for (int i = 0; i < duration; ) {
                    DateTime dateAfterIday = EnglishInit.dateTimeAfter7dayFromCurrent(startDate, i);
                    EnglishDayOfWeeks englishDayOfWeeks = EnglishDayOfWeeks.values()[dateAfterIday.getDayOfWeek() - 1];
                    //if today was friday
                    if (PersianDayOfWeeks.valueOf(englishDayOfWeeks.name()).getValue() == 7) {
                        dateTimesThatShouldMarkInCalender.add(dateAfterIday);
                        i = i + 7;
                    } else {
                        i++;
                    }
                }
            }
        }
        return new TasksReminderActions(dateTimesThatShouldMarkInCalender, isInRecyclerView, "", 0);
    }

    private static TasksReminderActions calculateRemainTimeInDaily(Object tasksOrReminder, DateTime selectedCalender, Resources resources, int intervalNum) {
        DateTime startDate;
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()));
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            startDate = EnglishInit.getTodayDateTimeWithSelectedTime(reminders.getReminders_time(), 0, false);
            if (reminders.getReminders_crdate() != null) {
                startDate = EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate());
            } else if (selectedCalender != null) {
                startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(EnglishInit.stringFormatDate(selectedCalender) + " " + reminders.getReminders_time()));
            }
        }
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        long selectedTime = EnglishInit.integerFormatDateTime(startDate) % 1000000;
        long nowTime = EnglishInit.integerFormatDateTime(startDuration) % 1000000;
        Interval interval;
        Long newStartInterval;
        if (EnglishInit.integerFormatDateTime(startDuration) > EnglishInit.integerFormatDateTime(startDate)) {
            //dar surati ke tarikh shoru gozahste bashad
            int diffDayMode = EnglishInit.getSpecificDiffDayBetweenTwoDate(startDate, startDuration, intervalNum);
            //if time was in range of 000000 until 095959
            StringBuilder zeroNum = new StringBuilder();
            String selectedTimeString = String.valueOf(selectedTime);
            while (selectedTimeString.length() < 6) {
                zeroNum.append("0");
                selectedTimeString = zeroNum + "" + selectedTime;
            }
            if (diffDayMode == 0) {
                if (selectedTime < nowTime) {
                    startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), intervalNum);
                } else {
                    startDate = EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString));
                }
            } else {
                startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), intervalNum - diffDayMode);
            }
        }
        interval = new Interval(startDuration, startDate);
        newStartInterval = interval.toDurationMillis();
        long day = interval.toDuration().getStandardHours() / 24;
        long hour = interval.toDuration().getStandardMinutes() / 60;
        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
        long second = interval.toDuration().getStandardSeconds() - (hour * 60 * 60) - (minute * 60);
        if (hour >= 24) {
            hour = hour % 24;
        }
        String remainTime = (day == 0 ? "" : day + resources.getString(R.string.day) + ",") + "(" + hour + ":" + minute + ":" + second + ")";
        TasksReminderActions tasksReminderActions = new TasksReminderActions();
        tasksReminderActions.setRemainDuration(newStartInterval);
        tasksReminderActions.setRemainTime(remainTime);
        return tasksReminderActions;
    }

    private static TasksReminderActions calculateRemainTimeInWeekly(Object tasksOrReminder, DateTime selectedCalender, Resources resources, int intervalNum) {
        DateTime startDate = null;
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()));
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            startDate = EnglishInit.getTodayDateTimeWithSelectedTime(reminders.getReminders_time(), 0, false);
            if (reminders.getReminders_crdate() != null) {
                startDate = EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate());
            } else if (selectedCalender != null) {
                startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(EnglishInit.stringFormatDate(selectedCalender) + " " + reminders.getReminders_time()));
            }
        }
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        long selectedTime = EnglishInit.integerFormatDateTime(startDate) % 1000000;
        long nowTime = EnglishInit.integerFormatDateTime(startDuration) % 1000000;
        Interval interval;
        Long newStartInterval;
        if (EnglishInit.integerFormatDateTime(startDuration) > EnglishInit.integerFormatDateTime(startDate)) {
            //dar surati ke tarikh shoru gozahste bashad
            int diffDayMode = EnglishInit.getSpecificDiffDayBetweenTwoDate(startDate, startDuration, intervalNum * 7);
            //if time was in range of 000000 until 095959
            StringBuilder zeroNum = new StringBuilder();
            String selectedTimeString = String.valueOf(selectedTime);
            while (selectedTimeString.length() < 6) {
                zeroNum.append("0");
                selectedTimeString = zeroNum + "" + selectedTime;
            }
            if (diffDayMode == 0) {
                if (selectedTime < nowTime) {
                    startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), intervalNum * 7);
                } else {
                    startDate = EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString));
                }
            } else {
                startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), (intervalNum * 7) - diffDayMode);
            }
        }
        interval = new Interval(startDuration, startDate);
        newStartInterval = interval.toDurationMillis();
        long day = interval.toDuration().getStandardHours() / 24;
        long hour = interval.toDuration().getStandardMinutes() / 60;
        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
        long second = interval.toDuration().getStandardSeconds() - (hour * 60 * 60) - (minute * 60);
        if (hour >= 24) {
            hour = hour % 24;
        }
        String remainTime = (day == 0 ? "" : day + resources.getString(R.string.day) + ",") + "(" + hour + ":" + minute + ":" + second + ")";
        TasksReminderActions tasksReminderActions = new TasksReminderActions();
        tasksReminderActions.setRemainDuration(newStartInterval);
        tasksReminderActions.setRemainTime(remainTime);
        return tasksReminderActions;
    }

    private static TasksReminderActions calculateRemainTimeInMonthly(Object tasksOrReminder, DateTime selectedCalender, Resources resources, int intervalNum) {
        DateTime startDate = null;
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()));
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            startDate = EnglishInit.getTodayDateTimeWithSelectedTime(reminders.getReminders_time(), 0, false);
            if (reminders.getReminders_crdate() != null) {
                startDate = EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate());
            } else if (selectedCalender != null) {
                startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(EnglishInit.stringFormatDate(selectedCalender) + " " + reminders.getReminders_time()));
            }
        }
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        long selectedTime = EnglishInit.integerFormatDateTime(startDate) % 1000000;
        long nowTime = EnglishInit.integerFormatDateTime(startDuration) % 1000000;
        Interval interval;
        Long newStartInterval;
        if (EnglishInit.integerFormatDateTime(startDuration) > EnglishInit.integerFormatDateTime(startDate)) {
            //dar surati ke tarikh shoru gozahste bashad
            int diffDayMode = EnglishInit.getSpecificDiffDayBetweenTwoDate(startDate, startDuration, intervalNum * 30);
            //if time was in range of 000000 until 095959
            StringBuilder zeroNum = new StringBuilder();
            String selectedTimeString = String.valueOf(selectedTime);
            while (selectedTimeString.length() < 6) {
                zeroNum.append("0");
                selectedTimeString = zeroNum + "" + selectedTime;
            }
            if (diffDayMode == 0) {
                if (selectedTime < nowTime) {
                    startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), intervalNum * 30);
                } else {
                    startDate = EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString));
                }
            } else {
                startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), (intervalNum * 30) - diffDayMode);
            }
        }
        interval = new Interval(startDuration, startDate);
        newStartInterval = interval.toDurationMillis();
        long day = interval.toDuration().getStandardHours() / 24;
        long hour = interval.toDuration().getStandardMinutes() / 60;
        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
        long second = interval.toDuration().getStandardSeconds() - (hour * 60 * 60) - (minute * 60);
        if (hour >= 24) {
            hour = hour % 24;
        }
        String remainTime = (day == 0 ? "" : day + resources.getString(R.string.day) + ",") + "(" + hour + ":" + minute + ":" + second + ")";
        TasksReminderActions tasksReminderActions = new TasksReminderActions();
        tasksReminderActions.setRemainDuration(newStartInterval);
        tasksReminderActions.setRemainTime(remainTime);
        return tasksReminderActions;
    }

    private static TasksReminderActions calculateRemainTimeInYearly(Object tasksOrReminder, DateTime selectedCalender, Resources resources, int intervalNum) {
        DateTime startDate = null;
        if (tasksOrReminder instanceof Tasks) {
            Tasks tasks = (Tasks) tasksOrReminder;
            startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(tasks.getTasks_startdate()));
        } else {
            Reminders reminders = (Reminders) tasksOrReminder;
            startDate = EnglishInit.getTodayDateTimeWithSelectedTime(reminders.getReminders_time(), 0, false);
            if (reminders.getReminders_crdate() != null) {
                startDate = EnglishInit.convertIntegerToDateTime(reminders.getReminders_crdate());
            } else if (selectedCalender != null) {
                startDate = EnglishInit.convertIntegerToDateTime(EnglishInit.integerFormatFromStringDate(EnglishInit.stringFormatDate(selectedCalender) + " " + reminders.getReminders_time()));
            }
        }
        DateTime startDuration = EnglishInit.getCurrentDateTimeWithSecond();
        long selectedTime = EnglishInit.integerFormatDateTime(startDate) % 1000000;
        long nowTime = EnglishInit.integerFormatDateTime(startDuration) % 1000000;
        Interval interval;
        Long newStartInterval;
        if (EnglishInit.integerFormatDateTime(startDuration) > EnglishInit.integerFormatDateTime(startDate)) {
            //dar surati ke tarikh shoru gozahste bashad
            int diffDayMode = EnglishInit.getSpecificDiffDayBetweenTwoDate(startDate, startDuration, intervalNum * 365);
            //if time was in range of 000000 until 095959
            StringBuilder zeroNum = new StringBuilder();
            String selectedTimeString = String.valueOf(selectedTime);
            while (selectedTimeString.length() < 6) {
                zeroNum.append("0");
                selectedTimeString = zeroNum + "" + selectedTime;
            }
            if (diffDayMode == 0) {
                if (selectedTime < nowTime) {
                    startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), intervalNum * 365);
                } else {
                    startDate = EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString));
                }
            } else {
                startDate = EnglishInit.dateTimeAfter7dayFromCurrent(EnglishInit.convertIntegerToDateTime(Long.parseLong(EnglishInit.integerFormatDateTime(startDuration) / 1000000 + "" + selectedTimeString)), (intervalNum * 365) - diffDayMode);
            }
        }
        interval = new Interval(startDuration, startDate);
        newStartInterval = interval.toDurationMillis();
        long day = interval.toDuration().getStandardHours() / 24;
        long hour = interval.toDuration().getStandardMinutes() / 60;
        long minute = interval.toDuration().getStandardMinutes() - hour * 60;
        long second = interval.toDuration().getStandardSeconds() - (hour * 60 * 60) - (minute * 60);
        if (hour >= 24) {
            hour = hour % 24;
        }
        String remainTime = (day == 0 ? "" : day + resources.getString(R.string.day) + ",") + "(" + hour + ":" + minute + ":" + second + ")";
        TasksReminderActions tasksReminderActions = new TasksReminderActions();
        tasksReminderActions.setRemainDuration(newStartInterval);
        tasksReminderActions.setRemainTime(remainTime);
        return tasksReminderActions;
    }

    public static float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
