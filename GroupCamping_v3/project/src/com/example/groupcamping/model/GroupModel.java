package com.example.groupcamping.model;

import java.io.Serializable;

public class GroupModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public int uid;
	public String jsonData;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

}
