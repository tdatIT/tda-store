package com.webapp.tdastore.repositories;

import com.webapp.tdastore.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepos extends JpaRepository<Category, Long> {
}
