package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.entities.CartItems;
import com.webapp.tdastore.entities.ItemShoppingCart;
import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.entities.ShoppingCart;
import com.webapp.tdastore.payload.UpdateCartResponse;
import com.webapp.tdastore.security.CustomUserDetails;
import com.webapp.tdastore.services.ItemShoppingCartService;
import com.webapp.tdastore.services.ProductService;
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
@RequestMapping(value = "/gio-hang")
public class CartController {
    @Autowired
    private ShoppingCart shoppingCart;
    @Autowired
    private ProductService productService;
    @Autowired
    private ItemShoppingCartService cartDbService;


    private Authentication auth;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getShoppingCartPage(ModelMap modelMap) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            List<CartItems> items = new ArrayList<>(shoppingCart.getCartItems());
            modelMap.addAttribute("items", items);
            modelMap.addAttribute("amount", shoppingCart.getTotalPrice());
            modelMap.addAttribute("total_discount", shoppingCart.getDiscountValue());
        } else {
            long userId = ((CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
            List<ItemShoppingCart> items = cartDbService.findAllByUserId(userId);
            modelMap.addAttribute("items", items);
            modelMap.addAttribute("amount", cartDbService.totalPriceForItem(userId));
            modelMap.addAttribute("total_discount", cartDbService.totalAmountHasDiscount(userId));
        }
        return "web/page/cart-page";
    }

    @RequestMapping(value = "/them", method = RequestMethod.POST)
    public ResponseEntity addToCart(@RequestParam String code,
                                    @RequestParam(required = false) Integer quantity) {
        Product product = productService.findProductByCode(code);
        auth = SecurityContextHolder.getContext().getAuthentication();
        if (quantity == null)
            quantity = 1;
        //check product and quantity not null
        if (product != null && quantity > 0) {
            //handle for anonymous or user login
            if (auth instanceof AnonymousAuthenticationToken) {
                shoppingCart.addToCart(product, quantity);
            } else {
                long userId = ((CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
                cartDbService.insert(code, quantity, userId);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FAIL: Not found product");
        }
        return ResponseEntity.status(HttpStatus.OK).body("SUCCESS: Add " + quantity + " product[" + code + "]");
    }

    @RequestMapping(value = "/cap-nhat", method = RequestMethod.POST)
    public ResponseEntity updateQuantity(@RequestParam String productCode,
                                         @RequestParam Integer quantity) {
        auth = SecurityContextHolder.getContext().getAuthentication();
        Product product = productService.findProductByCode(productCode);
        if (product != null && quantity >= 0) {
            if (auth instanceof AnonymousAuthenticationToken) {
                shoppingCart.updateToCart(productCode, quantity);
                return ResponseEntity.status(HttpStatus.OK).body(new UpdateCartResponse(
                        "Update quantity success",
                        shoppingCart.getTotalPrice(),
                        shoppingCart.getDiscountValue(),
                        shoppingCart.getSizeCart()
                ));
            } else {
                long userId = ((CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
                cartDbService.updateQuantity(userId, productCode, quantity);
                return ResponseEntity.status(HttpStatus.OK).body(new UpdateCartResponse(
                        "Update quantity success",
                        cartDbService.totalPriceForItem(userId),
                        cartDbService.totalAmountHasDiscount(userId),
                        cartDbService.size(userId)
                ));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT FOUND - FAIL");
    }

    @RequestMapping(value = "/so-luong", method = RequestMethod.GET)
    public ResponseEntity getQuantity() {
        int quantity = 0;
        auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof AnonymousAuthenticationToken) {
            quantity = shoppingCart.getSizeCart();
        } else {
            long userId = ((CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
            quantity = cartDbService.size(userId);
        }

        return ResponseEntity.status(HttpStatus.OK).body(quantity);
    }

}
