package com.ardemo.viewmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickyh on 9/1/16.
 */
public class AnnotationContainer {

    public static final int NO_DATA = -1;

    private List<Annotation> annotations;
    private int screenWidth;
    private int screenHeight;

    public AnnotationContainer() {
        screenWidth = NO_DATA;
        screenHeight = NO_DATA;
        annotations = new ArrayList<>();
    }

    public int getAnnotationsLength() {
        return annotations.size();
    }

    public void addAnnotation(Annotation a) {
        annotations.add(a);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

}
