package com.regall.old.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.widget.Toast;

public class Logger {
	
	private final static String FILENAME = "regall.log";
	
	private static Context mContext;
	
	private static String mId;

	private static PrintWriter mLogWriter;
	
	public static void init(Context context, String id){
		mContext = context;
		mId = id;
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File storageDir = Environment.getExternalStorageDirectory();
			File logFile = new File(storageDir, FILENAME);
			try {
				if(!logFile.exists()){
					logFile.createNewFile();
				}
				
				mLogWriter = new PrintWriter(new FileOutputStream(logFile), true);
				mLogWriter.write("-------------------------------------------------------------------------\n");
				mLogWriter.write("setup debug session " + DateFormat.format("dd.MM.yyyy kk:mm:ss", Calendar.getInstance()) + "\n");
				mLogWriter.write("id - " + mId + "\n");
				mLogWriter.write("-------------------------------------------------------------------------\n");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(mContext, "Cannot open local log file!", Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(mContext, "Cannot create log file!", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(mContext, "Logging unavailable due to incorrect storage state!", Toast.LENGTH_LONG).show();
		}
	}
	
	public static void shutdown(){
//		mContext = null;
//		mLogWriter.flush();
//		mLogWriter.close();
	}
	
	public static void logDebug(String tag, String message){
		//String record = String.format("%s   -   %s   -   %s\n", "debug", tag, message);
		//mLogWriter.write(record);
	}
	
	public static void logError(String tag, String message){
		//String record = String.format("%s   -   %s   -   %s\n", "error", tag, message);
		//mLogWriter.write(record);
	}
	
}
