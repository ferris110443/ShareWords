package org.yplin.project.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class InitEventData {
    private int id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String history;

    public InitEventData(int id) {
        this.id = id;
    }
}