package com.example.groupcamping.gui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.example.groupcamping.R;
import com.example.groupcamping.adapter.ChildItemArrayAdapter;
import com.example.groupcamping.database.ItemGroupTableHelper;
import com.example.groupcamping.database.ItemTableHelper;
import com.example.groupcamping.model.CampGroupModel;
import com.example.groupcamping.model.ItemGroupModel;
import com.example.groupcamping.model.ItemModel;
import com.example.groupcamping.model.sync.GroupSyncModel;
import com.google.gson.Gson;

public class ItemChirldActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	private CampGroupModel mCampGroupModel;
	private ItemModel mParentItem;
	private ArrayList<ItemModel> mItemModels;
	private ItemGroupTableHelper mItemGroupTableHelper;
	private ListView lvItems;
	private ChildItemArrayAdapter mAdapter;
	private ItemTableHelper mItemTableHelper;
	private GroupSyncModel mGroupSyncModel;
	private int itemIndex;
	private Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_chirld);
		gson = new Gson();
		initView();
	}

	private void initView() {
		mItemGroupTableHelper = new ItemGroupTableHelper(mContext);
		mItemTableHelper = new ItemTableHelper(mContext);

		mCampGroupModel = (CampGroupModel) getIntent().getExtras().getSerializable("GROUP_MODEL");
		mParentItem = (ItemModel) getIntent().getExtras().getSerializable("PARENT_MODEL");
		getActionBar().setTitle("Package - " + mParentItem.getItemName());
		mItemModels = mParentItem.getChildItems();

		//
		lvItems = (ListView) findViewById(R.id.lv_items);
		lvItems.setOnItemClickListener(this);

		// btnShare = (Button) findViewById(R.id.btn_share);
		// btnShare.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadListview();
	}

	private void loadListview() {
		if (isDropbox) {
			itemIndex = getIntent().getIntExtra("itemIndex", -1);
			mGroupSyncModel = (GroupSyncModel) getIntent().getSerializableExtra("mGroupSyncModel");
		}
		mAdapter = new ChildItemArrayAdapter(mContext, 0, mItemModels);
		mAdapter.setmCampGroupModel(mCampGroupModel);
		lvItems.setAdapter(mAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (isDropbox) {
			mGroupSyncModel.listPackages.get(itemIndex).listItems.get(position).selected = !mItemModels.get(position)
					.isSelected();
			String json = gson.toJson(mGroupSyncModel);
			writeToFile(json, getFilesDir().getAbsolutePath() + "/dropbox_download/" + mGroupSyncModel.name + ".json");
		}
		mAdapter.setChecked(position, !mItemModels.get(position).isSelected());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		if (!isDropbox) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.child_item_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add:
			addNewItem();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addNewItem() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add new package");
		alert.setMessage("Enter new package name:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setSingleLine(true);
		input.setHint("Package name");

		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newPackageName = input.getText().toString();
				// Do something with value!
				ItemModel itemModel = new ItemModel();
				itemModel.setItemName(newPackageName);
				itemModel.setParentId(mParentItem.getUid());
				mItemTableHelper.insert(itemModel);
				itemModel = mItemTableHelper.getByName(itemModel.getItemName());
				itemModel.setSelected(true);
				mParentItem.getChildItems().add(itemModel);

				// checked item
				ItemGroupModel itemGroupModel = new ItemGroupModel();
				itemGroupModel.setItemId(itemModel.getUid());
				itemGroupModel.setGroupId(mCampGroupModel.getUid());
				mItemGroupTableHelper.insert(itemGroupModel);

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

			break;

		default:
			break;
		}
	}

}
