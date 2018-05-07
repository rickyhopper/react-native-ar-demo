package com.ardemo.util;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rickyh on 9/5/16.
 */
public class GeoUtils {

    private static String TAG = "GeoUtils";

    private static final double EARTH_RADIUS = 6371000; // meters

    public static final double BEARING_NORTH = 0.0; // degrees
    public static final double BEARING_SOUTH = 180.0; // degrees
    public static final double BEARING_WEST = 270.0; // degrees
    public static final double BEARING_EAST = 90.0; // degrees


    // concrete method to make a location from a LatLng
    private static Location makeLocation(LatLng ll) {
        Location loc = new Location("generated"); // name doesn't matter for this
        loc.setLatitude(ll.getLatitude());
        loc.setLongitude(ll.getLongitude());
        return loc;
    }

    // get distance between 2 LatLng points in meters
    public static double distanceLatLng(LatLng p1, LatLng p2) {
        // make location objects (they have a distance method)
        Location loc1 = makeLocation(p1);
        Location loc2 = makeLocation(p2);

        // return distance
        return loc1.distanceTo(loc2);
    }

    public static double distance3DtoLatLng(LatLng pos1, double alt, LatLng pos2) {
        double dist2d = distanceLatLng(new LatLng(pos1.getLatitude(), pos1.getLongitude()), pos2);
        return Math.sqrt(Math.pow(dist2d, 2) + Math.pow(alt, 2));
    }

    // get distance between 2 LatLng points in meters
    public static double getPathDistance(List<LatLng> path) {
        if (path == null || path.size() < 2) {
            return -1; // invalid
        }

        double dist = 0;

        // get distance of all edges
        for (int i = 0; i < path.size() - 1; i++) {
            dist += distanceLatLng(path.get(i), path.get(i + 1));
        }

        // return distance
        return dist;
    }

    // get bearing b/t two points in degrees
    public static double getBearing(LatLng p1, LatLng p2) {
        // make location objects (they have a bearing method)
        Location loc1 = makeLocation(p1);
        Location loc2 = makeLocation(p2);

        return loc1.bearingTo(loc2);
    }

    // get angle between 2 bearings
    public static double getBearingDifference(double a1, double a2) {
        // make sure all angles are [0, 360]
        if (a1 < 0) a1 += 360;
        if (a2 < 0) a2 += 360;
        // do calculation
        return Math.min((a1 - a2) < 0 ? a1 - a2 + 360 : a1 - a2, (a2 - a1) < 0 ? a2 - a1 + 360 : a2 - a1);
    }

    public static boolean isAnnotationToRightOfView(double heading, double annotationBearing) {
        // make sure all angles are [0, 360]
        if (heading < 0) heading += 360;
        if (annotationBearing < 0) annotationBearing += 360;

        if (heading < annotationBearing) {
            return ((annotationBearing - heading) < 180);
        } else {
            return ((heading - annotationBearing) > 180);
        }
    }

    // gets a new LatLng based on distance and bearing away from source point (pt).
    public static LatLng destPoint(LatLng pt, double bearing, double dist) {
        double earthRadius = 6378.1;
        dist = dist / 1000; // convert to km
        bearing = Math.toRadians(bearing);

        double lat1 = Math.toRadians(pt.getLatitude());
        double lon1 = Math.toRadians(pt.getLongitude());

        double lat2, lon2;

        // surround in try/catch, in case of divide by zero error or something
        try {
            lat2 = Math.asin(Math.sin(lat1) * Math.cos(dist / earthRadius) +
                    Math.cos(lat1) * Math.sin(dist / earthRadius) * Math.cos(bearing));

            lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(dist / earthRadius) * Math.cos(lat1),
                    Math.cos(dist / earthRadius) - Math.sin(lat1) * Math.sin(lat2));
        } catch (Exception e) {
            Log.e(TAG, "Error calculating destination point:");
            Log.e(TAG, "Point: " + pt + " Bearing: " + bearing + " Distance: " + dist);
            return null;
        }

        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }

    // http://stackoverflow.com/questions/5416700/gps-area-in-android/16210785#16210785
    private static double calculatePolygonAreaSquareMeters(final List<LatLng> locations) {
        if (locations == null || locations.size() < 3) {
            return 0;
        }

        final double diameter = EARTH_RADIUS * 2;
        final double circumference = diameter * Math.PI;
        final List<Double> listY = new ArrayList<Double>();
        final List<Double> listX = new ArrayList<Double>();
        final List<Double> listArea = new ArrayList<Double>();
        // calculate segment x and y in degrees for each point
        final double latitudeRef = locations.get(0).getLatitude();
        final double longitudeRef = locations.get(0).getLongitude();
        for (int i = 1; i < locations.size(); i++) {
            final double latitude = locations.get(i).getLatitude();
            final double longitude = locations.get(i).getLongitude();
            listY.add(calculateYSegment(latitudeRef, latitude, circumference));
            Log.d(TAG, String.format("Y %s: %s", listY.size() - 1, listY.get(listY.size() - 1)));
            listX.add(calculateXSegment(longitudeRef, longitude, latitude, circumference));
            Log.d(TAG, String.format("X %s: %s", listX.size() - 1, listX.get(listX.size() - 1)));
        }

        // calculate areas for each triangle segment
        for (int i = 1; i < listX.size(); i++) {
            final double x1 = listX.get(i - 1);
            final double y1 = listY.get(i - 1);
            final double x2 = listX.get(i);
            final double y2 = listY.get(i);
            listArea.add(calculateAreaInSquareMeters(x1, x2, y1, y2));
            Log.d(TAG, String.format("area %s: %s", listArea.size() - 1, listArea.get(listArea.size() - 1)));
        }

        // sum areas of all triangle segments
        double areasSum = 0;
        for (final Double area : listArea) {
            areasSum = areasSum + area;
        }

        // get absolute value of area, it can't be negative
        return Math.abs(areasSum);// Math.sqrt(areasSum * areasSum);
    }

    public static int getEstimatedFlightTime(List<LatLng> transects, float speed, Context c) {
        int SECONDS_PER_CORNER = 5;

        double dist = 0.0;
        for (int i = 0; i < transects.size() - 1; i++) {
            dist += GeoUtils.distanceLatLng(transects.get(i), transects.get(i + 1));
        }

        double seconds = dist / speed;
        seconds += (SECONDS_PER_CORNER * transects.size());

        int minutes = (int) (seconds / 60) + 1;
        return minutes;
    }

    public static LatLng pointBetween(LatLng a, LatLng b) {
        return new LatLng((a.getLatitude() + b.getLatitude()) / 2, (a.getLongitude() + b.getLongitude()) / 2);
    }

    private static Double calculateAreaInSquareMeters(final double x1, final double x2, final double y1, final double y2) {
        return (y1 * x2 - x1 * y2) / 2;
    }

    private static double calculateYSegment(final double latitudeRef, final double latitude, final double circumference) {
        return (latitude - latitudeRef) * circumference / 360.0;
    }

    private static double calculateXSegment(final double longitudeRef, final double longitude, final double latitude,
                                            final double circumference) {
        return (longitude - longitudeRef) * circumference * Math.cos(Math.toRadians(latitude)) / 360.0;
    }

    /**
     * Function to convert meter to decimal degree
     *
     * @param meterDistance double
     * @param latitude      double
     * @return double
     */
    public static double convertMeterToDecimalDegree(double meterDistance, double latitude) {
        return meterDistance / (111.32 * 1000 * Math.cos(latitude * (Math.PI / 180)));
    }

    public static double getTransectsLength(List<LatLng> transects) {
        double dist = 0.0;
        for (int i = 0; i < transects.size() - 1; i++) {
            dist += GeoUtils.distanceLatLng(transects.get(i), transects.get(i + 1));
        }
        return dist;
    }

    /**
     * Function to check if the first way point is farthest or not.
     *  @param firstPoint LatLng
     * @param lastPoint  LatLng
     * @param homeLatLng LatLng  @return boolean
     */
    public static boolean shouldTransectsBeReversed(LatLng firstPoint, LatLng lastPoint, LatLng homeLatLng) {
        double homeDistFromFirstPoint = distanceLatLng(homeLatLng, firstPoint);
        double homeDistFromLastPoint = distanceLatLng(homeLatLng, lastPoint);
        return homeDistFromFirstPoint < homeDistFromLastPoint;
    }

}
