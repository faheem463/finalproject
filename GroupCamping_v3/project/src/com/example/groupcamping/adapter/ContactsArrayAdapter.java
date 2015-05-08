package com.example.groupcamping.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.example.groupcamping.R;

public class ContactsArrayAdapter extends ArrayAdapter<String> {

	private List<String> mObjects;
	private List<String> mPhones;
	private Context mContext;
	private List<Boolean> mSelects;

	public ContactsArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
		super(context, R.layout.item_list_child_items, objects);

		mObjects = objects;
		mContext = context;
		mSelects = new ArrayList<Boolean>();
		for (String string : objects) {
			mSelects.add(false);
		}
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

		viewHolder.tvItem.setText(mObjects.get(position));
		viewHolder.tvItem.setChecked(mSelects.get(position));

		return v;
	}

	class ViewHolder {

		CheckedTextView tvItem;
	}

	public void setChecked(int index) {

		mSelects.set(index, !mSelects.get(index));
		notifyDataSetChanged();
	}

	public ArrayList<String> getSelectPhones() {

		ArrayList<String> rs = new ArrayList<String>();
		for (int i = 0; i < mSelects.size(); i++) {
			if (mSelects.get(i)) {
				rs.add(mPhones.get(i));
			}
		}
		return rs;
	}

	public List<String> getmPhones() {
		return mPhones;
	}

	public void setmPhones(List<String> mPhones) {
		this.mPhones = mPhones;
	}

}
