package com.dragosholban.androidfacedetection;

import android.support.v7.app.AppCompatActivity;

public class InterClass {
    private GraphicOverlay mGraphicOverlay;
    private AppCompatActivity activity;

    public GraphicOverlay getmGraphicOverlay() {
        return mGraphicOverlay;
    }

    public void setmGraphicOverlay(GraphicOverlay mGraphicOverlay) {
        this.mGraphicOverlay = mGraphicOverlay;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
}
