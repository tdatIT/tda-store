package com.webapp.tdastore.entities;

import lombok.Data;

import java.util.List;

@Data
public class ShoppingCart {
    private List<CartItems> cartItems;
}
