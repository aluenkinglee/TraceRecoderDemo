package com.aluen.tracerecorder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

/*
 * a class represents a full record 
 * with galleries and a path
 * fills up a dir in sdcard
 * */
public class Record {

	private static final String xmlStart = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
			+ "<Document>\n";
	private static final String xmlEnd = "</Document>\n" + "</kml>\n";

	private String dirName;// dir name
	private WeakReference<Context> context;

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	private ArrayList<SinglePoint> list = new ArrayList<SinglePoint>();
	private LinePath path;

	public LinePath getPath() {
		return path;
	}

	public void setPath(LinePath path) {
		this.path = path;
	}

	private AssetManager assetManager;

	public Record(Context context, AssetManager asset) {
		dirName = Environment.getExternalStorageDirectory().getPath()
				+ "/trace/kml/"
				+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINESE)
						.format(Calendar.getInstance().getTime()) + "/";
		this.assetManager = asset;

		this.context = new WeakReference<Context>(context);

		this.path = new LinePath(this.context.get());
	}

	public Record(String file, Context context, AssetManager asset) {
		dirName = file + "/";

		this.context = new WeakReference<Context>(context);

		try {
			read();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addPointToPath(GeoPoint p) {
		path.addPoint(p.getAltitude(), p.getLatitude(), p.getLongitude());
	}

	public void addPhoto(GeoPoint p, String image, String description,
			String time) {
		SinglePoint point;
		if (list.isEmpty()) {
			point = new SinglePoint(this.context.get());
			point.setPoint(p);
			list.add(point);
		} else {
			point = (SinglePoint) list.get(list.size() - 1);
			if (point.getLocation().distanceTo(p) > 10) {// a circle around the
															// spot
				point = new SinglePoint(this.context.get());// another gallery
				point.setPoint(p);
				list.add(point);
			}
		}
		point.addImage(image, description, time);
	}

	public void save() {
		try {
			write();
			copyAssets(dirName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<SinglePoint> getList() {
		return list;
	}

	public void setList(ArrayList<SinglePoint> list) {
		this.list = list;
	}

	private void write() throws Exception {
		Log.d("Record write", dirName);
		File dir = new File(dirName);
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
		for (int i = 0; i < list.size(); ++i) {
			fw.append(list.get(i).toXML());
		}
		fw.append(path.toXML());
		fw.append(xmlEnd);
		fw.flush();
		fw.close();
	}

	private ArrayList<SinglePoint> read() throws Exception {
		list.clear();
		File dir = new File(dirName);
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
			AbsPlacemark placemark = AbsPlacemark.getInstance(
					this.context.get(), items.item(i));
			if (placemark.getLocation() != null) {
				list.add((SinglePoint) placemark);
			} else {
				path = (LinePath) placemark;
			}
		}
		return list;
	}

	private void copyAssets(String dirName) {
		String filename = "gallery.html";
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			out = new FileOutputStream(dirName + filename);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (IOException e) {
			Log.e("tag", "Failed to copy asset file: " + filename, e);
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
