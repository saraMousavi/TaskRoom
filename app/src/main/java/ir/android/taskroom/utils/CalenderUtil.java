package ir.android.taskroom.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;

import com.mohamadian.persianhorizontalexpcalendar.PersianHorizontalExpCalendar;
import com.mohamadian.persianhorizontalexpcalendar.enums.PersianCustomMarks;
import com.mohamadian.persianhorizontalexpcalendar.model.CustomGradientDrawable;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.PersianChronologyKhayyam;

public class CalenderUtil {
    private PersianHorizontalExpCalendar persianHorizontalExpCalendar;
    public CalenderUtil(PersianHorizontalExpCalendar persianHorizontalExpCalendar) {
        this.persianHorizontalExpCalendar = persianHorizontalExpCalendar;
    }

    public void cutomMarkTodaySelectedDay(){
        persianHorizontalExpCalendar
                .setMarkTodayCustomGradientDrawable(new CustomGradientDrawable(GradientDrawable.OVAL, new int[] {Color.parseColor("#55fefcea"), Color.parseColor("#55f1da36"), Color.parseColor("#55fefcea")})
                        .setstroke(2,Color.parseColor("#EFCF00"))
                        .setTextColor(Color.parseColor("#E88C02")))

                .setMarkSelectedDateCustomGradientDrawable(new CustomGradientDrawable(GradientDrawable.OVAL, new int[] {Color.parseColor("#55f3e2c7"), Color.parseColor("#55b68d4c"), Color.parseColor("#55e9d4b3")})
                        .setstroke(2,Color.parseColor("#E89314"))
                        .setTextColor(Color.parseColor("#E88C02")))
                .updateMarks();
    }

    public void markSomeDays(){
        Chronology perChr = PersianChronologyKhayyam.getInstance(DateTimeZone.forID("Asia/Tehran"));
        DateTime dt = new DateTime(1396,12,1,0,0,0,0,perChr);
        persianHorizontalExpCalendar
                .scrollToDate(dt);
        this.persianHorizontalExpCalendar
                .markDate(new DateTime(perChr).plusDays(7),
                        new CustomGradientDrawable(GradientDrawable.RECTANGLE, Color.BLACK)
                                .setViewLayoutSize(ViewGroup.LayoutParams.MATCH_PARENT, 10)
                                .setViewLayoutGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM)
                                .setcornerRadius(5)
                                .setTextColor(Color.BLUE))

                .markDate(new DateTime(perChr).plusDays(10),
                        new CustomGradientDrawable(GradientDrawable.OVAL, Color.BLACK)
                                .setViewLayoutSize(20, 20)
                                .setViewLayoutGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT)
                                .setTextColor(Color.BLUE))

                .markDate(new DateTime(1396, 8, 7, 0, 0, perChr), PersianCustomMarks.VerticalLine_Right, Color.parseColor("#b4e391"))

                .markDate(new DateTime(1396, 8, 5, 0, 0, perChr),
                        new CustomGradientDrawable(GradientDrawable.OVAL, new int[]{Color.parseColor("#35b4e391"), Color.parseColor("#5561c419"), Color.parseColor("#35b4e391")})
                                .setstroke(1, Color.parseColor("#62E200"))
                                .setcornerRadius(20)
                                .setTextColor(Color.parseColor("#000000")))

                .markDate(new DateTime(1396, 8, 15, 0, 0, perChr),
                        new CustomGradientDrawable(GradientDrawable.OVAL, Color.parseColor("#35a677bd"))
                                .setstroke(1, Color.parseColor("#a677bd")))

                .markDate(new DateTime(1396, 8, 23, 0, 0, perChr), PersianCustomMarks.SmallOval_Bottom, Color.GREEN)
                .markDate(new DateTime(perChr).plusDays(14), PersianCustomMarks.SmallOval_Bottom)
                .markDate(new DateTime(perChr).plusDays(15), PersianCustomMarks.VerticalLine_Right)
                .updateMarks();
    }
}
