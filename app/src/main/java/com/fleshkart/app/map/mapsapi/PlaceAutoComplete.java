package com.fleshkart.app.map.mapsapi;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Place
 * https://developers.google.com/places/documentation/autocomplete
 */
public class PlaceAutoComplete implements BufferDelayedTasker.DelayedRunnableTask {
    private static final String TAG = "PlaceAutoComplete";

    public static final String URL = MapsApi.BASE_URL + "/place/autocomplete/json";

    public static final String INPUT = "input";
    public static final String LOCATION = "location";
    public static final String RADIUS = "radius";

    /******************************************************************
     * PlaceAutoComplete:
     *   https://developers.google.com/places/documentation/autocomplete
     *
     * Usage Example:
     *   new PlaceAutoComplete( context, "1000 Enterp",
     *           new LatLng(37.403528, -122.035998), 1000, resultHandler )
     *       .runTask();
     *
     * @param context  Context
     * @param input  Search string.
     * @param location  Lat Lon of search bias.
     * @param radius  Search radius in meters.
     * @param resultHandler  Result handler callback.  Invoked at later time on main thread.
     ******************************************************************/

    public PlaceAutoComplete(Context context,
                             String input, LatLng location, double radius,
                             ResultHandler resultHandler) {
        Bundle params = MapsApi.createPlacesQueryParams( context );
        params.putString( INPUT, input );
        params.putString( LOCATION, location.latitude + "," + location.longitude );
        params.putString( RADIUS, Double.toString(radius) );
        mHttpTask.initUrl( URL, params );
        mResultHandler = resultHandler;
    }

    /**
     * AsyncHttpTask
     */
    private AsyncHttpTask.AsyncHttpJsonTask<PlaceAutoCompleteResult> mHttpTask =
            new AsyncHttpTask.AsyncHttpJsonTask<PlaceAutoCompleteResult>( PlaceAutoCompleteResult.class ) {
        @Override
        public void onJsonResult(PlaceAutoCompleteResult jsonResult) {
            if ( mResultHandler != null ) {
                mResultHandler.onPlaceAutoCompleteResult(jsonResult, getTaskId());
            }
        }
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
        public void onPlaceAutoCompleteResult(PlaceAutoCompleteResult result, long taskId);
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

    public static class PlaceAutoCompleteResult {
        public String status;
        public String error_message;
        public Prediction [] predictions;
    }

    public static class Prediction {
        public String description;
        public String place_id;
        public Term [] matched_substring;
        // public Term [] terms;
        public String[] types;

        @Override
        public String toString() { return description; }
    }

    public static class Term {
        public int length;
        public int offset;
    }

}
