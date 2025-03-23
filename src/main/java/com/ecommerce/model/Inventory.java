package com.ecommerce.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id")
    private UUID id;

//    @JoinColumn(name = "product_id", insertable = false, updatable = false)
//    @OneToOne(fetch =FetchType.LAZY, cascade = CascadeType.ALL)
//    private Product product;

    @Column(name = "product_id")
    private UUID productId;
    
    @Column(nullable = false)
    @JsonDeserialize(as = Integer.class)
    private Integer stock;

    @Version
    private Integer version; // Optimistic locking

    @Column(name = "created_at")
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private ZonedDateTime updatedAt;
}
