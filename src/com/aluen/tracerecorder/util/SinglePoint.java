package com.aluen.tracerecorder.util;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SinglePoint extends AbsPlacemark {
	// kml template
	private static String xmlStr = "<Placemark>\n"
			+ "<visibility>1</visibility>\n"
			+ "<Point>\n"
			+ "<coordinates>\n$coord</coordinates>\n"
			+ "</Point>\n"
			+ "<description>\n<![CDATA[\n"
			+ "<script>\ndata = $data;\n"
			+ "</script>\n"
			+ "<table id = \"wrapper\"><tr><td><iframe name=\"gallery\" frameborder=\"no\" "
			+ "border=\"0\" width=\"100%\" height=\"100%\" src=\"gallery.html\">"
			+ "</iframe></td></tr></table>\n" + "]]>\n</description>\n"
			+ "</Placemark>\n";
	// the location of this gallery
	private GeoPoint point;
	// for read
	private String html;
	// for write
	private ArrayList<String> images;
	private ArrayList<String> descriptions;
	private ArrayList<String> times;

	/**
	 * for write a new gallery
	 */
	public SinglePoint() {
		setImages(new ArrayList<String>());
		setDescriptions(new ArrayList<String>());
		setTimes(new ArrayList<String>());
	}

	/**
	 * for read an old gallery
	 * 
	 * @param xml
	 *            kml data
	 */
	public SinglePoint(Node xml) {
		// TODO Auto-generated constructor stub
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
		String result = xmlStr.replace("$coord", getPoint().toString());
		String json = "[{\"src\":\"" + getImages().get(0) + "\",\"desc\":\""
				+ getDescriptions().get(0) + "\",\"time\":\""
				+ getTimes().get(0) + "\"}";
		for (int i = 1; i < getImages().size(); ++i) {
			json += ",{\"src\":\"" + getImages().get(i) + "\",\"desc\":\""
					+ getDescriptions().get(i) + "\",\"time\":\""
					+ getTimes().get(i) + "\"}";
		}
		json += "]";
		result = result.replace("$data", json);
		return result;
	}

}
