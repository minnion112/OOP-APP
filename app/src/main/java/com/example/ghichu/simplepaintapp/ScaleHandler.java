package com.example.ghichu.simplepaintapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.ScaleGestureDetector;

public class ScaleHandler extends ScaleGestureDetector.SimpleOnScaleGestureListener
{
    public static final float MIN_WIDTH = 5.0f;
    public static final float MAX_WIDTH = 100.0f;

    private float scaleFactor;
    private ScaleChangedListener listener;

    public ScaleHandler()
    {
        scaleFactor = 60.0f;
    }

    public void setOnScaleChangedListener (ScaleChangedListener scaleChangedListener)
    {
        this.listener = scaleChangedListener;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector)
    {
        // get the scale pen size icon
        GradientDrawable gradientDrawable = listener.onScaleIconRequired();
        // get the scale as pixels
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                listener.getContextResources().getDisplayMetrics()
        );
        // set the size of the pen icon to the pixels
        gradientDrawable.setStroke((int) px, ColourManager.getPenSizeRimColour(listener.getContext()));
        listener.onScaleStarted();

        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector)
    {
        // increment the scale factor
        scaleFactor *= detector.getScaleFactor();
        // ensure the factor is between a minimum and a maximum
        scaleFactor = Math.max(5.0f, Math.min(scaleFactor, 100.0f));
        // cue the listener to change the pen size icon
        listener.onScaleChanged(scaleFactor);

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector)
    {
        // cue the listener
        listener.onScaleEnded();
    }

    public interface ScaleChangedListener
    {
        GradientDrawable onScaleIconRequired();
        Context getContext();
        Resources getContextResources();
        void onScaleStarted();
        void onScaleChanged(float scaleFactor);
        void onScaleEnded ();
    }
}