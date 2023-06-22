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

public class MovieControllerRA {
	
	private String movieTitle;
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
		postMovieInstance.put("title", "Ghost");
		postMovieInstance.put("score", 5.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
			.get("/movies")
		.then()
			.statusCode(200)
			.body("content.title", hasItems("The Witcher", "Matrix Resurrections"));
	}

	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		movieTitle = "Mat";
		given()
			.get("/movies?title={movieTitle}", movieTitle)
		.then()
			.statusCode(200)
			.body("content.title", hasItems("Matrix Resurrections", "Vingadores: Ultimato"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExist() {
		given()
			.get("/movies/{existMovieId}", existMovieId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
			.body("score", is(4.33F))
			.body("count", is(3));
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given()
			.get("/movies/{nonExistMovieId}", nonExistMovieId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		postMovieInstance.put("title", "");
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type","application/json")
			.header("Authorization","Bearer " + adminToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(422);
	}

	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws JSONException {
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type","application/json")
			.header("Authorization","Bearer " + clientToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
	}

	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws JSONException {
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type","application/json")
			.header("Authorization","Bearer " + invalidToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}


	
	
}
