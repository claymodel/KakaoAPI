package com.kakao.android.domain;

public class MainMenuItem {
	private String name;
	private Class<?> targetActivity;
	private String targetUrl;

	public MainMenuItem(String name) {
		super();
		this.name = name;
	}
	
	public MainMenuItem(String name, Class<?> activity) {
		super();
		this.name = name;
		this.targetActivity = activity;
	}

	public MainMenuItem(String name, String targetUrl) {
		super();
		this.name = name;
		this.targetUrl = targetUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getTargetActivity() {
		return targetActivity;
	}

	public void setTargetActivity(Class<?> activity) {
		this.targetActivity = activity;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

}
