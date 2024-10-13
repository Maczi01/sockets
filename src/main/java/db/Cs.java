/*
 * This file is generated by jOOQ.
 */
package db;


import db.tables.Messages;
import db.tables.Users;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Cs extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>cs</code>
     */
    public static final Cs CS = new Cs();

    /**
     * The table <code>cs.messages</code>.
     */
    public final Messages MESSAGES = Messages.MESSAGES;

    /**
     * The table <code>cs.users</code>.
     */
    public final Users USERS = Users.USERS;

    /**
     * No further instances allowed
     */
    private Cs() {
        super("cs", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Messages.MESSAGES,
            Users.USERS
        );
    }
}