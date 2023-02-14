package com.webapp.tdastore.controller.admin;

import com.webapp.tdastore.dto.ProductDTO;
import com.webapp.tdastore.dto.ProductTypeDTO;
import com.webapp.tdastore.entities.Category;
import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.entities.ProductImage;
import com.webapp.tdastore.entities.ProductType;
import com.webapp.tdastore.services.CategoryService;
import com.webapp.tdastore.services.ProductService;
import com.webapp.tdastore.services.ProductTypeService;
import com.webapp.tdastore.services.UploadImageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/san-pham")
public class ProductAdmin {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UploadImageService imageService;

    @Autowired
    private ProductTypeService productTypeService;

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
        long pageNum = productService.getCountProduct() / NO_PRODUCT;
        if (pageNum % NO_PRODUCT != 0)
            pageNum++;
        //Get data from database
        List<Product> products = productService.findQuery(categoryId, status, page, NO_PRODUCT);
        List<Category> categories = categoryService.getAll();

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
                productService.findByKeyword(keyword) :
                productService.listProductByCode(keyword);
        List<Category> categories = categoryService.getAll();
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
        List<Category> categories = categoryService.getAll();
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
        String code = productService.insert(product);
        return "redirect:/admin/san-pham/" + code;

    }

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    public String viewProductData(@PathVariable String code,
                                  ModelMap modelMap) {
        Product p = productService.findProductByCode(code);
        if (p != null) {
            List<Category> categories = categoryService.getAll();

            ProductDTO dto = modelMapper.map(p, ProductDTO.class);
            modelMap.addAttribute("dto", dto);
            modelMap.addAttribute("view", true);
            modelMap.addAttribute("categories", categories);
            return "admin/product-form";
        } else
            return "404";
    }

    @RequestMapping(value = "/them-style", method = RequestMethod.POST)
    public ResponseEntity addNewStyle(@Valid @ModelAttribute ProductTypeDTO dto) {
        Product p = productService.findProductByCode(dto.getProductCode());
        if (p != null) {
            ProductType type = modelMapper.map(dto, ProductType.class);
            if (dto.getSpecialPrice() == null) {
                dto.setSpecialPrice(0D);
            }
            type.setProduct(p);
            productTypeService.insert(type);
            return ResponseEntity.status(HttpStatus.OK).body("Insert success");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Not found product");
    }

    @RequestMapping(value = "/chinh-sua/{code}", method = RequestMethod.GET)
    public String getUpdateProduct(@PathVariable String code, ModelMap modelMap) {
        Product p = productService.findProductByCode(code);
        if (p != null) {
            ProductDTO dto = modelMapper.map(p, ProductDTO.class);
            List<Category> categories = categoryService.getAll();
            List<ProductType> options = dto.getProductType()
                    .stream().filter(t -> t.isDeleted() != true).collect(Collectors.toList());
            modelMap.addAttribute("options", options);
            modelMap.addAttribute("categories", categories);
            modelMap.addAttribute("dto", dto);
            return "admin/product-update-form";
        }
        return "redirect:/404";
    }

    @RequestMapping(value = "/cap-nhat", method = RequestMethod.POST)
    public String updateProduct(@Valid @ModelAttribute ProductDTO dto,
                                        ModelMap modelMap) {
        Product product = modelMapper.map(dto, Product.class);
        productService.update(product);
        return "redirect:/admin/san-pham/" + product.getProductCode();
    }

    @RequestMapping(value = "/xoa", method = RequestMethod.POST)
    public ResponseEntity deleteProduct(@RequestParam String productCode) {
        Product product = productService.findProductByCode(productCode);
        if (product != null) {
            productService.disableProduct(product);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Delete product success");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Not found product");
    }

    @RequestMapping(value = "/style/xoa", method = RequestMethod.POST)
    public ResponseEntity deleteProductStyle(@RequestParam Long id) {
        ProductType type = productTypeService.findById(id);
        if (type != null) {
            productTypeService.delete(type);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Delete type success");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Not found type");
    }
}
