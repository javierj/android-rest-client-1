# Simplistic Rest Client for Android

This library use android based resources to make operations on a rest server.

## Install

	1 - Clone the project;
	2 - In Eclipse > File > New > Java Project;
	3 - Unmark use default location and select the folder cloned;
	4 - Add to build path a android.jar compatible with your project version (sdk folder > platform > android-14 > android.jar);
	5 - Add to your Android project a the dependence to this project. 

## Instance
	
	String remote = "http://localhost:8888";
	String key = "teste";

	RestClient rest = new RestClient(remote, key);

**remote**: Rest Service base url.<br>
**key**: If your server has a authetication header field.<br>

## Usage

	BasicNameValuePair params[] = {
		new BasicNameValuePair("username", "teste"),
		new BasicNameValuePair("password", "teste")
	};
	String rpc = 'login';

	rest.call(rpc, params, new OnRestResponse() {
			
		public void onResponse(String response) {
			
		}
	
		public void onError(Integer errorCode, String errorMessage) {
			
		}

	});

The library will convert the the params array to a json format closured on a array calles parameters, the server will receive some like: {parameters: {username: "teste", password: "teste"}}

## TODO

* test suite.

## License

Copyright (C) 2012 Guilherme Henrique de Oliveira

Distributed under the MIT License.