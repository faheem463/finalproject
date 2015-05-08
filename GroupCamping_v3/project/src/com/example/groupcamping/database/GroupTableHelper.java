package com.example.groupcamping.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.groupcamping.database.DatabaseDefinition.BaseDefinetion;
import com.example.groupcamping.database.DatabaseDefinition.GroupDefinetion;
import com.example.groupcamping.model.GroupModel;

public class GroupTableHelper extends BaseTableHelper<GroupModel> {

	public GroupTableHelper(Context context) {
		super(context);
	}

	@Override
	protected BaseDefinetion getTableDefinetion() {
		return new GroupDefinetion();
	}

	@Override
	protected ContentValues getValuesObject(GroupModel model) {

		ContentValues values = new ContentValues();
		values.put(GroupDefinetion.COL_UID, model.getUid() <= 0 ? null : model.getUid());
		values.put(GroupDefinetion.COL_JSON_DATA, model.getJsonData());
		return values;
	}

	@Override
	protected ArrayList<GroupModel> parseCursor(Cursor c) {
		ArrayList<GroupModel> list = new ArrayList<GroupModel>();
		if (c != null && c.moveToFirst()) {
			do {
				GroupModel model = new GroupModel();
				model.setUid(c.getInt(c.getColumnIndex(GroupDefinetion.COL_UID)));
				model.setJsonData(c.getString(c.getColumnIndex(GroupDefinetion.COL_JSON_DATA)));
				list.add(model);
			} while (c.moveToNext());
		}
		return list;
	}

	@Override
	public GroupModel getObject(String uid) {
		return super.getObject(uid);
	}

	@Override
	public ArrayList<GroupModel> getAll() {

		mSqlOrderClause = String.format(" %s ASC", GroupDefinetion.COL_UID);
		return super.getAll();
	}

	@Override
	protected void updateWhereSQLClause(GroupModel model) {
		mSqlWhereClause = String.format("%s = '%s'", GroupDefinetion.COL_UID, model.getUid());
	}
}
