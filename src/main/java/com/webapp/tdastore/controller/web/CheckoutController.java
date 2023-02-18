package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.dto.OrderDTO;
import com.webapp.tdastore.dto.UserDTO;
import com.webapp.tdastore.entities.*;
import com.webapp.tdastore.exception.OrderException;
import com.webapp.tdastore.payload.OrderUserDTO;
import com.webapp.tdastore.repositories.UserAddressRepo;
import com.webapp.tdastore.security.CustomUserDetails;
import com.webapp.tdastore.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
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
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserAddressRepo addressRepo;

    private Authentication auth;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getCheckoutPage(ModelMap modelMap) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            if (sessionCart.getSizeCart() > 0) {
                UserDTO userDTO = new UserDTO();
                List<CartItems> items = new ArrayList<>(sessionCart.getCartItems());
                modelMap.addAttribute("items", items);
                modelMap.addAttribute("amount", sessionCart.getDiscountValue());
                modelMap.addAttribute("dto", userDTO);
                modelMap.addAttribute("action_postfix", "guest");
            } else
                return "redirect:/gio-hang";
        } else {
            long userId = ((CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
            if (dbCartService.size(userId) > 0) {
                //find user
                User us = userService.findById(userId);
                //data address of user
                String address_detail = us.getFirstname() + " " + us.getLastname() + ";" + us.getPhone() + ";";

                //get list item in cart storage from database
                List<ItemShoppingCart> items = dbCartService.findAllByUserId(userId);
                //calculate shipping by address default of user
                int cost_shipping = shippingService.getPriceFromUserAddress(us.getAddress().get(0).getProvince(),
                        us.getAddress().get(0).getDistrict());
                //Add attr
                modelMap.addAttribute("shipping", cost_shipping);
                modelMap.addAttribute("address_list", us.getAddress());
                modelMap.addAttribute("items", items);
                modelMap.addAttribute("dto", new OrderUserDTO());
                modelMap.addAttribute("amount", dbCartService.totalAmountHasDiscount(userId));
                modelMap.addAttribute("action_postfix", "auth");
            } else
                return "redirect:/gio-hang";
        }
        return "web/page/checkout";
    }

    @RequestMapping(value = "/hoan-tat/guest", method = RequestMethod.POST)

    public String confirmOrderForGuest(@Valid @ModelAttribute OrderDTO dto,
                                       BindingResult bindingResult) throws OrderException {
        if (bindingResult.hasErrors()) {
            return "redirect:/dat-hang?error=true";
        }
        if (sessionCart.getSizeCart() > 0) {
            orderService.insertOrderForGuest(dto);
            return "web/auth/order-success";
        }
        return "redirect:/dat-hang?error=true";
    }

    @RequestMapping(value = "/hoan-tat/auth", method = RequestMethod.POST)
    public String confirmOrderForUser(@RequestParam Long addressId,
                                      @RequestParam(required = false) Boolean paypal) throws OrderException {
        UserAddress address = addressRepo.findById(addressId).orElseThrow();
        auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.findByEmail(email);
        if (dbCartService.size(user.getUserId()) < 0)
            return "redirect:/gio-hang";
        if (address != null) {
            orderService.insertOrderForUser(user, address);
        } else {
            return "redirect:/dat-hang?error=true";
        }
        return "web/auth/order-success";
    }
}
