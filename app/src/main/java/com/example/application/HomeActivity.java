package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.application.flashcardactivities.FlashcardActivity;
import com.example.application.homeHelperClass.homeAdapterClass;
import com.example.application.homeHelperClass.homehelper;
import com.example.application.notesactivities.NoteActivity;
import com.example.application.pomodoroactivities.PomodoroActivity;
import com.example.application.tasksactivity.ToDoActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements homeAdapterClass.ListItemClickListener {
    RecyclerView homeRecyclerView;
    RecyclerView.Adapter adapter;
    ImageView showMenuBtn;
    private ViewPager2 viewPager2;
    private List<Image> imageList;
    private ImageAdapter imageadapter;
    private Handler sliderHandler = new Handler();

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
        showMenuBtn = findViewById(R.id.showMenuBtn);

        PopupMenu popupMenu = new PopupMenu(
                this,showMenuBtn
        );
        popupMenu.getMenuInflater().inflate(R.menu.homemenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.about_us){
                    Intent intent = new Intent(HomeActivity.this, AboutUs.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        showMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        viewPager2 = findViewById(R.id.viewPager2);
        imageList = new ArrayList<>();

        imageList.add(new Image(R.drawable.tips1));
        imageList.add(new Image(R.drawable.tips2));
        imageList.add(new Image(R.drawable.tips3));
        imageList.add(new Image(R.drawable.tips4));
        imageList.add(new Image(R.drawable.tips5));
        imageList.add(new Image(R.drawable.tips6));
        imageList.add(new Image(R.drawable.tips7));
        imageList.add(new Image(R.drawable.tips8));
        imageList.add(new Image(R.drawable.tips9));
        imageList.add(new Image(R.drawable.tips10));
        imageList.add(new Image(R.drawable.tips11));
        imageList.add(new Image(R.drawable.tips12));

        imageadapter = new ImageAdapter(imageList, viewPager2);
        viewPager2.setAdapter(imageadapter);

        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);

        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer =new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + 0.14f);
            }
        });

        viewPager2.setPageTransformer(transformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000);
            }
        });

    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onPause(){
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable,2000);
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
            case 3: // fourth item in Recycler view
                mIntent = new Intent(HomeActivity.this, FlashcardActivity.class);
                startActivity(mIntent);
                break;
        }
    }
}