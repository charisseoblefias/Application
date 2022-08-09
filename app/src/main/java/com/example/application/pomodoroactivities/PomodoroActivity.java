package com.example.application.pomodoroactivities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.application.R;

import java.util.Locale;

public class PomodoroActivity extends Activity {

    private final static long DEFAULT_WORK_DURATION = 1500000;
    private final static long DEFAULT_BREAK_DURATION = 300000;
    private final static int REQUEST_CODE_SETTINGS = 0;
    private final static int COUNTDOWN_INTERVAL = 150;
    private final static int NOTIFICATION_ID = 0;
    private final static String CHANNEL_ID = "togglechannel";
    private CountDownTimer countDownTimer;
    private TextView countdownTimeLabel;
    private ProgressBar countdownProgressBar;
    private Button startPauseButton;
    private Button cancelButton;
    private Button settingsButton;
    private ConstraintLayout mainLayout;
    private CharSequence startStatusLabel;
    private CharSequence pauseStatusLabel;
    private CharSequence resumeStatusLabel;
    private long setWorkDurationInMillis = DEFAULT_WORK_DURATION;
    private long setBreakDurationInMillis = DEFAULT_BREAK_DURATION;
    private boolean isCountdownRunning;
    private long currentTotalDurationInMillis;
    private long timeLeftInMillis;
    private boolean isWorkMode;
    private int colourPrimary;
    private int colourSecondary;
    private int colourText;
    private int colourText1;
    private int colourBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        // Initialize the "set timer".
        currentTotalDurationInMillis = setWorkDurationInMillis;
        timeLeftInMillis = currentTotalDurationInMillis;

        // Instantiate initial timer.
        countDownTimer = new PomodoroTimer(setWorkDurationInMillis, COUNTDOWN_INTERVAL);

        // Refers to a boolean indicating if timer is running.
        isCountdownRunning = false;

        // Refers to a boolean indicating if user is currently in the work interval.
        isWorkMode = true;

        // Create notification channels for newer APIs (26 and up)
        createNotificationChannel();

        // Update the custom app colour scheme depending on the current colour scheme.
        updateColourSchemeColour();

        // Get resources.
        startStatusLabel = getResources().getText(R.string.start_status_label);
        pauseStatusLabel = getResources().getText(R.string.pause_status_label);
        resumeStatusLabel = getResources().getText(R.string.resume_status_label);

        // Set up reference for main images and layout.
        mainLayout = findViewById(R.id.mainLayout);

        // Set up reference instance variables with resource.
        countdownTimeLabel = findViewById(R.id.countdownTimer);
        countdownProgressBar = findViewById(R.id.countdownProgressBar);
        startPauseButton = findViewById(R.id.startPauseButton);
        cancelButton = findViewById(R.id.cancelButton);
        settingsButton = findViewById(R.id.settingsButton);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"LexendDeca-Regular.ttf");
        countdownTimeLabel.setTypeface(typeface);
        startPauseButton.setTypeface(typeface);
        cancelButton.setTypeface(typeface);
        settingsButton.setTypeface(typeface);

        // Set instance variables with corresponding object listeners.
        startPauseButton.setOnClickListener(new ButtonListener());
        cancelButton.setOnClickListener(new ButtonListener());
        settingsButton.setOnClickListener(new ButtonListener());

        // Set up other all the other widget colour schemes now that they are referenced.
        setProgressBarColour(colourPrimary);
        updateWidgetColourScheme();
        updateTimerWidgets();
    }

    //Implementing the timer
    class PomodoroTimer extends CountDownTimer {

        PomodoroTimer(long countdownInMillis, long countdownInterval) {
            super(countdownInMillis, countdownInterval);

            // Set instance variable to acknowledge new timer.
            timeLeftInMillis = countdownInMillis;
        }

        //Timer
        @Override
        public void onTick(long millisUntilFinished) {

            // update instance variable responsible to keep track of current timer's countdown.
            timeLeftInMillis = millisUntilFinished;

            // update widget countdown widgets and text responsible for displaying the text.
            updateTimerWidgets();
        }

       //Finished Timer
        @Override
        public void onFinish() {

            // Execute necessary changes and steps to set up for the next timer.
            toggleWorkMode();

            updateTimerWidgets();

            // Ensure countdownLabel has the current colour for a cancelled timer.
            countdownTimeLabel.setTextColor(colourText);

            // Startup timer standby mode.
            timerStandby();

            // Send notification to notify that current timer is up.
            sendTimerToggleNotification();

        }
    }

   // Notiification Channels
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Initialize variables required to set up notification channel.
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system. The importance or any other notification
            // behaviors cannot be changed after this.
            NotificationManager channelManager = getSystemService(NotificationManager.class);

            // Try to register the channel with the system.
            try {
                channelManager.createNotificationChannel(channel);
            }
            catch (NullPointerException exception) {
                Log.d("notificationChannel", "Unable to create notification channel");
            }
        }
    }

    //inform the user whether they have finished the timer
    private void sendTimerToggleNotification() {

        // Declare local variables used.
        String text;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                getApplicationContext());

        // Create an explicit intent for the main activity.
        Intent intent = new Intent(getApplicationContext(), PomodoroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Determine text to display depending on new current mode.
        if (isWorkMode) {
            text = "Break is over, time to get to work!";
        }
        else {
            text = "You worked hard, time for a break!";
        }

        // Initiate notification with the correct/wanted properties.
        NotificationCompat.Builder notification = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.comfiler_logo)
                .setContentTitle("ComFiler")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        // Send notification.
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    //Cancel the notification
    private static void cancelNotification(Context context, int notifyId) {

        NotificationManager cancelManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        try {
            cancelManager.cancel(notifyId);
        }
        catch (NullPointerException exception) {
            Log.d("cancelNotification", "Attempted to cancel non-existent notification");
        }
    }

    //Inner class for button listeners
    class ButtonListener implements View.OnClickListener {

        //This method is automatically called when any button within main activity is pressed.
        @Override
        public void onClick( View v ) {

            // Cancel notifications, state of main activity has changed.
            cancelNotification(getApplicationContext(), NOTIFICATION_ID);

            // Start the settings activity if the corresponding button is pressed.
            if (v.getId() == R.id.settingsButton) {
                openSettingsActivity();
            }
            // When the first timer button is pressed, run start/Resume or pause depending on state.
            else if (v.getId() == R.id.startPauseButton) {
                // Determine whether the timer is running to carry out specific methods.
                if (!isCountdownRunning) {
                    startResumeTimer();
                }
                else {
                    pauseTimer();
                }
            }
            // When the cancel button is pressed, call the cancelTimer method.
            else if (v.getId() == R.id.cancelButton) {
                cancelTimer();
            }
        }
    }

    //Start a new Timer
    private void startResumeTimer() {

        // Instantiate timer with current time left.
        countDownTimer = new PomodoroTimer(timeLeftInMillis, COUNTDOWN_INTERVAL);

        // Make sure label is using the correct colour and start the timer.
        countdownTimeLabel.setTextColor(colourText);
        countDownTimer.start();

        // Set timer start up mode to change anythings necessary for the running timer.
        timerStartup();
    }

   //Pause the timer
    private void pauseTimer() {

        // cancel current timer.
        countDownTimer.cancel();

        // Set countdown label colour for better blinking colours.
        if (isWorkMode) {
            countdownTimeLabel.setTextColor(colourPrimary);
        }
        else {
            countdownTimeLabel.setTextColor(colourSecondary);
        }

        // enable timer standby mode.
        timerStandby();
    }

    // Cancel the Timer
    private void cancelTimer() {

        // Cancel the current timer, toggle work state, and update the timer widgets for display.
        countDownTimer.cancel();
        toggleWorkMode();
        updateTimerWidgets();

        // Ensure countdownLabel has the current colour for a cancelled timer.
        countdownTimeLabel.setTextColor(colourText);

        // Enable timer standby mode.
        timerStandby();
    }

   //Timer Standby Mode
    private void timerStandby() {

        // Set resume/pause button label depending on if the timer is fresh or already used.
        if (timeLeftInMillis != currentTotalDurationInMillis) {
            startPauseButton.setText(resumeStatusLabel);
        }
        else {
            startPauseButton.setText(startStatusLabel);
        }

        // Keep track that timer is now on standby and also start blinking.
        isCountdownRunning = false;
    }

    // Ensure that the current timer is prepared for timer startup (running state).
    private void timerStartup() {

        // Keep track that timer is now in a running state, clear blinking.
        isCountdownRunning = true;
        countdownTimeLabel.clearAnimation();

        // Set startPause label to pause now that the timer is running.
        startPauseButton.setText(pauseStatusLabel);
    }

    private void updateColourSchemeColour() {

        colourPrimary = getResources().getColor(R.color.card3);
        colourSecondary = getResources().getColor(com.pratik.commonnhelper.R.color.gray);
        colourText1 = getResources().getColor(R.color.white);
        colourText = getResources().getColor(R.color.black);
        colourBackground = getResources().getColor(R.color.white);

    }

    // update the colour of the widgets that has to be manually updated.
    private void updateWidgetColourScheme() {

        // Set colour background accordingly.
        mainLayout.setBackgroundColor(colourBackground);

        // Set label text colours accordingly.
        startPauseButton.setTextColor(colourText1);
        cancelButton.setTextColor(colourText1);
        settingsButton.setTextColor(colourText1);
        countdownTimeLabel.setTextColor(colourText);

        // If on work mode, change colour for work related widgets accordingly.
        if (isWorkMode) {

            // Set countdown label with work mode colour if currently paused (not stopped, at new).
            if (!isCountdownRunning && timeLeftInMillis != currentTotalDurationInMillis) {
                countdownTimeLabel.setTextColor(colourPrimary);
            }
            // Set progress bar to work mode colour.
            setProgressBarColour(colourPrimary);
        }
        // If on break mode, change colour for break related widgets accordingly.
        else {

            // Set countdown label with break mode colour if currently paused (not stopped, at new).
            if (!isCountdownRunning && timeLeftInMillis != currentTotalDurationInMillis) {
                countdownTimeLabel.setTextColor(colourSecondary);
            }
            // Set progress bar to break mode colour.
            setProgressBarColour(colourSecondary);
        }
    }

    //Update the color scheme
    private void updateActivityColourScheme() {

        // Update the colour scheme colours followed by the update of all widgets.
        updateColourSchemeColour();
        updateWidgetColourScheme();

    }


     //This method will update the current total time if required.

    private void updateCurrentTotalTime() {

        // If the timer is a fresh timer, update the current total time with countdown time.
        if (timeLeftInMillis == currentTotalDurationInMillis) {

            // If current state is work mode, change the total time for total work time.
            if (isWorkMode) {
                currentTotalDurationInMillis = setWorkDurationInMillis;
            }
            // If current state is break mode, change the total time for total break time.
            else {
                currentTotalDurationInMillis = setBreakDurationInMillis;
            }

            // Change the countdown timer to the new total time since this is a fresh start.
            timeLeftInMillis = currentTotalDurationInMillis;

            // update timer widgets for the progress bar changes along with the countdown label.
            updateTimerWidgets();
        }
    }

    // Change the Color of Progress Bar
    private void setProgressBarColour(int colour) {

        // User filtering to change the colour of the progress bar drawable.
        countdownProgressBar.getProgressDrawable().setColorFilter(
                colour, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    //toggle from work mode to break mode and vise versa depending on the current
    private void toggleWorkMode(){

        // Toggle to break mode if currently in work mode.
        if (isWorkMode) {

            isWorkMode = false;

            // Set total duration for the total break duration and set appropriate progress colour.
            currentTotalDurationInMillis = setBreakDurationInMillis;
            setProgressBarColour(colourSecondary);
        }
        // Toggle to work mode if currently in break mode.
        else {

            isWorkMode = true;

            // Set total duration for the total work duration and set appropriate progress colour.
            currentTotalDurationInMillis = setWorkDurationInMillis;
            setProgressBarColour(colourPrimary);
        }

        // Create new timer based on the new total duration.
        countDownTimer = new PomodoroTimer(currentTotalDurationInMillis, COUNTDOWN_INTERVAL);
    }

    // start a new intent to start the settings activity for receiving data changed

    public void openSettingsActivity() {

        // Creating new intent, passing the required variable for setting display.
        Intent intent = new Intent(getApplicationContext(), PomodoroSettingsActivity.class);
        intent.putExtra("setWorkDurationInMillis", setWorkDurationInMillis);
        intent.putExtra("setBreakDurationInMillis", setBreakDurationInMillis);

        // Start activity with request for to reference it again when the settings activity is done.
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {

        // Call constructor as is.
        super.onActivityResult(requestCode, resultCode, dataIntent);

        // The returned result data is identified by requestCode.
        // The request code is specified in startActivityForResult method.
        switch (requestCode)
        {
            // This request code is set by startActivityForResult(intent, REQUEST_CODE_SETTINGS).
            case REQUEST_CODE_SETTINGS:

                // If returned normally, continue to retrieve and store data.
                if(resultCode == RESULT_OK)
                {
                    // Save data from key value map.
                    setBreakDurationInMillis = dataIntent.getLongExtra("newBreakDurationInMillis",
                            DEFAULT_BREAK_DURATION);
                    setWorkDurationInMillis = dataIntent.getLongExtra("newWorkDurationInMillis",
                            DEFAULT_WORK_DURATION);

                    // Update theme and update current total time.
                    updateActivityColourScheme();
                    updateCurrentTotalTime();
                }
        }
    }

    //Update all the countdown
    private void updateTimerWidgets() {

        // Update countdown label.
        updateCountDownText();

        // Get actual current percentage of timer.
        int progressPercent = (int)(100.0 * timeLeftInMillis / currentTotalDurationInMillis);

        // If the timer is running and percent is 0, set it to 1 to avoid confusion.
        // This will be disregarded if timeLeftInMillis can be rounded to 0.
        if (isCountdownRunning && progressPercent == 0 && timeLeftInMillis > 1000) {
            countdownProgressBar.setProgress(1);
        }
        // Update progress bar based on current time left and total time if percent is not 0 while
        // running.
        else {
            countdownProgressBar.setProgress(progressPercent);
        }
    }

   //Update the countdown label
    private void updateCountDownText() {

        // Calculate the minutes and seconds of the total time separately.
        int minutes = (int)(timeLeftInMillis / 1000) / 60;
        int seconds = (int)(timeLeftInMillis / 1000) % 60;

        // Set up the formatted string for display.
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d",
                minutes, seconds);

        // Display formatted string.
        countdownTimeLabel.setText(timeLeft);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
