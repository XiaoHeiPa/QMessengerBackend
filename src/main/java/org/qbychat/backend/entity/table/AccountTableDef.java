package org.qbychat.backend.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

/**
 * 表定义层。
 *
 * @author zszf
 * @since 2024-06-15
 */
public class AccountTableDef extends TableDef {

    /**
     *
     */
    public static final AccountTableDef ACCOUNT = new AccountTableDef();
    @Serial
    private static final long serialVersionUID = 1L;
    public final QueryColumn ID = new QueryColumn(this, "id");


    public final QueryColumn ROLE = new QueryColumn(this, "role");


    public final QueryColumn EMAIL = new QueryColumn(this, "email");


    public final QueryColumn PASSWORD = new QueryColumn(this, "password");


    public final QueryColumn USERNAME = new QueryColumn(this, "username");


    public final QueryColumn REGISTER_TIME = new QueryColumn(this, "registerTime");


    public final QueryColumn MINECRAFT_UUID = new QueryColumn(this, "minecraftUuid");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USERNAME, PASSWORD, EMAIL, ROLE, MINECRAFT_UUID, REGISTER_TIME};

    public AccountTableDef() {
        super("", "db_account");
    }

    private AccountTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public AccountTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new AccountTableDef("", "db_account", alias));
    }

}
