package com.fleshkart.app.map;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fleshkart.app.R;
import com.fleshkart.app.ui.animation.AbstractAnimation;
import com.fleshkart.app.ui.animation.RadiusValueAnimator;
import com.fleshkart.app.utils.Logger;
import com.google.android.gms.maps.MapFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapPickerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapPickerFragment extends MapFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    View mContainer;
    RadiusValueAnimator<FrameLayout.LayoutParams> mRadiusAnimator = null;
    private float mZoomLevel = 0f;
    ImageView mCircleView;
    private static final String TAG = "PickerMapFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MapPickerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapPickerFragment newInstance(String param1, String param2) {
        MapPickerFragment fragment = new MapPickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.location_map_controls, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public float getZoomLevel() {
        return mZoomLevel;
    }

    public void setZoomLevel(float level) {
        mZoomLevel = level;
    }

    public int getMapWidth() {
        return mContainer.getWidth();
    }

    public void showCircleWidth(int width, int duration) {

        if (duration == 0) {
            setCircleWidth(width);
        }
        if (Logger.DEVELOPMENT) {
            Logger.d(TAG, "showCircleWidth: width=", width);
        }

        // Set up the animator
        if (mRadiusAnimator == null) {
            final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mCircleView.getLayoutParams();
            // Only animate width and height of the circle
            mRadiusAnimator = new RadiusValueAnimator<FrameLayout.LayoutParams>("RadiusAnimator",
                    new AbstractAnimation<FrameLayout.LayoutParams>(lp) {
                        Point mStart = new Point();
                        Point mEnd = new Point();

                        public void setValues(FrameLayout.LayoutParams start, FrameLayout.LayoutParams end) {
                            mStart.x = start.width;
                            mStart.y = start.height;
                            mEnd.x = end.width;
                            mEnd.y = end.height;
                        }

                        public void applyProgress(float progress) {
                            // for frame by frame debugging only
                            //Logger.d(TAG, "applyProgress: progress=", progress);
                            target.width = (int) (mStart.x + (mEnd.x - mStart.x) * progress);
                            target.height = (int) (mStart.y + (mEnd.y - mStart.y) * progress);
                        }
                    });

            mRadiusAnimator.setUpdateListener(
                    new android.animation.ValueAnimator.AnimatorUpdateListener() {
                        public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                            mCircleView.requestLayout();
                        }
                    });
        }

        // Animate to desired circle width
        mRadiusAnimator.animateTo(new FrameLayout.LayoutParams(width, width), duration);
    }

    public void setCircleWidth(int width) {
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)mCircleView.getLayoutParams();
        lp.width = width;
        lp.height = width;
        mCircleView.requestLayout();
    }

    public void showMyLocation(boolean showLocation) {
        //mCurrentLocationButton.setVisibility(showLocation ? View.VISIBLE : View.GONE);
    }
}
