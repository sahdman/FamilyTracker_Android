package com.shadman.familytracker;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class FamilyTracker extends FragmentActivity {

	HttpServerThread pollThread;
	HttpServerThread beaconThread;
	GoogleMap map;
	ArrayList<FamilyMember> Family = new ArrayList<FamilyMember>();
	Handler mHandler;
	
	int user_id;
	String user_pass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        
        getIntentArguments(this.getIntent().getExtras());
        startBeaconService();
        
//        InitializeMap();
//        
//        mHandler = new Handler(Looper.getMainLooper()) {
//        	@Override
//        	public void handleMessage(Message inputMsg) {
//        		FamilyMember member = (FamilyMember) inputMsg.obj;
//        		System.out.println("Message id = " + member.getId());
//        		member.updateMarker();
//        	}
//        };
//        
//        pollThread = new HttpServerThread(HttpServerThread.THREAD_MODE_POLLING);
//        pollThread.addArguments(user_id, user_pass, Family);
//        pollThread.setMap(map);
//        pollThread.setHandler(mHandler);
//
//        beaconThread = new HttpServerThread(HttpServerThread.THREAD_MODE_BEACON);
//        beaconThread.addArguments(user_id, user_pass, null);
//        
//        Button btn1 = (Button) findViewById(R.id.button1);
//        btn1.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				System.out.println("Received click event");
//				pollThread.start();
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.family_tracker, menu);
		return true;
	}

	private void InitializeMap() {
        // Get a handle to the Map Fragment
        map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        map.setMyLocationEnabled(true);
	}
	
	private void getIntentArguments(Bundle savedInstanceState) {
		if ( savedInstanceState == null )
			return;
		user_id = savedInstanceState.getInt(UserTable.ROW_FIELD_ID);
		user_pass = savedInstanceState.getString(UserTable.ROW_FIELD_PASS);
	}
	
	private void startBeaconService() {
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);

        Intent serviceIntent = new Intent(getBaseContext(), LocationUpdateBeacon.class);
        Bundle serviceBundle = new Bundle();
        serviceBundle.putInt(UserTable.ROW_FIELD_ID,user_id);
        serviceBundle.putString(UserTable.ROW_FIELD_PASS, user_pass);
        serviceIntent.putExtras(serviceBundle);
        startService(serviceIntent);
//        PendingIntent pintent = PendingIntent.getService(this, 0, serviceIntent, 0);
//        
//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        //for 30 mint 60*60*1000
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
//        		60*1000, pintent);
	}
}
