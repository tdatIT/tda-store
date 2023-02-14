package com.webapp.tdastore.controller.web;

import com.webapp.tdastore.entities.CartItems;
import com.webapp.tdastore.entities.Product;
import com.webapp.tdastore.entities.ShoppingCart;
import com.webapp.tdastore.payload.UpdateCartResponse;
import com.webapp.tdastore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getShoppingCartPage(ModelMap modelMap) {
        List<CartItems> items = new ArrayList<>(shoppingCart.getCartItems());
        modelMap.addAttribute("items", items);
        modelMap.addAttribute("amount", shoppingCart.getTotalPrice());
        modelMap.addAttribute("total_discount", shoppingCart.getDiscountValue());
        return "web/page/cart-page";
    }

    @RequestMapping(value = "/them", method = RequestMethod.POST)
    public ResponseEntity addToCart(@RequestParam String code,
                                    @RequestParam(required = false) Integer quantity) {
        Product product = productService.findProductByCode(code);
        if (quantity == null)
            quantity = 1;
        if (product != null && quantity > 0) {
            shoppingCart.addToCart(product, quantity);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("FAIL: Not found product");
        }
        return ResponseEntity.status(HttpStatus.OK).body("SUCCESS: Add " + quantity + " product[" + code + "]");
    }

    @RequestMapping(value = "/cap-nhat", method = RequestMethod.POST)
    public ResponseEntity updateQuantity(@RequestParam String productCode,
                                         @RequestParam Integer quantity) {
        Product product = productService.findProductByCode(productCode);
        if (product != null && quantity >= 0) {
            shoppingCart.updateToCart(productCode, quantity);
            return ResponseEntity.status(HttpStatus.OK).body(new UpdateCartResponse(
                    "Update quantity success",
                    shoppingCart.getTotalPrice(),
                    shoppingCart.getDiscountValue(),
                    shoppingCart.getSizeCart()
            ));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT FOUND - FAIL");
    }

    @RequestMapping(value = "/so-luong", method = RequestMethod.GET)
    public ResponseEntity getQuantity() {
        int quantity = shoppingCart.getSizeCart();
        return ResponseEntity.status(HttpStatus.OK).body(quantity);
    }

}
