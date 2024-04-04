package com.stepaniuk.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.stepaniuk.service.testspecific.ControllerLevelUnitTest;
import com.stepaniuk.zrobleno.payload.service.ServiceCreateRequest;
import com.stepaniuk.zrobleno.payload.service.ServiceResponse;
import com.stepaniuk.zrobleno.payload.service.ServiceUpdateRequest;
import com.stepaniuk.zrobleno.payload.service.category.ServiceCategoryResponse;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceByIdException;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceCategoryByIdException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.Link;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.stepaniuk.zrobleno.testspecific.hamcrest.TemporalStringMatchers.instantComparesEqualTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerLevelUnitTest(controllers = ServiceController.class)
@WithMockUser(username = "user", roles = "USER")
class ServiceControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ServiceService serviceService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldReturnServiceResponseWhenCreatingService() throws Exception {
    var ownerId = UUID.randomUUID();
    var timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    var timeOfModification = Instant.now().plus(Duration.ofHours(20));

    var request = new ServiceCreateRequest(
        1L,
        ownerId,
        "title",
        "description",
        1,
        List.of("https://example.com/image.jpg"),
        BigDecimal.valueOf(100)
    );

    var response = new ServiceResponse(
        1L,
        1L,
        ownerId,
        "title",
        "description",
        List.of("https://example.com/image.jpg"),
        BigDecimal.valueOf(100),
        1,
        timeOfCreation,
        timeOfModification
    );

    response.add(Link.of("http://localhost/services/1", "self"));

    when(serviceService.createService(eq(request))).thenReturn(response);

    // when && then
    // @formatter:off
    mockMvc.perform(post("/services")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.categoryId", is(response.getCategoryId()), Long.class))
        .andExpect(jsonPath("$.ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.title", is(response.getTitle())))
        .andExpect(jsonPath("$.description", is(response.getDescription())))
        .andExpect(jsonPath("$.imageUrls", is(response.getImageUrls())))
        .andExpect(jsonPath("$.price", is(response.getPrice()), BigDecimal.class))
        .andExpect(jsonPath("$.priority", is(response.getPriority())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnServiceResponseWhenGettingServiceById() throws Exception {
    var ownerId = UUID.randomUUID();
    var timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    var timeOfModification = Instant.now().plus(Duration.ofHours(20));

    var response = new ServiceResponse(
        1L,
        1L,
        ownerId,
        "title",
        "description",
        List.of("https://example.com/image.jpg"),
        BigDecimal.valueOf(100),
        1,
        timeOfCreation,
        timeOfModification
    );

    response.add(Link.of("http://localhost/services/1", "self"));

    when(serviceService.getService(1L)).thenReturn(response);

    // when && then
    // @formatter:off
    mockMvc.perform(get("/services/1")
            .contentType("application/json")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.categoryId", is(response.getCategoryId()), Long.class))
        .andExpect(jsonPath("$.ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.title", is(response.getTitle())))
        .andExpect(jsonPath("$.description", is(response.getDescription())))
        .andExpect(jsonPath("$.imageUrls", is(response.getImageUrls())))
        .andExpect(jsonPath("$.price", is(response.getPrice()), BigDecimal.class))
        .andExpect(jsonPath("$.priority", is(response.getPriority())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnErrorWhenGettingServiceByIdAndServiceDoesNotExist() throws Exception {
    when(serviceService.getService(1L)).thenThrow(new NoSuchServiceByIdException(1L));

    // when && then
    // @formatter:off
    mockMvc.perform(get("/services/1")
            .contentType("application/json")
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.title", is("No such service")))
        .andExpect(jsonPath("$.detail", is("No service with id 1 found")))
        .andExpect(jsonPath("$.instance", is("/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnNoContentWhenDeletingService() throws Exception {
    // @formatter:off
    mockMvc.perform(delete("/services/1")
            .contentType("application/json")
        )
        .andExpect(status().isNoContent());
    // @formatter:on
  }

  @Test
  void shouldReturnServiceResponseWhenUpdatingTitleOfService() throws Exception {
    var ownerId = UUID.randomUUID();
    var timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    var timeOfModification = Instant.now().plus(Duration.ofHours(20));

    var response = new ServiceResponse(
        1L,
        1L,
        ownerId,
        "new title",
        "description",
        List.of("https://example.com/image.jpg"),
        BigDecimal.valueOf(100),
        1,
        timeOfCreation,
        timeOfModification
    );

    response.add(Link.of("http://localhost/services/1", "self"));

    var request = new ServiceUpdateRequest(null, "new title", null, null, null, null);

    when(serviceService.updateService(1L, request)).thenReturn(response);

    // when && then
    // @formatter:off
    mockMvc.perform(patch("/services/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.categoryId", is(response.getCategoryId()), Long.class))
        .andExpect(jsonPath("$.ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.title", is(request.getTitle())))
        .andExpect(jsonPath("$.description", is(response.getDescription())))
        .andExpect(jsonPath("$.imageUrls", is(response.getImageUrls())))
        .andExpect(jsonPath("$.price", is(response.getPrice()), BigDecimal.class))
        .andExpect(jsonPath("$.priority", is(response.getPriority())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnErrorWhenChangingTitleOfNonExistingService() throws Exception {
    var request = new ServiceUpdateRequest(null, "new title", null, null, null, null);
    when(serviceService.updateService(1L, request)).thenThrow(new NoSuchServiceByIdException(1L));
    // @formatter:off
    mockMvc.perform(patch("/services/1")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.title", is("No such service")))
        .andExpect(jsonPath("$.detail", is("No service with id 1 found")))
        .andExpect(jsonPath("$.instance", is("/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnPageOfServicesWhenGettingAllServices() throws Exception {
    var ownerId = UUID.randomUUID();
    var timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    var timeOfModification = Instant.now().plus(Duration.ofHours(20));

    var response = new ServiceResponse(
        1L,
        1L,
        ownerId,
        "title",
        "description",
        List.of("https://example.com/image.jpg"),
        BigDecimal.valueOf(100),
        1,
        timeOfCreation,
        timeOfModification
    );

    response.add(Link.of("http://localhost/services/1","self"));

    var pageable = PageRequest.of(0, 2);
    when(serviceService.getAllServices(pageable, null, null)).thenReturn(
        new PageImpl<>(List.of(response), pageable, 1));
    // when && then
    // @formatter:off
    mockMvc.perform(get("/services/v1")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.content[0].id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.content[0].categoryId", is(response.getCategoryId()), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.content[0].title", is(response.getTitle())))
        .andExpect(jsonPath("$.content[0].description", is(response.getDescription())))
        .andExpect(jsonPath("$.content[0].imageUrls", is(response.getImageUrls())))
        .andExpect(jsonPath("$.content[0].price", is(response.getPrice()), BigDecimal.class))
        .andExpect(jsonPath("$.content[0].priority", is(response.getPriority())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnPageOfServicesWhenGettingAllServicesAndCategoryIdIsNotNull() throws Exception {
    var ownerId = UUID.randomUUID();
    var timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    var timeOfModification = Instant.now().plus(Duration.ofHours(20));
    long categoryId = 1L;

    var response = new ServiceResponse(
        1L,
        categoryId,
        ownerId,
        "title",
        "description",
        List.of("https://example.com/image.jpg"),
        BigDecimal.valueOf(100),
        1,
        timeOfCreation,
        timeOfModification
    );

    response.add(Link.of("http://localhost/services/1"));

    var pageable = PageRequest.of(0, 2);
    when(serviceService.getAllServices(pageable, categoryId, null)).thenReturn(
        new PageImpl<>(List.of(response), pageable, 1));
    // when && then
    // @formatter:off
    mockMvc.perform(get("/services/v1")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
            .param("categoryId", String.valueOf(categoryId))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.content[0].id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.content[0].categoryId", is(categoryId), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.content[0].title", is(response.getTitle())))
        .andExpect(jsonPath("$.content[0].description", is(response.getDescription())))
        .andExpect(jsonPath("$.content[0].imageUrls", is(response.getImageUrls())))
        .andExpect(jsonPath("$.content[0].price", is(response.getPrice()), BigDecimal.class))
        .andExpect(jsonPath("$.content[0].priority", is(response.getPriority())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnPageOfServicesWhenGettingAllServicesAndServiceIdsIsNotNull() throws Exception {
    var ownerId = UUID.randomUUID();
    var timeOfCreation = Instant.now().plus(Duration.ofHours(10));
    var timeOfModification = Instant.now().plus(Duration.ofHours(20));
    var categoryId = 1L;
    var listOfIds = List.of(1L);

    var response = new ServiceResponse(
        1L,
        categoryId,
        ownerId,
        "title",
        "description",
        List.of("https://example.com/image.jpg"),
        BigDecimal.valueOf(100),
        1,
        timeOfCreation,
        timeOfModification
    );

    response.add(Link.of("http://localhost/services/1"));

    var pageable = PageRequest.of(0, 2);
    when(serviceService.getAllServices(pageable, null, listOfIds)).thenReturn(
        new PageImpl<>(List.of(response), pageable, 1));
    // when && then
    // @formatter:off
    mockMvc.perform(get("/services/v2")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
            .param("serviceIds", "1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.content[0].id", is(listOfIds.get(0)), Long.class))
        .andExpect(jsonPath("$.content[0].categoryId", is(response.getCategoryId()), Long.class))
        .andExpect(jsonPath("$.content[0].ownerId", is(response.getOwnerId().toString())))
        .andExpect(jsonPath("$.content[0].title", is(response.getTitle())))
        .andExpect(jsonPath("$.content[0].description", is(response.getDescription())))
        .andExpect(jsonPath("$.content[0].imageUrls", is(response.getImageUrls())))
        .andExpect(jsonPath("$.content[0].price", is(response.getPrice()), BigDecimal.class))
        .andExpect(jsonPath("$.content[0].priority", is(response.getPriority())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/services/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnServiceCategoryResponseWhenGettingServiceCategoryById() throws Exception {
    var response = new ServiceCategoryResponse(1L, "NameOfCategory", "description",
        List.of("https://example.com/image.jpg"), Instant.now(), Instant.now());

    response.add(Link.of("http://localhost/services/categories/1", "self"));

    when(serviceService.getServiceCategory(1L)).thenReturn(response);
    // when && then
    // @formatter:off
    mockMvc.perform(get("/services/categories/1")
            .contentType("application/json")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
        .andExpect(jsonPath("$.title", is(response.getTitle())))
        .andExpect(jsonPath("$.description", is(response.getDescription())))
        .andExpect(jsonPath("$.imageUrls", is(response.getImageUrls())))
        .andExpect(jsonPath("$.createdAt", instantComparesEqualTo(response.getCreatedAt())))
        .andExpect(jsonPath("$.lastModifiedAt", instantComparesEqualTo(response.getLastModifiedAt())))
        .andExpect(jsonPath("$._links.self.href", is("http://localhost/services/categories/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnErrorWhenGettingServiceCategoryByIdAndCategoryDoesNotExist() throws Exception {
    when(serviceService.getServiceCategory(1L)).thenThrow(new NoSuchServiceCategoryByIdException(1L));
    // when && then
    // @formatter:off
    mockMvc.perform(get("/services/categories/1")
            .contentType("application/json")
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status", is(404)))
        .andExpect(jsonPath("$.title", is("No such service category")))
        .andExpect(jsonPath("$.detail", is("No service category with id 1 found")))
        .andExpect(jsonPath("$.instance", is("/services/categories/1")));
    // @formatter:on
  }

  @Test
  void shouldReturnPageOfServiceCategoriesWhenGettingAllServiceCategories() throws Exception {
    var categoryResponse = new ServiceCategoryResponse(1L, "NameOfCategory", "description",
        List.of("https://example.com/image.jpg"), Instant.now(), Instant.now());
    var pageable = PageRequest.of(0, 2);

    categoryResponse.add(Link.of("http://localhost/services/categories/1", "self"));

    when(serviceService.getAllServiceCategories(pageable, null)).thenReturn(
        new PageImpl<>(List.of(categoryResponse), pageable, 1));

    mockMvc.perform(
        get("/services/v1/categories")
            .contentType("application/json")
            .param("page", "0")
            .param("size", "2")
    )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.content[0].id", is(categoryResponse.getId()), Long.class))
        .andExpect(jsonPath("$.content[0].title", is(categoryResponse.getTitle())))
        .andExpect(jsonPath("$.content[0].description", is(categoryResponse.getDescription())))
        .andExpect(jsonPath("$.content[0].imageUrls", is(categoryResponse.getImageUrls())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(categoryResponse.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(categoryResponse.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/services/categories/1")));
  }

  @Test
  void shouldReturnPageOfServiceCategoriesWhenGettingAllServiceCategoriesAndIdsIsNotNull() throws Exception {
    var categoryResponse = new ServiceCategoryResponse(1L, "NameOfCategory", "description",
        List.of("https://example.com/image.jpg"), Instant.now(), Instant.now());
    var pageable = PageRequest.of(0, 2);
    var listOfIds = List.of(1L);

    categoryResponse.add(Link.of("http://localhost/services/categories/1", "self"));

    when(serviceService.getAllServiceCategories(pageable, listOfIds)).thenReturn(
        new PageImpl<>(List.of(categoryResponse), pageable, 1));

    mockMvc.perform(
            get("/services/v2/categories")
                .contentType("application/json")
                .param("page", "0")
                .param("size", "2")
                .param("ids", "1")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.content[0].id", is(listOfIds.get(0)), Long.class))
        .andExpect(jsonPath("$.content[0].title", is(categoryResponse.getTitle())))
        .andExpect(jsonPath("$.content[0].description", is(categoryResponse.getDescription())))
        .andExpect(jsonPath("$.content[0].imageUrls", is(categoryResponse.getImageUrls())))
        .andExpect(jsonPath("$.content[0].createdAt", instantComparesEqualTo(categoryResponse.getCreatedAt())))
        .andExpect(jsonPath("$.content[0].lastModifiedAt", instantComparesEqualTo(categoryResponse.getLastModifiedAt())))
        .andExpect(jsonPath("$.content[0].links[0].href", is("http://localhost/services/categories/1")));
  }
}
