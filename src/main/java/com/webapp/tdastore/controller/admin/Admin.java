package com.webapp.tdastore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class Admin {
    @RequestMapping(value = {"","/dashboard"},method = RequestMethod.GET)
    public String getAdminDashboard(){
        return "admin/dashboard";
    }

}
