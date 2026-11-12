package com.airbkk.autorotate;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.provider.Settings;
import android.os.Handler;
import android.os.Looper;

public class RotateAccessibilityService extends AccessibilityService {
    private static final String TARGET = "com.airbkk";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pending;
    private int lastApplied = -1;

    @Override protected void onServiceConnected() {
        super.onServiceConnected();
        // Do not infer foreground app on startup; wait for a window transition.
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;
        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            type != AccessibilityEvent.TYPE_WINDOWS_CHANGED) return;
        String pkg = event.getPackageName().toString();
        // Ignore system overlay windows to avoid toggling while AirBKK is still foreground.
        if (pkg.equals("android") || pkg.equals(getPackageName()) ||
            pkg.equals("com.android.systemui") || pkg.equals("com.google.android.gms")) return;
        int desired = TARGET.equals(pkg) ? 3 : 0;
        if (pending != null) handler.removeCallbacks(pending);
        pending = () -> applyRotation(desired);
        handler.postDelayed(pending, 650);
    }

    private void applyRotation(int rotation) {
        if (lastApplied == rotation) return;
        if (!Settings.System.canWrite(this)) return;
        try {
            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
            Settings.System.putInt(getContentResolver(), Settings.System.USER_ROTATION, rotation);
            lastApplied = rotation;
        } catch (SecurityException ignored) {}
    }

    @Override public void onInterrupt() {}
    @Override public void onDestroy() {
        if (pending != null) handler.removeCallbacks(pending);
        super.onDestroy();
    }
}
