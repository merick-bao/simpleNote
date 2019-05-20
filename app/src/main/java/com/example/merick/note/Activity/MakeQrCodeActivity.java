package com.example.merick.note.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.merick.note.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MakeQrCodeActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button makeQrCode;
    private EditText textOfQrCode;
    private FloatingActionButton saveQrCode;
    private ImageView imageView;
    private ImageView back;

    private static final String SD_PATH = "/sdcard/云笔记/pic/";
    private static final String IN_PATH = "/云笔记/pic/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_qrcode);

        init();
    }

    private void init() {
        toolbar = findViewById(R.id.makeQrCode_toolbar);
        setSupportActionBar(toolbar);

        makeQrCode = findViewById(R.id.makeQrCode);
        saveQrCode = findViewById(R.id.saveQrCode);
        textOfQrCode = findViewById(R.id.textOfQrCode);
        imageView = findViewById(R.id.pictureOfQrCode);
        back = findViewById(R.id.makeQrCode_toolbar_back);

        back.setOnClickListener(this);
        makeQrCode.setOnClickListener(this);
        saveQrCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.makeQrCode://生成二维码
                String textContent = textOfQrCode.getText().toString();
                if (TextUtils.isEmpty(textContent)) {
                    Toast.makeText(MakeQrCodeActivity.this, "您的输入为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                textOfQrCode.setText("");
                Bitmap mBitmap = CodeUtils.createImage(textContent, 400, 400, null);
                imageView.setImageBitmap(mBitmap);
                break;
            case R.id.saveQrCode://保存二维码
                if (imageView.getDrawable() != null){
                    Drawable drawable = imageView.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    String s = saveBitmap(getBaseContext(), bitmap);
                    Toast.makeText(MakeQrCodeActivity.this,"二维码保存在"+s, Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(MakeQrCodeActivity.this,"二维码未生成", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.makeQrCode_toolbar_back://返回上一页
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

}
