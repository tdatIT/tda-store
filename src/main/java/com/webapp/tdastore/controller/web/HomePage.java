package com.webapp.tdastore.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomePage {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getHomePage(ModelMap modelMap) {
        return "web/page/index";
    }
}
