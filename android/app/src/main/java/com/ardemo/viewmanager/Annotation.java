package com.ardemo.viewmanager;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by rickyh on 9/1/16.
 */
public class Annotation {

    private LatLng location;
    private String text;
    private int screenX;
    private int screenY;

    public Annotation(LatLng ll, String t) {
        location = ll;
        text = t;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getScreenX() {
        return screenX;
    }

    public void setScreenX(int screenX) {
        this.screenX = screenX;
    }

    public int getScreenY() {
        return screenY;
    }

    public void setScreenY(int screenY) {
        this.screenY = screenY;
    }
}
