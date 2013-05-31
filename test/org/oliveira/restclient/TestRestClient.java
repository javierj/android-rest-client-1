package org.oliveira.restclient;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.oliveira.restclient.listener.OnRestResponse;


/**
 * @author Javier J.
 *
 */
public class TestRestClient extends TestCase {


	public void testRestClient_GET() {
		final MovieFactory movieFactory = new MovieFactory();
		RestClient rest = new RestClient("http://www.omdbapi.com", "");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("t", "prometheus"));
		
		rest.get("?t=prometheus", new OnRestResponse() {

		    public void onResponse(String response) {
				try {
					movieFactory.createMovie(response);
				} catch (JSONException e) {
					e.printStackTrace();
					fail("JSon serialization failure: " + response);
				}
		    }

		    public void onError(Integer errorCode, String errorMessage) {
		    	fail("Unexpected error.");
		    }
		});
		  
		// Wait for the answer  
		try {
			Thread.currentThread().sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Waiting response.");
		}
		
		Movie movie = movieFactory.movie;
		assertNotNull(movie);
		assertEquals("Prometheus", movie.title);
		assertEquals("2012", movie.year);
		assertEquals("Ridley Scott", movie.director);
	}
		
	static class MovieFactory  {
		Movie movie;
		
		public void createMovie(String json) throws JSONException {
			JSONObject jsonObject = new JSONObject(json);
			movie = new Movie();
			movie.title = jsonObject.getString("Title");
			movie.year = jsonObject.getString("Year");
			movie.director = jsonObject.getString("Director");
		}
	}

	static class Movie  {
		String title;
		String year;
		String director;
	}
		
}
