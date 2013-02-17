package com.kakao.android.common;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * AlertDialog 및 Toast 출력에 사용하는 Utility class
 */
public class MessageUtil {
	public static void alert(Activity activity, String message) {
		new AlertDialog.Builder(activity)
			.setTitle("SDK Example App")
			.setMessage("" + message)
			.setPositiveButton(android.R.string.ok, null)
			.create().show();
	}
	
	public static void alert(Activity activity, String title, String message) {
		new AlertDialog.Builder(activity)
		.setTitle(title)
		.setMessage("" + message)
		.setPositiveButton(android.R.string.ok, null)
		.create().show();
	}
	
	public static void toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void toastForError(Context context, int httpStatus, int kakaoStatus, JSONObject result) {
		Toast.makeText(context, "onError() - httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result, Toast.LENGTH_SHORT).show();
	}
	
	public static void toastForComplete(Context context, int status, int statusCode, JSONObject result) {
		Toast.makeText(context, "onComplete() - httpStatus: " + status + ", kakaoStatus: " + statusCode + ", result: " + result, Toast.LENGTH_SHORT).show();
	}
}
