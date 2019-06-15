package com.notes;

public class Note {

    public static final String TABLE_NAME = "notes";

    public static final String ID = "id";
    public static final String NOTE = "note";
    public static final String TIMESTAMP = "timestamp";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + NOTE + " TEXT,"
                    + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public String note;
    public int id;
    public String timestamp;

    public Note(int id, String note, String timestamp) {
        this.note = note;
        this.id = id;
        this.timestamp = timestamp;
    }

    public Note() {
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
