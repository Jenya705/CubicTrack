package space.cubicworld.track;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;
import space.cubicworld.track.action.ActionContainer;
import space.cubicworld.track.database.ActionInsertTask;
import space.cubicworld.track.listener.CubicTrackListener;
import space.cubicworld.track.listener.PlayerJoinListener;
import space.cubicworld.track.map.LazyDatabaseMap;
import space.cubicworld.track.map.PreparedDatabaseMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
public final class CubicTrack extends JavaPlugin {

    @Getter
    private static CubicTrack instance;

    private final List<CubicTrackListener> listeners = new CopyOnWriteArrayList<>();
    private final ActionContainer actionContainer = new ActionContainer();
    private CubicTrackDataSource dataSource;
    private PreparedDatabaseMap worlds;
    private LazyDatabaseMap entities;
    private PreparedDatabaseMap materials;
    private PreparedDatabaseMap actions;
    private ActionInsertTask actionInsertTask;

    public CubicTrack() {
        instance = this;
    }

    @Override
    @SneakyThrows
    public void onEnable() {
        dataSource = new CubicTrackDataSource(
                getConfig().getString("sql.host", "localhost:3306"),
                getConfig().getString("sql.database", "CubicTrack"),
                getConfig().getString("sql.user", "root"),
                getConfig().getString("sql.password", "1"),
                getResource("hikari.properties")
        );
        setupDatabase();
        worlds = new PreparedDatabaseMap("worlds_map");
        entities = new LazyDatabaseMap("entities_map");
        materials = new PreparedDatabaseMap("materials_map");
        actions = new PreparedDatabaseMap("actions_map");
        actionInsertTask = new ActionInsertTask();
        listeners.addAll(List.of(
                new PlayerJoinListener()
        ));
        saveConfig();
    }

    @Override
    public void onDisable() {

    }

    private void setupDatabase() throws SQLException {
        InputStream setupSql = Objects.requireNonNull(
                getResource("setup.sql"),
                "setup.sql is not exist in resources"
        );
        String setupSqlContent = new BufferedReader(new InputStreamReader(setupSql))
                .lines().collect(Collectors.joining("\n"));
        String[] setupSqlStatements = setupSqlContent.split(";");
        try (Connection connection = dataSource.getConnection()) {
            for (String statementContent : setupSqlStatements) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(statementContent);
                }
            }
        }
    }

}
