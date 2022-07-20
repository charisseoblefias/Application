package com.example.application.notesactivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.example.application.customview.RevealAnimation;
import com.example.application.databinding.ActivityNoteBinding;
import com.example.application.notesadapters.NoteListAdapter;
import com.example.application.notesinterfaces.MainNoteClickListener;
import com.example.application.notesmodels.Note;

import java.util.ArrayList;

public class NoteActivity extends NoteBaseActivity implements MainNoteClickListener {

    ActivityNoteBinding activityNoteBinding;

    NoteListAdapter noteListAdapter;
    ArrayList<Note> noteArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityNoteBinding = DataBindingUtil.setContentView(this, R.layout.activity_note);

        noteListAdapter = new NoteListAdapter(mContext, this);
        activityNoteBinding.rvNoteList.setLayoutManager(new LinearLayoutManager(mContext));
        activityNoteBinding.rvNoteList.setAdapter(noteListAdapter);

        noteRepository.getAllNotes().observe(this, notes -> {
            noteArrayList.clear();
            noteArrayList.addAll(notes);
            noteListAdapter.submitNoteList(noteArrayList);
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                noteRepository.deleteNote(noteArrayList.get(position));
                noteArrayList.remove(position);
                noteListAdapter.notifyItemRemoved(position);

                Toast.makeText(mContext, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(activityNoteBinding.rvNoteList);

        activityNoteBinding.btnAddNote.setOnClickListener(v -> {

            //calculates the center of the View v you are passing
            int revealX = (int) (v.getX() + v.getWidth() / 2);
            int revealY = (int) (v.getY() + v.getHeight() / 2);

            Intent intent = new Intent(mContext, NoteEditActivityNote.class);
            intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX);
            intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY);
            intent.putExtra("fromCreation", true);

            //just start the activity as an shared transition, but set the options bundle to null
            ActivityCompat.startActivity(this, intent, null);

            //to prevent strange behaviours override the pending transitions
            overridePendingTransition(0, 0);

        });

        activityNoteBinding.imgSearch.setOnClickListener(v -> {
            activityNoteBinding.txtNotes.setVisibility(View.GONE);
            activityNoteBinding.imgSearch.setVisibility(View.GONE);
            activityNoteBinding.etSearchQuery.setVisibility(View.VISIBLE);
        });

        activityNoteBinding.etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFilter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void searchFilter(String text) {
        ArrayList<Note> tempArrayList = new ArrayList<>();
        for (Note note : noteArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches

            if (note.getTitle().contains(text) || note.getNote().contains(text)) {
                tempArrayList.add(note);
            }
        }
        //update recyclerview
        noteListAdapter.submitNoteList(tempArrayList);
    }

    @Override
    public void onMainNoteClick(View itemView, Note note) {
        //calculates the center of the View v you are passing
        int revealX = (int) (itemView.getX() + itemView.getWidth() / 2);
        int revealY = (int) (itemView.getY() + itemView.getHeight() / 2);

        Intent intent = new Intent(mContext, NoteEditActivityNote.class);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(RevealAnimation.EXTRA_CIRCULAR_REVEAL_Y, revealY);
        intent.putExtra("fromCreation", false);
        intent.putExtra("myNoteClass", note);

        //just start the activity as an shared transition, but set the options bundle to null
        ActivityCompat.startActivity(this, intent, null);

        //to prevent strange behaviours override the pending transitions
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {

        if (activityNoteBinding.imgSearch.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
            return;
        }

        if (activityNoteBinding.etSearchQuery.getVisibility() == View.VISIBLE) {
            activityNoteBinding.etSearchQuery.setVisibility(View.GONE);
        }
        if (activityNoteBinding.imgSearch.getVisibility() == View.GONE) {
            activityNoteBinding.imgSearch.setVisibility(View.VISIBLE);
        }
        if (activityNoteBinding.txtNotes.getVisibility() == View.GONE) {
            activityNoteBinding.txtNotes.setVisibility(View.VISIBLE);
        }

    }
}