package org.qbychat.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email implements BaseData, Serializable {
    private String to;
    private String subject;
    private String content;

}
