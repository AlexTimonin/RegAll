package com.regall.old.network;

public interface Callback<T> {

	void success(Object object);

	void failure(Exception e);

}
