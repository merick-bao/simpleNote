package com.example.merick.note.Activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merick.note.Bean.Data;
import com.example.merick.note.Bean.RootData;
import com.example.merick.note.R;
import com.example.merick.note.Util.CodeUtil;
import com.example.merick.note.Util.HttpUtil;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText getPhone;
    private EditText getPassWord;
    private Button loginButton;
    private Toolbar toolbar;
    private RelativeLayout relativeLayout;
    private CheckBox isSaved;
    private TextView gotoRegister;
    File file = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case R.id.login_button:
                    int code = msg.arg1;
                    if (code == 0){
                        //保存账号密码
                        try {
                            saveUserData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("phone", getPhone.getText().toString());
                        startActivity(intent);
                        finish();
                    }else if (code == 1002){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "用户未注册", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else if (code == 1005){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inits();
    }

    private void inits() {//初始化控件

        getPhone = findViewById(R.id.login_input_phone);
        getPassWord = findViewById(R.id.login_input_password);
        relativeLayout = findViewById(R.id.login_layout);
        loginButton = findViewById(R.id.login_button);
        toolbar = findViewById(R.id.login_toolbar);
        isSaved = findViewById(R.id.login_save);
        gotoRegister = findViewById(R.id.gotoRegister);

        setSupportActionBar(toolbar);
        toolbar.setTitle("登录");

        addLayoutListener(relativeLayout,loginButton);

        loginButton.setOnClickListener(this);
        gotoRegister.setOnClickListener(this);

        //用户上次保存的账号密码
        loadPreUseData();

    }

    //处理键盘遮挡输入框
    private void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //1，获取main在窗体的可视区域
                main.getWindowVisibleDisplayFrame(rect);
                //2.获取main在窗体不可见的区域高度，在键盘没有弹起时，main.getRootView().getHeight()调节
                // 高度应该和rect.bottom高度一样
                int mainInvisiableHeight = main.getRootView().getHeight() - rect.bottom;
                //3.不可见区域大于100，说明键盘弹起了
                if (mainInvisiableHeight > 100) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    //4获取scroll的窗体坐标，算出main需要滚动的高度
                    int scrollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    //5.让界面整体上移键盘的高度
                    main.scrollTo(0, scrollHeight);
                }else {
                    //3.不可见区域小于100，说明键盘隐藏了，吧界面下移，移回原有高度
                    main.scrollTo(0,0);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        //处理点击事件
        switch (view.getId()){
            case R.id.login_input_phone:
                Toast.makeText(LoginActivity.this,"getPhone",Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_input_password:
                Toast.makeText(LoginActivity.this,"getPassWord",Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_button:
                loginHandler();
                break;
            case R.id.gotoRegister:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent, CodeUtil.COED_REGISTER);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CodeUtil.COED_REGISTER){
            if (data != null){
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                getPhone.setText(username);
                getPassWord.setText(password);
            }
        }
    }

    private void loginHandler() {
        final String phone = getPhone.getText().toString();
        final String password = getPassWord.getText().toString();
        final String loginUrl = "http://123.206.22.45:8000/users/login?" + "phone=" + phone +
                "&password=" + password;
        //Toast.makeText(LoginActivity.this, loginUrl, Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(loginUrl).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    //处理返回数据
                    Gson gson = new Gson();
                    RootData rootData = gson.fromJson(responseData,RootData.class);

                    Message message = new Message();
                    message.what = R.id.login_button;
                    message.arg1 = rootData.getData().getCode();
                    handler.sendMessage(message);
                    Looper.prepare();
                    Toast.makeText(LoginActivity.this, rootData.getData().getMsg(), Toast.LENGTH_LONG).show();
                    Log.d("Login",responseData);
                    Log.d("Login",rootData.getData().getMsg());
                    Log.d("Login",String.valueOf(rootData.getData().getCode()));
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveUserData() throws IOException {
        FileOutputStream fileOutputStream = null;
        String phone = getPhone.getText().toString();
        String password = getPassWord.getText().toString();
        if (isSaved.isChecked()){
            try {
                file = new File(getFilesDir(),"info.txt");
                fileOutputStream = new FileOutputStream(file);
                //转换为字节数组存入
                fileOutputStream.write((phone+"##"+password).getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                if (fileOutputStream != null){
                    fileOutputStream.close();
                }
            }
        }else {
            //不保存密码
            if (file.exists()){
                file.delete();
            }
        }
    }

    private void loadPreUseData() {
        FileInputStream inputStream = null;
        BufferedReader reader = null;
        String phone,password;
        file = new File(getFilesDir(),"info.txt");
        if(file.exists()){
            try {
                inputStream = new FileInputStream(file);
                /* 将字节流转化为字符流，转化是因为我们知道info.txt
                 * 只有一行数据，为了使用readLine()方法，所以我们这里
                 * 转化为字符流，其实用字节流也是可以做的。但比较麻烦
                 */
                reader = new BufferedReader(new InputStreamReader(inputStream));
                //读取info.txt
                String str = reader.readLine();
                //分割info.txt里面的内容。这就是为什么写入的时候要加入##的原因
                String arr[] = str.split("##");
                phone = arr[0];
                password = arr[1];
                getPhone.setText(phone);
                getPassWord.setText(password);
                isSaved.setChecked(true);
                loginHandler();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
