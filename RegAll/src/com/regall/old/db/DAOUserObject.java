package com.regall.old.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.regall.old.network.response.ResponseGetUserObjects.ClientObject;

public class DAOUserObject {

	private DBHelper mHelper;

	public DAOUserObject(Context context) {
		mHelper = new DBHelper(context);
	}

	public void deleteAll() {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete(DBHelper.OBJECTS_TABLE, null, null);
		db.close();
	}

	public long insert(ClientObject object) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		long insertId = db.insert(DBHelper.OBJECTS_TABLE, null, object.toContentValues());
		db.close();
		return insertId;
	}

	public boolean insertAll(Collection<ClientObject> objects) {
		boolean result = true;
		for (ClientObject object : objects) {
			long insertId = insert(object);
			result = insertId != -1;
		}
		return result;
	}

	public int delete(int id) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int rowsAffected = db.delete(DBHelper.OBJECTS_TABLE, DBHelper.OBJECTS_BACKEND_ID + "=?", new String[] { Integer.valueOf(id).toString() });
		db.close();
		return rowsAffected;
	}

	public ClientObject getById(int id) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = db.query(DBHelper.OBJECTS_TABLE, new String[] { DBHelper.OBJECTS_BACKEND_ID, DBHelper.OBJECTS_TITLE }, DBHelper.OBJECTS_BACKEND_ID + "=?", new String[] { Integer.valueOf(id)
				.toString() }, null, null, null);

		ClientObject object = null;
		if(cursor != null && cursor.moveToFirst()){
			object = ClientObject.fromDb(cursor);
		}
		
		cursor.close();
		db.close();
		return object;
	}
	
	public List<ClientObject> getAll(){
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.OBJECTS_TABLE, new String[]{DBHelper.OBJECTS_BACKEND_ID, DBHelper.OBJECTS_TITLE}, null, null, null, null, null);
		List<ClientObject> objects = new ArrayList<ClientObject>();
		if(cursor != null && cursor.moveToFirst()){
			while(!cursor.isAfterLast()){
				objects.add(ClientObject.fromDb(cursor));
				cursor.moveToNext();
			}
		}
		
		cursor.close();
		db.close();
		return objects;
	}
}
