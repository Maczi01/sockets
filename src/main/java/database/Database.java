package database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Database {

  private final DSLContext create;

  public Database() {
    Properties properties = loadProperties();
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL(properties.getProperty("db.url"));
    dataSource.setUser(properties.getProperty("db.user"));
    dataSource.setPassword(properties.getProperty("db.password"));

    this.create = DSL.using(dataSource, org.jooq.SQLDialect.MYSQL);
  }

  private Properties loadProperties() {
    Properties properties = new Properties();
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
      if (input == null) {
        throw new RuntimeException("Sorry, unable to find application.properties");
      }
      properties.load(input);
    } catch (IOException ex) {
      ex.printStackTrace();
      throw new RuntimeException("Failed to load database properties", ex);
    }
    return properties;
  }

  public DSLContext getDSLContext() {
    return create;
  }
}
