package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.entities.Favourite;
import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.entities.User;
import com.webapp.tdastore.security.CustomUserDetails;
import com.webapp.tdastore.services.FavouriteService;
import com.webapp.tdastore.services.ProductService;
import com.webapp.tdastore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/yeu-thich")
public class FavouriteController {
    @Autowired
    private FavouriteService favouriteService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getFavouritePage(ModelMap modelMap) {
        List<Favourite> favourites = new ArrayList<>();


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Redirect to login if guest request
        if (authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/dang-nhap";
        }
        //Retrive user from spring security
        User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        if (user != null) {
            favourites = favouriteService.findAllByUser(user);
        }
        modelMap.addAttribute("items", favourites);
        return "web/page/favourite-page";
    }

    @RequestMapping(value = "/them", method = RequestMethod.POST)
    public ResponseEntity insertFavourite(@RequestParam String productCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            Product product = productService.findProductByCode(productCode);
            if (user != null && product != null) {
                Favourite favourite = new Favourite();
                favourite.setUser(user);
                favourite.setProduct(product);
                //insert product into favourite list of user
                favouriteService.insert(favourite);
                return ResponseEntity.status(HttpStatus.OK)
                        .body("insert new product into favourite success");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Product Code Or Email not exist");
    }

}
