package com.aluen.tracerecorder.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.content.Context;

/**
 * an abstract class for placemark in kml
 * 
 * @author Lee&GT
 */
public abstract class AbsPlacemark {

	/**
	 * by Lee \n a factory to get a subclass instance of AbsPlacemark
	 * 
	 * @param xml
	 *            the xml file to inflate the instance
	 * @return an instance of a subclass of AbsPlacemark
	 */
	public static AbsPlacemark getInstance(Context context, Node xml) {
		Element element = (Element) xml;
		if (element.getElementsByTagName("Point").item(0) != null) {
			return new SinglePoint(xml, context);
		}
		return new LinePath(xml, context);
	}

	public abstract GeoPoint getLocation();

	public abstract void parse(Node xml);

	public abstract String toXML();
}
