package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 *  实体类。
 *
 * @author zszf
 * @since 2024-06-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "db_global_msg")
public class GlobalMsg implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private String sender;

    private String msg;

    private Timestamp time;

}
