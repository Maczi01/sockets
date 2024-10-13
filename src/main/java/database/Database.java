package database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

public class Database {

  private final DSLContext create;

  public Database() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL("jdbc:mysql://localhost:3306/cs");
    dataSource.setUser("root");
    dataSource.setPassword("");

    this.create = DSL.using(dataSource, org.jooq.SQLDialect.MYSQL);
  }

  public DSLContext getDSLContext() {
    return create;
  }
}