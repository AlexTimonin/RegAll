package com.regall.old.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.text.format.DateFormat;

public class DateHelper {

	@SuppressLint("SimpleDateFormat")
	public static String convert(String dateString, String from, String to) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(from);
		Date date = dateFormat.parse(dateString);
		return DateFormat.format(to, date).toString();
	}

}
