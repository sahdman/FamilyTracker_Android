package com.shadman.familytracker;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MyLocationListner implements LocationListener {

	Handler mHandler;
	
	public MyLocationListner(Handler handler) {
		mHandler = handler;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		System.out.println("Location received");
		Message msg = new Message();
		LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
		msg.arg1 = LocationUpdateBeacon.MESSAGE_LOCATION_RECEIVED;
		msg.obj = latlng;
		mHandler.dispatchMessage(msg);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
