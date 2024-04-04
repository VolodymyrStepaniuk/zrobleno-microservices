package com.stepaniuk.service;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "services")
public class Service {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "services_id_gen")
  @SequenceGenerator(name = "services_id_gen", sequenceName = "services_id_seq", allocationSize = 1)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "category_id", nullable = false)
  private Long categoryId;

  @Column(name = "owner_id",columnDefinition = "uuid", nullable = false)
  private UUID ownerId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false, columnDefinition = "text")
  private String description;

  @Type(ListArrayType.class)
  @Column(name = "image_urls", columnDefinition = "text[]", nullable = false)
  private List<String> imageUrls;

  @Column(name = "price", nullable = true)
  private BigDecimal price;

  @Column(name = "priority", nullable = false)
  private Integer priority;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private Instant createdAt;

  @Column(name = "last_modified_at", nullable = false)
  @LastModifiedDate
  private Instant lastModifiedAt;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass = o instanceof HibernateProxy
        ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
        : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    Service service = (Service) o;
    return getId() != null && Objects.equals(getId(), service.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
        .getPersistentClass().hashCode() : getClass().hashCode();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" +
        "id = " + id + ", " +
        "categoryId = " + categoryId + ", " +
        "ownerId = " + ownerId + ", " +
        "title = " + title + ", " +
        "description = " + description + ", " +
        "imageUrls = " + imageUrls + ", " +
        "price = " + price + ", " +
        "priority = " + priority + ", " +
        "createdAt = " + createdAt + ", " +
        "lastModifiedAt = " + lastModifiedAt + ")";
  }
}
