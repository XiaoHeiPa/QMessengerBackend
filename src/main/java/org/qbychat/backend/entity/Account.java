package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类。
 *
 * @author zszf
 * @since 2024-06-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "db_account")
public class Account implements BaseData {
    @Id(keyType = KeyType.Auto)
    private Integer id;
    private String username;
    private String email;
    private String bio = "Default bio.";
    private String password;
    private Role role;
    @Column(value = "registerTime")
    private long registerTime;
    private String nickname;
    @Column(value = "state")
    private boolean active = true;
}
