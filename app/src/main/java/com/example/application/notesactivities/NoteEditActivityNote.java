package com.example.application.notesactivities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.example.application.R;
import com.example.application.customview.RevealAnimation;
import com.example.application.databinding.ActivityNoteEditBinding;
import com.example.application.notesmodels.Note;

import java.util.Random;

public class NoteEditActivityNote extends NoteBaseActivity {

    ActivityNoteEditBinding activityNoteEditBinding;

    Note noteModel;

    boolean fromCreation;

    //    Reveal Animation
    RevealAnimation mRevealAnimation;

    String[] colorArray = {"F5F5F5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityNoteEditBinding = DataBindingUtil.setContentView(this, R.layout.activity_note_edit);

        Bundle extraBundle = getIntent().getExtras();
        if (extraBundle != null) {
            fromCreation = extraBundle.getBoolean("fromCreation");

            if (!fromCreation) {
                noteModel = (Note) extraBundle.getSerializable("myNoteClass");
            }
        }

        mRevealAnimation = new RevealAnimation(activityNoteEditBinding.rootlayout, getIntent(), this);

        if (fromCreation) {
            activityNoteEditBinding.txtSave.setVisibility(View.VISIBLE);
            activityNoteEditBinding.imgEdit.setVisibility(View.GONE);
        } else {
            activityNoteEditBinding.txtSave.setVisibility(View.GONE);
            activityNoteEditBinding.imgEdit.setVisibility(View.VISIBLE);
        }


        if (noteModel != null) {
            activityNoteEditBinding.addTitle.setText(noteModel.getTitle());
            activityNoteEditBinding.addNote.setText(noteModel.getNote());
        }

        activityNoteEditBinding.imgEdit.setOnClickListener(v -> {
            activityNoteEditBinding.imgEdit.setVisibility(View.GONE);
            activityNoteEditBinding.txtSave.setVisibility(View.VISIBLE);
        });

        activityNoteEditBinding.imgBack.setOnClickListener(v -> onBackPressed());

        activityNoteEditBinding.txtSave.setOnClickListener(v -> {
            if (fromCreation) {
                noteModel = new Note();
                noteModel.setColorCode(colorArray[new Random().nextInt(colorArray.length)]);
            }
            noteModel.setTitle(activityNoteEditBinding.addTitle.getText().toString());
            noteModel.setNote(activityNoteEditBinding.addNote.getText().toString());

            if (fromCreation) {
                noteRepository.insertNote(noteModel);
                Toast.makeText(NoteEditActivityNote.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                noteRepository.updateNote(noteModel);
                Toast.makeText(NoteEditActivityNote.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
            }

            onBackPressed();
//            finish();
        });

        activityNoteEditBinding.addTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!fromCreation) {
                    if (activityNoteEditBinding.imgEdit.getVisibility() == View.VISIBLE) {
                        activityNoteEditBinding.imgEdit.setVisibility(View.GONE);
                        activityNoteEditBinding.txtSave.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityNoteEditBinding.addNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!fromCreation) {
                    if (activityNoteEditBinding.imgEdit.getVisibility() == View.VISIBLE) {
                        activityNoteEditBinding.imgEdit.setVisibility(View.GONE);
                        activityNoteEditBinding.txtSave.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        mRevealAnimation.unRevealActivity();
    }

}