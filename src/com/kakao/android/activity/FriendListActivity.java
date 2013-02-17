package com.kakao.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kakao.android.adapter.FriendListAdapter;
import com.kakao.android.common.KakaoManager;
import com.kakao.android.common.MessageUtil;
import com.kakao.android.common.SearchUtil;
import com.kakao.api.Kakao;
import com.kakao.api.KakaoResponseHandler;
import com.kakao.api.Logger;
import com.kakao.android.R;

public class FriendListActivity extends ListActivity implements OnClickListener, TextWatcher {

	private static final String TAG = "FriendListActivity";
	
	private Kakao kakao;
	private ArrayList<JSONObject> friends;
	private ArrayList<JSONObject> appFriends;
	private ArrayList<JSONObject> friendsForSearch;
	
	private ArrayList<Map<String, String>> metaInfo;
	private FriendListAdapter friendListAdapeter;
	
	private TextView tvFriendType;
	private EditText etSearch;
	
	static enum FriendInfoType {
		AppFriends, Friends
	}
	
	private FriendInfoType friendType = FriendInfoType.AppFriends;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_activity);

		kakao = KakaoManager.getKakao(getApplicationContext());

		if (!kakao.hasTokens()) {
			finish();
			return;
		}
		
		View vTitle = findViewById(R.id.friend_list_activity_v_title);
		TextView tvTitle = (TextView) vTitle.findViewById(R.id.activity_title_tv_title);
		tvTitle.setText("SDK Example Friend List");
		tvTitle.setOnClickListener(this);
		Button btnSearch = (Button) vTitle.findViewById(R.id.activity_title_btn_do);
		btnSearch.setOnClickListener(this);
		btnSearch.setBackgroundResource(android.R.drawable.ic_menu_search);
		btnSearch.setVisibility(View.VISIBLE);
		etSearch = (EditText) findViewById(R.id.friend_list_activity_et_search);
		etSearch.setVisibility(View.GONE);
		etSearch.addTextChangedListener(this);
		
		findViewById(R.id.friend_list_activity_btn_app_friends).setOnClickListener(this);
		findViewById(R.id.friend_list_activity_btn_friends).setOnClickListener(this);
		
		tvFriendType = (TextView) findViewById(R.id.friend_list_activity_tv_friend_type);
		
		friendsForSearch = new ArrayList<JSONObject>();
		appFriends = new ArrayList<JSONObject>();
		friends = new ArrayList<JSONObject>();
		friendListAdapeter = new FriendListAdapter(getApplicationContext(), friendsForSearch);
		setListAdapter(friendListAdapeter);
		getFriends(); 
		
		metaInfo = new ArrayList<Map<String,String>>();
		
		// excute url은 kakao + client_id + :// 과 같은 형식으로 만들어집니다.
		// 카카오톡에서 이 앱을 실행시키기 위해서 AndroidManifest.xml에 custom scheme을 설정해줍니다.
		Map<String, String> metaInfoAndroid = new HashMap<String, String>();
		metaInfoAndroid.put("os", "android");
		metaInfoAndroid.put("executeurl", "");
		metaInfo.add(metaInfoAndroid);

		Map<String, String> metaInfoIos = new HashMap<String, String>();
		metaInfoIos.put("os", "ios");
		metaInfoIos.put("executeurl", "");
		metaInfo.add(metaInfoIos);
	}
	
	private void getFriends() {
		kakao.friends(new KakaoResponseHandler(this) {

			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {

				if (!result.has("app_friends_info") && !result.has("friends_info")) {
					Logger.getInstance().e(TAG, "friends info is null.");
					return;
				}
				
				// 카카오톡 친구 중 해당 앱을 사용하는 친구 목록
				JSONArray appFriendArray = result.optJSONArray("app_friends_info");
				JSONArray friendArray = result.optJSONArray("friends_info");

				if (appFriendArray.length() == 0 && friendArray.length() == 0) 
					MessageUtil.alert(FriendListActivity.this, "친구가 없습니다!!");
				
				appFriends.clear();
				for (int i = 0, n = appFriendArray.length(); i < n; i++) {
					JSONObject friend = appFriendArray.optJSONObject(i);
					if (friend != null) {
						appFriends.add(friend);
					}
				}

				friends.clear();
				for (int i = 0, n = friendArray.length(); i < n; i++) {
					JSONObject friend = friendArray.optJSONObject(i);
					if (friend != null) {
						friends.add(friend);
					}
				}

				friendType = FriendInfoType.AppFriends;
				changeList();
				
				MessageUtil.alert(FriendListActivity.this, "onComplete", "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}

			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.alert(FriendListActivity.this, "onError", "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.activity_title_btn_do) {
			if (etSearch.getVisibility() == View.VISIBLE)
				etSearch.setVisibility(View.GONE);
			else  
				etSearch.setVisibility(View.VISIBLE);
		} else if (id == R.id.activity_title_tv_title) {
			getListView().setSelection(0);
		} else if (id == R.id.friend_list_activity_btn_app_friends) {
			friendType = FriendInfoType.AppFriends;
			changeList();
		} else if (id == R.id.friend_list_activity_btn_friends) {
			friendType = FriendInfoType.Friends;
			changeList();
		}
	}
	
	private void changeList() {
		friendsForSearch.clear();
		friendListAdapeter.notifyDataSetChanged();
		
		switch (friendType) {
		case AppFriends:
			for (JSONObject friend : appFriends) {
				friendsForSearch.add(friend);
			}
			tvFriendType.setText("카카오톡 친구 중 이 앱을 사용하는 친구");
			break;
			
		case Friends:
			for (JSONObject friend : friends) {
				friendsForSearch.add(friend);
			}
			tvFriendType.setText("카카오톡 친구 중 이 앱을 사용하지 않는 친구");
			break;
		}
		
		if (friendsForSearch != null && friendsForSearch.size() > 0) 
			getListView().setSelection(0);
		friendListAdapeter.notifyDataSetChanged();
	}

	@Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String inputName = s.toString();
		search(inputName);
	}
	
	/**
	 * @param inputName
	 */
	private void search(String inputName) {
		friendsForSearch.clear();
		
		switch (friendType) {
		case AppFriends:
			for (JSONObject friend : appFriends) {
				String friendName = friend.optString("nickname");
				if (SearchUtil.matchString(friendName, inputName)) {
					friendsForSearch.add(friend);
				}
			}
			break;
		case Friends:
			for (JSONObject friend : friends) {
				String friendName = friend.optString("nickname");
				if (SearchUtil.matchString(friendName, inputName)) {
					friendsForSearch.add(friend);
				}
			}
			break;
		}
		
		friendListAdapeter.notifyDataSetChanged();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final JSONObject friend = (JSONObject) l.getItemAtPosition(position);
		if (!friend.has("user_id"))
			return;

		String[] items = { "친구 정보 보기", "메세지 보내기" };
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("User: " + friend.optString("nickname"));
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String userId = friend.optString("user_id");

				switch (which) {
				// 친구 정보 보기
				case 0:
					String profileImageUrl = friend.optString("profile_image_url");
					String nickname = friend.optString("nickname");
					
					Bundle extras = new Bundle();
					extras.putString("userId", userId);
					extras.putString("nickname", nickname);
					extras.putString("profileImageUrl", profileImageUrl);
					extras.putBoolean("isFriendInfo", true);
					extras.putString("result", friend.toString());
					
					startActivity(new Intent(FriendListActivity.this,
							ProfileDialogActivity.class).putExtras(extras));
					break;

				// 메세지 보내기
				case 1:
					alertSendMessage(userId);
					break;
				}
			}
		});
		builder.create().show();

	}

	/**
	 * 메시지 전송 여부를 물어보는 알럿 다이얼로그
	 * 
	 * @param userId
	 */
	private void alertSendMessage(final String userId) {

		final String message = "카카오 SDK, sendMessage 테스트입니다.";
		String dialogMessage = "아래와 같이 메시지를 전송할까요?" + "<br/><br/>" + message;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(Html.fromHtml(dialogMessage));
		builder.setPositiveButton(android.R.string.ok,
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendMessage(userId, message);
				}
			});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.create().show();

	}

	/**
	 * sendMessage
	 * 
	 * @param id
	 * @param message
	 */
	private void sendMessage(String id, String message) {

		if (metaInfo == null) {
			metaInfo = new ArrayList<Map<String, String>>();
			
			// excute url은 kakao + client_id + :// 과 같은 형식으로 만들어집니다.
			// 카카오톡에서 이 앱을 실행시키기 위해서 AndroidManifest.xml에 custom scheme을 설정해줍니다.
			Map<String, String> metaInfoAndroid = new HashMap<String, String>();
			metaInfoAndroid.put("os", "android");
			metaInfoAndroid.put("executeurl", "");
			metaInfo.add(metaInfoAndroid);

			Map<String, String> metaInfoIos = new HashMap<String, String>();
			metaInfoIos.put("os", "ios");
			metaInfoIos.put("executeurl", "");
			metaInfo.add(metaInfoIos);
		}
		
		kakao.sendMessage(getApplicationContext(), new KakaoResponseHandler(this) {

			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.alert(FriendListActivity.this, "onComplete", "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}

			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.alert(FriendListActivity.this, "onError", "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}

		}, id, false, message, metaInfo);
	}

}
