package com.trueconf.circleview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.trueconf.circleview.R;
import com.trueconf.circleview.utils.GuiHelper;

public class Custom45CircleView extends View {
    private static final int STROKE_COLOR = 0xDEFFFFFF; // 87%
    private static final int INNER_STROKE_COLOR = 0x61FFFFFF; // 38%

    private Paint paint;
    private Bitmap icReset, icArrow;

    float centerX;
    float centerY;
    float radius;
    float innerRadius;

    private float strokeWidth; // 1dp
    private float sectorStrokeWidth; // 0.6dp
    private float dr; // расстояние между внутренней и внешней границами окружностей (т.е. R - r)
    private float arrowIndent; // отступ от границы для иконок стрелок

    private final int sectorsCount = 4;
    private Sector selectedSector = Sector.UNKNOWN;
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        radius = Math.min(getWidth(), getHeight()) / 2f;
        innerRadius = Math.min(getWidth(), getHeight()) / 2f - dr;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Рисуем закрашенную внешнюю окружность
        drawFilledCircle(canvas, radius);
        // Рисуем границы внешней окружности
        drawCircleBound(STROKE_COLOR, canvas, radius);
        drawCircleButtons(canvas);
        // Рисуем закрашенную внутреннюю окружность
        drawFilledCircle(canvas, innerRadius);
        // Рисуем изображение
        drawResetImage(canvas);
        // Рисуем границы внутренней окружности
        drawCircleBound(INNER_STROKE_COLOR, canvas, innerRadius);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paint = null;
        icReset = null;
        icArrow = null;
    }

    private void init() {
        paint = new Paint();

        strokeWidth = GuiHelper.convertDpToPx(getResources(), 1f);
        sectorStrokeWidth = GuiHelper.convertDpToPx(getResources(), 0.6f);

        dr = GuiHelper.convertDpToPx(getResources(), 50f);
        arrowIndent = GuiHelper.convertDpToPx(getResources(), 8f); // 8dp

        // Загружаем изображение из ресурсов
        icReset = getBitmapFromDrawable(getDrawableFromRes(R.drawable.ic_reset));
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

    private void drawCircleButtons(Canvas canvas) {
        for (int i = 0; i < sectorsCount; i++) {
            Sector sector = Sector.FromInt(i);
            // Рисуем границы сектора
            drawSectorBound(canvas, i);
            // Рисуем выделенную область, если есть
            drawSelectedSector(canvas, i);
            // Рисуем стрелочки
            drawArrowImage(canvas, sector, radius);
        }
    }

    private void drawFilledCircle(Canvas canvas, float radius) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(selectedSector == Sector.RESET ? Color.LTGRAY : GuiHelper.getSectorColor(Sector.RESET));
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private void drawCircleBound(int INNER_STROKE_COLOR, Canvas canvas, float radius) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(INNER_STROKE_COLOR);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private void drawSectorBound(Canvas canvas, int i) {
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
    }

    private void drawSelectedSector(Canvas canvas, int i) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(i == selectedSector.ToInt() ? Color.LTGRAY : GuiHelper.getSectorColor(Sector.FromInt(i)));
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

    private void drawResetImage(Canvas canvas) {
        float left = centerX - icReset.getWidth() / 2f;
        float top = centerY - icReset.getHeight() / 2f;
        canvas.drawBitmap(icReset, left, top, new Paint());
    }

    private void drawArrowImage(Canvas canvas, Sector sector, float radius) {
        float width = icArrow.getWidth();
        float height = icArrow.getHeight();

        float left = 0;
        float top = 0;
        float rotation = (sector.ToInt() + 1) * 90f;

        switch (sector) {
            case RIGHT:
                // правый сектор
                left = centerX + radius - arrowIndent - width;
                top = centerY - icArrow.getHeight() / 2f;
                break;
            case BOTTOM:
                // нижний сектор
                left = centerX - icArrow.getWidth() / 2f;
                top = centerY + radius - arrowIndent - height;
                break;
            case LEFT:
                // левый сектор
                left = centerX - radius + arrowIndent;
                top = centerY - icArrow.getHeight() / 2f;
                break;
            case TOP:
                // верхний сектор
                left = centerX - icArrow.getWidth() / 2f;
                top = centerY - radius + arrowIndent;
                break;
        }

        float imgCenterX = left + width / 2f;
        float imgCenterY = top + height / 2f;

        canvas.save();
        canvas.rotate(rotation, imgCenterX, imgCenterY);
        canvas.drawBitmap(icArrow, left, top, new Paint());
        canvas.restore();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                    selectedSector = Sector.RESET;
                } else {
                    // Проверка, в каком секторе произошёл клик
                    double atan2 = Math.atan2(y - centerY, x - centerX);
                    // Необходимо учесть смещение на 45 градусов
                    float angle = (float) Math.toDegrees(atan2) + offset; // Math.toDegrees(atan2) in [-PI;PI]
                    float correctAngle = (angle + 360) % 360;

                    // угол одного элемента
                    double oneSectorAngle = (double) 360 / sectorsCount;

                    // вычисляем позицию
                    int sector = (int) (correctAngle / oneSectorAngle);
                    selectedSector = Sector.FromInt(sector);
                }
                sendControlCommand(selectedSector);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                selectedSector = Sector.UNKNOWN;
                invalidate();
                break;
        }
        return true;
    }

    private void sendControlCommand(Sector sector) {
        Toast.makeText(getContext(), "Selected sector: " + sector, Toast.LENGTH_SHORT).show();
    }
}