package com.fleshkart.app.map.mapsapi;

import android.content.Context;
import android.os.Bundle;


/**
 * Place Details
 * https://developers.google.com/places/documentation/details
 */
public class PlaceDetails implements BufferDelayedTasker.DelayedRunnableTask {
    private static final String TAG = "PlaceDetails";

    public static final String URL = MapsApi.BASE_URL + "/place/details/json";

    public static final String PLACEID = "placeid";


    /******************************************************************
     * PlaceDetails:
     *   https://developers.google.com/places/documentation/details
     *
     * Usage Example:
     *   String placeId = "ChIJM086K4vAj4ARpgcC3rLwS0M";
     *   new PlaceDetails( context, placeId, resultHandler )
     *       .runTask();
     *
     * @param context
     * @param placeId
     * @param resultHandler
     *
     ******************************************************************/

     public PlaceDetails(Context context, String placeId, ResultHandler resultHandler) {
         Bundle params = MapsApi.createPlacesQueryParams( context );
         params.putString( PLACEID, placeId );
         mHttpTask.initUrl( URL, params );
         mResultHandler = resultHandler;
    }

    /**
     * AsyncHttpTask
     */
    private AsyncHttpTask.AsyncHttpJsonTask<PlaceDetailsResult> mHttpTask =
            new AsyncHttpTask.AsyncHttpJsonTask<PlaceDetailsResult>( PlaceDetailsResult.class ) {
        @Override
        public void onJsonResult(PlaceDetailsResult jsonResult) {
            if ( mResultHandler != null ) {
                mResultHandler.onPlaceDetailsResult(jsonResult, getTaskId());
            }
        }

        // Analytics testing BEGIN
        @Override
        protected PlaceDetailsResult doInBackground(Void... voids) {
            return super.doInBackground( voids );
        }
        // Analytics testing END
    };

    /**
     * BufferDelayedTasker.DelayedRunnableTask
     */
    private long mTaskId;
    @Override
    public void runTask() { mHttpTask.execute(); }
    @Override
    public long getTaskId() { return mTaskId; }
    @Override
    public void setTaskId(long taskId) { mTaskId = taskId; }

    /******************************************************************
     * Response handler interface
     ******************************************************************/

    public interface ResultHandler {
        /**
         * @param result Can be null.
         */
        public void onPlaceDetailsResult(PlaceDetailsResult result, long taskId);
    }

    private ResultHandler mResultHandler;

    /******************************************************************
     * Java model classes to hold json object from place details response:
     *   https://developers.google.com/places/documentation/details#PlaceDetailsResponses
     *
     * Gson library is used to automatically convert json objects to respective
     * java model classes.  This removes the need to explicitly implement
     * API-response-specific json parsing functions.
     *
     * Take !!!!!GREAT CARE!!!!! in defining member variable names
     * to match with those returned from json response objects.
     ******************************************************************/

    public static class PlaceDetailsResult {
        public String status;
        public String error_message;
        public MapsApi.Place result;
        public String[] html_attributions;
    }
}
