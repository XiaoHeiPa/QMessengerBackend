package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

@Data
@Table(value = "db_client")
public class ClientVerify {
    @Id(keyType = KeyType.Auto)
    private Integer id;
    private Long buildDate;
    private String buildUser;
    private String buildVersion;
    private String hash;
}
