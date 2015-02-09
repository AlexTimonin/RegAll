package com.regall.old.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public final static String OBJECTS_TABLE = "objects";
	public final static String OBJECTS_BACKEND_ID = "backend_id";
	public final static String OBJECTS_TITLE = "title";

	private final static String CREATE_OBJECTS_TABLE = "CREATE TABLE IF NOT EXISTS " + OBJECTS_TABLE + "(" + OBJECTS_BACKEND_ID + " integer not null, " + OBJECTS_TITLE + " text not null)";

	public DBHelper(Context context) {
		super(context, "db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_OBJECTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// not used at now
	}

}
