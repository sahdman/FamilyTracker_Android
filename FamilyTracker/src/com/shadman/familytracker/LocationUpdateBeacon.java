package com.shadman.familytracker;

import javax.net.ssl.HandshakeCompletedListener;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class LocationUpdateBeacon extends Service {
	
	static final int MESSAGE_AUTH_SUCCESS = 0;
	static final int MESSAGE_AUTH_FAIL = 1;
	static final int MESSAGE_LOCATION_RECEIVED = 2;
	static final int MESSAGE_LOCATION_TIMEDOUT = 4;
	
	int mId;
	String mPassword;
	LatLng mGeoLocation;
	boolean isLocationReceived = false;
	
	LocationManager mLocationManager;
	LocationListener mLocationListener;
	Handler mHandler;
	HttpServerThread beaconThread;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Service started");
		StartBeacon(intent);
		return START_NOT_STICKY;
	}
	
	private void StartBeacon(Intent intent) {
		if (intent == null)
			return;
		Bundle bundle = intent.getExtras();
		if (bundle == null)
			return;
		mId = bundle.getInt(UserTable.ROW_FIELD_ID);
		mPassword = bundle.getString(UserTable.ROW_FIELD_PASS);
		System.out.println("ID = " + mId + " and pass = " + mPassword);
		
		mHandler = new BeaconHandler(Looper.getMainLooper()); 

		beaconThread = new HttpServerThread(HttpServerThread.THREAD_MODE_BEACON);
		beaconThread.addArguments(mId, mPassword);
		beaconThread.setHandler(mHandler);
		
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListner(mHandler);
		if ( mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ) {
			System.out.println("Network provider available");
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);			
		} else if ( mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
			System.out.println("GPS provider available");
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		} else {
			System.out.println("No provider enabled");
		}
		
		(new LocationTimeOut(60*1000*2)).start();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	class BeaconHandler extends Handler {
		
		public BeaconHandler(Looper mainLooper) {
			super(mainLooper);
		}
		
		@Override
		public void handleMessage(Message inputMsg) {
			switch (inputMsg.arg1) {
			case MESSAGE_LOCATION_RECEIVED:
				System.out.println("Locatiuon update received");
				isLocationReceived = true;
				mGeoLocation = (LatLng) inputMsg.obj;
				handleLocationUpdate(mGeoLocation);
				break;
			case MESSAGE_AUTH_SUCCESS:
				System.out.println("Success update received");
				handleUpdateSuccess();
				break;
			case MESSAGE_AUTH_FAIL:
				System.out.println("Fail update received");
				handleUpdateFail();
				break;
			case MESSAGE_LOCATION_TIMEDOUT:
				System.out.println("TImed out");
				if ( !isLocationReceived )
					System.exit(0);
			default:
				System.out.println("Invalid update received");
				break;
			}
		}
		
		private void handleLocationUpdate(LatLng location) {
			if ( location != null ) {
				beaconThread.UpdateLocation((float)location.latitude, (float)location.longitude);
			}
		}
		
		private void handleUpdateSuccess() {
			System.exit(0);
		}
		
		private void handleUpdateFail() {
			System.exit(0);
		}
	}
	
	class LocationTimeOut extends Thread {
		long time_out;
		
		public LocationTimeOut(long timeout) {
			time_out = timeout;
		}
		
		public void run() {
			System.out.println("Waiting for timeout");
			try {
				Thread.sleep(time_out);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Timed out");			
			Message msg = new Message();
			msg.arg1 = MESSAGE_LOCATION_TIMEDOUT;
			mHandler.dispatchMessage(msg);
		}
	}

}
