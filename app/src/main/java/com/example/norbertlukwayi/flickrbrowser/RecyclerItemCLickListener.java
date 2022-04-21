package com.example.norbertlukwayi.flickrbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

class RecyclerItemCLickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecylcerItemCLickListen";

    interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        void onItemLongCLick(View view, int position);

    }
    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;

    public RecyclerItemCLickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null){
                    Log.d(TAG, "onSingleTapUp: calling listener.onclick");
                    mListener.onItemClick(childView,recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null){
                    Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    mListener.onItemLongCLick(childView,recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if (mGestureDetector != null){
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent(): returned " + result  );
            return result;
        }else{
            Log.d(TAG, "onInterceptTouchEvent(): returned: false");
            return false;
        }

    }
}
