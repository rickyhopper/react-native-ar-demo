package com.ardemo.viewmanager;

import android.graphics.PointF;

import com.ardemo.util.GeoUtils;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickyh on 8/30/16.
 */
public class AnnotationARViewManager extends SimpleViewManager<AnnotationARView> {

    public static final String REACT_CLASS = "AnnotationARView";

    private static final int NO_VALUE = -999;

    // list of annotations
    // TODO: may need to change to contain screen positions, since these shouldn't be calculated on draw
    private List<LatLng> annotationList = new ArrayList<>();

    // orientation data (for annotation display calculations
    private double heading = NO_VALUE;
    private double pitch = NO_VALUE;
    private float horizonSlope = NO_VALUE;

    // location data (for annotation direction calculations)
    private LatLng location = new LatLng(0,0);

    @Override
    protected AnnotationARView createViewInstance(ThemedReactContext reactContext) {
        return new AnnotationARView(reactContext);
    }

    @ReactProp(name = "horizonSlope", defaultFloat = 0f)
    public void setHorizonSlope(AnnotationARView view, float horizonSlope) {
        this.horizonSlope = horizonSlope;
    }

    @ReactProp(name = "heading", defaultDouble = 0.0)
    public void setHeading(AnnotationARView view, double heading) {
        this.heading = heading;
        recalculateAnnotationLocations(view);
    }

    @ReactProp(name = "pitch", defaultDouble = 0.0)
    public void setPitch(AnnotationARView view, double pitch) {
        this.pitch = pitch;
    }

    @ReactProp(name = "lat", defaultFloat = 0f)
    public void setLat(AnnotationARView view, float lat) {
        this.location.setLatitude(lat);
    }

    @ReactProp(name = "lon", defaultFloat = 0f)
    public void setLon(AnnotationARView view, float lon) {
        this.location.setLongitude(lon);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    private void recalculateAnnotationLocations(AnnotationARView view) {
        // get screen width / height
        int w = view.getMeasuredWidth();
        int h = view.getMeasuredHeight();
        double wAngle, hAngle; // width and height angle (v/h is too confusing here)

        double fov = 100; // field of view, in degrees - MIGHT NOT NEED
        if (w > h) {
            // landscape
            wAngle = fov / 2; // wider
            hAngle = fov / 3; // thinner
        } else {
            // portrait
            wAngle = fov / 3; // thinner
            hAngle = fov / 2; // wider
        }
        double wF = ((double) w) / (2.0*Math.tan(Math.toRadians(wAngle))); // so that x degrees = off screen
        double hF = ((double) h) / (2.0*Math.tan(Math.toRadians(hAngle))); // so that y degrees = off screen

        annotationList.clear();
        // pins around dorothea dix field
        annotationList.add(new LatLng(35.769461, -78.663761));
        annotationList.add(new LatLng(35.768335, -78.663105));
        annotationList.add(new LatLng(35.770308, -78.663586));
        annotationList.add(new LatLng(35.770031, -78.664480));

        // clear all current screen positions
        view.getAnnotationData().clear();

        // for each pin, calculate screen location based on the angles given
        for (LatLng pinLoc : annotationList) {
            double bearing = GeoUtils.getBearing(location, pinLoc);
            if (GeoUtils.getBearingDifference(heading, bearing) < 90) {
                AnnotationDrawData dd = new AnnotationDrawData();

                // get screen position
                float xOffset = (float) (wF * Math.tan(Math.toRadians(GeoUtils.getBearingDifference(heading, bearing))));
                float x = (GeoUtils.isAnnotationToRightOfView(heading, bearing) ?
                        (((float) w) / 2.0f) + xOffset :
                        (((float) w) / 2.0f) - xOffset);

                float yOffset = (float) (hF * Math.tan(Math.toRadians(GeoUtils.getBearingDifference(0, pitch))));
                float y = (GeoUtils.isAnnotationToRightOfView(0, pitch) ?
                        (((float) h) / 2.0f) - yOffset :
                        (((float) h) / 2.0f) + yOffset);
                dd.pos = new PointF(x,y);

                // get distance
                double dist = GeoUtils.distanceLatLng(location, pinLoc);
                if (dist < 3) {
                    dd.proximity = AnnotationARView.Proximity.HERE;
                } else if (dist < 10) {
                    dd.proximity = AnnotationARView.Proximity.CLOSE;
                } else if (dist < 30) {
                    dd.proximity = AnnotationARView.Proximity.FAR;
                } else {
                    dd.proximity = AnnotationARView.Proximity.VERY_FAR;
                }

                view.getAnnotationData().add(dd);
            }
        }

        view.invalidate();
    }
}
