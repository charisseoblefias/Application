package com.example.application.pomodorotimer;

import static com.example.application.PomodoroUtils.PomodoroConstants.INTENT_VALUE_CANCEL;
import static com.example.application.PomodoroUtils.PomodoroConstants.INTENT_VALUE_COMPLETE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.application.PomodoroUtils.PomodoroStopTimerUtils;

public class PomodoroStopTimerActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String receivedIntent = intent.getStringExtra("action");
        switch (receivedIntent) {
            case INTENT_VALUE_COMPLETE:
                PomodoroStopTimerUtils.sessionComplete(context);
                break;
            case INTENT_VALUE_CANCEL:
                PomodoroStopTimerUtils.sessionCancel(context, preferences);
                break;
        }

    }
}
