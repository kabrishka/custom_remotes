package com.trueconf.circleview.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;

import com.trueconf.circleview.view.Sector;

public class GuiHelper {
    public static float convertDpToPx(Resources resources, float dip) {
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                resources.getDisplayMetrics()
        );
        return px;
    }

    public static int getSectorColor(Sector sector) {
        String color;
        switch (sector) {
            case RIGHT:
                color = "#FF9A9F";
                break;
            case BOTTOM:
                color = "#998CEB";
                break;
            case LEFT:
                color = "#9EF5FF";
                break;
            case TOP:
                color = "#BAE8AC";
                break;
            case RESET:
                color = "#E9D787";
                break;
            default:
                color = "#5281CE";
                break;
        }
        return Color.parseColor(color);
    }
}
