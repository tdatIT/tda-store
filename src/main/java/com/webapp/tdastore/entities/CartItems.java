package com.webapp.tdastore.entities;

import lombok.Data;

@Data
public class CartItems {
    private Product product;
    private int quantity;
}
