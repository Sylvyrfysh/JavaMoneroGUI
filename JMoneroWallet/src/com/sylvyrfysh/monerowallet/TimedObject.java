package com.sylvyrfysh.monerowallet;

public class TimedObject<T> {
	private T obj;
	private long endTime;
	public TimedObject(T obj,long secondsOfValidity) {
		this.obj = obj;
		this.endTime = (secondsOfValidity * 1000) + System.currentTimeMillis();
	}
	public boolean isValid() {
		return System.currentTimeMillis() < endTime;
	}
	public T getObj() {
		return obj;
	}
}
