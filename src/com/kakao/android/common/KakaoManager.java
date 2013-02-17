package com.kakao.android.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.kakao.api.Kakao;
import com.kakao.api.Kakao.KakaoTokenListener;
import com.kakao.api.Kakao.LogLevel;

public class KakaoManager {
	private static Kakao kakao;
	
	public static Kakao getKakao(Context context) {
		if (kakao != null) {
			return kakao;
		}
		
		// TEST
		kakao = new Kakao(context, C.CLIENT_ID, C.CLIENT_SECRET, C.CLIENT_REDIRECT_URI);
		
		// 배포시, Release로 변경.
		kakao.setLogLevel(LogLevel.Debug);
		
		final SharedPreferences pref = context.getSharedPreferences(C.PREF_KEY, Context.MODE_PRIVATE);

        KakaoTokenListener tokenListener = new KakaoTokenListener() {
            public void onSetTokens(String accessToken, String refreshToken) {
                // 실제 적용시에는 보안이 보장되는 곳에 저장해야 합니다. 
                if(TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(refreshToken)) { // accessToken이 null이거나 빈스트링("")이면 로그아웃
                	// 저장해놓은 토큰을 지운다.
                    pref.edit().remove("access_token").remove("refresh_token").commit();

                    // TODO kakaoStatus가 STATUS_INVALID_GRANT 이거나, KakaoTokenListener에 access token이 null 또는 빈문자열로 오는경우 logout | 어플 재시작 | 종료
                } else {
                    pref.edit().putString("access_token", accessToken).putString("refresh_token", refreshToken).commit();
                }
            }
        };
        kakao.setTokenListener(tokenListener);

        String accessToken = pref.getString("access_token", null);
        String refreshToken = pref.getString("refresh_token", null);
        kakao.setTokens(accessToken, refreshToken);
		
		return kakao;
	}
	

}
