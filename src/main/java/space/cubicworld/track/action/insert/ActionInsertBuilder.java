package space.cubicworld.track.action.insert;

import org.bukkit.Location;
import org.bukkit.Material;
import space.cubicworld.track.CubicTrack;
import space.cubicworld.track.action.Action;
import space.cubicworld.track.database.ActionInsert;

import java.util.Objects;

public class ActionInsertBuilder {

    private final long epoch;
    private Short actionId;
    private Short worldId;
    private Integer locationX;
    private Integer locationY;
    private Integer locationZ;
    private Integer invokerId;
    private int targetId;
    private short oldMaterialId;
    private String oldMaterialData;
    private short newMaterialId;
    private String newMaterialData;

    public ActionInsertBuilder() {
        this.epoch = System.currentTimeMillis();
    }

    public ActionInsertBuilder action(Action action) {
        this.actionId = (short) CubicTrack.getInstance().getActions().getId(action.getName());
        return this;
    }

    public ActionInsertBuilder location(Location location) {
        this.worldId = (short) CubicTrack.getInstance().getWorlds().getId(location.getWorld().getName());
        this.locationX = location.getBlockX();
        this.locationY = location.getBlockY();
        this.locationZ = location.getBlockZ();
        return this;
    }

    public ActionInsertBuilder invoker(String name) {
        this.invokerId = CubicTrack.getInstance().getEntities().getId(name);
        return this;
    }

    public ActionInsertBuilder target(String name) {
        this.targetId = CubicTrack.getInstance().getEntities().getId(name);
        return this;
    }

    public ActionInsertBuilder oldMaterial(Material material) {
        this.oldMaterialId = (short) CubicTrack.getInstance().getMaterials().getId(material.getKey().value());
        return this;
    }

    public ActionInsertBuilder oldMaterialData(String data) {
        this.oldMaterialData = data;
        return this;
    }

    public ActionInsertBuilder newMaterial(Material material) {
        this.newMaterialId = (short) CubicTrack.getInstance().getMaterials().getId(material.getKey().value());
        return this;
    }

    public ActionInsertBuilder newMaterialData(String data) {
        this.newMaterialData = data;
        return this;
    }

    public ActionInsert build() {
        return new ActionInsert(
                epoch,
                Objects.requireNonNull(actionId, "action type is not filled"),
                Objects.requireNonNull(worldId, "location is not filled"),
                Objects.requireNonNull(locationX, "location is not filled"),
                Objects.requireNonNull(locationY, "location is not filled"),
                Objects.requireNonNull(locationZ, "location is not filled"),
                Objects.requireNonNull(invokerId, "invoker is not filled"),
                targetId,
                oldMaterialId,
                oldMaterialData,
                newMaterialId,
                newMaterialData
        );
    }

}
