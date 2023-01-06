package com.webapp.tdastore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Table
@Entity
@Getter
@Setter
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "TEXT")
    private String urlImage;

    @Column
    private Date uploadDate;

    @ManyToOne
    @JoinColumn(name =  "product_id")
    private Product product;
}
