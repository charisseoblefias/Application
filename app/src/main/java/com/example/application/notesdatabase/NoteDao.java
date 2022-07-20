package com.example.application.notesdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.application.notesmodels.Note;
import com.example.application.notesutils.NoteConstant;

import java.util.List;

@Dao
public interface NoteDao {

//    ""+Todo.COL_YOUR_PRIMARY_KEY" "

//    @Query("SELECT * FROM " + NoteConstant.TABLE_NAME + " ORDER BY " + NoteConstant.TABLE_NOTE + +" DESC")
    @Query("SELECT *  FROM " + NoteConstant.TABLE_NAME + " ORDER BY id DESC")
    LiveData<List<Note>> getNoteList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

}