package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "db_groups")
public class Group {
    @Id(keyType = KeyType.Auto)
    private Integer id;
    private Integer owner;
    private String name;
    private String description;
    private Date createTime;
    private Set<Integer> members;
}
