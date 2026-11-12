AirBKK Auto Rotate - Android Studio source project (NOT an APK)

1. Open this folder in Android Studio (with Android SDK 35 and JDK 17).
2. Build > Build APK(s), or run Gradle task assembleDebug.
3. Install app/build/outputs/apk/debug/app-debug.apk:
   adb install -r app-debug.apk
4. Grant write settings:
   adb shell appops set com.airbkk.autorotate WRITE_SETTINGS allow
5. Enable accessibility service on TV via Accessibility settings.
   If TV UI does not expose the service, use ADB carefully. Do NOT overwrite
   enabled_accessibility_services if other accessibility services are enabled.
6. Open AirBKK and then Home to test rotation 3 and 0.
7. Reboot TV and retest. Accessibility services normally reconnect after boot.

Notes:
- This is an untested prototype, not a verified Android TV 14 build.
- Some TV firmware ignores app-written rotation settings.
- Accessibility event filtering may need tuning for Xiaomi's launcher.
- ADB appops grant and enabling Accessibility are initial setup steps.
- This prototype does not require a foreground service or PC running continuously.
- To reset rotation: adb shell settings put system user_rotation 0
