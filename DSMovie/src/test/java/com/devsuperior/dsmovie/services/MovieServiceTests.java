package com.devsuperior.dsmovie.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.services.exceptions.DatabaseException;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class MovieServiceTests {
	
	@InjectMocks
	private MovieService service;
	
	@Mock
	private MovieRepository movieRepository;

	private Long existingMovieId;
	private Long nonExistingMovieId;
	private Long dependendMovieId;
	private MovieEntity movieEntity;
	private MovieDTO movieDTO;
	private String movieName;
	private PageImpl<MovieEntity> page;
	
	@BeforeEach
	void setUp(){
		existingMovieId = 1L;
		nonExistingMovieId = 2L;
		dependendMovieId = 3L;
		movieEntity = MovieFactory.createMovieEntity();
		movieDTO = MovieFactory.createMovieDTO();
		movieName = "The Witcher";
		page = new PageImpl<>(List.of(movieEntity));
		
		Mockito.when(movieRepository.searchByTitle(ArgumentMatchers.any(), (Pageable)ArgumentMatchers.any())).thenReturn(page);

		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movieEntity));
		Mockito.when(movieRepository.findById(nonExistingMovieId)).thenReturn(Optional.empty());

		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movieEntity);	
		
		Mockito.when(movieRepository.getReferenceById(existingMovieId)).thenReturn(movieEntity);
		Mockito.when(movieRepository.getReferenceById(nonExistingMovieId)).thenThrow(EntityNotFoundException.class);	

		Mockito.when(movieRepository.existsById(existingMovieId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(dependendMovieId)).thenReturn(true);
		Mockito.when(movieRepository.existsById(nonExistingMovieId)).thenReturn(false);
		
		Mockito.doNothing().when(movieRepository).deleteById(existingMovieId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(movieRepository).deleteById(dependendMovieId);
		
	}
	
	@Test
	public void findAllShouldReturnPagedMovieDTO() {
		Pageable pageable = PageRequest.of(0, 12);
		Page<MovieDTO> result = service.findAll(movieName, pageable);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);
	}
	
	@Test
	public void findByIdShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.findById(existingMovieId);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingMovieId);
		Assertions.assertEquals(result.getTitle(), movieEntity.getTitle());
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingMovieId);
		});
	}
	
	@Test
	public void insertShouldReturnMovieDTO() {
		MovieDTO result = service.insert(movieDTO);		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), movieEntity.getId());
		Assertions.assertEquals(result.getTitle(), movieEntity.getTitle());
	}
	
	@Test
	public void updateShouldReturnMovieDTOWhenIdExists() {
		MovieDTO result = service.update(existingMovieId, movieDTO);
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingMovieId);
		Assertions.assertEquals(result.getTitle(), movieDTO.getTitle());
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			MovieDTO result = service.update(nonExistingMovieId, movieDTO);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingMovieId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingMovieId);
		});
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependendMovieId);
		});
	}
}
