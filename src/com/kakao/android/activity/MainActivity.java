package com.kakao.android.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.kakao.android.adapter.MainMenuGridAdapter;
import com.kakao.android.common.KakaoManager;
import com.kakao.android.common.MessageUtil;
import com.kakao.android.domain.MainMenuItem;
import com.kakao.api.Kakao;
import com.kakao.api.KakaoResponseHandler;
import com.kakao.android.R;

public class MainActivity extends Activity implements OnItemClickListener {

	private static final String MENU_ME = "내정보";
	private static final String MENU_FRIENDS = "친구들";
	private static final String MENU_WRITE = "스토리 포스팅";
	private static final String MENU_LOGOUT = "로그아웃";
	private static final String MENU_UNREGISTER = "언레지스터";
	
	private ArrayList<MainMenuItem> menuList;
	private MainMenuGridAdapter adapter;

	private Kakao kakao;

	private static int[] imageList = {
		R.drawable.img_iu_00,
		R.drawable.img_iu_01,
		R.drawable.img_iu_02,
		R.drawable.img_iu_03,
		R.drawable.img_iu_04,
		R.drawable.img_iu_05,
		R.drawable.img_iu_06,
		R.drawable.img_iu_07,
		R.drawable.img_iu_08,
		R.drawable.img_iu_09
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		kakao = KakaoManager.getKakao(getApplicationContext());
		
		View vTitle = findViewById(R.id.main_activity_v_title);
		TextView tvTitle = (TextView) vTitle.findViewById(R.id.activity_title_tv_title);
		tvTitle.setText("SDK Example Home");
		
		menuList = new ArrayList<MainMenuItem>();
		GridView gvMenu = (GridView) findViewById(R.id.main_activity_gv_menu);
		gvMenu.setNumColumns(3);
		adapter = new MainMenuGridAdapter(getApplicationContext(), menuList);
		gvMenu.setAdapter(adapter);
		gvMenu.setOnItemClickListener(this);

		initializeMenu();
	}

	private void initializeMenu() {
		menuList.add(new MainMenuItem(MENU_ME));
		menuList.add(new MainMenuItem(MENU_FRIENDS, FriendListActivity.class));
		menuList.add(new MainMenuItem(MENU_WRITE));
		menuList.add(new MainMenuItem(MENU_LOGOUT));
		menuList.add(new MainMenuItem(MENU_UNREGISTER));

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MainMenuItem menu = (MainMenuItem) parent.getAdapter().getItem(position);

		if (!(menu instanceof MainMenuItem)) 
			return;
		
		if (menu.getName().equals(MENU_ME)) {
			kakao.localUser(new KakaoResponseHandler(getApplicationContext()) {

				@Override
				public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
					MessageUtil.alert(MainActivity.this, result.toString());
				}

				@Override
				public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
					MessageUtil.alert(MainActivity.this, "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
				}

			});
			
		} else if (menu.getName().equals(MENU_FRIENDS)) {
			
			startActivity(new Intent(this, menu.getTargetActivity()));
			
		} else if (menu.getName().equals(MENU_WRITE)) {
			
			// 스토리에 포스팅할 Bitmap 생성 예제 코드. 실제 릴리즈에는 3rd party에서 생성한 Bitmap 사용.
			int randomNumber = Math.abs((int)System.currentTimeMillis()) % 10;
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageList[randomNumber]);
			
			// 스토리 포스팅 액티비티 실행 
			kakao.startPostStoryActivity(new KakaoResponseHandler(getApplicationContext()) {
				
				@Override
				public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
					MessageUtil.toastForError(getApplicationContext(), httpStatus, kakaoStatus, result);
				}
				
				@Override
				public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
					// nothing...
				}
			}, MainActivity.this, ThirdPartyPostStoryActivity.class, bitmap);
				
		} else if (menu.getName().equals(MENU_LOGOUT)) {
			
			// 로그아웃
			kakao.logout(new KakaoResponseHandler(this) {

				@Override
				public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
					// TODO
					startActivity(new Intent(MainActivity.this, LoginActivity.class));
					finish();
				}

				@Override
				public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
					MessageUtil.alert(MainActivity.this, "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
				}
				
			});
			
		} else if (menu.getName().equals(MENU_UNREGISTER)) {
			
			// 언레지스터
			kakao.unregister(new KakaoResponseHandler(this) {
				
				@Override
				public void onComplete(int httpStatus, int kakaoStatus, JSONObject result) {
					// TODO
					startActivity(new Intent(MainActivity.this, LoginActivity.class));
					finish();
				}
				
				@Override
				public void onError(int httpStatus, int kakaoStatus, JSONObject result) {
					MessageUtil.alert(MainActivity.this, "httpStatus: " + httpStatus + ", kakaoStatus: " + kakaoStatus + ", result: " + result);
				}
				
			});
		} 
		
	}

	@Override
	public void onBackPressed() {
		// 종료 알럿
		new AlertDialog.Builder(this)
			.setTitle(R.string.app_name)
			.setMessage("종료하시겠습니까?")
			.setNegativeButton(android.R.string.cancel, null)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.create().show();
	}
}
