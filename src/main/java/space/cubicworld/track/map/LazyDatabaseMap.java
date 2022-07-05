package space.cubicworld.track.map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import space.cubicworld.track.CubicTrack;

import java.sql.*;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LazyDatabaseMap implements DatabaseMap {

    private final Map<String, Integer> permanentValueToId = new ConcurrentHashMap<>();
    private final Map<Integer, String> permanentIdToValue = new ConcurrentHashMap<>();
    private final Cache<Integer, String> cacheIdToValue = CacheBuilder.newBuilder().build();
    private final Cache<String, Integer> cacheValueToId;

    private final String selectByValueStatement;
    private final String selectByIdStatement;
    private final String insertStatement;

    private final AtomicInteger counter = new AtomicInteger(1);

    public LazyDatabaseMap(String tableName) throws SQLException {
        cacheValueToId = CacheBuilder
                .newBuilder()
                .expireAfterAccess(Duration.ofMinutes(30))
                .maximumSize(100)
                .<String, Integer>removalListener(notification -> {
                    if (notification.getValue() != null) {
                        cacheIdToValue.invalidate(notification.getValue());
                    }
                })
                .build();
        selectByIdStatement = "SELECT id FROM %s WHERE value = ?".formatted(tableName);
        selectByValueStatement = "SELECT name FROM %s WHERE id = ?".formatted(tableName);
        insertStatement = "INSERT %s(id, name) VALUES (?, ?)".formatted(tableName);
        try (Connection connection = CubicTrack.getInstance().getDataSource().getConnection();
             Statement selectLastStatement = connection.createStatement()) {
            ResultSet resultSet = selectLastStatement.executeQuery(
                    "SELECT id FROM %s ORDER BY id DESC LIMIT 1".formatted(tableName)
            );
            if (resultSet.next()) {
                counter.set(resultSet.getInt(1));
            }
            else {
                try (Statement insertEmptyStatement = connection.createStatement()) {
                    insertEmptyStatement.executeUpdate(
                            "INSERT IGNORE INTO %s (id, name) VALUES (0, \"\")".formatted(tableName)
                    );
                }
            }
        }
    }

    @Override
    @SneakyThrows
    public int getId(String value) {
        if (value == null || value.isEmpty()) return 0;
        Integer id = cacheValueToId.getIfPresent(value);
        if (id == null) id = permanentValueToId.get(value);
        if (id == null) {
            id = fetchId(value);
            cacheValueToId.put(value, id);
            cacheIdToValue.put(id, value);
        }
        return id;
    }

    @Override
    @SneakyThrows
    public String getValue(int id) {
        if (id == 0) return "";
        String value = cacheIdToValue.getIfPresent(id);
        if (value == null) value = permanentIdToValue.get(id);
        if (value == null) {
            value = fetchValue(id);
            if (value == null) return null;
            cacheValueToId.put(value, id);
            cacheIdToValue.put(id, value);
        }
        return value;
    }

    public void cache(String value) throws SQLException {
        String loweredValue = value.toLowerCase(Locale.ROOT);
        Integer id = cacheValueToId.getIfPresent(loweredValue);
        if (id == null) {
            id = fetchId(loweredValue);
        } else {
            cacheValueToId.invalidate(loweredValue);
        }
        permanentValueToId.put(loweredValue, id);
        permanentIdToValue.put(id, loweredValue);
    }

    public void unCache(String value) {
        permanentIdToValue.remove(permanentValueToId.remove(value.toLowerCase(Locale.ROOT)));
    }

    private int fetchId(String value) throws SQLException {
        String loweredValue = value.toLowerCase(Locale.ROOT);
        try (Connection connection = CubicTrack.getInstance().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectByValueStatement)
        ) {
            selectStatement.setString(1, loweredValue);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            int id = counter.getAndIncrement();
            try (PreparedStatement insertStatement = connection.prepareStatement(this.insertStatement)) {
                insertStatement.setInt(1, id);
                insertStatement.setString(2, loweredValue);
                insertStatement.executeUpdate();
            } catch (SQLException e) {
                counter.decrementAndGet();
                throw e;
            }
            return id;
        }
    }

    private String fetchValue(int id) throws SQLException {
        try (Connection connection = CubicTrack.getInstance().getDataSource().getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectByIdStatement)
        ) {
            selectStatement.setInt(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }
    }
}
