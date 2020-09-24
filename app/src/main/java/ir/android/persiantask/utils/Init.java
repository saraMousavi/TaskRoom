package ir.android.persiantask.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ir.android.persiantask.R;
import ir.android.persiantask.ui.services.AlarmJobService;
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
        return persianCalendar.getPersianYear() + "/"
                + (month < 10 ? "0" + month : month) + "/"
                + (persianCalendar.getPersianDay() < 10 ? "0" + persianCalendar.getPersianDay() : persianCalendar.getPersianDay())
                + " "
                + LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%d", value == 0 ? 12 : value))
                + ":"
                + LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.MINUTE) == 60 ? 0 : galena.get(Calendar.MINUTE)));
    }

    /**
     * current time with format dot
     *
     * @return
     */
    public static String getCurrentTime() {
        GregorianCalendar galena = new GregorianCalendar();
        int value = galena.get(Calendar.HOUR) % 12;
        return LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%d", value == 0 ? 12 : value))
                + ":"
                + LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.MINUTE) == 60 ? 0 : galena.get(Calendar.MINUTE)));
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
    public static Integer integerFormatFromStringDate(String dateTime) {
        if (dateTime.isEmpty()) {
            return 0;
        }
        return Integer.valueOf(dateTime.replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 8));
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
    public static DateTime convertIntegerToDateTime(Integer integerTime) {
        if (integerTime < 1000000) {
            return null;
        }
        int year = integerTime / 10000;
        int month = (integerTime % 10000) / 100;
        int day = integerTime % 100;
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
     * onClick method that schedules the jobs based on the parameters set.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(JobScheduler mScheduler, String pkg, int jobId, int deadline) {

        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;


        ComponentName serviceName = new ComponentName(pkg,
                AlarmJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(jobId, serviceName)
                .setRequiredNetworkType(selectedNetworkOption)
                .setRequiresDeviceIdle(true);

        builder.setOverrideDeadline(deadline * 1000);

        JobInfo myJobInfo = builder.build();
        mScheduler.schedule(myJobInfo);
    }

    /**
     * onClick method for cancelling all existing jobs.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void cancelJobs(JobScheduler mScheduler) {
        if (mScheduler != null) {
            mScheduler.cancelAll();
            mScheduler = null;
        }
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
                            if(finalI > 200){
                                view.setBackgroundColor(Color.argb(255 - finalI, finalI,  0, 0));
                            } else {
                                view.setBackgroundColor(Color.argb(100, finalI,  0, 0));
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
