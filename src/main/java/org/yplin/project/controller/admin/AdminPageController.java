package org.yplin.project.controller.admin;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
public class AdminPageController {
    @GetMapping("workspace")
    public String getWorkSpace() {
        return "workspace.html";
    }




}
