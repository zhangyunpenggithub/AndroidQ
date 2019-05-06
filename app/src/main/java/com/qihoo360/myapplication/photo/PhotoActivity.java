package com.qihoo360.myapplication.photo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.qihoo360.myapplication.MainActivity;
import com.qihoo360.myapplication.R;
import com.qihoo360.myapplication.util.ToastUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * +----------------------------------------------------------------------
 * | 功能描述:
 * +----------------------------------------------------------------------
 * | 时　　间: 2019/4/28.
 * +----------------------------------------------------------------------
 * | 代码创建: 张云鹏
 * +----------------------------------------------------------------------
 * | 版本信息: V1.0.0
 * +----------------------------------------------------------------------
 **/
public class PhotoActivity extends Activity {


    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(PhotoActivity.this, 4));

        new Thread(new Runnable(){
            @Override
            public void run() {
                loadPhotoFiles(PhotoActivity.this);
            }
        }).start();


    }

    public void loadPhotoFiles(Context context) {
        try {
            final ArrayList<Uri> photoUris = new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, null, null, null);
            if (cursor == null){
                return;
            }
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Images.Media._ID));
                Uri photoUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);
                Log.e("zhangyunpeng", "photoUri:" + photoUri);
                photoUris.add(photoUri);
            }
            cursor.close();
            if (photoUris.size() == 0){
                ToastUtil.showToast(PhotoActivity.this, "没有图片或者没有权限");
                return;
            }
            MainActivity.mUris = photoUris;
            final ArrayList<Bitmap> bitmaps = getAdapterData(photoUris);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bitmaps.size() > 0){
                        mRecyclerView.setAdapter(new PhotoAdapter(PhotoActivity.this, bitmaps));
                    }
                }
            });
        } catch (Exception e) {
            Log.e("zhangyunpeng", "e", e);
        }
    }

    private ArrayList<Bitmap> getAdapterData(List<Uri> photoUris) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (Uri uris : photoUris) {
            bitmaps.add(getBitmapFromUri(uris));
        }
        return bitmaps;
    }


    public Bitmap getBitmapFromUri(Uri uri) {
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

}
