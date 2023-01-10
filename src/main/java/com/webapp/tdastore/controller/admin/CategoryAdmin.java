package com.webapp.tdastore.controller.admin;

import com.cloudinary.Cloudinary;
import com.webapp.tdastore.dto.CategoryDTO;
import com.webapp.tdastore.entities.Category;
import com.webapp.tdastore.services.CategoryServices;
import com.webapp.tdastore.services.UploadImageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/doanh-muc")
public class CategoryAdmin {
    @Autowired
    private CategoryServices categoryServices;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UploadImageService uploadService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String showListCategory(@RequestParam(value = "page", required = false) String pageParam,
                                   ModelMap modelMap) {
        long total = categoryServices.getTotal();
        long numPage = total / 5;
        numPage = total % 5 != 0 ? numPage++ : numPage;     //increase number page if mod 5 !=0
        int page = pageParam != null ? (Integer.parseInt(pageParam)) : 0;
        if (page != 0)
            page--;//decrease if page != 0

        List<Category> categories = categoryServices.getAllPaging(page);
        modelMap.addAttribute("page", page);
        modelMap.addAttribute("numPage", numPage);
        modelMap.addAttribute("categories", categories);
        return "admin/category";
    }

    @RequestMapping(value = "/them", method = RequestMethod.GET)
    public String getCreatePage(ModelMap modelMap) {
        CategoryDTO dto = new CategoryDTO();
        modelMap.addAttribute("dto", dto);
        return "admin/category-form";
    }

    @RequestMapping(value = "/them", method = RequestMethod.POST)
    public String insertNewCategory(@Valid @ModelAttribute CategoryDTO dto,
                                    ModelMap modelMap) throws Exception {
        Category category = mapper.map(dto, Category.class);
        if (dto.getImage() != null) {
            category.setImages(uploadService.uploadFile(dto.getImage()));
        }
        categoryServices.insert(category);
        return "redirect:/admin/doanh-muc";
    }

    @RequestMapping(value = "chinh-sua", method = RequestMethod.GET)
    public String viewEditCategory(@RequestParam("id") long categoryId,
                                   ModelMap modelMap) {
        Category category = categoryServices.findById(categoryId);
        if (category != null) {
            CategoryDTO dto = mapper.map(category, CategoryDTO.class);
            modelMap.addAttribute("dto", dto);
            return "/admin/category-form";
        } else
            return "/404";
    }

    @RequestMapping(value = "chinh-sua", method = RequestMethod.POST)
    public String editCategory(@Valid @ModelAttribute CategoryDTO dto, ModelMap modelMap) throws Exception {
        Category category = mapper.map(dto, Category.class);
        if (dto.getImage() != null) {
            //Get old image delete and update new image
            Category old = categoryServices.findById(category.getCategoryId());
            if (old.getImages() != null) {
                uploadService.removeFile(old.getImages());
            }
            //upload and set new image for object
            category.setImages(uploadService.uploadFile(dto.getImage()));
        }
        categoryServices.update(category);
        return "redirect:/admin/doanh-muc";
    }
}
