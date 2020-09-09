package ir.android.persiantask.utils;

import android.widget.ImageView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import ir.android.persiantask.R;
import ir.android.persiantask.utils.calender.LanguageUtils;
import ir.android.persiantask.utils.calender.PersianCalendar;
import ir.android.persiantask.utils.enums.CategoryType;

public class Init {
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
}
