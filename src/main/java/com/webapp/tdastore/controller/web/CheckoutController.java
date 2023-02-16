package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.entities.CartItems;
import com.webapp.tdastore.entities.ItemShoppingCart;
import com.webapp.tdastore.entities.ShoppingCart;
import com.webapp.tdastore.entities.User;
import com.webapp.tdastore.security.CustomUserDetails;
import com.webapp.tdastore.services.ItemShoppingCartService;
import com.webapp.tdastore.services.ProductService;
import com.webapp.tdastore.services.ShippingService;
import com.webapp.tdastore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dat-hang")
public class CheckoutController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ItemShoppingCartService dbCartService;
    @Autowired
    private ShoppingCart sessionCart;
    @Autowired
    private ShippingService shippingService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCheckoutPage(ModelMap modelMap) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            if (sessionCart.getSizeCart() > 0) {
                List<CartItems> items = new ArrayList<>(sessionCart.getCartItems());
                modelMap.addAttribute("items", items);
                modelMap.addAttribute("amount", sessionCart.getDiscountValue());
            } else
                return "redirect:/gio-hang";
        } else {
            long userId = ((CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
            if (dbCartService.size(userId) > 0) {
                //find user
                User us = userService.findById(userId);
                //data address of user
                String address = us.getFirstname() + " " + us.getLastname() + ";" + us.getEmail() + ";" + us.getPhone()
                        + ";" + us.getAddress().get(0).getAPIName();
                //get list item in cart storage from database
                List<ItemShoppingCart> items = dbCartService.findAllByUserId(userId);
                //calculate shipping by address default of user
                int cost_shipping = shippingService.getPriceFromUserAddress(us.getAddress().get(0).getProvince(),
                        us.getAddress().get(0).getDistrict());
                modelMap.addAttribute("shipping", cost_shipping);
                modelMap.addAttribute("address", address);
                modelMap.addAttribute("items", items);
                modelMap.addAttribute("amount", dbCartService.totalAmountHasDiscount(userId));
            } else
                return "redirect:/gio-hang";
        }
        return "web/page/checkout";
    }
}
