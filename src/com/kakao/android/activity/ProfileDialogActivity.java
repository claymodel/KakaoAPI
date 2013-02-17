package com.kakao.android.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.android.common.ImageLoader;
import com.kakao.android.common.KakaoManager;
import com.kakao.android.common.MessageUtil;
import com.kakao.api.Kakao;
import com.kakao.api.KakaoResponseHandler;
import com.kakao.android.R;

public class ProfileDialogActivity extends Activity {

	private ImageView ivProfile;
	private TextView tvNickname;
	private TextView tvUserId;

	private Kakao kakao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.profile_dialog_activity);

		ivProfile = (ImageView) findViewById(R.id.profile_dialog_activity_iv_profile);
		tvNickname = (TextView) findViewById(R.id.profile_dialog_activity_tv_nickname);
		tvUserId = (TextView) findViewById(R.id.profile_dialog_activity_tv_user_id);

		kakao = KakaoManager.getKakao(getApplicationContext());
		
		Bundle extras = getIntent().getExtras();

		if (extras != null && extras.getBoolean("isFriendInfo", false)) {
			String userId = extras.getString("user_id");
			String nickname = extras.getString("nickname");
			String profileImageUrl = extras.getString("profileImageUrl");

			setProfile(userId, nickname, profileImageUrl);
			
			String result = extras.getString("result");
			if (!TextUtils.isEmpty(result))
				MessageUtil.alert(this, result);
		} else {
			localUser();
		}
		
	}

	private void localUser() {
		
		kakao.localUser(new KakaoResponseHandler(getApplicationContext()) {

			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.alert(ProfileDialogActivity.this, result.toString());
			}

			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.alert(ProfileDialogActivity.this, "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}

		});
	}

	private void setProfile(String userId, String nickname, String profileImageUrl) {
		if (getWindow().isActive()) {
			ImageLoader.getInstance(getApplicationContext()).loadThumbnailImage(profileImageUrl, ivProfile);
			tvNickname.setText("" + nickname);
			tvUserId.setText("" + userId);
		}
	}
}
