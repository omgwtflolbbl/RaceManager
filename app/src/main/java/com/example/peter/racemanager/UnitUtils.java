package com.example.peter.racemanager;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by peterlee on 8/4/16.
 */
public class UnitUtils {
    
    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }
}
