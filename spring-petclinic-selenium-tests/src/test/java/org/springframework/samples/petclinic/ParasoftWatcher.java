package org.springframework.samples.petclinic;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class ParasoftWatcher implements BeforeEachCallback, TestWatcher  {

	private static int envId;
	private static String sessionId;
	private static String baselineId;
	private static String ctpUrl;

	static {
		// get envId
		envId = System.getProperty("envId", 32);
		// get uri and port from ctpUrl
		ctpUrl = System.getProperty("ctpUrl","http://localhost:8080");
		String[] uriComponents = ctpUrl.split(":");
		String uri = uriComponents[0]; // Extract the URI (e.g., http://35.90.203.18)
		int port = Integer.parseInt(uriComponents[1]); // Extract the port (e.g., 8080)
	
	    // CTP connection
		RestAssured.baseURI = uri;
	    RestAssured.port = port;
	    RestAssured.authentication = RestAssured.basic("demo", "demo-user");
		sessionId = RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + envId + "/agents/session/start").body().jsonPath().getString("session");
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		System.out.println("Session Id is: " + sessionId);
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		String testId = getTestId(context);
		Response response = RestAssured.with().contentType(ContentType.JSON).body("{\"test\":\"" + testId + "\"}").post("em/api/v3/environments/" + envId + "/agents/test/start");
		System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Payload: " + response.getBody().asString());
    
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		String testId = getTestId(context);
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append('{');
		bodyBuilder.append("\"test\":\"" + testId + "\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"result\":\"PASS\"");
		bodyBuilder.append('}');
		Response response = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + envId + "/agents/test/stop");
		System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Payload: " + response.getBody().asString());
    }
	

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		String testId = getTestId(context);
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append('{');
		bodyBuilder.append("\"test\":\"" + testId + "\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"result\":\"FAIL\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"message\":\"" + cause.getMessage() + "\"");
		bodyBuilder.append('}');
		Response response = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + envId + "/agents/test/stop");
		System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Payload: " + response.getBody().asString());
    
	}

	private static String getTestId(ExtensionContext context) {
		return context.getTestClass().get().getName() + '#' + context.getTestMethod().get().getName();
	}

	static class ShutdownHook extends Thread {
		@Override
		public void run() {
			baselineId = System.getProperty("baselineId", "latestBaseline");
			RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + envId + "/agents/session/stop");
			StringBuilder bodyBuilder = new StringBuilder();
			bodyBuilder.append('{');
			bodyBuilder.append("\"sessionTag\":\"jenkins-build\"");
			bodyBuilder.append(',');
			bodyBuilder.append("\"analysisType\":\"FUNCTIONAL_TEST\"");
			bodyBuilder.append('}');
			String publish = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + envId + "/coverage/" + sessionId).body().asString();
			System.out.println(publish);
			String baseline = RestAssured.with().contentType(ContentType.JSON).body("string").post("em/api/v3/environments/" + envId + "/coverage/baselines/" + baselineId).body().asString();
			System.out.println(baseline);
		}
	}
}
