package com.kakao.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kakao.android.domain.MainMenuItem;
import com.kakao.android.R;

public class MainMenuGridAdapter extends ArrayAdapter<MainMenuItem> {

	public MainMenuGridAdapter(Context context, ArrayList<MainMenuItem> objects) {
		super(context, 0, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Holder holder;
		
		if (convertView == null) {
			
			convertView = View.inflate(getContext(), R.layout.main_menu, null);
			
			holder = new Holder();
			holder.tvName = (TextView) convertView.findViewById(R.id.main_menu_tv_name);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		MainMenuItem menu = getItem(position);
		
		holder.tvName.setText(Html.fromHtml("" + menu.getName()));
		
		return convertView;
	}

	private class Holder {
		public TextView tvName;
	}
}
