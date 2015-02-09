package com.regall.old.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.regall.R;

public class DialogHelper {

	public static AlertDialog showInputDialog(Context context, String title, String message, final InputListener inputListener, final String errorMessage) {

		LayoutInflater inflater = LayoutInflater.from(context);

		View layout = inflater.inflate(R.layout.dialog_input, null);

		final EditText editInput = (EditText) layout.findViewById(R.id.editInput);
		Button buttonOk = (Button) layout.findViewById(R.id.buttonOk);
		Button buttonCancel = (Button) layout.findViewById(R.id.buttonCancel);

		TextView dialogMessage = (TextView) layout.findViewById(R.id.textDialogMessage);
		dialogMessage.setText(message);

		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setView(layout);

		final AlertDialog dialog = builder.create();

		buttonOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String input = editInput.getText().toString().trim();
				if (!TextUtils.isEmpty(input)) {
					inputListener.onInputComplete(input);
					dialog.dismiss();
				} else {
					editInput.setError(errorMessage);
					editInput.requestFocus();
				}
			}
		});

		buttonCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		return dialog;
	}

	public interface InputListener {
		void onInputComplete(String userInput);
	}
	
	public static Dialog showConfirmBookingDialog(Context context, View layout, DialogInterface.OnClickListener okClickListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		builder.setTitle(R.string.message_dialog_confirm_booking);
		builder.setNegativeButton(R.string.button_back_to_edit, null);
		builder.setPositiveButton(R.string.button_confirm, okClickListener);
		return builder.create();
	}
	
	public static Dialog createConfirmDialog(Context context, int titleId, int messageId, DialogInterface.OnClickListener negativeClickListener){
		LayoutInflater inflater = LayoutInflater.from(context);
		
		View view = inflater.inflate(R.layout.dialog_confirm, null);
		TextView text = (TextView)view.findViewById(R.id.text);
		text.setText(messageId);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		builder.setTitle(titleId);
		builder.setNegativeButton(android.R.string.no, negativeClickListener);
		builder.setPositiveButton(android.R.string.yes, null);
		
		return builder.create();
	}
	
	public static Dialog createConfirmDialog(Context context, int titleId, String message, DialogInterface.OnClickListener okClickListener){
		LayoutInflater inflater = LayoutInflater.from(context);
		
		View view = inflater.inflate(R.layout.dialog_confirm, null);
		TextView text = (TextView)view.findViewById(R.id.text);
		text.setText(message);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);
		builder.setTitle(titleId);
		builder.setNegativeButton(android.R.string.no, null);
		builder.setPositiveButton(android.R.string.yes, okClickListener);
		
		return builder.create();
	}
	
	public static Dialog createModalMessageDialog(Context context, int titleId, int message, DialogInterface.OnClickListener okClickListener){
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.dialog_confirm, null);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(message);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(titleId);
		builder.setView(layout);
		builder.setPositiveButton(android.R.string.ok, okClickListener);
		return builder.create();
	}
}
