package com.androidfacedetection;

public class ComputingParameters {
    private static double[] areas;
    private static double[] angles;

    public static double[] getAreas() {
        return areas;
    }

    public static void setAreas(double[] areas) {
        ComputingParameters.areas = areas;
    }

    public static double[] getAngles() {
        return angles;
    }

    public static void setAngles(double[] angles) {
        ComputingParameters.angles = angles;
    }
}
