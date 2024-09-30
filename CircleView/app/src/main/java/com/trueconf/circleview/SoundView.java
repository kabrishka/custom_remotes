package com.trueconf.circleview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class SoundView extends View {

    private final int BACKGROUND_COLOR = Color.parseColor("#292929");
    private final int STROKE_COLOR = 0xDEFFFFFF; // 87%
    private final int SELECTED_COLOR = Color.parseColor("#565656");
    private Paint paint;
    private Bitmap icPlus, icMinus;

    private int clickedSector = -1;

    public SoundView(Context context) {
        super(context);
        init();
    }

    public SoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();

        icPlus = getBitmapFromDrawable(getDrawableFromRes(R.drawable.ic_add));
        icMinus = getBitmapFromDrawable(getDrawableFromRes(R.drawable.ic_minus));
    }

    private Drawable getDrawableFromRes(int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap icon = null;
        if (drawable instanceof BitmapDrawable) {
            icon = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable != null) {
            icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(icon);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return icon;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = convertDpToPx(24f);

        float left = 0f;
        float top = 0f;
        float padding = 1f; // отступ в 1px для того, чтобы не обрезалось по краям

        paint.setStyle(Paint.Style.FILL);
        // Рисуепм заполненный прямоугольник
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawRoundRect(left + padding, top + padding, width - padding, height - padding, radius, radius, paint);
        canvas.drawLine(left + padding, height / 2, width - padding, height / 2, paint);

        // Верхняя кнопка
        paint.setColor(clickedSector == 1 ? SELECTED_COLOR : BACKGROUND_COLOR);
        // Draw the top part that has rounded corners with twice the height of the radius
        canvas.drawRoundRect(left + padding, 0f, width - padding, height / 2, radius, radius, paint);
        // Draw the bottom part, partly on top of the top part
        canvas.drawRect(left + padding, radius, width - padding, height / 2f, paint);

        // Нижняя кнопка
        paint.setColor(clickedSector == 2 ? SELECTED_COLOR : BACKGROUND_COLOR);
        // Draw the top part that has rounded corners with twice the height of the radius
        canvas.drawRoundRect(left + padding, height / 2, width - padding, height - padding, radius, radius, paint);
        // Draw the bottom part, partly on top of the top part
        canvas.drawRect(left + padding, height / 2, width - padding, height - radius, paint);

        // Границы
        float strokeWidth = 1f; // 1dp

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(STROKE_COLOR);
        paint.setStrokeWidth(convertDpToPx(strokeWidth));
        canvas.drawRoundRect(left + padding, top + padding, width - padding, height - padding, radius, radius, paint);
        canvas.drawLine(left + padding, height / 2, width - padding, height / 2, paint);

        float dy = convertDpToPx(11f); // 11dp расстояние до границы
        // Рисуем + и -
        canvas.drawBitmap(icPlus, width / 2 - icPlus.getWidth() / 2f, dy, new Paint());
        canvas.drawBitmap(icMinus, width / 2 - icPlus.getWidth() / 2f, height - dy - icMinus.getHeight(), new Paint());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float centerLine = getHeight() / 2;

        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (y < centerLine) {
                    clickedSector = 1;
                } else if (y > centerLine) {
                    clickedSector = 2;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                clickedSector = -1;
                invalidate();
                break;
        }

        return true;
    }

    private float convertDpToPx(float dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return px;
    }
}
