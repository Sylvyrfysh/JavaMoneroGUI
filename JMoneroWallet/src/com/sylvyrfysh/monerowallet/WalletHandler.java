package com.sylvyrfysh.monerowallet;

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

		ProcessBuilder pb = new ProcessBuilder(String.format("res/%s/monerod.exe", TOOLS_VERSION));

		// TODO: Daemon and all that
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
}