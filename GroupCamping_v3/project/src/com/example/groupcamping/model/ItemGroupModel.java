package com.example.groupcamping.model;

import java.io.Serializable;

public class ItemGroupModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private int uid;
	private int groupId;
	private int itemId;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

}
