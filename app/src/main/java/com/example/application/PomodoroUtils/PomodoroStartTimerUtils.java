package com.example.application.PomodoroUtils;

import static com.example.application.PomodoroUtils.PomodoroConstants.RINGING_VOLUME_LEVEL_KEY;
import static com.example.application.PomodoroUtils.PomodoroConstants.START_ACTION_BROADCAST;
import static com.example.application.PomodoroUtils.PomodoroConstants.TICKING_VOLUME_LEVEL_KEY;
import static com.example.application.PomodoroUtils.PomodoroConstants.TIME_INTERVAL;
import static com.example.application.PomodoroUtils.PomodoroUtils.prepareSoundPool;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.convertToFloat;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.floatRingingVolumeLevel;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.floatTickingVolumeLevel;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.maxVolume;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.application.pomodorotimer.PomodoroCountDownTimerService;

public class PomodoroStartTimerUtils {

    public static void startTimer(long duration, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        prepareSoundPool(context); //Prepare SoundPool
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); // -1 just because otherwise converttofloat returns infinity
        floatTickingVolumeLevel = convertToFloat(preferences
                .getInt(TICKING_VOLUME_LEVEL_KEY, maxVolume - 1), maxVolume); //set ticking volume
        floatRingingVolumeLevel = convertToFloat(preferences
                .getInt(RINGING_VOLUME_LEVEL_KEY, maxVolume - 1), maxVolume); //also set ringing volume
        Intent serviceIntent = new Intent(context, PomodoroCountDownTimerService.class);
        serviceIntent.putExtra("time_period", duration);
        serviceIntent.putExtra("time_interval", TIME_INTERVAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(serviceIntent);
        else
            context.startService(serviceIntent);

        sendBroadcast(context);
    }

    private static void sendBroadcast(Context context) {
        LocalBroadcastManager completedBroadcastManager = LocalBroadcastManager.getInstance(context);
        completedBroadcastManager.sendBroadcast(
                new Intent(START_ACTION_BROADCAST));
    }
}
