package com.fleshkart.app;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.fleshkart.app.map.MapPickerFragment;
import com.fleshkart.app.map.google.SphericalUtil;
import com.fleshkart.app.map.mapsapi.BufferDelayedTasker;
import com.fleshkart.app.map.mapsapi.Geocode;
import com.fleshkart.app.map.mapsapi.MapsApi;
import com.fleshkart.app.utils.Logger;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Created by rbp687 on 12/29/15.
 */
public class LocationPickerActivity extends FragmentActivity implements
        GoogleMap.OnMapClickListener,
        TextView.OnEditorActionListener,
        GoogleMap.OnCameraChangeListener {

    private static final String TAG = "LocationPicker";
    private boolean mIsEditing = false;
    private MapPickerFragment mMapFragment;
    private GoogleMap mMap;
    private static int INVALID_CIRCLE_PIXEL_WIDTH = -1;
    // Return location.
    private LocationData mRtnLocation;

    @Override
    public void onMapClick(LatLng latLng) {
        if (Logger.DEVELOPMENT) {
            Logger.d(TAG, "onMapClick");
        }
        // Close keyboard editing
        if (isEditing()) {
            doEndEditing();
        }
    }

    protected boolean isEditing() {
        return (mIsEditing);
    }

    /**
     * Finish editing text.
     */
    protected void doEndEditing() {
        if (Logger.DEVELOPMENT) {
            Logger.d(TAG, "doEndEditing");
        }
        mIsEditing = false;
    }

    // Used for implementing OnEditorActionListener
    //v	The view that was clicked.
    //actionId	Identifier of the action. This will be either the identifier you supplied, or EditorInfo.IME_NULL if being called due to the enter key being pressed.
    //event	If triggered by an enter key, this is the event; otherwise, this is null.

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Logger.DEVELOPMENT) {
            Logger.d(TAG, "onEditorAction actionId=", actionId);
        }


        doEndEditing();

        return true;
    }

    /****************************************************************
     * Type down location search.
     ****************************************************************/

    private SnapAddrToPlace mSnapAddrToPlace;


    private class SnapAddrToPlace {
        double mLat, mLng;

        SnapAddrToPlace(double lat, double lng) {
            mLat = lat;
            mLng = lng;
        }
    }

    ;
    // Average US city block is 250 meters long.
    // Setting snapping threshold radius dist to 150 meters.
    // 0.001 radian degress ~ 100m.
    private static final double SNAPPING_RAD_DIST = 0.001;
    private static final double SNAPPING_RAD_DIST_SQR = SNAPPING_RAD_DIST * SNAPPING_RAD_DIST;

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (mMapFragment.getZoomLevel() != cameraPosition.zoom) {
            mMapFragment.setZoomLevel(cameraPosition.zoom);
            showRadiusOnZoom(mRtnLocation.getRadius());
        }

        // Cache current address.
        mRtnLocation.setLatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

        // Generally we update the display of the address at center of map.
        // (AKA Uber map panning style)
        boolean reverseGeocode = true;

        if (mSnapAddrToPlace != null) {
            // Exception is when we are displaying a place from explicit search result.
            reverseGeocode = false;

            // We turn snapping off when user pans map certain distance away from searched place.
            double diffLat = cameraPosition.target.latitude - mSnapAddrToPlace.mLat;
            double diffLng = cameraPosition.target.longitude - mSnapAddrToPlace.mLng;
            double distSqr = diffLat * diffLat + diffLng * diffLng;
            if (distSqr > SNAPPING_RAD_DIST_SQR) {
                reverseGeocode = true;


            }
        }

        // Look up address if needed.
        if (reverseGeocode) {
            lookupAddress(cameraPosition.target);
        }
    }




    /****************************************************************
     * Reverse Geocoding: Lat/Lng -> Address.
     * Used by map dragging.
     ****************************************************************/


    private void lookupAddress(LatLng latLng) {
        // Update address text and spinner
        // mLocationSearchTextView.setTextState( null, true, true );
/*        ImageView imageView = (ImageView) findViewById(R.id.current_location_icon);
        ProgressBar spinner = (ProgressBar) findViewById(R.id.current_location_spinner);
        imageView.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);*/

/*
        TextView textSummary = (TextView) findViewById(R.id.current_location_text2);
*/
/*
        Log.d(String.format("(%f, %f)", latLng.latitude, latLng.longitude);
*/

/*        // Disable confirm btn now while resolving for new location.
        mBtnConfirm.setEnabled(false);*/

        // Start async reverse geocoding.
        Geocode task = new Geocode(getApplicationContext(),
                latLng.latitude, latLng.longitude,
                mGoogleApiReverseGeocodeHandler);
        mCurrLocationUiTasker.pushTask(task);
    }
    private BufferDelayedTasker mCurrLocationUiTasker = new BufferDelayedTasker(750);

    private Geocode.ResultHandler mGoogleApiReverseGeocodeHandler = new Geocode.ResultHandler() {
        public void onGeocodeResult(Geocode.GeocodeResult result, long taskId) {
            if (LocationPickerActivity.this.isFinishing()) {
                return;  // Do nothing.
            }

            if (mCurrLocationUiTasker.isTaskStale(taskId)) {
                return;  // Do nothing.
            }

            if (result == null || !MapsApi.STATUS_OK.equals(result.status)) {
                // Didn't find a result.
                Log.d(null, null);
                return;
            }

            // Use the first returned result.
            Geocode.GeocodeAddress geoAddr = result.results[0];
/*            Log.d(geoAddr.formatted_address,
                    new LatLng(geoAddr.geometry.location.lat, geoAddr.geometry.location.lng));*/
        }
    };



    /**
     * Show the radius scaled to the current zoom level
     */
    public void showRadiusOnZoom(float radius) {
        int pixelWidth = calcCirclePixelWidth(mMapFragment, radius);
        if (pixelWidth != INVALID_CIRCLE_PIXEL_WIDTH) {
            mMapFragment.showCircleWidth(pixelWidth, 0);
        }
    }

    /**
     * calculate radius for the current zoom
     *
     * @param radius
     * @return
     */
    public int calcCirclePixelWidth(MapPickerFragment mapPickerFragment, float radius) {

        if (mMap.getProjection() != null) {
            final VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
            double mapDistanceWidth = SphericalUtil.computeDistanceBetween(visibleRegion.farLeft, visibleRegion.farRight);

            final int mapPixelWidth = mapPickerFragment.getMapWidth();
            int circlePixelWidth = (int) (mapPixelWidth / mapDistanceWidth * radius * 2);

            if (Logger.DEVELOPMENT) {
                Logger.d(TAG, "calcCirclePixelWidth: mapDistanceWidth: ", mapDistanceWidth, " mapPixelWidth:",
                        mapPixelWidth, " circleDistanceWidth:", radius * 2,
                        " circlePixelWidth:", circlePixelWidth);
            }
            return circlePixelWidth;
        }

        return INVALID_CIRCLE_PIXEL_WIDTH;
    }
}
