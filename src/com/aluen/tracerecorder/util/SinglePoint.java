package com.aluen.tracerecorder.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.aluen.tracerecoder.R;

import android.content.Context;

public class SinglePoint extends AbsPlacemark {
	// the location of this gallery
	private GeoPoint point;
	// for read
	private String html;
	// for write
	private ArrayList<String> images;
	private ArrayList<String> descriptions;
	private ArrayList<String> times;

	private WeakReference<Context> context;


	/**
	 * for write a new gallery
	 * 
	 * @param context
	 *            app context
	 */
	public SinglePoint(Context context) {
		setImages(new ArrayList<String>());
		setDescriptions(new ArrayList<String>());
		setTimes(new ArrayList<String>());

		this.context = new WeakReference<Context>(context);
	}

	/**
	 * for read an old gallery
	 * 
	 * @param xml
	 *            kml data
	 * @param context
	 *            application context
	 */
	public SinglePoint(Node xml, Context context) {
		// TODO Auto-generated constructor stub
		this.context = new WeakReference<Context>(context);

		parse(xml);
	}

	/**
	 * add a photo to gallery
	 * 
	 * @param image
	 *            photo file name
	 * @param desc
	 *            description
	 * @param time
	 */
	public void addImage(String image, String desc, String time) {
		images.add(image);
		descriptions.add(desc);
		times.add(time);
	}

	public ArrayList<String> getDescriptions() {
		return descriptions;
	}

	public String getHtml() {
		return html;
	}

	public ArrayList<String> getImages() {
		return images;
	}

	@Override
	public GeoPoint getLocation() {
		// TODO Auto-generated method stub
		return getPoint();
	}

	public GeoPoint getPoint() {
		return point;
	}

	public ArrayList<String> getTimes() {
		return times;
	}

	/*
	 * by Lee \n inflates a gallery using xml
	 */
	@Override
	public void parse(Node xml) {
		// TODO Auto-generated method stub
		Element element = (Element) xml;
		String[] rawPoint = element.getElementsByTagName("coordinates").item(0)
				.getFirstChild().getNodeValue().trim().split(",");
		// index 1 is latitude,index 2 is altitude,index 0 is longitude.
		// that is defined in the kml.
		GeoPoint gp = new GeoPoint(Double.parseDouble(rawPoint[2]),
				Double.parseDouble(rawPoint[1]),
				Double.parseDouble(rawPoint[0]));
		this.setPoint(gp);
		// parse html in the description
		this.setHtml(element.getElementsByTagName("description").item(0)
				.getTextContent().trim());
	}

	public void setDescriptions(ArrayList<String> descriptions) {
		this.descriptions = descriptions;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	public void setTimes(ArrayList<String> times) {
		this.times = times;
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		String json = "[";
		for (int i = 0; i < getImages().size(); ++i) {
			json += Utility.getFormatedString(this.context.get(),
					R.string.singlePointJson, getImages().get(i),
					getDescriptions().get(i), getTimes().get(i))+",";
		}
		json += "]";
		return Utility.getFormatedString(this.context.get(),
				R.string.singlePointXML, getPoint().toString(), json);
	}

}
