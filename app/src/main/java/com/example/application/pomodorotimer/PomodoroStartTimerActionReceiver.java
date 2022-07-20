package com.example.application.pomodorotimer;

import static com.example.application.PomodoroUtils.PomodoroConstants.INTENT_NAME_ACTION;
import static com.example.application.PomodoroUtils.PomodoroConstants.INTENT_VALUE_LONG_BREAK;
import static com.example.application.PomodoroUtils.PomodoroConstants.INTENT_VALUE_SHORT_BREAK;
import static com.example.application.PomodoroUtils.PomodoroConstants.INTENT_VALUE_START;
import static com.example.application.PomodoroUtils.PomodoroConstants.LONG_BREAK;
import static com.example.application.PomodoroUtils.PomodoroConstants.POMODORO;
import static com.example.application.PomodoroUtils.PomodoroConstants.SHORT_BREAK;
import static com.example.application.PomodoroUtils.PomodoroStartTimerUtils.startTimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.application.PomodoroUtils.PomodoroUtils;

public class PomodoroStartTimerActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedIntent = intent.getStringExtra(INTENT_NAME_ACTION);
        SharedPreferences prefences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (receivedIntent) {
            case INTENT_VALUE_START:
                long workDuration = PomodoroUtils.getCurrentDurationPreferenceOf(prefences, context,
                        POMODORO);
                startTimer(workDuration, context);
                Log.d("TIMER was started with", String.valueOf(workDuration));
                break;
            case INTENT_VALUE_SHORT_BREAK:
                long shortBreakDuration = PomodoroUtils.getCurrentDurationPreferenceOf(prefences, context,
                        SHORT_BREAK);
                startTimer(shortBreakDuration, context);
                Log.d("SHRT_BRK started with", String.valueOf(shortBreakDuration));
                break;
            case INTENT_VALUE_LONG_BREAK:
                long longBreakDuration = PomodoroUtils.getCurrentDurationPreferenceOf(prefences, context,
                        LONG_BREAK);
                startTimer(longBreakDuration, context);
                Log.d("LONG_BRK started with", String.valueOf(longBreakDuration));
        }
    }
}
