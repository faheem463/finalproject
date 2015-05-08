package com.example.groupcamping.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.groupcamping.R;

public class MainActivity extends BaseActivity implements OnClickListener {

	Button btnAddGroup, btnListGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {

		//
		btnAddGroup = (Button) findViewById(R.id.btn_add_group);
		btnAddGroup.setOnClickListener(this);

		//
		btnListGroup = (Button) findViewById(R.id.btn_list_group);
		btnListGroup.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_add_group:
			gotoListGroupLocal();
			break;
		case R.id.btn_list_group:
			gotoListGroupDropbox();
			break;
		default:
			break;
		}
	}

	private void addNewCampingGroup() {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add new group");
		alert.setMessage("Enter new group name:");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setSingleLine(true);
		input.setHint("Group name");

		alert.setView(input);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String newGroupName = input.getText().toString();
				// Do something with value!
				//CampGroupModel newCampGroupModel = new CampGroupModel();
				//newCampGroupModel.setCampGroupName(newGroupName);
				//mCampGroupTableHelper.insert(newCampGroupModel);
				//loadGroupListView();
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	private void gotoListGroupLocal() {
		Intent intent = new Intent(mContext, ListGroupActivity.class);
		startActivity(intent);
	}
	

	private void gotoListGroupDropbox() {
		Intent intent = new Intent(mContext, ListGroupActivity.class);
		intent.putExtra("GROUP_TYPE", true);
		startActivity(intent);
	}
}
