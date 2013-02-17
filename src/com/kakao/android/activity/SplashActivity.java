package com.kakao.android.activity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;

import com.kakao.android.common.KakaoManager;
import com.kakao.android.common.MessageUtil;
import com.kakao.api.Kakao;
import com.kakao.api.KakaoResponseHandler;
import com.kakao.api.Logger;
import com.kakao.android.R;

public class SplashActivity extends Activity {

	private static final int DELAY_TIME = 2000;

	private Kakao kakao; 

	private Map<String, String> mChatPlusMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);

		kakao = KakaoManager.getKakao(getApplicationContext());
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//startActivitySplashScreen();
				if (kakao.hasTokens()) {
					if (isChattingPlusData(getIntent().getData())) {						
						startActivityChattingPlus();
					} else {					
						// 1 로컬유저 호출
						localUser();
					}					
				} else {
					startActivity(new Intent(SplashActivity.this, SplashScreen.class));
					//startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				}
			}
		}, DELAY_TIME);
	}
	
	/**
	 * 내정보를 가져옵니다.
	 */
	private void localUser() {
		KakaoResponseHandler localUserHandler = new KakaoResponseHandler(getApplicationContext()) {
			
			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				
				if (kakaoStatus == Kakao.STATUS_INVALID_GRANT) {
					MessageUtil.toastForError(getApplicationContext(), httpStatus, kakaoStatus, result);
					// TODO kakaoStatus가 STATUS_INVALID_GRANT 이거나, KakaoTokenListener에 access token이 null 또는 빈문자열로 오는경우 logout | 어플 재시작 | 종료
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					return;
				}
				
				String message = "내 정보를 불러오지 못했습니다.<br/>다시 시도하시겠습니까?";
				
				new AlertDialog.Builder(SplashActivity.this)
					.setMessage(Html.fromHtml(message))
					.setPositiveButton("네", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 로컬유저 재호출
							localUser();
						}
					})
					.create().show();
			}
			
			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
				// 2 로컬유저 완료시 friends 호출
				friends();
			}
		};
		
		kakao.localUser(localUserHandler);
	}
	
	/**
	 * 친구목록을 가져옵니다. 
	 */
	@SuppressLint("NewApi")
	private void friends() {
		KakaoResponseHandler friendsHandler = new KakaoResponseHandler(getApplicationContext()) {
			
			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				if (kakaoStatus == Kakao.STATUS_INVALID_GRANT) {
					MessageUtil.toastForError(getApplicationContext(), httpStatus, kakaoStatus, result);
					// TODO kakaoStatus가 STATUS_INVALID_GRANT 이거나, KakaoTokenListener에 access token이 null 또는 빈문자열로 오는경우 logout | 어플 재시작 | 종료
					return;
				}
				
				String message = "친구목록을 불러오지 못했습니다.<br/>다시 시도하시겠습니까?";
				
				new AlertDialog.Builder(SplashActivity.this)
					.setMessage(message)
					.setPositiveButton("네", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 친구목록 재호출
							friends();
						}
					})
					.create().show();
			}
			
			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
				// 3 localUser & friends 완료시 화면 전환
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			}
		};
		
		kakao.friends(friendsHandler);
	}
	
	private boolean isChattingPlusData(Uri data) {
		if (data == null) {
			mChatPlusMap = null;
			return false;
		}

		Logger.getInstance().d("kakao-android-sdk", "getData: " + data);

		String query = data.getQuery();
		if (query == null || query.trim().length() <= 0) {
			mChatPlusMap = null;
			return false;
		}

		Map<String, String> tempMap = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(query, "&");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			int position = token.indexOf("=");
			if (position == -1) {
				mChatPlusMap = null;
				return false;
			}

			String key = token.substring(0, position);
			String value = token.substring(position + 1);

			tempMap.put(key, value);
		}

		if (tempMap.containsKey(ChattingPlusActivity.KEY_TITLE) && tempMap.containsKey(ChattingPlusActivity.KEY_CLIENT_TOKEN)
				&& tempMap.containsKey(ChattingPlusActivity.KEY_CHAT_ROOM_ID)) {
			mChatPlusMap = Collections.unmodifiableMap(tempMap);
			return true;
		}

		mChatPlusMap = null;
		return false;
	}
	
	private void startActivityChattingPlus() {
		Intent intent = new Intent(this, ChattingPlusActivity.class);
		intent.putExtra("title", mChatPlusMap.get("title"));
		intent.putExtra("clientToken", mChatPlusMap.get("clientToken"));
		intent.putExtra("chatRoomId", mChatPlusMap.get("chatRoomId"));
		startActivity(intent);
		mChatPlusMap = null;
	}
	
	private void startActivitySplashScreen() {
		Intent intent = new Intent(this, SplashScreen.class);
		startActivity(intent);
	}
	
}
