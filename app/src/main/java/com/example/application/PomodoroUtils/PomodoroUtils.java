package com.example.application.PomodoroUtils;

import static com.example.application.PomodoroUtils.PomodoroConstants.LONG_BREAK;
import static com.example.application.PomodoroUtils.PomodoroConstants.POMODORO;
import static com.example.application.PomodoroUtils.PomodoroConstants.SHORT_BREAK;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;

import com.example.application.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PomodoroUtils {
    public static SoundPool soundPool;
    public static int tickID, ringID;

    public static int updateWorkSessionCount(SharedPreferences preferences, Context context) {
        int oldWorkSessionCount = preferences.getInt(context.getString(R.string.work_session_count_key), 0);
        int taskOnHandCount = preferences.getInt(context.getString(R.string.task_on_hand_count_key), 0);

        int newWorkSessionCount = ++oldWorkSessionCount;
        int newtaskonhandcount = ++taskOnHandCount;

        // Writing value of workSessionCount after a session is completed (New value of workSessionCount) in SharedPreference.
        preferences
                .edit()
                .putInt(context.getString(R.string.task_on_hand_count_key), newtaskonhandcount)
                .putInt(context.getString(R.string.work_session_count_key), newWorkSessionCount)
                .apply();
        return newWorkSessionCount;
    }

    public static int getTypeOfBreak(SharedPreferences preferences, Context context) {
        int currentWorkSessionCount = preferences.getInt(context.getString(R.string.work_session_count_key), 0);
        int session = preferences.getInt(context.getString(R.string.start_long_break_after_key), 2);
        int longbreakintervalsession;
        switch (session) {
            case 0:
                longbreakintervalsession = 2;
                break;
            case 1:
                longbreakintervalsession = 3;
                break;
            case 2:
                longbreakintervalsession = 4;
                break;
            case 3:
                longbreakintervalsession = 5;
                break;
            case 4:
                longbreakintervalsession = 6;
                break;
            default:
                longbreakintervalsession = 4;
        }
        if (currentWorkSessionCount % longbreakintervalsession == 0)
            return LONG_BREAK;
        return SHORT_BREAK;
    }

    public static void updateCurrentlyRunningServiceType(SharedPreferences preferences, Context context, int currentlyRunningServiceType) {
        preferences
                .edit()
                .putInt(context.getString(R.string.currently_running_service_type_key), currentlyRunningServiceType)
                .apply();
    }

    public static int retrieveCurrentlyRunningServiceType(SharedPreferences preferences, Context context) {
        return preferences.getInt(context.getString(R.string.currently_running_service_type_key), 0);
    }

    public static long getCurrentDurationPreferenceOf(SharedPreferences preferences, Context context, int currentlyRunningServiceType) {
        if (currentlyRunningServiceType == POMODORO) {
            // Current value of work duration stored in shared-preference
            int currentWorkDurationPreference = preferences.getInt(context.getString(R.string.work_duration_key), 1);
            // Switch case to return appropriate minute value of work duration according value stored in shared-preference.
            switch (currentWorkDurationPreference) {
                case 0:
                    return 20 * 60000; // 20 minutes
                case 1:
                    return 25 * 60000; // 25 minutes
                case 2:
                    return 30 * 60000; // 30 minutes
                case 3:
                    return 40 * 60000; // 40 minutes
                case 4:
                    return 55 * 60000; // 55 minutes
            }
        } else if (currentlyRunningServiceType == SHORT_BREAK) {
            // Current value of short-break duration stored in shared-preference
            int currentShortBreakDurationPreference = preferences.getInt(context.getString(R.string.short_break_duration_key), 1);

            // Switch case to return appropriate minute value of short-break duration according value stored in shared-preference.
            switch (currentShortBreakDurationPreference) {
                case 0:
                    return 3 * 60000; // 3 minutes
                case 1:
                    return 5 * 60000; // 5 minutes
                case 2:
                    return 10 * 60000; // 10 minutes
                case 3:
                    return 15 * 60000; // 15 minutes
            }
        } else if (currentlyRunningServiceType == LONG_BREAK) {
            // Current value of long-break duration stored in shared-preference
            int currentLongBreakDurationPreference = preferences.getInt(context.getString(R.string.long_break_duration_key), 1);

            // Switch case to return appropriate minute value of long-break duration according value stored in shared-preference.
            switch (currentLongBreakDurationPreference) {
                case 0:
                    return 10 * 60000; // 10 minutes
                case 1:
                    return 15 * 60000; // 15 minutes
                case 2:
                    return 20 * 60000; // 20 minutes
                case 3:
                    return 25 * 60000; // 25 minutes
            }
        }
        return 0;
    }

    public static String getCurrentDurationPreferenceStringFor(long duration) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % 60,
                TimeUnit.MILLISECONDS.toSeconds(duration) % 60);
    }

    public static void prepareSoundPool(Context context) {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        tickID = soundPool.load(context, R.raw.clockticking, 2);
        ringID = soundPool.load(context, R.raw.bellringing, 2);
    }
}