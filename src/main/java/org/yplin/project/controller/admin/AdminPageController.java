package org.yplin.project.controller.admin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
public class AdminPageController {
    @GetMapping("createworkspace")
    public String getCreateWorkspace() {
        return "createworkspace";
    }

    @GetMapping("workspace")
    public String getWorkSpacPage() {
        return "workspace";
    }

    @GetMapping("friends")
    public String getFriendsPage() {
        return "friends";
    }

    @GetMapping("home")
    public String getHomePage() {
        return "home";
    }


}
