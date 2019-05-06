package com.qihoo360.myapplication.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qihoo360.myapplication.R;

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
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context mContext;
    private ArrayList<Bitmap> mBitmaps;

    PhotoAdapter(Context context, ArrayList<Bitmap> bitmaps) {
        mContext = context;
        mBitmaps = bitmaps;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.photo_item, null));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {

        holder.showImg(mBitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return mBitmaps.size();
    }


    class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        PhotoViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.photo_item_img);
        }

        private void showImg(Bitmap bitmap){
            mImageView.setImageBitmap(bitmap);
    }

    }
}
