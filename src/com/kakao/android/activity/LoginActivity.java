package com.kakao.android.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.kakao.android.common.KakaoManager;
import com.kakao.android.common.MessageUtil;
import com.kakao.api.Kakao;
import com.kakao.api.KakaoResponseHandler;
import com.kakao.api.Logger;
import com.kakao.android.R;

public class LoginActivity extends Activity implements OnClickListener {

	private KakaoResponseHandler loginResponseHandler;

	private Kakao kakao;
	Button btnLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		kakao = KakaoManager.getKakao(getApplicationContext());
		
		btnLogin = (Button)findViewById(R.id.login_activity_btn_login);
		btnLogin.setOnClickListener(this);
		
		loginResponseHandler = new KakaoResponseHandler(getApplicationContext()) {

			@Override
			public void onStart() {
				super.onStart();
				btnLogin.setVisibility(View.GONE);
			}
			
			@Override
			public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
				moveToMainActivity();
			}

			@Override
			public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
				MessageUtil.toastForError(getApplicationContext(), httpStatus, kakaoStatus, result);
				btnLogin.setVisibility(View.VISIBLE);
			}

		};
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// Kakao.hasTokens() 은 access token의 유효성을 체크하지는 않습니다. 
		// 3rd party app으로 부터 SDK에 세팅된 Access token 및 Refresh token 이 있는가를 확인합니다.
		// 실제 토큰 체크는 API를 call할때 이루어집니다.
		if (kakao.hasTokens()) {
			// 세팅된 토큰이 있으면 localUser를 호출.
			localUser();
		} else {
			// 세팅된 토큰이 없으면, authorize를 진행.
			// 카카오톡에서 [동의하고 시작하기]를 통해, onActivityResult()가 호출되었음에도,
			// access token 및 refresh token을 획득하지 못한 경우에 대비한 확인 및 재 인증 메소드 입니다.
			// 반드시 실행할 필요는 없습니다.
			kakao.authorize(loginResponseHandler);
		}
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
					return;
				}
				
				String message = "내 정보를 불러오지 못했습니다.<br/>다시 시도하시겠습니까?";
				
				new AlertDialog.Builder(LoginActivity.this)
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
				Logger.getInstance().d("localUser():onComplete() - httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
				moveToMainActivity();
			}
		};
		
		kakao.localUser(localUserHandler);
	}
	
	private void moveToMainActivity() {
		startActivity(new Intent(LoginActivity.this, MainActivity.class));
		finish();
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.login_activity_btn_login) 
			kakao.login(this, loginResponseHandler);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		kakao.onActivityResult(requestCode, resultCode, data, this, loginResponseHandler);
	}

}
