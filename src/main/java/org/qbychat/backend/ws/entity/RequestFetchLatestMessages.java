package org.qbychat.backend.ws.entity;

import lombok.Data;

@Data
public class RequestFetchLatestMessages {
    int channel;
    boolean directMessage;
}
