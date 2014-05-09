package com.frand.easyandroid.data;

import com.frand.easyandroid.db.annotation.FFTransient;

public class FFLogDataEntity extends FFBaseDataEntity {

	@FFTransient
	private static final long serialVersionUID = 6402489266524974747L;

	private String level = "";
	private String time = "";
	private String application = "";
	private String tag = "";
	private String message = "";
	private String appVer = "";
	private String brand = "";
	private String model = "";
	private String systemVer = "";
	private String os = "android";

	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
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
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAppVer() {
		return appVer;
	}
	public void setAppVer(String appVer) {
		this.appVer = appVer;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getSystemVer() {
		return systemVer;
	}
	public void setSystemVer(String systemVer) {
		this.systemVer = systemVer;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String toString() {
		return "level=" + level + ",time=" + time + ",application=" + application
				+ ",tag=" + tag + ",message=" + message + ",appVer=" + appVer
				+ ",brand=" + brand + ",model=" + model + ",systemVer=" + systemVer + ",os=" + os;
	}
}
