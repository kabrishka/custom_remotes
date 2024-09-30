package com.trueconf.circleview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

public class Custom45CircleView extends View {

    private final int HOME = 100;

    private Bitmap icReset, icArrow;

    private final int BACKGROUND_COLOR = Color.parseColor("#292929");
    private final int STROKE_COLOR = 0xDEFFFFFF; // 87%
    private final int INNER_STROKE_COLOR = 0x61FFFFFF; // 38%
    private final int SELECTED_COLOR = 0x3DFFFFFF; // 24%
    private final int SELECTED_COLOR_WITHOUT_TRANSPARENT = Color.parseColor("#565656");

    private float strokeWidth; // 1dp
    private float sectorStrokeWidth; // 0.6dp

    private float dr; // расстояние между внутренней и внешней границами
    private float indent; // отступ о границы для иконки

    private Paint paint;
    private int clickedSector = -1;
    private final int sectorsCount = 4;

    private final int offset = 45;
    private final float startAngle = (-1) * offset;
    private final float sweepAngle = 360f / sectorsCount;

    public Custom45CircleView(Context context) {
        super(context);
        init();
    }

    public Custom45CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Custom45CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();

        strokeWidth = convertDpToPx(1f);
        sectorStrokeWidth = convertDpToPx(0.6f);

        dr = convertDpToPx(50f);

        // Загружаем изображение из ресурсов
        icReset = getBitmapFromDrawable(getDrawableFromRes(R.drawable.ic_reset_ptz));
        icArrow = getBitmapFromDrawable(getDrawableFromRes(R.drawable.ic_arrow));
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleButtons(canvas);
    }

    private void drawCircleButtons(Canvas canvas) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 2f;
        float innerRadius = Math.min(getWidth(), getHeight()) / 2f - dr;

        // Рисуем внешнюю окружность
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Рисуем границы внешней окружности
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(STROKE_COLOR);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(centerX, centerY, radius, paint);


        for (int i = 0; i < sectorsCount; i++) {
            // Рисуем границы сектора
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(STROKE_COLOR);
            paint.setStrokeWidth(sectorStrokeWidth);
            canvas.drawArc(
                    centerX - radius,
                    centerY - radius,
                    centerX + radius,
                    centerY + radius,
                    startAngle + i * sweepAngle,  // начальный угол
                    sweepAngle, // на сколько градусов хотим нарисовать
                    true,
                    paint
            );

            // Рисуем выделенную область, если есть
            if (i == clickedSector) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(SELECTED_COLOR);
                canvas.drawArc(
                        centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius,
                        startAngle + i * sweepAngle,  // начальный угол
                        sweepAngle, // на сколько градусов хотим нарисовать
                        true,
                        paint
                );
            }

            // Рисуем стрелочки
            drawArrowImage(canvas, i, centerX, radius, centerY);


        }

        // Рисуем внутреннюю окружность
        paint.setStyle(Paint.Style.FILL);
        // добавляем цвет, который 1 в 1 как #FFFFFF с прозрачностью 24, но прозрачный цвет показывает внутренний крест
        paint.setColor(clickedSector == HOME ? SELECTED_COLOR_WITHOUT_TRANSPARENT : BACKGROUND_COLOR);
        canvas.drawCircle(centerX, centerY, innerRadius, paint);

        // Рисуем изображение
        float left = centerX - icReset.getWidth() / 2f;
        float top = centerY - icReset.getHeight() / 2f;
        canvas.drawBitmap(icReset, left, top, new Paint());

        // Рисуем границы внутренней окружности
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(INNER_STROKE_COLOR);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(centerX, centerY, innerRadius, paint);
    }

    private void drawArrowImage(Canvas canvas, int sector, float centerX, float radius, float centerY) {
        float width = icArrow.getWidth();
        float height = icArrow.getHeight();

        float left = 0;
        float top = 0;
        float rotation = (sector + 1) * 90f;

        switch (sector) {
            case 0:
                // правый сектор
                left = centerX + radius - indent - width;
                top = centerY - icArrow.getHeight() / 2f;
                break;
            case 1:
                // нижний сектор
                left = centerX - icArrow.getWidth() / 2f;
                top = centerY + radius - indent - height;
                break;
            case 2:
                // левый сектор
                left = centerX - radius + indent;
                top = centerY - icArrow.getHeight() / 2f;
                break;
            case 3:
                // верхний сектор
                left = centerX - icArrow.getWidth() / 2f;
                top = centerY - radius + indent;
                break;
        }

        float imgCenterX = left + width / 2f;
        float imgCenterY = top + height / 2f;

        canvas.save();
        canvas.rotate(rotation, imgCenterX, imgCenterY);
        canvas.drawBitmap(icArrow, left, top, new Paint());
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float innerRadius = Math.min(getWidth(), getHeight()) / 2f - dr;
        float leftX = centerX - innerRadius; // левая граница по X внутреннего круга
        float rightX = centerX + innerRadius; // правая граница по X внутреннего круга
        float bottomY = centerY - innerRadius; // нижняя граница по Y внутреннего круга
        float topY = centerY + innerRadius; // верхняя граница по Y внутреннего круга

        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (x >= leftX && x <= rightX && y >= bottomY && y <= topY) {
                    clickedSector = HOME;
                } else {
                    // Проверка, в каком секторе произошёл клик
                    double atan2 = Math.atan2(y - centerY, x - centerX);
                    // Необходимо учесть смещение на 45 градусов
                    float angle = (float) Math.toDegrees(atan2) + offset; // -PI ti PI
                    float correctAngle = (angle + 360) % 360;

                    // угол одного элемента
                    double oneSectorAngle = (double) 360 / sectorsCount;

                    // вычисляем позицию
                    clickedSector = (int) (correctAngle / oneSectorAngle);
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
    public void onSectorClick(int sector) {
        // Типа клик
        clickedSector = sector;
        invalidate();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            clickedSector = -1;
            invalidate();
        }, 100);
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