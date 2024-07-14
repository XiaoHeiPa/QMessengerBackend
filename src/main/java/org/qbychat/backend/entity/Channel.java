package org.qbychat.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    private int id;
    private String title;
    private String name;
    private boolean directMessage;
}
