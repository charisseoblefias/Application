package com.example.application.pomodoroactivities;

import static com.example.application.PomodoroUtils.PomodoroConstants.RINGING_VOLUME_LEVEL_KEY;
import static com.example.application.PomodoroUtils.PomodoroConstants.TICKING_VOLUME_LEVEL_KEY;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.convertToFloat;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.floatRingingVolumeLevel;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.floatTickingVolumeLevel;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.initializeSeekBar;
import static com.example.application.PomodoroUtils.PomodoroVolumeSeekBarUtils.maxVolume;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.application.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PomodoroSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.work_duration_spinner)
    Spinner workDurationSpinner;
    @BindView(R.id.short_break_duration_spinner)
    Spinner shortBreakDurationSpinner;
    @BindView(R.id.long_break_duration_spinner)
    Spinner longBreakDurationSpinner;
    @BindView(R.id.start_long_break_after_spinner)
    Spinner startlongbreakafterSpinner;
    @BindView(R.id.ticking_seek_bar)
    SeekBar tickingSeekBar;
    @BindView(R.id.ringing_seek_bar)
    SeekBar ringingSeekBar;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pomodoro_settings);

        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        initSpinner();

        seekBarInitialization();
    }

    private void seekBarInitialization() {
        tickingSeekBar = initializeSeekBar(this, tickingSeekBar);
        ringingSeekBar = initializeSeekBar(this, ringingSeekBar);
        tickingSeekBar.setOnSeekBarChangeListener(this);
        ringingSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initSpinner() {
        // Create an array adapter for all three spinners using the string array
        ArrayAdapter<CharSequence> workDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.work_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> shortBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.short_break_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> longBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.long_break_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> startlongbreakafterAdapter = ArrayAdapter.createFromResource(this,
                R.array.start_long_break_after_array, R.layout.spinner_item);

        // Layout to use when list of choices appears
        workDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        shortBreakDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        longBreakDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        startlongbreakafterAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        workDurationSpinner.setAdapter(workDurationAdapter);
        shortBreakDurationSpinner.setAdapter(shortBreakDurationAdapter);
        longBreakDurationSpinner.setAdapter(longBreakDurationAdapter);
        startlongbreakafterSpinner.setAdapter(startlongbreakafterAdapter);

        // Set the default selection
        workDurationSpinner.setSelection(preferences.getInt(getString(R.string.work_duration_key), 1));
        shortBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.short_break_duration_key), 1));
        longBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.long_break_duration_key), 1));
        startlongbreakafterSpinner.setSelection(preferences.getInt(getString((R.string.start_long_break_after_key)), 2));

        workDurationSpinner.setOnItemSelectedListener(this);
        shortBreakDurationSpinner.setOnItemSelectedListener(this);
        longBreakDurationSpinner.setOnItemSelectedListener(this);
        startlongbreakafterSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // initialize the editor
        SharedPreferences.Editor editor = preferences.edit();
        // switch case to handle different spinners
        switch (parent.getId()) {
            // item selected in work duration spinner
            case R.id.work_duration_spinner:
                // save the corresponding item position
                editor.putInt(getString(R.string.work_duration_key), position);
                break;
            // item selected in short break duration spinner
            case R.id.short_break_duration_spinner:
                // save the corresponding item position
                editor.putInt(getString(R.string.short_break_duration_key), position);
                break;
            // item selected in long break duration spinner
            case R.id.long_break_duration_spinner:
                // save the corresponding item position
                editor.putInt(getString(R.string.long_break_duration_key), position);
            case R.id.start_long_break_after_spinner:
                // save the corresponding item position
                editor.putInt(getString(R.string.start_long_break_after_key), position);

        }
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.ticking_seek_bar:
                preferences.edit().putInt(TICKING_VOLUME_LEVEL_KEY, progress).apply(); //Save volume level to SharedPreferences
                floatTickingVolumeLevel = convertToFloat(preferences.getInt(TICKING_VOLUME_LEVEL_KEY, maxVolume), maxVolume);
                break;
            case R.id.ringing_seek_bar:
                preferences.edit().putInt(RINGING_VOLUME_LEVEL_KEY, progress).apply(); //Save value to SharedPreferences
                floatRingingVolumeLevel = convertToFloat(preferences.getInt(RINGING_VOLUME_LEVEL_KEY, maxVolume), maxVolume);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}