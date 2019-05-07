package com.qihoo360.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.qihoo360.myapplication.FileFolder.FileFolderActivity;
import com.qihoo360.myapplication.photo.PhotoActivity;
import com.qihoo360.myapplication.util.DateUtil;
import com.qihoo360.myapplication.util.ToastUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity {

    public static ArrayList<Uri> mUris = new ArrayList<>();
    private static final String android_uri = "content://com.android.externalstorage.documents/tree/primary%3AAndroid";
    private String createFile_url;
    private static final int SAF_READ_REQUEST_CODE = 1000;
    private static final int SAF_CREATE_FILE_REQUEST_CODE = 1001;
    private static final int SAF_BROSWER_REQUEST_CODE = 1002;
    private static final int SAF_BROSWER2_REQUEST_CODE = 1003;
    private static final int REQUEST_PERMISSION_REQUEST_CODE = 1004;
    private static final int DEFAULT_PHOTO_REQUEST_CODE = 1005;
    private static final int SAF_DELETE_REQUEST_CODE = 1006;
    private static final int SAF_DELETE_FOLDER_REQUEST_CODE = 1007;
    private static final int EDIT_REQUEST_CODE = 1008;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //测试shareUID
        findViewById(R.id.button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.qihoo360.targetactivity", "com.qihoo360.targetactivity.SecondActivity"));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不需要权限的目录
                File file = new File(getExternalFilesDir(null), "测试");
                file.mkdirs();

                //任何目录都会被转为沙箱中的目录
                File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "沙箱");
                file1.mkdirs();

                File file3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/1", "沙箱");
                file3.mkdirs();
                if (file3.exists()) {
                    Log.e("zhangyunpeng", "file3文件存在");
                } else {
                    Log.e("zhangyunpeng", "file3文件不存在");
                }

                //不允许直接使用沙箱路径访问沙箱中的文件，即使是自己的沙箱
                File file2 = new File("/sdcard/Android/sandbox/com.qihoo360.application", "沙箱");
                if (file2.exists()) {
                    Log.e("zhangyunpeng", "file2文件存在");
                } else {
                    Log.e("zhangyunpeng", "file2文件不存在");
                }


            }
        });

        //SAF 创建文件
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                //设置创建的文件是可打开的
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //设置创建的文件的minitype为文本类型
                intent.setType("text/plain");
                //设置创建文件的名称，注意SAF中使用minitype而不是文件的后缀名来判断文件类型。
                intent.putExtra(Intent.EXTRA_TITLE, "123.txt");
                startActivityForResult(intent, SAF_CREATE_FILE_REQUEST_CODE);
            }
        });

        //SAF 读取文件内容
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
                intent.setType("text/plain");
                //是否支持多选，默认不支持
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent, SAF_READ_REQUEST_CODE);
            }
        });

        //SAF 编辑文件
        findViewById(R.id.button21).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDocument();
            }
        });

        //创建文件夹1
        findViewById(R.id.button42).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                //设置创建的文件是可打开的
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //设置创建的文件的minitype为文本类型
                intent.setType("vnd.android.document/directory");
                //设置创建文件的名称，注意SAF中使用minitype而不是文件的后缀名来判断文件类型。
                intent.putExtra(Intent.EXTRA_TITLE, "123");
                startActivityForResult(intent, SAF_CREATE_FILE_REQUEST_CODE);
            }
        });

        //创建文件夹2
        findViewById(R.id.button43).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse(android_uri);
                    Uri folderUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                            DocumentsContract.getTreeDocumentId(uri));
                    Uri document = DocumentsContract.createDocument(getContentResolver(), folderUri, "vnd.android.document/directory", "创建文件夹");
                    if (document != null){
                        ToastUtil.showToast(MainActivity.this, "文件夹创建成功");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        //SAF 遍历手机所有文件夹
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, SAF_BROSWER_REQUEST_CODE);
            }
        });

        //SAF 遍历手机所有文件夹
        findViewById(R.id.button50).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, SAF_BROSWER2_REQUEST_CODE);
            }
        });

        //再次遍历手机所有文件夹
        findViewById(R.id.button12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MainActivity.this, FileFolderActivity.class);
                    intent.putExtra("rootUri", android_uri);
                    startActivity(intent);
                } catch (Exception e) {
                    //防止有其他应用修改了uri
                    Log.e("zhangyunpeng", "e", e);
                }
            }
        });

        //删除指定文件
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("text/plain");
                intent.setType("*/*");
                startActivityForResult(intent, SAF_DELETE_REQUEST_CODE);
            }
        });

        //删除指定文件夹1
        findViewById(R.id.button30).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, SAF_DELETE_FOLDER_REQUEST_CODE);
            }
        });

        //MediaStore保存图片
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new Thread(new Runnable(){
                   @Override
                   public void run() {
                       Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                       MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", "discription");
                       ToastUtil.showToast(MainActivity.this, "保存图片完成");
                   }
               }).start();
            }
        });


        //显示手机所有图片
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhotoActivity.class));
            }
        });

        findViewById(R.id.button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

        //通用的保存文件的方式，上边MediaStore只能用来保存图片
        findViewById(R.id.button9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertMediaFile(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MainActivity.this, "image/jpeg", "insert_test_img", "test img save use insert", "110.png", "test", Environment.DIRECTORY_DCIM);
                ToastUtil.showToast(MainActivity.this, "保存媒体文件完成");
            }
        });

        //删除图片（通用，可以删除其他多媒体文件）
        findViewById(R.id.button10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUris.size() == 0){
                    ToastUtil.showToast(MainActivity.this, "还没有读取到图片文件");
                    return;
                }
                int deleteResult = getContentResolver().delete(mUris.get(mUris.size() - 1), null, null);
                if (deleteResult == 1){
                    ToastUtil.showToast(MainActivity.this, "删除文件完成");
                } else {
                    ToastUtil.showToast(MainActivity.this, "删除文件失败");
                }
            }
        });

        //申请成为默认图库
        findViewById(R.id.button11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("Q".equalsIgnoreCase(Build.VERSION.RELEASE) || "10".equalsIgnoreCase(Build.VERSION.RELEASE)) {
                    //设置默认应用
                    RoleManager roleManager = getSystemService(RoleManager.class);
                    if (roleManager == null){
                        return;
                    }
                    if (roleManager.isRoleAvailable(RoleManager.ROLE_GALLERY)) {
                        if (roleManager.isRoleHeld(RoleManager.ROLE_GALLERY)) {
                            // 默认图库
                            Log.e("zhangyunpeng", "默认图库");
                        } else {
                            Log.e("zhangyunpeng", "不是默认图库，请求");
                            Intent roleRequestIntent = roleManager.createRequestRoleIntent(
                                    RoleManager.ROLE_GALLERY);
                            startActivityForResult(roleRequestIntent, DEFAULT_PHOTO_REQUEST_CODE);
                        }
                    }
                }
            }
        });


    }

    /**
     * 编辑文件
     */
    private void editDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }


    /**
     * 保存多媒体文件到公共集合目录
     * @param uri：多媒体数据库的Uri
     * @param mimeType：需要保存文件的mimeType
     * @param displayName：显示的文件名字
     * @param description：文件描述信息
     * @param saveFileName：需要保存的文件名字
     * @param saveSecondaryDir：保存的二级目录
     * @param savePrimaryDir：保存的一级目录
     * @return 返回插入数据对应的uri
     */
    public String insertMediaFile(Uri uri, Context context, String mimeType,
                                         String displayName, String description, String saveFileName, String saveSecondaryDir, String savePrimaryDir) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.Media.PRIMARY_DIRECTORY, savePrimaryDir);
        values.put(MediaStore.Images.Media.SECONDARY_DIRECTORY, saveSecondaryDir);
        Uri url = null;
        String stringUrl = null;    /* value to be returned */
        ContentResolver cr = context.getContentResolver();
        try {
            url = cr.insert(uri, values);
            if (url == null) {
                return null;
            }
            byte[] buffer = new byte[1024];
            ParcelFileDescriptor parcelFileDescriptor = cr.openFileDescriptor(url, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
            InputStream inputStream = context.getResources().getAssets().open(saveFileName);
            while (true) {
                int numRead = inputStream.read(buffer);
                if (numRead == -1) {
                    break;
                }
                fileOutputStream.write(buffer, 0, numRead);
            }
            fileOutputStream.flush();
        } catch (Exception e) {
            Log.e("zhangyunpeng", "Failed to insert media file", e);
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }
        if (url != null) {
            stringUrl = url.toString();
        }
        return stringUrl;
    }


    /**
     * 申请文件读取权限
     */
    private void requestPermission() {
        //此处在beta1只能拿到sdk_int为28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED  || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_MEDIA_VIDEO},
                        REQUEST_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (null == data) {
            return;
        }
        Uri uri = data.getData();
        if (uri == null){
            return;
        }
        if (requestCode == SAF_READ_REQUEST_CODE) {
            handleOpenDocumentAction(uri);
            return;
        }
        if (requestCode == SAF_BROSWER_REQUEST_CODE){
            //设置记住用户的授权，重启以后不再需要再次申请
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // Check for the freshest data.
            getContentResolver().takePersistableUriPermission(uri, takeFlags);

            Intent intent = new Intent(MainActivity.this, FileFolderActivity.class);
            intent.putExtra("rootUri", uri.toString());
            startActivity(intent);
            return;
        }
        if (requestCode == SAF_CREATE_FILE_REQUEST_CODE){
            //创建文件
            ToastUtil.showToast(MainActivity.this, "文件创建成功");
            return;
        }
        if (requestCode == DEFAULT_PHOTO_REQUEST_CODE){
            //如果用户取消的话，result code=0，所以走到这里一定是用户将我们的app设置为默认app了
            ToastUtil.showToast(MainActivity.this, "恭喜成为默认图片管理软件");
            return;
        }
        if (requestCode == SAF_DELETE_REQUEST_CODE){
            try {
                DocumentsContract.deleteDocument(getContentResolver(), uri);
            } catch (Exception e) {
                Log.e("zhangyunpeng", "deleteDocument error: " + e);
            }
//            getContentResolver().delete(uri, null, null);
            return;
        }
        if (requestCode == EDIT_REQUEST_CODE){
            alterDocument(uri);
            return;
        }
        if (requestCode == SAF_BROSWER2_REQUEST_CODE){
            Intent intent = new Intent(MainActivity.this, FileFolderActivity.class);
            intent.putExtra("rootUri", uri.toString());
            startActivity(intent);
            return;
        }
        if (requestCode == SAF_DELETE_FOLDER_REQUEST_CODE){
            try {
                Uri treeUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
                DocumentsContract.deleteDocument(getContentResolver(), treeUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
    }


    private void alterDocument(Uri uri) {
        InputStream is = null;
        BufferedReader br = null;
        FileOutputStream fileOutputStream = null;
        try {
            //获取原来的内容
            is = getContentResolver().openInputStream(uri);
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine())!=null){
                sb.append(line);
            }
            //追加新内容
            ParcelFileDescriptor pfd = getContentResolver().
                    openFileDescriptor(uri, "w");
            fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write((sb + "\n" +"modified" +
                    DateUtil.getTime(System.currentTimeMillis()) + "\n").getBytes());
            pfd.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void handleOpenDocumentAction(Uri uri){
        //根据该Uri可以获取该Document的信息，其数据列的名称和解释可以在DocumentsContact类的内部类Document中找到
        //我们在此查询的信息仅仅只是演示作用
        Cursor cursor = getContentResolver().query(uri,null,
                null,null,null,null);
        StringBuilder sb = new StringBuilder("Uri:");
        sb.append(uri.toString());
        if(cursor!=null && cursor.moveToFirst()){
            String documentId = cursor.getString(cursor.getColumnIndex(
                    Document.COLUMN_DOCUMENT_ID));
            String name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            String size;
            if (!cursor.isNull(sizeIndex)) {
                // Technically the column stores an int, but cursor.getString()
                // will do the conversion automatically.
                size = cursor.getString(sizeIndex);
            } else {
                size = "Unknown";
            }
            sb.append("\n\n\nname:").append(name).append("\n\n\nsize:").append(size).append("B");
        }
        //以下为直接从该uri中获取InputSteam，并读取出文本的内容的操作，这个是纯粹的java流操作，大家应该已经很熟悉了
        //我就不多解释了。另外这里也可以直接使用OutputSteam，向文档中写入数据。
        BufferedReader br = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            sb.append("\n\n\ncontent:");
            while((line = br.readLine())!=null){
                sb.append(line);
            }
            Intent intent = new Intent(MainActivity.this, ContentActivity.class);
            intent.putExtra("content", sb.toString());
            startActivity(intent);
            Log.e("zhangyunpeng", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
                if (br != null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
