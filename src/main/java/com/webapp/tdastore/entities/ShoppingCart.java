package com.webapp.tdastore.entities;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@SessionScope
@Getter
@Setter
@NoArgsConstructor
public class ShoppingCart {
    static final Logger logger = LoggerFactory.getLogger("ShoppingCart");
    @Autowired
    private HttpServletResponse responseContext;
    private Map<String, CartItems> cart = new HashMap<String, CartItems>();

    public Collection<CartItems> getCartItems() {
        return cart.values();
    }

    public void addToCart(Product product, int quantity) {
        CartItems existItem = cart.get(product.getProductCode());
        if (existItem != null) {
            existItem.setQuantity(existItem.getQuantity() + quantity);
        } else
            cart.put(product.getProductCode(), new CartItems(product, quantity));
    }

    public void updateToCart(String code, int quantity) {
        CartItems existItem = cart.get(code);
        existItem.setQuantity(quantity);
        if (existItem.getQuantity() <= 0) {
            cart.remove(code);
        }
    }

    public int getSizeCart() {
        return cart.values().stream().mapToInt(t -> t.getQuantity()).sum();
    }

    public double getTotalPrice() {
        return cart.values().stream()
                .mapToDouble(t -> t.getQuantity() * t.getProduct().getPrice())
                .sum();
    }

    public double getDiscountValue() {
        return cart.values().stream()
                .mapToDouble(t ->
                        t.getProduct().getPromotionPrice() > 0 ?
                                t.getProduct().getPromotionPrice() : t.getProduct().getPrice() * t.getQuantity())
                .sum();
    }

    @PostConstruct
    public void initCart() {
        logger.info("Init shopping cart - Load data from cookie");
    }


    //Write cookie when close app
    @PreDestroy
    public void setCookie() throws JsonProcessingException {
        logger.info("Write data into cookie");
        ObjectMapper mapper = new ObjectMapper();
        responseContext.addCookie(new Cookie("cart-item", mapper.writeValueAsString(cart)));
    }
}
