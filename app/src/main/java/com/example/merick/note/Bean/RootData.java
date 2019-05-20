package com.example.merick.note.Bean;

public class RootData {
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private Data data;

    public RootData(Data data) {
        this.data = data;
    }
}
