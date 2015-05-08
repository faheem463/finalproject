package com.example.groupcamping.database;

import java.lang.Character.UnicodeBlock;

public interface DatabaseDefinition {

	public static final String DATABASE_NAME = "camp_group_db";
	public static final int DATABASE_VERSION = 8;

	public static final String CREATE_SQL = "createTable";
	public static final String UPDATE_SQL = "updateTable";

	public final static String CREATE_TABLE = "CREATE TABLE ";
	public final static String ALTER_TABLE = "ALTER TABLE ";
	public final static String ADD_COLUMN = " ADD COLUMN ";
	public final static String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

	public final static String T_LONG = " LONG ";
	public final static String T_DOUBLE = " DOUBLE ";
	public final static String T_INTEGER = " INTEGER ";
	public final static String T_TEXT = " TEXT ";

	//
	public final static String T_UNIQUE = " UNIQUE ";
	public final static String T_PRIMARY_KEY_AUTOINCREMENT = " PRIMARY KEY AUTOINCREMENT ";
	public final static String T_PRIMARY_KEY = " PRIMARY KEY ";
	public final static String T_NOCASE = " COLLATE NOCASE ";

	/** tables in database */
	public static final BaseDefinetion[] Tables = { new CampGroupDefinetion(), new ItemDefinetion(),
			new ItemGroupDefinetion(), new GroupDefinetion() };

	/**
	 * 
	 */
	public interface BaseDefinetion {

		public String getCreateQuery();

		public String getUpdateQuery();

		public String[] getColoumns();

		public String getTableName();

		public String getPrimaryKey();
	}

	/**
	 * table CampGroup *
	 */
	public static final class CampGroupDefinetion implements BaseDefinetion {

		public static final String TABLE_NAME = "tbl_CampGroup";

		public static final String COL_UID = "c_uid";
		public static final String COL_CAMP_GROUP_NAME = "c_camp_group_name";
		public static final String COL_START_TIME = "c_start_time";
		public static final String COL_END_TIME = "c_end_time";
		public static final String COL_IS_CREATE_MYSELF = "COL_IS_CREATE_MYSELF";
		public static final String COL_DROP_BOX_SHARE_FOLDER = "COL_DROP_BOX_SHARE_FOLDER";

		@Override
		public String[] getColoumns() {
			return new String[] { // 0
			COL_UID, // 1
					COL_CAMP_GROUP_NAME, // 2
					COL_START_TIME, // 3
					COL_END_TIME, // 4
					COL_IS_CREATE_MYSELF, // 4
					COL_DROP_BOX_SHARE_FOLDER };
		}

		@Override
		public String getTableName() {
			return TABLE_NAME;
		}

		@Override
		public String getPrimaryKey() {
			return COL_UID;
		}

		@Override
		public String getCreateQuery() {
			return CREATE_TABLE + TABLE_NAME + " (" // 0
					+ COL_UID + T_INTEGER + T_PRIMARY_KEY_AUTOINCREMENT + "," // 1
					+ COL_CAMP_GROUP_NAME + T_TEXT + T_UNIQUE + "," // 2
					+ COL_START_TIME + T_TEXT + "," // 3
					+ COL_END_TIME + T_TEXT + "," // 3
					+ COL_IS_CREATE_MYSELF + T_INTEGER + "," // 3
					+ COL_DROP_BOX_SHARE_FOLDER + T_TEXT // 3
					+ ")";
		}

		@Override
		public String getUpdateQuery() {
			return DROP_TABLE_IF_EXISTS + TABLE_NAME;
		}

	}

	/**
	 * table Item *
	 */
	public static final class ItemDefinetion implements BaseDefinetion {

		public static final String TABLE_NAME = "tbl_Item";

		public static final String COL_UID = "c_uid";
		public static final String COL_ITEM_NAME = "c_item_name";
		public static final String COL_PARENT_ID = "c_start_time";

		@Override
		public String[] getColoumns() {
			return new String[] { // 0
			COL_UID, // 1
					COL_ITEM_NAME, // 2
					COL_PARENT_ID // 3
			};
		}

		@Override
		public String getTableName() {
			return TABLE_NAME;
		}

		@Override
		public String getPrimaryKey() {
			return COL_UID;
		}

		@Override
		public String getCreateQuery() {
			return CREATE_TABLE + TABLE_NAME + " (" // 0
					+ COL_UID + T_INTEGER + T_PRIMARY_KEY_AUTOINCREMENT + "," // 1
					+ COL_ITEM_NAME + T_TEXT + T_UNIQUE + "," // 2
					+ COL_PARENT_ID + T_INTEGER // 3
					+ ")";
		}

		@Override
		public String getUpdateQuery() {
			return DROP_TABLE_IF_EXISTS + TABLE_NAME;
		}

	}

	/**
	 * table Item & group*
	 */
	public static final class ItemGroupDefinetion implements BaseDefinetion {

		public static final String TABLE_NAME = "tbl_ItemGroup";

		public static final String COL_UID = "c_uid";
		public static final String COL_ITEM_ID = "COL_ITEM_ID";
		public static final String COL_GROUP_ID = "COL_GROUP_ID";

		@Override
		public String[] getColoumns() {
			return new String[] { // 0
			COL_UID, // 1
					COL_ITEM_ID, // 2
					COL_GROUP_ID // 3
			};
		}

		@Override
		public String getTableName() {
			return TABLE_NAME;
		}

		@Override
		public String getPrimaryKey() {
			return COL_UID;
		}

		@Override
		public String getCreateQuery() {
			return CREATE_TABLE + TABLE_NAME + " (" // 0
					+ COL_UID + T_INTEGER + T_PRIMARY_KEY_AUTOINCREMENT + "," // 1
					+ COL_ITEM_ID + T_INTEGER + "," // 2
					+ COL_GROUP_ID + T_INTEGER // 3
					+ ")";
		}

		@Override
		public String getUpdateQuery() {
			return DROP_TABLE_IF_EXISTS + TABLE_NAME;
		}

	}

	/**
	 * table Group *
	 */
	public static final class GroupDefinetion implements BaseDefinetion {

		public static final String TABLE_NAME = "tbl_Group";

		public static final String COL_UID = "c_uid";
		public static final String COL_JSON_DATA = "COL_JSON_DATA";

		@Override
		public String[] getColoumns() {
			return new String[] { // 0
			COL_UID, // 1
					COL_JSON_DATA, // 2
			};
		}

		@Override
		public String getTableName() {
			return TABLE_NAME;
		}

		@Override
		public String getPrimaryKey() {
			return COL_UID;
		}

		@Override
		public String getCreateQuery() {
			return CREATE_TABLE + TABLE_NAME + " (" // 0
					+ COL_UID + T_INTEGER + T_PRIMARY_KEY_AUTOINCREMENT + "," // 1
					+ COL_JSON_DATA + T_TEXT // 2
					+ ")";
		}

		@Override
		public String getUpdateQuery() {
			return DROP_TABLE_IF_EXISTS + TABLE_NAME;
		}

	}

}
