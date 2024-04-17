package org.yplin.project.data.form;

import lombok.Data;

import java.util.List;

@Data
public class MessageDataForm {
    private String type;
    private long prev;
    private long t;
    private String chr;
    private long sender;
    private List<MessageDataForm> ops;
    private String roomId;
}
