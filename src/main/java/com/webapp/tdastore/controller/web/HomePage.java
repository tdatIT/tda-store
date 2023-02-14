package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class HomePage {
    @Autowired
    ProductService productService;

    @RequestMapping(value = {"", "/trang-chu"}, method = RequestMethod.GET)
    public String getHomePage(ModelMap modelMap) {
        List<Product> new_products_6 = productService.findNewProduct(0, 8);
        List<Product> new_products_3 = productService.findNewProduct(0, 3);
        List<Product> hot_products_3 = productService.findHotProduct(0, 3);
        List<Product> trend_products_3 = productService.findHotTrend(0, 3);
        modelMap.addAttribute("new_products_6", new_products_6);
        modelMap.addAttribute("new_products_3", new_products_3);
        modelMap.addAttribute("hot_products_3", hot_products_3);
        modelMap.addAttribute("trend_products_3", trend_products_3);
        return "web/page/index";
    }
}
