package ir.android.persiantask.utils;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
     * @param projectCategoryIcon
     * @param category_id
     * @param isWhite
     */
    public static void setProjectCategory(ImageView projectCategoryIcon, Integer category_id, boolean isWhite) {
        if(category_id.equals(CategoryType.ART.getValue())){
            if(isWhite){
                projectCategoryIcon.setImageResource(R.drawable.ic_white_art);
            } else {
                projectCategoryIcon.setImageResource(R.drawable.ic_black_art);
            }
        } else if(category_id.equals(CategoryType.SPORT.getValue())){
            if(isWhite) {
                projectCategoryIcon.setImageResource(R.drawable.ic_white_sports);
            } else {
                projectCategoryIcon.setImageResource(R.drawable.ic_black_sports);
            }
        } else if(category_id.equals(CategoryType.SCIENTIFIC.getValue())){
            if(isWhite){
                projectCategoryIcon.setImageResource(R.drawable.ic_white_scientific);
            } else {
                projectCategoryIcon.setImageResource(R.drawable.ic_black_scientific);
            }
        }
    }

    /**
     * current date with format slash and dot
     * @return
     */
    public static String getCurrentDate(){
        GregorianCalendar galena = new GregorianCalendar();
        PersianCalendar persianCalendar = new PersianCalendar();
        int value = galena.get(Calendar.HOUR) % 12;
        return persianCalendar.getPersianYear() + "/"
                + persianCalendar.getPersianMonth() + "/"
                + persianCalendar.getPersianDay()
                + " "
                + LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(),"%d", value==0?12:value))
                + ":"
                + LanguageUtils.getPersianNumbers(String.format(Locale.getDefault(), "%02d",
                galena.get(Calendar.MINUTE) == 60 ? 0 : galena.get(Calendar.MINUTE)));
    }

    /**
     * change the image resource of complete icon and toggle paint flag
     * @param title
     * @param isCompletedIcon
     * @param tasks_iscompleted
     */
    public static void toggleCompleteCircle(TextView title, ImageView isCompletedIcon, Integer tasks_iscompleted) {
        if(tasks_iscompleted == 1){
            isCompletedIcon.setImageResource(R.drawable.ic_radio_button_checked_green);
            title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            isCompletedIcon.setImageResource(R.drawable.ic_orange_circle);
            title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
