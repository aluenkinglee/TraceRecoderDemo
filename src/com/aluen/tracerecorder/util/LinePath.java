package com.aluen.tracerecorder.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.aluen.tracerecoder.R;

import android.content.Context;

/**
 * represents a line in kml
 * 
 * @author Lee&GT
 * 
 */
public class LinePath extends AbsPlacemark {
	// points in this path
	private ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();

	private WeakReference<Context> context;

	public LinePath(Context context) {
		this.context = new WeakReference<Context>(context);
	}

	/**
	 * inflate LinePath using xml
	 * 
	 * @param xml
	 *            kml code
	 */
	public LinePath(Node xml, Context context) {
		this.context = new WeakReference<Context>(context);

		parse(xml);
	}

	/**
	 * add point to path
	 * 
	 * @param al
	 *            altitude
	 * @param la
	 *            latitude
	 * @param lo
	 *            longitude
	 */
	public void addPoint(double al, double la, double lo) {
		points.add(new GeoPoint(al, la, lo));
	}

	/**
	 * return null to distiguish this from SinglePoint
	 */
	@Override
	public GeoPoint getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<GeoPoint> getPoints() {
		return points;
	}

	/**
	 * Parse the xml conveyed from the GeoPoint.And append to the
	 * ArrayList<GeoPoint> pointsby Lee \n parse the kml node into a path
	 * 
	 */
	@Override
	public void parse(Node xml) {
		// TODO Auto-generated method stub
		// ///////to implement by lee
		Element element = (Element) xml;
		String[] point;
		String[] rawPoint = element.getElementsByTagName("coordinates").item(0)
				.getFirstChild().getNodeValue().trim().split("\n");
		for (int i = 0; i < rawPoint.length; i++) {
			if (!rawPoint.equals("")) {
				point = rawPoint[i].trim().split(",");
				if (3 == point.length)
					// index 1 is latitude,index 2 is altitude,index 0 is
					// longitude.
					// that is defined in the kml.
					this.addPoint(Double.parseDouble(point[2]),
							Double.parseDouble(point[1]),
							Double.parseDouble(point[0]));
			}
		}

	}

	public void setPoints(ArrayList<GeoPoint> points) {
		this.points = points;
	}

	/**
	 * 
	 * @see com.aluen.tracerecorder.util.AbsPlacemark#toXML()
	 * @return the xml structure of all the LinePath.
	 */
	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		String coords = "";
		for (int i = 0; i < points.size(); ++i) {
			coords += points.get(i).toString();
		}
		return Utility.getFormatedString(this.context.get(),
				R.string.linePathXML, coords);
	}

}
