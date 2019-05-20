package com.example.merick.note.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.merick.note.Activity.NoteContentActivity;
import com.example.merick.note.Bean.Note;
import com.example.merick.note.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private Context context;
    private List<Note> mNoteList;

    public NoteAdapter(List<Note> mNoteList) {//传入数据源
        this.mNoteList = mNoteList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView summaries;
        TextView noteDate;
        ImageView noteImage;
        CardView noteItem;

        public ViewHolder(@NonNull View itemView) {//获取实例
            super(itemView);
            title = itemView.findViewById(R.id.note_item_title);
            summaries = itemView.findViewById(R.id.note_item_summarise);
            noteDate = itemView.findViewById(R.id.note_item_noteDate);
            noteImage = itemView.findViewById(R.id.note_item_image);
            noteItem = itemView.findViewById(R.id.note_item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        //加载布局
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_item,viewGroup,false);
        //创建并返回实例
        ViewHolder viewHolder = new ViewHolder(view);

        if (context == null){//获取context
            context = viewGroup.getContext();
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        //绑定数据
        Note note = mNoteList.get(i);
        viewHolder.title.setText(note.getTitle());
        viewHolder.summaries.setText(note.getSummaries());
        viewHolder.noteDate.setText(note.getNoteDate());
        //viewHolder.noteImage.setImageResource(note.getNoteImageUrl());
        Glide.with(context).load(note.getNoteImageUrl()).into(viewHolder.noteImage);

        viewHolder.noteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteContentActivity.class);
                intent.putExtra("nid",mNoteList.get(i).getNid());
                intent.putExtra("phone",mNoteList.get(i).getPhone());
                intent.putExtra("title",mNoteList.get(i).getTitle());
                intent.putExtra("content",mNoteList.get(i).getNoteContext());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

}
