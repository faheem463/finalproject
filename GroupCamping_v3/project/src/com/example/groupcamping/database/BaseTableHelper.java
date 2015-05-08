package com.example.groupcamping.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.groupcamping.database.DatabaseDefinition.BaseDefinetion;
import com.example.groupcamping.utis.MyLog;

public abstract class BaseTableHelper<M> {

	protected BaseDefinetion mDefinetion;
	protected MySQLiteHelper mSqLiteHelper;
	protected Context mContext;

	protected String mPrimaryKey;
	protected String mTableName;
	protected String[] mSelectColumns;

	protected String mSqlWhereClause;
	protected String mSqlOrderClause;

	public BaseTableHelper(Context context) {
		mContext = context;
		mSqLiteHelper = new MySQLiteHelper(context);
		mDefinetion = getTableDefinetion();
		mSelectColumns = mDefinetion.getColoumns();
		mTableName = mDefinetion.getTableName();
		mPrimaryKey = mDefinetion.getPrimaryKey();
	}

	abstract protected BaseDefinetion getTableDefinetion();
	abstract protected ContentValues getValuesObject(M model);
	abstract protected ArrayList<M> parseCursor(Cursor c);
	abstract protected void updateWhereSQLClause(M model);

	/**
	 * 
	 * @param model
	 * @return
	 */
	public long insert(M model) {

		SQLiteDatabase db = null;
		long id = -1;
		boolean needUpdate = false;
		synchronized (this) {
			try {
				db = mSqLiteHelper.getWritableDatabase();
				try {
					db.beginTransaction();
					id = db.insertOrThrow(mTableName, null, getValuesObject(model));
					db.setTransactionSuccessful();
					logInfo("insert id: " + id);
					// MyLog.iGeneral("BaseTableHelper-" + mTableName + "-insert id: " + id);
				} catch (SQLException e) {
					// update it when exist
					if (e.getMessage() != null && e.getMessage().contains("not unique")) {
						needUpdate = true;
					}
					e.printStackTrace();
					logError("insert error: " + e);
					// MyLog.eGeneral("BaseTableHelper-" + mTableName + "-insert error: " + e);
					return -1;
				} catch (Exception e) {
					e.printStackTrace();
					logError("insert error: " + e);
					// MyLog.eGeneral("BaseTableHelper-" + mTableName + "-insert error: " + e);
					return -1;
				} finally {
					db.endTransaction();
				}
			} finally {
				if (db != null) {
					db.close();
				}
			}
		}
		if (needUpdate)
			update(model);
		return id;
	}
	/**
	 * 
	 * @param model
	 * @return
	 */
	public boolean update(M model) {

		updateWhereSQLClause(model);
		SQLiteDatabase db = null;
		synchronized (this) {
			try {
				db = mSqLiteHelper.getWritableDatabase();
				try {
					db.beginTransaction();
					db.update(mTableName, getValuesObject(model), mSqlWhereClause, null);
					db.setTransactionSuccessful();
					logInfo("update id: " + model);
					// MyLog.iGeneral("BaseTableHelper-" + mTableName + "-update id: " + model);
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					logError("update " + model + " error: " + e);
					// MyLog.eGeneral("BaseTableHelper-" + mTableName + "-update error: " + e);
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					logError("update " + model + " error: " + e);
					// MyLog.eGeneral("BaseTableHelper-" + mTableName + "-update error: " + e);
					return false;
				} finally {
					db.endTransaction();
				}
			} finally {
				if (db != null) {
					db.close();
				}
			}
		}
	}

	/**
	 * 
	 * @param uid
	 * @return
	 */
	public boolean delete(String uid) {

		if (uid != null)
			mSqlWhereClause = String.format("%s = '%s'", mPrimaryKey, uid);
		SQLiteDatabase db = null;
		synchronized (this) {
			try {
				db = mSqLiteHelper.getWritableDatabase();
				try {
					db.beginTransaction();
					db.delete(mTableName, mSqlWhereClause, null);
					db.setTransactionSuccessful();
					logInfo("delete id: " + uid);
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					logError("delete id: " + uid + " error: " + e);
					MyLog.eGeneral("BaseTableHelper-" + mTableName + "-delete error: " + e);
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					logError("delete id: " + uid + " error: " + e);
					// MyLog.eGeneral("BaseTableHelper-" + mTableName + "-delete error: " + e);
					return false;
				} finally {
					db.endTransaction();
				}
			} finally {
				if (db != null) {
					db.close();
				}
			}
		}
	}
	public boolean deleteAll() {

		SQLiteDatabase db = null;
		synchronized (this) {
			try {
				db = mSqLiteHelper.getWritableDatabase();
				try {

					db.beginTransaction();
					db.delete(mTableName, " 1 ",null);
					db.setTransactionSuccessful();
					logInfo("deleteAll done");
					// MyLog.iLog("SmsTableHelper-deleteAll done");
					return true;
				} catch (SQLException e) {
					e.printStackTrace();
					logError("deleteAll error: " + e);
					return false;
				} catch (Exception e) {
					e.printStackTrace();
					logError("deleteAll error: " + e);
					return false;
				} finally {
					db.endTransaction();
				}
			} finally {
				if (db != null) {
					db.close();
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<M> getAll() {
		return getByQuery(null);
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<M> getByQuery() {

		ArrayList<M> lst = new ArrayList<M>();

		SQLiteDatabase db = null;
		Cursor c = null;

		try {
			db = mSqLiteHelper.getReadableDatabase();
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(mTableName);

			c = qb.query(db, // Database
					null, // select columns
					mSqlWhereClause, // where clause
					null, // argument[]
					null, // groupBy
					null, // having
					mSqlOrderClause,// order
					null // limit
			);
			lst = parseCursor(c);
		} catch (SQLException e) {
			e.printStackTrace();
			logError("getAll error: " + e);
		} catch (Exception e) {
			e.printStackTrace();
			logError("getAll error: " + e);
		} finally {
			if (db != null) {
				db.close();
			}
			if (c != null) {
				c.close();
			}
		}
		return lst;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<M> getByQuery(String query) {

		mSqlWhereClause = query;
		return getByQuery();
	}

	/**
	 * 
	 * @param uid
	 * @return
	 */
	public M getObject(String uid) {

		ArrayList<M> lst = getByQuery();
		if (lst.size() > 0)
			return lst.get(0);
		return null;
	}

	/**
	 * 
	 * @param mess
	 */
	private void logError(String mess) {
		MyLog.eGeneral("BaseTableHelper-" + mTableName + "-" + mess);
	}

	/**
	 * 
	 * @param mess
	 */
	private void logInfo(String mess) {
		MyLog.iGeneral("BaseTableHelper-" + mTableName + "-" + mess);
	}
}
