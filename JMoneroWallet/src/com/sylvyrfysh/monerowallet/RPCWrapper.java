package com.sylvyrfysh.monerowallet;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import wallet.MoneroAddress;
import wallet.MoneroTransaction;
import wallet.MoneroUtils;
import wallet.MoneroWalletRpc;

public class RPCWrapper {
	private static MoneroWalletRpc rpc;
	private static TimedObject<BigInteger> balance = new TimedObject<>(null, -1),
			unlockedBalance = new TimedObject<>(null, -1);
	private static TimedObject<Integer> height = new TimedObject<>(null, -1);
	private static TimedObject<MoneroAddress> stdAddress = new TimedObject<>(null, -1);
	private static TimedObject<List<MoneroTransaction>> transactions = new TimedObject<>(null, -1);

	public RPCWrapper(MoneroWalletRpc moneroWalletRpc) {
		rpc = moneroWalletRpc;
	}

	public static BigInteger getBalance() {
		if (!balance.isValid())
			balance = new TimedObject<>(rpc.getBalance(), 5);
		return balance.getObj();
	}

	public BigInteger getUnlockedBalance() {
		if (!unlockedBalance.isValid())
			unlockedBalance = new TimedObject<>(rpc.getBalance(), 5);
		return unlockedBalance.getObj();
	}

	public static Integer getHeight() {
		if (!height.isValid())
			height = new TimedObject<>(rpc.getHeight(), 5);
		return height.getObj();
	}

	public MoneroAddress getStandardAddress() {
		if (!stdAddress.isValid())
			stdAddress = new TimedObject<>(rpc.getStandardAddress(), 3600);
		return stdAddress.getObj();
	}

	public List<MoneroTransaction> getAllTransactions() {
		if (!transactions.isValid())
			transactions = new TimedObject<>(rpc.getAllTransactions(), 5);
		return transactions.getObj();
	}
}
