package com.example.smartalert;

import android.content.Context;
import android.content.res.Configuration;

public class ThemeUtils {
    public static boolean isDarkTheme(Context context) {
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
