package com.stepaniuk.service;


import com.stepaniuk.service.category.ServiceCategory;
import com.stepaniuk.service.category.ServiceCategoryMapper;
import com.stepaniuk.service.category.ServiceCategoryRepository;
import com.stepaniuk.zrobleno.payload.service.ServiceCreateRequest;
import com.stepaniuk.zrobleno.payload.service.ServiceResponse;
import com.stepaniuk.zrobleno.payload.service.ServiceUpdateRequest;
import com.stepaniuk.zrobleno.payload.service.category.ServiceCategoryResponse;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceByIdException;
import com.stepaniuk.zrobleno.types.exception.service.NoSuchServiceCategoryByIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

  private final ServiceRepository serviceRepository;
  private final ServiceCategoryRepository serviceCategoryRepository;
  private final ServiceMapper serviceMapper;
  private final ServiceCategoryMapper serviceCategoryMapper;

  public ServiceResponse createService(ServiceCreateRequest request) {
    Service service = new Service();

    service.setCategoryId(
        serviceCategoryRepository.findById(request.getCategoryId()).orElseThrow(
            () -> new NoSuchServiceCategoryByIdException(request.getCategoryId())
        ).getId());
    service.setOwnerId(request.getOwnerId());
    service.setTitle(request.getTitle());
    service.setDescription(request.getDescription());
    service.setPriority(request.getPriority());
    service.setImageUrls(request.getImageUrls());
    service.setPrice(request.getPrice());

    var savedService = serviceRepository.save(service);

    return serviceMapper.toResponse(savedService);
  }

  public ServiceResponse getService(Long id) {
    return serviceMapper.toResponse(serviceRepository.findById(id).orElseThrow(
        () -> new NoSuchServiceByIdException(id)
    ));
  }

  public Page<ServiceResponse> getAllServices(Pageable pageable, @Nullable Long categoryId,
      @Nullable List<Long> serviceIds) {
    Specification<Service> specification = Specification.where(null);

    if (categoryId != null) {
      specification.and((root, query, criteriaBuilder) -> criteriaBuilder
          .equal(root.get("categoryId"), categoryId)
      );
    }

    if (serviceIds != null && !serviceIds.isEmpty()) {
      specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
          .in(root.get("id")).value(serviceIds)
      );
    }

    return serviceRepository.findAll(specification, pageable).map(serviceMapper::toResponse);
  }

  public void deleteService(Long id) {
    var service = serviceRepository.findById(id).orElseThrow(
        () -> new NoSuchServiceByIdException(id)
    );

    serviceRepository.delete(service);
  }

  public ServiceResponse updateService(Long id, ServiceUpdateRequest request) {
    Service service = serviceRepository.findById(id).orElseThrow(
        () -> new NoSuchServiceByIdException(id)
    );

    if (request.getCategoryId() != null) {
      service.setCategoryId(
          serviceCategoryRepository.findById(request.getCategoryId()).orElseThrow(
              () -> new NoSuchServiceCategoryByIdException(request.getCategoryId())
          ).getId());
    }

    if (request.getTitle() != null) {
      service.setTitle(request.getTitle());
    }

    if (request.getDescription() != null) {
      service.setDescription(request.getDescription());
    }

    if (request.getPriority() != null) {
      service.setPriority(request.getPriority());
    }
    if (request.getImageUrls() != null) {
      service.setImageUrls(request.getImageUrls());
    }
    if (request.getPrice() != null) {
      service.setPrice(request.getPrice());
    }

    var updatedService = serviceRepository.save(service);

    return serviceMapper.toResponse(updatedService);
  }

  public ServiceCategoryResponse getServiceCategory(Long id) {
    return serviceCategoryMapper.toResponse(serviceCategoryRepository.findById(id).orElseThrow(
        () -> new NoSuchServiceCategoryByIdException(id)
    ));
  }

  public Page<ServiceCategoryResponse> getAllServiceCategories(Pageable pageable,
      @Nullable List<Long> serviceCategoryIds) {
    Specification<ServiceCategory> specification = Specification.where(null);

    if (serviceCategoryIds != null && !serviceCategoryIds.isEmpty()) {
      specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder
          .in(root.get("id")).value(serviceCategoryIds)
      );
    }

    return serviceCategoryRepository.findAll(specification, pageable)
        .map(serviceCategoryMapper::toResponse);
  }
}
