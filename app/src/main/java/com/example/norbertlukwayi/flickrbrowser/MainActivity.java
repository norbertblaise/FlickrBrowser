package com.example.norbertlukwayi.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivty implements GetFlickrJasonData.OnDataAvailable,
                            RecyclerItemCLickListener.OnRecyclerClickListener
{
    private static final String TAG = "MainActivity";
    private FlickrRecylcerViewAdapter mFlickrRecylcerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateToolbar(false);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylcer_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemCLickListener(this, recyclerView, this));

        mFlickrRecylcerViewAdapter = new FlickrRecylcerViewAdapter(this, new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickrRecylcerViewAdapter);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume starts");
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = SharedPreferences.getString(FLICKR_QUERY,"");

        if (queryResult.length() >0){
            GetFlickrJasonData getFlickrJasonData= new GetFlickrJasonData(this,"https://api.flickr.com/services/feeds/photos_public.gne", "en-us", true);
            getFlickrJasonData.execute(queryResult);
        }

        Log.d(TAG, "onResume ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status){
        Log.d(TAG, "onDataAvailable: Starts");
        if (status == DownloadStatus.OK){
            mFlickrRecylcerViewAdapter.LoadNewData(data);
        }else{
            //download or processing failed 
            Log.e(TAG, "onDataAvailable: failed with status " + status );
        }
        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(MainActivity.this, "Normael tap at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongCLick(View view, int position) {
        Log.d(TAG, "onItemLongCLick: starts");
//        Toast.makeText(MainActivity.this, "long Tap at position " + position, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER, mFlickrRecylcerViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}
