package com.aluen.tracerecorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aluen.tracerecoder.R;
import com.aluen.tracerecorder.util.Utility;

public class NewRecordActivity extends FragmentActivity {
	// ui
	private TextView tvLat;
	private TextView tvLng;
	private TextView tvGpsStatus;
	private ImageView ivGps;
	private Button bStart;
	private ImageButton ibCamera;
	private Button bSave;

	// Handle to SharedPreferences for this app
	private SharedPreferences mPrefs;

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.bCamera:
				Utility.sendCommand(NewRecordActivity.this, Utility.TakePhoto);
				break;
			case R.id.bStart:
				Intent i = new Intent(NewRecordActivity.this,
						RecordingService.class);
				startService(i);
				bStart.setEnabled(false);
				bSave.setEnabled(true);
				break;
			case R.id.bSave:
				Utility.sendCommand(NewRecordActivity.this, Utility.Save);
				resetViews();
				break;
			default:
				break;
			}
		}
	};
	private OnSharedPreferenceChangeListener preferenceListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// TODO Auto-generated method stub
			if (Utility.GpsLat.equals(key) || Utility.GpsLng.equals(key)) {// update
																			// location
				String lat = mPrefs.getString(Utility.GpsLat, "");
				tvLat.setText(lat);
				String lng = mPrefs.getString(Utility.GpsLng, "");
				tvLng.setText(lng);
			} else if (Utility.GpsStatus.equals(key)
					|| Utility.GpsIcon.equals(key)) {// update
				// status
				String status = mPrefs.getString(Utility.GpsStatus, "");
				tvGpsStatus.setText(status);
				int gpsIcon = mPrefs.getInt(Utility.GpsIcon, 0);
				ivGps.setImageResource(gpsIcon);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// init ui
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_record);
		tvGpsStatus = (TextView) findViewById(R.id.tvGpsStatus);
		tvLat = (TextView) findViewById(R.id.tvLat);
		tvLng = (TextView) findViewById(R.id.tvLng);
		ivGps = (ImageView) findViewById(R.id.ivGps);
		bStart = (Button) findViewById(R.id.bStart);
		ibCamera = (ImageButton) findViewById(R.id.bCamera);
		bSave = (Button) findViewById(R.id.bSave);

		bStart.setOnClickListener(clickListener);
		ibCamera.setOnClickListener(clickListener);
		bSave.setOnClickListener(clickListener);
		
		bSave.setEnabled(false);

		Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.alpha);
		ibCamera.startAnimation(anim);
		// Open Shared Preferences
		mPrefs = getSharedPreferences(Utility.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		// listen for service update
		mPrefs.registerOnSharedPreferenceChangeListener(preferenceListener);
	}

	protected void onResume() {
		getParent().getActionBar().setTitle(R.string.new_record_title);
		super.onResume();
	}

	/**
	 * reset the texts and images on the views
	 */
	private void resetViews() {
		tvLat.setText(R.string.initialText);
		tvLng.setText("");
		tvGpsStatus.setText(R.string.gpsFail);
		ivGps.setImageResource(R.drawable.gps_fail);
		bSave.setEnabled(false);
		bStart.setEnabled(true);
	}
}
