package com.sylvyrfysh.monerowallet;

public class Network {
	public enum NetworkStatus{
		ERROR,
		UNCONNECTED,
		CONNECTED,
	}
	private static NetworkStatus status=NetworkStatus.UNCONNECTED;
	private static String statusMessage="";
	public static void tryConnect(char[] address,char[] port){
		statusMessage="Connecting...";
		//TODO: Daemon and all that
	}
	public static String getStatusMessage() {
		
		return statusMessage;
	}
	public static NetworkStatus getStatus() {
		return status;
	}
}
