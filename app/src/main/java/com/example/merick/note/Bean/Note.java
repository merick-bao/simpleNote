package com.example.merick.note.Bean;

public class Note {
    private int nid;
    private String phone;
    private String title;
    private String summaries;
    private String noteDate;
    private String noteImageUrl;
    private String noteContext;

    public Note(int nid, String phone, String title, String summaries, String noteContext, String noteDate, String noteImageUrl) {
        this.nid = nid;
        this.phone = phone;
        this.title = title;
        this.summaries = summaries;
        this.noteContext = noteContext;
        this.noteDate = noteDate;
        this.noteImageUrl = noteImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummaries() {
        return summaries;
    }

    public void setSummaries(String summaries) {
        this.summaries = summaries;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteImageUrl() {
        return noteImageUrl;
    }

    public void setNoteImageUrl(String noteImageUrl) {
        this.noteImageUrl = noteImageUrl;
    }
    public String getNoteContext() {
        return noteContext;
    }

    public void setNoteContext(String noteContext) {
        this.noteContext = noteContext;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
