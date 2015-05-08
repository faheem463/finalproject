package com.example.groupcamping.utis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.example.groupcamping.R;
import com.example.groupcamping.database.GroupTableHelper;
import com.example.groupcamping.database.ItemTableHelper;
import com.example.groupcamping.model.CampGroupModel;
import com.example.groupcamping.model.ItemModel;
import com.example.groupcamping.model.sync.GroupSyncModel;
import com.example.groupcamping.model.sync.ItemSyncModel;
import com.example.groupcamping.model.sync.ParentItemSyncModel;

public class MyUtils {

	public static void readItemsDataFromRaw(Context context) {

		// Get Data From Text Resource File Contains Json Data.
		InputStream inputStream = context.getResources().openRawResource(R.raw.items);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int ctr;
		try {
			ctr = inputStream.read();
			while (ctr != -1) {
				byteArrayOutputStream.write(ctr);
				ctr = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.v("Text Data", byteArrayOutputStream.toString());
		try {
			// Parse the data into jsonobject to get original data in form of json.
			JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
			int version = jObject.getInt("version");
			ItemTableHelper itemTableHelper = new ItemTableHelper(context);

			// update
			if (MyPreferenceUtils.getInt(context, "VERSION", -1) < version || itemTableHelper.getAll().size() <= 0) {
				itemTableHelper.deleteAll();
				// CampGroupTableHelper campGroupTableHelper = new CampGroupTableHelper(context);
				JSONObject itemsObject = jObject.getJSONObject("items");
				Iterator<?> keys = itemsObject.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					ItemModel itemModel = new ItemModel();
					itemModel.setItemName(key);
					itemTableHelper.insert(itemModel);

					itemModel = itemTableHelper.getByName(itemModel.getItemName());
					// if (campGroupMode != null) {
					// }
					JSONArray jArray = (JSONArray) itemsObject.get(key);
					for (int i = 0; i < jArray.length(); i++) {
						ItemModel itemModel2 = new ItemModel();
						itemModel2.setParentId(itemModel.getUid());
						itemModel2.setItemName(jArray.getString(i));
						itemTableHelper.insert(itemModel2);
					}
				}

				MyPreferenceUtils.saveInt(context, "VERSION", version);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static GroupSyncModel createGroupSyncModel(Context context, CampGroupModel campGroupModel , ArrayList<ItemModel> items){
		
		GroupSyncModel groupSyncModel = new GroupSyncModel();
		groupSyncModel.name = campGroupModel.getCampGroupName();
		groupSyncModel.listPackages = new ArrayList<ParentItemSyncModel>();
		for (ItemModel itemModel : items) {
			
			boolean isSelect = false;
			for (ItemModel item : itemModel.getChildItems()) {
				if(item.isSelected()){
					isSelect = true;
					break;
				}
			}
			if (!isSelect) {
				continue;
			}
			
			ParentItemSyncModel itemSync = new ParentItemSyncModel();
			itemSync.name = itemModel.getItemName();
			itemSync.listItems = new ArrayList<ItemSyncModel>();
			
			for (ItemModel itemModel2 : itemModel.getChildItems()) {
				if(!itemModel2.isSelected())
					continue;
				ItemSyncModel itemSync2 = new ItemSyncModel();
				itemSync2.name = itemModel2.getItemName();	
				itemSync2.selected = false;
				itemSync.listItems.add(itemSync2);
			}
			groupSyncModel.listPackages.add(itemSync);
		}
		
		return groupSyncModel;
		
	}
}
