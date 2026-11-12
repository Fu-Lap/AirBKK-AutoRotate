package com.airbkk.autorotate;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("AirBKK Auto Rotate\n\n1. Enable AirBKK rotation detector in Accessibility settings.\n2. Allow Modify system settings for this app (or grant via ADB).\n\nAirBKK = rotation 3; other apps = rotation 0.\n\nThe service starts automatically when Android reconnects enabled accessibility services after boot.");
        view.setTextSize(19); view.setPadding(28,28,28,28);
        setContentView(view);
        if (!Settings.System.canWrite(this)) {
            try { startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:" + getPackageName()))); } catch (Exception ignored) {}
        }
    }
}
