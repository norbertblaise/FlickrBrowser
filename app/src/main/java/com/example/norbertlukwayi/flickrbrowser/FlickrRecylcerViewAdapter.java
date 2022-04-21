package com.example.norbertlukwayi.flickrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlickrRecylcerViewAdapter extends RecyclerView.Adapter<FlickrRecylcerViewAdapter.FlickrImageViewHolder>{
    private static final String TAG = "FlickrRecylcerViewAdapt";
    private List<Photo> mPhotoList;
    private Context mContext;

    public FlickrRecylcerViewAdapter(Context context, List<Photo> photoList) {
        mPhotoList = photoList;
        mContext = context;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder( ViewGroup parent, int i) {
        //called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder: new vuew requested");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {
        //called by layout manager when it wants data in an existing row

        if ((mPhotoList == null) || (mPhotoList.size() == 0)){
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText(mContext.getString(com.example.norbertlukwayi.flickrbrowser.R.string.empty_photo));
        }else {

            Photo photoItem = mPhotoList.get(position);
            Log.d(TAG, "onBindViewHolder:  " + photoItem.getTitle() + "-->" + position);
            Picasso.get().load(photoItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);

            holder.title.setText(photoItem.getTitle());
        }

    }

    @Override
    public int getItemCount() {
        return ((mPhotoList != null ) && (mPhotoList.size() != 0) ? mPhotoList.size() : 1);
    }

    void LoadNewData(List<Photo> newPhotos){
        mPhotoList = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null);
    }

    static class FlickrImageViewHolder extends  RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImagaViewHolder";
        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImagaViewHolder: starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
