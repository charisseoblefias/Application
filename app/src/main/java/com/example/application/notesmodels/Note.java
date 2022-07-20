package com.example.application.notesmodels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.application.notesutils.NoteConstant;

import java.io.Serializable;

@Entity(tableName = NoteConstant.TABLE_NAME)
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = NoteConstant.TABLE_NOTE_TITLE)
    private String title;

    @ColumnInfo(name = NoteConstant.TABLE_NOTE)
    private String note;

    @ColumnInfo(name = NoteConstant.TABLE_NOTE_COLOR)
    private String colorCode;

    public Note() {
    }

    public Note(int id, String title, String note, String colorCode) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.colorCode = colorCode;
    }

//    public Note(int id, String title, String note) {
//        this.id = id;
//        this.title = title;
//        this.note = note;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
