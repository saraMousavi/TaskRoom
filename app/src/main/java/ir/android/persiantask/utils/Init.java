package ir.android.persiantask.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import androidx.work.Operation;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.workers.AlarmWorker;
import ir.android.persiantask.utils.calender.LanguageUtils;
import ir.android.persiantask.utils.calender.PersianCalendar;
import ir.android.persiantask.utils.enums.CategoryType;

public class Init {
    /**
     * set image resource of project category
     *
     * @param projectCategoryIcon
     * @param category_id
     * @param isWhite
     */
    public static void setProjectCategory(ImageView projectCategoryIcon, Integer category_id, boolean isWhite) {
        if (category_id.equals(CategoryType.ART.getValue())) {
            if (isWhite) {
                projectCategoryIcon.setImageResource(R.drawable.ic_white_art);
            } else {
                projectCategoryIcon.setImageResource(R.drawable.ic_black_art);
            }
        } else if (category_id.equals(CategoryType.SPORT.getValue())) {
            if (isWhite) {
                projectCategoryIcon.setImageResource(R.drawable.ic_white_sports);
            } else {
                projectCategoryIcon.setImageResource(R.drawable.ic_black_sports);
            }
        } else if (category_id.equals(CategoryType.SCIENTIFIC.getValue())) {
            if (isWhite) {
                projectCategoryIcon.setImageResource(R.drawable.ic_white_scientific);
            } else {
                projectCategoryIcon.setImageResource(R.drawable.ic_black_scientific);
            }
        }
    }

    /**
     * current date with format slash and dot
     *
     * @return
     */
    public static String getCurrentDate() {
        GregorianCalendar galena = new GregorianCalendar();
        PersianCalendar persianCalendar = new PersianCalendar();
        int month = persianCalendar.getPersianMonth() + 1;
        int value = galena.get(Calendar.HOUR) % 12;
        int hour = Integer.parseInt(LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%d", value == 0 ? 12 : value)));
        int minute = Integer.parseInt(LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.MINUTE) == 60 ? 0 : galena.get(Calendar.MINUTE))));
        return persianCalendar.getPersianYear() + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (persianCalendar.getPersianDay() < 10 ? "0" + persianCalendar.getPersianDay() : persianCalendar.getPersianDay())
                + " "
                + (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute);
    }

    /**
     * current date time with second
     *
     * @return
     */
    public static DateTime getCurrentDateTimeWithSecond() {
        GregorianCalendar galena = new GregorianCalendar();
        PersianCalendar persianCalendar = new PersianCalendar();
        int month = persianCalendar.getPersianMonth() + 1;
        int value = galena.get(Calendar.HOUR) % 12;
        int hour = Integer.parseInt(LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%d", value == 0 ? 12 : value)));
        int minute = Integer.parseInt(LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.MINUTE) == 60 ? 0 : galena.get(Calendar.MINUTE))));
        int second = Integer.parseInt(LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.SECOND) == 60 ? 0 : galena.get(Calendar.SECOND))));

        return convertIntegerToDateTime(integerFormatFromStringDate(persianCalendar.getPersianYear() + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (persianCalendar.getPersianDay() < 10 ? "0" + persianCalendar.getPersianDay() : persianCalendar.getPersianDay())
                + " "
                + (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute)
                + ":" + (second < 10 ? "0" + second : second)));
    }

    /**
     * current date time with second
     *
     * @return
     */
    public static DateTime getTodayDateTimeWithTime(String time, Integer nextDay) {
        PersianCalendar persianCalendar = new PersianCalendar();
        int month = persianCalendar.getPersianMonth() + 1;
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);
        //@TODO check today is last day of moth or not
        int day = persianCalendar.getPersianDay() == 30 ? 1 : persianCalendar.getPersianDay() + nextDay;
        return convertIntegerToDateTime((long) integerFormatFromStringDate(persianCalendar.getPersianYear() + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (day < 10 ? "0" + day : day)
                + " "
                + (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute)
                + ":" + "00"));
    }

    /**
     * current time with format dot
     *
     * @return
     */
    public static String getCurrentTime() {
        GregorianCalendar galena = new GregorianCalendar();
        int value = galena.get(Calendar.HOUR) % 12;
        int hour = Integer.parseInt(LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%d", value == 0 ? 12 : value)));
        int minute = Integer.parseInt(LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.MINUTE) == 60 ? 0 : galena.get(Calendar.MINUTE))));
        return (hour < 10 ? "0" + hour : hour)
                + ":"
                + (minute < 10 ? "0" + minute : minute);
    }

    /**
     * convert date time to string with slash and :
     *
     * @param dateTime
     * @return
     */
    public static String stringFormatDate(DateTime dateTime) {
        return dateTime.getYear() + "/" + (dateTime.getMonthOfYear() <= 10 ? "0" + dateTime.getMonthOfYear() : dateTime.getMonthOfYear())
                + "/" + (dateTime.getDayOfMonth() < 10 ? "0" + dateTime.getDayOfMonth() : dateTime.getDayOfMonth());
//                + " " + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay())
//                + ":" + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour());
    }

    /**
     * convert date time to integer
     *
     * @param dateTime
     * @return
     */
    public static Long integerFormatDate(DateTime dateTime) {
        return Long.valueOf(dateTime.getYear() + ""
                + (dateTime.getMonthOfYear() <= 10 ? "0" + dateTime.getMonthOfYear() : dateTime.getMonthOfYear())
                + "" + (dateTime.getDayOfMonth() < 10 ? "0" + dateTime.getDayOfMonth() : dateTime.getDayOfMonth()));
//                + "" + (dateTime.getHourOfDay() < 10 ? "0" + dateTime.getHourOfDay() : dateTime.getHourOfDay())
//                + "" + (dateTime.getMinuteOfHour() < 10 ? "0" + dateTime.getMinuteOfHour() : dateTime.getMinuteOfHour()) );
    }

    /**
     * convert string date with slash and : to integer
     *
     * @param dateTime
     * @return date
     */
    public static Long integerFormatFromStringDate(String dateTime) {
        if (dateTime.isEmpty()) {
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
        if (integerTime < 1000000) {
            return null;
        }
        System.out.println("integerTime = " + integerTime);
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
    public static void setViewBackgroundDependOnTheme(List<Map<View, Boolean>> views, Context context) {

        for (Map<View, Boolean> view : views) {
            for (Map.Entry<View, Boolean> entry : view.entrySet()) {
                switch (getFlag(context)) {
                    case 2:
                        //if isPrimary
                        if (entry.getValue()) {
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary2)));
                        } else {
                            //if isAccent
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent2)));
                        }
                        break;
                    case 3:
                        //if isPrimary
                        if (entry.getValue()) {
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary3)));
                        } else {
                            //if isAccent
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent3)));
                        }
                        break;
                    case 4:
                        //if isPrimary
                        if (entry.getValue()) {
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary4)));
                        } else {
                            //if isAccent
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent4)));
                        }
                        break;
                    case 5:
                        //if isPrimary
                        if (entry.getValue()) {
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary5)));
                        } else {
                            //if isAccent
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent5)));
                        }
                        break;
                    case 6:
                        //if isPrimary
                        if (entry.getValue()) {
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary6)));
                        } else {
                            //if isAccent
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent6)));
                        }
                        break;
                    default:
                        //if isPrimary
                        if (entry.getValue()) {
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
                        } else {
                            //if isAccent
                            entry.getKey().setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String requestWork(Context context, String alarmTitle, Map<Integer, Object> repeatIntervalMap, long duration, boolean isPeriodic) {

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();
        Data data = new Data.Builder().putString("alarmTitle", alarmTitle).build();
        if (isPeriodic) {
            Integer repeatInterval = 1;
            long repeatIntervalLong = 0;
            TimeUnit repeatIntervalTimeUnit = TimeUnit.DAYS;
            ArrayList<Integer> repeatIntervalList = new ArrayList<>();
            for (Map.Entry map : repeatIntervalMap.entrySet()) {
                repeatInterval = (Integer) map.getKey();
                if (map.getValue() instanceof TimeUnit) {
                    repeatIntervalTimeUnit = (TimeUnit) map.getValue();
                } else if (map.getValue() instanceof String) {
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
            } else if (repeatIntervalLong == 0) {
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
                DateTime toDay = DateTime.now();
                ArrayList<PeriodicWorkRequest> periodicWorkRequestList = new ArrayList<>();
                StringBuilder periodRequestId = new StringBuilder();
                for (Integer repeatInteger : repeatIntervalList) {
                    int diffDay = 0;
                    if (repeatInteger == 1) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            diffDay = DayOfWeek.SATURDAY.getValue() - toDay.getDayOfWeek();
                            if (diffDay < 0) {
                                diffDay = diffDay + 7;
                            }
                        }
                    } else if (repeatInteger == 2) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            diffDay = DayOfWeek.SUNDAY.getValue() - toDay.getDayOfWeek();
                            if (diffDay < 0) {
                                diffDay = diffDay + 7;
                            }
                        }
                    } else if (repeatInteger == 3) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            diffDay = DayOfWeek.MONDAY.getValue() - toDay.getDayOfWeek();
                            if (diffDay < 0) {
                                diffDay = diffDay + 7;
                            }
                        }
                    } else if (repeatInteger == 4) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            diffDay = DayOfWeek.TUESDAY.getValue() - toDay.getDayOfWeek();
                            if (diffDay < 0) {
                                diffDay = diffDay + 7;
                            }
                        }
                    } else if (repeatInteger == 5) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            diffDay = DayOfWeek.WEDNESDAY.getValue() - toDay.getDayOfWeek();
                            if (diffDay < 0) {
                                diffDay = diffDay + 7;
                            }
                        }
                    } else if (repeatInteger == 6) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            diffDay = DayOfWeek.THURSDAY.getValue() - toDay.getDayOfWeek();
                            if (diffDay < 0) {
                                diffDay = diffDay + 7;
                            }
                        }
                    } else if (repeatInteger == 7) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            diffDay = DayOfWeek.FRIDAY.getValue() - toDay.getDayOfWeek();
                            if (diffDay < 0) {
                                diffDay = diffDay + 7;
                            }
                        }
                    }
                    PeriodicWorkRequest periodicWorkRequest =
                            new PeriodicWorkRequest.Builder(AlarmWorker.class,
                                    7, TimeUnit.DAYS)
                                    .setConstraints(constraints)
                                    .setInputData(data)
                                    .setInitialDelay(diffDay * 24 * 60 * 60 * 1000L, TimeUnit.MILLISECONDS)
                                    .build();
                    periodRequestId.append(periodicWorkRequest.getId()).append(",");
                    periodicWorkRequestList.add(periodicWorkRequest);
                }
                WorkManager
                        .getInstance(context)
                        .enqueue(periodicWorkRequestList);
                return periodRequestId.toString();
            }

        } else {
            OneTimeWorkRequest oneTimeWorkRequest =
                    new OneTimeWorkRequest.Builder(AlarmWorker.class)
                            .setConstraints(constraints)
                            .setInputData(data)
                            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
                            .build();
            System.out.println("duration = " + duration);
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
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            DateTime dateTime1 = Init.getCurrentDateTimeWithSecond();
            DateTime dateTime2 = new DateTime(calendar.get(Calendar.YEAR), currentMonth == 12 ? 1 : currentMonth + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
            Interval interval = new Interval(dateTime1, dateTime2);
            intervalRepeat.put(1, interval.toDurationMillis());
        } else if (repeatType.equals(mResourse.getString(R.string.yearly))) {
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            DateTime dateTime1 = Init.getCurrentDateTimeWithSecond();
            DateTime dateTime2 = new DateTime(calendar.get(Calendar.YEAR), currentYear + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
            Interval interval = new Interval(dateTime1, dateTime2);
            intervalRepeat.put(1, interval.toDurationMillis());
        } else if (repeatType.contains(",")) {
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
            if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
                intervalRepeat.put(7 * Integer.parseInt(repeatTypeSplit[1]), TimeUnit.DAYS);
            }
            if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
                intervalRepeat.put(30 * Integer.parseInt(repeatTypeSplit[1]), TimeUnit.DAYS);
            }
            if (typePeriodVal[0].equals(repeatTypeSplit[2])) {
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
}
