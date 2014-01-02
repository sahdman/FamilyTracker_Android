package com.shadman.familytracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FamilyTrackerLogin extends Activity {

	EditText txt_user_name;
	EditText txt_user_pass;
	Button bt_login;
	
	UserTable myself;
	
	Handler mHandler;
	
	int mId;
	String mPassword;
	HttpServerThread authThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myself = new UserTable(this);
		if ( myself.isUserSaved() ) {
			String id_string = myself.getValue(UserTable.ROW_FIELD_ID);
			String password = myself.getValue(UserTable.ROW_FIELD_PASS);
			if ( id_string != null && password != null) {
				myself.close();
				goToFamilyMap(Integer.parseInt(id_string), password);
			}
			else
				System.out.print("Id or password not foond");
			myself.close();
		}
			
		
        setContentView(R.layout.login_page);
        
        txt_user_name = (EditText) findViewById(R.id.txt_user_name);
        txt_user_pass = (EditText) findViewById(R.id.txt_user_pass);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bt_login.setClickable(false);
				mId = Integer.parseInt(txt_user_name.getText().toString().trim());
				mPassword = txt_user_pass.getText().toString();
				authThread = new HttpServerThread(HttpServerThread.THREAD_MODE_USER_AUTH);
				authThread.addArguments(mId, mPassword);
				authThread.setHandler(mHandler);
				authThread.start();
			}
		});
        
        mHandler = new Handler(Looper.getMainLooper()) {
        	@Override
        	public void handleMessage(Message inputMsg) {
        		bt_login.setClickable(true);
        		if ( inputMsg.arg1 == 0 ) {
        			System.out.println("Not authenticated");
        		} else if ( inputMsg.arg1 == 1 ) {
        			System.out.println("Authenticated");
        			myself.SaveUser(mId, "user", mPassword);
        			goToFamilyMap(mId, mPassword);
        		}
        	}
        };
	}
	
	private void goToFamilyMap(int id, String password) {
		Intent intent = new Intent(FamilyTrackerLogin.this, FamilyTracker.class);
		Bundle bundle = new Bundle();   
        bundle.putInt( UserTable.ROW_FIELD_ID, id);
        bundle.putString( UserTable.ROW_FIELD_PASS, password);
        intent.putExtras(bundle);
	    startActivity(intent);
	}
}
