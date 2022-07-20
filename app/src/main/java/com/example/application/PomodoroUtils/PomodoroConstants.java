package com.example.application.PomodoroUtils;

public class PomodoroConstants {

    public static final int POMODORO = 0;
    public static final int SHORT_BREAK = 1;
    public static final int LONG_BREAK = 2;

    public static final int TASK_INFORMATION_NOTIFICATION_ID = 10;
    public static final String CHANNEL_ID = "POMODORO";

    //Broadcast ID
    public static final String COUNTDOWN_BROADCAST = "countdown";
    public static final String STOP_ACTION_BROADCAST = "stop.action";
    public static final String COMPLETE_ACTION_BROADCAST = "complete.action";
    public static final String START_ACTION_BROADCAST = "start.action";

    //Intent names and values
    public static final String INTENT_NAME_ACTION = "action";
    public static final String INTENT_VALUE_START = "start";
    public static final String INTENT_VALUE_COMPLETE = "complete";
    public static final String INTENT_VALUE_CANCEL = "cancel";
    public static final String INTENT_VALUE_SHORT_BREAK = "short";
    public static final String INTENT_VALUE_LONG_BREAK = "long";

    public static final long TIME_INTERVAL = 1000; // Time Interval is 1 second

    //SharePreferences Keys
    public static final String WORK_DURATION_KEY = "WORK_DURATION";
    public static final String SHORT_BREAK_DURATION_KEY = "SHORT_BREAK_DURATION";
    public static final String LONG_BREAK_DURATION_KEY = "LONG_BREAK_DURATION";
    public static final String START_LONG_BREAK_AFTER_KEY = "LONG_BREAK_AFTER_DURATION";
    public static final String TICKING_VOLUME_LEVEL_KEY = "TICKING_VOLUME_LEVEL";
    public static final String RINGING_VOLUME_LEVEL_KEY = "RINGING_VOLUME_LEVEL";
}
