package com.example.application.pomodoroactivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.application.R;

public class PomodoroSettingsActivity extends Activity {

    // Declare instance variables for the settings activity.
    private final static long DEFAULT_WORK_DURATION = 1500000;
    private final static long DEFAULT_BREAK_DURATION = 300000;
    private ImageButton timerButton;
    private SeekBar breakSeekBar;
    private SeekBar workSeekBar;
    private TextView breakStatusView;
    private TextView workStatusView;
    private TextView breakLabel;
    private TextView workLabel;
    private TextView settingslabel;
    private ConstraintLayout settingsLayout;
    private Drawable lightTimerButtonImage;
    private int breakStatus;
    private int workStatus;
    private boolean isLightTheme;
    private long newBreakDurationInMillis;
    private long newWorkDurationInMillis;
    private final static int minTimeInMinutes = 1;

    // Declare reference for a SharedPreferences object
    private SharedPreferences savedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro_settings);

        // Set up reference for main images and layout.
        settingsLayout = findViewById(R.id.settingsLayout);
        lightTimerButtonImage = getResources().getDrawable(R.drawable.ic_back);

        // Set up reference instance variables with resource.
        timerButton = findViewById(R.id.timerButton);
        breakSeekBar = findViewById(R.id.breakSeekBar);
        workSeekBar = findViewById(R.id.workSeekBar);
        breakLabel = findViewById(R.id.breakLabel);
        settingslabel = findViewById(R.id.settingslabel);
        breakStatusView = findViewById(R.id.breakStatusView);
        workLabel = findViewById(R.id.workLabel);
        workStatusView = findViewById(R.id.workStatusView);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"LexendDeca-Regular.ttf");
        breakLabel.setTypeface(typeface);
        workLabel.setTypeface(typeface);
        settingslabel.setTypeface(typeface);
        workStatusView.setTypeface(typeface);
        breakStatusView.setTypeface(typeface);

        // Set instance variables with corresponding object listeners.
        timerButton.setOnClickListener(new ButtonListener());
        breakSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
        workSeekBar.setOnSeekBarChangeListener(new SeekBarListener());

        // Get variables from main activity, where this activity is called.
        Intent intent = getIntent();

        // Save appropriate variables retrieved from main to display current user settings.
        isLightTheme = intent.getBooleanExtra("isLightTheme", true);
        newBreakDurationInMillis = intent.getLongExtra("setBreakDurationInMillis",
                DEFAULT_BREAK_DURATION);
        newWorkDurationInMillis = intent.getLongExtra("setWorkDurationInMillis",
                DEFAULT_WORK_DURATION);
        breakStatus = convertMillisToMin(newBreakDurationInMillis);
        workStatus = convertMillisToMin(newWorkDurationInMillis);

        // get SharedPreferences object for saving variables onPause.
        savedPrefs = getSharedPreferences( "SettingsPrefs", MODE_PRIVATE );

    }

    //Seek Bar Listener
    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            // Declare local variables.
            String statusView;

            // If the break seek bar is changed, set text view for break status.
            if (seekBar.getId() == R.id.breakSeekBar) {
                // Get status, add minTime to accommodated for min value of 1 in seek bar.
                breakStatus = progress + minTimeInMinutes;

                // Decide to use singular version of minute if appropriate.
                if (breakStatus == 1) {
                    statusView = breakStatus + " Minute";
                }
                else {
                    statusView = breakStatus + " Minutes";
                }

                // Set break status and save new break duration in millis.
                breakStatusView.setText(statusView);
                newBreakDurationInMillis = convertMinToMillis(breakStatus);
            }

            // If the work seek bar is changed, set text view for work status.
            else if (seekBar.getId() == R.id.workSeekBar) {
                // Get status, add minTime to accommodated for min value of 1 in seek bar.
                workStatus = progress+minTimeInMinutes;

                // Decide to use singular version of minute if appropriate.
                if (workStatus == 1) {
                    statusView = workStatus + " Minute";
                }
                else {
                    statusView = workStatus + " Minutes";
                }

                // Set work status and save new work duration in millis.
                workStatusView.setText(statusView);
                newWorkDurationInMillis = convertMinToMillis(workStatus);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Called when the user starts changing the SeekBar
            // Not Used / Implemented
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Called when the user finishes changing the SeekBar
            // Not Used / Implemented
        }

    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // If the timer button is pressed, return to main activity.
            if (v.getId() == R.id.timerButton) {
                exitToTimerActivity();
            }
        }
    }

    public void exitToTimerActivity() {

        // Create the intent for saving variables back to main.
        Intent intent = new Intent();

        // Save variables as key value pairs.
        intent.putExtra("isLightTheme", isLightTheme);
        intent.putExtra("newBreakDurationInMillis", newBreakDurationInMillis);
        intent.putExtra("newWorkDurationInMillis", newWorkDurationInMillis);

        // Set result to ok code to declare that app has return normally.
        setResult(RESULT_OK, intent);

        // Finish settings activity, return to main activity.
        finish();
   }

   private long convertMinToMillis(int minutes) {

       // Return operation, converting minutes to milliseconds.
        return (minutes * 60 * 1000);

   }


    private int convertMillisToMin(long millis) {

        // Return operation from millis to minutes.
        return ((int)(millis / 60 / 1000));

    }

    @Override
    public void onPause() {
        // Save the billAmountString and tipPercentage instance variables
        SharedPreferences.Editor prefsEditor = savedPrefs.edit();
        prefsEditor.putInt("breakStatus", breakStatus);
        prefsEditor.putInt("workStatus", workStatus);
        prefsEditor.commit();

        // Calling the parent onPause() must be done LAST
        super.onPause();
    }

    @Override
    public void onResume() {

        // Call progress required for on resume call.
        super.onResume();

        // Load the instance variables back (or default values)
        breakStatus = savedPrefs.getInt("breakStatus", 5);
        workStatus = savedPrefs.getInt("workStatus", 25);
    }

    @Override
    public void onBackPressed() {
        // Uses standard exit method when back is pressed.
        exitToTimerActivity();
    }

}

