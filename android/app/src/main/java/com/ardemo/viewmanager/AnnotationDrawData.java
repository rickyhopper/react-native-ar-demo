package com.ardemo.viewmanager;

import android.graphics.PointF;

/**
 * Created by rickyh on 9/11/16.
 */
public class AnnotationDrawData {

    public int id;
    public String text;
    public PointF pos;
    public AnnotationARView.Proximity proximity;

    public AnnotationDrawData() {
        id = 0;
        text = "";
        pos = null;
        proximity = AnnotationARView.Proximity.HERE;
    }

}
