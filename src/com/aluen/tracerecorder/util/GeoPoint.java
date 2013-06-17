package com.aluen.tracerecorder.util;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

/**
 * to store a location and writes it to a specified form of string
 * 
 * @author GT
 * 
 */
public class GeoPoint {
	// google's approach
	private LatLng latLng;
	// addition to google
	private double altitude;

	public GeoPoint() {
	}

	public GeoPoint(double al, double la, double lo) {
		this.setAltitude(al);
		latLng = new LatLng(la, lo);
	}

	/**
	 * calculate the distance between p and this
	 * 
	 * @param p
	 *            the geopoint to be measured
	 * @return the distance between this and p
	 */
	public float distanceTo(GeoPoint p) {
		float[] result = new float[1];
		Location.distanceBetween(this.getLatitude(), this.getLongitude(),
				p.getLatitude(), p.getLongitude(), result);
		return result[0];
	}

	public double getAltitude() {
		return altitude;
	}

	public double getLatitude() {
		return latLng.latitude;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public double getLongitude() {
		return latLng.longitude;
	}

	/**
	 * transform the latLng to a encrypted Chinese version
	 * 
	 * @return Chinese version
	 */
	public LatLng getTransformedLatLng() {
		return Transform.transformLatLng(getLatitude(), getLongitude());
	}

	/*
	 * maybe used in hashmap in next version
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.toString().hashCode();
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}

	/*
	 * to a specified form that google earth can recognize
	 */
	public String toString() {
		return this.getLongitude() + "," + this.getLatitude() + ","
				+ getAltitude() + "\n";
	}

}
