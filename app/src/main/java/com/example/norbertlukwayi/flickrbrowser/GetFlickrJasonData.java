package com.example.norbertlukwayi.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJasonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJasonData";

    private List<Photo>  mPhotoList= null;
    private String mBaseURL;
    private String mLanguage;
    private Boolean mMatchAll;

    private final OnDataAvailable mCallBack;
    private boolean runningOnSameThread = false;

    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    public GetFlickrJasonData( OnDataAvailable callBack,String baseURL, String language, Boolean matchAll) {
        Log.d(TAG, "GetFlickrJasonData: called");
        mBaseURL = baseURL;
        mCallBack = callBack;
        mLanguage = language;
        mMatchAll = matchAll;

    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread: starts");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: start");
        if (mCallBack != null){
            mCallBack.onDataAvailable(mPhotoList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: Starts");
        String destinationUri = createUri(params[0], mLanguage,mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");

        return mPhotoList;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll){
        Log.d(TAG, "createUri: starts");

        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchAll ? "ALL": "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback","1")
                .build().toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: starts . Status  = " + status);

        if (status == DownloadStatus.OK){
            mPhotoList = new ArrayList<>();
            try {
                JSONObject jsonData = new JSONObject(data);
                JSONArray  itemsArray = jsonData.getJSONArray("items");

                for (int i=0; i<itemsArray.length(); i++){
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jasonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jasonMedia.getString("m");

                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title,author,authorId,link,tags,photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete " + photoObject.toString());
                }
            }catch (JSONException jsone){
                jsone.printStackTrace();
                Log.e(TAG, "onDownloadComplete:  Error processing Json Data "+ jsone.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (runningOnSameThread && mCallBack != null){
            //now inform the caller that the processing is done - possibly return null if theres and error
            mCallBack.onDataAvailable(mPhotoList, status);
        }
        Log.d(TAG, "onDownloadComplete: ends");
    }
}
