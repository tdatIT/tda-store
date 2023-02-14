package com.webapp.tdastore.controller.api;

import com.webapp.tdastore.payload.ShippingRequest;
import com.webapp.tdastore.payload.ShippingResponse;
import com.webapp.tdastore.services.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ShippingAPI {
    @Autowired
    private ShippingService shippingService;

    @RequestMapping(value = "/api/v1/shipping-calculate", method = RequestMethod.GET)
    public ShippingResponse calculateShipping(@RequestBody @Valid ShippingRequest request,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ShippingResponse(400, "Parameter is invaid", -1);
        }
        int cost = shippingService.getPriceFromUserAddress(request.getProvince_code(),
                request.getDistrict_code());
        return new ShippingResponse(200, "Calculate success", cost);
    }

}
