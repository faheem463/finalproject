package com.example.groupcamping.gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import com.example.groupcamping.dropbox.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.groupcamping.R;
import com.example.groupcamping.adapter.ParentItemArrayAdapter;
import com.example.groupcamping.database.ItemGroupTableHelper;
import com.example.groupcamping.database.ItemTableHelper;
import com.example.groupcamping.dropbox.Defines;
import com.example.groupcamping.model.CampGroupModel;
import com.example.groupcamping.model.ItemModel;
import com.example.groupcamping.model.sync.GroupSyncModel;
import com.example.groupcamping.model.sync.ItemSyncModel;
import com.example.groupcamping.model.sync.ParentItemSyncModel;
import com.example.groupcamping.utis.MyLog;
import com.example.groupcamping.utis.MyUtils;
import com.google.gson.Gson;

public class GroupDetailActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	private CampGroupModel mCampGroupModel;
	private ArrayList<ItemModel> mItemModels;
	private ItemGroupTableHelper mItemGroupTableHelper;
	private ItemTableHelper mItemTableHelper;
	private ListView lvItems;
	private Button btnShare;
	private GroupSyncModel mGroupSyncModel;
	private String fileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);

		initView();
	}

	private void initView() {
		mItemGroupTableHelper = new ItemGroupTableHelper(mContext);
		mItemTableHelper = new ItemTableHelper(mContext);

		mCampGroupModel = (CampGroupModel) getIntent().getExtras().getSerializable("GROUP_MODEL");
		getActionBar().setTitle("Group - " + mCampGroupModel.getCampGroupName());

		//
		lvItems = (ListView) findViewById(R.id.lv_items);
		lvItems.setOnItemClickListener(this);

		btnShare = (Button) findViewById(R.id.btn_share);
		btnShare.setOnClickListener(this);
		if (isDropbox)
			btnShare.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isDropbox) {
			loadListview();
		} else {
			loadListviewDropbox();
		}
	}

	private void loadListview() {
		mItemModels = mItemGroupTableHelper.getItemsByGroup(mCampGroupModel.getUid());
		ParentItemArrayAdapter adapter = new ParentItemArrayAdapter(mContext, 0, mItemModels);
		lvItems.setAdapter(adapter);
	}

	private void loadListviewDropbox() {
		mGroupSyncModel = (GroupSyncModel) getIntent().getSerializableExtra("groupSyncModel");
		fileName = getIntent().getStringExtra("groupSyncModel_FILENAME");

		Gson gson = new Gson();
		File dropboxDir = new File(getFilesDir().getAbsolutePath() + "/dropbox_download");
		String json = readFileAsString(new File(dropboxDir, fileName).getAbsolutePath());
		mGroupSyncModel = gson.fromJson(json, GroupSyncModel.class);

		mItemModels = new ArrayList<ItemModel>();
		for (ParentItemSyncModel item : mGroupSyncModel.listPackages) {
			ItemModel itemModel = new ItemModel();
			itemModel.setItemName(item.name);

			ArrayList<ItemModel> listChild = new ArrayList<ItemModel>();
			for (ItemSyncModel itemSync : item.listItems) {
				ItemModel childItem = new ItemModel();
				childItem.setItemName(itemSync.name);
				childItem.setSelected(itemSync.selected);
				listChild.add(childItem);
			}
			itemModel.setChildItems(listChild);
			mItemModels.add(itemModel);
		}
		ParentItemArrayAdapter adapter = new ParentItemArrayAdapter(mContext, 0, mItemModels);
		lvItems.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		gotoItemParentActivity(position);
	}

	private void gotoItemParentActivity(int index) {
		Intent intent = new Intent(mContext, ItemChirldActivity.class);
		intent.putExtra("mGroupSyncModel", mGroupSyncModel);
		intent.putExtra("itemIndex", index);
		intent.putExtra("PARENT_MODEL", mItemModels.get(index));
		intent.putExtra("GROUP_MODEL", mCampGroupModel);
		intent.putExtra("GROUP_TYPE", isDropbox);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		if (!isDropbox) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.group_detail_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add:
			addNewPackageType();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addNewPackageType() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add new package type");
		alert.setMessage("Enter new package type:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setSingleLine(true);
		input.setHint("Package type");

		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newPackageName = input.getText().toString();
				// Do something with value!
				ItemModel itemModel = new ItemModel();
				itemModel.setItemName(newPackageName);
				mItemTableHelper.insert(itemModel);
				loadListview();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share:
			gotoShareActivity();
			break;

		default:
			break;
		}
	}

	private void gotoShareActivity() {
		Intent intent = new Intent(this, ShareGroupActivity.class);
		intent.putExtra("mCampGroupModel", mCampGroupModel);
		intent.putExtra("mItemModels", mItemModels);
		startActivity(intent);
	}
}
