package com.example.application.flashcarddatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.application.flashcardactivities.Flashcard;

@Database(entities = {Flashcard.class}, version = 1)
public abstract class FlashcardAppDatabase extends RoomDatabase {
    public abstract FlashcardDao flashcardDao();
}
