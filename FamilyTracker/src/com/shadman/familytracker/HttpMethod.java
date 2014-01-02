package com.shadman.familytracker;

import java.net.*;
import java.io.*;

public class HttpMethod {
	
	static String getFamilyMembers(int id, String password ) throws ErrorCode {
		System.out.println("Getting family memebers for user = " + id);
		String args = "user="+id+"&"+"password="+password;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try {
			URL url = new URL(Constants.GET_FAMILY_MEMBERS);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(Constants.HTTP_PUSH);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			
			conn.getOutputStream().write( args.getBytes());

			InputStream is = conn.getInputStream();
			byte[] b = new byte[1024];
			while ( is.read(b) != -1 )
				output.write(b);

			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorCode(ErrorCode.ERROR_UNKNOWN);
		}
		
		String response = output.toString();
		
		if ( response.trim().equals("ERR_SERVER") )
			throw new ErrorCode(ErrorCode.ERROR_SERVER);
		else if ( response.trim().equals("ERR_USER_AUTH") )
			throw new ErrorCode(ErrorCode.ERROR_USER_AUTH);
		
		return response;
	}

	static void sendUpdateLocation(int id, String password, float lat, float lon) throws ErrorCode{
		System.out.println("Updating location " + lat + ", " + lon);
		String args = "user="+id+"&password="+password+"&lat="+lat+"&lon="+lon;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try {
			URL url = new URL(Constants.PUSH_CURRENT_LOCATION);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(Constants.HTTP_PUSH);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			
			conn.getOutputStream().write( args.getBytes());

			InputStream is = conn.getInputStream();
			byte[] b = new byte[1024];
			while ( is.read(b) != -1 )
				output.write(b);

			conn.disconnect();

		} catch (Exception e) {
			System.out.println("Unknown error " + e.getMessage());
			e.printStackTrace();
			throw new ErrorCode(ErrorCode.ERROR_UNKNOWN);
		}
		
		String response = output.toString();
		
		if ( response.trim().equals("ERR_SERVER") )
			throw new ErrorCode(ErrorCode.ERROR_SERVER);
		else if ( response.trim().equals("ERR_USER_AUTH") )
			throw new ErrorCode(ErrorCode.ERROR_USER_AUTH);
	}
	
	static boolean AuthenticateUser(int id, String password) {
		System.out.println("Authenticating user = " + id + " and " + password);
		String args = "user="+id+"&"+"password="+password;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try {
			URL url = new URL(Constants.USER_LOGIN);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(Constants.HTTP_PUSH);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			
			conn.getOutputStream().write( args.getBytes());

			InputStream is = conn.getInputStream();
			byte[] b = new byte[1024];
			while ( is.read(b) != -1 )
				output.write(b);

			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		String response = output.toString();
		
		if ( response.trim().equals("ERR_SERVER") )
			return false;
		else if ( response.trim().equals("ERR_USER_AUTH") )
			return false;
		else if ( response.trim().equals("AUTH_SUCCESS"))
			return true;
		
		return false;
	}
}
