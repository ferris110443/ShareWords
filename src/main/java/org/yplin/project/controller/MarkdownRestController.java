package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.service.FileContentService;

@Slf4j
@RestController
@RequestMapping("api/1.0/markdown")
public class MarkdownRestController {
    public static final Logger logger = LoggerFactory.getLogger(MarkdownRestController.class);

    @Autowired
    FileContentService fileContentService;

    @PostMapping("/markdownText")
    public void saveMarkdownText(@RequestBody MarkdownForm markdownForm) {

        fileContentService.saveFileContent(markdownForm);

    }

//    @GetMapping("/getMarkdownText")
//    public void  getMarkdownText() {
//
//    }

}
