package com.webapp.tdastore.controller.admin;

import com.webapp.tdastore.entities.Category;
import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.services.CategoryServices;
import com.webapp.tdastore.services.ProductServices;
import com.webapp.tdastore.services.UploadImageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/san-pham")
public class ProductAdmin {
    @Autowired
    private ProductServices productServices;
    @Autowired
    private CategoryServices categoryServices;
    @Autowired
    private UploadImageService imageService;
    @Autowired
    private ModelMapper modelMapper;

    static final int NO_PRODUCT = 10;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getAllProduct(@RequestParam(value = "status", required = false) Integer status,
                                @RequestParam(value = "doanh-muc", required = false) Long categoryId,
                                @RequestParam(value = "page", required = false) Integer page,
                                ModelMap modelMap) {
        //handle data from parameter
        page = page == null ? page = 0 : page--;
        long pageNum = productServices.getCountProduct() / NO_PRODUCT;
        if (pageNum % NO_PRODUCT != 0)
            pageNum++;
        //Get data from database
        List<Product> products = productServices.findQuery(categoryId, status, page, NO_PRODUCT);
        List<Category> categories = categoryServices.getAll();

        modelMap.addAttribute("queryId", categoryId);
        modelMap.addAttribute("page", page);
        modelMap.addAttribute("pageNum", pageNum);
        modelMap.addAttribute("status", status);
        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("products", products);
        return "admin/product";
    }

    @RequestMapping(value = "/tim-kiem", method = RequestMethod.GET)
    public String searchProduct(@RequestParam("type") int type,
                                @RequestParam("keyword") String keyword,
                                ModelMap modelMap) {
        List<Product> products = type == 1 ?
                productServices.findByKeyword(keyword) :
                productServices.findProductByCode(keyword);
        List<Category> categories = categoryServices.getAll();
        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("products", products);
        modelMap.addAttribute("queryId", 0);
        modelMap.addAttribute("page", 0);
        modelMap.addAttribute("pageNum", 0);
        modelMap.addAttribute("status", 0);
        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("products", products);
        return "admin/product";

    }

}
