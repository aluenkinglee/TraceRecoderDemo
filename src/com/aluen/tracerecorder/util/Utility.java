package com.aluen.tracerecorder.util;

import android.content.Context;
import android.content.Intent;

public final class Utility {
	// Debugging tag for the application
	public static final String APPTAG = "TraceRecorder";
	// Milliseconds per second
	public static final int MILLISECONDS_PER_SECOND = 1000;
	// The update interval
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// The GpsIcon title
	public static final String GpsIcon = "GpsIcon";
	public static final String GpsStatus = "status";
	public static final String GpsLat = "Lat";
	public static final String GpsLng = "Lng";

	public static final String CommandTag = "command";
	public static final String CommandAction = "Message";

	public static final String FileNameExtra = "fileName";
	public static final String DescExtra = "desc";
	public static final String TimeExtra = "now";
	public static final int Start = 0;
	public static final int Save = 1;
	public static final int TakePhoto = 2;
	public static final int AddPhoto = 3;
	public static final int Exit = 4;

	public static final String CurrentDir = "currentDir";
	// Name of shared preferences repository that stores persistent state
	public static final String SHARED_PREFERENCES = "com.example.android.location.SHARED_PREFERENCES";

	// Key for storing the "updates requested" flag in shared preferences
	public static final String KEY_UPDATES_REQUESTED = "com.example.android.location.KEY_UPDATES_REQUESTED";

	/**
	 * a static function to send a broadcast
	 * 
	 * @param context
	 *            the context that sends the broadcast
	 * @param command
	 *            a int represents the command
	 * 
	 */
	public static void sendCommand(Context context, int command) {
		Intent i = new Intent();
		i.setAction(CommandAction);
		i.putExtra(CommandTag, command);
		context.sendBroadcast(i);
	}

	public static String getFormatedString(Context context, int resId,
			Object... args) {
		return context.getString(resId, args);
	}

}
