package com.fleshkart.app.map.mapsapi;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;


/**
 * Google Places Search
 * https://developers.google.com/places/documentation/search
 */

public class PlaceSearch {
    private static final String TAG = "PlaceSearch";

    public static final String URL = MapsApi.BASE_URL + "/place";

    public static final String LOCATION = "location";
    public static final String RADIUS = "radius";
    public static final String KEYWORD = "keyword";
    public static final String NAME = "name";
    public static final String OPENNOW = "opennow";
    public static final String RANKBY = "rankby";
    public static final String TYPES = "types";

    public static final String QUERY = "query";
    public static final String MINPRICE = "minprice";
    public static final String MAXPRICE = "maxprice";

    /******************************************************************
     * NearbySearch:
     *   https://developers.google.com/places/documentation/search#PlaceSearchRequests
     *
     * Wrapper to NearbySearch api service:
     *   https://maps.googleapis.com/maps/api/place/nearbysearch/json?parameters
     *
     * Usage example:
     *   new PlaceSearch.NearbySearch( context, 37.403934, -122.036324, null, null, resultHandler )
     *       .runTask();
     ******************************************************************/

    public static class NearbySearch implements BufferDelayedTasker.DelayedRunnableTask {
        private static final String TAG = "NearbySearch";

        public static final String URL = PlaceSearch.URL + "/nearbysearch/json";

        private ResultHandler mResultHandler;

        private AsyncHttpTask.AsyncHttpJsonTask<PlaceSearchResult> mHttpTask =
                new AsyncHttpTask.AsyncHttpJsonTask<PlaceSearchResult>( PlaceSearchResult.class ) {
            @Override
            public void onJsonResult(PlaceSearchResult jsonResult) {
                if ( mResultHandler != null ) {
                    mResultHandler.onPlaceSearchResult( jsonResult, getTaskId() );
                }
            }

            // Analytics testing BEGIN
            @Override
            protected PlaceSearchResult doInBackground(Void... voids) {
                return super.doInBackground( voids );
            }
            // Analytics testing END
        };

        /**
         * @param context Context
         * @param lat Lat Required.
         * @param lng Lng Required.
         * @param keyword Optional. Can be null.
         * @param name Optional. Can be null.
         * @param resultHandler Result handler callback, invoked at later time on main thread.
         */
        public NearbySearch(Context context,
                            double lat, double lng,
                            String keyword, String name,
                            ResultHandler resultHandler) {

            Bundle params = MapsApi.createPlacesQueryParams( context );
            params.putString( LOCATION, lat + "," + lng );
            if ( !TextUtils.isEmpty(keyword) ) {
                params.putString( KEYWORD, keyword );
            }
            if ( !TextUtils.isEmpty(name) ) {
                params.putString(NAME, name);
            }

            params.putString( RANKBY, "distance" );
            params.putString(TYPES, MapsApi.Type.establishment.toString());
            mHttpTask.initUrl( URL, params );
            mResultHandler = resultHandler;
        }

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
    }

    /******************************************************************
     * TextSearch:
     *   https://developers.google.com/places/documentation/search#TextSearchRequests
     *
     * !!!!!===== IMPORTANT =====!!!!!
     * Using TextSearch API has a 10x multiplier effect on usage against quota limit.
     * !!!!!===== IMPORTANT =====!!!!!
     *
     * Usage example:
     *   PlaceSearch.ResultHandler resultHandler = new PlaceSearch.ResultHandler() {
     *       public onPlaceSearchResult(PlaceSearchResult result) {
     *           // Do my thing with returned result.
     *       }
     *   }
     *   new PlaceSearch.TextSearch( context, "Pizza in New York",
     *           null, null, null, resultHandler );
     ******************************************************************/

//    public static class TextSearch implements BufferDelayedTasker.DelayedRunnableTask {
//        private static final String TAG = "TextSearch";
//
//        public static final String URL = PlaceSearch.URL + "/textsearch/json";
//
//        private ResultHandler mResultHandler;
//
//        private AsyncHttpTask.AsyncHttpJsonTask<PlaceSearchResult> mHttpTask =
//                new AsyncHttpTask.AsyncHttpJsonTask<PlaceSearchResult>( PlaceSearchResult.class ) {
//            @Override
//            public void onJsonResult(PlaceSearchResult jsonResult) {
//                if ( mResultHandler != null ) {
//                    mResultHandler.onPlaceSearchResult( jsonResult, getTaskId() );
//                }
//            }
//
//// Analytics testing BEGIN
//            @Override
//            protected String doInBackground(Void... voids) {
//                ApiCounter.getInstance().incrCount( ApiCounter.ID_GOOGLE_PLACES );
//                return super.doInBackground( voids );
//            }
//// Analytics testing END
//
//        };
//
//        /**
//         *
//         * @param context Context
//         * @param query Query text. Required.
//         * @param lat Lat. Optional. Can be null.
//         * @param lng Lng. Optional. Can be null.
//         * @param radius In meters. Optional. Can be null. Max at 50,000 meters.
//         * @param resultHandler Result handler callback, invoked at later time on main thread.
//         */
//        public TextSearch(Context context,
//                          String query,
//                          Float lat, Float lng, Integer radius,
//                          ResultHandler resultHandler) {
//
//            Bundle params = MapsApi.createQueryParams( context );
//            params.putString( QUERY, query );
//            if ( lat != null && lng != null ) {
//                params.putString( LOCATION, lat.toString() + "," + lng.toString() );
//                if ( radius != null ) {
//                    params.putString( RADIUS, Integer.toString(radius) );
//                }
//            }
//            mHttpTask.initUrl( URL, params );
//            mResultHandler = resultHandler;
//        }
//
//        /**
//         * BufferDelayedTasker.DelayedRunnableTask
//         */
//        private long mTaskId = -1L;
//        @Override public void runTask() { mHttpTask.execute(); }
//        @Override public long getTaskId() { return mTaskId; }
//        @Override public void setTaskId(long taskId) { mTaskId = taskId; }
//    }


    /******************************************************************
     * RadarSearch:
     *   https://developers.google.com/places/documentation/search#RadarSearchRequests
     *
     * Usage example:
     *   PlaceSearch.ResultHandler resultHandler = new PlaceSearch.ResultHandler() {
     *       public onPlaceSearchResult(PlaceSearchResult result) {
     *           // Do my thing with returned result.
     *       }
     *   }
     *   new PlaceSearch.RadarSearch( context, 37.403934, -122.036324, 50, resultHandler );
     ******************************************************************/

    public static class RadarSearch implements BufferDelayedTasker.DelayedRunnableTask {
        private static final String TAG = "RadarSearch";

        public static final String URL = PlaceSearch.URL + "/radarsearch/json";

        private ResultHandler mResultHandler;

        private AsyncHttpTask.AsyncHttpJsonTask<PlaceSearchResult> mHttpTask =
                new AsyncHttpTask.AsyncHttpJsonTask<PlaceSearchResult>( PlaceSearchResult.class ) {
            @Override
            public void onJsonResult(PlaceSearchResult jsonResult) {
                if ( mResultHandler != null ) {
                    mResultHandler.onPlaceSearchResult( jsonResult, getTaskId() );
                }
            }
        };

        /**
         * To be implemented.
         */


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

    }

    /******************************************************************
     * Response handler interface
     ******************************************************************/

    public interface ResultHandler {
        /**
         * @param result Can be null.
         */
        public void onPlaceSearchResult(PlaceSearchResult result, long taskId);
    }

    /******************************************************************
     * Java model classes to hold json object from places search response:
     *   https://developers.google.com/places/documentation/search#PlaceSearchResponses
     *
     * Gson library is used to automatically convert json objects to respective
     * java model classes.  This removes the need to explicitly implement
     * API-response-specific json parsing functions.
     *
     * Take !!!!!GREAT CARE!!!!! in defining member variable names
     * to match with those returned from json response objects.
     ******************************************************************/

    public static class PlaceSearchResult {
        /**
         * https://developers.google.com/places/documentation/search#PlaceSearchStatusCodes
         */
        public String status;

        /**
         * https://developers.google.com/places/documentation/search#ErrorMessages
         */
        public String error_message;

        /**
         * https://developers.google.com/places/documentation/search#PlaceSearchResults
         */
        public MapsApi.Place[] results;

        public String[] html_attributions;
        public String next_page_token;
    }

}
