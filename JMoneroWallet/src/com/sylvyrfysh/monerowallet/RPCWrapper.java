package com.sylvyrfysh.monerowallet;

import java.math.BigInteger;

import wallet.MoneroWalletRpc;

public class RPCWrapper {
	private static MoneroWalletRpc rpc;
	private static TimedObject<BigInteger> balance = new TimedObject<>(null, -1);
	private static TimedObject<Integer> height = new TimedObject<>(null, -1);
	public RPCWrapper(MoneroWalletRpc moneroWalletRpc) {
		rpc = moneroWalletRpc;
	}
	public static BigInteger getBalance() {
		if(!balance.isValid())
			balance = new TimedObject<>(rpc.getBalance(),5);
		return balance.getObj();
	}
	public static Integer getHeight() {
		if(!height.isValid())
			height = new TimedObject<>(rpc.getHeight(),5);
		return height.getObj();
	}
}
