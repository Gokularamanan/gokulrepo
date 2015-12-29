package com.fleshkart.app.map.mapsapi;

import android.content.Context;
import android.os.Bundle;


/**
 * Wrapper to Google Maps Geocoding api service:
 *   https://developers.google.com/maps/documentation/geocoding
 */
public class Geocode implements BufferDelayedTasker.DelayedRunnableTask {
    private static final String TAG = "Geocode";

    public static final String URL = MapsApi.BASE_URL + "/geocode/json";

    public static final String ADDRESS = "address";
    public static final String BOUNDS = "bounds";
    public static final String REGION = "region";
    public static final String COMPONENTS = "components";

    public static final String LATLNG = "latlng";
    public static final String RESULT_TYPE = "result_type";
    public static final String LOCATION_TYPE = "location_type";

    /******************************************************************
     * Geocoding: Converts address to lat/lng.
     *   https://developers.google.com/maps/documentation/geocoding/#geocoding
     *
     * Usage Example:
     *   new Geocode( context, "1000 Enterprise Way, Mountain View, CA 94043", resultHandler )
     *       .runTask();
     *
     * @param context Context
     * @param address Required.
     * @param resultHandler Result handler callback, invoked at later time on main thread.
     ******************************************************************/

    public Geocode(Context context,
                   String address,
                   ResultHandler resultHandler) {

        Bundle params = MapsApi.createGeocodeQueryParams( context );
        params.putString( ADDRESS, address );

        mResultHandler = resultHandler;
        mHttpTask.initUrl( URL, params );
        mHttpTask.mUrl = MapsApi.signGeocodeUrl( context, mHttpTask.mUrl );
    }

    /******************************************************************
     * Reverse Geocoding: Converts lat/lng to address.
     *   https://developers.google.com/maps/documentation/geocoding/#ReverseGeocoding
     *
     * Usage Example:
     *   new Geocode( context, 37.403528, -122.035998, resultHandler )
     *       .runTask();
     *
     * @param context Context
     * @param lat Required.
     * @param lng Required.
     * @param resultHandler Result handler callback, invoked at later time on main thread.
     ******************************************************************/

    public Geocode(Context context,
                   double lat, double lng,
                   ResultHandler resultHandler) {

        Bundle params = MapsApi.createGeocodeQueryParams( context );
        params.putString( LATLNG,  lat + "," + lng );

        mResultHandler = resultHandler;
        mHttpTask.initUrl( URL, params );
        mHttpTask.mUrl = MapsApi.signGeocodeUrl( context, mHttpTask.mUrl );
    }

    /**
     * AsyncHttpTask.
     */
    private AsyncHttpTask.AsyncHttpJsonTask<GeocodeResult> mHttpTask =
            new AsyncHttpTask.AsyncHttpJsonTask<GeocodeResult>( GeocodeResult.class ) {
        @Override
        public void onJsonResult(GeocodeResult jsonResult) {
            if ( mResultHandler != null ) {
                mResultHandler.onGeocodeResult( jsonResult, getTaskId() );
            }
        }
    };

    /**
     * BufferDelayedTasker.DelayedRunnableTask
     */
    private long mTaskId = -1L;
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
        public void onGeocodeResult(GeocodeResult result, long taskId);
    }

    private ResultHandler mResultHandler;

    /******************************************************************
     * Java model classes to hold json object from geocode response:
     *   https://developers.google.com/maps/documentation/geocoding/#GeocodingResponses
     *
     * Gson library is used to automatically convert json objects to respective
     * java model classes.  This removes the need to explicitly implement
     * API-response-specific json parsing functions.
     *
     * Take !!!!!GREAT CARE!!!!! in defining member variable names
     * to match with those returned from json response objects.
     ******************************************************************/

    public static class GeocodeResult {
        /**
         * https://developers.google.com/maps/documentation/geocoding/#StatusCodes
         * https://developers.google.com/maps/documentation/geocoding/#reverse-status
         */
        public String status;

        /**
         * https://developers.google.com/maps/documentation/geocoding/#ErrorMessages
         */
        public String error_message;

        /**
         * https://developers.google.com/maps/documentation/geocoding/#Results
         * https://developers.google.com/maps/documentation/geocoding/#reverse-example
         */
        public GeocodeAddress [] results;
    }

    public static class GeocodeAddress {
        public String[] types;
        public String formatted_address;
        public AddressComponent [] address_components;
        public MapsApi.Geometry geometry;
        // public String location_type;
        // public String partial_match;
    }

    public static class AddressComponent {
        public String long_name;
        public String short_name;
        public String[] types;
    }
}
