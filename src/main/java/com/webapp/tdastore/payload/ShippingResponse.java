package com.webapp.tdastore.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShippingResponse {
    private int status;
    private String message;
    private int price;
}
