package org.yplin.project.data.form;

import lombok.Data;

import java.util.List;

@Data
public class MessageDataForm {
    private String type;
    private int prev;
    private int t;
    private String chr;
    private int sender;
    private List<MessageDataForm> ops;
    private String roomId;
}
