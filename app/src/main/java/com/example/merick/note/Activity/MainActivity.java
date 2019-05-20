package com.example.merick.note.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.merick.note.Adapter.NoteAdapter;
import com.example.merick.note.Bean.Msg;
import com.example.merick.note.Bean.Note;
import com.example.merick.note.Bean.RootNoteData;
import com.example.merick.note.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private List<Note> noteList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private Button loginOut;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String phone;
    private NoteAdapter noteAdapter;
    private int noteNum = 0;
    File file = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    noteList.clear();
                    RootNoteData rootNoteData = (RootNoteData) msg.obj;
                    List<Msg> msgs = rootNoteData.getData().getMsg();
                    for (int i = 0; i < msgs.size(); i++){
                        Note note = new Note(msgs.get(i).getNid(), msgs.get(i).getPhone(),msgs.get(i).getTitle(), msgs.get(i).getContents(), msgs.get(i).getContents(),
                                msgs.get(i).getCreateTime(), "https://cn.bing.com/az/hprichbg/rb/DriftwoodPirate_ZH-CN11949090819_1920x1080.jpg");
                        noteList.add(note);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noteAdapter.notifyDataSetChanged();
                        }
                    });

                    break;
                case 3001:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "还没有笔记哦", Toast.LENGTH_SHORT).show();
//                            if (noteNum == 0){
//                                Note note = new Note(0, "默认标题", "还没有笔记，快来新建一个吧",
//                                        " ", " ", "https://cn.bing.com/az/hprichbg/rb/DriftwoodPirate_ZH-CN11949090819_1920x1080.jpg");
//                                noteList.add(note);
//                                noteNum++;
//                                noteAdapter.notifyDataSetChanged();
//                            }

                        }
                    });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        init();
    }

    private void init() {
        //下拉刷新
        swipeRefreshLayout = findViewById(R.id.main_swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateNotes();
            }
        });

        //登出按钮
        loginOut = findViewById(R.id.main_login_out);
        loginOut.setOnClickListener(this);

        //设置工具栏
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setTitle("笔记");
        }

        //侧滑菜单
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.main_drawerLayout);
        //侧滑菜单点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_scan_qrCode://前往扫描二维码
                        Intent intent = new Intent(MainActivity.this, ScanQrCodeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_make_qrCode://前往生成二维码
                        intent = new Intent(MainActivity.this, MakeQrCodeActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        drawerLayout.closeDrawers();//关闭侧滑菜单
                        break;
                }

                return true;
            }
        });

        //新建笔记按钮
        floatingActionButton = findViewById(R.id.main_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteContentActivity.class);
                intent.putExtra("nid",0);
                intent.putExtra("phone",phone);
                intent.putExtra("title","");
                intent.putExtra("content","");
                startActivity(intent);
            }
        });

        //初始化日记内容
        //initNotes();
        updateNotes();

        //适配日记item
        recyclerView = findViewById(R.id.main_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        noteAdapter = new NoteAdapter(noteList);
        recyclerView.setAdapter(noteAdapter);
    }

    private void updateNotes() {
        //获取笔记
        final String url = "http://123.206.22.45:8000/users/getnotes?phone=" + phone;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    //处理返回的数据
                    Gson gson = new Gson();
                    RootNoteData rootNoteData = gson.fromJson(responseData, RootNoteData.class);

                    Message message = new Message();
                    message.what = rootNoteData.getData().getCode();
                    message.obj = rootNoteData;
                    handler.handleMessage(message);

//                    List<Msg> msgs = rootNoteData.getData().getMsg();
//                    for (int i = 0; i < msgs.size(); i++){
//                        Note note = new Note(msgs.get(i).getTitle(), msgs.get(i).getContents(),
//                                msgs.get(i).getCreateTime(), "https://cn.bing.com/az/hprichbg/rb/DriftwoodPirate_ZH-CN11949090819_1920x1080.jpg");
//                        noteList.add(note);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Toast.makeText(this,"刷新成功",Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载工具栏菜单布局
        getMenuInflater().inflate(R.menu.main_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //工具栏按钮点击事件
        switch (item.getItemId()){
            case R.id.refresh:
                Toast.makeText(this,"正在刷新",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(true);
                updateNotes();
                break;
            case R.id.delete:
                Toast.makeText(this,"Delete",Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                Toast.makeText(this,"Setting",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        //处理点击事件
        switch (view.getId()){
            case R.id.main_login_out://退出登录
                file = new File(getFilesDir(),"info.txt");
                if(file.exists()){
                    file.delete();
                }
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }
}
