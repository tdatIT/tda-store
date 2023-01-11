package com.webapp.tdastore.services;

import com.webapp.tdastore.entities.Product;

import java.util.List;

public interface ProductServices {
    List<Product> findAll(int page, int number);

    List<Product> findQuery(Long categoryId, Integer status, int page, int number);

    List<Product> findProductByCode(String code);
    List<Product> findByKeyword(String keyword);

    Product findById(long id);

    long getCountProduct();

    void insert(Product product);

    void update(Product product);

    void disableProduct(Product product);
}
