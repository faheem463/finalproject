package com.example.groupcamping.adapter;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.example.groupcamping.R;
import com.example.groupcamping.database.ItemGroupTableHelper;
import com.example.groupcamping.model.CampGroupModel;
import com.example.groupcamping.model.ItemGroupModel;
import com.example.groupcamping.model.ItemModel;
import com.google.gson.Gson;

public class ChildItemArrayAdapter extends ArrayAdapter<ItemModel> {

	private List<ItemModel> mObjects;
	private Context mContext;
	private ItemGroupTableHelper mItemGroupTableHelper;
	private CampGroupModel mCampGroupModel;

	public ChildItemArrayAdapter(@NonNull Context context, int resource, @NonNull List<ItemModel> objects) {
		super(context, R.layout.item_list_child_items, objects);

		mObjects = objects;
		mContext = context;
		mItemGroupTableHelper = new ItemGroupTableHelper(mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = convertView;
		final ViewHolder viewHolder;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_list_child_items, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.tvItem = (CheckedTextView) v.findViewById(R.id.tv_item);

			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) v.getTag();
		}

		ItemModel itemModel = mObjects.get(position);
		viewHolder.tvItem.setText(itemModel.getItemName());
		viewHolder.tvItem.setChecked(itemModel.isSelected());

		return v;
	}

	public CampGroupModel getmCampGroupModel() {
		return mCampGroupModel;
	}

	public void setmCampGroupModel(CampGroupModel mCampGroupModel) {
		this.mCampGroupModel = mCampGroupModel;
	}

	class ViewHolder {

		CheckedTextView tvItem;
	}

	public void setChecked(int index, boolean isChecked) {

		ItemModel item = mObjects.get(index);

		item.setSelected(isChecked);

		ItemGroupModel itemGroupModel = new ItemGroupModel();
		itemGroupModel.setItemId(item.getUid());
		itemGroupModel.setGroupId(mCampGroupModel.getUid());
		
		if (isChecked) {
			mItemGroupTableHelper.insert(itemGroupModel);
		} else {
			mItemGroupTableHelper.delete(mCampGroupModel.getUid(), item.getUid());
		}
		notifyDataSetChanged();
	}

}
