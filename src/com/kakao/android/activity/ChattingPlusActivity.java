package com.kakao.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kakao.android.common.KakaoManager;
import com.kakao.android.common.MessageUtil;
import com.kakao.api.Kakao;
import com.kakao.api.KakaoResponseHandler;
import com.kakao.api.Logger;
import com.kakao.android.R;

public class ChattingPlusActivity extends Activity {
	
	private Button buttonFinish;
	
	public static final String KEY_TITLE = "title";
	public static final String KEY_CLIENT_TOKEN = "clientToken";
	public static final String KEY_CHAT_ROOM_ID = "chatRoomId";
	
	// 챗방 타이틀
	private String title = null;
	
	// sendMessage를 보낼 채팅방 ID
	private String chatRoomId = null;
	
	// Invite message에서는 사용하지 않음. Object message에서 사용.
	private String clientToken = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatting_plus);
		
		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		clientToken = intent.getStringExtra("clientToken");
		chatRoomId = intent.getStringExtra("chatRoomId");
		
		if(TextUtils.isEmpty(chatRoomId)) {
			MessageUtil.toast(getApplicationContext(), "chatRoomId is null");
			finish();
		}
		
		Logger.getInstance().d("kakao-android-sdk", "title: " + title + ", clientToken: " + clientToken + ", chatRoomId: " + chatRoomId);
		
		((TextView) findViewById(R.id.chatting_plus_title)).setText("ChatRoom Title: " + title);
		((TextView) findViewById(R.id.chatting_plus_chatroomid)).setText("ChatRoom ID: " + chatRoomId);
	}
	
	public void onClickSendMessage(View v) {
		new AlertDialog.Builder(this)
			.setTitle("[" + title + "]")
			.setMessage("해당 채팅방으로 초대 메시지를 전송하시겠습니까?")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Kakao kakao = KakaoManager.getKakao(getApplicationContext());
					sendMessage(kakao);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
	}
	
	public void onClickFinish(View v) {
		finish();
	}
	
	private void sendMessage(Kakao kakao) {
		
		final ArrayList< Map< String, String > > metaInfo = new ArrayList< Map< String, String > >();

        Map < String, String > metaInfoAndroid = new HashMap<String, String>();
        metaInfoAndroid.put("os", "android");
        metaInfoAndroid.put("executeurl", "");
        metaInfo.add(metaInfoAndroid);
        
        Map < String, String > metaInfoIos = new HashMap<String, String>();
        metaInfoIos.put("os", "ios");
        metaInfoIos.put("executeurl", "");
        metaInfo.add(metaInfoIos);
		
		/**
		 * @param context
		 * @param responseHandler
	     * @param id: chatRoomId
	     * @param isRoom: true
	     * @param msg
	     * @param metaInfo
	     */
		
		kakao.sendMessage(getApplicationContext(), new KakaoResponseHandler(this) {

			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.alert(ChattingPlusActivity.this, "onComplete", "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}

			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.alert(ChattingPlusActivity.this, "onError", "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}

		}, chatRoomId, true, "카카오 example-sdk, 채팅플러스 test 메시지입니다.", metaInfo);
	}
}
