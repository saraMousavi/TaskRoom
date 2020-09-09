package ir.android.persiantask.utils.calender;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ViewAnimator;

public class AccessibleDateAnimator extends ViewAnimator {
    private long mDateMillis;

    public AccessibleDateAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDateMillis(long dateMillis) {
        mDateMillis = dateMillis;
    }

    /**
     * Announce the currently-selected date when launched.
     */
    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // Clear the event's current text so that only the current date will be spoken.
            event.getText().clear();
            PersianCalendar mPersianCalendar = new PersianCalendar();
            mPersianCalendar.setTimeInMillis(mDateMillis);
            String dateString = LanguageUtils.getPersianNumbers(
                    mPersianCalendar.getPersianMonthName() + " " +
                            mPersianCalendar.getPersianYear()
            );
            event.getText().add(dateString);
            return true;
        }
        return super.dispatchPopulateAccessibilityEvent(event);
    }
}
