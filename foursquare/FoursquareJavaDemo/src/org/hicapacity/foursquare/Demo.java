package org.hicapacity.foursquare;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Foursquare2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 * Demonstrates the use of the Foursquare API using scribe-java (https://github.com/fernandezpablo85/scribe-java).
 * 
 * Based on Foursquare example at:
 * https://github.com/fernandezpablo85/scribe-java/blob/master/src/test/java/org/scribe/examples/Foursquare2Example.java
 * 
 * JSON parsing done with JSON-Java (https://github.com/douglascrockford/JSON-java).
 * 
 * @author jsakuda
 */
public class Demo {
	private static final Token EMPTY_TOKEN = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("4s.properties"));
		}
		catch (Exception ex) {
			
		}
		
	    // Replace these with your own api key and secret
	    String apiKey = props.getProperty("clientId");
	    String apiSecret = props.getProperty("clientSecret");
	    OAuthService service = new ServiceBuilder()
	                                  .provider(Foursquare2Api.class)
	                                  .apiKey(apiKey)
	                                  .apiSecret(apiSecret)
	                                  .callback(props.getProperty("callback"))
	                                  .build();
	    Scanner in = new Scanner(System.in);

	    System.out.println("=== Foursquare2's OAuth Workflow ===");
	    System.out.println();

	    // Obtain the Authorization URL
	    System.out.println("Fetching the Authorization URL...");
	    String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
	    System.out.println("Got the Authorization URL!");
	    System.out.println("Now go and authorize Scribe here:");
	    System.out.println(authorizationUrl);
	    System.out.println("And paste the authorization code here");
	    System.out.print(">>");
	    Verifier verifier = new Verifier(in.nextLine());
	    System.out.println();
	    
	    // Trade the Request Token and Verfier for the Access Token
	    System.out.println("Trading the Request Token for an Access Token...");
	    Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
	    System.out.println("Got the Access Token!");
	    System.out.println("(if your curious it looks like this: " + accessToken + " )");
	    System.out.println();

	    System.out.println("Running my info example...");
	    showMyInfo(service, accessToken);
	    
	    System.out.println();
	    System.out.print("Press Enter To Run Next Example...");
	    in.nextLine();
	    
	    System.out.println("Running Austen info example...");
	    showAustenInfo(service, accessToken);
	    
	    System.out.println();
	    System.out.print("Press Enter To Run Next Example...");
	    in.nextLine();
	    
	    System.out.println("Running venue info example...");
	    showVenueInfo(service, accessToken);
	}
	
	private static void showMyInfo(OAuthService service, Token accessToken) {
	    String url = "https://api.foursquare.com/v2/users/self/friends?oauth_token=" + accessToken.getToken();
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
	    service.signRequest(accessToken, request);
	    Response response = request.send();
	    
		// Foursquare API returns JSON, going to parse it here for
		try {
			JSONObject json = new JSONObject(response.getBody());
			JSONObject responseObj = json.getJSONObject("response");
			JSONObject friendsObj = responseObj.getJSONObject("friends");
			
			System.out.println();
			System.out.println("My Friend Count: " + friendsObj.getInt("count"));
			System.out.println(response.getBody());
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private static void showAustenInfo(OAuthService service, Token accessToken) {
		// Austen Ito's ID: 12241969
	    String url = "https://api.foursquare.com/v2/users/12241969/friends?oauth_token=" + accessToken.getToken();
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
	    service.signRequest(accessToken, request);
	    Response response = request.send();
	    
		// Foursquare API returns JSON, going to parse it here for
		try {
			JSONObject json = new JSONObject(response.getBody());
			JSONObject responseObj = json.getJSONObject("response");
			JSONObject friendsObj = responseObj.getJSONObject("friends");
			
			System.out.println();
			System.out.println("Austen's Friend Count: " + friendsObj.getInt("count"));
			System.out.println(response.getBody());
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		// Code used to find Austen's user id
//	    OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.foursquare.com/v2/users/search?twitter=austenito&oauth_token=" + accessToken.getToken());
//	    service.signRequest(accessToken, request);
//	    Response response = request.send();
//	    System.out.println(response.getBody());
	}
	
	private static void showVenueInfo(OAuthService service, Token accessToken) {
		// https://foursquare.com/v/the-box-jelly/4df83f211f6e448ec28f078e
		String venueId = "4df83f211f6e448ec28f078e";
		
		String url = "https://api.foursquare.com/v2/venues/" + venueId + "?oauth_token=" + accessToken.getToken();
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		service.signRequest(accessToken, request);
		Response response = request.send();
		
		// Foursquare API returns JSON, going to parse it here
		try {
			JSONObject json = new JSONObject(response.getBody());
			JSONObject responseObj = json.getJSONObject("response");
			JSONObject venueObj = responseObj.getJSONObject("venue");
			JSONObject locObj = venueObj.getJSONObject("location");
			
			System.out.println();
			System.out.println("Information for venue with ID: " + venueId);
			System.out.println(venueObj.getString("name"));
			System.out.println(locObj.getString("address") + " " + locObj.getString("city") + ", " + locObj.getString("state"));
			System.out.println(response.getBody());
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
