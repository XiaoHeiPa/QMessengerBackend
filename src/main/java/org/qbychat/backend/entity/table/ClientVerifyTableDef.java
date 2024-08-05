package org.qbychat.backend.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

import java.io.Serial;

public class ClientVerifyTableDef extends TableDef {
    public static final ClientVerifyTableDef VERIFY_INFO = new ClientVerifyTableDef();

    @Serial
    private static final long serialVersionUID = 1L;
    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn BUILD_DATE = new QueryColumn(this, "buildDate");
    public final QueryColumn BUILD_VERSION = new QueryColumn(this, "buildVersion");
    public final QueryColumn BUILD_USER = new QueryColumn(this, "buildUser");
    public final QueryColumn HASH = new QueryColumn(this, "hash");
    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, BUILD_DATE, BUILD_VERSION, BUILD_USER, HASH};

    public ClientVerifyTableDef() {
        super("", "db_account");
    }

    private ClientVerifyTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public ClientVerifyTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new ClientVerifyTableDef("", "db_account", alias));
    }
}
