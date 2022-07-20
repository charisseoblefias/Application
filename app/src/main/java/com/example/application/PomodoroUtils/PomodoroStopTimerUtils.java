package com.example.application.PomodoroUtils;

import static com.example.application.PomodoroUtils.PomodoroConstants.COMPLETE_ACTION_BROADCAST;
import static com.example.application.PomodoroUtils.PomodoroConstants.POMODORO;
import static com.example.application.PomodoroUtils.PomodoroUtils.ringID;
import static com.example.application.PomodoroUtils.PomodoroUtils.soundPool;
import static com.example.application.PomodoroUtils.PomodoroUtils.updateCurrentlyRunningServiceType;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.floatRingingVolumeLevel;
import static com.example.application.pomodoroactivities.PomodoroActivity.currentlyRunningServiceType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.application.pomodorotimer.PomodoroCountDownTimerService;

import java.util.Date;

public class PomodoroStopTimerUtils {

    /**
     * Tasks executed when the timer Completes Ticking or is prematurely completed
     */
    public static void sessionComplete(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (currentlyRunningServiceType == POMODORO) {

            // Updates newWorkSessionCount in SharedPreferences and displays it on TextView.
            PomodoroUtils.updateWorkSessionCount(preferences, context);

            // Retrieves type of break user should take, either SHORT_BREAK or
            // LONG_BREAK, and updates value of currentlyRunningService in SharedPreferences.
            currentlyRunningServiceType = PomodoroUtils.getTypeOfBreak(preferences, context);
            PomodoroUtils.updateCurrentlyRunningServiceType(preferences, context,
                    currentlyRunningServiceType);

            stopTimer(context);
            soundPool.play(ringID, floatRingingVolumeLevel, floatRingingVolumeLevel, 2, 0,
                    1f);
            sendBroadcast(context);
        }
        Date date = new Date(System.currentTimeMillis()); //or simply new Date();
        long millis = date.getTime();
        preferences.edit().putInt("pause", (int) millis / 1000).apply();
    }


    public static void sessionCancel(Context context, SharedPreferences preferences) {
        updateCurrentlyRunningServiceType(preferences, context, POMODORO);
        stopTimer(context);
        sendBroadcast(context);
    }


    private static void sendBroadcast(Context context) {
        LocalBroadcastManager completedBroadcastManager = LocalBroadcastManager.getInstance(context);
        completedBroadcastManager.sendBroadcast(
                new Intent(COMPLETE_ACTION_BROADCAST));
    }

    private static void stopTimer(Context context) {
        Intent serviceIntent = new Intent(context, PomodoroCountDownTimerService.class);
        context.stopService(serviceIntent);
    }
}
