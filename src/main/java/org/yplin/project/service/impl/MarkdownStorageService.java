package org.yplin.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.repository.FileContentBatchInsertRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MarkdownStorageService {
    private final List<MarkdownForm> markdownForms = Collections.synchronizedList(new ArrayList<>());
    private final FileContentBatchInsertRepository repository;


    @Autowired
    public MarkdownStorageService(FileContentBatchInsertRepository repository) {
        this.repository = repository;
    }

    public void addMarkdownForm(MarkdownForm form) {
        synchronized (markdownForms) {
            markdownForms.add(form);
        }
    }

    @Scheduled(fixedDelay = 10000)  //  10 seconds
    public void batchUpdate() {
        List<MarkdownForm> batch;
        synchronized (markdownForms) {
            batch = new ArrayList<>(markdownForms);
            markdownForms.clear();
        }
        if (!batch.isEmpty()) {
            repository.updateFileContentInBatch(batch);
        }
    }
}