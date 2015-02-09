package com.regall.old.network.response;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.regall.old.db.DBHelper;
import com.regall.old.network.response.ResponseGetCarModels.CarModel;

import android.content.ContentValues;
import android.database.Cursor;

public class ResponseGetUserObjects extends BasicResponse {

	@Root(name = "client_object")
	public static class ClientObject {
		
		@Attribute(name = "client_id")
		private int mId;
		
		@Attribute(name = "phone")
		private String mPhone;
		
		@Attribute(name = "object_id")
		private int mObjectId;
		
		@Attribute(name = "object_name")
		private String mObjectName;

		public int getId() {
			return mId;
		}

		public String getPhone() {
			return mPhone;
		}

		public int getObjectId() {
			return mObjectId;
		}

		public String getObjectName() {
			return mObjectName;
		}
		
		public static ClientObject fromModel(CarModel carModel){
			ClientObject object = new ClientObject();
			object.mId = carModel.getObjectId();
			object.mObjectName = carModel.getName();
			return object;
		}
		
		public ContentValues toContentValues(){
			ContentValues cv = new ContentValues();
			cv.put(DBHelper.OBJECTS_BACKEND_ID, mObjectId);
			cv.put(DBHelper.OBJECTS_TITLE, mObjectName);
			return cv;
		}
		
		public static ClientObject fromDb(Cursor cursor){
			ClientObject object = new ClientObject();
			object.mObjectId = cursor.getInt(cursor.getColumnIndex(DBHelper.OBJECTS_BACKEND_ID));
			object.mObjectName = cursor.getString(cursor.getColumnIndex(DBHelper.OBJECTS_TITLE));
			return object;
		}
	}
	
	@ElementList(name = "client_object", required = false, inline = true)
	private List<ClientObject> mClientObjects;

	public List<ClientObject> getClientObjects() {
		return mClientObjects;
	}
}
