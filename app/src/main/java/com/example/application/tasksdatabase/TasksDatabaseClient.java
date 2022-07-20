package com.example.application.tasksdatabase;

import android.content.Context;

import androidx.room.Room;

public class TasksDatabaseClient {
    private Context mCtx;
    private static TasksDatabaseClient mInstance;

    //our app database object
    private TasksAppDatabase tasksAppDatabase;

    private TasksDatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
        tasksAppDatabase = Room.databaseBuilder(mCtx, TasksAppDatabase.class, "Task.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized TasksDatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new TasksDatabaseClient(mCtx);
        }
        return mInstance;
    }

    public TasksAppDatabase getAppDatabase() {
        return tasksAppDatabase;
    }
}
