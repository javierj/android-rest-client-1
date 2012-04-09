package org.oliveira.restclient.listener;

public interface OnRestResponse {
	public abstract void onResponse(String response);
	public abstract void onError(Integer errorCode, String errorMessage);
}
