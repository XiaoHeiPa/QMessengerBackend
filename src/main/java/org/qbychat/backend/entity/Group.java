package org.qbychat.backend.entity;

import com.alibaba.fastjson2.JSONArray;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
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
    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Set<Integer> members;
}
