package com.example.groupcamping.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private int uid;
	private String itemName;
	private int parentId;
	private ArrayList<ItemModel> childItems;
	private boolean isSelected;
	// private ArrayList<ItemModel> selectedItems;

	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public ArrayList<ItemModel> getChildItems() {
		return childItems;
	}
	public void setChildItems(ArrayList<ItemModel> childItems) {
		this.childItems = childItems;
	}
	// public ArrayList<ItemModel> getSelectedItems() {
	// return selectedItems;
	// }
	// public void setSelectedItems(ArrayList<ItemModel> selectedItems) {
	// this.selectedItems = selectedItems;
	// }
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getSelectedItems() {

		int count = 0;
		for (ItemModel item : childItems) {
			if (item.isSelected)
				count++;
		}
		return count;
	}
}
