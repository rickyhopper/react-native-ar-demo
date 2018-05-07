package com.ardemo.viewmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.ardemo.R;
import com.facebook.react.uimanager.ThemedReactContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rickyh on 8/30/16.
 */
public class AnnotationARView extends View {

    public enum Proximity {
        HERE, // 1x scale
        CLOSE, // .8x scale
        FAR, // .5x scale
        VERY_FAR // .2x scale
    }

    private List<AnnotationDrawData> annotationPos;
    private Drawable pinDrawable;
    private Map<Proximity, Bitmap> bitmapMap;
    private Paint paint;

    public AnnotationARView(ThemedReactContext context) {
        super(context);
        annotationPos = new ArrayList<>();
        pinDrawable = context.getResources().getDrawable(R.drawable.map_marker);
        Bitmap srcPin = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker);
        bitmapMap = new HashMap<>();
        bitmapMap.put(Proximity.HERE, srcPin);
        bitmapMap.put(Proximity.CLOSE,
                Bitmap.createScaledBitmap(srcPin, (int) (srcPin.getWidth() * 0.8), (int) (srcPin.getHeight()* 0.8), false));
        bitmapMap.put(Proximity.FAR,
                Bitmap.createScaledBitmap(srcPin, (int) (srcPin.getWidth() * 0.5), (int) (srcPin.getHeight()* 0.5), false));
        bitmapMap.put(Proximity.VERY_FAR,
                Bitmap.createScaledBitmap(srcPin, (int) (srcPin.getWidth() * 0.2), (int) (srcPin.getHeight()* 0.2), false));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.red));
        paint.setAlpha(255);
        paint.setStrokeWidth(4.0f);
    }

    public List<AnnotationDrawData> getAnnotationData() {
        return annotationPos;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw pins at location
        for (AnnotationDrawData dd : annotationPos) {
            Bitmap bmp = bitmapMap.get(dd.proximity);
            canvas.drawBitmap(bmp, dd.pos.x - (bmp.getWidth()/2), dd.pos.y - bmp.getHeight(), paint);
        }
    }
}
