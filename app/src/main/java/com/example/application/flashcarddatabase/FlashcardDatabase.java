package com.example.application.flashcarddatabase;

import android.content.Context;

import androidx.room.Room;

import com.example.application.flashcardactivities.Flashcard;

import java.util.List;

public class FlashcardDatabase {
    private final FlashcardAppDatabase db;

    public FlashcardDatabase(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(),
                FlashcardAppDatabase.class, "flashcard-database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public List<Flashcard> getAllCards() {
        return db.flashcardDao().getAll();
    }

    public void insertCard(Flashcard flashcard) {
        db.flashcardDao().insertAll(flashcard);
    }

    public void deleteCard(String flashcardQuestion) {
        List<Flashcard> allCards = db.flashcardDao().getAll();
        for (Flashcard f : allCards) {
            if (f.getQuestion().equals(flashcardQuestion)) {
                db.flashcardDao().delete(f);
            }
        }
    }

    public void updateCard(Flashcard flashcard) {
        db.flashcardDao().update(flashcard);
    }
}
