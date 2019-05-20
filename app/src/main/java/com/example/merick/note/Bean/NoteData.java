package com.example.merick.note.Bean;

import java.util.List;

public class NoteData {
    private List<Msg> msg ;

    private int code;

    public void setMsg(List<Msg> msg){
        this.msg = msg;
    }
    public List<Msg> getMsg(){
        return this.msg;
    }
    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
