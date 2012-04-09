package org.oliveira.restclient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.oliveira.restclient.listener.OnRestResponse;

import android.os.AsyncTask;

public class RestClient {
	
	public static final int GET = 0;
	public static final int POST = 1;
	
    private ArrayList <NameValuePair> headers;
    private String server;
    private String key;
    private String rpc;

    public RestClient(String server, String key)
    {
    	this.server = server;
    	this.key = key;
        headers = new ArrayList<NameValuePair>();
    }

    public void addHeader(String name, String value)
    {
    	headers.add(new BasicNameValuePair(name, value));
    }
    
    public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getRpc() {
		return rpc != null ? rpc : "rpc";
	}
	public void setRpc(String rpc) {
		this.rpc = rpc;
	}

	public void call(String url, BasicNameValuePair[] parameters, OnRestResponse onReponse) {
		
		JSONObject request = new JSONObject();
		JSONObject json = new JSONObject();
		
		try {
			
			if(parameters != null){
				for (int i = 0; i < parameters.length; i++)	{
					json.put(parameters[i].getName(), parameters[i].getValue());
				}
			}			
			
			request.put("parameters", json);
			
		} catch (JSONException e) {
			onReponse.onError(501, e.getLocalizedMessage());
		}finally {
			
			HttpPost http = new HttpPost(getServer() + "/"+ getRpc() +"/" + url);
			http.setHeader("Content-type", "application/json");
			http.setHeader("Authorization" , getKey());
	    	
	    	for(NameValuePair h : headers) {
	    		http.addHeader(h.getName(), h.getValue());
	        }
	    	
	    	try {
				
	    		http.setEntity(new StringEntity(request.toString()));
			
	    	} catch (UnsupportedEncodingException e) {
				onReponse.onError(501, e.getLocalizedMessage());
			}finally {
			
				RestRequest restRequest = new RestRequest();
		    	restRequest.setOnReponse(onReponse);
		    	restRequest.setRequest(http);
		    	restRequest.execute();
				
			}
	    	
		}
		
	}

	public void execute(String url, int method, OnRestResponse onReponse) {
    	
		HttpUriRequest request = null;
    	
    	switch(method) {
            case GET:
                request = new HttpGet(getServer() + "/" + url);                
                break;
            case POST:
                request = new HttpPost(getServer() + "/" + url);
                break;
        }
    	
    	request.setHeader("Content-type", "application/json");
	    request.setHeader("Authorization" , getKey());
    	
    	for(NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
        }
    	
    	RestRequest restRequest = new RestRequest();
    	restRequest.setOnReponse(onReponse);
    	restRequest.setRequest(request);
    	restRequest.execute();
    	
    }
	
	private class RestRequest extends AsyncTask<String, Integer, String> {
		
		private HttpUriRequest request;
		private OnRestResponse onReponse;
		private Integer errorCode;
		private String errorMessage;
		
		public HttpUriRequest getRequest() {
			return request;
		}
		public void setRequest(HttpUriRequest request) {
			this.request = request;
		}
		
		public OnRestResponse getOnReponse() {
			return onReponse;
		}
		public void setOnReponse(OnRestResponse onReponse) {
			this.onReponse = onReponse;
		}
		
		public Integer getErrorCode() {
			return errorCode;
		}
		public void setErrorCode(Integer errorCode) {
			this.errorCode = errorCode;
		}
		
		public String getErrorMessage() {
			return errorMessage;
		}
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			HttpClient client = new DefaultHttpClient();
	        HttpResponse httpResponse;
	        String response = null;

	        try {
	            
	        	httpResponse = client.execute(getRequest());
	        	
	        	setErrorCode(httpResponse.getStatusLine().getStatusCode());
	            setErrorMessage(httpResponse.getStatusLine().getReasonPhrase());

	            HttpEntity entity = httpResponse.getEntity();
	            if (entity != null) {
	            	response = EntityUtils.toString(entity);
	            }	            
	        
	        } catch (Exception e)  {
	        	
	        	setErrorCode(e.hashCode());
	        	setErrorMessage(e.getLocalizedMessage());
	        
	        }
	        
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(getOnReponse() != null){
				if(result != null){
					getOnReponse().onResponse(result);
				}else{
					getOnReponse().onError(getErrorCode(), getErrorMessage());
				}
			}
		}
		
	}
    
}
