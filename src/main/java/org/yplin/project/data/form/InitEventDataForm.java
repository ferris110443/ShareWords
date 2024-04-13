package org.yplin.project.data.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class InitEventDataForm {
    private int id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String history;

    public InitEventDataForm(int id) {
        this.id = id;
    }
}