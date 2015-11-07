package co.adrianblan.cheddar.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DesignUtils {

    public static float dpToPixels(float dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
