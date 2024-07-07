package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(value = "shownName")
    private String shownName;
    private String description;
    @Column(value = "createTime")
    private long createTime;
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Set<Integer> members;
}
