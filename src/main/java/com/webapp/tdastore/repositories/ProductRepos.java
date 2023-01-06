package com.webapp.tdastore.repositories;

import com.webapp.tdastore.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepos extends JpaRepository<Product, Long> {
}
