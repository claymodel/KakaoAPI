package com.kakao.android.adapter;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.android.common.ImageLoader;
import com.kakao.android.R;

public class FriendListAdapter extends ArrayAdapter<JSONObject> {

	public FriendListAdapter(Context context, List<JSONObject> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder h;
		
		if (convertView == null) {
			convertView = View.inflate(getContext(), R.layout.friend_item, null);
			
			h = new Holder();
			h.ivProfile = (ImageView) convertView.findViewById(R.id.friend_item_iv_profile);
			h.tvNickname = (TextView) convertView.findViewById(R.id.friend_item_tv_nickname);
			
			convertView.setTag(h);
		} else {
			h = (Holder) convertView.getTag();
		}
		
		JSONObject friend = getItem(position);
		
		h.tvNickname.setText(friend.optString("nickname"));
		ImageLoader.getInstance(getContext().getApplicationContext()).loadThumbnailImage(friend.optString("profile_image_url"), h.ivProfile);
		
		return convertView;
	}

	private class Holder {
		ImageView ivProfile;
		TextView tvNickname;
	}
}
