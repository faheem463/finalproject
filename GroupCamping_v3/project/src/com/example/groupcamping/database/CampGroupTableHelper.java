package com.example.groupcamping.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.groupcamping.database.DatabaseDefinition.BaseDefinetion;
import com.example.groupcamping.database.DatabaseDefinition.CampGroupDefinetion;
import com.example.groupcamping.model.CampGroupModel;

public class CampGroupTableHelper extends BaseTableHelper<CampGroupModel> {

	public CampGroupTableHelper(Context context) {
		super(context);
	}

	@Override
	protected BaseDefinetion getTableDefinetion() {
		return new CampGroupDefinetion();
	}

	@Override
	protected ContentValues getValuesObject(CampGroupModel model) {

		ContentValues values = new ContentValues();
		values.put(CampGroupDefinetion.COL_UID, model.getUid() <= 0 ? null : model.getUid());
		values.put(CampGroupDefinetion.COL_CAMP_GROUP_NAME, model.getCampGroupName());
		values.put(CampGroupDefinetion.COL_START_TIME, model.getStartTime());
		values.put(CampGroupDefinetion.COL_END_TIME, model.getEndTime());
		values.put(CampGroupDefinetion.COL_IS_CREATE_MYSELF, model.isCreateMySelf() ? 1 : 0);
		values.put(CampGroupDefinetion.COL_DROP_BOX_SHARE_FOLDER, model.getDropboxSharePath());
		return values;
	}

	@Override
	protected ArrayList<CampGroupModel> parseCursor(Cursor c) {
		ArrayList<CampGroupModel> list = new ArrayList<CampGroupModel>();
		if (c != null && c.moveToFirst()) {
			do {
				CampGroupModel model = new CampGroupModel();
				model.setUid(c.getInt(c.getColumnIndex(CampGroupDefinetion.COL_UID)));
				model.setCampGroupName(c.getString(c.getColumnIndex(CampGroupDefinetion.COL_CAMP_GROUP_NAME)));
				model.setStartTime(c.getString(c.getColumnIndex(CampGroupDefinetion.COL_START_TIME)));
				model.setEndTime(c.getString(c.getColumnIndex(CampGroupDefinetion.COL_END_TIME)));
				model.setDropboxSharePath(c.getString(c.getColumnIndex(CampGroupDefinetion.COL_DROP_BOX_SHARE_FOLDER)));
				model.setCreateMySelf(c.getInt(c.getColumnIndex(CampGroupDefinetion.COL_IS_CREATE_MYSELF)) > 0 ? true
						: false);
				list.add(model);
			} while (c.moveToNext());
		}
		return list;
	}

	@Override
	public CampGroupModel getObject(String uid) {
		return super.getObject(uid);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public CampGroupModel getyName(String name) {

		mSqlWhereClause = String.format(" %s = '%s'", CampGroupDefinetion.COL_CAMP_GROUP_NAME, name);
		ArrayList<CampGroupModel> lst = getByQuery();
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

		mSqlWhereClause = String.format("%s = %s", CampGroupDefinetion.COL_CAMP_GROUP_NAME, name);
		return super.delete(null);
	}

	@Override
	public ArrayList<CampGroupModel> getAll() {

		mSqlOrderClause = String.format(" %s ASC", CampGroupDefinetion.COL_UID);
		return super.getAll();
	}

	@Override
	protected void updateWhereSQLClause(CampGroupModel model) {
		mSqlWhereClause = String.format("%s = '%s'", CampGroupDefinetion.COL_CAMP_GROUP_NAME, model.getCampGroupName());
	}
}
