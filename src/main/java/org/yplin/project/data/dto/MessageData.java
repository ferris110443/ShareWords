package org.yplin.project.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class MessageData {
    private String type;
    private int prev;
    private int t;
    private String chr;
    private int sender;
    private List<MessageData> ops;
    private String roomId;
}
