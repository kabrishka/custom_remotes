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
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;

import com.trueconf.circleview.R;
import com.trueconf.circleview.utils.GuiHelper;

public class SoundView extends View {

    private static final int BACKGROUND_COLOR = Color.parseColor("#292929");
    private static final int STROKE_COLOR = 0xDEFFFFFF; // 87%
    private static final int SELECTED_COLOR = Color.parseColor("#565656");
    private static final int ICON_COLOR = 0x8AFFFFFF; // 54%
    float width, height, radius;
    float left = 0f;
    float top = 0f;
    float padding = 1f; // отступ в 1px для того, чтобы не обрезалось по краям

    private float strokeWidth;
    private Paint paint;
    private Bitmap icZoomIn, icZoomOut;

    private Sector selectedSector = Sector.UNKNOWN;

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

        strokeWidth = GuiHelper.convertDpToPx(getResources(), 0.5f);

        icZoomIn = getBitmapFromDrawable(getDrawableFromRes(R.drawable.ic_add));
        icZoomOut = getBitmapFromDrawable(getDrawableFromRes(R.drawable.ic_minus));
    }

    private Drawable getDrawableFromRes(int resId) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), resId);

        if (drawable != null) {
            drawable.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    ICON_COLOR,
                    BlendModeCompat.SRC_IN
            ));
        }
        return drawable;
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        width = getWidth();
        height = getHeight();
        radius = GuiHelper.convertDpToPx(getResources(), 24f);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.FILL);
        // Рисуем заполненный прямоугольник
        drawOuterRect(canvas, left, padding, top, width, height, radius);

        // Верхняя кнопка
        drawTopBtn(canvas, left, padding, width, height, radius);

        // Нижняя кнопка
        drawBottomBtn(canvas, left, padding, height, width, radius);

        // Внешние границы
        drawOuterRectBound(canvas, left, padding, top, width, height, radius);

        // Рисуем + и -
        drawIcons(canvas, width, height);
    }

    private void drawOuterRect(@NonNull Canvas canvas, float left, float padding, float top, float width, float height, float radius) {
        paint.setColor(BACKGROUND_COLOR);
        canvas.drawRoundRect(left + padding, top + padding, width - padding, height - padding, radius, radius, paint);
        canvas.drawLine(left + padding, height / 2, width - padding, height / 2, paint);
    }

    private void drawTopBtn(@NonNull Canvas canvas, float left, float padding, float width, float height, float radius) {
        paint.setColor(selectedSector == Sector.TOP ? SELECTED_COLOR : BACKGROUND_COLOR);
        // Верхняя часть со скругленными углами
        canvas.drawRoundRect(left + padding, 0f, width - padding, height / 2, radius, radius, paint);
        // Нижняя часть, частично поверх верхней
        canvas.drawRect(left + padding, radius, width - padding, height / 2f, paint);
    }

    private void drawBottomBtn(@NonNull Canvas canvas, float left, float padding, float height, float width, float radius) {
        paint.setColor(selectedSector == Sector.BOTTOM ? SELECTED_COLOR : BACKGROUND_COLOR);
        // Верхняя часть со скругленными углами
        canvas.drawRoundRect(left + padding, height / 2, width - padding, height - padding, radius, radius, paint);
        // Нижняя часть, частично поверх верхней
        canvas.drawRect(left + padding, height / 2, width - padding, height - radius, paint);
    }

    private void drawOuterRectBound(@NonNull Canvas canvas, float left, float padding, float top, float width, float height, float radius) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(STROKE_COLOR);
        paint.setStrokeWidth(GuiHelper.convertDpToPx(getResources(), strokeWidth));
        canvas.drawRoundRect(left + padding, top + padding, width - padding, height - padding, radius, radius, paint);
        // разделитель кнопок
        canvas.drawLine(left + padding, height / 2, width - padding, height / 2, paint);
    }

    private void drawIcons(@NonNull Canvas canvas, float width, float height) {
        float dy = GuiHelper.convertDpToPx(getResources(), 11f); // 11dp расстояние до границы
        canvas.drawBitmap(icZoomIn, width / 2 - icZoomIn.getWidth() / 2f, dy, new Paint());
        canvas.drawBitmap(icZoomOut, width / 2 - icZoomIn.getWidth() / 2f, height - dy - icZoomOut.getHeight(), new Paint());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float centerLine = getHeight() / 2f;

        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                selectedSector = y < centerLine ? Sector.TOP : Sector.BOTTOM;
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        paint = null;
        icZoomIn = null;
        icZoomOut = null;
    }
}
