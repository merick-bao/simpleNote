package com.example.merick.note.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.merick.note.Bean.RootData;
import com.example.merick.note.R;
import com.google.gson.Gson;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NoteContentActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText title;
    private EditText contents;
    private FloatingActionButton save;
    private String phone;
    private int nid;
    private String titles;
    private String content;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case R.id.noteContent_save:
                    int code = msg.arg1;
                    if(code == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                save.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondaryDark)));
                                save.setEnabled(false);
                                Toast.makeText(NoteContentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                               // finish();
                            }
                        });
                    }else if (code == 1001){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NoteContentActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        nid = code;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                save.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondaryDark)));
                                save.setEnabled(false);
                                Toast.makeText(NoteContentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                // finish();
                            }
                        });
                    }
                case R.id.delete:
                    if (msg.arg1 == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NoteContentActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }else {

                    }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_content);

        Intent intent = getIntent();
        //加载原先的笔记内容
        titles = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        phone = intent.getStringExtra("phone");
        nid = intent.getIntExtra("nid",0);
        Log.d("note","is" + titles);

        init();
    }

    private void init() {
        //初始化
        title = findViewById(R.id.noteContent_title);
        contents = findViewById(R.id.noteContent_content);
        if (!titles.isEmpty() && !content.isEmpty()){
            title.setText(titles);
            contents.setText(content);
        }

        //保存按钮
        save = findViewById(R.id.noteContent_save);
        save.setOnClickListener(this);

        //工具栏
        toolbar = findViewById(R.id.noteContent_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("笔记内容");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        }

        //笔记内容变化监听器
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        save.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        save.setEnabled(true);
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        contents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        save.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                        save.setEnabled(true);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                save.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondaryDark)));
                save.setEnabled(false);
                //Toast.makeText(NoteContentActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                // finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_context_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (save.isEnabled()) saveHandle();
                //Toast.makeText(this,"5555",Toast.LENGTH_SHORT).show();
                NoteContentActivity.this.finish();
                break;
            case R.id.noteContext_menu_share:
                Toast.makeText(this,"share",Toast.LENGTH_SHORT).show();
                break;
            case R.id.noteContext_menu_edit:
                Toast.makeText(this,"edit",Toast.LENGTH_SHORT).show();
                break;
            case R.id.noteContext_menu_delete:
                if (nid != 0){
                    delNoteHandle();
                    Toast.makeText(this,"delete",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"笔记未保存",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.noteContext_menu_sync:
                if (save.isEnabled()){
                    saveHandle();
                    Toast.makeText(this,"sync",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"没有更新内容",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void delNoteHandle() {
        final String url = "http://123.206.22.45:8000/users/delnote?" + "nid=" + nid;
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteContentActivity.this);
        builder.setMessage("确认删除？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //删除笔记
                if (nid != 0){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url(url).build();
                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();

                                //处理返回数据
                                Gson gson = new Gson();
                                RootData rootData = gson.fromJson(responseData,RootData.class);

                                Message message = new Message();
                                message.what = R.id.delete;
                                message.arg1 = rootData.getData().getCode();
                                handler.sendMessage(message);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.noteContent_save://保存日记到服务器
                saveHandle();
                break;
            default:
                break;
        }
    }

    private void saveHandle() {
        final String mTitle = title.getText().toString();
        final String mContents = contents.getText().toString();
        final String url = "http://123.206.22.45:8000/users/postnote?" + "phone=" + phone + "&title=" + mTitle
                + "&contents=" + mContents +"&nid=" + nid;

        if (!mTitle.isEmpty() && !mContents.isEmpty() && !phone.isEmpty()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url).build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();

                        //处理返回数据
                        Gson gson = new Gson();
                        RootData rootData = gson.fromJson(responseData,RootData.class);

                        Message message = new Message();
                        message.what = R.id.noteContent_save;
                        message.arg1 = rootData.getData().getCode();
                        handler.sendMessage(message);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            Toast.makeText(NoteContentActivity.this, "输入错误", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (save.isEnabled()){
            saveHandle();
        }
    }
}
