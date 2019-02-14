package com.waracle.androidtest;

import android.graphics.Bitmap;

import org.json.JSONObject;

public class CakeModel {
	private String title;
	private String desc;
	private String imageUrl;
	private Bitmap image;
	
	public CakeModel(JSONObject json) {
		setTitle("No title");
		setDesc("");
		setImageUrl("");
		setImage(null);
		if (json != null) {
			setTitle(json.optString("title", "No title"));
			setDesc(json.optString("desc"));
			setImageUrl(json.optString("image"));
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public void setImage(Bitmap image) {
		this.image = image;
	}
}
