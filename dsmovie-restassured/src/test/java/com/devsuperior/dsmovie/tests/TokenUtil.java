package com.devsuperior.dsmovie.tests;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TokenUtil {
	
	public static String obtainAccesToken(String userName, String password) {
		Response response = authRequest(userName, password);
		JsonPath jsonBody = response.jsonPath();
		return jsonBody.getString("access_token");
	}

	private static Response authRequest(String userName, String password) {
		return given()
				.auth()
				.preemptive()
				.basic("myclientid", "myclientsecret")
			.contentType("application/x-www-form-urlencoded")
				.formParam("grant_type", "password")
				.formParam("username", userName)
				.formParam("password", password)
			.when()
				.post("/oauth2/token");
	}
}
