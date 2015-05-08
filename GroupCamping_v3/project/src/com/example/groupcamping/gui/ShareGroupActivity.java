package com.example.groupcamping.gui;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.groupcamping.R;
import com.example.groupcamping.adapter.ContactsArrayAdapter;
import com.example.groupcamping.dropbox.Defines;
import com.example.groupcamping.dropbox.UploadFile;
import com.example.groupcamping.model.CampGroupModel;
import com.example.groupcamping.model.ItemModel;
import com.example.groupcamping.model.sync.GroupSyncModel;
import com.example.groupcamping.model.sync.ParentItemSyncModel;
import com.example.groupcamping.utis.MyLog;
import com.example.groupcamping.utis.MyUtils;
import com.google.gson.Gson;

public class ShareGroupActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private String url;
	private ListView lvContacts;
	private Button btnShare;
	private ContactsArrayAdapter adapter;
	private ArrayList<String> listContacts;
	private ArrayList<String> listPhones;

	// /
	private CampGroupModel mCampGroupModel;
	private ArrayList<ItemModel> mItemModels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_group);
		initView();

		mCampGroupModel = (CampGroupModel) getIntent().getSerializableExtra("mCampGroupModel");
		mItemModels = (ArrayList<ItemModel>) getIntent().getSerializableExtra("mItemModels");
	}

	private void initView() {

		//
		lvContacts = (ListView) findViewById(R.id.lv_contacts);
		lvContacts.setOnItemClickListener(this);

		btnShare = (Button) findViewById(R.id.btn_share);
		btnShare.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadListview();
	}

	private void loadListview() {
		getAllCallLogs();
		adapter = new ContactsArrayAdapter(mContext, 0, listContacts);
		adapter.setmPhones(listPhones);
		lvContacts.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share:
			shareGroup();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		adapter.setChecked(position);
	}

	public void getAllCallLogs() {
		ContentResolver cr = this.getContentResolver();
		listContacts = new ArrayList<String>();
		listPhones = new ArrayList<String>();
		Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
		while (phones.moveToNext()) {
			String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			System.out.println(".................." + phoneNumber);
			listContacts.add(name + " - " + phoneNumber);
			listPhones.add(phoneNumber);
		}

		phones.close();
	}

	private void shareGroup() {

		ArrayList<String> phones = adapter.getSelectPhones();

		if (phones.size() <= 0) {
			myToast.showToast("Error. Select contact first");
			return;
		}

		Gson gson = new Gson();
		GroupSyncModel groupSyncModel = MyUtils.createGroupSyncModel(mContext, mCampGroupModel, mItemModels);
		// boolean isSelected = false;
		// for (ParentItemSyncModel pakage : groupSyncModel.listPackages) {
		// if (pakage.listItems.size() > 0) {
		// isSelected = true;
		// break;
		// }
		// }
		if (groupSyncModel.listPackages.size() <= 0) {
			myToast.showToast("Error. You do not select any item");
			return;
		}

		String jsonString = gson.toJson(groupSyncModel);
		MyLog.iGeneral("Jsondata: " + jsonString);
		String filename = groupSyncModel.name + ".json";
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename,
					Context.MODE_PRIVATE));
			outputStreamWriter.write(jsonString);
			outputStreamWriter.close();

			File file = new File(getFilesDir() + "/" + filename);
			UploadFile uploadFile = new UploadFile(mContext, mApi, Defines.DROPBOX_DIR, file, phones);
			uploadFile.execute();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
}
