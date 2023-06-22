package com.devsuperior.dsmovie.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private UserService userService;

	private Long existingMovieId;
	private Long nonExistingMovieId;
	private Long dependendMovieId;
	private ScoreEntity scoreEntity;
	private MovieEntity movieEntity;
	private MovieDTO movieDTO;
	private ScoreDTO scoreDTO;
	private UserEntity client;
	private UserEntity admin;
	
	@BeforeEach
	void setUp() {
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependendMovieId = 3L;
		client = UserFactory.createUserEntity();
		scoreEntity = ScoreFactory.createScoreEntity();
		movieEntity = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();
		scoreDTO = ScoreFactory.createScoreDTO();

		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());
		
		Mockito.when(movieRepository.existsById(existingMovieId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(dependendMovieId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movieEntity);	
		Mockito.when(scoreRepository.save(ArgumentMatchers.any())).thenReturn(scoreEntity);	
		Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(scoreEntity);	
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		Mockito.when(userService.authenticated()).thenReturn(client);
		movieEntity.setId(existingMovieId);
		scoreEntity.setMovie(movieEntity);
		scoreEntity.setUser(client);
		ScoreDTO temp2 = new ScoreDTO(existingMovieId, 2.5);
		MovieDTO result = service.saveScore(temp2);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Mockito.when(userService.authenticated()).thenReturn(client);
		movieEntity.setId(nonExistingMovieId);
		MovieDTO temp = new MovieDTO(movieEntity);
		ScoreDTO temp2 = new ScoreDTO(nonExistingMovieId, 2.5);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			MovieDTO result = service.saveScore(temp2);
		});
	}
}
