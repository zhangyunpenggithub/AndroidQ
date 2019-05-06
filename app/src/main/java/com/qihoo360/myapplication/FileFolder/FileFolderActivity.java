package com.qihoo360.myapplication.FileFolder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.qihoo360.myapplication.R;
import com.qihoo360.myapplication.util.ToastUtil;

import java.util.ArrayList;

/**
 * +----------------------------------------------------------------------
 * | 功能描述:
 * +----------------------------------------------------------------------
 * | 时　　间: 2019/4/29.
 * +----------------------------------------------------------------------
 * | 代码创建: 张云鹏
 * +----------------------------------------------------------------------
 * | 版本信息: V1.0.0
 * +----------------------------------------------------------------------
 **/
@TargetApi(Build.VERSION_CODES.Q)
public class FileFolderActivity extends Activity {

    final ArrayList<Folder> arrayList = new ArrayList<>();
    private RecyclerView mRecyclerview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_folder);
        Intent intent = getIntent();
        if (intent == null){
            finish();
            return;
        }
        final String rootUri = intent.getStringExtra("rootUri");
        if (TextUtils.isEmpty(rootUri)){
            ToastUtil.showToast(FileFolderActivity.this, "rootUri 不合法");
            finish();
            return;
        }

        mRecyclerview = findViewById(R.id.file_folder_rv);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(FileFolderActivity.this));

        final String rootId = intent.getStringExtra("rootId");
        if (TextUtils.isEmpty(rootId)){
            //第一级父级界面
            new Thread(new Runnable(){
                @Override
                public void run() {
                    //遍历文件夹下所有目录
                    browserAllFiles(Uri.parse(rootUri));
                }
            }).start();
        } else {
            //二级或者多级界面
            new Thread(new Runnable(){
                @Override
                public void run() {
                    //遍历文件夹下所有目录
                    goThroughChildNodes(Uri.parse(rootUri), rootId);
                }
            }).start();
        }
    }

    /**
     * 遍历文件加下所有目录
     * @param mRootUri 根目录的uri
     */
    private void browserAllFiles(Uri mRootUri){
        String parentId = "";
        String parentMime = "";
        Uri treeUri = DocumentsContract.buildDocumentUriUsingTree(mRootUri,
                DocumentsContract.getTreeDocumentId(mRootUri));
        Cursor docCursor = getContentResolver().query(treeUri, new String[] {
                Document.COLUMN_DISPLAY_NAME, Document.COLUMN_MIME_TYPE,
                Document.COLUMN_DOCUMENT_ID,
                Document.COLUMN_SIZE, Document.COLUMN_LAST_MODIFIED,
                Document.COLUMN_FLAGS
        }, null, null, null);

        if (docCursor == null){
            return;
        }

        try {
            while (docCursor.moveToNext()) {
                String fileName = docCursor.getString(0);
                parentMime = docCursor.getString(1);
                parentId = docCursor.getString(2);
                long size = docCursor.getLong(3);
                long lastModified = docCursor.getLong(4);
                int flag = docCursor.getInt(5);
                Log.e("zhangyunpeng*********", "fileName*********" + fileName
                        + "parentMime*********" + parentMime + "parentId*********" + parentId + "size*********" +
                        size + "lastModified*********" + lastModified + "flag*********" + flag);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                docCursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        goThroughChildNodes(treeUri, parentId);
    }

    private void goThroughChildNodes(Uri treeUri, String parentId) {
        Uri childrenUri =
                DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentId);
        Cursor childCursor = getContentResolver().query(childrenUri, new String[] {
                Document.COLUMN_DISPLAY_NAME, Document.COLUMN_MIME_TYPE,
                Document.COLUMN_DOCUMENT_ID,
                Document.COLUMN_SIZE, Document.COLUMN_LAST_MODIFIED,
                Document.COLUMN_FLAGS
        }, null, null, null);
        if (childCursor == null){
            return;
        }
        try {

            while (childCursor.moveToNext()) {
                String fileName = childCursor.getString(0);
                String mime = childCursor.getString(1);
                String Id = childCursor.getString(2);
                long size = childCursor.getLong(3);
                long lastModified = childCursor.getLong(4);
                int flag = childCursor.getInt(5);
                arrayList.add(new Folder(fileName, size, mime, lastModified, childrenUri, Id));
                Log.e("zhangyunpeng*********", "fileName*********" + fileName
                        + "mime*********" + mime + "parentId*********" + parentId + "size*********" +
                        size + "lastModified*********" + lastModified + "flag*********" + flag);
                //判断是否是⽂文件夹，是⽂文件夹的话继续遍历⼦子⽂文件
//                if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
//                    goThroughChildNodes(childrenUri, Id);
//                }
            }
            if (arrayList.size() > 0){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerview.setAdapter(new FolderAdapter(FileFolderActivity.this, arrayList));
                    }
                });
            }

        } finally {
            try {
                childCursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    class Folder{
        public String name;
        public long size;
        public long lastModified;
        public Uri mUri;
        public String mMime;
        public String mId;

        public Folder(String name, long size, String mime, long lastModified, Uri uri, String id) {
            this.name = name;
            this.size = size;
            this.lastModified = lastModified;
            this.mUri = uri;
            this.mId = id;
            this.mMime = mime;
        }

    }

}
