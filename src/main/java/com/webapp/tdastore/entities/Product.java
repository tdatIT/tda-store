package com.webapp.tdastore.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Table(name = "product")
@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;

    @Column
    private String productCode;

    @Column
    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private int quantity;

    @Column
    private boolean status;

    @Column
    private double price;

    @Column
    private double promotionPrice;

    @Column
    private Timestamp createDate;

    @Column
    private Timestamp updateDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<ProductType> productType;

    @OneToMany(mappedBy = "product")
    private Set<OrderItems> items;

    @OneToMany(mappedBy = "product")
    private List<Comment> comments;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images;
}
