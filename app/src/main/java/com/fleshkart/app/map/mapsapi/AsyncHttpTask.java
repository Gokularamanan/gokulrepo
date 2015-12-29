package com.fleshkart.app.map.mapsapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.fleshkart.app.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

public class AsyncHttpTask {
    private static final String TAG = "AsyncHttpTask";

    /**
     * Helper function to format http request url.
     * @param baseUrl
     * @param parameters
     * @return URL
     */
    protected static URL formatUrl(String baseUrl, Bundle parameters) {
        URL url = null;
        StringBuilder sb = new StringBuilder(baseUrl);

        try {
            if (parameters != null) {
                boolean bFirst = true;
                for ( String key : parameters.keySet() ) {
                    String value = parameters.getString(key);
                    if ( !TextUtils.isEmpty(value) ) {
                        sb.append( bFirst ? "?" : "&" );
                        sb.append( key );
                        sb.append( "=" );
                        sb.append( URLEncoder.encode(value, "UTF-8") );
                        bFirst = false;
                    }
                }
            }

            url = new URL( sb.toString() );
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, e.toString());
            Logger.e( TAG, "Message: " + e.getMessage() != null ? e.getMessage() : "null" );
        } catch (MalformedURLException e) {
            Logger.e(TAG, e.toString());
            Logger.e( TAG, "Message: " + e.getMessage() != null ? e.getMessage() : "null" );
        }

        return url;
    }

    /**
     * Used to make http calls with Json response.
     * @param <T> Java concrete class representing the json response object.
     */
    public static abstract class AsyncHttpJsonTask<T> extends AsyncTask<Void, Void, T> {
        private static final String TAG = "AsyncHttpJsonTask";

        private Class<T> mResultJsonClass;
        protected URL mUrl = null;

        public AsyncHttpJsonTask(Class<T> resultJsonClass) {
            mResultJsonClass = resultJsonClass;
        }

        protected void initUrl(String baseUrl, Bundle parameters) {
            mUrl = formatUrl( baseUrl, parameters );
        }

        @Override
        protected T doInBackground(Void... voids) {
            if ( mResultJsonClass == null ) {
                Logger.e( TAG, "doInBackground(): mResultJsonClass=null. Need to init ResultJsonClass first." );
                return null;
            }

            if ( mUrl == null ) {
                Logger.e( TAG, "doInBackground(): mUrl=null. Need to init URL first." );
                return null;
            }

            // Debugging.
            if (Logger.DEVELOPMENT) {
                Logger.d( TAG, mUrl.toString() );
            }

            StringBuilder sb = new StringBuilder();
            String line;

            try {
                HttpsURLConnection conn = (HttpsURLConnection) mUrl.openConnection();
                int respCode = conn.getResponseCode();
                if (respCode != HttpsURLConnection.HTTP_OK) {
                    Logger.e(TAG, "Bad response from server: " + respCode);
                    return null;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), Charset.forName("UTF-8") ) );
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

            } catch (IOException e) {
                Logger.e( TAG, e.toString() );
                Logger.e( TAG, "Message: " + e.getMessage() != null ? e.getMessage() : "null" );
                return null;
            }

            String jsonStr = sb.toString();

            // Debugging
            if ( Logger.DEVELOPMENT ) {
                try {
                    Logger.d( TAG, "onJsonResult:\n");
                    Logger.d(TAG, (new JSONObject(jsonStr)).toString(4) );
                } catch ( JSONException e) {
                    Logger.d( TAG, e.toString() );
                    Logger.e( TAG, "Message: " + e.getMessage() != null ? e.getMessage() : "null" );
                }
            }

            T result = null;
            try {
                result = (new Gson()).fromJson( jsonStr, mResultJsonClass );
            } catch ( JsonSyntaxException e ) {
                Logger.e( TAG, e.toString() );
                Logger.e( TAG, "Message: " + e.getMessage() != null ? e.getMessage() : "null" );
            }

            return result;
        }

        @Override
        public void onPostExecute(T result) {
            onJsonResult( result );
        }

        /**
         * Derived class to implement handling of returned json object.
         * @param json
         */
        public abstract void onJsonResult(T json);
    }

    /**
     * Used to make http calls with image response.
     */
    public static abstract class AsyncHttpBitmapTask extends AsyncTask<Void, Void, Bitmap> {
        private static final String TAG = "AsyncHttpBitmapTask";

        private URL mUrl = null;

        public void initUrl(String baseUrl, Bundle parameters) {
            mUrl = formatUrl( baseUrl, parameters );
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            if (mUrl == null) {
                Logger.e(TAG, "doInBackground(): mUrl=null. Need to init URL first.");
                return null;
            }

            Bitmap bmp = null;

            try {
                URLConnection conn = mUrl.openConnection();
                /* int respCode = conn.getResponseCode();
                if (respCode != HttpsURLConnection.HTTP_OK) {
                    Logger.e(TAG, "Bad response from server: " + respCode);
                    return null;
                } */

                bmp = BitmapFactory.decodeStream(conn.getInputStream());

            } catch (IOException e) {
                Logger.e(TAG, e.toString());
                Logger.e(TAG, "Message: " + e.getMessage() != null ? e.getMessage() : "null");
                return null;
            }

            return bmp;
        }

        @Override
        public void onPostExecute(Bitmap result) {
            onBitmapResult(result);
        }

        /**
         * Derived class to implement handling of returned bitmap object.
         *
         * @param bitmap
         */
        public abstract void onBitmapResult(Bitmap bitmap);
    }

}
