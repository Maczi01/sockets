/*
 * This file is generated by jOOQ.
 */
package db.enums;


import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public enum UsersRole implements EnumType {

    USER("USER"),

    ADMIN("ADMIN");

    private final String literal;

    private UsersRole(String literal) {
        this.literal = literal;
    }

    @Override
    public Catalog getCatalog() {
        return null;
    }

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public String getName() {
        return "users_role";
    }

    @Override
    public String getLiteral() {
        return literal;
    }

    /**
     * Lookup a value of this EnumType by its literal
     */
    public static UsersRole lookupLiteral(String literal) {
        return EnumType.lookupLiteral(UsersRole.class, literal);
    }
}
