package com.aluen.tracerecorder;

import java.io.File;
import java.util.Iterator;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.aluen.tracerecoder.R;
import com.aluen.tracerecorder.util.GeoPoint;
import com.aluen.tracerecorder.util.Record;
import com.aluen.tracerecorder.util.Utility;

/**
 * @author GT
 * 
 */
public class RecordingService extends Service {
	// record related
	private Record record = null;
	private GeoPoint lastLocation;
	private long lastTime = 0;
	private LocationManager locationManager;
	// location request settings
	private long minTime = 1 * Utility.MILLISECONDS_PER_SECOND;
	private float minDis = 100;

	// float window
	private WindowManager windowManager;
	private ImageView ivFloat = null;
	private float touchStartX;
	private float touchStartY;
	private float x;
	private float y;
	private float statusBar = 30;
	private WindowManager.LayoutParams layoutParams;

	// Handle to SharedPreferences for this app
	private SharedPreferences mPrefs;
	// Handle to a SharedPreferences editor
	private SharedPreferences.Editor mEditor;
	// handles float window on touch events
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			x = event.getRawX();
			y = event.getRawY() - statusBar;

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchStartX = event.getX();
				touchStartY = event.getY();
				return false;
			case MotionEvent.ACTION_MOVE:
				updateViewPosition();
				return true;
			case MotionEvent.ACTION_UP:
				return false;
			}
			return false;
		}

	};
	// handles float window long click events
	// start for system camera
	private OnLongClickListener longClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			takePhoto();
			return true;
		}
	};
	// receive commands from activities
	private BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent i) {
			// TODO Auto-generated method stub
			RecordingService.this.execute(i);
		}
	};
	// handles location related events
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastTime > minTime) {// right time interval
				GeoPoint currentLocation = new GeoPoint(location.getAltitude(),
						location.getLatitude(), location.getLongitude());
				if (currentLocation.distanceTo(lastLocation) > minDis) {// right
																		// distance
																		// difference
					// update path and last items
					record.addPointToPath(currentLocation);
					lastLocation = currentLocation;
					lastTime = currentTime;
					updateLocation();
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			updateStatus(getApplicationContext().getString(R.string.gpsFail),
					R.drawable.gps_fail);
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
	};

	private GpsStatus.Listener statusListener = new GpsStatus.Listener() {

		/*
		 * change the sharedPreference to inform NewRecordActivity the gps
		 * status change
		 */
		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub

			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				GpsStatus status = locationManager.getGpsStatus(null);
				// count the fixed satellites and total number
				Iterator<GpsSatellite> ite = status.getSatellites().iterator();
				int inUse = 0;
				int total = 0;
				while (ite.hasNext()) {
					GpsSatellite s = ite.next();
					++total;
					if (s.usedInFix()) {
						++inUse;
					}
				}
				updateStatus(Utility.getFormatedString(getApplicationContext(),
						R.string.gpsStatus, inUse, total), R.drawable.gps_fixed);
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				updateStatus(
						RecordingService.this.getString(R.string.gpsStart),
						R.drawable.gps_scan);
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				updateStatus(
						RecordingService.this.getString(R.string.gpsFixed),
						R.drawable.gps_fixed);
				break;
			}
		}
	};

	/**
	 * create a float window which acts as a short cut to camera
	 */
	private void createFloatWindow() {
		// window manager
		windowManager = (WindowManager) this.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		// layout settings
		layoutParams = new WindowManager.LayoutParams();
		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		layoutParams.format = PixelFormat.RGBA_8888;
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		layoutParams.x = 0;
		layoutParams.y = 0;
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// view
		ivFloat = new ImageView(this);
		ivFloat.setOnTouchListener(touchListener);
		ivFloat.setOnLongClickListener(longClickListener);
		ivFloat.setImageResource(R.drawable.camera_small);
		// show
		windowManager.addView(ivFloat, layoutParams);
	}

	/**
	 * call corresponding methods as a respond to remote commands
	 * 
	 * @param i
	 *            the intent containing command
	 */
	private void execute(Intent i) {
		// TODO Auto-generated method stub
		int c = i.getExtras().getInt(Utility.CommandTag);
		switch (c) {
		case Utility.Start:
			startRecord();
			break;
		case Utility.Save:
			record.save();
			exit();
			break;
		case Utility.TakePhoto:
			takePhoto();
			break;
		case Utility.AddPhoto:
			// data is written to intent in CameraActivity
			String fileName = i.getStringExtra(Utility.FileNameExtra);
			String desc = i.getStringExtra(Utility.DescExtra);
			String now = i.getStringExtra(Utility.TimeExtra);
			record.addPhoto(lastLocation, fileName, desc, now);
			break;
		case Utility.Exit:
			break;
		default:
			break;
		}
	}

	/**
	 * stop current service
	 */
	private void exit() {
		if (ivFloat != null) {
			// clear float window
			windowManager.removeView(ivFloat);
			ivFloat = null;
			// stop receiving commands
			unregisterReceiver(br);
			// stop requesting gps updates
			locationManager.removeGpsStatusListener(statusListener);
			locationManager.removeUpdates(locationListener);
			// stop service
			stopForeground(true);
			stopSelf();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// register receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(Utility.CommandAction);
		registerReceiver(br, filter);
		// keep the service running
		startForeground(1, new Notification());

		createFloatWindow();
		readPreference();
		startRecord();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * get the dirName and create the directory for a new record
	 */
	private void prepareFile() {
		String dirName = record.getDirName();
		// save the dirName in sharedPreference
		mEditor.putString(Utility.CurrentDir, dirName);
		mEditor.commit();
		// mkdir
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * adds listeners to the gps service
	 * 
	 * @return whether the gps service is available
	 */
	private boolean prepareGPS() {
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, minTime, minDis,
					locationListener);
			locationManager.addGpsStatusListener(statusListener);
			return true;
		} else {
			Toast.makeText(getApplicationContext(), "open gps",
					Toast.LENGTH_LONG).show();
			locationManager.addGpsStatusListener(statusListener);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, minTime, minDis,
					locationListener);
			return false;
		}
	}

	/**
	 * read the settings of user \n to be done in next version
	 */
	private void readPreference() {
		// TODO Auto-generated method stub
		// Open Shared Preferences
		mPrefs = getSharedPreferences(Utility.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		// Get an editor
		mEditor = mPrefs.edit();

	}

	/**
	 * start a new record
	 */
	private void startRecord() {
		if (record == null) {
			record = new Record(getApplicationContext(), getAssets());
			lastLocation = new GeoPoint(0, 0, 0);
			prepareGPS();
			prepareFile();
		}
	}

	/**
	 * start a CameraActivity for a photo
	 */
	private void takePhoto() {
		Intent i = new Intent(RecordingService.this, CameraActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	/**
	 * update lat and lng to NewRecordActivity by setting sharedPreference
	 */
	private void updateLocation() {
		mEditor.putString(Utility.GpsLat, Utility.getFormatedString(
				getApplicationContext(), R.string.Lat,
				lastLocation.getLatitude()));
		mEditor.putString(Utility.GpsLng, Utility.getFormatedString(
				getApplicationContext(), R.string.Lng,
				lastLocation.getLongitude()));
		mEditor.commit();
	}

	/**
	 * update gps status to NewRecordActivity by setting sharedPreference
	 * 
	 * @param status
	 *            status string
	 * @param icon
	 *            the icon corresponding to current status
	 */
	private void updateStatus(String status, int icon) {
		mEditor.putString(Utility.GpsStatus, status);
		mEditor.putInt(Utility.GpsIcon, icon);
		mEditor.commit();
	}

	/**
	 * change the position of float window
	 */
	private void updateViewPosition() {

		layoutParams.x = (int) (x - touchStartX);
		layoutParams.y = (int) (y - touchStartY);

		windowManager.updateViewLayout(ivFloat, layoutParams);
	}
}
