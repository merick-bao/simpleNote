package com.example.merick.note.Bean;

public class Msg {
    private int nid;

    private String phone;

    private String title;

    private String contents;

    private String createTime;

    public void setNid(int nid){
        this.nid = nid;
    }
    public int getNid(){
        return this.nid;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public String getPhone(){
        return this.phone;
    }
    public void setContents(String contents){
        this.contents = contents;
    }
    public String getContents(){
        return this.contents;
    }
    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    public String getCreateTime(){
        return this.createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
