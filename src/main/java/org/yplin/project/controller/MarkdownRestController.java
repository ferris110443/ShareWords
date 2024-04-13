package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.data.dto.MarkdownDTO;

@Slf4j
@RestController
@RequestMapping("api/1.0/markdown")
public class MarkdownRestController {

    @PostMapping("/saveMarkdownText")
    public void saveMarkdownText(@RequestBody MarkdownDTO markdownText) {

        System.out.println("markdownText.getMarkdownText() : "+markdownText.getMarkdownText());
    }

    @GetMapping("/getMarkdownText")
    public void  getMarkdownText() {

    }

}
