package ir.android.taskroom.utils.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.provider.AlarmClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import ir.android.taskroom.R;
import ir.android.taskroom.ui.activity.AlarmActivity;

public class Circle extends View {

    private static final int START_ANGLE_POINT =360;

    private final Paint paint;
    private final RectF rect;

    private float angle;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        final int strokeWidth = 30;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //Circle color
        paint.setColor(context.getResources().getColor(R.color.colorPrimary));
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //size 200x200 example
//        rect = new RectF(strokeWidth, strokeWidth, (displayMetrics.widthPixels - displayMetrics.densityDpi), (displayMetrics.widthPixels - displayMetrics.densityDpi));
        rect = new RectF(200, 200, displayMetrics.widthPixels/2, displayMetrics.widthPixels/2);

        //Initial Angle (optional, it can be zero)
        angle = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}