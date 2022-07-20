package com.example.application.notesactivities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.application.notesdatabase.NoteRepository;

public class NoteBaseActivity extends com.pratik.commonnhelper.BaseActivity {

    NoteRepository noteRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteRepository = NoteRepository.getNoteRepository(getApplication());
    }
}
