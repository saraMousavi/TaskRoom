package ir.android.persiantask.utils.calender;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import java.util.Locale;

public class SimpleMonthView extends MonthView {
    DatePickerController controller;

    public SimpleMonthView(Context context, AttributeSet attr, DatePickerController controller) {
        super(context, attr, controller);
        this.controller = controller;
    }

    @Override
    public void drawMonthDay(Canvas canvas, int year, int month, int day,
                             int x, int y, int startX, int stopX, int startY, int stopY) {
        if (mSelectedDay == day) {
            canvas.drawCircle(x, y - (MINI_DAY_NUMBER_TEXT_SIZE / 3), DAY_SELECTED_CIRCLE_SIZE,
                    mSelectedCirclePaint);
        }
        if (isHighlighted(year, month, day)) {
            Typeface typefaceBold = Typeface.create(TypefaceHelper.get(getContext(), controller.getTypeface()), Typeface.BOLD);
            mMonthNumPaint.setTypeface(typefaceBold);
        } else {
            Typeface typefaceNormal = Typeface.create(TypefaceHelper.get(getContext(), controller.getTypeface()), Typeface.NORMAL);
            mMonthNumPaint.setTypeface(typefaceNormal);
        }

        // If we have a mindate or maxdate, gray out the day number if it's outside the range.
        if (isOutOfRange(year, month, day)) {
            mMonthNumPaint.setColor(mDisabledDayTextColor);
        } else if (mSelectedDay == day) {
            mMonthNumPaint.setColor(mSelectedDayTextColor);
        } else if (mHasToday && mToday == day) {
            mMonthNumPaint.setColor(mTodayNumberColor);
        } else {
            mMonthNumPaint.setColor(isHighlighted(year, month, day) ? mHighlightedDayTextColor : mDayTextColor);
        }

        canvas.drawText(LanguageUtils.
                getPersianNumbers(String.format(Locale.getDefault(), "%d", day)), x, y, mMonthNumPaint);
    }
}
