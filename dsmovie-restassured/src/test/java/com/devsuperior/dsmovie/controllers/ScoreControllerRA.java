package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {

	private Long existMovieId,nonExistMovieId;
	private String adminToken,clientToken,invalidToken;
	private String clienteUsername,clientPassword,adminUsername,adminPassword;

	private Map<String,Object> postMovieInstance;

	@BeforeEach
	void setUp() {
		baseURI = "http://localhost:8080";
		existMovieId = 1L;
		nonExistMovieId = 100L;
		clienteUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccesToken(clienteUsername, clientPassword);
		adminToken = TokenUtil.obtainAccesToken(adminUsername, adminPassword);
		invalidToken = adminToken + "sjfgh";
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("score", 4.0);
		
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws JSONException {
		postMovieInstance.put("movieId", nonExistMovieId);
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type","application/json")
			.header("Authorization","Bearer " + adminToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(404);
		
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws JSONException {
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type","application/json")
			.header("Authorization","Bearer " + clientToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422);
		
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws JSONException {
		postMovieInstance.put("movieId", existMovieId);
		postMovieInstance.put("score", -4.0);
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type","application/json")
			.header("Authorization","Bearer " + clientToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422);
		
	}
	
	
	
	
}
