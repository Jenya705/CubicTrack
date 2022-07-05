package space.cubicworld.track.database;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import space.cubicworld.track.CubicTrack;

import java.sql.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;

public class ActionInsertTask implements Runnable {

    private static boolean initialized = false;

    private final Queue<ActionInsert> actions = new ConcurrentLinkedDeque<>();

    private long actionCounter = 0;
    private long dataCounter;

    public ActionInsertTask() throws SQLException {
        synchronized (ActionInsertTask.class) {
            if (initialized) {
                throw new IllegalStateException("Can not create more than one instance of this class");
            }
            initialized = true;
        }
        FileConfiguration configuration = CubicTrack.getInstance().getConfig();
        configuration.addDefault("insert.batch_size", 1024);
        configuration.addDefault("insert.task_delta", 1);
        try (Connection connection = CubicTrack.getInstance().getDataSource().getConnection();
             Statement selectLastActionStatement = connection.createStatement();
             Statement selectLastDataStatement = connection.createStatement()
             ) {
            ResultSet actionResult = selectLastActionStatement
                    .executeQuery("SELECT id FROM actions ORDER BY id DESC LIMIT 1");
            if (actionResult.next()) {
                actionCounter = actionResult.getLong(1) + 1;
            }
            ResultSet dataResult = selectLastDataStatement
                    .executeQuery("SELECT id FROM materials_data ORDER BY id DESC LIMIT 1");
            if (dataResult.next()) {
                dataCounter = dataResult.getLong(1) + 1;
            }
            else {
                dataCounter = 1;
                try (Statement insertEmptyDataStatement = connection.createStatement()) {
                    insertEmptyDataStatement.executeUpdate("INSERT INTO materials_data (id, data) VALUES (0, \"\")");
                }
            }
        }
        schedule();
    }

    @Override
    public void run() {
        if (!actions.isEmpty()) {
            int batchSize = CubicTrack.getInstance().getConfig().getInt("insert.batch_size");
            try (Connection connection = CubicTrack.getInstance().getDataSource().getConnection();
                 PreparedStatement insertActionStatement = connection.prepareStatement("""
                         INSERT INTO actions(
                         id, epoch, action_id, world_id, invoker_id, target_id, location_x,
                         location_y, location_z, old_material_id, old_material_data_id,
                         new_material_id, new_material_data_id
                         ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                         """);
                 PreparedStatement insertDataStatement = connection.prepareStatement("""
                         INSERT INTO materials_data(id, data) VALUES (?, ?)
                         """)
            ) {
                for (int i = 0; i < batchSize; ++i) {
                    ActionInsert toInsert = actions.poll();
                    if (toInsert == null) break;
                    insertActionStatement.setLong(1, actionCounter++);
                    insertActionStatement.setLong(2, toInsert.getEpoch());
                    insertActionStatement.setShort(3, toInsert.getActionId());
                    insertActionStatement.setShort(4, toInsert.getWorldId());
                    insertActionStatement.setInt(5, toInsert.getInvokerId());
                    insertActionStatement.setInt(6, toInsert.getTargetId());
                    insertActionStatement.setInt(7, toInsert.getLocationX());
                    insertActionStatement.setInt(8, toInsert.getLocationY());
                    insertActionStatement.setInt(9, toInsert.getLocationZ());
                    insertActionStatement.setShort(10, toInsert.getOldMaterialId());
                    insertActionStatement.setShort(12, toInsert.getNewMaterialId());
                    if (toInsert.getOldMaterialData() != null && !toInsert.getOldMaterialData().isEmpty()) {
                        insertDataStatement.setLong(1, dataCounter++);
                        insertDataStatement.setString(2, toInsert.getOldMaterialData());
                        insertDataStatement.addBatch();
                        insertActionStatement.setLong(11, dataCounter);
                    }
                    else {
                        insertActionStatement.setLong(11, 0);
                    }
                    if (toInsert.getNewMaterialData() != null && !toInsert.getNewMaterialData().isEmpty()) {
                        insertDataStatement.setLong(1, dataCounter++);
                        insertDataStatement.setString(2, toInsert.getNewMaterialData());
                        insertDataStatement.addBatch();
                        insertActionStatement.setLong(13, dataCounter);
                    }
                    else {
                        insertActionStatement.setLong(13, 0);
                    }
                    insertActionStatement.addBatch();
                }
                insertDataStatement.executeLargeBatch();
                insertActionStatement.executeLargeBatch();
            } catch (SQLException e) {
                CubicTrack.getInstance().getLogger().log(Level.SEVERE, "SQLException while inserting actions:", e);
            }
        }
        schedule();
    }

    public void addAction(ActionInsert insert) {
        actions.add(insert);
    }

    public void schedule() {
        if (CubicTrack.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(
                    CubicTrack.getInstance(),
                    this,
                    CubicTrack.getInstance().getConfig().getLong("insert.task_delta")
            );
        }
    }

}
