package com.fleshkart.app.map.mapsapi;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;

import com.fleshkart.app.R;
import com.fleshkart.app.utils.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MapsApi {
    private static final String TAG = "MapsApi";

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api";

    public static final String KEY = "key";
    public static String MAPS_API_KEY = null;
    public static final String CLIENT = "client";
    public static String MAPS_CLIENT_ID = null;
    public static String SIGNATURE = "signature";
    public static String MAPS_PRIVATE_KEY = null;

    public static final String STATUS_OK = "OK";
    public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
    public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
    public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";
    public static final String STATUS_UNKNOWN_ERROR = "UNKNOWN_ERROR";

    protected static Bundle createPlacesQueryParams(Context context) {
        if ( MAPS_API_KEY == null ) {
            // Production key, being used by Moto Production apps out in field...
            MAPS_API_KEY = context.getResources().getString( R.string.maps_browser_key );
        }

        Bundle params = new Bundle();
        params.putString( KEY, MAPS_API_KEY );
        return params;
    }

    protected static Bundle createGeocodeQueryParams(Context context) {
        if ( MAPS_CLIENT_ID == null ) {
            // Production key, being used by Moto Production apps out in field...
            MAPS_CLIENT_ID = context.getResources().getString( R.string.maps_client );
        }

        Bundle params = new Bundle();
        params.putString( CLIENT, MAPS_CLIENT_ID );
        return params;
    }

    protected static URL signGeocodeUrl(Context context, URL url) {
        if ( MAPS_PRIVATE_KEY == null ) {
            MAPS_PRIVATE_KEY = context.getResources().getString( R.string.maps_key );
        }

        String signature = hmacSha1( url.getFile(), MAPS_PRIVATE_KEY );
        String urlString = url.toString() + "&" + SIGNATURE + "=" + signature;
        try {
            url = new URL( urlString );
        } catch (MalformedURLException e) {
            Logger.e(TAG, e.toString());
            Logger.e( TAG, "Message: " + e.getMessage() != null ? e.getMessage() : "null" );
        }
        return url;
    }

    public static String hmacSha1(String value, String keyString) {
        try {
            // Convert the key from 'web safe' base 64 to binary
            keyString = keyString.replace('-', '+');
            keyString = keyString.replace('_', '/');
            SecretKeySpec signingKey = new SecretKeySpec( Base64.decode(keyString, Base64.DEFAULT), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init( signingKey );
            String signature = Base64.encodeToString(mac.doFinal(value.getBytes()), Base64.DEFAULT);
            // Convert the signature to 'web safe' base 64
            signature = signature.replace('+', '-');
            signature = signature.replace('/', '_');
            return signature;
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeyException e) {
        }
        return value;
    }

    /******************************************************************
     * Java model classes to hold json object from Maps api response:
     *
     * Gson library is used to automatically convert json objects to respective
     * java model classes.  This removes the need to explicitly implement
     * API-response-specific json parsing functions.
     *
     * Take !!!!!GREAT CARE!!!!! in defining member variable names
     * to match with those returned from json response objects.
     ******************************************************************/

    public static class Location {
        public float lat;
        public float lng;
    }

    public static class Viewport {
        public Location northeast;
        public Location southwest;
        //? public Location southeast;
        //? public Location northwest;
    }

    public static class Geometry {
        public Location location;
        public String location_type;
        public Viewport viewport;
    }

    public static class Place {
        public String icon;
        // public String id;
        public MapsApi.Geometry geometry;
        public String name;
        // public OpeningHours opening_hours;
        public Photo [] photos;
        public String place_id;
        public String scope;
        // public AltId [] alt_ids;
        // public int price_level;
        public float rating;
        // public String reference;
        public Type [] types;
        public String vicinity;
        public String formatted_address;
    }

    public static class OpeningHours {
        public boolean open_now;
    }

    public static class Photo {
        public String photo_reference;
        public int width;
        public int height;
        public String[] html_attributions;
    }

    public enum Type {
        accounting,
        airport,
        amusement_park,
        aquarium,
        art_gallery,
        atm,
        bakery,
        bank,
        bar,
        beauty_salon,
        bicycle_store,
        book_store,
        bowling_alley,
        bus_station,
        cafe,
        campground,
        car_dealer,
        car_rental,
        car_repair,
        car_wash,
        casino,
        cemetery,
        church,
        city_hall,
        clothing_store,
        convenience_store,
        courthouse,
        dentist,
        department_store,
        doctor,
        electrician,
        electronics_store,
        embassy,
        establishment,
        finance,
        fire_station,
        florist,
        food,
        funeral_home,
        furniture_store,
        gas_station,
        general_contractor,
        grocery_or_supermarket,
        gym,
        hair_care,
        hardware_store,
        health,
        hindu_temple,
        home_goods_store,
        hospital,
        insurance_agency,
        jewelry_store,
        laundry,
        lawyer,
        library,
        liquor_store,
        local_government_office,
        locksmith,
        lodging,
        meal_delivery,
        meal_takeaway,
        mosque,
        movie_rental,
        movie_theater,
        moving_company,
        museum,
        night_club,
        painter,
        park,
        parking,
        pet_store,
        pharmacy,
        physiotherapist,
        place_of_worship,
        plumber,
        police,
        post_office,
        real_estate_agency,
        restaurant,
        roofing_contractor,
        rv_park,
        school,
        shoe_store,
        shopping_mall,
        spa,
        stadium,
        storage,
        store,
        subway_station,
        synagogue,
        taxi_stand,
        train_station,
        travel_agency,
        university,
        veterinary_care,
        zoo
    }

}
