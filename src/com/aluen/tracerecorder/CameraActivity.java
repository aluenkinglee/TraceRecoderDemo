package com.aluen.tracerecorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aluen.tracerecoder.R;
import com.aluen.tracerecorder.util.Utility;

/**
 * @author GT
 * 
 */
public class CameraActivity extends Activity {
	// tag for requesting camera
	private static final int CameraTag = 942;
	// ui
	private Button bOk;
	private Button bCancel;
	private EditText etDesc;
	// Handle to SharedPreferences for this app
	private SharedPreferences mPrefs;

	private String fileName;
	private String desc;
	private String now;

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.bOk:
				// savePhoto();
				sendInfo();
				CameraActivity.this.finish();
				break;
			case R.id.bCancel:
				CameraActivity.this.finish();
				break;
			}
		}
	};

	/**
	 * user defined directory in next version
	 * 
	 * @return the full path to current directory
	 */
	private String getCurrentDir() {
		return mPrefs.getString(Utility.CurrentDir, "");
	}

	/*
	 * get the photo in bitmap and store it in memory
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == CameraTag && resultCode == RESULT_OK) {
		} else {
			CameraActivity.this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// init ui
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		bOk = (Button) findViewById(R.id.bOk);
		bCancel = (Button) findViewById(R.id.bCancel);
		etDesc = (EditText) findViewById(R.id.etDesc);
		bOk.setOnClickListener(listener);
		bCancel.setOnClickListener(listener);
		// get preference for information
		mPrefs = getSharedPreferences(Utility.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		// start the system camera for a photo
		startCamera();
	}

	@Override
	protected void onResume() {
		setTitle(R.string.camera_title);
		super.onResume();
	}

	/**
	 * start system camera for a image and store it
	 */
	private void startCamera() {
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		now = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINESE)
				.format(Calendar.getInstance().getTime());
		fileName = now + ".jpg";
		File file = new File(getCurrentDir(), fileName);
		Uri imageUri = Uri.fromFile(file);
		i.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(i, CameraTag);
	}

	/**
	 * sends information about the photo to service
	 */
	private void sendInfo() {
		// send message to service to store info in record
		desc = etDesc.getText().toString();
		Intent i = new Intent();
		i.setAction(Utility.CommandAction);
		i.putExtra(Utility.CommandTag, Utility.AddPhoto);
		i.putExtra(Utility.FileNameExtra, fileName);
		i.putExtra(Utility.DescExtra, desc);
		i.putExtra(Utility.TimeExtra, now);
		CameraActivity.this.sendBroadcast(i);
	}

}
