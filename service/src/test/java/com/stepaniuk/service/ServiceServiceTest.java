package com.stepaniuk.service;


import com.stepaniuk.service.category.ServiceCategory;
import com.stepaniuk.service.category.ServiceCategoryMapperImpl;
import com.stepaniuk.service.category.ServiceCategoryRepository;
import com.stepaniuk.zrobleno.payload.service.ServiceCreateRequest;
import com.stepaniuk.zrobleno.payload.service.ServiceResponse;
import com.stepaniuk.zrobleno.payload.service.ServiceUpdateRequest;
import com.stepaniuk.zrobleno.payload.service.category.ServiceCategoryResponse;
import com.stepaniuk.zrobleno.testspecific.ServiceLevelUnitTest;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceByIdException;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceCategoryByIdException;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ServiceLevelUnitTest
@ContextConfiguration(classes = {ServiceService.class, ServiceMapperImpl.class,
    ServiceCategoryMapperImpl.class})
class ServiceServiceTest {

  @Autowired
  private ServiceService serviceService;
  @MockBean
  private ServiceRepository serviceRepository;
  @MockBean
  private ServiceCategoryRepository serviceCategoryRepository;

  @Test
  void shouldReturnServiceResponseWhenCreatingService(){

    var serviceCreateRequest = new ServiceCreateRequest(1L, UUID.randomUUID(), "title", "description",
        1, List.of("https://image.com/1"), BigDecimal.valueOf(100)
    );
    var serviceCategory = new ServiceCategory(1L, "title", "description", List.of("https://image.com/1"),
        Instant.now(), Instant.now()
    );

    when(serviceCategoryRepository.findById(1L)).thenReturn(Optional.of(serviceCategory));
    when(serviceRepository.save(any())).thenAnswer(answer(getFakeSave(1L)));

    ServiceResponse serviceResponse = serviceService.createService(serviceCreateRequest);

    assertNotNull(serviceResponse);
    assertEquals(1L, serviceResponse.getId());
    assertEquals(serviceCreateRequest.getCategoryId(), serviceResponse.getCategoryId());
    assertEquals(serviceCreateRequest.getOwnerId(), serviceResponse.getOwnerId());
    assertEquals(serviceCreateRequest.getTitle(), serviceResponse.getTitle());
    assertEquals(serviceCreateRequest.getDescription(), serviceResponse.getDescription());
    assertEquals(serviceCreateRequest.getImageUrls().get(0), serviceResponse.getImageUrls().get(0));
    assertEquals(serviceCreateRequest.getPriority(), serviceResponse.getPriority());
    assertThat(serviceCreateRequest.getPrice()).isEqualByComparingTo(serviceResponse.getPrice());
    assertTrue(serviceResponse.hasLinks());
  }

  @Test
  void shouldReturnServiceResponseWhenGetByExistingId() {

    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Service serviceToFind = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, timeOfCreation, timeOfModification
    );

    when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceToFind));

    // when
    ServiceResponse serviceResponse = serviceService.getService(1L);
    var listOfImages = serviceResponse.getImageUrls();

    // then
    assertNotNull(serviceResponse);
    assertNotNull(listOfImages);
    assertEquals(serviceToFind.getId(), serviceResponse.getId());
    assertEquals(serviceToFind.getCategoryId(), serviceResponse.getCategoryId());
    assertEquals(serviceToFind.getOwnerId(), serviceResponse.getOwnerId());
    assertEquals(serviceToFind.getTitle(), serviceResponse.getTitle());
    assertEquals(serviceToFind.getDescription(), serviceResponse.getDescription());
    assertEquals(serviceToFind.getImageUrls().get(0), listOfImages.get(0));
    assertEquals(serviceToFind.getPriority(), serviceResponse.getPriority());
    assertEquals(serviceToFind.getCreatedAt(), serviceResponse.getCreatedAt());
    assertEquals(serviceToFind.getLastModifiedAt(), serviceResponse.getLastModifiedAt());

    assertThat(serviceToFind.getPrice()).isEqualByComparingTo(serviceResponse.getPrice());
    assertTrue(serviceResponse.hasLinks());
  }

  @Test
  void shouldThrowNoSuchServiceByIdExceptionWhenGetByNonExistingId() {
    // given
    when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

    // when && then
    assertThrows(NoSuchServiceByIdException.class, () -> serviceService.getService(1L));
  }

  @Test
  void shouldReturnPageOfServicesWhenGettingAllServices() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Service serviceToFind = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, timeOfCreation, timeOfModification
    );
    Pageable pageable = PageRequest.of(0, 2);
    Specification<Service> specification = Specification.where(null);

    when(serviceRepository.findAll(specification, pageable)).thenReturn(
        new PageImpl<>(List.of(serviceToFind), pageable, 1));

    // when
    var responsePage = serviceService.getAllServices(pageable, null, null);
    var serviceResponse = responsePage.getContent().iterator().next();

    // then
    assertNotNull(responsePage);
    assertNotNull(serviceResponse);

    assertEquals(1, responsePage.getTotalElements());
    assertEquals(1, responsePage.getContent().size());

    assertEquals(serviceToFind.getId(), serviceResponse.getId());
    assertEquals(serviceToFind.getCategoryId(), serviceResponse.getCategoryId());
    assertEquals(serviceToFind.getOwnerId(), serviceResponse.getOwnerId());
    assertEquals(serviceToFind.getTitle(), serviceResponse.getTitle());
    assertEquals(serviceToFind.getDescription(), serviceResponse.getDescription());
    assertEquals(serviceToFind.getImageUrls().get(0), serviceResponse.getImageUrls().get(0));
    assertEquals(serviceToFind.getPriority(), serviceResponse.getPriority());
    assertEquals(serviceToFind.getCreatedAt(), serviceResponse.getCreatedAt());
    assertEquals(serviceToFind.getLastModifiedAt(), serviceResponse.getLastModifiedAt());

    assertThat(serviceToFind.getPrice()).isEqualByComparingTo(serviceResponse.getPrice());
    assertTrue(serviceResponse.hasLinks());
  }

  @Test
  void shouldReturnPageOfServicesWhenGettingAllServicesWhenCategoryIdNotNull() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Service serviceToFind = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, timeOfCreation, timeOfModification
    );
    Pageable pageable = PageRequest.of(0, 2);

    when(serviceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(
        new PageImpl<>(List.of(serviceToFind), pageable, 1));

    // when
    var responsePage = serviceService.getAllServices(pageable, 1L, null);
    var serviceResponse = responsePage.getContent().iterator().next();

    // then
    assertNotNull(responsePage);
    assertNotNull(serviceResponse);

    assertEquals(1, responsePage.getTotalElements());
    assertEquals(1, responsePage.getContent().size());

    assertEquals(serviceToFind.getId(), serviceResponse.getId());
    assertEquals(serviceToFind.getCategoryId(), serviceResponse.getCategoryId());
    assertEquals(serviceToFind.getOwnerId(), serviceResponse.getOwnerId());
    assertEquals(serviceToFind.getTitle(), serviceResponse.getTitle());
    assertEquals(serviceToFind.getDescription(), serviceResponse.getDescription());
    assertEquals(serviceToFind.getImageUrls().get(0), serviceResponse.getImageUrls().get(0));
    assertEquals(serviceToFind.getPriority(), serviceResponse.getPriority());
    assertEquals(serviceToFind.getCreatedAt(), serviceResponse.getCreatedAt());
    assertEquals(serviceToFind.getLastModifiedAt(), serviceResponse.getLastModifiedAt());

    assertThat(serviceToFind.getPrice()).isEqualByComparingTo(serviceResponse.getPrice());
    assertTrue(serviceResponse.hasLinks());
  }

  @Test
  void shouldReturnPageOfServicesWhenGettingAllServicesWhenServiceIdsNotNull() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Service serviceToFind = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, timeOfCreation, timeOfModification
    );
    Pageable pageable = PageRequest.of(0, 2);

    when(serviceRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(
        new PageImpl<>(List.of(serviceToFind), pageable, 1));

    // when
    var responsePage = serviceService.getAllServices(pageable, null, List.of(1L));
    var serviceResponse = responsePage.getContent().iterator().next();

    // then
    assertNotNull(responsePage);
    assertNotNull(serviceResponse);

    assertEquals(1, responsePage.getTotalElements());
    assertEquals(1, responsePage.getContent().size());

    assertEquals(serviceToFind.getId(), serviceResponse.getId());
    assertEquals(serviceToFind.getCategoryId(), serviceResponse.getCategoryId());
    assertEquals(serviceToFind.getOwnerId(), serviceResponse.getOwnerId());
    assertEquals(serviceToFind.getTitle(), serviceResponse.getTitle());
    assertEquals(serviceToFind.getDescription(), serviceResponse.getDescription());
    assertEquals(serviceToFind.getImageUrls().get(0), serviceResponse.getImageUrls().get(0));
    assertEquals(serviceToFind.getPriority(), serviceResponse.getPriority());
    assertEquals(serviceToFind.getCreatedAt(), serviceResponse.getCreatedAt());
    assertEquals(serviceToFind.getLastModifiedAt(), serviceResponse.getLastModifiedAt());

    assertThat(serviceToFind.getPrice()).isEqualByComparingTo(serviceResponse.getPrice());
    assertTrue(serviceResponse.hasLinks());
  }

  @Test
  void shouldReturnVoidWhenDeleteService(){
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Service serviceToFind = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, timeOfCreation, timeOfModification
    );

    when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceToFind));

    serviceService.deleteService(1L);

    verify(serviceRepository, times(1)).delete(serviceToFind);
  }

  @Test
  void shouldReturnNoSuchServiceByIdExceptionWhenDeleteServiceByNonExistingId(){
// given
    when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

    // when && then
    assertThrows(NoSuchServiceByIdException.class, () -> serviceService.deleteService(1L));
  }

  @Test
  void shouldUpdateAndReturnServiceResponseWhenChangingDescription(){
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    Service serviceToFind = new Service(
        1L, 1L, UUID.randomUUID(), "title", "description", List.of("https://image.com/1"), BigDecimal.valueOf(100),
        1, timeOfCreation, timeOfModification
    );

    var updateRequest = new ServiceUpdateRequest(null, null, "new description", null, null, null);

    when(serviceRepository.findById(1L)).thenReturn(Optional.of(serviceToFind));
    when(serviceRepository.save(any())).thenAnswer(answer(getFakeSave(1L)));

    // when
    ServiceResponse serviceResponse = serviceService.updateService(1L, updateRequest);

    // then
    assertNotNull(serviceResponse);
    assertEquals(1L, serviceResponse.getId());
    assertEquals(serviceToFind.getCategoryId(), serviceResponse.getCategoryId());
    assertEquals(serviceToFind.getOwnerId(), serviceResponse.getOwnerId());
    assertEquals(serviceToFind.getTitle(), serviceResponse.getTitle());
    assertEquals(updateRequest.getDescription(), serviceResponse.getDescription());
    assertEquals(serviceToFind.getImageUrls().get(0), serviceResponse.getImageUrls().get(0));
    assertEquals(serviceToFind.getPriority(), serviceResponse.getPriority());
    assertEquals(serviceToFind.getCreatedAt(), serviceResponse.getCreatedAt());
    assertEquals(serviceToFind.getLastModifiedAt(), serviceResponse.getLastModifiedAt());

    assertThat(serviceToFind.getPrice()).isEqualByComparingTo(serviceResponse.getPrice());
    assertTrue(serviceResponse.hasLinks());
  }

  @Test
  void shouldReturnServiceCategoryResponseWhenGetByExistingId(){

    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    ServiceCategory serviceCategoryToFind = new ServiceCategory(
      1L, "title", "description", List.of("https://image.com/1"), timeOfCreation, timeOfModification
    );

    when(serviceCategoryRepository.findById(1L)).thenReturn(Optional.of(serviceCategoryToFind));

    // when
    ServiceCategoryResponse serviceCategoryResponse = serviceService.getServiceCategory(1L);
    var listOfImages = serviceCategoryResponse.getImageUrls();

    // then
    assertNotNull(serviceCategoryResponse);
    assertNotNull(listOfImages);
    assertEquals(serviceCategoryToFind.getId(), serviceCategoryResponse.getId());
    assertEquals(serviceCategoryToFind.getTitle(), serviceCategoryResponse.getTitle());
    assertEquals(serviceCategoryToFind.getDescription(), serviceCategoryResponse.getDescription());
    assertEquals(serviceCategoryToFind.getImageUrls().get(0), listOfImages.get(0));
    assertEquals(serviceCategoryToFind.getCreatedAt(), serviceCategoryResponse.getCreatedAt());
    assertEquals(serviceCategoryToFind.getLastModifiedAt(), serviceCategoryResponse.getLastModifiedAt());

    assertTrue(serviceCategoryResponse.hasLinks());
  }

  @Test
  void shouldThrowNoSuchServiceCategoryByIdExceptionWhenGetByNonExistingId() {
    // given
    when(serviceCategoryRepository.findById(1L)).thenReturn(Optional.empty());

    // when && then
    assertThrows(NoSuchServiceCategoryByIdException.class, () -> serviceService.getServiceCategory(1L));
  }

  @Test
  void shouldReturnPageOfServiceCategoriesWhenGettingAllServiceCategories() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    ServiceCategory serviceCategoryToFind = new ServiceCategory(
        1L, "title", "description", List.of("https://image.com/1"), timeOfCreation, timeOfModification
    );
    Pageable pageable = PageRequest.of(0, 2);
    Specification<ServiceCategory> specification = Specification.where(null);

    when(serviceCategoryRepository.findAll(specification, pageable)).thenReturn(
        new PageImpl<>(List.of(serviceCategoryToFind), pageable, 1));

    // when
    var responsePage = serviceService.getAllServiceCategories(pageable, null);
    var serviceCategoryResponse = responsePage.getContent().iterator().next();

    // then
    assertNotNull(responsePage);
    assertNotNull(serviceCategoryResponse);

    assertEquals(1, responsePage.getTotalElements());
    assertEquals(1, responsePage.getContent().size());

    assertEquals(serviceCategoryToFind.getId(), serviceCategoryResponse.getId());
    assertEquals(serviceCategoryToFind.getTitle(), serviceCategoryResponse.getTitle());
    assertEquals(serviceCategoryToFind.getDescription(), serviceCategoryResponse.getDescription());
    assertEquals(serviceCategoryToFind.getImageUrls().get(0), serviceCategoryResponse.getImageUrls().get(0));
    assertEquals(serviceCategoryToFind.getCreatedAt(), serviceCategoryResponse.getCreatedAt());
    assertEquals(serviceCategoryToFind.getLastModifiedAt(), serviceCategoryResponse.getLastModifiedAt());

    assertTrue(serviceCategoryResponse.hasLinks());
  }

  @Test
  void shouldReturnPageOfServiceCategoriesWhenGettingAllServiceCategoriesAndListOfIdsNotEmpty() {
    // given
    Instant timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    Instant timeOfModification = Instant.now().plus(Duration.ofHours(20));

    ServiceCategory serviceCategoryToFind = new ServiceCategory(
        1L, "title", "description", List.of("https://image.com/1"), timeOfCreation, timeOfModification
    );
    Pageable pageable = PageRequest.of(0, 2);

    when(serviceCategoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(
        new PageImpl<>(List.of(serviceCategoryToFind), pageable, 1));

    // when
    var responsePage = serviceService.getAllServiceCategories(pageable, List.of(1L));
    var serviceCategoryResponse = responsePage.getContent().iterator().next();

    // then
    assertNotNull(responsePage);
    assertNotNull(serviceCategoryResponse);

    assertEquals(1, responsePage.getTotalElements());
    assertEquals(1, responsePage.getContent().size());

    assertEquals(serviceCategoryToFind.getId(), serviceCategoryResponse.getId());
    assertEquals(serviceCategoryToFind.getTitle(), serviceCategoryResponse.getTitle());
    assertEquals(serviceCategoryToFind.getDescription(), serviceCategoryResponse.getDescription());
    assertEquals(serviceCategoryToFind.getImageUrls().get(0), serviceCategoryResponse.getImageUrls().get(0));
    assertEquals(serviceCategoryToFind.getCreatedAt(), serviceCategoryResponse.getCreatedAt());
    assertEquals(serviceCategoryToFind.getLastModifiedAt(), serviceCategoryResponse.getLastModifiedAt());

    assertTrue(serviceCategoryResponse.hasLinks());
  }

  private Answer1<Service, Service> getFakeSave(long id) {
    return service -> {
      service.setId(id);
      return service;
    };
  }
}
