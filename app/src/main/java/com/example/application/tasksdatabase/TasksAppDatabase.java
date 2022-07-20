package com.example.application.tasksdatabase;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.application.tasksmodel.Task;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public  abstract class TasksAppDatabase extends RoomDatabase {

    public abstract TasksOnDataBaseAction dataBaseAction();
    private static volatile TasksAppDatabase tasksAppDatabase;

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}

