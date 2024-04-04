package com.stepaniuk.service;

import com.stepaniuk.service.testspecific.JpaLevelTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JpaLevelTest
@Sql(scripts = "classpath:sql/services.sql")
class ServiceRepositoryTest {

  @Autowired
  private ServiceRepository serviceRepository;

  @Test
  void shouldSaveService(){
    Service serviceToSave = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, Instant.now(), Instant.now()
    );

    Service savedService = serviceRepository.save(serviceToSave);

    assertNotNull(savedService);
    assertNotNull(savedService.getId());
    assertEquals(serviceToSave.getCategoryId(), savedService.getCategoryId());
    assertEquals(serviceToSave.getOwnerId(), savedService.getOwnerId());
    assertEquals(serviceToSave.getTitle(), savedService.getTitle());
    assertEquals(serviceToSave.getDescription(), savedService.getDescription());
    assertEquals(serviceToSave.getImageUrls(), savedService.getImageUrls());
    assertEquals(serviceToSave.getPrice(), savedService.getPrice());
    assertEquals(serviceToSave.getPriority(), savedService.getPriority());
    assertEquals(serviceToSave.getCreatedAt(), savedService.getCreatedAt());
    assertEquals(serviceToSave.getLastModifiedAt(), savedService.getLastModifiedAt());
  }

  @Test
  void shouldThrowExceptionWhenSavingServiceWithoutTitle(){
    Service serviceToSave = new Service(
        null, 1L, UUID.randomUUID(), null, "Description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, Instant.now(), Instant.now()
    );

    assertThrows(Exception.class, () -> serviceRepository.save(serviceToSave));
  }

  @Test
  void shouldReturnServiceWhenFindById(){
    Optional<Service> optionalService = serviceRepository.findById(1L);

    assertTrue(optionalService.isPresent());

    Service foundService = optionalService.get();

    assertNotNull(foundService);
    assertEquals(1L, foundService.getId());
    assertEquals(1L, foundService.getCategoryId());
    assertNotNull(foundService.getOwnerId());
    assertEquals("First title", foundService.getTitle());
    assertEquals("First description", foundService.getDescription());
    assertEquals(List.of("https://image.com/1"), foundService.getImageUrls());
    assertThat(foundService.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
    assertEquals(1, foundService.getPriority());
    assertNotNull(foundService.getCreatedAt());
    assertNotNull(foundService.getLastModifiedAt());
  }

  @Test
  void shouldUpdateServiceWhenChangingTitle(){
    Service serviceToUpdate = serviceRepository.findById(1L).orElseThrow();
    serviceToUpdate.setTitle("New title");
    Service updatedService = serviceRepository.save(
        serviceToUpdate);

    assertEquals(serviceToUpdate.getId(), updatedService.getId());
    assertEquals("New title", updatedService.getTitle());
  }

  @Test
  void shouldDeleteServiceWhenDeletingByExistingService(){
    // given
    Service serviceToDelete = serviceRepository.findById(1L).orElseThrow();

    // when
    serviceRepository.delete(serviceToDelete);

    // then
    Optional<Service> optionalService = serviceRepository.findById(1L);
    assertTrue(optionalService.isEmpty());
  }

  @Test
  void shouldDeleteServiceWhenDeletingByExistingServiceId(){
    // when
    serviceRepository.deleteById(1L);

    assertTrue(serviceRepository.findById(1L).isEmpty());
  }

  @Test
  void shouldReturnTrueWhenServiceExistsById(){
    assertTrue(serviceRepository.existsById(1L));
  }

  @Test
  void shouldReturnNonEmptyListWhenFindAll(){
    List<Service> services = serviceRepository.findAll();

    assertNotNull(services);
    assertFalse(services.isEmpty());
  }
}
