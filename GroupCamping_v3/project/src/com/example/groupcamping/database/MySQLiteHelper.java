package com.example.groupcamping.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.groupcamping.utis.MyLog;

public class MySQLiteHelper extends SQLiteOpenHelper implements DatabaseDefinition {

	public MySQLiteHelper(Context context) {
		super(context, DatabaseDefinition.DATABASE_NAME, null, DatabaseDefinition.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (BaseDefinetion table : Tables) {
			try {
				db.execSQL(table.getCreateQuery());
			} catch (Exception e) {
				e.printStackTrace();
				MyLog.eGeneral("Create table " + table.getTableName() + " fail: " + e);
			}
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (BaseDefinetion table : Tables) {
			try {
				db.execSQL(table.getUpdateQuery());
			} catch (Exception e) {
				e.printStackTrace();
				MyLog.eGeneral("Upgrade table " + table.getTableName() + " fail: " + e);
			}
		}

		onCreate(db);
	}

}
