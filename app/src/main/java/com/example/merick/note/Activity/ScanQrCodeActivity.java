package com.example.merick.note.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.merick.note.R;
import com.example.merick.note.Util.CodeUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class ScanQrCodeActivity extends BaseActivity implements View.OnClickListener {
    private Button scanQrCode;
    private EditText resultsOfScan;
    private FloatingActionButton copyResult;
    private Toolbar toolbar;
    private ImageView back;
    public static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        ZXingLibrary.initDisplayOpinion(this);
        init();
    }

    private void init() {
        scanQrCode = findViewById(R.id.scanQrCode);
        resultsOfScan = findViewById(R.id.resultsOfScan);
        copyResult = findViewById(R.id.copyResults);
        toolbar = findViewById(R.id.scanQrCode_toolbar);
        back = findViewById(R.id.scanQrCode_toolbar_back);

        setSupportActionBar(toolbar);

        scanQrCode.setOnClickListener(this);
        copyResult.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scanQrCode:
                //获取摄像头权限
                if(Build.VERSION.SDK_INT > 22){//安卓6.0需要申请权限
                    if (ContextCompat.checkSelfPermission(ScanQrCodeActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        //先判断有没有权限，没有就在这里进行权限的申请
                        ActivityCompat.requestPermissions(ScanQrCodeActivity.this,
                                new String[]{Manifest.permission.CAMERA}, 1);
                    }else {
                        //以获得权限
                        Intent intent = new Intent(ScanQrCodeActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, CodeUtil.REQUEST_CODE);
                    }
                }else {
                    //6.0一下不需要申请权限
                }
                break;
            case R.id.copyResults:
                //将扫描结果复制到粘贴板
                String res = resultsOfScan.getText().toString();
                ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (!res.isEmpty()){
                    manager.setText(res);
                    Toast.makeText(ScanQrCodeActivity.this, "扫描结果已复制到粘贴板", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ScanQrCodeActivity.this, "没有扫描结果", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.scanQrCode_toolbar_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodeUtil.REQUEST_CODE){
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    resultsOfScan.setText(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(ScanQrCodeActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
