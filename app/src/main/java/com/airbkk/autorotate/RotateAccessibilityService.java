
package com.airbkk.autorotate;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.provider.Settings;
import android.os.Handler;
import android.os.Looper;

public class RotateAccessibilityService extends AccessibilityService {
    private static final String TARGET = "com.airbkk";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pending;
    private int lastApplied = -1;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        handler.postDelayed(this::checkForeground, 1000);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;

        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            type != AccessibilityEvent.TYPE_WINDOWS_CHANGED) return;

        String pkg = event.getPackageName().toString();

        if (pkg.equals(getPackageName()) ||
            pkg.equals("android") ||
            pkg.equals("com.android.systemui") ||
            pkg.equals("com.google.android.gms")) return;

        if (pending != null) handler.removeCallbacks(pending);

        if (TARGET.equals(pkg)) {
            applyRotation(3);
        } else {
            pending = this::checkForeground;
            handler.postDelayed(pending, 1500);
        }
    }

    private void checkForeground() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null || root.getPackageName() == null) return;

        String pkg = root.getPackageName().toString();

        if (pkg.equals(getPackageName()) ||
            pkg.equals("android") ||
            pkg.equals("com.android.systemui")) return;

        applyRotation(TARGET.equals(pkg) ? 3 : 0);
    }

    private void applyRotation(int rotation) {
        if (!Settings.System.canWrite(this)) return;

        try {
            int current = Settings.System.getInt(
                getContentResolver(),
                Settings.System.USER_ROTATION, -1
            );

            if (current == rotation && lastApplied == rotation) return;

            Settings.System.putInt(
                getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0
            );

            Settings.System.putInt(
                getContentResolver(),
                Settings.System.USER_ROTATION, rotation
            );

            lastApplied = rotation;
        } catch (SecurityException ignored) {}
    }

    @Override
    public void onInterrupt() {}

    @Override
    public void onDestroy() {
        if (pending != null) handler.removeCallbacks(pending);
        super.onDestroy();
    }
}
