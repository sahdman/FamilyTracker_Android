package com.shadman.familytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserTable {

	private static final String DATABASE_CREATE = "create table family_tracker_user (field varchar(100) not null," +
			"value varchar(100) not null);";
	private static final String DATABASE_NAME = "family_tracker";
	private static final String DATABASE_TABLE = "family_tracker_user";
	private static final String COLUMN_FIELD = "field";
	private static final String COLUMN_VALUE = "value";
	
	static final String ROW_FIELD_ID = "id";
	static final String ROW_FIELD_USER_NAME = "user_name";
	static final String ROW_FIELD_PASS = "password";
	
	private SQLiteDatabase db;
	
	public UserTable(Context ctx) {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
		if (! isTableExists(db, DATABASE_TABLE) ) {
			db.execSQL(DATABASE_CREATE);
		}
	}
	
	public void close() {
		db.close();
	}
	
	public boolean isTableExists(SQLiteDatabase db, String tableName)
	{
	    if (tableName == null || db == null || !db.isOpen())
	    {
	        return false;
	    }
	    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
	    if (!cursor.moveToFirst())
	    {
	        return false;
	    }
	    int count = cursor.getInt(0);
	    cursor.close();
	    return count > 0;
	}
	
	public void insertRow(String field, String value) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_FIELD, field);
        initialValues.put(COLUMN_VALUE, value);
        db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public void updateRow(String field, String value) {
		String clause = COLUMN_FIELD + " = " + field;
		ContentValues args = new ContentValues();
        args.put(COLUMN_VALUE, value);
        db.update(DATABASE_TABLE, args, clause, null);
	}
	
	public void insertOrUpdate(String field, String value) {
		if ( isRowPresent(field) ) 
			updateRow(field, value);
		else 
			insertRow(field, value);
	}
	
	public void SaveUser(int id, String user_name, String pass) {
		String id_string = id + "";
		insertOrUpdate(ROW_FIELD_ID, id_string);
		insertOrUpdate(ROW_FIELD_USER_NAME, user_name);
		insertOrUpdate(ROW_FIELD_PASS, pass);
	}
	
	public boolean isRowPresent(String field) {
		String selection = COLUMN_FIELD + " = '" + field + "'";
		Cursor c = db.query(DATABASE_TABLE, new String[] {COLUMN_FIELD}, selection, null, null, null, null);
		if ( c.getCount() > 0 ) 
			return true;
		return false;
	}
	
	public boolean isUserSaved() {
		if ( isRowPresent(ROW_FIELD_ID) && isRowPresent(ROW_FIELD_PASS) ) 
			return true;
		return false;
	}
	
	public String getValue(String field) {
		if (!isRowPresent(field))
			return null;
		String selection = COLUMN_FIELD + " = '" + field + "'";
		Cursor c = db.query(DATABASE_TABLE, new String[] {COLUMN_VALUE}, selection, null, null, null, null);
		c.moveToFirst();
		return c.getString(0);
	}
}
