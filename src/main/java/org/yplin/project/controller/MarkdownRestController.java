package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.impl.FileContentServiceImp;

@Slf4j
@RestController
@RequestMapping("api/1.0/markdown")
public class MarkdownRestController {
    public static final Logger logger = LoggerFactory.getLogger(MarkdownRestController.class);

    @Autowired
    FileContentService fileContentService;

    @PostMapping("/saveMarkdownText")
    public void saveMarkdownText(@RequestBody MarkdownForm markdownForm) {
        fileContentService.saveFileContent(markdownForm);
//        logger.info("markdownText.getMarkdownText() : "+markdownForm.getMarkdownText());
//        logger.info("markdownText.getTitle() : "+markdownForm.getTitle());
//        logger.info("markdownText.getRoomId() : "+markdownForm.getRoomId());
    }

//    @GetMapping("/getMarkdownText")
//    public void  getMarkdownText() {
//
//    }

}
