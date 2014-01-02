package com.shadman.familytracker;

import java.util.ArrayList;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FamilyMember {
	
	private int id;
	private String name;
	private LatLng location;
	private ArrayList <String> family = new ArrayList<String>();
	private Marker memberMarker;
	private GoogleMap mMap;
	
	public FamilyMember(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public FamilyMember() {}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public LatLng getLocation() {
		return location;
	}
	
	public void setLocation(float lat, float lon) {
		LatLng lLocation = new LatLng(lat, lon);
		location = lLocation;
	}
	
	public void setLocation(LatLng location) {
		this.location = location;
	}
	
	public void setInformation(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public void setMap(GoogleMap map) {
		mMap = map;
	}
	
	public void addToFamily(String familyName) {
		family.add(familyName);
	}
	
	public void updateMarker() {
		if ( mMap == null ) {
			System.out.println("Error in google maps.");
			return;
		}
		
		if ( memberMarker == null ) {
			memberMarker = mMap.addMarker(new MarkerOptions()
		     .position(getLocation())
		     .title(getName()));
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocation(), 15));
			return;
		}

		// Marker already present in map
		memberMarker.setPosition(getLocation());
	}
	
}
