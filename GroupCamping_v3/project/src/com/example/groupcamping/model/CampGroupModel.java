package com.example.groupcamping.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.example.groupcamping.model.sync.GroupSyncModel;

public class CampGroupModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private int uid;
	private String campGroupName;
	private String startTime;
	private String endTime;
	private boolean isCreateMySelf;
	private String dropboxSharePath;
	private ArrayList<ItemModel> listItems;

	public CampGroupModel(GroupSyncModel groupSyncModel) {
		campGroupName = groupSyncModel.name;
	}

	public CampGroupModel() {
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getCampGroupName() {
		return campGroupName;
	}

	public void setCampGroupName(String campGroupName) {
		this.campGroupName = campGroupName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public ArrayList<ItemModel> getListItems() {
		return listItems;
	}

	public void setListItems(ArrayList<ItemModel> listItems) {
		this.listItems = listItems;
	}

	public boolean isCreateMySelf() {
		return isCreateMySelf;
	}

	public void setCreateMySelf(boolean isCreateMySelf) {
		this.isCreateMySelf = isCreateMySelf;
	}

	public String getDropboxSharePath() {
		return dropboxSharePath;
	}

	public void setDropboxSharePath(String dropboxSharePath) {
		this.dropboxSharePath = dropboxSharePath;
	}

}
