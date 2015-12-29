package com.fleshkart.app;

/**
 * Created by rbp687 on 12/29/15.
 */
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.appengine.repackaged.com.google.gson.annotations.Expose;
import com.google.appengine.repackaged.com.google.gson.annotations.SerializedName;


/**
 * LocationData - object containing the configuration, address, latitude, longitude, and radius.
 *
 */
public class LocationData implements Parcelable {
    // As discussed with jayashree mimimum ditance for unequal setting to 50.
    private static final float MIMIMUM_DISTANCE_FOR_UNEQUAL = 50;
    @SerializedName("config")
    @Expose
    private String mConfig;      // Type - work or home
    @SerializedName("address")
    @Expose
    private String mAddress;       // label from address or location name

    @SerializedName("latitude")
    @Expose
    private Double mLatitude;    // Latitude.
    @SerializedName("longitude")
    @Expose
    private Double mLongitude;   // Longitude.
    @SerializedName("radius")
    @Expose
    private float mRadius;       // Radius in meters
    @SerializedName("name")
    @Expose
    private String name;       // Name of the meaningful location

    private int    mId;           // Id of the location in CE's table so we can modify it if needed
    private String mBusinessType; //Type of the business coming from Places API
    private boolean mApplyAll;    //True means it should be applicable to all similar types


    public LocationData() {
    }

    public LocationData(LocationData src) {
        mConfig = src.mConfig;
        mAddress = src.mAddress;
        mLatitude = src.mLatitude;
        mLongitude = src.mLongitude;
        mRadius = src.mRadius;
        mId = src.mId;
        name = src.name;
        mBusinessType = src.mBusinessType;
        mApplyAll = src.mApplyAll;
    }

    /**
     * Location data with all info
     * @param   config      Contains the mode configuration, i.e. home.
     * @param   address       Long label for address.
     * @param   latitude    Latitude.
     * @param   longitude   Longitude.
     * @param   radius      radius around the marker.
     * @param   _name        name of the meaningful location.
     */
    public LocationData(String config, String address, double latitude, double longitude,
                        float radius, String _name) {
        mConfig = config;
        mAddress = address;
        mLatitude = latitude;
        mLongitude = longitude;
        this.name = _name;
        // Make sure to bound previous radius to new limit
        mRadius = Math.max(LocationConstants.MAP_MIN_RADIUS, Math.min(radius, LocationConstants.MAP_MAX_RADIUS));
    }

    /**
     * Location data with all info
     * @param   config      Contains the mode configuration, i.e. home.
     * @param   address       Long label for address.
     * @param   latitude    Latitude.
     * @param   longitude   Longitude.
     * @param   radius      radius around the marker.
     */
    public LocationData(String config, String address, double latitude, double longitude, float radius,
                        int id, String name, String businessType, boolean applyAll) {
        mConfig = config;
        mAddress = address;
        mLatitude = latitude;
        mLongitude = longitude;
        // Make sure to bound previous radius to new limit
        mRadius = Math.max(LocationConstants.MAP_MIN_RADIUS, Math.min(radius, LocationConstants.MAP_MAX_RADIUS));

        mId           = id;
        this.name         = name;
        mBusinessType = businessType;
        mApplyAll     = applyAll;
    }

    /**
     * Returns configuration, ex. home.
     * @return   String
     */
    public String getConfig() {
        return mConfig;
    }

    /**
     * Update configuration, ex. home.
     * @param config
     */
    public void setConfig(String config) {
        mConfig = config;
    }

    /** Update configuration, ex. home.
     * @param address
     */
    public void setAddress(String address) {
        mAddress = address;
    }

    public String getAddress() {
        return mAddress;
    }

    /**
     * Returns latitude.
     * @return  double
     */
    public Double getLatitude() {
        return mLatitude;
    }

    /**
     * Returns longitude.
     * @return  double
     */
    public Double getLongitude() {
        return mLongitude;
    }

    public void setLatitude(Double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setLongitude(Double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }


    /**
     * Update Type of the business
     * @param type
     */
    public void setBusinessType(String type) { mBusinessType = type; }

    /**
     * Returns latitude and latitude.
     * @return  LatLng
     */
    public LatLng getLatLng() {
        return new LatLng(mLatitude, mLongitude);
    }

    /**
     * Allows the updating of the Latitude and Longitude.
     * @param   latitude    Latitude.
     * @param   longitude   Longitude.
     */
    public void setLatLng(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    /**
     * Return radius.
     * @return float
     */
    public float getRadius() {
        return mRadius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Whether location is available
     * @return
     */
    public boolean hasLocation() {
        return (mAddress != null);
    }

    /**
     * Returns the short label of the address, i.e the part of the string before the
     * first comma.
     * @return   String   short label.
     */
    public String getShortLabel() {
        // Parse the label as an address delimited by comma
        final String longLabel = getLongLabel();
        int len = longLabel.indexOf(LocationConstants.LOCATION_ADDRESS_DELIM_TOKEN);
        if (len == -1) {
            return longLabel;
        }
        return longLabel.substring(0, len);
    }

    /**
     * Returns the address long label.
     * @return   String     long label.
     */
    public String getLongLabel() {
        return mAddress == null? "" : mAddress;
    };

    /**
     * Returns a string for logging the contents of the location data object.
     * @return  String
     */
    public String toString() {
        return "LocationData:" + mConfig + " " + getLongLabel() + " latLng:"
                + mLatitude + "  " + mLongitude + " radius:" + mRadius
                + " name:" + name;
    };

    private String mPlaceId;
    private String mIconUrl;
    private String mPhotoReference;

    public String getPlaceId () { return mPlaceId; }
    public void setPlaceId(String placeId) { mPlaceId = placeId; }

    public String getPhotoReference() { return mPhotoReference; }
    public void setPhotoReference(String photoReference) { mPhotoReference = photoReference; }

    public String getIconUrl() { return mIconUrl; }
    public void setIconUrl(String iconUrl) { mIconUrl = iconUrl; }

    /**
     * Returns type of the business stored in the CE DB
     * @return
     */
    public String getBusinessType() { return mBusinessType; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeFloat(mRadius);
        if(mAddress != null) {
            dest.writeInt(1);
            dest.writeString(mAddress);
        } else {
            dest.writeInt(0);
        }
        if(mConfig != null) {
            dest.writeInt(1);
            dest.writeString(mConfig);
        } else {
            dest.writeInt(0);
        }
        if(name != null) {
            dest.writeInt(1);
            dest.writeString(name);
        } else {
            dest.writeInt(0);
        }
    }

    protected LocationData(Parcel in) {
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mRadius = in.readFloat();
        if(in.readInt() != 0){
            mAddress = in.readString();
        }
        if(in.readInt() != 0){
            mConfig = in.readString();
        }
        if (in.readInt() != 0){
            name = in.readString();
        }
    }

    public static final Creator<LocationData> CREATOR = new Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel source) {
            return new LocationData(source);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationData that = (LocationData) o;
        float [] distance = new float[3];
        if (mConfig != null ? !mConfig.equals(that.mConfig) : that.mConfig != null) return false;
        Location.distanceBetween(mLatitude,mLongitude,that.mLatitude,that.mLongitude,distance);
        if (distance.length > 0 && distance[0] > MIMIMUM_DISTANCE_FOR_UNEQUAL) {
            return false;
        } else {
            return  true;
        }
    }

    @Override
    public int hashCode() {
        int result = mConfig != null ? mConfig.hashCode() : 0;
        result = 31 * result + (mAddress != null ? mAddress.hashCode() : 0);
        result = 31 * result + (mLatitude != null ? mLatitude.hashCode() : 0);
        result = 31 * result + (mLongitude != null ? mLongitude.hashCode() : 0);
        result = 31 * result + (mRadius != +0.0f ? Float.floatToIntBits(mRadius) : 0);
        return result;
    }
}