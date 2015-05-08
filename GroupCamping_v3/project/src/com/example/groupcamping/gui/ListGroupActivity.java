package com.example.groupcamping.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.groupcamping.R;
import com.example.groupcamping.database.CampGroupTableHelper;
import com.example.groupcamping.model.CampGroupModel;
import com.example.groupcamping.model.sync.GroupSyncModel;
import com.google.gson.Gson;

public class ListGroupActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {

	private TextView tvNoGroup;
	private FrameLayout layoutNoGroup;
	private ListView lvGroup;
	private CampGroupTableHelper mCampGroupTableHelper;

	private ArrayList<CampGroupModel> listGroupModel;
	private ArrayList<String> listGroupName;
	private ArrayList<GroupSyncModel> listGroupSyncModel;
	private File[] listFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camping);
		initView();
	}

	private void initView() {

		//
		tvNoGroup = (TextView) findViewById(R.id.tv_no_group);
		layoutNoGroup = (FrameLayout) findViewById(R.id.layout_no_group);

		//
		lvGroup = (ListView) findViewById(R.id.lv_groups);
		if (!isDropbox) {
			loadGroupListView();
		} else {
			loadGroupListViewDropbox();
		}
		lvGroup.setOnItemLongClickListener(this);
		// registerForContextMenu(lvGroup);
	}

	private void loadGroupListView() {
		mCampGroupTableHelper = new CampGroupTableHelper(mContext);
		listGroupModel = mCampGroupTableHelper.getAll();
		if (listGroupModel.size() > 0) {
			setStateHavingGroup();
		} else {
			setStateNoGroup();
		}

		listGroupName = new ArrayList<String>();
		for (CampGroupModel group : listGroupModel) {
			listGroupName.add(group.getCampGroupName());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,
				listGroupName);
		lvGroup.setAdapter(adapter);
		lvGroup.setOnItemClickListener(this);
	};

	private void loadGroupListViewDropbox() {

		File dropboxDir = new File(getFilesDir().getAbsolutePath() + "/dropbox_download");
		listFiles = dropboxDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		});
		listGroupModel = new ArrayList<CampGroupModel>();
		listGroupSyncModel = new ArrayList<GroupSyncModel>();
		if (listFiles != null) {
			Gson gson = new Gson();
			for (File file : listFiles) {
				String json = readFileAsString(file.getAbsolutePath());

				GroupSyncModel groupSyncModel = gson.fromJson(json, GroupSyncModel.class);
				CampGroupModel campGroupModel = new CampGroupModel(groupSyncModel);
				listGroupSyncModel.add(groupSyncModel);
				listGroupModel.add(campGroupModel);
			}
		}

		if (listGroupModel.size() > 0) {
			setStateHavingGroup();
		} else {
			setStateNoGroup();
		}

		listGroupName = new ArrayList<String>();
		for (CampGroupModel group : listGroupModel) {
			listGroupName.add(group.getCampGroupName());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1,
				listGroupName);
		lvGroup.setAdapter(adapter);
		lvGroup.setOnItemClickListener(this);
	};


	private void setStateNoGroup() {
		// tvNoGroup.setVisibility(View.VISIBLE);
		layoutNoGroup.setVisibility(View.VISIBLE);
		lvGroup.setVisibility(View.GONE);
	}

	private void setStateHavingGroup() {
		// tvNoGroup.setVisibility(View.GONE);
		layoutNoGroup.setVisibility(View.GONE);
		lvGroup.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (!isDropbox) {
			// Inflate the menu items for use in the action bar
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add:
			addNewCamping();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addNewCamping() {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add new group");
		alert.setMessage("Enter new group name:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setSingleLine(true);
		input.setHint("Group name");

		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newGroupName = input.getText().toString();
				// Do something with value!
				CampGroupModel newCampGroupModel = new CampGroupModel();
				newCampGroupModel.setCampGroupName(newGroupName);
				mCampGroupTableHelper.insert(newCampGroupModel);
				loadGroupListView();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		gotoItemParentActivity(position);
	}

	private void gotoItemParentActivity(int index) {

		Intent intent = new Intent(mContext, GroupDetailActivity.class);
		if (isDropbox){
			intent.putExtra("groupSyncModel", listGroupSyncModel.get(index));
			intent.putExtra("groupSyncModel_FILENAME", listFiles[index].getName());
		}
		intent.putExtra("GROUP_MODEL", listGroupModel.get(index));
		intent.putExtra("GROUP_TYPE", isDropbox);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.lv_groups) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Share to dropbox");
			menu.add(Menu.NONE, 112, 112, "Share");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		if (menuItemIndex == 112) {
			myToast.showToast("long click menu");
		}
		// String[] menuItems = getResources().getStringArray(R.array.menu);
		// String menuItemName = menuItems[menuItemIndex];
		// String listItemName = Countries[info.position];
		//
		// TextView text = (TextView)findViewById(R.id.footer);
		// text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
		return true;
	}
}
