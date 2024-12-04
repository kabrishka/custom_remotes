package com.trueconf.circleview.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class GuiHelper {
    public static float convertDpToPx(Resources resources, float dip) {
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                resources.getDisplayMetrics()
        );
        return px;
    }
}
