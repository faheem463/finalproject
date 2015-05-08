package com.example.groupcamping.model.sync;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupSyncModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public String name;
	public ArrayList<ParentItemSyncModel> listPackages;

}
