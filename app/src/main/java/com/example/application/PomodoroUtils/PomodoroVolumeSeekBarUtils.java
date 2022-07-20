package com.example.application.PomodoroUtils;

import static com.example.application.PomodoroUtils.PomodoroConstants.RINGING_VOLUME_LEVEL_KEY;
import static com.example.application.PomodoroUtils.PomodoroConstants.TICKING_VOLUME_LEVEL_KEY;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.widget.SeekBar;

import com.example.application.R;

public class PomodoroVolumeSeekBarUtils {
    public static int maxVolume;
    public static float floatTickingVolumeLevel;
    public static float floatRingingVolumeLevel;

    public static SeekBar initializeSeekBar(Activity activity, SeekBar seekBar) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        final AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) - 1); // -1 just because otherwise converttofloat returns infinity
        seekBar.setMax(maxVolume - 1);
        switch (seekBar.getId()) {
            case R.id.ticking_seek_bar:
                seekBar.setProgress(preferences.getInt(TICKING_VOLUME_LEVEL_KEY, maxVolume));
                break;
            case R.id.ringing_seek_bar:
                seekBar.setProgress(preferences.getInt(RINGING_VOLUME_LEVEL_KEY, maxVolume));
                break;
        }
        return seekBar;
    }

    public static float convertToFloat(int currentVolume, int maxVolume) {
        float value;
        value = (float) (1 - (Math.log(maxVolume - currentVolume) / Math.log(maxVolume)));
        return value;
    }

    public void VolumeSeekBarUtils() {
        //Empty
    }
}
