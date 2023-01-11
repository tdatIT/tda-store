package com.webapp.tdastore.services.impl;

import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.repositories.ProductRepos;
import com.webapp.tdastore.services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductServices {

    @Autowired
    ProductRepos productRepos;

    @Override
    public List<Product> findAll(int page, int number) {
        return productRepos.findAll(PageRequest.of(page, number)).getContent();
    }

    @Override
    public List<Product> findProductByCode(String code) {
        return productRepos.findByProductCode(code);
    }

    @Override
    public List<Product> findQuery(Long categoryId, Integer status, int page, int number) {
        return productRepos.findProductByQuery(
                categoryId,
                status,
                PageRequest.of(page, number)
        ).getContent();
    }

    @Override
    public List<Product> findByKeyword(String keyword) {
        return productRepos.findByKeyword(keyword);
    }

    @Override
    public Product findById(long id) {
        return productRepos.findById(id).orElseThrow();
    }

    @Override
    public long getCountProduct() {
        return productRepos.count();
    }

    @Override
    @Transactional
    public void insert(Product product) {
        productRepos.save(product);
    }

    @Override
    @Transactional
    public void update(Product product) {
        productRepos.save(product);
    }

    @Override
    @Transactional
    public void disableProduct(Product product) {
        product.setDeleted(true);
        productRepos.save(product);
    }
}
