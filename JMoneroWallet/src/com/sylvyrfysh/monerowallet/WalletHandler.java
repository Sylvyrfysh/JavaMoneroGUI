package com.sylvyrfysh.monerowallet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WalletHandler {

	private static final Logger logger = LogManager.getLogger();

	public enum WalletHandlerStatus {
		ERROR, UNCONNECTED, CONNECTED, CONNECTED_WITH_ERROR,
	}

	private static WalletHandlerStatus status = WalletHandlerStatus.UNCONNECTED;
	private static String statusMessage = "";
	public static String TOOLS_VERSION = "v0_11_1_0";
	private static boolean haveValidNode = false;
	private static String nodeAddress;

	public static void tryConnect(String address, String port) {
		int portN = 0;
		try {
			portN = Integer.parseInt(port);
			if (portN < 0 || portN > 65535)
				throw new NumberFormatException("Port is not 1-65535!");

		} catch (NumberFormatException e) {
			updateErrorStatus("Number %s is not a valid port! %s", e, port, e.getMessage());
			return;
		}
		statusMessage = "Connecting...";

		try {
			HttpURLConnection s = (HttpURLConnection) new URL(String.format("http://%s:%d/getheight", address, portN))
					.openConnection();
			s.addRequestProperty("Content-Type", "application/json");
			if (s.getResponseCode() != 200) {
				throw new IOException(String.format("%d", s.getResponseCode()));
			}
			logger.trace("node {}:{} ({}) is valid", address, portN,
					InetAddress.getByName(s.getURL().getHost()).getHostAddress());
			statusMessage = "Valid Node!";
			haveValidNode = true;
			nodeAddress = String.format("http://%s:%d/", address, portN);
		} catch (MalformedURLException e) {
			updateErrorStatus("Address %s:%d is not a valid address! %s", e, address, portN, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			updateErrorStatus("Could not connect to address %s:%d! %s", e, address, portN, e.getMessage());
			e.printStackTrace();
		}
	}

	public static void commitNode() {
		//Config.setAttribute("wallet.node_address", nodeAddress);
	}

	public static String getStatusMessage() {
		return statusMessage;
	}

	public static WalletHandlerStatus getStatus() {
		return status;
	}

	public static void clearErrorStatus() {
		switch (status) {
		case CONNECTED:
		case CONNECTED_WITH_ERROR:
			status = WalletHandlerStatus.CONNECTED;
			break;
		case ERROR:
		case UNCONNECTED:
			status = WalletHandlerStatus.UNCONNECTED;
			break;
		default:
			break;
		}
	}

	private static void updateErrorStatus(String format, Throwable err, Object... strs) {
		switch (status) {
		case CONNECTED:
		case CONNECTED_WITH_ERROR:
			status = WalletHandlerStatus.CONNECTED_WITH_ERROR;
			break;
		case ERROR:
		case UNCONNECTED:
			status = WalletHandlerStatus.ERROR;
			break;
		default:
			break;
		}
		statusMessage = String.format(format, strs);
		logger.error(statusMessage);
		logger.error(err);
	}

	public static boolean hasValidNode() {
		return haveValidNode;
	}

	public static void nodeChanged() {
		WalletHandler.haveValidNode = false;
		statusMessage = "";
	}
}