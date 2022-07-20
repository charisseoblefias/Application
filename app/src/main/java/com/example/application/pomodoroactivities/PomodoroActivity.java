package com.example.application.pomodoroactivities;

import static com.example.application.PomodoroUtils.PomodoroConstants.COMPLETE_ACTION_BROADCAST;
import static com.example.application.PomodoroUtils.PomodoroConstants.COUNTDOWN_BROADCAST;
import static com.example.application.PomodoroUtils.PomodoroConstants.LONG_BREAK;
import static com.example.application.PomodoroUtils.PomodoroConstants.LONG_BREAK_DURATION_KEY;
import static com.example.application.PomodoroUtils.PomodoroConstants.POMODORO;
import static com.example.application.PomodoroUtils.PomodoroConstants.SHORT_BREAK;
import static com.example.application.PomodoroUtils.PomodoroConstants.SHORT_BREAK_DURATION_KEY;
import static com.example.application.PomodoroUtils.PomodoroConstants.START_ACTION_BROADCAST;
import static com.example.application.PomodoroUtils.PomodoroConstants.START_LONG_BREAK_AFTER_KEY;
import static com.example.application.PomodoroUtils.PomodoroConstants.STOP_ACTION_BROADCAST;
import static com.example.application.PomodoroUtils.PomodoroConstants.WORK_DURATION_KEY;
import static com.example.application.PomodoroUtils.PomodoroStartTimerUtils.startTimer;
import static com.example.application.PomodoroUtils.PomodoroStopTimerUtils.sessionCancel;
import static com.example.application.PomodoroUtils.PomodoroStopTimerUtils.sessionComplete;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.application.PomodoroUtils.PomodoroUtils;
import com.example.application.R;
import com.example.application.pomodorotimer.PomodoroCountDownTimerService;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PomodoroActivity extends AppCompatActivity implements View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {


    public static int currentlyRunningServiceType; // Type of Service can be POMODORO, SHORT_BREAK or LONG_BREAK
    BroadcastReceiver stoppedBroadcastReceiver;
    BroadcastReceiver countDownReceiver;
    BroadcastReceiver completedBroadcastReceiver;
    BroadcastReceiver startBroadcastReceiver;
    @BindView(R.id.settings_imageview_main)
    ImageView settingsImageView;
    @BindView(R.id.timer_button_main)
    ToggleButton timerButton;
    @BindView(R.id.countdown_textview_main)
    TextView countDownTextView;
    @BindView(R.id.finish_imageview_main)
    ImageView finishImageView; // (Complete Button)
    private long workDuration; // Time Period for Pomodoro (Work-Session)
    private String workDurationString; // Time Period for Pomodoro in String
    private long shortBreakDuration; // Time Period for Short-Break
    private String shortBreakDurationString; // Time Period for Short-Break in String
    private long longBreakDuration; // Time Period for Long-Break
    private String longBreakDurationString; // Time Period for Long-Break in String
    private SharedPreferences preferences;
    private AlertDialog alertDialog;
    private boolean isAppVisible = true;
    private String currentCountDown; // Current duration for Work-Session, Short-Break or Long-Break


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        isAppVisible = true;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ButterKnife.bind(this);
        setOnClickListeners();

        determineViewState(isServiceRunning(PomodoroCountDownTimerService.class));

        // Receives broadcast that the timer has stopped.
        stoppedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sessionCompleteAVFeedback(context);
            }
        };

        // Receives broadcast for countDown at every tick.
        countDownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    currentCountDown = intent.getExtras().getString("countDown");
                    setTextCountDownTextView(currentCountDown);
                }
            }
        };

        //Receives broadcast when timer completes its time
        completedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sessionCompleteAVFeedback(context);
            }
        };

        //Receives broadcast when timer starts
        startBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sessionStartAVFeedback();
            }
        };

        retrieveDurationValues(); //Duration values for Session and Short and Long Breaks
        setInitialValuesOnScreen(); //Button Text and Worksession Count

        alertDialog = createTametuCompletionAlertDialog();
        displayTametuCompletionAlertDialog();

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener(this);

    }

    private void determineViewState(boolean serviceRunning) {
        // Set button as checked if the service is already running.
        timerButton.setChecked(serviceRunning);
    }

    private void sessionStartAVFeedback() {
        ToggleButton toggleButton = findViewById(R.id.timer_button_main);
        toggleButton.setChecked(true);
    }

    private void setInitialValuesOnScreen() {
        // Changing textOn & textOff according to value of currentlyRunningServiceType.
        currentlyRunningServiceType = PomodoroUtils.retrieveCurrentlyRunningServiceType(preferences, this);
        changeToggleButtonStateText(currentlyRunningServiceType);
    }

    private void retrieveDurationValues() {
        // Retrieving current value of Duration for POMODORO, SHORT_BREAK and
        // LONG_BREAK from SharedPreferences.
        workDuration = PomodoroUtils.getCurrentDurationPreferenceOf(preferences, this, POMODORO);
        shortBreakDuration = PomodoroUtils.getCurrentDurationPreferenceOf(preferences, this, SHORT_BREAK);
        longBreakDuration = PomodoroUtils.getCurrentDurationPreferenceOf(preferences, this, LONG_BREAK);

        // Retrieving duration in mm:ss format from duration value in milliSeconds.
        workDurationString = PomodoroUtils.getCurrentDurationPreferenceStringFor(workDuration);
        shortBreakDurationString = PomodoroUtils.getCurrentDurationPreferenceStringFor(shortBreakDuration);
        longBreakDurationString = PomodoroUtils.getCurrentDurationPreferenceStringFor(longBreakDuration);
    }

    private void sessionCompleteAVFeedback(Context context) {
        // Retrieving value of currentlyRunningServiceType from SharedPreferences.
        currentlyRunningServiceType = PomodoroUtils.retrieveCurrentlyRunningServiceType(preferences,
                getApplicationContext());
        changeToggleButtonStateText(currentlyRunningServiceType);
        alertDialog = createTametuCompletionAlertDialog();
        displayTametuCompletionAlertDialog();
        //Reset Timer TextView
        String duration = PomodoroUtils.getCurrentDurationPreferenceStringFor(PomodoroUtils.
                getCurrentDurationPreferenceOf(preferences, context, currentlyRunningServiceType));
        setTextCountDownTextView(duration);
    }

    private void setOnClickListeners() {
        settingsImageView.setOnClickListener(this);
        timerButton.setOnClickListener(this);
        finishImageView.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        isAppVisible = true;
        currentlyRunningServiceType = PomodoroUtils.retrieveCurrentlyRunningServiceType(preferences, this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        isAppVisible = true;
        registerLocalBroadcastReceivers();
        // Creates new Alert Dialog.
        alertDialog = createTametuCompletionAlertDialog();
        displayTametuCompletionAlertDialog();
        super.onResume();
    }

    @Override
    protected void onPause() {
        isAppVisible = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        isAppVisible = false;
        if (!isServiceRunning(PomodoroCountDownTimerService.class)) {
            unregisterLocalBroadcastReceivers();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        isAppVisible = false;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        currentCountDown = countDownTextView.getText().toString();
        outState.putString("currentCountDown", currentCountDown);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentCountDown = savedInstanceState.getString("currentCountDown");
        setTextCountDownTextView(currentCountDown);
    }

    @Override
    public void onClick(View v) {
        registerLocalBroadcastReceivers();

        // Retrieving value of currentlyRunningServiceType from SharedPreferences.
        currentlyRunningServiceType = PomodoroUtils.retrieveCurrentlyRunningServiceType(preferences, this);

        // Switch case to handle different button clicks
        switch (v.getId()) {

            // Settings button is clicked
            case R.id.settings_imageview_main:
                // launch PomodoroSettingsActivity
                Intent intent = new Intent(PomodoroActivity.this, PomodoroSettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.timer_button_main:
                Date date = new Date(System.currentTimeMillis()); //or simply new Date();
                long millis = date.getTime();
                int resume = (int) millis / 1000;
                int pause = preferences.getInt("pause", 0);
                if ((resume - pause) >= 14400)
                    preferences.edit().putInt(getString(R.string.work_session_count_key), 0).apply();

                if (currentlyRunningServiceType == POMODORO) {
                    if (timerButton.isChecked()) {
                        startTimer(workDuration, this);
                    } else {
                        // When "Cancel Pomodoro" is clicked, service is stopped and toggleButton
                        // is reset to "Start Pomodoro".
                        sessionCancel(this, preferences);
                    }
                } else if (currentlyRunningServiceType == SHORT_BREAK) {
                    if (timerButton.isChecked()) {
                        startTimer(shortBreakDuration, this);
                    } else {
                        // When "Skip Short Break" is clicked, service is stopped and toggleButton
                        // is reset to "Start Pomodoro".
                        sessionCancel(this, preferences);
                    }
                } else if (currentlyRunningServiceType == LONG_BREAK) {
                    if (timerButton.isChecked()) {
                        startTimer(longBreakDuration, this);
                    } else {
                        // When "Skip Long Break" is clicked, service is stopped and toggleButton
                        // is reset to "Start Pomodoro".
                        sessionCancel(this, preferences);
                    }
                }
                break;

            case R.id.finish_imageview_main:
                if (timerButton.isChecked()) {
                    // Finish (Complete Button) stops service and sets currentlyRunningServiceType
                    // to SHORT_BREAK or LONG_BREAK and updates number of completed WorkSessions.
                    sessionComplete(this);
                }
                break;
        }
    }

    private void changeToggleButtonStateText(int currentlyRunningServiceType) {
        timerButton.setChecked(isServiceRunning(PomodoroCountDownTimerService.class));
        if (currentlyRunningServiceType == POMODORO) {
            countDownTextView.setText(workDurationString);
        } else if (currentlyRunningServiceType == SHORT_BREAK) {
            countDownTextView.setText(shortBreakDurationString);
        } else if (currentlyRunningServiceType == LONG_BREAK) {
            countDownTextView.setText(longBreakDurationString);
        }

        timerButton.setChecked(timerButton.isChecked());
    }

    private void registerLocalBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver((stoppedBroadcastReceiver),
                new IntentFilter(STOP_ACTION_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver((countDownReceiver),
                new IntentFilter(COUNTDOWN_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver(completedBroadcastReceiver,
                new IntentFilter(COMPLETE_ACTION_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver(startBroadcastReceiver,
                new IntentFilter(START_ACTION_BROADCAST));
    }

    private void unregisterLocalBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stoppedBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(countDownReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(completedBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startBroadcastReceiver);
    }

    private void setTextCountDownTextView(String duration) {
        countDownTextView.setText(duration);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private AlertDialog createTametuCompletionAlertDialog() {
        if (alertDialog != null)
            alertDialog.cancel();

        View alertDialogLayout = View.inflate(getApplicationContext(), R.layout.layout_alert_dialog, null);
        final Button startBreakLargeButton = alertDialogLayout.findViewById(R.id.start_break);
        final Button startOtherBreakMediumButton = alertDialogLayout.findViewById(R.id.start_other_break);
        Button skipBreakSmallButton = alertDialogLayout.findViewById(R.id.skip_break);

        if (currentlyRunningServiceType == SHORT_BREAK) {
            startBreakLargeButton.setText(R.string.start_short_break);
            startOtherBreakMediumButton.setText(R.string.start_long_break);
        } else if (currentlyRunningServiceType == LONG_BREAK) {
            startBreakLargeButton.setText(R.string.start_long_break);
            startOtherBreakMediumButton.setText(R.string.start_short_break);
        }

        startBreakLargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentButtonText = startBreakLargeButton.getText().toString();
                startBreakFromAlertDialog(currentButtonText);
            }
        });

        startOtherBreakMediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentButtonText = startOtherBreakMediumButton.getText().toString();
                startBreakFromAlertDialog(currentButtonText);
            }
        });

        skipBreakSmallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionCancel(PomodoroActivity.this, preferences);
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(alertDialogLayout);
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder.create();
    }

    private void displayTametuCompletionAlertDialog() {
        if (currentlyRunningServiceType != POMODORO && isAppVisible && !alertDialog.isShowing() && !isServiceRunning(PomodoroCountDownTimerService.class)) {
            alertDialog.show();
        }
    }

    private void startBreakFromAlertDialog(String currentButtonText) {
        long breakDuration = 0;
        if (currentButtonText.equals(getString(R.string.start_long_break))) {
            PomodoroUtils.updateCurrentlyRunningServiceType(preferences, getApplicationContext(), LONG_BREAK);
            breakDuration = longBreakDuration;
        } else if (currentButtonText.equals(getString(R.string.start_short_break))) {
            PomodoroUtils.updateCurrentlyRunningServiceType(preferences, getApplicationContext(), SHORT_BREAK);
            breakDuration = shortBreakDuration;
        }

        currentlyRunningServiceType = PomodoroUtils.retrieveCurrentlyRunningServiceType(preferences, getApplicationContext());
        if (alertDialog != null)
            alertDialog.cancel();
        registerLocalBroadcastReceivers();
        changeToggleButtonStateText(currentlyRunningServiceType);
        startTimer(breakDuration, this);
        timerButton.setChecked(isServiceRunning(PomodoroCountDownTimerService.class));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case WORK_DURATION_KEY:
            case SHORT_BREAK_DURATION_KEY:
            case LONG_BREAK_DURATION_KEY:
            case START_LONG_BREAK_AFTER_KEY:
                retrieveDurationValues();
                setInitialValuesOnScreen();
                break;
        }
    }
}
