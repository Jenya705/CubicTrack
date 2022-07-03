package space.cubicworld.track;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CubicTrackEnum {

    private final Map<Integer, String> idToValue = new ConcurrentHashMap<>();
    private final Map<String, Integer> valueToId = new ConcurrentHashMap<>();
    private final AtomicInteger currentId = new AtomicInteger();
    private final String insertStatement;

    public CubicTrackEnum(String tableName) throws SQLException {
        insertStatement = "INSERT INTO %s (id, name) VALUES (?, ?)".formatted(tableName);
        try (Connection connection = CubicTrack.getInstance().getDataSource().getConnection();
             Statement insert0Statement = connection.createStatement();
             PreparedStatement selectStatement = connection.prepareStatement(
                     "SELECT * FROM %s".formatted(tableName)
             )
        ) {
            insert0Statement.executeUpdate("INSERT IGNORE INTO %s (id, name) VALUES (0, \"\")");
            ResultSet resultSet = selectStatement.executeQuery();
            int maxId = 0;
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String value = resultSet.getString(2);
                idToValue.put(id, value);
                valueToId.put(value, id);
                maxId = Math.max(maxId, id);
            }
            currentId.set(maxId);
        }
    }

    public int getId(String value) {
        return valueToId.computeIfAbsent(value, s -> {
            int id;
            try (Connection connection = CubicTrack.getInstance().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertStatement)
            ){
                id = currentId.incrementAndGet();
                statement.setInt(1, id);
                statement.setString(2, s);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return id;
        });
    }

    public String getValue(int id) {
        return idToValue.get(id);
    }

}
