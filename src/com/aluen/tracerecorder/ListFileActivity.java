package com.aluen.tracerecorder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aluen.tracerecoder.R;
import com.aluen.tracerecorder.util.Utility;

public class ListFileActivity extends Activity {
	// Handle the file list in such way <filename,directory path>.
	private HashMap<String, String> fileList = null;
	// This app's storage path
	private final String dirPath = Environment.getExternalStorageDirectory()
			.getPath() + "/trace/kml/";
	// Adapter to deal with the data and the listview
	private StableArrayAdapter adapter = null;
	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;
	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_file);
		// Open Shared Preferences
		mPrefs = getSharedPreferences(Utility.SHARED_PREFERENCES,
				Context.MODE_PRIVATE);
		// Get an editor
		mEditor = mPrefs.edit();

		final ListView listview = (ListView) findViewById(R.id.listview);
		fileList = getFileList(new File(dirPath));

		final ArrayList<String> list = new ArrayList<String>();
		// get the all the filename in current file list
		Set<String> set = fileList.keySet();
		for (String file : set) {
			list.add(file);
		}

		adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

		//
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				mEditor.putString("PATH", fileList.get(parent
						.getItemAtPosition(position).toString()));
				mEditor.commit();
				Intent intent = new Intent();
				intent.setClass(view.getContext(), MapActivity.class);
				startActivity(intent);
			}

		});
	}

	@Override
	protected void onResume() {
		getParent().getActionBar().setTitle(R.string.list_file_title);
		super.onResume();
	}

	public HashMap<String, String> getFileList(File path) {
		HashMap<String, String> fileList = new HashMap<String, String>();
		_getFileList(path, fileList);
		return fileList;

	}

	/**
	 * @param path
	 * @param fileList
	 * 
	 */

	private void _getFileList(File path, HashMap<String, String> fileList) {
		if (path.isDirectory()) {
			File[] files = path.listFiles();
			if (null == files)
				return;
			for (int i = 0; i < files.length; i++) {
				String filePath = files[i].getAbsolutePath();
				String fileName = files[i].getName();
				fileList.put(fileName, filePath);
			}
		}
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}
}
