package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 实体类。
 *
 * @author zszf
 * @since 2024-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "db_account")
public class Account implements BaseData {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String role;

    @Column(value = "minecraftUuid")
    private String minecraftUuid;

    @Column(value = "registerTime")
    private LocalDateTime registerTime;

}
