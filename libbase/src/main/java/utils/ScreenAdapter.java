package utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * @param
 * @param
 * @param // 是否是横屏
 */
public class ScreenAdapter {

    /** 设计稿标准 */
    private static final float width = 750f;
    private static final float high = 1334f;


    private static float textDensity = 0;
    private static float textScaledDensity = 0;


    /**
     * 今日头条的屏幕适配方案
     * 根据当前设备物理尺寸和分辨率去动态计算density、densityDpi、scaledDensity
     * 同时也解决了用户修改系统字体的情况
     * @param activity
     */
    public static void setCustomDensity(@NonNull Activity activity) {
        setCustomDensity(activity, false);
    }


    /**
     * @param activity
     * @param isLandscape 是否是横屏
     */
    public static void setCustomDensity(@NonNull final Activity activity, boolean isLandscape) {
        final Application application = activity.getApplication();
        final DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (textDensity == 0) {
            textDensity = displayMetrics.density;
            textScaledDensity = displayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration configuration) {
                    if (configuration != null && configuration.fontScale > 0) {
                        textScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        final float targetDensity;
        if (isLandscape) {//横屏
            targetDensity = displayMetrics.widthPixels / (high / 2); //当前UI标准750*1334
        } else {
            targetDensity = displayMetrics.widthPixels / (width / 2); //当前UI标准750*1334
        }


        final float targetScaledDensity = targetDensity * (textScaledDensity / textDensity);
        final int targetDpi = (int) (160 * targetDensity);

        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = targetDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDpi;
    }

}

