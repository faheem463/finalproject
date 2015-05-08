package com.example.groupcamping.adapter;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.groupcamping.R;
import com.example.groupcamping.model.ItemModel;

public class ParentItemArrayAdapter extends ArrayAdapter<ItemModel> {

	private List<ItemModel> mObjects;
	private Context mContext;

	public ParentItemArrayAdapter(@NonNull Context context, int resource, @NonNull List<ItemModel> objects) {
		super(context, R.layout.item_list_parent_items, objects);

		mObjects = objects;
		mContext = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View v = convertView;
		final ViewHolder viewHolder;

		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_list_parent_items, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) v.findViewById(R.id.tv_item_name);
			viewHolder.tvNumber = (TextView) v.findViewById(R.id.tv_item_number);

			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) v.getTag();
		}

		ItemModel itemModel = mObjects.get(position);
		viewHolder.tvName.setText(itemModel.getItemName());
		int childSize = itemModel.getChildItems().size();

		viewHolder.tvNumber.setText(itemModel.getSelectedItems() + " / " + childSize);

		return v;
	}

	class ViewHolder {

		TextView tvName;
		TextView tvNumber;
	}

}
