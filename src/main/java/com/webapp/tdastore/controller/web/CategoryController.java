package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.entities.Category;
import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.services.CategoryService;
import com.webapp.tdastore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class CategoryController {
    final String CPU_CATEGORY = "cpu";
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/cpu", method = RequestMethod.GET)
    public String getCpuCategory(ModelMap modelMap) {
        List<Category> categories = categoryService.getAll();
        List<Product> products = productService.findByCategoryCode(CPU_CATEGORY, 0);
        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("products", products);
        return "web/page/cpu-category";
    }
}
