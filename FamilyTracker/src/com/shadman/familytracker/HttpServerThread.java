package com.shadman.familytracker;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.GoogleMap;


public class HttpServerThread extends Thread{
	
	static final int THREAD_MODE_POLLING = 1;
	static final int THREAD_MODE_BEACON = 2;
	static final int THREAD_MODE_USER_AUTH = 3;
	
	int mServerMode;
	int mId;
	String mPassword;
	float mLat;
	float mLong;
	boolean mIsArgsSet;
	boolean mStopThread;
	ArrayList<FamilyMember> mFamilyMembers;
	GoogleMap mMop;
	Handler mHandler;
	
	public HttpServerThread(int mode) {
		mServerMode = mode;
		mIsArgsSet = false;
		mStopThread = false;
	}
	
	@Override
	public void run() {
		if ( ! mIsArgsSet )
			return;
		
		if ( mServerMode == THREAD_MODE_POLLING )
			updateFamilyMembers();
		else if ( mServerMode == THREAD_MODE_BEACON )
			updateLocationBeacon();
		else if ( mServerMode == THREAD_MODE_USER_AUTH )
			authenticateUser();
	}
	
	public void addArguments(int id, String password, ArrayList<FamilyMember> familyMembers) {
		mId = id;
		mPassword = password;
		mIsArgsSet = true;
		mFamilyMembers = familyMembers;
	}
	
	public void addArguments(int id, String password ) {
		mId = id;
		mPassword = password;
		mIsArgsSet = true;
	}
	
	public void setMap(GoogleMap map) {
		mMop = map;
	}
	
	private void updateFamilyMembers() {
		String serverResponse;
		System.out.println("Starting update family members thread");
		
		while ( !mStopThread ) {
			try {
				serverResponse = HttpMethod.getFamilyMembers(mId, mPassword);
			} catch (ErrorCode e) {
				if ( e.equals(ErrorCode.ERROR_SERVER) ) {
					System.out.println("Server error occured. Trying again");
					continue;
				} else {
					break;
				}
			}
			
			/** 
			 * User authenticated on server
			 * Parse response and populate family 
			 */
			parseResponse(serverResponse);
			
			/**
			 * Family members are updated. Update map
			 */
			updateMapMarkers();
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateLocationBeacon() {
		boolean success = false;
		Message msg = new Message();
		System.out.println("Starting update beacon");
		try {
			HttpMethod.sendUpdateLocation(mId, mPassword, mLat, mLong);
			success = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			success = false;
		}
		if ( success ) 
			msg.arg1 = LocationUpdateBeacon.MESSAGE_AUTH_SUCCESS;
		else
			msg.arg1 = LocationUpdateBeacon.MESSAGE_AUTH_FAIL;
		mHandler.dispatchMessage(msg);
	}

	public void StopThread() {
		mStopThread = true;
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void parseResponse(String response) {
		String members[];
		 
		if ( response == null )
			 return;
		
		members = response.split(";");
		for ( int i = 0; i < members.length; i++) {
			if ( members[i].trim().equals("END_LIST") )
				break;
			FamilyMember newMember = parseFamilyMember(members[i].trim());
			if ( newMember != null )
				updateFamily(newMember);
		}
	}
	
	private FamilyMember parseFamilyMember(String response) {
		FamilyMember lMember = new FamilyMember();
		String lProp[];
		int lId;
		float lLat, lLon;
		String lName;
		
		if ( response == null || response.equals("") )
			return null;
		lProp = response.split(":");
		
		try {
			lId = Integer.parseInt(lProp[0].trim());
			lName = lProp[1].trim();
			lLat = Float.parseFloat(lProp[2].trim());
			lLon = Float.parseFloat(lProp[3].trim());
		} catch (Exception e) {
			return null;
		}

		lMember.setInformation(lId, lName);
		lMember.setLocation(lLat, lLon);
		lMember.setMap(mMop);
		
		return lMember;
	}
	
	private void updateFamily(FamilyMember member) {
		for ( int i = 0; i < mFamilyMembers.size(); i++ ) {
			if ( mFamilyMembers.get(i).getId() == member.getId() ) {
				/**
				 * Already present in list
				 * Update to new location
				 */
				mFamilyMembers.get(i).setLocation(member.getLocation());
				return;
			}
		}
		/**
		 * Not found in the list
		 * Create an entry
		 */
		mFamilyMembers.add(member);
	}
	
	private void updateMapMarkers() {
		for ( int i = 0; i < mFamilyMembers.size(); i++ ) {
			Message msg = new Message();
			msg.obj = mFamilyMembers.get(i);
			mHandler.sendMessage(msg);
		}
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public void authenticateUser() {
		boolean isAuthenticated = false;
		Message msg;
		isAuthenticated = HttpMethod.AuthenticateUser(mId, mPassword);
		if (isAuthenticated) {
			msg = new Message();
			msg.arg1 = 1;
			mHandler.dispatchMessage(msg);
		} else {
			msg = new Message();
			msg.arg1 = 0;
			mHandler.dispatchMessage(msg);
		}
	}
	
	public void UpdateLocation(float lat,float lng) {
		if ( this.mServerMode != THREAD_MODE_BEACON ) 
			return;
		System.out.println("Staring update thread");
		mLat = lat;
		mLong = lng;
		
		this.start();
	}
}
