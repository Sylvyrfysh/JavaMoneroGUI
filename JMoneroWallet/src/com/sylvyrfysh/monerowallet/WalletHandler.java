package com.sylvyrfysh.monerowallet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wallet.MoneroWalletRpc;

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
	private static int RPC_PORT = 22665;
	private static RPCWrapper rpc;

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

	public static void openRPC(String wallet, String passcode) {
		Thread rpcThread = new Thread(() -> {
			logger.trace("Node is {}",Config.getJSON().get("wallet.node_address"));
			ProcessBuilder pf=new ProcessBuilder(String.format("res/%s/monero-wallet-rpc.exe",TOOLS_VERSION),String.format("--wallet-file=%s",wallet),String.format("--daemon-address=%s",Config.getJSON().get("wallet.node_address")),String.format("--rpc-bind-port=%d",RPC_PORT),"--disable-rpc-login");
			pf.redirectErrorStream(true);
			Process p = null;
			try {
				p=pf.start();
				BufferedReader bis = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";
				while((line = bis.readLine())!=null) {
					logger.trace("RPCth: {}",line);
					if(line.contains("Loading wallet...")) {
						p.getOutputStream().write(passcode.getBytes());
						p.getOutputStream().write('\n');
						p.getOutputStream().flush();
					}else if(line.contains("Starting wallet rpc server")) {
						Thread startInternalSocket = new Thread(() -> {
							logger.info("Waiting for RPC start");
							try {
								Thread.sleep(2500);
							} catch (InterruptedException e) {}
							try {
								/*String file = String.format("monero-wallet-rpc.%d.login", RPC_PORT);
								Path f = Paths.get(new File(file).toURI());
								String upp = new String(Files.readAllBytes(f));
								String[] pair = upp.split(":");*/
								URI s = new URL(String.format("http://127.0.0.1:%d", RPC_PORT)).toURI();
								rpc = new RPCWrapper(new MoneroWalletRpc(s.getHost(),s.getPort(),"",""/*,pair[0],pair[1]*/));
								logger.trace("Height is {}",RPCWrapper.getHeight());
							} catch (URISyntaxException | IOException e) {
								logger.error(e);
							}
							logger.trace("RPC Started");
						});
						startInternalSocket.setName("Start RPC");
						startInternalSocket.start();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		rpcThread.setName("RPC Thread");
		rpcThread.start();
		logger.info("RPC Thread started");
	}
	
	public static void commitNode() {
		Config.getJSON().put("wallet.node_address", nodeAddress);
		Config.writeConfig();
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

	public static RPCWrapper getRpc() {
		return rpc;
	}
}