package com.kakao.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.kakao.android.common.KakaoManager;
import com.kakao.android.common.MessageUtil;
import com.kakao.api.KakaoResponseHandler;
import com.kakao.api.Logger;
import com.kakao.api.story.BasePostStoryActivity;

public class ThirdPartyPostStoryActivity extends BasePostStoryActivity {

	private AlertDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// super.onCreate()를 실행하기 전에, Kakao 인스턴스를 생성해야합니다.
		kakao = KakaoManager.getKakao(getApplicationContext());
		
		super.onCreate(savedInstanceState);
		
		// 화면 블락용 다이얼로그 생성
		ProgressBar progress = new ProgressBar(this.getApplicationContext());
		progress.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		progress.setPadding(10, 10, 10, 10);
		dialog = new AlertDialog.Builder(this).create();
		dialog.setView(progress);
		dialog.setCancelable(false);
	}

	@Override
	public void postStory() {

		String mediaPathWithSize = mediaPath + "?width=" + bitmap.getWidth() + "&height=" + bitmap.getHeight(); 
		ArrayList<Map<String, String>> metaInfo = new ArrayList<Map<String, String>>();

		// excute url은 kakao + client_id + :// 과 같은 형식으로 만들어집니다.
		// 카카오톡에서 이 앱을 실행시키기 위해서 AndroidManifest.xml에 custom scheme을 설정해줍니다.
		// 추가적인 액션에 대해 exe?key1=value1&key2=value2 의 형식으로 excuteurl을 설정해주면됩니다.
		Map<String, String> metaInfoAndroid = new HashMap<String, String>();
		metaInfoAndroid.put("os", "android");
		metaInfoAndroid.put("executeurl", "");
		metaInfo.add(metaInfoAndroid);

		Map<String, String> metaInfoIos = new HashMap<String, String>();
		metaInfoIos.put("os", "ios");
		metaInfoIos.put("executeurl", "");
		metaInfo.add(metaInfoIos);

		// 글 내용
		String content = etContent.getText().toString();
		
		// 글 공개 설정
		boolean permission = cbPermission.isChecked();
		
		kakao.postStory(new KakaoResponseHandler(getApplicationContext()) {
			
			@Override
			public void onStart() {
				super.onStart();
				if (!dialog.isShowing() || dialog.getWindow().isActive()) 
					dialog.show();
			}
			
			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
				if (dialog.isShowing() || dialog.getWindow().isActive()) 
					dialog.dismiss();
				Logger.getInstance().e("httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result.toString());
				
				// 스토리 포스팅이 완료된 후, 액티비티를 종료해야 합니다.
				MessageUtil.toastForComplete(getApplicationContext(), httpStatus, kakaoStatus, result);
				finish();
			}

			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				if (dialog.isShowing() || dialog.getWindow().isActive()) 
					dialog.dismiss();
				MessageUtil.alert(ThirdPartyPostStoryActivity.this, "onError", "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
			}

		}, content, mediaPathWithSize, permission, metaInfo);
	}
	
}
