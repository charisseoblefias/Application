package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.TextView;

import com.example.application.homeHelperClass.homeAdapterClass;
import com.example.application.homeHelperClass.homehelper;
import com.example.application.notesactivities.NoteActivity;
import com.example.application.pomodoroactivities.PomodoroActivity;
import com.example.application.tasksactivity.ToDoActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements homeAdapterClass.ListItemClickListener {
    RecyclerView homeRecyclerView;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        TextView textViewDate = findViewById(R.id.textViewDate);
        textViewDate.setText(currentDate);
        homeRecyclerView = findViewById(R.id.my_recycler);
        homeRecyclerView();

    }

    private void homeRecyclerView() {

        //All Gradients
        GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff5B84B1, 0xff5B84B1});
        GradientDrawable gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffdddad7, 0xffdddad7});
        GradientDrawable gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffFC766A, 0xFFFC766A});
        GradientDrawable gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xffb8d7f5});


        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<homehelper> homeLocations = new ArrayList<>();
        homeLocations.add(new homehelper(gradient1, "Tasks"));
        homeLocations.add(new homehelper(gradient2, "Notes"));
        homeLocations.add(new homehelper(gradient3, "Pomodoro"));
        homeLocations.add(new homehelper(gradient4, "Flashcard"));

        adapter = new homeAdapterClass(homeLocations, this);
        homeRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onphoneListClick(int clickedItemIndex) {
        Intent mIntent;
        switch (clickedItemIndex) {
            case 0: // first item in Recycler view
                mIntent = new Intent(HomeActivity.this, ToDoActivity.class);
                startActivity(mIntent);
                break;
            case 1: // second item in Recycler view
                mIntent = new Intent(HomeActivity.this, NoteActivity.class);
                startActivity(mIntent);
                break;
            case 2: // third item in Recycler view
                mIntent = new Intent(HomeActivity.this, PomodoroActivity.class);
                startActivity(mIntent);
                break;
            //case 3: // fourth item in Recycler view
                //mIntent = new Intent(HomeActivity.this, ToDoActivity.class);
                //startActivity(mIntent);
                //break;
        }
    }
}