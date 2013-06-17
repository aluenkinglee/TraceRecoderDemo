package com.aluen.tracerecorder;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.aluen.tracerecoder.R;
import com.aluen.tracerecorder.util.Utility;

@SuppressWarnings("deprecation")
public class MainTabActivity extends TabActivity {
	private final static int TAB_INDEX_NEW = 0;
	private final static int TAB_INDEX_OLD = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_content);

		final TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("update").setIndicator("update")
				.setContent(new Intent(this, NewRecordActivity.class)));

		tabHost.addTab(tabHost.newTabSpec("fileview").setIndicator("fileview")
				.setContent(new Intent(this, ListFileActivity.class)));

		getTabWidget().setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tab_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		for (int i = 0; i < menu.size(); i++) {
			refreshTabIcon(menu.getItem(i), getTabHost().getCurrentTab());
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private void refreshTabIcon(MenuItem item, int currentTab) {
		if (item.getGroupId() == R.id.tab_group) {
			switch (item.getItemId()) {
			case R.id.tab_new:
				item.setIcon(currentTab == TAB_INDEX_NEW ? R.drawable.ic_tab_selected_recent
						: R.drawable.ic_tab_unselected_recent);
				break;
			case R.id.tab_old:
				item.setIcon(currentTab == TAB_INDEX_OLD ? R.drawable.ic_tab_selected_contacts
						: R.drawable.ic_tab_unselected_contacts);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == R.id.tab_group) {
			switch (item.getItemId()) {
			case R.id.tab_new:
				getTabHost().setCurrentTab(TAB_INDEX_NEW);
				break;
			case R.id.tab_old:
				getTabHost().setCurrentTab(TAB_INDEX_OLD);
				break;
			case R.id.tab_menu_exit:
				Utility.sendCommand(MainTabActivity.this,
						Utility.Exit);
				break;
			default:
				break;
			}
			invalidateOptionsMenu();
		}
		return super.onOptionsItemSelected(item);
	}

}
