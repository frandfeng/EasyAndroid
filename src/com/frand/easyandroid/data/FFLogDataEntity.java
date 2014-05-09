package com.frand.easyandroid.data;

import com.frand.easyandroid.db.annotation.FFTransient;

public class FFLogDataEntity extends FFBaseDataEntity {

	@FFTransient
	private static final long serialVersionUID = 6402489266524974747L;

	private String time = "";
	private String application = "";
	private int versionCode = 0;
	private String tag = "";
	private String text = "";
	private String level = "";
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String toString() {
		return "time=" + time + ",application=" + application + ",versionCode=" + versionCode
				+ ",tag=" + tag + ",text=" + text + ",level=" + level;
	}
}
