package org.qbychat.backend.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 *  表定义层。
 *
 * @author zszf
 * @since 2024-06-26
 */
public class FriendsTableDef extends TableDef {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public static final FriendsTableDef FRIENDS = new FriendsTableDef();

    
    public final QueryColumn ID = new QueryColumn(this, "id");
    
    public final QueryColumn USER1 = new QueryColumn(this, "user1");

    
    public final QueryColumn USER2 = new QueryColumn(this, "user2");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER2, USER1};

    public FriendsTableDef() {
        super("", "db_friends");
    }

    private FriendsTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public FriendsTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new FriendsTableDef("", "db_friends", alias));
    }
}
