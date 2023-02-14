package com.webapp.tdastore.payload;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ShippingRequest {
    @NotNull
    private int province_code;
    @NotNull
    private int district_code;
}
