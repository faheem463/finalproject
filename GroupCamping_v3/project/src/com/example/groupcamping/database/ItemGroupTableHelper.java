package com.example.groupcamping.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.groupcamping.database.DatabaseDefinition.BaseDefinetion;
import com.example.groupcamping.database.DatabaseDefinition.CampGroupDefinetion;
import com.example.groupcamping.database.DatabaseDefinition.ItemGroupDefinetion;
import com.example.groupcamping.model.ItemGroupModel;
import com.example.groupcamping.model.ItemModel;

public class ItemGroupTableHelper extends BaseTableHelper<ItemGroupModel> {

	public ItemGroupTableHelper(Context context) {
		super(context);
	}

	@Override
	protected BaseDefinetion getTableDefinetion() {
		return new ItemGroupDefinetion();
	}

	@Override
	protected ContentValues getValuesObject(ItemGroupModel model) {

		ContentValues values = new ContentValues();
		values.put(ItemGroupDefinetion.COL_UID, model.getUid() <= 0 ? null : model.getUid());
		values.put(ItemGroupDefinetion.COL_GROUP_ID, model.getGroupId());
		values.put(ItemGroupDefinetion.COL_ITEM_ID, model.getItemId());
		return values;
	}

	@Override
	protected ArrayList<ItemGroupModel> parseCursor(Cursor c) {
		ArrayList<ItemGroupModel> list = new ArrayList<ItemGroupModel>();
		if (c != null && c.moveToFirst()) {
			do {
				ItemGroupModel model = new ItemGroupModel();
				model.setUid(c.getInt(c.getColumnIndex(ItemGroupDefinetion.COL_UID)));
				model.setItemId(c.getInt(c.getColumnIndex(ItemGroupDefinetion.COL_ITEM_ID)));
				model.setGroupId(c.getInt(c.getColumnIndex(ItemGroupDefinetion.COL_GROUP_ID)));
				list.add(model);
			} while (c.moveToNext());
		}
		return list;
	}

	@Override
	public ItemGroupModel getObject(String uid) {
		return super.getObject(uid);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public ItemGroupModel getByGroup(int groupId) {

		mSqlWhereClause = String.format(" %s = %d", ItemGroupDefinetion.COL_GROUP_ID, groupId);
		ArrayList<ItemGroupModel> lst = getByQuery();
		if (lst.size() > 0)
			return lst.get(0);
		return null;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public ItemGroupModel getByItem(int itemId) {

		mSqlWhereClause = String.format(" %s = %d", ItemGroupDefinetion.COL_ITEM_ID, itemId);
		ArrayList<ItemGroupModel> lst = getByQuery();
		if (lst.size() > 0)
			return lst.get(0);
		return null;
	}

	@Override
	public ArrayList<ItemGroupModel> getAll() {

		mSqlOrderClause = String.format(" %s ASC", ItemGroupDefinetion.COL_UID);
		return super.getAll();
	}

	@Override
	protected void updateWhereSQLClause(ItemGroupModel model) {
		mSqlWhereClause = String.format("%s = '%s'", ItemGroupDefinetion.COL_GROUP_ID, model.getGroupId());
	}

	public ArrayList<ItemGroupModel> getListByGroup(int groupId) {

		mSqlWhereClause = String.format(" %s = %d", ItemGroupDefinetion.COL_GROUP_ID, groupId);
		return getByQuery();
	}

	public boolean delete(int groupId, int itemId) {
		mSqlWhereClause = String.format("%s = %d and %s = %d", ItemGroupDefinetion.COL_GROUP_ID, groupId,
				ItemGroupDefinetion.COL_ITEM_ID, itemId);
		return super.delete(null);
	}

	/**
	 * 
	 * @param groupId
	 * @return
	 */
	public ArrayList<ItemModel> getItemsByGroup(int groupId) {

		mSqlWhereClause = String.format(" %s = %d", ItemGroupDefinetion.COL_GROUP_ID, groupId);
		ArrayList<ItemGroupModel> lst = getByQuery();

		ItemTableHelper itemTableHelper = new ItemTableHelper(mContext);
		ArrayList<ItemModel> result = itemTableHelper.getParentItems();

		for (ItemModel parent : result) {
			ArrayList<ItemModel> childItems = itemTableHelper.getChildItems(parent.getUid());
			for (ItemGroupModel itemGroupModel : lst) {
				for (ItemModel itemModel : childItems) {
					if (itemGroupModel.getItemId() == itemModel.getUid()) {
						itemModel.setSelected(true);
						continue;
					}
				}
			}
			parent.setChildItems(childItems);
		}

		return result;
	}
	//
	// /**
	// *
	// * @param groupId
	// * @return
	// */
	// public ArrayList<ItemModel> getItemsByParent(int groupId) {
	//
	// mSqlWhereClause = String.format(" %s = %d", ItemGroupDefinetion.COL_GROUP_ID, groupId);
	// ArrayList<ItemGroupModel> lst = getByQuery();
	//
	// ItemTableHelper itemTableHelper = new ItemTableHelper(mContext);
	// ArrayList<ItemModel> result = itemTableHelper.getParentItems();
	//
	// for (ItemModel parent : result) {
	// ArrayList<ItemModel> childItems = itemTableHelper.getChildItems(parent.getUid());
	// for (ItemGroupModel itemGroupModel : lst) {
	// for (ItemModel itemModel : childItems) {
	// if(itemGroupModel.getItemId() == itemModel.getUid())
	// {
	// itemModel.setSelected(true);
	// continue;
	// }
	// }
	// }
	// parent.setChildItems(childItems);
	// }
	//
	// return result;
	// }
}
