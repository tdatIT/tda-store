package com.webapp.tdastore.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateCartResponse {
    String message;
    double new_total;
    double new_discount;
    int new_quantity;
}
