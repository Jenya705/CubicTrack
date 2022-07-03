package space.cubicworld.track;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class CubicTrackDataSource {

    private final HikariDataSource dataSource;

    public CubicTrackDataSource(
            String host, String database, String username,
            String password, InputStream properties
    ) throws IOException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://%s/%s".formatted(host, database));
        config.setUsername(username);
        config.setPassword(password);
        if (properties != null) {
            Properties propertiesObject = new Properties();
            propertiesObject.load(properties);
            config.setDataSourceProperties(propertiesObject);
        }
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
