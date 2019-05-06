package com.qihoo360.myapplication.FileFolder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qihoo360.myapplication.R;
import com.qihoo360.myapplication.util.DateUtil;
import com.qihoo360.myapplication.util.FileUtil;
import com.qihoo360.myapplication.util.ToastUtil;

import java.util.ArrayList;

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
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.PhotoViewHolder> {

    private Context mContext;
    private ArrayList<FileFolderActivity.Folder> mFolders;

    FolderAdapter(Context context, ArrayList<FileFolderActivity.Folder> folders) {
        mContext = context;
        mFolders = folders;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.folder_item, null));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {

        FileFolderActivity.Folder folder = mFolders.get(position);
        Log.e("zhangyunpeng2", folder.mUri.toString());
        holder.showView(folder.name, folder.size, folder.lastModified, folder.mUri, folder.mMime, folder.mId);
    }

    @Override
    public int getItemCount() {
        return mFolders.size();
    }


    class PhotoViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mSize;
        TextView mModifyTime;

        PhotoViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.folder_name);
            mSize = itemView.findViewById(R.id.folder_size);
            mModifyTime = itemView.findViewById(R.id.folder_modify_time);

        }

        private void showView(String name, long size, long modifyTime, final Uri uri,final String mime, final String id){
            mName.setText(name);
            mSize.setText(FileUtil.getFormatSizeSource(size));
            mModifyTime.setText(DateUtil.getTime(modifyTime));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                        Intent intent = new Intent(mContext, FileFolderActivity.class);
                        intent.putExtra("rootUri", uri.toString());
                        intent.putExtra("rootId", id);
                        mContext.startActivity(intent);
                    } else {
                        ToastUtil.showToast((FileFolderActivity)mContext, "不是文件夹，没有子目录");
                    }
                }
            });
    }

    }
}
