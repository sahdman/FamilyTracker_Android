package com.shadman.familytracker;

public class ErrorCode extends Exception {
	
	private static final long serialVersionUID = -2949017675291051618L;
	static final Throwable ERROR_SERVER = new Throwable("ERROR_SERVER");
	static final Throwable ERROR_USER_AUTH = new Throwable("ERROR_USER_AUTH");
	static final Throwable ERROR_UNKNOWN = new Throwable("ERROR_UNKNOWN");
	
	public ErrorCode(Throwable err) {
		super(err);
	}
	
}
