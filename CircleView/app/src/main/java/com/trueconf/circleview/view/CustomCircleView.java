package com.trueconf.circleview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class CustomCircleView extends View {

    private Paint paint;
    private RectF rectF;

    public CustomCircleView(Context context) {
        super(context);
        init();
    }

    public CustomCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        // Рисуем круг
        rectF.set(width / 2 - radius, height / 2 - radius,
                width / 2 + radius, height / 2 + radius);
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        // Рисуем сектора
        for (int i = 0; i < 4; i++) {
            paint.setColor(getSectorColor(i));
            canvas.drawArc(rectF, i * 90, 90, true, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            // Проверка, в каком секторе произошёл клик
            float angle = (float) Math.toDegrees(Math.atan2(y - getHeight() / 2, x - getWidth() / 2));
            if (angle < 0) {
                angle += 360;
            }

            int sector = (int) (angle / 90) + 1;
            Toast.makeText(getContext(), "Сектор: " + sector, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onTouchEvent(event);
    }

    // Метод для получения цвета сектора
    private int getSectorColor(int sector) {
        switch (sector) {
            case 1:
                return 0xFF00FF00; // Зеленый
            case 2:
                return 0xFFFF0000; // Красный
            case 3:
                return 0xFF0000FF; // Синий
            case 4:
                return 0xFFFFFF00; // Жёлтый
            default:
                return 0xFFFFFFFF; // Белый
        }
    }
}