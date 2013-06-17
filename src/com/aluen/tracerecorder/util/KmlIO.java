package com.aluen.tracerecorder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.Environment;

public class KmlIO {

	private static final String xmlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
			+ "<Document>\n";
	private static final String xmlEnd = "</Document>\n" + "</kml>\n";

	/** 
	 * Save the record
	 * @param dirname String
	 * @param placemarks ArrayList<AbsPlacemark>
	 * @throws Exception
	 */
	public static void write(String dirname, ArrayList<AbsPlacemark> placemarks)
			throws Exception {
		String dirPath = Environment.getExternalStorageDirectory().getPath()
				+ "/trace/" + "kml/" + dirname + "/";

		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String fileName = "doc.kml";
		File file = new File(dir, fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw;
		fw = new FileWriter(file);
		fw.write(xmlStart);
		for (int i = 0; i < placemarks.size(); ++i) {
			fw.append(placemarks.get(i).toXML());
		}
		fw.append(xmlEnd);
		fw.flush();
		fw.close();
	}

	/** Read a directory that contains the pictures,doc.kml and gallery.html.
	 *  and return a hashmap maped from point to AbsPlacemark.
	 * @param dirname String
	 * @return  A HashMap<GeoPoint, AbsPlacemark> maped from point to AbsPlacemark.
	 * And data such as html and linepath  will be used later.
	 * @throws Exception
	 */
	public static HashMap<GeoPoint, AbsPlacemark> read(String dirname)
			throws Exception {
		HashMap<GeoPoint, AbsPlacemark> result = new HashMap<GeoPoint, AbsPlacemark>();
		String dirPath = "kml/" + dirname + "/";
		File dir = new File(dirPath);
		if (!dir.exists()) {
			return null;
		}
		String fileName = "doc.kml";
		File file = new File(dir, fileName);
		if (!file.exists()) {
			return null;
		}
		FileInputStream is = new FileInputStream(file);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(is);
		Element rootElement = doc.getDocumentElement();
		NodeList items = rootElement.getElementsByTagName("Placemark");
		for (int i = 0; i < items.getLength(); ++i) {
			AbsPlacemark placemark = AbsPlacemark.getInstance(items.item(i));
			if (placemark.getLocation() != null) {
				result.put(placemark.getLocation(), placemark);
			}
		}
		return result;
	}

	/**
	 * Copy file from stream in to out
	 * @param in InputStream
	 * @param out OutputStream
	 * @throws IOException
	 */
	public static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
