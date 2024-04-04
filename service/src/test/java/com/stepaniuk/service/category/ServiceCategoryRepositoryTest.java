package com.stepaniuk.service.category;


import com.stepaniuk.service.testspecific.JpaLevelTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JpaLevelTest
@Sql(scripts = "classpath:sql/service_categories.sql")
class ServiceCategoryRepositoryTest {
  @Autowired
  private ServiceCategoryRepository serviceCategoryRepository;

  @Test
  void shouldSaveServiceCategory() {

    ServiceCategory serviceCategoryToSave = new ServiceCategory(
      null, "title", "description", List.of("https://image.com/1"), Instant.now(), Instant.now()
    );

    ServiceCategory savedServiceCategory = serviceCategoryRepository.save(serviceCategoryToSave);

    assertNotNull(savedServiceCategory);
    assertNotNull(savedServiceCategory.getId());
    assertEquals(serviceCategoryToSave.getTitle(), savedServiceCategory.getTitle());
    assertEquals(serviceCategoryToSave.getDescription(), savedServiceCategory.getDescription());
    assertEquals(serviceCategoryToSave.getImageUrls(), savedServiceCategory.getImageUrls());
    assertEquals(serviceCategoryToSave.getCreatedAt(), savedServiceCategory.getCreatedAt());
    assertEquals(serviceCategoryToSave.getLastModifiedAt(), savedServiceCategory.getLastModifiedAt());
  }

  @Test
  void shouldThrowExceptionWhenSavingServiceCategoryWithoutTitle() {

    ServiceCategory serviceCategoryToSave = new ServiceCategory(
        null, null, "description", List.of("https://image.com/1"), Instant.now(), Instant.now()
    );

    assertThrows(Exception.class, () -> serviceCategoryRepository.save(serviceCategoryToSave));
  }

  @Test
  void shouldReturnServiceCategoryWhenFindById() {
    Optional<ServiceCategory> optionalServiceCategory = serviceCategoryRepository.findById(1L);

    assertTrue(optionalServiceCategory.isPresent());

    ServiceCategory foundServiceCategory = optionalServiceCategory.get();

    assertNotNull(foundServiceCategory);
    assertEquals(1L, foundServiceCategory.getId());
    assertEquals("title", foundServiceCategory.getTitle());
    assertEquals("description", foundServiceCategory.getDescription());
    assertEquals(List.of("https://image.com/1"), foundServiceCategory.getImageUrls());
    assertNotNull(foundServiceCategory.getCreatedAt());
    assertNotNull(foundServiceCategory.getLastModifiedAt());
  }

  @Test
  void shouldUpdateServiceCategoryWhenChangingTitle() {
    ServiceCategory serviceCategoryToUpdate = serviceCategoryRepository.findById(1L).orElseThrow();
    serviceCategoryToUpdate.setTitle("New title");
    ServiceCategory updatedServiceCategory = serviceCategoryRepository.save(
        serviceCategoryToUpdate);

    assertEquals(serviceCategoryToUpdate.getId(), updatedServiceCategory.getId());
    assertEquals("New title", updatedServiceCategory.getTitle());
  }

  @Test
  void shouldDeleteServiceCategoryWhenDeletingByExistingService() {
    // given
    ServiceCategory serviceToDelete = serviceCategoryRepository.findById(1L).orElseThrow();
    // when
    serviceCategoryRepository.delete(serviceToDelete);
    // then
    assertTrue(serviceCategoryRepository.findById(1L).isEmpty());
  }

  @Test
  void shouldDeleteServiceCategoryByIdWhenDeletingByExistingId() {
    // when
    serviceCategoryRepository.deleteById(1L);
    // then
    assertTrue(serviceCategoryRepository.findById(1L).isEmpty());
  }

  @Test
  void shouldReturnTrueWhenServiceCategoryExists() {
    // when
    boolean exists = serviceCategoryRepository.existsById(1L);
    // then
    assertTrue(exists);
  }

  @Test
  void shouldReturnNonEmptyListWhenFindAll() {
    // when
    List<ServiceCategory> serviceCategories = serviceCategoryRepository.findAll();

    // then
    assertNotNull(serviceCategories);
    assertFalse(serviceCategories.isEmpty());
  }

}
