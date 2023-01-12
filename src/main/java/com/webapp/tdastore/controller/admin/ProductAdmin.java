package com.webapp.tdastore.controller.admin;

import com.webapp.tdastore.dto.ProductDTO;
import com.webapp.tdastore.entities.Category;
import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.entities.ProductImage;
import com.webapp.tdastore.services.CategoryServices;
import com.webapp.tdastore.services.ProductImageService;
import com.webapp.tdastore.services.ProductServices;
import com.webapp.tdastore.services.UploadImageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
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
    private ProductImageService productImageService;
    @Autowired
    private UploadImageService uploadService;
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

    @RequestMapping(value = "/them", method = RequestMethod.GET)
    public String getCreateProductPage(ModelMap modelMap) {
        ProductDTO dto = new ProductDTO();
        List<Category> categories = categoryServices.getAll();
        modelMap.addAttribute("categories", categories);
        modelMap.addAttribute("dto", dto);
        return "admin/product-form";
    }

    @RequestMapping(value = "/them", method = RequestMethod.POST)
    public String createNewProduct(@Valid @ModelAttribute ProductDTO dto,
                                   ModelMap modelMap) throws IOException {
        Product product = modelMapper.map(dto, Product.class);
        if (dto.getImageFile().length > 0) {
            List<ProductImage> images = new ArrayList<>();
            //upload multi image
            for (MultipartFile file : dto.getImageFile()) {
                String filename = imageService.uploadFile(file);
                //create new obj and set props
                ProductImage i = new ProductImage();
                i.setUrlImage(filename);
                i.setUploadDate(new Date(new java.util.Date().getTime()));
                i.setProduct(product);
                images.add(i);
            }
            product.setImages(images);
        }
        //save product into db
        productServices.insert(product);
        return "redirect:/admin/san-pham";

    }

}
