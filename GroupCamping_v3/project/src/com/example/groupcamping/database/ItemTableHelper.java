package com.example.groupcamping.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.groupcamping.database.DatabaseDefinition.BaseDefinetion;
import com.example.groupcamping.database.DatabaseDefinition.ItemDefinetion;
import com.example.groupcamping.model.ItemModel;

public class ItemTableHelper extends BaseTableHelper<ItemModel> {

	public ItemTableHelper(Context context) {
		super(context);
	}

	@Override
	protected BaseDefinetion getTableDefinetion() {
		return new ItemDefinetion();
	}

	@Override
	protected ContentValues getValuesObject(ItemModel model) {

		ContentValues values = new ContentValues();
		values.put(ItemDefinetion.COL_UID, model.getUid() <= 0 ? null : model.getUid());
		values.put(ItemDefinetion.COL_ITEM_NAME, model.getItemName());
		values.put(ItemDefinetion.COL_PARENT_ID, model.getParentId());
		return values;
	}

	@Override
	protected ArrayList<ItemModel> parseCursor(Cursor c) {
		ArrayList<ItemModel> list = new ArrayList<ItemModel>();
		if (c != null && c.moveToFirst()) {
			do {
				ItemModel model = new ItemModel();
				model.setUid(c.getInt(c.getColumnIndex(ItemDefinetion.COL_UID)));
				model.setItemName(c.getString(c.getColumnIndex(ItemDefinetion.COL_ITEM_NAME)));
				model.setParentId(c.getInt(c.getColumnIndex(ItemDefinetion.COL_PARENT_ID)));
				list.add(model);
			} while (c.moveToNext());
		}
		return list;
	}

	@Override
	public ItemModel getObject(String uid) {
		return super.getObject(uid);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public ItemModel getByName(String name) {

		mSqlWhereClause = String.format(" %s = '%s'", ItemDefinetion.COL_ITEM_NAME, name);
		ArrayList<ItemModel> lst = getByQuery();
		if (lst.size() > 0)
			return lst.get(0);
		return null;
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public boolean deleteByName(String name) {

		mSqlWhereClause = String.format("%s = %s", ItemDefinetion.COL_ITEM_NAME, name);
		return super.delete(null);
	}

	@Override
	public ArrayList<ItemModel> getAll() {

		mSqlOrderClause = String.format(" %s ASC", ItemDefinetion.COL_UID);
		return super.getAll();
	}

	public ArrayList<ItemModel> getParentItems() {

		mSqlWhereClause = String.format("%s is null or %s = 0", ItemDefinetion.COL_PARENT_ID,
				ItemDefinetion.COL_PARENT_ID);
		return super.getByQuery();
	}

	public ArrayList<ItemModel> getChildItems(int parentId) {

		mSqlWhereClause = String.format("%s = %d", ItemDefinetion.COL_PARENT_ID, parentId);
		return super.getByQuery();
	}

	// public ArrayList<ItemModel> getChildItems(int parentId, int groupId) {
	//
	// mSqlWhereClause = String.format("%s = %d", ItemDefinetion.COL_PARENT_ID, parentId);
	// ArrayList<ItemModel> result = super.getByQuery();
	//
	// ItemGroupTableHelper itemGroupTableHelper = new ItemGroupTableHelper(mContext);
	// ArrayList<ItemGroupModel> lst = itemGroupTableHelper.getListByGroup(groupId);
	// for (ItemGroupModel itemGroupModel : lst) {
	// for (ItemModel item : result) {
	// if (item.getUid() == itemGroupModel.getItemId()) {
	// item.setSelected(true);
	// continue;
	// }
	// }
	// }
	//
	// return result;
	// }

	@Override
	protected void updateWhereSQLClause(ItemModel model) {
		mSqlWhereClause = String.format("%s = '%s'", ItemDefinetion.COL_ITEM_NAME, model.getItemName());
	}
}
