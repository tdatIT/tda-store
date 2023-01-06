package com.webapp.tdastore.repositories;

import com.webapp.tdastore.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepos extends JpaRepository<ProductImage,Long> {
}
