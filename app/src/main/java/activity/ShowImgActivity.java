package activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mac.douyun.R;

import java.io.File;
import java.io.IOException;

import util.ToastUtil;
import Utils.MD5Util;
import config.RequestOptions;
import start.ImageLoader;

import static android.content.ContentValues.TAG;

/**
 * 这个activity是写着跳转用的。。以后方便复制 故不用MVP模式
 */
public class ShowImgActivity extends Activity {
    private ImageView imageView;
    private String url;
    private Button save;
    private Button set;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_img);
        imageView = findViewById(R.id.show_image);
        save = findViewById(R.id.SaveImg_Btn);
        set = findViewById(R.id.setImg_Btn);
        getBundle();
        SaveImg();
        SetImg();
    }

    private void getBundle(){
        Bundle bundle = getIntent().getBundleExtra("bd");
        if(bundle!=null){
            url = bundle.getString("img");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SetImgView(url);
                }
            });
        }
    }
    RequestOptions options = new RequestOptions()
            .setPreloadPic(R.mipmap.ic_launcher_round)
            .setErrorPic(R.mipmap.ic_launcher);
    private void SetImgView(final String url){
        if(url!=null){
//            ImageLoader
//                    .with(ShowImgActivity.this)
//                    .load(url)
//                    .into(imageView)
//                    .apply(options)
//                    .display();
            Glide
                    .with(ShowImgActivity.this)
                    .load(url)
                    .placeholder(Color.parseColor("#FFFFFF"))
                    .error(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImgActivity.this.finish();
            }
        });
    }

    private void SaveImg(){
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = MD5Util.StringToMD5(url);
                String path = ImageLoader.getInstance().getContext().getExternalCacheDir().getPath();
                Log.d(TAG, "在showImgActivity中保存图片");
                Bitmap bitmap = Utils.FileUtil.getBitmapFromDisk(filename,path);

                MediaStore.Images.Media.insertImage(ShowImgActivity.this.getContentResolver(),bitmap,filename,null);
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File file = null;
                if (filename != null) {
                    file = new File(path,filename);
                }
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                ShowImgActivity.this.sendBroadcast(intent);
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setBackgroundResource(R.mipmap.ic_launcher_round);

                new ToastUtil(ShowImgActivity.this
                        ,R.layout.toast_layout
                        ,"保存成功")
                        .setColor(Color.WHITE,Color.BLACK)
                        .setGravity(Gravity.CENTER,0,0)
                        .show(3000);
            }
        });
    }
    private void SetImg(){
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager manager = WallpaperManager.getInstance(ShowImgActivity.this);
                String filename = MD5Util.StringToMD5(url);
                Log.d(TAG, "在showImgActivity中保存图片");
                String path = ImageLoader.getInstance().getContext().getExternalCacheDir().getPath();
                Bitmap bitmap = Utils.FileUtil.getBitmapFromDisk(filename,path);
                try {
                    manager.setBitmap(bitmap);
                    new ToastUtil(ShowImgActivity.this
                            ,R.layout.toast_layout
                            ,"设置成功")
                            .setColor(Color.WHITE,Color.BLACK)
                            .setGravity(Gravity.CENTER,0,0)
                            .show(3000);
                } catch (IOException e) {
                    e.printStackTrace();
                    new ToastUtil(ShowImgActivity.this
                            ,R.layout.toast_layout
                            ,"设置失败")
                            .setColor(Color.WHITE,Color.BLACK)
//                                .setGravity(Gravity.CENTER,0,0)
                            .show(3000);
                }


            }
        });
    }
}
