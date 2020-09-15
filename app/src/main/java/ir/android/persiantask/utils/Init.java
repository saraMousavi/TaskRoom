package ir.android.persiantask.utils;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ir.android.persiantask.R;
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
        int value = galena.get(Calendar.HOUR) % 12;
        return persianCalendar.getPersianYear() + "/"
                + (persianCalendar.getPersianMonth() < 10 ? "0" + persianCalendar.getPersianMonth() : persianCalendar.getPersianMonth()) + "/"
                + (persianCalendar.getPersianDay() < 10  ? "0" + persianCalendar.getPersianDay() : persianCalendar.getPersianDay())
                + " "
                + LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%d", value == 0 ? 12 : value))
                + ":"
                + LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.MINUTE) == 60 ? 0 : galena.get(Calendar.MINUTE)));
    }

    /**
     * convert date time to string with slash and :
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
     * @param dateTime
     * @return
     */
    public static Integer integerFormatFromStringDate(String dateTime) {
        if(dateTime.isEmpty()){
            return 0;
        }
        return Integer.valueOf(dateTime.replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0 , 8));
    }

    /**
     * convert integer date to date time type
     * @param integerTime
     * @return
     */
    public static DateTime convertIntegerToDateTime(Integer integerTime){
        int year = integerTime / 10000;
        int month  = (integerTime % 10000) / 100;
        int day = integerTime % 100;
        return  new DateTime(year, month, day, 1, 1);
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
}
