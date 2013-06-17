package com.aluen.tracerecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aluen.tracerecoder.R;
import com.aluen.tracerecorder.util.Record;
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
	private Bitmap bitmap = null;
	private EditText etDesc;
	// Handle to SharedPreferences for this app
	private SharedPreferences mPrefs;

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.bOk:
				savePhoto();
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
		return mPrefs.getString(Record.CurrentDir, "");
	}

	/**
	 * user defined quality in next version
	 * 
	 * @return the request quality of the photo
	 */
	private int getQuality() {
		return 100;
	}

	/*
	 * get the photo in bitmap and store it in memory
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == CameraTag && resultCode == RESULT_OK
				&& data.getExtras().get("data") != null) {
			bitmap = (Bitmap) data.getExtras().get("data");
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
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(i, CameraTag);
	}

	@Override
	protected void onResume() {
		getParent().getActionBar().setTitle(R.string.camera_title);
		super.onResume();
	}

	/**
	 * write the bitmap got from system camera to file file is in the dirName
	 * directory and names after its save time
	 */
	private void savePhoto() {
		if (bitmap == null) {// no photo returned from camera
			return;
		}
		// parent directory
		File dir = new File(getCurrentDir());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// photo file name
		final String now = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
				Locale.CHINESE).format(Calendar.getInstance().getTime());
		final String fileName = now + ".jpg";
		File file = new File(dir, fileName);
		// write
		FileOutputStream out = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, getQuality(), out);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}

		// send message to service to store info in record
		String desc = etDesc.getText().toString();
		Intent i = new Intent();
		i.setAction(Utility.CommandAction);
		i.putExtra("command", Utility.AddPhoto);
		i.putExtra("fileName", fileName);
		i.putExtra("desc", desc);
		i.putExtra("now", now);
		CameraActivity.this.sendBroadcast(i);
	}
}
