package com.example.merick.note.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.merick.note.Bean.RootData;
import com.example.merick.note.R;
import com.example.merick.note.Util.CodeUtil;
import com.example.merick.note.Util.HttpUtil;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity{

    private EditText getUsername;
    private EditText getPassword;
    private EditText getInviteCode;
    private Button register;
    private Toolbar toolbar;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case R.id.register:
                    int code = msg.arg1;
                    if (code == 0){
                        //注册成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"注册成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String username = getUsername.getText().toString();
                        String password = getPassword.getText().toString();
                        Intent intent = getIntent();
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        setResult(CodeUtil.COED_REGISTER, intent);
                        finish();
                    }else if (code == 1002){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"已注册", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else if (code == 1004){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this,"邀请码错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarOfRegister);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);

        getUsername = findViewById(R.id.register_get_username);
        getPassword = findViewById(R.id.register_get_password);
        getInviteCode = findViewById(R.id.register_get_inviteCode);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerHandler();
            }
        });
    }

    private void registerHandler() {
        String username = getUsername.getText().toString();
        String password = getPassword.getText().toString();
        String inviteCode = getInviteCode.getText().toString();
        final String registerUrl = "http://123.206.22.45:8000/users/regist?" +
                "phone=" + username + "&password=" + password + "&code=" + inviteCode;

        if (!username.isEmpty() && !password.isEmpty() && !inviteCode.isEmpty()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(registerUrl)
                                .build();
                        Response response = client.newCall(request).execute();
                        String data = response.body().string();

                        Gson gson = new Gson();
                        RootData rootData = gson.fromJson(data, RootData.class);

                        Message message = new Message();
                        message.what = R.id.register;
                        message.arg1 = rootData.getData().getCode();
                        handler.handleMessage(message);
                        Looper.prepare();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }else {
            Toast.makeText(RegisterActivity.this, "请完整输入", Toast.LENGTH_SHORT).show();
        }
    }
}
